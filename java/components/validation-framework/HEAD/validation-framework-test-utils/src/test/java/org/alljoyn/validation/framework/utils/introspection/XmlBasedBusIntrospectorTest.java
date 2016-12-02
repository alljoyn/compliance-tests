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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.alljoyn.bus.BusAttachment;
import org.alljoyn.bus.BusException;
import org.alljoyn.bus.ProxyBusObject;
import org.alljoyn.bus.ifaces.Introspectable;
import org.alljoyn.validation.framework.utils.introspection.IntrospectionXmlParser;
import org.alljoyn.validation.framework.utils.introspection.XmlBasedBusIntrospector;
import org.alljoyn.validation.framework.utils.introspection.bean.InterfaceDetail;
import org.alljoyn.validation.framework.utils.introspection.bean.IntrospectionInterface;
import org.alljoyn.validation.framework.utils.introspection.bean.IntrospectionNode;
import org.alljoyn.validation.framework.utils.introspection.bean.IntrospectionSubNode;
import org.alljoyn.validation.framework.utils.introspection.bean.NodeDetail;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.xml.sax.SAXException;

public class XmlBasedBusIntrospectorTest
{
    private static final String STANDARDIZED_INTERFACE_NAME = "org.alljoyn.About";
    private static final String ROOT_PATH = "/";
    private static final String SUB_NODE_NAME = "About";
    private static final String SUB_NODE_PATH = ROOT_PATH + SUB_NODE_NAME;
    private static final String SUB_NODE_CHILD_NAME = "Device";
    private static final String SUB_NODE_CHILD_PATH = SUB_NODE_PATH + "/" + SUB_NODE_CHILD_NAME;
    private static final String SUB_NODE_GRANDCHILD_NAME = "Model";
    private static final String SUB_NODE_GRANDCHILD_PATH = SUB_NODE_CHILD_PATH + "/" + SUB_NODE_GRANDCHILD_NAME;
    private static final String ROOT_INTROSPECTION_XML = "root introspection xml";
    private static final String SUB_NODE_INTROSPECTION_XML = "sub node introspection xml";
    private static final String SUB_NODE_CHILD_INTROSPECTION_XML = "sub node child introspection xml";
    private static final String SUB_NODE_GRANDCHILD_INTROSPECTION_XML = "sub node grand child introspection xml";
    private static final String PEER_NAME = "peer";
    private static final int SESSION_ID = 1;
    @Mock
    private IntrospectionXmlParser introspectionXmlParser;
    @Mock
    private BusAttachment busAttachment;
    @Mock
    private ProxyBusObject rootProxyBusObject;
    @Mock
    private ProxyBusObject subNodeProxyBusObject;
    @Mock
    private ProxyBusObject subNodeChildProxyBusObject;
    @Mock
    private ProxyBusObject subNodeGrandChildProxyBusObject;
    @Mock
    private Introspectable rootIntrospectable;
    @Mock
    private IntrospectionNode rootIntrospectionNode;
    @Mock
    private IntrospectionSubNode introspectionSubNode;
    @Mock
    private ByteArrayInputStream rootInputStream;
    @Mock
    private Introspectable subNodeIntrospectable;
    @Mock
    private ByteArrayInputStream subNodeInputStream;
    @Mock
    private IntrospectionNode subNodeIntrospectionNode;
    @Mock
    private IntrospectionSubNode introspectionSubNodeChild;
    @Mock
    private Introspectable subNodeChildIntrospectable;
    @Mock
    private ByteArrayInputStream subNodeChildInputStream;
    @Mock
    private IntrospectionNode subNodeChildIntrospectionNode;
    @Mock
    private IntrospectionSubNode introspectionSubNodeGrandChild;
    @Mock
    private Introspectable subNodeGrandChildIntrospectable;
    @Mock
    private ByteArrayInputStream subNodeGrandChildInputStream;
    @Mock
    private IntrospectionNode subNodeGrandChildIntrospectionNode;

    private XmlBasedBusIntrospector xmlBasedBusIntrospector;
    private List<IntrospectionSubNode> introspectionSubNodes;
    private Class<?>[] classes;
    private List<IntrospectionSubNode> introspectionSubNodeChildren;
    private List<IntrospectionSubNode> introspectionSubNodeGrandChildren;

