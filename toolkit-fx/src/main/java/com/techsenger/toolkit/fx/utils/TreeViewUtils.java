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

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.skin.VirtualFlow;

/**
 *
 * @author Pavel Castornii
 */
public final class TreeViewUtils {

    /**
     * Scrolls the tree view only when the given index is outside the fully visible range, mimicking natural keyboard
     * navigation behavior. Partially visible cells are not considered visible.
     *
     * <p><b>Important:</b> This method relies on {@link VirtualFlow} being fully initialized and its cells being
     * rendered. It must not be called immediately after structural changes to the tree (such as expanding nodes,
     * replacing items, or any operation that triggers a layout pass), as {@code VirtualFlow} may not yet have
     * rebuilt its visible cells, causing the scroll to have no effect. This method is intended for
     * navigation-driven scrolling only (e.g., keyboard or programmatic selection on an already stable view).
     * Wrapping the call in {@code Platform.runLater} — or even nested calls — is not a reliable workaround;
     * if the view may have just been updated, prefer a direct {@link TreeView#scrollTo(int)} call instead.
     *
     * @param treeView the tree view to scroll
     * @param index    the index that should be visible
     */
    public static void scrollToIfNeeded(TreeView<?> treeView, int index) {
        VirtualFlow<?> flow = (VirtualFlow<?>) treeView.lookup(".virtual-flow");
        if (flow == null) {
            return;
        }
        NodeUtils.scrollToIfNeeded(flow, treeView::scrollTo, index);
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
