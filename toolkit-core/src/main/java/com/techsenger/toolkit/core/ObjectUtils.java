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

/**
 *
 * @author Pavel Castornii
 */
public final class ObjectUtils {

    /**
     * Returns a concise identity string representation of the object in the format {@code SimpleClassName@hashCode}.
     *
     * <p>Example: {@code "String@5e91993f"}
     *
     * @param obj the object to represent (may be null)
     * @return identity string, or "null" if the object is null
     */
    public static String getIdentity(Object obj) {
        if (obj == null) {
            return "null";
        }
        return obj.getClass().getSimpleName() + "@" + Integer.toHexString(obj.hashCode());
    }

    private ObjectUtils() {
        // empty
    }
}
