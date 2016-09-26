#/******************************************************************************
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

#include <alljoyn\SecurityApplicationProxy.h>

class Extended_SecurityApplicationProxy : public ajn::SecurityApplicationProxy
{
public:
	Extended_SecurityApplicationProxy(ajn::BusAttachment &bus, AJ_PCSTR busName, ajn::SessionId sessionId = 0U);

	QStatus SetApplicationVersion(const uint16_t &version);
	QStatus SetApplicationState(const ajn::PermissionConfigurator::ApplicationState &state);
	QStatus SetManifestTemplateDigest(const uint8_t &digestAlgorithm, const std::vector<uint8_t> &digestData);
	QStatus SetEccPublicKey(const uint8_t &eccAlgorithm, const uint8_t &eccCurveIdentifier,
		const std::vector<uint8_t> &eccXCoordinate, const std::vector<uint8_t> &eccYCoordinate);
	QStatus SetManufacturerCertificate(const std::vector<ajn::MsgArg> &certificates);
	QStatus SetManifestTemplate(const std::vector<ajn::MsgArg> &manifestTemplate);
	QStatus SetClaimCapabilities(const ajn::PermissionConfigurator::ClaimCapabilities &capabilities);

	QStatus SetManagedApplicationVersion(const uint16_t &version);
	QStatus SetIdentity(ajn::MsgArg &identity);
	QStatus SetManifests(const std::vector<ajn::MsgArg> &manifests);
	QStatus GetManifestsMsgArg(std::vector<ajn::MsgArg> &manifests);
	QStatus SetIdentityCertificateId(ajn::MsgArg &identityCertificateId);
	QStatus GetIdentityCertificateIdMsgArg(ajn::MsgArg &identityCertificateIdMsgArg);
	QStatus SetPolicyVersion(const uint32_t &version);
	QStatus SetPolicy(ajn::MsgArg &policy);
	QStatus SetDefaultPolicy(ajn::MsgArg &defaultPolicy);
	QStatus SetMembershipSummaries(ajn::MsgArg &membershipSummaries);
};
