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

class LeaderElectionAndStateSyncBusObject : ajn::MessageReceiver
{
public:
	LeaderElectionAndStateSyncBusObject(ajn::BusAttachment&, const std::string&, const ajn::SessionId = 0);

	void BlobChangedSignalHandler(const ajn::InterfaceDescription::Member* member, const char*, ajn::Message&);
	bool DidBlobChanged();

	QStatus GetVersion(uint32_t&);

	QStatus GetChecksumAndModificationTimestamp(std::vector<ajn::MsgArg>&);
	QStatus GetBlob(const uint32_t, uint32_t&, qcc::String&, uint32_t&, uint64_t&);
	QStatus Overthrow(bool&);

private:
	ajn::BusAttachment* m_BusAttachment;
	std::string m_BusName;
	ajn::SessionId m_SessionId;
	bool m_BlobChangedSignalReceived;
};