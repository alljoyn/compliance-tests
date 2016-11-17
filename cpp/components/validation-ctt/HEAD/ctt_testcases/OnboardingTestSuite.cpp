/******************************************************************************
* *    Copyright (c) Open Connectivity Foundation (OCF) and AllJoyn Open
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
*     THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL
*     WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED
*     WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE
*     AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL
*     DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR
*     PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER
*     TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
*     PERFORMANCE OF THIS SOFTWARE.
******************************************************************************/
#include "stdafx.h"
#include "OnboardingTestSuite.h"

#include "AboutAnnouncementDetails.h"
#include "AvailableSoftAPException.h"
#include "ArrayParser.h"
#include "NotAvailableSoftAPException.h"
#include "SoftAPValidator.h"
#include "WifiNotEnabledException.h"

#include <thread>

#include <alljoyn\AllJoynStd.h>

using namespace std;
using namespace ajn;
using namespace services;

const char* OnboardingTestSuite::BUS_APPLICATION_NAME = "OnboardingTestSuite";
const short OnboardingTestSuite::OBS_CONFIGURE_WIFI_RETURN_NO_CHANNEL_SWITCHING = 1;
const short OnboardingTestSuite::OBS_CONFIGURE_WIFI_RETURN_SUPPORTS_CHANNEL_SWITCHING = 2;
const char* OnboardingTestSuite::INVALID_NETWORK_NAME = "InvalidPersonalAP";
const char* OnboardingTestSuite::INVALID_PASSCODE = "123456";
const char* OnboardingTestSuite::TEMP_PASSCODE = "111111";
const char* OnboardingTestSuite::NEW_DEVICE_NAME = "newDeviceName";

OnboardingTestSuite::OnboardingTestSuite() : IOManager(ServiceFramework::ONBOARDING)
{

}

void OnboardingTestSuite::SetUp()
{
	CLOG(INFO, "raw") << "====================================================";
	LOG(INFO) << "test setUp started";

	m_DutDeviceId = m_IxitMap.at("IXITCO_DeviceId");
	m_DutAppId = ArrayParser::parseAppIdFromString(m_IxitMap.at("IXITCO_AppId"));
	m_DefaultSrpKeyXPincode = m_IxitMap.at("IXITCO_SrpKeyXPincode");
	m_DefaultEcdhePskPassword = m_IxitMap.at("IXITCO_EcdhePskPassword");
	m_DefaultEcdheSpekePassword = m_IxitMap.at("IXITCO_EcdheSpekePassword");

	m_OnboardingHelper = new OnboardingHelper(BUS_APPLICATION_NAME,
		atol(m_GeneralParameterMap.at("GPON_TimeToWaitForScanResults").c_str())*1000);

	short personalApSecurity = static_cast<short>(atoi(m_IxitMap.at("IXITON_PersonalAPAuthType").c_str()));
	string personalApAuthTypeString = m_OnboardingHelper->mapAuthTypeToAuthTypeString(personalApSecurity);
	string personalApSsid = m_IxitMap.at("IXITON_PersonalAP");
	string personalApPassphrase = m_IxitMap.at("IXITON_PersonalAPpassphrase");
	
	m_PersonalApConfig = new WifiNetworkConfig(personalApSsid, personalApPassphrase, personalApAuthTypeString);
	m_OnboardingHelper->setPersonalAPConfig(m_PersonalApConfig);

	short softApSecurity = static_cast<short>(atoi(m_IxitMap.at("IXITON_SoftAPAuthType").c_str()));
	string softApAuthTypeString = m_OnboardingHelper->mapAuthTypeToAuthTypeString(softApSecurity);
	string softApSsid = m_IxitMap.at("IXITON_SoftAP");
	string softApPassphrase = m_IxitMap.at("IXITON_SoftAPpassphrase");

	m_SoftApConfig = new WifiNetworkConfig(softApSsid, softApPassphrase, softApAuthTypeString);
	m_OnboardingHelper->setSoftAPConfig(m_SoftApConfig);

	ASSERT_EQ(ER_OK, m_OnboardingHelper->initialize("/Keystore", m_DutDeviceId, m_DutAppId,
		m_IcsMap.at("ICSCO_SrpKeyX"), m_IxitMap.at("IXITCO_SrpKeyXPincode"),
		m_IcsMap.at("ICSCO_SrpLogon"), m_IxitMap.at("IXITCO_SrpLogonUser"), m_IxitMap.at("IXITCO_SrpLogonPass"),
		m_IcsMap.at("ICSCO_EcdheNull"),
		m_IcsMap.at("ICSCO_EcdhePsk"), m_IxitMap.at("IXITCO_EcdhePskPassword"),
		m_IcsMap.at("ICSCO_EcdheEcdsa"), m_IxitMap.at("IXITCO_EcdheEcdsaPrivateKey"), m_IxitMap.at("IXITCO_EcdheEcdsaCertChain"),
		m_IcsMap.at("ICSCO_EcdheSpeke"), m_IxitMap.at("IXITCO_EcdheSpekePassword")))
		<< "OnboardingHelper Initialize() failed";

	LOG(INFO) << "test setUp done";
	CLOG(INFO, "raw") << "====================================================";
}

