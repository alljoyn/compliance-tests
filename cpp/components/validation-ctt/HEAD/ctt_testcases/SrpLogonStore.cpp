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
#include "SrpLogonStore.h"

AJ_PCSTR SrpLogonStore::getUser(const std::string& t_PeerName)
{
	return m_UserStore.empty() ? nullptr : m_UserStore.at(t_PeerName);
}

AJ_PCSTR SrpLogonStore::getPass(const std::string& t_PeerName)
{
	return m_PassStore.empty() ? nullptr : m_PassStore.at(t_PeerName);
}

void SrpLogonStore::setUser(const std::string& t_PeerName, AJ_PCSTR t_User)
{
	std::map<std::string, AJ_PCSTR>::iterator iterator = m_UserStore.find(t_PeerName);
	if (iterator != m_UserStore.end())
	{
		iterator->second = t_User;
	}
	else
	{
		m_UserStore.insert(std::pair<std::string, AJ_PCSTR>(t_PeerName, t_User));
	}
}

void SrpLogonStore::setPass(const std::string& t_PeerName, AJ_PCSTR t_Pass)
{
	std::map<std::string, AJ_PCSTR>::iterator iterator = m_PassStore.find(t_PeerName);
	if (iterator != m_PassStore.end())
	{
		iterator->second = t_Pass;
	}
	else
	{
		m_PassStore.insert(std::pair<std::string, AJ_PCSTR>(t_PeerName, t_Pass));
	}
}