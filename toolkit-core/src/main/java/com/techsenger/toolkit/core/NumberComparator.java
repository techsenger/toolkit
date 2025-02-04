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

import java.math.BigDecimal;
import java.util.Comparator;

/**
 *
 * @author Pavel Castornii
 */
public class NumberComparator implements Comparator<Number> {

    public int compare(Number a, Number b) {
        return new BigDecimal(a.toString()).compareTo(new BigDecimal(b.toString()));
    }
}
