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

package com.techsenger.toolkit.ascii.table;

import com.techsenger.toolkit.core.StringUtils;
import com.techsenger.toolkit.core.os.OsUtils;
import de.vandermeer.asciitable.CWC_FixedWidth;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Pavel Castornii
 */
public final class AsciiTableUtils {

    private static final Logger logger = LoggerFactory.getLogger(AsciiTableUtils.class);

    /**
     * Creates column width calculator using column widths defined in characters count or percents.
     *
     * @param totalWidth the width of table defined in characters counts
     * @param columnWidths
     * @return
     */
    public static CWC_FixedWidth createColumnWidthCalculator(final int totalWidth, final ColumnWidth... columnWidths) {
        var percentsColumnCount = 0;
        var lastPercentsColumnIndex = 0;
        var characterColumnsTotalWidth = 0;
        //checking if total percents is 100
        int check100Percents = 0;
        for (int i = 0; i < columnWidths.length; i++) {
            var columnWidth = columnWidths[i];
            if (columnWidth.getUnit() == ColumnWidth.Unit.PERCENT) {
                percentsColumnCount++;
                lastPercentsColumnIndex = i;
                check100Percents += columnWidth.getValue();
            } else {
                characterColumnsTotalWidth += columnWidth.getValue();
            }
        }
        if (check100Percents != 100) {
            throw new IllegalArgumentException(StringUtils.format("Total width of percent columns is {}% but not 100%",
                    check100Percents));
        }
        //total width minus borders (columnCount + 1)
        int realTotalWidth = totalWidth - (columnWidths.length + 1);
        if (OsUtils.isWindows()) {
            //minus end of line char (for windows)
            realTotalWidth--;
        }
        int widthOfPercentColumnsWithoutLast = 0;
        List<Integer> calculatedColumnWidths = new ArrayList<>();
        for (int i = 0; i < columnWidths.length; i++) {
            var columnWidth = columnWidths[i];
            if (columnWidth.getUnit() == ColumnWidth.Unit.PERCENT) {
                //percent column
                if (i == lastPercentsColumnIndex) {
                    //temp value for last percent column
                    calculatedColumnWidths.add(0);
                } else {
                    double width = (columnWidth.getValue() * 0.01 * (realTotalWidth - characterColumnsTotalWidth));
                    var bd = new BigDecimal(width);
                    bd.setScale(0, RoundingMode.HALF_UP);
                    calculatedColumnWidths.add(bd.intValue());
                    widthOfPercentColumnsWithoutLast += width;
                }
            } else {
                //pixel column
                calculatedColumnWidths.add(columnWidth.getValue());
            }

        }
        //correction - last is calculated as the difference
        calculatedColumnWidths.set(lastPercentsColumnIndex,
                realTotalWidth - characterColumnsTotalWidth - widthOfPercentColumnsWithoutLast);
        if (logger.isDebugEnabled()) {
            logger.debug("Input values: {}, totalWidth: {}, realTotalWidth: {}, characterColumnsTotalWidth: {}, "
                    + "percentsColumnCount: {}, lastPercentsColumnIndex: {},  calculated widths : {}", columnWidths,
                    totalWidth, realTotalWidth, characterColumnsTotalWidth, percentsColumnCount,
                    lastPercentsColumnIndex, calculatedColumnWidths);
        }
        CWC_FixedWidth cwc = new CWC_FixedWidth();
        calculatedColumnWidths.forEach(v -> cwc.add(v));
        return cwc;
    }

    private AsciiTableUtils() {
        //empty
    }
}
