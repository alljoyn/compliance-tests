/******************************************************************************
* Copyright (c) Open Connectivity Foundation (OCF) and AllJoyn Open
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
#include "ControllerServiceTransitionEffectBusObject.h"

#define CHECK_BREAK(x) if ((status = x) != ER_OK) { break; }
#define CHECK_RETURN(x) if ((status = x) != ER_OK) { return status; }

static const char* CONTROLLERSERVICE_OBJECT_PATH = "/org/allseen/LSF/ControllerService";
static const char* TRANSITIONEFFECT_INTERFACE_NAME = "org.allseen.LSF.ControllerService.TransitionEffect";

ControllerServiceTransitionEffectBusObject::ControllerServiceTransitionEffectBusObject(ajn::BusAttachment& t_BusAttachment, const std::string& t_BusName, const ajn::SessionId t_SessionId) :
m_BusAttachment(&t_BusAttachment), m_BusName(t_BusName), m_SessionId(t_SessionId),
m_TransitionEffectsCreatedSignalReceived(false),
m_TransitionEffectsUpdatedSignalReceived(false), m_TransitionEffectsDeletedSignalReceived(false),
m_TransitionEffectsNameChangedSignalReceived(false)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* getIface = NULL;
	getIface = m_BusAttachment->GetInterface(TRANSITIONEFFECT_INTERFACE_NAME);
	if (!getIface) {
		ajn::InterfaceDescription* createIface = NULL;
		status = m_BusAttachment->CreateInterface(TRANSITIONEFFECT_INTERFACE_NAME, createIface, ajn::AJ_IFC_SECURITY_OFF);
		if (createIface && status == ER_OK) {
			do {
				CHECK_BREAK(createIface->AddMethod("ApplyTransitionEffectOnLampGroups", "sas", "usas", "transitionEffectID,lampGroupIDs,responseCode,transitionEffectID,lampGroupIDs"))
				CHECK_BREAK(createIface->AddMethod("ApplyTransitionEffectOnLamps", "sas", "usas", "transitionEffectID,lampIDs,responseCode,transitionEffectID,lampIDs"))
				CHECK_BREAK(createIface->AddMethod("CreateTransitionEffect", "a{sv}suss", "us", "lampState,presetID,transitionPeriod,transitionEffectName,language,responseCode,transitionEffectID"))
				CHECK_BREAK(createIface->AddMethod("DeleteTransitionEffect", "s", "us", "transitionEffectID,responseCode,transitionEffectID"))
				CHECK_BREAK(createIface->AddMethod("GetAllTransitionEffectIDs", NULL, "uas", "responseCode,transitionEffectIDs"))
				CHECK_BREAK(createIface->AddMethod("GetTransitionEffect", "s", "usa{sv}su", "transitionEffectID,responseCode,transitionEffectID,lampState,presetID,transitionPeriod"))
				CHECK_BREAK(createIface->AddMethod("GetTransitionEffectName", "ss", "usss", "transitionEffectID,language,responseCode,transitionEffectID,language,transitionEffectName"))
				CHECK_BREAK(createIface->AddMethod("SetTransitionEffectName", "sss", "uss", "transitionEffectID,transitionEffectName,language,responseCode,transitionEffectID,language"))
				CHECK_BREAK(createIface->AddMethod("UpdateTransitionEffect", "sa{sv}su", "us", "transitionEffectID,lampState,presetID,transitionPeriod,responseCode,transitionEffectID"))

				CHECK_BREAK(createIface->AddProperty("Version", "u", ajn::PROP_ACCESS_READ))

				CHECK_BREAK(createIface->AddSignal("TransitionEffectsNameChanged", "as", "transitionEffectIDs"))
				CHECK_BREAK(createIface->AddSignal("TransitionEffectsCreated", "as", "transitionEffectIDs"))
				CHECK_BREAK(createIface->AddSignal("TransitionEffectsUpdated", "as", "transitionEffectIDs"))
				CHECK_BREAK(createIface->AddSignal("TransitionEffectsDeleted", "as", "transitionEffectIDs"))
				createIface->Activate();

				const ajn::InterfaceDescription* uniqueId = m_BusAttachment->GetInterface(TRANSITIONEFFECT_INTERFACE_NAME);
				const ajn::InterfaceDescription::Member* sig = uniqueId->GetMember("TransitionEffectsCreated");
				m_BusAttachment->RegisterSignalHandler(this, static_cast<ajn::MessageReceiver::SignalHandler>(&ControllerServiceTransitionEffectBusObject::TransitionEffectsCreatedSignalHandler), sig, CONTROLLERSERVICE_OBJECT_PATH);

				sig = uniqueId->GetMember("TransitionEffectsUpdated");
				m_BusAttachment->RegisterSignalHandler(this, static_cast<ajn::MessageReceiver::SignalHandler>(&ControllerServiceTransitionEffectBusObject::TransitionEffectsUpdatedSignalHandler), sig, CONTROLLERSERVICE_OBJECT_PATH);

				sig = uniqueId->GetMember("TransitionEffectsDeleted");
				m_BusAttachment->RegisterSignalHandler(this, static_cast<ajn::MessageReceiver::SignalHandler>(&ControllerServiceTransitionEffectBusObject::TransitionEffectsDeletedSignalHandler), sig, CONTROLLERSERVICE_OBJECT_PATH);

				sig = uniqueId->GetMember("TransitionEffectsNameChanged");
				m_BusAttachment->RegisterSignalHandler(this, static_cast<ajn::MessageReceiver::SignalHandler>(&ControllerServiceTransitionEffectBusObject::TransitionEffectsNameChangedSignalHandler), sig, CONTROLLERSERVICE_OBJECT_PATH);

				return;
			} while (0);
		} //if (createIface)
	} //if (!getIface)
}

void ControllerServiceTransitionEffectBusObject::TransitionEffectsCreatedSignalHandler(const ajn::InterfaceDescription::Member* member, const char* sourcePath, ajn::Message& msg)
{
	LOG(INFO) << "TransitionEffectsCreated signal received";
	m_TransitionEffectsCreatedSignalReceived = true;
}

bool ControllerServiceTransitionEffectBusObject::DidTransitionEffectsCreated()
{
	return m_TransitionEffectsCreatedSignalReceived;
}

void ControllerServiceTransitionEffectBusObject::TransitionEffectsUpdatedSignalHandler(const ajn::InterfaceDescription::Member* member, const char* sourcePath, ajn::Message& msg)
{
	LOG(INFO) << "TransitionEffectsUpdated signal received";
	m_TransitionEffectsUpdatedSignalReceived = true;
}

bool ControllerServiceTransitionEffectBusObject::DidTransitionEffectsUpdated()
{
	return m_TransitionEffectsUpdatedSignalReceived;
}

void ControllerServiceTransitionEffectBusObject::TransitionEffectsDeletedSignalHandler(const ajn::InterfaceDescription::Member* member, const char* sourcePath, ajn::Message& msg)
{
	LOG(INFO) << "TransitionEffectsDeleted signal received";
	m_TransitionEffectsDeletedSignalReceived = true;
}

bool ControllerServiceTransitionEffectBusObject::DidTransitionEffectsDeleted()
{
	return m_TransitionEffectsDeletedSignalReceived;
}

void ControllerServiceTransitionEffectBusObject::TransitionEffectsNameChangedSignalHandler(const ajn::InterfaceDescription::Member* member, const char* sourcePath, ajn::Message& msg)
{
	LOG(INFO) << "TransitionEffectsNameChanged signal received";
	m_TransitionEffectsNameChangedSignalReceived = true;
}

bool ControllerServiceTransitionEffectBusObject::DidTransitionEffectsNameChanged()
{
	return m_TransitionEffectsNameChangedSignalReceived;
}

QStatus ControllerServiceTransitionEffectBusObject::GetVersion(uint32_t& version)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* p_InterfaceDescription = m_BusAttachment->GetInterface(TRANSITIONEFFECT_INTERFACE_NAME);
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
		CHECK_BREAK(proxyBusObj->GetProperty(TRANSITIONEFFECT_INTERFACE_NAME, "Version", arg))
		version = arg.v_variant.val->v_uint32;
	} while (0);
	delete proxyBusObj;
	return status;
}

QStatus ControllerServiceTransitionEffectBusObject::ApplyTransitionEffectOnLampGroups(const char* t_TransitionEffectID, const std::vector<const char*>& t_LampGroupIDs, uint32_t& t_ResponseCode, qcc::String& t_RetrievedTransitionEffectID, std::vector<qcc::String>& t_RetrievedLampGroupIDs)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* p_InterfaceDescription = m_BusAttachment->GetInterface(TRANSITIONEFFECT_INTERFACE_NAME);
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
		msgArg[0].Set("s", t_TransitionEffectID);
		msgArg[1].Set("as", t_LampGroupIDs.size(), t_LampGroupIDs.data());
		ajn::Message responseMessage(*m_BusAttachment);
		CHECK_RETURN(proxyBusObj->MethodCall(TRANSITIONEFFECT_INTERFACE_NAME, "ApplyTransitionEffectOnLampGroups", msgArg, 2, responseMessage))
		CHECK_RETURN(responseMessage->GetArg(0)->Get("u", &t_ResponseCode))

		char* tempRetrievedTransitionEffectID;
		CHECK_RETURN(responseMessage->GetArg(1)->Get("s", &tempRetrievedTransitionEffectID))
		t_RetrievedTransitionEffectID = qcc::String(tempRetrievedTransitionEffectID);

		size_t tempLampGroupIDsSize;
		char** tempLampGroupIDs;
		CHECK_RETURN(responseMessage->GetArg(1)->Get("as", &tempLampGroupIDsSize, &tempLampGroupIDs))

		t_RetrievedLampGroupIDs.clear();
		for (size_t i = 0; i < tempLampGroupIDsSize; ++i)
		{
			t_RetrievedLampGroupIDs.push_back(tempLampGroupIDs[i]);
		}
	}
	delete proxyBusObj;
	return status;
}

QStatus ControllerServiceTransitionEffectBusObject::ApplyTransitionEffectOnLamps(const char* t_TransitionEffectID, const std::vector<const char*>& t_LampIDs, uint32_t& t_ResponseCode, qcc::String& t_RetrievedTransitionEffectID, std::vector<qcc::String>& t_RetrievedLampIDs)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* p_InterfaceDescription = m_BusAttachment->GetInterface(TRANSITIONEFFECT_INTERFACE_NAME);
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
		msgArg[0].Set("s", t_TransitionEffectID);
		msgArg[1].Set("as", t_LampIDs.size(), t_LampIDs.data());
		ajn::Message responseMessage(*m_BusAttachment);
		CHECK_RETURN(proxyBusObj->MethodCall(TRANSITIONEFFECT_INTERFACE_NAME, "ApplyTransitionEffectOnLamps", msgArg, 2, responseMessage))
		CHECK_RETURN(responseMessage->GetArg(0)->Get("u", &t_ResponseCode))

		char* tempRetrievedTransitionEffectID;
		CHECK_RETURN(responseMessage->GetArg(1)->Get("s", &tempRetrievedTransitionEffectID))
			t_RetrievedTransitionEffectID = qcc::String(tempRetrievedTransitionEffectID);

		size_t tempLampIDsSize;
		char** tempLampIDs;
		CHECK_RETURN(responseMessage->GetArg(2)->Get("as", &tempLampIDsSize, &tempLampIDs))

		t_RetrievedLampIDs.clear();
		for (size_t i = 0; i < tempLampIDsSize; ++i)
		{
			t_RetrievedLampIDs.push_back(tempLampIDs[i]);
		}
	}
	delete proxyBusObj;
	return status;
}

QStatus ControllerServiceTransitionEffectBusObject::CreateTransitionEffect(ajn::MsgArg* t_LampState, const char* t_PresetID, const uint32_t t_TransitionPeriod, const char* t_TransitionEffectName, const char* t_Language, uint32_t& t_ResponseCode, qcc::String& t_RetrievedTransitionEffectID)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* p_InterfaceDescription = m_BusAttachment->GetInterface(TRANSITIONEFFECT_INTERFACE_NAME);
	if (!p_InterfaceDescription) {
		return ER_FAIL;
	}

	ajn::ProxyBusObject* proxyBusObj = new ajn::ProxyBusObject(*m_BusAttachment, m_BusName.c_str(), CONTROLLERSERVICE_OBJECT_PATH, m_SessionId);
	if (!proxyBusObj) {
		return ER_FAIL;
	}
	status = proxyBusObj->AddInterface(*p_InterfaceDescription);
	if (status == ER_OK) {
		ajn::MsgArg* msgArg = new ajn::MsgArg[5];
		msgArg[0].Set("a{sv}", 5, t_LampState);
		msgArg[1].Set("s", t_PresetID);
		msgArg[2].Set("u", t_TransitionPeriod);
		msgArg[3].Set("s", t_TransitionEffectName);
		msgArg[4].Set("s", t_Language);

		ajn::Message responseMessage(*m_BusAttachment);
		CHECK_RETURN(proxyBusObj->MethodCall(TRANSITIONEFFECT_INTERFACE_NAME, "CreateTransitionEffect", msgArg, 5, responseMessage))
		CHECK_RETURN(responseMessage->GetArg(0)->Get("u", &t_ResponseCode))

		char* tempRetrievedTransitionEffectID;
		CHECK_RETURN(responseMessage->GetArg(1)->Get("s", &tempRetrievedTransitionEffectID))
		t_RetrievedTransitionEffectID = qcc::String(tempRetrievedTransitionEffectID);
	}
	delete proxyBusObj;
	return status;
}

QStatus ControllerServiceTransitionEffectBusObject::DeleteTransitionEffect(const char* t_TransitionEffectID, uint32_t& t_ResponseCode, qcc::String& t_RetrievedTransitionEffectID)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* p_InterfaceDescription = m_BusAttachment->GetInterface(TRANSITIONEFFECT_INTERFACE_NAME);
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
		msgArg[0].Set("s", t_TransitionEffectID);

		ajn::Message responseMessage(*m_BusAttachment);
		CHECK_RETURN(proxyBusObj->MethodCall(TRANSITIONEFFECT_INTERFACE_NAME, "DeleteTransitionEffect", msgArg, 1, responseMessage))
		CHECK_RETURN(responseMessage->GetArg(0)->Get("u", &t_ResponseCode))

		char* tempRetrievedTransitionEffectID;
		CHECK_RETURN(responseMessage->GetArg(1)->Get("s", &tempRetrievedTransitionEffectID))
		t_RetrievedTransitionEffectID = qcc::String(tempRetrievedTransitionEffectID);
	}
	delete proxyBusObj;
	return status;
}

QStatus ControllerServiceTransitionEffectBusObject::GetAllTransitionEffectIDs(uint32_t& t_ResponseCode, std::vector<qcc::String>& t_TransitionEffectIDs)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* p_InterfaceDescription = m_BusAttachment->GetInterface(TRANSITIONEFFECT_INTERFACE_NAME);
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
		CHECK_RETURN(proxyBusObj->MethodCall(TRANSITIONEFFECT_INTERFACE_NAME, "GetAllTransitionEffectIDs", NULL, 0, responseMessage))
		CHECK_RETURN(responseMessage->GetArg(0)->Get("u", &t_ResponseCode))

		size_t tempTransitionEffectIDsSize;
		ajn::MsgArg* tempTransitionEffectIDs;
		CHECK_RETURN(responseMessage->GetArg(1)->Get("as", &tempTransitionEffectIDsSize, &tempTransitionEffectIDs))

		t_TransitionEffectIDs.clear();
		for (size_t i = 0; i < tempTransitionEffectIDsSize; ++i)
		{
			t_TransitionEffectIDs.push_back(tempTransitionEffectIDs[i].v_string.str);
		}
	}
	delete proxyBusObj;
	return status;
}

QStatus ControllerServiceTransitionEffectBusObject::GetTransitionEffect(const char* t_TransitionEffectID, uint32_t& t_ResponseCode, qcc::String& t_RetrievedTransitionEffectID, std::vector<ajn::MsgArg>& t_LampState, qcc::String& t_PresetID, uint32_t& t_TransitionPeriod)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* p_InterfaceDescription = m_BusAttachment->GetInterface(TRANSITIONEFFECT_INTERFACE_NAME);
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
		msgArg[0].Set("s", t_TransitionEffectID);

		ajn::Message responseMessage(*m_BusAttachment);
		CHECK_RETURN(proxyBusObj->MethodCall(TRANSITIONEFFECT_INTERFACE_NAME, "GetTransitionEffect", msgArg, 1, responseMessage))
		CHECK_RETURN(responseMessage->GetArg(0)->Get("u", &t_ResponseCode))

		char* tempRetrievedTransitionEffectID;
		CHECK_RETURN(responseMessage->GetArg(1)->Get("s", &tempRetrievedTransitionEffectID))
		t_RetrievedTransitionEffectID = qcc::String(tempRetrievedTransitionEffectID);

		size_t tempLampStateSize;
		ajn::MsgArg* tempLampState;
		CHECK_RETURN(responseMessage->GetArg(2)->Get("a{sv}", &tempLampStateSize, &tempLampState))

		t_LampState.clear();
		for (size_t i = 0; i < tempLampStateSize; ++i)
		{
			t_LampState.push_back(tempLampState[i]);
		}

		char* tempPresetID;
		CHECK_RETURN(responseMessage->GetArg(3)->Get("s", &tempPresetID))
		t_PresetID = qcc::String(tempPresetID);

		CHECK_RETURN(responseMessage->GetArg(4)->Get("u", &t_TransitionPeriod))
	}
	delete proxyBusObj;
	return status;
}

QStatus ControllerServiceTransitionEffectBusObject::GetTransitionEffectName(const char* t_TransitionEffectID, const char* t_Language, uint32_t& t_ResponseCode, qcc::String& t_RetrievedTransitionEffectID, qcc::String& t_RetrievedLanguage, qcc::String& t_TransitionEffectName)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* p_InterfaceDescription = m_BusAttachment->GetInterface(TRANSITIONEFFECT_INTERFACE_NAME);
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
		msgArg[0].Set("s", t_TransitionEffectID);
		msgArg[1].Set("s", t_Language);

		ajn::Message responseMessage(*m_BusAttachment);
		CHECK_RETURN(proxyBusObj->MethodCall(TRANSITIONEFFECT_INTERFACE_NAME, "GetTransitionEffectName", msgArg, 2, responseMessage))
		CHECK_RETURN(responseMessage->GetArg(0)->Get("u", &t_ResponseCode))

		char* tempRetrievedTransitionEffectID;
		CHECK_RETURN(responseMessage->GetArg(1)->Get("s", &tempRetrievedTransitionEffectID))
		t_RetrievedTransitionEffectID = qcc::String(tempRetrievedTransitionEffectID);

		char* tempRetrievedLanguage;
		CHECK_RETURN(responseMessage->GetArg(2)->Get("s", &tempRetrievedLanguage))
		t_RetrievedLanguage = qcc::String(tempRetrievedLanguage);

		char* tempTransitionEffectName;
		CHECK_RETURN(responseMessage->GetArg(3)->Get("s", &tempTransitionEffectName))
		t_TransitionEffectName = qcc::String(tempTransitionEffectName);
	}
	delete proxyBusObj;
	return status;
}

QStatus ControllerServiceTransitionEffectBusObject::SetTransitionEffectName(const char* t_TransitionEffectID, const char* t_TransitionEffectName, const char* t_Language, uint32_t& t_ResponseCode, qcc::String& t_RetrievedTransitionEffectID, qcc::String& t_RetrievedLanguage)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* p_InterfaceDescription = m_BusAttachment->GetInterface(TRANSITIONEFFECT_INTERFACE_NAME);
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
		msgArg[0].Set("s", t_TransitionEffectID);
		msgArg[1].Set("s", t_TransitionEffectName);
		msgArg[2].Set("s", t_Language);

		ajn::Message responseMessage(*m_BusAttachment);
		CHECK_RETURN(proxyBusObj->MethodCall(TRANSITIONEFFECT_INTERFACE_NAME, "SetTransitionEffectName", msgArg, 3, responseMessage))
		CHECK_RETURN(responseMessage->GetArg(0)->Get("u", &t_ResponseCode))

		char* tempRetrievedTransitionEffectID;
		CHECK_RETURN(responseMessage->GetArg(1)->Get("s", &tempRetrievedTransitionEffectID))
		t_RetrievedTransitionEffectID = qcc::String(tempRetrievedTransitionEffectID);

		char* tempRetrievedLanguage;
		CHECK_RETURN(responseMessage->GetArg(2)->Get("s", &tempRetrievedLanguage))
		t_RetrievedLanguage = qcc::String(tempRetrievedLanguage);
	}
	delete proxyBusObj;
	return status;
}

QStatus ControllerServiceTransitionEffectBusObject::UpdateTransitionEffect(const char* t_TransitionEffectID, ajn::MsgArg* t_LampState, const char* t_PresetID, const uint32_t t_TransitionPeriod, uint32_t& t_ResponseCode, qcc::String& t_RetrievedTransitionEffectID)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* p_InterfaceDescription = m_BusAttachment->GetInterface(TRANSITIONEFFECT_INTERFACE_NAME);
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
		msgArg[0].Set("s", t_TransitionEffectID);
		msgArg[1].Set("a{sv}", 5, t_LampState);
		msgArg[2].Set("s", t_PresetID);
		msgArg[3].Set("u", t_TransitionPeriod);

		ajn::Message responseMessage(*m_BusAttachment);
		CHECK_RETURN(proxyBusObj->MethodCall(TRANSITIONEFFECT_INTERFACE_NAME, "UpdateTransitionEffect", msgArg, 4, responseMessage))
		CHECK_RETURN(responseMessage->GetArg(0)->Get("u", &t_ResponseCode))

		char* tempRetrievedTransitionEffectID;
		CHECK_RETURN(responseMessage->GetArg(1)->Get("s", &tempRetrievedTransitionEffectID))
		t_RetrievedTransitionEffectID = qcc::String(tempRetrievedTransitionEffectID);
	}
	delete proxyBusObj;
	return status;
}