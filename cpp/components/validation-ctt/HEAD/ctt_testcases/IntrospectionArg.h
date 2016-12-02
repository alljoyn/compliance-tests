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

#include "IntrospectionBaseTag.h"

class IntrospectionArg : public IntrospectionBaseTag
{
public:
	std::string getType();
	void setType(std::string);
	std::string getDirection();
	void setDirection(std::string);
	int hashCode();
	//bool equals(object);
	std::string toString();
private:
	std::string m_Type;
	std::string m_Direction;
};