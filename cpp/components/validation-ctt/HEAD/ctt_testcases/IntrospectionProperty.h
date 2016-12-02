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

class IntrospectionProperty : public IntrospectionBaseTag
{
public:
	std::string getType() const;
	void setType(std::string);
	std::string getAccess() const;
	void setAccess(std::string);
	int compareTo(IntrospectionProperty);
	int hashCode();
	bool operator < (const IntrospectionProperty&) const;
	operator std::string() const;
private:
	std::string m_Type;
	std::string m_Access;
};