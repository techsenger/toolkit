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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;

/**
 * Class which contains utilities for working with maps.
 * @author Pavel Castornii
 */
public final class MapUtils {

    /**
     * Suppose we have two maps with entities by uuid, and we need to find entities that:<ol>
     * <li>exist only in first map,
     * <li>entities that exist with two map (if you need list with corresponding index then just sort items by uuid)
     * <li>entities that exist only in the second map. So, we use this DiffFinder.
     * </ol>
     * For this kind of tasks we need map difference.
     * @param <K>
     * @param <V>
     */
    public static final class MapDifference<K, V> {

        /**
         * Elements, existing only in the first map.
         */
        private final Map<K, V> firstMapDifferentElements;

        /**
         * Elements, existing only in the second map.
         */
        private final Map<K, V> secondMapDifferentElements;

        /**
         * Elements from the first map, when there are elements in the second map with the same key.
         */
        private final Map<K, V> firstMapIntersectingElements;

        /**
         * Elements from the second map, when there are elements in the first map with the same key.
         */
        private final Map<K, V> secondMapIntersectingElements;

        /**
         * Uuids of the entities that exists in both maps.
         */
        private final Set<K> intersectingKeys;

        public MapDifference(Map<K, V> firstMapDifferentElements,
                Map<K, V> secondMapDifferentElements,
                Map<K, V> firstMapIntersectingElements,
                Map<K, V> secondMapIntersectingElements, Set<K> intersectingKeys) {
            this.firstMapDifferentElements = firstMapDifferentElements;
            this.secondMapDifferentElements = secondMapDifferentElements;
            this.firstMapIntersectingElements = firstMapIntersectingElements;
            this.secondMapIntersectingElements = secondMapIntersectingElements;
            this.intersectingKeys = intersectingKeys;
        }

        public Map<K, V> getFirstMapDifferentElements() {
            return firstMapDifferentElements;
        }

        public Map<K, V> getSecondMapDifferentElements() {
            return secondMapDifferentElements;
        }

        public Map<K, V> getFirstMapIntersectingElements() {
            return firstMapIntersectingElements;
        }

        public Map<K, V> getSecondMapIntersectingElements() {
            return secondMapIntersectingElements;
        }

        public Set<K> getIntersectingKeys() {
            return intersectingKeys;
        }

        public boolean differenceExists() {
            return !this.firstMapDifferentElements.isEmpty() || !this.secondMapDifferentElements.isEmpty();
        }
    }

    /**
     * Finds difference in two maps using their keys. Original maps are not changed.
     *
     * @param firstMap
     * @param secondMap
     * @param intersectingMapsRequired if false then firstMapIntersectingEntitiesByUuid and
     * secondMapIntersectingEntitiesByUuid won't be found, and they will be null in result. These maps can be
     * easily found by difference.
     * @return
     */
    public static <K, V> MapDifference<K, V> findDifference(final Map<K, V> firstMap, final Map<K, V> secondMap,
            boolean intersectingMapsRequired) {
        return findDifference(firstMap, secondMap, (a, b) -> intersectingMapsRequired);
    }

    /**
     * Finds difference in two maps using their keys. Original maps are not changed.
     *
     * @param firstMap
     * @param secondMap
     * @param function function that taking firstMapDifference and secondMapDifference calculates the necessity
     * of finding intersecting maps.
     * @return
     */
    public static <K, V> MapDifference<K, V> findDifference(final Map<K, V> firstMap, final Map<K, V> secondMap,
            BiFunction<Map<K, V>, Map<K, V>, Boolean> function) {
        var firstMapDifference = new HashMap<>(firstMap);
        var secondMapDifference = new HashMap<>(secondMap);

        //calculating uuid insersection set
        var intersectionSet = new HashSet<K>(firstMapDifference .keySet());
        intersectionSet.retainAll(secondMapDifference.keySet());

        //removing entities that are in both maps
        firstMapDifference.keySet().removeAll(intersectionSet);
        secondMapDifference.keySet().removeAll(intersectionSet);

        if (function.apply(firstMapDifference, secondMapDifference)) {
            //find entities, that are in both map. They are found this way: original - difference = intersection
            var firstMapIntersecting = new HashMap<>(firstMap);
            var secondMapIntersecting = new HashMap<>(secondMap);
            firstMapIntersecting.keySet().removeAll(firstMapDifference.keySet());
            secondMapIntersecting.keySet().removeAll(secondMapDifference.keySet());
            return new MapDifference(firstMapDifference, secondMapDifference,
                    firstMapIntersecting, secondMapIntersecting, intersectionSet);
        } else {
            return new MapDifference(firstMapDifference, secondMapDifference, null, null, intersectionSet);
        }
    }

    /**
     * Constructor.
     */
    private MapUtils() {
        //does nothing
    }
}
