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

package com.techsenger.toolkit.fx.table;

import java.util.Comparator;

/**
 * Compares all items except last row that is always last. This method can be used for total row in tables.
 *
 * @author Pavel Castornii
 */
public abstract class AbstractLastRowComparator<T, S> implements Comparator<T> {

    @Override
    public int compare(T o1, T o2) {
        var i = 1;
        if (!isAscendingSort()) {
            i = -1;
        }
        if (isLastRow(o1)) {
            return +1 * i;
        }
        if (isLastRow(o2)) {
            return -1 * i;
        }
        var d1 = getValue(o1);
        var d2 = getValue(o2);
        if (d1 == null || d2 == null) {
            return 0;
        }
        return compareValues(d1, d2);
    }

    /**
     * Checks if ascending sort type is used.
     *
     * @return
     */
    protected abstract boolean isAscendingSort();

    /**
     * Checks if item will be in the last row.
     * @param item
     * @return
     */
    protected abstract boolean isLastRow(T item);

    /**
     * Returns the value that will be compared.
     *
     * @param item
     * @return
     */
    protected abstract S getValue(T item);

    /**
     * Compares values. Its implementation can be return Long.compare(..).
     * @param s1
     * @param s2
     * @return
     */
    protected abstract int compareValues(S s1, S s2);
}
