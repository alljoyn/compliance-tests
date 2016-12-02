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
#include "ServiceHelper.h"

#include <set>

#include <alljoyn\AboutProxy.h>

class Security20TestSuite : public ::testing::Test, public IOManager
{
public:
	Security20TestSuite();
	void SetUp();
	void TearDown();

protected:
	static const char* BUS_APPLICATION_NAME;

	std::string m_DutDeviceId = std::string{ "" };
	uint8_t* m_DutAppId{ nullptr };
	ServiceHelper* m_ServiceHelper{ nullptr };
	AboutAnnouncementDetails* m_DeviceAboutAnnouncement{ nullptr };
	ajn::AboutProxy* m_AboutProxy{ nullptr };
	std::string m_DefaultLanguage = std::string{ "" };
	ajn::SecurityApplicationProxy* m_SecurityApplicationProxy{ nullptr };

	void releaseResources();
	void setManifestTemplate(ajn::BusAttachment&);
};