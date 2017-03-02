/******************************************************************************
*    Copyright (c) Open Connectivity Foundation (OCF), AllJoyn Open Source
*    Project (AJOSP) Contributors and others.
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
#include "stdafx.h"
#include "ConfigTestSuite.h"

#include <thread>

#include "ArrayParser.h"

using namespace ajn;
using namespace std;
using namespace services;

AJ_PCSTR ConfigTestSuite::BUS_APPLICATION_NAME = "ConfigTestSuite";
AJ_PCSTR ConfigTestSuite::INVALID_LANGUAGE_CODE = "INVALID";
AJ_PCSTR ConfigTestSuite::NEW_DEVICE_NAME = "NewDeviceName";
const int ConfigTestSuite::CONFIG_CLIENT_RECONNECT_WAIT_TIME = 10000;
AJ_PCSTR ConfigTestSuite::NEW_PASSCODE = "1111111111111111";
AJ_PCSTR ConfigTestSuite::SINGLE_CHAR_PASSCODE = "1";
AJ_PCSTR ConfigTestSuite::SPECIAL_CHARS_PASSCODE = "!@#$%^!@#$%^!@#$";

ConfigTestSuite::ConfigTestSuite() : IOManager(ServiceFramework::CONFIGURATION)
{

}

void ConfigTestSuite::SetUp()
{
	CLOG(INFO, "raw") << "====================================================";
	LOG(INFO) << "test setUp started";

	m_DutDeviceId = m_IxitMap.at("IXITCO_DeviceId");
	m_DutAppId = ArrayParser::parseAppIdFromString(m_IxitMap.at("IXITCO_AppId"));
	m_DefaultLanguage = m_IxitMap.at("IXITCO_DefaultLanguage");
	m_DefaultSrpKeyXPincode = m_IxitMap.at("IXITCO_SrpKeyXPincode");
	m_DefaultEcdhePskPassword = m_IxitMap.at("IXITCO_EcdhePskPassword");
	m_DefaultEcdheSpekePassword = m_IxitMap.at("IXITCO_EcdheSpekePassword");

	m_ServiceHelper = new ServiceHelper();

	QStatus status = m_ServiceHelper->initializeClient(BUS_APPLICATION_NAME, m_DutDeviceId, m_DutAppId);
	ASSERT_EQ(ER_OK, status) << "serviceHelper Initialize() failed: " << QCC_StatusText(status);

	m_DeviceAboutAnnouncement = waitForDeviceAboutAnnouncement();
	ASSERT_NE(m_DeviceAboutAnnouncement, nullptr) << "Timed out waiting for About announcement";
	SUCCEED();

	m_ServiceAvailabilityHandler = new ServiceAvailabilityHandler();

	m_AboutProxy = m_ServiceHelper->connectAboutProxy(*m_DeviceAboutAnnouncement);
	ASSERT_NE(m_AboutProxy, nullptr) << "AboutProxy connection failed";
	SUCCEED() << "AboutProxy connected";

	m_ConfigClient = m_ServiceHelper->connectConfigClient(m_SessionId);
	ASSERT_NE(m_ConfigClient, nullptr) << "ConfigClient connection failed";
	SUCCEED() << "ConfigClient connected";

	m_UseEcdheNullInTest = strcmp("Config_v1_02", ::testing::UnitTest::GetInstance()->current_test_info()->name()) == 0 ?
		false : m_IcsMap.at("ICSCO_EcdheNull");

	status = m_ServiceHelper->enableAuthentication("/Keystore",
		m_IcsMap.at("ICSCO_SrpKeyX"), m_IxitMap.at("IXITCO_SrpKeyXPincode"),
		m_IcsMap.at("ICSCO_SrpLogon"), m_IxitMap.at("IXITCO_SrpLogonUser"), m_IxitMap.at("IXITCO_SrpLogonPass"),
		m_UseEcdheNullInTest,
		m_IcsMap.at("ICSCO_EcdhePsk"), m_IxitMap.at("IXITCO_EcdhePskPassword"),
		m_IcsMap.at("ICSCO_EcdheEcdsa"), m_IxitMap.at("IXITCO_EcdheEcdsaPrivateKey"), m_IxitMap.at("IXITCO_EcdheEcdsaCertChain"),
		m_IcsMap.at("ICSCO_EcdheSpeke"), m_IxitMap.at("IXITCO_EcdheSpekePassword"));
	ASSERT_EQ(ER_OK, status);

	if (m_IcsMap.at("ICSCO_SrpKeyX") || m_IcsMap.at("ICSCO_EcdhePsk") || m_IcsMap.at("ICSCO_EcdheSpeke"))
	{
		ASSERT_EQ(ER_OK, resetPasscodeIfNeeded()) << "Resetting passcode failed";
	}

	LOG(INFO) << "test setUp done";
	CLOG(INFO, "raw") << "====================================================";
}

AboutAnnouncementDetails* ConfigTestSuite::waitForDeviceAboutAnnouncement()
{
	return m_ServiceHelper->waitForNextDeviceAnnouncement(atol(
		m_GeneralParameterMap.at("GPCO_AnnouncementTimeout").c_str()
		) * 1000);
}

QStatus ConfigTestSuite::resetPasscodeIfNeeded()
{
	QStatus status;
	if ((status = callMethodToCheckAuthentication()) != ER_OK)
	{
		if ((status = setPasscode(NEW_PASSCODE)) != ER_OK)
		{
			if ((status = setPasscode(SINGLE_CHAR_PASSCODE)) != ER_OK)
			{
				status = setPasscode(SPECIAL_CHARS_PASSCODE);
			}
		}
	}

	return status;
}

QStatus ConfigTestSuite::setPasscode(AJ_PCSTR t_passcode)
{
	m_ServiceHelper->clearKeyStore();

	if (m_IcsMap.at("ICSCO_SrpKeyX"))
	{
		m_ServiceHelper->setSrpKeyXPincode(*m_DeviceAboutAnnouncement, t_passcode);
	}

	if (m_IcsMap.at("ICSCO_EcdhePsk"))
	{
		m_ServiceHelper->setEcdhePskPassword(*m_DeviceAboutAnnouncement, t_passcode);
	}

	if (m_IcsMap.at("ICSCO_EcdheSpeke"))
	{
		m_ServiceHelper->setEcdheSpekePassword(*m_DeviceAboutAnnouncement, t_passcode);
	}
	
	QStatus status = callMethodToCheckAuthentication();

	if (status != ER_OK)
	{
		return status;
	}
	else
	{
		if (m_IcsMap.at("ICSCO_EcdheSpeke"))
		{
			changePasscodeAndReconnect(m_DefaultEcdheSpekePassword.c_str());
		}
		else if (m_IcsMap.at("ICSCO_EcdhePsk"))
		{
			changePasscodeAndReconnect(m_DefaultEcdhePskPassword.c_str());
		}
		else if (m_IcsMap.at("ICSCO_SrpKeyX"))
		{
			changePasscodeAndReconnect(m_DefaultSrpKeyXPincode.c_str());
		}
		else
		{
			return ER_FAIL;
		}
		
		return ER_OK;
	}
}

void ConfigTestSuite::TearDown()
{
	CLOG(INFO, "raw") << "====================================================";
	LOG(INFO) << "test tearDown started";

	releaseResources();

	LOG(INFO) << "test tearDown done";
	CLOG(INFO, "raw") << "====================================================";

	storeResults();
}

void ConfigTestSuite::releaseResources()
{
	if (m_AboutProxy != nullptr)
	{
		delete m_AboutProxy;
		m_AboutProxy = nullptr;
	}

	if (m_ConfigClient != nullptr)
	{
		delete m_ConfigClient;
		m_ConfigClient = nullptr;
	}

	if (m_ServiceHelper != nullptr)
	{
		QStatus status = m_ServiceHelper->release();
		EXPECT_EQ(status, ER_OK) << "serviceHelper Release() failed: " << QCC_StatusText(status);
		delete m_ServiceHelper;
		m_ServiceHelper = nullptr;
	}
}

TEST_F(ConfigTestSuite, Config_v1_01)
{
	std::string appIdString = ArrayParser::parseAppId(m_DeviceAboutAnnouncement->getAppId());
	std::string deviceIdString(m_DeviceAboutAnnouncement->getDeviceId());

	if (appIdString.compare(deviceIdString) == 0)
	{
		SUCCEED() << "DUT AppId and DeviceId are equal";
	}
	else
	{
		LOG(INFO) << "NOTE ADDED: Dut AppId: " << appIdString
			<< " and DeviceId: " << deviceIdString << " are not equal";
	}
}

TEST_F(ConfigTestSuite, Config_v1_02)
{
	QStatus status;
	reconnectClients(status);
	ASSERT_EQ(ER_OK, status) << "Clients reconnection returned error: " << QCC_StatusText(status);

	setWrongAuthValues();
	LOG(INFO) << "Attempting to retrieve Version property from Config interface using wrong credentials";

	status = callMethodToCheckAuthentication();
	ASSERT_EQ(ER_AUTH_FAIL, status)
		<< "Calling to GetVersion() method was expected to return ER_AUTH_FAIL status";
	SUCCEED() << "Calling to GetVersion() method returned ER_AUTH_FAIL status";

	bool isPeerAuthenticated = m_ServiceHelper->isPeerAuthenticationSuccessful(*m_DeviceAboutAnnouncement);
	ASSERT_FALSE(isPeerAuthenticated)
		<< "A call to a Config interface method with the wrong passcode must fail authentication";
}

QStatus ConfigTestSuite::callMethodToCheckAuthentication()
{
	int version;
	return m_ConfigClient->GetVersion(m_DeviceAboutAnnouncement->getServiceName().c_str(), version, m_SessionId);
}

void ConfigTestSuite::setWrongAuthValues()
{
	m_ServiceHelper->clearKeyStore();

	if (m_IcsMap.at("ICSCO_SrpKeyX"))
	{
		m_ServiceHelper->setSrpKeyXPincode(*m_DeviceAboutAnnouncement, m_IxitMap.at("IXITCO_SrpKeyXWrongPincode").c_str());
	}

	if (m_IcsMap.at("ICSCO_SrpLogon"))
	{
		m_ServiceHelper->setSrpLogonPass(*m_DeviceAboutAnnouncement, m_IxitMap.at("IXITCO_SrpLogonWrongPass").c_str());
	}

	if (m_IcsMap.at("ICSCO_EcdhePsk"))
	{
		m_ServiceHelper->setEcdhePskPassword(*m_DeviceAboutAnnouncement, m_IxitMap.at("IXITCO_EcdhePskWrongPassword").c_str());
	}

	if (m_IcsMap.at("ICSCO_EcdheEcdsa"))
	{
		m_ServiceHelper->setEcdheEcdsaCredentials(*m_DeviceAboutAnnouncement, 
			m_IxitMap.at("IXITCO_EcdheEcdsaWrongPrivateKey").c_str(), m_IxitMap.at("IXITCO_EcdheEcdsaWrongCertChain").c_str());
	}

	if (m_IcsMap.at("ICSCO_EcdheSpeke"))
	{
		m_ServiceHelper->setEcdheSpekePassword(*m_DeviceAboutAnnouncement, m_IxitMap.at("IXITCO_EcdheSpekeWrongPassword").c_str());
	}
}

TEST_F(ConfigTestSuite, Config_v1_04)
{
	ConfigClient::Configurations configurations;
	ASSERT_EQ(ER_OK, getConfigurations(m_DefaultLanguage.c_str(), configurations))
		<< "Calling GetConfigurations() method failed";
	checkConfigurationsForRequiredFields(configurations);
	checkConsistencyWithAboutAnnouncement(configurations);
}

QStatus ConfigTestSuite::getConfigurations(AJ_PCSTR t_Language, ConfigClient::Configurations& t_Configurations)
{
	t_Configurations.clear();
	return m_ConfigClient->GetConfigurations(m_DeviceAboutAnnouncement->getServiceName().c_str(),
		t_Language, t_Configurations, m_SessionId);
}

void ConfigTestSuite::checkConfigurationsForRequiredFields(const ConfigClient::Configurations& t_Configurations)
{
	LOG(INFO) << "Checking that DeviceName field is present";
	EXPECT_NE(t_Configurations.find(AboutKeys::DEVICE_NAME), t_Configurations.end())
		<< "Required DeviceName field not present in Configuration";
	LOG(INFO) << "Checking that DefaultLanguage field is present";
	EXPECT_NE(t_Configurations.find(AboutKeys::DEFAULT_LANGUAGE), t_Configurations.end())
		<< "Required DeviceName field not present in Configuration";
}

void ConfigTestSuite::checkConsistencyWithAboutAnnouncement(const ConfigClient::Configurations& t_Configurations)
{
	LOG(INFO) << "Checking that DeviceName field matches About announcement";
	EXPECT_EQ(string(t_Configurations.at(AboutKeys::DEVICE_NAME).v_string.str), string(m_DeviceAboutAnnouncement->getDeviceName()))
		<< "DeviceName from GetConfigurations() does not match About announcement";
	LOG(INFO) << "Checking that DefaultLanguage field matches About announcement";
	EXPECT_EQ(string(t_Configurations.at(AboutKeys::DEFAULT_LANGUAGE).v_string.str), string(m_DeviceAboutAnnouncement->getDefaultLanguage()))
		<< "DefaultLanguage from GetConfigurations() does not match About announcement";
}

TEST_F(ConfigTestSuite, Config_v1_05)
{
	ConfigClient::Configurations configurationsWithDefaultLanguage;
	ASSERT_EQ(ER_OK, getConfigurations(m_DefaultLanguage.c_str(), configurationsWithDefaultLanguage))
		<< "Calling GetConfigurations() method with default language failed";

	ConfigClient::Configurations configurationsWithUnspecifiedLanguage;
	ASSERT_EQ(ER_OK, getConfigurations(NULL, configurationsWithUnspecifiedLanguage))
		<< "Calling GetConfigurations() method with unspecified language failed";

	LOG(INFO) << "Checking received parameters when default language";
	checkConfigurationsForRequiredFields(configurationsWithDefaultLanguage);

	LOG(INFO) << "Checking received parameters when unspecified language";
	checkConfigurationsForRequiredFields(configurationsWithUnspecifiedLanguage);

	LOG(INFO) << "Checking that DeviceName and DefaultLanguage from the two GetConfigurations() calls match";
	compareConfigurations(configurationsWithDefaultLanguage,
		configurationsWithUnspecifiedLanguage);
}

void ConfigTestSuite::compareConfigurations(const ajn::services::ConfigClient::Configurations& t_ConfigurationOne,
	const ajn::services::ConfigClient::Configurations& t_ConfigurationTwo)
{
	LOG(INFO) << "Comparing DeviceName";
	compareConfigurationsForField(AboutKeys::DEVICE_NAME, t_ConfigurationOne, t_ConfigurationTwo);
	LOG(INFO) << "Comparing DefaultLanguage";
	compareConfigurationsForField(AboutKeys::DEFAULT_LANGUAGE, t_ConfigurationOne, t_ConfigurationTwo);
}

void ConfigTestSuite::compareConfigurationsForField(AJ_PCSTR t_Field,
	const ajn::services::ConfigClient::Configurations& t_ConfigurationOne,
	const ajn::services::ConfigClient::Configurations& t_ConfigurationTwo)
{
	ASSERT_EQ(string(t_ConfigurationOne.at(t_Field).v_string.str),
		string(t_ConfigurationTwo.at(t_Field).v_string.str))
		<< t_Field << " does not match";
	SUCCEED() << t_Field << " matches";
}

TEST_F(ConfigTestSuite, Config_v1_06)
{
	size_t supportedLanguagesSize = m_DeviceAboutAnnouncement->getAboutData()->GetSupportedLanguages(NULL, 0);
	AJ_PCSTR* supportedLanguages = new AJ_PCSTR[supportedLanguagesSize];
	m_DeviceAboutAnnouncement->getAboutData()->GetSupportedLanguages(supportedLanguages, supportedLanguagesSize);
	
	if (supportedLanguagesSize > 1)
	{
		QStatus status;

		for (size_t i = 0; i < supportedLanguagesSize; ++i)
		{
			ConfigClient::Configurations configurations;
			LOG(INFO) << "Calling GetConfigurations() method with language: " << supportedLanguages[i];
			status = getConfigurations(supportedLanguages[i], configurations);
			ASSERT_EQ(ER_OK, status) << "Calling GetConfigurations() method returned status code: " << QCC_StatusText(status);

			MsgArg aboutDataMsgArg;
			LOG(INFO) << "Calling GetAboutData() method with language: " << supportedLanguages[i];
			status = m_AboutProxy->GetAboutData(supportedLanguages[i], aboutDataMsgArg);
			ASSERT_EQ(ER_OK, status) << "Calling GetAboutData() method returned status code: " << QCC_StatusText(status);
			AboutData aboutData(aboutDataMsgArg, supportedLanguages[i]);

			checkConfigurationsForRequiredFields(configurations);
			checkAboutDataForRequiredFields(aboutData);

			LOG(INFO) << "Comparing Config and About maps for the language: " << supportedLanguages[i];
			compareConfigAndAbout(configurations, aboutData);
		}
	}
	else
	{
		LOG(INFO) << "NOTE ADDED: Only one language is supported";
	}
}

void ConfigTestSuite::checkAboutDataForRequiredFields(AboutData& t_AboutData)
{
	LOG(INFO) << "Checking that DeviceName field is present";
	AJ_PSTR deviceName;
	EXPECT_EQ(t_AboutData.GetDeviceName(&deviceName), ER_OK);

	LOG(INFO) << "Checking that DefaultLanguage field is present";
    AJ_PSTR defaultLanguage;
	EXPECT_EQ(t_AboutData.GetDefaultLanguage(&defaultLanguage), ER_OK);
}

void ConfigTestSuite::compareConfigAndAbout(const ConfigClient::Configurations& t_Configurations,
	const AboutData& t_AboutData)
{
	LOG(INFO) << "Comparing DeviceName";
	compareConfigAndAboutForField(AboutKeys::DEVICE_NAME, t_Configurations, t_AboutData);

	LOG(INFO) << "Comparing DefaultLanguage";
	compareConfigAndAboutForField(AboutKeys::DEFAULT_LANGUAGE, t_Configurations, t_AboutData);
}

void ConfigTestSuite::compareConfigAndAboutForField(AJ_PCSTR t_Field,
	const ConfigClient::Configurations& t_Configurations,
	const AboutData& t_AboutData)
{
	MsgArg* fieldValue;
	t_AboutData.GetField(t_Field, fieldValue);
	ASSERT_STREQ(fieldValue->v_string.str, t_Configurations.at(t_Field).v_string.str)
		<< t_Field << " does not match";
	SUCCEED() << t_Field << " matches";
}

TEST_F(ConfigTestSuite, Config_v1_07)
{
	ConfigClient::Configurations configurations;
	QStatus status = getConfigurations(INVALID_LANGUAGE_CODE, configurations);

	ASSERT_NE(status, ER_OK) << "Calling GetConfigurations() on the Config interface with INVALID language did not return an error status";
	LOG(INFO) << "Calling GetConfigurations() with INVALID language returned an error status";
	ASSERT_EQ(ER_BUS_REPLY_IS_ERROR_MESSAGE, status) << "Returned error was not ER_BUS_REPLY_IS_ERROR_MESSAGE";
}

TEST_F(ConfigTestSuite, Config_v1_08)
{
	LOG(INFO) << AboutKeys::DEVICE_NAME;
	QStatus status = testUpdateConfigurations(AboutKeys::DEVICE_NAME, NEW_DEVICE_NAME);
	ASSERT_EQ(ER_OK, status) << "Testing UpdateConfigurations() returned status code: " << QCC_StatusText(status);
}

QStatus ConfigTestSuite::testUpdateConfigurations(AJ_PCSTR t_Field, AJ_PCSTR t_NewValue)
{
	AJ_PCSTR originalValue;

	if (string(t_Field).compare(AboutKeys::DEVICE_NAME) == 0)
	{
		originalValue = m_DeviceAboutAnnouncement->getDeviceName();
	}
	else
	{
		originalValue = m_DefaultLanguage.c_str();
	}

	QStatus status = updateConfigurationsAndVerifyResult(t_Field, t_NewValue);
	if (status != ER_OK)
	{
		return status;
	}
	else
	{
		status = updateConfigurationsAndVerifyResult(t_Field, originalValue);
		return status;
	}
}

QStatus ConfigTestSuite::updateConfigurationsAndVerifyResult(AJ_PCSTR t_Field, AJ_PCSTR t_Value)
{
	ConfigClient::Configurations configurations;
	configurations.insert(std::pair<qcc::String, ajn::MsgArg>(t_Field, MsgArg("s", t_Value)));
	QStatus status = updateConfigurations(m_DefaultLanguage.c_str(), configurations); 

	if (status != ER_OK)
	{
		return status;
	}
	else
	{
		waitForNextAnnouncementAndVerifyFieldValue(t_Field, t_Value);
	}

	return ER_OK;
}

QStatus ConfigTestSuite::updateConfigurations(AJ_PCSTR t_Language, const ConfigClient::Configurations& t_Configurations)
{
	return m_ConfigClient->UpdateConfigurations(m_DeviceAboutAnnouncement->getServiceName().c_str(),
		t_Language, t_Configurations, m_SessionId);
}

void ConfigTestSuite::waitForNextAnnouncementAndVerifyFieldValue(AJ_PCSTR t_Field, AJ_PCSTR t_Value)
{
	m_DeviceAboutAnnouncement = waitForNextAnnouncementAndCheckFieldValue(t_Field, t_Value);

	ConfigClient::Configurations configurations;
	QStatus status = getConfigurations(m_DefaultLanguage.c_str(), configurations);

	MsgArg aboutDataMsgArg;
	m_AboutProxy->GetAboutData(m_DefaultLanguage.c_str(), aboutDataMsgArg);
	AboutData aboutData(aboutDataMsgArg, m_DefaultLanguage.c_str());

	verifyValueForConfigAndAbout(configurations, aboutData, t_Field, t_Value);
}

AboutAnnouncementDetails* ConfigTestSuite::waitForNextAnnouncementAndCheckFieldValue(AJ_PCSTR t_Field, AJ_PCSTR t_Value)
{
	LOG(INFO) << "Waiting for updating About announcement";
	AboutAnnouncementDetails* deviceAboutAnnouncement = waitForDeviceAboutAnnouncement();

	if (string(t_Field).compare(AboutKeys::DEVICE_NAME) == 0)
	{
		EXPECT_STREQ(t_Value, deviceAboutAnnouncement->getDeviceName())
			<< "Received About announcement did not contain expected DeviceName";
	}
	else
	{
		EXPECT_STREQ(t_Value, deviceAboutAnnouncement->getDefaultLanguage())
			<< "Received About announcement did not contain expected DefaultLanguage";
	}

	return deviceAboutAnnouncement;
}

void ConfigTestSuite::verifyValueForConfigAndAbout(const ConfigClient::Configurations& t_Configurations,
	const AboutData& t_AboutData, AJ_PCSTR t_Field, AJ_PCSTR t_ExpectedValue)
{
	LOG(INFO) << "Checking if " << t_Field << " from GetConfigurations() matches expected value";
	EXPECT_STREQ(t_ExpectedValue, t_Configurations.at(t_Field).v_string.str)
		<< "Value for " << t_Field << " retrieved from GetConfigurations() does not match expected value";

	LOG(INFO) << "Checking if " << t_Field << " from GetAboutData() matches expected value";
	MsgArg* value;
	t_AboutData.GetField(t_Field, value);
	EXPECT_STREQ(t_ExpectedValue, value->v_string.str)
		<< "Value for " << t_Field << " retrieved from GetAboutData() does not match expected value";
}

TEST_F(ConfigTestSuite, Config_v1_12)
{
	testUpdateConfigurations(AboutKeys::DEVICE_NAME, getDeviceNameWithSpecialCharacters().c_str());
}

string ConfigTestSuite::getDeviceNameWithSpecialCharacters()
{
	string specialCharacterString;
	appendChars(specialCharacterString, 33, 47);
	appendChars(specialCharacterString, 58, 64);
	appendChars(specialCharacterString, 91, 96);
	appendChars(specialCharacterString, 123, 126);
	return specialCharacterString;
}

void ConfigTestSuite::appendChars(string& str, int startIdx, int endIdx)
{
	for (int asciiCode = startIdx; asciiCode < endIdx; asciiCode++)
	{
		str.push_back(static_cast<char>(asciiCode));
	}
}

TEST_F(ConfigTestSuite, Config_v1_13)
{
	ConfigClient::Configurations configurations;
	configurations.insert(std::pair<qcc::String, ajn::MsgArg>(AboutKeys::DEVICE_NAME, 
		MsgArg("s", m_DeviceAboutAnnouncement->getDeviceName())));
	
	QStatus status = updateConfigurations(INVALID_LANGUAGE_CODE, configurations); 
	ASSERT_NE(status, ER_OK)
		<< "Calling UpdateConfigurations() on the Config interface with an unsupported language must return an error status";
	
	LOG(INFO) << "Calling UpdateConfigurations() on the Config interface returned an error status";
	ASSERT_EQ(status, ER_BUS_REPLY_IS_ERROR_MESSAGE)
		<< "Calling UpdateConfigurations() on the Config interface with an unsupported language did not return the expected error status";
	
	SUCCEED() << "Calling UpdateConfigurations() on the Config interface with an unsupported language returned ER_BUS_REPLY_IS_ERROR_MESSAGE";
}

TEST_F(ConfigTestSuite, Config_v1_14)
{
	size_t supportedLanguagesSize = m_DeviceAboutAnnouncement->getAboutData()->GetSupportedLanguages(NULL, 0);
	AJ_PCSTR* supportedLanguages = new AJ_PCSTR[supportedLanguagesSize];
	m_DeviceAboutAnnouncement->getAboutData()->GetSupportedLanguages(supportedLanguages, supportedLanguagesSize);

	if (supportedLanguagesSize > 1)
	{
		AJ_PCSTR newLanguage = (strcmp(supportedLanguages[0], m_DefaultLanguage.c_str()) == 0)
			? supportedLanguages[1] : supportedLanguages[0];
		testUpdateConfigurations(AboutKeys::DEFAULT_LANGUAGE, newLanguage);
	}
	else
	{
		LOG(INFO) << "NOTE ADDED: Only one language supported";
	}
}

TEST_F(ConfigTestSuite, Config_v1_15)
{
	ConfigClient::Configurations configurations;
	configurations.insert(std::pair<qcc::String, ajn::MsgArg>(AboutKeys::DEFAULT_LANGUAGE,
		MsgArg("s", INVALID_LANGUAGE_CODE)));
	
	QStatus status = updateConfigurations(m_DefaultLanguage.c_str(), configurations);
	ASSERT_NE(status, ER_OK)
		<< "Calling UpdateConfigurations() to set the DefaultLanguage to an unsupported language must return an error status";

	LOG(INFO) << "Calling UpdateConfigurations() to set the DefaultLanguage to an unsupported language returned an error status";
	ASSERT_EQ(status, ER_BUS_REPLY_IS_ERROR_MESSAGE)
		<< "Calling UpdateConfigurations() to set the DefaultLanguage to an unsupported language did not return the expected error status";
	SUCCEED() << "Calling UpdateConfigurations() to set the DefaultLanguage to an unsupported language returned ER_BUS_REPLY_IS_ERROR_MESSAGE";
}

TEST_F(ConfigTestSuite, Config_v1_16)
{
	ConfigClient::Configurations configurations;
	configurations.insert(std::pair<qcc::String, ajn::MsgArg>(AboutKeys::DEFAULT_LANGUAGE,
		MsgArg("s", "")));
	
	QStatus status = updateConfigurations(m_DefaultLanguage.c_str(), configurations);
	ASSERT_NE(status, ER_OK)
		<< "Calling UpdateConfigurations() to set the DefaultLanguage to an unspecified language must return an error status";
	
	LOG(INFO) << "Calling UpdateConfigurations() to set the DefaultLanguage to an unspecified language returned an error status";
	ASSERT_EQ(status, ER_BUS_REPLY_IS_ERROR_MESSAGE)
		<< "Calling UpdateConfigurations() to set the DefaultLanguage to an unspecified language did not return the expected error status";
	SUCCEED() << "Calling UpdateConfigurations() to set the DefaultLanguage to an unspecified language returned ER_BUS_REPLY_IS_ERROR_MESSAGE";
}

TEST_F(ConfigTestSuite, Config_v1_19)
{
	ConfigClient::Configurations configurations;
	configurations.insert(std::pair<qcc::String, ajn::MsgArg>(INVALID_LANGUAGE_CODE,
		MsgArg("s", INVALID_LANGUAGE_CODE)));
	QStatus status = updateConfigurations(m_DefaultLanguage.c_str(), configurations);

	ASSERT_NE(status, ER_OK)
		<< "Calling UpdateConfigurations() to set an invalid field must return an error status";
	LOG(INFO) << "Calling UpdateConfigurations() to set an invalid field returned an error status";
	ASSERT_EQ(status, ER_BUS_REPLY_IS_ERROR_MESSAGE)
		<< "Calling UpdateConfigurations() to set an invalid field did not return the expected error status";
	SUCCEED() << "Calling UpdateConfigurations() to set an invalid field returned ER_BUS_REPLY_IS_ERROR_MESSAGE";
}

TEST_F(ConfigTestSuite, Config_v1_20)
{
	vector<qcc::String> fieldsToReset = { AboutKeys::DEVICE_NAME };
	QStatus status = resetConfigurations(m_DefaultLanguage.c_str(), fieldsToReset);
	ASSERT_EQ(ER_OK, status) << "Calling ResetConfigurations() method returned status code: " << QCC_StatusText(status);

	m_DeviceAboutAnnouncement = waitForNextAnnouncementAndCheckFieldValue(AboutKeys::DEFAULT_LANGUAGE, m_DefaultLanguage.c_str());

	MsgArg aboutDataMsgArg;
	status = m_AboutProxy->GetAboutData(m_DefaultLanguage.c_str(), aboutDataMsgArg);
	ASSERT_EQ(ER_OK, status) << "Calling GetAboutData() method returned status code: " << QCC_StatusText(status);
	AboutData aboutData(aboutDataMsgArg, m_DefaultLanguage.c_str());

    AJ_PSTR originalDeviceName;
	status = aboutData.GetDeviceName(&originalDeviceName, m_DefaultLanguage.c_str());
	ASSERT_EQ(ER_OK, status) << "Retrieving DeviceName returned status code: " << QCC_StatusText(status);

	std::string originalDeviceNameStr(originalDeviceName);
	LOG(INFO) << "Original DeviceName: " << originalDeviceNameStr;

	m_ServiceHelper->clearQueuedDeviceAnnouncements();

	ConfigClient::Configurations configurations;
	configurations.insert(std::pair<qcc::String, ajn::MsgArg>(AboutKeys::DEVICE_NAME,
		MsgArg("s", NEW_DEVICE_NAME)));
	status = updateConfigurations(m_DefaultLanguage.c_str(), configurations);
	ASSERT_EQ(ER_OK, status) << "Calling UpdateConfigurations() returned status code: " << QCC_StatusText(status);

	m_DeviceAboutAnnouncement = waitForNextAnnouncementAndCheckFieldValue(AboutKeys::DEVICE_NAME, NEW_DEVICE_NAME);

	configurations.clear();
	status = getConfigurations(m_DefaultLanguage.c_str(), configurations);
	ASSERT_EQ(ER_OK, status) << "Calling GetConfigurations() returned status code: " << QCC_StatusText(status);

	status = m_AboutProxy->GetAboutData(m_DefaultLanguage.c_str(), aboutDataMsgArg);
	ASSERT_EQ(ER_OK, status) << "Calling GetAboutData() method returned status code: " << QCC_StatusText(status);
	aboutData = AboutData(aboutDataMsgArg, m_DefaultLanguage.c_str());

	verifyValueForConfigAndAbout(configurations, aboutData, AboutKeys::DEVICE_NAME, NEW_DEVICE_NAME);

	status = resetConfigurations(m_DefaultLanguage.c_str(), fieldsToReset);
	ASSERT_EQ(ER_OK, status) << "Calling ResetConfigurations() returned status code: " << QCC_StatusText(status);

	m_DeviceAboutAnnouncement = waitForNextAnnouncementAndCheckFieldValue(AboutKeys::DEVICE_NAME, originalDeviceNameStr.c_str());

	status = getConfigurations(m_DefaultLanguage.c_str(), configurations);
	ASSERT_EQ(ER_OK, status) << "Calling GetConfigurations() returned status code: " << QCC_StatusText(status);

	status = m_AboutProxy->GetAboutData(m_DefaultLanguage.c_str(), aboutDataMsgArg);
	ASSERT_EQ(ER_OK, status) << "Calling GetAboutData() returned status code: " << QCC_StatusText(status);
	aboutData = AboutData(aboutDataMsgArg, m_DefaultLanguage.c_str());

	verifyValueForConfigAndAbout(configurations, aboutData, AboutKeys::DEVICE_NAME, originalDeviceNameStr.c_str());
}

QStatus ConfigTestSuite::resetConfigurations(AJ_PCSTR t_Language, const vector<qcc::String>& t_FieldsToReset)
{
	return m_ConfigClient->ResetConfigurations(m_DeviceAboutAnnouncement->getServiceName().c_str(),
		t_Language, t_FieldsToReset, m_SessionId);
}

TEST_F(ConfigTestSuite, Config_v1_21)
{
	vector<qcc::String> fieldsToReset = { AboutKeys::DEFAULT_LANGUAGE };
	QStatus status = resetConfigurations(m_DefaultLanguage.c_str(), fieldsToReset);
	ASSERT_EQ(ER_OK, status) << "Calling ResetConfigurations() returned status code: " << QCC_StatusText(status);

	m_DeviceAboutAnnouncement = waitForNextAnnouncementAndCheckFieldValue(AboutKeys::DEFAULT_LANGUAGE, m_DefaultLanguage.c_str());

	ConfigClient::Configurations configurations;
	status = getConfigurations(m_DefaultLanguage.c_str(), configurations);
	ASSERT_EQ(ER_OK, status) << "Calling GetConfigurations() returned status code: " << QCC_StatusText(status);

	MsgArg aboutDataMsgArg;
	status = m_AboutProxy->GetAboutData(m_DefaultLanguage.c_str(), aboutDataMsgArg);
	ASSERT_EQ(ER_OK, status) << "Calling GetAboutData() returned status code: " << QCC_StatusText(status);
	AboutData aboutData(aboutDataMsgArg, m_DefaultLanguage.c_str());

	compareConfigAndAboutForField(AboutKeys::DEFAULT_LANGUAGE, configurations, aboutData);
}

TEST_F(ConfigTestSuite, Config_v1_22)
{
	size_t supportedLanguagesSize = m_DeviceAboutAnnouncement->getAboutData()->GetSupportedLanguages(NULL, 0);
	AJ_PCSTR* supportedLanguages = new AJ_PCSTR[supportedLanguagesSize];
	m_DeviceAboutAnnouncement->getAboutData()->GetSupportedLanguages(supportedLanguages, supportedLanguagesSize);

	if (supportedLanguagesSize > 1)
	{
		AJ_PCSTR newLanguage = (strcmp(supportedLanguages[0], m_DefaultLanguage.c_str()) == 0)
			? supportedLanguages[1] : supportedLanguages[0];

		vector<qcc::String> fieldsToReset = { AboutKeys::DEFAULT_LANGUAGE };
		QStatus status = resetConfigurations(m_DefaultLanguage.c_str(), fieldsToReset);
		ASSERT_EQ(ER_OK, status) << "Calling ResetConfigurations() returned status code: " << QCC_StatusText(status);

		ConfigClient::Configurations configurations;
		status = getConfigurations(NULL, configurations);
		ASSERT_EQ(ER_OK, status) << "Calling GetConfigurations() returned status code: " << QCC_StatusText(status);

		MsgArg aboutDataMsgArg;
		m_AboutProxy->GetAboutData(NULL, aboutDataMsgArg);
		AboutData aboutData(aboutDataMsgArg);

		compareConfigAndAboutForField(AboutKeys::DEFAULT_LANGUAGE, configurations, aboutData);

        AJ_PSTR originalDefaultLanguage;
		aboutData.GetDefaultLanguage(&originalDefaultLanguage);

		LOG(INFO) << "Original DefaultLanguage: " << originalDefaultLanguage;

		m_ServiceHelper->clearQueuedDeviceAnnouncements();

		configurations.clear();
		configurations.insert(std::pair<qcc::String, ajn::MsgArg>(AboutKeys::DEFAULT_LANGUAGE,
			MsgArg("s", newLanguage)));
		status = updateConfigurations(m_DefaultLanguage.c_str(), configurations);

		ASSERT_EQ(ER_OK, status) << "Calling UpdateConfigurations() returned error: " << QCC_StatusText(status);

		m_DeviceAboutAnnouncement = waitForNextAnnouncementAndCheckFieldValue(AboutKeys::DEFAULT_LANGUAGE, newLanguage);
		
		status = getConfigurations(NULL, configurations);
		ASSERT_EQ(ER_OK, status) << "Calling GetConfigurations() returned error: " << QCC_StatusText(status);

		m_AboutProxy->GetAboutData(NULL, aboutDataMsgArg);
		aboutData = AboutData(aboutDataMsgArg);

		verifyValueForConfigAndAbout(configurations, aboutData, AboutKeys::DEFAULT_LANGUAGE, newLanguage);

        AJ_PSTR temporalDefaultLanguage;
		aboutData.GetDefaultLanguage(&temporalDefaultLanguage);

		status = resetConfigurations(m_DefaultLanguage.c_str(), fieldsToReset);
		ASSERT_EQ(ER_OK, status) << "Calling ResetConfigurations() returned error: " << QCC_StatusText(status);

		m_DeviceAboutAnnouncement = waitForNextAnnouncementAndCheckFieldValue(AboutKeys::DEFAULT_LANGUAGE, originalDefaultLanguage);

		status = getConfigurations(NULL, configurations);
		ASSERT_EQ(ER_OK, status) << "Calling GetConfigurations() returned error: " << QCC_StatusText(status);

		m_AboutProxy->GetAboutData(NULL, aboutDataMsgArg);
		aboutData = AboutData(aboutDataMsgArg);

		verifyValueForConfigAndAbout(configurations, aboutData, AboutKeys::DEFAULT_LANGUAGE, originalDefaultLanguage);
	}
	else
	{
		LOG(INFO) << "NOTE ADDED: Only one language is supported";
	}
}

TEST_F(ConfigTestSuite, Config_v1_24)
{
	vector<qcc::String> fieldsToReset = { AboutKeys::DEVICE_NAME };
	QStatus status = resetConfigurations(INVALID_LANGUAGE_CODE, fieldsToReset);
	ASSERT_NE(ER_OK, status)
		<< "Calling ResetConfigurations() with the DefaultLanguage set to an unsupported language must return an error status";

	LOG(INFO) << "Calling ResetConfigurations() with the DefaultLanguage set to an unsupported language returned an error status";
	ASSERT_EQ(ER_BUS_REPLY_IS_ERROR_MESSAGE, status)
		<< "Calling ResetConfigurations() with the DefaultLanguage set to an unsupported language did not return the expected error status";
	SUCCEED() << "Calling ResetConfigurations() with the DefaultLanguage set to an unsupported language returned ER_BUS_REPLY_IS_ERROR_MESSAGE";
}

TEST_F(ConfigTestSuite, Config_v1_25)
{
	vector<qcc::String> fieldsToReset = { INVALID_LANGUAGE_CODE };
	QStatus status = resetConfigurations(m_DefaultLanguage.c_str(), fieldsToReset);
	ASSERT_NE(ER_OK, status)
		<< "Calling ResetConfigurations() to set an invalid field must return an error status";

	LOG(INFO) << "Calling UpdateConfigurations() to set an invalid field returned an error status";
	ASSERT_EQ(ER_BUS_REPLY_IS_ERROR_MESSAGE, status)
		<< "Calling ResetConfigurations() to set an invalid field did not return the expected error status";
	SUCCEED() << "Calling UpdateConfigurations() to set an invalid field returned ER_BUS_REPLY_IS_ERROR_MESSAGE";
}

TEST_F(ConfigTestSuite, Config_v1_26)
{
	QStatus status = callRestartOnConfig();
	ASSERT_EQ(ER_OK, status) << "Calling Restart() method returned status code: " << QCC_StatusText(status);
	loseSessionOrWait();
	
	m_ServiceAvailabilityHandler = new ServiceAvailabilityHandler();
	m_AboutProxy = m_ServiceHelper->connectAboutProxy(*m_DeviceAboutAnnouncement);

	ASSERT_NE(m_AboutProxy, nullptr) << "AboutProxy connection failed";
	SUCCEED() << "AboutProxy connected";
}

QStatus ConfigTestSuite::callRestartOnConfig()
{
	LOG(INFO) << "Calling Restart() on Config interface";
	return m_ConfigClient->Restart(m_DeviceAboutAnnouncement->getServiceName().c_str(), m_SessionId);
}

void ConfigTestSuite::loseSessionOrWait()
{
	if (m_ServiceAvailabilityHandler->waitForSessionLost(atol(m_GeneralParameterMap.at("GPCF_SessionLost").c_str()) * 1000))
	{
		m_DeviceAboutAnnouncement = waitForDeviceAboutAnnouncement();
	}
	else
	{
		LOG(INFO) << "Failed to lose session, waiting before attempting to reconnect";
		std::this_thread::sleep_for(std::chrono::milliseconds(CONFIG_CLIENT_RECONNECT_WAIT_TIME));
	}
}

TEST_F(ConfigTestSuite, Config_v1_27)
{
    AJ_PSTR originalDeviceName = m_DeviceAboutAnnouncement->getDeviceName();
	updateConfigurationsAndVerifyResult(AboutKeys::DEVICE_NAME, NEW_DEVICE_NAME);

	callRestartOnConfig();
	loseSessionOrWait();
	
	QStatus status;
	reconnectClients(status);
	ASSERT_EQ(ER_OK, status) << "Reconnecting clients returned status code: " << QCC_StatusText(status);

	ConfigClient::Configurations configurations;
	status = getConfigurations(m_DefaultLanguage.c_str(), configurations);
	ASSERT_EQ(ER_OK, status) << "Calling GetConfigurations() method returned status code: " << QCC_StatusText(status);

	MsgArg aboutDataMsgArg;
	m_AboutProxy->GetAboutData(m_DefaultLanguage.c_str(), aboutDataMsgArg);
	ASSERT_EQ(ER_OK, status) << "Calling GetAboutData() method returned status code: " << QCC_StatusText(status);
	AboutData aboutData(aboutDataMsgArg);

	verifyValueForConfigAndAbout(configurations, aboutData, AboutKeys::DEVICE_NAME, NEW_DEVICE_NAME);
	updateConfigurationsAndVerifyResult(AboutKeys::DEVICE_NAME, originalDeviceName);
}

void ConfigTestSuite::reconnectClients(QStatus& status)
{
	releaseResources();
	
	m_ServiceHelper = new ServiceHelper();
	status = m_ServiceHelper->initializeClient(BUS_APPLICATION_NAME, m_DutDeviceId, m_DutAppId);
	ASSERT_EQ(ER_OK, status) << "serviceHelper Initialize() failed: " << QCC_StatusText(status);

	m_DeviceAboutAnnouncement = waitForDeviceAboutAnnouncement();
	ASSERT_NE(m_DeviceAboutAnnouncement, nullptr) << "Timed out waiting for About announcement";
	SUCCEED();

	m_ServiceAvailabilityHandler = new ServiceAvailabilityHandler();
	m_AboutProxy = m_ServiceHelper->connectAboutProxy(*m_DeviceAboutAnnouncement);

	ASSERT_NE(m_AboutProxy, nullptr) << "AboutProxy connection failed";
	SUCCEED() << "AboutProxy connected";

	m_ConfigClient = m_ServiceHelper->connectConfigClient(m_SessionId);

	ASSERT_NE(m_ConfigClient, nullptr) << "ConfigClient connection failed";
	SUCCEED() << "ConfigClient connected";

	status = m_ServiceHelper->enableAuthentication("/Keystore",
		m_IcsMap.at("ICSCO_SrpKeyX"), m_IxitMap.at("IXITCO_SrpKeyXPincode"),
		m_IcsMap.at("ICSCO_SrpLogon"), m_IxitMap.at("IXITCO_SrpLogonUser"), m_IxitMap.at("IXITCO_SrpLogonPass"),
		m_UseEcdheNullInTest,
		m_IcsMap.at("ICSCO_EcdhePsk"), m_IxitMap.at("IXITCO_EcdhePskPassword"),
		m_IcsMap.at("ICSCO_EcdheEcdsa"), m_IxitMap.at("IXITCO_EcdheEcdsaPrivateKey"), m_IxitMap.at("IXITCO_EcdheEcdsaCertChain"),
		m_IcsMap.at("ICSCO_EcdheSpeke"), m_IxitMap.at("IXITCO_EcdheSpekePassword"));
	ASSERT_EQ(ER_OK, status);
}

TEST_F(ConfigTestSuite, Config_v1_29)
{
	testChangePasscode(NEW_PASSCODE);
}

void ConfigTestSuite::testChangePasscode(AJ_PCSTR t_Passcode)
{
	changePasscodeAndReconnect(t_Passcode);
	restorePasswordInStores();
}

void ConfigTestSuite::changePasscodeAndReconnect(AJ_PCSTR t_Passcode)
{
	LOG(INFO) << "Calling SetPasscode() on Config with passcode " << t_Passcode;
	QStatus status = m_ConfigClient->SetPasscode(m_DeviceAboutAnnouncement->getServiceName().c_str(),
		"MyDaemonRealm", qcc::String(t_Passcode).length(), (const uint8_t*)t_Passcode, m_SessionId);
	ASSERT_EQ(ER_OK, status) << "Calling SetPasscode() on Config interface returned status code: "
		<< QCC_StatusText(status);

	reconnectClients(status);
	ASSERT_EQ(ER_OK, status) << "Reconnecting clients returned status code: " << QCC_StatusText(status);

	m_ServiceHelper->clearKeyStore();

	changePasswordInStores(t_Passcode);

	ASSERT_EQ(ER_OK, callMethodToCheckAuthentication()) << "Checking authentication failed";
}

void ConfigTestSuite::restorePasswordInStores()
{
	AJ_PCSTR passcode = "";
	if (m_IcsMap.at("ICSCO_EcdheSpeke"))
	{
		passcode = m_DefaultEcdheSpekePassword.c_str();
	}
	else if (m_IcsMap.at("ICSCO_EcdhePsk"))
	{
		passcode = m_DefaultEcdhePskPassword.c_str();
	}
	else if (m_IcsMap.at("ICSCO_SrpKeyX"))
	{
		passcode = m_DefaultSrpKeyXPincode.c_str();
	}

	changePasscodeAndReconnect(passcode);
}

void ConfigTestSuite::changePasswordInStores(AJ_PCSTR t_Passcode)
{
	m_ServiceHelper->clearKeyStore();

	if (m_IcsMap.at("ICSCO_SrpKeyX"))
	{
		m_ServiceHelper->setSrpKeyXPincode(*m_DeviceAboutAnnouncement, t_Passcode);
	}

	if (m_IcsMap.at("ICSCO_EcdhePsk"))
	{
		m_ServiceHelper->setEcdhePskPassword(*m_DeviceAboutAnnouncement, t_Passcode);
	}

	if (m_IcsMap.at("ICSCO_EcdheSpeke"))
	{
		m_ServiceHelper->setEcdheSpekePassword(*m_DeviceAboutAnnouncement, t_Passcode);
	}
}

TEST_F(ConfigTestSuite, Config_v1_30)
{
	testChangePasscode(SINGLE_CHAR_PASSCODE);
}

TEST_F(ConfigTestSuite, Config_v1_31)
{
	testChangePasscode(SPECIAL_CHARS_PASSCODE);
}

TEST_F(ConfigTestSuite, Config_v1_32)
{
	changePasscodeAndReconnect(NEW_PASSCODE);
	callRestartOnConfig();
	loseSessionOrWait();
	
	QStatus status;
	reconnectClients(status);
	ASSERT_EQ(ER_OK, status) << "Reconnecting clients returned status code: " << QCC_StatusText(status);

	m_ServiceHelper->clearKeyStore();
	changePasswordInStores(NEW_PASSCODE);
	ASSERT_EQ(ER_OK, callMethodToCheckAuthentication());
	restorePasswordInStores();
}

TEST_F(ConfigTestSuite, Config_v1_33)
{
	if (m_DeviceAboutAnnouncement->supportsInterface("org.alljoyn.Onboarding"))
	{
		LOG(INFO) << "NOTE ADDED: The device supports Onboarding so this Test Case is not applicable";
	}
	else
	{
		vector<qcc::String> fieldsToReset = { 
			AboutKeys::DEVICE_NAME,
			AboutKeys::DEFAULT_LANGUAGE
		};

		QStatus status = resetConfigurations(m_DefaultLanguage.c_str(), fieldsToReset);
		ASSERT_EQ(ER_OK, status) << "Calling ResetConfigurations() returned status code: " << QCC_StatusText(status);

		ConfigClient::Configurations configurations;
		status = getConfigurations(NULL, configurations);
		ASSERT_EQ(ER_OK, status) << "Calling GetConfigurations() returned status code: " << QCC_StatusText(status);

		std::string deviceNameBeforeReset = std::string(configurations.at(AboutKeys::DEVICE_NAME).v_string.str);
		std::string defaultLanguageBeforeReset = std::string(configurations.at(AboutKeys::DEFAULT_LANGUAGE).v_string.str);

		m_ServiceHelper->clearQueuedDeviceAnnouncements();

		LOG(INFO) << "Calling FactoryReset() on Config";
		status = m_ConfigClient->FactoryReset(m_DeviceAboutAnnouncement->getServiceName().c_str(),
			m_SessionId);
		ASSERT_NE(status, ER_FEATURE_NOT_AVAILABLE) << "FactoryReset() method is not a supported feature!";
		ASSERT_EQ(ER_OK, status) << "Calling FactoryReset() method returned error: " << QCC_StatusText(status);

		loseSessionOrWait();

		MessageBox(GetConsoleWindow(),
			L"FactoryReset() has been called on the DUT.\nPlease Onboard the device to the Personal AP (if needed) and then click OK",
			L"Config-v1-33",
			MB_OK | MB_ICONINFORMATION | MB_APPLMODAL);

		m_DeviceAboutAnnouncement = waitForDeviceAboutAnnouncement();
		ASSERT_NE(m_DeviceAboutAnnouncement, nullptr);

		reconnectClients(status);
		ASSERT_EQ(ER_OK, status);

		status = getConfigurations(NULL, configurations);
		ASSERT_EQ(ER_OK, status) << "Calling GetConfigurations() returned error: " << QCC_StatusText(status);

		AJ_PCSTR deviceNameAfterReset = configurations.at(AboutKeys::DEVICE_NAME).v_string.str;
		AJ_PCSTR defaultLanguageAfterReset = configurations.at(AboutKeys::DEFAULT_LANGUAGE).v_string.str;

		EXPECT_STREQ(deviceNameBeforeReset.c_str(), deviceNameAfterReset)
			<< "FactoryReset() set the DeviceName to a different value than ResetConfigurations()";
		EXPECT_STREQ(defaultLanguageBeforeReset.c_str(), defaultLanguageAfterReset)
			<< "FactoryReset() set the DefaultLanguage to a different value than ResetConfigurations()";
	}
}