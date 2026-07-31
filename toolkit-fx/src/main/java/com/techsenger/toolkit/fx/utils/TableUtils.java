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

    /**
     * Forces the row currently showing {@code itemIndex} to re-derive its visual content from its current
     * item, without touching the table's items/scroll position/selection — see
     * {@link VirtualFlowUtils#updateCell}. A table view's rows are its items one-to-one, so {@code itemIndex}
     * is exactly the row index.
     *
     * @param tableView the table view to update
     * @param itemIndex the item index whose row should be updated
     */
    public static void updateRow(TableView<?> tableView, int itemIndex) {
        VirtualFlowUtils.updateCell(tableView, itemIndex);
    }

    /**
     * Forces the tree table row currently showing {@code itemIndex} to re-derive its visual content from its
     * current item, without touching the tree table's items/scroll position/selection — see
     * {@link VirtualFlowUtils#updateCell}. A tree table view's rows are its (visible, expanded) items
     * one-to-one, so {@code itemIndex} is exactly the row index.
     *
     * @param treeTableView the tree table view to update
     * @param itemIndex     the item index whose row should be updated
     */
    public static void updateRow(TreeTableView<?> treeTableView, int itemIndex) {
        VirtualFlowUtils.updateCell(treeTableView, itemIndex);
    }

    /**
     * Forces every row of the table view to re-derive its visual content from its current item, without
     * touching the table's items/scroll position/selection — see {@link VirtualFlowUtils#updateCells}. A
     * table view's rows are its items one-to-one, so passing {@code false} touches every item.
     *
     * @param tableView   the table view to update
     * @param onlyVisible whether to touch only the currently visible rows (cheap) or every row (thorough)
     */
    public static void updateRows(TableView<?> tableView, boolean onlyVisible) {
        VirtualFlowUtils.updateCells(tableView, onlyVisible);
    }

    /**
     * Forces every row of the tree table view to re-derive its visual content from its current item, without
     * touching the tree table's items/scroll position/selection — see {@link VirtualFlowUtils#updateCells}.
     * A tree table view's rows are its (visible, expanded) items one-to-one, so this touches every item.
     *
     * @param treeTableView the tree table view to update
     * @param onlyVisible   whether to touch only the currently visible rows (cheap) or every row (thorough)
     */
    public static void updateRows(TreeTableView<?> treeTableView, boolean onlyVisible) {
        VirtualFlowUtils.updateCells(treeTableView, onlyVisible);
    }

    private TableUtils() {
        // empty
    }
}
