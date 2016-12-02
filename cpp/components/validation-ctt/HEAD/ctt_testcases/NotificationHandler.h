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

#include <alljoyn\notification\NotificationReceiver.h>

class NotificationHandler : public ajn::services::NotificationReceiver
{
public:
	NotificationHandler(std::string, uint8_t*);
	void Receive(ajn::services::Notification const&);
	void Dismiss(const int32_t, const qcc::String);
	ajn::services::Notification* getReceivedNotification();
	ajn::services::Notification* getReceivedNotification(const long);
	void clearReceivedNotifications();
	std::string normalizeAppId(const char*);
private:
	std::string m_SenderDeviceId;
	uint8_t* m_SenderAppId;
	std::deque<ajn::services::Notification> m_ReceivedNotifications;
};