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
#include "ECDHENullHandlerImpl.h"

ECDHENullHandlerImpl::ECDHENullHandlerImpl() {}

void ECDHENullHandlerImpl::completed(std::string t_Mechanism, std::string t_AuthPeer, bool t_Authenticated)
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

void ECDHENullHandlerImpl::resetAuthentication(std::string t_AuthPeer)
{
    m_PeerAuthenticated.insert(std::pair<std::string, bool>(t_AuthPeer, false));
    m_PeerAuthenticationSuccessful.insert(std::pair<std::string, bool>(t_AuthPeer, false));
}

bool ECDHENullHandlerImpl::isPeerAuthenticated(std::string t_AuthPeer)
{
    return isTrueBoolean(m_PeerAuthenticated.at(t_AuthPeer));
}

bool ECDHENullHandlerImpl::isPeerAuthenticationSuccessful(std::string t_AuthPeer)
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

bool ECDHENullHandlerImpl::isTrueBoolean(bool t_Value)
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