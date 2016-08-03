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
#pragma once

#include <map>

#include "SrpLogonHandlerImpl.h"
#include "SrpLogonStore.h"

#include <alljoyn\AuthListener.h>

class SrpLogonListener : public ajn::AuthListener
{
public:
	SrpLogonListener(SrpLogonHandlerImpl*, const std::string&, const std::string&);
	bool RequestCredentials(const char*, const char*, uint16_t, const char*, uint16_t, Credentials&);
	void AuthenticationComplete(const char*, const char*, bool);

private:
	SrpLogonHandlerImpl* m_PasswordHandler{ nullptr };
	std::string m_DefaultUser = std::string{ "" };
	std::string m_DefaultPass = std::string{ "" };
};
