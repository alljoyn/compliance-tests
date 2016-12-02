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
#include "OnboardingHelper.h"

#include "ArrayParser.h"
#include "AvailableSoftAPException.h"

#include <alljoyn\onboarding\OnboardingService.h>

const long OnboardingHelper::TIME_TO_WAIT_TO_CONNECT_TO_PERSONAL_AP = 60000;
const long OnboardingHelper::TIME_TO_WAIT_FOR_NEXT_DEVICE_ANNOUNCEMENT = 180000;
const long OnboardingHelper::TIME_TO_WAIT_FOR_SOFT_AP = 120000;
const long OnboardingHelper::TIME_TO_WAIT_TO_CONNECT_TO_SOFT_AP = 60000;
const long OnboardingHelper::TIME_TO_WAIT_FOR_DISCONNECT = 30000;

const int OnboardingHelper::NUMBER_OF_DEVICE_ID_DIGITS_IN_SOFT_AP = 7;
const short OnboardingHelper::MAX_NUMBER_OF_TRIES_TO_CONNECT_TO_SOFT_AP = 5;

OnboardingHelper::OnboardingHelper(const std::string& t_BusApplicationName,
	const int t_TimeToWaitForScanResults) :
	m_BusApplicationName(t_BusApplicationName),
	m_TimeToWaitForScanResults(t_TimeToWaitForScanResults) {}

std::string OnboardingHelper::mapAuthTypeToAuthTypeString(const short t_AuthType)
{
	std::string authTypeString;

	switch (t_AuthType)
	{
		case ajn::services::OBAuthType::WPA2_AUTO:
		{
			authTypeString = "WPA2_AUTO";
			break;
		}
		case ajn::services::OBAuthType::WPA2_CCMP:
		{
			authTypeString = "WPA2_CCMP";
			break;
		}
		case ajn::services::OBAuthType::WPA2_TKIP:
		{
			authTypeString = "WPA2_TKIP";
			break;
		}
		case ajn::services::OBAuthType::WPA_AUTO:
		{
			authTypeString = "WPA_AUTO";
			break;
		}
		case ajn::services::OBAuthType::WPA_CCMP:
		{
			authTypeString = "WPA_CCMP";
			break;
		}
		case ajn::services::OBAuthType::WPA_TKIP:
		{
			authTypeString = "WPA_TKIP";
			break;
		}
		case ajn::services::OBAuthType::WEP:
		{
			authTypeString = "WEP";
			break;
		}
		case ajn::services::OBAuthType::WPS:
		{
			authTypeString = "WPS";
			break;
		}
		case ajn::services::OBAuthType::OPEN:
		{
			authTypeString = "OPEN";
			break;
		}
		case ajn::services::OBAuthType::ANY:
		{
			authTypeString = "ANY";
			break;
		}
	}

	return authTypeString;
}

void OnboardingHelper::setPersonalAPConfig(WifiNetworkConfig* t_PersonalApConfig)
{
	m_PersonalApConfig = t_PersonalApConfig;
}

WifiNetworkConfig* OnboardingHelper::getPersonalAPConfig()
{
	return m_PersonalApConfig;
}

void OnboardingHelper::setSoftAPConfig(WifiNetworkConfig* t_SoftApConfig)
{
	m_SoftApConfig = t_SoftApConfig;
}

WifiNetworkConfig* OnboardingHelper::getSoftAPConfig()
{
	return m_SoftApConfig;
}

