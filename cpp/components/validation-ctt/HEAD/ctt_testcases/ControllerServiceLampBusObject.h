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
#pragma once

#include <vector>

#include <alljoyn/BusAttachment.h>

class ControllerServiceLampBusObject : public ajn::MessageReceiver
{
public:
	ControllerServiceLampBusObject(ajn::BusAttachment&, const std::string&, const ajn::SessionId = 0);

	void LampNameChangedSignalHandler(const ajn::InterfaceDescription::Member* member, const char*, ajn::Message&);
	void LampStateChangedSignalHandler(const ajn::InterfaceDescription::Member*, const char*, ajn::Message&);
	void LampsFoundSignalHandler(const ajn::InterfaceDescription::Member*, const char*, ajn::Message&);
	void LampsLostSignalHandler(const ajn::InterfaceDescription::Member*, const char*, ajn::Message&);
	bool DidLampNameChanged();
	bool DidLampStateChanged();
	bool DidLampsFound();
	bool DidLampsLost();
	void ResetSignals();

	QStatus GetVersion(uint32_t&);

	QStatus GetAllLampIDs(uint32_t&, size_t&, std::vector<qcc::String>&);
	QStatus GetLampSupportedLanguages(const char*, uint32_t&, qcc::String&, std::vector<qcc::String>&);
	QStatus GetLampManufacturer(const char*, const char*, uint32_t&, qcc::String&, qcc::String&, qcc::String&);
	QStatus GetLampName(const char*, const char*, uint32_t&, qcc::String&, qcc::String&, qcc::String&);
	QStatus SetLampName(const char*, const char*, const char*, uint32_t&, qcc::String&, qcc::String&);
	QStatus GetLampDetails(const char*, uint32_t&, qcc::String&, std::vector<ajn::MsgArg>&);
	QStatus GetLampParameters(const char*, uint32_t&, qcc::String&, std::vector<ajn::MsgArg>&);
	QStatus GetLampParametersField(const char*, const char*, uint32_t&, qcc::String&, qcc::String&, qcc::String&);
	QStatus GetLampState(const char*, uint32_t&, qcc::String&, std::vector<ajn::MsgArg>&);
	QStatus GetLampStateField(const char*, const char*, uint32_t&, qcc::String&, qcc::String&, qcc::String&);
	QStatus TransitionLampState(const char*, ajn::MsgArg*, const uint32_t, uint32_t&, qcc::String&);
	QStatus TransitionLampStateField(const char*, const char*, ajn::MsgArg, const uint32_t, uint32_t&, qcc::String&, qcc::String&);
	QStatus PulseLampWithState(const char*, ajn::MsgArg*, ajn::MsgArg*, const uint32_t, const uint32_t, const uint32_t, uint32_t&, qcc::String&);
	QStatus PulseLampWithPreset(const char*, const char*, const char*, const uint32_t, const uint32_t, const uint32_t, uint32_t&, qcc::String&);
	QStatus TransitionLampStateToPreset(const char*, const char*, const uint32_t, uint32_t&, qcc::String&);
	QStatus ResetLampState(const char*, uint32_t&, qcc::String&);
	QStatus ResetLampStateField(const char*, const char*, uint32_t&, qcc::String&, qcc::String&);
	QStatus GetLampFaults(const char*, uint32_t&, qcc::String&, std::vector<uint32_t>&);
	QStatus ClearLampFault(const char*, const uint32_t, uint32_t&, qcc::String&, uint32_t&);
	QStatus GetLampServiceVersion(const char*, uint32_t&, qcc::String&, uint32_t&);
	
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