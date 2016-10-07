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

#include <alljoyn/BusAttachment.h>

class LampStateBusObject : ajn::MessageReceiver
{
public:
	LampStateBusObject(ajn::BusAttachment&, const std::string&, const ajn::SessionId = 0);

	void LampStateChangedSignalHandler(const ajn::InterfaceDescription::Member* member, const char* sourcePath, ajn::Message& msg);
	bool DidLampStateChanged();

	QStatus GetVersion(uint32_t&);
	QStatus GetOnOff(bool&);
	QStatus SetOnOff(const bool);
	QStatus GetHue(uint32_t&);
	QStatus SetHue(const uint32_t);
	QStatus GetSaturation(uint32_t&);
	QStatus SetSaturation(const uint32_t);
	QStatus GetColorTemp(uint32_t&);
	QStatus SetColorTemp(const uint32_t);
	QStatus GetBrightness(uint32_t&);
	QStatus SetBrightness(const uint32_t);

	QStatus TransitionLampState(const uint64_t, ajn::MsgArg*, const uint32_t, uint32_t&);
	QStatus ApplyPulseEffect(ajn::MsgArg*, ajn::MsgArg*, const uint32_t, const uint32_t, const uint32_t, const uint64_t, uint32_t&);

private:
	ajn::BusAttachment* m_BusAttachment;
	std::string m_BusName;
	ajn::SessionId m_SessionId;
	bool m_LampStateChangedSignalReceived;
};