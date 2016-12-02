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
#include "ECDHESpekeStore.h"

const char* ECDHESpekeStore::getPassword(std::string t_PeerName)
{
	return m_PasswordStore.empty() ? nullptr : m_PasswordStore.at(t_PeerName);
}

void ECDHESpekeStore::setPassword(std::string t_PeerName, const char* t_Password)
{
	std::map<std::string, const char*>::iterator iterator = m_PasswordStore.find(t_PeerName);
	if (iterator != m_PasswordStore.end())
	{
		iterator->second = t_Password;
	}
	else
	{
		m_PasswordStore.insert(std::pair<std::string, const char*>(t_PeerName, t_Password));
	}
}