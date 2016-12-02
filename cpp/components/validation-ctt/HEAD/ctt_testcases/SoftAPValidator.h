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

#include <string>

class SoftAPValidator
{
public:
	static bool validateSoftAP(const std::string&);
private:
	static const char* SOFT_AP_ASSERT_MESSAGE;
	static const char* SOFT_AP_PREFIX;
	static const char* SOFT_AP_SUFFIX;

	static inline bool starts_with(const std::string&, const std::string&);
	static inline bool ends_with(const std::string&, const std::string&);
};