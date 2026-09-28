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
import javafx.scene.control.TextField;

/**
 *
 * @author Pavel Castornii
 */
public final class TextFieldUtils {

    private static final Object FOCUS_JUST_GAINED_KEY = new Object();

    /**
     * Pins {@code textField}'s caret to the end of its text against the {@code TextFieldBehavior} resets
     * described below, without interfering with the user's own subsequent clicks or keyboard navigation.
     *
     * <p>Works around a JavaFX {@code TextFieldBehavior} quirk that resets a {@link TextField}'s caret whenever the
     * owning {@code Scene}'s focus owner changes. Confirmed by a stack trace from a real reset: moving focus away
     * from the field to any other node &mdash; e.g. some unrelated component in the same scene grabbing focus for
     * its own bookkeeping, the field itself need never have been genuinely focused &mdash; collapses its selection
     * down to caret position 0 ({@code selectRange(0, 0)}). {@code TextFieldBehavior}'s own source additionally
     * selects the whole text with the caret left at 0 ({@code selectRange(getLength(), 0)}) when the field gains
     * focus other than via a mouse click; that branch is only inferred from reading the source, not independently
     * reproduced the way the first one was. Either way, both would silently undo a caret deliberately positioned
     * at the end of the text, e.g. to show the tail of a long, read-only path.
     *
     *
     * @param textField the field to keep scrolled to the end of its text
     */
    public static void keepCaretAtEnd(TextField textField) {
        textField.focusedProperty().addListener((ov, oldV, newV) -> {
            if (newV) {
                textField.getProperties().put(FOCUS_JUST_GAINED_KEY, Boolean.TRUE);
                Platform.runLater(() -> textField.getProperties().remove(FOCUS_JUST_GAINED_KEY));
            }
        });
        textField.caretPositionProperty().addListener((ov, oldV, newV) -> {
            var focusJustGained = textField.getProperties().containsKey(FOCUS_JUST_GAINED_KEY);
            if (focusJustGained || (!textField.isFocused() && newV.intValue() != textField.getLength())) {
                textField.getProperties().remove(FOCUS_JUST_GAINED_KEY);
                textField.end();
            }
        });
    }

    private TextFieldUtils() {
        // empty
    }
}
