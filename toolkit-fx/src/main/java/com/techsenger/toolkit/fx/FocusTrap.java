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

package com.techsenger.toolkit.fx;

import java.util.ArrayList;
import java.util.List;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.ToolBar;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Traps focus inside pane, so, that user can't move focus outside pane. Important! This method must be called
 * only after adding all controls to pane, at the same time they can be invisible.
 *
 * @author Pavel Castornii
 */
public class FocusTrap {

    private static final Logger logger = LoggerFactory.getLogger(FocusTrap.class);

    private final Pane pane;

    private final List<Control> controls = new ArrayList<>();

    private final EventHandler<? super KeyEvent> handler;

    private boolean activated = false;

    public FocusTrap(Pane pane) {
        this.pane = pane;
        this.handler = event -> {
            if (event.getCode() == KeyCode.TAB) {
                int currentIndex = controls.indexOf(pane.getScene().getFocusOwner());
                if (event.isShiftDown()) {
                    // Shift + Tab (focus backward)
                    if (currentIndex >= 0) {
                        //we need to find previous node or the last node
                        var newControl = findBackward(currentIndex - 1);
                        if (newControl == null) {
                            newControl = findBackward(controls.size() - 1);
                        }
                        if (newControl != null) {
                            newControl.requestFocus();
                        }
                        event.consume();
                    }
                } else {
                    // Tab (focus forward)
                    if (currentIndex >= 0) {
                        //we need to find next node or the first node
                        var newControl = findForward(currentIndex + 1);
                        if (newControl == null) {
                            newControl = findForward(0);
                        }
                        if (newControl != null) {
                            newControl.requestFocus();
                        }
                        event.consume();
                    }
                }
            }
        };
    }

    /**
     * Activates this trap.
     */
    public void activate() {
        pane.addEventFilter(KeyEvent.KEY_PRESSED, this.handler);
        this.activated = true;
        this.update();
    }

    /**
     * Deactivates this trap.
     */
    public void deactivate() {
        this.pane.removeEventFilter(KeyEvent.KEY_PRESSED, handler);
        this.activated = false;
    }

    /**
     * Returns activation status.
     *
     * @return
     */
    public boolean isActivated() {
        return activated;
    }

    /**
     * This method must be called when children were added/removed from pane.
     */
    public void update() {
        if (!activated) {
            throw new IllegalStateException("Can't update not activated trap");
        }
        this.controls.clear();
        this.findControls(pane.getChildren());
        if (logger.isDebugEnabled()) {
            var builder = new StringBuilder();
            for (var c: controls) {
                builder.append(System.lineSeparator());
                builder.append("\t");
                builder.append(c.toString());
            }
            logger.debug("Focus will be trapped within controls: {}", builder.toString());
        }

    }

    private Control findBackward(Integer from) {
        for (var i = from; i >= 0; i--) {
            var control = controls.get(i);
            if (control.isFocusTraversable() && control.isVisible() && !control.isDisable()
                    && !control.isDisabled()) {
                return control;
            }
        }
        return null;
    }

    private Control findForward(Integer from) {
        for (var i = from; i < controls.size(); i++) {
            var control = controls.get(i);
            if (control.isFocusTraversable() && control.isVisible() && !control.isDisable()
                    && !control.isDisabled()) {
                return control;
            }
        }
        return null;
    }

    /**
     * Traverses recursively all controls in pane and calls handler for every control.
     *
     * @param pane
     * @param handler
     */
    private void findControls(List<Node> nodes) {
        for (Node node : nodes) {
            if (node instanceof Pane) {
                findControls(((Pane) node).getChildren());
            } else if (node instanceof ToolBar) {
                findControls(((ToolBar) node).getItems());
            } else if (node instanceof Control) {
                controls.add((Control) node);
            }
        }
    }
}
