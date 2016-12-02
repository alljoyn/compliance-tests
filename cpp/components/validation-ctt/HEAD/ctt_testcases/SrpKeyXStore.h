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

class SrpKeyXStore
{
public:
	const char* getPincode(std::string);
	void setPincode(std::string, const char*);
private:
	std::map<std::string, const char*> m_PincodeStore;
};