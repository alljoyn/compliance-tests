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

#include <set>

#include "IntrospectionMethod.h"
#include "IntrospectionProperty.h"
#include "IntrospectionSignal.h"

class IntrospectionInterface : public IntrospectionBaseTag
{
public:
	std::set<IntrospectionMethod> getMethods();
	std::set<IntrospectionProperty> getProperties();
	std::set<IntrospectionSignal> getSignals();
	std::set<IntrospectionAnnotation> getAnnotations();
	int hashCode();
	//bool equals(object);
private:
	std::set<IntrospectionMethod> m_Methods;
	std::set<IntrospectionProperty> m_Properties;
	std::set<IntrospectionSignal> m_Signals;
	std::set<IntrospectionAnnotation> m_Annotations;
};