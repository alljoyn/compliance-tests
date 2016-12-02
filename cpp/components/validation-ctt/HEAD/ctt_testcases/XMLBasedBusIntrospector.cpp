/******************************************************************************
* * 
*    Copyright (c) 2016 Open Connectivity Foundation and AllJoyn Open
*    Source Project Contributors and others.
*    
*    All rights reserved. This program and the accompanying materials are
*    made available under the terms of the Apache License, Version 2.0
*    which accompanies this distribution, and is available at
*    http://www.apache.org/licenses/LICENSE-2.0

******************************************************************************/
#include "stdafx.h"
#include "XMLBasedBusIntrospector.h"

#include <alljoyn\AllJoynStd.h>

using namespace ajn;
using namespace std;

std::string XMLBasedBusIntrospector::SLASH_CHARACTER = "/";
std::string XMLBasedBusIntrospector::ROOT_PATH = "/";
std::string XMLBasedBusIntrospector::ALL_STANDARDIZED_INTERFACES = "ALL";
std::string XMLBasedBusIntrospector::STANDARDIZED_INTERFACE_NAME_PREFIX = "org.alljoyn";
std::string XMLBasedBusIntrospector::STANDARDIZED_INTERFACE_NAME_PREFIX_NEW = "org.allseen";

XMLBasedBusIntrospector::XMLBasedBusIntrospector(BusAttachment* t_BusAttachment, string t_PeerName,
	int t_SessionId) : m_BusAttachment(t_BusAttachment), m_PeerName(t_PeerName),
	m_SessionId(t_SessionId) {}

ProxyBusObject* XMLBasedBusIntrospector::getProxyBusObject(const std::string& t_Path)
{
	ProxyBusObject* proxyBusObject = new ProxyBusObject(*m_BusAttachment, m_PeerName.c_str(), t_Path.c_str(), m_SessionId);
	proxyBusObject->IntrospectRemoteObject();
	return proxyBusObject;
}

NodeDetail* XMLBasedBusIntrospector::introspect(string t_Path)
{
	ProxyBusObject proxyBusObject(*m_BusAttachment, m_PeerName.c_str(), t_Path.c_str(), m_SessionId);
	proxyBusObject.IntrospectRemoteObject();
	
	Message replyMessage(*m_BusAttachment);
	if (ER_OK == proxyBusObject.MethodCall(ajn::org::freedesktop::DBus::Introspectable::InterfaceName,
		"Introspect", NULL, 0, replyMessage))
	{
		char* introspectionXml = NULL;
		replyMessage->GetArg(0)->Get("s", &introspectionXml);

		IntrospectionNode introspectionNode = getIntrospectionXmlParser().parseXML(introspectionXml);

		return new NodeDetail(t_Path, introspectionNode);
	}
	else
	{
		return nullptr;
	}
}

list<NodeDetail> XMLBasedBusIntrospector::introspectEntireTree(string t_Path)
{
	NodeDetail* nodeDetail = introspect(t_Path);
	list<NodeDetail> nodeDetailList;

	if (nodeDetail != nullptr)
	{
		nodeDetailList.push_back(*nodeDetail);

		for (auto introspectedNodeDetail : introspectUsingNodeDetail(*nodeDetail))
		{
			nodeDetailList.push_back(introspectedNodeDetail);
		}
	}

	return nodeDetailList;
}

list<InterfaceDetail> XMLBasedBusIntrospector::getStandardizedInterfacesExposedOnBus()
{
	if (m_NodeDetailList.empty())
	{
		m_NodeDetailList = introspectEntireTree(ROOT_PATH);
	}

	return determineIntrospectionInterfaces(m_NodeDetailList, XMLBasedBusIntrospector::ALL_STANDARDIZED_INTERFACES);
}

list<InterfaceDetail> XMLBasedBusIntrospector::getInterfacesExposedOnBusBasedOnName(string t_InterfaceName)
{
	if (m_NodeDetailList.empty())
	{
		m_NodeDetailList = introspectEntireTree(ROOT_PATH);
	}

	return determineIntrospectionInterfaces(m_NodeDetailList, t_InterfaceName);
}

list<InterfaceDetail> XMLBasedBusIntrospector::getInterfacesExposedOnBusUnderSpecifiedPathBasedOnName(string t_Path, string t_InterfaceName)
{
	list<NodeDetail> nodeDetailList = introspectEntireTree(t_Path);

	return determineIntrospectionInterfaces(nodeDetailList, t_InterfaceName);
}

bool XMLBasedBusIntrospector::isInterfacePresent(string t_Path, string t_InterfaceName)
{
	NodeDetail* nodeDetail = introspect(t_Path);
	list<IntrospectionInterface> introspectionInterfaces = *nodeDetail->getIntrospectionNode().getInterfaces();

	for (auto introspectionInterface : introspectionInterfaces)
	{
		if (introspectionInterface.getName() == t_InterfaceName)
		{
			return true;
		}
	}

	return false;
}

