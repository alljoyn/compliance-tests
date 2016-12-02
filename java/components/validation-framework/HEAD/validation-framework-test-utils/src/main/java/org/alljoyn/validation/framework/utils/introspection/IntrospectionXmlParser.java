/*******************************************************************************
 *   *  
 *     Copyright (c) 2016 Open Connectivity Foundation and AllJoyn Open
 *     Source Project Contributors and others.
 *     
 *     All rights reserved. This program and the accompanying materials are
 *     made available under the terms of the Apache License, Version 2.0
 *     which accompanies this distribution, and is available at
 *     http://www.apache.org/licenses/LICENSE-2.0

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