QStatus OnboardingHelper::initialize(const std::string& t_KeystorePath,
	const std::string& t_DeviceId, uint8_t* t_AppId,
	const bool t_SupportsSrpKeyX, const std::string& t_DefaultSrpXPincode,
	const bool t_SupportsSrpLogon, const std::string& t_DefaultLogonUser, const std::string& t_DefaultLogonPass,
	const bool t_SupportsEcdheNull,
	const bool t_SupportsEcdhePsk, const std::string& t_DefaultECDHEPskPassword,
	const bool t_SupportsEcdheEcdsa, const std::string& t_DefaultECDHEEcdsaPrivateKey, const std::string& t_DefaultECDHEEcdsaCertChain,
	const bool t_SupportsEcdheSpeke, const std::string& t_DefaultECDHESpekePassword)
{
	m_DeviceId = t_DeviceId;
	m_AppId = t_AppId;
	m_KeystorePath = t_KeystorePath;

	QStatus status = initServiceHelper(m_SupportsSrpKeyX = t_SupportsSrpKeyX, m_DefaultSrpXPincode = t_DefaultSrpXPincode,
		m_SupportsSrpLogon = t_SupportsSrpLogon, m_DefaultLogonUser = t_DefaultLogonUser, m_DefaultLogonPass = t_DefaultLogonPass,
		m_SupportsEcdheNull = t_SupportsEcdheNull,
		m_SupportsEcdhePsk = t_SupportsEcdhePsk, m_DefaultECDHEPskPassword = t_DefaultECDHEPskPassword,
		m_SupportsEcdheEcdsa = t_SupportsEcdheEcdsa, m_DefaultECDHEEcdsaPrivateKey = t_DefaultECDHEEcdsaPrivateKey, m_DefaultECDHEEcdsaCertChain = t_DefaultECDHEEcdsaCertChain,
		m_SupportsEcdheSpeke = t_SupportsEcdheSpeke, m_DefaultECDHESpekePassword = t_DefaultECDHESpekePassword);
	if (ER_OK != status)
	{
		return status;
	}

	m_WifiHelper = new WifiHelperImpl();
	m_WifiHelper->initialize();

	return status;
}

QStatus OnboardingHelper::initServiceHelper(const bool t_SupportsSrpKeyX, const std::string& t_DefaultSrpXPincode,
	const bool t_SupportsSrpLogon, const std::string& t_DefaultLogonUser, const std::string& t_DefaultLogonPass,
	const bool t_SupportsEcdheNull,
	const bool t_SupportsEcdhePsk, const std::string& t_DefaultECDHEPskPassword,
	const bool t_SupportsEcdheEcdsa, const std::string& t_DefaultECDHEEcdsaPrivateKey, const std::string& t_DefaultECDHEEcdsaCertChain,
	const bool t_SupportsEcdheSpeke, const std::string& t_DefaultECDHESpekePassword)
{
	releaseServiceHelper();
	m_ServiceHelper = new ServiceHelper();
	QStatus status = m_ServiceHelper->initializeClient(m_BusApplicationName, m_DeviceId, m_AppId,
		t_SupportsSrpKeyX, t_DefaultSrpXPincode,
		t_SupportsSrpLogon, t_DefaultLogonUser, t_DefaultLogonPass,
		t_SupportsEcdheNull,
		t_SupportsEcdhePsk, t_DefaultECDHEPskPassword,
		t_SupportsEcdheEcdsa, t_DefaultECDHEEcdsaPrivateKey, t_DefaultECDHEEcdsaCertChain,
		t_SupportsEcdheSpeke, t_DefaultECDHESpekePassword);
	if (status != ER_OK)
	{
		return status;
	}

	//m_config_client = m_ServiceHelper->connectConfigClient(m_session_id);

	return m_ServiceHelper->enableAuthentication(m_KeystorePath);
}

void OnboardingHelper::releaseServiceHelper()
{
	disconnectOnboardingClient();

	if (m_ServiceHelper != nullptr)
	{
		QStatus status = m_ServiceHelper->release();

		EXPECT_EQ(ER_OK, status) << "serviceHelper Release() failed: " << QCC_StatusText(status);
		m_ServiceHelper = nullptr;
	}
}

void OnboardingHelper::disconnectOnboardingClient()
{
	if (m_OnboardingClient != nullptr)
	{
		LOG(INFO) << "Disconnecting onboarding client";
		delete m_OnboardingClient;
		m_OnboardingClient = nullptr;
	}
}

