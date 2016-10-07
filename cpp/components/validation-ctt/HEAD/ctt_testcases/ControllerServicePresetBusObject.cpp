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
#include "ControllerServicePresetBusObject.h"

#define CHECK_BREAK(x) if ((status = x) != ER_OK) { break; }
#define CHECK_RETURN(x) if ((status = x) != ER_OK) { return status; }

static const char* CONTROLLERSERVICE_OBJECT_PATH = "/org/allseen/LSF/ControllerService";
static const char* PRESET_INTERFACE_NAME = "org.allseen.LSF.ControllerService.Preset";

ControllerServicePresetBusObject::ControllerServicePresetBusObject(ajn::BusAttachment& t_BusAttachment, const std::string& t_BusName, const ajn::SessionId t_SessionId) :
m_BusAttachment(&t_BusAttachment), m_BusName(t_BusName), m_SessionId(t_SessionId),
m_DefaultLampStateChangedSignalReceived(false),
m_PresetNameChangedSignalReceived(false), m_PresetsCreatedSignalReceived(false),
m_PresetsUpdatedSignalReceived(false), m_PresetsDeletedSignalReceived(false)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* getIface = NULL;
	getIface = m_BusAttachment->GetInterface(PRESET_INTERFACE_NAME);
	if (!getIface) {
		ajn::InterfaceDescription* createIface = NULL;
		status = m_BusAttachment->CreateInterface(PRESET_INTERFACE_NAME, createIface, ajn::AJ_IFC_SECURITY_OFF);
		if (createIface && status == ER_OK) {
			do {
				CHECK_BREAK(createIface->AddMethod("GetDefaultLampState", NULL, "ua{sv}", "responseCode,lampState"))
				CHECK_BREAK(createIface->AddMethod("SetDefaultLampState", "a{sv}", "u", "lampState,responseCode"))
				CHECK_BREAK(createIface->AddMethod("GetAllPresetIDs", NULL, "uas", "responseCode,presetIDs"))
				CHECK_BREAK(createIface->AddMethod("GetPresetName", "ss", "usss", "presetID,language,responseCode,presetID,language,presetName"))
				CHECK_BREAK(createIface->AddMethod("SetPresetName", "sss", "uss", "presetID,presetName,language,responseCode,presetID,language"))
				CHECK_BREAK(createIface->AddMethod("CreatePreset", "a{sv}ss", "us", "lampState,presetName,language,responseCode,presetID"))
				CHECK_BREAK(createIface->AddMethod("UpdatePreset", "sa{sv}", "us", "presetID,lampState,responseCode,presetID"))
				CHECK_BREAK(createIface->AddMethod("DeletePreset", "s", "us", "presetID,responseCode,presetID"))
				CHECK_BREAK(createIface->AddMethod("GetPreset", "s", "usa{sv}", "presetID,responseCode,presetID,lampState"))
					
				CHECK_BREAK(createIface->AddProperty("Version", "u", ajn::PROP_ACCESS_READ))

				CHECK_BREAK(createIface->AddSignal("DefaultLampStateChanged", NULL, NULL))
				CHECK_BREAK(createIface->AddSignal("PresetsNameChanged", "as", "presetIDs"))
				CHECK_BREAK(createIface->AddSignal("PresetsCreated", "as", "presetIDs"))
				CHECK_BREAK(createIface->AddSignal("PresetsUpdated", "as", "presetIDs"))
				CHECK_BREAK(createIface->AddSignal("PresetsDeleted", "as", "presetIDs"))
					createIface->Activate();

				const ajn::InterfaceDescription* uniqueId = m_BusAttachment->GetInterface(PRESET_INTERFACE_NAME);
				const ajn::InterfaceDescription::Member* sig = uniqueId->GetMember("DefaultLampStateChanged");
				m_BusAttachment->RegisterSignalHandler(this, static_cast<ajn::MessageReceiver::SignalHandler>(&ControllerServicePresetBusObject::DefaultLampStateChangedSignalHandler), sig, CONTROLLERSERVICE_OBJECT_PATH);

				sig = uniqueId->GetMember("PresetsNameChanged");
				m_BusAttachment->RegisterSignalHandler(this, static_cast<ajn::MessageReceiver::SignalHandler>(&ControllerServicePresetBusObject::PresetNameChangedSignalHandler), sig, CONTROLLERSERVICE_OBJECT_PATH);

				sig = uniqueId->GetMember("PresetsCreated");
				m_BusAttachment->RegisterSignalHandler(this, static_cast<ajn::MessageReceiver::SignalHandler>(&ControllerServicePresetBusObject::PresetsCreatedSignalHandler), sig, CONTROLLERSERVICE_OBJECT_PATH);

				sig = uniqueId->GetMember("PresetsUpdated");
				m_BusAttachment->RegisterSignalHandler(this, static_cast<ajn::MessageReceiver::SignalHandler>(&ControllerServicePresetBusObject::PresetsUpdatedSignalHandler), sig, CONTROLLERSERVICE_OBJECT_PATH);

				sig = uniqueId->GetMember("PresetsDeleted");
				m_BusAttachment->RegisterSignalHandler(this, static_cast<ajn::MessageReceiver::SignalHandler>(&ControllerServicePresetBusObject::PresetsDeletedSignalHandler), sig, CONTROLLERSERVICE_OBJECT_PATH);

				return;
			} while (0);
		} //if (createIface)
	} //if (!getIface)
}

