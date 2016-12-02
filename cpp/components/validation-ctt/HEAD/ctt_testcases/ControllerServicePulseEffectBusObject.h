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

#include <vector>

#include <alljoyn/BusAttachment.h>

class ControllerServicePulseEffectBusObject : public ajn::MessageReceiver
{
public:
	ControllerServicePulseEffectBusObject(ajn::BusAttachment&, const std::string&, const ajn::SessionId = 0);

	void PulseEffectsNameChangedSignalHandler(const ajn::InterfaceDescription::Member* member, const char*, ajn::Message&);
	void PulseEffectsCreatedSignalHandler(const ajn::InterfaceDescription::Member* member, const char*, ajn::Message&);
	void PulseEffectsUpdatedSignalHandler(const ajn::InterfaceDescription::Member* member, const char*, ajn::Message&);
	void PulseEffectsDeletedSignalHandler(const ajn::InterfaceDescription::Member* member, const char*, ajn::Message&);
	bool DidPulseEffectsNameChanged();
	bool DidPulseEffectsCreated();
	bool DidPulseEffectsUpdated();
	bool DidPulseEffectsDeleted();

	QStatus GetVersion(uint32_t&);

	QStatus ApplyPulseEffectOnLampGroups(const char*, const std::vector<const char*>&, uint32_t&, qcc::String&, std::vector<qcc::String>&);
	QStatus ApplyPulseEffectOnLamps(const char*, const std::vector<const char*>&, uint32_t&, qcc::String&, std::vector<qcc::String>&);
	QStatus CreatePulseEffect(ajn::MsgArg*, const uint32_t, const uint32_t, const uint32_t, ajn::MsgArg*, const char*, const char*, const char*, const char*, uint32_t&, qcc::String&);
	QStatus DeletePulseEffect(const char*, uint32_t&, qcc::String&);
	QStatus GetAllPulseEffectIDs(uint32_t&, std::vector<qcc::String>&);
	QStatus GetPulseEffect(const char*, uint32_t&, qcc::String&, std::vector<ajn::MsgArg>&, uint32_t&, uint32_t&, uint32_t&, std::vector<ajn::MsgArg>&, qcc::String&, qcc::String&);
	QStatus GetPulseEffectName(const char*, const char*, uint32_t&, qcc::String&, qcc::String&, qcc::String&);
	QStatus SetPulseEffectName(const char*, const char*, const char*, uint32_t&, qcc::String&, qcc::String&);
	QStatus UpdatePulseEffect(const char*, ajn::MsgArg*, const uint32_t, const uint32_t, const uint32_t, ajn::MsgArg*, const char*, const char*, uint32_t&, qcc::String&);

private:
	ajn::BusAttachment* m_BusAttachment;
	std::string m_BusName;
	ajn::SessionId m_SessionId;
	bool m_PulseEffectsCreatedSignalReceived;
	bool m_PulseEffectsDeletedSignalReceived;
	bool m_PulseEffectsNameChangedSignalReceived;
	bool m_PulseEffectsUpdatedSignalReceived;
};