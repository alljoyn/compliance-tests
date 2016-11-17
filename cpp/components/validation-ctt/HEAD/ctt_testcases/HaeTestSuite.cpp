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
#include "HaeTestSuite.h"

#include "ArrayParser.h"

#include <algorithm>

#include <alljoyn/hae/HaeAboutData.h>
#include <alljoyn/hae/DeviceTypeDescription.h>

const char* HaeTestSuite::BUS_APPLICATION_NAME = "HaeTestSuite";
const char* HaeTestSuite::ALL_HAE_INTERFACE = "*";

const static std::map<ajn::services::DeviceType, std::vector<std::vector<qcc::String> > > defaultInterfacesForDeviceType = {
	{ ajn::services::ROOT                /* 0  */,{} },
	{ ajn::services::OTHER               /* 1  */,{} },
	{ ajn::services::REFRIGERATOR        /* 2  */,{ { "org.alljoyn.SmartSpaces.Operation.ClosedStatus" } } },
	{ ajn::services::FREEZER             /* 3  */,{ { "org.alljoyn.SmartSpaces.Operation.ClosedStatus" } } },
	{ ajn::services::ICE_MAKER           /* 4  */,{} },
	{ ajn::services::AIR_CONDITIONER     /* 5  */,{ { "org.alljoyn.SmartSpaces.Operation.OnOffStatus" },
	{ "org.alljoyn.SmartSpaces.Operation.ClimateControlMode" },
	{ "org.alljoyn.SmartSpaces.Environment.TargetTemperature" },
	{ "org.alljoyn.SmartSpaces.Environment.CurrentTemperature" } } },
	{ ajn::services::THERMOSTAT          /* 6  */,{ { "org.alljoyn.SmartSpaces.Operation.OnOffStatus" },
	{ "org.alljoyn.SmartSpaces.Operation.ClimateControlMode" },
	{ "org.alljoyn.SmartSpaces.Environment.TargetTemperature" },
	{ "org.alljoyn.SmartSpaces.Environment.CurrentTemperature" } } },
	{ ajn::services::HUMIDIFIER          /* 7  */,{ { "org.alljoyn.SmartSpaces.Operation.OnOffStatus" } } },
	{ ajn::services::DEHUMIDIFIER        /* 8  */,{ { "org.alljoyn.SmartSpaces.Operation.OnOffStatus" },
	{ "org.alljoyn.SmartSpaces.Environment.TargetHumidity" },
	{ "org.alljoyn.SmartSpaces.Environment.CurrentHumidity" } } },
	{ ajn::services::AIR_PURIFIER        /* 9  */,{ { "org.alljoyn.SmartSpaces.Operation.OnOffStatus" } } },
	{ ajn::services::ELECTRIC_FAN        /* 10 */,{ { "org.alljoyn.SmartSpaces.Operation.OnOffStatus" } } },
	{ ajn::services::AIR_QUALITY_MONITOR /* 11 */,{ { "org.alljoyn.SmartSpaces.Environment.CurrentAirQuality", "org.alljoyn.SmartSpaces.Environment.CurrentAirQualityLevel" } } },
	{ ajn::services::CLOTHES_WASHER      /* 12 */,{ { "org.alljoyn.SmartSpaces.Operation.OnOffStatus" },
	{ "org.alljoyn.SmartSpaces.Operation.CycleControl" } } },
	{ ajn::services::CLOTHES_DRYER       /* 13 */,{ { "org.alljoyn.SmartSpaces.Operation.OnOffStatus" },
	{ "org.alljoyn.SmartSpaces.Operation.CycleControl" } } },
	{ ajn::services::CLOTHES_WASHER_DRYER/* 14 */,{ { "org.alljoyn.SmartSpaces.Operation.OnOffStatus" },
	{ "org.alljoyn.SmartSpaces.Operation.CycleControl" } } },
	{ ajn::services::DISH_WASHER         /* 15 */,{ { "org.alljoyn.SmartSpaces.Operation.OnOffStatus" },
	{ "org.alljoyn.SmartSpaces.Operation.CycleControl" } } },
	{ ajn::services::ROBOT_CLEANER       /* 16 */,{ { "org.alljoyn.SmartSpaces.Operation.OnOffStatus" },
	{ "org.alljoyn.SmartSpaces.Operation.CycleControl" } } },
	{ ajn::services::OVEN                /* 17 */,{ { "org.alljoyn.SmartSpaces.Operation.OnOffStatus" },
	{ "org.alljoyn.SmartSpaces.Operation.CycleControl" } } },
	{ ajn::services::COOKER_HOOD         /* 18 */,{ { "org.alljoyn.SmartSpaces.Operation.OnOffStatus" },
	{ "org.alljoyn.SmartSpaces.Operation.HvacMode" },
	{ "org.alljoyn.SmartSpaces.Operation.FanSpeedLevel" } } },
	{ ajn::services::COOKTOP             /* 19 */,{ { "org.alljoyn.SmartSpaces.Operation.HeatingZone" } } },
	{ ajn::services::FOOD_PROBE          /* 20 */,{ { "org.alljoyn.SmartSpaces.Environment.TargetTemperature" } } },
	{ ajn::services::TELEVISION          /* 21 */,{ { "org.alljoyn.SmartSpaces.Operation.OnOffStatus" },
	{ "org.alljoyn.SmartSpaces.Operation.Channel" },
	{ "org.alljoyn.SmartSpaces.Operation.AudioVolume" } } },
	{ ajn::services::SET_TOP_BOX         /* 22 */,{ { "org.alljoyn.SmartSpaces.Operation.OnOffStatus" } } }
};

