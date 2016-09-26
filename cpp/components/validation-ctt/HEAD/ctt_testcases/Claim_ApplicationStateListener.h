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

#include <alljoyn\ApplicationStateListener.h>

class Claim_ApplicationStateListener : public ajn::ApplicationStateListener
{
public:
	Claim_ApplicationStateListener();
	virtual void State(const char* busName, const qcc::KeyInfoNISTP256& publicKeyInfo, ajn::PermissionConfigurator::ApplicationState state);
	bool isClaimable(const char* busName);
	bool isClaimed(const char* busName);
	bool checkApplicationState(const char* busName, const ajn::PermissionConfigurator::ApplicationState& applicationState);
	bool checkEccPublicKey(const char* busName, const qcc::ECCPublicKey &publicKey);

private:
	std::map<std::string, ajn::PermissionConfigurator::ApplicationState> m_BusApplicationStates;
	std::map<std::string, qcc::KeyInfoNISTP256> m_BusApplicationEccPublicKeys;

	void updateBusApplicationState(const char* busName, const ajn::PermissionConfigurator::ApplicationState &state);
	void updateBusEccPublicKey(const char* busName, const qcc::KeyInfoNISTP256 &eccPublicKey);
};
