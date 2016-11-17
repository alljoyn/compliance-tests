/******************************************************************************
* Copyright (c) 2016 Open Connectivity Foundation (OCF) and AllJoyn Open
*    Source Project (AJOSP) Contributors and others.
*
*    SPDX-License-Identifier: Apache-2.0
*
*    All rights reserved. This program and the accompanying materials are
*    made available under the terms of the Apache License, Version 2.0
*    which accompanies this distribution, and is available at
*    http://www.apache.org/licenses/LICENSE-2.0
*
*    Copyright 2016 Open Connectivity Foundation and Contributors to
*    AllSeen Alliance. All rights reserved.
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
#include "LSFLampTestSuite.h"

#include <thread>

#include "AboutAnnouncementDetails.h"
#include "ArrayParser.h"

#include <alljoyn\AllJoynStd.h>

using namespace ajn;
using namespace std;

const char* LSFLampTestSuite::BUS_APPLICATION_NAME = "LSFLampTestSuite";
const char* LSFLampTestSuite::BUS_OBJECT_PATH = "/org/allseen/LSF/Lamp";
const char* LSFLampTestSuite::LAMPSERVICE_INTERFACE_NAME = "org.allseen.LSF.LampService";
const char* LSFLampTestSuite::LAMPSTATE_INTERFACE_NAME = "org.allseen.LSF.LampState";
const char* LSFLampTestSuite::LAMPDETAILS_INTERFACE_NAME = "org.allseen.LSF.LampDetails";
const char* LSFLampTestSuite::LAMPPARAMETERS_INTERFACE_NAME = "org.allseen.LSF.LampParameters";
const char* LSFLampTestSuite::LAMP_STATE_FIELD_ON_OFF = "OnOff";
const char* LSFLampTestSuite::LAMP_STATE_FIELD_BRIGHTNESS = "Brightness";
const char* LSFLampTestSuite::LAMP_STATE_FIELD_HUE = "Hue";
const char* LSFLampTestSuite::LAMP_STATE_FIELD_SATURATION = "Saturation";
const char* LSFLampTestSuite::LAMP_STATE_FIELD_COLOR_TEMP = "ColorTemp";

LSFLampTestSuite::LSFLampTestSuite() : IOManager(ServiceFramework::LSF_LAMP)
{

}

void LSFLampTestSuite::SetUp()
{
	CLOG(INFO, "raw") << "====================================================";
	LOG(INFO) << "test setUp started";

	m_DutDeviceId = m_IxitMap.at("IXITCO_DeviceId");
	m_DutAppId = ArrayParser::parseAppIdFromString(m_IxitMap.at("IXITCO_AppId"));

	m_LampObjectPath = BUS_OBJECT_PATH;
	LOG(INFO) << "Executing lamp test against Stream object found at " << m_LampObjectPath;

	QStatus status = initServiceHelper();
	ASSERT_EQ(ER_OK, status) << "serviceHelper Initialize() failed: " << QCC_StatusText(status);

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

	m_ServiceHelper->enableAuthentication("/Keystore");
	m_BusIntrospector = new XMLBasedBusIntrospector(m_ServiceHelper->getBusIntrospector(*m_DeviceAboutAnnouncement));

	LOG(INFO) << "test setUp done";
	CLOG(INFO, "raw") << "====================================================";
}

QStatus LSFLampTestSuite::initServiceHelper()
{
	QStatus status = ER_OK;

	releaseResources();

	m_ServiceHelper = new ServiceHelper();
	if ((status = m_ServiceHelper->initializeClient(BUS_APPLICATION_NAME, m_DutDeviceId, m_DutAppId,
		m_IcsMap.at("ICSCO_SrpKeyX"), m_IxitMap.at("IXITCO_SrpKeyXPincode"),
		m_IcsMap.at("ICSCO_SrpLogon"), m_IxitMap.at("IXITCO_SrpLogonUser"), m_IxitMap.at("IXITCO_SrpLogonPass"),
		m_IcsMap.at("ICSCO_EcdheNull"),
		m_IcsMap.at("ICSCO_EcdhePsk"), m_IxitMap.at("IXITCO_EcdhePskPassword"),
		m_IcsMap.at("ICSCO_EcdheEcdsa"), m_IxitMap.at("IXITCO_EcdheEcdsaPrivateKey"), m_IxitMap.at("IXITCO_EcdheEcdsaCertChain"),
		m_IcsMap.at("ICSCO_EcdheSpeke"), m_IxitMap.at("IXITCO_EcdheSpekePassword"))) != ER_OK)
	{
		return status;
	}

	return status;
}

void LSFLampTestSuite::initProxyBusObjects()
{
	m_LampServiceBusObject = new LampServiceBusObject(*m_ServiceHelper->getBusAttachmentMgr()->getBusAttachment(),
		m_DeviceAboutAnnouncement->getServiceName(), m_ServiceHelper->getSessionId());

	m_LampParametersBusObject = new LampParametersBusObject(*m_ServiceHelper->getBusAttachmentMgr()->getBusAttachment(),
		m_DeviceAboutAnnouncement->getServiceName(), m_ServiceHelper->getSessionId());

	m_LampDetailsBusObject = new LampDetailsBusObject(*m_ServiceHelper->getBusAttachmentMgr()->getBusAttachment(),
		m_DeviceAboutAnnouncement->getServiceName(), m_ServiceHelper->getSessionId());

	m_LampStateBusObject = new LampStateBusObject(*m_ServiceHelper->getBusAttachmentMgr()->getBusAttachment(),
		m_DeviceAboutAnnouncement->getServiceName(), m_ServiceHelper->getSessionId());
}

void LSFLampTestSuite::releaseResources()
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

/*void LSFLampTestSuite::waitForSessionToClose()
{
	LOG(INFO) << "Waiting for session to close";
	m_ServiceHelper->waitForSessionToClose(SESSION_CLOSE_TIMEOUT);
}*/

