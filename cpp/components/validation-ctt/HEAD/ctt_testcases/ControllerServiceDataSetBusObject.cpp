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
#include "stdafx.h"
#include "ControllerServiceDataSetBusObject.h"

#define CHECK_BREAK(x) if ((status = x) != ER_OK) { break; }
#define CHECK_RETURN(x) if ((status = x) != ER_OK) { return status; }

static const char* CONTROLLERSERVICE_OBJECT_PATH = "/org/allseen/LSF/ControllerService";
static const char* DATASET_INTERFACE_NAME = "org.allseen.LSF.ControllerService.DataSet";

ControllerServiceDataSetBusObject::ControllerServiceDataSetBusObject(ajn::BusAttachment& t_BusAttachment, const std::string& t_BusName, const ajn::SessionId t_SessionId) :
m_BusAttachment(&t_BusAttachment), m_BusName(t_BusName), m_SessionId(t_SessionId)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* getIface = NULL;
	getIface = m_BusAttachment->GetInterface(DATASET_INTERFACE_NAME);
	if (!getIface) {
		ajn::InterfaceDescription* createIface = NULL;
		status = m_BusAttachment->CreateInterface(DATASET_INTERFACE_NAME, createIface, ajn::AJ_IFC_SECURITY_OFF);
		if (createIface && status == ER_OK) {
			do {
				CHECK_BREAK(createIface->AddMethod("GetLampDataSet", "ss", "usssa{sv}a{sv}a{sv}", "lampID,language,responseCode,lampID,language,lampName,lampDetails,lampState,lampParameters"))

				CHECK_BREAK(createIface->AddProperty("Version", "u", ajn::PROP_ACCESS_READ))
				createIface->Activate();
				return;
			} while (0);
		} //if (createIface)
	} //if (!getIface)
}

QStatus ControllerServiceDataSetBusObject::GetVersion(uint32_t& version)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* p_InterfaceDescription = m_BusAttachment->GetInterface(DATASET_INTERFACE_NAME);
	if (!p_InterfaceDescription) {
		return ER_FAIL;
	}

	ajn::ProxyBusObject* proxyBusObj = new ajn::ProxyBusObject(*m_BusAttachment, m_BusName.c_str(), CONTROLLERSERVICE_OBJECT_PATH, m_SessionId);
	if (!proxyBusObj) {
		return ER_FAIL;
	}
	do {
		ajn::MsgArg arg;
		CHECK_BREAK(proxyBusObj->AddInterface(*p_InterfaceDescription))
		CHECK_BREAK(proxyBusObj->GetProperty(DATASET_INTERFACE_NAME, "Version", arg))
		version = arg.v_variant.val->v_uint32;
	} while (0);
	delete proxyBusObj;
	return status;
}

QStatus ControllerServiceDataSetBusObject::GetLampDataSet(const char* t_LampID, const char* t_Language, uint32_t& t_ResponseCode,
	qcc::String& t_RetrievedLampID, qcc::String& t_RetrievedLanguage, qcc::String& t_LampName, std::vector<ajn::MsgArg>& t_LampDetails, std::vector<ajn::MsgArg>& t_LampState, std::vector<ajn::MsgArg>& t_LampParameters)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* p_InterfaceDescription = m_BusAttachment->GetInterface(DATASET_INTERFACE_NAME);
	if (!p_InterfaceDescription) {
		return ER_FAIL;
	}

	ajn::ProxyBusObject* proxyBusObj = new ajn::ProxyBusObject(*m_BusAttachment, m_BusName.c_str(), CONTROLLERSERVICE_OBJECT_PATH, m_SessionId);
	if (!proxyBusObj) {
		return ER_FAIL;
	}
	status = proxyBusObj->AddInterface(*p_InterfaceDescription);
	if (status == ER_OK) {
		ajn::MsgArg* msgArg = new ajn::MsgArg[2];
		msgArg[0].Set("s", t_LampID);
		msgArg[1].Set("s", t_Language);
		ajn::Message responseMessage(*m_BusAttachment);
		CHECK_RETURN(proxyBusObj->MethodCall(DATASET_INTERFACE_NAME, "GetLampDataSet", msgArg, 2, responseMessage))
		CHECK_RETURN(responseMessage->GetArg(0)->Get("u", &t_ResponseCode))

		char* tempRetrievedLampID;
		CHECK_RETURN(responseMessage->GetArg(1)->Get("s", &tempRetrievedLampID));
		t_RetrievedLampID = qcc::String(tempRetrievedLampID);

		char* tempRetrievedLanguage;
		CHECK_RETURN(responseMessage->GetArg(2)->Get("s", &tempRetrievedLanguage));
		t_RetrievedLanguage = qcc::String(tempRetrievedLanguage);

		char* tempLampName;
		CHECK_RETURN(responseMessage->GetArg(3)->Get("s", &tempLampName));
		t_LampName = qcc::String(tempLampName);

		uint32_t tempLampDetailsSize;
		ajn::MsgArg* tempLampDetails;
		CHECK_RETURN(responseMessage->GetArg(4)->Get("a{sv}", &tempLampDetailsSize, &tempLampDetails))

		t_LampDetails.clear();
		for (size_t i = 0; i < tempLampDetailsSize; ++i)
		{
			t_LampDetails.push_back(tempLampDetails[i]);
		}

		uint32_t tempLampStateSize;
		ajn::MsgArg* tempLampState;
		CHECK_RETURN(responseMessage->GetArg(5)->Get("a{sv}", &tempLampStateSize, &tempLampState))

		t_LampState.clear();
		for (size_t i = 0; i < tempLampStateSize; ++i)
		{
			t_LampState.push_back(tempLampState[i]);
		}

		uint32_t tempLampParametersSize;
		ajn::MsgArg* tempLampParameters;
		CHECK_RETURN(responseMessage->GetArg(6)->Get("a{sv}", &tempLampParametersSize, &tempLampParameters))

		t_LampParameters.clear();
		for (size_t i = 0; i < tempLampParametersSize; ++i)
		{
			t_LampParameters.push_back(tempLampParameters[i]);
		}
	}
	delete proxyBusObj;
	return status;
}