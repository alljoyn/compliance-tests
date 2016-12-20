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

#include "AboutAnnouncementDetails.h"
#include "ServiceHelper.h"
#include "WifiNetworkConfig.h"
#include "WifiHelperImpl.h"

#include <alljoyn\AboutProxy.h>
#include <alljoyn\config\ConfigClient.h>
#include <alljoyn\onboarding\Onboarding.h>
#include <alljoyn\onboarding\OnboardingClient.h>

class OnboardingHelper
{
public:
	OnboardingHelper(const std::string&, const int);
	std::string mapAuthTypeToAuthTypeString(const short);
	void setPersonalAPConfig(WifiNetworkConfig*);
	WifiNetworkConfig* getPersonalAPConfig();
	void setSoftAPConfig(WifiNetworkConfig*);
	WifiNetworkConfig* getSoftAPConfig();
	QStatus initialize(const std::string&, const std::string&, uint8_t*,
		const bool, const std::string&,
		const bool, const std::string&, const std::string&,
		const bool,
		const bool, const std::string&,
		const bool, const std::string&, const std::string&,
		const bool, const std::string&, const std::string&,
		const bool, const std::string&);
	QStatus initServiceHelper(const bool, const std::string&,
		const bool, const std::string&, const std::string&,
		const bool,
		const bool, const std::string&,
		const bool, const std::string&, const std::string&,
		const bool, const std::string&, const std::string&,
		const bool, const std::string&);
	void connectToPersonalAPIfNeeded();
	QStatus connectToPersonalAP();
	bool isDeviceInOnboardedState();
	QStatus retrieveVersionProperty(int&);
	QStatus callOffboard();
	std::string connectToDUTOnSoftAP();
	AboutAnnouncementDetails* waitForAboutAnnouncementAndThenConnect();
	QStatus callConfigureWiFi(WifiNetworkConfig, short&);
	ajn::services::OBAuthType getOnboardingServiceAuthType(const std::string&);
	void callConnectWiFiAndWaitForSoftAPDisconnect();
	void waitForSoftAPDisconnect();
	QStatus retrieveStateProperty(short&);
	ajn::services::OBLastError retrieveLastErrorProperty();
	void release();
	void releaseResources();
	void releaseWifiHelper();
	ajn::services::OnboardingClient::ScanInfos callScanInfo();
	void setPasscode(const AboutAnnouncementDetails&, const char*);
	ajn::services::ConfigClient* connectConfigClient(ajn::SessionId&);
	void clearQueuedDeviceAnnouncements();
	ajn::AboutProxy* connectAboutProxy(const AboutAnnouncementDetails&);

protected:
	bool announcementMatchesDeviceIdSuffix(AboutAnnouncementDetails&);
	bool haveDeviceIdAndAppId();

private:
	static const long TIME_TO_WAIT_TO_CONNECT_TO_PERSONAL_AP;
	static const long TIME_TO_WAIT_FOR_NEXT_DEVICE_ANNOUNCEMENT;
	static const long TIME_TO_WAIT_FOR_SOFT_AP;
	static const long TIME_TO_WAIT_TO_CONNECT_TO_SOFT_AP;
	static const long TIME_TO_WAIT_FOR_DISCONNECT;

	static const short MAX_NUMBER_OF_TRIES_TO_CONNECT_TO_SOFT_AP;

	static const int NUMBER_OF_DEVICE_ID_DIGITS_IN_SOFT_AP;

	std::string m_BusApplicationName = std::string{ "" };
	int m_TimeToWaitForScanResults;
	std::string m_DeviceId = std::string{ "" };
	uint8_t* m_AppId{ nullptr };
	std::string m_KeystorePath = std::string{ "" };
	WifiNetworkConfig* m_PersonalApConfig{ nullptr };
	WifiNetworkConfig* m_SoftApConfig{ nullptr };
	ajn::services::ConfigClient* m_ConfigClient{ nullptr };
	ajn::AboutProxy* m_AboutProxy{ nullptr };
	WifiHelperImpl* m_WifiHelper{ nullptr };
	ServiceHelper* m_ServiceHelper{ nullptr };
	ajn::services::OnboardingClient* m_OnboardingClient{ nullptr };
	AboutAnnouncementDetails* m_DeviceAboutAnnouncement{ nullptr };
	std::string m_DeviceIdSuffix = std::string{ "" };

	bool m_SupportsSrpKeyX;
	std::string m_DefaultSrpXPincode;
	bool m_SupportsSrpLogon;
	std::string m_DefaultLogonUser;
	std::string m_DefaultLogonPass;
	bool m_SupportsEcdheNull;
	bool m_SupportsEcdhePsk;
	std::string m_DefaultECDHEPskPassword;
	bool m_SupportsEcdheEcdsa;
	std::string m_DefaultECDHEEcdsaPrivateKey;
	std::string m_DefaultECDHEEcdsaCertChain;
	bool m_SupportsRsaKeyX;
	std::string m_DefaultRsaKeyXPrivateKey;
	std::string m_DefaultRsaKeyXCertX509;
	bool m_SupportsPinKeyX;
	std::string m_DefaultPinKeyXPincode;
	
	void releaseServiceHelper();
	void disconnectOnboardingClient();
	std::string getSoftAPSsid();
	inline bool starts_with(const std::string&, const std::string&);
	inline bool ends_with(const std::string&, const std::string&);
	bool softAPMatchesDeviceId(const std::string&, const std::string&);
	bool connectToSoftAPNetwork();
	AboutAnnouncementDetails* waitForNextAboutAnnouncementFromDevice(const long);
	std::string convertToHex(const std::string&);
	void verifyOnboardingState(const short);
};