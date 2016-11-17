/******************************************************************************
*    Copyright (c) Open Connectivity Foundation (OCF) and AllJoyn Open
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
#include "ControllerServiceSceneElementBusObject.h"

#define CHECK_BREAK(x) if ((status = x) != ER_OK) { break; }
#define CHECK_RETURN(x) if ((status = x) != ER_OK) { return status; }

static const char* CONTROLLERSERVICE_OBJECT_PATH = "/org/allseen/LSF/ControllerService";
static const char* SCENEELEMENT_INTERFACE_NAME = "org.allseen.LSF.ControllerService.SceneElement";

ControllerServiceSceneElementBusObject::ControllerServiceSceneElementBusObject(ajn::BusAttachment& t_BusAttachment, const std::string& t_BusName, const ajn::SessionId t_SessionId) :
m_BusAttachment(&t_BusAttachment), m_BusName(t_BusName), m_SessionId(t_SessionId),
m_SceneElementsAppliedSignalReceived(false),
m_SceneElementsNameChangedSignalReceived(false), m_SceneElementsCreatedSignalReceived(false),
m_SceneElementsUpdatedSignalReceived(false), m_SceneElementsDeletedSignalReceived(false)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* getIface = NULL;
	getIface = m_BusAttachment->GetInterface(SCENEELEMENT_INTERFACE_NAME);
	if (!getIface) {
		ajn::InterfaceDescription* createIface = NULL;
		status = m_BusAttachment->CreateInterface(SCENEELEMENT_INTERFACE_NAME, createIface, ajn::AJ_IFC_SECURITY_OFF);
		if (createIface && status == ER_OK) {
			do {
				CHECK_BREAK(createIface->AddMethod("ApplySceneElement", "s", "us", "sceneElementID,responseCode,sceneElementID"))
					CHECK_BREAK(createIface->AddMethod("CreateSceneElement", "asassss", "us", "lampIDs,lampGroupIDs,effectID,sceneElementName,language,responseCode,sceneElementID"))
					CHECK_BREAK(createIface->AddMethod("DeleteSceneElement", "s", "us", "sceneElementID,responseCode,sceneElementID"))
					CHECK_BREAK(createIface->AddMethod("GetAllSceneElementIDs", NULL, "uas", "responseCode,sceneElementIDs"))
					CHECK_BREAK(createIface->AddMethod("GetSceneElement", "s", "usasass", "sceneElementID,responseCode,sceneElementID,lampIDs,lampGroupIDs,effectID"))
					CHECK_BREAK(createIface->AddMethod("GetSceneElementName", "ss", "usss", "sceneElementID,language,responseCode,sceneElementID,language,sceneElementName"))
					CHECK_BREAK(createIface->AddMethod("SetSceneElementName", "sss", "uss", "sceneElementID,sceneElementName,language,responseCode,sceneElementID,language"))
					CHECK_BREAK(createIface->AddMethod("UpdateSceneElement", "sasass", "us", "sceneElementID,lampIDs,lampGroupIDs,effectID,responseCode,sceneElementID"))

					CHECK_BREAK(createIface->AddProperty("Version", "u", ajn::PROP_ACCESS_READ))

					CHECK_BREAK(createIface->AddSignal("SceneElementsApplied", "as", "sceneElementIDs", 0))
					CHECK_BREAK(createIface->AddSignal("SceneElementsCreated", "as", "sceneElementIDs", 0))
					CHECK_BREAK(createIface->AddSignal("SceneElementsUpdated", "as", "sceneElementIDs", 0))
					CHECK_BREAK(createIface->AddSignal("SceneElementsDeleted", "as", "sceneElementIDs", 0))
					CHECK_BREAK(createIface->AddSignal("SceneElementsNameChanged", "as", "sceneElementIDs", 0))
					createIface->Activate();

				const ajn::InterfaceDescription* uniqueId = m_BusAttachment->GetInterface(SCENEELEMENT_INTERFACE_NAME);
				const ajn::InterfaceDescription::Member* sig = uniqueId->GetMember("SceneElementsApplied");
				m_BusAttachment->RegisterSignalHandler(this, static_cast<ajn::MessageReceiver::SignalHandler>(&ControllerServiceSceneElementBusObject::SceneElementsAppliedSignalHandler), sig, CONTROLLERSERVICE_OBJECT_PATH);

				sig = uniqueId->GetMember("SceneElementsCreated");
				m_BusAttachment->RegisterSignalHandler(this, static_cast<ajn::MessageReceiver::SignalHandler>(&ControllerServiceSceneElementBusObject::SceneElementsCreatedSignalHandler), sig, CONTROLLERSERVICE_OBJECT_PATH);

				sig = uniqueId->GetMember("SceneElementsUpdated");
				m_BusAttachment->RegisterSignalHandler(this, static_cast<ajn::MessageReceiver::SignalHandler>(&ControllerServiceSceneElementBusObject::SceneElementsUpdatedSignalHandler), sig, CONTROLLERSERVICE_OBJECT_PATH);

				sig = uniqueId->GetMember("SceneElementsDeleted");
				m_BusAttachment->RegisterSignalHandler(this, static_cast<ajn::MessageReceiver::SignalHandler>(&ControllerServiceSceneElementBusObject::SceneElementsDeletedSignalHandler), sig, CONTROLLERSERVICE_OBJECT_PATH);

				sig = uniqueId->GetMember("SceneElementsNameChanged");
				m_BusAttachment->RegisterSignalHandler(this, static_cast<ajn::MessageReceiver::SignalHandler>(&ControllerServiceSceneElementBusObject::SceneElementsNameChangedSignalHandler), sig, CONTROLLERSERVICE_OBJECT_PATH);

				return;
			} while (0);
		} //if (createIface)
	} //if (!getIface)
}

void ControllerServiceSceneElementBusObject::SceneElementsAppliedSignalHandler(const ajn::InterfaceDescription::Member* member, const char* sourcePath, ajn::Message& msg)
{
	LOG(INFO) << "SceneElementsApplied signal received";
	m_SceneElementsAppliedSignalReceived = true;
}

bool ControllerServiceSceneElementBusObject::DidSceneElementsApplied()
{
	return m_SceneElementsAppliedSignalReceived;
}

void ControllerServiceSceneElementBusObject::SceneElementsCreatedSignalHandler(const ajn::InterfaceDescription::Member* member, const char* sourcePath, ajn::Message& msg)
{
	LOG(INFO) << "SceneElementsCreated signal received";
	m_SceneElementsCreatedSignalReceived = true;
}

bool ControllerServiceSceneElementBusObject::DidSceneElementsCreated()
{
	return m_SceneElementsCreatedSignalReceived;
}

void ControllerServiceSceneElementBusObject::SceneElementsUpdatedSignalHandler(const ajn::InterfaceDescription::Member* member, const char* sourcePath, ajn::Message& msg)
{
	LOG(INFO) << "SceneElementsUpdated signal received";
	m_SceneElementsUpdatedSignalReceived = true;
}

bool ControllerServiceSceneElementBusObject::DidSceneElementsUpdated()
{
	return m_SceneElementsUpdatedSignalReceived;
}

void ControllerServiceSceneElementBusObject::SceneElementsDeletedSignalHandler(const ajn::InterfaceDescription::Member* member, const char* sourcePath, ajn::Message& msg)
{
	LOG(INFO) << "SceneElementsDeleted signal received";
	m_SceneElementsDeletedSignalReceived = true;
}

bool ControllerServiceSceneElementBusObject::DidSceneElementsDeleted()
{
	return m_SceneElementsDeletedSignalReceived;
}

void ControllerServiceSceneElementBusObject::SceneElementsNameChangedSignalHandler(const ajn::InterfaceDescription::Member* member, const char* sourcePath, ajn::Message& msg)
{
	LOG(INFO) << "SceneElementsNameChanged signal received";
	m_SceneElementsNameChangedSignalReceived = true;
}

bool ControllerServiceSceneElementBusObject::DidSceneElementsNameChanged()
{
	return m_SceneElementsNameChangedSignalReceived;
}

QStatus ControllerServiceSceneElementBusObject::GetVersion(uint32_t& version)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* p_InterfaceDescription = m_BusAttachment->GetInterface(SCENEELEMENT_INTERFACE_NAME);
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
		CHECK_BREAK(proxyBusObj->GetProperty(SCENEELEMENT_INTERFACE_NAME, "Version", arg))
		version = arg.v_variant.val->v_uint32;
	} while (0);
	delete proxyBusObj;
	return status;
}

QStatus ControllerServiceSceneElementBusObject::ApplySceneElement(const char* t_SceneElementID, uint32_t& t_ResponseCode,
	qcc::String& t_RetrievedSceneElementID)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* p_InterfaceDescription = m_BusAttachment->GetInterface(SCENEELEMENT_INTERFACE_NAME);
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
		msgArg[0].Set("s", t_SceneElementID);
		ajn::Message responseMessage(*m_BusAttachment);
		CHECK_RETURN(proxyBusObj->MethodCall(SCENEELEMENT_INTERFACE_NAME, "ApplySceneElement", msgArg, 1, responseMessage))
		CHECK_RETURN(responseMessage->GetArg(0)->Get("u", &t_ResponseCode))

		char* tempRetrievedSceneElementID;
		CHECK_RETURN(responseMessage->GetArg(1)->Get("s", &tempRetrievedSceneElementID))
		t_RetrievedSceneElementID = qcc::String(tempRetrievedSceneElementID);
	}
	delete proxyBusObj;
	return status;
}

QStatus ControllerServiceSceneElementBusObject::CreateSceneElement(const std::vector<const char*>& t_LampIDs, const std::vector<const char*>& t_LampGroupIDs, const char* t_EffectID, const char* t_SceneElementName, const char* t_Language, uint32_t& t_ResponseCode, qcc::String& t_RetrievedSceneElementID)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* p_InterfaceDescription = m_BusAttachment->GetInterface(SCENEELEMENT_INTERFACE_NAME);
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
		msgArg[0].Set("as", t_LampIDs.size(), t_LampIDs.data());
		msgArg[1].Set("as", t_LampGroupIDs.size(), t_LampGroupIDs.data());
		msgArg[2].Set("s", t_EffectID);
		msgArg[3].Set("s", t_SceneElementName);
		msgArg[4].Set("s", t_Language);
		ajn::Message responseMessage(*m_BusAttachment);
		CHECK_RETURN(proxyBusObj->MethodCall(SCENEELEMENT_INTERFACE_NAME, "CreateSceneElement", msgArg, 5, responseMessage))
		CHECK_RETURN(responseMessage->GetArg(0)->Get("u", &t_ResponseCode))

		char* tempRetrievedSceneElementID;
		CHECK_RETURN(responseMessage->GetArg(1)->Get("s", &tempRetrievedSceneElementID))
		t_RetrievedSceneElementID = qcc::String(tempRetrievedSceneElementID);
	}
	delete proxyBusObj;
	return status;
}

QStatus ControllerServiceSceneElementBusObject::DeleteSceneElement(const char* t_SceneElementID, uint32_t& t_ResponseCode, qcc::String& t_RetrievedSceneElementID)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* p_InterfaceDescription = m_BusAttachment->GetInterface(SCENEELEMENT_INTERFACE_NAME);
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
		msgArg[0].Set("s", t_SceneElementID);

		ajn::Message responseMessage(*m_BusAttachment);
		CHECK_RETURN(proxyBusObj->MethodCall(SCENEELEMENT_INTERFACE_NAME, "DeleteSceneElement", msgArg, 1, responseMessage))
		CHECK_RETURN(responseMessage->GetArg(0)->Get("u", &t_ResponseCode))

		char* tempRetrievedSceneElementID;
		CHECK_RETURN(responseMessage->GetArg(1)->Get("s", &tempRetrievedSceneElementID))
		t_RetrievedSceneElementID = qcc::String(tempRetrievedSceneElementID);
	}
	delete proxyBusObj;
	return status;
}

QStatus ControllerServiceSceneElementBusObject::GetAllSceneElementIDs(uint32_t& t_ResponseCode, std::vector<qcc::String>& t_SceneElementIDs)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* p_InterfaceDescription = m_BusAttachment->GetInterface(SCENEELEMENT_INTERFACE_NAME);
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
		CHECK_RETURN(proxyBusObj->MethodCall(SCENEELEMENT_INTERFACE_NAME, "GetAllSceneElementIDs", NULL, 0, responseMessage))
			CHECK_RETURN(responseMessage->GetArg(0)->Get("u", &t_ResponseCode))

		size_t tempSceneElementIDsSize;
		char** tempSceneElementIDs;
		CHECK_RETURN(responseMessage->GetArg(1)->Get("as", &tempSceneElementIDsSize, &tempSceneElementIDs))

		t_SceneElementIDs.clear();
		for (size_t i = 0; i < tempSceneElementIDsSize; ++i)
		{
			t_SceneElementIDs.push_back(tempSceneElementIDs[i]);
		}
	}
	delete proxyBusObj;
	return status;
}

QStatus ControllerServiceSceneElementBusObject::GetSceneElement(const char* t_SceneElementID, uint32_t& t_ResponseCode, qcc::String& t_RetrievedSceneElementID, std::vector<qcc::String>& t_LampIDs, std::vector<qcc::String>& t_LampGroupIDs, qcc::String& t_EffectID)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* p_InterfaceDescription = m_BusAttachment->GetInterface(SCENEELEMENT_INTERFACE_NAME);
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
		msgArg[0].Set("s", t_SceneElementID);

		ajn::Message responseMessage(*m_BusAttachment);
		CHECK_RETURN(proxyBusObj->MethodCall(SCENEELEMENT_INTERFACE_NAME, "GetSceneElement", msgArg, 1, responseMessage))
			CHECK_RETURN(responseMessage->GetArg(0)->Get("u", &t_ResponseCode))

		char* tempRetrievedSceneElementID;
		CHECK_RETURN(responseMessage->GetArg(1)->Get("s", &tempRetrievedSceneElementID))
		t_RetrievedSceneElementID = qcc::String(tempRetrievedSceneElementID);

		size_t tempLampIDsSize;
		char** tempLampIDs;
		CHECK_RETURN(responseMessage->GetArg(2)->Get("as", &tempLampIDsSize, &tempLampIDs))

		t_LampIDs.clear();
		for (size_t i = 0; i < tempLampIDsSize; ++i)
		{
			t_LampIDs.push_back(tempLampIDs[i]);
		}

		size_t tempLampGroupIDsSize;
		char** tempLampGroupIDs;
		CHECK_RETURN(responseMessage->GetArg(3)->Get("as", &tempLampGroupIDsSize, &tempLampGroupIDs))

		t_LampGroupIDs.clear();
		for (size_t i = 0; i < tempLampGroupIDsSize; ++i)
		{
			t_LampGroupIDs.push_back(tempLampGroupIDs[i]);
		}

		char* tempEffectID;
		CHECK_RETURN(responseMessage->GetArg(4)->Get("s", &tempEffectID))
		t_EffectID = qcc::String(tempEffectID);
	}
	delete proxyBusObj;
	return status;
}

QStatus ControllerServiceSceneElementBusObject::GetSceneElementName(const char* t_SceneElementID, const char* t_Language, uint32_t& t_ResponseCode, qcc::String& t_RetrievedSceneElementID, qcc::String& t_RetrievedLanguage, qcc::String& t_SceneElementName)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* p_InterfaceDescription = m_BusAttachment->GetInterface(SCENEELEMENT_INTERFACE_NAME);
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
		msgArg[0].Set("s", t_SceneElementID);
		msgArg[1].Set("s", t_Language);

		ajn::Message responseMessage(*m_BusAttachment);
		CHECK_RETURN(proxyBusObj->MethodCall(SCENEELEMENT_INTERFACE_NAME, "GetSceneElementName", msgArg, 2, responseMessage))
		CHECK_RETURN(responseMessage->GetArg(0)->Get("u", &t_ResponseCode))

		char* tempRetrievedSceneElementID;
		CHECK_RETURN(responseMessage->GetArg(1)->Get("s", &tempRetrievedSceneElementID))
		t_RetrievedSceneElementID = qcc::String(tempRetrievedSceneElementID);

		char* tempRetrievedLanguage;
		CHECK_RETURN(responseMessage->GetArg(2)->Get("s", &tempRetrievedLanguage))
		t_RetrievedLanguage = qcc::String(tempRetrievedLanguage);

		char* tempSceneElementName;
		CHECK_RETURN(responseMessage->GetArg(3)->Get("s", &tempSceneElementName))
		t_SceneElementName = qcc::String(tempSceneElementName);
	}
	delete proxyBusObj;
	return status;
}

QStatus ControllerServiceSceneElementBusObject::SetSceneElementName(const char* t_SceneElementID, const char* t_SceneElementName, const char* t_Language, uint32_t& t_ResponseCode, qcc::String& t_RetrievedSceneElementID, qcc::String& t_RetrievedLanguage)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* p_InterfaceDescription = m_BusAttachment->GetInterface(SCENEELEMENT_INTERFACE_NAME);
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
		msgArg[0].Set("s", t_SceneElementID);
		msgArg[1].Set("s", t_SceneElementName);
		msgArg[2].Set("s", t_Language);

		ajn::Message responseMessage(*m_BusAttachment);
		CHECK_RETURN(proxyBusObj->MethodCall(SCENEELEMENT_INTERFACE_NAME, "SetSceneElementName", msgArg, 3, responseMessage))
		CHECK_RETURN(responseMessage->GetArg(0)->Get("u", &t_ResponseCode))

		char* tempRetrievedSceneElementID;
		CHECK_RETURN(responseMessage->GetArg(1)->Get("s", &tempRetrievedSceneElementID))
		t_RetrievedSceneElementID = qcc::String(tempRetrievedSceneElementID);

		char* tempRetrievedLanguage;
		CHECK_RETURN(responseMessage->GetArg(2)->Get("s", &tempRetrievedLanguage))
			t_RetrievedLanguage = qcc::String(tempRetrievedLanguage);
	}
	delete proxyBusObj;
	return status;
}

QStatus ControllerServiceSceneElementBusObject::UpdateSceneElement(const char* t_SceneElementID, const std::vector<const char*>& t_LampIDs, const std::vector<const char*>& t_LampGroupIDs, const char* t_EffectID, uint32_t& t_ResponseCode, qcc::String& t_RetrievedSceneElementID)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* p_InterfaceDescription = m_BusAttachment->GetInterface(SCENEELEMENT_INTERFACE_NAME);
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
		msgArg[0].Set("s", t_SceneElementID);
		msgArg[1].Set("as", t_LampIDs.size(), t_LampIDs.data());
		msgArg[2].Set("as", t_LampGroupIDs.size(), t_LampGroupIDs.data());
		msgArg[3].Set("s", t_EffectID);

		ajn::Message responseMessage(*m_BusAttachment);
		CHECK_RETURN(proxyBusObj->MethodCall(SCENEELEMENT_INTERFACE_NAME, "UpdateSceneElement", msgArg, 4, responseMessage))
		CHECK_RETURN(responseMessage->GetArg(0)->Get("u", &t_ResponseCode))

		char* tempRetrievedSceneElementID;
		CHECK_RETURN(responseMessage->GetArg(1)->Get("s", &tempRetrievedSceneElementID))
		t_RetrievedSceneElementID = qcc::String(tempRetrievedSceneElementID);
	}
	delete proxyBusObj;
	return status;
}