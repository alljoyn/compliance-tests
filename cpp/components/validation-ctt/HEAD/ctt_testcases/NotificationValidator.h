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

#include <atomic>
#include <string>

#include "AboutAnnouncementDetails.h"
#include "BusIntrospector.h"
#include "NotificationHandler.h"
#include "XMLBasedBusIntrospector.h"

#include <alljoyn\notification\Notification.h>
#include <alljoyn\notification\NotificationReceiver.h>

class NotificationValidator : public ajn::services::NotificationReceiver
{
public:
	NotificationValidator(std::string, uint8_t*, bool, bool, bool, uint16_t);
	void initializeForDevice(AboutAnnouncementDetails&, XMLBasedBusIntrospector&);
	void Receive(ajn::services::Notification const&);
	void Dismiss(const int32_t, const qcc::String);
	unsigned int getNumberOfNotificationsReceived();
	

private:
	static const char* URL_REGEX;

	NotificationHandler* m_NotificationHandler{ nullptr };
	AboutAnnouncementDetails* m_DeviceAboutAnnouncement{ nullptr };
	XMLBasedBusIntrospector* m_BusIntrospector{ nullptr };
	std::atomic<unsigned int> m_NotificationCounter{ 0 };
	uint16_t m_ExpectedNotificationVersion;
	bool m_ExpectedRichIconUrl;
	bool m_ExpectedRichAudioUrl;
	bool m_ExpectedResponseObjectPath;
	bool m_ReadingNotifications;

	void validateUrl(const std::string&);
	void readNotification();

};