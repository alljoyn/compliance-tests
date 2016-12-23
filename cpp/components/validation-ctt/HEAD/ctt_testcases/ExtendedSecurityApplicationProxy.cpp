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
#include "ExtendedSecurityApplicationProxy.h"

#include <alljoyn\AllJoynStd.h>

ExtendedSecurityApplicationProxy::ExtendedSecurityApplicationProxy(ajn::BusAttachment &bus, AJ_PCSTR busName, ajn::SessionId sessionId) :
    ajn::SecurityApplicationProxy(bus, busName, sessionId) {}

QStatus ExtendedSecurityApplicationProxy::SetApplicationVersion(uint16_t version)
{
    QStatus status;

    ajn::MsgArg msgArg;
    status = msgArg.Set("q", version);
    if (ER_OK != status) {
        return status;
    }
    
    return SetProperty(ajn::org::alljoyn::Bus::Security::Application::InterfaceName, "Version", msgArg);
}

QStatus ExtendedSecurityApplicationProxy::SetApplicationState(ajn::PermissionConfigurator::ApplicationState state)
{
    QStatus status;

    ajn::MsgArg msgArg;
    status = msgArg.Set("q", state);
    if (ER_OK != status) {
        return status;
    }

    return SetProperty(ajn::org::alljoyn::Bus::Security::Application::InterfaceName, "ApplicationState", msgArg);
}

QStatus ExtendedSecurityApplicationProxy::SetManifestTemplateDigest(uint8_t digestAlgorithm, const std::vector<uint8_t> &digestData)
{
    QStatus status;

    ajn::MsgArg msgArg;
    status = msgArg.Set("(yay)", digestAlgorithm, digestData.size(), digestData.data());
    if (ER_OK != status) {
        return status;
    }

    return SetProperty(ajn::org::alljoyn::Bus::Security::Application::InterfaceName, "ManifestTemplateDigest", msgArg);
}

QStatus ExtendedSecurityApplicationProxy::SetEccPublicKey(uint8_t eccAlgorithm, uint8_t eccCurveIdentifier,
    const std::vector<uint8_t>& eccXCoordinate, const std::vector<uint8_t>& eccYCoordinate)
{
    QStatus status;

    ajn::MsgArg msgArg;
    status = msgArg.Set("(yyayay)", eccAlgorithm, eccCurveIdentifier, eccXCoordinate.size(), eccXCoordinate.data(), 
        eccYCoordinate.size(), eccYCoordinate.data());
    if (ER_OK != status) {
        return status;
    }

    return SetProperty(ajn::org::alljoyn::Bus::Security::Application::InterfaceName, "EccPublicKey", msgArg);
}

QStatus ExtendedSecurityApplicationProxy::SetManufacturerCertificate(const std::vector<ajn::MsgArg>& certificates)
{
    QStatus status;

    ajn::MsgArg msgArg;
    status = msgArg.Set("a(yay)", certificates.size(), certificates.data());
    if (ER_OK != status) {
        return status;
    }

    return SetProperty(ajn::org::alljoyn::Bus::Security::Application::InterfaceName, "ManufacturerCertificate", msgArg);
}

QStatus ExtendedSecurityApplicationProxy::SetManifestTemplate(const std::vector<ajn::MsgArg>& manifestTemplate)
{
    QStatus status;

    ajn::MsgArg msgArg;
    status = msgArg.Set("a(ssya(syy))", manifestTemplate.size(), manifestTemplate.data());
    if (ER_OK != status) {
        return status;
    }

    return SetProperty(ajn::org::alljoyn::Bus::Security::Application::InterfaceName, "ManifestTemplate", msgArg);
}

QStatus ExtendedSecurityApplicationProxy::SetClaimCapabilities(ajn::PermissionConfigurator::ClaimCapabilities capabilities)
{
    QStatus status;

    ajn::MsgArg msgArg;
    status = msgArg.Set("q", capabilities);
    if (ER_OK != status) {
        return status;
    }

    return SetProperty(ajn::org::alljoyn::Bus::Security::Application::InterfaceName, "ClaimCapabilities", msgArg);
}

