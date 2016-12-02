/*
 *  * 
 *    Copyright (c) 2016 Open Connectivity Foundation and AllJoyn Open
 *    Source Project Contributors and others.
 *    
 *    All rights reserved. This program and the accompanying materials are
 *    made available under the terms of the Apache License, Version 2.0
 *    which accompanies this distribution, and is available at
 *    http://www.apache.org/licenses/LICENSE-2.0

 */
package com.at4wireless.alljoyn.core.introspection;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;





import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.at4wireless.alljoyn.core.introspection.bean.IntrospectionNode;


// TODO: Auto-generated Javadoc
/**
 * The Class IntrospectionXmlParser.
 */
public class IntrospectionXmlParser
{
    
    /**
     * Parses the xml.
     *
     * @param inputStream the input stream
     * @return the introspection node
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws ParserConfigurationException the parser configuration exception
     * @throws SAXException the SAX exception
     */
    public IntrospectionNode parseXML(InputStream inputStream) throws IOException, ParserConfigurationException, SAXException
    {
        IntrospectionXmlHandler introspectionXmlHandler = new IntrospectionXmlHandler();
        parse(inputStream, introspectionXmlHandler);
        return introspectionXmlHandler.getIntrospectionNode();
    }

    /**
     * Parses the.
     *
     * @param inputStream the input stream
     * @param testMetadataXmlHandler the test metadata xml handler
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws SAXException the SAX exception
     * @throws ParserConfigurationException the parser configuration exception
     */
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

    /**
     * Gets the SAX parser factory.
     *
     * @return the SAX parser factory
     */
    protected SAXParserFactory getSAXParserFactory()
    {
        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        return saxParserFactory;
    }

}