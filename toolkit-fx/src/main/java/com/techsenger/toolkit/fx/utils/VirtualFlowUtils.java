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
 * {@code VirtualFlow} happens to scroll horizontally instead of vertically &mdash; see {@link #visibleRange}).
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



    private static final int MAX_CONVERGENCE_ATTEMPTS = 8;

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
        var range = visibleRange(flow);
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
            var range = visibleRange(flow);
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
    private static VisibleRange visibleRange(VirtualFlow<?> flow) {
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
