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

#include <string>

class ScanResult
{
public:
	ScanResult(const std::string&, const std::string&, const std::string&, const int, const int);
	std::string getSsid();
	std::string getBssid();
	std::string getCapabilities();
	int getLevel();
	int getFrequency();
	std::string toString();

private:
	std::string m_Ssid;
	std::string m_Bssid;
	std::string m_Capabilities;
	int m_Level;
	int m_Frequency;
};