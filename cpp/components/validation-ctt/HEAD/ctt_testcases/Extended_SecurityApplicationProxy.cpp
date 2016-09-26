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
#include "Extended_SecurityApplicationProxy.h"

#include <alljoyn\AllJoynStd.h>

Extended_SecurityApplicationProxy::Extended_SecurityApplicationProxy(ajn::BusAttachment &bus, AJ_PCSTR busName, ajn::SessionId sessionId) :
	ajn::SecurityApplicationProxy(bus, busName, sessionId) {}

QStatus Extended_SecurityApplicationProxy::SetApplicationVersion(const uint16_t &version)
{
	QStatus status;

	ajn::MsgArg msgArg;
	status = msgArg.Set("q", version);
	if (ER_OK != status)
	{
		return status;
	}
	
	return SetProperty(ajn::org::alljoyn::Bus::Security::Application::InterfaceName, "Version", msgArg);
}

QStatus Extended_SecurityApplicationProxy::SetApplicationState(const ajn::PermissionConfigurator::ApplicationState &state)
{
	QStatus status;

	ajn::MsgArg msgArg;
	status = msgArg.Set("q", state);
	if (ER_OK != status)
	{
		return status;
	}

	return SetProperty(ajn::org::alljoyn::Bus::Security::Application::InterfaceName, "ApplicationState", msgArg);
}

QStatus Extended_SecurityApplicationProxy::SetManifestTemplateDigest(const uint8_t &digestAlgorithm, const std::vector<uint8_t> &digestData)
{
	QStatus status;

	ajn::MsgArg msgArg;
	status = msgArg.Set("(yay)", digestAlgorithm, digestData.size(), digestData.data());
	if (ER_OK != status)
	{
		return status;
	}

	return SetProperty(ajn::org::alljoyn::Bus::Security::Application::InterfaceName, "ManifestTemplateDigest", msgArg);
}

QStatus Extended_SecurityApplicationProxy::SetEccPublicKey(const uint8_t &eccAlgorithm, const uint8_t &eccCurveIdentifier,
	const std::vector<uint8_t> &eccXCoordinate, const std::vector<uint8_t> &eccYCoordinate)
{
	QStatus status;

	ajn::MsgArg msgArg;
	status = msgArg.Set("(yyayay)", eccAlgorithm, eccCurveIdentifier, eccXCoordinate.size(), eccXCoordinate.data(), 
		eccYCoordinate.size(), eccYCoordinate.data());
	if (ER_OK != status)
	{
		return status;
	}

	return SetProperty(ajn::org::alljoyn::Bus::Security::Application::InterfaceName, "EccPublicKey", msgArg);
}

QStatus Extended_SecurityApplicationProxy::SetManufacturerCertificate(const std::vector<ajn::MsgArg> &certificates)
{
	QStatus status;

	ajn::MsgArg msgArg;
	status = msgArg.Set("a(yay)", certificates.size(), certificates.data());
	if (ER_OK != status)
	{
		return status;
	}

	return SetProperty(ajn::org::alljoyn::Bus::Security::Application::InterfaceName, "ManufacturerCertificate", msgArg);
}

QStatus Extended_SecurityApplicationProxy::SetManifestTemplate(const std::vector<ajn::MsgArg> &manifestTemplate)
{
	QStatus status;

	ajn::MsgArg msgArg;
	status = msgArg.Set("a(ssya(syy))", manifestTemplate.size(), manifestTemplate.data());
	if (ER_OK != status)
	{
		return status;
	}

	return SetProperty(ajn::org::alljoyn::Bus::Security::Application::InterfaceName, "ManifestTemplate", msgArg);
}

QStatus Extended_SecurityApplicationProxy::SetClaimCapabilities(const ajn::PermissionConfigurator::ClaimCapabilities &capabilities)
{
	QStatus status;

	ajn::MsgArg msgArg;
	status = msgArg.Set("q", capabilities);
	if (ER_OK != status)
	{
		return status;
	}

	return SetProperty(ajn::org::alljoyn::Bus::Security::Application::InterfaceName, "ClaimCapabilities", msgArg);
}

QStatus Extended_SecurityApplicationProxy::SetManagedApplicationVersion(const uint16_t &version)
{
	QStatus status;

	ajn::MsgArg msgArg;
	status = msgArg.Set("q", version);
	if (ER_OK != status)
	{
		return status;
	}

	return SetProperty(ajn::org::alljoyn::Bus::Security::ManagedApplication::InterfaceName, "Version", msgArg);
}

