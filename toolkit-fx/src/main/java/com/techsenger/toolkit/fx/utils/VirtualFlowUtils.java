/*
 * Copyright 2016-2025 Pavel Castornii.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.techsenger.toolkit.fx.utils;

import com.techsenger.toolkit.fx.ViewPortScrollingHelper;
import javafx.scene.control.skin.VirtualFlow;
import javafx.scene.layout.Region;

/**
 * Utilities for querying and controlling the scroll position of a {@code VirtualFlow}-backed control
 * ({@code TableView}, {@code ListView}, {@code TreeView}, {@code TreeTableView}, or a custom control whose
 * {@code VirtualFlow} happens to scroll horizontally instead of vertically &mdash; see {@link #getFullyVisibleRange}).
 *
 * <p>Every method here takes the control itself (as a {@link Region}), not the {@code VirtualFlow} directly —
 * the flow is looked up via {@code owner.lookup(".virtual-flow")} internally, and the same {@code owner} is
 * what {@code forceLayout} acts on. Passing a control whose skin has not created a {@code VirtualFlow} yet
 * (e.g. never added to a scene) is safe: every method degrades to a no-op ({@code isFullyVisible} returns
 * {@code false}).
 *
 * @author Pavel Castornii
 */
public final class VirtualFlowUtils {

    private static final class VisibleRange {
        private final int first;
        private final int last;

        private VisibleRange(int first, int last) {
            this.first = first;
            this.last = last;
        }

        int count() {
            return last - first + 1;
        }
    }

    private static final int MAX_CONVERGENCE_ATTEMPTS = 8;

    /**
     * Returns whether {@code index} is currently, fully visible in {@code owner}'s viewport. A row that is
     * only partially visible at the bottom edge does not count as visible.
     *
     * @param owner       the {@code VirtualFlow}-backed control (e.g. {@code TableView}, {@code ListView})
     * @param index       the row index to check
     * @param forceLayout whether to force an immediate CSS + layout pass on {@code owner} first; needed when
     *                    {@code owner}'s items may have just changed, since {@code VirtualFlow} does not
     *                    rebuild its visible cells until the next layout pass — see {@link #scrollTo}
     * @return {@code true} if {@code index} is fully visible, {@code false} otherwise (including when the
     *     flow has no realized cells at all, e.g. {@code owner} was never laid out)
     */
    public static boolean isFullyVisible(Region owner, int index, boolean forceLayout) {
        ensureLayout(owner, forceLayout);
        var flow = lookupFlow(owner);
        if (flow == null) {
            return false;
        }
        var range = getFullyVisibleRange(flow);
        return range != null && index >= range.first && index <= range.last;
    }

    /**
     * Scrolls {@code owner} so {@code index} lands at {@code position} within the viewport, regardless of
     * whether it is already visible. Use {@link #scrollToIfNeeded} instead if the point is only to guarantee
     * visibility without disturbing an already-fine scroll position.
     *
     * <p>Forces an immediate CSS + layout pass on {@code owner} before reading {@code VirtualFlow}'s realized
     * cells (when {@code forceLayout} is {@code true}), so this is safe to call right after a structural
     * change to {@code owner} (e.g. replacing items), not only on an already-stable view. Pass {@code false}
     * only when the caller already knows the view is stable (e.g. a real keyboard event on a control whose
     * items have not just changed) and wants to skip the extra layout pass.
     *
     * @param owner       the {@code VirtualFlow}-backed control (e.g. {@code TableView}, {@code ListView})
     * @param index       the row index to scroll to
     * @param position    where {@code index} should end up in the viewport
     * @param forceLayout whether to force an immediate CSS + layout pass on {@code owner} first
     */
    public static void scrollTo(Region owner, int index, ScrollPosition position, boolean forceLayout) {
        ensureLayout(owner, forceLayout);
        var flow = lookupFlow(owner);
        if (flow == null) {
            return;
        }
        var itemCount = flow.getCellCount();
        // A first, minimal-effort move to get somewhere near the target and realize cells around it, so the
        // very first visibleRange() read below reflects a viewport close to where we're headed rather than
        // wherever the flow happened to be before this call.
        flow.scrollTo(index);
        owner.applyCss();
        owner.layout();
        // How many cells fit fully is not a fixed constant: with non-integer row/column sizes, it can differ
        // by one depending on exactly which pixel offset the viewport starts at, which itself depends on the
        // scroll position just applied. So target is recomputed from a fresh visibleRange() on every
        // iteration instead of once upfront. Convergence requires two things to both hold, not just one:
        // the freshly computed target must match what was computed last iteration (the formula itself has
        // stabilized, not just coincidentally matched a transient viewportCount reading), AND the flow must
        // already be sitting at that target (not just about to be told to move there). Checking only
        // "range.first == target" against a single reading is not enough — viewportCount can still shift on
        // the very next layout pass even when this iteration's numbers happen to agree, silently leaving the
        // flow one cell short of the intended edge.
        var previousTarget = -1;
        for (int attempt = 0; attempt < MAX_CONVERGENCE_ATTEMPTS; attempt++) {
            var range = getFullyVisibleRange(flow);
            if (range == null) {
                return;
            }
            var viewportCount = range.count();
            if (itemCount <= viewportCount) {
                return; // everything fits, nothing to scroll
            }
            var maxTarget = itemCount - viewportCount;
            var target = clamp(computeTarget(position, index, viewportCount, itemCount), 0, maxTarget);
            if (target == previousTarget && range.first == target) {
                return;
            }
            if (range.first != target) {
                flow.setPosition(target / (double) maxTarget);
                // setPosition only marks the flow dirty; without forcing this pass now, the next iteration's
                // visibleRange() read (or the caller's, once this method returns) would still see the
                // pre-move state until the next layout pulse.
                owner.applyCss();
                owner.layout();
            }
            previousTarget = target;
        }
    }

