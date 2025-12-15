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

package com.techsenger.toolkit.fx.binding;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Pavel Castornii
 */
public class ListBinderTest {

    private static class TestItem {

        private final IntegerProperty value = new SimpleIntegerProperty();

        TestItem(int value) {
            this.value.set(value);
        }

        public int getValue() {
            return value.get();
        }

        public void setValue(int value) {
            this.value.set(value);
        }

        public IntegerProperty valueProperty() {
            return value;
        }
    }

    private ListBinder<String, Integer> binder;
    private ObservableList<String> targetList;
    private ObservableList<Integer> sourceList;
    private List<Integer> addedItems;
    private List<Integer> removedItems;

    @BeforeEach
    void setUp() {
        targetList = FXCollections.observableArrayList();
        sourceList = FXCollections.observableArrayList();
        addedItems = new ArrayList<>();
        removedItems = new ArrayList<>();
        binder = ListBinder.bindContent(targetList, sourceList, Object::toString, addedItems::add, removedItems::add);
    }

    @AfterEach
    void tearDown() {
        binder.unbind();
    }

    @Test
    void initialSync_withAddedElements_shouldMirrorToSecondary() {
        sourceList.addAll(1, 2, 3);

        assertThat(targetList).containsExactly("1", "2", "3");
        assertThat(addedItems).containsExactly(1, 2, 3);
    }

    @Test
    void addAtIndex_whenInsertingMiddleElement_shouldMaintainOrder() {
        sourceList.addAll(1, 3);
        sourceList.add(1, 2);

        assertThat(targetList).containsExactly("1", "2", "3");
        assertThat(addedItems).containsExactly(1, 3, 2);
    }

    @Test
    void remove_whenMiddleElementRemoved_shouldUpdateSecondary() {
        sourceList.addAll(1, 2, 3);
        sourceList.remove(1);

        assertThat(targetList).containsExactly("1", "3");
        assertThat(removedItems).containsExactly(2);
    }

    @Test
    void replace_whenElementReplaced_shouldUpdateSecondary() {
        sourceList.addAll(1, 2, 3);
        sourceList.set(1, 4);

        assertThat(targetList).containsExactly("1", "4", "3");
        assertThat(addedItems).containsExactly(1, 2, 3, 4);
        assertThat(removedItems).containsExactly(2);
    }

    @Test
    void permutation_whenListSorted_shouldMirrorOrder() {
        sourceList.addAll(3, 1, 2);
        FXCollections.sort(sourceList);

        assertThat(targetList).containsExactly("1", "2", "3");
        assertThat(addedItems).containsExactly(3, 1, 2);
        assertThat(removedItems).isEmpty();
    }

    @Test
    void permutation_whenListReversed_shouldMirrorOrder() {
        sourceList.addAll(1, 2, 3);
        sourceList.sort(Comparator.reverseOrder());

        assertThat(targetList).containsExactly("3", "2", "1");
        assertThat(addedItems).containsExactly(1, 2, 3);
        assertThat(removedItems).isEmpty();
    }

    @Test
    void update_whenElementUpdated_shouldUpdateSecondary() {
        ObservableList<TestItem> sourceItems = FXCollections.observableArrayList(
            item -> new Property[] {item.valueProperty()}
        );
        ObservableList<String> targetItems = FXCollections.observableArrayList();
        List<TestItem> added = new ArrayList<>();
        List<TestItem> removed = new ArrayList<>();

        ListBinder<String, TestItem> itemBinder = ListBinder.bindContent(
            targetItems, sourceItems,
            item -> Integer.toString(item.getValue()),
            added::add,
            removed::add
        );

        TestItem item1 = new TestItem(1);
        TestItem item2 = new TestItem(2);
        sourceItems.addAll(item1, item2);

        item1.setValue(10);

        assertThat(targetItems).containsExactly("10", "2");
        assertThat(added).containsExactly(item1, item2);
        assertThat(removed).isEmpty();

        itemBinder.unbind();
    }

    @Test
    void multipleReplacements_shouldWorkCorrectly() {
        sourceList.addAll(1, 2, 3, 4);
        sourceList.set(0, 5);
        sourceList.set(2, 6);
        sourceList.set(3, 7);

        assertThat(targetList).containsExactly("5", "2", "6", "7");
        assertThat(addedItems).containsExactly(1, 2, 3, 4, 5, 6, 7);
        assertThat(removedItems).containsExactly(1, 3, 4);
    }

    @Test
    void complexOperations_combinationOfAllTypes() {
        sourceList.addAll(1, 2, 3);
        sourceList.remove(1);
        sourceList.add(4);
        sourceList.set(0, 5);
        FXCollections.sort(sourceList);

        assertThat(targetList).containsExactly("3", "4", "5");
        assertThat(addedItems).containsExactly(1, 2, 3, 4, 5);
        assertThat(removedItems).containsExactly(2, 1);
    }
}

