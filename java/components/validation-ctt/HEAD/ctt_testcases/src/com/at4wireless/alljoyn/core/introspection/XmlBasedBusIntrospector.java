/*
 *    Copyright (c) Open Connectivity Foundation (OCF) and AllJoyn Open
 *    Source Project (AJOSP) Contributors and others.
 *
 *    SPDX-License-Identifier: Apache-2.0
 *
 *    All rights reserved. This program and the accompanying materials are
 *    made available under the terms of the Apache License, Version 2.0
 *    which accompanies this distribution, and is available at
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Copyright (c) Open Connectivity Foundation and Contributors to AllSeen
 *    Alliance. All rights reserved.
 *
 *    Permission to use, copy, modify, and/or distribute this software for
 *    any purpose with or without fee is hereby granted, provided that the
 *    above copyright notice and this permission notice appear in all
 *    copies.
 *
 *    THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL
 *    WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED
 *    WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE
 *    AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL
 *    DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR
 *    PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER
 *    TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 *    PERFORMANCE OF THIS SOFTWARE.
 */
package com.at4wireless.alljoyn.core.introspection;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.alljoyn.bus.BusAttachment;
import org.alljoyn.bus.BusException;
import org.alljoyn.bus.ProxyBusObject;
import org.alljoyn.bus.ifaces.Introspectable;
import org.xml.sax.SAXException;

import com.at4wireless.alljoyn.core.introspection.bean.InterfaceDetail;
import com.at4wireless.alljoyn.core.introspection.bean.IntrospectionInterface;
import com.at4wireless.alljoyn.core.introspection.bean.IntrospectionNode;
import com.at4wireless.alljoyn.core.introspection.bean.IntrospectionSubNode;
import com.at4wireless.alljoyn.core.introspection.bean.NodeDetail;


// TODO: Auto-generated Javadoc
/**
 * The Class XmlBasedBusIntrospector.
 */
public class XmlBasedBusIntrospector implements BusIntrospector
{
    
    /** The Constant SLASH_CHARACTER. */
    private static final String SLASH_CHARACTER = "/";
    
    /** The Constant ROOT_PATH. */
    private static final String ROOT_PATH = "/";
    
    /** The Constant ALL_STANDARDIZED_INTERFACES. */
    private static final String ALL_STANDARDIZED_INTERFACES = "ALL";
    
    /** The Constant STANDARDIZED_INTERFACE_NAME_PREFIX. */
    private static final String STANDARDIZED_INTERFACE_NAME_PREFIX = "org.alljoyn";
    
    /** The introspection xml parser. */
    private IntrospectionXmlParser introspectionXmlParser = new IntrospectionXmlParser();
    
    /** The bus attachment. */
    private BusAttachment busAttachment;
    
    /** The peer name. */
    private String peerName;
    
    /** The session id. */
    private int sessionId;

