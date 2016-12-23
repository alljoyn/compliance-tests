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
#include "PermissionMgmtHelper.h"

#include <qcc\time.h>

QStatus PermissionMgmtHelper::CreateAllInclusiveManifest(ajn::Manifest& manifest)
{
    // All Inclusive manifest
    const size_t manifestSize = 1;
    ajn::PermissionPolicy::Rule manifestRules[manifestSize];
    manifestRules[0].SetObjPath("*");
    manifestRules[0].SetInterfaceName("*");
    {
        ajn::PermissionPolicy::Rule::Member member[3];
        member[0].Set("*", ajn::PermissionPolicy::Rule::Member::METHOD_CALL,
            ajn::PermissionPolicy::Rule::Member::ACTION_PROVIDE |
            ajn::PermissionPolicy::Rule::Member::ACTION_MODIFY);
        member[1].Set("*", ajn::PermissionPolicy::Rule::Member::SIGNAL,
            ajn::PermissionPolicy::Rule::Member::ACTION_PROVIDE |
            ajn::PermissionPolicy::Rule::Member::ACTION_OBSERVE);
        member[2].Set("*", ajn::PermissionPolicy::Rule::Member::PROPERTY,
            ajn::PermissionPolicy::Rule::Member::ACTION_PROVIDE |
            ajn::PermissionPolicy::Rule::Member::ACTION_MODIFY |
            ajn::PermissionPolicy::Rule::Member::ACTION_OBSERVE);

        manifestRules[0].SetMembers(3, member);
    }

    return manifest->SetRules(manifestRules, manifestSize);
}

QStatus PermissionMgmtHelper::CreateIdentityCertChain(ajn::BusAttachment& caBus,
                                                      ajn::BusAttachment& issuerBus, 
                                                      const qcc::String& serial,
                                                      const qcc::String& subject,
                                                      const qcc::ECCPublicKey* subjectPubKey,
                                                      const qcc::String& alias, 
                                                      uint32_t expiredInSecs,
                                                      qcc::IdentityCertificate* certChain,
                                                      size_t chainCount)
{
    if (chainCount > 3) {
        return ER_INVALID_DATA;
    }

    QStatus status = ER_CRYPTO_ERROR;

    qcc::GUID128 ca(0);
    GetGUID(caBus, ca);
    qcc::String caStr = ca.ToString();
    ajn::PermissionConfigurator& caPC = caBus.GetPermissionConfigurator();
    if (chainCount == 3)
    {
        /* generate the self signed CA cert */
        qcc::String caSerial = serial + "02";
        certChain[2].SetSerial(reinterpret_cast<const uint8_t*>(caSerial.data()), caSerial.size());
        certChain[2].SetIssuerCN((const uint8_t*)caStr.data(), caStr.size());
        certChain[2].SetSubjectCN((const uint8_t*)caStr.data(), caStr.size());
        qcc::CertificateX509::ValidPeriod validity;
        BuildValidity(validity, expiredInSecs);
        certChain[2].SetValidity(&validity);
        certChain[2].SetCA(true);
        qcc::KeyInfoNISTP256 keyInfo;
        caPC.GetSigningPublicKey(keyInfo);
        certChain[2].SetSubjectPublicKey(keyInfo.GetPublicKey());
        status = caPC.SignCertificate(certChain[2]);
        if (ER_OK != status) {
            return status;
        }
    }

    /* generate the issuer cert */
    qcc::GUID128 issuer(0);
    GetGUID(issuerBus, issuer);
    qcc::String issuerStr = issuer.ToString();

    qcc::String issuerSerial = serial + "01";
    certChain[1].SetSerial(reinterpret_cast<const uint8_t*>(issuerSerial.data()), issuerSerial.size());
    certChain[1].SetIssuerCN((const uint8_t*)caStr.data(), caStr.size());
    certChain[1].SetSubjectCN((const uint8_t*)issuerStr.data(), issuerStr.size());
    qcc::CertificateX509::ValidPeriod validity;
    BuildValidity(validity, expiredInSecs);
    certChain[1].SetValidity(&validity);
    certChain[1].SetCA(true);
    ajn::PermissionConfigurator& pc = issuerBus.GetPermissionConfigurator();
    qcc::KeyInfoNISTP256 keyInfo;
    pc.GetSigningPublicKey(keyInfo);
    certChain[1].SetSubjectPublicKey(keyInfo.GetPublicKey());

    status = caPC.SignCertificate(certChain[1]);
    if (ER_OK != status) {
        return status;
    }

    /* generate the leaf cert */
    certChain[0].SetSerial(reinterpret_cast<const uint8_t*>(serial.data()), serial.size());
    certChain[0].SetIssuerCN((const uint8_t*)issuerStr.data(), issuerStr.size());
    certChain[0].SetSubjectCN((const uint8_t*)subject.data(), subject.size());
    certChain[0].SetSubjectPublicKey(subjectPubKey);
    certChain[0].SetAlias(alias);
    certChain[0].SetValidity(&validity);

    /* use the issuer bus to sign the cert */
    status = pc.SignCertificate(certChain[0]);
    if (ER_OK != status)
    {
        return status;
    }

    status = certChain[0].Verify(certChain[1].GetSubjectPublicKey());
    if (ER_OK != status)
    {
        return status;
    }

    return ER_OK;
}

