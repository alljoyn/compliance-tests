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
#include "ECDHEEcdsaListener.h"

ECDHEEcdsaListener::ECDHEEcdsaListener(ECDHEEcdsaHandlerImpl* t_ECDHEPskHandlerImpl, const std::string& t_DefaultECDHEEcdsaPrivateKey,
    const std::string& t_DefaultECDHEEcdsaCertChain) :
    m_PasswordHandler(t_ECDHEPskHandlerImpl), m_DefaultPrivateKey(t_DefaultECDHEEcdsaPrivateKey),
    m_DefaultCertChain(t_DefaultECDHEEcdsaCertChain) {}

bool ECDHEEcdsaListener::RequestCredentials(AJ_PCSTR t_AuthMechanism,
    AJ_PCSTR t_AuthPeer, uint16_t t_AuthCount, AJ_PCSTR t_UserID,
    uint16_t t_CredMask, Credentials& t_Creds)
{
    if ((t_CredMask & AuthListener::CRED_PRIVATE_KEY) == AuthListener::CRED_PRIVATE_KEY)
    {
        AJ_PCSTR privateKey = m_DefaultPrivateKey.c_str();
        AJ_PCSTR storedPrivateKey = m_PasswordHandler->getPrivateKey(t_AuthPeer);

        if (m_PasswordHandler != nullptr && storedPrivateKey != nullptr)
        {
            privateKey = storedPrivateKey;
        }

        t_Creds.SetPrivateKey(qcc::String(privateKey));
    }

    if ((t_CredMask & AuthListener::CRED_CERT_CHAIN) == AuthListener::CRED_CERT_CHAIN)
    {
        AJ_PCSTR certChain = m_DefaultCertChain.c_str();
        AJ_PCSTR storedCertChain = m_PasswordHandler->getCertChain(t_AuthPeer);

        if (m_PasswordHandler != nullptr && storedCertChain != nullptr)
        {
            certChain = storedCertChain;
        }

        t_Creds.SetPrivateKey(qcc::String(certChain));
    }

    return true;
}

bool ECDHEEcdsaListener::VerifyCredentials(AJ_PCSTR t_AuthMechanism,
    AJ_PCSTR t_AuthPeer, const Credentials& t_Credentials)
{
    LOG(INFO) << "Verifying Credentials for peer " << t_AuthPeer << " with authMechanism " << t_AuthMechanism;
    return true;
}

void ECDHEEcdsaListener::AuthenticationComplete(AJ_PCSTR t_AuthMechanism,
    AJ_PCSTR t_AuthPeer, bool t_Success)
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