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
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBase;
import javafx.scene.layout.Pane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class contains utilities for working with buttons.
 * @author Pavel Castornii
 */
public final class ButtonUtils {

    private static final Logger logger = LoggerFactory.getLogger(ButtonUtils.class);

    /**
     * Makes all buttons have same width using button text length.
     *
     * @param buttons
     */
    public static void makeEqualWidth(ButtonBase... buttons) {
        //find button with max text length
        ButtonBase b = buttons[0];
        for (var button : buttons) {
            if (button.getText().length() > b.getText().length()) {
                b = button;
            }
        }
        for (var button : buttons) {
            if (button != b) {
                button.minWidthProperty().bind(b.widthProperty());
                button.maxWidthProperty().bind(b.widthProperty());
            }
        }
    }

    /**
     * Makes square buttons by binding button width to button height.
     * @param buttons
     */
    public static void makeSquareByHeight(ButtonBase... buttons) {
        for (var button : buttons) {
            button.maxWidthProperty().bind(button.heightProperty());
            button.minWidthProperty().bind(button.heightProperty());
            /* it is neccesary to set pref because layout can be calculated using pref value */
            button.prefWidthProperty().bind(button.heightProperty());
        }
    }

    /**
     * Makes square buttons by binding button width to button height.
     * @param buttons
     */
    public static void makeSquareByHeight(List<ButtonBase> buttons) {
        makeSquareByHeight(buttons.toArray(new ButtonBase[buttons.size()]));
    }

    /**
     * Makes all buttons have same width. This method doesn't require buttons to be visible.
     * The width of the button is calculated upon temp scene.
     * @param parentPane is the pane, that contains buttons.
     */
    public static void makeEqualWidth(final Pane parentPane, List<String> sceneStylesheets) {
        List<Button> buttons = new ArrayList<>();
        for (Node node : parentPane.getChildren()) {
            if (node instanceof Button) {
                buttons.add((Button) node);
            }
        }
        ButtonUtils.makeEqualWidth(buttons, sceneStylesheets);
    }

    /**
     * Makes all buttons have same width. This method doesn't require buttons to be visible.
     * The width of the button is calculated upon temp scene.
     * @param buttons are the buttons that must have same width.
     */
    public static void makeEqualWidth(final List<Button> buttons, List<String> sceneStylesheets) {
        double maxWidth = 0;
        Pane tempPane = new Pane();
        new Scene(tempPane).getStylesheets().addAll(sceneStylesheets);
        List<Button> tempButtons = new ArrayList<>();
        for (Button button : buttons) {
            Button tempButton = new Button();
            tempButton.setText(button.getText());
            tempButton.setPrefSize(button.getPrefWidth(), button.getPrefHeight());
            tempButton.setMinSize(button.getMinWidth(), button.getMinHeight());
            tempButton.setMaxSize(button.getMaxWidth(), button.getMaxHeight());
            tempButtons.add(tempButton);
        }
        tempPane.getChildren().addAll(tempButtons);
        tempPane.applyCss();
        tempPane.layout();
        for (Button tempButton : tempButtons) {
            double buttonWidth = tempButton.getWidth();
            if (buttonWidth > maxWidth) {
                maxWidth = buttonWidth;
            }
            logger.trace("The width of the button with text {} was calculated as {}",
                    tempButton.getText(), buttonWidth);
        }
        for (Button button : buttons) {
            button.setPrefWidth(maxWidth);
            button.setMinWidth(maxWidth);
            button.setMaxWidth(maxWidth);
        }
    }

    private ButtonUtils() {
        //empty
    }
}
