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

class ControllerServiceBusObject : public ajn::MessageReceiver
{
public:
	ControllerServiceBusObject(ajn::BusAttachment&, const std::string&, const ajn::SessionId = 0);

	void LightingResetSignalHandler(const ajn::InterfaceDescription::Member* member, const char* sourcePath, ajn::Message& msg);
	bool DidLightingReset();

	QStatus GetVersion(uint32_t&);

	QStatus LightingResetControllerService(uint32_t&);
	QStatus GetControllerServiceVersion(uint32_t&);
	
private:
	ajn::BusAttachment* m_BusAttachment;
	std::string m_BusName;
	ajn::SessionId m_SessionId;
	bool m_LightingResetSignalReceived;
};