void LSFLampTestSuite::TearDown()
{
	CLOG(INFO, "raw") << "====================================================";
	LOG(INFO) << "test tearDown started";

	releaseResources();

	LOG(INFO) << "test tearDown done";
	CLOG(INFO, "raw") << "====================================================";

	storeResults();
}

TEST_F(LSFLampTestSuite, LSF_Lamp_v1_01)
{
	uint32_t lampInterfaceVersion;
	QStatus status = m_LampServiceBusObject->GetVersion(lampInterfaceVersion);
	ASSERT_EQ(ER_OK, status) << "Retrieving Version property from LampService interface returned error: "
		<< QCC_StatusText(status);

	LOG(INFO) << "Checking if Interface version matches IXIT";
	EXPECT_EQ(static_cast<uint32_t>(ArrayParser::stringToUint16(m_IxitMap.at("IXITL_LampServiceVersion").c_str())),
		lampInterfaceVersion) << "Interface version does not match";
}

TEST_F(LSFLampTestSuite, LSF_Lamp_v1_02)
{
	uint32_t lampServiceVersion;
	QStatus status = m_LampServiceBusObject->GetLampServiceVersion(lampServiceVersion);
	ASSERT_EQ(ER_OK, status)
		<< "Retrieving LampServiceVersion property from LampService interface returned error: " << QCC_StatusText(status);

	LOG(INFO) << "Checking if Interface version matches IXIT";
	EXPECT_EQ(static_cast<uint32_t>(ArrayParser::stringToUint16(m_IxitMap.at("IXITL_LampServiceVersion").c_str())),
		lampServiceVersion) << "Interface version does not match";
}

TEST_F(LSFLampTestSuite, LSF_Lamp_v1_03)
{
	std::vector<uint32_t> lampFaults;
	QStatus status = m_LampServiceBusObject->GetLampFaults(lampFaults);
	ASSERT_EQ(ER_OK, status)
		<< "Retrieving LampFaults property from LampService interface returned error: " << QCC_StatusText(status);

	if (lampFaults.size() > 0)
	{
		uint32_t lampResponseCode;
		uint32_t retrievedLampFaultCode;
		status = m_LampServiceBusObject->ClearLampFault(lampFaults[0], lampResponseCode, retrievedLampFaultCode);
		ASSERT_EQ(ER_OK, status) << "Calling ClearLampFault() method returned status code: " << QCC_StatusText(status);

		if (lampResponseCode == 0)
		{
			status = m_LampServiceBusObject->GetLampFaults(lampFaults);
			ASSERT_EQ(ER_OK, status)
				<< "Retrieving LampFaults property from LampService interface returned error: " << QCC_StatusText(status);
		}

		LOG(INFO) << "Clear lamp fault returned " << lampResponseCode << " for fault code " << retrievedLampFaultCode;
	}
}

