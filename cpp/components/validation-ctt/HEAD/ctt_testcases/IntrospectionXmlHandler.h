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
#include <set>
#include <string>

#include "IntrospectionNode.h"

#include <xercesc\sax2\DefaultHandler.hpp>

class IntrospectionXmlHandler : public xercesc::DefaultHandler
{
public:
	IntrospectionNode getIntrospectionNode();
	void startElement(const XMLCh *const, const XMLCh *const,  const XMLCh *const, const xercesc::Attributes&);
	void endElement(const XMLCh *const, const XMLCh *const, const XMLCh *const);

private:
	static const XMLCh XML_QNAME_NAME[];
	static const XMLCh XML_QNAME_VALUE[];
	static const XMLCh XML_QNAME_TYPE[];
	static const XMLCh XML_QNAME_DIRECTION[];
	static const XMLCh XML_QNAME_ACCESS[];

	static const XMLCh NODE_TAG[];
	static const XMLCh INTERFACE_TAG[];
	static const XMLCh METHOD_TAG[];
	static const XMLCh SIGNAL_TAG[];
	static const XMLCh PROPERTY_TAG[];
	static const XMLCh ARG_TAG[];
	static const XMLCh ANNOTATION_TAG[];

	bool m_SawRootNode = false;
	bool m_ProcessInterface = false;
	bool m_ProcessMethodArgs = false;
	bool m_ProcessSignalArgs = false;
	IntrospectionNode m_IntrospectionNode;
	std::list<IntrospectionInterface>* m_Interfaces;
	IntrospectionInterface m_Ifaces;
	std::set<IntrospectionMethod> m_Methods;
	IntrospectionMethod m_Method;
	std::set<IntrospectionProperty> m_Properties;
	std::set<IntrospectionSignal> m_Signals;
	std::set<IntrospectionAnnotation> m_InterfaceAnnotations;
	std::list<IntrospectionSubNode>* m_SubNodes;
	std::list<IntrospectionArg> m_MethodArgs;
	IntrospectionSignal m_Signal;
	std::list<IntrospectionArg> m_SignalArgs;
	std::set<IntrospectionAnnotation> m_MethodAnnotations;
	std::set<IntrospectionAnnotation>* m_NodeAnnotations;

	void createMethodAnnotation(const XMLCh*, const xercesc::Attributes&);
	void createSignalArg(const XMLCh*, const xercesc::Attributes&);
	void createMethodArg(const XMLCh*, const xercesc::Attributes&);
	void createSubNode(const XMLCh*, const xercesc::Attributes&);
	void createProperty(const XMLCh*, const xercesc::Attributes&);
	void createSignal(const XMLCh*, const xercesc::Attributes&);
	void createAnnotation(const XMLCh*, const xercesc::Attributes&);
	void createMethod(const XMLCh*, const xercesc::Attributes&);
	void createInterface(const XMLCh*, const xercesc::Attributes&);
	void createIntrospectionNode(const XMLCh*, const xercesc::Attributes&);
};