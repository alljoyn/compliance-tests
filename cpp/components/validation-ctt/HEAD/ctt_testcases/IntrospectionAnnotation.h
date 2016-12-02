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

class IntrospectionAnnotation : public IntrospectionBaseTag
{
public:
	std::string getValue() const;
	void setValue(std::string);
	int compareTo(IntrospectionAnnotation introspectionAnnotation);
	int hashCode();
	bool operator < (const IntrospectionAnnotation&) const;
	operator std::string() const;
private:
	std::string m_Value;
};