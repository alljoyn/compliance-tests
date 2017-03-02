/******************************************************************************
*    Copyright (c) Open Connectivity Foundation (OCF), AllJoyn Open Source
*    Project (AJOSP) Contributors and others.
*    
*    SPDX-License-Identifier: Apache-2.0
*    
*    All rights reserved. This program and the accompanying materials are
*    made available under the terms of the Apache License, Version 2.0
*    which accompanies this distribution, and is available at
*    http://www.apache.org/licenses/LICENSE-2.0
*    
*    Copyright (c) Open Connectivity Foundation and Contributors to AllSeen
*    Alliance. All rights reserved.
*    
*    Permission to use, copy, modify, and/or distribute this software for
*    any purpose with or without fee is hereby granted, provided that the
*    above copyright notice and this permission notice appear in all
*    copies.
*    
*    THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL
*    WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED
*    WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE
*    AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL
*    DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR
*    PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER
*    TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
*    PERFORMANCE OF THIS SOFTWARE.
******************************************************************************/
#pragma once

#include <alljoyn\SecurityApplicationProxy.h>

class PermissionMgmtHelper
{
public:
	static QStatus CreateAllInclusiveManifest(ajn::Manifest&);
	static QStatus CreateIdentityCertChain(ajn::BusAttachment& caBus, 
                                           ajn::BusAttachment& issuerBus, 
                                           const qcc::String& serial,
		                                   const qcc::String& subject, 
                                           const qcc::ECCPublicKey* subjectPubKey, 
                                           const qcc::String& alias, 
                                           uint32_t expiredInSecs, 
		                                   qcc::IdentityCertificate* certChain, 
                                           size_t chainCount);
	static QStatus GetGUID(ajn::BusAttachment& bus,
                           qcc::GUID128& guid);
	static void BuildValidity(qcc::CertificateX509::ValidPeriod& validity,
                              uint32_t expiredInSecs);
	static QStatus SignManifest(ajn::BusAttachment& issuerBus,
                                const std::vector<uint8_t>& subjectThumbprint,
                                ajn::Manifest& manifest);
	static QStatus SignManifest(ajn::BusAttachment& issuerBus,
                                const qcc::CertificateX509& subjectCertificate,
                                ajn::Manifest& manifest);
	static QStatus SignManifests(ajn::BusAttachment& issuerBus,
                                 const qcc::CertificateX509& subjectCertificate,
                                 std::vector<ajn::Manifest>& manifests);
	static QStatus CreateMembershipCert(const qcc::String& serial,
                                        ajn::BusAttachment& signingBus,
                                        const qcc::String& subject, 
		                                const qcc::ECCPublicKey* subjectPubKey,
                                        const qcc::GUID128& guild,
                                        bool delegate,
                                        uint32_t expiredInSecs, 
		                                qcc::MembershipCertificate& cert);
	static QStatus CreateMembershipCert(const qcc::String& serial,
                                        ajn::BusAttachment& signingBus,
                                        const qcc::String& subject,
		                                const qcc::ECCPublicKey* subjectPubKey,
                                        const qcc::GUID128& guild,
                                        bool delegate,
                                        uint32_t expiredInSecs,
		                                qcc::String& der);
	static QStatus CreateMembershipCert(const qcc::String& serial,
                                        ajn::BusAttachment& signingBus,
                                        const qcc::String& subject,
		                                const qcc::ECCPublicKey* subjectPubKey,
                                        const qcc::GUID128& guild,
                                        bool delegate,
                                        qcc::String& der);
	static QStatus CreateMembershipCert(const qcc::String& serial,
                                        ajn::BusAttachment& signingBus,
                                        const qcc::String& subject,
		                                const qcc::ECCPublicKey* subjectPubKey,
                                        const qcc::GUID128& guild,
                                        qcc::String& der);
	static QStatus CreateIdentityCert(ajn::BusAttachment& issuerBus,
                                      const qcc::String& serial,
		                              const qcc::String& subject,
                                      const qcc::ECCPublicKey* subjectPubKey,
                                      const qcc::String& alias,
		                              uint32_t expiredInSecs,
                                      qcc::IdentityCertificate& cert);
};