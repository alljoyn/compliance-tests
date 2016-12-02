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
#include <set>

#include "IntrospectionArg.h"
#include "IntrospectionAnnotation.h"

class IntrospectionMethod : public IntrospectionBaseTag
{
public:
	std::list<IntrospectionArg> getArgs() const;
	std::set<IntrospectionAnnotation> getAnnotations() const;
	int compareTo(IntrospectionMethod);
	int hashCode();
	bool operator < (const IntrospectionMethod&) const;
	operator std::string() const;
	bool operator == (const IntrospectionMethod&) const;
private:
	std::list<IntrospectionArg> m_Args;
	std::set<IntrospectionAnnotation> m_Annotations;
};