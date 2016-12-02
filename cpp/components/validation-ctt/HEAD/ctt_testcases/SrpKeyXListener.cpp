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
#include "SrpKeyXListener.h"

SrpKeyXListener::SrpKeyXListener(SrpKeyXHandlerImpl* t_AuthPasswordHandlerImpl, const std::string& t_DefaultSrpKeyXPincode) :
	m_PasswordHandler(t_AuthPasswordHandlerImpl), m_DefaultPincode(t_DefaultSrpKeyXPincode){}

bool SrpKeyXListener::RequestCredentials(const char* t_AuthMechanism,
	const char* t_AuthPeer, uint16_t t_AuthCount, const char* t_UserID,
	uint16_t t_CredMask, Credentials& t_Creds)
{
	if (t_CredMask & AuthListener::CRED_PASSWORD)
	{
		const char* pinCode = m_DefaultPincode.c_str();
		const char* storedPass = m_PasswordHandler->getPassword(t_AuthPeer);

		if (m_PasswordHandler != nullptr && storedPass != nullptr)
		{
			pinCode = storedPass;
		}

		t_Creds.SetPassword(qcc::String(pinCode));	
	}

	return true;
}

void SrpKeyXListener::AuthenticationComplete(const char* t_AuthMechanism,
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