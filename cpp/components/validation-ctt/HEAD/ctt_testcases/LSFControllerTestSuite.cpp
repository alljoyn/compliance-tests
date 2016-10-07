/******************************************************************************
* * Copyright (c) Open Connectivity Foundation (OCF) and AllJoyn Open
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
#include "LSFControllerTestSuite.h"

#include <thread>

#include "AboutAnnouncementDetails.h"
#include "ArrayParser.h"
#include "ControllerServiceBusObject.h"
#include "InterfaceValidator.h"

#include <alljoyn\AllJoynStd.h>

using namespace ajn;
using namespace std;

#define WAIT_MILLISECONDS(x) std::this_thread::sleep_for(std::chrono::milliseconds(x));

const char* LSFControllerTestSuite::BUS_APPLICATION_NAME = "LSFControllerTestSuite";
const char* LSFControllerTestSuite::CONTROLLER_BUS_OBJECT_PATH = "/org/allseen/LSF/ControllerService";

const char* LSFControllerTestSuite::CONTROLLERSERVICE_INTERFACE_NAME = "org.allseen.LSF.ControllerService";
const char* LSFControllerTestSuite::LAMP_INTERFACE_NAME = "org.allseen.LSF.ControllerService.Lamp";
const char* LSFControllerTestSuite::LAMPGROUP_INTERFACE_NAME = "org.allseen.LSF.ControllerService.LampGroup";
const char* LSFControllerTestSuite::PRESET_INTERFACE_NAME = "org.allseen.LSF.ControllerService.Preset";
const char* LSFControllerTestSuite::SCENE_INTERFACE_NAME = "org.allseen.LSF.ControllerService.Scene";
const char* LSFControllerTestSuite::MASTERSCENE_INTERFACE_NAME = "org.allseen.LSF.ControllerService.MasterScene";
const char* LSFControllerTestSuite::LEADER_ELECTION_INTERFACE_NAME = "org.allseen.LeaderElectionAndStateSync";

LSFControllerTestSuite::LSFControllerTestSuite() : IOManager(ServiceFramework::LSF_CONTROLLER)
{

}

void LSFControllerTestSuite::SetUp()
{
	CLOG(INFO, "raw") << "====================================================";
	LOG(INFO) << "test setUp started";

	m_DutDeviceId = m_IxitMap.at("IXITCO_DeviceId");
	m_DutAppId = ArrayParser::parseAppIdFromString(m_IxitMap.at("IXITCO_AppId"));

	m_ServiceHelper = new ServiceHelper();

	QStatus status = m_ServiceHelper->initializeClient(BUS_APPLICATION_NAME, m_DutDeviceId, m_DutAppId);
	ASSERT_EQ(status, ER_OK) << "serviceHelper Initialize() failed: " << QCC_StatusText(status);

	m_DeviceAboutAnnouncement =
		m_ServiceHelper->waitForNextDeviceAnnouncement(atol(
		m_GeneralParameterMap.at("GPCO_AnnouncementTimeout").c_str()
		) * 1000);

	ASSERT_NE(m_DeviceAboutAnnouncement, nullptr) << "Timed out waiting for About announcement";
	SUCCEED();

	m_AboutProxy = m_ServiceHelper->connectAboutProxy(*m_DeviceAboutAnnouncement);
	ASSERT_NE(m_AboutProxy, nullptr) << "AboutProxy connection failed";
	SUCCEED() << "AboutProxy connected";

	initProxyBusObjects();

	LOG(INFO) << "test setUp done";
	CLOG(INFO, "raw") << "====================================================";
}

void LSFControllerTestSuite::initProxyBusObjects()
{
	m_ControllerServiceBusObject = new ControllerServiceBusObject(*m_ServiceHelper->getBusAttachmentMgr()->getBusAttachment(), m_DeviceAboutAnnouncement->getServiceName(), m_ServiceHelper->getSessionId());
	m_LampBusObject = new ControllerServiceLampBusObject(*m_ServiceHelper->getBusAttachmentMgr()->getBusAttachment(), m_DeviceAboutAnnouncement->getServiceName(), m_ServiceHelper->getSessionId());
	m_PresetBusObject = new ControllerServicePresetBusObject(*m_ServiceHelper->getBusAttachmentMgr()->getBusAttachment(), m_DeviceAboutAnnouncement->getServiceName(), m_ServiceHelper->getSessionId());
	m_LampGroupBusObject = new ControllerServiceLampGroupBusObject(*m_ServiceHelper->getBusAttachmentMgr()->getBusAttachment(), m_DeviceAboutAnnouncement->getServiceName(), m_ServiceHelper->getSessionId());
	m_SceneBusObject = new ControllerServiceSceneBusObject(*m_ServiceHelper->getBusAttachmentMgr()->getBusAttachment(), m_DeviceAboutAnnouncement->getServiceName(), m_ServiceHelper->getSessionId());
	m_MasterSceneBusObject = new ControllerServiceMasterSceneBusObject(*m_ServiceHelper->getBusAttachmentMgr()->getBusAttachment(), m_DeviceAboutAnnouncement->getServiceName(), m_ServiceHelper->getSessionId());
	m_LeaderElectionAndStateSyncBusObject = new LeaderElectionAndStateSyncBusObject(*m_ServiceHelper->getBusAttachmentMgr()->getBusAttachment(), m_DeviceAboutAnnouncement->getServiceName(), m_ServiceHelper->getSessionId());
}

void LSFControllerTestSuite::releaseResources()
{
	if (m_AboutProxy != nullptr)
	{
		delete m_AboutProxy;
		m_AboutProxy = nullptr;
	}

	if (m_ServiceHelper != nullptr)
	{
		m_ServiceHelper->release();
		//waitForSessionToClose();
		delete m_ServiceHelper;
	}
}

/*void LSFControllerTestSuite::waitForSessionToClose()
{
	LOG(INFO) << "Waiting for session to close";
	m_ServiceHelper->waitForSessionToClose(ArrayParser::stringToUint16(m_IxitMap.at("GPLC_SessionClose").c_str()) * 1000);
}*/

void LSFControllerTestSuite::TearDown()
{
	CLOG(INFO, "raw") << "====================================================";
	LOG(INFO) << "test tearDown started";

	releaseResources();

	LOG(INFO) << "test tearDown done";
	CLOG(INFO, "raw") << "====================================================";

	storeResults();
}

TEST_F(LSFControllerTestSuite, LSF_Controller_v1_01)
{
	verifyInterfacesFromAnnouncement();
	verifyLeaderElectionAndStateSyncInterface();
}

void LSFControllerTestSuite::verifyInterfacesFromAnnouncement()
{
	std::set<std::string> interfaces_seen;
	InterfaceValidator interface_validator;
	XMLBasedBusIntrospector introspector = m_ServiceHelper->getBusIntrospector(*m_DeviceAboutAnnouncement);

	size_t num_paths = m_DeviceAboutAnnouncement->getObjectDescriptions()->GetPaths(NULL, 0);
	const char** paths = new const char*[num_paths];
	m_DeviceAboutAnnouncement->getObjectDescriptions()->GetPaths(paths, num_paths);

	for (size_t i = 0; i < num_paths; ++i)
	{
		if (string(CONTROLLER_BUS_OBJECT_PATH).compare(paths[i]) == 0)
		{
			size_t num_interfaces = m_DeviceAboutAnnouncement->getObjectDescriptions()->GetInterfaces(paths[i], NULL, 0);
			const char** interfaces = new const char*[num_interfaces];
			m_DeviceAboutAnnouncement->getObjectDescriptions()->GetInterfaces(paths[i], interfaces, num_interfaces);

			for (size_t j = 0; j < num_interfaces; ++j)
			{
				LOG(INFO) << "Attempting to validate " << interfaces[j];
				interfaces_seen.insert(std::string(interfaces[j]));

				std::list<InterfaceDetail> interface_details = introspector.getInterfacesExposedOnBusBasedOnName(interfaces[j]);
				ValidationResult result = interface_validator.validate(interface_details);

				EXPECT_TRUE(result.isValid()) << result.getFailureReason();
			}
		}
	}

	std::list<std::string> expected_interfaces({
		CONTROLLERSERVICE_INTERFACE_NAME,
		LAMP_INTERFACE_NAME,
		LAMPGROUP_INTERFACE_NAME,
		PRESET_INTERFACE_NAME,
		SCENE_INTERFACE_NAME,
		MASTERSCENE_INTERFACE_NAME,
	});

	for (auto expected_interface : expected_interfaces)
	{
		EXPECT_TRUE(interfaces_seen.find(expected_interface.c_str()) != interfaces_seen.end())
			<< "Failed to find interface " << expected_interface;
	}
}

void LSFControllerTestSuite::verifyLeaderElectionAndStateSyncInterface()
{
	XMLBasedBusIntrospector introspector = m_ServiceHelper->getBusIntrospector(*m_DeviceAboutAnnouncement);
	std::list<InterfaceDetail> interface_details = introspector.getInterfacesExposedOnBusBasedOnName(LEADER_ELECTION_INTERFACE_NAME);

	ASSERT_GT(interface_details.size(), 0) << "Could not find the interface " << LEADER_ELECTION_INTERFACE_NAME;

	InterfaceValidator interface_validator;
	ValidationResult result = interface_validator.validate(interface_details);

	ASSERT_TRUE(result.isValid()) << result.getFailureReason();
}

