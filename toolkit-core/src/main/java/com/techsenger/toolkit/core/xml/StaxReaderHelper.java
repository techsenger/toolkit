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
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

/**
 *
 * @author Pavel Castornii
 */
public class StaxReaderHelper {

    /**
     * XmlReader.
     */
    private final XMLEventReader xmlEventReader;

    /**
     * SubPaths.
     */
    private final Deque<String> subPaths = new LinkedList();

    /**
     * For performance we save current path.
     */
    private String currentPath;

    /**
     * Attributes.
     */
    private final Map<String, String> attributes = new HashMap<>();

    /**
     * Constructor.
     * @param xmlEventReader
     */
    public StaxReaderHelper(XMLEventReader xmlEventReader) {
        this.xmlEventReader = xmlEventReader;
    }

    /**
     * Adds subpath to path. This method must be executed at the beginning startElement.
     * @param subPath is the sub path that will be added to path.
     */
    public void enterSubPath(final String subPath) {
        subPaths.addLast(subPath);
        currentPath = null;
    }

    /**
     * Removes subpath from path. This method must be executed at the beginning endElement.
     * @param subPath  is the sub path that will be removed from path.
     */
    public void exitSubPath(final String subPath) {
        subPaths.pollLast();
        currentPath = null;
    }

    /**
     * Returns current path with "/" as divider.
     * @return current path.
     */
    public String getCurrentPath() {
        if (currentPath != null) {
            return currentPath;
        }
        StringBuilder builder = new StringBuilder();
        String divider = "";
        for (String subPath : subPaths) {
            builder.append(divider + subPath);
            divider = "/";
        }
        currentPath = builder.toString();
        return currentPath;
    }

    /**
     * Reads attrivbutes. After calling this method use getAttribute method.
     * @param startElement from which attributes will be read.
     */
    public void readAttributes(final StartElement startElement) {
        attributes.clear();
        Iterator attributeIterator = startElement.getAttributes();
        while (attributeIterator.hasNext()) {
            Attribute attribute = (Attribute) attributeIterator.next();
            attributes.put(attribute.getName().getLocalPart(), attribute.getValue());
        }
    }

    /**
     * Returns attribute.
     * @param name of the attribute.
     * @return attribute value.
     */
    public String getAttribute(final String name) {
        return attributes.get(name);
    }

    /**
     * Returns the value of the xml node.
     * @return
     * @throws XMLStreamException
     */
    public String getValue() throws XMLStreamException {
        XMLEvent xmlEvent = xmlEventReader.nextEvent();
        return xmlEvent.asCharacters().getData();
    }
}