void OnboardingTestSuite::TearDown()
{
	CLOG(INFO, "raw") << "====================================================";
	LOG(INFO) << "test tearDown started";

	releaseResources();

	LOG(INFO) << "test tearDown done";
	CLOG(INFO, "raw") << "====================================================";

	storeResults();
}

void OnboardingTestSuite::releaseResources()
{
	disconnectAboutProxy();
	disconnectConfigClient();
	
	if (m_OnboardingHelper != nullptr)
	{
		m_OnboardingHelper->release();
		m_OnboardingHelper = nullptr;
	}
}

void OnboardingTestSuite::disconnectAboutProxy()
{
	if (m_AboutProxy != nullptr)
	{
		delete m_AboutProxy;
	}
}

void OnboardingTestSuite::disconnectConfigClient()
{
	if (m_ConfigClient != nullptr)
	{
		LOG(INFO) << "Disconnecting ConfigClient";
		m_ConfigClient = nullptr;
	}
}

TEST_F(OnboardingTestSuite, Onboarding_v1_01)
{
	try
	{
		m_OnboardingHelper->connectToPersonalAPIfNeeded();
	}
	catch (WifiNotEnabledException)
	{
		FAIL() << "WiFi is not enabled";
	}
	
	if (!m_OnboardingHelper->isDeviceInOnboardedState())
	{
		LOG(INFO) << "Device currently in Offboarded state, so onboarding it";

		try
		{
			QStatus status = placeDUTInOnboardState();
			ASSERT_EQ(ER_OK, status) << "Placing DUT in Onboard State returned status code: " << QCC_StatusText(status);
		}
		catch (AvailableSoftAPException ex)
		{
			FAIL() << ex.what();
		}
	}

	LOG(INFO) << "Checking Onboarding interface version property";
	int ixit_version = atoi(m_IxitMap.at("IXITON_OnboardingVersion").c_str());
	int retrieved_version = 0;
	QStatus status = m_OnboardingHelper->retrieveVersionProperty(retrieved_version);
	ASSERT_EQ(ER_OK, status) << "Calling Onboarding.GetVersion() method returned status code: " << QCC_StatusText(status);

	EXPECT_EQ(ixit_version, retrieved_version) << "Onboarding interface version mismatches IXITON_OnboardingVersion";

	status = placeDUTInOffboardState();
	ASSERT_EQ(ER_OK, status)
		<< "Calling Onboarding.Offboard() method failed with status code: " << QCC_StatusText(status);

	verifyOnboardingState(OBState::NOT_CONFIGURED);

	if (!SoftAPValidator::validateSoftAP(m_SoftApSsid))
	{
		FAIL();
	}
}

QStatus OnboardingTestSuite::placeDUTInOffboardState()
{
	QStatus status = m_OnboardingHelper->callOffboard();
	if (ER_OK == status)
	{
		string ssid = m_OnboardingHelper->connectToDUTOnSoftAP();
		if (m_SoftApSsid.empty())
		{
			m_SoftApSsid = ssid;
		}

		m_OnboardingHelper->waitForAboutAnnouncementAndThenConnect();
	}

	return status;
}

TEST_F(OnboardingTestSuite, Onboarding_v1_02)
{
	QStatus status = makeSureDeviceIsInOffboardedState();
	ASSERT_EQ(ER_OK, status) << "Something went wrong while making sure device is in offboarded state";

	try
	{
		ASSERT_EQ(ER_OK, status = placeDUTInOnboardState())
			<< "Placing DUT in Onboard State returned status code: " << QCC_StatusText(status);
	}
	catch (AvailableSoftAPException ex)
	{
		FAIL() << ex.what();
	}
}

