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
#include "ECDHEEcdsaHandlerImpl.h"

ECDHEEcdsaHandlerImpl::ECDHEEcdsaHandlerImpl(ECDHEEcdsaStore* t_PasswordStore, const std::string& t_DefaultPrivateKey,
	const std::string& t_DefaultCertChain) :
	m_PasswordStore(t_PasswordStore), m_DefaultPrivateKey(t_DefaultPrivateKey), m_DefaultCertChain(t_DefaultCertChain) {}

const char* ECDHEEcdsaHandlerImpl::getPrivateKey(const std::string& t_PeerName)
{
	const char* securedSessionPrivateKey = m_PasswordStore->getPrivateKey(t_PeerName);

	securedSessionPrivateKey = (securedSessionPrivateKey != nullptr) ? securedSessionPrivateKey : m_DefaultPrivateKey.c_str();
	LOG(INFO) << "Providing password for " << t_PeerName << " as " << securedSessionPrivateKey;
	m_PeerAuthenticated.insert(std::pair<std::string, bool>(t_PeerName, true));
	return securedSessionPrivateKey;
}

const char* ECDHEEcdsaHandlerImpl::getCertChain(const std::string& t_PeerName)
{
	const char* securedSessionCertChain = m_PasswordStore->getCertChain(t_PeerName);

	securedSessionCertChain = (securedSessionCertChain != nullptr) ? securedSessionCertChain : m_DefaultCertChain.c_str();
	LOG(INFO) << "Providing password for " << t_PeerName << " as " << securedSessionCertChain;
	m_PeerAuthenticated.insert(std::pair<std::string, bool>(t_PeerName, true));
	return securedSessionCertChain;
}

void ECDHEEcdsaHandlerImpl::completed(std::string t_Mechanism, std::string t_AuthPeer, bool t_Authenticated)
{
	if (!t_Authenticated)
	{
		m_PeerAuthenticated.insert(std::pair<std::string, bool>(t_AuthPeer, true));
		m_PeerAuthenticationSuccessful.insert(std::pair<std::string, bool>(t_AuthPeer, false));
		LOG(INFO) << " ** " << t_AuthPeer << " failed to authenticate";
	}
	else
	{
		m_PeerAuthenticated.insert(std::pair<std::string, bool>(t_AuthPeer, true));
		m_PeerAuthenticationSuccessful.insert(std::pair<std::string, bool>(t_AuthPeer, true));
		LOG(INFO) << " ** " << t_AuthPeer << " successfully authenticated";
	}
}

void ECDHEEcdsaHandlerImpl::resetAuthentication(std::string t_AuthPeer)
{
	m_PeerAuthenticated.insert(std::pair<std::string, bool>(t_AuthPeer, false));
	m_PeerAuthenticationSuccessful.insert(std::pair<std::string, bool>(t_AuthPeer, false));
}

bool ECDHEEcdsaHandlerImpl::isPeerAuthenticated(std::string t_AuthPeer)
{
	return isTrueBoolean(m_PeerAuthenticated.at(t_AuthPeer));
}

bool ECDHEEcdsaHandlerImpl::isPeerAuthenticationSuccessful(std::string t_AuthPeer)
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

bool ECDHEEcdsaHandlerImpl::isTrueBoolean(bool t_Value)
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