QStatus PermissionMgmtHelper::GetGUID(ajn::BusAttachment& bus,
                                      qcc::GUID128& guid)
{
    qcc::String strGuid;
    QStatus status = bus.GetPeerGUID(NULL, strGuid);
    guid = qcc::GUID128(strGuid);

    return status;
}

void PermissionMgmtHelper::BuildValidity(qcc::CertificateX509::ValidPeriod& validity,
                                         uint32_t expiredInSecs)
{
    validity.validFrom = qcc::GetEpochTimestamp() / 1000;
    validity.validTo = validity.validFrom + expiredInSecs;
}

QStatus PermissionMgmtHelper::SignManifests(ajn::BusAttachment& issuerBus,
                                            const qcc::CertificateX509& subjectCertificate,
                                            std::vector<ajn::Manifest>& manifests)
{
    for (ajn::Manifest manifest : manifests)
    {
        QStatus status = SignManifest(issuerBus, subjectCertificate, manifest);
        if (ER_OK != status) {
            return status;
        }
    }

    return ER_OK;
}

QStatus PermissionMgmtHelper::SignManifest(ajn::BusAttachment& issuerBus,
                                           const qcc::CertificateX509& subjectCertificate,
                                           ajn::Manifest& manifest)
{
    return issuerBus.GetPermissionConfigurator().ComputeThumbprintAndSignManifest(subjectCertificate, manifest);
}

QStatus PermissionMgmtHelper::SignManifest(ajn::BusAttachment& issuerBus,
                                           const std::vector<uint8_t>& subjectThumbprint,
                                           ajn::Manifest& manifest)
{
    return issuerBus.GetPermissionConfigurator().SignManifest(subjectThumbprint, manifest);
}

QStatus PermissionMgmtHelper::CreateMembershipCert(const qcc::String& serial,
                                                   ajn::BusAttachment& signingBus,
                                                   const qcc::String& subject, 
                                                   const qcc::ECCPublicKey* subjectPubKey,
                                                   const qcc::GUID128& guild,
                                                   bool delegate,
                                                   uint32_t expiredInSecs,
                                                   qcc::MembershipCertificate& cert)
{
    qcc::GUID128 issuer(0);
    GetGUID(signingBus, issuer);

    cert.SetSerial(reinterpret_cast<const uint8_t*>(serial.data()), serial.size());
    qcc::String issuerStr = issuer.ToString();
    cert.SetIssuerCN((const uint8_t*)issuerStr.data(), issuerStr.size());
    cert.SetSubjectCN((const uint8_t*)subject.data(), subject.size());
    cert.SetSubjectPublicKey(subjectPubKey);
    cert.SetGuild(guild);
    cert.SetCA(delegate);
    qcc::CertificateX509::ValidPeriod validity;
    BuildValidity(validity, expiredInSecs);
    cert.SetValidity(&validity);
    /* use the signing bus to sign the cert */
    ajn::PermissionConfigurator& pc = signingBus.GetPermissionConfigurator();
    return pc.SignCertificate(cert);
}

