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

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.text.Text;

/**
 *
 * @author Pavel Castornii
 */
public final class TextUtils {

    /**
     * Calculates width of the text.
     * @param text is the text which width is required.
     * @return width of the text.
     */
    public static double calculateWidth(final String text) {
        final Text tempText = new Text(text);
        new Scene(new Group(tempText));
        tempText.applyCss();
        return tempText.getLayoutBounds().getWidth();
    }

    /**
     * Constructor.
     */
    private TextUtils() {
    }

}
