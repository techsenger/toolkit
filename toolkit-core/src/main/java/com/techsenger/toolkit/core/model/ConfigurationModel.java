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

/**
 *
 * @author Pavel Castornii
 */
public class ConfigurationModel implements Serializable {

    private Map<String, ResolvedModuleModel> modulesByName;

    private List<ConfigurationModel> parents;

    public ConfigurationModel() {

    }

    public Map<String, ResolvedModuleModel> getModulesByName() {
        return modulesByName;
    }

    public void setModulesByName(Map<String, ResolvedModuleModel> modulesByName) {
        this.modulesByName = modulesByName;
    }

    public List<ConfigurationModel> getParents() {
        return parents;
    }

    public void setParents(List<ConfigurationModel> parents) {
        this.parents = parents;
    }
}
