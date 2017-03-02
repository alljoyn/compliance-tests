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
#include "ControllerServiceLampBusObject.h"

#define CHECK_BREAK(x) if ((status = x) != ER_OK) { break; }
#define CHECK_RETURN(x) if ((status = x) != ER_OK) { return status; }

static const char* CONTROLLERSERVICE_OBJECT_PATH = "/org/allseen/LSF/ControllerService";
static const char* LAMP_INTERFACE_NAME = "org.allseen.LSF.ControllerService.Lamp";

ControllerServiceLampBusObject::ControllerServiceLampBusObject(ajn::BusAttachment& t_BusAttachment, const std::string& t_BusName, const ajn::SessionId m_SessionId) :
m_BusAttachment(&t_BusAttachment), m_BusName(t_BusName), m_SessionId(m_SessionId),
m_LampNameChangedSignalReceived(false), m_LampStateChangedSignalReceived(false),
m_LampsFoundSignalReceived(false), m_LampsLostSignalReceived(false)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* getIface = NULL;
	getIface = m_BusAttachment->GetInterface(LAMP_INTERFACE_NAME);
	if (!getIface) {
		ajn::InterfaceDescription* createIface = NULL;
		status = m_BusAttachment->CreateInterface(LAMP_INTERFACE_NAME, createIface, ajn::AJ_IFC_SECURITY_OFF);
		if (createIface && status == ER_OK) {
			do {
				CHECK_BREAK(createIface->AddMethod("GetAllLampIDs", NULL, "uas", "responseCode,lampIDs"))
				CHECK_BREAK(createIface->AddMethod("GetLampSupportedLanguages", "s", "usas", "lampID,responseCode,lampID,supportedLanguages"))
				CHECK_BREAK(createIface->AddMethod("GetLampManufacturer", "ss", "usss", "lampID,language,responseCode,lampID,language,manufacturer"))
				CHECK_BREAK(createIface->AddMethod("GetLampName", "ss", "usss", "lampID,language,responseCode,lampID,language,lampName"))
				CHECK_BREAK(createIface->AddMethod("SetLampName", "sss", "uss", "lampID,lampName,language,responseCode,lampID,language"))
				CHECK_BREAK(createIface->AddMethod("GetLampDetails", "s", "usa{sv}", "lampID,responseCode,lampID,lampDetails"))
				CHECK_BREAK(createIface->AddMethod("GetLampParameters", "s", "usa{sv}", "lampID,responseCode,lampID,lampParameters"))
				CHECK_BREAK(createIface->AddMethod("GetLampParametersField", "ss", "ussv", "lampID,lampParameterFieldName,responseCode,lampID,lampParameterFieldName,lampParameterFieldValue"))
				CHECK_BREAK(createIface->AddMethod("GetLampState", "s", "usa{sv}", "lampID,responseCode,lampID,lampState"))
				CHECK_BREAK(createIface->AddMethod("GetLampStateField", "ss", "ussv", "lampID,lampStateFieldName,responseCode,lampID,lampStateFieldName,lampStateFieldValue"))
				CHECK_BREAK(createIface->AddMethod("TransitionLampState", "sa{sv}u", "us", "lampID,lampState,transitionPeriod,responseCode,lampID"))
				CHECK_BREAK(createIface->AddMethod("PulseLampWithState", "sa{sv}a{sv}uuu", "us", "lampID,fromLampState,toLampState,period,duration,numPulses,responseCode,lampID"))
				CHECK_BREAK(createIface->AddMethod("PulseLampWithPreset", "sssuuu", "us", "lampID,fromPresetID,toPresetID,period,duration,numPulses,responseCode,lampID"))
				CHECK_BREAK(createIface->AddMethod("TransitionLampStateToPreset", "ssu", "us", "lampID,presetID,transitionPeriod,responseCode,lampID"))
				CHECK_BREAK(createIface->AddMethod("TransitionLampStateField", "ssvu", "uss", "lampID,lampStateFieldName,lampStateFieldValue,transitionPeriod,responseCode,lampID,lampStateFieldName"))
				CHECK_BREAK(createIface->AddMethod("ResetLampState", "s", "us", "lampID,responseCode,lampID"))
				CHECK_BREAK(createIface->AddMethod("ResetLampStateField", "ss", "uss", "lampID,lampStateFieldName,responseCode,lampID,lampStateFieldName"))
				CHECK_BREAK(createIface->AddMethod("GetLampFaults", "s", "usau", "lampID,responseCode,lampID,lampFaults"))
				CHECK_BREAK(createIface->AddMethod("ClearLampFault", "su", "usu", "lampID,lampFault,responseCode,lampID,lampFault"))
				CHECK_BREAK(createIface->AddMethod("GetLampServiceVersion", "s", "usu", "lampID,responseCode,lampID,lampServiceVersion"))
				CHECK_BREAK(createIface->AddProperty("Version", "u", ajn::PROP_ACCESS_READ))
				CHECK_BREAK(createIface->AddSignal("LampNameChanged", "ss", "lampID,lampName"))
				CHECK_BREAK(createIface->AddSignal("LampStateChanged", "ss", "lampID,lampName"))
				CHECK_BREAK(createIface->AddSignal("LampsFound", "s", "lampID"))
				CHECK_BREAK(createIface->AddSignal("LampsLost", "as", "lampIDs"))
				createIface->Activate();

				const ajn::InterfaceDescription* uniqueId = m_BusAttachment->GetInterface(LAMP_INTERFACE_NAME);
				const ajn::InterfaceDescription::Member* sig = uniqueId->GetMember("LampNameChanged");
				m_BusAttachment->RegisterSignalHandler(this, static_cast<ajn::MessageReceiver::SignalHandler>(&ControllerServiceLampBusObject::LampNameChangedSignalHandler), sig, CONTROLLERSERVICE_OBJECT_PATH);

				sig = uniqueId->GetMember("LampStateChanged");
				m_BusAttachment->RegisterSignalHandler(this, static_cast<ajn::MessageReceiver::SignalHandler>(&ControllerServiceLampBusObject::LampStateChangedSignalHandler), sig, CONTROLLERSERVICE_OBJECT_PATH);

				sig = uniqueId->GetMember("LampsFound");
				m_BusAttachment->RegisterSignalHandler(this, static_cast<ajn::MessageReceiver::SignalHandler>(&ControllerServiceLampBusObject::LampsFoundSignalHandler), sig, CONTROLLERSERVICE_OBJECT_PATH);

				sig = uniqueId->GetMember("LampsLost");
				m_BusAttachment->RegisterSignalHandler(this, static_cast<ajn::MessageReceiver::SignalHandler>(&ControllerServiceLampBusObject::LampsLostSignalHandler), sig, CONTROLLERSERVICE_OBJECT_PATH);

				return;
			} while (0);
		} //if (createIface)
	} //if (!getIface)
}

