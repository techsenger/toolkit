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

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * LocalDate converter.
 * @author Pavel Castornii
 */
@Converter(autoApply = true)
public class LocalDateConverter implements AttributeConverter<LocalDate, Date> {

    /**
     * Converts to database column.
     * @param localDate which will be converted.
     * @return date.
     */
    @Override
    public final Date convertToDatabaseColumn(final LocalDate localDate) {
        if (localDate == null) {
            return null;
        }
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    /**
     * Converts to entity attribute.
     * @param date which will be converted.
     * @return LocalDate.
     */
    @Override
    public final LocalDate convertToEntityAttribute(final Date date) {
        if (date == null) {
            return null;
        }
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }
}

