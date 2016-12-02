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
#include "IntrospectionXmlErrorHandler.h"

#include <xercesc\util\XMLString.hpp>
#include <xercesc\sax\SAXParseException.hpp>

using namespace xercesc;

void IntrospectionXmlErrorHandler::error(const SAXParseException& t_Exception)
{
	LOG(ERROR) << XMLString::transcode(t_Exception.getMessage())
		<< ", systemId: " << t_Exception.getSystemId()
		<< ", publicId: " << t_Exception.getPublicId()
		<< ", line: " << t_Exception.getLineNumber()
		<< ", column: " << t_Exception.getColumnNumber();

	throw t_Exception;
}

void IntrospectionXmlErrorHandler::fatalError(const SAXParseException& t_Exception)
{
	LOG(ERROR) << XMLString::transcode(t_Exception.getMessage())
		<< ", systemId: " << t_Exception.getSystemId()
		<< ", publicId: " << t_Exception.getPublicId()
		<< ", line: " << t_Exception.getLineNumber()
		<< ", column: " << t_Exception.getColumnNumber();

	throw t_Exception;
}

void IntrospectionXmlErrorHandler::warning(const SAXParseException& t_Exception)
{
	LOG(WARNING) << XMLString::transcode(t_Exception.getMessage());
}

void IntrospectionXmlErrorHandler::resetErrors()
{

}