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

#include <map>

#include "SrpLogonHandlerImpl.h"
#include "SrpLogonStore.h"

#include <alljoyn\AuthListener.h>

class SrpLogonListener : public ajn::AuthListener
{
public:
	SrpLogonListener(SrpLogonHandlerImpl*, const std::string&, const std::string&);
	bool RequestCredentials(const char*, const char*, uint16_t, const char*, uint16_t, Credentials&);
	void AuthenticationComplete(const char*, const char*, bool);

private:
	SrpLogonHandlerImpl* m_PasswordHandler{ nullptr };
	std::string m_DefaultUser = std::string{ "" };
	std::string m_DefaultPass = std::string{ "" };
};