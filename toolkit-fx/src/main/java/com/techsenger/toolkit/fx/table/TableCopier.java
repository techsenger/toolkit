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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javafx.collections.ObservableList;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTablePosition;
import javafx.scene.control.TreeTableView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * When cells are copied it is necessary to know in what column they are to match column headers.
 * Because user can select only some cells in a row. For example, he selects the 1st and the 3rd cell in the first row,
 * the 2nd cell in the second one, and all cells in the third row.
 *
 * <p>To keeps cells in proper columns \t symbol is used to separate cells (it is the standard) - the count of \t
 * must be equal to the column count - 1 (if all columns are copied). At the same time not all columns can be copied,
 * but only some of them. So, the count of \t is calculated at the end.
 *
 * <p>Considering what has been said above and that there are two types of tables - TableView and TreeTableView
 * cells are copied in two steps. At the first step cells are copied to table model. At the second step table model
 * is converted to string.
 *
 * @author Pavel Castornii
 */
public final class TableCopier {

    private static final Logger logger = LoggerFactory.getLogger(TableCopier.class);

    public interface UpdatableTableCell<T> {

        void updateItem(T item, boolean empty);
    }

    private static class CopierCell {

        private final int columnIndex;

        private final String value;

        CopierCell(int columnIndex, String value) {
            this.columnIndex = columnIndex;
            if (value == null) {
                value = "";
            }
            this.value = value;
        }
    }

    private static class CopierHeadRow {

        private final CopierTable table;

        private final List<CopierCell> cells = new ArrayList<>();

        CopierHeadRow(CopierTable table) {
            this.table = table;
        }

        public void addCell(CopierCell cell) {
            cells.add(cell);
        }

        public CopierTable getTable() {
            return table;
        }

        public List<CopierCell> getCells() {
            return cells;
        }
    }

    private static class CopierBodyRow extends CopierHeadRow {

        CopierBodyRow(CopierTable table) {
            super(table);
        }

        @Override
        public void addCell(CopierCell cell) {
            super.addCell(cell);
            this.getTable().columnIndexes.add(cell.columnIndex);
        }
    }

    private static final class CopierTable {

        private CopierHeadRow headRow;

        private final List<CopierBodyRow> bodyRows = new ArrayList<>();

        /**
         * Indexes of all copied columns.
         */
        private final Set<Integer> columnIndexes = new HashSet<>();

        private int columnMinIndex;

        private int columnMaxIndex;

        public void setHeadRow(CopierHeadRow headRow) {
            this.headRow = headRow;
        }

        public void addBodyRow(CopierBodyRow row) {
            bodyRows.add(row);
        }

        public String toString() {
            logger.debug("Converting CopierTable to String. Copied column indexes: {}", this.columnIndexes);
            initColumnRange();
            StringBuilder sb = new StringBuilder();
            //headers
            boolean isFirstCellInRow = true;
            for (var i = columnMinIndex; i <= columnMaxIndex; i++) {
                var cell = headRow.cells.get(i);
                if (columnIndexes.contains(cell.columnIndex)) {
                    if (!isFirstCellInRow) {
                        sb.append("\t");
                    }
                    sb.append(cell.value);
                    isFirstCellInRow = false;
                }
            }
            sb.append("\n");
            //rows
            for (var row : bodyRows) {
                CopierCell previousCell = null;
                for (var currentCell : row.getCells()) {
                    if (previousCell == null) {
                        sb.append("\t".repeat(currentCell.columnIndex - this.columnMinIndex));
                    } else {
                        var tabCount = calculateTabCount(currentCell, previousCell);
                        sb.append("\t".repeat(tabCount));
                    }
                    sb.append(currentCell.value);
                    previousCell = currentCell;
                }
                sb.append("\n");
            }
            return sb.toString();
        }

        private void initColumnRange() {
            this.columnMinIndex = Collections.min(columnIndexes);
            this.columnMaxIndex = Collections.max(columnIndexes);
        }

        /**
         * If there are columns between two cells, it is required to check if these columns are used or not.
         * @param current
         * @param previous
         * @return
         */
        private int calculateTabCount(CopierCell current, CopierCell previous) {
            var tabCount = current.columnIndex - previous.columnIndex;
            if (tabCount == 1) {
                return tabCount;
            }
            for (var i = previous.columnIndex + 1; i < current.columnIndex; i++) {
                if (!columnIndexes.contains(i)) {
                    tabCount--;
                }
            }
            return tabCount;
        }
    }

