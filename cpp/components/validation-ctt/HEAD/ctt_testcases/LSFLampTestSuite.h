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
	static AJ_PCSTR BUS_APPLICATION_NAME;
	static AJ_PCSTR BUS_OBJECT_PATH;

	static AJ_PCSTR LAMPSERVICE_INTERFACE_NAME;
	static AJ_PCSTR LAMPSTATE_INTERFACE_NAME;
	static AJ_PCSTR LAMPDETAILS_INTERFACE_NAME;
	static AJ_PCSTR LAMPPARAMETERS_INTERFACE_NAME;

	static AJ_PCSTR LAMP_STATE_FIELD_ON_OFF;
	static AJ_PCSTR LAMP_STATE_FIELD_BRIGHTNESS;
	static AJ_PCSTR LAMP_STATE_FIELD_HUE;
	static AJ_PCSTR LAMP_STATE_FIELD_SATURATION;
	static AJ_PCSTR LAMP_STATE_FIELD_COLOR_TEMP;

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