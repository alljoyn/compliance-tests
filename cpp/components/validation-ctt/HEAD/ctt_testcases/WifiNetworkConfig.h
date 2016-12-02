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

class WifiNetworkConfig
{
public:
	WifiNetworkConfig(const std::string&);
	WifiNetworkConfig(const std::string&, const std::string&, const std::string&);
	std::string getSecurityType();
	void setSecurityType(const std::string&);
	std::string getPassphrase();
	void setPassphrase(const std::string&);
	std::string getSsid();
	void setSsid(const std::string&);

private:
	std::string m_Ssid = std::string{ "" };
	std::string m_Passphrase = std::string{ "" };
	std::string m_SecurityType = std::string{ "" };
};