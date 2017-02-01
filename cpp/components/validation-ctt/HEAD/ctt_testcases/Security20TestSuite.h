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

#include "ApplicationStateListenerImpl.h"
#include "IOManager.h"
#include "ServiceHelper.h"

class Security20TestSuite : public ::testing::Test, public IOManager
{
public:
	Security20TestSuite();
	void SetUp();
	void TearDown();

protected:
	static AJ_PCSTR BUS_APPLICATION_NAME;
	static const qcc::GUID128 ADMIN_GROUP_GUID;

	QStatus status;
    // Needed ICS
	bool m_SupportEcdheNull;
	bool m_SupportEcdhePsk;
	bool m_SupportEcdheEcdsa;
	bool m_SupportEcdheSpeke;
    // Needed IXIT
	std::string m_DutDeviceId = std::string{ "" };
	uint8_t* m_DutAppId{ nullptr };
	std::string m_EcdhePskPassword = std::string{ "" };
	std::string m_EcdheEcdsaPrivateKey = std::string{ "" };
	std::string m_EcdheEcdsaCertChain = std::string{ "" };
	std::string m_EcdheSpekePassword = std::string{ "" };
	uint16_t m_SecurityApplicationVersion;
	uint16_t m_SecurityManagedApplicationVersion;
    // Needed General Parameter
	long m_WaitForAnnouncementTimeoutInMs;

	ServiceHelper* m_ServiceHelper{ nullptr };
	AboutAnnouncementDetails* m_DeviceAboutAnnouncement{ nullptr };
	ExtendedSecurityApplicationProxy* m_SecurityApplicationProxy{ nullptr };
	ApplicationStateListenerImpl* m_ApplicationStateListener{ nullptr };
	qcc::ECCPublicKey* m_DutPublicKey{ nullptr };
	std::vector<ajn::Manifest> m_Manifests;
	std::vector<qcc::IdentityCertificate> m_IdentityCertificateChain;

    ajn::BusAttachment* m_TemporalPeer{ nullptr };

	QStatus SelfClaimManagerBus();
	QStatus InstallMembershipOnManager();

	void ResetDutSecurity();
	void releaseResources();

	// Security20-v1-01
	void ListenSignalAndCheckState(const std::string &t_BusName,
                                   ajn::PermissionConfigurator::ApplicationState t_ApplicationState);

	// Security20-v1-02
	void CallApplicationPropertiesAndCheckState(ajn::PermissionConfigurator::ApplicationState t_ApplicationState);

	// Security20-v1-04
	void ClaimDutBus();
	static QStatus GenerateAllowAllManifest(std::vector<ajn::Manifest>&);
	void GetAppPublicKey(qcc::ECCPublicKey&);
	QStatus EnableEcdheEcdsaInManager();
	void InstallMembershipOnDut(qcc::ECCPublicKey*);

	// Security20-v1-09
	void CreatePolicyDeny(ajn::PermissionPolicy& policy);
	void UpdatePolicy(ajn::PermissionPolicy& policy,
                      bool keepCAentry = true,
                      bool keepAdminGroupEntry = false,
                      bool keepInstallMembershipEntry = false);
	QStatus UpdatePolicyWithValuesFromDefaultPolicy(const ajn::PermissionPolicy& defaultPolicy,
                                                    ajn::PermissionPolicy& policy,
                                                    bool keepCAentry = true,
                                                    bool keepAdminGroupEntry = false,
                                                    bool keepInstallMembershipEntry = false);

	// Security20-v1-11
	void ComparePolicyAndDefaultPolicy(bool equal);
};