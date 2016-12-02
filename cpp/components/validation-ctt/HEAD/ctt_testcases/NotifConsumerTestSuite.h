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

#include "IOManager.h"
#include "MessageSet.h"
#include "ServiceHelper.h"

#include <alljoyn\AboutProxy.h>
#include <alljoyn\notification\NotificationSender.h>

class NotifConsumerTestSuite : public ::testing::Test, public IOManager
{
public:
	NotifConsumerTestSuite();
	void SetUp();
	void TearDown();

protected:
	static const char* BUS_APPLICATION_NAME;

	std::string m_DutDeviceId = std::string{ "" };
	uint8_t* m_DutAppId{ nullptr };
	ServiceHelper* m_ServiceHelper{ nullptr };
	AboutAnnouncementDetails* m_DeviceAboutAnnouncement{ nullptr };
	ajn::AboutProxy* m_AboutProxy{ nullptr };
	ajn::services::NotificationSender* m_NotificationSender{ nullptr };
	ajn::AboutData* m_AboutDataStore{ nullptr };
	uint16_t m_TtlInSeconds;

	ajn::AboutData* getAboutDataStore();

	// Notification-Consumer-v1-01
	int getRandomNumber(const size_t);
	void checkUserInput(const std::vector<std::string>&, const std::string&, const int);

	// Notification-Consumer-v1-05
	std::string buildPromptText(const std::vector<MessageSet>&);

	void releaseResources();
};