/******************************************************************************
* * Copyright (c) Open Connectivity Foundation (OCF) and AllJoyn Open
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
*     THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL
*     WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED
*     WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE
*     AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL
*     DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR
*     PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER
*     TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
*     PERFORMANCE OF THIS SOFTWARE.
******************************************************************************/
#include "stdafx.h"
#include "ControlPanelTestSuite.h"

#include <regex>

#include "ArrayParser.h"

using namespace std;
using namespace ajn;
using namespace services;

const char* ControlPanelTestSuite::BUS_APPLICATION_NAME = "ControlPanelTestSuite";
const char* ControlPanelTestSuite::CONTROLPANEL_INTERFACE_NAME = "org.alljoyn.ControlPanel.ControlPanel";
const char* ControlPanelTestSuite::CONTAINER_INTERFACE_NAME = "org.alljoyn.ControlPanel.Container";
const char* ControlPanelTestSuite::SECURED_CONTAINER_INTERFACE_NAME = "org.alljoyn.ControlPanel.SecuredContainer";
const char* ControlPanelTestSuite::NOTIFICATION_ACTION_INTERFACE_NAME = "org.alljoyn.ControlPanel.NotificationAction";
//const char* ControlPanelTestSuite::LIST_PROPERTY_INTERFACE_NAME = "org.alljoyn.ControlPanel.ListProperty";
//const char* ControlPanelTestSuite::SECURED_LIST_PROPERTY_INTERFACE_NAME = "org.alljoyn.ControlPanel.SecuredListProperty";
const char* ControlPanelTestSuite::PROPERTY_INTERFACE_NAME = "org.alljoyn.ControlPanel.Property";
const char* ControlPanelTestSuite::SECURED_PROPERTY_INTERFACE_NAME = "org.alljoyn.ControlPanel.SecuredProperty";
const char* ControlPanelTestSuite::LABEL_PROPERTY_INTERFACE_NAME = "org.alljoyn.ControlPanel.LabelProperty";
const char* ControlPanelTestSuite::ACTION_INTERFACE_NAME = "org.alljoyn.ControlPanel.Action";
const char* ControlPanelTestSuite::SECURED_ACTION_INTERFACE_NAME = "org.alljoyn.ControlPanel.SecuredAction";
const char* ControlPanelTestSuite::DIALOG_INTERFACE_NAME = "org.alljoyn.ControlPanel.Dialog";
const char* ControlPanelTestSuite::SECURED_DIALOG_INTERFACE_NAME = "org.alljoyn.ControlPanel.SecuredDialog";
const char* ControlPanelTestSuite::HTTP_CONTROL_INTERFACE_NAME = "org.alljoyn.ControlPanel.HttpControl";

const char* ControlPanelTestSuite::CONTROLPANEL_PATH_PATTERN = "/ControlPanel/[^/]+/[^/]+";
const char* ControlPanelTestSuite::HTTPCONTROL_PATH_PATTERN = "/ControlPanel/[^/]+/HTTPControl";
const char* ControlPanelTestSuite::URL_REGEX = R"(^(([^:\/?#]+):)?(//([^\/?#]*))?([^?#]*)(\?([^#]*))?(#(.*))?)";

ControlPanelTestSuite::ControlPanelTestSuite() : IOManager(ServiceFramework::CONTROL_PANEL)
{

}

void ControlPanelTestSuite::SetUp()
{
	CLOG(INFO, "raw") << "====================================================";
	LOG(INFO) << "test setUp started";

	m_DutDeviceId = m_IxitMap.at("IXITCO_DeviceId");
	m_DutAppId = ArrayParser::parseAppIdFromString(m_IxitMap.at("IXITCO_AppId"));

	m_ServiceHelper = new ServiceHelper();
	QStatus status = m_ServiceHelper->initializeClient(BUS_APPLICATION_NAME, m_DutDeviceId, m_DutAppId);
	ASSERT_EQ(status, ER_OK) << "serviceHelper Initialize() failed: " << QCC_StatusText(status);

	m_DeviceAboutAnnouncement =
		m_ServiceHelper->waitForNextDeviceAnnouncement(atol(
		m_GeneralParameterMap.at("GPCO_AnnouncementTimeout").c_str()
		) * 1000);

	ASSERT_NE(m_DeviceAboutAnnouncement, nullptr) << "Timed out waiting for About announcement";
	SUCCEED();

	m_AboutProxy = m_ServiceHelper->connectAboutProxy(*m_DeviceAboutAnnouncement);
	ASSERT_NE(m_AboutProxy, nullptr) << "AboutProxy connection failed";
	SUCCEED() << "AboutProxy connected";

	m_ServiceHelper->enableAuthentication("/Keystore",
		m_IcsMap.at("ICSCO_SrpKeyX"), m_IxitMap.at("IXITCO_SrpKeyXPincode"),
		m_IcsMap.at("ICSCO_SrpLogon"), m_IxitMap.at("IXITCO_SrpLogonUser"), m_IxitMap.at("IXITCO_SrpLogonPass"),
		m_IcsMap.at("ICSCO_EcdheNull"),
		m_IcsMap.at("ICSCO_EcdhePsk"), m_IxitMap.at("IXITCO_EcdhePskPassword"),
		m_IcsMap.at("ICSCO_EcdheEcdsa"), m_IxitMap.at("IXITCO_EcdheEcdsaPrivateKey"), m_IxitMap.at("IXITCO_EcdheEcdsaCertChain"),
		m_IcsMap.at("ICSCO_EcdheSpeke"), m_IxitMap.at("IXITCO_EcdheSpekePassword"));
	m_BusIntrospector = new XMLBasedBusIntrospector(m_ServiceHelper->getBusIntrospector(*m_DeviceAboutAnnouncement));

	LOG(INFO) << "test setUp done";
	CLOG(INFO, "raw") << "====================================================";
}

void ControlPanelTestSuite::TearDown()
{
	CLOG(INFO, "raw") << "====================================================";
	LOG(INFO) << "test tearDown started";

	releaseResources();

	LOG(INFO) << "test tearDown done";
	CLOG(INFO, "raw") << "====================================================";

	storeResults();
}

void ControlPanelTestSuite::releaseResources()
{
	if (m_AboutProxy != nullptr)
	{
		delete m_AboutProxy;
		m_AboutProxy = nullptr;
	}

	if (m_ServiceHelper != nullptr)
	{
		QStatus status = m_ServiceHelper->release();

		EXPECT_EQ(status, ER_OK) << "serviceHelper Release() failed: " << QCC_StatusText(status);
		delete m_ServiceHelper;
	}
}

TEST_F(ControlPanelTestSuite, ControlPanel_v1_01)
{
	list<InterfaceDetail> controlPanelInterfaceListExposedOnBus;

	try
	{
		controlPanelInterfaceListExposedOnBus = m_BusIntrospector->getInterfacesExposedOnBusBasedOnName(CONTROLPANEL_INTERFACE_NAME);
	}
	catch (xercesc::SAXParseException&)
	{
		FAIL() << "Introspecting ControlPanel interface exposed on bus failed";
	}

	ASSERT_FALSE(controlPanelInterfaceListExposedOnBus.empty())
		<< "No bus objects implement ControlPanel interface";

	validateControlPanelInterfaceDetailList(controlPanelInterfaceListExposedOnBus);
}

void ControlPanelTestSuite::validateControlPanelInterfaceDetailList(list<InterfaceDetail> t_InterfaceDetailList)
{
	for (auto& interfaceDetail : t_InterfaceDetailList)
	{
		string path = interfaceDetail.getPath();
		LOG(INFO) << "Validating ControlPanel object at: " << path;

		EXPECT_TRUE(stringMatchesPattern(CONTROLPANEL_PATH_PATTERN, path))
			<< path << " does not match te expected pattern /ControlPanel/{unit}/{panelName}";
		validateControlPanelBusObject(path);
	}
}

