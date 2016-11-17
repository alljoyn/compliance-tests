/******************************************************************************
*    Copyright (c) Open Connectivity Foundation (OCF) and AllJoyn Open
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
#include "WifiManager.h"

#include <thread>

#include <alljoyn\onboarding\Onboarding.h>

VOID WINAPI WifiManager::WlanNotificationCallback(PWLAN_NOTIFICATION_DATA pNotifData,
	PVOID pContext)
{
	PWLAN_CONNECTION_NOTIFICATION_DATA pConnNotifData;

	if (pNotifData != nullptr)
	{
		switch (pNotifData->NotificationSource)
		{
			case WLAN_NOTIFICATION_SOURCE_ACM:
			{
				switch (pNotifData->NotificationCode)
				{
					case wlan_notification_acm_scan_complete:
					{
						m_ScanCompleted = true;
						break;
					}
					case wlan_notification_acm_connection_complete:
					{
						pConnNotifData = (PWLAN_CONNECTION_NOTIFICATION_DATA)pNotifData->pData;

						if (pConnNotifData->wlanReasonCode == 0)
						{
							LOG(INFO) << "The connection succeeded";
							m_Connected = true;
							m_Disconnected = false;

							if (pConnNotifData->wlanConnectionMode == 3 || pConnNotifData->wlanConnectionMode == 2)
							{
								LOG(INFO) << "The profile used for this connection is as follows";
								LOG(INFO) << pConnNotifData->strProfileXml;
							}
						}
						else
						{
							LOG(INFO) << "The connection failed";
							LOG(INFO) << "Reason code: " + pConnNotifData->wlanReasonCode;
						}
						break;
					}
					case wlan_notification_acm_connection_start:
					{
						pConnNotifData = (PWLAN_CONNECTION_NOTIFICATION_DATA)pNotifData->pData;

						LOG(INFO) << "Currently connecting to " << pConnNotifData->dot11Ssid.ucSSID
							<< " using profile " << pConnNotifData->strProfileName
							<< ", connection mode is " << pConnNotifData->wlanConnectionMode
							<< ", BSS type is " << pConnNotifData->dot11BssType;
						break;
					}
					case wlan_notification_acm_disconnected:
					{
						pConnNotifData = (PWLAN_CONNECTION_NOTIFICATION_DATA)pNotifData->pData;
						if (pConnNotifData->wlanReasonCode == 0)
						{
							LOG(INFO) << "The disconnection succeeded";
							m_Disconnected = true;
							m_Connected = false;
						}
						else
						{
							LOG(INFO) << "The disconnection failed";
							LOG(INFO) << "Reason code: " << pConnNotifData->wlanReasonCode;
						}
						break;
					}
				}
				break;
			}
			case WLAN_NOTIFICATION_SOURCE_MSM:
			{
				LOG(INFO) << "Got notification " << pNotifData->NotificationCode << " from MSM.";
				break;
			}
		}
	}
}

bool WifiManager::m_ScanCompleted = false;
bool WifiManager::m_Connected = false;
bool WifiManager::m_Disconnected = false;

WifiManager::WifiManager()
{
	m_HClient = openHandle();
	m_InterfaceGuid = enumInterfaces().InterfaceInfo[0].InterfaceGuid;
}

HANDLE WifiManager::openHandle()
{
	DWORD dwClientVersion = 2;
	DWORD dwNegotiatedVersion;
	HANDLE hClientHandle;

	DWORD dwError;
	if ((dwError = WlanOpenHandle(
		dwClientVersion,
		NULL,
		&dwNegotiatedVersion,
		&hClientHandle
		)) != ERROR_SUCCESS)
	{
		return NULL;
	}
	else
	{
		return hClientHandle;
	}
}

WLAN_INTERFACE_INFO_LIST WifiManager::enumInterfaces()
{
	PWLAN_INTERFACE_INFO_LIST pInterfaceInfoList;
	
	DWORD dwError = WlanEnumInterfaces(
		m_HClient,
		NULL,
		&pInterfaceInfoList);

	return *pInterfaceInfoList;
}

bool WifiManager::scan(const long t_startTime, const long t_timeToWaitInMs)
{
	registerNotification();

	DWORD dwError;
	if ((dwError = WlanScan(
		m_HClient,
		&m_InterfaceGuid,
		NULL,
		NULL,
		NULL)) != ERROR_SUCCESS)
	{
		return false;
	}

	const clock_t begin_time = clock();
	while (!m_ScanCompleted && (clock() - t_startTime < t_timeToWaitInMs))
	{
		std::this_thread::sleep_for(std::chrono::milliseconds(100));
	}

	m_ScanCompleted = false;

	unregisterNotification();

	return true;
}

std::list<ScanResult> WifiManager::getVisibleNetworkList()
{
	DWORD dwFlags = 1;
	PWLAN_AVAILABLE_NETWORK_LIST pAvailableNetworkList;
	std::list<ScanResult> scanResults;

	DWORD dwError;
	if ((dwError = WlanGetAvailableNetworkList(
		m_HClient,
		&m_InterfaceGuid,
		dwFlags,
		NULL,
		&pAvailableNetworkList
		)) != ERROR_SUCCESS)
	{
		return scanResults;
	}
	else
	{
		ScanResult* network;

		for (DWORD i = 0; i < pAvailableNetworkList->dwNumberOfItems; ++i)
		{
			ULONG ssidArrayLength = pAvailableNetworkList->Network[i].dot11Ssid.uSSIDLength;

			if (ssidArrayLength > DOT11_SSID_MAX_LENGTH)
			{
				ssidArrayLength = DOT11_SSID_MAX_LENGTH;
			}

			UCHAR* ssid = pAvailableNetworkList->Network[i].dot11Ssid.ucSSID;
			ULONG bssid = pAvailableNetworkList->Network[i].uNumberOfBssids;
			ULONG ssidLength = pAvailableNetworkList->Network[i].dot11Ssid.uSSIDLength;
			std::string capabilities = pAvailableNetworkList->Network[i].dot11DefaultAuthAlgorithm
				+ ", " + pAvailableNetworkList->Network[i].dot11DefaultCipherAlgorithm;
			int level = -100 + pAvailableNetworkList->Network[i].wlanSignalQuality/2;
			int frequency = 2400;

			network = new ScanResult(std::string(reinterpret_cast<char*>(ssid), ssidLength),
				std::to_string(bssid), capabilities, level, frequency);
			scanResults.push_back(*network);
		}

		return scanResults;
	}
}



bool WifiManager::connect(const std::string& t_targetNetworkType, const std::string& t_targetNetworkProfile)
{
	DOT11_SSID dot11Ssid = DOT11_SSID();
	memcpy(dot11Ssid.ucSSID, t_targetNetworkProfile.c_str(), t_targetNetworkProfile.length());
	dot11Ssid.uSSIDLength = t_targetNetworkProfile.length();

	WLAN_CONNECTION_PARAMETERS wlanConnParams = WLAN_CONNECTION_PARAMETERS();
	wlanConnParams.wlanConnectionMode = wlan_connection_mode_profile;
	std::wstring temp(t_targetNetworkProfile.begin(), t_targetNetworkProfile.end());
	wlanConnParams.strProfile = temp.c_str();
	wlanConnParams.pDot11Ssid = &dot11Ssid;
	wlanConnParams.pDesiredBssidList = NULL;
	
	if (t_targetNetworkType.compare("adhoc") == 0 || t_targetNetworkType.compare("a") == 0)
	{
		wlanConnParams.dot11BssType = dot11_BSS_type_independent;
	}
	else if (t_targetNetworkType.compare("infrastructure") == 0 || t_targetNetworkType.compare("i") == 0)
	{
		wlanConnParams.dot11BssType = dot11_BSS_type_infrastructure;
	}
	else
	{
		return false;
	}

	wlanConnParams.dwFlags = 0;

	DWORD dwError;
	if ((dwError = WlanConnect(
		m_HClient,
		&m_InterfaceGuid,
		&wlanConnParams,
		NULL)) != ERROR_SUCCESS)
	{
		return false;
	}

	return true;
}

void WifiManager::disconnect()
{
	DWORD dwError = WlanDisconnect(
		m_HClient,
		&m_InterfaceGuid,
		NULL);
}

void WifiManager::registerNotification()
{
	DWORD dwError;
	if ((dwError = WlanRegisterNotification(
		m_HClient,
		WLAN_NOTIFICATION_SOURCE_ACM,
		false,
		&WlanNotificationCallback,
		NULL,
		NULL,
		0)) != ERROR_SUCCESS)
	{
		LOG(ERROR) << "Unable to register notifications";
	}
}

void WifiManager::unregisterNotification()
{
	DWORD dwError = WlanRegisterNotification(
		m_HClient,
		WLAN_NOTIFICATION_SOURCE_NONE,
		false,
		NULL,
		NULL,
		NULL,
		0);
}

std::list<std::string> WifiManager::getProfileList()
{
	PWLAN_PROFILE_INFO_LIST pWlanProfileInfoList;
	std::list<std::string> profileList;

	DWORD dwError;
	
	if ((dwError = WlanGetProfileList(
		m_HClient,
		&m_InterfaceGuid,
		NULL,
		&pWlanProfileInfoList)) == ERROR_SUCCESS)
	{
		for (DWORD i = 0; i < pWlanProfileInfoList->dwNumberOfItems; ++i)
		{
			std::wstring ws(pWlanProfileInfoList->ProfileInfo[i].strProfileName);
			std::string profileName(ws.begin(), ws.end());
			profileList.push_back(trim(profileName));
		}
	}

	return profileList;
}

std::string WifiManager::trim(const std::string &s)
{
	std::string::const_iterator it = s.begin();
	while (it != s.end() && isspace(*it))
		it++;

	std::string::const_reverse_iterator rit = s.rbegin();
	while (rit.base() != it && isspace(*rit))
		rit++;

	return std::string(it, rit.base());
}

bool WifiManager::setProfile(const std::string& t_profile)
{
	DWORD dwFlags = 0;
	WLAN_REASON_CODE dwReasonCode;
	std::wstring temp(t_profile.begin(), t_profile.end());
	LPCWSTR strProfileXml = temp.c_str();

	DWORD dwError;
	if ((dwError = WlanSetProfile(
		m_HClient,
		&m_InterfaceGuid,
		dwFlags,
		strProfileXml,
		NULL,
		true,
		NULL,
		&dwReasonCode)) != ERROR_SUCCESS)
	{
		//reasonCodeToString(dwReasonCode);
		return false;
	}
	else
	{
		return true;
	}
}

void WifiManager::reasonCodeToString(DWORD t_reason_code)
{
	DWORD dwBufferSize = 256;
	PWCHAR pStringBuffer = new WCHAR[dwBufferSize];

	DWORD dwError;
	dwError = WlanReasonCodeToString(
		t_reason_code,
		dwBufferSize,
		pStringBuffer,
		NULL);

	LOG(ERROR) << pStringBuffer;
}

bool WifiManager::deleteProfile(const std::string& t_profile_name)
{
	DWORD dwError;

	if ((dwError = WlanDeleteProfile(
		m_HClient,
		&m_InterfaceGuid,
		std::wstring(t_profile_name.begin(), t_profile_name.end()).c_str(),
		NULL)) != ERROR_SUCCESS)
	{
		return false;
	}
	else
	{
		return true;
	}
}

bool WifiManager::isWifiEnabled()
{
	return (enumInterfaces().dwNumberOfItems > 0);
}

std::string WifiManager::connectedSsid()
{
	DWORD dwDataSize;
	PWLAN_CONNECTION_ATTRIBUTES pWlanConnectionAttributes = new WLAN_CONNECTION_ATTRIBUTES();
	WLAN_OPCODE_VALUE_TYPE wlanOpcodeValueType;

	DWORD dwError;
	if ((dwError = WlanQueryInterface(
		m_HClient,
		&m_InterfaceGuid,
		wlan_intf_opcode_current_connection,
		NULL,
		&dwDataSize,
		(PVOID*)&pWlanConnectionAttributes,
		&wlanOpcodeValueType)) != ERROR_SUCCESS)
	{
		return std::string("");
	}
	else
	{
		ULONG ssidArrayLength = pWlanConnectionAttributes->wlanAssociationAttributes.dot11Ssid.uSSIDLength;
		if (ssidArrayLength > DOT11_SSID_MAX_LENGTH)
		{
			ssidArrayLength = DOT11_SSID_MAX_LENGTH;
		}

		return std::string(pWlanConnectionAttributes->wlanAssociationAttributes.dot11Ssid.ucSSID,
			pWlanConnectionAttributes->wlanAssociationAttributes.dot11Ssid.ucSSID + ssidArrayLength);
	}
}

std::list<ScanResult> WifiManager::waitForScanResults(const long t_timeout)
{
	std::list<ScanResult> scanResults;
	
	const clock_t beginTime = clock();

	if (scan(beginTime, t_timeout))
	{
		while (scanResults.empty() && (clock() - beginTime < t_timeout))
		{
			scanResults = getVisibleNetworkList();
			std::this_thread::sleep_for(std::chrono::milliseconds(100));
		}
	}

	return scanResults;
}

std::string WifiManager::waitForDisconnect(const long t_Timeout)
{
	registerNotification();
	disconnect();

	const clock_t beginTime = clock();
	while (!m_Disconnected && (clock() - beginTime < t_Timeout))
	{
		std::this_thread::sleep_for(std::chrono::milliseconds(100));
	}

	unregisterNotification();

	if (m_Disconnected)
	{
		return connectedSsid();
	}
	else
	{
		return std::string("");
	}
}

std::string WifiManager::waitForConnect(const std::string& t_Ssid, const long t_Timeout)
{
	if (t_Ssid.compare(connectedSsid()) == 0)
	{
		return t_Ssid;
	}
	else
	{
		const clock_t beginTime = clock();
		scan(beginTime, t_Timeout);

		registerNotification();

		if (connect("infrastructure", t_Ssid))
		{
			while (!m_Connected && (clock() - beginTime < t_Timeout))
			{
				std::this_thread::sleep_for(std::chrono::milliseconds(100));
			}
		}

		unregisterNotification();

		if (m_Connected)
		{
			return t_Ssid;
		}
		else
		{
			return std::string("");
		}
	}
}

std::list<std::string> WifiManager::getConfiguredNetworks()
{
	return getProfileList();
}

bool WifiManager::addNetwork(const std::string& t_profile)
{
	return setProfile(t_profile);
}

bool WifiManager::removeNetwork(const std::string& t_profile_name)
{
	return deleteProfile(t_profile_name);
}

void WifiManager::release()
{
	if (m_HClient != nullptr)
	{
		WlanCloseHandle(
			m_HClient,
			NULL);
	}

	/*if (&m_InterfaceGuid != nullptr)
	{
		WlanFreeMemory(&m_InterfaceGuid);
	}*/
}