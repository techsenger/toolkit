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

/**
 *
 * @author Pavel Castornii
 */
public final class TableUtils {

    /**
     * Returns whether the given index is currently, fully visible in the table view's viewport. Safe to call
     * right after a structural change to the table (such as replacing items), not only on an already-stable
     * view — see {@link VirtualFlowUtils#isFullyVisible}.
     *
     * @param tableView the table view to check
     * @param index     the index to check
     * @return {@code true} if the index is fully visible, {@code false} otherwise
     */
    public static boolean isFullyVisible(TableView<?> tableView, int index) {
        return VirtualFlowUtils.isFullyVisible(tableView, index, true);
    }

    /**
     * Returns whether the given index is currently, fully visible in the tree table view's viewport. Safe to
     * call right after a structural change to the tree (such as expanding nodes or replacing items), not only
     * on an already-stable view — see {@link VirtualFlowUtils#isFullyVisible}.
     *
     * @param treeTableView the tree table view to check
     * @param index         the index to check
     * @return {@code true} if the index is fully visible, {@code false} otherwise
     */
    public static boolean isFullyVisible(TreeTableView<?> treeTableView, int index) {
        return VirtualFlowUtils.isFullyVisible(treeTableView, index, true);
    }

    /**
     * Scrolls the table view so the given index lands at {@code position} within the viewport, regardless of
     * whether it is already visible. Safe to call right after a structural change to the table (such as
     * replacing items), not only on an already-stable view — see {@link VirtualFlowUtils#scrollTo}.
     *
     * @param tableView the table view to scroll
     * @param index     the index to scroll to
     * @param position  where the index should end up in the viewport
     */
    public static void scrollTo(TableView<?> tableView, int index, ScrollPosition position) {
        VirtualFlowUtils.scrollTo(tableView, index, position, true);
    }

    /**
     * Scrolls the tree table view so the given index lands at {@code position} within the viewport, regardless
     * of whether it is already visible. Safe to call right after a structural change to the tree (such as
     * expanding nodes or replacing items), not only on an already-stable view — see
     * {@link VirtualFlowUtils#scrollTo}.
     *
     * @param treeTableView the tree table view to scroll
     * @param index         the index to scroll to
     * @param position      where the index should end up in the viewport
     */
    public static void scrollTo(TreeTableView<?> treeTableView, int index, ScrollPosition position) {
        VirtualFlowUtils.scrollTo(treeTableView, index, position, true);
    }

    /**
     * Scrolls the table view only when the given index is outside the visible range. Safe to call right after
     * a structural change to the table (such as replacing items), not only on an already-stable view — see
     * {@link VirtualFlowUtils#scrollToIfNeeded}.
     *
     * @param tableView the table view to scroll
     * @param index     the index that should be visible
     * @param position  where the index should end up in the viewport if it needs to be scrolled to
     */
    public static void scrollToIfNeeded(TableView<?> tableView, int index, ScrollPosition position) {
        VirtualFlowUtils.scrollToIfNeeded(tableView, index, position, true);
    }

    /**
     * Scrolls the tree table view only when the given index is outside the visible range. Safe to call right
     * after a structural change to the tree (such as expanding nodes or replacing items), not only on an
     * already-stable view — see {@link VirtualFlowUtils#scrollToIfNeeded}.
     *
     * @param treeTableView the tree table view to scroll
     * @param index         the index that should be visible
     * @param position      where the index should end up in the viewport if it needs to be scrolled to
     */
    public static void scrollToIfNeeded(TreeTableView<?> treeTableView, int index, ScrollPosition position) {
        VirtualFlowUtils.scrollToIfNeeded(treeTableView, index, position, true);
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
