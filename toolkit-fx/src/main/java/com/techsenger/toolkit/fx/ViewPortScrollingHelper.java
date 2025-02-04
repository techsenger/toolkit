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

/**
 * This class calculates indexes of the elements that should be scrolled to to show selected element in the center.
 *
 * @author Pavel Castornii
 */
public final class ViewPortScrollingHelper {

    /**
     * Calculates the index of the element the container must scroll to.
     *
     * @param viewPortElementCount the count of elements in view port that can be visible fully.
     * @param elementCount the count of elements in table/list/combobox etc.
     * @param selectedElementIndex the index of the selected element.
     * @return
     */
    public static int calculateScrolledElementIndex(int viewPortElementCount, int elementCount,
            int selectedElementIndex) {
        //all elements are visible
        if (viewPortElementCount >= elementCount) {
            return 0;
        }
        //one element is the selected one.
        viewPortElementCount -= 1;
        var beforeSelectedCount = viewPortElementCount / 2; // 5 / 2 = 2
        var afterSelectedCount = viewPortElementCount - beforeSelectedCount;
        int beforeSelectedIndex;
        int afterSelectedIndex;
        beforeSelectedIndex = selectedElementIndex - beforeSelectedCount;
        afterSelectedIndex = selectedElementIndex + afterSelectedCount;
        if (beforeSelectedIndex >= 0) {
            if (afterSelectedIndex < elementCount) {
                //#1 - in the center
                return beforeSelectedIndex;
            } else {
                //how many we can add after selected
                var validAfterSelectedCount = elementCount - 1 - selectedElementIndex;
                beforeSelectedIndex = beforeSelectedIndex - (afterSelectedCount - validAfterSelectedCount);
                if (beforeSelectedIndex >= 0) {
                    return beforeSelectedIndex;
                } else {
                    return 0;
                }
            }

        } else {
            //#2 - in the first half
            return 0;
        }
    }

    private ViewPortScrollingHelper() {
        //empty
    }
}
