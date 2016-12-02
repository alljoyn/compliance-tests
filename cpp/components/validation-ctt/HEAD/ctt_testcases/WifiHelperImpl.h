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

#include "WifiHelper.h"
#include "WifiManager.h"

class WifiHelperImpl : public WifiHelper
{
public:
	void initialize();
	void release();
	bool isWifiEnabled();
	std::string getCurrentSSID();
	std::list<ScanResult> waitForScanResults(const long);
	std::string waitForDisconnect(const long);
	std::string waitForConnect(const std::string&, const long);
	bool waitForNetworkAvailable(const std::string&, const long);
	std::string connectToNetwork(WifiNetworkConfig&, const bool, const long);


protected:
	WifiManager getWifiManager();

private:
	WifiManager* m_WifiManager{ nullptr };

	void removeWifiNetwork(const std::string&);
	void checkForWifiEnabled();
	std::string createWifiConfiguration(const std::string&, const std::string&, const std::string&);
};