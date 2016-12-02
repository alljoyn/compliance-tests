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

class LampServiceBusObject
{
public:
	LampServiceBusObject(ajn::BusAttachment&, const std::string&, const ajn::SessionId = 0);

	QStatus GetVersion(uint32_t&);
	QStatus GetLampServiceVersion(uint32_t&);
	QStatus GetLampFaults(std::vector<uint32_t>&);

	QStatus ClearLampFault(const uint32_t, uint32_t&, uint32_t&);

private:
	ajn::BusAttachment* m_BusAttachment;
	std::string m_BusName;
	ajn::SessionId m_SessionId;
};