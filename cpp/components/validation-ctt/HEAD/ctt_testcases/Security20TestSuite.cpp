/******************************************************************************
* * Copyright (c) Open Connectivity Foundation (OCF) and AllJoyn Open
*    Source Project (AJOSP) Contributors and others.
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
*     THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL
*     WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED
*     WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE
*     AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL
*     DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR
*     PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER
*     TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
*     PERFORMANCE OF THIS SOFTWARE.
******************************************************************************/
#include "stdafx.h"
#include "Security20TestSuite.h"

#include "ArrayParser.h"
#include "Claim_ApplicationStateListener.h"

#include <thread>

#include <alljoyn\AllJoynStd.h>
#include <alljoyn\SecurityApplicationProxy.h>
#include <qcc\Crypto.h>

const char* Security20TestSuite::BUS_APPLICATION_NAME = "Security20TestSuite";

/*
* The unit test use many busy wait loops.  The busy wait loops were chosen
* over thread sleeps because of the ease of understanding the busy wait loops.
* Also busy wait loops do not require any platform specific threading code.
*/
#define WAIT_MSECS 5

Security20TestSuite::Security20TestSuite() : IOManager(ServiceFramework::CORE)
{

}

void Security20TestSuite::SetUp()
{
	CLOG(INFO, "raw") << "====================================================";
	LOG(INFO) << "test setUp started";

	m_DutDeviceId = m_IxitMap.at("IXITCO_DeviceId");
	m_DutAppId = ArrayParser::parseAppIdFromString(m_IxitMap.at("IXITCO_AppId"));
	m_DefaultLanguage = m_IxitMap.at("IXITCO_DefaultLanguage");

	m_ServiceHelper = new ServiceHelper();

	QStatus status = m_ServiceHelper->initializeClient(BUS_APPLICATION_NAME, m_DutDeviceId, m_DutAppId,
		m_IcsMap.at("ICSCO_SrpKeyX"), m_IxitMap.at("IXITCO_SrpKeyXPincode"),
		m_IcsMap.at("ICSCO_SrpLogon"), m_IxitMap.at("IXITCO_SrpLogonUser"), m_IxitMap.at("IXITCO_SrpLogonPass"),
		m_IcsMap.at("ICSCO_EcdheNull"),
		m_IcsMap.at("ICSCO_EcdhePsk"), m_IxitMap.at("IXITCO_EcdhePskPassword"),
		m_IcsMap.at("ICSCO_EcdheEcdsa"), m_IxitMap.at("IXITCO_EcdheEcdsaPrivateKey"), m_IxitMap.at("IXITCO_EcdheEcdsaCertChain"),
		m_IcsMap.at("ICSCO_EcdheSpeke"), m_IxitMap.at("IXITCO_EcdheSpekePassword"));
	ASSERT_EQ(status, ER_OK) << "serviceHelper Initialize() failed: " << QCC_StatusText(status);

	m_DeviceAboutAnnouncement =
		m_ServiceHelper->waitForNextDeviceAnnouncement(atol(
			m_GeneralParameterMap.at("GPCO_AnnouncementTimeout").c_str()
		) * 1000);

	ASSERT_NE(m_DeviceAboutAnnouncement, nullptr) << "Timed out waiting for About announcement";
	SUCCEED();

	m_AboutProxy = m_ServiceHelper->connectAboutProxy(*m_DeviceAboutAnnouncement);
	ASSERT_NE(m_AboutProxy, nullptr) << "AboutProxy connection failed";
	SUCCEED() << "AboutProxy connected";

	status = m_ServiceHelper->enableAuthentication("/Keystore");
	ASSERT_EQ(ER_OK, status);

	//setManifestTemplate(*m_ServiceHelper->getBusAttachmentMgr()->getBusAttachment());

	m_SecurityApplicationProxy = new ajn::SecurityApplicationProxy(*m_ServiceHelper->getBusAttachmentMgr()->getBusAttachment(),
		m_DeviceAboutAnnouncement->getServiceName().c_str(), 0);
	ajn::PermissionConfigurator::ApplicationState applicationStatePeer1;
	LOG(INFO) << "Retrieving Application State...";
	ASSERT_EQ(ER_OK, status = m_SecurityApplicationProxy->GetApplicationState(applicationStatePeer1))
		<< "Retrieving Application State returned status code " << QCC_StatusText(status);
	LOG(INFO) << "Checking if Application State is Claimable";
	ASSERT_EQ(ajn::PermissionConfigurator::CLAIMABLE, applicationStatePeer1) << "Application State is not Claimable";

	LOG(INFO) << "test setUp done";
	CLOG(INFO, "raw") << "====================================================";
}

