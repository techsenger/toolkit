/*
 * Copyright 2025 Pavel Castornii.
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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

/**
 * A utility class that synchronizes two {@link ObservableList} instances in one direction.
 * <p>
 * The {@code sourceList} contains elements of type {@code S}.
 * The {@code targetList} contains corresponding elements of type {@code T},
 * which are derived from the source elements using the provided {@link Function} converter.
 * <p>
 * Whenever the {@code sourceList} changes (additions, removals, replacements, permutations, or updates),
 * the {@code targetList} is automatically updated to reflect these changes.
 * Synchronization is one-way only — modifications to the {@code targetList} do not affect the {@code sourceList}.
 * <p>
 * To stop synchronization, call {@link #dispose()}.
 *
 * @param <S> the type of elements in the source list
 * @param <T> the type of elements in the target list
 *
 * @author Pavel Castornii
 */
public class ListSynchronizer<S, T> {

    private final ObservableList<S> sourceList;

    private final ObservableList<T> targetList;

    private final Function<S, T> converter;

    private final ListChangeListener<S> listener;

    public ListSynchronizer(ObservableList<S> sourceList,
                          ObservableList<T> targetList,
                          Function<S, T> converter) {
        this.sourceList = sourceList;
        this.targetList = targetList;
        this.converter = converter;

        synchronizeAll();

        this.listener = this::handleChanges;
        sourceList.addListener(listener);
    }

    public ObservableList<S> getSourceList() {
        return sourceList;
    }

    public ObservableList<T> getTargetList() {
        return targetList;
    }

    public Function<S, T> getConverter() {
        return converter;
    }

    /**
     * Stops list synchronization. After calling this method, changes in sourceList will no longer be reflected
     * in targetList.
     */
    public void dispose() {
        sourceList.removeListener(listener);
    }

    private void synchronizeAll() {
        targetList.clear();
        sourceList.forEach(item -> targetList.add(converter.apply(item)));
    }

    private void handleChanges(ListChangeListener.Change<? extends S> change) {
        while (change.next()) {
            if (change.wasPermutated()) {
                handlePermutations(change);
            } else if (change.wasUpdated()) {
                handleUpdates(change);
            } else if (change.wasReplaced()) {
                handleReplacements(change);
            } else {
                if (change.wasRemoved()) {
                    handleRemovals(change);
                }
                if (change.wasAdded()) {
                    handleAdditions(change);
                }
            }
        }
    }

    private void handleAdditions(ListChangeListener.Change<? extends S> change) {
        int startIndex = change.getFrom();
        for (int i = 0; i < change.getAddedSize(); i++) {
            S addedItem = change.getList().get(startIndex + i);
            targetList.add(startIndex + i, converter.apply(addedItem));
        }
    }

    private void handleRemovals(ListChangeListener.Change<? extends S> change) {
        targetList.subList(change.getFrom(), change.getFrom() + change.getRemovedSize()).clear();
    }

    private void handleReplacements(ListChangeListener.Change<? extends S> change) {
        for (int i = change.getFrom(); i < change.getTo(); i++) {
            S newItem = change.getList().get(i);
            targetList.set(i, converter.apply(newItem));
        }
    }

    private void handlePermutations(ListChangeListener.Change<? extends S> change) {
        List<T> tempCopy = new ArrayList<>(targetList);
        for (int oldIndex = change.getFrom(); oldIndex < change.getTo(); oldIndex++) {
            int newIndex = change.getPermutation(oldIndex);
            targetList.set(newIndex, tempCopy.get(oldIndex));
        }
    }

    private void handleUpdates(ListChangeListener.Change<? extends S> change) {
        for (int i = change.getFrom(); i < change.getTo(); i++) {
            targetList.set(i, converter.apply(change.getList().get(i)));
        }
    }
}