TEST_F(LSFLampTestSuite, LSF_Lamp_v1_04)
{
	bool onOff = true;
	LOG(INFO) << "Setting OnOff property in LampState interface with the value " << onOff;
	QStatus status = m_LampStateBusObject->SetOnOff(onOff);
	ASSERT_EQ(ER_OK, status) << "Setting OnOff property in LampState interface returned error: " << QCC_StatusText(status);

	bool newOnOff;
	LOG(INFO) << "Retrieving OnOff property from LampState interface";
	status = m_LampStateBusObject->GetOnOff(newOnOff);
	ASSERT_EQ(ER_OK, status) << "Retrieving OnOff property from LampState interface returned error: " << QCC_StatusText(status);

	ASSERT_EQ(onOff, newOnOff) << "OnOff property from LampState was not modified";
}

TEST_F(LSFLampTestSuite, LSF_Lamp_v1_05)
{
	uint32_t hue = 100;
	LOG(INFO) << "Setting Hue property in LampState interface with the value " << hue;
	QStatus status = m_LampStateBusObject->SetHue(hue);
	ASSERT_EQ(ER_OK, status) << "Setting Hue property in LampState interface returned error: " << QCC_StatusText(status);

	uint32_t newHue;
	LOG(INFO) << "Retrieving Hue property from LampState interface";
	status = m_LampStateBusObject->GetHue(newHue);
	ASSERT_EQ(ER_OK, status) << "Retrieving Hue property from LampState interface returned error: " << QCC_StatusText(status);

	ASSERT_EQ(hue, newHue) << "Hue property from LampState was not modified";
}

TEST_F(LSFLampTestSuite, LSF_Lamp_v1_06)
{
	uint32_t saturation = 100;
	LOG(INFO) << "Setting Saturation property in LampState interface with the value " << saturation;
	QStatus status = m_LampStateBusObject->SetSaturation(saturation);
	ASSERT_EQ(ER_OK, status) << "Setting Saturation property in LampState interface returned error: " << QCC_StatusText(status);

	uint32_t newSaturation;
	LOG(INFO) << "Retrieving Saturation property from LampState interface";
	status = m_LampStateBusObject->GetSaturation(newSaturation);
	ASSERT_EQ(ER_OK, status) << "Retrieving Saturation property from LampState interface returned error: " << QCC_StatusText(status);

	ASSERT_EQ(saturation, newSaturation) << "Saturation property from LampState was not modified";
}

TEST_F(LSFLampTestSuite, LSF_Lamp_v1_07)
{
	if (getVariableColorTemp())
	{
		uint32_t colorTemp = 100;
		LOG(INFO) << "Setting ColorTemp property in LampState interface with the value " << colorTemp;
		QStatus status = m_LampStateBusObject->SetColorTemp(colorTemp);
		ASSERT_EQ(ER_OK, status) << "Setting ColorTemp property in LampState interface returned error: " << QCC_StatusText(status);

		uint32_t newColorTemp;
		LOG(INFO) << "Retrieving ColorTemp property from LampState interface";
		status = m_LampStateBusObject->GetColorTemp(newColorTemp);
		ASSERT_EQ(ER_OK, status) << "Retrieving ColorTemp property from LampState interface returned error: " << QCC_StatusText(status);

		ASSERT_EQ(colorTemp, newColorTemp) << "ColorTemp property from LampState was not modified";
	}
}

bool LSFLampTestSuite::getVariableColorTemp()
{
	LOG(INFO) << "Retrieving VariableColorTemp property from LampDetails interface";
	bool variableColorTemp = false;
	QStatus status = m_LampDetailsBusObject->GetVariableColorTemp(variableColorTemp);
	EXPECT_EQ(ER_OK, status) << "Retrieving VariableColorTemp property from LampDetails interface returned status code: " << QCC_StatusText(status);
	
	return variableColorTemp;
}

TEST_F(LSFLampTestSuite, LSF_Lamp_v1_08)
{
	if (getDimmable())
	{
		uint32_t brightness = 100;
		LOG(INFO) << "Setting Brightness property in LampState interface with the value " << brightness;
		QStatus status = m_LampStateBusObject->SetBrightness(brightness);
		ASSERT_EQ(ER_OK, status) << "Setting Brightness property in LampState interface returned error: " << QCC_StatusText(status);

		uint32_t newBrightness;
		LOG(INFO) << "Retrieving Brightness property from LampState interface";
		status = m_LampStateBusObject->GetBrightness(newBrightness);
		ASSERT_EQ(ER_OK, status) << "Retrieving Brightness property from LampState interface returned error: " << QCC_StatusText(status);

		ASSERT_EQ(brightness, newBrightness) << "Brightness property from LampState was not modified";
	}
}

