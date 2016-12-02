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

class ValidationResult
{
public:
	ValidationResult(bool, const std::string&);
	bool isValid();
	std::string getFailureReason();
private:
	bool m_Valid;
	std::string m_FailureReason;
};