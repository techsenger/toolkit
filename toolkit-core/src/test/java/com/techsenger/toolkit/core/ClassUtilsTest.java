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
import org.junit.jupiter.api.Test;

/**
 *
 * @author Pavel Castornii
 */
public class ClassUtilsTest {

    private interface I1 { }

    private interface I2 { }

    private static class A implements I1 { }

    private static final class B extends A implements I2 { }

    @Test
    public void getHierarchyClasses_classesWithInterfaces_setWithAllClassesAndInterfaces() {
        var result = ClassUtils.getHierarchyClasses(B.class);
        assertThat(result).hasSize(5); //+ Object
    }
}
