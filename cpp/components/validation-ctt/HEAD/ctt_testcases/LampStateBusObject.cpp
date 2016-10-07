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
#include "LampStateBusObject.h"

#define CHECK_BREAK(x) if ((status = x) != ER_OK) { break; }
#define CHECK_RETURN(x) if ((status = x) != ER_OK) { return status; }

static const char* LAMPSERVICE_OBJECT_PATH = "/org/allseen/LSF/Lamp";
static const char* LAMPSTATE_INTERFACE_NAME = "org.allseen.LSF.LampState";

LampStateBusObject::LampStateBusObject(ajn::BusAttachment& t_BusAttachment, const std::string& t_BusName, const ajn::SessionId t_SessionId) :
m_BusAttachment(&t_BusAttachment), m_BusName(t_BusName), m_SessionId(t_SessionId),
m_LampStateChangedSignalReceived(false)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* getIface = NULL;
	getIface = m_BusAttachment->GetInterface(LAMPSTATE_INTERFACE_NAME);
	if (!getIface) {
		ajn::InterfaceDescription* createIface = NULL;
		status = m_BusAttachment->CreateInterface(LAMPSTATE_INTERFACE_NAME, createIface, ajn::AJ_IFC_SECURITY_OFF);
		if (createIface && status == ER_OK) {
			do {
				CHECK_BREAK(createIface->AddMethod("TransitionLampState", "ta{sv}u", "u", "Timestamp,NewState,TransitionPeriod,LampResponseCode"))
				CHECK_BREAK(createIface->AddMethod("ApplyPulseEffect", "a{sv}a{sv}uuut", "u", "FromState,ToState,period,duration,numPulses,timestamp,LampResponseCode"))
				CHECK_BREAK(createIface->AddProperty("Version", "u", ajn::PROP_ACCESS_READ))
				CHECK_BREAK(createIface->AddProperty("OnOff", "b", ajn::PROP_ACCESS_RW))
				CHECK_BREAK(createIface->AddProperty("Hue", "u", ajn::PROP_ACCESS_RW))
				CHECK_BREAK(createIface->AddProperty("Saturation", "u", ajn::PROP_ACCESS_RW))
				CHECK_BREAK(createIface->AddProperty("ColorTemp", "u", ajn::PROP_ACCESS_RW))
				CHECK_BREAK(createIface->AddProperty("Brightness", "u", ajn::PROP_ACCESS_RW))
				CHECK_BREAK(createIface->AddSignal("LampStateChanged", "s", "LampID"))
				createIface->Activate();

				const ajn::InterfaceDescription* uniqueId = m_BusAttachment->GetInterface(LAMPSTATE_INTERFACE_NAME);
				const ajn::InterfaceDescription::Member* sig = uniqueId->GetMember("LampStateChanged");
				m_BusAttachment->RegisterSignalHandler(this, static_cast<ajn::MessageReceiver::SignalHandler>(&LampStateBusObject::LampStateChangedSignalHandler), sig, LAMPSERVICE_OBJECT_PATH);
				return;
			} while (0);
		} //if (createIface)
	} //if (!getIface)
}

void LampStateBusObject::LampStateChangedSignalHandler(const ajn::InterfaceDescription::Member* member, const char* sourcePath, ajn::Message& msg)
{
	LOG(INFO) << "LampStateChanged signal received";
	m_LampStateChangedSignalReceived = true;
}

bool LampStateBusObject::DidLampStateChanged()
{
	return m_LampStateChangedSignalReceived;
}

QStatus LampStateBusObject::GetVersion(uint32_t& t_Version)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* p_InterfaceDescription = m_BusAttachment->GetInterface(LAMPSTATE_INTERFACE_NAME);
	if (!p_InterfaceDescription) {
		return ER_FAIL;
	}

	ajn::ProxyBusObject* proxyBusObj = new ajn::ProxyBusObject(*m_BusAttachment, m_BusName.c_str(), LAMPSERVICE_OBJECT_PATH, m_SessionId);
	if (!proxyBusObj) {
		return ER_FAIL;
	}
	do {
		ajn::MsgArg arg;
		CHECK_BREAK(proxyBusObj->AddInterface(*p_InterfaceDescription))
		CHECK_BREAK(proxyBusObj->GetProperty(LAMPSTATE_INTERFACE_NAME, "Version", arg))
		t_Version = arg.v_variant.val->v_uint32;
	} while (0);
	delete proxyBusObj;
	return status;
}

