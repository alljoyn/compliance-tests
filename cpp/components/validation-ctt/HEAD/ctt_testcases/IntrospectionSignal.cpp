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
#include "IntrospectionSignal.h"

using namespace std;

list<IntrospectionArg> IntrospectionSignal::getArgs()
{
	return m_Args;
}

int IntrospectionSignal::compareTo(IntrospectionSignal t_IntrospectionSignal)
{
	return getName() == t_IntrospectionSignal.getName();
}

int IntrospectionSignal::hashCode()
{
	//COMPLETAR
	return 1;
}

bool IntrospectionSignal::operator < (const IntrospectionSignal& t_IntrospectionSignal) const
{
	if (getName().compare(t_IntrospectionSignal.getName()) < 0)
	{
		return true;
	}
	else
	{
		return false;
	}

	// Args should be compared also.
}

IntrospectionSignal::operator std::string() const
{
	string stringToReturn;
	stringToReturn.append("Signal [name=");
	stringToReturn.append(getName());
	stringToReturn.append(", Args=");
	//stringToReturn.append(getArgs());
	stringToReturn.append("]");

	return stringToReturn;
}