HaeTestSuite::HaeTestSuite() : IOManager(ServiceFramework::HAE) {}

void HaeTestSuite::SetUp()
{
	CLOG(INFO, "raw") << "====================================================";
	LOG(INFO) << "test setUp started";

	m_DutDeviceId = m_IxitMap.at("IXITCO_DeviceId");
	m_DutAppId = ArrayParser::parseAppIdFromString(m_IxitMap.at("IXITCO_AppId"));
	m_AnnouncementTimeout = atol(m_GeneralParameterMap.at("GPCO_AnnouncementTimeout").c_str()) * 1000;

	m_ServiceHelper = new ServiceHelper();

	QStatus status = m_ServiceHelper->initializeClient(BUS_APPLICATION_NAME, m_DutDeviceId, m_DutAppId,
		m_IcsMap.at("ICSCO_SrpKeyX"), m_IxitMap.at("IXITCO_SrpKeyXPincode"),
		m_IcsMap.at("ICSCO_SrpLogon"), m_IxitMap.at("IXITCO_SrpLogonUser"), m_IxitMap.at("IXITCO_SrpLogonPass"),
		m_IcsMap.at("ICSCO_EcdheNull"),
		m_IcsMap.at("ICSCO_EcdhePsk"), m_IxitMap.at("IXITCO_EcdhePskPassword"),
		m_IcsMap.at("ICSCO_EcdheEcdsa"), m_IxitMap.at("IXITCO_EcdheEcdsaPrivateKey"), m_IxitMap.at("IXITCO_EcdheEcdsaCertChain"),
		m_IcsMap.at("ICSCO_EcdheSpeke"), m_IxitMap.at("IXITCO_EcdheSpekePassword"));
	ASSERT_EQ(status, ER_OK) << "serviceHelper Initialize() failed: " << QCC_StatusText(status);

	m_Controller = new ajn::services::HaeController(*m_ServiceHelper->getBusAttachmentMgr()->getBusAttachment(), this);
	
	status = m_ServiceHelper->enableAuthentication("/Keystore");
	ASSERT_EQ(ER_OK, status) << "Enabling authentication returned status code " << status;

	status = m_Controller->Start();
	ASSERT_EQ(ER_OK, status) << "Starting HAE controller returned status code " << status;

	LOG(INFO) << "test setUp done";
	CLOG(INFO, "raw") << "====================================================";
}