void ControllerServiceLampBusObject::LampNameChangedSignalHandler(const ajn::InterfaceDescription::Member* member, const char* sourcePath, ajn::Message& msg)
{
	LOG(INFO) << "LampNameChanged signal received";
	m_LampNameChangedSignalReceived = true;
}

bool ControllerServiceLampBusObject::DidLampNameChanged()
{
	return m_LampNameChangedSignalReceived;
}

void ControllerServiceLampBusObject::LampStateChangedSignalHandler(const ajn::InterfaceDescription::Member* member, const char* sourcePath, ajn::Message& msg)
{
	LOG(INFO) << "LampStateChanged signal received";
	m_LampStateChangedSignalReceived = true;
}

bool ControllerServiceLampBusObject::DidLampStateChanged()
{
	return m_LampStateChangedSignalReceived;
}

void ControllerServiceLampBusObject::LampsFoundSignalHandler(const ajn::InterfaceDescription::Member* member, const char* sourcePath, ajn::Message& msg)
{
	LOG(INFO) << "LampsFound signal received";
	m_LampsFoundSignalReceived = true;
}

bool ControllerServiceLampBusObject::DidLampsFound()
{
	return m_LampsFoundSignalReceived;
}

void ControllerServiceLampBusObject::LampsLostSignalHandler(const ajn::InterfaceDescription::Member* member, const char* sourcePath, ajn::Message& msg)
{
	LOG(INFO) << "LampsLost signal received";
	m_LampsLostSignalReceived = true;
}

bool ControllerServiceLampBusObject::DidLampsLost()
{
	return m_LampsLostSignalReceived;
}

void ControllerServiceLampBusObject::ResetSignals()
{
	m_LampNameChangedSignalReceived = false;
	m_LampStateChangedSignalReceived = false;
}

QStatus ControllerServiceLampBusObject::GetVersion(uint32_t& t_Version)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* p_InterfaceDescription = m_BusAttachment->GetInterface(LAMP_INTERFACE_NAME);
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
		CHECK_BREAK(proxyBusObj->GetProperty(LAMP_INTERFACE_NAME, "Version", arg))
		t_Version = arg.v_variant.val->v_uint32;
	} while (0);
	delete proxyBusObj;
	return status;
}

QStatus ControllerServiceLampBusObject::GetAllLampIDs(uint32_t& t_ResponseCode,
	size_t& t_NumberOfLamps, std::vector<qcc::String>& t_LampIDs)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* p_InterfaceDescription = m_BusAttachment->GetInterface(LAMP_INTERFACE_NAME);
	if (!p_InterfaceDescription) {
		return ER_FAIL;
	}

	ajn::ProxyBusObject* proxyBusObj = new ajn::ProxyBusObject(*m_BusAttachment, m_BusName.c_str(), CONTROLLERSERVICE_OBJECT_PATH, m_SessionId);
	if (!proxyBusObj) {
		return ER_FAIL;
	}
	status = proxyBusObj->AddInterface(*p_InterfaceDescription);
	if (status == ER_OK) {
		ajn::Message responseMessage(*m_BusAttachment);
		CHECK_RETURN(proxyBusObj->MethodCall(LAMP_INTERFACE_NAME, "GetAllLampIDs", NULL, 0, responseMessage))
		CHECK_RETURN(responseMessage->GetArg(0)->Get("u", &t_ResponseCode))

		ajn::MsgArg* tempIDsArray;
		CHECK_RETURN(responseMessage->GetArg(1)->Get("as", &t_NumberOfLamps, &tempIDsArray))

		for (size_t i = 0; i < t_NumberOfLamps; ++i)
		{
			char* lampID;
			CHECK_RETURN(tempIDsArray[i].Get("s", &lampID))

			t_LampIDs.push_back(lampID);
		}
	}
	delete proxyBusObj;
	return status;
}

