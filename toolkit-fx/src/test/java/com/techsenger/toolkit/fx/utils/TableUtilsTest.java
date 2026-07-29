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

import com.techsenger.toolkit.fx.FxPlatform;
import java.util.function.Supplier;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.skin.VirtualFlow;
import javafx.stage.Stage;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Exercises {@link TableUtils} against a real, shown {@code TableView} and {@code TreeTableView} — both have
 * their own skin ({@code TableViewSkin}, {@code TreeTableViewSkin}) and cell type ({@code TableRow}, {@code
 * TreeTableRow}), which is exactly why this gets its own test class per control type instead of assuming
 * {@code ListViewUtilsTest} generalizes. See {@code VirtualFlowUtils} for why a real display is required and
 * why this can't run on a display-less CI runner as-is.
 *
 * <p>The tree table used here is deliberately flat (an invisible root with {@code itemCount} direct,
 * non-expandable children) so its row geometry is directly comparable to the plain table's flat item list.
 *
 * @author Pavel Castornii
 */
class TableUtilsTest {

    private static final class Pair<A, B> {

        private final A first;

        private final B second;

        private Pair(A first, B second) {
            this.first = first;
            this.second = second;
        }
    }

    private static Stage stage;

    @BeforeAll
    static void initJavaFxToolkit() throws InterruptedException {
        FxPlatform.start();
        FxPlatform.runLaterAndWait(() -> {
            stage = new Stage();
            stage.setX(-3000);
            stage.setY(-3000);
        });
    }

    @AfterAll
    static void closeStage() throws InterruptedException {
        FxPlatform.runLaterAndWait(() -> stage.hide());
    }

