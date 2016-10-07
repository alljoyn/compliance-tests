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
#pragma once

#include <vector>

#include <alljoyn/BusAttachment.h>

class ControllerServicePresetBusObject : public ajn::MessageReceiver
{
public:
	ControllerServicePresetBusObject(ajn::BusAttachment&, const std::string&, const ajn::SessionId = 0);

	void DefaultLampStateChangedSignalHandler(const ajn::InterfaceDescription::Member* member, const char*, ajn::Message&);
	void PresetNameChangedSignalHandler(const ajn::InterfaceDescription::Member* member, const char*, ajn::Message&);
	void PresetsCreatedSignalHandler(const ajn::InterfaceDescription::Member* member, const char*, ajn::Message&);
	void PresetsUpdatedSignalHandler(const ajn::InterfaceDescription::Member* member, const char*, ajn::Message&);
	void PresetsDeletedSignalHandler(const ajn::InterfaceDescription::Member* member, const char*, ajn::Message&);
	bool DidDefaultLampStateChanged();
	bool DidPresetNameChanged();
	bool DidPresetsCreated();
	bool DidPresetsUpdated();
	bool DidPresetsDeleted();

	QStatus GetVersion(uint32_t&);

	QStatus GetDefaultLampState(uint32_t&, std::vector<ajn::MsgArg>&);
	QStatus SetDefaultLampState(ajn::MsgArg*, uint32_t&);
	QStatus GetAllPresetIDs(uint32_t&, std::vector<qcc::String>&);
	QStatus GetPresetName(const char*, const char*, uint32_t&, qcc::String&, qcc::String&, qcc::String&);
	QStatus SetPresetName(const char*, const char*, const char*, uint32_t&, qcc::String&, qcc::String&);
	QStatus CreatePreset(ajn::MsgArg*, const char*, const char*, uint32_t&, qcc::String&);
	QStatus UpdatePreset(const char*, ajn::MsgArg*, uint32_t&, qcc::String&);
	QStatus DeletePreset(const char*, uint32_t&, qcc::String&);
	QStatus GetPreset(const char*, uint32_t&, qcc::String&, std::vector<ajn::MsgArg>&);

private:
	ajn::BusAttachment* m_BusAttachment;
	std::string m_BusName;
	ajn::SessionId m_SessionId;
	bool m_DefaultLampStateChangedSignalReceived;
	bool m_PresetNameChangedSignalReceived;
	bool m_PresetsCreatedSignalReceived;
	bool m_PresetsUpdatedSignalReceived;
	bool m_PresetsDeletedSignalReceived;
};