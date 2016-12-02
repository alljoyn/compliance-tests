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

class ArrayParser
{
public:
	static std::string parseAppId(const uint8_t*);
	static uint16_t stringToUint16(const char*);
	static uint8_t* parseAppIdFromString(const std::string&);
	static uint8_t* parseBytesFromHexString(const std::string&, size_t&);
};