QStatus ControllerServiceLampBusObject::GetLampSupportedLanguages(const char* t_LampID,
	uint32_t& t_ResponseCode, qcc::String& t_RetrievedLampID, std::vector<qcc::String>& t_Languages)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* p_InterfaceDescription = m_BusAttachment->GetInterface(LAMP_INTERFACE_NAME);
	if (!p_InterfaceDescription) {
		return ER_FAIL;
	}

	ajn::ProxyBusObject* proxyBusObj = new ajn::ProxyBusObject(*m_BusAttachment, m_BusName.c_str(), CONTROLLERSERVICE_OBJECT_PATH, m_SessionId);
	if (!proxyBusObj) {
		return ER_FAIL;
	}
	status = proxyBusObj->AddInterface(*p_InterfaceDescription);
	if (status == ER_OK) {
		ajn::Message responseMessage(*m_BusAttachment);
		CHECK_RETURN(proxyBusObj->MethodCall(LAMP_INTERFACE_NAME, "GetLampSupportedLanguages", new ajn::MsgArg("s", t_LampID), 1, responseMessage))
		CHECK_RETURN(responseMessage->GetArg(0)->Get("u", &t_ResponseCode))

		char* tempLampId;
		CHECK_RETURN(responseMessage->GetArg(1)->Get("s", &tempLampId))
		t_RetrievedLampID = qcc::String(tempLampId);

		uint32_t* tempLanguagesSize = new uint32_t();
		ajn::MsgArg* tempLanguagesArray;
		CHECK_RETURN(responseMessage->GetArg(2)->Get("as", tempLanguagesSize, &tempLanguagesArray))

		for (size_t i = 0; i < *tempLanguagesSize; ++i)
		{
			char* language;
			CHECK_RETURN(tempLanguagesArray[i].Get("s", &language))

			t_Languages.push_back(language);
		}
	}
	delete proxyBusObj;
	return status;
}

QStatus ControllerServiceLampBusObject::GetLampManufacturer(const char* t_LampID,
	const char* t_Language, uint32_t& t_ResponseCode, qcc::String& t_RetrievedLampID, qcc::String& t_RetrievedLanguage, qcc::String& t_Manufacturer)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* p_InterfaceDescription = m_BusAttachment->GetInterface(LAMP_INTERFACE_NAME);
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
		CHECK_RETURN(proxyBusObj->MethodCall(LAMP_INTERFACE_NAME, "GetLampManufacturer", msgArg, 2, responseMessage))
		CHECK_RETURN(responseMessage->GetArg(0)->Get("u", &t_ResponseCode))
		
		char* tempRetrievedLampID;
		CHECK_RETURN(responseMessage->GetArg(1)->Get("s", &tempRetrievedLampID))
		t_RetrievedLampID = qcc::String(tempRetrievedLampID);

		char* tempRetrievedLanguage;
		CHECK_RETURN(responseMessage->GetArg(2)->Get("s", &tempRetrievedLanguage))
		t_RetrievedLanguage = qcc::String(tempRetrievedLanguage);

		char* tempManufacturer;
		CHECK_RETURN(responseMessage->GetArg(3)->Get("s", &tempManufacturer))
		t_Manufacturer = qcc::String(tempManufacturer);
	}
	delete proxyBusObj;
	return status;
}

QStatus ControllerServiceLampBusObject::GetLampName(const char* t_LampID,
	const char* t_Language, uint32_t& t_ResponseCode, qcc::String& t_RetrievedLampID, qcc::String& t_RetrievedLanguage, qcc::String& t_LampName)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* p_InterfaceDescription = m_BusAttachment->GetInterface(LAMP_INTERFACE_NAME);
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
		CHECK_RETURN(proxyBusObj->MethodCall(LAMP_INTERFACE_NAME, "GetLampName", msgArg, 2, responseMessage))
		CHECK_RETURN(responseMessage->GetArg(0)->Get("u", &t_ResponseCode))

		char* tempRetrievedLampID;
		CHECK_RETURN(responseMessage->GetArg(1)->Get("s", &tempRetrievedLampID))
		t_RetrievedLampID = qcc::String(tempRetrievedLampID);

		char* tempRetrievedLanguage;
		CHECK_RETURN(responseMessage->GetArg(2)->Get("s", &tempRetrievedLanguage))
		t_RetrievedLanguage = qcc::String(tempRetrievedLanguage);

		char* tempLampName;
		CHECK_RETURN(responseMessage->GetArg(3)->Get("s", &tempLampName))
		t_LampName = qcc::String(tempLampName);
	}
	delete proxyBusObj;
	return status;
}

QStatus ControllerServiceLampBusObject::SetLampName(const char* t_LampID, const char* t_LampName,
	const char* t_Language, uint32_t& t_ResponseCode, qcc::String& t_RetrievedLampID, qcc::String& t_RetrievedLanguage)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* p_InterfaceDescription = m_BusAttachment->GetInterface(LAMP_INTERFACE_NAME);
	if (!p_InterfaceDescription) {
		return ER_FAIL;
	}

	ajn::ProxyBusObject* proxyBusObj = new ajn::ProxyBusObject(*m_BusAttachment, m_BusName.c_str(), CONTROLLERSERVICE_OBJECT_PATH, m_SessionId);
	if (!proxyBusObj) {
		return ER_FAIL;
	}
	status = proxyBusObj->AddInterface(*p_InterfaceDescription);
	if (status == ER_OK) {
		ajn::MsgArg* msgArg = new ajn::MsgArg[3];
		msgArg[0].Set("s", t_LampID);
		msgArg[1].Set("s", t_LampName);
		msgArg[2].Set("s", t_Language);
		ajn::Message responseMessage(*m_BusAttachment);
		CHECK_RETURN(proxyBusObj->MethodCall(LAMP_INTERFACE_NAME, "SetLampName", msgArg, 3, responseMessage))
		CHECK_RETURN(responseMessage->GetArg(0)->Get("u", &t_ResponseCode))

		char* tempRetrievedLampID;
		CHECK_RETURN(responseMessage->GetArg(1)->Get("s", &tempRetrievedLampID))
		t_RetrievedLampID = qcc::String(tempRetrievedLampID);

		char* tempRetrievedLanguage;
		CHECK_RETURN(responseMessage->GetArg(2)->Get("s", &tempRetrievedLanguage))
		t_RetrievedLanguage = qcc::String(tempRetrievedLanguage);
	}
	delete proxyBusObj;
	return status;
}