    @Before
    public void setup() throws Exception
    {
        initMocks(this);

        classes = new Class<?>[]
        { Introspectable.class };

        setupRootIntrospectionNode();
        setupSubNodeIntrospectionNode();
        setupSubNodeChildIntrospectionNode();
        setupSubNodeGrandChildIntrospectionNode();

        constructBusIntrospector();
    }

    @Test
    public void getIntrospectionXmlParser()
    {
        xmlBasedBusIntrospector = new XmlBasedBusIntrospector(busAttachment, PEER_NAME, SESSION_ID);
        IntrospectionXmlParser introspectionXmlParserReturned = xmlBasedBusIntrospector.getIntrospectionXmlParser();
        assertNotNull(introspectionXmlParserReturned);
    }

    @Test
    public void getClasses()
    {
        xmlBasedBusIntrospector = new XmlBasedBusIntrospector(busAttachment, PEER_NAME, SESSION_ID);
        Class<?>[] classesReturned = xmlBasedBusIntrospector.getClasses(Introspectable.class);
        assertEquals(1, classes.length);
        assertEquals(Introspectable.class, classes[0]);
        assertTrue(classesReturned != xmlBasedBusIntrospector.getClasses(Introspectable.class));
    }

    @Test
    public void getInputstream() throws IOException
    {
        xmlBasedBusIntrospector = new XmlBasedBusIntrospector(busAttachment, PEER_NAME, SESSION_ID);
        ByteArrayInputStream inputStream = xmlBasedBusIntrospector.getInputStream("xml");
        assertEquals("xml", convertToString(inputStream));
    }

    @Test
    public void getInterface()
    {
        assertEquals(rootIntrospectable, xmlBasedBusIntrospector.getInterface(ROOT_PATH, Introspectable.class));
    }

    @Test
    public void introspect() throws Exception
    {
        NodeDetail nodeDetail = xmlBasedBusIntrospector.introspect(ROOT_PATH);
        assertEquals(rootIntrospectionNode, nodeDetail.getIntrospectionNode());
        assertEquals(ROOT_PATH, nodeDetail.getPath());
    }

    @Test
    public void introspectEntireTree() throws Exception
    {
        List<NodeDetail> nodeDetailList = xmlBasedBusIntrospector.introspectEntireTree(ROOT_PATH);

        assertEquals(rootIntrospectionNode, nodeDetailList.get(0).getIntrospectionNode());
        assertEquals(ROOT_PATH, nodeDetailList.get(0).getPath());
        assertEquals(subNodeIntrospectionNode, nodeDetailList.get(1).getIntrospectionNode());
        assertEquals(SUB_NODE_PATH, nodeDetailList.get(1).getPath());
        assertEquals(subNodeChildIntrospectionNode, nodeDetailList.get(2).getIntrospectionNode());
        assertEquals(SUB_NODE_CHILD_PATH, nodeDetailList.get(2).getPath());
    }

    @Test
    public void emptyMapReturnedIfNoStandardizedInterfaceFound() throws Exception
    {
        List<InterfaceDetail> interfaceDetailList = xmlBasedBusIntrospector.getStandardizedInterfacesExposedOnBus();
        assertTrue(interfaceDetailList.isEmpty());
    }

    @Test
    public void getStandardizedIntrospectionInterfacesExposedOnBus() throws Exception
    {
        IntrospectionInterface nonStandardizedInterface = Mockito.mock(IntrospectionInterface.class);
        when(nonStandardizedInterface.getName()).thenReturn("com");
        IntrospectionInterface standardizedInterface = Mockito.mock(IntrospectionInterface.class);
        when(standardizedInterface.getName()).thenReturn(STANDARDIZED_INTERFACE_NAME);
        List<IntrospectionInterface> introspectionInterfaces = new ArrayList<IntrospectionInterface>();
        introspectionInterfaces.add(nonStandardizedInterface);
        introspectionInterfaces.add(standardizedInterface);

        when(rootIntrospectionNode.getInterfaces()).thenReturn(introspectionInterfaces);
        List<InterfaceDetail> interfaceDetailList = xmlBasedBusIntrospector.getStandardizedInterfacesExposedOnBus();

        assertEquals(1, interfaceDetailList.size());
        InterfaceDetail interfaceDetail = interfaceDetailList.get(0);
        assertEquals(ROOT_PATH, interfaceDetail.getPath());
        assertEquals(1, interfaceDetail.getIntrospectionInterfaces().size());
        assertEquals(standardizedInterface, interfaceDetail.getIntrospectionInterfaces().get(0));
    }

