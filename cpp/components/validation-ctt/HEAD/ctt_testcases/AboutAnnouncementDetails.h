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

#include "AboutAnnouncement.h"

class AboutAnnouncementDetails : public AboutAnnouncement
{
public:
	AboutAnnouncementDetails(const char*, const uint16_t, const uint16_t, const ajn::MsgArg&, const ajn::MsgArg&);
	char* getDeviceId() const;
	uint8_t* getAppId() const;
	char* getDeviceName() const;
	char* getAppName() const;
	char* getManufacturer() const;
	char* getModel() const;
	const char* getSupportedLanguages() const;
	char* getDescription() const;
	char* getManufactureDate() const;
	char* getSoftwareVersion() const;
	char* getAjSoftwareVersion() const;
	char* getHardwareVersion() const;
	char* getSupportUrl() const;
	bool supportsInterface(char*);
	char* getDefaultLanguage() const;
private:
	static std::map<qcc::String, qcc::String> KEY_SIGNATURE_MAP;

	bool checkForCorrectType(qcc::String key, qcc::String signature);
};