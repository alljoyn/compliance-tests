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

#include "CdmSemaphore.h"
#include "IOManager.h"
#include "ServiceHelper.h"

#include <iostream>
#include <vector>

#include <alljoyn\cdm\common\DeviceListener.h>
#include <alljoyn\cdm\controller\CdmController.h>

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
	SessionId sessionId;
	std::string objectPath;
	CdmAboutData aboutData;
	AboutObjectDescription aboutDescription;

	InterfaceInfo()
	{
	}
	InterfaceInfo(const char* name, SessionPort port, const char* path, CdmAboutData& data, AboutObjectDescription& description);
};

class CdmTestSuite : public ::testing::Test, public IOManager, public DeviceListener, public SessionListener
{
public:
	CdmTestSuite();
	void SetUp();
	void TearDown();

	virtual void OnDeviceAdded(const char* busname, SessionPort port, const CdmAboutData& data, const AboutObjectDescription& description);
	virtual void OnDeviceRemoved(const char* busname);
	virtual void OnDeviceSessionJoined(const Ref<DeviceInfo> info);
	virtual void OnDeviceSessionLost(SessionId sessionId);

	QStatus WaitForControllee(CdmInterfaceType type = (CdmInterfaceType)-1);
protected:
	static AJ_PCSTR BUS_APPLICATION_NAME;
	static AJ_PCSTR ALL_CDM_INTERFACE;

	std::string m_DutDeviceId = std::string{ "" };
	uint8_t* m_DutAppId{ nullptr };
	uint32_t m_AnnouncementTimeout;
	ServiceHelper* m_ServiceHelper{ nullptr };
	AboutAnnouncementDetails* m_DeviceAboutAnnouncement{ nullptr };

	CdmController* m_controller;
	qcc::String m_interfaceNameForTest;
	std::vector<InterfaceInfo> m_interfaces;
	qcc::Event m_eventOnDeviceAdded;
	int LOG_NO;

	void releaseResources();
};
