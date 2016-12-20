/******************************************************************************
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
******************************************************************************/
#include "stdafx.h"
#include "CdmTestSuite.h"

#include "ArrayParser.h"

AJ_PCSTR CdmTestSuite::ALL_CDM_INTERFACE = "*";
AJ_PCSTR CdmTestSuite::BUS_APPLICATION_NAME = "CdmTestSuite";

InterfaceInfo::InterfaceInfo(const char* name, ajn::SessionPort port, const char* path, ajn::services::CdmAboutData& data, ajn::AboutObjectDescription& description) :
	busName(name), sessionPort(port), sessionId(0), objectPath(path), aboutData(data), aboutDescription(description)
{
	ajn::MsgArg* arg;
	data.GetField("DeviceName", arg, "en");
	const char* bus_name;
	arg->Get("s", &bus_name);

	deviceName = bus_name;
}

CdmTestSuite::CdmTestSuite() : IOManager(ServiceFramework::CDM) {}

void CdmTestSuite::SetUp()
{
	CLOG(INFO, "raw") << "====================================================";
	LOG(INFO) << "test setUp started";

	m_DutDeviceId = m_IxitMap.at("IXITCO_DeviceId");
	m_DutAppId = ArrayParser::parseAppIdFromString(m_IxitMap.at("IXITCO_AppId"));
	m_AnnouncementTimeout = atol(m_GeneralParameterMap.at("GPCO_AnnouncementTimeout").c_str()) * 1000;

	m_ServiceHelper = new ServiceHelper();

	QStatus status = m_ServiceHelper->initializeClient(BUS_APPLICATION_NAME, m_DutDeviceId, m_DutAppId);
	ASSERT_EQ(ER_OK, status) << "serviceHelper Initialize() failed: " << QCC_StatusText(status);

	m_controller = new ajn::services::CdmController(*m_ServiceHelper->getBusAttachmentMgr()->getBusAttachment(), this);
	
	status = m_ServiceHelper->enableAuthentication("/Keystore",
		m_IcsMap.at("ICSCO_SrpKeyX"), m_IxitMap.at("IXITCO_SrpKeyXPincode"),
		m_IcsMap.at("ICSCO_SrpLogon"), m_IxitMap.at("IXITCO_SrpLogonUser"), m_IxitMap.at("IXITCO_SrpLogonPass"),
		m_IcsMap.at("ICSCO_EcdheNull"),
		m_IcsMap.at("ICSCO_EcdhePsk"), m_IxitMap.at("IXITCO_EcdhePskPassword"),
		m_IcsMap.at("ICSCO_EcdheEcdsa"), m_IxitMap.at("IXITCO_EcdheEcdsaPrivateKey"), m_IxitMap.at("IXITCO_EcdheEcdsaCertChain"),
		m_IcsMap.at("ICSCO_EcdheSpeke"), m_IxitMap.at("IXITCO_EcdheSpekePassword"));
	ASSERT_EQ(ER_OK, status) << "Enabling authentication returned status code " << status;

	status = m_controller->Start();
	ASSERT_EQ(ER_OK, status) << "Starting HAE controller returned status code " << status;

	LOG(INFO) << "test setUp done";
	CLOG(INFO, "raw") << "====================================================";
}

void CdmTestSuite::TearDown()
{
	CLOG(INFO, "raw") << "====================================================";
	LOG(INFO) << "test tearDown started";

	releaseResources();

	LOG(INFO) << "test tearDown done";
	CLOG(INFO, "raw") << "====================================================";

	storeResults();
}

void CdmTestSuite::releaseResources()
{
	m_controller->Stop();
	delete m_controller;

	if (m_ServiceHelper != nullptr)
	{
		QStatus status = m_ServiceHelper->release();

		EXPECT_EQ(status, ER_OK) << "serviceHelper Release() failed: " << QCC_StatusText(status);
		delete m_ServiceHelper;
	}

	m_interfaces.clear();
}

QStatus CdmTestSuite::WaitForControllee(ajn::services::CdmInterfaceType type)
{
	TEST_LOG_1("The test device listens for an About announcement from the application on the DUT");
	if (type == (ajn::services::CdmInterfaceType)-1)
	{
		m_interfaceNameForTest = ALL_CDM_INTERFACE;
		return qcc::Event::Wait(m_eventOnDeviceAdded, m_AnnouncementTimeout);
	}
	else
	{
		m_interfaceNameForTest = ajn::services::CdmInterface::GetInterfaceName(type);
		QStatus status = qcc::Event::Wait(m_eventOnDeviceAdded, m_AnnouncementTimeout);
		std::string name = m_interfaceNameForTest.c_str();
		TEST_LOG_1("After receiving an About announcement from the application, the test device joins"
			" a session with the application at the port specified in the received About announcement"
			" if there is \"" + name.substr(name.find_last_of('.') + 1) + "\" Interface on DUT.");
		return status;
	}
}

void CdmTestSuite::OnDeviceAdded(const char* busname, ajn::SessionPort port, const ajn::services::CdmAboutData& data, const ajn::AboutObjectDescription& description)
{
	char* retrievedDeviceId;
	QStatus status = const_cast<ajn::services::CdmAboutData&>(data).GetDeviceId(&retrievedDeviceId);
	ASSERT_EQ(ER_OK, status) << "Retrieving DeviceId from HaeAboutData returned status code " << status;

	uint8_t* retrievedAppId = new uint8_t[16];
	size_t retrievedAppIdSize;
	status = const_cast<ajn::services::CdmAboutData&>(data).GetAppId(&retrievedAppId, &retrievedAppIdSize);
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
			if (m_interfaceNameForTest.compare(interfaces[j]) && m_interfaceNameForTest.compare(ALL_CDM_INTERFACE))
				continue;

			InterfaceInfo info(busname, port, paths[i], const_cast<ajn::services::CdmAboutData&>(data), const_cast<ajn::AboutObjectDescription&>(description));
			m_controller->JoinDevice(info.busName, port, data, const_cast<ajn::AboutObjectDescription&>(description));
			m_interfaces.push_back(info);
			isFound = true;
		}
		delete[] interfaces;
	}
	delete[] paths;
	if (isFound)
		m_eventOnDeviceAdded.SetEvent();
}

void CdmTestSuite::OnDeviceRemoved(const char* busname)
{
}

void CdmTestSuite::OnDeviceSessionJoined(const ajn::services::DeviceInfoPtr& info)
{
}

void CdmTestSuite::OnDeviceSessionLost(ajn::SessionId id)
{
	ASSERT_TRUE(false);
}