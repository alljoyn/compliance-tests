/******************************************************************************
* Copyright (c) Open Connectivity Foundation (OCF) and AllJoyn Open
*    Source Project (AJOSP) Contributors and others.
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
*     THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL
*     WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED
*     WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE
*     AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL
*     DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR
*     PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER
*     TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
*     PERFORMANCE OF THIS SOFTWARE.
******************************************************************************/
#include "stdafx.h"
#include "RsaKeyXStore.h"

const char* RsaKeyXStore::getPrivateKey(std::string t_PeerName)
{
	return m_PrivateKeyStore.empty() ? nullptr : m_PrivateKeyStore.at(t_PeerName);
}

void RsaKeyXStore::setPrivateKey(std::string t_PeerName, const char* t_PrivateKey)
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

const char* RsaKeyXStore::getX509Cert(std::string t_PeerName)
{
	return m_X509CertStore.empty() ? nullptr : m_X509CertStore.at(t_PeerName);
}

void RsaKeyXStore::setX509Cert(std::string t_PeerName, const char* t_CertChain)
{
	std::map<std::string, const char*>::iterator iterator = m_X509CertStore.find(t_PeerName);
	if (iterator != m_X509CertStore.end())
	{
		iterator->second = t_CertChain;
	}
	else
	{
		m_X509CertStore.insert(std::pair<std::string, const char*>(t_PeerName, t_CertChain));
	}
}