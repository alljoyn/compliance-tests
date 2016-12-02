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
#include "AboutAnnouncement.h"

using namespace ajn;
using namespace std;

AboutAnnouncement::AboutAnnouncement(const char* t_ServiceName, 
	const uint16_t t_Version, const uint16_t t_Port, const MsgArg& t_ObjectDescription, 
	const MsgArg& t_AboutData) : 
	m_Version(t_Version), m_ServiceName(t_ServiceName), m_Port(t_Port)
{

	m_ObjectDescription = new AboutObjectDescription();
	m_ObjectDescription->CreateFromMsgArg(t_ObjectDescription);

	m_AboutData = new AboutData(t_AboutData);
}

string AboutAnnouncement::getServiceName() const
{
	return m_ServiceName;
}

uint16_t AboutAnnouncement::getPort() const
{
	return m_Port;
}

AboutObjectDescription* AboutAnnouncement::getObjectDescriptions() const
{
	return m_ObjectDescription;
}

uint16_t AboutAnnouncement::getVersion() const
{
	return m_Version;
}

AboutData* AboutAnnouncement::getAboutData() const
{
	return m_AboutData;
}