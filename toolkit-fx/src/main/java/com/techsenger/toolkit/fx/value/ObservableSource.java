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

/**
 *
 * @author Pavel Castornii
 */
public interface ObservableSource<T> {

    /**
     * Notifies listeners about a new value.
     *
     * @param value
     */
    void next(T value);

    /**
     * Returns the last value that listeners were notified about.
     * @return
     */
    T getValue();

    /**
     * Returns true if next() method has been called and false otherwise.
     *
     * @return
     */
    boolean hasValue();

    /**
     * Adds a listener that will receive notifications about new values.
     *
     * @param listener
     */
    void addListener(SourceListener<T> listener);

    /**
     * Removes a listener, so it will no longer receive notifications.
     *
     * @param listener
     */
    void removeListener(SourceListener<T> listener);
}