void Security20TestSuite::setManifestTemplate(ajn::BusAttachment& bus)
{
	// All Inclusive manifest template
	ajn::PermissionPolicy::Rule::Member member[1];
	member[0].Set("*", ajn::PermissionPolicy::Rule::Member::NOT_SPECIFIED, ajn::PermissionPolicy::Rule::Member::ACTION_PROVIDE
		| ajn::PermissionPolicy::Rule::Member::ACTION_MODIFY
		| ajn::PermissionPolicy::Rule::Member::ACTION_OBSERVE);
	const size_t manifestSize = 1;
	ajn::PermissionPolicy::Rule manifestTemplate[manifestSize];
	manifestTemplate[0].SetObjPath("*");
	manifestTemplate[0].SetInterfaceName("*");
	manifestTemplate[0].SetMembers(1, member);
	EXPECT_EQ(ER_OK, bus.GetPermissionConfigurator().SetPermissionManifest(manifestTemplate, manifestSize));
}

void Security20TestSuite::TearDown()
{
	CLOG(INFO, "raw") << "====================================================";
	LOG(INFO) << "test tearDown started";

	releaseResources();

	LOG(INFO) << "test tearDown done";
	CLOG(INFO, "raw") << "====================================================";

	storeResults();
}

void Security20TestSuite::releaseResources()
{
	if (m_ServiceHelper != nullptr)
	{
		QStatus status = m_ServiceHelper->release();

		EXPECT_EQ(status, ER_OK) << "serviceHelper Release() failed: " << QCC_StatusText(status);
		delete m_ServiceHelper;
	}
}

TEST_F(Security20TestSuite, Security20_v1_01)
{
	Claim_ApplicationStateListener appStateListener;
	EXPECT_EQ(ER_OK, m_ServiceHelper->getBusAttachmentMgr()->getBusAttachment()->RegisterApplicationStateListener(appStateListener));

	//Wait for a maximum of 10 sec for the Application.State Signal.
	const clock_t beginTime = clock();
	while (!appStateListener.stateChanged && (clock() - beginTime < 10000))
	{
		std::this_thread::sleep_for(std::chrono::milliseconds(WAIT_MSECS));
	}

	ASSERT_TRUE(appStateListener.stateChanged) << "Application State signal not received";
}

