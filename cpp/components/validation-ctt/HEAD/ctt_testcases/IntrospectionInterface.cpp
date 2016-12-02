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
#include "stdafx.h"
#include "IntrospectionInterface.h"

using namespace std;

set<IntrospectionMethod> IntrospectionInterface::getMethods()
{
	return m_Methods;
}

set<IntrospectionProperty> IntrospectionInterface::getProperties()
{
	return m_Properties;
}

set<IntrospectionSignal> IntrospectionInterface::getSignals()
{
	return m_Signals;
}

set<IntrospectionAnnotation> IntrospectionInterface::getAnnotations()
{
	return m_Annotations;
}

int IntrospectionInterface::hashCode()
{
	//COMPLETAR
	return 1;
}

//bool equals(object t_object)
//{
//
//}