    @Test
    public void getInterfacesExposedOnBusBasedOnName() throws Exception
    {
        IntrospectionInterface nonStandardizedInterface = Mockito.mock(IntrospectionInterface.class);
        when(nonStandardizedInterface.getName()).thenReturn("com");
        IntrospectionInterface standardizedInterface = Mockito.mock(IntrospectionInterface.class);
        when(standardizedInterface.getName()).thenReturn(STANDARDIZED_INTERFACE_NAME);
        List<IntrospectionInterface> introspectionInterfaces = new ArrayList<IntrospectionInterface>();
        introspectionInterfaces.add(nonStandardizedInterface);
        introspectionInterfaces.add(standardizedInterface);

        when(rootIntrospectionNode.getInterfaces()).thenReturn(introspectionInterfaces);
        List<InterfaceDetail> interfaceDetailList = xmlBasedBusIntrospector.getInterfacesExposedOnBusBasedOnName("com");

        assertEquals(1, interfaceDetailList.size());
        InterfaceDetail interfaceDetail = interfaceDetailList.get(0);
        assertEquals(ROOT_PATH, interfaceDetail.getPath());
        assertEquals(1, interfaceDetail.getIntrospectionInterfaces().size());
        assertEquals(nonStandardizedInterface, interfaceDetail.getIntrospectionInterfaces().get(0));
    }

    @Test
    public void getInterfacesExposedOnBusBasedOnNameAndPath() throws Exception
    {
        IntrospectionInterface nonStandardizedInterface = Mockito.mock(IntrospectionInterface.class);
        when(nonStandardizedInterface.getName()).thenReturn("com");
        List<IntrospectionInterface> nonStandardizedIntrospectionInterfaces = new ArrayList<IntrospectionInterface>();
        nonStandardizedIntrospectionInterfaces.add(nonStandardizedInterface);

        IntrospectionInterface standardizedInterface = Mockito.mock(IntrospectionInterface.class);
        when(standardizedInterface.getName()).thenReturn(STANDARDIZED_INTERFACE_NAME);
        List<IntrospectionInterface> standardizedIntrospectionInterfaces = new ArrayList<IntrospectionInterface>();
        standardizedIntrospectionInterfaces.add(standardizedInterface);

        when(rootIntrospectionNode.getInterfaces()).thenReturn(standardizedIntrospectionInterfaces);
        when(subNodeIntrospectionNode.getInterfaces()).thenReturn(nonStandardizedIntrospectionInterfaces);
        List<InterfaceDetail> interfaceDetailList = xmlBasedBusIntrospector.getInterfacesExposedOnBusUnderSpecifiedPathBasedOnName(SUB_NODE_PATH, "com");

        assertEquals(1, interfaceDetailList.size());
        InterfaceDetail interfaceDetail = interfaceDetailList.get(0);
        assertEquals(SUB_NODE_PATH, interfaceDetail.getPath());
        assertEquals(1, interfaceDetail.getIntrospectionInterfaces().size());
        assertEquals(nonStandardizedInterface, interfaceDetail.getIntrospectionInterfaces().get(0));
        assertTrue(xmlBasedBusIntrospector.getInterfacesExposedOnBusUnderSpecifiedPathBasedOnName(SUB_NODE_CHILD_PATH, "com").isEmpty());
    }

    @Test
    public void isInterfacePresentReturnsFalseIfInterfaceNotFoundAtPath() throws Exception
    {
        IntrospectionInterface standardizedInterface = Mockito.mock(IntrospectionInterface.class);
        when(standardizedInterface.getName()).thenReturn(STANDARDIZED_INTERFACE_NAME);
        List<IntrospectionInterface> standardizedIntrospectionInterfaces = new ArrayList<IntrospectionInterface>();
        standardizedIntrospectionInterfaces.add(standardizedInterface);
        when(subNodeIntrospectionNode.getInterfaces()).thenReturn(standardizedIntrospectionInterfaces);

        assertFalse(xmlBasedBusIntrospector.isInterfacePresent(SUB_NODE_PATH, "com"));
    }

