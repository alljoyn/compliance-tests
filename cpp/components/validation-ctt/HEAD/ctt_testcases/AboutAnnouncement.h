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

#include <alljoyn\AboutData.h>
#include <alljoyn\AboutObjectDescription.h>
#include <alljoyn\MsgArg.h>

class AboutAnnouncement
{
	public:
		AboutAnnouncement(const char*, const uint16_t, const uint16_t, const ajn::MsgArg&, const ajn::MsgArg&);
		std::string getServiceName() const;
		uint16_t getPort() const;
		ajn::AboutObjectDescription* getObjectDescriptions() const;
		ajn::AboutData* getAboutData() const;
		uint16_t getVersion() const;
	private:
		std::string m_ServiceName = std::string{ "" };
		uint16_t m_Version;
		uint16_t m_Port;
		ajn::AboutObjectDescription* m_ObjectDescription{ nullptr };
		ajn::AboutData* m_AboutData{ nullptr };
};