QStatus LampStateBusObject::GetOnOff(bool& t_OnOff)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* p_InterfaceDescription = m_BusAttachment->GetInterface(LAMPSTATE_INTERFACE_NAME);
	if (!p_InterfaceDescription) {
		return ER_FAIL;
	}

	ajn::ProxyBusObject* proxyBusObj = new ajn::ProxyBusObject(*m_BusAttachment, m_BusName.c_str(), LAMPSERVICE_OBJECT_PATH, m_SessionId);
	if (!proxyBusObj) {
		return ER_FAIL;
	}
	do {
		ajn::MsgArg arg;
		CHECK_BREAK(proxyBusObj->AddInterface(*p_InterfaceDescription))
		CHECK_BREAK(proxyBusObj->GetProperty(LAMPSTATE_INTERFACE_NAME, "OnOff", arg))
		t_OnOff = arg.v_variant.val->v_bool;
	} while (0);
	delete proxyBusObj;
	return status;
}

QStatus LampStateBusObject::SetOnOff(const bool t_OnOff)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* p_InterfaceDescription = m_BusAttachment->GetInterface(LAMPSTATE_INTERFACE_NAME);
	if (!p_InterfaceDescription) {
		return ER_FAIL;
	}

	ajn::ProxyBusObject* proxyBusObj = new ajn::ProxyBusObject(*m_BusAttachment, m_BusName.c_str(), LAMPSERVICE_OBJECT_PATH, m_SessionId);
	if (!proxyBusObj) {
		return ER_FAIL;
	}
	do {
		ajn::MsgArg arg;
		arg.Set("b", t_OnOff);
		CHECK_BREAK(proxyBusObj->AddInterface(*p_InterfaceDescription))
		CHECK_BREAK(proxyBusObj->SetProperty(LAMPSTATE_INTERFACE_NAME, "OnOff", arg))
	} while (0);
	delete proxyBusObj;
	return status;
}

QStatus LampStateBusObject::GetHue(uint32_t& t_Hue)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* p_InterfaceDescription = m_BusAttachment->GetInterface(LAMPSTATE_INTERFACE_NAME);
	if (!p_InterfaceDescription) {
		return ER_FAIL;
	}

	ajn::ProxyBusObject* proxyBusObj = new ajn::ProxyBusObject(*m_BusAttachment, m_BusName.c_str(), LAMPSERVICE_OBJECT_PATH, m_SessionId);
	if (!proxyBusObj) {
		return ER_FAIL;
	}
	do {
		ajn::MsgArg arg;
		CHECK_BREAK(proxyBusObj->AddInterface(*p_InterfaceDescription))
		CHECK_BREAK(proxyBusObj->GetProperty(LAMPSTATE_INTERFACE_NAME, "Hue", arg))
		t_Hue = arg.v_variant.val->v_uint32;
	} while (0);
	delete proxyBusObj;
	return status;
}

QStatus LampStateBusObject::SetHue(const uint32_t t_Hue)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* p_InterfaceDescription = m_BusAttachment->GetInterface(LAMPSTATE_INTERFACE_NAME);
	if (!p_InterfaceDescription) {
		return ER_FAIL;
	}

	ajn::ProxyBusObject* proxyBusObj = new ajn::ProxyBusObject(*m_BusAttachment, m_BusName.c_str(), LAMPSERVICE_OBJECT_PATH, m_SessionId);
	if (!proxyBusObj) {
		return ER_FAIL;
	}
	do {
		ajn::MsgArg arg;
		arg.Set("u", t_Hue);
		CHECK_BREAK(proxyBusObj->AddInterface(*p_InterfaceDescription))
		CHECK_BREAK(proxyBusObj->SetProperty(LAMPSTATE_INTERFACE_NAME, "Hue", arg))
	} while (0);
	delete proxyBusObj;
	return status;
}

