/******************************************************************************
* * Copyright (c) Open Connectivity Foundation (OCF) and AllJoyn Open
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