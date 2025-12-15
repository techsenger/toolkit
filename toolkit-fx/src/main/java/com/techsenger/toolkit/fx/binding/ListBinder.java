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
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

/**
 * Binds the content of two {@link ObservableList}s by keeping one list synchronized with another using a mapping
 * function.
 *
 * @param <T> the element type of the target list
 * @param <S> the element type of the source list
 *
 * @author Pavel Castornii
 */
public final class ListBinder<T, S> {

    /**
     * Creates a binder that keeps {@code targetList} synchronized with {@code sourceList} by mapping elements of
     * {@code sourceList} to {@code targetList}.
     *
     * @param targetList the target list to be synchronized
     * @param sourceList the source list to synchronize from
     * @param mapper maps elements of {@code sourceList} to elements of {@code targetList}
     *
     * @return a {@code ListBinder} that keeps the lists synchronized
     */
    public static <T, S> ListBinder<T, S> bindContent(ObservableList<T> targetList, ObservableList<S> sourceList,
            Function<S, T> mapper) {
        return new ListBinder<>(targetList, sourceList, mapper, null, null);
    }

    /**
     * Creates a binder that keeps {@code targetList} synchronized with {@code sourceList} and invokes callbacks when
     * elements are added or removed.
     *
     * @param targetList the target list to be synchronized
     * @param sourceList the source list to synchronize from
     * @param mapper maps elements of {@code sourceList} to elements of {@code targetList}
     * @param onAdded callback invoked when an element is added to {@code sourceList}
     * @param onRemoved callback invoked when an element is removed from {@code sourceList}
     *
     * @return a {@code ListBinder} that keeps the lists synchronized
     */
    public static <T, S> ListBinder<T, S> bindContent(ObservableList<T> targetList, ObservableList<S> sourceList,
            Function<S, T> mapper, Consumer<S> onAdded, Consumer<S> onRemoved) {
        return new ListBinder<>(targetList, sourceList, mapper, onAdded, onRemoved);
    }

    private final ObservableList<T> targetList;

    private final ObservableList<S> sourceList;

    private final Function<S, T> mapper;

    private final ListChangeListener<S> listener;

    private final Consumer<S> onAdded;

    private final Consumer<S> onRemoved;

    private ListBinder(ObservableList<T> targetList, ObservableList<S> sourceList, Function<S, T> mapper,
                          Consumer<S> onAdded, Consumer<S> onRemoved) {
        this.targetList = targetList;
        this.sourceList = sourceList;
        this.mapper = mapper;
        this.onAdded = onAdded;
        this.onRemoved = onRemoved;
        synchronizeAll();
        this.listener = this::handleChanges;
        sourceList.addListener(listener);
    }

    public void unbind() {
        sourceList.removeListener(listener);
    }

    private void synchronizeAll() {
        targetList.clear();
        sourceList.forEach(item -> targetList.add(mapper.apply(item)));
    }

    private void handleChanges(ListChangeListener.Change<? extends S> change) {
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

    private void handleAdditions(ListChangeListener.Change<? extends S> change) {
        int startIndex = change.getFrom();
        for (int i = 0; i < change.getAddedSize(); i++) {
            S addedItem = change.getList().get(startIndex + i);
            if (this.onAdded != null) {
                this.onAdded.accept(addedItem);
            }
            targetList.add(startIndex + i, mapper.apply(addedItem));
        }
    }

    private void handleRemovals(ListChangeListener.Change<? extends S> change) {
        targetList.subList(change.getFrom(), change.getFrom() + change.getRemovedSize()).clear();
        if (this.onRemoved != null) {
            for (var item : change.getRemoved()) {
                this.onRemoved.accept(item);
            }
        }
    }

    private void handleReplacements(ListChangeListener.Change<? extends S> change) {
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
            S newItem = change.getList().get(i);
            targetList.set(i, mapper.apply(newItem));
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
            targetList.set(i, mapper.apply(change.getList().get(i)));
        }
    }
}
