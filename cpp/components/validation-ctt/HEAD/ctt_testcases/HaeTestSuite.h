/******************************************************************************
* *    Copyright (c) Open Connectivity Foundation (OCF) and AllJoyn Open
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
#pragma once

#include "IOManager.h"
#include "InterfaceInfo.h"
#include "ServiceHelper.h"

#include <iostream>
#include <set>
#include <vector>

#include <alljoyn\AboutProxy.h>
#include <alljoyn/hae/DeviceListener.h>
#include <alljoyn/hae/HaeController.h>
#include <qcc\Event.h>

class HaeTestSuite : public ::testing::Test, public IOManager, public ajn::services::DeviceListener, public ajn::SessionListener
{
public:
	HaeTestSuite();
	void SetUp();
	void TearDown();

	virtual void OnDeviceAdded(const char* busname, ajn::SessionPort port, const ajn::services::HaeAboutData& data, const ajn::AboutObjectDescription& description);
	virtual void OnDeviceRemoved(const char* busname);
	virtual void OnDeviceSessionJoined(const ajn::services::DeviceInfoPtr& info);
	virtual void OnDeviceSessionLost(ajn::SessionId sessionId);

	QStatus WaitForControllee(ajn::services::HaeInterfaceType type = (ajn::services::HaeInterfaceType)-1);
protected:
	static const char* BUS_APPLICATION_NAME;
	static const char* ALL_HAE_INTERFACE;

	std::string m_DutDeviceId = std::string{ "" };
	uint8_t* m_DutAppId{ nullptr };
	uint32_t m_AnnouncementTimeout;
	ServiceHelper* m_ServiceHelper{ nullptr };
	AboutAnnouncementDetails* m_DeviceAboutAnnouncement{ nullptr };
	ajn::AboutProxy* m_AboutProxy{ nullptr };

	ajn::services::HaeController* m_Controller;
	qcc::String m_InterfaceNameForTest;
	std::vector<InterfaceInfo> m_Interfaces;
	qcc::Event m_EventOnDeviceAdded;

	void releaseResources();

	// HAE-v1-HaeAbout
	bool HasDefaultInterfaces(const qcc::String& objPath, const ajn::AboutObjectDescription& description, std::vector<std::vector<qcc::String> >& defaultInterfaces);
	void RemoveInterface(const qcc::String& iface, std::vector<std::vector<qcc::String> >& interfaces);
};