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
#include "SrpLogonStore.h"

const char* SrpLogonStore::getUser(const std::string& t_PeerName)
{
	return m_UserStore.empty() ? nullptr : m_UserStore.at(t_PeerName);
}

const char* SrpLogonStore::getPass(const std::string& t_PeerName)
{
	return m_PassStore.empty() ? nullptr : m_PassStore.at(t_PeerName);
}

void SrpLogonStore::setUser(const std::string& t_PeerName, const char* t_User)
{
	std::map<std::string, const char*>::iterator iterator = m_UserStore.find(t_PeerName);
	if (iterator != m_UserStore.end())
	{
		iterator->second = t_User;
	}
	else
	{
		m_UserStore.insert(std::pair<std::string, const char*>(t_PeerName, t_User));
	}
}

void SrpLogonStore::setPass(const std::string& t_PeerName, const char* t_Pass)
{
	std::map<std::string, const char*>::iterator iterator = m_PassStore.find(t_PeerName);
	if (iterator != m_PassStore.end())
	{
		iterator->second = t_Pass;
	}
	else
	{
		m_PassStore.insert(std::pair<std::string, const char*>(t_PeerName, t_Pass));
	}
}