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
import javafx.scene.Scene;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.skin.VirtualFlow;
import javafx.stage.Stage;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Exercises {@link TreeViewUtils} against a real, shown {@code TreeView} — {@code TreeViewSkin} realizes cells
 * (with indentation/disclosure nodes affecting row height) differently from {@code ListViewSkin}, which is
 * exactly why this gets its own test class instead of assuming {@code ListViewUtilsTest} generalizes. See
 * {@code VirtualFlowUtils} for why a real display is required and why this can't run on a display-less CI
 * runner as-is.
 *
 * <p>The tree used here is deliberately flat (an invisible root with {@code itemCount} direct, non-expandable
 * children) so its row geometry is directly comparable to {@code ListViewUtilsTest}'s flat item list, rather
 * than entangling this with tree-expansion behavior, which is a separate concern.
 *
 * @author Pavel Castornii
 */
class TreeViewUtilsTest {

    private static final class Pair<A, B> {

        private final A first;

        private final B second;

        private Pair(A first, B second) {
            this.first = first;
            this.second = second;
        }
    }

    /**
     * A plain, non-observable holder — mutating {@link #setText} does not fire any change event, the same way
     * a domain object's field can mutate without the cell showing it finding out on its own. Used by the
     * {@code updateCell}/{@code updateCells} tests, which are specifically about forcing a redraw of such
     * silently-mutated data.
     */
    private static final class MutableItem {

        private String text;

        private MutableItem(String text) {
            this.text = text;
        }

        private String getText() {
            return text;
        }

