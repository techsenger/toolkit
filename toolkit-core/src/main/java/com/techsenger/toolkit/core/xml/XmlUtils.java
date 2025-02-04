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

import java.io.StringReader;
import java.io.StringWriter;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Pavel Castornii
 */
public final class XmlUtils {

    private static final Logger logger = LoggerFactory.getLogger(XmlUtils.class);

    /**
     * Transforms a xml string to tree to make it more readable using Transformer.
     *
     * @param xml
     * @param headerComment that is added before root element. For example, `Don't modify ...`.
     * @return
     */
    public static String transformToTree(String xml, String headerComment) {
        try (StringReader reader = new StringReader(xml)) {
            StreamResult result = new StreamResult(new StringWriter());
            result.getWriter().write("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>"
                    + System.getProperty("line.separator"));
            if (headerComment != null) {
                result.getWriter().write(headerComment + System.getProperty("line.separator"));
            }

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

            StreamSource source = new StreamSource(reader);
            transformer.transform(source, result);

            String xmlTree = result.getWriter().toString();
            return xmlTree;
        } catch (Exception ex) {
            logger.error("Error transforming XML", ex);
            return null;
        }
    }

//    /**
//     * Transforms a xml string to tree to make it more readable using LSSerializer.
//     * @param xml
//     * @return
//     */
//    public static String transformToTree(String xml) {
//        try {
//            final InputSource src = new InputSource(new StringReader(xml));
//            //we create dom from string
//            final Node document =
//                    DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(src).getDocumentElement();
//            //May need this: System.setProperty(DOMImplementationRegistry.PROPERTY,
//            //"com.sun.org.apache.xerces.internal.dom.DOMImplementationSourceImpl");
//            final DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();
//            final DOMImplementationLS impl = (DOMImplementationLS) registry.getDOMImplementation("LS");
//            final LSSerializer writer = impl.createLSSerializer();
//            writer.setNewLine(System.getProperty("line.separator"));
//            writer.getDomConfig().setParameter("format-pretty-print", Boolean.TRUE);
//            writer.getDomConfig().setParameter("xml-declaration", Boolean.TRUE);
//            //we serialize dom
//            return writer.writeToString(document);
//        } catch (Exception ex) {
//            logger.error("Error transforming xml", ex);
//            return null;
//        }
//    }

    /**
     * Hidden constructor.
     */
    private XmlUtils() {
        //does nothing
    }
}