TEST_F(LSFControllerTestSuite, LSF_Controller_v1_02)
{
	LOG(INFO) << "Checking that interfaces Version property match defined IXIT";

	uint32_t controllerServiceVersion;
	QStatus status = m_ControllerServiceBusObject->GetVersion(controllerServiceVersion);
	ASSERT_EQ(ER_OK, status) << "Retrieving Version property from ControllerService interface returned status code: " << QCC_StatusText(status);
	EXPECT_EQ(static_cast<uint32_t>(ArrayParser::stringToUint16(m_IxitMap.at("IXITLC_ControllerServiceVersion").c_str())), controllerServiceVersion)
		<< "ControllerService interface Version property does not match IXIT";

	uint32_t lampVersion;
	status = m_LampBusObject->GetVersion(lampVersion);
	ASSERT_EQ(ER_OK, status) << "Retrieving Version property from Lamp interface returned status code: " << QCC_StatusText(status);
	EXPECT_EQ(static_cast<uint32_t>(ArrayParser::stringToUint16(m_IxitMap.at("IXITLC_ControllerServiceLampVersion").c_str())), lampVersion)
		<< "Lamp interface Version property does not match IXIT";

	uint32_t presetVersion;
	status = m_PresetBusObject->GetVersion(presetVersion);
	ASSERT_EQ(ER_OK, status) << "Retrieving Version property from Preset interface returned status code: " << QCC_StatusText(status);
	EXPECT_EQ(static_cast<uint32_t>(ArrayParser::stringToUint16(m_IxitMap.at("IXITLC_ControllerServicePresetVersion").c_str())), presetVersion)
		<< "Preset interface Version property does not match IXIT";

	uint32_t lampGroupVersion;
	status = m_LampGroupBusObject->GetVersion(lampGroupVersion);
	ASSERT_EQ(ER_OK, status) << "Retrieving Version property from LampGroup interface returned status code: " << QCC_StatusText(status);
	EXPECT_EQ(static_cast<uint32_t>(ArrayParser::stringToUint16(m_IxitMap.at("IXITLC_ControllerServiceLampGroupVersion").c_str())), lampGroupVersion)
		<< "LampGroup interface Version property does not match IXIT";

	uint32_t sceneVersion;
	status = m_SceneBusObject->GetVersion(sceneVersion);
	ASSERT_EQ(ER_OK, status) << "Retrieving Version property from Scene interface returned status code: " << QCC_StatusText(status);
	EXPECT_EQ(static_cast<uint32_t>(ArrayParser::stringToUint16(m_IxitMap.at("IXITLC_ControllerServiceSceneVersion").c_str())), sceneVersion)
		<< "Scene interface Version property does not match IXIT";

	uint32_t masterSceneVersion;
	status = m_MasterSceneBusObject->GetVersion(masterSceneVersion);
	ASSERT_EQ(ER_OK, status) << "Retrieving Version property from MasterScene interface returned status code: " << QCC_StatusText(status);
	EXPECT_EQ(static_cast<uint32_t>(ArrayParser::stringToUint16(m_IxitMap.at("IXITLC_ControllerServiceMasterSceneVersion").c_str())), masterSceneVersion)
		<< "MasterScene interface Version property does not match IXIT";

	uint32_t leaderElectionAndStateSyncVersion;
	status = m_ControllerServiceBusObject->GetControllerServiceVersion(leaderElectionAndStateSyncVersion);
	ASSERT_EQ(ER_OK, status) << "Calling GetControllerServiceVersion() method returned status code: " << QCC_StatusText(status);
	EXPECT_EQ(static_cast<uint32_t>(ArrayParser::stringToUint16(m_IxitMap.at("IXITLC_LeaderElectionAndStateSyncVersion").c_str())), leaderElectionAndStateSyncVersion)
		<< "ControllerService Leader Election interface Version property does not match IXIT";
}

TEST_F(LSFControllerTestSuite, LSF_Controller_v1_03)
{
	uint32_t response_code;
	QStatus status = m_ControllerServiceBusObject->LightingResetControllerService(response_code);
	ASSERT_EQ(ER_OK, status) << "Calling LightingResetControllerService() method returned status: " << QCC_StatusText(status);

	WAIT_MILLISECONDS(500)

	bool signalReceived = m_ControllerServiceBusObject->DidLightingReset();
	LOG(INFO) << "Checking if LightingReset signal was received";
	ASSERT_TRUE(signalReceived) << "Signal for Lighting was not received";
}

TEST_F(LSFControllerTestSuite, LSF_Controller_v1_04)
{
	std::string lampID;
	QStatus status = getConnectedLamp(lampID);
	ASSERT_EQ(ER_OK, status) << "A connected lamp could not be retrieved";

	uint32_t responseCode;
	qcc::String retrievedLampID;
	std::vector<qcc::String> languages;
	status = m_LampBusObject->GetLampSupportedLanguages(lampID.c_str(), responseCode, retrievedLampID, languages);
	ASSERT_EQ(ER_OK, status) << "Calling GetLampSupportedLanguages() method returned status: " << QCC_StatusText(status);

	ASSERT_GT(languages.size(), 0) << "At least a language should be returned";

	for (auto language : languages)
	{
		LOG(INFO) << "Lamp supported language: " << language.c_str();
	}

	qcc::String retrievedLanguage;
	qcc::String manufacturer;
	status = m_LampBusObject->GetLampManufacturer(lampID.c_str(), languages[0].c_str(), responseCode, retrievedLampID, retrievedLanguage, manufacturer);

	ASSERT_EQ(ER_OK, status) << "Calling GetLampManufacturer() method returned status: " << QCC_StatusText(status);

	ASSERT_EQ(0, responseCode) << "An error occurred in getting LampManufacturer";
	LOG(INFO) << "Lamp manufacturer: " << manufacturer.c_str();
}

QStatus LSFControllerTestSuite::getConnectedLamp(std::string& lampID)
{
	uint32_t lampResponseCode;
	size_t numOfLamps;
	std::vector<qcc::String> lampIDs;
	QStatus status = m_LampBusObject->GetAllLampIDs(lampResponseCode, numOfLamps, lampIDs);
	EXPECT_EQ(ER_OK, status) << "Calling GetAllLampIDs() method returned status code: " << QCC_StatusText(status);

	if (ER_OK != status)
	{
		return status;
	}

	EXPECT_GT(numOfLamps, 0) << "There must be at least 1 lamp connected to controller";

	if (numOfLamps > 0)
	{
		lampID = std::string(lampIDs[0].c_str());
	}
	else
	{
		lampID = std::string("");
		status = ER_FAIL;
	}

	return status;
}

TEST_F(LSFControllerTestSuite, LSF_Controller_v1_05)
{
	std::string lampID;
	QStatus status = getConnectedLamp(lampID);
	ASSERT_EQ(ER_OK, status) << "A connected lamp could not be retrieved";

	std::string lampName("ControllerTestSuite-Lamp");
	uint32_t responseCode;
	qcc::String retrievedLampID;
	qcc::String retrievedLanguage;
	status = m_LampBusObject->SetLampName(lampID.c_str(), lampName.c_str(), "en", responseCode, retrievedLampID, retrievedLanguage);
	ASSERT_EQ(ER_OK, status) << "Calling SetLampName() method returned status code: " << QCC_StatusText(status);

	qcc::String retrievedLampName;
	status = m_LampBusObject->GetLampName(lampID.c_str(), "en", responseCode, retrievedLampID, retrievedLanguage, retrievedLampName);
	ASSERT_EQ(ER_OK, status) << "Calling GetLampName() method returned status code: " << QCC_StatusText(status);

	LOG(INFO) << "Checking if lamp names match";
	EXPECT_EQ(lampName, std::string(retrievedLampName.c_str())) << "Lamp names are not the same";

	WAIT_MILLISECONDS(5000);
	bool signalReceived = m_LampBusObject->DidLampNameChanged();
	LOG(INFO) << "Checking if LampNameChanged signal was received";
	ASSERT_TRUE(signalReceived) << "Signal for LampNameChanged was not received";
}

TEST_F(LSFControllerTestSuite, LSF_Controller_v1_06)
{
	std::string lampID;
	QStatus status = getConnectedLamp(lampID);
	ASSERT_EQ(ER_OK, status) << "A connected lamp could not be retrieved";

	uint32_t responseCode;
	qcc::String retrievedLampID;
	std::vector<ajn::MsgArg> lampDetails;
	status = m_LampBusObject->GetLampDetails(lampID.c_str(), responseCode, retrievedLampID, lampDetails);
	ASSERT_EQ(ER_OK, status) << "Calling GetLampDetails() method returned status code: " << QCC_StatusText(status);
	ASSERT_EQ(0, responseCode) << "An error occurred in getting lamp details";

	for (auto& lampDetail : lampDetails)
	{
		char* detailName;
		uint32_t uDetail;
		if ((status = lampDetail.Get("{su}", &detailName, &uDetail)) == ER_BUS_SIGNATURE_MISMATCH)
		{
			bool bDetail;
			if ((status = lampDetail.Get("{sb}", &detailName, &bDetail)) == ER_BUS_SIGNATURE_MISMATCH)
			{
				char* cDetail;
				status = lampDetail.Get("{ss}", &detailName, &cDetail);
				LOG(INFO) << detailName << " : " << cDetail;
			}
			else
			{
				LOG(INFO) << detailName << " : " << (bDetail ? "true" : "false");
			}
		}
		else
		{
			LOG(INFO) << detailName << " : " << uDetail;
		}
	}
}

