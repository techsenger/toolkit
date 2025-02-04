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
import java.lang.module.ModuleDescriptor;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * @author Pavel Castornii
 */
public class ModuleDescriptorModel implements Serializable {

    public static class ExportsModel implements Serializable {

        private String source;

        private Set<String> targets;

        private Set<ModuleDescriptor.Exports.Modifier> modifiers;

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public Set<String> getTargets() {
            return targets;
        }

        public void setTargets(Set<String> targets) {
            this.targets = targets;
        }

        public Set<ModuleDescriptor.Exports.Modifier> getModifiers() {
            return modifiers;
        }

        public void setModifiers(Set<ModuleDescriptor.Exports.Modifier> modifiers) {
            this.modifiers = modifiers;
        }
    }

    public static class OpensModel implements Serializable {

        private String source;

        private Set<String> targets;

        private Set<ModuleDescriptor.Opens.Modifier> modifiers;

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public Set<String> getTargets() {
            return targets;
        }

        public void setTargets(Set<String> targets) {
            this.targets = targets;
        }

        public Set<ModuleDescriptor.Opens.Modifier> getModifiers() {
            return modifiers;
        }

        public void setModifiers(Set<ModuleDescriptor.Opens.Modifier> modifiers) {
            this.modifiers = modifiers;
        }
    }

    public static class ProvidesModel implements Serializable {

        private String service;

        private List<String> providers;

        public String getService() {
            return service;
        }

        public void setService(String service) {
            this.service = service;
        }

        public List<String> getProviders() {
            return providers;
        }

        public void setProviders(List<String> providers) {
            this.providers = providers;
        }
    }

    public static class RequiresModel implements Serializable {

        private String name;

        private Set<ModuleDescriptor.Requires.Modifier> modifiers;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Set<ModuleDescriptor.Requires.Modifier> getModifiers() {
            return modifiers;
        }

        public void setModifiers(Set<ModuleDescriptor.Requires.Modifier> modifiers) {
            this.modifiers = modifiers;
        }
    }

    /**
     * Creates model from descriptor instance.
     *
     * @param descriptor
     * @return
     */
    public static ModuleDescriptorModel from(ModuleDescriptor descriptor) {
        var model = new ModuleDescriptorModel();
        var exports = descriptor.exports().stream().map(e -> createExports(e)).collect(Collectors.toSet());
        model.setExports(exports);
        model.setAutomatic(descriptor.isAutomatic());
        model.setOpen(descriptor.isOpen());
        model.setName(descriptor.name());
        var opens = descriptor.opens().stream().map(o -> createOpens(o)).collect(Collectors.toSet());
        model.setOpens(opens);
        model.setPackages(descriptor.packages());
        var provides = descriptor.provides().stream().map(p -> createProvides(p)).collect(Collectors.toSet());
        model.setProvides(provides);
        var requires = descriptor.requires().stream().map(r -> createRequires(r)).collect(Collectors.toSet());
        model.setRequires(requires);
        model.setUses(descriptor.uses());
        model.setModifiers(descriptor.modifiers());
        if (descriptor.version().isPresent()) {
            model.setVersion(descriptor.version().get().toString());
        }
        return model;
    }

    private static ModuleDescriptorModel.ExportsModel createExports(ModuleDescriptor.Exports exports) {
        var model = new ModuleDescriptorModel.ExportsModel();
        model.setSource(exports.source());
        model.setTargets(exports.targets());
        model.setModifiers(exports.modifiers());
        return model;
    }

    private static ModuleDescriptorModel.OpensModel createOpens(ModuleDescriptor.Opens opens) {
        var model = new ModuleDescriptorModel.OpensModel();
        model.setSource(opens.source());
        model.setTargets(opens.targets());
        model.setModifiers(opens.modifiers());
        return model;
    }

    private static ModuleDescriptorModel.ProvidesModel createProvides(ModuleDescriptor.Provides provides) {
        var model = new ModuleDescriptorModel.ProvidesModel();
        model.setProviders(provides.providers());
        model.setService(provides.service());
        return model;
    }

    private static ModuleDescriptorModel.RequiresModel createRequires(ModuleDescriptor.Requires requires) {
        var model = new ModuleDescriptorModel.RequiresModel();
        model.setName(requires.name());
        model.setModifiers(requires.modifiers());
        return model;
    }

    private Set<ExportsModel> exports;

    private boolean automatic;

    private boolean open;

    private String name;

    private Set<OpensModel> opens;

    private Set<String> packages;

    private Set<ProvidesModel> provides;

    private Set<RequiresModel> requires;

    private Set<String> uses;

    private Set<ModuleDescriptor.Modifier> modifiers;

    private String version;

    public Set<ExportsModel> getExports() {
        return exports;
    }

    public void setExports(Set<ExportsModel> exports) {
        this.exports = exports;
    }

    public boolean isAutomatic() {
        return automatic;
    }

    public void setAutomatic(boolean automatic) {
        this.automatic = automatic;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<OpensModel> getOpens() {
        return opens;
    }

    public void setOpens(Set<OpensModel> opens) {
        this.opens = opens;
    }

    public Set<String> getPackages() {
        return packages;
    }

    public void setPackages(Set<String> packages) {
        this.packages = packages;
    }

    public Set<ProvidesModel> getProvides() {
        return provides;
    }

    public void setProvides(Set<ProvidesModel> provides) {
        this.provides = provides;
    }

    public Set<RequiresModel> getRequires() {
        return requires;
    }

    public void setRequires(Set<RequiresModel> requires) {
        this.requires = requires;
    }

    public Set<String> getUses() {
        return uses;
    }

    public void setUses(Set<String> uses) {
        this.uses = uses;
    }

    public Set<ModuleDescriptor.Modifier> getModifiers() {
        return modifiers;
    }

    public void setModifiers(Set<ModuleDescriptor.Modifier> modifiers) {
        this.modifiers = modifiers;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
