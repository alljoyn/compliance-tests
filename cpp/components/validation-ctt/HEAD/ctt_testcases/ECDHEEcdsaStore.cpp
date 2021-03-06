/******************************************************************************
*    Copyright (c) Open Connectivity Foundation (OCF), AllJoyn Open Source
*    Project (AJOSP) Contributors and others.
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
*    THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL
*    WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED
*    WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE
*    AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL
*    DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR
*    PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER
*    TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
*    PERFORMANCE OF THIS SOFTWARE.
******************************************************************************/
#include "stdafx.h"
#include "ECDHEEcdsaStore.h"

AJ_PCSTR ECDHEEcdsaStore::getPrivateKey(std::string t_PeerName)
{
    return m_PrivateKeyStore.empty() ? nullptr : m_PrivateKeyStore.at(t_PeerName);
}

void ECDHEEcdsaStore::setPrivateKey(std::string t_PeerName, AJ_PCSTR t_PrivateKey)
{
    std::map<std::string, AJ_PCSTR>::iterator iterator = m_PrivateKeyStore.find(t_PeerName);
    if (iterator != m_PrivateKeyStore.end())
    {
        iterator->second = t_PrivateKey;
    }
    else
    {
        m_PrivateKeyStore.insert(std::pair<std::string, AJ_PCSTR>(t_PeerName, t_PrivateKey));
    }
}

AJ_PCSTR ECDHEEcdsaStore::getCertChain(std::string t_PeerName)
{
    return m_CertChainStore.empty() ? nullptr : m_CertChainStore.at(t_PeerName);
}

void ECDHEEcdsaStore::setCertChain(std::string t_PeerName, AJ_PCSTR t_CertChain)
{
    std::map<std::string, AJ_PCSTR>::iterator iterator = m_CertChainStore.find(t_PeerName);
    if (iterator != m_CertChainStore.end())
    {
        iterator->second = t_CertChain;
    }
    else
    {
        m_CertChainStore.insert(std::pair<std::string, AJ_PCSTR>(t_PeerName, t_CertChain));
    }
}