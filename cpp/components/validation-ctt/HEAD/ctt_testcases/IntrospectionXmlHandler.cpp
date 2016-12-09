/******************************************************************************
* Copyright (c) 2016 Open Connectivity Foundation (OCF) and AllJoyn Open
*    Source Project (AJOSP) Contributors and others.
*
*    SPDX-License-Identifier: Apache-2.0
*
*    All rights reserved. This program and the accompanying materials are
*    made available under the terms of the Apache License, Version 2.0
*    which accompanies this distribution, and is available at
*    http://www.apache.org/licenses/LICENSE-2.0
*
*    Copyright 2016 Open Connectivity Foundation and Contributors to
*    AllSeen Alliance. All rights reserved.
*
*    Permission to use, copy, modify, and/or distribute this software for
*    any purpose with or without fee is hereby granted, provided that the
*    above copyright notice and this permission notice appear in all
*    copies.
*
*     THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL
*     WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED
*     WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE
*     AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL
*     DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR
*     PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER
*     TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
*     PERFORMANCE OF THIS SOFTWARE.
******************************************************************************/
#include "stdafx.h"
#include "IntrospectionXmlHandler.h"

#include <xercesc\util\XMLString.hpp>
#include <xercesc\internal\VecAttributesImpl.hpp>

using namespace std;
using namespace xercesc;

const XMLCh IntrospectionXmlHandler::XML_QNAME_NAME[] =
{
	chLatin_n, chLatin_a, chLatin_m, chLatin_e, chNull
};
const XMLCh IntrospectionXmlHandler::XML_QNAME_VALUE[] =
{
	chLatin_v, chLatin_a, chLatin_l, chLatin_u, chLatin_e, chNull
};
const XMLCh IntrospectionXmlHandler::XML_QNAME_TYPE[] =
{
	chLatin_t, chLatin_y, chLatin_p, chLatin_e, chNull
};
const XMLCh IntrospectionXmlHandler::XML_QNAME_DIRECTION[] =
{
	chLatin_d, chLatin_i, chLatin_r, chLatin_e, chLatin_c,
	chLatin_t, chLatin_i, chLatin_o, chLatin_n, chNull
};
const XMLCh IntrospectionXmlHandler::XML_QNAME_ACCESS[] =
{
	chLatin_a, chLatin_c, chLatin_c, chLatin_e, chLatin_s,
	chLatin_s, chNull
};

const XMLCh IntrospectionXmlHandler::NODE_TAG[] =
{
	chLatin_n, chLatin_o, chLatin_d, chLatin_e, chNull
};
const XMLCh IntrospectionXmlHandler::INTERFACE_TAG[] =
{
	chLatin_i, chLatin_n, chLatin_t, chLatin_e, chLatin_r,
	chLatin_f, chLatin_a, chLatin_c, chLatin_e, chNull
};
const XMLCh IntrospectionXmlHandler::METHOD_TAG[] =
{
	chLatin_m, chLatin_e, chLatin_t, chLatin_h, chLatin_o,
	chLatin_d, chNull
};
const XMLCh IntrospectionXmlHandler::SIGNAL_TAG[] =
{
	chLatin_s, chLatin_i, chLatin_g, chLatin_n, chLatin_a,
	chLatin_l, chNull
};
const XMLCh IntrospectionXmlHandler::PROPERTY_TAG[] =
{
	chLatin_p, chLatin_r, chLatin_o, chLatin_p, chLatin_e,
	chLatin_r, chLatin_t, chLatin_y, chNull
};
const XMLCh IntrospectionXmlHandler::ARG_TAG[] =
{
	chLatin_a, chLatin_r, chLatin_g, chNull
};
const XMLCh IntrospectionXmlHandler::ANNOTATION_TAG[] =
{
	chLatin_a, chLatin_n, chLatin_n, chLatin_o, chLatin_t,
	chLatin_a, chLatin_t, chLatin_i, chLatin_o, chLatin_n,
	chNull
};

IntrospectionNode IntrospectionXmlHandler::getIntrospectionNode()
{
	return m_IntrospectionNode;
}

