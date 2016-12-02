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
#include "InterfaceDetail.h"

using namespace std;

InterfaceDetail::InterfaceDetail(string t_Path, list<IntrospectionInterface> t_IntrospectionInterfaces) :
m_Path(t_Path), m_IntrospectionInterfaces(t_IntrospectionInterfaces) {}

string InterfaceDetail::getPath()
{
	return m_Path;
}

list<IntrospectionInterface> InterfaceDetail::getIntrospectionInterfaces()
{
	return m_IntrospectionInterfaces;
}