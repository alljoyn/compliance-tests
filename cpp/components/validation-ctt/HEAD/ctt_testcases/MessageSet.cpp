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
#include "MessageSet.h"

MessageSet::MessageSet(std::string t_Text, ajn::services::NotificationMessageType t_Priority) :
	m_Text(t_Text), m_Priority(t_Priority) {}

std::string MessageSet::getText()
{
	return m_Text;
}

ajn::services::NotificationMessageType MessageSet::getPriority()
{
	return m_Priority;
}

std::string MessageSet::getPriorityAsString()
{
	switch (m_Priority)
	{
		case ajn::services::NotificationMessageType::INFO :
			return "INFO";
		case ajn::services::NotificationMessageType::WARNING :
			return "WARNING";
		case ajn::services::NotificationMessageType::EMERGENCY :
			return "EMERGENCY";
		default :
			return "";
	}
}