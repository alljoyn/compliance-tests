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
#include "Claim_ApplicationStateListener.h"

#include <alljoyn\config\ConfigClient.h>

Claim_ApplicationStateListener::Claim_ApplicationStateListener()
{
	m_BusApplicationStates.clear();
}

void Claim_ApplicationStateListener::State(const char* busName, const qcc::KeyInfoNISTP256& publicKeyInfo, ajn::PermissionConfigurator::ApplicationState state)
{
	LOG(INFO) << "Received State signal from " << busName << " with Application State " << ajn::PermissionConfigurator::ToString(state);

	updateBusApplicationState(busName, state);
	updateBusEccPublicKey(busName, publicKeyInfo);
}

void Claim_ApplicationStateListener::updateBusApplicationState(const char* busName, const ajn::PermissionConfigurator::ApplicationState &state)
{
	bool exists = false;
	for (auto& applicationStatePair : m_BusApplicationStates)
	{
		if (0 == applicationStatePair.first.compare(busName))
		{
			applicationStatePair.second = state;
			exists = true;
			break;
		}
	}

	if (!exists)
	{
		m_BusApplicationStates.insert(std::make_pair(busName, state));
	}
}

void Claim_ApplicationStateListener::updateBusEccPublicKey(const char* busName, const qcc::KeyInfoNISTP256 &publicKeyInfo)
{
	bool exists = false;
	for (auto& eccPubliKeyPair : m_BusApplicationEccPublicKeys)
	{
		if (0 == eccPubliKeyPair.first.compare(busName))
		{
			eccPubliKeyPair.second = publicKeyInfo;
			exists = true;
			break;
		}
	}

	if (!exists)
	{
		m_BusApplicationEccPublicKeys.insert(std::make_pair(busName, publicKeyInfo));
	}
}

bool Claim_ApplicationStateListener::isClaimed(const char* busName)
{
	return checkApplicationState(busName, ajn::PermissionConfigurator::CLAIMED);
}

bool Claim_ApplicationStateListener::isClaimable(const char* busName)
{
	return checkApplicationState(busName, ajn::PermissionConfigurator::CLAIMABLE);
}

bool Claim_ApplicationStateListener::checkApplicationState(const char* busName, const ajn::PermissionConfigurator::ApplicationState& applicationState)
{
	bool matchesState = false;
	if (!m_BusApplicationStates.empty())
	{
		try
		{
			matchesState = m_BusApplicationStates.at(busName) == applicationState;
		}
		catch (const std::out_of_range&)
		{
			matchesState = false;
		}
	}

	return matchesState;
}

bool Claim_ApplicationStateListener::checkEccPublicKey(const char* busName, const qcc::ECCPublicKey& eccPublicKey)
{
	bool matchesKey = false;
	if (!m_BusApplicationEccPublicKeys.empty())
	{
		try
		{
			matchesKey = *m_BusApplicationEccPublicKeys.at(busName).GetPublicKey() == eccPublicKey;
		}
		catch (const std::out_of_range&)
		{
			matchesKey = false;
		}
	}

	return matchesKey;
}