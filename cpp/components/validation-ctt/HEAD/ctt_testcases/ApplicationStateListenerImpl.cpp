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
#include "ApplicationStateListenerImpl.h"

ApplicationStateListenerImpl::ApplicationStateListenerImpl()
{
    m_BusApplicationStates.clear();
}

void ApplicationStateListenerImpl::State(AJ_PCSTR busName, const qcc::KeyInfoNISTP256& publicKeyInfo, ajn::PermissionConfigurator::ApplicationState state)
{
    LOG(INFO) << "Received State signal from " << busName << " with Application State " << ajn::PermissionConfigurator::ToString(state);

    m_BusApplicationStates[busName] = state;
    m_BusApplicationEccPublicKeys[busName] = publicKeyInfo;
}

bool ApplicationStateListenerImpl::IsClaimed(AJ_PCSTR busName)
{
    return CheckApplicationState(busName, ajn::PermissionConfigurator::CLAIMED);
}

bool ApplicationStateListenerImpl::IsClaimable(AJ_PCSTR busName)
{
    return CheckApplicationState(busName, ajn::PermissionConfigurator::CLAIMABLE);
}

bool ApplicationStateListenerImpl::CheckApplicationState(AJ_PCSTR busName, ajn::PermissionConfigurator::ApplicationState applicationState)
{
    return ((m_BusApplicationStates.find(busName) != m_BusApplicationStates.end()) &&
        (m_BusApplicationStates[busName] == applicationState));
}

bool ApplicationStateListenerImpl::CheckEccPublicKey(AJ_PCSTR busName, const qcc::ECCPublicKey& eccPublicKey)
{
    return ((m_BusApplicationEccPublicKeys.find(busName) != m_BusApplicationEccPublicKeys.end()) &&
        (*m_BusApplicationEccPublicKeys[busName].GetPublicKey() == eccPublicKey));
}