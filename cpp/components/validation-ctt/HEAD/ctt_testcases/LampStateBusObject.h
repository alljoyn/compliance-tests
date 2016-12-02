/******************************************************************************
* * 
*    Copyright (c) 2016 Open Connectivity Foundation and AllJoyn Open
*    Source Project Contributors and others.
*    
*    All rights reserved. This program and the accompanying materials are
*    made available under the terms of the Apache License, Version 2.0
*    which accompanies this distribution, and is available at
*    http://www.apache.org/licenses/LICENSE-2.0

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