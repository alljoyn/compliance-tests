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
#include "AuthPasswordHandlerImpl.h"

#include "SrpAnonymousKeyListener.h"

using namespace std;

AuthPasswordHandlerImpl::AuthPasswordHandlerImpl(PasswordStore* t_PasswordStore) :
	m_PasswordStore(t_PasswordStore) {}

const char* AuthPasswordHandlerImpl::getPassword(string t_PeerName)
{
	const char* securedSessionPassword = m_PasswordStore->getPassword(t_PeerName);

	securedSessionPassword = (securedSessionPassword != nullptr) ? securedSessionPassword : SrpAnonymousKeyListener::DEFAULT_PINCODE;
	LOG(INFO) << "Providing password for " << t_PeerName << " as " << securedSessionPassword;
	m_PeerAuthenticated.insert(pair<string, bool>(t_PeerName, true));
	return securedSessionPassword;
}

void AuthPasswordHandlerImpl::completed(string t_Mechanism, string t_AuthPeer, bool t_Authenticated)
{
	if (!t_Authenticated)
	{
		m_PeerAuthenticated.insert(pair<string, bool>(t_AuthPeer, true));
		m_PeerAuthenticationSuccessful.insert(pair<string, bool>(t_AuthPeer, false));
		LOG(INFO) << " ** " << t_AuthPeer << " failed to authenticate";
	}
	else
	{
		m_PeerAuthenticated.insert(pair<string, bool>(t_AuthPeer, true));
		m_PeerAuthenticationSuccessful.insert(pair<string, bool>(t_AuthPeer, true));
		LOG(INFO) << " ** " << t_AuthPeer << " successfully authenticated";
	}
}

void AuthPasswordHandlerImpl::resetAuthentication(string t_AuthPeer)
{
	m_PeerAuthenticated.insert(pair<string, bool>(t_AuthPeer, false));
	m_PeerAuthenticationSuccessful.insert(pair<string, bool>(t_AuthPeer, false));
}

bool AuthPasswordHandlerImpl::isPeerAuthenticated(string t_AuthPeer)
{
	return isTrueBoolean(m_PeerAuthenticated.at(t_AuthPeer));
}

bool AuthPasswordHandlerImpl::isPeerAuthenticationSuccessful(string t_AuthPeer)
{
	if (m_PeerAuthenticationSuccessful.empty())
	{
		return false;
	}
	else
	{
		return isTrueBoolean(m_PeerAuthenticationSuccessful.at(t_AuthPeer));
	}
}

bool AuthPasswordHandlerImpl::isTrueBoolean(bool t_Value)
{
	bool result;

	if (t_Value == NULL)
	{
		result = false;
	}
	else
	{
		result = t_Value;
	}

	return result;
}