void IntrospectionXmlHandler::startElement(const XMLCh *const t_Uri, const XMLCh *const t_LocalName,
	const XMLCh *const t_QName, const Attributes& t_Attributes)
{
	createIntrospectionNode(t_LocalName, t_Attributes);
	createInterface(t_LocalName, t_Attributes);
	createMethod(t_LocalName, t_Attributes);
	createMethodArg(t_LocalName, t_Attributes);
	createMethodAnnotation(t_LocalName, t_Attributes);
	createSignal(t_LocalName, t_Attributes);
	createSignalArg(t_LocalName, t_Attributes);
	createProperty(t_LocalName, t_Attributes);
	createAnnotation(t_LocalName, t_Attributes);
	createSubNode(t_LocalName, t_Attributes);
}

void IntrospectionXmlHandler::endElement(const XMLCh *const t_Uri, const XMLCh *const t_LocalName,
	const XMLCh *const t_QName)
{
	if (XMLString::compareIString(t_LocalName, METHOD_TAG) == 0)
	{
		m_ProcessMethodArgs = false;
	}

	if (XMLString::compareIString(t_LocalName, SIGNAL_TAG) == 0)
	{
		m_ProcessSignalArgs = false;
	}

	if (XMLString::compareIString(t_LocalName, INTERFACE_TAG) == 0)
	{
		m_ProcessInterface = false;
	}
}

void IntrospectionXmlHandler::createMethodAnnotation(const XMLCh* t_LocalName, const Attributes& t_Attributes)
{
	if ((XMLString::compareIString(t_LocalName, ANNOTATION_TAG) == 0)
		&& (m_ProcessMethodArgs))
	{
		m_MethodAnnotations = m_Method.getAnnotations();
		IntrospectionAnnotation annotation;
		annotation.setName(XMLString::transcode(t_Attributes.getValue(XML_QNAME_NAME)));
		annotation.setValue(XMLString::transcode(t_Attributes.getValue(XML_QNAME_VALUE)));
		m_MethodAnnotations.insert(annotation);
	}
}

void IntrospectionXmlHandler::createSignalArg(const XMLCh* t_LocalName, const Attributes& t_Attributes)
{
	if ((XMLString::compareIString(t_LocalName, ARG_TAG) == 0)
		&& (m_ProcessSignalArgs))
	{
		m_SignalArgs = m_Signal.getArgs();
		IntrospectionArg arg;
		const XMLCh* signalArgName = t_Attributes.getValue(XML_QNAME_NAME);
		arg.setName(signalArgName != NULL ? XMLString::transcode(signalArgName) : "");
		arg.setType(XMLString::transcode(t_Attributes.getValue(XML_QNAME_TYPE)));
		const XMLCh* signalArgDirection = t_Attributes.getValue(XML_QNAME_DIRECTION);
		arg.setDirection(signalArgDirection != NULL ? XMLString::transcode(signalArgDirection) : "");
		m_SignalArgs.push_front(arg);
	}
}

void IntrospectionXmlHandler::createMethodArg(const XMLCh* t_LocalName, const Attributes& t_Attributes)
{
	if ((XMLString::compareIString(t_LocalName, ARG_TAG) == 0)
		&& (m_ProcessMethodArgs))
	{
		m_MethodArgs = m_Method.getArgs();
		IntrospectionArg arg;
		const XMLCh* methodArgName = t_Attributes.getValue(XML_QNAME_NAME);
		arg.setName(methodArgName != NULL ? XMLString::transcode(methodArgName) : "");
		arg.setType(XMLString::transcode(t_Attributes.getValue(XML_QNAME_TYPE)));
		arg.setDirection(XMLString::transcode(t_Attributes.getValue(XML_QNAME_DIRECTION)));
		m_MethodArgs.push_front(arg);
	}
}

void IntrospectionXmlHandler::createSubNode(const XMLCh* t_LocalName, const Attributes& t_Attributes)
{
	if ((XMLString::compareIString(t_LocalName, NODE_TAG) == 0)
		&& (m_SawRootNode))
	{
		const XMLCh* nodeName = t_Attributes.getValue(XML_QNAME_NAME);

		if ((nodeName != NULL) && (XMLString::transcode(nodeName) != m_IntrospectionNode.getName()))
		{
			m_SubNodes = m_IntrospectionNode.getSubNodes();
			IntrospectionSubNode subNode;
			subNode.setName(XMLString::transcode(nodeName));
			m_SubNodes->push_front(subNode);
		}
	}
}