QStatus ControllerServiceLampBusObject::GetLampDetails(const char* t_LampID, uint32_t& t_ResponseCode, qcc::String& t_RetrievedLampID, std::vector<ajn::MsgArg>& t_LampDetails)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* p_InterfaceDescription = m_BusAttachment->GetInterface(LAMP_INTERFACE_NAME);
	if (!p_InterfaceDescription) {
		return ER_FAIL;
	}

	ajn::ProxyBusObject* proxyBusObj = new ajn::ProxyBusObject(*m_BusAttachment, m_BusName.c_str(), CONTROLLERSERVICE_OBJECT_PATH, m_SessionId);
	if (!proxyBusObj) {
		return ER_FAIL;
	}
	status = proxyBusObj->AddInterface(*p_InterfaceDescription);
	if (status == ER_OK) {
		ajn::MsgArg* msgArg = new ajn::MsgArg[1];
		msgArg[0].Set("s", t_LampID);
		ajn::Message responseMessage(*m_BusAttachment);
		CHECK_RETURN(proxyBusObj->MethodCall(LAMP_INTERFACE_NAME, "GetLampDetails", msgArg, 1, responseMessage))
		CHECK_RETURN(responseMessage->GetArg(0)->Get("u", &t_ResponseCode))

		char* tempRetrievedLampID;
		CHECK_RETURN(responseMessage->GetArg(1)->Get("s", &tempRetrievedLampID))
		t_RetrievedLampID = qcc::String(tempRetrievedLampID);

		size_t tempLampDetailsSize;
		ajn::MsgArg* tempLampDetails;
		CHECK_RETURN(responseMessage->GetArg(2)->Get("a{sv}", &tempLampDetailsSize, &tempLampDetails))
		for (size_t i = 0; i < tempLampDetailsSize; ++i)
		{
			t_LampDetails.push_back(tempLampDetails[i]);
		}
	}
	delete proxyBusObj;
	return status;
}

QStatus ControllerServiceLampBusObject::GetLampParameters(const char* t_LampID, uint32_t& t_ResponseCode, qcc::String& t_RetrievedLampID, std::vector<ajn::MsgArg>& t_LampParameters)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* p_InterfaceDescription = m_BusAttachment->GetInterface(LAMP_INTERFACE_NAME);
	if (!p_InterfaceDescription) {
		return ER_FAIL;
	}

	ajn::ProxyBusObject* proxyBusObj = new ajn::ProxyBusObject(*m_BusAttachment, m_BusName.c_str(), CONTROLLERSERVICE_OBJECT_PATH, m_SessionId);
	if (!proxyBusObj) {
		return ER_FAIL;
	}
	status = proxyBusObj->AddInterface(*p_InterfaceDescription);
	if (status == ER_OK) {
		ajn::MsgArg* msgArg = new ajn::MsgArg[1];
		msgArg[0].Set("s", t_LampID);
		ajn::Message responseMessage(*m_BusAttachment);
		CHECK_RETURN(proxyBusObj->MethodCall(LAMP_INTERFACE_NAME, "GetLampParameters", msgArg, 1, responseMessage))
		CHECK_RETURN(responseMessage->GetArg(0)->Get("u", &t_ResponseCode))

		char* tempRetrievedLampID;
		CHECK_RETURN(responseMessage->GetArg(1)->Get("s", &tempRetrievedLampID))
		t_RetrievedLampID = qcc::String(tempRetrievedLampID);

		size_t tempLampParametersSize;
		ajn::MsgArg* tempLampParameters;
		CHECK_RETURN(responseMessage->GetArg(2)->Get("a{sv}", &tempLampParametersSize, &tempLampParameters))
		for (size_t i = 0; i < tempLampParametersSize; ++i)
		{
			t_LampParameters.push_back(tempLampParameters[i]);
		}
	}
	delete proxyBusObj;
	return status;
}

