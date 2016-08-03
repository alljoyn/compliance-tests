/******************************************************************************
* Copyright AllSeen Alliance. All rights reserved.
*
*    Permission to use, copy, modify, and/or distribute this software for any
*    purpose with or without fee is hereby granted, provided that the above
*    copyright notice and this permission notice appear in all copies.
*
*    THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
*    WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
*    MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
*    ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
*    WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
*    ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
*    OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
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