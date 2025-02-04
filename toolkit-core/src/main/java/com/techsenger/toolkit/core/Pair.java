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

package com.techsenger.toolkit.core;

/**
 * This generic class is used when it is necessary to return from method two values.
 *
 * @author Pavel Castornii
 * @param <T> Type of the first element of pair.
 * @param <U> Type of the second element of pair.
 */
 public final class Pair<T, U> {

    /**
     * The first element in pair.
     */
    private final T first;

    /**
     * The second element in pair.
     */
    private final U second;

    /**
     * Constructor.
     * @param first element of pair.
     * @param second element of pair.
     */
    public Pair(final T first, final U second) {
        this.first = first;
        this.second = second;
    }

    /**
     * Returns the first element.
     * @return first element.
     */
    public T getFirst() {
        return first;
    }

    /**
     * Returns the second element.
     * @return second element.
     */
    public U getSecond() {
        return second;
    }

    @Override
    public String toString() {
        return "Pair{" + "first=" + first + ", second=" + second + '}';
    }
 }
