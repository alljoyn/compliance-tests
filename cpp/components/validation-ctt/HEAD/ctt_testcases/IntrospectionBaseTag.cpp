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
#include "IntrospectionBaseTag.h"

using namespace std;

string IntrospectionBaseTag::getName() const
{
	return m_Name;
}

void IntrospectionBaseTag::setName(string t_Name)
{
	m_Name = t_Name;
}

int IntrospectionBaseTag::hashCode()
{
	//COMPLETAR
	return 1;
}

bool IntrospectionBaseTag::operator ==(const IntrospectionBaseTag& t_IntrospectionBaseTag) const
{
	if (this == &t_IntrospectionBaseTag)
	{
		return true;
	}

	if (getName().empty())
	{
		if (!t_IntrospectionBaseTag.getName().empty())
		{
			return false;
		}
	}
	else if (getName().compare(t_IntrospectionBaseTag.getName()) != 0)
	{
		return false;
	}

	return true;
}