QStatus ControllerServiceLampBusObject::GetLampParametersField(const char* t_LampID, const char* t_FieldName, uint32_t& t_ResponseCode, qcc::String& t_RetrievedLampID, qcc::String& t_RetrievedFieldName, qcc::String& t_FieldValue)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* p_InterfaceDescription = m_BusAttachment->GetInterface(LAMP_INTERFACE_NAME);
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
		msgArg[1].Set("s", t_FieldName);
		ajn::Message responseMessage(*m_BusAttachment);
		CHECK_RETURN(proxyBusObj->MethodCall(LAMP_INTERFACE_NAME, "GetLampParametersField", msgArg, 2, responseMessage))
			CHECK_RETURN(responseMessage->GetArg(0)->Get("u", &t_ResponseCode))

			char* tempRetrievedLampID;
		CHECK_RETURN(responseMessage->GetArg(1)->Get("s", &tempRetrievedLampID))
			t_RetrievedLampID = qcc::String(tempRetrievedLampID);

		char* tempRetrievedFieldName;
		CHECK_RETURN(responseMessage->GetArg(2)->Get("s", &tempRetrievedFieldName))
			t_RetrievedFieldName = qcc::String(tempRetrievedFieldName);

		std::string tempFieldValue;
		CHECK_RETURN(getParamValue(*responseMessage->GetArg(3), tempFieldValue))
			t_FieldValue = qcc::String(tempFieldValue.c_str());
	}
	delete proxyBusObj;
	return status;
}

QStatus ControllerServiceLampBusObject::getParamValue(const ajn::MsgArg& t_Param, std::string& t_Value)
{
	QStatus status = ER_OK;
	uint32_t uValue;
	if ((status = t_Param.Get("u", &uValue)) == ER_BUS_SIGNATURE_MISMATCH)
	{
		bool bValue;
		if ((status = t_Param.Get("b", &bValue)) == ER_BUS_SIGNATURE_MISMATCH)
		{
			char* cValue;
			status = t_Param.Get("s", &cValue);
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

	return status;
}

QStatus ControllerServiceLampBusObject::GetLampState(const char* t_LampID, uint32_t& t_ResponseCode, qcc::String& t_RetrievedLampID, std::vector<ajn::MsgArg>& t_LampStates)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* p_InterfaceDescription = m_BusAttachment->GetInterface(LAMP_INTERFACE_NAME);
	if (!p_InterfaceDescription) {
		return ER_FAIL;
	}

	ajn::ProxyBusObject* proxyBusObj = new ajn::ProxyBusObject(*m_BusAttachment, m_BusName.c_str(), CONTROLLERSERVICE_OBJECT_PATH, m_SessionId);
	if (!proxyBusObj) {
		return ER_FAIL;
	}
	status = proxyBusObj->AddInterface(*p_InterfaceDescription);
	if (status == ER_OK) {
		ajn::MsgArg* msgArg = new ajn::MsgArg[1];
		msgArg[0].Set("s", t_LampID);
		ajn::Message responseMessage(*m_BusAttachment);
		CHECK_RETURN(proxyBusObj->MethodCall(LAMP_INTERFACE_NAME, "GetLampState", msgArg, 1, responseMessage))
			CHECK_RETURN(responseMessage->GetArg(0)->Get("u", &t_ResponseCode))

		char* tempRetrievedLampID;
		CHECK_RETURN(responseMessage->GetArg(1)->Get("s", &tempRetrievedLampID))
		t_RetrievedLampID = qcc::String(tempRetrievedLampID);

		size_t tempLampStatesSize;
		ajn::MsgArg* tempLampStates;
		CHECK_RETURN(responseMessage->GetArg(2)->Get("a{sv}", &tempLampStatesSize, &tempLampStates))

		t_LampStates.clear();
		for (size_t i = 0; i < tempLampStatesSize; ++i)
		{
			t_LampStates.push_back(tempLampStates[i]);
		}
	}
	delete proxyBusObj;
	return status;
}

QStatus ControllerServiceLampBusObject::GetLampStateField(const char* t_LampID, const char* t_FieldName, uint32_t& t_ResponseCode, qcc::String& t_RetrievedLampID, qcc::String& t_RetrievedFieldName, qcc::String& t_FieldValue)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* p_InterfaceDescription = m_BusAttachment->GetInterface(LAMP_INTERFACE_NAME);
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
		msgArg[1].Set("s", t_FieldName);
		ajn::Message responseMessage(*m_BusAttachment);
		CHECK_RETURN(proxyBusObj->MethodCall(LAMP_INTERFACE_NAME, "GetLampStateField", msgArg, 2, responseMessage))
			CHECK_RETURN(responseMessage->GetArg(0)->Get("u", &t_ResponseCode))

			char* tempRetrievedLampID;
		CHECK_RETURN(responseMessage->GetArg(1)->Get("s", &tempRetrievedLampID))
			t_RetrievedLampID = qcc::String(tempRetrievedLampID);

		char* tempRetrievedFieldName;
		CHECK_RETURN(responseMessage->GetArg(2)->Get("s", &tempRetrievedFieldName))
			t_RetrievedFieldName = qcc::String(tempRetrievedFieldName);

		std::string tempFieldValue;
		CHECK_RETURN(getParamValue(*responseMessage->GetArg(3), tempFieldValue))
			t_FieldValue = qcc::String(tempFieldValue.c_str());
	}
	delete proxyBusObj;
	return status;
}

