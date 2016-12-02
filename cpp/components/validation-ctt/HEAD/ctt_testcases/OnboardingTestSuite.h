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

#include <set>

#include "IOManager.h"
#include "OnboardingHelper.h"
#include "ServiceHelper.h"

#include <alljoyn\AboutProxy.h>

class OnboardingTestSuite : public ::testing::Test, public IOManager
{
public:
	OnboardingTestSuite();
	void SetUp();
	void TearDown();

protected:
	static const char* BUS_APPLICATION_NAME;
	static const short OBS_CONFIGURE_WIFI_RETURN_NO_CHANNEL_SWITCHING;
	static const short OBS_CONFIGURE_WIFI_RETURN_SUPPORTS_CHANNEL_SWITCHING;
	static const char* INVALID_NETWORK_NAME;
	static const char* INVALID_PASSCODE;
	static const char* TEMP_PASSCODE;
	static const char* NEW_DEVICE_NAME;

	std::string m_DutDeviceId = std::string{ "" };
	uint8_t* m_DutAppId{ nullptr };
	ajn::AboutProxy* m_AboutProxy{ nullptr };
	ajn::services::ConfigClient* m_ConfigClient{ nullptr };
	OnboardingHelper* m_OnboardingHelper{ nullptr };
	std::string m_SoftApSsid = std::string{ "" };
	WifiNetworkConfig* m_PersonalApConfig;
	WifiNetworkConfig* m_SoftApConfig;
	AboutAnnouncementDetails* m_AboutAnnouncementDetails{ nullptr };

	std::string m_DefaultSrpKeyXPincode = std::string{ "" };
	std::string m_DefaultEcdhePskPassword = std::string{ "" };
	std::string m_DefaultEcdheSpekePassword = std::string{ "" };
	
	void releaseResources();
	void disconnectAboutProxy();
	void disconnectConfigClient();

	// Onboarding-v1-01
	QStatus placeDUTInOnboardState();
	QStatus placeDUTInOffboardState();

	// Onboarding-v1-02
	QStatus makeSureDeviceIsInOffboardedState();
	void verifyConfigureWiFiReturnValue(const short);
	QStatus verifyOnboardingState(const short);
	void verifyOnboardingErrorCode(const short);

	// Onboarding-v1-03
	AboutAnnouncementDetails* connectToDUTInOffboardedState();

	// Onboarding-v1-08
	void validateScanResult(const ajn::services::OBScanInfo&);
	void validateAuthType(const ajn::services::OBAuthType&);

	// Onboarding-v1-10
	void setDefaultAuthentication(AboutAnnouncementDetails, const std::string&, ajn::SessionId);
};