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

#include "IntrospectionNode.h"
#include "IntrospectionXmlHandler.h"

#include <xercesc\sax2\SAX2XMLReader.hpp>

class IntrospectionXmlParser
{
public:
	~IntrospectionXmlParser();
	IntrospectionNode parseXML(std::string);
	IntrospectionNode parseXMLFile(std::string);

private:
	void parse(std::string, IntrospectionXmlHandler*, const bool);

	xercesc::SAX2XMLReader* t_Parser{ nullptr };
};