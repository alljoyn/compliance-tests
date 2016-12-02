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
#pragma once

#include "BusIntrospector.h"
#include "IntrospectionXmlParser.h"

#include <alljoyn\BusAttachment.h>

class XMLBasedBusIntrospector : public BusIntrospector
{
public:
	XMLBasedBusIntrospector(ajn::BusAttachment*, std::string, int);
	ajn::ProxyBusObject* getProxyBusObject(const std::string&);
	NodeDetail* introspect(std::string);
	std::list<NodeDetail> introspectEntireTree(std::string);
	std::list<InterfaceDetail> getStandardizedInterfacesExposedOnBus();
	std::list<InterfaceDetail> getInterfacesExposedOnBusBasedOnName(std::string);
	std::list<InterfaceDetail> getInterfacesExposedOnBusUnderSpecifiedPathBasedOnName(std::string, std::string);
	bool isInterfacePresent(std::string, std::string);
	bool isAncestorInterfacePresent(std::string, std::string);
	IntrospectionXmlParser getIntrospectionXmlParser();
	std::list<NodeDetail> introspectUsingNodeDetail(NodeDetail);
	std::string determinePath(std::string, IntrospectionSubNode);
	std::list<InterfaceDetail> determineIntrospectionInterfaces(std::list<NodeDetail>, std::string);
	std::list<IntrospectionInterface> filterIntrospectionInterfaces(std::list<IntrospectionInterface>, std::string);
	std::list<IntrospectionInterface> filterInterfacesBasedOnName(std::list<IntrospectionInterface>, std::string);
	std::list<IntrospectionInterface> filterStandardizedInterfaces(std::list<IntrospectionInterface>);
private:
	static std::string SLASH_CHARACTER;
	static std::string ROOT_PATH;
	static std::string ALL_STANDARDIZED_INTERFACES;
	static std::string STANDARDIZED_INTERFACE_NAME_PREFIX;
	static std::string STANDARDIZED_INTERFACE_NAME_PREFIX_NEW;
	IntrospectionXmlParser m_IntrospectionXmlParser;
	ajn::BusAttachment* m_BusAttachment;
	std::string m_PeerName;
	int m_SessionId;
	std::list<NodeDetail> m_NodeDetailList;
};