bool LSFLampTestSuite::getDimmable()
{
	LOG(INFO) << "Retrieving Dimmable property from LampDetails interface";
	bool dimmable = false;
	QStatus status = m_LampDetailsBusObject->GetDimmable(dimmable);
	EXPECT_EQ(ER_OK, status) << "Retrieving Dimmable property from LampDetails interface returned status code: " << QCC_StatusText(status);

	return dimmable;
}

TEST_F(LSFLampTestSuite, LSF_Lamp_v1_09)
{
	bool onOffValue = true;
	uint32_t brightnessValue = 10;
	uint32_t hueValue = 20;
	uint32_t saturationValue = 30;
	uint32_t colorTempValue = 40;
	clock_t timestamp = clock();
	uint32_t transitionPeriod = 100;

	MsgArg* lampState = getLampStateMsgArg(onOffValue, brightnessValue, hueValue, saturationValue, colorTempValue);

	LOG(INFO) << "Calling TransitionLampState() method";
	uint32_t lampResponseCode;
	QStatus status = m_LampStateBusObject->TransitionLampState(timestamp, lampState, transitionPeriod, lampResponseCode);
	ASSERT_EQ(ER_OK, status) << "Calling TransitionLampState() returned status code: " << QCC_StatusText(status);

	LOG(INFO) << "Retrieving Hue property from LampState interface";
	uint32_t newHue;
	status = m_LampStateBusObject->GetHue(newHue);
	ASSERT_EQ(ER_OK, status) << "Retrieving Hue property from LampState interface returned status code: " << QCC_StatusText(status);
	EXPECT_EQ(hueValue, newHue) << "Retrieved Hue does not match the one set";

	LOG(INFO) << "Retrieving Saturation property from LampState interface";
	uint32_t newSaturation;
	status = m_LampStateBusObject->GetSaturation(newSaturation);
	ASSERT_EQ(ER_OK, status) << "Retrieving Saturation property from LampState interface returned status code: " << QCC_StatusText(status);
	EXPECT_EQ(saturationValue, newSaturation) << "Retrieved Saturation does not match the one set";

	LOG(INFO) << "Retrieving ColorTemp property from LampState interface";
	uint32_t newColorTemp;
	status = m_LampStateBusObject->GetColorTemp(newColorTemp);
	ASSERT_EQ(ER_OK, status) << "Retrieving ColorTemp property from LampState interface returned status code: " << QCC_StatusText(status);
	EXPECT_EQ(colorTempValue, newColorTemp) << "Retrieved ColorTemp does not match the one set";

	LOG(INFO) << "Retrieving Brightness property from LampState interface";
	uint32_t newBrightness;
	status = m_LampStateBusObject->GetBrightness(newBrightness);
	ASSERT_EQ(ER_OK, status) << "Retrieving Brightness property from LampState interface returned status code: " << QCC_StatusText(status);
	EXPECT_EQ(brightnessValue, newBrightness) << "Retrieved Brightness does not match the one set";

	LOG(INFO) << "Retrieving OnOff property from LampState interface";
	bool newOnOff;
	status = m_LampStateBusObject->GetOnOff(newOnOff);
	ASSERT_EQ(ER_OK, status) << "Retrieving OnOff property from LampState interface returned status code: " << QCC_StatusText(status);
	EXPECT_EQ(onOffValue, newOnOff) << "Retrieved OnOff does not match the one set";

	LOG(INFO) << "Waiting for TransitionLampState signal return";
	std::this_thread::sleep_for(std::chrono::milliseconds(5000));
	ASSERT_TRUE(m_LampStateBusObject->DidLampStateChanged()) << "LSF_Lamp TransitionLampState await signal returns failure";
}

