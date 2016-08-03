/******************************************************************************
* Copyright AllSeen Alliance. All rights reserved.
*
*    Permission to use, copy, modify, and/or distribute this software for any
*    purpose with or without fee is hereby granted, provided that the above
*    copyright notice and this permission notice appear in all copies.
*
*    THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
*    WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
*    MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
*    ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
*    WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
*    ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
*    OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
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
	static const char* VALID_DEFAULT_PASSCODE;
	static const char* NEW_DEVICE_NAME;

	std::string m_DutDeviceId = std::string{ "" };
	uint8_t* m_DutAppId{ nullptr };
	ajn::AboutProxy* m_AboutProxy{ nullptr };
	ajn::services::ConfigClient* m_ConfigClient{ nullptr };
	OnboardingHelper* m_OnboardingHelper{ nullptr };
	std::string m_SoftApSsid = std::string{ "" };
	WifiNetworkConfig* m_PersonalApConfig;
	WifiNetworkConfig* m_SoftApConfig;
	
	void releaseResources();
	void disconnectAboutProxy();
	void disconnectConfigClient();

	// Onboarding-v1-01
	AboutAnnouncementDetails placeDUTInOnboardState();
	QStatus placeDUTInOffboardState();

	// Onboarding-v1-02
	QStatus makeSureDeviceIsInOffboardedState();
	void verifyConfigureWiFiReturnValue(const short);
	QStatus verifyOnboardingState(const short);
	void verifyOnboardingErrorCode(const short);

	// Onboarding-v1-03
	AboutAnnouncementDetails connectToDUTInOffboardedState();

	// Onboarding-v1-08
	void validateScanResult(const ajn::services::OBScanInfo&);
	void validateAuthType(const ajn::services::OBAuthType&);
};