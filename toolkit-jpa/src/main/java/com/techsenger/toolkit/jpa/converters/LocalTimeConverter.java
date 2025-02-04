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

package com.techsenger.toolkit.jpa.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * LocalTime converter.
 * @author Pavel Castornii
 */
@Converter(autoApply = true)
public class LocalTimeConverter implements AttributeConverter<LocalTime, Date> {

    /**
     * Converts to database column.
     * @param localTime that will be converted.
     * @return date.
     */
    @Override
    public final Date convertToDatabaseColumn(final LocalTime localTime) {
        if (localTime == null) {
            return null;
        }
        Instant instant = Instant.from(localTime);
        return Date.from(instant);
    }

    /**
     * Converts to entity attribute.
     * @param date that will be converted.
     * @return LocalTime.
     */
    @Override
    public final LocalTime convertToEntityAttribute(final Date date) {
        if (date == null) {
            return null;
        }
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
    }
}
