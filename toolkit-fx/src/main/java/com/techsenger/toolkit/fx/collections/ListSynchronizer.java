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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
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
 * It’s important to note that the JavaFX API does not define when permutation changes should occur. For example,
 * {@code FXCollections.reverse(list)} performs a reordering using two types of changes — added and removed. On the
 * other hand, {@code List.sort(Comparator.reverseOrder())} produces permutation changes.
 * <p>
 * To stop synchronization, call {@link #dispose()}.
 *
 * @param <T> the type of elements in the source list
 * @param <S> the type of elements in the target list
 *
 * @author Pavel Castornii
 */
public class ListSynchronizer<T, S> {

    private final ObservableList<T> sourceList;

    private final ObservableList<S> targetList;

    private final Function<T, S> converter;

    private final ListChangeListener<T> listener;

    private final Consumer<T> onAdded;

    private final Consumer<T> onRemoved;

    public ListSynchronizer(ObservableList<T> sourceList, ObservableList<S> targetList, Function<T, S> converter) {
        this(sourceList, targetList, converter, null, null);
    }

    public ListSynchronizer(ObservableList<T> sourceList, ObservableList<S> targetList, Function<T, S> converter,
                          Consumer<T> onAdded, Consumer<T> onRemoved) {
        this.sourceList = sourceList;
        this.targetList = targetList;
        this.converter = converter;
        this.onAdded = onAdded;
        this.onRemoved = onRemoved;
        synchronizeAll();
        this.listener = this::handleChanges;
        sourceList.addListener(listener);
    }

    public ObservableList<T> getSourceList() {
        return sourceList;
    }

    public ObservableList<S> getTargetList() {
        return targetList;
    }

    public Function<T, S> getConverter() {
        return converter;
    }

    public Consumer<T> getOnAdded() {
        return onAdded;
    }

    public Consumer<T> getOnRemoved() {
        return onRemoved;
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

    private void handleChanges(ListChangeListener.Change<? extends T> change) {
        // | Operation  | wasAdded | wasRemoved | wasReplaced | wasPermutated | wasUpdated |
        // | ---------- | -------- | ---------- | ----------- | ------------- | ---------- |
        // | Replaced   | +        | +          | +           | -             | -          |
        // | Permutated | -        | -          | -           | +             | -          |
        // | Updated    | -        | -          | -           | -             | +          |
        // | Added      | +        | -          | -           | -             | -          |
        // | Removed    | -        | +          | -           | -             | -          |
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

    private void handleAdditions(ListChangeListener.Change<? extends T> change) {
        int startIndex = change.getFrom();
        for (int i = 0; i < change.getAddedSize(); i++) {
            T addedItem = change.getList().get(startIndex + i);
            if (this.onAdded != null) {
                this.onAdded.accept(addedItem);
            }
            targetList.add(startIndex + i, converter.apply(addedItem));
        }
    }

    private void handleRemovals(ListChangeListener.Change<? extends T> change) {
        targetList.subList(change.getFrom(), change.getFrom() + change.getRemovedSize()).clear();
        if (this.onRemoved != null) {
            for (var item : change.getRemoved()) {
                this.onRemoved.accept(item);
            }
        }
    }

    private void handleReplacements(ListChangeListener.Change<? extends T> change) {
        if (this.onRemoved != null && change.wasRemoved()) {
            for (var item : change.getRemoved()) {
                this.onRemoved.accept(item);
            }
        }
        if (this.onAdded != null && change.wasAdded()) {
            for (var item : change.getAddedSubList()) {
                this.onAdded.accept(item);
            }
        }
        for (int i = change.getFrom(); i < change.getTo(); i++) {
            T newItem = change.getList().get(i);
            targetList.set(i, converter.apply(newItem));
        }
    }

    private void handlePermutations(ListChangeListener.Change<? extends T> change) {
        List<S> tempCopy = new ArrayList<>(targetList);
        for (int oldIndex = change.getFrom(); oldIndex < change.getTo(); oldIndex++) {
            int newIndex = change.getPermutation(oldIndex);
            targetList.set(newIndex, tempCopy.get(oldIndex));
        }
    }

    private void handleUpdates(ListChangeListener.Change<? extends T> change) {
        for (int i = change.getFrom(); i < change.getTo(); i++) {
            targetList.set(i, converter.apply(change.getList().get(i)));
        }
    }
}