    @Test
    public void isInterfacePresentReturnsTrueIfInterfaceIsFoundAtPath() throws Exception
    {
        IntrospectionInterface standardizedInterface = Mockito.mock(IntrospectionInterface.class);
        when(standardizedInterface.getName()).thenReturn(STANDARDIZED_INTERFACE_NAME);
        List<IntrospectionInterface> standardizedIntrospectionInterfaces = new ArrayList<IntrospectionInterface>();
        standardizedIntrospectionInterfaces.add(standardizedInterface);
        when(subNodeGrandChildIntrospectionNode.getInterfaces()).thenReturn(standardizedIntrospectionInterfaces);

        assertTrue(xmlBasedBusIntrospector.isInterfacePresent(SUB_NODE_GRANDCHILD_PATH, STANDARDIZED_INTERFACE_NAME));
    }

    @Test
    public void isAncestorInterfacePresentReturnsFalseIfNotFound() throws Exception
    {
        assertFalse(xmlBasedBusIntrospector.isAncestorInterfacePresent(SUB_NODE_GRANDCHILD_PATH, STANDARDIZED_INTERFACE_NAME));
    }

    @Test
    public void isAncestorInterfacePresentReturnsTrueIfParentFound() throws Exception
    {
        IntrospectionInterface standardizedInterface = Mockito.mock(IntrospectionInterface.class);
        when(standardizedInterface.getName()).thenReturn(STANDARDIZED_INTERFACE_NAME);
        List<IntrospectionInterface> standardizedIntrospectionInterfaces = new ArrayList<IntrospectionInterface>();
        standardizedIntrospectionInterfaces.add(standardizedInterface);
        when(subNodeChildIntrospectionNode.getInterfaces()).thenReturn(standardizedIntrospectionInterfaces);

        assertTrue(xmlBasedBusIntrospector.isAncestorInterfacePresent(SUB_NODE_GRANDCHILD_PATH, STANDARDIZED_INTERFACE_NAME));
    }

    @Test
    public void isAncestorInterfacePresentReturnsTrueIfGrandParentFound() throws Exception
    {
        IntrospectionInterface standardizedInterface = Mockito.mock(IntrospectionInterface.class);
        when(standardizedInterface.getName()).thenReturn(STANDARDIZED_INTERFACE_NAME);
        List<IntrospectionInterface> standardizedIntrospectionInterfaces = new ArrayList<IntrospectionInterface>();
        standardizedIntrospectionInterfaces.add(standardizedInterface);
        when(subNodeIntrospectionNode.getInterfaces()).thenReturn(standardizedIntrospectionInterfaces);

        assertTrue(xmlBasedBusIntrospector.isAncestorInterfacePresent(SUB_NODE_GRANDCHILD_PATH, STANDARDIZED_INTERFACE_NAME));
    }

    private void constructBusIntrospector()
    {
        xmlBasedBusIntrospector = new XmlBasedBusIntrospector(busAttachment, PEER_NAME, SESSION_ID)
        {
            @Override
            IntrospectionXmlParser getIntrospectionXmlParser()
            {
                return introspectionXmlParser;
            }

            @Override
            Class<?>[] getClasses(Class<?> interfaceClass)
            {
                return classes;
            }

            @Override
            ByteArrayInputStream getInputStream(String introspectionXml)
            {
                if (ROOT_INTROSPECTION_XML.equals(introspectionXml))
                {
                    return rootInputStream;
                }
                else if (SUB_NODE_INTROSPECTION_XML.equals(introspectionXml))
                {
                    return subNodeInputStream;
                }
                else if (SUB_NODE_CHILD_INTROSPECTION_XML.equals(introspectionXml))
                {
                    return subNodeChildInputStream;
                }
                else if (SUB_NODE_GRANDCHILD_INTROSPECTION_XML.equals(introspectionXml))
                {
                    return subNodeGrandChildInputStream;
                }
                else
                {
                    return null;
                }
            }
        };
    }