void ControllerServicePresetBusObject::DefaultLampStateChangedSignalHandler(const ajn::InterfaceDescription::Member* member, const char* sourcePath, ajn::Message& msg)
{
	LOG(INFO) << "DefaultLampStateChanged signal received";
	m_DefaultLampStateChangedSignalReceived = true;
}

bool ControllerServicePresetBusObject::DidDefaultLampStateChanged()
{
	return m_DefaultLampStateChangedSignalReceived;
}

void ControllerServicePresetBusObject::PresetNameChangedSignalHandler(const ajn::InterfaceDescription::Member* member, const char* sourcePath, ajn::Message& msg)
{
	LOG(INFO) << "PresetNameChanged signal received";
	m_PresetNameChangedSignalReceived = true;
}

bool ControllerServicePresetBusObject::DidPresetNameChanged()
{
	return m_PresetNameChangedSignalReceived;
}

void ControllerServicePresetBusObject::PresetsCreatedSignalHandler(const ajn::InterfaceDescription::Member* member, const char* sourcePath, ajn::Message& msg)
{
	LOG(INFO) << "PresetsCreated signal received";
	m_PresetsCreatedSignalReceived = true;
}

bool ControllerServicePresetBusObject::DidPresetsCreated()
{
	return m_PresetsCreatedSignalReceived;
}

void ControllerServicePresetBusObject::PresetsUpdatedSignalHandler(const ajn::InterfaceDescription::Member* member, const char* sourcePath, ajn::Message& msg)
{
	LOG(INFO) << "PresetsUpdated signal received";
	m_PresetsUpdatedSignalReceived = true;
}

bool ControllerServicePresetBusObject::DidPresetsUpdated()
{
	return m_PresetsUpdatedSignalReceived;
}

void ControllerServicePresetBusObject::PresetsDeletedSignalHandler(const ajn::InterfaceDescription::Member* member, const char* sourcePath, ajn::Message& msg)
{
	LOG(INFO) << "PresetsDeleted signal received";
	m_PresetsDeletedSignalReceived = true;
}

bool ControllerServicePresetBusObject::DidPresetsDeleted()
{
	return m_PresetsDeletedSignalReceived;
}

QStatus ControllerServicePresetBusObject::GetVersion(uint32_t& version)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* p_InterfaceDescription = m_BusAttachment->GetInterface(PRESET_INTERFACE_NAME);
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
		CHECK_BREAK(proxyBusObj->GetProperty(PRESET_INTERFACE_NAME, "Version", arg))
		version = arg.v_variant.val->v_uint32;
	} while (0);
	delete proxyBusObj;
	return status;
}

QStatus ControllerServicePresetBusObject::GetDefaultLampState(uint32_t& t_ResponseCode,
	std::vector<ajn::MsgArg>& t_LampState)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* p_InterfaceDescription = m_BusAttachment->GetInterface(PRESET_INTERFACE_NAME);
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
		CHECK_RETURN(proxyBusObj->MethodCall(PRESET_INTERFACE_NAME, "GetDefaultLampState", NULL, 0, responseMessage))
		CHECK_RETURN(responseMessage->GetArg(0)->Get("u", &t_ResponseCode))

		uint32_t* tempIDsArraySize = new uint32_t();
		ajn::MsgArg* tempIDsArray;
		CHECK_RETURN(responseMessage->GetArg(1)->Get("a{sv}", tempIDsArraySize, &tempIDsArray))

		t_LampState.clear();
		for (size_t i = 0; i < *tempIDsArraySize; ++i)
		{
			t_LampState.push_back(tempIDsArray[i]);
		}
	}
	delete proxyBusObj;
	return status;
}

