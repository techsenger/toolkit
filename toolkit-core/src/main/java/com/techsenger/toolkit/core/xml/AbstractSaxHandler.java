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

package com.techsenger.toolkit.core.xml;

import java.util.Deque;
import java.util.LinkedList;
import org.xml.sax.helpers.DefaultHandler;

/**
 * This classes keeps builder length that allows not to use loops.
 *
 * @author Pavel Castornii
 */
public abstract class AbstractSaxHandler extends DefaultHandler {

    private final StringBuilder pathStringBuilder = new StringBuilder();

    private final Deque<Integer> savedBuilderLengths = new LinkedList<>();

    /**
     * Adds subpath to path. This method must be executed in startElement method.
     * @param subPath is the sub path that will be added to path.
     */
    protected void enterSubPath(final String subPath) {
        savedBuilderLengths.addLast(pathStringBuilder.length());
        if (savedBuilderLengths.size() > 1) {
            pathStringBuilder.append("/");
        }
        pathStringBuilder.append(subPath);
    }

    /**
     * Removes subpath from path. This method must be executed in endElement method.
     * @param subPath  is the sub path that will be removed from path.
     */
    protected void exitSubPath(final String subPath) {
        var savedLenth = savedBuilderLengths.pollLast();
        pathStringBuilder.setLength(savedLenth);
    }

    /**
     * Returns current path.
     * @return current path.
     */
    protected String getCurrentPath() {
        return pathStringBuilder.toString();
    }

    /**
     * Returns level/deep of the path. For example for path books/path will return 2.
     * @return
     */
    protected int getPathLevel() {
        return this.savedBuilderLengths.size();
    }
}
