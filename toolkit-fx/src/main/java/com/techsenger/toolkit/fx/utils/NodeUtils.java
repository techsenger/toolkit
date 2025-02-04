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
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Parent;

/**
 *
 * @author Pavel Castornii
 */
public final class NodeUtils {

    public static void requestFocus(Node node) {
        Platform.runLater(() -> {
            if (!node.isFocused()) {
                node.requestFocus();
                requestFocus(node);
            }
        });
    }

    public static void requestFocus(Node node, Runnable onSuccess) {
        Platform.runLater(() -> {
            if (!node.isFocused()) {
                node.requestFocus();
                requestFocus(node, onSuccess);
            } else {
                onSuccess.run();
            }
        });
    }

    /**
     * Returns all child nodes of some node. It seems that this method finds only visible nodes.
     * @param parent node.
     * @return list of nodes.
     */
    public static ArrayList<Node> getAllNodes(final Parent parent) {
        ArrayList<Node> nodes = new ArrayList<Node>();
        addAllChildren(parent, nodes);
        return nodes;
    }

    /**
     * Return all descendent/children of some node.
     * @param parent that will be searched for children.
     * @param nodes that are used for storing found children.
     */
    private static void addAllChildren(final Parent parent, final ArrayList<Node> nodes) {
        for (Node node : parent.getChildrenUnmodifiable()) {
            nodes.add(node);
            if (node instanceof Parent) {
                addAllChildren((Parent) node, nodes);
            }
        }
    }

    private NodeUtils() {
        //empty
    }
}