QStatus OnboardingTestSuite::makeSureDeviceIsInOffboardedState()
{
	if (m_SoftApConfig->getSsid().empty())
	{
		LOG(ERROR) << "Soft AP SSID cannot be empty. You have to provide the soft AP SSID to run the Onboarding tests";
		return ER_FAIL;
	}

	try
	{
		if (m_OnboardingHelper->isDeviceInOnboardedState())
		{
			LOG(INFO) << "Device currently in Onboarded state, so offboarding it";
			// Get Version property lines are added here because Offboard method does not ask for credentials
			int retrieved_version = 0;
			QStatus status = m_OnboardingHelper->retrieveVersionProperty(retrieved_version);

			if (ER_OK != status)
			{
				return status;
			}

			return placeDUTInOffboardState();
		}
	}
	catch (NotAvailableSoftAPException)
	{
		LOG(ERROR) << "Announcement not received and Soft AP not available";
		return ER_FAIL;
	}
	
	return ER_OK;
}

QStatus OnboardingTestSuite::placeDUTInOnboardState()
{
	m_OnboardingHelper->connectToDUTOnSoftAP();
	m_OnboardingHelper->waitForAboutAnnouncementAndThenConnect();

	short configureWiFiRetVal;
	QStatus status = m_OnboardingHelper->callConfigureWiFi(*m_PersonalApConfig, configureWiFiRetVal);
	if (ER_OK != status)
	{
		return status;
	}

	verifyConfigureWiFiReturnValue(configureWiFiRetVal);
	verifyOnboardingState(OBState::CONFIGURED_NOT_VALIDATED);

	m_OnboardingHelper->callConnectWiFiAndWaitForSoftAPDisconnect();
	m_OnboardingHelper->connectToPersonalAP();

	std::this_thread::sleep_for(std::chrono::milliseconds(5000)); // Added due to the delay of Soft AP radio when turning off
	m_AboutAnnouncementDetails = m_OnboardingHelper->waitForAboutAnnouncementAndThenConnect();

	verifyOnboardingState(OBState::CONFIGURED_VALIDATED);
	verifyOnboardingErrorCode(OBValidationState::VALIDATED);

	return status;
}

void OnboardingTestSuite::verifyConfigureWiFiReturnValue(const short t_ConfigureWifiReturnValue)
{
	ASSERT_NE(OBS_CONFIGURE_WIFI_RETURN_SUPPORTS_CHANNEL_SWITCHING, t_ConfigureWifiReturnValue)
		<< "fast-channel switching is presently not supported on any platform, so no ConnectionResult signal is being sent";
	ASSERT_EQ(OBS_CONFIGURE_WIFI_RETURN_NO_CHANNEL_SWITCHING, t_ConfigureWifiReturnValue)
		<< "Got unexpected retval from configureWiFi(): " + t_ConfigureWifiReturnValue;
}

QStatus OnboardingTestSuite::verifyOnboardingState(const short t_State)
{
	LOG(INFO) << "Retrieving the State property from the Onboarding interface";
	short currentState;
	QStatus status = m_OnboardingHelper->retrieveStateProperty(currentState);

	if (ER_OK != status)
	{
		return status;
	}

	EXPECT_EQ(t_State, currentState) << "State property does not match expected value";
	return status;
}

void OnboardingTestSuite::verifyOnboardingErrorCode(const short t_Error)
{
	LOG(INFO) << "Retrieving the ErrorCode property from the Onboarding interface";
	OBLastError lastError = m_OnboardingHelper->retrieveLastErrorProperty();
	ASSERT_EQ(t_Error, lastError.validationState) << "ErrorCode property does not match expected value";
}

TEST_F(OnboardingTestSuite, Onboarding_v1_03)
{
	connectToDUTInOffboardedState();

	QStatus status = verifyOnboardingState(OBState::NOT_CONFIGURED);
	ASSERT_EQ(ER_OK, status) << "Retrieving State property returned status code: " << QCC_StatusText(status);
}

AboutAnnouncementDetails* OnboardingTestSuite::connectToDUTInOffboardedState()
{
	makeSureDeviceIsInOffboardedState();

	m_OnboardingHelper->connectToDUTOnSoftAP();

	return m_OnboardingHelper->waitForAboutAnnouncementAndThenConnect();
}

