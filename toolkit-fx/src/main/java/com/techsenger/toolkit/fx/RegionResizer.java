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

import javafx.beans.property.DoubleProperty;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;

/**
 * This class makes region, for example dialog, resizable. Important! Resizable region must be located in unmanaged
 * Pane or use region.setManaged(false) if it is inside managed panes like StackPane etc.
 *
 * <p>If min/max width/height are changed after dialog resizing then dialog size won't be updated.
 *
 * @author Pavel Castornii
 */
public class RegionResizer extends AbstractResizer {

    private Region region;

    private final EventHandler<MouseEvent> mousedPressedHandler = (event) -> mousePressed(event);

    private final EventHandler<MouseEvent> mousedMovedHandler = (event) -> mouseMoved(event);

    private final EventHandler<MouseEvent> mousedDraggedHandler = (event) -> mouseDragged(event);

    private final EventHandler<MouseEvent> mousedReleasedHandler = (event) -> mouseReleased(event);

    private boolean validateSize = true;

    public RegionResizer(DoubleProperty minWidth, DoubleProperty minHeight,
            DoubleProperty maxWidth, DoubleProperty maxHeight, EventHandler<? super MouseEvent> startedHandler,
            EventHandler<? super MouseEvent> finishedHandler) {
        super(minWidth, minHeight, maxWidth, maxHeight, startedHandler, finishedHandler);
    }

    /**
     * Initializes resizer for concrete region.
     *
     * @param region
     */
    public void initialize(Region region) {
        this.region = region;
        //we use filters because we need to get event event when there are other panes inside window (onAction handler
        //wont be called this case)
        this.region.addEventFilter(MouseEvent.MOUSE_PRESSED, mousedPressedHandler);
        this.region.addEventFilter(MouseEvent.MOUSE_MOVED, mousedMovedHandler);
        this.region.addEventFilter(MouseEvent.MOUSE_DRAGGED, mousedDraggedHandler);
        this.region.addEventFilter(MouseEvent.MOUSE_RELEASED, mousedReleasedHandler);
    }

    /**
     * Deinitialize resizer by removing all its handlers from region.
     */
    public void deinitialize() {
        this.region.removeEventFilter(MouseEvent.MOUSE_PRESSED, mousedPressedHandler);
        this.region.removeEventFilter(MouseEvent.MOUSE_MOVED, mousedMovedHandler);
        this.region.removeEventFilter(MouseEvent.MOUSE_DRAGGED, mousedDraggedHandler);
        this.region.removeEventFilter(MouseEvent.MOUSE_RELEASED, mousedReleasedHandler);
        this.region = null;
    }

    public boolean isValidateSize() {
        return validateSize;
    }

    public void setValidateSize(boolean validateSize) {
        this.validateSize = validateSize;
    }

    @Override
    protected double getWidth() {
        return this.region.getWidth();
    }

    @Override
    protected void setWidth(double width) {
        var valid = true;
        if (this.validateSize && !isWidthValid(width)) {
            valid = false;
        }
        if (valid) {
            region.setMaxWidth(width);
            region.setMinWidth(width);
        }
    }

    protected boolean isWidthValid(double width) {
        return width >= this.region.prefWidth(-1);
    }

    @Override
    protected double getHeight() {
        return this.region.getHeight();
    }

    @Override
    protected void setHeight(double height) {
        var valid = true;
        if (this.validateSize && !isHeightValid(height)) {
            valid = false;
        }
        if (valid) {
            region.setMaxHeight(height);
            region.setMinHeight(height);
        }
    }

    protected boolean isHeightValid(double height) {
        return height >= this.region.prefHeight(-1);
    }

    @Override
    protected void setCursor(Cursor cursor) {
        this.region.setCursor(cursor);
    }

    @Override
    protected double getX() {
        return this.region.getLayoutX();
    }

    @Override
    protected double getY() {
        return this.region.getLayoutY();
    }

    @Override
    protected void setX(double x) {
        this.region.setLayoutX(x);
    }

    @Override
    protected void setY(double y) {
        this.region.setLayoutY(y);
    }

    @Override
    protected double getMouseX(MouseEvent event) {
        return event.getSceneX();
    }

    @Override
    protected double getMouseY(MouseEvent event) {
        return event.getSceneY();
    }

    /**
    * It is important to remember that window zone is RESIZE_MARGIN outside and inside window. At the same handlers
    * will be called when mouse is over window shadow.
    */
    @Override
    protected double getMargin() {
        return 2;
    }

    protected Region getRegion() {
        return region;
    }
}
