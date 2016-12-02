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

class ControllerServiceSceneBusObject : public ajn::MessageReceiver
{
public:
	ControllerServiceSceneBusObject(ajn::BusAttachment&, const std::string&, const ajn::SessionId = 0);

	void ScenesNameChangedSignalHandler(const ajn::InterfaceDescription::Member* member, const char*, ajn::Message&);
	void ScenesCreatedSignalHandler(const ajn::InterfaceDescription::Member* member, const char*, ajn::Message&);
	void ScenesUpdatedSignalHandler(const ajn::InterfaceDescription::Member* member, const char*, ajn::Message&);
	void ScenesDeletedSignalHandler(const ajn::InterfaceDescription::Member* member, const char*, ajn::Message&);
	void ScenesAppliedSignalHandler(const ajn::InterfaceDescription::Member* member, const char*, ajn::Message&);
	bool DidScenesNameChanged();
	bool DidScenesCreated();
	bool DidScenesUpdated();
	bool DidScenesDeleted();
	bool DidScenesApplied();

	QStatus GetVersion(uint32_t&);

	QStatus GetAllSceneIDs(uint32_t&, std::vector<qcc::String>&);
	QStatus GetSceneName(const char*, const char*, uint32_t&, qcc::String&, qcc::String&, qcc::String&);
	QStatus SetSceneName(const char*, const char*, const char*, uint32_t&, qcc::String&, qcc::String&);
	QStatus CreateScene(const std::vector<ajn::MsgArg>&, const std::vector<ajn::MsgArg>&, const std::vector<ajn::MsgArg>&, const std::vector<ajn::MsgArg>&, const char*, const char*, uint32_t&, qcc::String&);
	QStatus UpdateScene(const char*, const std::vector<ajn::MsgArg>&, const std::vector<ajn::MsgArg>&, const std::vector<ajn::MsgArg>&, const std::vector<ajn::MsgArg>&, uint32_t&, qcc::String&);
	QStatus DeleteScene(const char*, uint32_t&, qcc::String&);
	QStatus GetScene(const char*, uint32_t&, qcc::String&, std::vector<ajn::MsgArg>&, std::vector<ajn::MsgArg>&, std::vector<ajn::MsgArg>&, std::vector<ajn::MsgArg>&);
	QStatus ApplyScene(const char*, uint32_t&, qcc::String&);
	
private:
	ajn::BusAttachment* m_BusAttachment;
	std::string m_BusName;
	ajn::SessionId m_SessionId;
	bool m_ScenesNameChangedSignalReceived;
	bool m_ScenesCreatedSignalReceived;
	bool m_ScenesUpdatedSignalReceived;
	bool m_ScenesDeletedSignalReceived;
	bool m_ScenesAppliedSignalReceived;
};