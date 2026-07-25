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
import com.techsenger.toolkit.fx.utils.VirtualFlowUtils.ScrollPosition;
import java.util.function.Supplier;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.skin.VirtualFlow;
import javafx.stage.Stage;
import static org.assertj.core.api.Assertions.assertThat;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Exercises {@link ListViewUtils} against a real, shown {@code ListView} — see {@code VirtualFlowUtils} for
 * why a real display is required and why this can't run on a display-less CI runner as-is.
 *
 * @author Pavel Castornii
 */
class ListViewUtilsTest {

    private static final class Pair<A, B> {

        private final A first;

        private final B second;

        private Pair(A first, B second) {
            this.first = first;
            this.second = second;
        }
    }

    private static final Offset<Integer> ONE_ROW = Offset.offset(1);

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
     * Builds a {@code ListView} with {@code itemCount} string items, makes it the content of {@link #stage}'s
     * scene at {@code width}x{@code height}, shows the stage (a no-op if already showing) and forces a layout
     * pass, so its {@code VirtualFlow} has realized cells with real, non-zero measurements immediately.
     *
     * <p>Must be called on the FX Application Thread.
     */
    private static ListView<String> newListView(int itemCount, double width, double height) {
        var items = FXCollections.<String>observableArrayList();
        for (int i = 0; i < itemCount; i++) {
            items.add("item-" + i);
        }
        var listView = new ListView<>(items);
        stage.setScene(new Scene(listView, width, height));
        if (!stage.isShowing()) {
            stage.show();
        }
        listView.applyCss();
        listView.layout();
        return listView;
    }

    private static VirtualFlow<?> flowOf(ListView<?> listView) {
        return (VirtualFlow<?>) listView.lookup(".virtual-flow");
    }

    private static int firstVisibleIndex(ListView<?> listView) {
        return flowOf(listView).getFirstVisibleCell().getIndex();
    }

    private static int lastVisibleIndex(ListView<?> listView) {
        return flowOf(listView).getLastVisibleCell().getIndex();
    }

    private static ObservableList<String> bigItemList(int itemCount) {
        var items = FXCollections.<String>observableArrayList();
        for (int i = 0; i < itemCount; i++) {
            items.add("big-item-" + i);
        }
        return items;
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
            var listView = newListView(100, 200, 300);
            return ListViewUtils.isFullyVisible(listView, 0);
        });

