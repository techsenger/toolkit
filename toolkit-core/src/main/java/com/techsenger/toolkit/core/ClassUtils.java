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

package com.techsenger.toolkit.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class which contain utilities for working with classes.
 * @author Pavel Castornii
 */
public final class ClassUtils {

    private static final Logger logger = LoggerFactory.getLogger(ClassUtils.class);

    /**
     * Returns all public and protected method of class and all its super classes.
     * @param klass from which methods will be extracted.
     * @return list of methods.
     */
    public static List<Method> getAccessibleMethods(final Class klass) {
        Class aClass = klass;
        List<Method> result = new ArrayList<Method>();
        while (aClass != null) {
            for (Method method : aClass.getDeclaredMethods()) {
                int modifiers = method.getModifiers();
                if (Modifier.isPublic(modifiers) || Modifier.isProtected(modifiers)) {
                    result.add(method);
                }
            }
            aClass = aClass.getSuperclass();
        }
        return result;
    }

    /**
     * Returns a set with the passed class and all its super classes and interfaces.
     * @param klass which will be worked.
     * @return set of classes which are super classes of this klass.
     */
    public static Set<Class<?>> getHierarchyClasses(final Class<?> klass) {
        LinkedHashSet<Class<?>> result = new LinkedHashSet<Class<?>>();
        result.add(klass);
        getInheritance(klass, result);
        return result;
    }

    /**
     * Returns all methods from class and its super classes annotated with certain annotation.
     * To get annotation from method use Annotation annotInstance = method.getAnnotation(annotation);
     * @param klass from which methods will be extracted.
     * @param annotation filter.
     * @return list of methods.
     */
    public static List<Method> getMethodsAnnotatedWith(final Class<?> klass,
            final Class<? extends Annotation> annotation) {
        final List<Method> methods = new ArrayList<Method>();
        Class<?> k = klass;
        // need to iterated thought hierarchy in order to retrieve methods from above the current instance
        while (k != Object.class) {
            // iterate though the list of methods declared in the class represented by klass variable,
            //and add those annotated with the specified annotation
            final List<Method> allMethods = new ArrayList<Method>(Arrays.asList(k.getDeclaredMethods()));
            for (final Method method : allMethods) {
                if (method.isAnnotationPresent(annotation)) {
                    // TODO process annotInstance
                    methods.add(method);
                }
            }
            // move to the upper class in the hierarchy in search for more methods
            k = k.getSuperclass();
        }
        return methods;
    }

    /**
     * Returns all fields of class including fields of superclasses.
     *
     * @param type of the class.
     * @return all fields.
     */
    public static List<Field> getFields(final Class<?> type) {
        return getAllFields(new ArrayList<>(), type);
    }

    /**
     * Returns all static final fields of class including fields of superclasses and all interfaces.
     *
     * @param type of the class.
     * @return all fields.
     */
    public static List<Field> getStaticFinalFields(final Class<?> type, boolean interfacesIncluded) {
        if (interfacesIncluded) {
            List<Field> fields = new ArrayList<>();
            var classes = getHierarchyClasses(type);
            for (var c : classes) {
                var classFields = c.getDeclaredFields();
                for (var field : classFields) {
                    if (Modifier.isStatic(field.getModifiers()) && Modifier.isFinal(field.getModifiers())) {
                        fields.add(field);
                    }
                }
            }
            return fields;
        } else {
            return getAllStaticFinalFields(new ArrayList<>(), type);
        }
    }

    /**
     * Returns map of all final static fields as keys and their values. Parent classes fields are also included.
     *
     * @param type
     * @return
     */
    public static Map<String, Object> getStaticFinalFieldMap(Class<?> type, boolean interfacesIncluded) {
        List<Field> fields = getStaticFinalFields(type, interfacesIncluded);
        var result = new HashMap<String, Object>();
        try {
            for (var field : fields) {
                field.setAccessible(true);
                Object value = field.get(null);
                result.put(field.getName(), value);
            }
        } catch (Exception ex) {
            logger.error("Error getting value of field", ex);
        }
        return result;
    }

    /**
     * Returns package name from class canonical name.
     *
     * @param classCanonicalName
     * @return empty string for default package.
     */
    public static String getPackageName(String classCanonicalName) {
        int index = classCanonicalName.lastIndexOf(".");
        if (index != -1) {
            return classCanonicalName.substring(0, index);
        } else {
            return "";
        }
    }

    /**
     * Returns class simple name from class canonical name.
     *
     * @param classCanonicalName
     * @return
     */
    public static String getSimpleName(String classCanonicalName) {
        int index = classCanonicalName.lastIndexOf(".");
        if (index != -1) {
            return classCanonicalName.substring(index + 1);
        } else {
            return classCanonicalName;
        }
    }

    /**
     * Returns inheritance of type.
     * @param klass from which super classes will be derived.
     * @param result set to which derived super classes will be added.
     */
    private static void getInheritance(final Class<?> klass, final Set<Class<?>> result) {
        Class<?> superclass = getSuperclass(klass);
        if (superclass != null) {
            result.add(superclass);
            getInheritance(superclass, result);
        }
        getInterfaceInheritance(klass, result);
    }

    /**
     * Returns interfaces that the type inherits from.
     * @param in class from which interfaces will be derived.
     * @param result set to which derived interfaces will be added.
     */
    private static void getInterfaceInheritance(final Class<?> in, final Set<Class<?>> result) {
        for (Class<?> c : in.getInterfaces()) {
            result.add(c);
            getInterfaceInheritance(c, result);
        }
    }

    /**
     * Returns superclass of class.
     * @param klass the class from which super class will be derived.
     * @return super class.
     */
    private static Class<?> getSuperclass(final Class<?> klass) {
        if (klass == null) {
            return null;
        }
        if (klass.isArray() && klass != Object[].class) {
            Class<?> type = klass.getComponentType();
            while (type.isArray()) {
                type = type.getComponentType();
            }
            return type;
        }
        return klass.getSuperclass();
    }

    /**
     * Returns all fields of class including fields of superclasses.
     * @param fields list that will be filled
     * @param type of the next class
     * @return all fields
     */
    private static List<Field> getAllFields(final List<Field> fields, final Class<?> type) {
        fields.addAll(Arrays.asList(type.getDeclaredFields()));
        if (type.getSuperclass() != null) {
            getAllFields(fields, type.getSuperclass());
        }
        return fields;
    }

    /**
     * Returns all static final fields of class including fields of superclasses.
     * @param fields list that will be filled
     * @param type of the next class
     * @return all fields
     */
    private static List<Field> getAllStaticFinalFields(final List<Field> fields, final Class<?> type) {
        Field[] declaredFields = type.getDeclaredFields();
        for (Field field : declaredFields) {
            if (Modifier.isStatic(field.getModifiers()) && Modifier.isFinal(field.getModifiers())) {
                fields.add(field);
            }
        }
        if (type.getSuperclass() != null) {
            getAllStaticFinalFields(fields, type.getSuperclass());
        }
        return fields;
    }

    /**
     * Constructor.
     */
    private ClassUtils() {
        //do nothing
    }
}
