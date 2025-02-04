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
public class AnnotationModel implements Serializable {

    private ClassModel<? extends Class<? extends Enum<?>>> annotationType;

    public AnnotationModel() {

    }

    public ClassModel<? extends Class<? extends Enum<?>>> getAnnotationType() {
        return annotationType;
    }

    public void setAnnotationType(ClassModel<? extends Class<? extends Enum<?>>> annotationType) {
        this.annotationType = annotationType;
    }

    @Override
    public String toString() {
        //without using other fields that are references to reflection classes because of possible cyclic dependency
        return "DefaultAnnotationDescriptor{" + '}';
    }
}