QStatus ControllerServiceLampBusObject::TransitionLampState(const char* t_LampID, ajn::MsgArg* t_LampState, const uint32_t t_TransitionPeriod, uint32_t& t_ResponseCode, qcc::String& t_RetrievedLampID)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* p_InterfaceDescription = m_BusAttachment->GetInterface(LAMP_INTERFACE_NAME);
	if (!p_InterfaceDescription) {
		return ER_FAIL;
	}

	ajn::ProxyBusObject* proxyBusObj = new ajn::ProxyBusObject(*m_BusAttachment, m_BusName.c_str(), CONTROLLERSERVICE_OBJECT_PATH, m_SessionId);
	if (!proxyBusObj) {
		return ER_FAIL;
	}
	status = proxyBusObj->AddInterface(*p_InterfaceDescription);
	if (status == ER_OK) {
		ajn::MsgArg* msgArg = new ajn::MsgArg[3];
		msgArg[0].Set("s", t_LampID);
		msgArg[1].Set("a{sv}", 5, t_LampState);
		msgArg[2].Set("u", t_TransitionPeriod);
		ajn::Message responseMessage(*m_BusAttachment);
		CHECK_RETURN(proxyBusObj->MethodCall(LAMP_INTERFACE_NAME, "TransitionLampState", msgArg, 3, responseMessage))
		CHECK_RETURN(responseMessage->GetArg(0)->Get("u", &t_ResponseCode))

		char* tempRetrievedLampID;
		CHECK_RETURN(responseMessage->GetArg(1)->Get("s", &tempRetrievedLampID))
		t_RetrievedLampID = qcc::String(tempRetrievedLampID);
	}
	delete proxyBusObj;
	return status;
}

QStatus ControllerServiceLampBusObject::TransitionLampStateField(const char* t_LampID, const char* t_LampStateFieldName, ajn::MsgArg t_LampStateFieldValue, const uint32_t t_TransitionPeriod, uint32_t& t_ResponseCode, qcc::String& t_RetrievedLampID, qcc::String& t_RetrievedLampStateFieldName)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* p_InterfaceDescription = m_BusAttachment->GetInterface(LAMP_INTERFACE_NAME);
	if (!p_InterfaceDescription) {
		return ER_FAIL;
	}

	ajn::ProxyBusObject* proxyBusObj = new ajn::ProxyBusObject(*m_BusAttachment, m_BusName.c_str(), CONTROLLERSERVICE_OBJECT_PATH, m_SessionId);
	if (!proxyBusObj) {
		return ER_FAIL;
	}
	status = proxyBusObj->AddInterface(*p_InterfaceDescription);
	if (status == ER_OK) {
		ajn::MsgArg* msgArg = new ajn::MsgArg[4];
		msgArg[0].Set("s", t_LampID);
		msgArg[1].Set("s", t_LampStateFieldName);
		msgArg[2].Set("v", t_LampStateFieldValue);
		msgArg[3].Set("u", t_TransitionPeriod);
		ajn::Message responseMessage(*m_BusAttachment);
		CHECK_RETURN(proxyBusObj->MethodCall(LAMP_INTERFACE_NAME, "TransitionLampStateField", msgArg, 4, responseMessage))
		CHECK_RETURN(responseMessage->GetArg(0)->Get("u", &t_ResponseCode))

		char* tempRetrievedLampID;
		CHECK_RETURN(responseMessage->GetArg(1)->Get("s", &tempRetrievedLampID))
		t_RetrievedLampID = qcc::String(tempRetrievedLampID);

		char* tempRetrievedLampStateFieldName;
		CHECK_RETURN(responseMessage->GetArg(2)->Get("s", &tempRetrievedLampStateFieldName))
		t_RetrievedLampStateFieldName = qcc::String(tempRetrievedLampStateFieldName);
	}
	delete proxyBusObj;
	return status;
}

QStatus ControllerServiceLampBusObject::PulseLampWithState(const char* t_LampID, ajn::MsgArg* t_FromLampState, ajn::MsgArg* t_ToLampState, const uint32_t t_Period, const uint32_t t_Duration, const uint32_t t_NumPulses, uint32_t& t_ResponseCode, qcc::String& t_RetrievedLampID)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* p_InterfaceDescription = m_BusAttachment->GetInterface(LAMP_INTERFACE_NAME);
	if (!p_InterfaceDescription) {
		return ER_FAIL;
	}

	ajn::ProxyBusObject* proxyBusObj = new ajn::ProxyBusObject(*m_BusAttachment, m_BusName.c_str(), CONTROLLERSERVICE_OBJECT_PATH, m_SessionId);
	if (!proxyBusObj) {
		return ER_FAIL;
	}
	status = proxyBusObj->AddInterface(*p_InterfaceDescription);
	if (status == ER_OK) {
		ajn::MsgArg* msgArg = new ajn::MsgArg[6];
		msgArg[0].Set("s", t_LampID);
		msgArg[1].Set("a{sv}", 5, t_FromLampState);
		msgArg[2].Set("a{sv}", 5, t_ToLampState);
		msgArg[3].Set("u", t_Period);
		msgArg[4].Set("u", t_Duration);
		msgArg[5].Set("u", t_NumPulses);
		ajn::Message responseMessage(*m_BusAttachment);
		CHECK_RETURN(proxyBusObj->MethodCall(LAMP_INTERFACE_NAME, "PulseLampWithState", msgArg, 6, responseMessage))
		CHECK_RETURN(responseMessage->GetArg(0)->Get("u", &t_ResponseCode))

		char* tempRetrievedLampID;
		CHECK_RETURN(responseMessage->GetArg(1)->Get("s", &tempRetrievedLampID))
		t_RetrievedLampID = qcc::String(tempRetrievedLampID);
	}
	delete proxyBusObj;
	return status;
}

