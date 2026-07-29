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

package com.techsenger.toolkit.fx.utils;

/**
 * Where a target cell should end up within the viewport after scrolling at the leading edge ({@code START},
 * e.g. the top of a vertical flow or the left of a horizontal one), at the trailing edge ({@code END}), or centered
 * between them.
 *
 * @author Pavel Castornii
 */
public enum ScrollPosition {

    /**
     * Aligns the target cell with the leading edge of the viewport, i.e. the top edge for a vertical
     * flow or the left edge for a horizontal one.
     */
    START,

    /**
     * Aligns the target cell so that it is centered within the viewport, with equal space (where
     * possible) on both sides.
     */
    CENTER,

    /**
     * Aligns the target cell with the trailing edge of the viewport, i.e. the bottom edge for a vertical
     * flow or the right edge for a horizontal one.
     */
    END
}
