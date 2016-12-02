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

#include "IntrospectionBaseTag.h"
#include "IntrospectionInterface.h"
#include "IntrospectionAnnotation.h"
#include "IntrospectionSubNode.h"

class IntrospectionNode : public IntrospectionBaseTag
{
public:
	std::list<IntrospectionInterface>* getInterfaces();
	std::list<IntrospectionSubNode>* getSubNodes();
	std::set<IntrospectionAnnotation>* getAnnotations();
private:
	std::list<IntrospectionInterface> m_Interfaces;
	std::list<IntrospectionSubNode> m_SubNodes;
	std::set<IntrospectionAnnotation> m_Annotations;
};