QStatus ControllerServicePresetBusObject::SetDefaultLampState(ajn::MsgArg* t_LampState, uint32_t& t_ResponseCode)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* p_InterfaceDescription = m_BusAttachment->GetInterface(PRESET_INTERFACE_NAME);
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
		msgArg[0].Set("a{sv}", 5, t_LampState);
		ajn::Message responseMessage(*m_BusAttachment);
		CHECK_RETURN(proxyBusObj->MethodCall(PRESET_INTERFACE_NAME, "SetDefaultLampState", msgArg, 1, responseMessage))
		CHECK_RETURN(responseMessage->GetArg(0)->Get("u", &t_ResponseCode))
	}
	delete proxyBusObj;
	return status;
}

QStatus ControllerServicePresetBusObject::GetAllPresetIDs(uint32_t& t_ResponseCode,
	std::vector<qcc::String>& t_PresetIDs)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* p_InterfaceDescription = m_BusAttachment->GetInterface(PRESET_INTERFACE_NAME);
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
		CHECK_RETURN(proxyBusObj->MethodCall(PRESET_INTERFACE_NAME, "GetAllPresetIDs", NULL, 0, responseMessage))
		CHECK_RETURN(responseMessage->GetArg(0)->Get("u", &t_ResponseCode))

		uint32_t* tempIDsArraySize = new uint32_t();
		ajn::MsgArg* tempIDsArray;
		CHECK_RETURN(responseMessage->GetArg(1)->Get("as", tempIDsArraySize, &tempIDsArray))

		t_PresetIDs.clear();
		for (size_t i = 0; i < *tempIDsArraySize; ++i)
		{
			t_PresetIDs.push_back(tempIDsArray[i].v_string.str);
		}
	}
	delete proxyBusObj;
	return status;
}

QStatus ControllerServicePresetBusObject::GetPresetName(const char* t_PresetID, const char* t_Language, uint32_t& t_ResponseCode, qcc::String& t_RetrievedPresetID, qcc::String& t_RetrievedLanguage, qcc::String& t_PresetName)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* p_InterfaceDescription = m_BusAttachment->GetInterface(PRESET_INTERFACE_NAME);
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
		msgArg[0].Set("s", t_PresetID);
		msgArg[1].Set("s", t_Language);
		ajn::Message responseMessage(*m_BusAttachment);
		CHECK_RETURN(proxyBusObj->MethodCall(PRESET_INTERFACE_NAME, "GetPresetName", msgArg, 2, responseMessage))
		CHECK_RETURN(responseMessage->GetArg(0)->Get("u", &t_ResponseCode))

		char* tempRetrievedPresetID;
		CHECK_RETURN(responseMessage->GetArg(1)->Get("s", &tempRetrievedPresetID))
		t_RetrievedPresetID = qcc::String(tempRetrievedPresetID);

		char* tempRetrievedLanguage;
		CHECK_RETURN(responseMessage->GetArg(2)->Get("s", &tempRetrievedLanguage))
		t_RetrievedLanguage = qcc::String(tempRetrievedPresetID);

		char* tempPresetName;
		CHECK_RETURN(responseMessage->GetArg(3)->Get("s", &tempPresetName))
		t_PresetName = qcc::String(tempRetrievedPresetID);

	}
	delete proxyBusObj;
	return status;
}

QStatus ControllerServicePresetBusObject::SetPresetName(const char* t_PresetID, const char* t_PresetName, const char* t_Language, uint32_t& t_ResponseCode, qcc::String& t_RetrievedPresetID, qcc::String& t_RetrievedLanguage)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* p_InterfaceDescription = m_BusAttachment->GetInterface(PRESET_INTERFACE_NAME);
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
		msgArg[0].Set("s", t_PresetID);
		msgArg[1].Set("s", t_PresetName);
		msgArg[2].Set("s", t_Language);
		ajn::Message responseMessage(*m_BusAttachment);
		CHECK_RETURN(proxyBusObj->MethodCall(PRESET_INTERFACE_NAME, "SetPresetName", msgArg, 3, responseMessage))
		CHECK_RETURN(responseMessage->GetArg(0)->Get("u", &t_ResponseCode))

		char* tempRetrievedPresetID;
		CHECK_RETURN(responseMessage->GetArg(1)->Get("s", &tempRetrievedPresetID))
		t_RetrievedPresetID = qcc::String(tempRetrievedPresetID);

		char* tempRetrievedLanguage;
		CHECK_RETURN(responseMessage->GetArg(2)->Get("s", &tempRetrievedLanguage))
		t_RetrievedLanguage = qcc::String(tempRetrievedPresetID);
	}
	delete proxyBusObj;
	return status;
}

