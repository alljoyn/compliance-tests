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
#include "AuthListeners.h"

#include "SrpKeyXListener.h"
#include "SrpLogonListener.h"
#include "ECDHENullListener.h"
#include "ECDHEPskListener.h"
#include "ECDHEEcdsaListener.h"
#include "ECDHESpekeListener.h"

AuthListeners::AuthListeners(bool t_SupportsSrpKeyX, SrpKeyXHandlerImpl* t_SrpKeyXHandler, const std::string& t_DefaultSrpKeyXPincode,
	bool t_SupportsSrpLogon, SrpLogonHandlerImpl* t_SrpLogonHandler, const std::string& t_DefaultLogonUser, const std::string& t_DefaultLogonPass,
	bool t_SupportsEcdheNull, ECDHENullHandlerImpl* t_ECDHENullHandler, 
	bool t_SupportsEcdhePsk, ECDHEPskHandlerImpl* t_ECDHEPskHandler, const std::string& t_DefaultECDHEPskPassword,
	bool t_SupportsEcdheEcdsa, ECDHEEcdsaHandlerImpl* t_ECDHEEcdsaHandler, const std::string& t_DefaultECDHEEcdsaPrivateKey, const std::string& t_DefaultECDHEEcdsaCertChain,
	bool t_SupportsEcdheSpeke, ECDHESpekeHandlerImpl* t_ECDHESpekeHandler, const std::string& t_DefaultECDHESpekePassword)
{
	if (t_SupportsSrpKeyX)
	{
		m_AuthListeners.insert(std::pair<std::string, ajn::AuthListener*>("ALLJOYN_SRP_KEYX", new SrpKeyXListener(t_SrpKeyXHandler, t_DefaultSrpKeyXPincode)));
	}
	
	if (t_SupportsSrpLogon)
	{
		m_AuthListeners.insert(std::pair<std::string, ajn::AuthListener*>("ALLJOYN_SRP_LOGON", 
			new SrpLogonListener(t_SrpLogonHandler, t_DefaultLogonUser, t_DefaultLogonPass)));
	}
	
	if (t_SupportsEcdheNull)
	{
		m_AuthListeners.insert(std::pair<std::string, ajn::AuthListener*>("ALLJOYN_ECDHE_NULL", new ECDHENullListener(t_ECDHENullHandler)));
	}
	
	if (t_SupportsEcdhePsk)
	{
		m_AuthListeners.insert(std::pair<std::string, ajn::AuthListener*>("ALLJOYN_ECDHE_PSK", new ECDHEPskListener(t_ECDHEPskHandler, t_DefaultECDHEPskPassword)));
	}
	
	if (t_SupportsEcdheEcdsa)
	{
		m_AuthListeners.insert(std::pair<std::string, ajn::AuthListener*>("ALLJOYN_ECDHE_ECDSA",
			new ECDHEEcdsaListener(t_ECDHEEcdsaHandler, t_DefaultECDHEEcdsaPrivateKey, t_DefaultECDHEEcdsaCertChain)));
	}
	
	if (t_SupportsEcdheSpeke)
	{
		m_AuthListeners.insert(std::pair<std::string, ajn::AuthListener*>("ALLJOYN_ECDHE_SPEKE",
			new ECDHESpekeListener(t_ECDHESpekeHandler, t_DefaultECDHESpekePassword)));
	}
}

bool AuthListeners::RequestCredentials(const char* t_AuthMechanism,
	const char* t_AuthPeer, uint16_t t_AuthCount, const char* t_UserID,
	uint16_t t_CredMask, Credentials& t_Creds)
{
	LOG(INFO) << " ** requested, mechanism = " << t_AuthMechanism
		<< " peer = " << t_AuthPeer;

	ajn::AuthListener* listener = m_AuthListeners.at(t_AuthMechanism);

	if (listener != nullptr)
	{
		return listener->RequestCredentials(t_AuthMechanism, t_AuthPeer, t_AuthCount, t_UserID, t_CredMask, t_Creds);
	}
	else
	{
		return false;
	}
}

void AuthListeners::AuthenticationComplete(const char* t_AuthMechanism,
	const char* t_AuthPeer, bool t_Success)
{
	ajn::AuthListener* listener = nullptr;
	
	if (!std::string(t_AuthMechanism).empty())
	{
		listener = m_AuthListeners.at(t_AuthMechanism);
	}

	if (listener != nullptr)
	{
		listener->AuthenticationComplete(t_AuthMechanism, t_AuthPeer, t_Success);
	}
	else
	{
		FAIL() << "Auth mechanism '" << t_AuthMechanism << "' is not configured as supported";
	}
}

std::vector<std::string> AuthListeners::getAuthMechanisms()
{
	std::vector<std::string> mechanisms;

	for (auto mechanism : m_AuthListeners)
	{
		mechanisms.push_back(mechanism.first);
	}

	return mechanisms;
}

std::string AuthListeners::getAuthMechanismsAsString()
{
	std::string separator(" ");
	std::string mechanisms;

	for (auto mechanism : m_AuthListeners)
	{
		mechanisms.append(mechanism.first).append(separator);
	}

	return mechanisms;
}