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
package com.at4wireless.alljoyn.core.about;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.at4wireless.alljoyn.core.commons.log.Logger;
import com.at4wireless.alljoyn.core.commons.log.WindowsLoggerImpl;

public class EvAcIntrospectionNode
{
    class NoOpEntityResolver implements EntityResolver
    {
        public InputSource resolveEntity(String publicId, String systemId) throws SAXException, java.io.IOException
        {
            return new InputSource(new ByteArrayInputStream("".getBytes()));
        }
    } 

    //=============================================//

    class IntrospectionParser extends DefaultHandler
    {
        private XMLReader xmlReader = null;
        private SAXParser saxParser = null;

        private EvAcIntrospectionNode currentNode = null;
        private boolean sawRootNode = false;

        public IntrospectionParser() throws IOException, ParserConfigurationException, SAXException
        {
            SAXParserFactory spf = SAXParserFactory.newInstance();
            spf.setNamespaceAware(false);
            saxParser = spf.newSAXParser();
            xmlReader = saxParser.getXMLReader();
            xmlReader.setContentHandler(this);
            xmlReader.setEntityResolver(new NoOpEntityResolver());
        }

        public void parse(EvAcIntrospectionNode node, String xml) throws SAXException
        {
            this.currentNode = node;
            sawRootNode = false;
            try
            {
                xmlReader.parse(new InputSource(new StringReader(xml)));
            }
            catch (IOException cantReallyHappen)
            {
                logger.error("Failed to read the XML: '" + cantReallyHappen.getMessage() + "'", cantReallyHappen);
            }
            this.currentNode = null;
        }

        public void startElement(String namespaceURI, String localName, String qName, Attributes attrs) throws SAXException
        {
            if (qName.equals("node"))
            {
                if (!sawRootNode)
                {
                    sawRootNode = true;
                    return;
                }
                currentNode.addChild(getNameAttr(attrs));
            }
            else if (qName.equals("interface"))
            {
                if (null == currentNode) 
                	throw new SAXException("interface not in node");
                currentNode.interfaces.add(getNameAttr(attrs));
            }

        }

        private String getNameAttr(Attributes attrs) throws SAXException
        {
            int i = attrs.getIndex("name");
            if (-1 == i) 
            	throw new SAXException("inner node without a name");
            return attrs.getValue(i);
        }
    }

    // ================================================//
    // END OF NESTED CLASSES //
    // ================================================//

    private static final Logger logger = new WindowsLoggerImpl(EvAcIntrospectionNode.class.getSimpleName());

    private boolean parsed = false;
    private String path = null;
    private IntrospectionParser parser = null;

    private List<EvAcIntrospectionNode> children = new LinkedList<EvAcIntrospectionNode>();
    private List<String> interfaces = new LinkedList<String>();

    public EvAcIntrospectionNode(String path) throws ParserConfigurationException, IOException, SAXException
    {
        this.path = path;
        this.parser = new IntrospectionParser();
    }

    private EvAcIntrospectionNode(String path, IntrospectionParser parser)
    {
        this.path = path;
        this.parser = parser;
    }

    protected void addChild(String name)
    {
        StringBuilder sb = new StringBuilder(path);
        if (!name.endsWith("/")) 
        	sb.append('/');
        sb.append(name);
        children.add(new EvAcIntrospectionNode(sb.toString(), parser));
    }

    public String getPath()
    {
        return path;
    }

    public boolean isParsed()
    {
        return parsed;
    }

    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(path);
        sb.append('\n');

        if (!parsed)
        {
            sb.append(" Not parsed\n");
            return sb.toString();
        }

        for (String ifc : interfaces)
        {
            sb.append(' ');
            sb.append(ifc);
            sb.append('\n');
        }

        for (EvAcIntrospectionNode node : children )
        {
            sb.append(node.toString());
        }

        return sb.toString();
    }

    /**
     * Parse the given XML
     * 
     * @param xml
     * @throws SAXException
     */
    public void parse(String xml) throws SAXException
    {
        parser.parse(this, xml);
        parsed = true;
    }// parse

    public List<EvAcIntrospectionNode> getChidren()
    {
        return children;
    }

    public List<String> getInterfaces()
    {
        return interfaces;
    }
}