QStatus ControllerServiceLampBusObject::PulseLampWithPreset(const char* t_LampID, const char* t_FromPresetID, const char* t_ToPresetID, const uint32_t t_Period, const uint32_t t_Duration, const uint32_t t_NumPulses, uint32_t& t_ResponseCode, qcc::String& t_RetrievedLampID)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* p_InterfaceDescription = m_BusAttachment->GetInterface(LAMP_INTERFACE_NAME);
	if (!p_InterfaceDescription) {
		return ER_FAIL;
	}

	ajn::ProxyBusObject* proxyBusObj = new ajn::ProxyBusObject(*m_BusAttachment, m_BusName.c_str(), CONTROLLERSERVICE_OBJECT_PATH, m_SessionId);
	if (!proxyBusObj) {
		return ER_FAIL;
	}
	status = proxyBusObj->AddInterface(*p_InterfaceDescription);
	if (status == ER_OK) {
		ajn::MsgArg* msgArg = new ajn::MsgArg[6];
		msgArg[0].Set("s", t_LampID);
		msgArg[1].Set("s", t_FromPresetID);
		msgArg[2].Set("s", t_ToPresetID);
		msgArg[3].Set("u", t_Period);
		msgArg[4].Set("u", t_Duration);
		msgArg[5].Set("u", t_NumPulses);
		ajn::Message responseMessage(*m_BusAttachment);
		CHECK_RETURN(proxyBusObj->MethodCall(LAMP_INTERFACE_NAME, "PulseLampWithPreset", msgArg, 6, responseMessage))
		CHECK_RETURN(responseMessage->GetArg(0)->Get("u", &t_ResponseCode))

		char* tempRetrievedLampID;
		CHECK_RETURN(responseMessage->GetArg(1)->Get("s", &tempRetrievedLampID))
		t_RetrievedLampID = qcc::String(tempRetrievedLampID);
	}
	delete proxyBusObj;
	return status;
}

QStatus ControllerServiceLampBusObject::TransitionLampStateToPreset(const char* t_LampID, const char* t_PresetID, const uint32_t t_TransitionPeriod, uint32_t& t_ResponseCode, qcc::String& t_RetrievedLampID)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* p_InterfaceDescription = m_BusAttachment->GetInterface(LAMP_INTERFACE_NAME);
	if (!p_InterfaceDescription) {
		return ER_FAIL;
	}

	ajn::ProxyBusObject* proxyBusObj = new ajn::ProxyBusObject(*m_BusAttachment, m_BusName.c_str(), CONTROLLERSERVICE_OBJECT_PATH, m_SessionId);
	if (!proxyBusObj) {
		return ER_FAIL;
	}
	status = proxyBusObj->AddInterface(*p_InterfaceDescription);
	if (status == ER_OK) {
		ajn::MsgArg* msgArg = new ajn::MsgArg[3];
		msgArg[0].Set("s", t_LampID);
		msgArg[1].Set("s", t_PresetID);
		msgArg[2].Set("u", t_TransitionPeriod);
		ajn::Message responseMessage(*m_BusAttachment);
		CHECK_RETURN(proxyBusObj->MethodCall(LAMP_INTERFACE_NAME, "TransitionLampStateToPreset", msgArg, 3, responseMessage))
		CHECK_RETURN(responseMessage->GetArg(0)->Get("u", &t_ResponseCode))

		char* tempRetrievedLampID;
		CHECK_RETURN(responseMessage->GetArg(1)->Get("s", &tempRetrievedLampID))
		t_RetrievedLampID = qcc::String(tempRetrievedLampID);
	}
	delete proxyBusObj;
	return status;
}

QStatus ControllerServiceLampBusObject::ResetLampState(const char* t_LampID, uint32_t& t_ResponseCode, qcc::String& t_RetrievedLampID)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* p_InterfaceDescription = m_BusAttachment->GetInterface(LAMP_INTERFACE_NAME);
	if (!p_InterfaceDescription) {
		return ER_FAIL;
	}

	ajn::ProxyBusObject* proxyBusObj = new ajn::ProxyBusObject(*m_BusAttachment, m_BusName.c_str(), CONTROLLERSERVICE_OBJECT_PATH, m_SessionId);
	if (!proxyBusObj) {
		return ER_FAIL;
	}
	status = proxyBusObj->AddInterface(*p_InterfaceDescription);
	if (status == ER_OK) {
		ajn::MsgArg* msgArg = new ajn::MsgArg[1];
		msgArg[0].Set("s", t_LampID);
		ajn::Message responseMessage(*m_BusAttachment);
		CHECK_RETURN(proxyBusObj->MethodCall(LAMP_INTERFACE_NAME, "ResetLampState", msgArg, 1, responseMessage))
		CHECK_RETURN(responseMessage->GetArg(0)->Get("u", &t_ResponseCode))

		char* tempRetrievedLampID;
		CHECK_RETURN(responseMessage->GetArg(1)->Get("s", &tempRetrievedLampID))
		t_RetrievedLampID = qcc::String(tempRetrievedLampID);
	}
	delete proxyBusObj;
	return status;
}

