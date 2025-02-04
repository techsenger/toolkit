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

package com.techsenger.toolkit.fx.value;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 *
 * @author Pavel Castornii
 */
public final class ValueUtils {

    /**
    * <p>This method immediately calls the listener with null and the current value,
    * and then adds a listener to handle changes as they occur.</p>
    *
     * @param <T>
     * @param property
     * @param listener
     */
    public static <T> void callAndAddListener(ObservableValue<T> property, ChangeListener<? super T> listener) {
        T initialValue = property.getValue();
        listener.changed(property, null, initialValue);
        property.addListener(listener);
    }

    private ValueUtils() {
        //empty
    }
}