TEST_F(OnboardingTestSuite, Onboarding_v1_05)
{
	connectToDUTInOffboardedState();

	short configureWifiRetVal;
	QStatus status = m_OnboardingHelper->callConfigureWiFi(
		WifiNetworkConfig(INVALID_NETWORK_NAME, m_PersonalApConfig->getPassphrase(), m_PersonalApConfig->getSecurityType()), configureWifiRetVal);

	verifyConfigureWiFiReturnValue(configureWifiRetVal);
	status = verifyOnboardingState(OBState::CONFIGURED_NOT_VALIDATED);
	ASSERT_EQ(ER_OK, status) << "Retrieving State property returned status code: " << QCC_StatusText(status);

	m_OnboardingHelper->callConnectWiFiAndWaitForSoftAPDisconnect();
	m_OnboardingHelper->connectToDUTOnSoftAP();
	m_OnboardingHelper->waitForAboutAnnouncementAndThenConnect();

	verifyOnboardingState(OBState::CONFIGURED_ERROR);
	verifyOnboardingErrorCode(OBValidationState::UNREACHABLE);
}

TEST_F(OnboardingTestSuite, Onboarding_v1_06)
{
	connectToDUTInOffboardedState();

	short configureWifiRetVal;
	QStatus status = m_OnboardingHelper->callConfigureWiFi(
		WifiNetworkConfig(m_PersonalApConfig->getSsid(),
		std::string("0").append(m_PersonalApConfig->getPassphrase()),
		m_PersonalApConfig->getSecurityType()), configureWifiRetVal);

	verifyConfigureWiFiReturnValue(configureWifiRetVal);
	verifyOnboardingState(OBState::CONFIGURED_NOT_VALIDATED);

	m_OnboardingHelper->callConnectWiFiAndWaitForSoftAPDisconnect();
	m_OnboardingHelper->connectToDUTOnSoftAP();
	m_OnboardingHelper->waitForAboutAnnouncementAndThenConnect();

	verifyOnboardingState(OBState::CONFIGURED_ERROR);
	
	LOG(INFO) << "Retrieving error property from Onboarding interface";
	ASSERT_NE(m_OnboardingHelper->retrieveLastErrorProperty().validationState, OBValidationState::VALIDATED);
}

TEST_F(OnboardingTestSuite, Onboarding_v1_07)
{
	connectToDUTInOffboardedState();

	short configureWifiRetVal;
	QStatus status = m_OnboardingHelper->callConfigureWiFi(
		WifiNetworkConfig(m_PersonalApConfig->getSsid(),
		m_PersonalApConfig->getPassphrase(),
		"ANY"), configureWifiRetVal);

	verifyConfigureWiFiReturnValue(configureWifiRetVal);
	verifyOnboardingState(OBState::CONFIGURED_NOT_VALIDATED);

	m_OnboardingHelper->callConnectWiFiAndWaitForSoftAPDisconnect();
	m_OnboardingHelper->connectToPersonalAP();
	std::this_thread::sleep_for(std::chrono::milliseconds(5000)); // Added due to the delay of Soft AP radio when turning off
	try
	{
		m_OnboardingHelper->waitForAboutAnnouncementAndThenConnect();
	}
	catch (AvailableSoftAPException ex)
	{
		FAIL() << ex.what();
	}

	verifyOnboardingState(OBState::CONFIGURED_VALIDATED);
	verifyOnboardingErrorCode(OBValidationState::VALIDATED);

	placeDUTInOffboardState();

	/*m_OnboardingHelper->connectToDUTOnSoftAP();
	m_OnboardingHelper->waitForAboutAnnouncementAndThenConnect();*/
}

TEST_F(OnboardingTestSuite, Onboarding_v1_08)
{
	connectToDUTInOffboardedState();

	OnboardingClient::ScanInfos scanInfos = m_OnboardingHelper->callScanInfo();

	for (auto scanInfo : scanInfos)
	{
		validateScanResult(scanInfo);
	}
}

void OnboardingTestSuite::validateScanResult(const OBScanInfo& t_ScanInfo)
{
	validateAuthType(t_ScanInfo.authType);
}

void OnboardingTestSuite::validateAuthType(const OBAuthType& t_AuthType)
{
	ASSERT_LT(OBAuthType::WPA2_AUTO, t_AuthType)
		<< "AuthType must not be lower than " << OBAuthType::WPA2_AUTO;
	ASSERT_GT(OBAuthType::WPS, t_AuthType)
		<< "AuthType must not be greater than " << OBAuthType::WPS;
}

