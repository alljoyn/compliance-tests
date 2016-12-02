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
#include "NodeDetail.h"

using namespace std;

NodeDetail::NodeDetail(string t_Path, IntrospectionNode t_IntrospectionNode) :
m_Path(t_Path), m_IntrospectionNode(t_IntrospectionNode) {}

string NodeDetail::getPath()
{
	return m_Path;
}

IntrospectionNode NodeDetail::getIntrospectionNode()
{
	return m_IntrospectionNode;
}