QStatus ControllerServiceLampBusObject::ResetLampStateField(const char* t_LampID, const char* t_LampStateFieldName, uint32_t& t_ResponseCode, qcc::String& t_RetrievedLampID, qcc::String& t_RetrievedLampStateFieldName)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* p_InterfaceDescription = m_BusAttachment->GetInterface(LAMP_INTERFACE_NAME);
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
		msgArg[1].Set("s", t_LampStateFieldName);
		ajn::Message responseMessage(*m_BusAttachment);
		CHECK_RETURN(proxyBusObj->MethodCall(LAMP_INTERFACE_NAME, "ResetLampStateField", msgArg, 2, responseMessage))
		CHECK_RETURN(responseMessage->GetArg(0)->Get("u", &t_ResponseCode))

		char* tempRetrievedLampID;
		CHECK_RETURN(responseMessage->GetArg(1)->Get("s", &tempRetrievedLampID))
		t_RetrievedLampID = qcc::String(tempRetrievedLampID);

		char* tempRetrievedLampStateFieldName;
		CHECK_RETURN(responseMessage->GetArg(2)->Get("s", &tempRetrievedLampStateFieldName));
		t_RetrievedLampStateFieldName = qcc::String(tempRetrievedLampStateFieldName);
	}
	delete proxyBusObj;
	return status;
}

QStatus ControllerServiceLampBusObject::GetLampFaults(const char* t_LampID, uint32_t& t_ResponseCode, qcc::String& t_RetrievedLampID, std::vector<uint32_t>& t_LampFaults)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* p_InterfaceDescription = m_BusAttachment->GetInterface(LAMP_INTERFACE_NAME);
	if (!p_InterfaceDescription) {
		return ER_FAIL;
	}

	ajn::ProxyBusObject* proxyBusObj = new ajn::ProxyBusObject(*m_BusAttachment, m_BusName.c_str(), CONTROLLERSERVICE_OBJECT_PATH, m_SessionId);
	if (!proxyBusObj) {
		return ER_FAIL;
	}
	status = proxyBusObj->AddInterface(*p_InterfaceDescription);
	if (status == ER_OK) {
		ajn::MsgArg* msgArg = new ajn::MsgArg[1];
		msgArg[0].Set("s", t_LampID);
		ajn::Message responseMessage(*m_BusAttachment);
		CHECK_RETURN(proxyBusObj->MethodCall(LAMP_INTERFACE_NAME, "GetLampFaults", msgArg, 1, responseMessage))
			CHECK_RETURN(responseMessage->GetArg(0)->Get("u", &t_ResponseCode))

			char* tempRetrievedLampID;
		CHECK_RETURN(responseMessage->GetArg(1)->Get("s", &tempRetrievedLampID))
			t_RetrievedLampID = qcc::String(tempRetrievedLampID);

		size_t tempLampFaultsSize;
		uint32_t* tempLampFaults;
		CHECK_RETURN(responseMessage->GetArg(2)->Get("au", &tempLampFaultsSize, &tempLampFaults));
		
		for (size_t i = 0; i < tempLampFaultsSize; ++i)
		{
			t_LampFaults.push_back(tempLampFaults[i]);
		}
	}
	delete proxyBusObj;
	return status;
}

QStatus ControllerServiceLampBusObject::ClearLampFault(const char* t_LampID, const uint32_t t_LampFault, uint32_t& t_ResponseCode, qcc::String& t_RetrievedLampID, uint32_t& t_RetrievedLampFault)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* p_InterfaceDescription = m_BusAttachment->GetInterface(LAMP_INTERFACE_NAME);
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
		msgArg[1].Set("u", t_LampFault);
		ajn::Message responseMessage(*m_BusAttachment);
		CHECK_RETURN(proxyBusObj->MethodCall(LAMP_INTERFACE_NAME, "ClearLampFault", msgArg, 2, responseMessage))
		CHECK_RETURN(responseMessage->GetArg(0)->Get("u", &t_ResponseCode))

		char* tempRetrievedLampID;
		CHECK_RETURN(responseMessage->GetArg(1)->Get("s", &tempRetrievedLampID))
		t_RetrievedLampID = qcc::String(tempRetrievedLampID);

		CHECK_RETURN(responseMessage->GetArg(2)->Get("u", &t_RetrievedLampFault));
	}
	delete proxyBusObj;
	return status;
}

QStatus ControllerServiceLampBusObject::GetLampServiceVersion(const char* t_LampID, uint32_t& t_ResponseCode, qcc::String& t_RetrievedLampID, uint32_t& t_LampServiceVersion)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* p_InterfaceDescription = m_BusAttachment->GetInterface(LAMP_INTERFACE_NAME);
	if (!p_InterfaceDescription) {
		return ER_FAIL;
	}

	ajn::ProxyBusObject* proxyBusObj = new ajn::ProxyBusObject(*m_BusAttachment, m_BusName.c_str(), CONTROLLERSERVICE_OBJECT_PATH, m_SessionId);
	if (!proxyBusObj) {
		return ER_FAIL;
	}
	status = proxyBusObj->AddInterface(*p_InterfaceDescription);
	if (status == ER_OK) {
		ajn::MsgArg* msgArg = new ajn::MsgArg[1];
		msgArg[0].Set("s", t_LampID);
		ajn::Message responseMessage(*m_BusAttachment);
		CHECK_RETURN(proxyBusObj->MethodCall(LAMP_INTERFACE_NAME, "GetLampServiceVersion", msgArg, 1, responseMessage))
			CHECK_RETURN(responseMessage->GetArg(0)->Get("u", &t_ResponseCode))

			char* tempRetrievedLampID;
		CHECK_RETURN(responseMessage->GetArg(1)->Get("s", &tempRetrievedLampID))
			t_RetrievedLampID = qcc::String(tempRetrievedLampID);

		CHECK_RETURN(responseMessage->GetArg(2)->Get("s", &t_LampServiceVersion));
	}
	delete proxyBusObj;
	return status;
}