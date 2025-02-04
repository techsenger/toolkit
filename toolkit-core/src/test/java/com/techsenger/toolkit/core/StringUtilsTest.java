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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Pavel Castornii
 */
public class StringUtilsTest {

    @Test
    public void format_oneValueAtTheBeginning_success() {
        var result = StringUtils.format("{} is a good day", "Monday");
        assertThat(result).isEqualTo("Monday is a good day");
        result = StringUtils.format("{} is a good day", (Object) null);
        assertThat(result).isEqualTo("null is a good day");
    }

    @Test
    public void format_oneValueAtTheEnd_success() {
        var result = StringUtils.format("Monday is a good {}", "day");
        assertThat(result).isEqualTo("Monday is a good day");
        result = StringUtils.format("Monday is a good {}", (Object) null);
        assertThat(result).isEqualTo("Monday is a good null");
    }

    @Test
    public void format_manyValues_success() {
        var result = StringUtils.format("{} is {} good {}", "Monday", "a", "day");
        assertThat(result).isEqualTo("Monday is a good day");
        result = StringUtils.format("{} is {} good {}", "Monday", (Object) null, "day");
        assertThat(result).isEqualTo("Monday is null good day");
    }

    @Test()
    public void format_argumentCountMismatch_illegalArgumentException() {
        assertThatThrownBy(() -> StringUtils.format("{} is a good {}", "day"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> StringUtils.format("{} is a good", "Monday", "day"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test()
    public void countMatches_simpleCharacter_success() {
        var matchesCount = StringUtils.countMatches("Abbccdbd", "b");
        assertThat(matchesCount).isSameAs(3);
    }

    @Test()
    public void countMatches_specialCharacter_success() {
        var matchesCount = StringUtils.countMatches("127.0.0.1", ".");
        assertThat(matchesCount).isSameAs(3);
    }
}
