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
#include "SoftAPValidator.h"

const char* SoftAPValidator::SOFT_AP_ASSERT_MESSAGE = "Soft AP name must start with AJ_ or end with _AJ string";
const char* SoftAPValidator::SOFT_AP_PREFIX = "AJ_";
const char* SoftAPValidator::SOFT_AP_SUFFIX = "_AJ";

bool SoftAPValidator::validateSoftAP(const std::string& t_SoftApName)
{
	bool condition = starts_with(t_SoftApName, SOFT_AP_PREFIX) || ends_with(t_SoftApName, SOFT_AP_SUFFIX);
	EXPECT_TRUE(condition) << SOFT_AP_ASSERT_MESSAGE;
	return condition;
}

inline bool SoftAPValidator::starts_with(const std::string &t_Value, const std::string &t_Starting)
{
	if (t_Starting.size() > t_Value.size()) return false;
	return t_Value.find(t_Starting) == 0;
}

inline bool SoftAPValidator::ends_with(const std::string &t_Value, const std::string &t_Ending)
{
	if (t_Ending.size() > t_Value.size()) return false;
	return std::equal(t_Ending.rbegin(), t_Ending.rend(), t_Value.rbegin());
}