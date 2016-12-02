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

#include "SrpKeyXHandlerImpl.h"
#include "SrpLogonHandlerImpl.h"
#include "ECDHENullHandlerImpl.h"
#include "ECDHEPskHandlerImpl.h"
#include "ECDHEEcdsaHandlerImpl.h"
#include "ECDHESpekeHandlerImpl.h"

#include <alljoyn\AuthListener.h>

class AuthListeners : public ajn::AuthListener
{
public:
	AuthListeners(bool, SrpKeyXHandlerImpl*, const std::string&,
		bool, SrpLogonHandlerImpl*, const std::string&, const std::string&,
		bool, ECDHENullHandlerImpl*, 
		bool, ECDHEPskHandlerImpl*, const std::string&, 
		bool, ECDHEEcdsaHandlerImpl*, const std::string&, const std::string&,
		bool, ECDHESpekeHandlerImpl*, const std::string&);
	bool RequestCredentials(const char*, const char*, uint16_t, const char*, uint16_t, Credentials&);
	void AuthenticationComplete(const char*, const char*, bool);
	std::vector<std::string> getAuthMechanisms();
	std::string getAuthMechanismsAsString();

private:
	std::map<std::string, ajn::AuthListener*> m_AuthListeners;
};