TEST_F(LSFLampTestSuite, LSF_Lamp_v1_10)
{
	bool fromOnOffValue = true;
	uint32_t fromBrightnessValue = 10;
	uint32_t fromHueValue = 20;
	uint32_t fromSaturationValue = 30;
	uint32_t fromColorTempValue = 40;
	ajn::MsgArg* fromState = getLampStateMsgArg(fromOnOffValue, fromBrightnessValue,
		fromHueValue, fromSaturationValue, fromColorTempValue);

	bool toOnOffValue = true;
	uint32_t toBrightnessValue = 11;
	uint32_t toHueValue = 21;
	uint32_t toSaturationValue = 31;
	uint32_t toColorTempValue = 41;
	ajn::MsgArg* toState = getLampStateMsgArg(toOnOffValue, toBrightnessValue,
		toHueValue, toSaturationValue, toColorTempValue);

	clock_t startTimestamp = clock();
	uint32_t period = 100;
	uint32_t duration = 200;
	uint32_t numPulses = 3;

	LOG(INFO) << "Calling ApplyPulseEffect() method";
	uint32_t lampResponseCode;
	QStatus status = m_LampStateBusObject->ApplyPulseEffect(fromState, toState, period, duration, numPulses, startTimestamp, lampResponseCode);
	ASSERT_EQ(ER_OK, status) << "Calling ApplyPulseEffect() method returned status code: " << QCC_StatusText(status);
	ASSERT_EQ(0, lampResponseCode) << "LSF_Lamp ApplyPulseEffect() method returns failure: " << lampResponseCode;
}

ajn::MsgArg* LSFLampTestSuite::getLampStateMsgArg(bool t_OnOff,
	uint32_t t_Brightness, uint32_t t_Hue, uint32_t t_Saturation,
	uint32_t t_ColorTemp)
{
	ajn::MsgArg* message = new MsgArg[5];

	message[0].Set("{sv}", LAMP_STATE_FIELD_ON_OFF, new MsgArg("b", t_OnOff));
	message[1].Set("{sv}", LAMP_STATE_FIELD_BRIGHTNESS, new MsgArg("u", t_Brightness));
	message[2].Set("{sv}", LAMP_STATE_FIELD_HUE, new MsgArg("u", t_Hue));
	message[3].Set("{sv}", LAMP_STATE_FIELD_SATURATION, new MsgArg("u", t_Saturation));
	message[4].Set("{sv}", LAMP_STATE_FIELD_COLOR_TEMP, new MsgArg("u", t_ColorTemp));

	return message;
}

TEST_F(LSFLampTestSuite, LSF_Lamp_v1_11)
{
	checkForUnknownInterfacesFromAboutAnnouncement();

	std::list<InterfaceDetail> interfacesExposedOnBusBasedOnName = m_BusIntrospector->getInterfacesExposedOnBusBasedOnName(LAMPPARAMETERS_INTERFACE_NAME);

	for (auto interfaceDetail : interfacesExposedOnBusBasedOnName)
	{
		for (auto introspectionInterface : interfaceDetail.getIntrospectionInterfaces())
		{
			LOG(INFO) << "Found object at " << interfaceDetail.getPath() << " implementing " << introspectionInterface.getName();
		}
	}

	LOG(INFO) << "Checking that LampParameters interface XML matches";
	ValidationResult validationResult = getInterfaceValidator()->validate(interfacesExposedOnBusBasedOnName);
	ASSERT_TRUE(validationResult.isValid()) << validationResult.getFailureReason();

	interfacesExposedOnBusBasedOnName = m_BusIntrospector->getInterfacesExposedOnBusBasedOnName(LAMPSERVICE_INTERFACE_NAME);
	for (auto interfaceDetail : interfacesExposedOnBusBasedOnName)
	{
		for (auto introspectionInterface : interfaceDetail.getIntrospectionInterfaces())
		{
			LOG(INFO) << "Found object at " << interfaceDetail.getPath() << " implementing " << introspectionInterface.getName();
		}
	}

	LOG(INFO) << "Checking that LampService interface XML matches";
	validationResult = getInterfaceValidator()->validate(interfacesExposedOnBusBasedOnName);
	ASSERT_TRUE(validationResult.isValid()) << validationResult.getFailureReason();

	interfacesExposedOnBusBasedOnName = m_BusIntrospector->getInterfacesExposedOnBusBasedOnName(LAMPDETAILS_INTERFACE_NAME);
	for (auto interfaceDetail : interfacesExposedOnBusBasedOnName)
	{
		for (auto introspectionInterface : interfaceDetail.getIntrospectionInterfaces())
		{
			LOG(INFO) << "Found object at " << interfaceDetail.getPath() << " implementing " << introspectionInterface.getName();
		}
	}

	LOG(INFO) << "Checking that LampDetails interface XML matches";
	validationResult = getInterfaceValidator()->validate(interfacesExposedOnBusBasedOnName);
	ASSERT_TRUE(validationResult.isValid()) << validationResult.getFailureReason();

	interfacesExposedOnBusBasedOnName = m_BusIntrospector->getInterfacesExposedOnBusBasedOnName(LAMPSTATE_INTERFACE_NAME);
	for (auto interfaceDetail : interfacesExposedOnBusBasedOnName)
	{
		for (auto introspectionInterface : interfaceDetail.getIntrospectionInterfaces())
		{
			LOG(INFO) << "Found object at " << interfaceDetail.getPath() << " implementing " << introspectionInterface.getName();
		}
	}

	LOG(INFO) << "Checking that LampState interface XML matches";
	validationResult = getInterfaceValidator()->validate(interfacesExposedOnBusBasedOnName);
	ASSERT_TRUE(validationResult.isValid()) << validationResult.getFailureReason();
}

