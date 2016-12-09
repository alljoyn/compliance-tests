/******************************************************************************
* Copyright (c) 2016 Open Connectivity Foundation (OCF) and AllJoyn Open
*    Source Project (AJOSP) Contributors and others.
*
*    SPDX-License-Identifier: Apache-2.0
*
*    All rights reserved. This program and the accompanying materials are
*    made available under the terms of the Apache License, Version 2.0
*    which accompanies this distribution, and is available at
*    http://www.apache.org/licenses/LICENSE-2.0
*
*    Copyright 2016 Open Connectivity Foundation and Contributors to
*    AllSeen Alliance. All rights reserved.
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
#pragma once

#include "IOManager.h"
#include "ServiceHelper.h"

#include <iostream>
#include <vector>

#include <alljoyn/cdm/DeviceListener.h>
#include <alljoyn/cdm/CdmController.h>
#include <qcc\Event.h>

#define TIMEOUT 30000

using namespace ajn;
using namespace services;

#define TEST_LOG_1(logMsg)  \
    LOG(INFO) << ++LOG_NO << ". " << logMsg
#define TEST_LOG_2(logMsg)  \
    LOG(INFO) << "  * " << logMsg
#define TEST_LOG_3(logMsg)  \
    LOG(INFO) << "    - " << logMsg
#define TEST_LOG_OBJECT_PATH(logMsg)  \
    LOG(INFO) << "[ObjectPath : " << logMsg << "]"

class InterfaceInfo
{
public:
	std::string busName;
	std::string deviceName;
	ajn::SessionPort sessionPort;
	ajn::SessionId sessionId;
	std::string objectPath;
	ajn::services::CdmAboutData aboutData;
	ajn::AboutObjectDescription aboutDescription;

	InterfaceInfo()
	{
	}
	InterfaceInfo(const char* name, ajn::SessionPort port, const char* path, ajn::services::CdmAboutData& data, ajn::AboutObjectDescription& description);
};

class CdmTestSuite : public ::testing::Test, public IOManager, public ajn::services::DeviceListener, public ajn::SessionListener
{
public:
	CdmTestSuite();
	void SetUp();
	void TearDown();

	virtual void OnDeviceAdded(const char* busname, ajn::SessionPort port, const ajn::services::CdmAboutData& data, const ajn::AboutObjectDescription& description);
	virtual void OnDeviceRemoved(const char* busname);
	virtual void OnDeviceSessionJoined(const ajn::services::DeviceInfoPtr& info);
	virtual void OnDeviceSessionLost(ajn::SessionId sessionId);

	QStatus WaitForControllee(ajn::services::CdmInterfaceType type = (ajn::services::CdmInterfaceType)-1);
protected:
	static AJ_PCSTR BUS_APPLICATION_NAME;
	static AJ_PCSTR ALL_CDM_INTERFACE;

	std::string m_DutDeviceId = std::string{ "" };
	uint8_t* m_DutAppId{ nullptr };
	uint32_t m_AnnouncementTimeout;
	ServiceHelper* m_ServiceHelper{ nullptr };
	AboutAnnouncementDetails* m_DeviceAboutAnnouncement{ nullptr };

	ajn::services::CdmController* m_controller;
	qcc::String m_interfaceNameForTest;
	std::vector<InterfaceInfo> m_interfaces;
	qcc::Event m_eventOnDeviceAdded;
	int LOG_NO;

	void releaseResources();
};