TEST_F(LSFControllerTestSuite, LSF_Controller_v1_07)
{
	std::string lampID;
	QStatus status = getConnectedLamp(lampID);
	ASSERT_EQ(ER_OK, status) << "A connected lamp could not be retrieved";

	uint32_t responseCode;
	qcc::String retrievedLampID;
	std::vector<ajn::MsgArg> lampParameters;
	status = m_LampBusObject->GetLampParameters(lampID.c_str(), responseCode, retrievedLampID, lampParameters);
	ASSERT_EQ(ER_OK, status) << "Calling GetLampParameters() method returned status code: " << QCC_StatusText(status);

	ASSERT_EQ(0, responseCode) << "An error occurred in getting lamp parameters";

	for (auto& lampParameter : lampParameters)
	{
		std::string parameterName;
		std::string parameterValue;
		status = getParamNameAndValue(lampParameter, parameterName, parameterValue);
		
		LOG(INFO) << "Checking " << parameterName << " parameter";

		qcc::String retrievedParameterName;
		qcc::String singleValue;
		status = m_LampBusObject->GetLampParametersField(lampID.c_str(), parameterName.c_str(), responseCode, retrievedLampID, retrievedParameterName, singleValue);
		ASSERT_EQ(ER_OK, status) << "Calling GetLampParametersField() method returned status code: " << QCC_StatusText(status);

		LOG(INFO) << "Checking if parameters match";
		EXPECT_EQ(parameterValue, std::string(singleValue.c_str())) << "The lamp parameters are not the same";
	}
}

QStatus LSFControllerTestSuite::getParamNameAndValue(const ajn::MsgArg& t_Param, std::string& t_Name, std::string& t_Value)
{
	QStatus status = ER_OK;
	char* pName;
	uint32_t uValue;
	if ((status = t_Param.Get("{su}", &pName, &uValue)) == ER_BUS_SIGNATURE_MISMATCH)
	{
		bool bValue;
		if ((status = t_Param.Get("{sb}", &pName, &bValue)) == ER_BUS_SIGNATURE_MISMATCH)
		{
			char* cValue;
			status = t_Param.Get("{ss}", &pName, &cValue);
			t_Value = std::string(cValue);
		}
		else
		{
			t_Value = std::to_string(bValue);
		}
	}
	else
	{
		t_Value = std::to_string(uValue);
	}

	t_Name = std::string(pName);
	return status;
}

TEST_F(LSFControllerTestSuite, LSF_Controller_v1_08)
{
	std::string lampID;
	QStatus status = getConnectedLamp(lampID);
	ASSERT_EQ(ER_OK, status) << "A connected lamp could not be retrieved";

	uint32_t responseCode;
	qcc::String retrievedLampID;
	std::vector<ajn::MsgArg> lampStates;
	status = m_LampBusObject->GetLampState(lampID.c_str(), responseCode, retrievedLampID, lampStates);
	ASSERT_EQ(ER_OK, status) << "Calling GetLampState() method returned status code: " << QCC_StatusText(status);

	ASSERT_EQ(0, responseCode) << "An error occurred in getting lamp states";

	for (auto& lampState : lampStates)
	{
		std::string stateName;
		std::string stateValue;
		status = getParamNameAndValue(lampState, stateName, stateValue);

		LOG(INFO) << "Checking " << stateName << " state";

		qcc::String retrievedStateName;
		qcc::String singleValue;
		status = m_LampBusObject->GetLampStateField(lampID.c_str(), stateName.c_str(), responseCode, retrievedLampID, retrievedStateName, singleValue);
		ASSERT_EQ(ER_OK, status) << "Calling GetLampStateField() method returned status code: " << QCC_StatusText(status);

		LOG(INFO) << "Checking if states match";
		EXPECT_EQ(stateValue, std::string(singleValue.c_str())) << "The lamp states are not the same";
	}
}

TEST_F(LSFControllerTestSuite, LSF_Controller_v1_09)
{
	std::string lampID;
	QStatus status = getConnectedLamp(lampID);
	ASSERT_EQ(ER_OK, status) << "A connected lamp could not be retrieved";

	LOG(INFO) << "Testing TransitionLampState()";

	ajn::MsgArg* lampState = newLampState(true, 147483648, 739688812, 2061584302, 384286547);

	uint32_t responseCode;
	qcc::String retrievedLampID;
	status = m_LampBusObject->TransitionLampState(lampID.c_str(), lampState, 0, responseCode, retrievedLampID);
	ASSERT_EQ(ER_OK, status) << "Calling TransitionLampState() method returned status code: " << QCC_StatusText(status);
	ASSERT_EQ(0, responseCode) << "An error occurred in TransitionLampState";

	WAIT_MILLISECONDS(1000);

	bool signalReceived = m_LampBusObject->DidLampStateChanged();
	LOG(INFO) << "Checking if LampStateChanged was not received";
	ASSERT_TRUE(signalReceived) << "Signal for LampStateChanged was not received";
	m_LampBusObject->ResetSignals();

	LOG(INFO) << "Testing TransitionFieldValues()";
	lampState[0].Set("sv", "OnOff", ajn::MsgArg("b", false));

	qcc::String retrievedLampStateFieldName;
	status = m_LampBusObject->TransitionLampStateField(lampID.c_str(), "OnOff", ajn::MsgArg("b", false), 0, responseCode, retrievedLampID, retrievedLampStateFieldName);

	ASSERT_EQ(0, responseCode) << "An error occurred in TransitionLampStateField";

	WAIT_MILLISECONDS(1000);
	signalReceived = m_LampBusObject->DidLampStateChanged();
	ASSERT_TRUE(signalReceived) << "Signal for LampStateChanged was not received";

	LOG(INFO) << "Verify state change is persistent";
	std::vector<ajn::MsgArg> lampStates;
	status = m_LampBusObject->GetLampState(lampID.c_str(), responseCode, retrievedLampID, lampStates);

	for (size_t i = 0; i < lampStates.size(); ++i)
	{
		std::string retrievedStateName;
		std::string retrievedStateValue;
		status = getParamNameAndValue(lampStates[i], retrievedStateName, retrievedStateValue);

		LOG(INFO) << "Checking " << retrievedStateName << " state";

		std::string stateValue;
		status = getParamNameAndValue(lampState[i], retrievedStateName, stateValue);

		ASSERT_EQ(retrievedStateValue, stateValue) << "Unexpected lamp state value for " << retrievedStateName;
	}
}

MsgArg* LSFControllerTestSuite::newLampState(const bool t_on_off, const uint32_t t_brightness,
	const uint32_t t_hue, const uint32_t t_saturation, const uint32_t t_color_temp)
{
	MsgArg* msg_arg = new MsgArg[5];
	msg_arg[0].Set("{sv}", "OnOff", new MsgArg("b", t_on_off));
	msg_arg[1].Set("{sv}", "Brightness", new MsgArg("u", t_brightness));
	msg_arg[2].Set("{sv}", "Hue", new MsgArg("u", t_hue));
	msg_arg[3].Set("{sv}", "Saturation", new MsgArg("u", t_saturation));
	msg_arg[4].Set("{sv}", "ColorTemp", new MsgArg("u", t_color_temp));

	return msg_arg;
}

TEST_F(LSFControllerTestSuite, LSF_Controller_v1_10)
{
	std::string lampID;
	QStatus status = getConnectedLamp(lampID);
	ASSERT_EQ(ER_OK, status) << "A connected lamp could not be retrieved";

	ajn::MsgArg* to_state = newLampState(true, 2147483648, 739688812, 2061584302, 384286547);
	ajn::MsgArg* from_state = newLampState(false, 2147483648, 739688812, 2061584302, 384286547);

	uint32_t responseCode;
	qcc::String retrievedLampID;
	status = m_LampBusObject->PulseLampWithState(lampID.c_str(), from_state, to_state, 1000, 500, 5, responseCode, retrievedLampID);
	ASSERT_EQ(ER_OK, status) << "Calling PulseLampWithState() method returned status code: " << QCC_StatusText(status);
	ASSERT_EQ(0, responseCode) << "An error occurred in PulseLampWithState";

	WAIT_MILLISECONDS(5000);
}

