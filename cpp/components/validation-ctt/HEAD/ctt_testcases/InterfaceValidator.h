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

#include <list>

#include "InterfaceDetail.h"
#include "IntrospectionNode.h"
#include "IntrospectionXmlParser.h"
#include "ValidationResult.h"

class InterfaceValidator
{
public:
	ValidationResult validate(InterfaceDetail);
	ValidationResult validate(std::list<InterfaceDetail>);
	std::list<std::string> getXmlFilesToBeLoaded();

private:
	static const char* INTROSPECTION_XML_PATH;

	std::list<IntrospectionNode> m_IntrospectionNodesLoadedFromXmlFiles;
	IntrospectionXmlParser m_IntrospectionXmlParser;

	ValidationResult validateInterface(IntrospectionInterface, std::string);
	ValidationResult compare(InterfaceDetail, IntrospectionInterface, std::string);
	bool isVersionPropertyMissing(IntrospectionInterface, ValidationResult);
	bool isUndefinedMethodPresentForConfigInterface(IntrospectionInterface, ValidationResult);
	bool isAnnotationMissingForPeerAuthenticationInterface(IntrospectionInterface, ValidationResult);
	std::list<IntrospectionNode> getIntrospectionNodesLoadedFromXmlFiles();
	void buildIntrospectionNodesFromXmlFiles();
	InterfaceDetail* getInterfaceDetail(std::list<IntrospectionNode>, std::string);
	void appendToFailureReason(std::string&, const std::string&);
	std::string trim(const std::string&);
};