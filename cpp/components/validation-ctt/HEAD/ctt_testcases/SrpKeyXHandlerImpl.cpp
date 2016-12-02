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
#include "SrpKeyXHandlerImpl.h"

SrpKeyXHandlerImpl::SrpKeyXHandlerImpl(SrpKeyXStore* t_PasswordStore, std::string t_DefaultPincode) :
	m_PasswordStore(t_PasswordStore), m_DefaultPincode(t_DefaultPincode) {}

const char* SrpKeyXHandlerImpl::getPassword(std::string t_PeerName)
{
	const char* securedSessionPassword = m_PasswordStore->getPincode(t_PeerName);

	securedSessionPassword = (securedSessionPassword != nullptr) ? securedSessionPassword : m_DefaultPincode.c_str();
	LOG(INFO) << "Providing password for " << t_PeerName << " as " << securedSessionPassword;
	m_PeerAuthenticated.insert(std::pair<std::string, bool>(t_PeerName, true));
	return securedSessionPassword;
}

void SrpKeyXHandlerImpl::completed(std::string t_Mechanism, std::string t_AuthPeer, bool t_Authenticated)
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

void SrpKeyXHandlerImpl::resetAuthentication(std::string t_AuthPeer)
{
	m_PeerAuthenticated.insert(std::pair<std::string, bool>(t_AuthPeer, false));
	m_PeerAuthenticationSuccessful.insert(std::pair<std::string, bool>(t_AuthPeer, false));
}

bool SrpKeyXHandlerImpl::isPeerAuthenticated(std::string t_AuthPeer)
{
	return isTrueBoolean(m_PeerAuthenticated.at(t_AuthPeer));
}

bool SrpKeyXHandlerImpl::isPeerAuthenticationSuccessful(std::string t_AuthPeer)
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

bool SrpKeyXHandlerImpl::isTrueBoolean(bool t_Value)
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