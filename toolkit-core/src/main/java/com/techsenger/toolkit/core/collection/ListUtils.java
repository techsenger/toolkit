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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Class which contains utilities for lists.
 * @author Pavel Castornii
 */
public final class ListUtils {

    /**
     * Creates ArrayList and adds objects to it.
     * @param <T> type of objects.
     * @param objects that will be added to list.
     * @return created list.
     */
    public static <T> ArrayList<T> newArrayList(final T... objects) {
        ArrayList<T> list = new ArrayList();
        for (T object:objects) {
            list.add(object);
        }
        return list;
    }

    /**
     * Creates LinkedList and adds objects to it.
     * @param <T> type of objects.
     * @param objects that will be added to list.
     * @return created list.
     */
    public static <T> LinkedList<T> newLinkedList(final T... objects) {
        LinkedList<T> list = new LinkedList();
        for (T object:objects) {
            list.add(object);
        }
        return list;
    }

    /**
     * Divides list into sublists by partition size. It can be used to generate columns
     * of the tables.
     * @param <T>
     * @param list
     * @param partitionSize
     * @return
     */
    public static <T> List<List<T>> partition(final List<T> list, int partitionSize) {
        List<List<T>> parts = new ArrayList<List<T>>();
        final int initialListSize = list.size();
        for (int i = 0; i < initialListSize; i += partitionSize) {
            parts.add(new ArrayList<T>(
                list.subList(i, Math.min(initialListSize, i + partitionSize)))
            );
        }
        return parts;
    }

    /**
     * Finds duplicates in list and these duplicates returned in set.
     * @param <T>
     * @param listContainingDuplicates
     * @return
     */
    public static <T> Set<T> findDuplicates(List<T> listContainingDuplicates) {
        final Set<T> setToReturn = new HashSet<>();
        final Set<T> set1 = new HashSet<>();
        for (T element : listContainingDuplicates) {
            if (!set1.add(element)) {
                setToReturn.add(element);
            }
        }
        return setToReturn;
    }

    /**
     * Returns empty list if passed list is null, otherwise return passed list. It allows not to check if collection
     * is null.
     *
     * @param <T>
     * @param list
     * @return
     */
    public static <T> List<T> emptyIfNull(List<T> list) {
        if (list == null) {
            return Collections.emptyList();
        }
        return list;
    }

    /**
     * Constructor.
     */
    private ListUtils() {
        //does nothing

    }
}
