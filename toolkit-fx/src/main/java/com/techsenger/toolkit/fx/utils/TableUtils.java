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

import javafx.scene.control.TableView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.skin.VirtualFlow;

/**
 *
 * @author Pavel Castornii
 */
public final class TableUtils {

    /**
     * Scrolls the table view only when the given index is outside the visible range, mimicking natural keyboard
     * navigation behavior.
     *
     * <p><b>Important:</b> This method relies on {@link VirtualFlow} being fully initialized and its cells being
     * rendered. It must not be called immediately after structural changes to the table (such as replacing items
     * or any operation that triggers a layout pass), as {@code VirtualFlow} may not yet have rebuilt its visible
     * cells, causing the scroll to have no effect. This method is intended for navigation-driven scrolling only
     * (e.g., keyboard or programmatic selection on an already stable view). Wrapping the call in
     * {@code Platform.runLater} — or even nested calls — is not a reliable workaround; if the view may have
     * just been updated, prefer a direct {@link TableView#scrollTo(int)} call instead.
     *
     *
     * @param tableView the table view to scroll
     * @param index     the index that should be visible
     */
    public static void scrollToIfNeeded(TableView<?> tableView, int index) {
        VirtualFlow<?> flow = (VirtualFlow<?>) tableView.lookup(".virtual-flow");
        if (flow == null) {
            return;
        }
        NodeUtils.scrollToIfNeeded(flow, tableView::scrollTo, index);
    }

    /**
     * Scrolls the tree table view only when the given index is outside the visible range, mimicking natural keyboard
     * navigation behavior.
     *
     * <p><b>Important:</b> This method relies on {@link VirtualFlow} being fully initialized and its cells being
     * rendered. It must not be called immediately after structural changes to the tree (such as expanding nodes,
     * replacing items, or any operation that triggers a layout pass), as {@code VirtualFlow} may not yet have
     * rebuilt its visible cells, causing the scroll to be silently skipped. This method is intended for
     * navigation-driven scrolling only (e.g., keyboard or programmatic selection on an already stable view).
     * Wrapping the call in {@code Platform.runLater} — or even nested calls — is not a reliable workaround;
     * if the view may have just been updated, prefer a direct {@link TreeTableView#scrollTo(int)} call instead.
     *
     * @param treeTableView the tree table view to scroll
     * @param index         the index that should be visible
     */
    public static void scrollToIfNeeded(TreeTableView<?> treeTableView, int index) {
        VirtualFlow<?> flow = (VirtualFlow<?>) treeTableView.lookup(".virtual-flow");
        if (flow == null) {
            return;
        }
        NodeUtils.scrollToIfNeeded(flow, treeTableView::scrollTo, index);
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

    private TableUtils() {
        // empty
    }
}
