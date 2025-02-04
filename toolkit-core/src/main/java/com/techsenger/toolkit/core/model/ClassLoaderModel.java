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

package com.techsenger.toolkit.core.model;

import java.io.Serializable;

/**
 *
 * @author Pavel Castornii
 */
public class ClassLoaderModel implements Serializable {

    private String name;

    private ClassLoaderModel parent;

    private String toString;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ClassLoaderModel getParent() {
        return parent;
    }

    public void setParent(ClassLoaderModel parent) {
        this.parent = parent;
    }

    public String getToString() {
        return toString;
    }

    public void setToString(String toString) {
        this.toString = toString;
    }
}