void OnboardingHelper::connectToPersonalAPIfNeeded()
{
	std::string currentSsid = m_WifiHelper->getCurrentSSID();
	if (currentSsid.empty() || !(currentSsid.compare(m_PersonalApConfig->getSsid())) == 0)
	{
		connectToPersonalAP();
	}
	else
	{
		LOG(INFO) << "Already connected to " << currentSsid;
	}
}

QStatus OnboardingHelper::connectToPersonalAP()
{
	releaseServiceHelper();
	std::string personalApNetworkName = m_PersonalApConfig->getSsid();

	std::string connectedSsid;
	int num_tries = 0;

	while ((connectedSsid.empty() || (personalApNetworkName.compare(connectedSsid) != 0)) && (num_tries < 2))
	{
		LOG(INFO) << "Attempting to connect to Personal AP network";
		connectedSsid = m_WifiHelper->connectToNetwork(*m_PersonalApConfig, false, TIME_TO_WAIT_TO_CONNECT_TO_PERSONAL_AP);
		num_tries++;
	}

	if (connectedSsid.empty())
	{
		//throw Exception
	}

	if (connectedSsid.compare(personalApNetworkName) != 0)
	{
		//throw Exception
	}

	return initServiceHelper(m_SupportsSrpKeyX, m_DefaultSrpXPincode,
		m_SupportsSrpLogon, m_DefaultLogonUser, m_DefaultLogonPass,
		m_SupportsEcdheNull,
		m_SupportsEcdhePsk, m_DefaultECDHEPskPassword,
		m_SupportsEcdheEcdsa, m_DefaultECDHEEcdsaPrivateKey, m_DefaultECDHEEcdsaCertChain,
		m_SupportsEcdheSpeke, m_DefaultECDHESpekePassword);
}

bool OnboardingHelper::isDeviceInOnboardedState()
{
	bool isDeviceInOnboardedState = false;

	LOG(INFO) << "Checking if DUT is in Onboarded state";

	std::string personalApNetworkName = m_PersonalApConfig->getSsid();
	std::string softApSsid = getSoftAPSsid();

	std::string currentSsid;
	do
	{
		currentSsid = m_WifiHelper->getCurrentSSID();

		if (currentSsid.empty())
		{
			connectToPersonalAP();
		}
		else if (personalApNetworkName.compare(currentSsid) == 0)
		{
			clock_t startTime = clock();
			long timeToWaitInMs = TIME_TO_WAIT_FOR_NEXT_DEVICE_ANNOUNCEMENT;
			bool isSoftApAvailable = false;
			m_DeviceAboutAnnouncement = nullptr;

			while (!isSoftApAvailable && (m_DeviceAboutAnnouncement == nullptr) && (clock() < startTime + timeToWaitInMs))
			{
				m_DeviceAboutAnnouncement = m_ServiceHelper->waitForNextDeviceAnnouncement(1000);

				if (m_DeviceAboutAnnouncement == nullptr && !softApSsid.empty())
				{
					isSoftApAvailable = m_WifiHelper->waitForNetworkAvailable(softApSsid, m_TimeToWaitForScanResults);
				}
			}

			if (m_DeviceAboutAnnouncement != nullptr)
			{
				isDeviceInOnboardedState = true;
				m_AboutProxy = m_ServiceHelper->connectAboutProxy(*m_DeviceAboutAnnouncement);
				m_OnboardingClient = m_ServiceHelper->connectOnboardingClient(*m_DeviceAboutAnnouncement);
			}
			else if (!isSoftApAvailable)
			{
				//throw exception
			}
		}
		else if (currentSsid.compare(softApSsid) == 0)
		{
			isDeviceInOnboardedState = false;
		}
	}
	while (currentSsid.empty());

	if (isDeviceInOnboardedState)
	{
		LOG(INFO) << "DUT is in Onboarded state";
	}
	else
	{
		LOG(INFO) << "DUT is in Offboarded state";
	}

	return isDeviceInOnboardedState;
}

std::string OnboardingHelper::getSoftAPSsid()
{
	return getSoftAPConfig() != nullptr ? getSoftAPConfig()->getSsid() : "";
}

