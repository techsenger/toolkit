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

import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 *
 * @author Pavel Castornii
 */
public final class ImageUtils {

    /**
     * Creates an icon image from font icon.
     *
     * @param icon
     * @param font
     * @param fgColor the color of the icon.
     * @param bgColor the color of the image.
     * @param x the x coordinate of the icon in the image.
     * @param y the y coordinate of the icon in the image.
     * @param width the width of the image.
     * @param height the height of the image.
     * @return
     */
    public static WritableImage createIcon(String icon, Font font, Color fgColor, Color bgColor, double x, double y,
            int width, int height) {
        final Canvas canvas = new Canvas(width, height);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(bgColor);
        gc.fillRect(0, 0, width, height);
        gc.setFill(fgColor);
        gc.setFont(font);
        gc.fillText(icon, x, y);
        WritableImage image = new WritableImage(width, height);
        SnapshotParameters sp = new SnapshotParameters();
        sp.setFill(Color.TRANSPARENT);
        canvas.snapshot(sp, image);
        return image;
    }

    private ImageUtils() {
        //empty
    }
}
