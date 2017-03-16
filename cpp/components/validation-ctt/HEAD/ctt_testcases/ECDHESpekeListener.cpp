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
#include "stdafx.h"
#include "ECDHESpekeListener.h"

#include <qcc\GUID.h>

ECDHESpekeListener::ECDHESpekeListener(ECDHESpekeHandlerImpl* t_ECDHEPskHandlerImpl, const std::string& t_DefaultECDHESpekePassword) :
	m_PasswordHandler(t_ECDHEPskHandlerImpl), m_DefaultPassword(t_DefaultECDHESpekePassword) {}

bool ECDHESpekeListener::RequestCredentials(AJ_PCSTR t_AuthMechanism,
	AJ_PCSTR t_AuthPeer, uint16_t t_AuthCount, AJ_PCSTR t_UserID,
	uint16_t t_CredMask, Credentials& t_Creds)
{
	if (t_AuthCount > 10)
	{
		/* If the peer making a large number of attempts, they may be an attacking trying to guess the password. */
		LOG(DEBUG) << "RequestCredentials called for ECDHE_SPEKE more than 10 times, authentication will fail.";
		return false;
	}

	if (ajn::AuthListener::CRED_PASSWORD & t_CredMask)
	{
		AJ_PCSTR password = m_DefaultPassword.c_str();
		AJ_PCSTR storedPass = m_PasswordHandler->getPassword(t_AuthPeer);

		if (m_PasswordHandler != nullptr && storedPass != nullptr)
		{
			password = storedPass;
		}

		t_Creds.SetPassword(password);
	}

	if (ajn::AuthListener::CRED_EXPIRATION & t_CredMask)
	{
		t_Creds.SetExpiration(300);
	}

	return true;
}

void ECDHESpekeListener::AuthenticationComplete(AJ_PCSTR t_AuthMechanism,
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