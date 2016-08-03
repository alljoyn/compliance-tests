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