TEST_F(LSFControllerTestSuite, LSF_Controller_v1_11)
{
	std::string lampID;
	QStatus status = getConnectedLamp(lampID);
	ASSERT_EQ(ER_OK, status) << "A connected lamp could not be retrieved";

	ajn::MsgArg* toState = newLampState(true, 2147483648, 739688812, 2061584302, 384286547);
	ajn::MsgArg* fromState = newLampState(false, 2147483648, 739688812, 2061584302, 384286547);

	uint32_t responseCode;
	qcc::String toPresetID;
	status = m_PresetBusObject->CreatePreset(toState, "presetA", "en", responseCode, toPresetID);
	ASSERT_EQ(ER_OK, status) << "Calling CreatePreset() method returned status code: " << QCC_StatusText(status);

	qcc::String fromPresetID;
	status = m_PresetBusObject->CreatePreset(fromState, "presetB", "en", responseCode, fromPresetID);
	ASSERT_EQ(ER_OK, status) << "Calling CreatePreset() method returned status code: " << QCC_StatusText(status);

	qcc::String retrievedLampID;
	status = m_LampBusObject->TransitionLampStateToPreset(lampID.c_str(), toPresetID.c_str(), 0, responseCode, retrievedLampID);
	ASSERT_EQ(ER_OK, status) << "Calling TransitionLampStateToPreset() method returned status code: " << QCC_StatusText(status);

	status = m_LampBusObject->PulseLampWithPreset(lampID.c_str(), fromPresetID.c_str(), toPresetID.c_str(), 1000, 500, 5, responseCode, retrievedLampID);
	ASSERT_EQ(ER_OK, status) << "Calling PulseLampWithPreset() method returned status code: " << QCC_StatusText(status);

	WAIT_MILLISECONDS(5000);
}

TEST_F(LSFControllerTestSuite, LSF_Controller_v1_12)
{
	std::string lampID;
	QStatus status = getConnectedLamp(lampID);
	ASSERT_EQ(ER_OK, status) << "A connected lamp could not be retrieved";

	uint32_t responseCode;
	qcc::String retrievedLampID;
	status = m_LampBusObject->ResetLampState(lampID.c_str(), responseCode, retrievedLampID);
	ASSERT_EQ(ER_OK, status) << "Calling ResetLampState() method returned status code: " << QCC_StatusText(status);
	
	std::vector<ajn::MsgArg> resetLampState;
	status = m_LampBusObject->GetLampState(lampID.c_str(), responseCode, retrievedLampID, resetLampState);
	ASSERT_EQ(ER_OK, status) << "Calling GetLampState() method returned status code: " << QCC_StatusText(status);
	
	ajn::MsgArg* lampState = newLampState(true, 2147483648, 739688812, 2061584302, 384286547);
	status = m_LampBusObject->TransitionLampState(lampID.c_str(), lampState, 0, responseCode, retrievedLampID);
	ASSERT_EQ(ER_OK, status) << "Calling TransitionLampState() method returned status code: " << QCC_StatusText(status);

	qcc::String retrievedStateFieldName;
	status = m_LampBusObject->ResetLampStateField(lampID.c_str(), "Brightness", responseCode, retrievedLampID, retrievedStateFieldName);
	ASSERT_EQ(ER_OK, status) << "Calling ResetLampStateField() method returned status code: " << QCC_StatusText(status);

	std::vector<ajn::MsgArg> newLampState;
	status = m_LampBusObject->GetLampState(lampID.c_str(), responseCode, retrievedLampID, newLampState);
	ASSERT_EQ(ER_OK, status) << "Calling GetLampState() method returned status code: " << QCC_StatusText(status);

	for (auto& resetState : resetLampState)
	{
		std::string paramName;
		std::string paramValue;
		status = getParamNameAndValue(resetState, paramName, paramValue);

		if (paramName.compare("Brightness") == 0)
		{
			for (auto& newState : newLampState)
			{
				std::string newParamName;
				std::string newParamValue;
				status = getParamNameAndValue(newState, newParamName, newParamValue);

				if (newParamName.compare("Brightness") == 0)
				{
					ASSERT_EQ(paramValue, newParamValue) << "The values are not the same after reset";
					return;
				}
			}
		}
	}
}

TEST_F(LSFControllerTestSuite, LSF_Controller_v1_13)
{
	std::string lampID;
	QStatus status = getConnectedLamp(lampID);
	ASSERT_EQ(ER_OK, status) << "A connected lamp could not be retrieved";

	uint32_t responseCode;
	qcc::String retrievedLampID;
	std::vector<uint32_t> lampFaults;
	status = m_LampBusObject->GetLampFaults(lampID.c_str(), responseCode, retrievedLampID, lampFaults);
	ASSERT_EQ(ER_OK, status) << "Calling GetLampFaults() method returned status code: " << QCC_StatusText(status);

	uint32_t retrievedLampFault;
	for (auto& lampFault : lampFaults)
	{
		LOG(INFO) << "Clearing fault " << lampFault;
		status = m_LampBusObject->ClearLampFault(lampID.c_str(), lampFault, responseCode, retrievedLampID, retrievedLampFault);
		ASSERT_EQ(ER_OK, status) << "Calling ClearLampFault() method returned status code: " << QCC_StatusText(status);
	}
}

TEST_F(LSFControllerTestSuite, LSF_Controller_v1_14)
{
	LOG(INFO) << "Test CreateLampGroup";
	std::string lampID;
	QStatus status = getConnectedLamp(lampID);
	ASSERT_EQ(ER_OK, status) << "A connected lamp could not be retrieved";

	std::vector<const char*> lampIDs = { lampID.c_str() };
	uint32_t responseCode;
	qcc::String lampGroupID;
	status = createLampGroup(lampIDs, responseCode, lampGroupID);
	ASSERT_EQ(ER_OK, status) << "Calling CreateLampGroup() method returned status code: " << QCC_StatusText(status);

	qcc::String retrievedLampGroupID;
	std::vector<qcc::String> retrievedLampIDs;
	std::vector<qcc::String> retrievedLampGroupIDs;
	status = m_LampGroupBusObject->GetLampGroup(lampGroupID.c_str(), responseCode, retrievedLampGroupID, retrievedLampIDs, retrievedLampGroupIDs);
	ASSERT_EQ(ER_OK, status) << "Calling GetLampGroup() method returned status code: " << QCC_StatusText(status);

	ASSERT_EQ(lampIDs.size(), retrievedLampIDs.size()) << "Incorrect number of lamps in the group";
	ASSERT_STREQ(lampID.c_str(), retrievedLampIDs[0].c_str()) << "Expected lamp not found in the group";

	WAIT_MILLISECONDS(500);
	bool signalReceived = m_LampGroupBusObject->DidLampGroupsCreated();
	ASSERT_TRUE(signalReceived) << "Did no t receive signal LampGroupsCreated";

	LOG(INFO) << "Test UpdateLampGroup";

	std::vector<const char*> newLampIDs = {
		lampID.c_str(),
		lampID.c_str()
	};

	std::vector<const char*> lampGroupIDs;
	for (auto& lgID : retrievedLampGroupIDs)
	{
		lampGroupIDs.push_back(lgID.c_str());
	}

	status = m_LampGroupBusObject->UpdateLampGroup(lampGroupID.c_str(), newLampIDs, lampGroupIDs, responseCode, retrievedLampGroupID);
	ASSERT_EQ(ER_OK, status) << "Calling UpdateLampGroup() method returned status code: " << QCC_StatusText(status);

	status = m_LampGroupBusObject->GetLampGroup(lampGroupID.c_str(), responseCode, retrievedLampGroupID, retrievedLampIDs, retrievedLampGroupIDs);
	ASSERT_EQ(ER_OK, status) << "Calling GetLampGroup() method returned status code: " << QCC_StatusText(status);
	ASSERT_EQ(1, retrievedLampIDs.size()) << "Incorrect number of lamps in the group";

	WAIT_MILLISECONDS(500);
	signalReceived = m_LampGroupBusObject->DidLampGroupsUpdated();
	ASSERT_TRUE(signalReceived) << "Did no t receive signal LampGroupsUpdated";

	LOG(INFO) << "Test DeleteLampGroup";

	status = m_LampGroupBusObject->GetAllLampGroupIDs(responseCode, retrievedLampGroupIDs);
	ASSERT_EQ(ER_OK, status) << "Calling GetAllLampGroupIDs() method returned status code: " << QCC_StatusText(status);

	size_t numGroupsPrior = retrievedLampGroupIDs.size();

	status = m_LampGroupBusObject->DeleteLampGroup(lampGroupID.c_str(), responseCode, retrievedLampGroupID);
	ASSERT_EQ(ER_OK, status) << "Calling DeleteLampGroup() method returned status code: " << QCC_StatusText(status);

	status = m_LampGroupBusObject->GetAllLampGroupIDs(responseCode, retrievedLampGroupIDs);
	ASSERT_EQ(ER_OK, status) << "Calling GetAllLampGroupIDs() method returned status code: " << QCC_StatusText(status);
	ASSERT_EQ(numGroupsPrior - 1, retrievedLampGroupIDs.size()) << "Incorrect number of groups";

	WAIT_MILLISECONDS(500);
	signalReceived = m_LampGroupBusObject->DidLampGroupsDeleted();
	ASSERT_TRUE(signalReceived) << "Did no t receive signal LampGroupsDeleted";
}