        assertThat(visible).isTrue();
    }

    @Test
    void isFullyVisible_indexBeyondInitialViewport_returnsFalse() throws InterruptedException {
        var visible = onFxThread(() -> {
            var listView = newListView(100, 200, 300);
            return ListViewUtils.isFullyVisible(listView, 99);
        });

        assertThat(visible).isFalse();
    }

    @Test
    void isFullyVisible_negativeIndex_returnsFalse() throws InterruptedException {
        var visible = onFxThread(() -> {
            var listView = newListView(100, 200, 300);
            return ListViewUtils.isFullyVisible(listView, -1);
        });

        assertThat(visible).isFalse();
    }

    @Test
    void isFullyVisible_emptyControlNeverShown_returnsFalse() throws InterruptedException {
        var visible = onFxThread(() -> ListViewUtils.isFullyVisible(new ListView<String>(), 0));

        assertThat(visible).isFalse();
    }

    @Test
    void isFullyVisible_afterItemsReplaced_reflectsNewViewport() throws InterruptedException {
        // This is the regression this whole utility exists for: reading the viewport right after a structural
        // change must be reliable, not just work on an already-stable view.
        var visible = onFxThread(() -> {
            var listView = newListView(3, 200, 300); // all 3 fit, nothing to scroll
            listView.setItems(bigItemList(200));
            return new Pair<>(ListViewUtils.isFullyVisible(listView, 0), ListViewUtils.isFullyVisible(listView, 150));
        });

        assertThat(visible.first).isTrue();
        assertThat(visible.second).isFalse();
    }

    // scrollTo

    @Test
    void scrollTo_positionTop_targetBecomesFirstVisibleRow() throws InterruptedException {
        var result = onFxThread(() -> {
            var listView = newListView(100, 200, 300);
            ListViewUtils.scrollTo(listView, 50, ScrollPosition.TOP);
            return new Pair<>(firstVisibleIndex(listView), ListViewUtils.isFullyVisible(listView, 50));
        });

        assertThat(result.first).isEqualTo(50);
        assertThat(result.second).isTrue();
    }

    @Test
    void scrollTo_positionBottom_targetBecomesLastFullyVisibleRow() throws InterruptedException {
        var result = onFxThread(() -> {
            var listView = newListView(100, 200, 300);
            ListViewUtils.scrollTo(listView, 50, ScrollPosition.BOTTOM);
            return new Pair<>(ListViewUtils.isFullyVisible(listView, 50), ListViewUtils.isFullyVisible(listView, 51));
        });

        assertThat(result.first).isTrue();
        assertThat(result.second).isFalse();
    }

    @Test
    void scrollTo_positionCenter_targetLandsWithRoughlyEqualNeighborsOnEachSide() throws InterruptedException {
        var result = onFxThread(() -> {
            var listView = newListView(200, 200, 300);
            ListViewUtils.scrollTo(listView, 100, ScrollPosition.CENTER);
            return new Pair<>(firstVisibleIndex(listView), lastVisibleIndex(listView));
        });

        assertThat(result.first).isLessThanOrEqualTo(100);
        assertThat(result.second).isGreaterThanOrEqualTo(100);
        var before = 100 - result.first;
        var after = result.second - 100;
        assertThat(before).isCloseTo(after, ONE_ROW);
    }

    @Test
    void scrollTo_positionCenterNearListStart_clampsInsteadOfScrollingPastZero() throws InterruptedException {
        var result = onFxThread(() -> {
            var listView = newListView(200, 200, 300);
            ListViewUtils.scrollTo(listView, 2, ScrollPosition.CENTER);
            return new Pair<>(firstVisibleIndex(listView), ListViewUtils.isFullyVisible(listView, 2));
        });

        assertThat(result.first).isEqualTo(0);
        assertThat(result.second).isTrue();
    }

    @Test
    void scrollTo_positionCenterNearListEnd_clampsInsteadOfScrollingPastLastItem() throws InterruptedException {
        var result = onFxThread(() -> {
            var listView = newListView(200, 200, 300);
            ListViewUtils.scrollTo(listView, 198, ScrollPosition.CENTER);
            return new Pair<>(lastVisibleIndex(listView), ListViewUtils.isFullyVisible(listView, 198));
        });

        assertThat(result.first).isEqualTo(199);
        assertThat(result.second).isTrue();
    }

    @Test
    void scrollTo_allItemsAlreadyFitViewport_leavesNothingToScroll() throws InterruptedException {
        var result = onFxThread(() -> {
            var listView = newListView(3, 200, 300);
            ListViewUtils.scrollTo(listView, 2, ScrollPosition.BOTTOM);
            return new Pair<>(ListViewUtils.isFullyVisible(listView, 0), ListViewUtils.isFullyVisible(listView, 2));
        });

        assertThat(result.first).isTrue();
        assertThat(result.second).isTrue();
    }

    @Test
    void scrollTo_rightAfterItemsReplaced_scrollsAgainstNewList() throws InterruptedException {
        var first = onFxThread(() -> {
            var listView = newListView(3, 200, 300);
            listView.setItems(bigItemList(200));
            ListViewUtils.scrollTo(listView, 150, ScrollPosition.TOP);
            return firstVisibleIndex(listView);
        });

        assertThat(first).isEqualTo(150);
    }

    // scrollToIfNeeded

    @Test
    void scrollToIfNeeded_indexAlreadyFullyVisible_doesNotMovePosition() throws InterruptedException {
        var result = onFxThread(() -> {
            var listView = newListView(100, 200, 300);
            var flow = flowOf(listView);
            var positionBefore = flow.getPosition();
            ListViewUtils.scrollToIfNeeded(listView, 0, ScrollPosition.CENTER);
            return new Pair<>(positionBefore, flow.getPosition());
        });

        assertThat(result.second).isEqualTo(result.first);
    }

    @Test
    void scrollToIfNeeded_indexNotVisible_scrollsToRequestedPosition() throws InterruptedException {
        var first = onFxThread(() -> {
            var listView = newListView(100, 200, 300);
            // 60, not close to 100: with ~14 rows fitting in a 300px viewport, an index too close to the end
            // of a 100-item list could not become the literal first visible row without leaving blank space
            // past the list end, so it would legitimately get clamped — this test isn't about clamping.
            ListViewUtils.scrollToIfNeeded(listView, 60, ScrollPosition.TOP);
            return firstVisibleIndex(listView);
        });

        assertThat(first).isEqualTo(60);
    }

    @Test
    void scrollToIfNeeded_rightAfterItemsReplaced_reachesNewlyAddedIndex() throws InterruptedException {
        // Mirrors the real bug this API was built to fix: select+reveal an index that only exists after a
        // structural change, in the same call that discovers it, with no separate warm-up layout pass.
        var visible = onFxThread(() -> {
            var listView = newListView(3, 200, 300); // all 3 fit, nothing to scroll
            listView.setItems(bigItemList(200));
            ListViewUtils.scrollToIfNeeded(listView, 199, ScrollPosition.CENTER);
            return ListViewUtils.isFullyVisible(listView, 199);
        });

        assertThat(visible).isTrue();
    }
}
