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

class ControllerServiceSceneWithSceneElementsBusObject
{
public:
	ControllerServiceSceneWithSceneElementsBusObject(ajn::BusAttachment&, const std::string&, const ajn::SessionId = 0);

	QStatus GetVersion(uint32_t&);

	QStatus CreateSceneWithSceneElements(const std::vector<const char*>&, const char*, const char*, uint32_t&, qcc::String&);
	QStatus GetSceneWithSceneElements(const char*, uint32_t&, qcc::String&, std::vector<qcc::String>&);
	QStatus UpdateSceneWithSceneElements(const char*, const std::vector<const char*>&, uint32_t&, qcc::String&);

private:
	ajn::BusAttachment* m_BusAttachment;
	std::string m_BusName;
	ajn::SessionId m_SessionId;
};