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
#include "WifiNetworkConfig.h"

WifiNetworkConfig::WifiNetworkConfig(const std::string& t_Ssid)
	: WifiNetworkConfig(t_Ssid, "", ""){}

WifiNetworkConfig::WifiNetworkConfig(const std::string& t_Ssid, const std::string& t_Passphrase,
	const std::string& t_SecurityType)
{
	m_Ssid = t_Ssid;
	m_Passphrase = t_Passphrase;
	m_SecurityType = t_SecurityType;
}

std::string WifiNetworkConfig::getSecurityType()
{
	return m_SecurityType;
}

void WifiNetworkConfig::setSecurityType(const std::string& t_SecurityType)
{
	m_SecurityType = t_SecurityType;
}

std::string WifiNetworkConfig::getPassphrase()
{
	return m_Passphrase;
}

void WifiNetworkConfig::setPassphrase(const std::string& t_passprhase)
{
	m_Passphrase = t_passprhase;
}

std::string WifiNetworkConfig::getSsid()
{
	return m_Ssid;
}

void WifiNetworkConfig::setSsid(const std::string& t_Ssid)
{
	m_Ssid = t_Ssid;
}