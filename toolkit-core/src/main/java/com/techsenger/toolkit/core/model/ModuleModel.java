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
import java.util.Set;

/**
 *
 * @author Pavel Castornii
 */
public class ModuleModel implements Serializable {

    private AnnotationModel[] annotations;

    private ModuleDescriptorModel descriptor;

    private ModuleLayerModel layer;

    private String name;

    private Set<String> packages;

    private boolean named;

    private ClassLoaderModel classLoader;

    public AnnotationModel[] getAnnotations() {
        return annotations;
    }

    public void setAnnotations(AnnotationModel[] annotations) {
        this.annotations = annotations;
    }

    public ModuleDescriptorModel getDescriptor() {
        return descriptor;
    }

    public void setDescriptor(ModuleDescriptorModel descriptor) {
        this.descriptor = descriptor;
    }

    public ModuleLayerModel getLayer() {
        return layer;
    }

    public void setLayer(ModuleLayerModel layer) {
        this.layer = layer;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<String> getPackages() {
        return packages;
    }

    public void setPackages(Set<String> packages) {
        this.packages = packages;
    }

    public boolean isNamed() {
        return named;
    }

    public void setNamed(boolean named) {
        this.named = named;
    }

    public ClassLoaderModel getClassLoader() {
        return classLoader;
    }

    public void setClassLoader(ClassLoaderModel classLoader) {
        this.classLoader = classLoader;
    }
}
