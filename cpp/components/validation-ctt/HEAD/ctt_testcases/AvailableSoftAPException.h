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

#include <stdexcept>

class AvailableSoftAPException : public std::runtime_error
{
public:
	AvailableSoftAPException() : std::runtime_error("") {};
	AvailableSoftAPException(const std::string& t_Message) : std::runtime_error(t_Message.c_str()) {};
};