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
#include "SrpAnonymousKeyListener.h"

const char* SrpAnonymousKeyListener::DEFAULT_PINCODE = "000000";

SrpAnonymousKeyListener::SrpAnonymousKeyListener(AuthPasswordHandlerImpl* t_PasswordHandler)
{
	m_PasswordHandler = t_PasswordHandler;
	m_AuthMechanisms.push_back("ALLJOYN_SRP_KEYX");
	m_AuthMechanisms.push_back("ALLJOYN_ECDHE_PSK");
}

SrpAnonymousKeyListener::SrpAnonymousKeyListener(AuthPasswordHandlerImpl* t_PasswordHandler,
	std::vector<std::string> t_AuthMechanisms) : 
	SrpAnonymousKeyListener::SrpAnonymousKeyListener(t_PasswordHandler)
{
		for (auto mechanism : t_AuthMechanisms)
		{
			m_AuthMechanisms.push_back(mechanism);
		}
}

bool SrpAnonymousKeyListener::RequestCredentials(const char* t_AuthMechanism,
	const char* t_AuthPeer, uint16_t t_AuthCount, const char* t_UserID,
	uint16_t t_CredMask, Credentials& t_Creds)
{
	LOG(INFO) << " ** requested, mechanism = " << t_AuthMechanism
		<< " peer = " << t_AuthPeer;

	if (std::find(m_AuthMechanisms.begin(), m_AuthMechanisms.end(), t_AuthMechanism) != m_AuthMechanisms.end())
	{
		const char* pinCode = DEFAULT_PINCODE;
		const char* storedPass = m_PasswordHandler->getPassword(t_AuthPeer);

		if (m_PasswordHandler != nullptr && storedPass != nullptr)
		{
			pinCode = storedPass;
		}

		t_Creds.SetPassword(qcc::String(pinCode));
		return true;
	}
	else
	{
		return false;
	}
}

void SrpAnonymousKeyListener::AuthenticationComplete(const char* t_AuthMechanism,
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

std::vector<std::string> SrpAnonymousKeyListener::getAuthMechanisms()
{
	return m_AuthMechanisms;
}

std::string SrpAnonymousKeyListener::getAuthMechanismsAsString()
{
	std::string separator(" ");
	std::string s;

	for (size_t i = 0; i < m_AuthMechanisms.size(); ++i)
	{
		s.append(m_AuthMechanisms[i]);

		if (i != m_AuthMechanisms.size() - 1)
		{
			s.append(separator);
		}
	}

	return s;
}