QStatus LSFControllerTestSuite::createLampGroup(const std::vector<const char*>& t_LampIDs, uint32_t& t_ResponseCode,
	qcc::String& t_LampGroupID)
{
	std::vector<const char*> lampGroupIDs;
	const char* lampGroupName = "ControllerTestGroup";
	return m_LampGroupBusObject->CreateLampGroup(t_LampIDs, lampGroupIDs, lampGroupName, "en", t_ResponseCode, t_LampGroupID);
}

TEST_F(LSFControllerTestSuite, LSF_Controller_v1_15)
{
	std::string lampID;
	QStatus status = getConnectedLamp(lampID);
	ASSERT_EQ(ER_OK, status) << "A connected lamp could not be retrieved";

	std::vector<const char*> lampIDs = { lampID.c_str() };
	uint32_t responseCode;
	qcc::String lampGroupID;
	status = createLampGroup(lampIDs, responseCode, lampGroupID);
	ASSERT_EQ(ER_OK, status) << "Calling CreateLampGroup() method returned status code: " << QCC_StatusText(status);

	qcc::String retrievedLampGroupID;
	qcc::String retrievedLanguage;
	qcc::String lampGroupName;
	status = m_LampGroupBusObject->GetLampGroupName(lampGroupID.c_str(), "en", responseCode, retrievedLampGroupID, retrievedLanguage, lampGroupName);
	ASSERT_EQ(ER_OK, status) << "Calling GetLampGroupName() method returned status code: " << QCC_StatusText(status);

	qcc::String newLampGroupName = lampGroupName.append("X");
	status = m_LampGroupBusObject->SetLampGroupName(lampGroupID.c_str(), newLampGroupName.c_str(), "en", responseCode, retrievedLampGroupID, retrievedLanguage);
	ASSERT_EQ(ER_OK, status) << "Calling SetLampGroupName() method returned status code: " << QCC_StatusText(status);

	WAIT_MILLISECONDS(500);
	bool signalReceived = m_LampGroupBusObject->DidLampGroupsNameChanged();
	ASSERT_TRUE(signalReceived) << "Did not receive signal LampGroupsNameChanged";

	status = m_LampGroupBusObject->GetLampGroupName(lampGroupID.c_str(), "en", responseCode, retrievedLampGroupID, retrievedLanguage, lampGroupName);
	ASSERT_EQ(ER_OK, status) << "Calling GetLampGroupName() method returned status code: " << QCC_StatusText(status);
	ASSERT_STRNE(newLampGroupName.c_str(), lampGroupName.c_str()) << "The LampGroupName has not been changed";
}

TEST_F(LSFControllerTestSuite, LSF_Controller_v1_16)
{
	std::string lampID;
	QStatus status = getConnectedLamp(lampID);
	ASSERT_EQ(ER_OK, status) << "A connected lamp could not be retrieved";

	std::vector<const char*> lampIDs = { lampID.c_str() };
	uint32_t responseCode;
	qcc::String lampGroupID;
	status = createLampGroup(lampIDs, responseCode, lampGroupID);
	ASSERT_EQ(ER_OK, status) << "Calling CreateLampGroup() method returned status code: " << QCC_StatusText(status);

	LOG(INFO) << "Testing TransitionLampGroupState";
	ajn::MsgArg* lampState = newLampState(true, 2147483648, 739688812, 2061584302, 384286547);
	qcc::String retrievedLampGroupID;
	status = m_LampGroupBusObject->TransitionLampGroupState(lampGroupID.c_str(), lampState, 0, responseCode, retrievedLampGroupID);
	ASSERT_EQ(ER_OK, status) << "Calling TransitionLampGroupState() method returned status code: " << QCC_StatusText(status);

	LOG(INFO) << "Testing TransitionLampGroupFieldValues";
	qcc::String retrievedLampGroupStateFieldName;
	lampState[0].Set("{sv}", "OnOff", new MsgArg("b", false));
	status = m_LampGroupBusObject->TransitionLampGroupStateField(lampGroupID.c_str(), "OnOff", ajn::MsgArg("b", false), 0, responseCode, retrievedLampGroupID, retrievedLampGroupStateFieldName);
	ASSERT_EQ(ER_OK, status) << "Calling TransitionLampGroupStateField() method returned status code: " << QCC_StatusText(status);

	LOG(INFO) << "Verify state change occurred";
	qcc::String retrievedLampID;
	std::vector<ajn::MsgArg> lampStateValues;
	status = m_LampBusObject->GetLampState(lampID.c_str(), responseCode, retrievedLampID, lampStateValues);
	ASSERT_EQ(ER_OK, status) << "Calling GetLampState() method returned status code: " << QCC_StatusText(status);

	ASSERT_TRUE(compareLampStates(lampStateValues, lampState)) << "LampState did not change";
}

bool LSFControllerTestSuite::compareLampStates(const std::vector<ajn::MsgArg>& t_RetrievedLampState, ajn::MsgArg* t_CreatedLampState)
{
	bool equal = true;
	size_t i = 0;
	while (i < 5 && equal)
	{
		LOG(INFO) << "Comparing key: " << t_CreatedLampState[i].v_dictEntry.key->v_string.str;
		const ajn::MsgArg retrievedLampStateValue =
			getValueFromLampState(t_RetrievedLampState, t_CreatedLampState[i].v_dictEntry.key->v_string.str);
		if (string("OnOff").compare(t_CreatedLampState[i].v_dictEntry.key->v_string.str) == 0)
		{
			equal = (t_CreatedLampState[i].v_dictEntry.val->v_variant.val->v_bool == retrievedLampStateValue.v_dictEntry.val->v_variant.val->v_bool);
		}
		else
		{
			equal = (t_CreatedLampState[i].v_dictEntry.val->v_variant.val->v_uint32 == retrievedLampStateValue.v_dictEntry.val->v_variant.val->v_uint32);
		}

		++i;
	}

	return equal;
}

ajn::MsgArg LSFControllerTestSuite::getValueFromLampState(const std::vector<ajn::MsgArg>& t_RetrievedLampState, const char* t_Value)
{
	for (auto& state : t_RetrievedLampState)
	{
		if (std::string(t_Value).compare(state.v_dictEntry.key->v_string.str) == 0)
		{
			return state;
		}
	}

	return MsgArg();
}

TEST_F(LSFControllerTestSuite, LSF_Controller_v1_17)
{
	std::string lampID;
	QStatus status = getConnectedLamp(lampID);
	ASSERT_EQ(ER_OK, status) << "A connected lamp could not be retrieved";

	std::vector<const char*> lampIDs = { lampID.c_str() };
	uint32_t responseCode;
	qcc::String lampGroupID;
	status = createLampGroup(lampIDs, responseCode, lampGroupID);
	ASSERT_EQ(ER_OK, status) << "Calling CreateLampGroup() method returned status code: " << QCC_StatusText(status);

	ajn::MsgArg* toState = newLampState(true, 2147483648, 739688812, 2061584302, 384286547);
	ajn::MsgArg* fromState = newLampState(false, 2147483648, 739688812, 2061584302, 384286547);

	qcc::String retrievedLampGroupID;
	status = m_LampGroupBusObject->PulseLampGroupWithState(lampGroupID.c_str(), fromState, toState, 100, 500, 5, responseCode, retrievedLampGroupID);
	ASSERT_EQ(ER_OK, status) << "Calling PulseLampGroupWithState() method returned status code: " << QCC_StatusText(status);
	ASSERT_EQ(0, responseCode) << "Error occurred in PulseLampGroupWithState";

	WAIT_MILLISECONDS(500);
}

TEST_F(LSFControllerTestSuite, LSF_Controller_v1_18)
{
	std::string lampID;
	QStatus status = getConnectedLamp(lampID);
	ASSERT_EQ(ER_OK, status) << "A connected lamp could not be retrieved";

	std::vector<const char*> lampIDs = { lampID.c_str() };
	uint32_t responseCode;
	qcc::String lampGroupID;
	status = createLampGroup(lampIDs, responseCode, lampGroupID);
	ASSERT_EQ(ER_OK, status) << "Calling CreateLampGroup() method returned status code: " << QCC_StatusText(status);

	qcc::String retrievedLampGroupID;
	status = m_LampGroupBusObject->ResetLampGroupState(lampGroupID.c_str(), responseCode, retrievedLampGroupID);
	ASSERT_EQ(ER_OK, status) << "Calling ResetLampGroupState() method returned status code: " << QCC_StatusText(status);

	qcc::String retrievedLampID;
	std::vector<ajn::MsgArg> resetStateValues;
	status = m_LampBusObject->GetLampState(lampID.c_str(), responseCode, retrievedLampID, resetStateValues);
	ASSERT_EQ(ER_OK, status) << "Calling GetLampState() method returned status code: " << QCC_StatusText(status);

	ajn::MsgArg* lampState = newLampState(true, 2147483648, 739688812, 2061584302, 384286547);
	status = m_LampGroupBusObject->TransitionLampGroupState(lampID.c_str(), lampState, 0, responseCode, retrievedLampGroupID);
	ASSERT_EQ(ER_OK, status) << "Calling TransitionLampGroupState() method returned status code: " << QCC_StatusText(status);

	qcc::String retrievedLampGroupStateFieldName;
	status = m_LampGroupBusObject->ResetLampGroupStateField(lampGroupID.c_str(), "Brightness", responseCode, retrievedLampGroupID, retrievedLampGroupStateFieldName);
	ASSERT_EQ(ER_OK, status) << "Calling ResetLampGroupStateField() method returned status code: " << QCC_StatusText(status);

	std::vector<ajn::MsgArg> newStateValues;
	status = m_LampBusObject->GetLampState(lampID.c_str(), responseCode, retrievedLampID, newStateValues);
	ASSERT_EQ(ER_OK, status) << "Calling GetLampState() method returned status code: " << QCC_StatusText(status);

	for (size_t i = 0; i < resetStateValues.size(); ++i)
	{
		if (std::string(resetStateValues[i].v_dictEntry.key->v_string.str).compare("Brightness") == 0)
		{
			ASSERT_EQ(resetStateValues[i].v_dictEntry.val->v_variant.val->v_uint32, newStateValues[i].v_dictEntry.val->v_variant.val->v_uint32)
				<< "Values are not consistent after reset";
		}
	}
}