QStatus Extended_SecurityApplicationProxy::SetIdentity(ajn::MsgArg &identity)
{
	return SetProperty(ajn::org::alljoyn::Bus::Security::ManagedApplication::InterfaceName, "Identity", identity);
}

QStatus Extended_SecurityApplicationProxy::SetManifests(const std::vector<ajn::MsgArg> &manifests)
{
	QStatus status;

	ajn::MsgArg msgArg;
	status = msgArg.Set("a(ua(ssa(syy))saysay)", manifests.size(), manifests.data());
	if (ER_OK != status)
	{
		return status;
	}

	return SetProperty(ajn::org::alljoyn::Bus::Security::ManagedApplication::InterfaceName, "Manifests", msgArg);
}

QStatus Extended_SecurityApplicationProxy::GetManifestsMsgArg(std::vector<ajn::MsgArg> &manifests)
{
	QStatus status;

	ajn::MsgArg arg;
	status = GetProperty(ajn::org::alljoyn::Bus::Security::ManagedApplication::InterfaceName, "Manifests", arg);
	if (ER_OK == status)
	{
		/* GetProperty returns a variant wrapper */
		ajn::MsgArg* resultArg;
		status = arg.Get("v", &resultArg);
		if (ER_OK != status) {
			return status;
		}
		ajn::MsgArg* signedManifestArg;
		size_t signedManifestCount;
		status = resultArg->Get("a(ua(ssa(syy))saysay)", &signedManifestCount, &signedManifestArg);
		if (ER_OK != status) {
			return status;
		}

		manifests.clear();
		for (int i = 0; i < signedManifestCount; ++i) {
			manifests.push_back(signedManifestArg[i]);
		}
	}

	return status;
}

QStatus Extended_SecurityApplicationProxy::SetIdentityCertificateId(ajn::MsgArg &identityCertificateId)
{
	return SetProperty(ajn::org::alljoyn::Bus::Security::ManagedApplication::InterfaceName, "IdentityCertificateId", identityCertificateId);
}

QStatus Extended_SecurityApplicationProxy::GetIdentityCertificateIdMsgArg(ajn::MsgArg &identityCertificateIdMsgArg)
{
	QStatus status;
	ajn::MsgArg arg;
	status = GetProperty(ajn::org::alljoyn::Bus::Security::ManagedApplication::InterfaceName, "IdentityCertificateId", arg);
	if (ER_OK == status)
	{
		ajn::MsgArg* resultArg;
		status = arg.Get("v", &resultArg);
		if (ER_OK != status) {
			return status;
		}
		uint8_t* serialVal;
		size_t serialLen;
		uint8_t* akiVal;
		size_t akiLen;
		uint8_t algorithm;
		uint8_t curve;
		uint8_t* xCoord;
		size_t xLen;
		uint8_t* yCoord;
		size_t yLen;
		status = resultArg->Get("(ayay(yyayay))", &serialLen, &serialVal, &akiLen, &akiVal, &algorithm, &curve, &xLen, &xCoord, &yLen, &yCoord);
		if (ER_OK != status) {
			return status;
		}

		status = identityCertificateIdMsgArg.Set("(ayay(yyayay))", serialLen, serialVal, akiLen, akiVal, algorithm, curve, xLen, xCoord, yLen, yCoord);
	}

	return status;
}

QStatus Extended_SecurityApplicationProxy::SetPolicyVersion(const uint32_t &version)
{
	QStatus status;

	ajn::MsgArg msgArg;
	status = msgArg.Set("u", version);
	if (ER_OK != status)
	{
		return status;
	}

	return SetProperty(ajn::org::alljoyn::Bus::Security::ManagedApplication::InterfaceName, "PolicyVersion", msgArg);
}

QStatus Extended_SecurityApplicationProxy::SetPolicy(ajn::MsgArg &policy)
{
	return SetProperty(ajn::org::alljoyn::Bus::Security::ManagedApplication::InterfaceName, "Policy", policy);
}

QStatus Extended_SecurityApplicationProxy::SetDefaultPolicy(ajn::MsgArg &defaultPolicy)
{
	return SetProperty(ajn::org::alljoyn::Bus::Security::ManagedApplication::InterfaceName, "DefaultPolicy", defaultPolicy);
}

QStatus Extended_SecurityApplicationProxy::SetMembershipSummaries(ajn::MsgArg &membershipSummaries)
{
	return SetProperty(ajn::org::alljoyn::Bus::Security::ManagedApplication::InterfaceName, "MembershipSummaries", membershipSummaries);
}