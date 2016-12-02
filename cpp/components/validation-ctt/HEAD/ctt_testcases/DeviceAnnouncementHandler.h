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

#include <alljoyn\AboutListener.h>
#include "AboutAnnouncementDetails.h"
#include <deque>
#include <chrono>

class DeviceAnnouncementHandler : public ajn::AboutListener
{
public:
	DeviceAnnouncementHandler(const std::string&, const uint8_t*);
	void Announced(const char*, uint16_t, ajn::SessionPort, const ajn::MsgArg&, const ajn::MsgArg&);
	//~DeviceAnnouncementHandler();
	AboutAnnouncementDetails* waitForNextDeviceAnnouncement(long);
	//void waitForSessionToClose(const uint16_t);
	void clearQueuedDeviceAnnouncements();

private:
	std::string m_DutDeviceId;
	const uint8_t* m_DutAppId{ nullptr };
	//std::deque<qcc::String> m_LostDevices;
	std::deque<AboutAnnouncementDetails> m_ReceivedAnnouncements;

	bool deviceIdMatches(const std::string&, const std::string&);
	bool appIdMatches(const uint8_t*, const uint8_t*);
};