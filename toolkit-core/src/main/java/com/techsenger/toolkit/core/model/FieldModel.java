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
import java.util.List;

/**
 *
 * @author Pavel Castornii
 */
public class FieldModel implements Serializable {

    /**
     * Field name.
     */
    private String name;

    /**
     * Field type. If this a generic type than this field contains main type that is left after type erasure.
     */
    private ClassModel<?> type;

    /**
     * This field has only generic classes that are between &lt; and &gt; by order without ?, extends, super etc.
     */
    private List<ClassModel<?>> genericTypes;

    /**
     * This is text that was in source between &lt; and &gt;.
     */
    private String genericText;

    /**
     * Declared annotations.
     */
    private List<AnnotationModel> declaredAnnotations;

    /**
     * Constructor.
     * @param name
     */
    public FieldModel(String name) {
        this.name = name;
    }

    /**
     * Constructor.
     */
    public FieldModel() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ClassModel<?> getType() {
        return type;
    }

    public void setType(ClassModel<?> type) {
        this.type = type;
    }

    public List<ClassModel<?>> getGenericTypes() {
        return genericTypes;
    }

    public void setGenericTypes(List<ClassModel<?>> genericTypes) {
        this.genericTypes = genericTypes;
    }

    public String getGenericText() {
        return genericText;
    }

    public void setGenericText(String genericText) {
        this.genericText = genericText;
    }

    public List<AnnotationModel> getDeclaredAnnotations() {
        return declaredAnnotations;
    }

    public void setDeclaredAnnotations(List<AnnotationModel> declaredAnnotations) {
        this.declaredAnnotations = declaredAnnotations;
    }

    @Override
    public String toString() {
        //without using other fields that are references to reflection classes because of possible cyclic dependency
        return "DefaultFieldDescriptor{" + "name=" + name + ", genericText=" + genericText + '}';
    }
}
