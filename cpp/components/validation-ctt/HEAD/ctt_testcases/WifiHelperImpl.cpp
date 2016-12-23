#include "stdafx.h"
#include "WifiHelperImpl.h"
#include "WifiNotEnabledException.h"

void WifiHelperImpl::initialize()
{
	m_WifiManager = new WifiManager();
}

void WifiHelperImpl::release()
{
	if (m_WifiManager != nullptr)
	{
		m_WifiManager->release();
		delete m_WifiManager;
		m_WifiManager = nullptr;
	}
}

WifiManager WifiHelperImpl::getWifiManager()
{
	return *m_WifiManager;
}

bool WifiHelperImpl::isWifiEnabled()
{
	return m_WifiManager->isWifiEnabled();
}

std::string WifiHelperImpl::getCurrentSSID()
{
	checkForWifiEnabled();
	return m_WifiManager->connectedSsid();
}

std::list<ScanResult> WifiHelperImpl::waitForScanResults(long t_timeout)
{
	checkForWifiEnabled();
	return m_WifiManager->waitForScanResults(t_timeout);
}

std::string WifiHelperImpl::waitForDisconnect(long t_timeout)
{
	checkForWifiEnabled();
	return m_WifiManager->waitForDisconnect(t_timeout);
}

std::string WifiHelperImpl::waitForConnect(const std::string& t_Ssid, long t_timeout)
{
	checkForWifiEnabled();
	LOG(INFO) << "Waiting for WiFi to connect to " << t_Ssid;

	return m_WifiManager->waitForConnect(t_Ssid, t_timeout);
}

bool WifiHelperImpl::waitForNetworkAvailable(const std::string& t_Ssid, long t_timeout)
{
	LOG(INFO) << "Waiting for network " << t_Ssid << " to be available";

	bool isAvailable = false;
	const clock_t beginTime = clock();
	while (!isAvailable && (clock() - beginTime < t_timeout))
	{
		std::list<ScanResult> scanResults = waitForScanResults(beginTime + t_timeout - clock());

		if (!scanResults.empty())
		{
			for (auto scanResult : scanResults)
			{
				if (t_Ssid.compare(scanResult.getSsid()) == 0)
				{
					isAvailable = true;
					break;
				}
			}
		}
	}

	return isAvailable;
}

std::string WifiHelperImpl::connectToNetwork(WifiNetworkConfig& t_WifiNetworkConfig, bool t_Recreate,
	long t_Timeout)
{
	const clock_t beginTime = clock();

	LOG(INFO) << "Attempting to connect to SSID : " << t_WifiNetworkConfig.getSsid();

	std::string network;
	for (auto configured_network : m_WifiManager->getConfiguredNetworks())
	{
		if (configured_network.compare(t_WifiNetworkConfig.getSsid()) == 0)
		{
			network = configured_network;
			break;
		}
	}

	bool status = true;
	if (network.empty())
	{
		std::string profileConfig = createWifiConfiguration(t_WifiNetworkConfig.getSsid(),
			t_WifiNetworkConfig.getPassphrase(), t_WifiNetworkConfig.getSecurityType());
		
		status = m_WifiManager->addNetwork(profileConfig);
	}
	else if (t_Recreate)
	{
		std::string profileConfig = createWifiConfiguration(t_WifiNetworkConfig.getSsid(),
			t_WifiNetworkConfig.getPassphrase(), t_WifiNetworkConfig.getSecurityType());

		removeWifiNetwork(network);
		status = m_WifiManager->addNetwork(profileConfig);
	}

	if (status)
	{
		std::string currentSsid = m_WifiManager->connectedSsid();
		long timeRemaining = beginTime + t_Timeout - clock();

		if (!currentSsid.empty())
		{
			if (waitForDisconnect(timeRemaining).empty())
			{
				//EXCEPTION
			}
		}

		timeRemaining = beginTime + t_Timeout - clock();
		return waitForConnect(t_WifiNetworkConfig.getSsid(), timeRemaining);
	}
	else
	{
		return std::string("");
	}
}