QStatus LampStateBusObject::GetSaturation(uint32_t& t_Saturation)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* p_InterfaceDescription = m_BusAttachment->GetInterface(LAMPSTATE_INTERFACE_NAME);
	if (!p_InterfaceDescription) {
		return ER_FAIL;
	}

	ajn::ProxyBusObject* proxyBusObj = new ajn::ProxyBusObject(*m_BusAttachment, m_BusName.c_str(), LAMPSERVICE_OBJECT_PATH, m_SessionId);
	if (!proxyBusObj) {
		return ER_FAIL;
	}
	do {
		ajn::MsgArg arg;
		CHECK_BREAK(proxyBusObj->AddInterface(*p_InterfaceDescription))
		CHECK_BREAK(proxyBusObj->GetProperty(LAMPSTATE_INTERFACE_NAME, "Saturation", arg))
		t_Saturation = arg.v_variant.val->v_uint32;
	} while (0);
	delete proxyBusObj;
	return status;
}

QStatus LampStateBusObject::SetSaturation(const uint32_t t_Saturation)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* p_InterfaceDescription = m_BusAttachment->GetInterface(LAMPSTATE_INTERFACE_NAME);
	if (!p_InterfaceDescription) {
		return ER_FAIL;
	}

	ajn::ProxyBusObject* proxyBusObj = new ajn::ProxyBusObject(*m_BusAttachment, m_BusName.c_str(), LAMPSERVICE_OBJECT_PATH, m_SessionId);
	if (!proxyBusObj) {
		return ER_FAIL;
	}
	do {
		ajn::MsgArg arg;
		arg.Set("u", t_Saturation);
		CHECK_BREAK(proxyBusObj->AddInterface(*p_InterfaceDescription))
		CHECK_BREAK(proxyBusObj->SetProperty(LAMPSTATE_INTERFACE_NAME, "Saturation", arg))
	} while (0);
	delete proxyBusObj;
	return status;
}

QStatus LampStateBusObject::GetColorTemp(uint32_t& t_ColorTemp)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* p_InterfaceDescription = m_BusAttachment->GetInterface(LAMPSTATE_INTERFACE_NAME);
	if (!p_InterfaceDescription) {
		return ER_FAIL;
	}

	ajn::ProxyBusObject* proxyBusObj = new ajn::ProxyBusObject(*m_BusAttachment, m_BusName.c_str(), LAMPSERVICE_OBJECT_PATH, m_SessionId);
	if (!proxyBusObj) {
		return ER_FAIL;
	}
	do {
		ajn::MsgArg arg;
		CHECK_BREAK(proxyBusObj->AddInterface(*p_InterfaceDescription))
		CHECK_BREAK(proxyBusObj->GetProperty(LAMPSTATE_INTERFACE_NAME, "ColorTemp", arg))
		t_ColorTemp = arg.v_variant.val->v_uint32;
	} while (0);
	delete proxyBusObj;
	return status;
}

QStatus LampStateBusObject::SetColorTemp(const uint32_t t_ColorTemp)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* p_InterfaceDescription = m_BusAttachment->GetInterface(LAMPSTATE_INTERFACE_NAME);
	if (!p_InterfaceDescription) {
		return ER_FAIL;
	}

	ajn::ProxyBusObject* proxyBusObj = new ajn::ProxyBusObject(*m_BusAttachment, m_BusName.c_str(), LAMPSERVICE_OBJECT_PATH, m_SessionId);
	if (!proxyBusObj) {
		return ER_FAIL;
	}
	do {
		ajn::MsgArg arg;
		arg.Set("u", t_ColorTemp);
		CHECK_BREAK(proxyBusObj->AddInterface(*p_InterfaceDescription))
		CHECK_BREAK(proxyBusObj->SetProperty(LAMPSTATE_INTERFACE_NAME, "ColorTemp", arg))
	} while (0);
	delete proxyBusObj;
	return status;
}

QStatus LampStateBusObject::GetBrightness(uint32_t& t_Brightness)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* p_InterfaceDescription = m_BusAttachment->GetInterface(LAMPSTATE_INTERFACE_NAME);
	if (!p_InterfaceDescription) {
		return ER_FAIL;
	}

	ajn::ProxyBusObject* proxyBusObj = new ajn::ProxyBusObject(*m_BusAttachment, m_BusName.c_str(), LAMPSERVICE_OBJECT_PATH, m_SessionId);
	if (!proxyBusObj) {
		return ER_FAIL;
	}
	do {
		ajn::MsgArg arg;
		CHECK_BREAK(proxyBusObj->AddInterface(*p_InterfaceDescription))
		CHECK_BREAK(proxyBusObj->GetProperty(LAMPSTATE_INTERFACE_NAME, "Brightness", arg))
		t_Brightness = arg.v_variant.val->v_uint32;
	} while (0);
	delete proxyBusObj;
	return status;
}

