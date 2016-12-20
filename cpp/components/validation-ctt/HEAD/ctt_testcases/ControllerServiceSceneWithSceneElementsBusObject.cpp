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
#include "ControllerServiceSceneWithSceneElementsBusObject.h"

#define CHECK_BREAK(x) if ((status = x) != ER_OK) { break; }
#define CHECK_RETURN(x) if ((status = x) != ER_OK) { return status; }

static const char* CONTROLLERSERVICE_OBJECT_PATH = "/org/allseen/LSF/ControllerService";
static const char* SWSE_INTERFACE_NAME = "org.allseen.LSF.ControllerService.SceneWithSceneElements";

ControllerServiceSceneWithSceneElementsBusObject::ControllerServiceSceneWithSceneElementsBusObject(ajn::BusAttachment& t_BusAttachment, const std::string& t_BusName, const ajn::SessionId t_SessionId) :
m_BusAttachment(&t_BusAttachment), m_BusName(t_BusName), m_SessionId(t_SessionId)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* getIface = NULL;
	getIface = m_BusAttachment->GetInterface(SWSE_INTERFACE_NAME);
	if (!getIface) {
		ajn::InterfaceDescription* createIface = NULL;
		status = m_BusAttachment->CreateInterface(SWSE_INTERFACE_NAME, createIface, ajn::AJ_IFC_SECURITY_OFF);
		if (createIface && status == ER_OK) {
			do {
				CHECK_BREAK(createIface->AddMethod("CreateSceneWithSceneElements", "asss", "us", "sceneElementIDs,sceneName,language,responseCode,sceneID"))
				CHECK_BREAK(createIface->AddMethod("GetSceneWithSceneElements", "s", "usas", "sceneID,responseCode,sceneID,sceneElementIDs"))
				CHECK_BREAK(createIface->AddMethod("UpdateSceneWithSceneElements", "sas", "us", "sceneID,sceneElementIDs,responseCode,sceneID"))

				CHECK_BREAK(createIface->AddProperty("Version", "u", ajn::PROP_ACCESS_READ))
				createIface->Activate();

				return;
			} while (0);
		} //if (createIface)
	} //if (!getIface)
}

QStatus ControllerServiceSceneWithSceneElementsBusObject::GetVersion(uint32_t& version)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* p_InterfaceDescription = m_BusAttachment->GetInterface(SWSE_INTERFACE_NAME);
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
		CHECK_BREAK(proxyBusObj->GetProperty(SWSE_INTERFACE_NAME, "Version", arg))
		version = arg.v_variant.val->v_uint32;
	} while (0);
	delete proxyBusObj;
	return status;
}

QStatus ControllerServiceSceneWithSceneElementsBusObject::CreateSceneWithSceneElements(const std::vector<const char*>& t_SceneElementIDs, const char* t_SceneName, const char* t_Language, uint32_t& t_ResponseCode, qcc::String& t_SceneID)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* p_InterfaceDescription = m_BusAttachment->GetInterface(SWSE_INTERFACE_NAME);
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
		msgArg[0].Set("as", t_SceneElementIDs.size(), t_SceneElementIDs.data());
		msgArg[1].Set("s", t_SceneName);
		msgArg[2].Set("s", t_Language);
		ajn::Message responseMessage(*m_BusAttachment);
		CHECK_RETURN(proxyBusObj->MethodCall(SWSE_INTERFACE_NAME, "CreateSceneWithSceneElements", msgArg, 3, responseMessage))
		CHECK_RETURN(responseMessage->GetArg(0)->Get("u", &t_ResponseCode))

		char* tempSceneID;
		CHECK_RETURN(responseMessage->GetArg(1)->Get("s", &tempSceneID))
		t_SceneID = qcc::String(tempSceneID);
	}
	delete proxyBusObj;
	return status;
}

QStatus ControllerServiceSceneWithSceneElementsBusObject::GetSceneWithSceneElements(const char* t_SceneID, uint32_t& t_ResponseCode, qcc::String& t_RetrievedSceneID, std::vector<qcc::String>& t_SceneElementIDs)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* p_InterfaceDescription = m_BusAttachment->GetInterface(SWSE_INTERFACE_NAME);
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
		msgArg[0].Set("s", t_SceneID);
		ajn::Message responseMessage(*m_BusAttachment);
		CHECK_RETURN(proxyBusObj->MethodCall(SWSE_INTERFACE_NAME, "GetSceneWithSceneElements", msgArg, 1, responseMessage))
		CHECK_RETURN(responseMessage->GetArg(0)->Get("u", &t_ResponseCode))

		char* tempSceneID;
		CHECK_RETURN(responseMessage->GetArg(1)->Get("s", &tempSceneID))
		t_RetrievedSceneID = qcc::String(tempSceneID);

		size_t tempSceneElementIDsSize;
		char** tempSceneElementIDs;
		CHECK_RETURN(responseMessage->GetArg(2)->Get("as", &tempSceneElementIDsSize, &tempSceneElementIDs))

		t_SceneElementIDs.clear();
		for (size_t i = 0; i < tempSceneElementIDsSize; ++i)
		{
			t_SceneElementIDs.push_back(tempSceneElementIDs[i]);
		}
	}
	delete proxyBusObj;
	return status;
}

QStatus ControllerServiceSceneWithSceneElementsBusObject::UpdateSceneWithSceneElements(const char* t_SceneID, const std::vector<const char*>& t_SceneElementIDs, uint32_t& t_ResponseCode, qcc::String& t_RetrievedSceneID)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* p_InterfaceDescription = m_BusAttachment->GetInterface(SWSE_INTERFACE_NAME);
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
		msgArg[0].Set("s", t_SceneID);
		msgArg[1].Set("as", t_SceneElementIDs.size(), t_SceneElementIDs.data());
		
		ajn::Message responseMessage(*m_BusAttachment);
		CHECK_RETURN(proxyBusObj->MethodCall(SWSE_INTERFACE_NAME, "UpdateSceneWithSceneElements", msgArg, 2, responseMessage))
		CHECK_RETURN(responseMessage->GetArg(0)->Get("u", &t_ResponseCode))

		char* tempSceneID;
		CHECK_RETURN(responseMessage->GetArg(1)->Get("s", &tempSceneID))
		t_RetrievedSceneID = qcc::String(tempSceneID);
	}
	delete proxyBusObj;
	return status;
}