void WifiHelperImpl::removeWifiNetwork(const std::string& t_ProfileName)
{
	if (!m_WifiManager->removeNetwork(t_ProfileName))
	{
		//EXCEPTION
	}
}

void WifiHelperImpl::checkForWifiEnabled()
{
	if (!isWifiEnabled())
	{
		throw WifiNotEnabledException();
	}
}

std::string WifiHelperImpl::createWifiConfiguration(const std::string& t_Ssid,
	const std::string& t_Password, const std::string& t_SecurityType)
{
	std::string profile;

	if (t_SecurityType.compare("OPEN") == 0)
	{
		profile.append("<?xml version=\"1.0\"?>");
		profile.append("<WLANProfile xmlns=\"http://www.microsoft.com/networking/WLAN/profile/v1\">");
		profile.append("<name>").append(t_Ssid).append("</name>");
		profile.append("<SSIDConfig><SSID>");
		profile.append("<name>").append(t_Ssid).append("</name>");
		profile.append("</SSID></SSIDConfig>");
		profile.append("<connectionType>ESS</connectionType>");
		profile.append("<connectionMode>manual</connectionMode>");
		profile.append("<MSM><security><authEncryption>");
		profile.append("<authentication>open</authentication>");
		profile.append("<encryption>none</encryption>");
		profile.append("<useOneX>false</useOneX>");
		profile.append("</authEncryption></security></MSM>");
		profile.append("</WLANProfile>");
	}
	else if (t_SecurityType.compare("WEP") == 0)
	{
		profile.append("<?xml version=\"1.0\"?>");
		profile.append("<WLANProfile xmlns=\"http://www.microsoft.com/networking/WLAN/profile/v1\">");
		profile.append("<name>").append(t_Ssid).append("</name>");
		profile.append("<SSIDConfig><SSID>");
		profile.append("<name>").append(t_Ssid).append("</name>");
		profile.append("</SSID></SSIDConfig>");
		profile.append("<connectionType>ESS</connectionType>");
		profile.append("<connectionMode>manual</connectionMode>");
		profile.append("<MSM><security>");
		profile.append("<authEncryption>");
		profile.append("<authentication>open</authentication>");
		profile.append("<encryption>WEP</encryption>");
		profile.append("<useOneX>false</useOneX>");
		profile.append("</authEncryption>");
		profile.append("<sharedKey>");
		profile.append("<keyType>networkKey</keyType>");
		profile.append("<protected>false</protected>");
		profile.append("<keyMaterial>").append(t_Password).append("</keyMaterial>");
		profile.append("</sharedKey>");
		profile.append("<keyIndex>0</keyIndex>");
		profile.append("</security></MSM>");
		profile.append("</WLANProfile>");
	}
	else if (t_SecurityType.compare("WPA_TKIP") == 0)
	{
		profile.append("<?xml version=\"1.0\"?>");
		profile.append("<WLANProfile xmlns=\"http://www.microsoft.com/networking/WLAN/profile/v1\">");
		profile.append("<name>").append(t_Ssid).append("</name>");
		profile.append("<SSIDConfig><SSID>");
		profile.append("<name>").append(t_Ssid).append("</name>");
		profile.append("</SSID></SSIDConfig>");
		profile.append("<connectionType>ESS</connectionType>");
		profile.append("<connectionMode>manual</connectionMode>");
		profile.append("<MSM><security>");
		profile.append("<authEncryption>");
		profile.append("<authentication>WPAPSK</authentication>");
		profile.append("<encryption>TKIP</encryption>");
		profile.append("<useOneX>false</useOneX>");
		profile.append("</authEncryption>");
		profile.append("<sharedKey>");
		profile.append("<keyType>passPhrase</keyType>");
		profile.append("<protected>false</protected>");
		profile.append("<keyMaterial>").append(t_Password).append("</keyMaterial>");
		profile.append("</sharedKey>");
		profile.append("</security></MSM>");
		profile.append("</WLANProfile>");
	}
	else if (t_SecurityType.compare("WPA_CCMP") == 0)
	{
		profile.append("<?xml version=\"1.0\"?>");
		profile.append("<WLANProfile xmlns=\"http://www.microsoft.com/networking/WLAN/profile/v1\">");
		profile.append("<name>").append(t_Ssid).append("</name>");
		profile.append("<SSIDConfig><SSID>");
		profile.append("<name>").append(t_Ssid).append("</name>");
		profile.append("</SSID></SSIDConfig>");
		profile.append("<connectionType>ESS</connectionType>");
		profile.append("<connectionMode>manual</connectionMode>");
		profile.append("<MSM><security>");
		profile.append("<authEncryption>");
		profile.append("<authentication>WPAPSK</authentication>");
		profile.append("<encryption>AES</encryption>");
		profile.append("<useOneX>false</useOneX>");
		profile.append("</authEncryption>");
		profile.append("<sharedKey>");
		profile.append("<keyType>passPhrase</keyType>");
		profile.append("<protected>false</protected>");
		profile.append("<keyMaterial>").append(t_Password).append("</keyMaterial>");
		profile.append("</sharedKey>");
		profile.append("</security></MSM>");
		profile.append("</WLANProfile>");
	}
	else if (t_SecurityType.compare("WPA2_TKIP") == 0)
	{
		profile.append("<?xml version=\"1.0\"?>");
		profile.append("<WLANProfile xmlns=\"http://www.microsoft.com/networking/WLAN/profile/v1\">");
		profile.append("<name>").append(t_Ssid).append("</name>");
		profile.append("<SSIDConfig><SSID>");
		profile.append("<name>").append(t_Ssid).append("</name>");
		profile.append("</SSID></SSIDConfig>");
		profile.append("<connectionType>ESS</connectionType>");
		profile.append("<connectionMode>manual</connectionMode>");
		profile.append("<MSM><security>");
		profile.append("<authEncryption>");
		profile.append("<authentication>WPA2PSK</authentication>");
		profile.append("<encryption>TKIP</encryption>");
		profile.append("<useOneX>false</useOneX>");
		profile.append("</authEncryption>");
		profile.append("<sharedKey>");
		profile.append("<keyType>passPhrase</keyType>");
		profile.append("<protected>false</protected>");
		profile.append("<keyMaterial>").append(t_Password).append("</keyMaterial>");
		profile.append("</sharedKey>");
		profile.append("</security></MSM>");
		profile.append("</WLANProfile>");
	}
	else if (t_SecurityType.compare("WPA2_CCMP") == 0)
	{
		profile.append("<?xml version=\"1.0\"?>");
		profile.append("<WLANProfile xmlns=\"http://www.microsoft.com/networking/WLAN/profile/v1\">");
		profile.append("<name>").append(t_Ssid).append("</name>");
		profile.append("<SSIDConfig><SSID>");
		profile.append("<name>").append(t_Ssid).append("</name>");
		profile.append("</SSID></SSIDConfig>");
		profile.append("<connectionType>ESS</connectionType>");
		profile.append("<connectionMode>manual</connectionMode>");
		profile.append("<MSM><security>");
		profile.append("<authEncryption>");
		profile.append("<authentication>WPA2PSK</authentication>");
		profile.append("<encryption>AES</encryption>");
		profile.append("<useOneX>false</useOneX>");
		profile.append("</authEncryption>");
		profile.append("<sharedKey>");
		profile.append("<keyType>passPhrase</keyType>");
		profile.append("<protected>false</protected>");
		profile.append("<keyMaterial>").append(t_Password).append("</keyMaterial>");
		profile.append("</sharedKey>");
		profile.append("</security></MSM>");
		profile.append("</WLANProfile>");
	}
	else
	{
		LOG(INFO) << "Security type not supported";
	}

	return profile;
}
