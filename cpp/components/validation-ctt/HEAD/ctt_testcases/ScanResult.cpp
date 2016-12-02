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
#include "ScanResult.h"

ScanResult::ScanResult(const std::string& t_Ssid, const std::string& t_Bssid,
	const std::string& t_Capabilities, const int t_Level, const int t_Frequency)
	: m_Ssid(t_Ssid), m_Bssid(t_Bssid), m_Capabilities(t_Capabilities), m_Level(t_Level),
	m_Frequency(t_Frequency) {}

std::string ScanResult::getSsid()
{
	return m_Ssid;
}

std::string ScanResult::getBssid()
{
	return m_Bssid;
}

std::string ScanResult::getCapabilities()
{
	return m_Capabilities;
}

int ScanResult::getLevel()
{
	return m_Level;
}

int ScanResult::getFrequency()
{
	return m_Frequency;
}

std::string ScanResult::toString()
{
	std::string strToReturn;
	std::string none("<none>");

	strToReturn.append("SSID: ").append(m_Ssid.empty() ? none : m_Ssid);
	strToReturn.append(", BSSID: ").append(m_Bssid.empty() ? none : m_Bssid);
	strToReturn.append(", capabilities: ").append(m_Capabilities.empty() ? none : m_Capabilities);
	strToReturn.append(", level: ").append(std::to_string(m_Level));
	strToReturn.append(", frequency: ").append(std::to_string(m_Frequency));

	return strToReturn;
}