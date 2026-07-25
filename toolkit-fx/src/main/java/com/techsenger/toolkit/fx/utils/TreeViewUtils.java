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

import com.techsenger.toolkit.fx.utils.VirtualFlowUtils.ScrollPosition;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

/**
 *
 * @author Pavel Castornii
 */
public final class TreeViewUtils {

    /**
     * Returns whether the given index is currently, fully visible in the tree view's viewport. Partially
     * visible cells are not considered visible.
     *
     * <p>Safe to call right after a structural change to the tree (such as expanding nodes or replacing
     * items), not only on an already-stable view — see {@link VirtualFlowUtils#isFullyVisible}.
     *
     * @param treeView the tree view to check
     * @param index    the index to check
     * @return {@code true} if the index is fully visible, {@code false} otherwise
     */
    public static boolean isFullyVisible(TreeView<?> treeView, int index) {
        return VirtualFlowUtils.isFullyVisible(treeView, index, true);
    }

    /**
     * Scrolls the tree view so the given index lands at {@code position} within the viewport, regardless of
     * whether it is already visible. Safe to call right after a structural change to the tree (such as
     * expanding nodes or replacing items), not only on an already-stable view — see
     * {@link VirtualFlowUtils#scrollTo}.
     *
     * @param treeView the tree view to scroll
     * @param index    the index to scroll to
     * @param position where the index should end up in the viewport
     */
    public static void scrollTo(TreeView<?> treeView, int index, ScrollPosition position) {
        VirtualFlowUtils.scrollTo(treeView, index, position, true);
    }

    /**
     * Scrolls the tree view only when the given index is outside the fully visible range. Partially visible
     * cells are not considered visible.
     *
     * <p>Safe to call right after a structural change to the tree (such as expanding nodes or replacing items),
     * not only on an already-stable view — see {@link VirtualFlowUtils#scrollToIfNeeded}.
     *
     * @param treeView the tree view to scroll
     * @param index    the index that should be visible
     * @param position where the index should end up in the viewport if it needs to be scrolled to
     */
    public static void scrollToIfNeeded(TreeView<?> treeView, int index, ScrollPosition position) {
        VirtualFlowUtils.scrollToIfNeeded(treeView, index, position, true);
    }

    /**
     * Searches for a {@link TreeItem} containing the specified value in the tree rooted at the given item.
     * Uses reference equality (==) to compare values.
     *
     * @param <T> the type of the value in the tree items
     * @param root the root item to start the search from
     * @param value the value to search for
     * @return the {@link TreeItem} containing the value, or {@code null} if not found
     */
    public static <T> TreeItem<T> findTreeItem(TreeItem<T> root, T value) {
        return TreeUtils.findTreeItem(root, value);
    }

    private TreeViewUtils() {
        // empty
    }
}
