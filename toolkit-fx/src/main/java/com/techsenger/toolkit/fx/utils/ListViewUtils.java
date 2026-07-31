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

import javafx.scene.control.ListView;

/**
 *
 * @author Pavel Castornii
 */
public final class ListViewUtils {

    /**
     * Returns whether the given index is currently, fully visible in the list view's viewport. Safe to call
     * right after a structural change to the list (such as replacing items), not only on an already-stable
     * view — see {@link VirtualFlowUtils#isFullyVisible}.
     *
     * @param listView the list view to check
     * @param index    the index to check
     * @return {@code true} if the index is fully visible, {@code false} otherwise
     */
    public static boolean isFullyVisible(ListView<?> listView, int index) {
        return VirtualFlowUtils.isFullyVisible(listView, index, true);
    }

    /**
     * Scrolls the list view so the given index lands at {@code position} within the viewport, regardless of
     * whether it is already visible. Safe to call right after a structural change to the list (such as
     * replacing items), not only on an already-stable view — see {@link VirtualFlowUtils#scrollTo}.
     *
     * @param listView the list view to scroll
     * @param index    the index to scroll to
     * @param position where the index should end up in the viewport
     */
    public static void scrollTo(ListView<?> listView, int index, ScrollPosition position) {
        VirtualFlowUtils.scrollTo(listView, index, position, true);
    }

    /**
     * Scrolls the list view only when the given index is outside the fully visible range. Safe to call right
     * after a structural change to the list (such as replacing items), not only on an already-stable view —
     * see {@link VirtualFlowUtils#scrollToIfNeeded}.
     *
     * @param listView the list view to scroll
     * @param index    the index that should be visible
     * @param position where the index should end up in the viewport if it needs to be scrolled to
     */
    public static void scrollToIfNeeded(ListView<?> listView, int index, ScrollPosition position) {
        VirtualFlowUtils.scrollToIfNeeded(listView, index, position, true);
    }

    /**
     * Forces the cell currently showing {@code itemIndex} to re-derive its visual content from its current
     * item, without touching the list's items/scroll position/selection — see
     * {@link VirtualFlowUtils#updateCell}. A list view's cells are its items one-to-one, so {@code itemIndex}
     * is exactly the cell index.
     *
     * @param listView  the list view to update
     * @param itemIndex the item index whose cell should be updated
     */
    public static void updateCell(ListView<?> listView, int itemIndex) {
        VirtualFlowUtils.updateCell(listView, itemIndex);
    }

    /**
     * Forces every cell of the list view to re-derive its visual content from its current item, without
     * touching the list's items/scroll position/selection — see {@link VirtualFlowUtils#updateCells}. A
     * list view's cells are its items one-to-one, so passing {@code false} touches every item.
     *
     * @param listView    the list view to update
     * @param onlyVisible whether to touch only the currently visible cells (cheap) or every cell (thorough)
     */
    public static void updateCells(ListView<?> listView, boolean onlyVisible) {
        VirtualFlowUtils.updateCells(listView, onlyVisible);
    }

    private ListViewUtils() {
        // empty
    }
}
