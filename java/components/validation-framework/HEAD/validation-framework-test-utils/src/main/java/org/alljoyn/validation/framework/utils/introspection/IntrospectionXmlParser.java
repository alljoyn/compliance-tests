/*******************************************************************************
 *  Copyright (c) Open Connectivity Foundation (OCF) and AllJoyn Open
 *     Source Project (AJOSP) Contributors and others.
 *
 *     SPDX-License-Identifier: Apache-2.0
 *
 *     All rights reserved. This program and the accompanying materials are
 *     made available under the terms of the Apache License, Version 2.0
 *     which accompanies this distribution, and is available at
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Copyright (c) Open Connectivity Foundation and Contributors to AllSeen
 *     Alliance. All rights reserved.
 *
 *     Permission to use, copy, modify, and/or distribute this software for
 *     any purpose with or without fee is hereby granted, provided that the
 *     above copyright notice and this permission notice appear in all
 *     copies.
 *
 *      THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL
 *      WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED
 *      WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE
 *      AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL
 *      DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR
 *      PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER
 *      TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 *      PERFORMANCE OF THIS SOFTWARE.
 *******************************************************************************/
package org.alljoyn.validation.framework.utils.introspection;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.alljoyn.validation.framework.utils.introspection.bean.IntrospectionNode;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public class IntrospectionXmlParser
{
    public IntrospectionNode parseXML(InputStream inputStream) throws IOException, ParserConfigurationException, SAXException
    {
        IntrospectionXmlHandler introspectionXmlHandler = new IntrospectionXmlHandler();
        parse(inputStream, introspectionXmlHandler);
        return introspectionXmlHandler.getIntrospectionNode();
    }

    private void parse(InputStream inputStream, IntrospectionXmlHandler testMetadataXmlHandler) throws IOException, SAXException, ParserConfigurationException
    {
        XMLReader xmlReader = null;
        SAXParserFactory saxParserFactory = getSAXParserFactory();
        saxParserFactory.setNamespaceAware(true);

        SAXParser parser = saxParserFactory.newSAXParser();
        xmlReader = parser.getXMLReader();
        xmlReader.setContentHandler(testMetadataXmlHandler);
        xmlReader.parse(new InputSource(inputStream));
    }

    protected SAXParserFactory getSAXParserFactory()
    {
        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        return saxParserFactory;
    }

}