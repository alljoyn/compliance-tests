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