bool XMLBasedBusIntrospector::isAncestorInterfacePresent(string t_Path, string t_AncestorInterfaceName)
{
	if (t_Path.find_last_of("/") != 0)
	{
		string parentInterfacePath = t_Path.substr(0, t_Path.find_last_of("/"));

		if (isInterfacePresent(parentInterfacePath, t_AncestorInterfaceName))
		{
			return true;
		}
		else
		{
			return isAncestorInterfacePresent(parentInterfacePath, t_AncestorInterfaceName);
		}
	}

	return false;
}

IntrospectionXmlParser XMLBasedBusIntrospector::getIntrospectionXmlParser()
{
	return m_IntrospectionXmlParser;
}

list<NodeDetail> XMLBasedBusIntrospector::introspectUsingNodeDetail(NodeDetail t_NodeDetail)
{
	list<IntrospectionSubNode> introspectionSubNodes = *t_NodeDetail.getIntrospectionNode().getSubNodes();
	list<NodeDetail> nodeDetailList;

	if (!introspectionSubNodes.empty())
	{
		for (auto introspectionSubNode : introspectionSubNodes)
		{
			string path = determinePath(t_NodeDetail.getPath(), introspectionSubNode);

			for (auto introspectedNodeDetail : introspectEntireTree(path))
			{
				nodeDetailList.push_back(introspectedNodeDetail);
			}
		}
	}

	return nodeDetailList;
}

string XMLBasedBusIntrospector::determinePath(string t_PathPrefix, IntrospectionSubNode t_IntrospectionSubNode)
{
	if (t_PathPrefix.compare(t_PathPrefix.length() - XMLBasedBusIntrospector::SLASH_CHARACTER.length(),
		XMLBasedBusIntrospector::SLASH_CHARACTER.length(), XMLBasedBusIntrospector::SLASH_CHARACTER) == 0)
	{
		return t_PathPrefix + t_IntrospectionSubNode.getName();
	}
	else
	{
		return t_PathPrefix + XMLBasedBusIntrospector::SLASH_CHARACTER + t_IntrospectionSubNode.getName();
	}
}

list<InterfaceDetail> XMLBasedBusIntrospector::determineIntrospectionInterfaces(list<NodeDetail> t_NodeDetailList,
	string t_InterfaceName)
{
	list<InterfaceDetail> interfaceDetailList;

	for (auto nodeDetail : t_NodeDetailList)
	{
		IntrospectionNode introspectionNode = nodeDetail.getIntrospectionNode();
		list<IntrospectionInterface> filteredIntrospectionInterfaces = 
			filterIntrospectionInterfaces(*introspectionNode.getInterfaces(), t_InterfaceName);

		if (!filteredIntrospectionInterfaces.empty())
		{
			interfaceDetailList.push_back(InterfaceDetail(nodeDetail.getPath(), filteredIntrospectionInterfaces));
		}
	}

	return interfaceDetailList;
}

list<IntrospectionInterface> XMLBasedBusIntrospector::filterIntrospectionInterfaces(list<IntrospectionInterface> t_interfaces,
	string t_InterfaceName)
{
	if (t_InterfaceName == XMLBasedBusIntrospector::ALL_STANDARDIZED_INTERFACES)
	{
		return filterStandardizedInterfaces(t_interfaces);
	}
	else
	{
		return filterInterfacesBasedOnName(t_interfaces, t_InterfaceName);
	}
}

list<IntrospectionInterface> XMLBasedBusIntrospector::filterInterfacesBasedOnName(list<IntrospectionInterface> t_introspectionInterfaces,
	string t_InterfaceName)
{
	list<IntrospectionInterface> filteredInterfaces;

	for (auto introspectionInterface : t_introspectionInterfaces)
	{
		if (introspectionInterface.getName() == t_InterfaceName)
		{
			filteredInterfaces.push_back(introspectionInterface);
		}
	}

	return filteredInterfaces;
}

list<IntrospectionInterface> XMLBasedBusIntrospector::filterStandardizedInterfaces(list<IntrospectionInterface> t_introspectionInterfaces)
{
	list<IntrospectionInterface> standardizedInterfaces;

	for (auto introspectionInterface : t_introspectionInterfaces)
	{
		if ((introspectionInterface.getName().compare(0, XMLBasedBusIntrospector::STANDARDIZED_INTERFACE_NAME_PREFIX.length(),
			XMLBasedBusIntrospector::STANDARDIZED_INTERFACE_NAME_PREFIX) == 0)
			|| (introspectionInterface.getName().compare(0, XMLBasedBusIntrospector::STANDARDIZED_INTERFACE_NAME_PREFIX_NEW.length(),
			XMLBasedBusIntrospector::STANDARDIZED_INTERFACE_NAME_PREFIX_NEW) == 0))
		{
			standardizedInterfaces.push_back(introspectionInterface);
		}
	}

	return standardizedInterfaces;
}