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

/**
 *
 * @author Pavel Castornii
 */
final class TreeUtils {

    /**
     * Searches for a {@link TreeItem} containing the specified value in the tree rooted at the given item.
     * Uses reference equality (==) to compare values.
     *
     * @param <T> the type of the value in the tree items
     * @param root the root item to start the search from
     * @param value the value to search for
     * @return the {@link TreeItem} containing the value, or {@code null} if not found
     */
    static <T> TreeItem<T> findTreeItem(TreeItem<T> root, T value) {
        if (root.getValue() == value) {
            return root;
        }
        for (TreeItem<T> child : root.getChildren()) {
            TreeItem<T> result = findTreeItem(child, value);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    private TreeUtils() {
        // empty
    }
}
