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

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 * By default stax returns xml as one string without indents. There is a solution to use transformer,
 * however, this solution is not good for performance and besides it doesn't create end of line at the first line.
 *
 * @author Pavel Castornii
 */
public class StaxWriterHelper {

    /**
     * Writer. We need this writer to make indents without using transform method.
     */
    private final XMLStreamWriter writer;

    /**
     * Depth.
     */
    private int depth;

    /**
     * Constructor.
     * @param writer that will be used for creating indents.
     */
    public StaxWriterHelper(final XMLStreamWriter writer) {
        this.writer = writer;
    }

    /**
     * Writes start document.
     * @param encoding of the document.
     * @param version version of the document.
     * @throws XMLStreamException if there is a xml stream problem.
     */
    public void writeStartDocument(final String encoding, final String version) throws XMLStreamException {
        writer.writeStartDocument(encoding, version);
    }

    /**
     * Writes start element.
     * @param localName of the element.
     * @throws XMLStreamException if there is a xml stream problem.
     */
    public void writeStartElement(final String localName) throws XMLStreamException {
        writeIndent();
        writer.writeStartElement(localName);
        depth++;
    }

    /**
     * Writes empty element {@code <.. /.>}. Empty element has no children inside.
     * @param localName of the empty element.
     * @throws XMLStreamException if there is a xml stream problem.
     */
    public void writeEmptyElement(final String localName) throws XMLStreamException {
        writeIndent();
        writer.writeEmptyElement(localName);
    }

    /**
     * Writes attribute.
     * @param localName of the attribute.
     * @param value of the attribute.
     * @throws XMLStreamException if there is a xml stream problem.
     */
    public void writeAttribute(final String localName, final String value) throws XMLStreamException {
        writer.writeAttribute(localName, value);
    }

    /**
     * Writes end element.
     * @throws XMLStreamException if there is a xml stream problem.
     */
    public void writeEndElement() throws XMLStreamException {
        depth--;
        writeIndent();
        writer.writeEndElement();
    }

    /**
     * Writes end document.
     * @throws XMLStreamException if there is a xml stream problem.
     */
    public void writeEndDocument() throws XMLStreamException {
        writer.writeEndDocument();
    }

    /**
     * Flush.
     * @throws XMLStreamException if there is a xml stream problem.
     */
    public void flush() throws XMLStreamException {
        writer.flush();
    }

    /**
     * Close.
     * @throws XMLStreamException if there is a xml stream problem.
     */
    public void close() throws XMLStreamException {
        writer.close();
    }

    /**
     * Writes indent.
     * @throws XMLStreamException if there is a xml stream problem.
     */
    private void writeIndent() throws XMLStreamException {
        writer.writeCharacters(System.getProperty("line.separator"));
        for (int i = 0; i < depth; i++) {
            writer.writeCharacters("    ");
        }
    }
}
