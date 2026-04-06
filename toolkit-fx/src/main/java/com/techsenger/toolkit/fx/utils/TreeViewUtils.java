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

    private TreeViewUtils() {
        // empty
    }
}