TEST_F(OnboardingTestSuite, Onboarding_v1_09)
{
	makeSureDeviceIsInOffboardedState();

	m_OnboardingHelper->connectToDUTOnSoftAP();
	AboutAnnouncementDetails* deviceAboutAnnouncement = m_OnboardingHelper->waitForAboutAnnouncementAndThenConnect();
	m_OnboardingHelper->setPasscode(*deviceAboutAnnouncement, INVALID_PASSCODE);

	LOG(INFO) << "Calling Onboarding.GetVersion() with invalid passcode";
	int version;
	QStatus status = m_OnboardingHelper->retrieveVersionProperty(version);
	ASSERT_NE(ER_OK, status)
		<< "An error status must be received when attempting to call a method on the secured Onboarding interface with an invalid password";
	ASSERT_EQ(ER_AUTH_FAIL, status)
		<< "Did not receive an ER_AUTH_FAIL status indicating authentication failure";
}

TEST_F(OnboardingTestSuite, Onboarding_v1_10)
{
	std::string daemonName = "";
	AboutAnnouncementDetails* deviceAboutAnnouncement = connectToDUTInOffboardedState();

	ASSERT_TRUE(deviceAboutAnnouncement->supportsInterface("org.alljoyn.Config"))
		<< "DUT does not support Config interface";

	LOG(INFO) << "Calling Config.SetPasscode() to change passcode to: " << TEMP_PASSCODE;
	ajn::SessionId sessionId;
	m_ConfigClient = m_OnboardingHelper->connectConfigClient(sessionId);
	m_ConfigClient->SetPasscode(deviceAboutAnnouncement->getServiceName().c_str(),
		daemonName.c_str(), 6, (const uint8_t*)TEMP_PASSCODE, sessionId);

	disconnectConfigClient();

	m_OnboardingHelper->connectToDUTOnSoftAP();
	deviceAboutAnnouncement = m_OnboardingHelper->waitForAboutAnnouncementAndThenConnect();
	m_OnboardingHelper->setPasscode(*deviceAboutAnnouncement, TEMP_PASSCODE);

	LOG(INFO) << "Calling Onboarding.GetVersion() after changing passcode";
	int version;
	m_OnboardingHelper->retrieveVersionProperty(version);

	m_ConfigClient = m_OnboardingHelper->connectConfigClient(sessionId);
	setDefaultAuthentication(*deviceAboutAnnouncement, daemonName, sessionId);
}

void OnboardingTestSuite::setDefaultAuthentication(AboutAnnouncementDetails t_DeviceAboutAnnouncement,
	const std::string& t_DaemonName, ajn::SessionId t_SessionId)
{
	LOG(INFO) << "Calling Config.SetPasscode() to change passcode back to default passcode";

	if (m_IcsMap.at("ICSCO_EcdheSpeke"))
	{
		m_ConfigClient->SetPasscode(t_DeviceAboutAnnouncement.getServiceName().c_str(),
			t_DaemonName.c_str(), 6, (const uint8_t*)m_DefaultEcdheSpekePassword.c_str(), t_SessionId);
	}
	else if (m_IcsMap.at("ICSCO_EcdhePsk"))
	{
		m_ConfigClient->SetPasscode(t_DeviceAboutAnnouncement.getServiceName().c_str(),
			t_DaemonName.c_str(), 6, (const uint8_t*)m_DefaultEcdhePskPassword.c_str(), t_SessionId);
	}
	else if (m_IcsMap.at("ICSCO_SrpKeyX"))
	{
		m_ConfigClient->SetPasscode(t_DeviceAboutAnnouncement.getServiceName().c_str(),
			t_DaemonName.c_str(), 6, (const uint8_t*)m_DefaultSrpKeyXPincode.c_str(), t_SessionId);
	}
	
}