QStatus ExtendedSecurityApplicationProxy::SetManagedApplicationVersion(uint16_t version)
{
    QStatus status;

    ajn::MsgArg msgArg;
    status = msgArg.Set("q", version);
    if (ER_OK != status) {
        return status;
    }

    return SetProperty(ajn::org::alljoyn::Bus::Security::ManagedApplication::InterfaceName, "Version", msgArg);
}

QStatus ExtendedSecurityApplicationProxy::SetIdentity(ajn::MsgArg& identity)
{
    return SetProperty(ajn::org::alljoyn::Bus::Security::ManagedApplication::InterfaceName, "Identity", identity);
}

QStatus ExtendedSecurityApplicationProxy::SetManifests(const std::vector<ajn::MsgArg>& manifests)
{
    QStatus status;

    ajn::MsgArg msgArg;
    status = msgArg.Set("a(ua(ssa(syy))saysay)", manifests.size(), manifests.data());
    if (ER_OK != status) {
        return status;
    }

    return SetProperty(ajn::org::alljoyn::Bus::Security::ManagedApplication::InterfaceName, "Manifests", msgArg);
}

QStatus ExtendedSecurityApplicationProxy::GetManifestsMsgArg(std::vector<ajn::MsgArg>& manifests)
{
    QStatus status;

    ajn::MsgArg arg;
    status = GetProperty(ajn::org::alljoyn::Bus::Security::ManagedApplication::InterfaceName, "Manifests", arg);
    if (ER_OK != status) {
        return status;
    }

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

    MsgArgPointerArrayToVector(signedManifestArg, signedManifestCount, manifests);

    return status;
}

void ExtendedSecurityApplicationProxy::MsgArgPointerArrayToVector(ajn::MsgArg* msgArgPointerArray, 
    size_t msgArgPointerArraySize, std::vector<ajn::MsgArg> &msgArgVector)
{
    msgArgVector.clear();
    for (int i = 0; i < msgArgPointerArraySize; ++i) {
        msgArgVector.push_back(msgArgPointerArray[i]);
    }
}

QStatus ExtendedSecurityApplicationProxy::SetIdentityCertificateId(ajn::MsgArg& identityCertificateId)
{
    return SetProperty(ajn::org::alljoyn::Bus::Security::ManagedApplication::InterfaceName, "IdentityCertificateId", identityCertificateId);
}

QStatus ExtendedSecurityApplicationProxy::GetIdentityCertificateIdMsgArg(ajn::MsgArg &identityCertificateIdMsgArg)
{
    QStatus status;
    ajn::MsgArg arg;
    status = GetProperty(ajn::org::alljoyn::Bus::Security::ManagedApplication::InterfaceName, "IdentityCertificateId", arg);
    if (ER_OK != status) {
        return status;
    }

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

    return identityCertificateIdMsgArg.Set("(ayay(yyayay))", serialLen, serialVal, akiLen, akiVal, algorithm, curve, xLen, xCoord, yLen, yCoord);
}

QStatus ExtendedSecurityApplicationProxy::SetPolicyVersion(uint32_t version)
{
    QStatus status;

    ajn::MsgArg msgArg;
    status = msgArg.Set("u", version);
    if (ER_OK != status) {
        return status;
    }

    return SetProperty(ajn::org::alljoyn::Bus::Security::ManagedApplication::InterfaceName, "PolicyVersion", msgArg);
}

QStatus ExtendedSecurityApplicationProxy::SetPolicy(ajn::MsgArg& policy)
{
    return SetProperty(ajn::org::alljoyn::Bus::Security::ManagedApplication::InterfaceName, "Policy", policy);
}

QStatus ExtendedSecurityApplicationProxy::SetDefaultPolicy(ajn::MsgArg& defaultPolicy)
{
    return SetProperty(ajn::org::alljoyn::Bus::Security::ManagedApplication::InterfaceName, "DefaultPolicy", defaultPolicy);
}

QStatus ExtendedSecurityApplicationProxy::SetMembershipSummaries(ajn::MsgArg& membershipSummaries)
{
    return SetProperty(ajn::org::alljoyn::Bus::Security::ManagedApplication::InterfaceName, "MembershipSummaries", membershipSummaries);
}