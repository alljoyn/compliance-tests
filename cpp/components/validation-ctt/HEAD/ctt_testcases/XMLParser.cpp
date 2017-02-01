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
#include "XMLParser.h"

#include "rapidxml_print.hpp"

using namespace std;
using namespace rapidxml;

XMLParser::XMLParser(string t_FileName) : m_FileName(t_FileName)
{
	if (ifstream is{ t_FileName, is.binary | is.ate | is.in | is.out })
	{
		m_File.resize(static_cast<size_t>(is.tellg()));
		is.seekg(0);
		is.read(m_File.data(), m_File.size());
		m_File.push_back(0);
	}
}

void XMLParser::processXMLFile(map<string, bool> &t_Ics, map<string, string> &t_Ixit,
	map<string, string> &t_GeneralParameter)
{
	enum
	{
		PARSE_FLAGS = parse_non_destructive
	};

	// NOTE :	There is a 'const cast<>', but 'rapidxml::parse_non_destructive'
	//			guarantees 'data' is not overwritten.
	xml_document<> xmlDoc;
	xmlDoc.parse<PARSE_FLAGS>(const_cast<char*>(m_File.data()));

	for (xml_node<char>* n = xmlDoc.first_node()->first_node(); n; n = n->next_sibling())
	{
		char* nodeName = new char[n->name_size() + 1];
		strncpy_s(nodeName, n->name_size() + 1, n->name(), _TRUNCATE);

		if ((strcmp(nodeName, "Ics") == 0)
			|| (strcmp(nodeName, "Ixit") == 0)
			|| (strcmp(nodeName, "Parameter") == 0))
		{
			char* name = new char[n->first_node("Name")->value_size() + 1];
			strncpy_s(name, n->first_node("Name")->value_size() + 1, n->first_node("Name")->value(), _TRUNCATE);

			char* value;
			if (n->first_node("Value")->value_size() != 0)
			{
				value = new char[n->first_node("Value")->value_size() + 1];
				strncpy_s(value, n->first_node("Value")->value_size() + 1, n->first_node("Value")->value(), _TRUNCATE);
			}
			else
			{
				value = new char[1] {'\0'};
			}

			if (strcmp(nodeName, "Ics") == 0)
			{
				t_Ics.insert(pair<string, bool>(name, (strcmp(value, "true") == 0)));
			}
			else if (strcmp(nodeName, "Ixit") == 0)
			{
				t_Ixit.insert(pair<string, string>(name, value));
			}
			else
			{
				t_GeneralParameter.insert(pair<string, string>(name, value));
			}

			delete[] name;
			delete[] value;
		}

		delete[] nodeName;	
	}

	xmlDoc.clear();
}

void XMLParser::loadTestCaseInfo(string& t_TestCaseId, const string& t_TestCaseName, string& t_TestCaseDescription)
{
	enum
	{
		PARSE_FLAGS = parse_non_destructive
	};

	// NOTE :	There is a 'const cast<>', but 'rapidxml::parse_non_destructive'
	//			guarantees 'data' is not overwritten.
	xml_document<> xmlDoc;
	xmlDoc.parse<PARSE_FLAGS>(const_cast<char*>(m_File.data()));

	for (xml_node<>* n = xmlDoc.first_node()->first_node(); n; n = n->next_sibling())
	{
		char* nodeName = new char[n->name_size() + 1];
		strncpy_s(nodeName, n->name_size() + 1, n->name(), _TRUNCATE);

		if ((strcmp(nodeName, "TestCase") == 0))
		{
			char* name = new char[n->first_node("Name")->value_size() + 1];
			strncpy_s(name, n->first_node("Name")->value_size() + 1, n->first_node("Name")->value(), _TRUNCATE);

			if (strcmp(name, t_TestCaseName.c_str()) == 0)
			{
				char* id = new char[n->first_node("Id")->value_size() + 1];
				char* description = new char[n->first_node("Description")->value_size() + 1];

				strncpy_s(id, n->first_node("Id")->value_size() + 1, n->first_node("Id")->value(), _TRUNCATE);
				strncpy_s(description, n->first_node("Description")->value_size() + 1, n->first_node("Description")->value(), _TRUNCATE);

				t_TestCaseId = string(id);
				t_TestCaseDescription = string(description);
			}
		}
	}
}

void XMLParser::saveResultsToFile(const string& t_TestCaseId, const string& t_TestCaseName,
	const string& t_TestCaseDescription, const tm& t_TestCaseDatetime)
{
	enum
	{
		PARSE_FLAGS = parse_non_destructive
	};

	// NOTE :	There is a 'const cast<>', but 'rapidxml::parse_non_destructive'
	//			guarantees 'data' is not overwritten.
	xml_document<> xmlDoc;
	
	if (m_File.size() != 0)
	{
		xmlDoc.parse<PARSE_FLAGS>(const_cast<char*>(m_File.data()));
	}
	else
	{
		xml_node<> *rootNode = xmlDoc.allocate_node(node_element, "Results");
		xmlDoc.append_node(rootNode);
	}

	xml_node<> *resultsNode = xmlDoc.allocate_node(node_element, "TestCase");
	xmlDoc.first_node()->append_node(resultsNode);

	char* logDatetime = new char[16];
	std::strftime(logDatetime, 16, "%Y%m%d-%H%M%S", &t_TestCaseDatetime);

	char* xmlDatetime = new char[20];
	std::strftime(xmlDatetime, 20, "%Y-%m-%d %H:%M:%S", &t_TestCaseDatetime);

	::testing::UnitTest& unitTest = *::testing::UnitTest::GetInstance();
	string verdict;
	if (unitTest.failed_test_case_count() == 1)
	{
		verdict = "FAIL";
	}
	else if (unitTest.successful_test_case_count() == 1)
	{
		verdict = "PASS";
	}

	string logFileName = "Log-";
	logFileName.append(t_TestCaseName);
	logFileName.append("-");
	logFileName.append(logDatetime);
	logFileName.append(".log");

	xml_node<> *idNode = xmlDoc.allocate_node(node_element, "Id", t_TestCaseId.c_str());
	xml_node<> *nameNode = xmlDoc.allocate_node(node_element, "Name", t_TestCaseName.c_str());
	xml_node<> *descriptionNode = xmlDoc.allocate_node(node_element, "Description", t_TestCaseDescription.c_str());
	xml_node<> *datetimeNode = xmlDoc.allocate_node(node_element, "DateTime", xmlDatetime);
	xml_node<> *verdictNode = xmlDoc.allocate_node(node_element, "Verdict", verdict.c_str());
	xml_node<> *versionNode = xmlDoc.allocate_node(node_element, "Version", CERTIFICATION_RELEASE.c_str());
	xml_node<> *logFileNode = xmlDoc.allocate_node(node_element, "LogFile", logFileName.c_str());

	resultsNode->append_node(idNode);
	resultsNode->append_node(nameNode);
	resultsNode->append_node(descriptionNode);
	resultsNode->append_node(datetimeNode);
	resultsNode->append_node(verdictNode);
	resultsNode->append_node(versionNode);
	resultsNode->append_node(logFileNode);


	ofstream file;
	file.open(m_FileName);
	file << xmlDoc;
	file.close();
}