    /**
     * Builds a single-column {@code TableView} with {@code itemCount} string items, makes it the content of
     * {@link #stage}'s scene at {@code width}x{@code height}, shows the stage (a no-op if already showing)
     * and forces a layout pass, so its {@code VirtualFlow} has realized cells with real, non-zero measurements
     * immediately.
     *
     * <p>Must be called on the FX Application Thread.
     */
    private static TableView<String> newTableView(int itemCount, double width, double height) {
        var tableView = new TableView<>(items(itemCount, "item-"));
        var column = new TableColumn<String, String>("value");
        column.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue()));
        tableView.getColumns().add(column);
        stage.setScene(new Scene(tableView, width, height));
        if (!stage.isShowing()) {
            stage.show();
        }
        tableView.applyCss();
        tableView.layout();
        return tableView;
    }

    /**
     * Builds a flat, single-column {@code TreeTableView} (invisible root, {@code itemCount} direct children)
     * the same way {@link #newTableView} builds a {@code TableView}.
     *
     * <p>Must be called on the FX Application Thread.
     */
    private static TreeTableView<String> newTreeTableView(int itemCount, double width, double height) {
        var treeTableView = new TreeTableView<String>();
        treeTableView.setRoot(newRoot(itemCount, "item-"));
        treeTableView.setShowRoot(false);
        var column = new TreeTableColumn<String, String>("value");
        column.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getValue()));
        treeTableView.getColumns().add(column);
        stage.setScene(new Scene(treeTableView, width, height));
        if (!stage.isShowing()) {
            stage.show();
        }
        treeTableView.applyCss();
        treeTableView.layout();
        return treeTableView;
    }

    private static ObservableList<String> items(int itemCount, String prefix) {
        var items = FXCollections.<String>observableArrayList();
        for (int i = 0; i < itemCount; i++) {
            items.add(prefix + i);
        }
        return items;
    }

    private static TreeItem<String> newRoot(int itemCount, String prefix) {
        var root = new TreeItem<String>("root");
        for (int i = 0; i < itemCount; i++) {
            root.getChildren().add(new TreeItem<>(prefix + i));
        }
        root.setExpanded(true);
        return root;
    }

    private static VirtualFlow<?> flowOf(TableView<?> tableView) {
        return (VirtualFlow<?>) tableView.lookup(".virtual-flow");
    }

    private static VirtualFlow<?> flowOf(TreeTableView<?> treeTableView) {
        return (VirtualFlow<?>) treeTableView.lookup(".virtual-flow");
    }

    private static int firstVisibleIndex(TableView<?> tableView) {
        return flowOf(tableView).getFirstVisibleCell().getIndex();
    }

    private static int firstVisibleIndex(TreeTableView<?> treeTableView) {
        return flowOf(treeTableView).getFirstVisibleCell().getIndex();
    }

    /**
     * Runs {@code action} on the FX Application Thread and returns its result, blocking the calling (test)
     * thread until it completes.
     */
    private static <T> T onFxThread(Supplier<T> action) throws InterruptedException {
        var box = new Object[1];
        FxPlatform.runLaterAndWait(() -> box[0] = action.get());
        @SuppressWarnings("unchecked")
        var result = (T) box[0];
        return result;
    }

    // TableView: isFullyVisible

    @Test
    void isFullyVisible_tableView_indexWithinInitialViewport_returnsTrue() throws InterruptedException {
        var visible = onFxThread(() -> {
            var tableView = newTableView(100, 200, 300);
            return TableUtils.isFullyVisible(tableView, 0);
        });

        assertThat(visible).isTrue();
    }

    @Test
    void isFullyVisible_tableView_indexBeyondInitialViewport_returnsFalse() throws InterruptedException {
        var visible = onFxThread(() -> {
            var tableView = newTableView(100, 200, 300);
            return TableUtils.isFullyVisible(tableView, 99);
        });

        assertThat(visible).isFalse();
    }

    @Test
    void isFullyVisible_tableView_afterItemsReplaced_reflectsNewViewport() throws InterruptedException {
        // This is the regression this whole utility exists for: reading the viewport right after a structural
        // change must be reliable, not just work on an already-stable view.
        var visible = onFxThread(() -> {
            var tableView = newTableView(3, 200, 300); // all 3 fit, nothing to scroll
            tableView.setItems(items(200, "big-item-"));
            return new Pair<>(TableUtils.isFullyVisible(tableView, 0), TableUtils.isFullyVisible(tableView, 150));
        });

        assertThat(visible.first).isTrue();
        assertThat(visible.second).isFalse();
    }

    // TableView: scrollTo

    @Test
    void scrollTo_tableView_positionStart_targetBecomesFirstVisibleRow() throws InterruptedException {
        var result = onFxThread(() -> {
            var tableView = newTableView(100, 200, 300);
            TableUtils.scrollTo(tableView, 50, ScrollPosition.START);
            return new Pair<>(firstVisibleIndex(tableView), TableUtils.isFullyVisible(tableView, 50));
        });

        assertThat(result.first).isEqualTo(50);
        assertThat(result.second).isTrue();
    }

    @Test
    void scrollTo_tableView_positionEnd_targetBecomesLastFullyVisibleRow() throws InterruptedException {
        var result = onFxThread(() -> {
            var tableView = newTableView(100, 200, 300);
            TableUtils.scrollTo(tableView, 50, ScrollPosition.END);
            return new Pair<>(TableUtils.isFullyVisible(tableView, 50), TableUtils.isFullyVisible(tableView, 51));
        });

        assertThat(result.first).isTrue();
        assertThat(result.second).isFalse();
    }

    @Test
    void scrollTo_tableView_positionCenter_targetBecomesVisible() throws InterruptedException {
        var visible = onFxThread(() -> {
            var tableView = newTableView(200, 200, 300);
            TableUtils.scrollTo(tableView, 100, ScrollPosition.CENTER);
            return TableUtils.isFullyVisible(tableView, 100);
        });

        assertThat(visible).isTrue();
    }

    // TableView: scrollToIfNeeded

    @Test
    void scrollToIfNeeded_tableView_indexAlreadyFullyVisible_doesNotMovePosition() throws InterruptedException {
        var result = onFxThread(() -> {
            var tableView = newTableView(100, 200, 300);
            var flow = flowOf(tableView);
            var positionBefore = flow.getPosition();
            TableUtils.scrollToIfNeeded(tableView, 0, ScrollPosition.CENTER);
            return new Pair<>(positionBefore, flow.getPosition());
        });

        assertThat(result.second).isEqualTo(result.first);
    }

    @Test
    void scrollToIfNeeded_tableView_rightAfterItemsReplaced_reachesNewlyAddedIndex() throws InterruptedException {
        // Mirrors the real bug this API was built to fix: select+reveal an index that only exists after a
        // structural change, in the same call that discovers it, with no separate warm-up layout pass.
        var visible = onFxThread(() -> {
            var tableView = newTableView(3, 200, 300); // all 3 fit, nothing to scroll
            tableView.setItems(items(200, "big-item-"));
            TableUtils.scrollToIfNeeded(tableView, 199, ScrollPosition.CENTER);
            return TableUtils.isFullyVisible(tableView, 199);
        });

        assertThat(visible).isTrue();
    }

    // TreeTableView: isFullyVisible

    @Test
    void isFullyVisible_treeTableView_indexWithinInitialViewport_returnsTrue() throws InterruptedException {
        var visible = onFxThread(() -> {
            var treeTableView = newTreeTableView(100, 200, 300);
            return TableUtils.isFullyVisible(treeTableView, 0);
        });

        assertThat(visible).isTrue();
    }

    @Test
    void isFullyVisible_treeTableView_indexBeyondInitialViewport_returnsFalse() throws InterruptedException {
        var visible = onFxThread(() -> {
            var treeTableView = newTreeTableView(100, 200, 300);
            return TableUtils.isFullyVisible(treeTableView, 99);
        });

        assertThat(visible).isFalse();
    }

    @Test
    void isFullyVisible_treeTableView_afterRootReplaced_reflectsNewViewport() throws InterruptedException {
        var visible = onFxThread(() -> {
            var treeTableView = newTreeTableView(3, 200, 300); // all 3 fit, nothing to scroll
            treeTableView.setRoot(newRoot(200, "big-item-"));
            return new Pair<>(
                    TableUtils.isFullyVisible(treeTableView, 0), TableUtils.isFullyVisible(treeTableView, 150));
        });

        assertThat(visible.first).isTrue();
        assertThat(visible.second).isFalse();
    }

    // TreeTableView: scrollTo

    @Test
    void scrollTo_treeTableView_positionStart_targetBecomesFirstVisibleRow() throws InterruptedException {
        var result = onFxThread(() -> {
            var treeTableView = newTreeTableView(100, 200, 300);
            TableUtils.scrollTo(treeTableView, 50, ScrollPosition.START);
            return new Pair<>(firstVisibleIndex(treeTableView), TableUtils.isFullyVisible(treeTableView, 50));
        });

        assertThat(result.first).isEqualTo(50);
        assertThat(result.second).isTrue();
    }

    @Test
    void scrollTo_treeTableView_positionEnd_targetBecomesLastFullyVisibleRow() throws InterruptedException {
        var result = onFxThread(() -> {
            var treeTableView = newTreeTableView(100, 200, 300);
            TableUtils.scrollTo(treeTableView, 50, ScrollPosition.END);
            return new Pair<>(
                    TableUtils.isFullyVisible(treeTableView, 50), TableUtils.isFullyVisible(treeTableView, 51));
        });

        assertThat(result.first).isTrue();
        assertThat(result.second).isFalse();
    }

    @Test
    void scrollTo_treeTableView_positionCenter_targetBecomesVisible() throws InterruptedException {
        var visible = onFxThread(() -> {
            var treeTableView = newTreeTableView(200, 200, 300);
            TableUtils.scrollTo(treeTableView, 100, ScrollPosition.CENTER);
            return TableUtils.isFullyVisible(treeTableView, 100);
        });

        assertThat(visible).isTrue();
    }

    // TreeTableView: scrollToIfNeeded

    @Test
    void scrollToIfNeeded_treeTableView_indexAlreadyFullyVisible_doesNotMovePosition() throws InterruptedException {
        var result = onFxThread(() -> {
            var treeTableView = newTreeTableView(100, 200, 300);
            var flow = flowOf(treeTableView);
            var positionBefore = flow.getPosition();
            TableUtils.scrollToIfNeeded(treeTableView, 0, ScrollPosition.CENTER);
            return new Pair<>(positionBefore, flow.getPosition());
        });

        assertThat(result.second).isEqualTo(result.first);
    }

    @Test
    void scrollToIfNeeded_treeTableView_rightAfterRootReplaced_reachesNewlyAddedIndex() throws InterruptedException {
        var visible = onFxThread(() -> {
            var treeTableView = newTreeTableView(3, 200, 300); // all 3 fit, nothing to scroll
            treeTableView.setRoot(newRoot(200, "big-item-"));
            TableUtils.scrollToIfNeeded(treeTableView, 199, ScrollPosition.CENTER);
            return TableUtils.isFullyVisible(treeTableView, 199);
        });

        assertThat(visible).isTrue();
    }
}
