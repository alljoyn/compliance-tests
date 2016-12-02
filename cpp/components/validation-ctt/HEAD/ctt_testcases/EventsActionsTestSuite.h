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

#include "IOManager.h"
#include "ServiceHelper.h"

#include <alljoyn\AboutProxy.h>

class EventsActionsTestSuite : public ::testing::Test, public IOManager
{
public:
	EventsActionsTestSuite();
	void SetUp();
	void TearDown();

protected:
	static const char* BUS_APPLICATION_NAME;
	static const char* INTROSPECTION_XML_DESC_EXPECTED;
	static const char* INTROSPECTION_XML_DESC_REGEX;
	static const char* INTROSPECTION_XML_DESC_PLACEHOLDER;

	std::string m_DutDeviceId = std::string{ "" };
	uint8_t* m_DutAppId{ nullptr };
	ServiceHelper* m_ServiceHelper{ nullptr };
	AboutAnnouncementDetails* m_DeviceAboutAnnouncement{ nullptr };
	ajn::AboutProxy* m_AboutProxy{ nullptr };

	// EventsActions-v1-01
	std::list<std::string> getAllSeenIntrospectableObjectPaths();
	bool testObjectValidity(std::string);
	void getDescriptionLanguages(ajn::ProxyBusObject, std::string, std::vector<const char*>&);
	void getIntrospectionXml(ajn::ProxyBusObject, std::string, std::string, std::string&);
	bool testChildrenObjectValidity(std::string, std::string);
	bool testObjectValidityPerLanguages(ajn::ProxyBusObject, std::string, std::vector<const char*>);
	std::string removeXmlDescription(std::string);

	void releaseResources();
};