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

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextInputControl;

/**
 * The methods in this class can be used for TextArea, TextField etc text inputs.
 * @author Pavel Castornii
 */
public final class TextInputUtils {

    /**
     * If this method is used then in css file {@code error-text-input} style
     * must be defined.
     * @param textInput is TextArea or TextField.
     */
    public static void markErrorContaining(final TextInputControl textInput) {
        textInput.getStyleClass().add("error-text-input");
        //we can't use lambda because we need reference to listener.
        textInput.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(final ObservableValue<? extends String> observable, final String oldValue,
                    final String newValue) {
                textInput.getStyleClass().remove("error-text-input");
                textInput.textProperty().removeListener(this);
            }
        });
    }

    public static void markErrorContaining(final ComboBox comboBoxInput) {
        comboBoxInput.getStyleClass().add("error-text-input");
        //we can't use lambda because we need reference to listener.
        comboBoxInput.valueProperty().addListener(new ChangeListener<Object>() {
            @Override
            public void changed(final ObservableValue<? extends Object> observable, final Object oldValue,
                    final Object newValue) {
                comboBoxInput.getStyleClass().remove("error-text-input");
                comboBoxInput.valueProperty().removeListener(this);
            }
        });
    }

    /**
     * JavaFX can request focus only for those text inputs that are visible and ready.
     * It is impossible to detect this moment, so we use this reflexive function.
     * @param textInput for which request focus is needed.
     */
    public static void requestFocus(final TextInputControl textInput) {
        Platform.runLater(() -> {
            if (!textInput.isFocused()) {
                textInput.requestFocus();
                textInput.selectEnd();
                requestFocus(textInput);
            }
        });
    }

    /**
     * Constructor.
     */
    private TextInputUtils() {
        //does nothign.
    }

}
