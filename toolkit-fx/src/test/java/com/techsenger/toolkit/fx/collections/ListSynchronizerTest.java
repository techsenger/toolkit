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

package com.techsenger.toolkit.fx.collections;

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
public class ListSynchronizerTest {

    private ObservableList<Integer> source;
    private ObservableList<String> target;
    private ListSynchronizer<Integer, String> synchronizer;

    @BeforeEach
    void setUp() {
        source = FXCollections.observableArrayList();
        target = FXCollections.observableArrayList();
        synchronizer = new ListSynchronizer<>(source, target, Object::toString);
    }

    @AfterEach
    void tearDown() {
        synchronizer.dispose();
    }

    @Test
    void initialSync_withAddedElements_shouldMirrorToSecondary() {
        source.addAll(1, 2, 3);

        assertThat(target)
            .hasSize(3)
            .containsExactly("1", "2", "3");
    }

    @Test
    void addAtIndex_whenInsertingMiddleElement_shouldMaintainOrder() {
        source.addAll(1, 3);
        source.add(1, 2);

        assertThat(target)
            .hasSize(3)
            .containsExactly("1", "2", "3");
    }

    @Test
    void remove_whenMiddleElementRemoved_shouldUpdateSecondary() {
        source.addAll(1, 2, 3);
        source.remove(1);

        assertThat(target)
            .hasSize(2)
            .containsExactly("1", "3");
    }

    @Test
    void replace_whenFirstElementReplaced_shouldUpdateSecondary() {
        source.addAll(1, 2);
        source.set(0, 3);

        assertThat(target)
            .hasSize(2)
            .containsExactly("3", "2");
    }

    @Test
    void dispose_whenCalled_shouldStopSynchronization() {
        synchronizer.dispose();
        source.add(1);

        assertThat(target).isEmpty();
    }

    @Test
    void permutation_whenListSorted_shouldMirrorOrder() {
        source.addAll(3, 1, 2);
        source.sort(Integer::compareTo);

        assertThat(target)
            .hasSize(3)
            .containsExactly("1", "2", "3");
    }

}