QStatus OnboardingHelper::retrieveVersionProperty(int& t_Version)
{
	LOG(INFO) << "Calling Onboarding.GetVersion() method";
	QStatus status = m_OnboardingClient->GetVersion(m_DeviceAboutAnnouncement->getServiceName().c_str(), 
		t_Version, m_ServiceHelper->getSessionId());
	return status;
}

QStatus OnboardingHelper::callOffboard()
{
	LOG(INFO) << "Calling Onboarding.Offboard() method on DUT";
	return m_OnboardingClient->OffboardFrom(m_DeviceAboutAnnouncement->getServiceName().c_str(),
		m_ServiceHelper->getSessionId());
}

std::string OnboardingHelper::connectToDUTOnSoftAP()
{
	releaseServiceHelper();

	bool isSoftApAvailable = false;
	std::string ssid = getSoftAPSsid();

	if (ssid.empty())
	{
		LOG(INFO) << "Waiting to determine the soft AP name";

		long timeToWaitInMs = TIME_TO_WAIT_FOR_SOFT_AP;
		clock_t startTime = clock();

		while (ssid.empty() && (clock() < startTime + timeToWaitInMs))
		{
			long timeRemaining = startTime + timeToWaitInMs - clock();
			std::list<ScanResult> availableAccessPoints = m_WifiHelper->waitForScanResults(timeRemaining);

			if (!availableAccessPoints.empty())
			{
				for (auto scanResult : availableAccessPoints)
				{
					if ((starts_with(scanResult.getSsid(), "AJ_") || ends_with(scanResult.getSsid(), "_AJ"))
						&& softAPMatchesDeviceId(scanResult.getSsid(), m_DeviceId))
					{
						ssid = scanResult.getSsid();
						LOG(INFO) << "Found matching ssid: " << ssid << " for deviceId: " << m_DeviceId;
						break;
					}
				}
			}
		}

		if (!ssid.empty())
		{
			m_SoftApConfig->setSsid(ssid);
		}
	}

	if (!ssid.empty())
	{
		isSoftApAvailable = m_WifiHelper->waitForNetworkAvailable(ssid, TIME_TO_WAIT_FOR_SOFT_AP);
	}

	if (!isSoftApAvailable)
	{
		//throw exception
	}

	bool connectedToSoftAp = connectToSoftAPNetwork();
	if (!connectedToSoftAp)
	{
		//throw exception
	}

	initServiceHelper(m_SupportsSrpKeyX, m_DefaultSrpXPincode,
		m_SupportsSrpLogon, m_DefaultLogonUser, m_DefaultLogonPass,
		m_SupportsEcdheNull,
		m_SupportsEcdhePsk, m_DefaultECDHEPskPassword,
		m_SupportsEcdheEcdsa, m_DefaultECDHEEcdsaPrivateKey, m_DefaultECDHEEcdsaCertChain,
		m_SupportsEcdheSpeke, m_DefaultECDHESpekePassword);

	return ssid;
}

inline bool OnboardingHelper::starts_with(const std::string &t_Value, const std::string &t_Starting)
{
	if (t_Starting.size() > t_Value.size()) return false;
	return t_Value.find(t_Starting) == 0;
}

inline bool OnboardingHelper::ends_with(const std::string &t_Value, const std::string &t_Ending)
{
	if (t_Ending.size() > t_Value.size()) return false;
	return std::equal(t_Ending.rbegin(), t_Ending.rend(), t_Value.rbegin());
}

bool OnboardingHelper::softAPMatchesDeviceId(const std::string& t_Ssid, const std::string& t_DeviceId)
{
	std::string device_id_suffix = t_Ssid.substr(t_Ssid.length() - NUMBER_OF_DEVICE_ID_DIGITS_IN_SOFT_AP);
	return ends_with(t_DeviceId, device_id_suffix);
}

