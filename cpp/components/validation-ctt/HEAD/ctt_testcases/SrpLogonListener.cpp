/******************************************************************************
*    Copyright (c) Open Connectivity Foundation (OCF), AllJoyn Open Source
*    Project (AJOSP) Contributors and others.
*    
*    SPDX-License-Identifier: Apache-2.0
*    
*    All rights reserved. This program and the accompanying materials are
*    made available under the terms of the Apache License, Version 2.0
*    which accompanies this distribution, and is available at
*    http://www.apache.org/licenses/LICENSE-2.0
*    
*    Copyright (c) Open Connectivity Foundation and Contributors to AllSeen
*    Alliance. All rights reserved.
*    
*    Permission to use, copy, modify, and/or distribute this software for
*    any purpose with or without fee is hereby granted, provided that the
*    above copyright notice and this permission notice appear in all
*    copies.
*    
*    THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL
*    WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED
*    WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE
*    AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL
*    DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR
*    PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER
*    TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
*    PERFORMANCE OF THIS SOFTWARE.
******************************************************************************/
#include "stdafx.h"
#include "SrpLogonListener.h"

/* Populate the username, password table used by this listener */
SrpLogonListener::SrpLogonListener(SrpLogonHandlerImpl* t_SrpLogonHandler, const std::string& t_DefaultUser, const std::string& t_DefaultPass) :
	m_PasswordHandler(t_SrpLogonHandler), m_DefaultUser(t_DefaultUser), m_DefaultPass(t_DefaultPass) {}

/*
	Given the user name, lookup the password. Returning true without setting the password
	tells the authentication engine to ask the peer for the username again.

	A pre-computed token called the logon entry may also be supplied instead of the given
	password. See LogonEntryRequest
*/
bool SrpLogonListener::RequestCredentials(AJ_PCSTR t_AuthMechanism,
	AJ_PCSTR t_AuthPeer, uint16_t t_AuthCount, AJ_PCSTR t_UserID,
	uint16_t t_CredMask, Credentials& t_Creds)
{
	if (t_CredMask & AuthListener::CRED_USER_NAME)
	{
		AJ_PCSTR currentUser = m_DefaultUser.c_str();
		AJ_PCSTR currentPass = m_DefaultPass.c_str();

		AJ_PCSTR storedUser = m_PasswordHandler->getUser(t_AuthPeer);
		AJ_PCSTR storedPass = m_PasswordHandler->getPass(t_AuthPeer);

		if (m_PasswordHandler != nullptr && storedUser != nullptr && storedPass != nullptr)
		{
			currentUser = storedUser;
			currentPass = storedPass;
		}

		t_Creds.SetUserName(qcc::String(currentUser));
		t_Creds.SetPassword(qcc::String(currentPass));
	}

	return true;
}

void SrpLogonListener::AuthenticationComplete(AJ_PCSTR t_AuthMechanism,
	AJ_PCSTR t_AuthPeer, bool t_Success)
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