bool ControlPanelTestSuite::stringMatchesPattern(const string& t_PathPattern, const string& t_Path)
{
	regex pathRegex(t_PathPattern, regex::extended);
	smatch pathMatchResult;

	return regex_match(t_Path, pathMatchResult, pathRegex);
}

void ControlPanelTestSuite::validateControlPanelBusObject(const string& t_Path)
{
	ProxyBusObject* proxyBusObject = m_BusIntrospector->getProxyBusObject(t_Path);

	uint16_t controlPanelInterfaceVersion;
	QStatus status = getVersionPropertyFromInterface(proxyBusObject,
		CONTROLPANEL_INTERFACE_NAME, controlPanelInterfaceVersion);
	ASSERT_EQ(ER_OK, status) << "Retrieving Version property from ControlPanel interface returned error: "
		<< QCC_StatusText(status);

	EXPECT_EQ(ArrayParser::stringToUint16(m_IxitMap.at("IXITCP_ControlPanelVersion").c_str()),
		controlPanelInterfaceVersion) << "Interface version does not match";

	assertContainerObjectExists(t_Path);
}

QStatus ControlPanelTestSuite::getVersionPropertyFromInterface(ProxyBusObject* t_ProxyBusObject,
	const string& t_Interface, uint16_t& t_Version)
{
	MsgArg propertyMsgArg;
	QStatus status = t_ProxyBusObject->GetProperty(t_Interface.c_str(), "Version", propertyMsgArg);

	if (status != ER_OK)
	{
		return status;
	}
	else
	{
		return propertyMsgArg.Get("q", &t_Version);
	}
}

void ControlPanelTestSuite::assertContainerObjectExists(const string& t_Path)
{
	list<InterfaceDetail> containerInterfaceDetailList = 
		m_BusIntrospector->getInterfacesExposedOnBusUnderSpecifiedPathBasedOnName(t_Path, CONTAINER_INTERFACE_NAME);

	if (containerInterfaceDetailList.empty())
	{
		containerInterfaceDetailList =
			m_BusIntrospector->getInterfacesExposedOnBusUnderSpecifiedPathBasedOnName(t_Path, SECURED_CONTAINER_INTERFACE_NAME);

		EXPECT_FALSE(containerInterfaceDetailList.empty())
			<< "No object implementing " << CONTAINER_INTERFACE_NAME
			<< " nor " << SECURED_CONTAINER_INTERFACE_NAME 
			<< " is under path " << t_Path;
	}
}

TEST_F(ControlPanelTestSuite, ControlPanel_v1_02)
{
	list<InterfaceDetail> containerInterfaceDetailListExposedOnBus;
	list<InterfaceDetail> securedContainerInterfaceDetailListExposedOnBus;

	try
	{
		containerInterfaceDetailListExposedOnBus =
			m_BusIntrospector->getInterfacesExposedOnBusBasedOnName(CONTAINER_INTERFACE_NAME);
		securedContainerInterfaceDetailListExposedOnBus =
			m_BusIntrospector->getInterfacesExposedOnBusBasedOnName(SECURED_CONTAINER_INTERFACE_NAME);
	}
	catch (xercesc::SAXParseException&)
	{
		FAIL() << "Introspecting Container and SecuredContainer interfaces exposed on bus failed";
	}

	ASSERT_FALSE(containerInterfaceDetailListExposedOnBus.empty()
		&& securedContainerInterfaceDetailListExposedOnBus.empty())
		<< "No bus objects implement Container nor SecuredContainer interfaces";

	validateContainerInterfaceDetailList(containerInterfaceDetailListExposedOnBus, false);
	validateContainerInterfaceDetailList(securedContainerInterfaceDetailListExposedOnBus, true);
}

void ControlPanelTestSuite::validateContainerInterfaceDetailList(const list<InterfaceDetail>& t_InterfaceDetailList,
	const bool t_IsSecured)
{
	for (auto interfaceDetail : t_InterfaceDetailList)
	{
		string path = interfaceDetail.getPath();
		LOG(INFO) << "Validating ControlPanel.Container object at: " << path;
		assertValidAncestorIsPresentForContainer(path);
		validateContainerBusObject(path, t_IsSecured);
	}
}

void ControlPanelTestSuite::assertValidAncestorIsPresentForContainer(const std::string& t_Path)
{
	ASSERT_TRUE(m_BusIntrospector->isAncestorInterfacePresent(t_Path, CONTROLPANEL_INTERFACE_NAME)
		|| m_BusIntrospector->isAncestorInterfacePresent(t_Path, NOTIFICATION_ACTION_INTERFACE_NAME)
		|| m_BusIntrospector->isAncestorInterfacePresent(t_Path, CONTAINER_INTERFACE_NAME)
		|| m_BusIntrospector->isAncestorInterfacePresent(t_Path, SECURED_CONTAINER_INTERFACE_NAME)
		/*|| m_BusIntrospector->isAncestorInterfacePresent(t_Path, LIST_PROPERTY_INTERFACE_NAME)
		|| m_BusIntrospector->isAncestorInterfacePresent(t_Path, SECURED_LIST_PROPERTY_INTERFACE_NAME)*/)
		<< "No parent bus object that implements ControlPanel nor NotificationAction nor Container nor SecuredContainer nor ListProperty nor SecuredListProperty interface found";
}

void ControlPanelTestSuite::validateContainerBusObject(const std::string& t_Path, const bool t_IsSecured)
{
	ProxyBusObject* proxyBusObject = m_BusIntrospector->getProxyBusObject(t_Path);
	const char* targetInterface = t_IsSecured ? SECURED_CONTAINER_INTERFACE_NAME : CONTAINER_INTERFACE_NAME;

	uint16_t version;
	QStatus status = getVersionPropertyFromInterface(proxyBusObject, targetInterface, version);
	ASSERT_EQ(ER_OK, status) << "Retrieving Version property from Container interface returned status code: "
		<< QCC_StatusText(status);

	EXPECT_EQ(ArrayParser::stringToUint16(m_IxitMap.at("IXITCP_ContainerVersion").c_str()),
		version) << "Interface version does not match";

	uint32_t states;
	status = getStatesPropertyFromInterface(proxyBusObject, targetInterface, states);
	ASSERT_EQ(ER_OK, status) << "Retrieving States property from Container interface returned error: "
		<< QCC_StatusText(status);

	EXPECT_TRUE(states == 0 || states == 1)
		<< "Interface state " << states << " does not equals expected value of 0x00 or 0x01";
	
	MsgArg propertyMsgArg;
	status = proxyBusObject->GetProperty(targetInterface, "OptParams", propertyMsgArg);
	ASSERT_EQ(ER_OK, status) << "Retrieving OptParams property from Container interface returned error: "
		<< QCC_StatusText(status);

	size_t optParamsSize;
	MsgArg* optParams;
	status = propertyMsgArg.Get("a{qv}", &optParamsSize, &optParams);

	validateContainerParameters(optParamsSize, optParams);
}

QStatus ControlPanelTestSuite::getStatesPropertyFromInterface(ProxyBusObject* t_ProxyBusObject,
	const string& t_Interface, uint32_t& t_States)
{
	MsgArg propertyMsgArg;
	QStatus status = t_ProxyBusObject->GetProperty(t_Interface.c_str(), "States", propertyMsgArg);

	if (status != ER_OK)
	{
		return status;
	}
	else
	{
		return propertyMsgArg.Get("u", &t_States);
	}
}

void ControlPanelTestSuite::validateContainerParameters(const size_t t_OptParamsSize,
	const MsgArg* t_OptParams)
{
	validateOptionalParameter0(t_OptParamsSize, t_OptParams);
	validateOptionalParameter1(t_OptParamsSize, t_OptParams);
	validateContainerParameterLayoutHints(t_OptParamsSize, t_OptParams);
}

void ControlPanelTestSuite::validateOptionalParameter0(const size_t t_OptParamsSize, const MsgArg* t_OptParams)
{
	for (size_t i = 0; i < t_OptParamsSize; ++i)
	{
		char* str;
		uint16_t key;
		QStatus status = t_OptParams[i].Get("{qs}", &key, &str);

		if (key == 0)
		{
			ASSERT_NE(ER_BUS_SIGNATURE_MISMATCH, status)
				<< "Signature does not match for key " << key;
			break;
		}
	}
}