TEST_F(OnboardingTestSuite, Onboarding_v1_11)
{
	makeSureDeviceIsInOffboardedState();
	
	try
	{
		ASSERT_EQ(ER_OK, placeDUTInOnboardState()) << "Something was wrong when trying to Onboard DUT";
	}
	catch (AvailableSoftAPException ex)
	{
		FAIL() << ex.what();
	}

	ASSERT_TRUE(m_AboutAnnouncementDetails->supportsInterface("org.alljoyn.Config"))
		<< "DUT does not support Config interface";

	char* defaultLanguage = m_AboutAnnouncementDetails->getDefaultLanguage();
	LOG(INFO) << "Default language is: " << defaultLanguage;

	m_AboutProxy = m_OnboardingHelper->connectAboutProxy(*m_AboutAnnouncementDetails);
	ajn::SessionId sessionId;
	m_ConfigClient = m_OnboardingHelper->connectConfigClient(sessionId);

	ajn::AboutData aboutData;
	ConfigClient::Configurations configurations;
	configurations.clear();
	std::string defaultDeviceName;

	if (m_IcsMap.at("ICSCO_DeviceName"))
	{
		std::vector<qcc::String> fieldsToReset = { AboutData::DEVICE_NAME };
		QStatus status = m_ConfigClient->ResetConfigurations(m_AboutAnnouncementDetails->getServiceName().c_str(),
			defaultLanguage, fieldsToReset, sessionId);
		ASSERT_EQ(ER_OK, status) << "Calling ResetConfigurations for DeviceName field returned status code: " << QCC_StatusText(status);

		status = m_ConfigClient->GetConfigurations(m_AboutAnnouncementDetails->getServiceName().c_str(),
			defaultLanguage, configurations, sessionId);
		ASSERT_EQ(ER_OK, status) << "Calling GetConfigurations returned status code: " << QCC_StatusText(status);

		MsgArg aboutDataMsgArg;
		m_AboutProxy->GetAboutData(defaultLanguage, aboutDataMsgArg);
		aboutData = AboutData(aboutDataMsgArg, defaultLanguage);

		char* defaultDeviceNameChar;
		aboutData.GetDeviceName(&defaultDeviceNameChar);
		defaultDeviceName = std::string(defaultDeviceNameChar);

		EXPECT_STREQ(defaultDeviceName.c_str(), configurations.at(AboutData::DEVICE_NAME).v_string.str)
			<< "GetConfigurations() does not return the same DeviceName as GetAboutData()";

		m_OnboardingHelper->clearQueuedDeviceAnnouncements();

		configurations.clear();
		configurations.insert(std::pair<qcc::String, ajn::MsgArg>(AboutData::DEVICE_NAME, MsgArg("s", NEW_DEVICE_NAME)));
		m_ConfigClient->UpdateConfigurations(m_AboutAnnouncementDetails->getServiceName().c_str(),
			defaultLanguage, configurations, sessionId);

		disconnectAboutProxy();
		disconnectConfigClient();

		m_AboutAnnouncementDetails = m_OnboardingHelper->waitForAboutAnnouncementAndThenConnect();

		EXPECT_STREQ(NEW_DEVICE_NAME, m_AboutAnnouncementDetails->getDeviceName())
			<< "Device name in announcement was not modified as expected";

		m_AboutProxy = m_OnboardingHelper->connectAboutProxy(*m_AboutAnnouncementDetails);
		m_ConfigClient = m_OnboardingHelper->connectConfigClient(sessionId);

		m_AboutProxy->GetAboutData(defaultLanguage, aboutDataMsgArg);
		aboutData = AboutData(aboutDataMsgArg, defaultLanguage);

		char* modifiedDeviceName;
		aboutData.GetDeviceName(&modifiedDeviceName);
		EXPECT_STREQ(NEW_DEVICE_NAME, modifiedDeviceName)
			<< "Device name in AboutData was not modified as expected";

		configurations.clear();
		m_ConfigClient->GetConfigurations(m_AboutAnnouncementDetails->getServiceName().c_str(),
			defaultLanguage, configurations, sessionId);

		EXPECT_STREQ(NEW_DEVICE_NAME, configurations.at(AboutData::DEVICE_NAME).v_string.str)
			<< "GetConfigurations() returns the same DeviceName as GetAboutData()";
	}

	LOG(INFO) << "Calling FactoryReset() method";
	QStatus status = m_ConfigClient->FactoryReset(m_AboutAnnouncementDetails->getServiceName().c_str(), sessionId);

	if (ER_OK != status)
	{
		EXPECT_EQ(ER_FEATURE_NOT_AVAILABLE, status)
			<< "Unexpected error received from FactoryReset() method call";

		if (m_IcsMap.at("ICSCO_DeviceName"))
		{
			std::vector<qcc::String> fieldsToReset = { AboutData::DEVICE_NAME };
			m_ConfigClient->ResetConfigurations(m_AboutAnnouncementDetails->getServiceName().c_str(),
				defaultLanguage, fieldsToReset, sessionId);
		}

		return;
	}

	disconnectConfigClient();
	disconnectAboutProxy();

	m_OnboardingHelper->connectToDUTOnSoftAP();

	m_AboutAnnouncementDetails = m_OnboardingHelper->waitForAboutAnnouncementAndThenConnect();

	if (m_IcsMap.at("ICSCO_DeviceName"))
	{
		EXPECT_STREQ(defaultDeviceName.c_str(), m_AboutAnnouncementDetails->getDeviceName())
			<< "Device name in announcement was not the default as expected";

		m_AboutProxy = m_OnboardingHelper->connectAboutProxy(*m_AboutAnnouncementDetails);
		m_ConfigClient = m_OnboardingHelper->connectConfigClient(sessionId);

		MsgArg aboutDataMsgArg;
		m_AboutProxy->GetAboutData(defaultLanguage, aboutDataMsgArg);
		aboutData = AboutData(aboutDataMsgArg, defaultLanguage);

		char* deviceName;
		aboutData.GetDeviceName(&deviceName);
		LOG(INFO) << "After FactoryReset() method call, the DeviceName is: " << deviceName;

		EXPECT_STREQ(defaultDeviceName.c_str(), deviceName)
			<< "DeviceName not reset to default value";

		configurations.clear();
		m_ConfigClient->GetConfigurations(m_AboutAnnouncementDetails->getServiceName().c_str(),
			defaultLanguage, configurations, sessionId);

		EXPECT_STREQ(defaultDeviceName.c_str(), configurations.at(AboutData::DEVICE_NAME).v_string.str)
			<< "GetConfigurations() does not return the same DeviceName as GetAboutData()";
	}
}

