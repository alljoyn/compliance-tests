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
#include "stdafx.h"
#include "InterfaceInfo.h"

InterfaceInfo::InterfaceInfo(const char* name, ajn::SessionPort port, const char* path, ajn::services::HaeAboutData& data, ajn::AboutObjectDescription& description) :
	busName(name), sessionPort(port), sessionId(0), objectPath(path), aboutData(data), aboutDescription(description)
{
	ajn::MsgArg* arg;
	data.GetField("DeviceName", arg, "en");
	const char* bus_name;
	arg->Get("s", &bus_name);

	deviceName = bus_name;
}