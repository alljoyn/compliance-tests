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
#include "IntrospectionProperty.h"

using namespace std;

string IntrospectionProperty::getType() const
{
	return m_Type;
}

void IntrospectionProperty::setType(string t_Type)
{
	m_Type = t_Type;
}

string IntrospectionProperty::getAccess() const
{
	return m_Access;
}

void IntrospectionProperty::setAccess(string t_Access)
{
	m_Access = t_Access;
}

int IntrospectionProperty::compareTo(IntrospectionProperty t_IntrospectionProperty)
{
	return getName() == t_IntrospectionProperty.getName();
}

int IntrospectionProperty::hashCode()
{
	//COMPLETAR
	return 1;
}

bool IntrospectionProperty::operator < (const IntrospectionProperty& t_IntrospectionProperty) const
{
	if (getName().compare(t_IntrospectionProperty.getName()) < 0)
	{
		return true;
	}
	else if (getName().compare(t_IntrospectionProperty.getName()) > 0)
	{
		return false;
	}
	else if (getType().compare(t_IntrospectionProperty.getType()) < 0)
	{
		return true;
	}
	else if (getType().compare(t_IntrospectionProperty.getType()) > 0)
	{
		return false;
	}
	else if (getAccess().compare(t_IntrospectionProperty.getAccess()) < 0)
	{
		return true;
	}
	else
	{
		return false;
	}
}

IntrospectionProperty::operator std::string() const
{
	string stringToReturn;
	stringToReturn.append("Property [name=");
	stringToReturn.append(getName());
	stringToReturn.append(", type=");
	stringToReturn.append(getType());
	stringToReturn.append(", Access=");
	stringToReturn.append(getAccess());
	stringToReturn.append("]");

	return stringToReturn;
}