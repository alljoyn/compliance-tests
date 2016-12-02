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