void LSFLampTestSuite::checkForUnknownInterfacesFromAboutAnnouncement()
{
	size_t numPaths = m_DeviceAboutAnnouncement->getObjectDescriptions()->GetPaths(NULL, 0);
	const char** paths = new const char*[numPaths];
	m_DeviceAboutAnnouncement->getObjectDescriptions()->GetPaths(paths, numPaths);

	for (size_t i = 0; i < numPaths; ++i)
	{
		if (std::string(paths[i]).compare(BUS_OBJECT_PATH) == 0)
		{
			size_t numInterfaces = m_DeviceAboutAnnouncement->getObjectDescriptions()->GetInterfaces(paths[i], NULL, 0);
			const char** interfaces = new const char*[numInterfaces];
			m_DeviceAboutAnnouncement->getObjectDescriptions()->GetInterfaces(paths[i], interfaces, numInterfaces);

			for (size_t j = 0; j < numInterfaces; ++j)
			{
				// Found an interface on our bus object
				LOG(INFO) << "Found on our object interface " << interfaces[j];

				// see if we can validate found interface
				std::list<InterfaceDetail> interfacesExposedOnBusBasedOnName = m_BusIntrospector->getInterfacesExposedOnBusBasedOnName(interfaces[j]);
				for (auto interfaceDetail : interfacesExposedOnBusBasedOnName)
				{
					for (auto introspectionInterface : interfaceDetail.getIntrospectionInterfaces())
					{
						LOG(INFO) << "Found (unknown?) object at " << interfaceDetail.getPath() << " implementing " << introspectionInterface.getName();
					}
				}

				ValidationResult validationResult = getInterfaceValidator()->validate(interfacesExposedOnBusBasedOnName);
				LOG(INFO) << "after found interface validate " << validationResult.isValid();
				ASSERT_TRUE(validationResult.isValid()) << validationResult.getFailureReason();
			}
		}
	}
}

InterfaceValidator* LSFLampTestSuite::getInterfaceValidator()
{
	if (!m_InterfaceValidator)
	{
		m_InterfaceValidator = new InterfaceValidator();
	}

	return m_InterfaceValidator;
}

TEST_F(LSFLampTestSuite, LSF_Lamp_v1_12)
{
	uint32_t lampParametersInterfaceVersion;
	QStatus status = m_LampParametersBusObject->GetVersion(lampParametersInterfaceVersion);
	ASSERT_EQ(ER_OK, status) << "Retrieving Version property from LampParameters interface returned status code: "
		<< QCC_StatusText(status);

	LOG(INFO) << "Checking if LampParameters interface version matches IXIT";
	EXPECT_EQ(static_cast<uint32_t>(ArrayParser::stringToUint16(m_IxitMap.at("IXITL_LampParametersVersion").c_str())),
		lampParametersInterfaceVersion) << "Interface version does not match";
}

TEST_F(LSFLampTestSuite, LSF_Lamp_v1_13)
{
	LOG(INFO) << "Retrieving Energy_Usage_Milliwatts property from LampParameters interface";
	uint32_t lampParametersInterfaceEnergy;
	QStatus status = m_LampParametersBusObject->GetEnergyUsageMilliwatts(lampParametersInterfaceEnergy);
	ASSERT_EQ(ER_OK, status) << "Retrieving Energy_Usage_Milliwatts property from LampParameters interface returned status code: "
		<< QCC_StatusText(status);
}

