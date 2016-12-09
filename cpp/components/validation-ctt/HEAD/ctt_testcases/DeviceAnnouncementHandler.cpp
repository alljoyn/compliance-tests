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
#include "stdafx.h"
#include "DeviceAnnouncementHandler.h"

#include "ArrayParser.h"

#include <thread>

using namespace std;
using namespace ajn;

DeviceAnnouncementHandler::DeviceAnnouncementHandler(const string& t_DutDeviceId,
	const uint8_t* t_DutAppId) : m_DutDeviceId(t_DutDeviceId),
	m_DutAppId(t_DutAppId) {}

bool DeviceAnnouncementHandler::deviceIdMatches(const std::string& t_DutDeviceId, 
	const std::string& t_DeviceId)
{
	return t_DutDeviceId.compare(t_DeviceId) == 0;
}

bool DeviceAnnouncementHandler::appIdMatches(const uint8_t* t_DutAppId, 
	const uint8_t* t_AppId)
{
	bool equal = true;

	for (size_t i = 0; i < sizeof(t_DutAppId); ++i)
	{
		if (t_DutAppId[i] != t_AppId[i])
		{
			equal = false;
			break;
		}
	}
	return equal;
}

void DeviceAnnouncementHandler::clearQueuedDeviceAnnouncements()
{
	m_ReceivedAnnouncements.clear();
}

void DeviceAnnouncementHandler::Announced(const char* t_BusName, 
	uint16_t t_Version, SessionPort t_Port, const MsgArg& t_ObjectDescriptions,
	const MsgArg& t_AboutData)
{
	AboutAnnouncementDetails* receivedAboutAnnouncement = new AboutAnnouncementDetails(
		t_BusName,
		t_Version,
		t_Port,
		t_ObjectDescriptions,
		t_AboutData);

	char* deviceId = receivedAboutAnnouncement->getDeviceId();
	uint8_t* appId = receivedAboutAnnouncement->getAppId();

	if (deviceIdMatches(m_DutDeviceId, deviceId) 
		&& appIdMatches(m_DutAppId, appId))
	{
		LOG(INFO) << "Received About announcement signal from DUT with deviceId: " 
			<< deviceId << ", appId: " << ArrayParser::parseAppId(appId);
		m_ReceivedAnnouncements.push_front(*receivedAboutAnnouncement);
	}
	else
	{
		LOG(INFO) << "Ignoring About announcement signal from DUT with deviceId: " 
			<< deviceId << ", appId: " << ArrayParser::parseAppId(appId);
		//m_LostDevices.push_front(deviceId);
	}

	delete receivedAboutAnnouncement;
}

AboutAnnouncementDetails* DeviceAnnouncementHandler::waitForNextDeviceAnnouncement(long t_Timeout)
{
	LOG(INFO) << "Waiting for About Announcement signal from DUT with deviceId: " 
		<< m_DutDeviceId << "; appId: " << ArrayParser::parseAppId(m_DutAppId);
	
	const clock_t beginTime = clock();

	while (m_ReceivedAnnouncements.empty() && (clock() - beginTime < t_Timeout))
	{
		std::this_thread::sleep_for(std::chrono::milliseconds(100));
	}

	AboutAnnouncementDetails* announcement = nullptr;

	if (!m_ReceivedAnnouncements.empty())
	{
		announcement = new AboutAnnouncementDetails(m_ReceivedAnnouncements.front());
		m_ReceivedAnnouncements.pop_front();
	}
	
	return announcement;
}

/*void DeviceAnnouncementHandler::waitForSessionToClose(const uint16_t t_Timeout)
{
	LOG(INFO) << "Waiting for session to close for device: " << m_DutDeviceId;
	
	const clock_t beginTime = clock();

	while (m_LostDevices.empty() && (clock() - beginTime < t_Timeout))
	{
		std::this_thread::sleep_for(std::chrono::milliseconds(100));
	}

	if (!m_LostDevices.empty())
	{
		m_LostDevices.pop_front();
	}
}*/