QStatus ControllerServicePresetBusObject::CreatePreset(ajn::MsgArg* t_LampState, const char* t_PresetName, const char* t_Language, uint32_t& t_ResponseCode, qcc::String& t_RetrievedPresetID)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* p_InterfaceDescription = m_BusAttachment->GetInterface(PRESET_INTERFACE_NAME);
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
		msgArg[0].Set("a{sv}", 5, t_LampState);
		msgArg[1].Set("s", t_PresetName);
		msgArg[2].Set("s", t_Language);
		ajn::Message responseMessage(*m_BusAttachment);
		CHECK_RETURN(proxyBusObj->MethodCall(PRESET_INTERFACE_NAME, "CreatePreset", msgArg, 3, responseMessage))
		CHECK_RETURN(responseMessage->GetArg(0)->Get("u", &t_ResponseCode))

		char* tempRetrievedPresetID;
		CHECK_RETURN(responseMessage->GetArg(1)->Get("s", &tempRetrievedPresetID))
		t_RetrievedPresetID = qcc::String(tempRetrievedPresetID);
	}
	delete proxyBusObj;
	return status;
}

QStatus ControllerServicePresetBusObject::UpdatePreset(const char* t_PresetID, ajn::MsgArg* t_LampState, uint32_t& t_ResponseCode, qcc::String& t_RetrievedPresetID)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* p_InterfaceDescription = m_BusAttachment->GetInterface(PRESET_INTERFACE_NAME);
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
		msgArg[0].Set("s", t_PresetID);
		msgArg[1].Set("a{sv}", 5, t_LampState);
		ajn::Message responseMessage(*m_BusAttachment);
		CHECK_RETURN(proxyBusObj->MethodCall(PRESET_INTERFACE_NAME, "UpdatePreset", msgArg, 2, responseMessage))
		CHECK_RETURN(responseMessage->GetArg(0)->Get("u", &t_ResponseCode))

		char* tempRetrievedPresetID;
		CHECK_RETURN(responseMessage->GetArg(1)->Get("s", &tempRetrievedPresetID))
		t_RetrievedPresetID = qcc::String(tempRetrievedPresetID);
	}
	delete proxyBusObj;
	return status;
}

QStatus ControllerServicePresetBusObject::DeletePreset(const char* t_PresetID, uint32_t& t_ResponseCode, qcc::String& t_RetrievedPresetID)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* p_InterfaceDescription = m_BusAttachment->GetInterface(PRESET_INTERFACE_NAME);
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
		msgArg[0].Set("s", t_PresetID);
		ajn::Message responseMessage(*m_BusAttachment);
		CHECK_RETURN(proxyBusObj->MethodCall(PRESET_INTERFACE_NAME, "DeletePreset", msgArg, 1, responseMessage))
		CHECK_RETURN(responseMessage->GetArg(0)->Get("u", &t_ResponseCode))

		char* tempRetrievedPresetID;
		CHECK_RETURN(responseMessage->GetArg(1)->Get("s", &tempRetrievedPresetID))
		t_RetrievedPresetID = qcc::String(tempRetrievedPresetID);
	}
	delete proxyBusObj;
	return status;
}

QStatus ControllerServicePresetBusObject::GetPreset(const char* t_PresetID, uint32_t& t_ResponseCode, qcc::String& t_RetrievedPresetID, std::vector<ajn::MsgArg>& t_LampState)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* p_InterfaceDescription = m_BusAttachment->GetInterface(PRESET_INTERFACE_NAME);
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
		msgArg[0].Set("s", t_PresetID);
		ajn::Message responseMessage(*m_BusAttachment);
		CHECK_RETURN(proxyBusObj->MethodCall(PRESET_INTERFACE_NAME, "GetPreset", msgArg, 1, responseMessage))
			CHECK_RETURN(responseMessage->GetArg(0)->Get("u", &t_ResponseCode))

		char* tempRetrievedPresetID;
		CHECK_RETURN(responseMessage->GetArg(1)->Get("s", &tempRetrievedPresetID))
		t_RetrievedPresetID = qcc::String(tempRetrievedPresetID);

		size_t tempLampStateArraySize;
		ajn::MsgArg* tempLampStateArray;
		CHECK_RETURN(responseMessage->GetArg(2)->Get("a{sv}", &tempLampStateArraySize, &tempLampStateArray))
		
		t_LampState.clear();
		for (size_t i = 0; i < tempLampStateArraySize; ++i)
		{
			t_LampState.push_back(tempLampStateArray[i]);
		}
	}
	delete proxyBusObj;
	return status;
}