    /**
     * Instantiates a new xml based bus introspector.
     *
     * @param busAttachment the bus attachment
     * @param peerName the peer name
     * @param sessionId the session id
     */
    public XmlBasedBusIntrospector(BusAttachment busAttachment, String peerName, int sessionId)
    {
        this.busAttachment = busAttachment;
        this.peerName = peerName;
        this.sessionId = sessionId;
    }

    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.introspection.BusIntrospector#getInterface(java.lang.String, java.lang.Class)
     */
    @Override
    public <T> T getInterface(String path, Class<T> interfaceClass)
    {
        ProxyBusObject proxyBusObject = busAttachment.getProxyBusObject(peerName, path, sessionId, getClasses(interfaceClass));

        return proxyBusObject.getInterface(interfaceClass);
    }

    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.introspection.BusIntrospector#introspect(java.lang.String)
     */
    public NodeDetail introspect(String path) throws BusException, IOException, ParserConfigurationException, SAXException
    {
        ProxyBusObject proxyBusObject = busAttachment.getProxyBusObject(peerName, path, sessionId, getClasses(Introspectable.class));
        Introspectable introspectableInterface = proxyBusObject.getInterface(Introspectable.class);
        String introspectionXml = introspectableInterface.Introspect();
        IntrospectionNode introspectionNode = getIntrospectionXmlParser().parseXML(getInputStream(introspectionXml));

        return new NodeDetail(path, introspectionNode);
    }

    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.introspection.BusIntrospector#introspectEntireTree(java.lang.String)
     */
    public List<NodeDetail> introspectEntireTree(String path) throws BusException, IOException, ParserConfigurationException, SAXException
    {
        NodeDetail nodeDetail = introspect(path);
        List<NodeDetail> nodeDetailList = new ArrayList<NodeDetail>();
        nodeDetailList.add(nodeDetail);
        nodeDetailList.addAll(introspectUsingNodeDetail(nodeDetail));

        return nodeDetailList;
    }

    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.introspection.BusIntrospector#getStandardizedInterfacesExposedOnBus()
     */
    public List<InterfaceDetail> getStandardizedInterfacesExposedOnBus() throws BusException, IOException, ParserConfigurationException, SAXException
    {
        List<NodeDetail> nodeDetailList = introspectEntireTree(ROOT_PATH);

        return determineIntrospectionInterfaces(nodeDetailList, ALL_STANDARDIZED_INTERFACES);
    }

    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.introspection.BusIntrospector#getInterfacesExposedOnBusBasedOnName(java.lang.String)
     */
    public List<InterfaceDetail> getInterfacesExposedOnBusBasedOnName(String interfaceName) throws BusException, IOException, ParserConfigurationException, SAXException
    {
        List<NodeDetail> nodeDetailList = introspectEntireTree(ROOT_PATH);

        return determineIntrospectionInterfaces(nodeDetailList, interfaceName);
    }

    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.introspection.BusIntrospector#getInterfacesExposedOnBusUnderSpecifiedPathBasedOnName(java.lang.String, java.lang.String)
     */
    public List<InterfaceDetail> getInterfacesExposedOnBusUnderSpecifiedPathBasedOnName(String path, String interfaceName) throws BusException, IOException,
            ParserConfigurationException, SAXException
    {
        List<NodeDetail> nodeDetailList = introspectEntireTree(path);

        return determineIntrospectionInterfaces(nodeDetailList, interfaceName);
    }

    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.introspection.BusIntrospector#isInterfacePresent(java.lang.String, java.lang.String)
     */
    public boolean isInterfacePresent(String path, String interfaceName) throws BusException, IOException, ParserConfigurationException, SAXException
    {
        NodeDetail nodeDetail = introspect(path);
        List<IntrospectionInterface> introspectionInterfaces = nodeDetail.getIntrospectionNode().getInterfaces();

        for (IntrospectionInterface introspectionInterface : introspectionInterfaces)
        {
            if (introspectionInterface.getName().equals(interfaceName))
            {
                return true;
            }
        }

        return false;
    }

    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.introspection.BusIntrospector#isAncestorInterfacePresent(java.lang.String, java.lang.String)
     */
    public boolean isAncestorInterfacePresent(String path, String ancestorInterfaceName) throws BusException, IOException, ParserConfigurationException, SAXException
    {
        if (path.lastIndexOf("/") != 0)
        {
            String parentInterfacePath = path.substring(0, path.lastIndexOf("/"));

            if (isInterfacePresent(parentInterfacePath, ancestorInterfaceName))
            {
                return true;
            }
            else
            {
                return isAncestorInterfacePresent(parentInterfacePath, ancestorInterfaceName);
            }
        }

        return false;
    }

    /**
     * Gets the introspection xml parser.
     *
     * @return the introspection xml parser
     */
    IntrospectionXmlParser getIntrospectionXmlParser()
    {
        return introspectionXmlParser;
    }

    /**
     * Gets the classes.
     *
     * @param interfaceClass the interface class
     * @return the classes
     */
    Class<?>[] getClasses(Class<?> interfaceClass)
    {
        return new Class<?>[]
        { interfaceClass };
    }

    /**
     * Gets the input stream.
     *
     * @param introspectionXml the introspection xml
     * @return the input stream
     */
    ByteArrayInputStream getInputStream(String introspectionXml)
    {
        return new ByteArrayInputStream(introspectionXml.getBytes());
    }