void ControlPanelTestSuite::validateOptionalParameter1(const size_t t_OptParamsSize, const MsgArg* t_OptParams)
{
	for (size_t i = 0; i < t_OptParamsSize; ++i)
	{
		uint32_t num;
		uint16_t key;
		QStatus status = t_OptParams[i].Get("{qu}", &key, &num);
		
		if (key == 1)
		{
			ASSERT_NE(ER_BUS_SIGNATURE_MISMATCH, status)
				<< "Signature does not match for key " << key;
			break;
		}
	}
}

void ControlPanelTestSuite::validateContainerParameterLayoutHints(const size_t t_OptParamsSize, const MsgArg* t_OptParams)
{
	for (size_t i = 0; i < t_OptParamsSize; ++i)
	{
		uint16_t* layoutHints;
		size_t layoutHintsSize;
		uint16_t key;

		QStatus status = t_OptParams[i].Get("{qaq}", &key, &layoutHintsSize, &layoutHints);

		if (key == 2)
		{
			ASSERT_NE(ER_BUS_SIGNATURE_MISMATCH, status)
				<< "Signature does not match for key " << key;
			ASSERT_GT(layoutHintsSize, 0) << "Key 2 contains no value";

			for (size_t j = 0; j < layoutHintsSize; ++j)
			{
				EXPECT_TRUE(layoutHints[j] == 1 || layoutHints[j] == 2)
					<< layoutHints[j] << " does not match expected value of 1 or 2 for key 2";
			}

			break;
		}
	}
}

TEST_F(ControlPanelTestSuite, ControlPanel_v1_03)
{
	list<InterfaceDetail> propertyInterfaceDetailListExposedOnBus;
	list<InterfaceDetail> securedPropertyInterfaceDetailListExposedOnBus;

	try
	{
		propertyInterfaceDetailListExposedOnBus =
			m_BusIntrospector->getInterfacesExposedOnBusBasedOnName(PROPERTY_INTERFACE_NAME);
		securedPropertyInterfaceDetailListExposedOnBus =
			m_BusIntrospector->getInterfacesExposedOnBusBasedOnName(SECURED_PROPERTY_INTERFACE_NAME);
	}
	catch (xercesc::SAXParseException&)
	{
		FAIL() << "Introspecting Property and SecuredProperty interfaces exposed on bus failed";
	}

	ASSERT_FALSE(propertyInterfaceDetailListExposedOnBus.empty()
		&& securedPropertyInterfaceDetailListExposedOnBus.empty())
		<< "No bus objects implement Property nor SecuredProperty interfaces";

	validatePropertyInterfaceDetailList(propertyInterfaceDetailListExposedOnBus, false);
	validatePropertyInterfaceDetailList(securedPropertyInterfaceDetailListExposedOnBus, true);
}

void ControlPanelTestSuite::validatePropertyInterfaceDetailList(const list<InterfaceDetail>& t_InterfaceDetailList,
	const bool t_IsSecured)
{
	for (auto interfaceDetail : t_InterfaceDetailList)
	{
		string path = interfaceDetail.getPath();
		LOG(INFO) << "Validating ControlPanel.Property object at: " << path;
		assertAncestorContainerIsPresent(path);
		validatePropertyBusObject(path, t_IsSecured);
	}
}

void ControlPanelTestSuite::assertAncestorContainerIsPresent(const string& t_Path)
{
	ASSERT_TRUE(m_BusIntrospector->isAncestorInterfacePresent(t_Path, CONTAINER_INTERFACE_NAME)
		|| m_BusIntrospector->isAncestorInterfacePresent(t_Path, SECURED_CONTAINER_INTERFACE_NAME))
		<< "No parent bus object that implements Container nor SecuredContainer interface found";
}

void ControlPanelTestSuite::validatePropertyBusObject(const string& t_Path, const bool t_IsSecured)
{
	ProxyBusObject* proxyBusObject = m_BusIntrospector->getProxyBusObject(t_Path);
	const char* targetInterface = t_IsSecured ? SECURED_PROPERTY_INTERFACE_NAME : PROPERTY_INTERFACE_NAME;

	uint16_t version;
	QStatus status = getVersionPropertyFromInterface(proxyBusObject, targetInterface, version);
	ASSERT_EQ(ER_OK, status) << "Retrieving Version property from Property interface returned error: "
		<< QCC_StatusText(status);

	EXPECT_EQ(ArrayParser::stringToUint16(m_IxitMap.at("IXITCP_PropertyVersion").c_str()),
		version) << "Interface version does not match";

	uint32_t states;
	status = getStatesPropertyFromInterface(proxyBusObject, targetInterface, states);
	ASSERT_EQ(ER_OK, status) << "Retrieving States property from Property interface returned error: "
		<< QCC_StatusText(status);

	EXPECT_TRUE(states == 0 || states == 1 || states == 2 || states == 3)
		<< "Interface state " << states << " does not equals expected value of 0x00, 0x01, 0x02 nor 0x03";

	MsgArg propertyMsgArg;
	status = proxyBusObject->GetProperty(targetInterface, "OptParams", propertyMsgArg);
	ASSERT_EQ(ER_OK, status) << "Retrieving OptParams property from Container interface returned error: "
		<< QCC_StatusText(status);

	size_t optParamsSize;
	MsgArg* optParams;
	status = propertyMsgArg.Get("a{qv}", &optParamsSize, &optParams);

	MsgArg values;
	status = proxyBusObject->GetProperty(targetInterface, "Value", values);
	ASSERT_EQ(ER_OK, status) << "Retrieving Version property from Property interface returned error: "
		<< QCC_StatusText(status);

	validatePropertyControlParameters(optParamsSize, optParams, values);
}

void ControlPanelTestSuite::validatePropertyControlParameters(const size_t t_OptParamsSize, const MsgArg* t_OptParams,
	const MsgArg t_PropertyValue)
{
	validateOptionalParameter0(t_OptParamsSize, t_OptParams);
	validateOptionalParameter1(t_OptParamsSize, t_OptParams);
	validatePropertyControlParameterLayoutHints(t_OptParamsSize, t_OptParams, t_PropertyValue);
	validatePropertyControlOptionalParameter3(t_OptParamsSize, t_OptParams);
	validatePropertyControlOptionalParameter4(t_OptParamsSize, t_OptParams, t_PropertyValue);
	validatePropertyControlOptionalParameter5(t_OptParamsSize, t_OptParams, t_PropertyValue);
}

void ControlPanelTestSuite::validatePropertyControlParameterLayoutHints(const size_t t_OptParamsSize, const MsgArg* t_OptParams,
	const MsgArg t_PropertyValue)
{
	for (size_t i = 0; i < t_OptParamsSize; ++i)
	{
		uint16_t key;
		ajn::MsgArg* optParam;

		QStatus status = t_OptParams[i].Get("{qv}", &key, &optParam);

		if (key == 2)
		{
			uint16_t* layoutHints;
			size_t layoutHintsSize;

			ASSERT_NE(ER_BUS_SIGNATURE_MISMATCH, status)
				<< "Signature does not match for key 2";

			status = optParam->Get("aq", &layoutHintsSize, &layoutHints);
			ASSERT_EQ(ER_OK, status) << "Retrieving layout hints failed";

			ASSERT_EQ(layoutHintsSize, 1) << "Key 2 contains more than one value";
			EXPECT_TRUE(layoutHints[0] > 0 && layoutHints[0] < 14)
				<< layoutHints[0] << " is not a valid value for key 2";

			validateBasedOnLayoutHintId(t_OptParamsSize, t_OptParams, layoutHints[0], t_PropertyValue);
			break;
		}
	}
}

