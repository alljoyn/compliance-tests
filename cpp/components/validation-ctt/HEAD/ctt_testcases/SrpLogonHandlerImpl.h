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

#include "SrpLogonStore.h"

class SrpLogonHandlerImpl
{
public:
	SrpLogonHandlerImpl(SrpLogonStore*, const std::string&, const std::string&);
	virtual ~SrpLogonHandlerImpl() {}
	virtual const char* getUser(const std::string&);
	virtual const char* getPass(const std::string&);
	virtual void completed(std::string, std::string, bool);
	void resetAuthentication(std::string);
	bool isPeerAuthenticated(std::string);
	bool isPeerAuthenticationSuccessful(std::string);
protected:
	bool isTrueBoolean(bool);
private:
	SrpLogonStore* m_PasswordStore;
	std::map<std::string, bool> m_PeerAuthenticated;
	std::map<std::string, bool> m_PeerAuthenticationSuccessful;
	std::string m_DefaultUser = std::string{ "" };
	std::string m_DefaultPass = std::string{ "" };
};