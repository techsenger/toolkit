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

    private TableUtils() {
        // empty
    }
}
