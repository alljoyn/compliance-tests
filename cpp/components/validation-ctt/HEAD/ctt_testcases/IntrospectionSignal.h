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

#include <list>

#include "IntrospectionBaseTag.h"
#include "IntrospectionArg.h"

class IntrospectionSignal : public IntrospectionBaseTag
{
public:
	std::list<IntrospectionArg> getArgs();
	void setArgs(std::list<IntrospectionArg>);
	int compareTo(IntrospectionSignal);
	int hashCode();
	bool operator < (const IntrospectionSignal&) const;
	operator std::string() const;
private:
	std::list<IntrospectionArg> m_Args;
};