    /**
     * This method works in two modes. When using cell factory is true, then every cell of the table must open
     * access to updateItem method (that is protected) by creating custom cell table class with UpdatableTableCell
     * interface. This method is used when column data type is the same as table type. For example
     * {@code Column<Person, Person>}. When using cell factory is false, then {@code column.getCellData(rowIndex)} is
     * used.
     *
     * @param tableView
     * @param usingCellFactory
     */
    public static void copyToClipboard(TableView<?> tableView, boolean usingCellFactory) {
        ObservableList<TablePosition> posList = tableView.getSelectionModel().getSelectedCells();
        Map<Integer, TableCell<?, ?>> tableCellsByColumnIndex = null;
        if (usingCellFactory) {
             tableCellsByColumnIndex = new HashMap<>();
            //creating new cell for every column and cache it. We don't create every cell
            //for every item, as it is now known how it will work in terms of performance
            for (var i = 0; i < tableView.getColumns().size(); i++) {
                var column = tableView.getColumns().get(i);
                TableCell<?, ?> cell =  column.getCellFactory().call((TableColumn) column);
                tableCellsByColumnIndex.put(i, cell);
            }
        }
        var copierTable = new CopierTable();
        //head
        var headRow = new CopierHeadRow(copierTable);
        for (var i = 0; i < tableView.getColumns().size(); i++) {
            var column = tableView.getColumns().get(i);
            headRow.addCell(new CopierCell(i, column.getText()));
        }
        copierTable.setHeadRow(headRow);
        //body
        int previousRowIndex = -1;
        CopierBodyRow currentRow = null;
        for (var p : posList) {
            int rowIndex = p.getRow();
            var columnIndex = p.getColumn();
            String value = null;
            if (usingCellFactory) {
                Object rowData = tableView.getItems().get(rowIndex);
                var cell = tableCellsByColumnIndex.get(columnIndex);
                //setting data to cell
                ((UpdatableTableCell) cell).updateItem(rowData, false);
                //getting result
                value = cell.getText();
            } else {
                Object v = tableView.getColumns().get(columnIndex).getCellData(rowIndex);
                if (v == null) {
                    value = "";
                } else {
                    value = String.valueOf(v);
                }
            }
            if (rowIndex != previousRowIndex) {
                currentRow = new CopierBodyRow(copierTable);
                copierTable.addBodyRow(currentRow);
            }
            currentRow.addCell(new CopierCell(columnIndex, value));
            previousRowIndex = rowIndex;
        }
        final ClipboardContent content = new ClipboardContent();
        content.putString(copierTable.toString());
        Clipboard.getSystemClipboard().setContent(content);
    }

    /**
     * This method works in two modes. When using cell factory is true, then every cell of the table must open
     * access to updateItem method (that is protected) by creating custom cell table class with UpdatableTableCell
     * interface. This method is used when column data type is the same as table type. For example
     * {@code Column<Person, Person>}. When using cell factory is false, then {@code column.getCellData(rowIndex)}
     * is used.
     *
     * @param tableView
     * @param usingCellFactory
     */
    public static void copyToClipboard(TreeTableView<?> tableView, boolean usingCellFactory) {
        ObservableList<TreeTablePosition> posList = (ObservableList) tableView.getSelectionModel().getSelectedCells();
        Map<Integer, TreeTableCell<?, ?>> tableCellsByColumnIndex = null;
        if (usingCellFactory) {
             tableCellsByColumnIndex = new HashMap<>();
            //creating new cell for every column and cache it. We don't create every cell
            //for every item, as it is now known how it will work in terms of performance
            for (var i = 0; i < tableView.getColumns().size(); i++) {
                var column = tableView.getColumns().get(i);
                TreeTableCell<?, ?> cell =  column.getCellFactory().call((TreeTableColumn) column);
                tableCellsByColumnIndex.put(i, cell);
            }
        }
        var copierTable = new CopierTable();
        //head
        var headRow = new CopierHeadRow(copierTable);
        for (var i = 0; i < tableView.getColumns().size(); i++) {
            var column = tableView.getColumns().get(i);
            headRow.addCell(new CopierCell(i, column.getText()));
        }
        copierTable.setHeadRow(headRow);
        //body
        int previousRowIndex = -1;
        CopierBodyRow currentRow = null;
        for (var p : posList) {
            int rowIndex = p.getRow();
            var columnIndex = p.getColumn();
            String value = null;
            if (usingCellFactory) {
                TreeItem<?> rowData = tableView.getTreeItem(rowIndex);
                var cell = tableCellsByColumnIndex.get(columnIndex);
                //setting data to cell
                ((UpdatableTableCell) cell).updateItem(rowData.getValue(), false);
                //getting result
                value = cell.getText();
            } else {
                Object v = tableView.getColumns().get(columnIndex).getCellData(rowIndex);
                if (v == null) {
                    value = "";
                } else {
                    value = String.valueOf(v);
                }
            }
            if (rowIndex != previousRowIndex) {
                currentRow = new CopierBodyRow(copierTable);
                copierTable.addBodyRow(currentRow);
            }
            currentRow.addCell(new CopierCell(columnIndex, value));
            previousRowIndex = rowIndex;
        }
        final ClipboardContent content = new ClipboardContent();
        content.putString(copierTable.toString());
        Clipboard.getSystemClipboard().setContent(content);
    }

    private TableCopier() {
        //empty
    }
}
