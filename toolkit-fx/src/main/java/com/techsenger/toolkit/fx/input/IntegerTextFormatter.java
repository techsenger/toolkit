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

package com.techsenger.toolkit.fx.input;

import java.util.function.UnaryOperator;
import java.util.regex.Pattern;
import javafx.scene.control.TextFormatter;
import javafx.util.converter.IntegerStringConverter;

/**
 *
 * @author Pavel Castornii
 */
public class IntegerTextFormatter extends TextFormatter<Integer> {

    private static UnaryOperator<TextFormatter.Change> createUnaryOperator(boolean signed) {
        Pattern pattern = null;
        if (signed) {
            pattern = Pattern.compile("-?([1-9][0-9]*)?");
        } else {
            pattern = Pattern.compile("([1-9][0-9]*)?");
        }
        var p = pattern;
        UnaryOperator<TextFormatter.Change> integerFilter = change -> {
            String newText = change.getControlNewText();
            if (p.matcher(newText).matches()) {
                return change;
            }
            return null;
        };
        return integerFilter;
    }

    public IntegerTextFormatter(Integer initialValue, boolean signed) {
        super(new IntegerStringConverter(), initialValue, createUnaryOperator(signed));
    }
}
