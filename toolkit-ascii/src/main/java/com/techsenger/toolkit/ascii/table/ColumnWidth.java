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

package com.techsenger.toolkit.ascii.table;

/**
 * Represents the width of a column, which can be specified either in characters (chars) or as a percentage.
 *
 * @author Pavel Castornii
 */
public final class ColumnWidth {

    /**
     * Creates a new {@code ColumnWidth} instance with the specified value in characters (chars).
     *
     * <p>This factory method is provided for convenience.
     *
     * @param value the width value in characters (chars); must be a positive integer.
     * @return a new {@code ColumnWidth} instance representing the specified width in characters.
     */
    public static ColumnWidth chars(int value) {
        return new ColumnWidth(value, Unit.CHARS);
    }

    /**
     * Creates a new {@code ColumnWidth} instance with the specified value as a percentage.
     *
     * <p>This factory method is provided for convenience.
     *
     * @param value the width value as a percentage; must be a positive integer.
     * @return a new {@code ColumnWidth} instance representing the specified percentage width.
     */
    public static ColumnWidth percent(int value) {
        return new ColumnWidth(value, Unit.PERCENT);
    }

    /**
     * Enum representing the unit of measurement for the column width.
     */
    public enum Unit {
        /**
         * The width is specified in characters (chars).
         */
        CHARS,

        /**
         * The width is specified as a percentage of the total available width.
         */
        PERCENT
    }

    private final int value;

    private final Unit unit;

    /**
     * Constructs a new {@code ColumnWidth} instance with the specified value and unit.
     *
     * @param value the width value; must be a positive integer.
     * @param unit  the unit of measurement for the width (e.g., CHARS or PERCENT).
     */
    private ColumnWidth(int value, Unit unit) {
        this.value = value;
        this.unit = unit;
    }

    /**
     * Returns the width value of this {@code ColumnWidth}.
     *
     * @return the width value as an integer.
     */
    public int getValue() {
        return value;
    }

    /**
     * Returns the unit of measurement for this {@code ColumnWidth}.
     *
     * @return the unit of measurement, either {@code Unit.CHARS} or {@code Unit.PERCENT}.
     */
    public Unit getUnit() {
        return unit;
    }

    @Override
    public String toString() {
        return "{" + value + " " + unit + '}';
    }
}