void ControlPanelTestSuite::validateBasedOnLayoutHintId(const size_t t_OptParamsSize, const MsgArg* t_OptParams,
	const uint16_t t_LayoutHintId, const MsgArg t_PropertyValue)
{
	if (t_LayoutHintId == 1)
	{
		bool switchStatus;
		ASSERT_EQ(ER_OK, t_PropertyValue.v_variant.val->v_variant.val->Get("b", &switchStatus))
			<< "Signature does not match for property value when hint id is 1";
	}
	else if (t_LayoutHintId == 2 || t_LayoutHintId == 3 || t_LayoutHintId == 4)
	{
		assertOptionalParameter4IsPresent(t_OptParamsSize, t_OptParams, t_LayoutHintId);
	}
	else if (t_LayoutHintId == 6)
	{
		validateDateTimeHint(t_LayoutHintId, t_PropertyValue, 1);
	}
	else if (t_LayoutHintId == 7)
	{
		validateDateTimeHint(t_LayoutHintId, t_PropertyValue, 0);
	}
	else if (t_LayoutHintId == 5 || t_LayoutHintId == 8 || t_LayoutHintId == 9 || t_LayoutHintId == 10 || t_LayoutHintId == 12)
	{
		ajn::MsgArg* value = t_PropertyValue.v_variant.val;
		ASSERT_TRUE(value->v_variant.val->HasSignature("y") || value->v_variant.val->HasSignature("n")
			|| value->v_variant.val->HasSignature("q") || value->v_variant.val->HasSignature("i")
			|| value->v_variant.val->HasSignature("u") || value->v_variant.val->HasSignature("x")
			|| value->v_variant.val->HasSignature("t") || value->v_variant.val->HasSignature("d"))
			<< "Property Value type needs to be a numeric when hint id is " << t_LayoutHintId
			<< ". Found signature: " << value->v_variant.val->Signature();
	}
	else if (t_LayoutHintId == 11 || t_LayoutHintId == 13)
	{
		ASSERT_TRUE(t_PropertyValue.v_variant.val->v_variant.val->HasSignature("s"))
			<< "Signature does not match for property Value when hint id is " << t_LayoutHintId;
	}
}

void ControlPanelTestSuite::assertOptionalParameter4IsPresent(const size_t t_OptParamsSize, const MsgArg* t_OptParams,
	const uint16_t t_LayoutHintId)
{
	for (size_t i = 0; i < t_OptParamsSize; ++i)
	{
		uint16_t key;
		ajn::MsgArg* optParam;

		QStatus status = t_OptParams[i].Get("{qv}", &key, &optParam);

		if (key == 4)
		{
			size_t size;

			struct
			{
				MsgArg* value;
				char* label;
			} constrainToValue;

			status = optParam->Get("a(vs)", &size, &constrainToValue.value, &constrainToValue.label);
			ASSERT_EQ(ER_OK, status) << "Signature does not match for key 4";

			ASSERT_GT(size, 0) << "constrainToValue array cannot be empty";
			ASSERT_NE(constrainToValue.value, nullptr) << "Value field from constrainToValue array cannot be empty";
			break;
		}
	}
}

void ControlPanelTestSuite::validateDateTimeHint(const uint16_t t_LayoutHintId,
	const ajn::MsgArg t_PropertyValue, const uint16_t t_ComposyteType)
{
	ASSERT_TRUE(t_PropertyValue.v_variant.val->v_variant.val->HasSignature("q(qqq)"))
		<< "Signature does not match for property Value when hint id is " << t_LayoutHintId;

	uint16_t key;

	if (t_ComposyteType == 0)
	{
		struct {
			uint16_t mday;
			uint16_t month;
			uint16_t fullyear;
		} date;

		t_PropertyValue.v_variant.val->v_variant.val->Get("q(qqq)", &key, &date.mday, &date.month, &date.fullyear);
	}
	else
	{
		struct {
			uint16_t hour;
			uint16_t minute;
			uint16_t second;
		} time;

		t_PropertyValue.v_variant.val->v_variant.val->Get("q(qqq)", &key, &time.hour, &time.minute, &time.second);
	}

	ASSERT_EQ(t_ComposyteType, key) << "The first value in the composite type does not match when hint id is " << t_LayoutHintId;
}

void ControlPanelTestSuite::validatePropertyControlOptionalParameter3(const size_t t_OptParamsSize,
	const MsgArg* t_OptParams)
{
	for (size_t i = 0; i < t_OptParamsSize; ++i)
	{
		char* value;
		uint16_t key;

		QStatus status = t_OptParams[i].Get("{qs}", &key, &value);

		if (key == 3)
		{
			ASSERT_EQ(ER_OK, status) << "Signature does not match for key 3";
			break;
		}
	}
}

void ControlPanelTestSuite::validatePropertyControlOptionalParameter4(const size_t t_OptParamsSize,
	const MsgArg* t_OptParams, const MsgArg t_PropertyValue)
{
	for (size_t i = 0; i < t_OptParamsSize; ++i)
	{
		if (t_OptParams[i].HasSignature("{qa(vs)}"))
		{
			struct {
				MsgArg* value;
				char* label;
			} constraintToValue;
			size_t size;
			uint16_t key;

			if (t_OptParams[i].Get("{qa(vs)}", &key, &size, &constraintToValue.value, &constraintToValue.label) == ER_OK)
			{
				ASSERT_EQ(key, 4) << "Signature does not match for key 4";

				for (size_t j = 0; j < size; ++j)
				{
					ASSERT_EQ(constraintToValue.value[j].Signature(), t_PropertyValue.v_variant.val->v_variant.val->Signature())
						<< "Signature does not match for value variant instance in value for key 4";
				}
				break;
			}
		}
	}
}

void ControlPanelTestSuite::validatePropertyControlOptionalParameter5(const size_t t_OptParamsSize,
	const MsgArg* t_OptParams, const MsgArg t_PropertyValue)
{
	for (size_t i = 0; i < t_OptParamsSize; ++i)
	{
		struct {
			MsgArg* min;
			MsgArg* max;
			MsgArg* increment;
		} range;

		uint16_t key;

		if (t_OptParams[i].Get("{q(vvv)}", &key, &range.min, &range.max, &range.increment) == ER_OK)
		{
			ASSERT_EQ(key, 5) << "Signature does not match for key 5";
			ASSERT_EQ(range.min->Signature(), t_PropertyValue.v_variant.val->v_variant.val->Signature())
				<< "Signature does not match for min Variant instance in value for key 5";
			ASSERT_EQ(range.max->Signature(), t_PropertyValue.v_variant.val->v_variant.val->Signature())
				<< "Signature does not match for max Variant instance in value for key 5";
			ASSERT_EQ(range.increment->Signature(), t_PropertyValue.v_variant.val->v_variant.val->Signature())
				<< "Signature does not match for increment Variant instance in value for key 5";
			break;
		}
	}
}

TEST_F(ControlPanelTestSuite, ControlPanel_v1_04)
{
	list<InterfaceDetail> labelPropertyInterfaceDetailListExposedOnBus;

	try
	{
		labelPropertyInterfaceDetailListExposedOnBus =
			m_BusIntrospector->getInterfacesExposedOnBusBasedOnName(LABEL_PROPERTY_INTERFACE_NAME);
	}
	catch (xercesc::SAXParseException&)
	{
		FAIL() << "Introspecting LabelProperty interface exposed on bus failed";
	}

	ASSERT_FALSE(labelPropertyInterfaceDetailListExposedOnBus.empty())
		<< "No bus objects implement LabelProperty interface";

	validateLabelPropertyInterfaceDetailList(labelPropertyInterfaceDetailListExposedOnBus);
}

void ControlPanelTestSuite::validateLabelPropertyInterfaceDetailList(const std::list<InterfaceDetail>& t_InterfaceDetailList)
{
	for (auto interfaceDetail : t_InterfaceDetailList)
	{
		string path = interfaceDetail.getPath();
		LOG(INFO) << "Validating ControlPanel.LabelProperty object at: " << path;
		assertAncestorContainerIsPresent(path);
		validateLabelPropertyBusObject(path);
	}
}

