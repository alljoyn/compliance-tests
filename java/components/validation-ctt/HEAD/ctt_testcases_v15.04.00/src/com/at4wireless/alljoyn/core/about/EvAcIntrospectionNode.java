/*
 * Copyright AllSeen Alliance. All rights reserved.
 *
 *    Permission to use, copy, modify, and/or distribute this software for any
 *    purpose with or without fee is hereby granted, provided that the above
 *    copyright notice and this permission notice appear in all copies.
 *
 *    THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 *    WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 *    MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 *    ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 *    WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 *    ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 *    OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

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
import com.at4wireless.alljoyn.core.commons.log.LoggerFactory;



// TODO: Auto-generated Javadoc
/**
 * The Class EvAcIntrospectionNode.
 */
public class EvAcIntrospectionNode {

    /**
     * The Class NoOpEntityResolver.
     */
    class NoOpEntityResolver implements EntityResolver {
            
            /* (non-Javadoc)
             * @see org.xml.sax.EntityResolver#resolveEntity(java.lang.String, java.lang.String)
             */
            public InputSource resolveEntity(String publicId, String systemId)
                                        throws SAXException, java.io.IOException {
                return new InputSource(new ByteArrayInputStream("".getBytes()));
            }

    } 

    //=============================================//

    /**
     * The Class IntrospectionParser.
     */
    class IntrospectionParser extends DefaultHandler{

        /** The xml reader. */
        private XMLReader xmlReader = null;
        
        /** The sax parser. */
        private SAXParser saxParser = null;

        /** The current node. */
        private EvAcIntrospectionNode currentNode = null;
        
        /** The saw root node. */
        private boolean sawRootNode = false;

        /**
         * Instantiates a new introspection parser.
         *
         * @throws IOException Signals that an I/O exception has occurred.
         * @throws ParserConfigurationException the parser configuration exception
         * @throws SAXException the SAX exception
         */
        public IntrospectionParser() throws IOException, ParserConfigurationException, SAXException {
            SAXParserFactory spf = SAXParserFactory.newInstance();
            spf.setNamespaceAware(false);
            saxParser = spf.newSAXParser();
            xmlReader = saxParser.getXMLReader();
            xmlReader.setContentHandler(this);
            xmlReader.setEntityResolver(new NoOpEntityResolver());
        }

        /**
         * Parses the.
         *
         * @param node the node
         * @param xml the xml
         * @throws SAXException the SAX exception
         */
        public void parse(EvAcIntrospectionNode node, String xml) throws SAXException {
            this.currentNode = node;
            sawRootNode = false;
            try{
                xmlReader.parse(new InputSource(new StringReader(xml)));
            }catch(IOException cantReallyHappen) {
                //Logger.error("Failed to read the XML: '" + cantReallyHappen.getMessage() + "'", cantReallyHappen);
            }
            this.currentNode = null;
        }

        /* (non-Javadoc)
         * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
         */
        public void startElement(String namespaceURI, String localName,
                        String qName, Attributes attrs) throws SAXException {
            if(qName.equals("node")) {
                if(!sawRootNode) {
                        sawRootNode = true;
                        return;
                }
                currentNode.addChild(getNameAttr(attrs));
            }else if(qName.equals("interface")){
                if(null == currentNode) throw new SAXException("interface not in node");
                currentNode.interfaces.add(getNameAttr(attrs));
            }

        }

        /**
         * Gets the name attr.
         *
         * @param attrs the attrs
         * @return the name attr
         * @throws SAXException the SAX exception
         */
        private String getNameAttr(Attributes attrs) throws SAXException {
            int i = attrs.getIndex("name");
            if(-1 == i) throw new SAXException("inner node without a name");
            return attrs.getValue(i);
        }
    }

    //================================================//
    //                END OF NESTED CLASSES           //
    //================================================//

    /** The Constant TAG. */
    private static final String TAG    = "EvAcIntrospectionNode";
    
    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger(TAG);

    /** The parsed. */
    private boolean parsed                       = false;
    
    /** The path. */
    private String path                          = null;
    
    /** The parser. */
    private IntrospectionParser parser           = null;

    /** The children. */
    private List<EvAcIntrospectionNode> children = new LinkedList<EvAcIntrospectionNode>();
    
    /** The interfaces. */
    private List<String> interfaces              = new LinkedList<String>();

    /**
     * Instantiates a new ev ac introspection node.
     *
     * @param path the path
     * @throws ParserConfigurationException the parser configuration exception
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws SAXException the SAX exception
     */
    public EvAcIntrospectionNode(String path) throws ParserConfigurationException, IOException, SAXException {
        this.path   = path;
        this.parser = new IntrospectionParser();
    }

    /**
     * Instantiates a new ev ac introspection node.
     *
     * @param path the path
     * @param parser the parser
     */
    private EvAcIntrospectionNode(String path, IntrospectionParser parser){
        this.path   = path;
        this.parser = parser;
    }

    /**
     * Adds the child.
     *
     * @param name the name
     */
    protected void addChild(String name) {
        StringBuilder sb = new StringBuilder(path);
        if(!name.endsWith("/")) sb.append('/');
        sb.append(name);
        children.add(new EvAcIntrospectionNode(sb.toString(), parser));
    }

    /**
     * Gets the path.
     *
     * @return the path
     */
    public String getPath() {
        return path;
    }

    /**
     * Checks if is parsed.
     *
     * @return true, if is parsed
     */
    public boolean isParsed() {
        return parsed;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(path);
        sb.append('\n');

        if(!parsed) {
            sb.append(" Not parsed\n");
            return sb.toString();
        }

        for(String ifc : interfaces) {
            sb.append(' ');
            sb.append(ifc);
            sb.append('\n');
        }

        for(EvAcIntrospectionNode node : children ) {
            sb.append(node.toString());
        }

        return sb.toString();
    }

    /**
     * Parse the given XML
     * @param xml
     * @throws SAXException
     */
    public void parse(String xml) throws SAXException {

        parser.parse(this, xml);
        parsed = true;
    }//parse

    /**
     * Gets the chidren.
     *
     * @return the chidren
     */
    public List<EvAcIntrospectionNode> getChidren() {
        return children;
    }

    /**
     * Gets the interfaces.
     *
     * @return the interfaces
     */
    public List<String> getInterfaces() {
        return interfaces;
    }

}
