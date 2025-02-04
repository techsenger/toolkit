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

package com.techsenger.toolkit.fx.table;

import java.util.LinkedHashSet;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class gives the indexes of the first/last visible rows.
 *
 * <p>The base of this class was taken from https://stackoverflow.com/a/46491017/5057736.
 *
 * @author Pavel Castornii
 *
 */
public class TableVisibleRowHelper<T> {

    private static final Logger logger = LoggerFactory.getLogger(TableVisibleRowHelper.class);

    private final TableView<T> tableView;

    private final LinkedHashSet<TableRow<T>> rows = new LinkedHashSet<>();

    private Node tableHeader;

    private double viewPortHeight;

    private int firstIndex;

    private int lastIndex;

    private TableRow<T> firstRow;

    private TableRow<T> lastRow;

    public TableVisibleRowHelper(TableView<T> tableView) {
        this.tableView = tableView;
        // Callback to monitor row creation and to identify visible screen rows
        final Callback<TableView<T>, TableRow<T>> rf = tableView.getRowFactory();
        final Callback<TableView<T>, TableRow<T>> modifiedRowFactory = new Callback<TableView<T>, TableRow<T>>() {

            @Override
            public TableRow<T> call(TableView<T> param) {
                TableRow<T> r = rf != null ? rf.call(param) : new TableRow<T>();
                // Save row, this implementation relies on JaxaFX re-using TableRow efficiently
                rows.add(r);
                return r;
            }
        };
        tableView.setRowFactory(modifiedRowFactory);
    }

    /**
     * Changes the current view to ensure that one of the passed index positions
     * is visible on screen. The view is not changed if any of the passed index positions is already visible.
     * The table scroll position is moved so that the closest index to the current position is visible.
     * @param indices Assumed to be in ascending order.
     *
     */
    public void scrollToIndex(int... indices) {
        int first = getFirstVisibleIndex();
        int last = getLastVisibleIndex();
        int where = first;
        boolean changeScrollPos = true;
        // No point moving current scroll position if one of the index items is visible already:
        if (first >= 0 && last >= first) {
            for (int idx : indices) {
                if (first <= idx && idx <= last) {
                    changeScrollPos = false;
                    break;
                }
            }
        }
        if (indices.length > 0 && changeScrollPos) {
            where = indices[0];
            if (first >= 0) {
                int x = closestTo(indices, first);
                int abs = Math.abs(x - first);
                if (abs < Math.abs(where - first)) {
                    where = x;
                }
            }
            if (last >= 0) {
                int x = closestTo(indices, last);
                int abs = Math.abs(x - last);
                if (abs < Math.abs(where - last)) {
                    where = x;
                }
            }
            tableView.scrollTo(where);
        }
    }

    /**
     * Find the first row in the tableView which is visible on the display.
     * @return -1 if none visible or the index of the first visible row (wholly or fully)
     */
    public int getFirstVisibleIndex() {
        recomputeVisibleIndexes();
        this.firstRow = null;
        this.lastRow = null;
        logger.debug("getFirstVisibleIndex: {}, rows: {}", firstIndex, rows.size());
        return firstIndex;
    }

    /**
     * Find the last row in the tableView which is visible on the display.
     * @return -1 if none visible or the index of the last visible row (wholly or fully)
     */
    public int getLastVisibleIndex() {
        recomputeVisibleIndexes();
        this.firstRow = null;
        this.lastRow = null;
        logger.debug("getLastVisibleIndex: {}, rows: {}", lastIndex, rows.size());
        return lastIndex;
    }

    /**
     * Ensure that some part of the current selection is visible in the display view.
     */
    public void scrollToSelection() {
        ObservableList<Integer> seln = tableView.getSelectionModel().getSelectedIndices();
        int[] indices = new int[seln.size()];
        for (int i = 0; i < indices.length; i++) {
            indices[i] = seln.get(i).intValue();
        }
        scrollToIndex(indices);
    }

    /**
     * Returns the count of rows that are fully visible in view port.
     * @return the count of rows or -1;
     */
    public int getViewPortRowCount() {
        recomputeVisibleIndexes();
        int result;
        if (this.firstIndex != -1 && this.firstRow != null) {
            var firstRowHeight = firstRow.getHeight();
            result = (int) (this.viewPortHeight / firstRowHeight);
            logger.debug("viewPortHeight: {}, firstRowIndex: {}, firstRowHeight: {}", this.viewPortHeight,
                    this.firstIndex, firstRow.getHeight());
        } else {
            result = -1;
        }
        this.firstRow = null;
        this.lastRow = null;
        return result;
    }

    private void recomputeVisibleIndexes() {
        firstIndex = -1;
        lastIndex = -1;
        // Work out which of the rows are visible
        this.viewPortHeight = this.calculateViewPortHeight();
        for (TableRow<T> r : rows) {
            if (!r.isVisible()) {
                continue;
            }
            double minY = r.getBoundsInParent().getMinY();
            double maxY = r.getBoundsInParent().getMaxY();
            boolean hidden  = (maxY < 0) || (minY > viewPortHeight);
            // boolean fullyVisible = !hidden && (maxY <= viewPortHeight) && (minY >= 0);
            if (!hidden) {
                if (firstIndex < 0 || r.getIndex() < firstIndex) {
                    firstIndex = r.getIndex();
                    firstRow = r;
                }
                if (lastIndex < 0 || r.getIndex() > lastIndex) {
                    lastIndex = r.getIndex();
                    lastRow = r;
                }
            }
        }
    }

    private int closestTo(int[] indices, int value) {
        int x = indices[0];
        int diff = Math.abs(value - x);
        int newDiff = diff;
        for (int v : indices) {
            newDiff = Math.abs(value - v);
            if (newDiff < diff) {
                x = v;
                diff = newDiff;
            }
        }
        return x;
    }

    private double calculateViewPortHeight() {
        double tblViewHeight = tableView.getHeight();
        if (this.tableHeader == null) {
            this.tableHeader = tableView.lookup(".column-header-background");
        }
        double headerHeight = this.tableHeader.getBoundsInLocal().getHeight();
        double viewPortHeight = tblViewHeight - headerHeight;
        return viewPortHeight;
    }
}
