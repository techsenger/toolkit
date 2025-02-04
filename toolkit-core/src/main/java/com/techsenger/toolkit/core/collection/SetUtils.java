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

package com.techsenger.toolkit.core.collection;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Class which contains utilities for sets.
 * @author Pavel Castornii
 */
public final class SetUtils {

    /**
     * Creates new HashSet and adds objects to it.
     * @param <T> type of objects.
     * @param objects that will be added to set.
     * @return created set.
     */
    public static <T> HashSet<T> newHashSet(final T... objects) {
        HashSet<T> set = new HashSet();
        if (objects != null) {
            for (T object:objects) {
                set.add(object);
            }
        }
        return set;
    }

    /**
     * Returns empty set if passed set is null, otherwise return passed set. It allows not to check if collection
     * is null.
     *
     * @param <T>
     * @param set
     * @return
     */
    public static <T> Set<T> emptyIfNull(Set<T> set) {
        if (set == null) {
            return Collections.emptySet();
        }
        return set;
    }

    /**
     * Constructor.
     */
    private SetUtils() {
        //does nothing
    }
}
