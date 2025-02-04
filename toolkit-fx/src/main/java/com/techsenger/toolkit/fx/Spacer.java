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

import javafx.geometry.Orientation;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * This is node for creating stretching of fixed space.
 *
 * @author Pavel Castornii
 */
public class Spacer extends Region {

    public Spacer() {
        this(Orientation.HORIZONTAL);
    }

    public Spacer(Orientation orientation) {
        switch (orientation) {
            case HORIZONTAL:
                HBox.setHgrow(this, Priority.ALWAYS);
                break;
            case VERTICAL:
                VBox.setVgrow(this, Priority.ALWAYS);
                break;
            default:
                throw new AssertionError();
        }
    }

    public Spacer(Double size) {
        this(size, Orientation.HORIZONTAL);
    }

    public Spacer(Double size, Orientation orientation) {
        switch (orientation) {
            case HORIZONTAL:
                setMaxWidth(size);
                setMinWidth(size);
                setPrefWidth(size);
                break;
            case VERTICAL:
                setMaxHeight(size);
                setMinHeight(size);
                setPrefHeight(size);
                break;
            default:
                throw new AssertionError();
        }
    }
}
