/******************************************************************************
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
	static AJ_PCSTR BUS_APPLICATION_NAME;
	static AJ_PCSTR INTROSPECTION_XML_DESC_EXPECTED;
	static AJ_PCSTR INTROSPECTION_XML_DESC_REGEX;
	static AJ_PCSTR INTROSPECTION_XML_DESC_PLACEHOLDER;

	std::string m_DutDeviceId = std::string{ "" };
	uint8_t* m_DutAppId{ nullptr };
	ServiceHelper* m_ServiceHelper{ nullptr };
	AboutAnnouncementDetails* m_DeviceAboutAnnouncement{ nullptr };
	ajn::AboutProxy* m_AboutProxy{ nullptr };

	// EventsActions-v1-01
	std::list<std::string> getAllSeenIntrospectableObjectPaths();
	bool testObjectValidity(std::string);
	void getDescriptionLanguages(ajn::ProxyBusObject, std::string, std::vector<AJ_PCSTR>&);
	void getIntrospectionXml(ajn::ProxyBusObject, std::string, std::string, std::string&);
	bool testChildrenObjectValidity(std::string, std::string);
	bool testObjectValidityPerLanguages(ajn::ProxyBusObject, std::string, std::vector<AJ_PCSTR>);
	std::string removeXmlDescription(std::string);

	void releaseResources();
};