bool OnboardingHelper::connectToSoftAPNetwork()
{
	std::string ssid = getSoftAPSsid();
	std::string connectedSsid;
	size_t numTries = 0;

	while (((connectedSsid.empty() || !ssid.compare(connectedSsid) == 0)) && (numTries < MAX_NUMBER_OF_TRIES_TO_CONNECT_TO_SOFT_AP))
	{
		LOG(INFO) << "Attempting to connect to Soft AP network";

		connectedSsid = m_WifiHelper->connectToNetwork(*getSoftAPConfig(), true, TIME_TO_WAIT_TO_CONNECT_TO_SOFT_AP);
		++numTries;
	}

	return ssid.compare(connectedSsid) == 0;
}

AboutAnnouncementDetails* OnboardingHelper::waitForAboutAnnouncementAndThenConnect()
{
	bool foundMatch = false;
	m_DeviceAboutAnnouncement = nullptr;
	long timeToWaitInMs = TIME_TO_WAIT_FOR_NEXT_DEVICE_ANNOUNCEMENT;
	long startTime = clock();

	std::string currentSsid = m_WifiHelper->getCurrentSSID();
	std::string personalApNetworkName = m_PersonalApConfig->getSsid();
	bool connectedtoPersonalAp = personalApNetworkName.compare(currentSsid) == 0;

	std::string softApSsid = getSoftAPSsid();

	while (!foundMatch && (clock() < startTime + timeToWaitInMs))
	{
		m_DeviceAboutAnnouncement = waitForNextAboutAnnouncementFromDevice(1000);

		if (m_DeviceAboutAnnouncement != nullptr)
		{
			foundMatch = true;
		}
		else if (connectedtoPersonalAp && !softApSsid.empty())
		{
			bool isSoftApAvailable = m_WifiHelper->waitForNetworkAvailable(softApSsid, m_TimeToWaitForScanResults);

			if (isSoftApAvailable)
			{
				std::string message("Soft AP became available while waiting to receive About announcement from app ");

				if (!m_DeviceId.empty() && m_AppId != nullptr)
				{
					message.append("with deviceId: ").append(m_DeviceId).append(", appId: ")
						.append(ArrayParser::parseAppId(m_AppId));
				}
				else
				{
					message.append("supporting Onboarding and having a deviceId ending in ").append(m_DeviceIdSuffix);
				}

				throw AvailableSoftAPException(message);
			}
		}
		else if (!connectedtoPersonalAp && !softApSsid.empty())
		{
			if (softApSsid.compare(m_WifiHelper->getCurrentSSID()) != 0)
			{
				if (false == connectToSoftAPNetwork())
				{
					std::string message("Soft AP became unavailable while waiting to receive About announcement from app");
					//throw Exception
				}
			}
		}
	}

	if (m_DeviceAboutAnnouncement == nullptr)
	{
		std::string message("Timed out waiting for About announcement from app ");

		if (!m_DeviceId.empty() && m_AppId != nullptr)
		{
			message.append("with deviceId: ").append(m_DeviceId).append("; appId: ").append(ArrayParser::parseAppId(m_AppId));
		}
		else
		{
			message.append("supporting Onboarding and having a deviceId ending in: ").append(m_DeviceIdSuffix);
		}

		//throw Exception
	}

	m_OnboardingClient = m_ServiceHelper->connectOnboardingClient(*m_DeviceAboutAnnouncement);
	return m_DeviceAboutAnnouncement;
}

AboutAnnouncementDetails* OnboardingHelper::waitForNextAboutAnnouncementFromDevice(const long t_TimeRemaining)
{
	AboutAnnouncementDetails* deviceAboutAnnouncement = m_ServiceHelper->waitForNextDeviceAnnouncement(t_TimeRemaining);

	if (deviceAboutAnnouncement != nullptr)
	{
		if (!haveDeviceIdAndAppId() && !announcementMatchesDeviceIdSuffix(*deviceAboutAnnouncement))
		{
			deviceAboutAnnouncement = nullptr;
		}
	}

	return deviceAboutAnnouncement;
}

bool OnboardingHelper::haveDeviceIdAndAppId()
{
	return (!m_DeviceId.empty() && m_AppId != nullptr);
}

