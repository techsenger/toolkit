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

import java.util.Comparator;
import java.util.List;
import javafx.scene.control.ButtonBase;

/**
 * Class contains utilities for working with buttons.
 *
 * @author Pavel Castornii
 */
public final class ButtonUtils {

    /**
     * Makes all buttons equal width by binding their width to the button with the longest text.
     * <p>
     * The method finds the button with the maximum text length and binds all other buttons' min/max width properties to
     * its actual width property. The widest button remains unbound and determines the final width for all buttons.
     * <p>
     * Note: Width calculation is based on text length (character count), not rendered text width. This works well for
     * uniform fonts but may not be pixel-perfect for proportional fonts.
     *
     * @param buttons list of buttons to make equal width; if null or empty, no action is taken
     * @param resetPrevious if true, unbinds any existing width property bindings before applying new ones
     */
    public static void makeEqualWidthByText(List<? extends ButtonBase> buttons, boolean resetPrevious) {
        ButtonBase maxWidthButton = buttons.stream()
                .max(Comparator.comparingInt(b -> b.getText().length()))
                .orElse(null);
        if (maxWidthButton != null) {
            for (var button : buttons) {
                if (resetPrevious) {
                    button.minWidthProperty().unbind();
                    button.maxWidthProperty().unbind();
                }
                if (button != maxWidthButton) {
                    button.minWidthProperty().bind(maxWidthButton.widthProperty());
                    button.maxWidthProperty().bind(maxWidthButton.widthProperty());
                }
            }
        }
    }

    /**
     * Makes all buttons equal width by binding their width to the button with the largest actual rendered width.
     * <p>
     * The method finds the button with the maximum current width and binds all other buttons' min/max width properties
     * to its width property. The widest button remains unbound and determines the final width for all buttons.
     * <p>
     * Important: This method requires buttons to be already laid out and rendered. Call {@code applyCss()} and
     * {@code layout()} on buttons before using this method, or ensure they are part of a visible scene graph.
     *
     * @param buttons list of buttons to make equal width; if null or empty, no action is taken
     * @param resetPrevious if true, unbinds any existing width property bindings before applying new ones
     */
    public static void makeEqualWidthBySize(List<? extends ButtonBase> buttons, boolean resetPrevious) {
        ButtonBase maxWidthButton = buttons.stream()
                .max(Comparator.comparingDouble(b -> b.getWidth()))
                .orElse(null);
        if (maxWidthButton != null) {
            for (var button : buttons) {
                if (resetPrevious) {
                    button.minWidthProperty().unbind();
                    button.maxWidthProperty().unbind();
                }
                if (button != maxWidthButton) {
                    button.minWidthProperty().bind(maxWidthButton.widthProperty());
                    button.maxWidthProperty().bind(maxWidthButton.widthProperty());
                }
            }
        }
    }

    /**
     * Makes buttons square by binding their width to match their height.
     * <p>
     * Binds min, max, and preferred width properties to the height property for each button. This ensures buttons
     * maintain a square aspect ratio as their height changes. The preferred width binding is necessary because JavaFX
     * layout calculations may use the preferred size to determine the final dimensions.
     *
     * @param buttons buttons to make square; if empty, no action is taken
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
     * Makes buttons square by binding their width to match their height.
     * <p>
     * Binds min, max, and preferred width properties to the height property for each button. This ensures buttons
     * maintain a square aspect ratio as their height changes. The preferred width binding is necessary because JavaFX
     * layout calculations may use the preferred size to determine the final dimensions.
     *
     * @param buttons buttons to make square; if empty, no action is taken
     */
    public static void makeSquareByHeight(List<ButtonBase> buttons) {
        makeSquareByHeight(buttons.toArray(new ButtonBase[buttons.size()]));
    }

    private ButtonUtils() {
        //empty
    }
}
