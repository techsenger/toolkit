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
 * ({@code TableView}, {@code ListView}, {@code TreeView}, {@code TreeTableView}).
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

    /**
     * Where a target row should end up within the viewport after {@link #scrollTo}/{@link #scrollToIfNeeded}.
     */
    public enum ScrollPosition {
        TOP, CENTER, BOTTOM
    }

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
        // How many rows fit fully is not a fixed constant: with non-integer row heights, it can differ by one
        // depending on exactly which pixel offset the viewport starts at, which itself depends on the scroll
        // position. So target is recomputed from a fresh visibleRange() on every iteration instead of once
        // upfront, and each iteration's move is re-verified rather than assumed correct — this converges in a
        // couple of iterations since the index-to-position mapping is close to linear, and simply stops moving
        // once a computed target agrees with what is actually realized.
        for (int attempt = 0; attempt < 5; attempt++) {
            var range = visibleRange(flow);
            if (range == null) {
                return;
            }
            var viewportCount = range.count();
            if (itemCount <= viewportCount) {
                return; // everything fits, nothing to scroll
            }
            var maxTarget = itemCount - viewportCount;
            int target;
            switch (position) {
                case TOP:
                    target = index;
                    break;
                case BOTTOM:
                    target = index - viewportCount + 1;
                    break;
                case CENTER:
                    target = ViewPortScrollingHelper.calculateScrolledElementIndex(viewportCount, itemCount, index);
                    break;
                default:
                    throw new IllegalStateException("Unexpected position: " + position);
            }
            target = clamp(target, 0, maxTarget);
            if (range.first == target) {
                return;
            }
            flow.setPosition(target / (double) maxTarget);
            // setPosition only marks the flow dirty; without forcing this pass now, the next iteration's
            // visibleRange() read (or the caller's, once this method returns) would still see the pre-move
            // state until the next layout pulse.
            owner.applyCss();
            owner.layout();
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

    /**
     * Returns the range of currently, fully visible rows, or {@code null} if {@code flow} has no realized
     * cells (e.g. never laid out) or fewer than one row is fully visible (viewport shorter than a single row).
     */
    private static VisibleRange visibleRange(VirtualFlow<?> flow) {
        var firstCell = flow.getFirstVisibleCell();
        var lastCell = flow.getLastVisibleCell();
        if (firstCell == null || lastCell == null) {
            return null;
        }
        var first = firstCell.getIndex();
        // A cell whose bottom edge falls past the viewport is only partially visible, so it is excluded.
        var last = lastCell.getBoundsInParent().getMaxY() > flow.getHeight() ? lastCell.getIndex() - 1
                : lastCell.getIndex();
        return last < first ? null : new VisibleRange(first, last);
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    private VirtualFlowUtils() {
        // empty
    }
}