TEST_F(Security20TestSuite, Security20_v1_02)
{
	QStatus status(ER_OK);
	// "Version"  Expected value=1
	LOG(INFO) << "Checking Version property";
	uint16_t securityApplicationVersion = 0;
	EXPECT_EQ(ER_OK, status = m_SecurityApplicationProxy->GetSecurityApplicationVersion(securityApplicationVersion))
		<< "Retrieving Version property returned status code: " << QCC_StatusText(status);
	EXPECT_EQ(1, securityApplicationVersion)
		<< "Retrieved Version property (" << securityApplicationVersion << ") does not match IXIT (" << 1 << ")";

	// "ApplicationState"  Expected value="Claimable"
	LOG(INFO) << "Checking ApplicationState property";
	ajn::PermissionConfigurator::ApplicationState applicationState;
	EXPECT_EQ(ER_OK, status = m_SecurityApplicationProxy->GetApplicationState(applicationState))
		<< "Retrieving ApplicationState property returned status code: " << QCC_StatusText(status);
	EXPECT_EQ(ajn::PermissionConfigurator::CLAIMABLE, applicationState)
		<< "Retrieved ApplicationState property value (" << ajn::PermissionConfigurator::ToString(applicationState) << "is not CLAIMABLE";

	// "ManifestTemplateDigest"  Expected value=Unknown
	LOG(INFO) << "Checking ManifestTemplateDigest property";
	uint8_t manifestTemplateDigest[qcc::Crypto_SHA256::DIGEST_SIZE];
	EXPECT_EQ(ER_BAD_ARG_2, status = m_SecurityApplicationProxy->GetManifestTemplateDigest(manifestTemplateDigest, qcc::Crypto_SHA256::DIGEST_SIZE))
		<< "Retrieving ManifestTemplateDigest property returned status code: " << QCC_StatusText(status);

	// "EccPublicKey"  Expected value=Public key of DUT
	LOG(INFO) << "Checking EccPublicKey property";
	qcc::ECCPublicKey eccPublicKey;
	EXPECT_EQ(ER_OK, status = m_SecurityApplicationProxy->GetEccPublicKey(eccPublicKey))
		<< "Retrieving EccPublicKey property returned status code: " << QCC_StatusText(status);
	/*ajn::PermissionConfigurator& pcPeer2 = peer2Bus.GetPermissionConfigurator();
	KeyInfoNISTP256 peer2KeyInfo;
	EXPECT_EQ(ER_OK, pcPeer2.GetSigningPublicKey(peer2KeyInfo));
	EXPECT_EQ(*peer2KeyInfo.GetPublicKey(), eccPublicKey);*/

	// "ManufacturerCertificate"  Expected value=empty array
	LOG(INFO) << "Checking ManufacturerCertificate property";
	ajn::MsgArg manufacturerCertificate;
	EXPECT_EQ(ER_OK, status = m_SecurityApplicationProxy->GetManufacturerCertificate(manufacturerCertificate))
		<< "Retrieving ManufacturerCertificate property returned status code: " << QCC_StatusText(status);
	EXPECT_EQ((size_t)0, manufacturerCertificate.v_array.GetNumElements())
		<< "ManufacturerCertificate property is not empty";

	// "ManifestTemplate"  Expected value=Unknown
	LOG(INFO) << "Checking ManifestTemplate property";
	ajn::MsgArg manifestTemplate;
	EXPECT_EQ(ER_OK, status = m_SecurityApplicationProxy->GetManifestTemplate(manifestTemplate))
		<< "Retrieving ManifestTemplate property returned status code " << QCC_StatusText(status);
	// manifestTemplate should be empty
	EXPECT_EQ((size_t)0, manifestTemplate.v_array.GetNumElements());

	// "ClaimCapabilities"  Expected value=Unknown
	LOG(INFO) << "Checking ClaimCapabilities property";
	ajn::PermissionConfigurator::ClaimCapabilities claimCapabilities;
	EXPECT_EQ(ER_OK, status = m_SecurityApplicationProxy->GetClaimCapabilities(claimCapabilities))
		<< "Retrieving ClaimCapabilities property returned status code: " << QCC_StatusText(status);
	EXPECT_EQ(ajn::PermissionConfigurator::CLAIM_CAPABILITIES_DEFAULT, claimCapabilities);

	LOG(INFO) << "Calling GetAllProperties method";
	ajn::MsgArg props;
	ASSERT_EQ(ER_OK, m_SecurityApplicationProxy->GetAllProperties(ajn::org::alljoyn::Bus::Security::Application::InterfaceName, props));
	// check GetAllProperties contains the values from org.alljoyn.Bus.Security.Application interface
	// The value of each element was already checked above. Just check the GetAllProperties
	// have an entry for each property we are interested in.
	LOG(INFO) << "Checking that all properties are retrieved";
	ajn::MsgArg* propArg;
	EXPECT_EQ(ER_OK, props.GetElement("{sv}", "Version", &propArg)) << "Version property is not retrieved";
	EXPECT_EQ(ER_OK, props.GetElement("{sv}", "ApplicationState", &propArg)) << "ApplicationState property is not retrieved";
	EXPECT_EQ(ER_OK, props.GetElement("{sv}", "ManifestTemplateDigest", &propArg)) << "ManifestTemplateDigest property is not retrieved";
	EXPECT_EQ(ER_OK, props.GetElement("{sv}", "EccPublicKey", &propArg)) << "EccPublicKey property is not retrieved";
	EXPECT_EQ(ER_OK, props.GetElement("{sv}", "ManufacturerCertificate", &propArg)) << "ManufacturerCertificate property is not retrieved";
	EXPECT_EQ(ER_OK, props.GetElement("{sv}", "ManifestTemplate", &propArg)) << "ManifestTemplate property is not retrieved";
	EXPECT_EQ(ER_OK, props.GetElement("{sv}", "ClaimCapabilities", &propArg)) << "ClaimCapabilities property is not retrieved";
}

/*TEST_F(Security20TestSuite, Security20_v1_03)
{
	QStatus status = ER_OK;
	status = m_SecurityApplicationProxy->SetProperty(ajn::org::alljoyn::Bus::Security::Application::InterfaceName, "Version", 1);
}*/
