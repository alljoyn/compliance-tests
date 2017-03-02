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
#pragma once

#include <vector>

#include <alljoyn/BusAttachment.h>

class ControllerServiceLampBusObject : public ajn::MessageReceiver
{
public:
	ControllerServiceLampBusObject(ajn::BusAttachment&, const std::string&, const ajn::SessionId = 0);

	void LampNameChangedSignalHandler(const ajn::InterfaceDescription::Member* member, AJ_PCSTR, ajn::Message&);
	void LampStateChangedSignalHandler(const ajn::InterfaceDescription::Member*, AJ_PCSTR, ajn::Message&);
	void LampsFoundSignalHandler(const ajn::InterfaceDescription::Member*, AJ_PCSTR, ajn::Message&);
	void LampsLostSignalHandler(const ajn::InterfaceDescription::Member*, AJ_PCSTR, ajn::Message&);
	bool DidLampNameChanged();
	bool DidLampStateChanged();
	bool DidLampsFound();
	bool DidLampsLost();
	void ResetSignals();

	QStatus GetVersion(uint32_t&);

	QStatus GetAllLampIDs(uint32_t&, size_t&, std::vector<qcc::String>&);
	QStatus GetLampSupportedLanguages(AJ_PCSTR, uint32_t&, qcc::String&, std::vector<qcc::String>&);
	QStatus GetLampManufacturer(AJ_PCSTR, AJ_PCSTR, uint32_t&, qcc::String&, qcc::String&, qcc::String&);
	QStatus GetLampName(AJ_PCSTR, AJ_PCSTR, uint32_t&, qcc::String&, qcc::String&, qcc::String&);
	QStatus SetLampName(AJ_PCSTR, AJ_PCSTR, AJ_PCSTR, uint32_t&, qcc::String&, qcc::String&);
	QStatus GetLampDetails(AJ_PCSTR, uint32_t&, qcc::String&, std::vector<ajn::MsgArg>&);
	QStatus GetLampParameters(AJ_PCSTR, uint32_t&, qcc::String&, std::vector<ajn::MsgArg>&);
	QStatus GetLampParametersField(AJ_PCSTR, AJ_PCSTR, uint32_t&, qcc::String&, qcc::String&, qcc::String&);
	QStatus GetLampState(AJ_PCSTR, uint32_t&, qcc::String&, std::vector<ajn::MsgArg>&);
	QStatus GetLampStateField(AJ_PCSTR, AJ_PCSTR, uint32_t&, qcc::String&, qcc::String&, qcc::String&);
	QStatus TransitionLampState(AJ_PCSTR, ajn::MsgArg*, uint32_t, uint32_t&, qcc::String&);
	QStatus TransitionLampStateField(AJ_PCSTR, AJ_PCSTR, ajn::MsgArg, uint32_t, uint32_t&, qcc::String&, qcc::String&);
	QStatus PulseLampWithState(AJ_PCSTR, ajn::MsgArg*, ajn::MsgArg*, uint32_t, uint32_t, uint32_t, uint32_t&, qcc::String&);
	QStatus PulseLampWithPreset(AJ_PCSTR, AJ_PCSTR, AJ_PCSTR, uint32_t, uint32_t, uint32_t, uint32_t&, qcc::String&);
	QStatus TransitionLampStateToPreset(AJ_PCSTR, AJ_PCSTR, uint32_t, uint32_t&, qcc::String&);
	QStatus ResetLampState(AJ_PCSTR, uint32_t&, qcc::String&);
	QStatus ResetLampStateField(AJ_PCSTR, AJ_PCSTR, uint32_t&, qcc::String&, qcc::String&);
	QStatus GetLampFaults(AJ_PCSTR, uint32_t&, qcc::String&, std::vector<uint32_t>&);
	QStatus ClearLampFault(AJ_PCSTR, uint32_t, uint32_t&, qcc::String&, uint32_t&);
	QStatus GetLampServiceVersion(AJ_PCSTR, uint32_t&, qcc::String&, uint32_t&);
	
private:
	ajn::BusAttachment* m_BusAttachment;
	std::string m_BusName;
	ajn::SessionId m_SessionId;
	bool m_LampNameChangedSignalReceived;
	bool m_LampStateChangedSignalReceived;
	bool m_LampsFoundSignalReceived;
	bool m_LampsLostSignalReceived;

	QStatus getParamValue(const ajn::MsgArg&, std::string&);
};