void HaeTestSuite::TearDown()
{
	CLOG(INFO, "raw") << "====================================================";
	LOG(INFO) << "test tearDown started";

	releaseResources();

	LOG(INFO) << "test tearDown done";
	CLOG(INFO, "raw") << "====================================================";

	storeResults();
}

void HaeTestSuite::releaseResources()
{
	m_Controller->Stop();
	delete m_Controller;

	if (m_ServiceHelper != nullptr)
	{
		QStatus status = m_ServiceHelper->release();

		EXPECT_EQ(status, ER_OK) << "serviceHelper Release() failed: " << QCC_StatusText(status);
		delete m_ServiceHelper;
	}

	m_Interfaces.clear();
}

QStatus HaeTestSuite::WaitForControllee(ajn::services::HaeInterfaceType type)
{
	LOG(INFO) << "The test device listens for an About announcement from the application on the DUT";
	if (type == (ajn::services::HaeInterfaceType)-1)
	{
		m_InterfaceNameForTest = ALL_HAE_INTERFACE;
		return qcc::Event::Wait(m_EventOnDeviceAdded, m_AnnouncementTimeout);
	}
	else
	{
		m_InterfaceNameForTest = ajn::services::HaeInterface::GetInterfaceName(type);
		QStatus status = qcc::Event::Wait(m_EventOnDeviceAdded, m_AnnouncementTimeout);
		std::string name = m_InterfaceNameForTest.c_str();
		LOG(INFO) << "After receiving an About announcement from the application, the test device joins\n"
			"   a session with the application at the port specified in the received About announcement\n"
			"   if there is \"" + name.substr(name.find_last_of('.') + 1) + "\" Interface on DUT.";
		return status;
	}
}

void HaeTestSuite::OnDeviceAdded(const char* busname, ajn::SessionPort port, const ajn::services::HaeAboutData& data, const ajn::AboutObjectDescription& description)
{
	char* retrievedDeviceId;
	QStatus status = const_cast<ajn::services::HaeAboutData&>(data).GetDeviceId(&retrievedDeviceId);
	ASSERT_EQ(ER_OK, status) << "Retrieving DeviceId from HaeAboutData returned status code " << status;

	uint8_t* retrievedAppId = new uint8_t[16];
	size_t retrievedAppIdSize;
	status = const_cast<ajn::services::HaeAboutData&>(data).GetAppId(&retrievedAppId, &retrievedAppIdSize);
	ASSERT_EQ(ER_OK, status) << "Retrieving AppId from HaeAboutData returned status code " << status;

	if ((m_IxitMap.at("IXITCO_AppId").compare(ArrayParser::parseAppId(retrievedAppId).c_str()) != 0)
		|| (m_DutDeviceId.compare(retrievedDeviceId) != 0))
	{
		return;
	}

	size_t path_num = description.GetPaths(NULL, 0);
	const char** paths = new const char*[path_num];
	description.GetPaths(paths, path_num);

	bool isFound = false;
	for (size_t i = 0; i < path_num; ++i)
	{
		if (!strncmp(paths[i], "/About", strlen(paths[i])))
		{
			continue;
		}
		size_t numInterfaces = description.GetInterfaces(paths[i], NULL, 0);
		const char** interfaces = new const char*[numInterfaces];
		description.GetInterfaces(paths[i], interfaces, numInterfaces);
		for (size_t j = 0; j < numInterfaces; ++j)
		{
			if (m_InterfaceNameForTest.compare(interfaces[j]) && m_InterfaceNameForTest.compare(ALL_HAE_INTERFACE))
				continue;

			InterfaceInfo info(busname, port, paths[i], const_cast<ajn::services::HaeAboutData&>(data), const_cast<ajn::AboutObjectDescription&>(description));
			m_Controller->JoinDevice(info.busName, port, data, const_cast<ajn::AboutObjectDescription&>(description));
			m_Interfaces.push_back(info);
			isFound = true;
		}
		delete[] interfaces;
	}
	delete[] paths;
	if (isFound)
		m_EventOnDeviceAdded.SetEvent();
}