TEST_F(LSFLampTestSuite, LSF_Lamp_v1_14)
{
	LOG(INFO) << "Retrieving Brightness_Lumens property from LampParameters interface";
	uint32_t lampParametersInterfaceBrightness;
	QStatus status = m_LampParametersBusObject->GetBrightnessLumens(lampParametersInterfaceBrightness);
	ASSERT_EQ(ER_OK, status) << "Retrieving Brightness_Lumens property from LampParameters interface returned status code: "
		<< QCC_StatusText(status);
}

TEST_F(LSFLampTestSuite, LSF_Lamp_v1_15)
{
	uint32_t version;
	QStatus status = m_LampDetailsBusObject->GetVersion(version);
	ASSERT_EQ(ER_OK, status) << "Retrieving Version property from LampDetails interface returned status code: "
		<< QCC_StatusText(status);

	LOG(INFO) << "Checking if LampDetails interface version matches IXIT";
	EXPECT_EQ(static_cast<uint32_t>(ArrayParser::stringToUint16(m_IxitMap.at("IXITL_LampDetailsVersion").c_str())),
		version) << "Interface version does not match";
}

TEST_F(LSFLampTestSuite, LSF_Lamp_v1_16)
{
	LOG(INFO) << "Retrieving Make property from LampDetails interface";
	uint32_t make;
	QStatus status = m_LampDetailsBusObject->GetMake(make);
	ASSERT_EQ(ER_OK, status) << "Retrieving Make property from LampDetails interface returned error: "
		<< QCC_StatusText(status);
}

TEST_F(LSFLampTestSuite, LSF_Lamp_v1_17)
{
	LOG(INFO) << "Retrieving Model property from LampDetails interface";
	uint32_t model;
	QStatus status = m_LampDetailsBusObject->GetModel(model);
	ASSERT_EQ(ER_OK, status) << "Retrieving Model property from LampDetails interface returned error: "
		<< QCC_StatusText(status);
}

TEST_F(LSFLampTestSuite, LSF_Lamp_v1_18)
{
	LOG(INFO) << "Retrieving Type property from LampDetails interface";
	uint32_t type;
	QStatus status = m_LampDetailsBusObject->GetType(type);
	ASSERT_EQ(ER_OK, status) << "Retrieving Type property from LampDetails interface returned error: "
		<< QCC_StatusText(status);
}

TEST_F(LSFLampTestSuite, LSF_Lamp_v1_19)
{
	LOG(INFO) << "Retrieving LampType property from LampDetails interface";
	uint32_t lampType;
	QStatus status = m_LampDetailsBusObject->GetLampType(lampType);
	ASSERT_EQ(ER_OK, status) << "Retrieving LampType property from LampDetails interface returned error: "
		<< QCC_StatusText(status);
}

TEST_F(LSFLampTestSuite, LSF_Lamp_v1_20)
{
	LOG(INFO) << "Retrieving LampBaseType property from LampDetails interface";
	uint32_t lampBaseType;
	QStatus status = m_LampDetailsBusObject->GetLampBaseType(lampBaseType);
	ASSERT_EQ(ER_OK, status) << "Retrieving LampBaseType property from LampDetails interface returned error: "
		<< QCC_StatusText(status);
}

TEST_F(LSFLampTestSuite, LSF_Lamp_v1_21)
{
	LOG(INFO) << "Retrieving LampBeamAngle property from LampDetails interface";
	uint32_t lampBeamAngle;
	QStatus status = m_LampDetailsBusObject->GetLampBeamAngle(lampBeamAngle);
	ASSERT_EQ(ER_OK, status) << "Retrieving LampBeamAngle property from LampDetails interface returned error: "
		<< QCC_StatusText(status);
}

TEST_F(LSFLampTestSuite, LSF_Lamp_v1_22)
{
	getDimmable();
}

TEST_F(LSFLampTestSuite, LSF_Lamp_v1_23)
{
	LOG(INFO) << "Retrieving Color property from LampDetails interface";
	bool color;
	QStatus status = m_LampDetailsBusObject->GetColor(color);
	ASSERT_EQ(ER_OK, status) << "Retrieving Color property from LampDetails interface returned error: "
		<< QCC_StatusText(status);
}

TEST_F(LSFLampTestSuite, LSF_Lamp_v1_24)
{
	getVariableColorTemp();
}