QStatus LampStateBusObject::SetBrightness(const uint32_t t_Brightness)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* p_InterfaceDescription = m_BusAttachment->GetInterface(LAMPSTATE_INTERFACE_NAME);
	if (!p_InterfaceDescription) {
		return ER_FAIL;
	}

	ajn::ProxyBusObject* proxyBusObj = new ajn::ProxyBusObject(*m_BusAttachment, m_BusName.c_str(), LAMPSERVICE_OBJECT_PATH, m_SessionId);
	if (!proxyBusObj) {
		return ER_FAIL;
	}
	do {
		ajn::MsgArg arg;
		arg.Set("u", t_Brightness);
		CHECK_BREAK(proxyBusObj->AddInterface(*p_InterfaceDescription))
		CHECK_BREAK(proxyBusObj->SetProperty(LAMPSTATE_INTERFACE_NAME, "Brightness", arg))
	} while (0);
	delete proxyBusObj;
	return status;
}

QStatus LampStateBusObject::TransitionLampState(const uint64_t t_Timestamp, ajn::MsgArg* t_NewState, const uint32_t t_TransitionPeriod, uint32_t& t_LampResponseCode)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* p_InterfaceDescription = m_BusAttachment->GetInterface(LAMPSTATE_INTERFACE_NAME);
	if (!p_InterfaceDescription) {
		return ER_FAIL;
	}

	ajn::ProxyBusObject* proxyBusObj = new ajn::ProxyBusObject(*m_BusAttachment, m_BusName.c_str(), LAMPSERVICE_OBJECT_PATH, m_SessionId);
	if (!proxyBusObj) {
		return ER_FAIL;
	}
	status = proxyBusObj->AddInterface(*p_InterfaceDescription);
	if (status == ER_OK) {
		ajn::MsgArg* msgArg = new ajn::MsgArg[3];
		msgArg[0].Set("t", t_Timestamp);
		msgArg[1].Set("a{sv}", 5, t_NewState);
		msgArg[2].Set("u", t_TransitionPeriod);

		ajn::Message responseMessage(*m_BusAttachment);
		CHECK_RETURN(proxyBusObj->MethodCall(LAMPSTATE_INTERFACE_NAME, "TransitionLampState", msgArg, 3, responseMessage))

		status = responseMessage->GetArg(0)->Get("u", &t_LampResponseCode);
	}
	delete proxyBusObj;
	return status;
}

QStatus LampStateBusObject::ApplyPulseEffect(ajn::MsgArg* t_FromState, ajn::MsgArg* t_ToState, const uint32_t t_Period, const uint32_t t_Duration, const uint32_t t_NumPulses, const uint64_t t_Timestamp, uint32_t& t_LampResponseCode)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* p_InterfaceDescription = m_BusAttachment->GetInterface(LAMPSTATE_INTERFACE_NAME);
	if (!p_InterfaceDescription) {
		return ER_FAIL;
	}

	ajn::ProxyBusObject* proxyBusObj = new ajn::ProxyBusObject(*m_BusAttachment, m_BusName.c_str(), LAMPSERVICE_OBJECT_PATH, m_SessionId);
	if (!proxyBusObj) {
		return ER_FAIL;
	}
	status = proxyBusObj->AddInterface(*p_InterfaceDescription);
	if (status == ER_OK) {
		ajn::MsgArg* msgArg = new ajn::MsgArg[6];
		msgArg[0].Set("a{sv}", 5, t_FromState);
		msgArg[1].Set("a{sv}", 5, t_ToState);
		msgArg[2].Set("u", t_Period);
		msgArg[3].Set("u", t_Duration);
		msgArg[4].Set("u", t_NumPulses);
		msgArg[5].Set("t", t_Timestamp);

		ajn::Message responseMessage(*m_BusAttachment);
		CHECK_RETURN(proxyBusObj->MethodCall(LAMPSTATE_INTERFACE_NAME, "ApplyPulseEffect", msgArg, 6, responseMessage));

		status = responseMessage->GetArg(0)->Get("u", &t_LampResponseCode);
	}
	delete proxyBusObj;
	return status;
}