QStatus PermissionMgmtHelper::CreateMembershipCert(const qcc::String& serial,
                                                   ajn::BusAttachment& signingBus,
                                                   const qcc::String& subject, 
                                                   const qcc::ECCPublicKey* subjectPubKey,
                                                   const qcc::GUID128& guild, bool delegate,
                                                   uint32_t expiredInSecs,
                                                   qcc::String& der)
{
    qcc::MembershipCertificate cert;
    QStatus status = CreateMembershipCert(serial, signingBus, subject, subjectPubKey, guild, delegate, expiredInSecs, cert);
    if (ER_OK != status) {
        return status;
    }
    return cert.EncodeCertificateDER(der);
}

QStatus PermissionMgmtHelper::CreateMembershipCert(const qcc::String& serial,
                                                   ajn::BusAttachment& signingBus,
                                                   const qcc::String& subject, 
                                                   const qcc::ECCPublicKey* subjectPubKey,
                                                   const qcc::GUID128& guild,
                                                   bool delegate,
                                                   qcc::String& der)
{
    /* expire the cert in 1 hour */
    return CreateMembershipCert(serial, signingBus, subject, subjectPubKey, guild, delegate, 24 * 3600, der);
}

QStatus PermissionMgmtHelper::CreateMembershipCert(const qcc::String& serial,
                                                   ajn::BusAttachment& signingBus,
                                                   const qcc::String& subject, 
                                                   const qcc::ECCPublicKey* subjectPubKey,
                                                   const qcc::GUID128& guild,
                                                   qcc::String& der)
{
    return CreateMembershipCert(serial, signingBus, subject, subjectPubKey, guild, false, der);
}

QStatus PermissionMgmtHelper::CreateIdentityCert(ajn::BusAttachment& issuerBus,
                                                 const qcc::String& serial, 
                                                 const qcc::String& subject,
                                                 const qcc::ECCPublicKey* subjectPubKey,
                                                 const qcc::String& alias,
                                                 uint32_t expiredInSecs,
                                                 qcc::IdentityCertificate& cert)
{
    qcc::GUID128 issuer(0);
    GetGUID(issuerBus, issuer);

    QStatus status = ER_CRYPTO_ERROR;

    cert.SetSerial(reinterpret_cast<const uint8_t*>(serial.data()), serial.size());
    qcc::String issuerStr = issuer.ToString();
    cert.SetIssuerCN((const uint8_t*)issuerStr.data(), issuerStr.size());
    cert.SetSubjectCN((const uint8_t*)subject.data(), subject.size());
    cert.SetSubjectPublicKey(subjectPubKey);
    cert.SetAlias(alias);
    qcc::CertificateX509::ValidPeriod validity;
    BuildValidity(validity, expiredInSecs);
    cert.SetValidity(&validity);

    /* use the issuer bus to sign the cert */
    ajn::PermissionConfigurator& pc = issuerBus.GetPermissionConfigurator();

    if (ER_OK != pc.SignCertificate(cert)) {
        return status;
    }

    qcc::KeyInfoNISTP256 keyInfo;
    pc.GetSigningPublicKey(keyInfo);
    status = cert.Verify(keyInfo.GetPublicKey());
    if (ER_OK != status) {
        return status;
    }

    return ER_OK;
}