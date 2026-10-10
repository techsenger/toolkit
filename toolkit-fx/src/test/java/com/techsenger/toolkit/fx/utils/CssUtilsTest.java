/*
 * Copyright 2016-2026 Pavel Castornii.
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

package com.techsenger.toolkit.fx.utils;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Pavel Castornii
 */
public class CssUtilsTest {

    @Test
    public void setProperty_nullStyle_addsDeclaration() {
        var result = CssUtils.setProperty(null, "-fx-fill", "red");
        assertThat(result).isEqualTo("-fx-fill: red;");
    }

    @Test
    public void setProperty_existingProperty_replacesItInPlaceAndKeepsOthers() {
        var result = CssUtils.setProperty("-fx-a: 1; -fx-fill: red; -fx-b: 2;", "-fx-fill", "blue");
        assertThat(result).isEqualTo("-fx-a: 1; -fx-fill: blue; -fx-b: 2;");
    }

    @Test
    public void setProperty_nullValueForLastDeclaration_returnsNull() {
        var result = CssUtils.setProperty("-fx-fill: red;", "-fx-fill", null);
        assertThat(result).isNull();
    }

    @Test
    public void setProperty_sameArgumentsTwice_returnsSameString() {
        var first = CssUtils.setProperty("-fx-a:1;-fx-fill : red", "-fx-fill", "blue");
        var second = CssUtils.setProperty(first, "-fx-fill", "blue");
        assertThat(first).isEqualTo("-fx-a:1; -fx-fill: blue;");
        assertThat(second).isEqualTo(first);
    }
}
