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
#include "IntrospectionArg.h"

using namespace std;

string IntrospectionArg::getType()
{
	return m_Type;
}

void IntrospectionArg::setType(string t_Type)
{
	m_Type = t_Type;
}

string IntrospectionArg::getDirection()
{
	return m_Direction;
}

void IntrospectionArg::setDirection(string t_Direction)
{
	m_Direction = t_Direction;
}

int IntrospectionArg::hashCode()
{
	//COMPLETAR
	return 1;
}

//bool IntrospectionArg::equals(object t_object)
//{
//
//}

string IntrospectionArg::toString()
{
	string stringToReturn;
	stringToReturn.append("[Type=");
	stringToReturn.append(getType());
	stringToReturn.append("]");

	return stringToReturn;
}