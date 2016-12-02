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

class IntrospectionBaseTag
{
public:
	std::string getName() const;
	void setName(std::string);
	int hashCode();
	bool operator == (const IntrospectionBaseTag&) const;
private:
	std::string m_Name;
};