TEST_F(LSFControllerTestSuite, LSF_Controller_v1_19)
{
	std::string lampID;
	QStatus status = getConnectedLamp(lampID);
	ASSERT_EQ(ER_OK, status) << "A connected lamp could not be retrieved";

	std::vector<const char*> lampIDs = { lampID.c_str() };
	uint32_t responseCode;
	qcc::String lampGroupID;
	status = createLampGroup(lampIDs, responseCode, lampGroupID);
	ASSERT_EQ(ER_OK, status) << "Calling CreateLampGroup() method returned status code: " << QCC_StatusText(status);

	ajn::MsgArg* toState = newLampState(true, 2147483648, 739688812, 2061584302, 384286547);
	ajn::MsgArg* fromState = newLampState(false, 2147483648, 739688812, 2061584302, 384286547);

	qcc::String toPresetID;
	status = m_PresetBusObject->CreatePreset(toState, "presetA", "en", responseCode, toPresetID);
	ASSERT_EQ(ER_OK, status) << "Calling CreatePreset() method returned status code: " << QCC_StatusText(status);
	qcc::String fromPresetID;
	status = m_PresetBusObject->CreatePreset(fromState, "presetB", "en", responseCode, fromPresetID);
	ASSERT_EQ(ER_OK, status) << "Calling CreatePreset() method returned status code: " << QCC_StatusText(status);

	qcc::String retrievedLampGroupID;
	status = m_LampGroupBusObject->TransitionLampGroupStateToPreset(lampGroupID.c_str(), toPresetID.c_str(), 0, responseCode, retrievedLampGroupID);
	ASSERT_EQ(ER_OK, status) << "Calling TransitionLampGroupStateToPreset() method returned status code: " << QCC_StatusText(status);

	status = m_LampGroupBusObject->PulseLampGroupWithPreset(lampGroupID.c_str(), fromPresetID.c_str(), toPresetID.c_str(), 1000, 500, 5, responseCode, retrievedLampGroupID);

	WAIT_MILLISECONDS(5000);
}

TEST_F(LSFControllerTestSuite, LSF_Controller_v1_20)
{
	ajn::MsgArg* defaultLampState = newLampState(true, 2147483648, 739688812, 2061584302, 384286547);
	uint32_t responseCode;
	QStatus status = m_PresetBusObject->SetDefaultLampState(defaultLampState, responseCode);
	ASSERT_EQ(ER_OK, status) << "Calling SetDefaultLampState() method returned status code: " << QCC_StatusText(status);

	WAIT_MILLISECONDS(500)
	bool signalReceived = m_PresetBusObject->DidDefaultLampStateChanged();
	ASSERT_TRUE(signalReceived) << "Did not receive signal DefaultLampStateChanged";

	std::vector<ajn::MsgArg> newDefaultLampState;
	status = m_PresetBusObject->GetDefaultLampState(responseCode, newDefaultLampState);
	ASSERT_EQ(ER_OK, status) << "Calling GetDefaultLampState() method returned status code: " << QCC_StatusText(status);

	ASSERT_TRUE(compareLampStates(newDefaultLampState, defaultLampState)) << "DefaultLampStates does not match";
}

TEST_F(LSFControllerTestSuite, LSF_Controller_v1_21)
{
	ajn::MsgArg* presetState = newLampState(true, 2147483648, 739688812, 2061584302, 384286547);
	const char* presetName = "ControllerTest Preset";
	uint32_t responseCode;
	qcc::String presetID;
	QStatus status = m_PresetBusObject->CreatePreset(presetState, presetName, "en", responseCode, presetID);
	ASSERT_EQ(ER_OK, status) << "Calling CreatePreset() method returned status code: " << QCC_StatusText(status);

	WAIT_MILLISECONDS(500)
	bool signalReceived = m_PresetBusObject->DidPresetsCreated();
	ASSERT_TRUE(signalReceived) << "Did not receive signal PresetCreated";

	qcc::String retrievedPresetID;
	std::vector<ajn::MsgArg> lampState;
	status = m_PresetBusObject->GetPreset(presetID.c_str(), responseCode, retrievedPresetID, lampState);
	ASSERT_EQ(ER_OK, status) << "Calling GetPreset() method returned status code: " << QCC_StatusText(status);

	ASSERT_TRUE(compareLampStates(lampState, presetState)) << "LampState values does not match";

	presetState[0].Set("{sv}", "OnOff", new MsgArg("b", false));
	status = m_PresetBusObject->UpdatePreset(presetID.c_str(), presetState, responseCode, retrievedPresetID);
	ASSERT_EQ(ER_OK, status) << "Calling UpdatePreset() method returned status code: " << QCC_StatusText(status);

	WAIT_MILLISECONDS(500)
	signalReceived = m_PresetBusObject->DidPresetsUpdated();

	std::vector<qcc::String> presetIDs;
	status = m_PresetBusObject->GetAllPresetIDs(responseCode, presetIDs);
	ASSERT_EQ(ER_OK, status) << "Calling GetAllPresetIDs() method returned status code: " << QCC_StatusText(status);
	ASSERT_EQ(1, presetIDs.size()) << "The presets are not 1";

	status = m_PresetBusObject->DeletePreset(presetID.c_str(), responseCode, retrievedPresetID);
	ASSERT_EQ(ER_OK, status) << "Calling DeletePreset() method returned status code: " << QCC_StatusText(status);

	WAIT_MILLISECONDS(500)
	signalReceived = m_PresetBusObject->DidPresetsDeleted();
	ASSERT_TRUE(signalReceived) << "Did not receive signal PresetsDeleted";

	status = m_PresetBusObject->GetAllPresetIDs(responseCode, presetIDs);
	ASSERT_EQ(ER_OK, status) << "Calling GetAllPresetIDs() method returned status code: " << QCC_StatusText(status);
	ASSERT_EQ(0, presetIDs.size()) << "The presets are not 0";
}

TEST_F(LSFControllerTestSuite, LSF_Controller_v1_22)
{
	ajn::MsgArg* presetState = newLampState(true, 2147483648, 739688812, 2061584302, 384286547);
	const char* presetName = "ControllerTest Preset";
	uint32_t responseCode;
	qcc::String presetID;
	QStatus status = m_PresetBusObject->CreatePreset(presetState, presetName, "en", responseCode, presetID);
	ASSERT_EQ(ER_OK, status) << "Calling CreatePreset() method returned status code: " << QCC_StatusText(status);

	std::string newPresetName = std::string(presetName).append("X");
	qcc::String retrievedPresetID;
	qcc::String retrievedLanguage;
	status = m_PresetBusObject->SetPresetName(presetID.c_str(), newPresetName.c_str(), "en", responseCode, retrievedPresetID, retrievedLanguage);
	ASSERT_EQ(ER_OK, status) << "Calling SetPresetName() method returned status code: " << QCC_StatusText(status);

	WAIT_MILLISECONDS(500)
	bool signalReceived = m_PresetBusObject->DidPresetNameChanged();
	ASSERT_TRUE(signalReceived) << "Did not receive signal PresetsNameChanged";

	qcc::String retrievedPresetName;
	status = m_PresetBusObject->GetPresetName(presetID.c_str(), "en", responseCode, retrievedPresetID, retrievedLanguage, retrievedPresetName);
	ASSERT_EQ(ER_OK, status) << "Calling GetPresetName() method returned status code: " << QCC_StatusText(status);
	ASSERT_STRNE(newPresetName.c_str(), retrievedPresetName.c_str()) << "PresetName has not changed";
}

TEST_F(LSFControllerTestSuite, LSF_Controller_v1_23)
{
	qcc::String transitionSceneID;
	QStatus status = createTransitionScene(transitionSceneID);
	ASSERT_EQ(ER_OK, status) << "An error occurred creating a transition scene";

	qcc::String pulseSceneID;
	status = createPulseScene(pulseSceneID);
	ASSERT_EQ(ER_OK, status) << "An error occurred creating a pulse scene";

	WAIT_MILLISECONDS(500)
	bool signalReceived = m_SceneBusObject->DidScenesCreated();
	ASSERT_TRUE(signalReceived) << "Did not receive signal ScenesCreated";
}

