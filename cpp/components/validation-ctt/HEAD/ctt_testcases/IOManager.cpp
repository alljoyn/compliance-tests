/******************************************************************************
* *    Copyright (c) Open Connectivity Foundation (OCF) and AllJoyn Open
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
#include "IOManager.h"

#include "XMLParser.h"

using namespace std;

IOManager::IOManager(ServiceFramework serviceFramework)
{
	initializeData();
	printTestLabel();
	printIcs(serviceFramework);
	printIxit(serviceFramework);
	printGeneralParameter(serviceFramework);
}

void IOManager::initializeData()
{
	string xmlPath(__argv[2]);
	XMLParser* xmlParser = new XMLParser(xmlPath);

	xmlParser->loadTestCaseInfo(m_TestCaseId, string(__argv[1]), m_TestCaseDescription);
	xmlParser->processXMLFile(m_IcsMap, m_IxitMap, m_GeneralParameterMap);

	delete xmlParser;

	time_t startTime = std::time(NULL);
	localtime_s(&m_StartTime, &startTime);
}

void IOManager::printTestLabel()
{
	CLOG(INFO, "raw") << "====================================================";
	CLOG(INFO, "raw") << "Test Name: " << __argv[1];
	CLOG(INFO, "raw") << "Description: " << m_TestCaseDescription;
	CLOG(INFO, "raw") << getLastSubstring(__argv[0]);
	CLOG(INFO, "raw") << "====================================================";
}

string IOManager::getLastSubstring(char* t_String)
{
	char *substring = NULL;
	char *remainingString = NULL;
	string lastSubstring;

	substring = strtok_s(t_String, "\\", &remainingString);

	while (substring != NULL)
	{
		lastSubstring = string(substring);
		substring = strtok_s(NULL, "\\", &remainingString);
	}

	return lastSubstring;
}

void IOManager::printIcs(ServiceFramework t_ServiceFramework)
{
	vector<string> filters = { "ICSCO" };

	switch (t_ServiceFramework)
	{
		case (ServiceFramework::NOTIFICATION) :
		{
			filters.push_back("ICSN");
			break;
		}
		case (ServiceFramework::CONFIGURATION) :
		{
			filters.push_back("ICSCF");
			break;
		}
		case (ServiceFramework::CONTROL_PANEL) :
		{
			filters.push_back("ICSCP");
			break;
		}
		case (ServiceFramework::ONBOARDING) :
		{
			filters.push_back("ICSON");
			break;
		}
		case (ServiceFramework::LSF_LAMP) :
		{
			filters.push_back("ICSL");
			break;
		}
		case(ServiceFramework::LSF_CONTROLLER) :
		{
			filters.push_back("ICSLC");
			break;
		}
		case(ServiceFramework::HAE) :
		{
			filters.push_back("ICSH");
			break;
		}
	}

	for (auto filter : filters)
	{
		for (auto const &icsEntry : m_IcsMap)
		{
			if (icsEntry.first.find(filter) == 0)
			{
				CLOG(INFO, "raw") << icsEntry.first << " : " << (icsEntry.second ? "true" : "false");
			}
		}
	}
	
}

void IOManager::printIxit(ServiceFramework t_ServiceFramework)
{
	vector<string> filters = { "IXITCO" };

	switch (t_ServiceFramework)
	{
		case (ServiceFramework::NOTIFICATION) :
		{
			filters.push_back("IXITN");
			break;
		}
		case (ServiceFramework::CONFIGURATION) :
		{
			filters.push_back("IXITCF");
			break;
		}
		case (ServiceFramework::CONTROL_PANEL) :
		{
			filters.push_back("IXITCP");
			break;
		}
		case (ServiceFramework::ONBOARDING) :
		{
			filters.push_back("IXITON");
			break;
		}
		case (ServiceFramework::LSF_LAMP) :
		{
			filters.push_back("IXITL");
			break;
		}
		case(ServiceFramework::LSF_CONTROLLER) :
		{
			filters.push_back("IXITLC");
			break;
		}
		case(ServiceFramework::HAE):
		{
			filters.push_back("IXITH");
			break;
		}
	}

	for (auto filter : filters)
	{
		for (auto const &ixitEntry : m_IxitMap)
		{
			if (ixitEntry.first.find(filter) == 0)
			{
				CLOG(INFO, "raw") << ixitEntry.first << " : " << ixitEntry.second;
			}
		}
	}
}

void IOManager::printGeneralParameter(ServiceFramework t_ServiceFramework)
{
	vector<string> filters = { "GPCO" };

	switch (t_ServiceFramework)
	{
		case (ServiceFramework::NOTIFICATION) :
		{
			filters.push_back("GPN");
			break;
		}
		case (ServiceFramework::CONFIGURATION) :
		{
			filters.push_back("GPCF");
			break;
		}
		case (ServiceFramework::CONTROL_PANEL) :
		{
			filters.push_back("GPCP");
			break;
		}
		case (ServiceFramework::ONBOARDING) :
		{
			filters.push_back("GPON");
			break;
		}
		case (ServiceFramework::LSF_LAMP) :
		{
			filters.push_back("GPL");
			break;
		}
		case( ServiceFramework::LSF_CONTROLLER) :
		{
			filters.push_back("GPLC");
			break;
		}
		case(ServiceFramework::HAE):
		{
			filters.push_back("GPH");
			break;
		}
	}

	for (auto filter : filters)
	{
		for (auto const &generalParameterEntry : m_GeneralParameterMap)
		{
			if (generalParameterEntry.first.find(filter) == 0)
			{
				CLOG(INFO, "raw") << generalParameterEntry.first << " : " << generalParameterEntry.second;
			}
		}
	}
}

void IOManager::storeResults()
{
	XMLParser* xmlParser = new XMLParser("Results.xml");

	xmlParser->saveResultsToFile(m_TestCaseId, string(__argv[1]), m_TestCaseDescription, m_StartTime);

	delete xmlParser;
}