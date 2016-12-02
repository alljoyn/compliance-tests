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
#include "stdafx.h"
#include "NotificationHandler.h"

#include <thread>

#include "ArrayParser.h"

NotificationHandler::NotificationHandler(std::string t_DeviceId, uint8_t* t_AppId) :
	m_SenderDeviceId(t_DeviceId), m_SenderAppId(t_AppId) {}

void NotificationHandler::Receive(ajn::services::Notification const& t_Notification)
{
	const char* appId = t_Notification.getAppId();
	const char* deviceId = t_Notification.getDeviceId();

	if ((ArrayParser::parseAppId(m_SenderAppId).compare(normalizeAppId(appId)) == 0)
		&& (m_SenderDeviceId.compare(deviceId) == 0))
	{
		LOG(INFO) << "Received notification from DUT with messageId: "
			<< t_Notification.getMessageId();
		m_ReceivedNotifications.push_back(t_Notification);
	}
	else
	{
		LOG(INFO) << "Ignoring notification received from: " << deviceId
			<< "; appId: " << appId;
	}
}

void NotificationHandler::Dismiss(const int32_t arg0, const qcc::String arg1) {}

ajn::services::Notification* NotificationHandler::getReceivedNotification()
{
	ajn::services::Notification* notification = nullptr;

	if (!m_ReceivedNotifications.empty())
	{
		notification = new ajn::services::Notification(m_ReceivedNotifications.front());
		m_ReceivedNotifications.pop_front();
	}
	
	return notification;
}

ajn::services::Notification* NotificationHandler::getReceivedNotification(const long t_Timeout)
{
	const clock_t beginTime = clock();

	while (m_ReceivedNotifications.empty() && (clock() - beginTime < t_Timeout))
	{
		std::this_thread::sleep_for(std::chrono::milliseconds(100));
	}

	if (!m_ReceivedNotifications.empty())
	{
		return &m_ReceivedNotifications.front();
	}
	else
	{
		return nullptr;
	}
}

void NotificationHandler::clearReceivedNotifications()
{
	m_ReceivedNotifications.clear();
}

std::string NotificationHandler::normalizeAppId(const char* t_AppId)
{
	std::string temp(t_AppId);
	std::transform(temp.begin(), temp.end(), temp.begin(), ::tolower);
	temp.insert(20, "-");
	temp.insert(16, "-");
	temp.insert(12, "-");
	temp.insert(8, "-");

	return temp;
}