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
 * This helper class wraps the variable of the functional interface type. This approach solves the problem with the
 * local variable initialization. So, it is just a wrapper for lambda when lambda is used with recursion.
 *
 * @author Pavel Castornii
 */
public class Recursive<T> {

    private T function;

    public T getFunction() {
        return function;
    }

    public void setFunction(T function) {
        this.function = function;
    }
}