    /**
     * Scrolls {@code owner} so {@code index} becomes visible, but only if it is not already fully visible;
     * an already-visible index is left untouched. When a scroll is needed, {@code index} ends up at
     * {@code position} within the viewport — see {@link #scrollTo}.
     *
     * @param owner       the {@code VirtualFlow}-backed control (e.g. {@code TableView}, {@code ListView})
     * @param index       the row index that should be visible
     * @param position    where {@code index} should end up in the viewport if it needs to be scrolled to
     * @param forceLayout whether to force an immediate CSS + layout pass on {@code owner} first — see
     *                    {@link #scrollTo}
     */
    public static void scrollToIfNeeded(Region owner, int index, ScrollPosition position, boolean forceLayout) {
        ensureLayout(owner, forceLayout);
        // Layout was already forced above (if requested), so the nested calls below never need to do it again.
        if (!isFullyVisible(owner, index, false)) {
            scrollTo(owner, index, position, false);
        }
    }

    /**
     * Forces cells of {@code owner}'s virtual flow to re-derive their visual content from their current
     * item, by toggling each one away from and back to its own index via the standard, public
     * {@code IndexedCell#updateIndex(int)} hook: a cell's skin reacts to that the same way it does to being
     * recycled onto a different index (re-fetching the item and calling {@code updateItem} again). Unlike a
     * structural {@code refresh()} (e.g. {@code TableView#refresh()}), this never touches the flow's
     * item/offset bookkeeping or the current scroll position/selection, so it works for any
     * {@code VirtualFlow}-backed control (e.g. {@code TableView}, a custom column-based view), not just ones
     * whose own cells happen to always re-render on {@code updateIndex} even when the index doesn't actually
     * change.
     *
     * <p>Use this instead of a structural refresh when only cosmetic, per-cell state that a cell's own
     * {@code updateItem} derives from its item (e.g. a mutable flag on the item itself) may have changed,
     * not the item list/order itself.
     *
     * <p>When {@code onlyVisible} is {@code true}, only cells from {@link VirtualFlow#getFirstVisibleCell()}
     * to {@link VirtualFlow#getLastVisibleCell()} are touched &mdash; including ones only partially visible
     * at the viewport's edge (deliberately not {@link #isFullyVisible}'s stricter notion, which this class's
     * own {@code visibleRange} trims to for scroll-target purposes elsewhere: a partially clipped cell at
     * the edge is still visible to the user and must still be reached here). This is the cheap, common case,
     * but a cell the flow keeps recycled/cached at an index that never actually changes while it sits
     * off-screen won't be reached, and stays stale until something else disturbs it.
     *
     * <p>When {@code onlyVisible} is {@code false}, every index from {@code 0} to {@link VirtualFlow#getCellCount()}
     * is touched via {@link VirtualFlow#getCell(int)}, resolving each one from whatever the flow already has
     * realized/cached for it (its currently positioned cell, or a recycled one sitting in its pool
     * off-screen) rather than creating anything new. Note that this is a pass over the flow's own cells, not
     * necessarily over every item {@code owner} is showing: for a control whose cells are themselves items
     * one-to-one (e.g. {@code TableView}), the two coincide, but for a control whose cells are each a whole
     * row/column of several items (e.g. a column-based view), {@code getCellCount()} is the row/column count,
     * a much smaller number — reaching cells {@code onlyVisible} would miss, at the cost of a full pass over
     * all of the flow's cells instead of just the visible ones.
     *
     * @param owner       the {@code VirtualFlow}-backed control (e.g. {@code TableView}, {@code ListView})
     * @param onlyVisible whether to touch only the currently visible cells (cheap) or every cell the flow has
     *                    (thorough)
     */
    public static void updateCells(Region owner, boolean onlyVisible) {
        var flow = lookupFlow(owner);
        if (flow == null) {
            return;
        }
        int first;
        int last;
        if (onlyVisible) {
            var firstCell = flow.getFirstVisibleCell();
            var lastCell = flow.getLastVisibleCell();
            if (firstCell == null || lastCell == null) {
                return;
            }
            first = firstCell.getIndex();
            last = lastCell.getIndex();
        } else {
            first = 0;
            last = flow.getCellCount() - 1;
        }
        for (var index = first; index <= last; index++) {
            toggleCell(flow, index);
        }
        // Without forcing this pass now, a cell whose bounds are clipped at the viewport's edge can have
        // its content changes above sit un-rendered until some later, unrelated layout pulse - see the
        // identical reasoning in scrollTo.
        owner.applyCss();
        owner.layout();
    }

