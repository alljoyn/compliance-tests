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

#include "AuthPasswordHandler.h"
#include "RsaKeyXStore.h"

class RsaKeyXHandlerImpl
{
public:
	RsaKeyXHandlerImpl(RsaKeyXStore*, const std::string&, const std::string&);
	virtual ~RsaKeyXHandlerImpl() {}
	virtual const char* getPrivateKey(const std::string&);
	virtual const char* getX509Cert(const std::string&);
	virtual void completed(std::string, std::string, bool);
	void resetAuthentication(std::string);
	bool isPeerAuthenticated(std::string);
	bool isPeerAuthenticationSuccessful(std::string);
protected:
	bool isTrueBoolean(bool);
private:
	RsaKeyXStore* m_PasswordStore;
	std::map<std::string, bool> m_PeerAuthenticated;
	std::map<std::string, bool> m_PeerAuthenticationSuccessful;
	std::string m_DefaultPrivateKey = std::string("");
	std::string m_DefaultX509Cert = std::string("");
};