void ControlPanelTestSuite::validateLabelPropertyBusObject(const string& t_Path)
{
	ProxyBusObject* proxyBusObject = m_BusIntrospector->getProxyBusObject(t_Path);

	uint16_t version;
	QStatus status = getVersionPropertyFromInterface(proxyBusObject, LABEL_PROPERTY_INTERFACE_NAME, version);
	ASSERT_EQ(ER_OK, status) << "Retrieving Version property from LabelProperty interface returned error: "
		<< QCC_StatusText(status);

	EXPECT_EQ(ArrayParser::stringToUint16(m_IxitMap.at("IXITCP_LabelPropertyVersion").c_str()), version)
		<< "Interface version does not match";

	uint32_t states;
	status = getStatesPropertyFromInterface(proxyBusObject, LABEL_PROPERTY_INTERFACE_NAME, states);
	ASSERT_EQ(ER_OK, status) << "Retrieving States property from LabelProperty interface returned error: "
		<< QCC_StatusText(status);

	ASSERT_TRUE(states == 0 || states == 1)
		<< "Interface state " << states << "does not equal expected value of 0x00 or 0x01";

	MsgArg labelMsgArg;
	proxyBusObject->GetProperty(LABEL_PROPERTY_INTERFACE_NAME, "Label", labelMsgArg);

	const char* label;
	ASSERT_EQ(ER_OK, labelMsgArg.Get("s", &label))
		<< "Label property must be a string";

	MsgArg optParamsMsgArg;
	proxyBusObject->GetProperty(LABEL_PROPERTY_INTERFACE_NAME, "OptParams", optParamsMsgArg);

	size_t optParamsSize;
	MsgArg* optParams;
	optParamsMsgArg.Get("a{qv}", &optParamsSize, &optParams);

	validateLabelPropertyParameters(optParamsSize, optParams);
}

void ControlPanelTestSuite::validateLabelPropertyParameters(const size_t t_OptParamsSize, const MsgArg* t_OptParams)
{
	validateOptionalParameter1(t_OptParamsSize, t_OptParams);
	validateParameterLayoutHints(t_OptParamsSize, t_OptParams);
}

void ControlPanelTestSuite::validateParameterLayoutHints(const size_t t_OptParamsSize, const MsgArg* t_OptParams)
{
	for (size_t i = 0; i < t_OptParamsSize; ++i)
	{
		uint16_t* layoutHints;
		size_t layoutHintsSize;
		uint16_t key;

		QStatus status = t_OptParams[i].Get("{qaq}", &key, &layoutHintsSize, &layoutHints);

		if (status == ER_OK)
		{
			ASSERT_EQ(key, 2) << "Signature does not match for key 2";
			ASSERT_GT(layoutHintsSize, 0) << "Key 2 contains no value";

			for (size_t j = 0; j < layoutHintsSize; ++j)
			{
				EXPECT_TRUE(layoutHints[j] == 1)
					<< layoutHints[j] << " does not match expected value of 1 for key 2";
			}

			break;
		}
	}
}

TEST_F(ControlPanelTestSuite, ControlPanel_v1_05)
{
	list<InterfaceDetail> actionInterfaceDetailListExposedOnBus;
	list<InterfaceDetail> securedActionInterfaceDetailListExposedOnBus;

	try
	{
		actionInterfaceDetailListExposedOnBus =
			m_BusIntrospector->getInterfacesExposedOnBusBasedOnName(ACTION_INTERFACE_NAME);
		securedActionInterfaceDetailListExposedOnBus =
			m_BusIntrospector->getInterfacesExposedOnBusBasedOnName(SECURED_ACTION_INTERFACE_NAME);
	}
	catch (xercesc::SAXParseException&)
	{
		FAIL() << "Introspecting Action and SecuredAction interfaces exposed on bus failed";
	}

	ASSERT_FALSE(actionInterfaceDetailListExposedOnBus.empty()
		&& securedActionInterfaceDetailListExposedOnBus.empty())
		<< "No bus objects implement Action nor SecuredAction interface";

	validateActionInterfaceDetailList(actionInterfaceDetailListExposedOnBus, false);
	validateActionInterfaceDetailList(securedActionInterfaceDetailListExposedOnBus, true);
}

void ControlPanelTestSuite::validateActionInterfaceDetailList(const std::list<InterfaceDetail>& t_InterfaceDetailList,
	const bool t_IsSecured)
{
	for (auto interfaceDetail : t_InterfaceDetailList)
	{
		string path = interfaceDetail.getPath();
		LOG(INFO) << "Validating ControlPanel.Action object at: " << path;
		assertAncestorContainerIsPresent(path);
		validateActionBusObject(path, t_IsSecured);
	}
}

void ControlPanelTestSuite::validateActionBusObject(const string& t_Path, const bool t_IsSecured)
{
	ProxyBusObject* proxyBusObject = m_BusIntrospector->getProxyBusObject(t_Path);
	const char* targetInterface = t_IsSecured ? SECURED_ACTION_INTERFACE_NAME : ACTION_INTERFACE_NAME;

	uint16_t version;
	QStatus status = getVersionPropertyFromInterface(proxyBusObject, targetInterface, version);
	ASSERT_EQ(ER_OK, status) << "Retrieving Version property from Action interface returned error: "
		<< QCC_StatusText(status);

	EXPECT_EQ(ArrayParser::stringToUint16(m_IxitMap.at("IXITCP_ActionVersion").c_str()),
		version) << "Interface version does not match";

	uint32_t states;
	status = getStatesPropertyFromInterface(proxyBusObject, targetInterface, states);
	ASSERT_EQ(ER_OK, status) << "Retrieving States property from Action interface returned error: "
		<< QCC_StatusText(status);

	EXPECT_TRUE(states == 0 || states == 1)
		<< "Interface state " << states << " does not equal expected value of 0x00 or 0x01";

	MsgArg optParamsMsgArg;
	proxyBusObject->GetProperty(targetInterface, "OptParams", optParamsMsgArg);

	size_t optParamsSize;
	MsgArg* optParams;
	optParamsMsgArg.Get("a{qv}", &optParamsSize, &optParams);

	validateActionParameters(optParamsSize, optParams);
}

void ControlPanelTestSuite::validateActionParameters(const size_t t_OptParamsSize, const MsgArg* t_OptParams)
{
	validateOptionalParameter0(t_OptParamsSize, t_OptParams);
	validateOptionalParameter1(t_OptParamsSize, t_OptParams);
	validateParameterLayoutHints(t_OptParamsSize, t_OptParams);
}

TEST_F(ControlPanelTestSuite, ControlPanel_v1_06)
{
	list<InterfaceDetail> dialogInterfaceDetailListExposedOnBus;
	list<InterfaceDetail> securedDialogInterfaceDetailListExposedOnBus;

	try
	{
		dialogInterfaceDetailListExposedOnBus =
			m_BusIntrospector->getInterfacesExposedOnBusBasedOnName(DIALOG_INTERFACE_NAME);
		securedDialogInterfaceDetailListExposedOnBus =
			m_BusIntrospector->getInterfacesExposedOnBusBasedOnName(SECURED_DIALOG_INTERFACE_NAME);
	}
	catch (xercesc::SAXParseException&)
	{
		FAIL() << "Introspecting Dialog and SecuredDialog interfaces exposed on bus failed";
	}

	ASSERT_FALSE(dialogInterfaceDetailListExposedOnBus.empty()
		&& securedDialogInterfaceDetailListExposedOnBus.empty())
		<< "No bus objects implement Dialog nor SecuredDialog interfaces";

	validateDialogInterfaceDetailList(dialogInterfaceDetailListExposedOnBus, false);
	validateDialogInterfaceDetailList(securedDialogInterfaceDetailListExposedOnBus, true);
}

void ControlPanelTestSuite::validateDialogInterfaceDetailList(const list<InterfaceDetail>& t_InterfaceDetailList,
	const bool t_IsSecured)
{
	for (auto interfaceDetail : t_InterfaceDetailList)
	{
		string path = interfaceDetail.getPath();
		LOG(INFO) << "Validating ControlPanel.Dialog object at: " << path;
		assertValidAncestorIsPresentForDialog(path);
		validateDialogBusObject(path, t_IsSecured);
	}
}

