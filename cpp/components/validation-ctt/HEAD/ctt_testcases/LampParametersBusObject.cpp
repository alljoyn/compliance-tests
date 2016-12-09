/******************************************************************************
* Copyright (c) 2016 Open Connectivity Foundation (OCF) and AllJoyn Open
*    Source Project (AJOSP) Contributors and others.
*
*    SPDX-License-Identifier: Apache-2.0
*
*    All rights reserved. This program and the accompanying materials are
*    made available under the terms of the Apache License, Version 2.0
*    which accompanies this distribution, and is available at
*    http://www.apache.org/licenses/LICENSE-2.0
*
*    Copyright 2016 Open Connectivity Foundation and Contributors to
*    AllSeen Alliance. All rights reserved.
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
#include "LampParametersBusObject.h"

#define CHECK_BREAK(x) if ((status = x) != ER_OK) { break; }
#define CHECK_RETURN(x) if ((status = x) != ER_OK) { return status; }

static const char* LAMPSERVICE_OBJECT_PATH = "/org/allseen/LSF/Lamp";
static const char* LAMPPARAMETERS_INTERFACE_NAME = "org.allseen.LSF.LampParameters";

LampParametersBusObject::LampParametersBusObject(ajn::BusAttachment& t_BusAttachment, const std::string& t_BusName, const ajn::SessionId t_SessionId) :
m_BusAttachment(&t_BusAttachment), m_BusName(t_BusName), m_SessionId(t_SessionId)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* getIface = NULL;
	getIface = m_BusAttachment->GetInterface(LAMPPARAMETERS_INTERFACE_NAME);
	if (!getIface) {
		ajn::InterfaceDescription* createIface = NULL;
		status = m_BusAttachment->CreateInterface(LAMPPARAMETERS_INTERFACE_NAME, createIface, ajn::AJ_IFC_SECURITY_OFF);
		if (createIface && status == ER_OK) {
			do {
				CHECK_BREAK(createIface->AddProperty("Version", "u", ajn::PROP_ACCESS_READ))
				CHECK_BREAK(createIface->AddProperty("Energy_Usage_Milliwatts", "u", ajn::PROP_ACCESS_READ))
				CHECK_BREAK(createIface->AddProperty("Brightness_Lumens", "u", ajn::PROP_ACCESS_READ))

				createIface->Activate();
				return;
			} while (0);
		} //if (createIface)
	} //if (!getIface)
}

QStatus LampParametersBusObject::GetVersion(uint32_t& t_Version)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* p_InterfaceDescription = m_BusAttachment->GetInterface(LAMPPARAMETERS_INTERFACE_NAME);
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
		CHECK_BREAK(proxyBusObj->GetProperty(LAMPPARAMETERS_INTERFACE_NAME, "Version", arg))
		t_Version = arg.v_variant.val->v_uint32;
	} while (0);
	delete proxyBusObj;
	return status;
}

QStatus LampParametersBusObject::GetEnergyUsageMilliwatts(uint32_t& t_EnergyUsageMilliwatts)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* p_InterfaceDescription = m_BusAttachment->GetInterface(LAMPPARAMETERS_INTERFACE_NAME);
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
		CHECK_BREAK(proxyBusObj->GetProperty(LAMPPARAMETERS_INTERFACE_NAME, "Energy_Usage_Milliwatts", arg))
		t_EnergyUsageMilliwatts = arg.v_variant.val->v_uint32;
	} while (0);
	delete proxyBusObj;
	return status;
}

QStatus LampParametersBusObject::GetBrightnessLumens(uint32_t& t_BrightnessLumens)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* p_InterfaceDescription = m_BusAttachment->GetInterface(LAMPPARAMETERS_INTERFACE_NAME);
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
		CHECK_BREAK(proxyBusObj->GetProperty(LAMPPARAMETERS_INTERFACE_NAME, "Brightness_Lumens", arg))
		t_BrightnessLumens = arg.v_variant.val->v_uint32;
	} while (0);
	delete proxyBusObj;
	return status;
}