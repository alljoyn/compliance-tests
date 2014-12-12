/*******************************************************************************
 *  Copyright (c) 2013 - 2014, AllSeen Alliance. All rights reserved.
 *
 *     Permission to use, copy, modify, and/or distribute this software for any
 *     purpose with or without fee is hereby granted, provided that the above
 *     copyright notice and this permission notice appear in all copies.
 *
 *     THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 *     WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 *     MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 *     ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 *     WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 *     ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 *     OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 *******************************************************************************/
package org.alljoyn.validation.framework.utils.introspection;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.alljoyn.bus.BusAttachment;
import org.alljoyn.bus.BusException;
import org.alljoyn.bus.ProxyBusObject;
import org.alljoyn.bus.ifaces.Introspectable;
import org.alljoyn.validation.framework.utils.introspection.bean.InterfaceDetail;
import org.alljoyn.validation.framework.utils.introspection.bean.IntrospectionInterface;
import org.alljoyn.validation.framework.utils.introspection.bean.IntrospectionNode;
import org.alljoyn.validation.framework.utils.introspection.bean.IntrospectionSubNode;
import org.alljoyn.validation.framework.utils.introspection.bean.NodeDetail;
import org.xml.sax.SAXException;

public class XmlBasedBusIntrospector implements BusIntrospector
{
    private static final String SLASH_CHARACTER = "/";
    private static final String ROOT_PATH = "/";
    private static final String ALL_STANDARDIZED_INTERFACES = "ALL";
    private static final String STANDARDIZED_INTERFACE_NAME_PREFIX = "org.alljoyn";
    // This prefix was created due to alljoyn prefix is deprecated
    private static final String STANDARDIZED_INTERFACE_NAME_PREFIX_NEW = "org.allseen";
    private IntrospectionXmlParser introspectionXmlParser = new IntrospectionXmlParser();
    private BusAttachment busAttachment;
    private String peerName;
    private int sessionId;

    public XmlBasedBusIntrospector(BusAttachment busAttachment, String peerName, int sessionId)
    {
        this.busAttachment = busAttachment;
        this.peerName = peerName;
        this.sessionId = sessionId;
    }

    @Override
    public <T> T getInterface(String path, Class<T> interfaceClass)
    {
        ProxyBusObject proxyBusObject = busAttachment.getProxyBusObject(peerName, path, sessionId, getClasses(interfaceClass));

        return proxyBusObject.getInterface(interfaceClass);
    }

    public NodeDetail introspect(String path) throws BusException, IOException, ParserConfigurationException, SAXException
    {
        ProxyBusObject proxyBusObject = busAttachment.getProxyBusObject(peerName, path, sessionId, getClasses(Introspectable.class));
        Introspectable introspectableInterface = proxyBusObject.getInterface(Introspectable.class);
        String introspectionXml = introspectableInterface.Introspect();
        IntrospectionNode introspectionNode = getIntrospectionXmlParser().parseXML(getInputStream(introspectionXml));

        return new NodeDetail(path, introspectionNode);
    }

    public List<NodeDetail> introspectEntireTree(String path) throws BusException, IOException, ParserConfigurationException, SAXException
    {
        NodeDetail nodeDetail = introspect(path);
        List<NodeDetail> nodeDetailList = new ArrayList<NodeDetail>();
        nodeDetailList.add(nodeDetail);
        nodeDetailList.addAll(introspectUsingNodeDetail(nodeDetail));

        return nodeDetailList;
    }

    public List<InterfaceDetail> getStandardizedInterfacesExposedOnBus() throws BusException, IOException, ParserConfigurationException, SAXException
    {
        List<NodeDetail> nodeDetailList = introspectEntireTree(ROOT_PATH);

        return determineIntrospectionInterfaces(nodeDetailList, ALL_STANDARDIZED_INTERFACES);
    }

    public List<InterfaceDetail> getInterfacesExposedOnBusBasedOnName(String interfaceName) throws BusException, IOException, ParserConfigurationException, SAXException
    {
        List<NodeDetail> nodeDetailList = introspectEntireTree(ROOT_PATH);

        return determineIntrospectionInterfaces(nodeDetailList, interfaceName);
    }

    public List<InterfaceDetail> getInterfacesExposedOnBusUnderSpecifiedPathBasedOnName(String path, String interfaceName) throws BusException, IOException,
            ParserConfigurationException, SAXException
    {
        List<NodeDetail> nodeDetailList = introspectEntireTree(path);

        return determineIntrospectionInterfaces(nodeDetailList, interfaceName);
    }

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

    IntrospectionXmlParser getIntrospectionXmlParser()
    {
        return introspectionXmlParser;
    }

    Class<?>[] getClasses(Class<?> interfaceClass)
    {
        return new Class<?>[]
        { interfaceClass };
    }

    ByteArrayInputStream getInputStream(String introspectionXml)
    {
        return new ByteArrayInputStream(introspectionXml.getBytes());
    }

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

    private List<IntrospectionInterface> filterStandardizedInterfaces(List<IntrospectionInterface> introspectionInterfaces)
    {
        List<IntrospectionInterface> standardizedInterfaces = new ArrayList<IntrospectionInterface>();

        for (IntrospectionInterface introspectionInterface : introspectionInterfaces)
        {
            if (introspectionInterface.getName().startsWith(STANDARDIZED_INTERFACE_NAME_PREFIX)
            		|| introspectionInterface.getName().startsWith(STANDARDIZED_INTERFACE_NAME_PREFIX_NEW))
            {
                standardizedInterfaces.add(introspectionInterface);
            }
        }

        return standardizedInterfaces;
    }
}