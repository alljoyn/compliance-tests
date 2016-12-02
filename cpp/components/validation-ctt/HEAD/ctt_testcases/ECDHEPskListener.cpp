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
#include "ECDHEPskListener.h"

#include "ArrayParser.h"

//#include <qcc\GUID.h>

ECDHEPskListener::ECDHEPskListener(ECDHEPskHandlerImpl* t_ECDHEPskHandlerImpl, const std::string& t_DefaultECDHEPskPassword) :
	m_PasswordHandler(t_ECDHEPskHandlerImpl), m_DefaultPassword(t_DefaultECDHEPskPassword) {}

bool ECDHEPskListener::RequestCredentials(const char* t_AuthMechanism,
	const char* t_AuthPeer, uint16_t t_AuthCount, const char* t_UserID,
	uint16_t t_CredMask, Credentials& t_Creds)
{
	const char* password = m_DefaultPassword.c_str();
	const char* storedPass = m_PasswordHandler->getPassword(t_AuthPeer);

	if (m_PasswordHandler != nullptr && storedPass != nullptr)
	{
		password = storedPass;
	}

	qcc::String outpsk;
	//outpsk.assign(reinterpret_cast<const char*>(qcc::GUID128(password).GetBytes()), qcc::GUID128::SIZE);
	size_t byteArraySize;
	uint8_t* bytes = ArrayParser::parseBytesFromHexString(password, byteArraySize);
	outpsk.assign(reinterpret_cast<const char*>(bytes), byteArraySize);
	t_Creds.SetPassword(outpsk);    /* The credentials class has only one way to store pre-shared credentials. */
	outpsk.clear();

	return true;
}

void ECDHEPskListener::AuthenticationComplete(const char* t_AuthMechanism,
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