QStatus LSFControllerTestSuite::createTransitionScene(qcc::String& t_SceneID)
{
	std::string lampID;
	QStatus status = getConnectedLamp(lampID);
	if (ER_OK != status)
	{
		return status;
	}

	std::vector<const char*> lampIDs = { lampID.c_str() };
	std::vector<const char*> lampGroupIDs;
	ajn::MsgArg* lampState = newLampState(true, 2147483648, 739688812, 2061584302, 384286547);
	std::vector<ajn::MsgArg> lampStateVector;
	for (size_t i = 0; i < 5; ++i)
	{
		lampStateVector.push_back(lampState[i]);
	}
	ajn::MsgArg transitionToState;
	status = transitionToState.Set("(asasa{sv}u)", lampIDs.size(), lampIDs.data(), lampGroupIDs.size(), lampGroupIDs.data(), lampStateVector.size(), lampStateVector.data(), 5000);
	if (ER_OK != status)
	{
		return status;
	}

	std::vector<ajn::MsgArg> transitionToStateVector = { transitionToState };
	std::vector<ajn::MsgArg> transitionToPresetVector;
	std::vector<ajn::MsgArg> pulseWithStateVector;
	std::vector<ajn::MsgArg> pulseWithPresetVector;

	uint32_t responseCode;
	status = m_SceneBusObject->CreateScene(transitionToStateVector, transitionToPresetVector, pulseWithStateVector, pulseWithPresetVector, "ControllerTransitionScene", "en", responseCode, t_SceneID);

	return status;
}

QStatus LSFControllerTestSuite::createPulseScene(qcc::String& t_SceneID)
{
	std::string lampID;
	QStatus status = getConnectedLamp(lampID);
	if (ER_OK != status)
	{
		return status;
	}

	std::vector<const char*> lampIDs = { lampID.c_str() };
	std::vector<const char*> lampGroupIDs;
	ajn::MsgArg* toState = newLampState(true, 2147483648, 1574821342, 2061584302, 384286547);
	std::vector<ajn::MsgArg> toStateVector;
	for (size_t i = 0; i < 5; ++i)
	{
		toStateVector.push_back(toState[i]);
	}
	ajn::MsgArg* fromState = newLampState(true, 2147483648, 739688812, 2061584302, 384286547);
	std::vector<ajn::MsgArg> fromStateVector;
	for (size_t i = 0; i < 5; ++i)
	{
		fromStateVector.push_back(fromState[i]);
	}
	ajn::MsgArg pulseWithState;
	pulseWithState.Set("(asasa{sv}a{sv}uuu)", lampIDs.size(), lampIDs.data(), lampGroupIDs.size(), lampGroupIDs.data(), toStateVector.size(), toStateVector.data(), fromStateVector.size(), fromStateVector.data(), 1000, 500, 5);

	std::vector<ajn::MsgArg> transitionToStateVector;
	std::vector<ajn::MsgArg> transitionToPresetVector;
	std::vector<ajn::MsgArg> pulseWithStateVector = { pulseWithState };
	std::vector<ajn::MsgArg> pulseWithPresetVector;

	uint32_t responseCode;
	status = m_SceneBusObject->CreateScene(transitionToStateVector, transitionToPresetVector, pulseWithStateVector, pulseWithPresetVector, "ControllerPulseScene", "en", responseCode, t_SceneID);

	if (0 != responseCode)
	{
		return ER_FAIL;
	}

	return status;
}

TEST_F(LSFControllerTestSuite, LSF_Controller_v1_24)
{
	qcc::String pulseSceneID;
	QStatus status = createPulseScene(pulseSceneID);
	ASSERT_EQ(ER_OK, status) << "An error occurred creating a pulse scene";

	uint32_t responseCode;
	qcc::String retrievedSceneID;
	std::vector<ajn::MsgArg> transitionlampsLampGroupsToState;
	std::vector<ajn::MsgArg> transitionlampsLampGroupsToPreset;
	std::vector<ajn::MsgArg> pulselampsLampGroupsWithState;
	std::vector<ajn::MsgArg> pulselampsLampGroupWithPreset;
	status = m_SceneBusObject->GetScene(pulseSceneID.c_str(), responseCode, retrievedSceneID, transitionlampsLampGroupsToState, transitionlampsLampGroupsToPreset, pulselampsLampGroupsWithState, pulselampsLampGroupWithPreset);
	ASSERT_EQ(ER_OK, status) << "Calling GetScene() method returned status code: " << QCC_StatusText(status);
	ASSERT_EQ(0, responseCode) << "Calling GetScene() method returned response code: " << responseCode;

	size_t lampIDsSize;
	ajn::MsgArg* lampIDs;
	size_t lampGroupIDsSize;
	ajn::MsgArg* lampGroupIDs;
	size_t toStateSize;
	ajn::MsgArg* toState;
	size_t fromStateSize;
	ajn::MsgArg* fromState;
	uint32_t period;
	uint32_t duration;
	uint32_t numPulses;
	status = pulselampsLampGroupsWithState[0].Get("(asasa{sv}a{sv}uuu)", &lampIDsSize, &lampIDs, &lampGroupIDsSize, &lampGroupIDs, &toStateSize, &toState, &fromStateSize, &fromState, &period, &duration, &numPulses);
	ASSERT_EQ(ER_OK, status) << "Retrieving FromState returned status code: " << QCC_StatusText(status);

	std::vector<const char*> lampIDsVector;
	for (size_t i = 0; i < lampIDsSize; ++i)
	{
		lampIDsVector.push_back(lampIDs[i].v_string.str);
	}

	std::vector<const char*> lampGroupIDsVector;
	for (size_t i = 0; i < lampGroupIDsSize; ++i)
	{
		lampGroupIDsVector.push_back(lampGroupIDs[i].v_string.str);
	}

	std::vector<ajn::MsgArg> toStateVector;
	for (size_t i = 0; i < toStateSize; ++i)
	{
		toStateVector.push_back(toState[i]);
	}

	ajn::MsgArg* newFromState = newLampState(false, 2147483648, 739688812, 2061584302, 384286547);
	std::vector<ajn::MsgArg> fromStateVector;
	for (size_t i = 0; i < fromStateSize; ++i)
	{
		fromStateVector.push_back(newFromState[i]);
	}

	ajn::MsgArg msgArg;
	status = msgArg.Set("(asasa{sv}a{sv}uuu)", lampIDsVector.size(), lampIDsVector.data(), lampGroupIDsVector.size(), lampGroupIDsVector.data(), toStateVector.size(), toStateVector.data(), fromStateVector.size(), fromStateVector.data(), period, duration, numPulses);
	ASSERT_EQ(ER_OK, status) << "Modifying pulselampsLampGroupsWithState returned status code: " << QCC_StatusText(status);
	
	std::vector<ajn::MsgArg> newPulselampsLampGroupsWithState;
	newPulselampsLampGroupsWithState.push_back(msgArg);

	status = m_SceneBusObject->UpdateScene(pulseSceneID.c_str(), transitionlampsLampGroupsToState, transitionlampsLampGroupsToPreset, newPulselampsLampGroupsWithState, pulselampsLampGroupWithPreset, responseCode, retrievedSceneID);
	ASSERT_EQ(ER_OK, status) << "Calling UpdateScene() method returned status code: " << QCC_StatusText(status);

	WAIT_MILLISECONDS(500)
	bool signalReceived = m_SceneBusObject->DidScenesUpdated();
	ASSERT_TRUE(signalReceived) << "Did not receive signal ScenesUpdated";

	status = m_SceneBusObject->DeleteScene(pulseSceneID.c_str(), responseCode, retrievedSceneID);
	ASSERT_EQ(ER_OK, status) << "Calling DeleteScene() method returned status code: " << QCC_StatusText(status);

	WAIT_MILLISECONDS(500)
	signalReceived = m_SceneBusObject->DidScenesDeleted();
	ASSERT_TRUE(signalReceived) << "Did not receive signal ScenesDeleted";

	std::vector<qcc::String> sceneIDs;
	status = m_SceneBusObject->GetAllSceneIDs(responseCode, sceneIDs);
	ASSERT_EQ(ER_OK, status) << "Calling GetAllSceneIDs() method returned status code: " << QCC_StatusText(status);
	ASSERT_EQ(0, sceneIDs.size()) << "Scene Update has not been deleted";
}

TEST_F(LSFControllerTestSuite, LSF_Controller_v1_25)
{
	qcc::String pulseSceneID;
	QStatus status = createPulseScene(pulseSceneID);
	ASSERT_EQ(ER_OK, status) << "An error occurred creating a pulse scene";
	
	uint32_t responseCode;
	qcc::String retrievedPulseSceneID;
	status = m_SceneBusObject->ApplyScene(pulseSceneID.c_str(), responseCode, retrievedPulseSceneID);
	ASSERT_EQ(ER_OK, status) << "Calling ApplyScene() method returned status code: " << QCC_StatusText(status);

	WAIT_MILLISECONDS(500)
	ASSERT_TRUE(m_SceneBusObject->DidScenesApplied()) << "Did not receive signal SceneApplied";
}

