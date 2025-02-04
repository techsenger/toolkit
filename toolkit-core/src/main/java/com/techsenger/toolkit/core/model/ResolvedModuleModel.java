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
public class ResolvedModuleModel implements Serializable {

    private ConfigurationModel configuration;

    private String name;

    private Set<ResolvedModuleModel> reads;

    private ModuleReferenceModel reference;

    public ConfigurationModel getConfiguration() {
        return configuration;
    }

    public void setConfiguration(ConfigurationModel configuration) {
        this.configuration = configuration;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<ResolvedModuleModel> getReads() {
        return reads;
    }

    public void setReads(Set<ResolvedModuleModel> reads) {
        this.reads = reads;
    }

    public ModuleReferenceModel getReference() {
        return reference;
    }

    public void setReference(ModuleReferenceModel reference) {
        this.reference = reference;
    }
}
