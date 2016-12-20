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
#include "stdafx.h"
#include "IntrospectionXmlParser.h"

#include "IntrospectionXmlErrorHandler.h"

#include <xercesc\framework\MemBufInputSource.hpp>
#include <xercesc\framework\URLInputSource.hpp>

#include <xercesc\sax2\XMLReaderFactory.hpp>

using namespace xercesc;
using namespace std;

IntrospectionXmlParser::~IntrospectionXmlParser()
{
	delete t_Parser;
}

IntrospectionNode IntrospectionXmlParser::parseXML(string t_InputXml)
{
	IntrospectionXmlHandler* introspectionXmlHandler =
		new IntrospectionXmlHandler();
	parse(t_InputXml, introspectionXmlHandler, false);

	return introspectionXmlHandler->getIntrospectionNode();
}

IntrospectionNode IntrospectionXmlParser::parseXMLFile(string t_InputXml)
{
	IntrospectionXmlHandler* introspectionXmlHandler =
		new IntrospectionXmlHandler();
	parse(t_InputXml, introspectionXmlHandler, true);

	return introspectionXmlHandler->getIntrospectionNode();
}

void IntrospectionXmlParser::parse(string t_InputXml, IntrospectionXmlHandler* t_TestMetadataXmlHandler,
	const bool t_IsFile)
{
	try
	{
		XMLPlatformUtils::Initialize();
	}
	catch (const XMLException& exception)
	{
		char* message = XMLString::transcode(exception.getMessage());
		LOG(ERROR) << "Error during initialization! : " << message;
		XMLString::release(&message);
		return;
	}

	if (!t_Parser)
	{
		t_Parser = XMLReaderFactory::createXMLReader();

		t_Parser->setFeature(XMLUni::fgSAX2CoreValidation, false);
		t_Parser->setFeature(XMLUni::fgSAX2CoreNameSpaces, true);			// must be true
		t_Parser->setFeature(XMLUni::fgXercesCacheGrammarFromParse, true);	// this is done to improve performance when parsing multiple files
	}
	
	t_Parser->setContentHandler(t_TestMetadataXmlHandler);
	t_Parser->setErrorHandler(new IntrospectionXmlErrorHandler);

	if (!t_IsFile)
	{
		MemBufInputSource inputSource((const XMLByte*)t_InputXml.c_str(), t_InputXml.size(), "xml in memory", false);

		try
		{
			t_Parser->parse(inputSource);
		}
		catch (const XMLException& exception)
		{
			char* message = XMLString::transcode(exception.getMessage());
			LOG(INFO) << "Exception message is: " << message;
			XMLString::release(&message);
		}
		catch (const SAXParseException&)
		{
			t_InputXml.replace(t_InputXml.find("http"), 4, "https");
			inputSource.resetMemBufInputSource((const XMLByte*)t_InputXml.c_str(), t_InputXml.size());
			t_Parser->parse(inputSource);
		}
		catch (...)
		{
			LOG(INFO) << "Unexpected exception";
		}
	}
	else
	{
		try
		{
			t_Parser->parse(t_InputXml.c_str());
		}
		catch (const XMLException& exception)
		{
			char* message = XMLString::transcode(exception.getMessage());
			LOG(INFO) << "Exception message is: " << message;
			XMLString::release(&message);
		}
		catch (const SAXParseException& exception)
		{
			char* message = XMLString::transcode(exception.getMessage());
			LOG(INFO) << "Exception message is: " << message;
			XMLString::release(&message);
		}
		catch (...)
		{
			LOG(INFO) << "Unexpected exception";
		}
	}
}