    private void setupSubNodeGrandChildIntrospectionNode() throws BusException, IOException, ParserConfigurationException, SAXException
    {
        when(busAttachment.getProxyBusObject(PEER_NAME, SUB_NODE_GRANDCHILD_PATH, SESSION_ID, classes)).thenReturn(subNodeGrandChildProxyBusObject);
        when(subNodeGrandChildProxyBusObject.getInterface(Introspectable.class)).thenReturn(subNodeGrandChildIntrospectable);
        when(subNodeGrandChildIntrospectable.Introspect()).thenReturn(SUB_NODE_GRANDCHILD_INTROSPECTION_XML);
        when(subNodeGrandChildIntrospectionNode.getSubNodes()).thenReturn(null);
        when(introspectionXmlParser.parseXML(subNodeGrandChildInputStream)).thenReturn(subNodeGrandChildIntrospectionNode);
    }

    private void setupSubNodeChildIntrospectionNode() throws BusException, IOException, ParserConfigurationException, SAXException
    {
        when(busAttachment.getProxyBusObject(PEER_NAME, SUB_NODE_CHILD_PATH, SESSION_ID, classes)).thenReturn(subNodeChildProxyBusObject);
        when(subNodeChildProxyBusObject.getInterface(Introspectable.class)).thenReturn(subNodeChildIntrospectable);
        when(subNodeChildIntrospectable.Introspect()).thenReturn(SUB_NODE_CHILD_INTROSPECTION_XML);
        when(introspectionXmlParser.parseXML(subNodeChildInputStream)).thenReturn(subNodeChildIntrospectionNode);

        introspectionSubNodeGrandChildren = constructSubNodes(SUB_NODE_GRANDCHILD_NAME, introspectionSubNodeGrandChild);
        when(subNodeChildIntrospectionNode.getSubNodes()).thenReturn(introspectionSubNodeGrandChildren);
    }

    private void setupSubNodeIntrospectionNode() throws BusException, IOException, ParserConfigurationException, SAXException
    {
        when(busAttachment.getProxyBusObject(PEER_NAME, SUB_NODE_PATH, SESSION_ID, classes)).thenReturn(subNodeProxyBusObject);
        when(subNodeProxyBusObject.getInterface(Introspectable.class)).thenReturn(subNodeIntrospectable);
        when(subNodeIntrospectable.Introspect()).thenReturn(SUB_NODE_INTROSPECTION_XML);
        when(introspectionXmlParser.parseXML(subNodeInputStream)).thenReturn(subNodeIntrospectionNode);

        introspectionSubNodeChildren = constructSubNodes(SUB_NODE_CHILD_NAME, introspectionSubNodeChild);
        when(subNodeIntrospectionNode.getSubNodes()).thenReturn(introspectionSubNodeChildren);
    }

    private void setupRootIntrospectionNode() throws BusException, IOException, ParserConfigurationException, SAXException
    {
        when(busAttachment.getProxyBusObject(PEER_NAME, ROOT_PATH, SESSION_ID, classes)).thenReturn(rootProxyBusObject);
        when(rootProxyBusObject.getInterface(Introspectable.class)).thenReturn(rootIntrospectable);
        when(rootIntrospectable.Introspect()).thenReturn(ROOT_INTROSPECTION_XML);
        when(introspectionXmlParser.parseXML(rootInputStream)).thenReturn(rootIntrospectionNode);

        introspectionSubNodes = constructSubNodes(SUB_NODE_NAME, introspectionSubNode);
        when(rootIntrospectionNode.getSubNodes()).thenReturn(introspectionSubNodes);
    }

    private static String convertToString(InputStream inputStream) throws IOException
    {
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        while ((line = bufferedReader.readLine()) != null)
        {
            stringBuilder.append(line);
        }

        return stringBuilder.toString();
    }

    private List<IntrospectionSubNode> constructSubNodes(String subNodeName, IntrospectionSubNode subNode)
    {
        when(subNode.getName()).thenReturn(subNodeName);
        List<IntrospectionSubNode> subNodes = new ArrayList<IntrospectionSubNode>();
        subNodes.add(subNode);

        return subNodes;
    }
}