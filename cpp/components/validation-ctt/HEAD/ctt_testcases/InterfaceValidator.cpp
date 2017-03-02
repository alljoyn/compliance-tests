/******************************************************************************
*    Copyright (c) Open Connectivity Foundation (OCF), AllJoyn Open Source
*    Project (AJOSP) Contributors and others.
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
#include "stdafx.h"
#include "InterfaceValidator.h"
#include "SetValidator.h"

using namespace std;

#if _DEBUG
AJ_PCSTR InterfaceValidator::INTROSPECTION_XML_PATH = "../lib/";
#else
AJ_PCSTR InterfaceValidator::INTROSPECTION_XML_PATH = "lib/";
#endif

ValidationResult InterfaceValidator::validate(InterfaceDetail t_InterfaceDetail)
{
	string failureReason;
	bool valid = true;

	for (auto standardizedIntrospectionInterface : t_InterfaceDetail.getIntrospectionInterfaces())
	{
		ValidationResult validationResult = validateInterface(standardizedIntrospectionInterface, t_InterfaceDetail.getPath());

		if (!validationResult.isValid())
		{
			valid = false;
			appendToFailureReason(failureReason, validationResult.getFailureReason());
		}
	}

	return ValidationResult(valid, failureReason);
}

ValidationResult InterfaceValidator::validate(list<InterfaceDetail> t_InterfaceDetailList)
{
	string failureReason;
	bool valid = true;

	for (auto interfaceDetail : t_InterfaceDetailList)
	{
		ValidationResult validationResult = validate(interfaceDetail);

		if (!validationResult.isValid())
		{
			valid = false;
			appendToFailureReason(failureReason, validationResult.getFailureReason());
		}
		else
		{
			LOG(INFO) << "Object found at " << interfaceDetail.getPath() << " is correct";
		}
	}

	return ValidationResult(valid, failureReason);
}

list<string> InterfaceValidator::getXmlFilesToBeLoaded()
{
	AJ_PCSTR x[] = { "introspection-xml/About.xml", "introspection-xml/Action.xml", "introspection-xml/Audio.xml",
		"introspection-xml/Config.xml", "introspection-xml/Container.xml", "introspection-xml/ControlPanel.xml",
		"introspection-xml/DeviceIcon.xml", "introspection-xml/Dialog.xml", "introspection-xml/HTTPControl.xml",
		"introspection-xml/LabelProperty.xml", "introspection-xml/ListProperty.xml", "introspection-xml/Notification.xml",
		"introspection-xml/NotificationAction.xml", "introspection-xml/Onboarding.xml", "introspection-xml/org.alljoyn.Bus/Peer.xml",
		"introspection-xml/Property.xml", "introspection-xml/Lamp.xml", "introspection-xml/GWAgentCtrlAcl.xml",
		"introspection-xml/GWAgentCtrlAclMgmt.xml", "introspection-xml/GWAgentCtrlApp.xml", "introspection-xml/GWAgentCtrlAppMgmt.xml",
		"introspection-xml/ControllerService.xml", "introspection-xml/LeaderElectionAndStateSync.xml", "introspection-xml/Introspectable.xml",
		"introspection-xml/GWAgentConnectorApp.xml", "introspection-xml/org.alljoyn.Bus/Application-v1.xml",
		"introspection-xml/org.alljoyn.Bus.Security/Application-v1.xml",
		"introspection-xml/org.alljoyn.Bus.Security/ClaimableApplication-v1.xml",
		"introspection-xml/org.alljoyn.Bus.Security/ManagedApplication-v1.xml" };

	return list<string>(x, x + sizeof(x) / sizeof(*x));
}

ValidationResult InterfaceValidator::validateInterface(IntrospectionInterface t_StandardizedIntrospectionInterface,
	string t_Path)
{
	string failureReason("No interface detail for ");
	failureReason.append(t_StandardizedIntrospectionInterface.getName());
	ValidationResult validationResult(false, failureReason);

	InterfaceDetail* interfaceDetail = getInterfaceDetail(getIntrospectionNodesLoadedFromXmlFiles(),
		t_StandardizedIntrospectionInterface.getName());

	if (interfaceDetail == nullptr)
	{
		//COMPLETAR
	}
	else
	{
		validationResult = compare(*interfaceDetail, t_StandardizedIntrospectionInterface, t_Path);
	}

	return validationResult;
}

ValidationResult InterfaceValidator::compare(InterfaceDetail t_InterfaceDetail,
	IntrospectionInterface t_StandardizedIntrospectionInterface, string t_Path)
{
	bool valid = true;
	string failureReason;
	IntrospectionInterface expectedIntrospectionInterface = t_InterfaceDetail.getIntrospectionInterfaces().front();

	if ((t_InterfaceDetail.getPath() != "") && (t_InterfaceDetail.getPath().compare(t_Path) != 0))
	{
		valid = false;
		appendToFailureReason(failureReason, " - Interface ");
		appendToFailureReason(failureReason, t_StandardizedIntrospectionInterface.getName());
		appendToFailureReason(failureReason, " found at invalid t_Path ");
		appendToFailureReason(failureReason, t_Path);
	}

	ValidationResult methodValidationResult = SetValidator<IntrospectionMethod>().validate(expectedIntrospectionInterface.getMethods(),
		t_StandardizedIntrospectionInterface.getMethods());
	ValidationResult propertyValidationResult = SetValidator<IntrospectionProperty>().validate(expectedIntrospectionInterface.getProperties(),
		t_StandardizedIntrospectionInterface.getProperties());
	ValidationResult signalValidationResult = SetValidator<IntrospectionSignal>().validate(expectedIntrospectionInterface.getSignals(),
		t_StandardizedIntrospectionInterface.getSignals());
	ValidationResult annotationValidationResult = SetValidator<IntrospectionAnnotation>().validate(expectedIntrospectionInterface.getAnnotations(),
		t_StandardizedIntrospectionInterface.getAnnotations());

	if (!methodValidationResult.isValid() || !propertyValidationResult.isValid()
		|| !signalValidationResult.isValid() || !annotationValidationResult.isValid())
	{
		valid = false;

		if (isVersionPropertyMissing(expectedIntrospectionInterface, propertyValidationResult))
		{
			LOG(WARNING) << "Ignoring interface property match comparison : " << propertyValidationResult.getFailureReason();
			valid = true;
		}

		if (isUndefinedMethodPresentForConfigInterface(expectedIntrospectionInterface, methodValidationResult))
		{
			LOG(WARNING) << "Ignoring interface method match comparison : " << methodValidationResult.getFailureReason();
			valid = true;
		}

		if (isAnnotationMissingForPeerAuthenticationInterface(expectedIntrospectionInterface, annotationValidationResult))
		{
			LOG(WARNING) << "Ignoring interface annotation match comparison : " << annotationValidationResult.getFailureReason();
			valid = true;
		}

		if (!valid)
		{
			appendToFailureReason(failureReason, methodValidationResult.getFailureReason());
			appendToFailureReason(failureReason, propertyValidationResult.getFailureReason());
			appendToFailureReason(failureReason, signalValidationResult.getFailureReason());
			appendToFailureReason(failureReason, annotationValidationResult.getFailureReason());
			appendToFailureReason(failureReason, " ---------- ");
		}
	}

	if (!valid)
	{
		failureReason.insert(0, string("Interface definition does not match for ").append(t_StandardizedIntrospectionInterface.getName()));
	}

	return ValidationResult(valid, failureReason);
}

bool InterfaceValidator::isVersionPropertyMissing(IntrospectionInterface t_ExpectedIntrospectionInterface,
	ValidationResult t_PropertyValidationResult)
{
	return (!t_PropertyValidationResult.isValid()
		&& ((t_ExpectedIntrospectionInterface.getName().compare("org.alljoyn.Config") == 0)
		|| (t_ExpectedIntrospectionInterface.getName().compare("org.alljoyn.Notification") == 0))
		&& (t_PropertyValidationResult.getFailureReason().compare(" - Missing Property [name=Version, type=q, access=read]") == 0));
}

bool InterfaceValidator::isUndefinedMethodPresentForConfigInterface(IntrospectionInterface t_ExpectedIntrospectionInterface,
	ValidationResult t_MethodValidationResult)
{
	return (!t_MethodValidationResult.isValid()
		&& (t_ExpectedIntrospectionInterface.getName().compare("org.alljoyn.Config") == 0)
		&& (t_MethodValidationResult.getFailureReason().compare(" - Undefined Method [name=getVersion, args=[[type=q]], annotations=[]]") == 0));
}

bool InterfaceValidator::isAnnotationMissingForPeerAuthenticationInterface(IntrospectionInterface t_ExpectedIntrospectionInterface,
	ValidationResult t_AnnotationValidationResult)
{
	return (!t_AnnotationValidationResult.isValid()
		&& (t_ExpectedIntrospectionInterface.getName().compare("org.alljoyn.Bus.Peer.Authentication") == 0)
		&& (t_AnnotationValidationResult.getFailureReason().find("Missing Annotation") != string::npos)
		&& (t_AnnotationValidationResult.getFailureReason().find("Undefined Annotation") != string::npos));
}

list<IntrospectionNode> InterfaceValidator::getIntrospectionNodesLoadedFromXmlFiles()
{
	if (m_IntrospectionNodesLoadedFromXmlFiles.empty())
	{
		LOG(INFO) << "Loading introspection xml templates...";
		buildIntrospectionNodesFromXmlFiles();
		LOG(INFO) << "Introspection xml templates loaded";
	}

	return m_IntrospectionNodesLoadedFromXmlFiles;
}

void InterfaceValidator::buildIntrospectionNodesFromXmlFiles()
{
	string rootPath(INTROSPECTION_XML_PATH);
	rootPath.append("v");
	rootPath.append(CERTIFICATION_RELEASE);
	rootPath.append("/");

	for (auto xmlFileToBeLoaded : getXmlFilesToBeLoaded())
	{
		m_IntrospectionNodesLoadedFromXmlFiles.push_back(m_IntrospectionXmlParser.parseXMLFile(rootPath + xmlFileToBeLoaded));
	}
}

InterfaceDetail* InterfaceValidator::getInterfaceDetail(list<IntrospectionNode> t_IntrospectionNodes,
	string t_IntrospectionInterfaceName)
{
	for (auto introspectionNode : t_IntrospectionNodes)
	{
		for (auto introspectionInterface : *introspectionNode.getInterfaces())
		{
			if (t_IntrospectionInterfaceName.compare(introspectionInterface.getName()) == 0)
			{
				list<IntrospectionInterface> interfaces;
				interfaces.push_front(introspectionInterface);

				return new InterfaceDetail(introspectionNode.getName(), interfaces);
			}
		}
	}

	return nullptr;
}

void InterfaceValidator::appendToFailureReason(string& t_FailureReason, const string& t_Reason)
{
	if (!trim(t_Reason).empty())
	{
		t_FailureReason.append(t_Reason);
	}
}

string InterfaceValidator::trim(const string& t_StringToTrim)
{
	size_t first = t_StringToTrim.find_first_not_of(' ');
	size_t last = t_StringToTrim.find_last_not_of(' ');
	return t_StringToTrim.substr(first, (last - first + 1));
}