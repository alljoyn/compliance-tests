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

#include <string>
#include <alljoyn\AboutObjectDescription.h>
#include <alljoyn\Session.h>
#include <alljoyn/hae/HaeAboutData.h>

class InterfaceInfo
{
public:
	std::string busName;
	std::string deviceName;
	ajn::SessionPort sessionPort;
	ajn::SessionId sessionId;
	std::string objectPath;
	ajn::services::HaeAboutData aboutData;
	ajn::AboutObjectDescription aboutDescription;

	InterfaceInfo()
	{
	}
	InterfaceInfo(const char* name, ajn::SessionPort port, const char* path, ajn::services::HaeAboutData& data, ajn::AboutObjectDescription& description);
};