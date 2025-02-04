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

package com.techsenger.toolkit.core.jpms;

import com.techsenger.toolkit.core.StringUtils;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Pavel Castornii
 */
public final class ServiceUtils {

    private static final Logger logger = LoggerFactory.getLogger(ServiceUtils.class);

    /**
     * Returns fully qualified names of all service classes that modules in the layer provided. Parent layers module
     * services are NOT included. This method doesn't load providers and doesn't instantiate them.
     *
     * @param layer
     * @return
     */
    public static Set<String> services(final ModuleLayer layer) {
        return layer
        .modules()
        .stream()
        .map(Module::getDescriptor)
        .filter(m -> !m.provides().isEmpty())
        .flatMap((m) -> m.provides().stream())
        .map((p) -> p.service())
        .collect(Collectors.toCollection(HashSet::new));
    }

    /**
     * This is overloaded method of {@code providers(final ModuleLayer, final Class<?>)}.
     *
     * @param layer
     * @param type
     * @return
     */
    public static Set<String> providers(final ModuleLayer layer, final ServiceType<?> type) {
        return providers(layer, type.getServiceClass());
    }

    /**
     * Returns fully qualified names of all provider classes that modules in the layer provided. Parent layers module
     * providers are NOT included. This method doesn't load providers and doesn't instantiate them.
     *
     * @param layer
     * @return
     */
    public static Set<String> providers(final ModuleLayer layer, final Class<?> serviceClass) {
        return layer
        .modules()
        .stream()
        .map(Module::getDescriptor)
        .filter(m -> !m.provides().isEmpty())
        .flatMap((m) -> m.provides().stream())
        .filter((p) -> p.service().equals(serviceClass.getName()))
        .flatMap((p) -> p.providers().stream())
        .collect(Collectors.toCollection(HashSet::new));
    }

    /**
     * This is overloaded method of {@code loadProvider(final ModuleLayer, boolean, final Class<T>)}.
     *
     * @param <T>
     * @param layer
     * @param type
     * @return
     */
    public static <T> Optional<T> loadProvider(final ModuleLayer layer, final boolean parentLayersIncluded,
            final ServiceType<T> type) {
        Class<T> serviceClass = type.getServiceClass();
        return loadProvider(layer, parentLayersIncluded, serviceClass);
    }

    /**
     * This method finds providers from modules in the given module layer [and in parent module layers]. It's supposed
     * that there is only ONE provider of the service type in the whole layer graph that will be scanned. If there are
     * more than one providers, than exception is thrown. Method uses ServiceLoader.load(layer, serviceClass) so
     * all providers are instantiated.
     *
     * Note: we don't look up using class loader as alpha framework creates base and web layers with different
     * parent loaders, so we don't use ServiceLoader.load(serviceClass, loader)`.
     * @return
     */
    public static <T> Optional<T> loadProvider(final ModuleLayer layer, final boolean parentLayersIncluded,
            final Class<T> serviceClass) {
        provideServiceAccess(serviceClass);
        List<T> providers = new ArrayList<>();
        ServiceLoader<T> sl = ServiceLoader.load(layer, serviceClass);
        Iterator<T> iter = sl.iterator();
        while (iter.hasNext()) {
            T service = iter.next();
            if (!parentLayersIncluded) {
                if (service.getClass().getModule().getLayer() == layer) {
                    providers.add(service);
                }
            } else {
                providers.add(service);
            }
        }
        if (!providers.isEmpty()) {
            if (providers.size() != 1) {
                throw new IllegalStateException(StringUtils.format("Multiple services of {} found",
                        serviceClass.getName()));
            }
            logger.debug("Found service {} in layer [{}]; parent layers included: {}", serviceClass.getName(), layer,
                    parentLayersIncluded);
            return Optional.of(providers.get(0));
        } else {
            logger.debug("Didn't find service {} in layer [{}]", serviceClass.getName(), layer);
            return Optional.empty();
        }
    }

    /**
     * This is overloaded method of {@code loadProviders(ModuleLayer, boolean, Class<T>)}.
     *
     * @param <T>
     * @param layer
     * @param parentLayersIncluded
     * @param type
     * @return
     */
    public static <T> List<T> loadProviders(final ModuleLayer layer, final boolean parentLayersIncluded,
            final ServiceType<T> type) {
        Class<T> serviceClass = type.getServiceClass();
        return loadProviders(layer, parentLayersIncluded, serviceClass);
    }

    /**
     * Returns all providers of the type in the current layer or also from its parents in the order according
     * to ServiceLoader.load(layer, serviceClass) method, so read its API about service providers order.
     * Method uses ServiceLoader.load(layer, serviceClass) so all providers are instantiated.
     *
     * @param <T>
     * @param layer that is the leaf in the layer graph.
     * @param parentLayersIncluded if true, then providers of the parent layers will be included, otherwise not.
     * @param serviceClass
     * @return
     */
    public static <T> List<T> loadProviders(final ModuleLayer layer, final boolean parentLayersIncluded,
            final Class<T> serviceClass) {
        provideServiceAccess(serviceClass);
        List<T> providers = new ArrayList<>();
        ServiceLoader<T> sl = ServiceLoader.load(layer, serviceClass);
        Iterator<T> iter = sl.iterator();
        while (iter.hasNext()) {
            T provider = iter.next();
            if (parentLayersIncluded) {
                //add in any case
                providers.add(provider);
            } else {
                if (provider.getClass().getModule().getLayer() == layer) {
                    providers.add(provider);
                }
            }
        }
        logger.debug("Found {} services of {} in layer [{}]; parent layers included: {}", providers.size(),
                serviceClass.getName(), layer, parentLayersIncluded);
        return providers;
    }

    /**
     * If a module doesn't declare uses serviceClass in module-info so, when we try to get services using service() or
     * services() methods we get ServiceConfigurationError. To solve this problem we add use to Kit module manually.
     *
     * Note: this method can't be used outside this module. So, every other module must duplicate this code.
     * Otherwise Caused by: java.lang.IllegalCallerException: module kit.core != module alpha.core
     *
     * @param serviceClass
     */
    private static void provideServiceAccess(Class<?> serviceClass) {
        var module = ServiceUtils.class.getModule();
        if (!module.canUse(serviceClass)) {
            module.addUses(serviceClass);
            logger.trace("Module {} added use for the service {}", module.getName(),
                    serviceClass.getName());
        }
    }

    private ServiceUtils() {
        //empty
    }
}
