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
import java.util.Map;
import java.util.Objects;

/**
 *
 * @author Pavel Castornii
 */
public class ClassModel<T extends Class<?>> implements Serializable {

    /**
     * Reserved word.
     */
    private PackageModel pakage;

    private Map<String, String> importsBySimpleName;

    /**
     * Reserved word.
     */
    private boolean abstrct;

    private String simpleName;

    private ClassModel<? super T> superclass;

    private List<FieldModel> declaredFields;

    /**
     * Declared annotations - annotations declared only on the class and ignores inherited annotations by class.
     */
    private List<AnnotationModel> declaredAnnotations;

    public ClassModel(String simpleName) {
        this.simpleName = simpleName;
    }

    public ClassModel() {

    }

    public PackageModel getPakage() {
        return pakage;
    }

    public void setPakage(PackageModel pakage) {
        this.pakage = pakage;
    }

    public String getSimpleName() {
        return simpleName;
    }

    public void setSimpleName(String simpleName) {
        this.simpleName = simpleName;
    }

    public List<FieldModel> getDeclaredFields() {
        return declaredFields;
    }

    public void setDeclaredFields(List<FieldModel> declaredFields) {
        this.declaredFields = declaredFields;
    }

    public ClassModel<? super T> getSuperclass() {
        return superclass;
    }

    public void setSuperclass(ClassModel<? super T> superclass) {
        this.superclass = superclass;
    }

    public String getName() {
        if (this.getPakage() != null) {
            return this.getPakage().getName() + "." + this.simpleName;
        } else {
            return this.simpleName; //default package?
        }
    }

    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + Objects.hashCode(this.pakage);
        hash = 79 * hash + Objects.hashCode(this.simpleName);
        return hash;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ClassModel<?> other = (ClassModel<?>) obj;
        if (!Objects.equals(this.simpleName, other.getSimpleName())) {
            return false;
        }
        if (!Objects.equals(this.pakage, other.getPakage())) {
            return false;
        }
        return true;
    }

    public boolean isAbstrct() {
        return abstrct;
    }

    public void setAbstrct(boolean abstrct) {
        this.abstrct = abstrct;
    }

    public Map<String, String> getImportsBySimpleName() {
        return importsBySimpleName;
    }

    public void setImportsBySimpleName(Map<String, String> importsBySimpleName) {
        this.importsBySimpleName = importsBySimpleName;
    }

    public List<AnnotationModel> getDeclaredAnnotations() {
        return declaredAnnotations;
    }

    public void setDeclaredAnnotations(List<AnnotationModel> declaredAnnotations) {
        this.declaredAnnotations = declaredAnnotations;
    }

    public String toString() {
        //without using other fields that are references to reflection classes because of possible cyclic dependency
        return "DefaultClassDescriptor{" + "importsBySimpleName=" + importsBySimpleName
                + ", abstract=" + abstrct + ", simpleName=" + simpleName + '}';
    }
}
