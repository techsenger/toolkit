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

package com.techsenger.toolkit.jpa;

import com.techsenger.toolkit.core.jpms.ModuleUtils;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.io.File;
import java.io.IOException;
import java.lang.module.ModuleReference;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import org.hibernate.cfg.AvailableSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Pavel Castornii
 */
public final class EntityManagerFactoryProvider {

    private static final class ModuleClassLoaderWrapper extends ClassLoader {

        private static final Logger logger = LoggerFactory.getLogger(ModuleClassLoaderWrapper.class);

        private final Module module;

        ModuleClassLoaderWrapper(Module module) {
            super(module.getClassLoader());
            this.module = module;
        }

        @Override
        public Enumeration<URL> getResources(String name) throws IOException {
            if (name.equals("META-INF/persistence.xml")) {
                try {
                    ModuleReference reference = ModuleUtils.getReference(module);
                    URI moduleUri = reference.location().orElseThrow();
                    URL persistenceUrl;
                    //if module location is folder, for example for test modules (test-classes)
                    if ("file".equals(moduleUri.toURL().getProtocol()) && new File(moduleUri).isDirectory()) {
                        // it's a directory
                        persistenceUrl = new URL(
                            moduleUri
                            + "META-INF"
                            + File.separator
                            + "persistence.xml");
                    } else {
                        persistenceUrl = new URL("jar:"
                            + reference.location().orElseThrow() + "!/META-INF/persistence.xml");
                    }
                    logger.debug("Persistence.xml file url is {}", persistenceUrl);
                    return Collections.enumeration(Arrays.asList(persistenceUrl));
                } catch (Exception ex) {
                    logger.error("Error building persistence.xml url", ex);
                    return null;
                }
            } else {
                return super.getResources(name);
            }
        }
    }

    private static final Logger logger = LoggerFactory.getLogger(EntityManagerFactoryProvider.class);

    private static volatile boolean initialized = false;

    private static EntityManagerFactory factory = null;

    /**
     * Node: if module is a folder, for example, test-classes then hibernate works differently with
     * it. It seems that this case it look for classes differently.
     *
     * How it works. We pass in properties list of classloaders. Hibernate to this list will add Application
     * ClassLoader so it can find any other persistence.xml that are not in our module. Hibernate will
     * parse all found xml files (so, they can't have xml errors and must be valid to schema). After parsing
     * hibernate will choose the persistence unit according to the name we passed.
     *
     * We pass our moduleClassLoaderWrapper just to let hibernate find our persistence.xml and load entities using it.
     *
     * About TC_CLASSLOADER see these:
     *
     * https://hibernate.atlassian.net/browse/HHH-14306?oldIssueView=true
     * https://github.com/hibernate/hibernate-orm/commit/4666d774e42266da90d6fc132193454020b37019
     * https://docs.jboss.org/hibernate/orm//current/javadocs/org/hibernate/cfg/EnvironmentSettings.html
     *
     * Note: if you need all exceptions from JPA provider set in LOG4J2 "all" level.
     *
     * @param module - module, that contains persistenceUnit
     * @param persistenceUnit
     * @param properties
     */
    public static synchronized void initializeEntityManagerFactory(Module module,
            String persistenceUnit, Map<String, Object> properties) throws Exception {
        if (initialized) {
            return;
        }
        if (properties == null) {
            properties = new HashMap<>();
        }
        Collection<ClassLoader> classLoaders = new ArrayList<>();
        properties.put(AvailableSettings.CLASSLOADERS, classLoaders);
        properties.put(AvailableSettings.TC_CLASSLOADER, "never");
        var moduleClassLoaderWrapper = new ModuleClassLoaderWrapper(module);
        classLoaders.add(moduleClassLoaderWrapper);
        try {
            factory = Persistence.createEntityManagerFactory(persistenceUnit, properties);
            initialized = true;
            logger.info("EntityManagerFactory for persistence unit {} was created", persistenceUnit);
        } catch (Throwable ex) {
            logger.error("Error creating EntityManagerFactory for persistence unit {} in module {}",
                    persistenceUnit, module, ex);
            throw new Exception("Error initializing EntityManagerFactory");
        }
    }

    public static synchronized void deinitializeEntityManagerFactory() {
        if (!initialized) {
            return;
        }
        if (factory != null) {
            var properties = factory.getProperties();
            String persistenceUnit = properties.get("hibernate.persistenceUnitName").toString();
            try {
                factory.close();
                initialized = false;
                logger.info("EntityManagerFactory for persistence unit {} was closed", persistenceUnit);
            } catch (Throwable ex) {
                logger.error("Error closing EntityManagerFactory for persistence unit {}", persistenceUnit, ex);
            }
        }
    }

    public static EntityManagerFactory factory() {
        return factory;
    }

    private EntityManagerFactoryProvider() {
        //empty
    }
}
