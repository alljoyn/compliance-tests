/******************************************************************************
*    Copyright (c) Open Connectivity Foundation (OCF) and AllJoyn Open
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
*    THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL
*    WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED
*    WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE
*    AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL
*    DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR
*    PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER
*    TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
*    PERFORMANCE OF THIS SOFTWARE.
******************************************************************************/
#pragma once

#include "IOManager.h"
#include "ServiceAvailabilityHandler.h"
#include "ServiceHelper.h"

#include <alljoyn\AboutProxy.h>

class ConfigTestSuite : public ::testing::Test, public IOManager
{
public:
	ConfigTestSuite();
	void SetUp();
	void TearDown();

protected:
	static const char* BUS_APPLICATION_NAME;
	static const char* INVALID_LANGUAGE_CODE;
	static const char* NEW_DEVICE_NAME;
	static const int CONFIG_CLIENT_RECONNECT_WAIT_TIME;
	static const char* NEW_PASSCODE;
	static const char* SINGLE_CHAR_PASSCODE;
	static const char* SPECIAL_CHARS_PASSCODE;

	std::string m_DutDeviceId = std::string{ "" };
	uint8_t* m_DutAppId{ nullptr };
	std::string m_DefaultLanguage = std::string{ "" };
	ServiceHelper* m_ServiceHelper{ nullptr };
	AboutAnnouncementDetails* m_DeviceAboutAnnouncement{ nullptr };
	ajn::AboutProxy* m_AboutProxy{ nullptr };
	ajn::services::ConfigClient* m_ConfigClient{ nullptr };
	ajn::SessionId m_SessionId;
	ServiceAvailabilityHandler* m_ServiceAvailabilityHandler{ nullptr };

	std::string m_DefaultSrpKeyXPincode = std::string{ "" };
	bool m_UseEcdheNullInTest;
	std::string m_DefaultEcdhePskPassword = std::string{ "" };
	std::string m_DefaultEcdheSpekePassword = std::string{ "" };

	AboutAnnouncementDetails* waitForDeviceAboutAnnouncement();
	QStatus resetPasscodeIfNeeded();
	QStatus setPasscode(const char*);
	void releaseResources();

	// Config-v1-02
	QStatus callMethodToCheckAuthentication();
	void setWrongAuthValues();

	// Config-v1-04
	QStatus getConfigurations(const char*, ajn::services::ConfigClient::Configurations&);
	void checkConfigurationsForRequiredFields(const ajn::services::ConfigClient::Configurations&);
	void checkConsistencyWithAboutAnnouncement(const ajn::services::ConfigClient::Configurations&);

	// Config-v1-05
	void compareConfigurations(const ajn::services::ConfigClient::Configurations&,
		const ajn::services::ConfigClient::Configurations&);
	void compareConfigurationsForField(const char*, const ajn::services::ConfigClient::Configurations&,
		const ajn::services::ConfigClient::Configurations&);

	// Config-v1-06
	void checkAboutDataForRequiredFields(ajn::AboutData&);
	void compareConfigAndAbout(const ajn::services::ConfigClient::Configurations&,
		const ajn::AboutData&);
	void compareConfigAndAboutForField(const char*, const ajn::services::ConfigClient::Configurations&,
		const ajn::AboutData&);

	// Config-v1-08
	QStatus testUpdateConfigurations(const char*, const char*);
	QStatus updateConfigurationsAndVerifyResult(const char*, const char*);
	QStatus updateConfigurations(const char*, const ajn::services::ConfigClient::Configurations&);
	void waitForNextAnnouncementAndVerifyFieldValue(const char*, const char*);
	AboutAnnouncementDetails* waitForNextAnnouncementAndCheckFieldValue(const char*, const char*);
	void verifyValueForConfigAndAbout(const ajn::services::ConfigClient::Configurations&,
		const ajn::AboutData&, const char*, const char*);

	// Config-v1-12
	std::string getDeviceNameWithSpecialCharacters();
	void appendChars(std::string&, int, int);

	// Config-v1-20
	QStatus resetConfigurations(const char*, const std::vector<qcc::String>&);

	// Config-v1-26
	QStatus callRestartOnConfig();
	void loseSessionOrWait();

	// Config-v1-27
	void reconnectClients(QStatus&);

	// Config-v1-29
	void testChangePasscode(const char*);
	void changePasscodeAndReconnect(const char*);
	void restorePasswordInStores();
	void changePasswordInStores(const char*);
};