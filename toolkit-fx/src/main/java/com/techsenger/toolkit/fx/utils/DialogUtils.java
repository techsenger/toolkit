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

import javafx.geometry.Rectangle2D;
import javafx.scene.control.Dialog;
import javafx.stage.Screen;
import javafx.stage.Window;

/**
 *
 * @author Pavel Castornii
 */
public final class DialogUtils {

    /**
     * Changes dialog position to be in the middle of the screen. Note: before calling this method
     * it is necessary to set dialog window width and height.
     *
     * Note: because of JavaFX bug when window width and height are set (setWidth/setHeight) window position
     * x and y position can't be set properly. Because of this we pass width and height as parameters.
     *
     * @param dialog
     * @param width dialog window width
     * @param height dialog window height
     */
    public static void center(Dialog<?> dialog, int width, int height) {
        final Window window = dialog.getDialogPane().getScene().getWindow();
        if (!Double.isNaN(window.getWidth()) || !Double.isNaN(window.getHeight())) {
            throw new IllegalStateException("Don't use setWidth/setHeight for dialog window."
                    + " Use minWidth/maxWidth and minHeight/maxHeight instead");
        }
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        window.setX((screenBounds.getWidth() - width) / 2); //window.getWidth()
        window.setY((screenBounds.getHeight() - height) / 2); //window.getHeight()
    }

    private DialogUtils() {
        //empty
    }
}