void ControlPanelTestSuite::assertValidAncestorIsPresentForDialog(const string& t_Path)
{
	ASSERT_TRUE(m_BusIntrospector->isAncestorInterfacePresent(t_Path, ACTION_INTERFACE_NAME)
		|| m_BusIntrospector->isAncestorInterfacePresent(t_Path, SECURED_ACTION_INTERFACE_NAME)
		|| m_BusIntrospector->isAncestorInterfacePresent(t_Path, NOTIFICATION_ACTION_INTERFACE_NAME))
		<< "No parent bus object that implements Action nor SecuredAction nor NotificationAction interface found";
}

void ControlPanelTestSuite::validateDialogBusObject(const string& t_Path, const bool t_IsSecured)
{
	ProxyBusObject* proxyBusObject = m_BusIntrospector->getProxyBusObject(t_Path);
	const char* targetInterface = t_IsSecured ? SECURED_DIALOG_INTERFACE_NAME : DIALOG_INTERFACE_NAME;

	uint16_t version;
	QStatus status = getVersionPropertyFromInterface(proxyBusObject, targetInterface, version);
	ASSERT_EQ(ER_OK, status) << "Retrieving Version property from Dialog interface returned error: "
		<< QCC_StatusText(status);

	EXPECT_EQ(ArrayParser::stringToUint16(m_IxitMap.at("IXITCP_DialogVersion").c_str()),
		version) << "Interface version does not match";

	uint32_t states;
	status = getStatesPropertyFromInterface(proxyBusObject, targetInterface, states);
	ASSERT_EQ(ER_OK, status) << "Retrieving States property from Dialog interface returned error: "
		<< QCC_StatusText(status);

	EXPECT_TRUE(states == 0 || states == 1) << "Interface state " << states << " does not equal expected value of 0x00 or 0x01";

	MsgArg numActionsMsgArg;
	proxyBusObject->GetProperty(targetInterface, "NumActions", numActionsMsgArg);

	uint16_t numActions;
	numActionsMsgArg.Get("q", &numActions);

	MsgArg optParamsMsgArg;
	proxyBusObject->GetProperty(targetInterface, "OptParams", optParamsMsgArg);

	size_t optParamsSize;
	MsgArg* optParams;
	optParamsMsgArg.Get("a{qv}", &optParamsSize, &optParams);

	validateDialogParameters(optParamsSize, optParams, numActions);
	
	MsgArg messageMsgArg;
	proxyBusObject->GetProperty(targetInterface, "Message", messageMsgArg);

	uint16_t message;
	messageMsgArg.Get("q", &message);

	LOG(INFO) << "Dialog message: " << message;
	
	validateDialogActions(proxyBusObject, targetInterface, numActions);
}

void ControlPanelTestSuite::validateDialogParameters(const size_t t_OptParamsSize,
	const MsgArg* t_OptParams, const uint16_t t_NumActions)
{
	validateOptionalParameter0(t_OptParamsSize, t_OptParams);
	validateOptionalParameter1(t_OptParamsSize, t_OptParams);
	validateParameterLayoutHints(t_OptParamsSize, t_OptParams);
	validateDialogParameterActionLabelIds(t_OptParamsSize, t_OptParams, t_NumActions);
}

void ControlPanelTestSuite::validateDialogParameterActionLabelIds(const size_t t_OptParamsSize,
	const ajn::MsgArg* t_OptParams, const uint16_t t_NumActions)
{
	for (size_t i = 0; i < t_OptParamsSize; ++i)
	{
		char* labelAction;
		uint16_t key;

		QStatus status = t_OptParams[i].Get("{qs}", &key, &labelAction);

		if (status == ER_OK)
		{
			if ((key == 6) || (key == 7 && t_NumActions >= 2) || (key == 8 && t_NumActions == 3))
			{
				ASSERT_NE(labelAction, nullptr) << "Key " << key << " is missing";
			}
		}
	}
}

void ControlPanelTestSuite::validateDialogActions(ProxyBusObject* proxyBusObject,
	const string& t_Interface, const uint16_t t_NumActions)
{
	if (t_NumActions == 1)
	{
		validateInvokingDialogActionReturnsErrorStatus(proxyBusObject, t_Interface, "Action2");
		validateInvokingDialogActionReturnsErrorStatus(proxyBusObject, t_Interface, "Action3");
	}
	else if (t_NumActions == 2)
	{
		validateInvokingDialogActionReturnsErrorStatus(proxyBusObject, t_Interface, "Action3");
	}
}

void ControlPanelTestSuite::validateInvokingDialogActionReturnsErrorStatus(ProxyBusObject* proxyBusObject,
	const string& t_Interface, const char* t_Method)
{
	Message message(*m_ServiceHelper->getBusAttachmentMgr()->getBusAttachment());
	QStatus status = proxyBusObject->MethodCall(t_Interface.c_str(), t_Method, NULL, 0, message);
	ASSERT_NE(ER_OK, status) << "Invoking " << t_Method << "() must return error status";
	ASSERT_EQ(ER_BUS_REPLY_IS_ERROR_MESSAGE, status) << "Invoking " << t_Method << "() must return ER_BUS_REPLY_IS_ERROR_MESSAGE status";
}

/*TEST_F(ControlPanelTestSuite, ControlPanel_v1_07)
{
	list<InterfaceDetail> listPropertyInterfaceDetailExposedOnBus;
	list<InterfaceDetail> securedListPropertyInterfaceDetailExposedOnBus;

	try
	{
		listPropertyInterfaceDetailExposedOnBus =
			m_BusIntrospector->getInterfacesExposedOnBusBasedOnName(LIST_PROPERTY_INTERFACE_NAME);
		securedListPropertyInterfaceDetailExposedOnBus =
			m_BusIntrospector->getInterfacesExposedOnBusBasedOnName(SECURED_LIST_PROPERTY_INTERFACE_NAME);
	}
	catch (xercesc::SAXParseException&)
	{
		FAIL() << "Introspecting ListProperty and SecuredListProperty interfaces exposed on bus failed";
	}

	ASSERT_FALSE(listPropertyInterfaceDetailExposedOnBus.empty()
		&& securedListPropertyInterfaceDetailExposedOnBus.empty())
		<< "No bus objects implement ListProperty nor SecuredListProperty interfaces";

	validateListPropertyInterfaceDetailList(listPropertyInterfaceDetailExposedOnBus, false);
	validateListPropertyInterfaceDetailList(securedListPropertyInterfaceDetailExposedOnBus, true);
}

void ControlPanelTestSuite::validateListPropertyInterfaceDetailList(const list<InterfaceDetail>& t_InterfaceDetailList,
	const bool t_IsSecured)
{
	for (auto interfaceDetail : t_InterfaceDetailList)
	{
		string path = interfaceDetail.getPath();
		LOG(INFO) << "Validating ControlPanel.ListProperty object at: " << path;
		assertAncestorContainerIsPresent(path);
		validateListPropertyBusObject(path, t_IsSecured);
	}
}

void ControlPanelTestSuite::validateListPropertyBusObject(const string& t_Path, const bool t_IsSecured)
{
	ProxyBusObject* proxyBusObject = m_BusIntrospector->getProxyBusObject(t_Path);
	const char* targetInterface = t_IsSecured ? SECURED_LIST_PROPERTY_INTERFACE_NAME : LIST_PROPERTY_INTERFACE_NAME;

	uint16_t version;
	QStatus status = getVersionPropertyFromInterface(proxyBusObject, targetInterface, version);
	ASSERT_EQ(ER_OK, status) << "Retrieving Version property from ListProperty interface returned error: "
		<< QCC_StatusText(status);

	EXPECT_EQ(ArrayParser::stringToUint16(m_IxitMap.at("IXITCP_ListPropertyVersion").c_str()),
		version)
		<< "Interface version does not match";

	uint32_t states;
	status = getStatesPropertyFromInterface(proxyBusObject, targetInterface, states);
	ASSERT_EQ(ER_OK, status) << "Retrieving States property from Dialog interface returned error: "
		<< QCC_StatusText(status);

	EXPECT_TRUE(states == 0 || states == 1)
		<< "Interface state " << states << " does not equal expected value of 0x00 or 0x01";

	MsgArg optParamsMsgArg;
	proxyBusObject->GetProperty(targetInterface, "OptParams", optParamsMsgArg);

	size_t optParamsSize;
	MsgArg* optParams;
	optParamsMsgArg.Get("a{qv}", &optParamsSize, &optParams);

	validateListPropertyParameters(optParamsSize, optParams);
	
	MsgArg valueMsgArg;
	proxyBusObject->GetProperty(targetInterface, "Value", valueMsgArg);

	size_t valueSize;
	MsgArg* value;
	valueMsgArg.Get("a{qs}", &valueSize, &value);

	validateListPropertyValues(valueSize, value);
}

void ControlPanelTestSuite::validateListPropertyParameters(const size_t t_OptParamsSize, const MsgArg* t_OptParams)
{
	validateOptionalParameter0(t_OptParamsSize, t_OptParams);
	validateOptionalParameter1(t_OptParamsSize, t_OptParams);
	validateParameterLayoutHints(t_OptParamsSize, t_OptParams);
}

void ControlPanelTestSuite::validateListPropertyValues(const size_t t_ValueSize, const MsgArg* t_Value)
{
	ASSERT_GT(t_ValueSize, 0) << "Values array cannot be empty";

	set<uint16_t> recordIDs;

	for (size_t i = 0; i < t_ValueSize; ++i)
	{
		uint16_t recordID;
		char* label;

		ASSERT_EQ(ER_OK, t_Value[i].Get("{qs}", &recordID, &label));
		ASSERT_NE(label, nullptr) << "Label cannot be null";
		ASSERT_FALSE(strlen(label) == 0) << "Label cannot be empty";
		recordIDs.insert(recordID);
	}

	ASSERT_EQ(t_ValueSize, recordIDs.size()) << "Record IDs need to be unique";
}*/

