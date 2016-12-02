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
#include "IntrospectionAnnotation.h"

using namespace std;

string IntrospectionAnnotation::getValue() const
{
	return m_Value;
}

void IntrospectionAnnotation::setValue(string t_Value)
{
	m_Value = t_Value;
}

int IntrospectionAnnotation::compareTo(IntrospectionAnnotation t_IntrospectionAnnotation)
{
	return getName() == t_IntrospectionAnnotation.getName();
}

bool IntrospectionAnnotation::operator <(const IntrospectionAnnotation& t_IntrospectionAnnotation) const
{
	if (getName().compare(t_IntrospectionAnnotation.getName()) < 0)
	{
		return true;
	}
	else if (getName().compare(t_IntrospectionAnnotation.getName()) > 0)
	{
		return false;
	}
	else
	{
		return (getValue().compare(t_IntrospectionAnnotation.getValue()) < 0);
	}
}

IntrospectionAnnotation::operator std::string() const
{
	string stringToReturn;
	stringToReturn.append("Annotation [name=");
	stringToReturn.append(getName());
	stringToReturn.append(", Value=");
	stringToReturn.append(getValue());
	stringToReturn.append("]");

	return stringToReturn;
}