    /**
     * The single-index counterpart of {@link #updateCells(Region, boolean)} &mdash; forces just the one cell
     * currently realized at {@code index} to re-derive its visual content from its current item, via the
     * same {@code updateIndex} toggle, without touching any other cell or the flow's item/offset bookkeeping
     * or scroll position/selection. A no-op if {@code index} isn't currently realized (e.g. scrolled far out
     * of view) or {@code owner} has no {@code VirtualFlow} yet.
     *
     * <p>{@code index} is the flow's own cell index, not necessarily an item index &mdash; for controls whose
     * cells are themselves items one-to-one (e.g. {@code TableView}, {@code ListView}) the two coincide, but
     * for a control whose cells are each a whole row/column of several items (e.g. a column-based view) they
     * do not; such a control needs its own translation layer on top of this method, not a direct call to it
     * with an item index.
     *
     * @param owner the {@code VirtualFlow}-backed control (e.g. {@code TableView}, {@code ListView})
     * @param index the flow's own cell index to update
     */
    public static void updateCell(Region owner, int index) {
        var flow = lookupFlow(owner);
        if (flow == null) {
            return;
        }
        toggleCell(flow, index);
        // See the identical reasoning in updateCells/scrollTo.
        owner.applyCss();
        owner.layout();
    }

    private static void toggleCell(VirtualFlow<?> flow, int index) {
        var cell = flow.getCell(index);
        if (cell != null && !cell.isEmpty()) {
            cell.updateIndex(-1);
            cell.updateIndex(index);
        }
    }

    private static void ensureLayout(Region owner, boolean forceLayout) {
        if (forceLayout) {
            owner.applyCss();
            owner.layout();
        }
    }

    private static VirtualFlow<?> lookupFlow(Region owner) {
        return (VirtualFlow<?>) owner.lookup(".virtual-flow");
    }

    private static int computeTarget(ScrollPosition position, int index, int viewportCount, int itemCount) {
        switch (position) {
            case START:
                return index;
            case END:
                return index - viewportCount + 1;
            case CENTER:
                return ViewPortScrollingHelper.calculateScrolledElementIndex(viewportCount, itemCount, index);
            default:
                throw new IllegalStateException("Unexpected position: " + position);
        }
    }

    /**
     * Returns the range of currently, fully visible cells, or {@code null} if {@code flow} has no realized
     * cells (e.g. never laid out) or fewer than one cell is fully visible (viewport shorter than a single
     * cell).
     */
    private static VisibleRange getFullyVisibleRange(VirtualFlow<?> flow) {
        var firstCell = flow.getFirstVisibleCell();
        var lastCell = flow.getLastVisibleCell();
        if (firstCell == null || lastCell == null) {
            return null;
        }
        var first = firstCell.getIndex();
        var bounds = lastCell.getBoundsInParent();
        // A cell whose trailing edge falls past the viewport is only partially visible, so it is excluded.
        // Which edge is "trailing" depends on the flow's orientation: a vertical flow (e.g. TableView,
        // ListView) scrolls top-to-bottom, so it's the cell's bottom (Y); a horizontal flow (e.g. a custom
        // control whose cells are themselves whole columns) scrolls left-to-right, so it's the right edge (X).
        var lastFullyVisible = flow.isVertical()
                ? bounds.getMaxY() <= flow.getHeight()
                : bounds.getMaxX() <= flow.getWidth();
        var last = lastFullyVisible ? lastCell.getIndex() : lastCell.getIndex() - 1;
        return last < first ? null : new VisibleRange(first, last);
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    private VirtualFlowUtils() {
        // empty
    }
}
