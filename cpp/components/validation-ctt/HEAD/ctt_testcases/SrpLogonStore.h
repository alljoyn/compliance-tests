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
#include <string>

class SrpLogonStore
{
public:
	const char* getUser(const std::string&);
	const char* getPass(const std::string&);
	void setUser(const std::string&, const char*);
	void setPass(const std::string&, const char*);
private:
	std::map<std::string, const char*> m_UserStore;
	std::map<std::string, const char*> m_PassStore;
};