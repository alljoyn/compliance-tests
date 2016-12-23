/******************************************************************************
* Copyright AllSeen Alliance. All rights reserved.
*
*    Permission to use, copy, modify, and/or distribute this software for any
*    purpose with or without fee is hereby granted, provided that the above
*    copyright notice and this permission notice appear in all copies.
*
*    THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
*    WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
*    MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
*    ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
*    WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
*    ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
*    OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
******************************************************************************/
#pragma once

#include <list>
#include <wlanapi.h>
#include <windows.h>

#include "ScanResult.h"

class WifiManager
{
public:
	WifiManager();
	bool connect(const std::string&, const std::string&);
	bool isWifiEnabled();
	std::string connectedSsid();
	std::list<ScanResult> waitForScanResults(long);
	std::string waitForDisconnect(long);
	std::string waitForConnect(const std::string&, long);
	std::list<std::string> getConfiguredNetworks();
	bool addNetwork(const std::string&);
	bool removeNetwork(const std::string&);
	void release();

private:
	HANDLE m_HClient;
	GUID m_InterfaceGuid;

	static VOID WINAPI WlanNotificationCallback(PWLAN_NOTIFICATION_DATA, PVOID);
	static bool m_Connected;
	static bool m_Disconnected;
	static bool m_ScanCompleted;

	HANDLE openHandle();
	WLAN_INTERFACE_INFO_LIST enumInterfaces();
	bool scan(long, long);
	std::list<ScanResult> getVisibleNetworkList();
	void disconnect();
	void registerNotification();
	void unregisterNotification();
	std::list<std::string> getProfileList();
	bool setProfile(const std::string&);
	bool deleteProfile(const std::string&);
	std::string trim(const std::string &);
	void reasonCodeToString(DWORD);
};