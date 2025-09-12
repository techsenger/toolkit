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

package com.techsenger.toolkit.fx.pulse;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.Scene;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Pavel Castornii
 */
public class PulseListenerManager {

    private static class ListenerWrapper {

        private final LayoutPulseListener listener;

        private boolean executed;

        ListenerWrapper(LayoutPulseListener listener) {
            this.listener = listener;
        }

        public LayoutPulseListener getListener() {
            return listener;
        }

        public boolean isExecuted() {
            return executed;
        }

        public void setExecuted(boolean executed) {
            this.executed = executed;
        }
    }

    private static final Logger logger = LoggerFactory.getLogger(PulseListenerManager.class);

    private final String parentInfo;

    private final Supplier<ReadOnlyObjectProperty<Scene>> sceneSupplier;

    private ChangeListener<? super Scene> sceneListener;

    private Runnable preLayoutPulseListener;

    private Runnable postLayoutPulseListener;

    private final List<ListenerWrapper> preLayoutWrappers = new CopyOnWriteArrayList<>();

    private final List<ListenerWrapper> postLayoutWrappers = new CopyOnWriteArrayList<>();

    public PulseListenerManager(String parentInfo, Supplier<ReadOnlyObjectProperty<Scene>> sceneSupplier) {
        this.parentInfo = parentInfo;
        this.sceneSupplier = sceneSupplier;
    }

    /**
     * Adds a layout pulse listener for the specified phase.
     * <p>
     * If a listener is added or removed during the pulse execution (inside another listener's {@code onLayoutPulse}
     * method), the changes will take effect only on the next pulse event. The current iteration will continue
     * with the snapshot of listeners that existed at the start of the pulse.
     *
     * @param phase the layout phase (PRE or POST)
     * @param listener the listener to add
     */
    public void addListener(LayoutPhase phase, LayoutPulseListener listener) {
        //if we have scene, we add pulse listener, otherwise we add scene listener
        var scene = sceneSupplier.get().get();
        switch (phase) {
            case PRE:
                if (this.preLayoutWrappers.isEmpty()) {
                    if (this.sceneListener == null) {
                        if (scene == null) {
                            addSceneListener();
                        } else {
                            addPreLayoutPulseListener(scene);
                        }
                    }
                }
                this.preLayoutWrappers.add(new ListenerWrapper(listener));
                break;
            case POST:
                if (this.postLayoutWrappers.isEmpty()) {
                    if (this.sceneListener == null) {
                        if (scene == null) {
                            addSceneListener();
                        } else {
                            addPostLayoutPulseListener(scene);
                        }
                    }
                }
                this.postLayoutWrappers.add(new ListenerWrapper(listener));
                break;
            default:
                throw new AssertionError();
        }
    }

    /**
     * Removes a layout pulse listener for the specified phase.
     * <p>
     * If a listener is removed during the pulse execution (inside another listener's {@code onLayoutPulse} method),
     * the removal will take effect only on the next pulse event. The current iteration will continue with the
     * snapshot of listeners that existed at the start of the pulse.
     *
     * @param phase the layout phase (PRE or POST)
     * @param listener the listener to remove
     */
    public void removeListener(LayoutPhase phase, LayoutPulseListener listener) {
        var scene = sceneSupplier.get().get();
        int index;
        switch (phase) {
            case PRE:
                index = findListener(preLayoutWrappers, listener);
                if (index == -1) {
                    return;
                }
                this.preLayoutWrappers.remove(index);
                if (this.sceneListener == null) {
                    checkPreLayoutPulseListener(scene);
                } else {
                    checkSceneListener();
                }
                break;
            case POST:
                index = findListener(postLayoutWrappers, listener);
                if (index == -1) {
                    return;
                }
                this.postLayoutWrappers.remove(index);
                if (this.sceneListener == null) {
                    checkPostLayoutPulseListener(scene);
                } else {
                    checkSceneListener();
                }
                break;
            default:
                throw new AssertionError();
        }
    }

    private int findListener(List<ListenerWrapper> wrappers, LayoutPulseListener listener) {
        for (var i = 0; i < wrappers.size(); i++) {
            var wrapper = wrappers.get(i);
            if (wrapper.getListener() == listener) {
                return i;
            }
        }
        return -1;
    }

    private void addSceneListener() {
        this.sceneListener = (ov, oldV, newV) -> {
            if (newV != null) {
                if (!this.preLayoutWrappers.isEmpty()) {
                    addPreLayoutPulseListener(newV);
                }
                if (!this.postLayoutWrappers.isEmpty()) {
                    addPostLayoutPulseListener(newV);
                }
                removeSceneListener();
            }
        };
        sceneSupplier.get().addListener(this.sceneListener);
        logger.debug("Added scene listener for {}", this.parentInfo);
    }

    private void checkSceneListener() {
        if (this.preLayoutWrappers.isEmpty() && this.postLayoutWrappers.isEmpty()) {
            removeSceneListener();
        }
    }

    private void removeSceneListener() {
        if (this.sceneListener != null) {
            sceneSupplier.get().removeListener(this.sceneListener);
            this.sceneListener = null;
            logger.debug("Removed scene listener for {}", this.parentInfo);
        }
    }

    private void addPreLayoutPulseListener(Scene scene) {
        this.preLayoutPulseListener = () -> {
            callListeners(this.preLayoutWrappers);
            checkPreLayoutPulseListener(scene);
        };
        scene.addPreLayoutPulseListener(this.preLayoutPulseListener);
        logger.debug("Added pre layout pulse listener for {}", this.parentInfo);
    }

    private void checkPreLayoutPulseListener(Scene scene) {
        if (this.preLayoutWrappers.isEmpty()) {
            removePreLayoutPulseListener(scene);
        }
    }

    private void removePreLayoutPulseListener(Scene scene) {
        if (this.preLayoutPulseListener != null) {
            scene.removePreLayoutPulseListener(this.preLayoutPulseListener);
            this.preLayoutPulseListener = null;
            logger.debug("Removed pre layout pulse listener for {}", this.parentInfo);
        }
    }

    private void addPostLayoutPulseListener(Scene scene) {
        this.postLayoutPulseListener = () -> {
            callListeners(this.postLayoutWrappers);
            checkPostLayoutPulseListener(scene);
        };
        scene.addPostLayoutPulseListener(this.postLayoutPulseListener);
        logger.debug("Added post layout pulse listener for {}", this.parentInfo);
    }

    private void checkPostLayoutPulseListener(Scene scene) {
        if (this.postLayoutWrappers.isEmpty()) {
            removePostLayoutPulseListener(scene);
        }
    }

    private void removePostLayoutPulseListener(Scene scene) {
        if (this.postLayoutPulseListener != null) {
            scene.removePostLayoutPulseListener(this.postLayoutPulseListener);
            this.postLayoutPulseListener = null;
            logger.debug("Removed post layout pulse listener for {}", this.parentInfo);
        }
    }

    private void callListeners(List<ListenerWrapper> wrappers) {
        boolean executedPresent = false;
        for (var wrapper : wrappers) {
           var listener = wrapper.getListener();
           if (!listener.onLayoutPulse()) {
               wrapper.setExecuted(true);
               executedPresent = true;
           }
       }
       if (executedPresent) {
           wrappers.removeIf(ListenerWrapper::isExecuted);
       }
    }
}
