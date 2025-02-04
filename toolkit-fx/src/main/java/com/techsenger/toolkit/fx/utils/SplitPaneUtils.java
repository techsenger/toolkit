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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;

/**
 * Class contains utilities for working with slit panes.
 * @author Pavel Castornii
 */
public final class SplitPaneUtils {

    /**
     * This function is used to get ordered set of dividers - list of dividers
     * when 0 element is the first divider etc.
     *
     * <p>Lookups are not guaranteed to work until CSS has been applied to the scene. So,
     * before calling this function do on some parent of splitPane the following code:
     * parent.applyCss();parent.layout();. Only this case this function will work.
     *
     * <p>Instead of using this method use SplitPane skin.
     *
     * @param splitPane from which dividers will be derived.
     * @return list of nodes.
     */
    public static List<Node> getDividerNodes(final SplitPane splitPane) {
        //horizontal splitpane is pane with vertical dividers
        //vertical splitpane is pane with horizont dividers
        //using positions of X and Y and sorting them we find certain devider
        Map<Double, Node> sortedMap = new TreeMap<>();
        if (splitPane.getOrientation() == Orientation.HORIZONTAL) {
            splitPane.lookupAll(".split-pane-divider").stream().forEach(div ->  {
                if (div.getParent() == splitPane) {
                    sortedMap.put(div.getLayoutX(), div);
                }
            });
        } else {
            splitPane.lookupAll(".split-pane-divider").stream().forEach(div ->  {
                if (div.getParent() == splitPane) {
                    sortedMap.put(div.getLayoutY(), div);
                }
            });
        }
        return new ArrayList<>(sortedMap.values());
    }

    /**
     * Constructor.
     */
    private SplitPaneUtils() {

    }
}
