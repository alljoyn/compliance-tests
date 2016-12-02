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

#include "WifiNetworkConfig.h"
#include "ScanResult.h"

class WifiHelper
{
public:
	virtual void initialize() = 0;
	virtual bool isWifiEnabled() = 0;
	virtual std::string getCurrentSSID() = 0;
	virtual std::list<ScanResult> waitForScanResults(const long) = 0;
	virtual std::string waitForDisconnect(const long) = 0;
	virtual std::string waitForConnect(const std::string&, const long) = 0;
	virtual bool waitForNetworkAvailable(const std::string&, const long) = 0;
	virtual std::string connectToNetwork(WifiNetworkConfig&, const bool, const long) = 0;
	virtual void release() = 0;
};