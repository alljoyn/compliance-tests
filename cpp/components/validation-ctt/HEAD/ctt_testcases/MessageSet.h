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

#include <alljoyn\notification\NotificationEnums.h>

class MessageSet
{
public:
	MessageSet(std::string, ajn::services::NotificationMessageType);
	std::string getText();
	ajn::services::NotificationMessageType getPriority();
	std::string getPriorityAsString();
	
private:
	std::string m_Text = std::string{ "" };
	ajn::services::NotificationMessageType m_Priority;
};