void HaeTestSuite::OnDeviceRemoved(const char* busname)
{
}

void HaeTestSuite::OnDeviceSessionJoined(const ajn::services::DeviceInfoPtr& info)
{
}

void HaeTestSuite::OnDeviceSessionLost(ajn::SessionId id)
{
	ASSERT_TRUE(false);
}

TEST_F(HaeTestSuite, HAE_v1_HaeAbout)
{
	ASSERT_EQ(ER_OK, WaitForControllee()) << "Controllee device not found";
	ajn::services::HaeAboutData &aboutData = m_Interfaces[0].aboutData;
	ajn::AboutObjectDescription &aboutDescription = m_Interfaces[0].aboutDescription;
	ajn::services::DeviceTypeDescription* deviceTypeDescription = new ajn::services::DeviceTypeDescription();

	LOG(INFO) << "Check Additional About Metadata Fields";
	{
		char* location;
		LOG(DEBUG) << "Location field";
		QStatus status = aboutData.GetLocation(&location);
		EXPECT_EQ(status, ER_OK) << "Retrieving Location field returned status code " << status;
		LOG(DEBUG) << "DeviceTypeDescription field";
		status = aboutData.GetDeviceTypeDescription(&deviceTypeDescription);
		ASSERT_EQ(status, ER_OK) << "Retrieving DeviceTypeDescription field returned status code " << status;
	}

	LOG(INFO) << "Check existence of Minimum required interfaces of DeviceType";
	{
		ajn::services::DeviceTypeDescription::DescriptionsType::const_iterator itr = deviceTypeDescription->GetDescriptions().begin();
		ajn::services::DeviceTypeDescription::DescriptionsType::const_iterator end = deviceTypeDescription->GetDescriptions().end();
		for (; itr != end; ++itr)
		{
			std::vector<std::vector<qcc::String> > defaultInterfaces = defaultInterfacesForDeviceType.at(itr->first);
			if (!HasDefaultInterfaces(itr->second, aboutDescription, defaultInterfaces))
			{
				qcc::String defIntf = "";
				for (size_t i = 0; i < defaultInterfaces.size(); ++i)
				{
					for (size_t j = 0; j < defaultInterfaces[i].size(); ++j)
						defIntf += "\n  * " + defaultInterfaces[i][j];
				}
				ADD_FAILURE() << "DUT doesn't have default interfaces of DeviceType[" << itr->first << "] as following :" << defIntf.c_str();
			}
		}
	}
	delete deviceTypeDescription;
}

bool HaeTestSuite::HasDefaultInterfaces(const qcc::String& objPath, const ajn::AboutObjectDescription& description, std::vector<std::vector<qcc::String> >& defaultInterfaces)
{
	size_t path_num = description.GetPaths(NULL, 0);
	const char** paths = new const char*[path_num];
	description.GetPaths(paths, path_num);

	for (size_t i = 0; i < path_num; ++i)
	{
		if (strstr(paths[i], objPath.c_str()) != paths[i])
		{
			continue;
		}

		size_t numInterfaces = description.GetInterfaces(paths[i], NULL, 0);
		const char** interfaces = new const char*[numInterfaces];
		description.GetInterfaces(paths[i], interfaces, numInterfaces);
		for (size_t i = 0; i < numInterfaces; ++i)
		{
			qcc::String intf = interfaces[i];

			RemoveInterface(intf, defaultInterfaces);
			if (defaultInterfaces.empty())
			{
				return true;
			}
		}
		delete[] interfaces;
	}
	delete[] paths;
	return false;
}

void HaeTestSuite::RemoveInterface(const qcc::String& iface, std::vector<std::vector<qcc::String> >& interfaces)
{
	std::vector<std::vector<qcc::String> >::iterator itr = interfaces.begin();
	for (; itr != interfaces.end(); ++itr)
	{
		if (std::find(itr->begin(), itr->end(), iface) != itr->end())
		{
			interfaces.erase(itr);
			return;
		}
	}
}