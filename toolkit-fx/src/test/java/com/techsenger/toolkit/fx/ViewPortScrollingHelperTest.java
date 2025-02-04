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

package com.techsenger.toolkit.fx;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Pavel Castornii
 */
public class ViewPortScrollingHelperTest {

    @Test
    public void calculateScrolledElementIndex_allElementsVisible_correctResult() {
        var scrolledIndex = ViewPortScrollingHelper.calculateScrolledElementIndex(10, 6, 3);
        assertThat(scrolledIndex).isEqualTo(0);
    }

    @Test
    public void calculateScrolledElementIndex_selectedElementInCenterWithEvenViewPort_correctResult() {
        var scrolledIndex = ViewPortScrollingHelper.calculateScrolledElementIndex(4, 6, 3);
        assertThat(scrolledIndex).isEqualTo(2);
    }

    @Test
    public void calculateScrolledElementIndex_selectedElementInCenterWithOddViewPort_correctResult() {
        var scrolledIndex = ViewPortScrollingHelper.calculateScrolledElementIndex(5, 6, 3);
        assertThat(scrolledIndex).isEqualTo(1);
    }

    @Test
    public void calculateScrolledElementIndex_selectedElementInFirstHalf_correctResult() {
        var scrolledIndex = ViewPortScrollingHelper.calculateScrolledElementIndex(5, 8, 1);
        assertThat(scrolledIndex).isEqualTo(0);
    }

    @Test
    public void calculateScrolledElementIndex_selectedElementInSecondHalf_correctResult() {
        var scrolledIndex = ViewPortScrollingHelper.calculateScrolledElementIndex(5, 8, 6);
        assertThat(scrolledIndex).isEqualTo(3);
    }
}