bool OnboardingHelper::announcementMatchesDeviceIdSuffix(AboutAnnouncementDetails& t_DeviceAboutAnnouncement)
{
	return t_DeviceAboutAnnouncement.getDeviceId() != nullptr && ends_with(t_DeviceAboutAnnouncement.getDeviceId(), m_DeviceIdSuffix)
		&& t_DeviceAboutAnnouncement.supportsInterface("org.alljoyn.Onboarding");
}

QStatus OnboardingHelper::callConfigureWiFi(WifiNetworkConfig t_NetworkConfig, short& t_ResultStatus)
{
	std::string ssid = t_NetworkConfig.getSsid();
	std::string passphrase = t_NetworkConfig.getPassphrase();
	ajn::services::OBAuthType authType = getOnboardingServiceAuthType(t_NetworkConfig.getSecurityType());

	std::string convertedPassphrase = convertToHex(t_NetworkConfig.getPassphrase());

	LOG(INFO) << "Calling Onboarding.ConfigureWiFi() method on DUT with ssid: " << ssid
		<< " and authType: " << authType;
	ajn::services::OBInfo onboardingInfo;
	onboardingInfo.authType = authType;
	onboardingInfo.passcode.assign(convertedPassphrase.c_str());
	onboardingInfo.SSID.assign(ssid.c_str());

	QStatus status = m_OnboardingClient->ConfigureWiFi(m_DeviceAboutAnnouncement->getServiceName().c_str(),
		onboardingInfo, t_ResultStatus, m_ServiceHelper->getSessionId());

	if (status != ER_OK)
	{
		return status;
	}

	verifyOnboardingState(ajn::services::OBState::CONFIGURED_NOT_VALIDATED);
	
	return status;
}

ajn::services::OBAuthType OnboardingHelper::getOnboardingServiceAuthType(const std::string& t_security_type)
{
	ajn::services::OBAuthType authType = ajn::services::OBAuthType::ANY;

	if (t_security_type.compare("WPA2_AUTO") == 0)
	{
		authType = ajn::services::OBAuthType::WPA2_AUTO;
	}
	else if (t_security_type.compare("WPA2") == 0)
	{
		authType = ajn::services::OBAuthType::WPA2_AUTO;
	}
	else if (t_security_type.compare("WPA_AUTO") == 0)
	{
		authType = ajn::services::OBAuthType::WPA_AUTO;
	}
	else if (t_security_type.compare("WPA") == 0)
	{
		authType = ajn::services::OBAuthType::WPA_AUTO;
	}
	else if (t_security_type.compare("ANY") == 0)
	{
		authType = ajn::services::OBAuthType::ANY;
	}
	else if (t_security_type.compare("OPEN") == 0)
	{
		authType = ajn::services::OBAuthType::OPEN;
	}
	else if (t_security_type.compare("NONE") == 0)
	{
		authType = ajn::services::OBAuthType::OPEN;
	}
	else if (t_security_type.compare("WEP") == 0)
	{
		authType = ajn::services::OBAuthType::WEP;
	}
	else if (t_security_type.compare("WPA_TKIP") == 0)
	{
		authType = ajn::services::OBAuthType::WPA_TKIP;
	}
	else if (t_security_type.compare("WPA_CCMP") == 0)
	{
		authType = ajn::services::OBAuthType::WPA_CCMP;
	}
	else if (t_security_type.compare("WPA2_TKIP") == 0)
	{
		authType = ajn::services::OBAuthType::WPA2_TKIP;
	}
	else if (t_security_type.compare("WPA2_CCMP") == 0)
	{
		authType = ajn::services::OBAuthType::WPA2_CCMP;
	}
	else if (t_security_type.compare("WPS") == 0)
	{
		authType = ajn::services::OBAuthType::WPS;
	}

	return authType;
}

std::string OnboardingHelper::convertToHex(const std::string& t_NetworkPassword)
{
	static const char* const lut = "0123456789ABCDEF";
	size_t len = t_NetworkPassword.length();

	std::string output;
	output.reserve(2 * len);
	for (size_t i = 0; i < len; ++i)
	{
		const unsigned char c = t_NetworkPassword[i];
		output.push_back(lut[c >> 4]);
		output.push_back(lut[c & 15]);
	}

	return output;
}

