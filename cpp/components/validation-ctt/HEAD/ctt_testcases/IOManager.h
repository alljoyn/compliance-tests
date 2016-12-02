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
#pragma once

#include "ServiceFramework.h"

#include <gtest\gtest.h>

class IOManager
{
public:
	IOManager(ServiceFramework);
protected:
	std::map<std::string, bool> m_IcsMap;
	std::map<std::string, std::string> m_IxitMap;
	std::map<std::string, std::string> m_GeneralParameterMap;
	std::string m_TestCaseId;
	std::string m_TestCaseDescription;
	std::tm m_StartTime;

	void initializeData();
	void printTestLabel();
	void printIcs(ServiceFramework);
	void printIxit(ServiceFramework);
	void printGeneralParameter(ServiceFramework);
	void storeResults();
private:
	std::string getLastSubstring(char*);
};