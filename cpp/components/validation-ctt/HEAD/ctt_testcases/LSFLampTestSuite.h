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

#include "InterfaceValidator.h"
#include "IOManager.h"
#include "LampDetailsBusObject.h"
#include "LampParametersBusObject.h"
#include "LampServiceBusObject.h"
#include "LampStateBusObject.h"
#include "ServiceHelper.h"

#include <alljoyn\AboutProxy.h>

class LSFLampTestSuite : public ::testing::Test, public IOManager
{
public:
	LSFLampTestSuite();
	void SetUp();
	void TearDown();

protected:
	static const char* BUS_APPLICATION_NAME;
	static const char* BUS_OBJECT_PATH;

	static const char* LAMPSERVICE_INTERFACE_NAME;
	static const char* LAMPSTATE_INTERFACE_NAME;
	static const char* LAMPDETAILS_INTERFACE_NAME;
	static const char* LAMPPARAMETERS_INTERFACE_NAME;

	static const char* LAMP_STATE_FIELD_ON_OFF;
	static const char* LAMP_STATE_FIELD_BRIGHTNESS;
	static const char* LAMP_STATE_FIELD_HUE;
	static const char* LAMP_STATE_FIELD_SATURATION;
	static const char* LAMP_STATE_FIELD_COLOR_TEMP;

	std::string m_DutDeviceId = std::string{ "" };
	uint8_t* m_DutAppId{ nullptr };
	std::string m_LampObjectPath = std::string{ "" };
	ServiceHelper* m_ServiceHelper{ nullptr };
	AboutAnnouncementDetails* m_DeviceAboutAnnouncement{ nullptr };
	ajn::AboutProxy* m_AboutProxy{ nullptr };
	XMLBasedBusIntrospector* m_BusIntrospector{ nullptr };
	InterfaceValidator* m_InterfaceValidator{ nullptr };
	LampServiceBusObject* m_LampServiceBusObject{ nullptr };
	LampParametersBusObject* m_LampParametersBusObject{ nullptr };
	LampDetailsBusObject* m_LampDetailsBusObject{ nullptr };
	LampStateBusObject* m_LampStateBusObject{ nullptr };

	QStatus initServiceHelper();
	void initProxyBusObjects();
	void releaseResources();
	//void waitForSessionToClose();

	// LSF_Lamp-v1-07
	bool getVariableColorTemp();

	// LSF_Lamp-v1-08
	bool getDimmable();

	// LSF_Lamp-v1-10
	ajn::MsgArg* getLampStateMsgArg(bool, uint32_t, uint32_t, uint32_t, uint32_t);

	// LSF_Lamp-v1-11
	void checkForUnknownInterfacesFromAboutAnnouncement();
	InterfaceValidator* getInterfaceValidator();
};