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
#include "PasswordStore.h"

using namespace std;

const char* PasswordStore::getPassword(string t_PeerName)
{
	return m_PasswordStore.empty() ? nullptr : m_PasswordStore.at(t_PeerName);
}

void PasswordStore::setPassword(string t_PeerName, const char* t_Password)
{
	std::map<string, const char*>::iterator iterator = m_PasswordStore.find(t_PeerName);
	if (iterator != m_PasswordStore.end())
	{
		iterator->second = t_Password;
	}
	else
	{
		m_PasswordStore.insert(pair<string, const char*>(t_PeerName, t_Password));
	}
}