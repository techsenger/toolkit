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

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This key is used instead of {@code Enum<?>}. This key is used in classes with {@code public static final}
 * fields.We don't use classes and instance of because of JPMS.
 *
 * @author Pavel Castornii
 */
public class Key {

    private static final Logger logger = LoggerFactory.getLogger(Key.class);

    /**
     * Returns map of keys by field name. This method doesn't cache maps, so, every time new map is created from
     * the scratch.
     *
     * @param keysClass the class, that contains fields with keys
     * @param keyClass the class representing type of the keys
     * @return
     */
    public static <T extends Key> Map<String, T> getKeysByField(Class<?> keysClass, Class<T> keyClass) {
        List<Field> fields = ClassUtils.getStaticFinalFields(keysClass, true);
        var result = new HashMap<String, T>();
        try {
            for (var field : fields) {
                field.setAccessible(true);
                Object value = field.get(null);
                if (keyClass.isInstance(value)) {
                    result.put(field.getName(), (T) value);
                }
            }
        } catch (Exception ex) {
            logger.error("Error getting value of field", ex);
        }
        return result;
    }

    /**
     * Returns map of field names by key. This method doesn't cache maps, so, every time new map is created from
     *
     * @param keysClass the class, that contains fields with keys
     * @param keyClass the class representing type of the keys
     * @return
     */
    public static <T extends Key> Map<T, String> getFieldsByKey(Class<?> keysClass, Class<T> keyClass) {
        List<Field> fields = ClassUtils.getStaticFinalFields(keysClass, true);
        var result = new HashMap<T, String>();
        try {
            for (var field : fields) {
                field.setAccessible(true);
                Object value = field.get(null);
                if (keyClass.isInstance(value)) {
                    result.put((T) value, field.getName());
                }
            }
        } catch (Exception ex) {
            logger.error("Error getting value of field", ex);
        }
        return result;
    }

    private final String text;

    /**
     * Constructor.
     * @param text string value for this key.
     */
    public Key(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        if (this.text == null) {
            return super.toString();
        } else {
            return this.text;
        }
    }
}
