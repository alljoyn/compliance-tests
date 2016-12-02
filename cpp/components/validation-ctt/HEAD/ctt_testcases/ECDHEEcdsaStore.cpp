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
#include "ECDHEEcdsaStore.h"

const char* ECDHEEcdsaStore::getPrivateKey(std::string t_PeerName)
{
	return m_PrivateKeyStore.empty() ? nullptr : m_PrivateKeyStore.at(t_PeerName);
}

void ECDHEEcdsaStore::setPrivateKey(std::string t_PeerName, const char* t_PrivateKey)
{
	std::map<std::string, const char*>::iterator iterator = m_PrivateKeyStore.find(t_PeerName);
	if (iterator != m_PrivateKeyStore.end())
	{
		iterator->second = t_PrivateKey;
	}
	else
	{
		m_PrivateKeyStore.insert(std::pair<std::string, const char*>(t_PeerName, t_PrivateKey));
	}
}

const char* ECDHEEcdsaStore::getCertChain(std::string t_PeerName)
{
	return m_CertChainStore.empty() ? nullptr : m_CertChainStore.at(t_PeerName);
}

void ECDHEEcdsaStore::setCertChain(std::string t_PeerName, const char* t_CertChain)
{
	std::map<std::string, const char*>::iterator iterator = m_CertChainStore.find(t_PeerName);
	if (iterator != m_CertChainStore.end())
	{
		iterator->second = t_CertChain;
	}
	else
	{
		m_CertChainStore.insert(std::pair<std::string, const char*>(t_PeerName, t_CertChain));
	}
}