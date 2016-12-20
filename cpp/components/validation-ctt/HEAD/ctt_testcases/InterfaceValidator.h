/******************************************************************************
*    Copyright (c) Open Connectivity Foundation (OCF) and AllJoyn Open
*    Source Project (AJOSP) Contributors and others.
*
*    SPDX-License-Identifier: Apache-2.0
*
*    All rights reserved. This program and the accompanying materials are
*    made available under the terms of the Apache License, Version 2.0
*    which accompanies this distribution, and is available at
*    http://www.apache.org/licenses/LICENSE-2.0
*
*    Copyright (c) Open Connectivity Foundation and Contributors to AllSeen
*    Alliance. All rights reserved.
*
*    Permission to use, copy, modify, and/or distribute this software for
*    any purpose with or without fee is hereby granted, provided that the
*    above copyright notice and this permission notice appear in all
*    copies.
*
*    THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL
*    WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED
*    WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE
*    AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL
*    DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR
*    PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER
*    TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
*    PERFORMANCE OF THIS SOFTWARE.
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