    /**
     * Introspect using node detail.
     *
     * @param nodeDetail the node detail
     * @return the list
     * @throws BusException the bus exception
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws ParserConfigurationException the parser configuration exception
     * @throws SAXException the SAX exception
     */
    private List<NodeDetail> introspectUsingNodeDetail(NodeDetail nodeDetail) throws BusException, IOException, ParserConfigurationException, SAXException
    {
        List<IntrospectionSubNode> introspectionSubNodes = nodeDetail.getIntrospectionNode().getSubNodes();
        List<NodeDetail> nodeDetailList = new ArrayList<NodeDetail>();

        if (introspectionSubNodes != null)
        {
            for (IntrospectionSubNode introspectionSubNode : introspectionSubNodes)
            {
                String path = determinePath(nodeDetail.getPath(), introspectionSubNode);
                nodeDetailList.addAll(introspectEntireTree(path));
            }
        }

        return nodeDetailList;
    }

    /**
     * Determine path.
     *
     * @param pathPrefix the path prefix
     * @param introspectionSubNode the introspection sub node
     * @return the string
     */
    private String determinePath(String pathPrefix, IntrospectionSubNode introspectionSubNode)
    {
        if (pathPrefix.endsWith(SLASH_CHARACTER))
        {
            return pathPrefix + introspectionSubNode.getName();
        }
        else
        {
            return pathPrefix + SLASH_CHARACTER + introspectionSubNode.getName();
        }
    }

    /**
     * Determine introspection interfaces.
     *
     * @param nodeDetailList the node detail list
     * @param interfaceName the interface name
     * @return the list
     */
    private List<InterfaceDetail> determineIntrospectionInterfaces(List<NodeDetail> nodeDetailList, String interfaceName)
    {
        List<InterfaceDetail> interfaceDetailList = new ArrayList<InterfaceDetail>();

        for (NodeDetail nodeDetail : nodeDetailList)
        {
            IntrospectionNode introspectionNode = nodeDetail.getIntrospectionNode();
            List<IntrospectionInterface> filteredIntrospectionInterfaces = filterIntrospectionInterfaces(introspectionNode.getInterfaces(), interfaceName);

            if (!filteredIntrospectionInterfaces.isEmpty())
            {
                interfaceDetailList.add(new InterfaceDetail(nodeDetail.getPath(), filteredIntrospectionInterfaces));
            }
        }

        return interfaceDetailList;
    }

    /**
     * Filter introspection interfaces.
     *
     * @param interfaces the interfaces
     * @param interfaceName the interface name
     * @return the list
     */
    private List<IntrospectionInterface> filterIntrospectionInterfaces(List<IntrospectionInterface> interfaces, String interfaceName)
    {
        if (interfaceName.equals(ALL_STANDARDIZED_INTERFACES))
        {
            return filterStandardizedInterfaces(interfaces);
        }
        else
        {
            return filterInterfacesBasedOnName(interfaces, interfaceName);
        }
    }

    /**
     * Filter interfaces based on name.
     *
     * @param introspectionInterfaces the introspection interfaces
     * @param interfaceName the interface name
     * @return the list
     */
    private List<IntrospectionInterface> filterInterfacesBasedOnName(List<IntrospectionInterface> introspectionInterfaces, String interfaceName)
    {
        List<IntrospectionInterface> filteredInterfaces = new ArrayList<IntrospectionInterface>();

        for (IntrospectionInterface introspectionInterface : introspectionInterfaces)
        {
            if (introspectionInterface.getName().equals(interfaceName))
            {
                filteredInterfaces.add(introspectionInterface);
            }
        }

        return filteredInterfaces;
    }

    /**
     * Filter standardized interfaces.
     *
     * @param introspectionInterfaces the introspection interfaces
     * @return the list
     */
    private List<IntrospectionInterface> filterStandardizedInterfaces(List<IntrospectionInterface> introspectionInterfaces)
    {
        List<IntrospectionInterface> standardizedInterfaces = new ArrayList<IntrospectionInterface>();

        for (IntrospectionInterface introspectionInterface : introspectionInterfaces)
        {
            if (introspectionInterface.getName().startsWith(STANDARDIZED_INTERFACE_NAME_PREFIX))
            {
                standardizedInterfaces.add(introspectionInterface);
            }
        }

        return standardizedInterfaces;
    }
}