TEST_F(ControlPanelTestSuite, ControlPanel_v1_08)
{
	list<InterfaceDetail> notificationActionInterfaceDetailListExposedOnBus;

	try
	{
		notificationActionInterfaceDetailListExposedOnBus =
			m_BusIntrospector->getInterfacesExposedOnBusBasedOnName(NOTIFICATION_ACTION_INTERFACE_NAME);
	}
	catch (xercesc::SAXParseException&)
	{
		FAIL() << "Introspecting NotificationAction interface exposed on bus failed";
	}

	ASSERT_FALSE(notificationActionInterfaceDetailListExposedOnBus.empty())
		<< "No bus objects implement NotificationAction interface";

	validateNotificationActionInterfaceDetailList(notificationActionInterfaceDetailListExposedOnBus);
}

void ControlPanelTestSuite::validateNotificationActionInterfaceDetailList(const list<InterfaceDetail>& t_InterfaceDetailList)
{
	for (auto interfaceDetail : t_InterfaceDetailList)
	{
		string path = interfaceDetail.getPath();
		LOG(INFO) << "Validating ControlPanel.ListProperty object at: " << path;
		
		EXPECT_TRUE(stringMatchesPattern(CONTROLPANEL_PATH_PATTERN, path))
			<< path << " does not match te expected pattern /ControlPanel/{unit}/{panelName}";
		validateNotificationActionBusObject(path);
	}
}

void ControlPanelTestSuite::validateNotificationActionBusObject(const string& t_Path)
{
	ProxyBusObject* proxyBusObject = m_BusIntrospector->getProxyBusObject(t_Path);

	uint16_t version;
	QStatus status = getVersionPropertyFromInterface(proxyBusObject, NOTIFICATION_ACTION_INTERFACE_NAME, version);
	ASSERT_EQ(ER_OK, status) << "Retrieving Version property from Dialog interface returned error: "
		<< QCC_StatusText(status);

	EXPECT_EQ(ArrayParser::stringToUint16(m_IxitMap.at("IXITCP_NotificationActionVersion").c_str()),
		version) << "Interface version does not match";

	assertContainerOrDialogObjectExists(t_Path);
}

void ControlPanelTestSuite::assertContainerOrDialogObjectExists(const string& t_Path)
{
	list<InterfaceDetail> containerInterfaceDetailList =
		m_BusIntrospector->getInterfacesExposedOnBusUnderSpecifiedPathBasedOnName(t_Path, CONTAINER_INTERFACE_NAME);
	list<InterfaceDetail> securedContainerInterfaceDetailList =
		m_BusIntrospector->getInterfacesExposedOnBusUnderSpecifiedPathBasedOnName(t_Path, SECURED_CONTAINER_INTERFACE_NAME);
	list<InterfaceDetail> dialogInterfaceDetailList =
		m_BusIntrospector->getInterfacesExposedOnBusUnderSpecifiedPathBasedOnName(t_Path, DIALOG_INTERFACE_NAME);
	list<InterfaceDetail> securedDialogInterfaceDetailList =
		m_BusIntrospector->getInterfacesExposedOnBusUnderSpecifiedPathBasedOnName(t_Path, SECURED_DIALOG_INTERFACE_NAME);

	ASSERT_FALSE(containerInterfaceDetailList.empty()
		&& securedContainerInterfaceDetailList.empty()
		&& dialogInterfaceDetailList.empty()
		&& securedDialogInterfaceDetailList.empty())
		<< "No object implementing org.alljoyn.ControlPanel.Container nor org.alljoyn.ControlPanel.SecuredContainer nor org.alljoyn.ControlPanel.Dialog nor org.alljoyn.ControlPanel.SecuredDialog is found under path " << t_Path;
}

TEST_F(ControlPanelTestSuite, ControlPanel_v1_09)
{
	list<InterfaceDetail> httpControlInterfaceDetailListExposedOnBus;

	try
	{
		httpControlInterfaceDetailListExposedOnBus =
			m_BusIntrospector->getInterfacesExposedOnBusBasedOnName(HTTP_CONTROL_INTERFACE_NAME);
	}
	catch (xercesc::SAXParseException&)
	{
		FAIL() << "Introspecting HTTPControl interface exposed on bus failed";
	}

	ASSERT_FALSE(httpControlInterfaceDetailListExposedOnBus.empty())
		<< "No bus objects implement HttpControl interface";

	validateHttpControlInterfaceDetailList(httpControlInterfaceDetailListExposedOnBus);
}

void ControlPanelTestSuite::validateHttpControlInterfaceDetailList(const list<InterfaceDetail>& t_InterfaceDetailList)
{
	for (auto interfaceDetail : t_InterfaceDetailList)
	{
		string path = interfaceDetail.getPath();
		LOG(INFO) << "Validating ControlPanel.ListProperty object at: " << path;

		EXPECT_TRUE(stringMatchesPattern(HTTPCONTROL_PATH_PATTERN, path))
			<< path << " does not match te expected pattern /ControlPanel/{unit}/{panelName}";
		validateHttpControlBusObject(path);
	}
}

void ControlPanelTestSuite::validateHttpControlBusObject(const string& t_Path)
{
	ProxyBusObject* proxyBusObject = m_BusIntrospector->getProxyBusObject(t_Path);

	uint16_t version;
	QStatus status = getVersionPropertyFromInterface(proxyBusObject, HTTP_CONTROL_INTERFACE_NAME, version);
	ASSERT_EQ(ER_OK, status) << "Retrieving Version property from HttpControl interface returned error: "
		<< QCC_StatusText(status);

	EXPECT_EQ(ArrayParser::stringToUint16(m_IxitMap.at("IXITCP_HttpControlVersion").c_str()),
		version) << "Interface version does not match";

	validateRootUrl(proxyBusObject);
}

