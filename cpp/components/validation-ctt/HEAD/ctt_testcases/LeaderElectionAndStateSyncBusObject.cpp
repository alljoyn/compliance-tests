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
#include "LeaderElectionAndStateSyncBusObject.h"

#define CHECK_BREAK(x) if ((status = x) != ER_OK) { break; }
#define CHECK_RETURN(x) if ((status = x) != ER_OK) { return status; }

static const char* CONTROLLERSERVICE_OBJECT_PATH = "/org/allseen/LeaderElectionAndStateSync";
static const char* CONTROLLERSERVICE_INTERFACE_NAME = "org.allseen.LeaderElectionAndStateSync";

LeaderElectionAndStateSyncBusObject::LeaderElectionAndStateSyncBusObject(ajn::BusAttachment& t_BusAttachment, const std::string& t_BusName, const ajn::SessionId t_SessionId) :
m_BusAttachment(&t_BusAttachment), m_BusName(t_BusName), m_SessionId(t_SessionId),
m_BlobChangedSignalReceived(false)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* getIface = NULL;
	getIface = m_BusAttachment->GetInterface(CONTROLLERSERVICE_INTERFACE_NAME);
	if (!getIface) {
		ajn::InterfaceDescription* createIface = NULL;
		status = m_BusAttachment->CreateInterface(CONTROLLERSERVICE_INTERFACE_NAME, createIface, ajn::AJ_IFC_SECURITY_OFF);
		if (createIface && status == ER_OK) {
			do {
				CHECK_BREAK(createIface->AddMethod("GetChecksumAndModificationTimestamp", NULL, "a(uut)", "checksumAndTimestamp"))
				CHECK_BREAK(createIface->AddMethod("GetBlob", "u", "usut", "blobType,blobType,blob,checksum,timestamp"))
				CHECK_BREAK(createIface->AddMethod("Overthrow", NULL, "b", "success"))
				CHECK_BREAK(createIface->AddProperty("Version", "u", ajn::PROP_ACCESS_READ))
				CHECK_BREAK(createIface->AddSignal("BlobChanged", "usut", "blobType,blob,checksum,timestamp", 0))
				createIface->Activate();

				const ajn::InterfaceDescription* uniqueId = m_BusAttachment->GetInterface(CONTROLLERSERVICE_INTERFACE_NAME);
				const ajn::InterfaceDescription::Member* sig = uniqueId->GetMember("BlobChanged");
				m_BusAttachment->RegisterSignalHandler(this, static_cast<ajn::MessageReceiver::SignalHandler>(&LeaderElectionAndStateSyncBusObject::BlobChangedSignalHandler), sig, CONTROLLERSERVICE_OBJECT_PATH);
				return;
			} while (0);
		} //if (createIface)
	} //if (!getIface)
}

void LeaderElectionAndStateSyncBusObject::BlobChangedSignalHandler(const ajn::InterfaceDescription::Member* member, const char* sourcePath, ajn::Message& msg)
{
	LOG(INFO) << "LightingReset signal received";
	m_BlobChangedSignalReceived = true;
}

bool LeaderElectionAndStateSyncBusObject::DidBlobChanged()
{
	return m_BlobChangedSignalReceived;
}

QStatus LeaderElectionAndStateSyncBusObject::GetVersion(uint32_t& t_Version)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* p_InterfaceDescription = m_BusAttachment->GetInterface(CONTROLLERSERVICE_INTERFACE_NAME);
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
		CHECK_BREAK(proxyBusObj->GetProperty(CONTROLLERSERVICE_INTERFACE_NAME, "Version", arg))
		t_Version = arg.v_variant.val->v_uint32;
	} while (0);
	delete proxyBusObj;
	return status;
}

QStatus LeaderElectionAndStateSyncBusObject::GetChecksumAndModificationTimestamp(std::vector<ajn::MsgArg>& t_ChecksumAndTimestamp)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* p_InterfaceDescription = m_BusAttachment->GetInterface(CONTROLLERSERVICE_INTERFACE_NAME);
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
		CHECK_RETURN(proxyBusObj->MethodCall(CONTROLLERSERVICE_INTERFACE_NAME, "GetChecksumAndModificationTimestamp", NULL, 0, responseMessage));

		size_t tempArraySize;
		ajn::MsgArg* tempArray;
		CHECK_RETURN(responseMessage->GetArg(0)->Get("a(uut)", &tempArraySize, &tempArray));

		t_ChecksumAndTimestamp.clear();
		for (size_t i = 0; i < tempArraySize; ++i)
		{
			t_ChecksumAndTimestamp.push_back(tempArray[i]);
		}
	}
	delete proxyBusObj;
	return status;
}

QStatus LeaderElectionAndStateSyncBusObject::GetBlob(const uint32_t t_BlobType, uint32_t& t_RetrievedBlobType, qcc::String& t_Blob, uint32_t& t_Checksum, uint64_t& t_Timestamp)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* p_InterfaceDescription = m_BusAttachment->GetInterface(CONTROLLERSERVICE_INTERFACE_NAME);
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
		msgArg[0].Set("u", t_BlobType);
		ajn::Message responseMessage(*m_BusAttachment);
		CHECK_RETURN(proxyBusObj->MethodCall(CONTROLLERSERVICE_INTERFACE_NAME, "GetBlob", msgArg, 1, responseMessage));
		CHECK_RETURN(responseMessage->GetArg(0)->Get("u", &t_RetrievedBlobType))

		char* tempBlob;
		CHECK_RETURN(responseMessage->GetArg(1)->Get("s", &tempBlob))
		t_Blob = qcc::String(tempBlob);

		CHECK_RETURN(responseMessage->GetArg(2)->Get("u", &t_Checksum))
		CHECK_RETURN(responseMessage->GetArg(3)->Get("t", &t_Timestamp))
	}
	delete proxyBusObj;
	return status;
}

QStatus LeaderElectionAndStateSyncBusObject::Overthrow(bool& t_Success)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* p_InterfaceDescription = m_BusAttachment->GetInterface(CONTROLLERSERVICE_INTERFACE_NAME);
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
		CHECK_RETURN(proxyBusObj->MethodCall(CONTROLLERSERVICE_INTERFACE_NAME, "Overthrow", NULL, 0, responseMessage));
		CHECK_RETURN(responseMessage->GetArg(0)->Get("b", &t_Success));
	}
	delete proxyBusObj;
	return status;
}