TEST_F(LSFLampTestSuite, LSF_Lamp_v1_25)
{
	LOG(INFO) << "Retrieving LampID property from LampDetails interface";
	qcc::String lampID;
	QStatus status = m_LampDetailsBusObject->GetLampID(lampID);
	ASSERT_EQ(ER_OK, status) << "Retrieving LampID property from LampDetails interface returned error: "
		<< QCC_StatusText(status);
}

TEST_F(LSFLampTestSuite, LSF_Lamp_v1_26)
{
	LOG(INFO) << "Retrieving HasEffects property from LampDetails interface";
	bool hasEffects;
	QStatus status = m_LampDetailsBusObject->GetHasEffects(hasEffects);
	ASSERT_EQ(ER_OK, status) << "Retrieving HasEffects property from LampDetails interface returned error: "
		<< QCC_StatusText(status);
}

TEST_F(LSFLampTestSuite, LSF_Lamp_v1_27)
{
	LOG(INFO) << "Retrieving MinVoltage property from LampDetails interface";
	uint32_t minVoltage;
	QStatus status = m_LampDetailsBusObject->GetMinVoltage(minVoltage);
	ASSERT_EQ(ER_OK, status) << "Retrieving MinVoltage property from LampDetails interface returned error: "
		<< QCC_StatusText(status);
}

TEST_F(LSFLampTestSuite, LSF_Lamp_v1_28)
{
	LOG(INFO) << "Retrieving MaxVoltage property from LampDetails interface";
	uint32_t maxVoltage;
	QStatus status = m_LampDetailsBusObject->GetMaxVoltage(maxVoltage);
	ASSERT_EQ(ER_OK, status) << "Retrieving MaxVoltage property from LampDetails interface returned error: "
		<< QCC_StatusText(status);
}

TEST_F(LSFLampTestSuite, LSF_Lamp_v1_29)
{
	LOG(INFO) << "Retrieving Wattage property from LampDetails interface";
	uint32_t wattage;
	QStatus status = m_LampDetailsBusObject->GetWattage(wattage);
	ASSERT_EQ(ER_OK, status) << "Retrieving Wattage property from LampDetails interface returned error: "
		<< QCC_StatusText(status);
}

TEST_F(LSFLampTestSuite, LSF_Lamp_v1_30)
{
	LOG(INFO) << "Retrieving IncandescentEquivalent property from LampDetails interface";
	uint32_t incandescentEquivalent;
	QStatus status = m_LampDetailsBusObject->GetIncandescentEquivalent(incandescentEquivalent);
	ASSERT_EQ(ER_OK, status) << "Retrieving IncandescentEquivalent property from LampDetails interface returned error: "
		<< QCC_StatusText(status);
}

TEST_F(LSFLampTestSuite, LSF_Lamp_v1_31)
{
	LOG(INFO) << "Retrieving MaxLumens property from LampDetails interface";
	uint32_t maxLumens;
	QStatus status = m_LampDetailsBusObject->GetMaxLumens(maxLumens);
	ASSERT_EQ(ER_OK, status) << "Retrieving MaxLumens property from LampDetails interface returned error: "
		<< QCC_StatusText(status);
}

TEST_F(LSFLampTestSuite, LSF_Lamp_v1_32)
{
	LOG(INFO) << "Retrieving MinTemperature property from LampDetails interface";
	uint32_t minTemperature;
	QStatus status = m_LampDetailsBusObject->GetMinTemperature(minTemperature);
	ASSERT_EQ(ER_OK, status) << "Retrieving MinTemperature property from LampDetails interface returned error: "
		<< QCC_StatusText(status);
}

TEST_F(LSFLampTestSuite, LSF_Lamp_v1_33)
{
	LOG(INFO) << "Retrieving MaxTemperature property from LampDetails interface";
	uint32_t maxTemperature;
	QStatus status = m_LampDetailsBusObject->GetMaxTemperature(maxTemperature);
	ASSERT_EQ(ER_OK, status) << "Retrieving MaxTemperature property from LampDetails interface returned error: "
		<< QCC_StatusText(status);
}

TEST_F(LSFLampTestSuite, LSF_Lamp_v1_34)
{
	LOG(INFO) << "Retrieving ColorRenderingIndex property from LampDetails interface";
	uint32_t colorRenderingIndex;
	QStatus status = m_LampDetailsBusObject->GetColorRenderingIndex(colorRenderingIndex);
	ASSERT_EQ(ER_OK, status) << "Retrieving ColorRenderingIndex property from LampDetails interface returned error: "
		<< QCC_StatusText(status);
}