TEST_F(LSFControllerTestSuite, LSF_Controller_v1_26)
{
	qcc::String pulseSceneID;
	QStatus status = createPulseScene(pulseSceneID);
	ASSERT_EQ(ER_OK, status) << "An error occurred creating a pulse scene";

	std::string sceneName = "ControllerPulse";
	uint32_t responseCode;
	qcc::String retrievedPulseSceneID;
	qcc::String retrievedLanguage;
	status = m_SceneBusObject->SetSceneName(pulseSceneID.c_str(), sceneName.c_str(), "en", responseCode, retrievedPulseSceneID, retrievedLanguage);
	ASSERT_EQ(ER_OK, status) << "Calling SetSceneName() method returned status code: " << QCC_StatusText(status);

	WAIT_MILLISECONDS(500)
	ASSERT_TRUE(m_SceneBusObject->DidScenesNameChanged()) << "Did not receive signal ScenesNameChanged";

	qcc::String retrievedSceneName;
	status = m_SceneBusObject->GetSceneName(pulseSceneID.c_str(), "en", responseCode, retrievedPulseSceneID, retrievedLanguage, retrievedSceneName);
	ASSERT_EQ(ER_OK, status) << "Calling GetSceneName() method returned status code: " << QCC_StatusText(status);
	ASSERT_EQ(sceneName, std::string(retrievedSceneName.c_str())) << "Scene Name has not been changed";
}

TEST_F(LSFControllerTestSuite, LSF_Controller_v1_27)
{
	qcc::String masterSceneID;
	QStatus status = createMasterScene(masterSceneID);
	ASSERT_EQ(ER_OK, status) << "An error occurred creating MasterScene";

	WAIT_MILLISECONDS(500)
	ASSERT_TRUE(m_MasterSceneBusObject->DidMasterScenesCreated()) << "Did not receive signal MasterScenesCreated";
}

QStatus LSFControllerTestSuite::createMasterScene(qcc::String& t_MasterSceneID)
{
	qcc::String pulseSceneID;
	QStatus status = createPulseScene(pulseSceneID);
	if (ER_OK != status)
	{
		return status;
	}

	std::vector<const char*> sceneIDs = { pulseSceneID.c_str() };
	uint32_t responseCode;
	status = m_MasterSceneBusObject->CreateMasterScene(sceneIDs, "ControllerTestMS", "en", responseCode, t_MasterSceneID);

	if (0 != responseCode)
	{
		return ER_FAIL;
	}

	return status;
}

TEST_F(LSFControllerTestSuite, LSF_Controller_v1_28)
{
	qcc::String masterSceneID;
	QStatus status = createMasterScene(masterSceneID);
	ASSERT_EQ(ER_OK, status) << "An error occurred creating MasterScene";

	qcc::String transitionSceneID;
	status = createTransitionScene(transitionSceneID);
	ASSERT_EQ(ER_OK, status) << "An error occurred creating transition scene";

	std::vector<const char*> newScenes = { transitionSceneID.c_str() };
	uint32_t responseCode;
	qcc::String retrievedMasterSceneID;
	status = m_MasterSceneBusObject->UpdateMasterScene(masterSceneID.c_str(), newScenes, responseCode, retrievedMasterSceneID);
	ASSERT_EQ(ER_OK, status) << "Calling UpdateMasterScene() method returned status code: " << QCC_StatusText(status);

	WAIT_MILLISECONDS(500)
	ASSERT_TRUE(m_MasterSceneBusObject->DidMasterScenesUpdated()) << "Did not receive signal MasterScenesUpdated";

	status = m_MasterSceneBusObject->DeleteMasterScene(masterSceneID.c_str(), responseCode, retrievedMasterSceneID);
	ASSERT_EQ(ER_OK, status) << "Calling DeleteMasterScene() method returned status code: " << QCC_StatusText(status);

	WAIT_MILLISECONDS(500)
	ASSERT_TRUE(m_MasterSceneBusObject->DidMasterScenesDeleted()) << "Did not receive signal MasterScenesDeleted";

	std::vector<qcc::String> masterSceneIDs;
	status = m_MasterSceneBusObject->GetAllMasterSceneIDs(responseCode, masterSceneIDs);
	ASSERT_EQ(ER_OK, status) << "Calling GetAllMasterSceneIDs() method returned status code: " << QCC_StatusText(status);
	ASSERT_EQ(0, masterSceneIDs.size()) << "MasterScene update has not been deleted";
}

TEST_F(LSFControllerTestSuite, LSF_Controller_v1_29)
{
	qcc::String masterSceneID;
	QStatus status = createMasterScene(masterSceneID);
	ASSERT_EQ(ER_OK, status) << "An error occurred creating MasterScene";

	uint32_t responseCode;
	qcc::String retrievedMasterSceneID;
	status = m_MasterSceneBusObject->ApplyMasterScene(masterSceneID.c_str(), responseCode, retrievedMasterSceneID);
	ASSERT_EQ(ER_OK, status) << "Calling ApplyMasterScene() method returned status code: " << QCC_StatusText(status);

	WAIT_MILLISECONDS(5000)
	ASSERT_TRUE(m_MasterSceneBusObject->DidMasterScenesApplied()) << "Did not receive signal MasterScenesApplied";
}

TEST_F(LSFControllerTestSuite, LSF_Controller_v1_30)
{
	qcc::String masterSceneID;
	QStatus status = createMasterScene(masterSceneID);
	ASSERT_EQ(ER_OK, status) << "An error occurred creating MasterScene";
	std::string masterSceneName("ControllerMasterScene");

	uint32_t responseCode;
	qcc::String retrievedMasterSceneID;
	qcc::String retrievedLanguage;
	status = m_MasterSceneBusObject->SetMasterSceneName(masterSceneID.c_str(), masterSceneName.c_str(), "en", responseCode, retrievedMasterSceneID, retrievedLanguage);
	ASSERT_EQ(ER_OK, status) << "Calling SetMasterSceneName() method returned status code: " << QCC_StatusText(status);

	WAIT_MILLISECONDS(500)
	ASSERT_TRUE(m_MasterSceneBusObject->DidMasterScenesNameChanged()) << "Did not receive signal MasterScenesNameChanged";

	qcc::String retrievedMasterSceneName;
	status = m_MasterSceneBusObject->GetMasterSceneName(masterSceneID.c_str(), "en", responseCode, retrievedMasterSceneID, retrievedLanguage, retrievedMasterSceneName);
	ASSERT_EQ(ER_OK, status) << "Calling GetMasterSceneName() method returned status code: " << QCC_StatusText(status);

	ASSERT_EQ(masterSceneName, std::string(retrievedMasterSceneName.c_str())) << "MasterScene name has not been changed";
}

TEST_F(LSFControllerTestSuite, LSF_Controller_v1_31)
{
	std::vector<ajn::MsgArg> checksumAndTimeStampVector;
	QStatus status = m_LeaderElectionAndStateSyncBusObject->GetChecksumAndModificationTimestamp(checksumAndTimeStampVector);
	ASSERT_EQ(ER_OK, status) << "Calling GetChecksumAndModificationTimestamp() method returned status code: " << QCC_StatusText(status);

	for (auto& checksumAndTimestamp : checksumAndTimeStampVector)
	{
		uint32_t blobType;
		uint32_t checksum;
		uint64_t timestamp;
		checksumAndTimestamp.Get("(uut)", &blobType, &checksum, &timestamp);

		LOG(INFO) << "blobType: " << blobType;
		uint32_t retrievedBlobType;
		qcc::String blob;
		uint32_t retrievedChecksum;
		uint64_t retrievedTimestamp;
		status = m_LeaderElectionAndStateSyncBusObject->GetBlob(blobType, retrievedBlobType, blob, retrievedChecksum, retrievedTimestamp);
		ASSERT_EQ(ER_OK, status) << "Calling GetBlob() method returned status code: " << QCC_StatusText(status);
		ASSERT_EQ(checksum, retrievedChecksum) << "Checksum returned from calling GetBlob() is not consistent with that returned by GetChecksumAndModificationTimestamp()";
		LOG(INFO) << "timestamp: " << timestamp;
		LOG(INFO) << "blob: " << blob.c_str();
	}
}

TEST_F(LSFControllerTestSuite, LSF_Controller_v1_32)
{
	qcc::String pulseSceneID;
	QStatus status = createPulseScene(pulseSceneID);
	ASSERT_EQ(ER_OK, status) << "An error occurred creating pulse scene";

	WAIT_MILLISECONDS(500);
	ASSERT_TRUE(m_LeaderElectionAndStateSyncBusObject->DidBlobChanged()) << "Did not receive signal BlobChanged";
}

TEST_F(LSFControllerTestSuite, LSF_Controller_v1_33)
{
	bool overthrown;
	QStatus status = m_LeaderElectionAndStateSyncBusObject->Overthrow(overthrown);
	ASSERT_EQ(ER_OK, status) << "Calling Overthrow() method returned status code: " << QCC_StatusText(status);
	LOG(INFO) << "overthrown: " << overthrown;
}