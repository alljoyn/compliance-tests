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
	std::list<ScanResult> waitForScanResults(const long);
	std::string waitForDisconnect(const long);
	std::string waitForConnect(const std::string&, const long);
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
	bool scan(const long, const long);
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