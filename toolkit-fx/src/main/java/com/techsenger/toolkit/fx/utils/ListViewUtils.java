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
import javafx.scene.control.skin.VirtualFlow;

/**
 *
 * @author Pavel Castornii
 */
public final class ListViewUtils {

    /**
     * Scrolls the list view only when the given index is outside the visible range, mimicking natural keyboard
     * navigation behavior.
     *
     * @param listView the list view to scroll
     * @param index    the index that should be visible
     */
    public static void scrollToIfNeeded(ListView<?> listView, int index) {
        VirtualFlow<?> flow = (VirtualFlow<?>) listView.lookup(".virtual-flow");
        if (flow == null) {
            return;
        }

        int first = flow.getFirstVisibleCell().getIndex();
        int last = flow.getLastVisibleCell().getIndex();

        if (index < first) {
            listView.scrollTo(index);
        } else if (index > last) {
            listView.scrollTo(index - (last - first));
        }
    }

    private ListViewUtils() {
        // empty
    }
}