TEST_F(OnboardingTestSuite, Onboarding_v1_12)
{
	std::string daemonName("");
	AboutAnnouncementDetails* deviceAboutAnnouncement = connectToDUTInOffboardedState();

	ASSERT_TRUE(deviceAboutAnnouncement->supportsInterface("org.alljoyn.Config"))
		<< "DUT does not support Config interface";

	ajn::SessionId sessionId;
	m_ConfigClient = m_OnboardingHelper->connectConfigClient(sessionId);
	m_ConfigClient->SetPasscode(deviceAboutAnnouncement->getServiceName().c_str(),
		daemonName.c_str(), 6, (const uint8_t*)TEMP_PASSCODE, sessionId);

	disconnectConfigClient();

	m_OnboardingHelper->connectToDUTOnSoftAP();

	deviceAboutAnnouncement = m_OnboardingHelper->waitForAboutAnnouncementAndThenConnect();

	m_OnboardingHelper->setPasscode(*deviceAboutAnnouncement, TEMP_PASSCODE);
	int version;
	m_OnboardingHelper->retrieveVersionProperty(version);

	m_ConfigClient = m_OnboardingHelper->connectConfigClient(sessionId);

	QStatus status = m_ConfigClient->FactoryReset(deviceAboutAnnouncement->getServiceName().c_str(), sessionId);

	if (ER_OK != status)
	{
		EXPECT_EQ(ER_FEATURE_NOT_AVAILABLE, status)
			<< "Unexpected error received from FactoryReset() method call";

		setDefaultAuthentication(*deviceAboutAnnouncement, daemonName, sessionId);
		return;
	}

	m_OnboardingHelper->waitForSoftAPDisconnect();

	disconnectConfigClient();

	m_OnboardingHelper->connectToDUTOnSoftAP();
	deviceAboutAnnouncement = m_OnboardingHelper->waitForAboutAnnouncementAndThenConnect();
	
	if (m_IcsMap.at("ICSCO_EcdheSpeke"))
	{
		m_OnboardingHelper->setPasscode(*deviceAboutAnnouncement, m_DefaultEcdheSpekePassword.c_str());
	}
	else if (m_IcsMap.at("ICSCO_EcdhePsk"))
	{
		m_OnboardingHelper->setPasscode(*deviceAboutAnnouncement, m_DefaultEcdhePskPassword.c_str());
	}
	else if (m_IcsMap.at("ICSCO_SrpKeyX"))
	{
		m_OnboardingHelper->setPasscode(*deviceAboutAnnouncement, m_DefaultSrpKeyXPincode.c_str());
	}
	

	LOG(INFO) << "Calling Onboarding.GetVersion() method";
	m_OnboardingHelper->retrieveVersionProperty(version);
}