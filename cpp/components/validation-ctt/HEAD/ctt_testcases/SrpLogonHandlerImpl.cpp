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
#include "SrpLogonHandlerImpl.h"

SrpLogonHandlerImpl::SrpLogonHandlerImpl(SrpLogonStore* t_PasswordStore, const std::string& t_DefaultUser, const std::string& t_DefaultPass) :
	m_PasswordStore(t_PasswordStore), m_DefaultUser(t_DefaultUser), m_DefaultPass(t_DefaultPass) {}

const char* SrpLogonHandlerImpl::getUser(const std::string& t_PeerName)
{
	const char* securedSessionUser = m_PasswordStore->getUser(t_PeerName);

	securedSessionUser = (securedSessionUser != nullptr) ? securedSessionUser : m_DefaultUser.c_str();
	LOG(INFO) << "Providing credentials for " << t_PeerName << " as user: " << securedSessionUser;
	m_PeerAuthenticated.insert(std::pair<std::string, bool>(t_PeerName, true));
	return securedSessionUser;
}

const char* SrpLogonHandlerImpl::getPass(const std::string& t_PeerName)
{
	const char* securedSessionPass = m_PasswordStore->getPass(t_PeerName);

	securedSessionPass = (securedSessionPass != nullptr) ? securedSessionPass : m_DefaultPass.c_str();
	LOG(INFO) << "Providing credentials for " << t_PeerName << " as pass: " << securedSessionPass;
	m_PeerAuthenticated.insert(std::pair<std::string, bool>(t_PeerName, true));
	return securedSessionPass;
}

void SrpLogonHandlerImpl::completed(std::string t_Mechanism, std::string t_AuthPeer, bool t_Authenticated)
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

void SrpLogonHandlerImpl::resetAuthentication(std::string t_AuthPeer)
{
	m_PeerAuthenticated.insert(std::pair<std::string, bool>(t_AuthPeer, false));
	m_PeerAuthenticationSuccessful.insert(std::pair<std::string, bool>(t_AuthPeer, false));
}

bool SrpLogonHandlerImpl::isPeerAuthenticated(std::string t_AuthPeer)
{
	return isTrueBoolean(m_PeerAuthenticated.at(t_AuthPeer));
}

bool SrpLogonHandlerImpl::isPeerAuthenticationSuccessful(std::string t_AuthPeer)
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

bool SrpLogonHandlerImpl::isTrueBoolean(bool t_Value)
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