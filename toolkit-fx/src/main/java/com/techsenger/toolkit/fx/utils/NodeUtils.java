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
import java.util.Objects;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Pavel Castornii
 */
public final class NodeUtils {

    private static final Logger logger = LoggerFactory.getLogger(NodeUtils.class);

    public static final int MAX_FOCUS_ATTEMPTS = 10;

    /**
     * Requests focus on the given JavaFX node. If focus is not immediately acquired, retries up to
     * {@link #MAX_FOCUS_ATTEMPTS} times. This method runs asynchronously on the JavaFX application thread.
     *
     * @param node the JavaFX node to receive focus
     */
    public static void requestFocus(Node node) {
        doRequestFocus(node, null, MAX_FOCUS_ATTEMPTS);
    }

    /**
     * Requests focus on the given JavaFX node and executes a callback upon success. If focus is not immediately
     * acquired, retries up to {@link #MAX_FOCUS_ATTEMPTS} times. The callback is only executed when focus is
     * successfully acquired. This method runs asynchronously on the JavaFX application thread.
     *
     * @param node the JavaFX node to receive focus
     * @param onSuccess callback to execute when focus is acquired
     */
    public static void requestFocus(Node node, Runnable onSuccess) {
        Objects.requireNonNull(onSuccess);
        doRequestFocus(node, onSuccess, MAX_FOCUS_ATTEMPTS);
    }

    /**
     * Requests focus on the given JavaFX node. If focus is not immediately acquired, retries up to
     * {@code maxAttempts} times. This method runs asynchronously on the JavaFX application thread.
     *
     * @param node the JavaFX node to receive focus
     * @param maxAttempts the maximum number of attempts to acquire focus
     */
    public static void requestFocus(Node node, int maxAttempts) {
        doRequestFocus(node, null, maxAttempts);
    }

    /**
     * Requests focus on the given JavaFX node and executes a callback upon success. If focus is not immediately
     * acquired, retries up to {@code maxAttempts} times. The callback is only executed when focus is
     * successfully acquired. This method runs asynchronously on the JavaFX application thread.
     *
     * @param node the JavaFX node to receive focus
     * @param onSuccess callback to execute when focus is acquired
     * @param maxAttempts the maximum number of attempts to acquire focus
     */
    public static void requestFocus(Node node, Runnable onSuccess, int maxAttempts) {
        Objects.requireNonNull(onSuccess);
        doRequestFocus(node, onSuccess, maxAttempts);
    }

    /**
     * Returns all child nodes of some node. It seems that this method finds only visible nodes.
     *
     * @param parent node.
     * @return list of nodes.
     */
    public static ArrayList<Node> getAllNodes(final Parent parent) {
        ArrayList<Node> nodes = new ArrayList<Node>();
        addAllChildren(parent, nodes);
        return nodes;
    }

    private static void doRequestFocus(Node node, Runnable onSuccess, int maxAttempts) {
        Timeline[] holder = new Timeline[1];
        holder[0] = new Timeline(
            new KeyFrame(Duration.millis(25), e -> {
                if (!node.isFocused()) {
                    node.requestFocus();
                } else {
                    holder[0].stop();
                    if (onSuccess != null) {
                        onSuccess.run();
                    }
                }
            })
        );
        holder[0].setCycleCount(maxAttempts);
        holder[0].setOnFinished(e -> {
            if (!node.isFocused()) {
                logger.debug("Couldn't request focus for {}, after {} attempts", node, maxAttempts);
            }
        });
        holder[0].play();
    }

    /**
     * Return all descendent/children of some node.
     *
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