        private void setText(String text) {
            this.text = text;
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
     * Builds a flat {@code TreeView} (invisible root, {@code itemCount} direct children) as the content of
     * {@link #stage}'s scene at {@code width}x{@code height}, shows the stage (a no-op if already showing)
     * and forces a layout pass, so its {@code VirtualFlow} has realized cells with real, non-zero measurements
     * immediately.
     *
     * <p>Must be called on the FX Application Thread.
     */
    private static TreeView<String> newTreeView(int itemCount, double width, double height) {
        var treeView = new TreeView<String>();
        treeView.setRoot(newRoot(itemCount, "item-"));
        treeView.setShowRoot(false);
        stage.setScene(new Scene(treeView, width, height));
        if (!stage.isShowing()) {
            stage.show();
        }
        treeView.applyCss();
        treeView.layout();
        return treeView;
    }

    private static TreeItem<String> newRoot(int itemCount, String prefix) {
        var root = new TreeItem<String>("root");
        for (int i = 0; i < itemCount; i++) {
            root.getChildren().add(new TreeItem<>(prefix + i));
        }
        root.setExpanded(true);
        return root;
    }

    private static VirtualFlow<?> flowOf(TreeView<?> treeView) {
        return (VirtualFlow<?>) treeView.lookup(".virtual-flow");
    }

    private static int firstVisibleIndex(TreeView<?> treeView) {
        return flowOf(treeView).getFirstVisibleCell().getIndex();
    }

    private static int lastVisibleIndex(TreeView<?> treeView) {
        return flowOf(treeView).getLastVisibleCell().getIndex();
    }

    /**
     * Builds a flat {@code TreeView} of {@link MutableItem}s the same way {@link #newTreeView} does for plain
     * strings — see there for details. Used by the {@code updateCell}/{@code updateCells} tests, which need
     * an item whose displayed value can mutate without the tree's structure itself changing.
     */
    private static TreeView<MutableItem> newMutableTreeView(int itemCount, double width, double height) {
        var root = new TreeItem<MutableItem>(new MutableItem("root"));
        for (int i = 0; i < itemCount; i++) {
            root.getChildren().add(new TreeItem<>(new MutableItem("item-" + i)));
        }
        root.setExpanded(true);
        var treeView = new TreeView<>(root);
        treeView.setShowRoot(false);
        treeView.setCellFactory(tv -> new TreeCell<MutableItem>() {
            @Override
            protected void updateItem(MutableItem item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getText());
            }
        });
        stage.setScene(new Scene(treeView, width, height));
        if (!stage.isShowing()) {
            stage.show();
        }
        treeView.applyCss();
        treeView.layout();
        return treeView;
    }

    /**
     * Returns the currently rendered text of the cell showing {@code index}, or {@code null} if that index
     * isn't currently realized.
     */
    private static String cellText(TreeView<?> treeView, int index) {
        for (var node : treeView.lookupAll(".tree-cell")) {
            if (node instanceof TreeCell<?>) {
                var cell = (TreeCell<?>) node;
                if (!cell.isEmpty() && cell.getIndex() == index) {
                    return cell.getText();
                }
            }
        }
        return null;
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

    // isFullyVisible

    @Test
    void isFullyVisible_indexWithinInitialViewport_returnsTrue() throws InterruptedException {
        var visible = onFxThread(() -> {
            var treeView = newTreeView(100, 200, 300);
            return TreeViewUtils.isFullyVisible(treeView, 0);
        });

        assertThat(visible).isTrue();
    }

    @Test
    void isFullyVisible_indexBeyondInitialViewport_returnsFalse() throws InterruptedException {
        var visible = onFxThread(() -> {
            var treeView = newTreeView(100, 200, 300);
            return TreeViewUtils.isFullyVisible(treeView, 99);
        });

        assertThat(visible).isFalse();
    }

    @Test
    void isFullyVisible_afterRootReplaced_reflectsNewViewport() throws InterruptedException {
        // This is the regression this whole utility exists for: reading the viewport right after a structural
        // change must be reliable, not just work on an already-stable view.
        var visible = onFxThread(() -> {
            var treeView = newTreeView(3, 200, 300); // all 3 fit, nothing to scroll
            treeView.setRoot(newRoot(200, "big-item-"));
            return new Pair<>(TreeViewUtils.isFullyVisible(treeView, 0), TreeViewUtils.isFullyVisible(treeView, 150));
        });

        assertThat(visible.first).isTrue();
        assertThat(visible.second).isFalse();
    }

    // scrollTo

    @Test
    void scrollTo_positionStart_targetBecomesFirstVisibleRow() throws InterruptedException {
        var result = onFxThread(() -> {
            var treeView = newTreeView(100, 200, 300);
            TreeViewUtils.scrollTo(treeView, 50, ScrollPosition.START);
            return new Pair<>(firstVisibleIndex(treeView), TreeViewUtils.isFullyVisible(treeView, 50));
        });

        assertThat(result.first).isEqualTo(50);
        assertThat(result.second).isTrue();
    }

    @Test
    void scrollTo_positionEnd_targetBecomesLastFullyVisibleRow() throws InterruptedException {
        var result = onFxThread(() -> {
            var treeView = newTreeView(100, 200, 300);
            TreeViewUtils.scrollTo(treeView, 50, ScrollPosition.END);
            return new Pair<>(TreeViewUtils.isFullyVisible(treeView, 50), TreeViewUtils.isFullyVisible(treeView, 51));
        });

        assertThat(result.first).isTrue();
        assertThat(result.second).isFalse();
    }

    @Test
    void scrollTo_positionCenter_targetLandsWithinRoughlyEqualNeighborsOnEachSide() throws InterruptedException {
        var result = onFxThread(() -> {
            var treeView = newTreeView(200, 200, 300);
            TreeViewUtils.scrollTo(treeView, 100, ScrollPosition.CENTER);
            return new Pair<>(firstVisibleIndex(treeView), lastVisibleIndex(treeView));
        });

        assertThat(result.first).isLessThanOrEqualTo(100);
        assertThat(result.second).isGreaterThanOrEqualTo(100);
    }

    // scrollToIfNeeded

    @Test
    void scrollToIfNeeded_indexAlreadyFullyVisible_doesNotMovePosition() throws InterruptedException {
        var result = onFxThread(() -> {
            var treeView = newTreeView(100, 200, 300);
            var flow = flowOf(treeView);
            var positionBefore = flow.getPosition();
            TreeViewUtils.scrollToIfNeeded(treeView, 0, ScrollPosition.CENTER);
            return new Pair<>(positionBefore, flow.getPosition());
        });

        assertThat(result.second).isEqualTo(result.first);
    }

    @Test
    void scrollToIfNeeded_indexNotVisible_scrollsToRequestedPosition() throws InterruptedException {
        var first = onFxThread(() -> {
            var treeView = newTreeView(100, 200, 300);
            TreeViewUtils.scrollToIfNeeded(treeView, 60, ScrollPosition.START);
            return firstVisibleIndex(treeView);
        });

        assertThat(first).isEqualTo(60);
    }

    @Test
    void scrollToIfNeeded_rightAfterRootReplaced_reachesNewlyAddedIndex() throws InterruptedException {
        // Mirrors the real bug this API was built to fix: select+reveal an index that only exists after a
        // structural change, in the same call that discovers it, with no separate warm-up layout pass.
        var visible = onFxThread(() -> {
            var treeView = newTreeView(3, 200, 300); // all 3 fit, nothing to scroll
            treeView.setRoot(newRoot(200, "big-item-"));
            TreeViewUtils.scrollToIfNeeded(treeView, 199, ScrollPosition.CENTER);
            return TreeViewUtils.isFullyVisible(treeView, 199);
        });

        assertThat(visible).isTrue();
    }

    // updateCell

    @Test
    void updateCell_itemMutatedInPlace_cellTextReflectsNewValue() throws InterruptedException {
        var result = onFxThread(() -> {
            var treeView = newMutableTreeView(50, 200, 300);
            treeView.getRoot().getChildren().get(5).getValue().setText("changed");
            var stillStale = cellText(treeView, 5);
            TreeViewUtils.updateCell(treeView, 5);
            return new Pair<>(stillStale, cellText(treeView, 5));
        });

        assertThat(result.first).isEqualTo("item-5");
        assertThat(result.second).isEqualTo("changed");
    }

    @Test
    void updateCell_indexOutOfRange_doesNotThrow() throws InterruptedException {
        onFxThread(() -> {
            var treeView = newMutableTreeView(50, 200, 300);
            TreeViewUtils.updateCell(treeView, 999);
            return null;
        });
    }

    // updateCells

    @Test
    void updateCells_onlyVisibleTrue_leavesCellsBeyondViewportStale() throws InterruptedException {
        var result = onFxThread(() -> {
            var treeView = newMutableTreeView(50, 200, 300);
            treeView.getRoot().getChildren().get(0).getValue().setText("changed-visible");
            treeView.getRoot().getChildren().get(49).getValue().setText("changed-far");
            TreeViewUtils.updateCells(treeView, true);
            return new Pair<>(cellText(treeView, 0), cellText(treeView, 49));
        });

        assertThat(result.first).isEqualTo("changed-visible");
        // Cell 49 isn't realized at all in a fresh, unscrolled view, so there is nothing to even read; the
        // point is only that updateCells(true) doesn't force it into existence the way false does below.
        assertThat(result.second).isNull();
    }

    @Test
    void updateCells_onlyVisibleFalse_realizesAndUpdatesCellNeverScrolledTo() throws InterruptedException {
        var text = onFxThread(() -> {
            var treeView = newMutableTreeView(50, 200, 300);
            treeView.getRoot().getChildren().get(49).getValue().setText("changed-far");
            TreeViewUtils.updateCells(treeView, false);
            return cellText(treeView, 49);
        });

        assertThat(text).isEqualTo("changed-far");
    }
}
