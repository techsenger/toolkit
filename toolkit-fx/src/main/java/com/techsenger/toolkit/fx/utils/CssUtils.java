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

/**
 * Utilities for working with inline CSS styles.
 *
 * <p>The declarations are split by {@code ;}, so the values that contain it themselves, such as
 * {@code url("data:image/png;base64,...")}, are not supported.
 *
 * @author Pavel Castornii
 */
public final class CssUtils {

    /**
     * Sets the declaration in the style string, leaving all other declarations intact. An existing declaration is
     * replaced in its place, a new one is added to the end, so repeated calls with the same arguments give the same
     * string.
     *
     * @param style the style string, may be {@code null} or blank
     * @param property the name of the property, for example {@code -fx-fill}
     * @param value the value of the property, or {@code null} to remove the declaration
     * @return the new style string, or {@code null} if no declarations are left
     */
    public static String setProperty(String style, String property, String value) {
        var declarations = new ArrayList<String>();
        boolean found = false;
        if (style != null) {
            for (String part : style.split(";")) {
                var declaration = part.trim();
                if (declaration.isEmpty()) {
                    continue;
                }
                if (!getName(declaration).equals(property)) {
                    declarations.add(declaration);
                } else if (!found) {
                    found = true;
                    if (value != null) {
                        declarations.add(property + ": " + value);
                    }
                }
            }
        }
        if (!found && value != null) {
            declarations.add(property + ": " + value);
        }
        return declarations.isEmpty() ? null : String.join("; ", declarations) + ";";
    }

    private static String getName(String declaration) {
        int index = declaration.indexOf(':');
        return index < 0 ? declaration : declaration.substring(0, index).trim();
    }

    private CssUtils() {
        // empty
    }
}
