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
#pragma once

#include <vector>

#include <alljoyn/BusAttachment.h>

class ControllerServiceLampGroupBusObject : public ajn::MessageReceiver
{
public:
	ControllerServiceLampGroupBusObject(ajn::BusAttachment&, const std::string&, const ajn::SessionId = 0);

	void LampGroupsNameChangedSignalHandler(const ajn::InterfaceDescription::Member* member, const char*, ajn::Message&);
	void LampGroupsCreatedSignalHandler(const ajn::InterfaceDescription::Member* member, const char*, ajn::Message&);
	void LampGroupsUpdatedSignalHandler(const ajn::InterfaceDescription::Member* member, const char*, ajn::Message&);
	void LampGroupsDeletedSignalHandler(const ajn::InterfaceDescription::Member* member, const char*, ajn::Message&);
	bool DidLampGroupsNameChanged();
	bool DidLampGroupsCreated();
	bool DidLampGroupsUpdated();
	bool DidLampGroupsDeleted();

	QStatus GetVersion(uint32_t&);

	QStatus GetAllLampGroupIDs(uint32_t&, std::vector<qcc::String>&);
	QStatus GetLampGroupName(const char*, const char*, uint32_t&, qcc::String&, qcc::String&, qcc::String&);
	QStatus SetLampGroupName(const char*, const char*, const char*, uint32_t&, qcc::String&, qcc::String&);
	QStatus CreateLampGroup(const std::vector<const char*>&, const std::vector<const char*>&, const char*, const char*, uint32_t&, qcc::String&);
	QStatus UpdateLampGroup(const char*, const std::vector<const char*>&, const std::vector<const char*>&, uint32_t&, qcc::String&);
	QStatus DeleteLampGroup(const char*, uint32_t&, qcc::String&);
	QStatus GetLampGroup(const char*, uint32_t&, qcc::String&, std::vector<qcc::String>&, std::vector<qcc::String>&);
	QStatus TransitionLampGroupState(const char*, ajn::MsgArg*, const uint32_t, uint32_t&, qcc::String&);
	QStatus PulseLampGroupWithState(const char*, ajn::MsgArg*, ajn::MsgArg*, const uint32_t, const uint32_t, const uint32_t, uint32_t&, qcc::String&);
	QStatus PulseLampGroupWithPreset(const char*, const char*, const char*, const uint32_t, const uint32_t, const uint32_t, uint32_t&, qcc::String&);
	QStatus TransitionLampGroupStateToPreset(const char*, const char*, const uint32_t, uint32_t&, qcc::String&);
	QStatus TransitionLampGroupStateField(const char*, const char*, ajn::MsgArg, const uint32_t, uint32_t&, qcc::String&, qcc::String&);
	QStatus ResetLampGroupState(const char*, uint32_t&, qcc::String&);
	QStatus ResetLampGroupStateField(const char*, const char*, uint32_t&, qcc::String&, qcc::String&);

private:
	ajn::BusAttachment* m_BusAttachment;
	std::string m_BusName;
	ajn::SessionId m_SessionId;

	bool m_LampGroupsNameChangedSignalReceived;
	bool m_LampGroupsCreatedSignalReceived;
	bool m_LampGroupsUpdatedSignalReceived;
	bool m_LampGroupsDeletedSignalReceived;
};