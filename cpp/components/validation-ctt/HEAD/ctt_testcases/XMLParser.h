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

#include "rapidxml.hpp"

class XMLParser
{
public:
	XMLParser(const std::string&);
	void processXMLFile(std::map<std::string, bool>&, std::map<std::string, std::string>&, 
		std::map<std::string, std::string>&);
	void loadTestCaseInfo(std::string&, const std::string&, std::string&);
	void saveResultsToFile(const std::string&, const std::string&, const std::string&, const std::tm&);
	
private:
	std::vector<char> m_File;
	std::string m_FileName;
};