void ControlPanelTestSuite::validateRootUrl(ProxyBusObject* t_proxyBusObject)
{
	Message replyMessage(*m_ServiceHelper->getBusAttachmentMgr()->getBusAttachment());
	t_proxyBusObject->MethodCall(HTTP_CONTROL_INTERFACE_NAME, "GetRootUrl", NULL, 0, replyMessage);

	char* url = nullptr;
	replyMessage->GetArg(0)->Get("s", &url);

	ASSERT_NE(url, nullptr) << "Root URL returned is null";
	ASSERT_TRUE(stringMatchesPattern(URL_REGEX, url)) << "Malformed URL";
	// Check that connects
}

TEST_F(ControlPanelTestSuite, ControlPanel_v1_10)
{
	list<InterfaceDetail> securedContainerInterfaceDetailListExposedOnBus;
	list<InterfaceDetail> securedPropertyInterfaceDetailListExposedOnBus;
	list<InterfaceDetail> securedActionInterfaceDetailListExposedOnBus;
	list<InterfaceDetail> securedDialogInterfaceDetailListExposedOnBus;
	//list<InterfaceDetail> securedListInterfaceDetailListExposedOnBus;

	try
	{
		securedContainerInterfaceDetailListExposedOnBus =
			m_BusIntrospector->getInterfacesExposedOnBusBasedOnName(SECURED_CONTAINER_INTERFACE_NAME);
		securedPropertyInterfaceDetailListExposedOnBus =
			m_BusIntrospector->getInterfacesExposedOnBusBasedOnName(SECURED_PROPERTY_INTERFACE_NAME);
		securedActionInterfaceDetailListExposedOnBus =
			m_BusIntrospector->getInterfacesExposedOnBusBasedOnName(SECURED_ACTION_INTERFACE_NAME);
		securedDialogInterfaceDetailListExposedOnBus =
			m_BusIntrospector->getInterfacesExposedOnBusBasedOnName(SECURED_DIALOG_INTERFACE_NAME);
		/*securedListInterfaceDetailListExposedOnBus =
			m_BusIntrospector->getInterfacesExposedOnBusBasedOnName(SECURED_LIST_PROPERTY_INTERFACE_NAME);*/
	}
	catch (xercesc::SAXParseException&)
	{
		FAIL() << "Introspecting secured interfaces exposed on bus failed";
	}

	ASSERT_FALSE(securedContainerInterfaceDetailListExposedOnBus.empty()
		&& securedPropertyInterfaceDetailListExposedOnBus.empty()
		&& securedActionInterfaceDetailListExposedOnBus.empty()
		&& securedDialogInterfaceDetailListExposedOnBus.empty()
		/*&& securedListInterfaceDetailListExposedOnBus.empty()*/)
		<< "No bus objects implement one of the secured ControlPanel interfaces";

	validateSecuredContainerInvalidPasscodeInterfaceDetailList(securedContainerInterfaceDetailListExposedOnBus);
	validateSecuredPropertyInvalidPasscodeInterfaceDetailList(securedPropertyInterfaceDetailListExposedOnBus);
	validateSecuredActionInvalidPasscodeInterfaceDetailList(securedActionInterfaceDetailListExposedOnBus);
	validateSecuredDialogInvalidPasscodeInterfaceDetailList(securedDialogInterfaceDetailListExposedOnBus);
	//validateSecuredListPropertyInvalidPasscodeInterfaceDetailList(securedListInterfaceDetailListExposedOnBus);
}

void ControlPanelTestSuite::validateSecuredContainerInvalidPasscodeInterfaceDetailList(const list<InterfaceDetail>& t_InterfaceDetailList)
{
	for (auto interfaceDetail : t_InterfaceDetailList)
	{
		string path = interfaceDetail.getPath();
		validateSecuredInterfaceInvalidPasscodeBusObject(path, SECURED_CONTAINER_INTERFACE_NAME);
	}
}

void ControlPanelTestSuite::validateSecuredInterfaceInvalidPasscodeBusObject(const string& t_Path, const char* t_Interface)
{
	ProxyBusObject* proxyBusObject = m_BusIntrospector->getProxyBusObject(t_Path);
	setInvalidAuthentication();

	/*const char* currentPasscode = m_ServiceHelper->getSrpKeyXPincode(*m_DeviceAboutAnnouncement);
	LOG(INFO) << "Connecting with invalid password: " << currentPasscode;*/
	LOG(INFO) << "Connecting with invalid credentials";

	uint16_t version;
	QStatus status = getVersionPropertyFromInterface(proxyBusObject, t_Interface,
		version);

	ASSERT_EQ(ER_AUTH_FAIL, status)
		<< "ER_AUTH_FAIL status should be returned on connecting with the wrong password to retrieve "
		<< t_Interface << " version";
}

void ControlPanelTestSuite::setInvalidAuthentication()
{
	m_ServiceHelper->clearPeerAuthenticationFlags(*m_DeviceAboutAnnouncement);

	if (m_IcsMap.at("ICSCO_SrpKeyX"))
	{
		m_ServiceHelper->setSrpKeyXPincode(*m_DeviceAboutAnnouncement, m_IxitMap.at("IXITCO_SrpKeyXWrongPincode").c_str());
	}

	if (m_IcsMap.at("ICSCO_SrpLogon"))
	{
		m_ServiceHelper->setSrpLogonPass(*m_DeviceAboutAnnouncement, m_IxitMap.at("IXITCO_SrpLogonWrongPass").c_str());
	}

	if (m_IcsMap.at("ICSCO_EcdhePsk"))
	{
		m_ServiceHelper->setEcdhePskPassword(*m_DeviceAboutAnnouncement, m_IxitMap.at("IXITCO_EcdhePskWrongPassword").c_str());
	}

	if (m_IcsMap.at("ICSCO_EcdheEcdsa"))
	{
		m_ServiceHelper->setEcdheEcdsaCredentials(*m_DeviceAboutAnnouncement, 
			m_IxitMap.at("IXITCO_EcdheEcdsaWrongPrivateKey").c_str(), m_IxitMap.at("IXITCO_EcdheEcdsaWrongCertChain").c_str());
	}

	if (m_IcsMap.at("ICSCO_EcdheSpeke"))
	{
		m_ServiceHelper->setEcdheSpekePassword(*m_DeviceAboutAnnouncement, m_IxitMap.at("IXITCO_EcdheSpekeWrongPassword").c_str());
	}
}

void ControlPanelTestSuite::validateSecuredPropertyInvalidPasscodeInterfaceDetailList(const list<InterfaceDetail>& t_InterfaceDetailList)
{
	for (auto interfaceDetail : t_InterfaceDetailList)
	{
		string path = interfaceDetail.getPath();
		validateSecuredInterfaceInvalidPasscodeBusObject(path, SECURED_PROPERTY_INTERFACE_NAME);
	}
}

void ControlPanelTestSuite::validateSecuredActionInvalidPasscodeInterfaceDetailList(const list<InterfaceDetail>& t_InterfaceDetailList)
{
	for (auto interfaceDetail : t_InterfaceDetailList)
	{
		string path = interfaceDetail.getPath();
		validateSecuredInterfaceInvalidPasscodeBusObject(path, SECURED_ACTION_INTERFACE_NAME);
	}
}

void ControlPanelTestSuite::validateSecuredDialogInvalidPasscodeInterfaceDetailList(const list<InterfaceDetail>& t_InterfaceDetailList)
{
	for (auto interfaceDetail : t_InterfaceDetailList)
	{
		string path = interfaceDetail.getPath();
		validateSecuredInterfaceInvalidPasscodeBusObject(path, SECURED_DIALOG_INTERFACE_NAME);
	}
}

/*void ControlPanelTestSuite::validateSecuredListPropertyInvalidPasscodeInterfaceDetailList(const list<InterfaceDetail>& t_InterfaceDetailList)
{
	for (auto interfaceDetail : t_InterfaceDetailList)
	{
		string path = interfaceDetail.getPath();
		validateSecuredInterfaceInvalidPasscodeBusObject(path, SECURED_LIST_PROPERTY_INTERFACE_NAME);
	}
}*/