void OnboardingHelper::verifyOnboardingState(const short t_ExpectedState)
{
	LOG(INFO) << "Retrieving the State property from the Onboarding interface";
	short clientState;
	m_OnboardingClient->GetState(m_DeviceAboutAnnouncement->getServiceName().c_str(), clientState,
		m_ServiceHelper->getSessionId());
	if (t_ExpectedState != clientState)
	{
		//throw Exception
	}
}

void OnboardingHelper::callConnectWiFiAndWaitForSoftAPDisconnect()
{
	LOG(INFO) << "Calling Onboarding.ConnectWiFi() method";
	QStatus status = m_OnboardingClient->ConnectTo(m_DeviceAboutAnnouncement->getServiceName().c_str(),
		m_ServiceHelper->getSessionId());

	waitForSoftAPDisconnect();
}

void OnboardingHelper::waitForSoftAPDisconnect()
{
	std::string disconnectedSsid = m_WifiHelper->waitForDisconnect(TIME_TO_WAIT_FOR_DISCONNECT);

	if (disconnectedSsid.empty())
	{
		//throw Exception
	}
}

QStatus OnboardingHelper::retrieveStateProperty(short& t_State)
{
	LOG(INFO) << "Calling Onboarding.GetState() method";
	QStatus status = m_OnboardingClient->GetState(m_DeviceAboutAnnouncement->getServiceName().c_str(),
		t_State, m_ServiceHelper->getSessionId());

	return status;
}

ajn::services::OBLastError OnboardingHelper::retrieveLastErrorProperty()
{
	LOG(INFO) << "Calling Onboarding.GetLastError() method";
	ajn::services::OBLastError lastError;
	QStatus status = m_OnboardingClient->GetLastError(m_DeviceAboutAnnouncement->getServiceName().c_str(),
		lastError, m_ServiceHelper->getSessionId());

	return lastError;
}

void OnboardingHelper::release()
{
	releaseResources();
}

void OnboardingHelper::releaseResources()
{
	releaseWifiHelper();
	releaseServiceHelper();
}

void OnboardingHelper::releaseWifiHelper()
{
	if (m_WifiHelper != nullptr)
	{
		m_WifiHelper->release();
		m_WifiHelper = nullptr;
	}
}

ajn::services::OnboardingClient::ScanInfos OnboardingHelper::callScanInfo()
{
	LOG(INFO) << "Calling Onboarding.GetScanInfo() method";
	ajn::services::OnboardingClient::ScanInfos scanInfos;
	unsigned short age = 0;
	QStatus status = m_OnboardingClient->GetScanInfo(m_DeviceAboutAnnouncement->getServiceName().c_str(),
		age, scanInfos, m_ServiceHelper->getSessionId());

	return scanInfos;
}

void OnboardingHelper::setPasscode(const AboutAnnouncementDetails& t_AboutAnnouncement, const char* t_Passcode)
{
	if (m_SupportsEcdheSpeke)
	{
		m_ServiceHelper->setEcdheSpekePassword(t_AboutAnnouncement, t_Passcode);
	}
	else if (m_SupportsEcdhePsk)
	{
		m_ServiceHelper->setEcdhePskPassword(t_AboutAnnouncement, t_Passcode);
	}
	else if (m_SupportsSrpKeyX)
	{
		m_ServiceHelper->setSrpKeyXPincode(t_AboutAnnouncement, t_Passcode);
	}
}

ajn::services::ConfigClient* OnboardingHelper::connectConfigClient(ajn::SessionId& t_SessionId)
{
	return m_ServiceHelper->connectConfigClient(t_SessionId);
}

void OnboardingHelper::clearQueuedDeviceAnnouncements()
{
	m_ServiceHelper->clearQueuedDeviceAnnouncements();
}

ajn::AboutProxy* OnboardingHelper::connectAboutProxy(const AboutAnnouncementDetails& t_AboutAnnouncementDetails)
{
	return m_ServiceHelper->connectAboutProxy(t_AboutAnnouncementDetails);
}