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

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.module.ModuleReader;
import java.lang.module.ModuleReference;
import java.lang.module.ResolvedModule;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Pavel Castornii
 */
public final class ModuleUtils {

    private static final Logger logger = LoggerFactory.getLogger(ModuleUtils.class);

    /**
     * Finds module by name only in boot layer.
     * @param string
     * @return
     */
    public static Module findModule(String string) {
        Module module = ModuleLayer.boot().findModule(string).orElse(null);
        return module;
    }

    /**
     * Finds module by name in specific layer.
     * @param string
     * @param layer
     * @return
     */
    public static Module findModule(String string, ModuleLayer layer) {
        Module module = layer.findModule(string).orElse(null);
        return module;
    }

    /**
     * Returns path of the module.
     * @param module which path is required.
     * @return path.
     */
    public static Path getPath(final ResolvedModule module) {
        return Paths.get(module.reference().location().get());
    }

    /**
     * Returns path of the module.
     * @param module which path is required.
     * @return path.
     */
    public static Path getPath(final Module module) {
        ModuleReference reference = getReference(module);
        return Paths.get(reference.location().get());
    }

    /**
     * Returns reference of the module.
     * @param module which reference is required.
     * @return reference.
     */
    public static ModuleReference getReference(final Module module) {
        return module
                .getLayer()
                .configuration()
                .findModule(module.getName())
                .map(ResolvedModule::reference)
                .orElseThrow(() -> new RuntimeException("should not happen"));
    }

    /**
     * Returns resolved module.
     * @param module
     * @return
     */
    public static Optional<ResolvedModule> getResolvedModule(final Module module) {
        return module
                .getLayer()
                .configuration()
                .findModule(module.getName());
    }

    /**
     * List of all content of the module (manifest, classes, resources etc).
     * @param module which content is required.
     * @return list of string.
     */
    public static List<String> getContent(final Module module) throws IOException {
        ModuleReference reference = getReference(module);
        return reference.open().list().collect(Collectors.toList());
    }

    /**
     * Loads class from module.
     * @param module from which class will be loaded.
     * @param path is the string path to class in module.
     * @return loaded class.
     * @throws ClassNotFoundException if class not found.
     */
    public static Class loadClass(final Module module, final String path)
        throws ClassNotFoundException {
        String className = path.replaceAll("/", ".");
        className = className.substring(0, className.length() - 6);
        return module.getClassLoader().loadClass(className);
    }

    /**
     * Find and returns list of classes annotated with annotation.
     * @param module
     * @param annotationClass
     * @return empty list or list with annotated classes.
     */
    public static List<Class<?>> findClassesAnnotatedWith(final Module module,
            final Class<? extends Annotation> annotationClass) {
        List<Class<?>> classes = new ArrayList<>();
        ModuleReference moduleReference = ModuleUtils.getReference(module);
        ClassLoader cl = module.getClassLoader();
        try (ModuleReader moduleReader = moduleReference.open()) {
                Stream<String> stream = moduleReader.list();
                stream.forEach(filePath -> {
                    if (filePath.endsWith(".class") && !filePath.equals("module-info.class")) {
                        try {
                            String fileName = filePath.substring(0, filePath.length() - 6);
                            fileName = fileName.replaceAll("/", ".");
                            Class<?> loadedClass = cl.loadClass(fileName);
                            if (loadedClass.isAnnotationPresent(annotationClass)) {
                                classes.add(loadedClass);
                            }

                        } catch (ClassNotFoundException ex) {
                            logger.error("Error loading class", ex);
                        }
                    }
                });
                stream.close();
        } catch (IOException ex) {
            logger.error("Error reading module {}", module.getName(), ex);
        }
        return classes;
    }

    /**
     * Generic version of findClassesAnnotatedWith, invokes non generic method.
     * @param <T>
     * @param module
     * @param annotationClass
     * @param classType the type of the classes we are expecting to be found
     * @return
     */
    public static <T> List<Class<T>> findClassesAnnotatedWith(final Module module,
            final Class<? extends Annotation> annotationClass, Class<T> classType) {
        return (List) findClassesAnnotatedWith(module, annotationClass);
    }

    /**
     * Prints information about this module.
     * @param module
     */
    public static void print(Module module) {
        System.out.println("");
        System.out.println("MODULE NAME: " + module.getName());
        System.out.println("");
        System.out.println("MODULE READS:");
        ResolvedModule rm = getResolvedModule(module).get();
        rm.reads().stream().forEach(System.out::println);
        System.out.println("");
        System.out.println("MODULE REQUIRES:");
        module.getDescriptor().requires().stream().forEach(System.out::println);
        System.out.println("");
        System.out.println("MODULE OPENS:");
        module.getDescriptor().opens().stream()
                .map((o) -> o.source() + " to " + o.targets())
                .forEach(System.out::println);
        System.out.println("");
        System.out.println("MODULE PROVIDES:");
        module.getDescriptor().provides().stream().map((p) -> p.service() + " with" + p.providers());
        System.out.println("");
        System.out.println("MODULE PACKAGES:");
        module.getDescriptor().packages().stream().forEach(System.out::println);
        System.out.println("");
    }

    /**
     * Returns true if this module is a standard java module and false if it is a custom module.
     *
     * @param name
     * @return
     */
    public static boolean isStandard(String name) {
        return name.startsWith("java") || name.startsWith("jdk");
    }

    private ModuleUtils() {
        //empty
    }
}
