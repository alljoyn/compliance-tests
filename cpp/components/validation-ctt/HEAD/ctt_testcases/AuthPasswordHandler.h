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

class AuthPasswordHandler
{
	public:
		virtual ~AuthPasswordHandler() {}
		virtual const char* getPassword(std::string) = 0;
		virtual void completed(std::string, std::string, bool) = 0;
};