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
#pragma once

#include <alljoyn\ApplicationStateListener.h>

#include <map>

class ApplicationStateListenerImpl : public ajn::ApplicationStateListener
{
public:
    ApplicationStateListenerImpl();
    virtual void State(AJ_PCSTR busName, const qcc::KeyInfoNISTP256& publicKeyInfo, ajn::PermissionConfigurator::ApplicationState state);
    bool IsClaimable(AJ_PCSTR busName);
    bool IsClaimed(AJ_PCSTR busName);
    bool CheckApplicationState(AJ_PCSTR busName, ajn::PermissionConfigurator::ApplicationState applicationState);
    bool CheckEccPublicKey(AJ_PCSTR busName, const qcc::ECCPublicKey& publicKey);

private:
    std::map<std::string, ajn::PermissionConfigurator::ApplicationState> m_BusApplicationStates;
    std::map<std::string, qcc::KeyInfoNISTP256> m_BusApplicationEccPublicKeys;
};