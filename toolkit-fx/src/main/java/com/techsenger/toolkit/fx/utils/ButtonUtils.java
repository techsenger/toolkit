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

import java.util.List;
import javafx.scene.control.ButtonBase;

/**
 * Class contains utilities for working with buttons.
 * @author Pavel Castornii
 */
public final class ButtonUtils {

    /**
     * Adjusts the width of all buttons to match the widest button's width based on their text content. This method
     * does not require buttons to have their actual sizes computed beforehand.
     *
     * @param buttons
     */
    public static void makeEqualWidthByText(ButtonBase... buttons) {
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
     * Adjusts the width of all buttons to match the widest button's width based on their size. This method requires
     * buttons to have their actual sizes computed beforehand.
     *
     * @param buttons
     */
    public static void makeEqualWidthBySize(ButtonBase... buttons) {
        //find button with max text length
        ButtonBase b = buttons[0];
        for (var button : buttons) {
            if (button.getWidth() > b.getWidth()) {
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

    private ButtonUtils() {
        //empty
    }
}
