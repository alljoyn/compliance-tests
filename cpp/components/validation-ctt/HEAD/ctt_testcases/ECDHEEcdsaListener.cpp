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
#include "ECDHEEcdsaListener.h"

ECDHEEcdsaListener::ECDHEEcdsaListener(ECDHEEcdsaHandlerImpl* t_ECDHEPskHandlerImpl, const std::string& t_DefaultECDHEEcdsaPrivateKey,
	const std::string& t_DefaultECDHEEcdsaCertChain) :
	m_PasswordHandler(t_ECDHEPskHandlerImpl), m_DefaultPrivateKey(t_DefaultECDHEEcdsaPrivateKey),
	m_DefaultCertChain(t_DefaultECDHEEcdsaCertChain) {}

bool ECDHEEcdsaListener::RequestCredentials(const char* t_AuthMechanism,
	const char* t_AuthPeer, uint16_t t_AuthCount, const char* t_UserID,
	uint16_t t_CredMask, Credentials& t_Creds)
{
	if ((t_CredMask & AuthListener::CRED_PRIVATE_KEY) == AuthListener::CRED_PRIVATE_KEY)
	{
		const char* privateKey = m_DefaultPrivateKey.c_str();
		const char* storedPrivateKey = m_PasswordHandler->getPrivateKey(t_AuthPeer);

		if (m_PasswordHandler != nullptr && storedPrivateKey != nullptr)
		{
			privateKey = storedPrivateKey;
		}

		t_Creds.SetPrivateKey(qcc::String(privateKey));
	}

	if ((t_CredMask & AuthListener::CRED_CERT_CHAIN) == AuthListener::CRED_CERT_CHAIN)
	{
		const char* certChain = m_DefaultCertChain.c_str();
		const char* storedCertChain = m_PasswordHandler->getCertChain(t_AuthPeer);

		if (m_PasswordHandler != nullptr && storedCertChain != nullptr)
		{
			certChain = storedCertChain;
		}

		t_Creds.SetPrivateKey(qcc::String(certChain));
	}

	return true;
}

void ECDHEEcdsaListener::AuthenticationComplete(const char* t_AuthMechanism,
	const char* t_AuthPeer, bool t_Success)
{
	if (!t_Success)
	{
		LOG(INFO) << " ** " << t_AuthPeer << " failed to authenticate using mechanism " << t_AuthMechanism;
	}
	else
	{
		LOG(INFO) << " ** " << t_AuthPeer << " successfully authenticated using mechanism " << t_AuthMechanism;
		m_PasswordHandler->completed(t_AuthMechanism, t_AuthPeer, t_Success);
	}
}