void IntrospectionXmlHandler::createProperty(const XMLCh* t_LocalName, const Attributes& t_Attributes)
{
	if (XMLString::compareIString(t_LocalName, PROPERTY_TAG) == 0)
	{
		m_Properties = m_Ifaces.getProperties();
		IntrospectionProperty property;
		property.setName(XMLString::transcode(t_Attributes.getValue(XML_QNAME_NAME)));
		property.setType(XMLString::transcode(t_Attributes.getValue(XML_QNAME_TYPE)));
		property.setAccess(XMLString::transcode(t_Attributes.getValue(XML_QNAME_ACCESS)));
		m_Properties.insert(property);
	}
}
void IntrospectionXmlHandler::createSignal(const XMLCh* t_LocalName, const Attributes& t_Attributes)
{
	if (XMLString::compareIString(t_LocalName, SIGNAL_TAG) == 0)
	{
		m_Signals = m_Ifaces.getSignals();
		m_Signal.setName(XMLString::transcode(t_Attributes.getValue(XML_QNAME_NAME)));
		m_Signals.insert(m_Signal);
		
		m_ProcessSignalArgs = true;
	}
}
void IntrospectionXmlHandler::createAnnotation(const XMLCh* t_LocalName, const Attributes& t_Attributes)
{
	if (XMLString::compareIString(t_LocalName, ANNOTATION_TAG) == 0)
	{
		if (!m_ProcessInterface)
		{
			m_NodeAnnotations = m_IntrospectionNode.getAnnotations();
			IntrospectionAnnotation annotation;
			annotation.setName(XMLString::transcode(t_Attributes.getValue(XML_QNAME_NAME)));
			annotation.setValue(XMLString::transcode(t_Attributes.getValue(XML_QNAME_VALUE)));
			m_NodeAnnotations->insert(annotation);
		}
		else if (!m_ProcessMethodArgs)
		{
			m_InterfaceAnnotations = m_Ifaces.getAnnotations();
			IntrospectionAnnotation annotation;
			annotation.setName(XMLString::transcode(t_Attributes.getValue(XML_QNAME_NAME)));
			annotation.setValue(XMLString::transcode(t_Attributes.getValue(XML_QNAME_VALUE)));
			m_InterfaceAnnotations.insert(annotation);
		}
	}
}
void IntrospectionXmlHandler::createMethod(const XMLCh* t_LocalName, const Attributes& t_Attributes)
{
	if (XMLString::compareIString(t_LocalName, METHOD_TAG) == 0)
	{
		m_Methods = m_Ifaces.getMethods();
		const XMLCh* methodName = t_Attributes.getValue(XML_QNAME_NAME);
		m_Method.setName(methodName != NULL ? XMLString::transcode(methodName) : "");
		m_Methods.insert(m_Method);

		m_ProcessMethodArgs = true;
	}
}
void IntrospectionXmlHandler::createInterface(const XMLCh* t_LocalName, const Attributes& t_Attributes)
{
	if (XMLString::compareIString(t_LocalName, INTERFACE_TAG) == 0)
	{
		m_Interfaces = m_IntrospectionNode.getInterfaces();
		m_Ifaces = IntrospectionInterface();
		const XMLCh* interfaceName = t_Attributes.getValue(XML_QNAME_NAME);
		m_Ifaces.setName(interfaceName != NULL ? XMLString::transcode(interfaceName) : "");
		m_Interfaces->push_front(m_Ifaces);

		m_ProcessInterface = true;
	}
}
void IntrospectionXmlHandler::createIntrospectionNode(const XMLCh* t_LocalName, const Attributes& t_Attributes)
{
	if ((XMLString::compareIString(t_LocalName, NODE_TAG) == 0)
		&& !(m_SawRootNode))
	{
		const XMLCh* nodeName = t_Attributes.getValue(XML_QNAME_NAME);
		m_IntrospectionNode.setName(nodeName != NULL ? XMLString::transcode(nodeName) : "");
		
		m_SawRootNode = true;
	}
}