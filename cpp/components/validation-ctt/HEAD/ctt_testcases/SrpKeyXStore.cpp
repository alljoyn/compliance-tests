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
#include "SrpKeyXStore.h"

AJ_PCSTR SrpKeyXStore::getPincode(std::string t_PeerName)
{
	return m_PincodeStore.empty() ? nullptr : m_PincodeStore.at(t_PeerName);
}

void SrpKeyXStore::setPincode(std::string t_PeerName, AJ_PCSTR t_Pincode)
{
	std::map<std::string, AJ_PCSTR>::iterator iterator = m_PincodeStore.find(t_PeerName);
	if (iterator != m_PincodeStore.end())
	{
		iterator->second = t_Pincode;
	}
	else
	{
		m_PincodeStore.insert(std::pair<std::string, AJ_PCSTR>(t_PeerName, t_Pincode));
	}
}