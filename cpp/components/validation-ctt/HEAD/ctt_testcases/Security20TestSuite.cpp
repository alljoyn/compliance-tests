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
#include "Security20TestSuite.h"

#include "ArrayParser.h"
#include "ECDHENullListener.h"
#include "PermissionMgmtHelper.h"
#include "Security_SessionPortListener.h"

#include <thread>

#include <alljoyn\AllJoynStd.h>
#include <alljoyn\config\ConfigClient.h>
#include <alljoyn\SecurityApplicationProxy.h>
#include <qcc\Crypto.h>

#define CHECK_RETURN(x) if (ER_OK != (status = x)) { return status; }

const char* Security20TestSuite::BUS_APPLICATION_NAME = "Security20TestSuite";
const qcc::GUID128 Security20TestSuite::ADMIN_GROUP_GUID = qcc::GUID128("00112233445566778899AABBCCDDEEFF");
static const char* s_dutIdentityCertificateSerialNumber = "1010";
static const char* s_dutAdminGroupMembershipSerialNumber = "1";
static AJ_PCSTR s_defaultManifestTemplateXml =
	"<manifest>"
	"<node>"
	"<interface>"
	"<any>"
	"<annotation name = \"org.alljoyn.Bus.Action\" value = \"Modify\"/>"
	"<annotation name = \"org.alljoyn.Bus.Action\" value = \"Observe\"/>"
	"<annotation name = \"org.alljoyn.Bus.Action\" value = \"Provide\"/>"
	"</any>"
	"</interface>"
	"</node>"
	"</manifest>";

/*
* The unit test use many busy wait loops. The busy wait loops were chosen
* over thread sleeps because of the ease of understanding the busy wait loops.
* Also busy wait loops do not require any platform specific threading code.
*/
#define WAIT_MSECS 5

Security20TestSuite::Security20TestSuite() : IOManager(ServiceFramework::CORE)
{
	// Needed ICS
	m_SupportEcdheNull = m_IcsMap.at("ICSCO_EcdheNull");
	m_SupportEcdhePsk = m_IcsMap.at("ICSCO_EcdhePsk");
	m_SupportEcdheEcdsa = m_IcsMap.at("ICSCO_EcdheEcdsa");
	m_SupportEcdheSpeke = m_IcsMap.at("ICSCO_EcdheSpeke");

	// Needed IXIT
	m_DutDeviceId = m_IxitMap.at("IXITCO_DeviceId");
	m_DutAppId = ArrayParser::parseAppIdFromString(m_IxitMap.at("IXITCO_AppId"));
	m_EcdhePskPassword = m_IxitMap.at("IXITCO_EcdhePskPassword");
	m_EcdheEcdsaPrivateKey = m_IxitMap.at("IXITCO_EcdheEcdsaPrivateKey");
	m_EcdheEcdsaCertChain = m_IxitMap.at("IXITCO_EcdheEcdsaCertChain");
	m_EcdheSpekePassword = m_IxitMap.at("IXITCO_EcdheSpekePassword");
	m_SecurityApplicationVersion = ArrayParser::stringToUint16(m_IxitMap.at("IXITCO_ApplicationVersion").c_str());
	m_SecurityManagedApplicationVersion = ArrayParser::stringToUint16(m_IxitMap.at("IXITCO_ManagedApplicationVersion").c_str());
	
	// Needed General Parameters
	m_WaitForAnnouncementTimeoutInMs = atol(m_GeneralParameterMap.at("GPCO_AnnouncementTimeout").c_str()) * 1000;
}

void Security20TestSuite::SetUp()
{
	CLOG(INFO, "raw") << "====================================================";
	LOG(INFO) << "test setUp started";

	m_ServiceHelper = new ServiceHelper();

	ASSERT_EQ(ER_OK, status = m_ServiceHelper->initializeClient(BUS_APPLICATION_NAME, m_DutDeviceId, m_DutAppId))
		<< "serviceHelper Initialize() failed: " << QCC_StatusText(status);

	ASSERT_EQ(ER_OK, status = m_ServiceHelper->enableAuthentication("/Keystore",
		false, "", // SRP_KEYX is not needed
		false, "", "", // SRP_LOGON is not needed
		m_SupportEcdheNull,
		m_SupportEcdhePsk, m_EcdhePskPassword,
		m_SupportEcdheEcdsa, m_EcdheEcdsaPrivateKey, m_EcdheEcdsaCertChain,
		m_SupportEcdheSpeke, m_EcdheSpekePassword))
		<< "Calling EnablePeerSecurity returned status code: " << QCC_StatusText(status);

	ASSERT_EQ(ER_OK, status = SelfClaimManagerBus()) << "An error occurred while trying to self claim manager bus: " << QCC_StatusText(status);

	ASSERT_NE(nullptr, m_DeviceAboutAnnouncement =
		m_ServiceHelper->waitForNextDeviceAnnouncement(m_WaitForAnnouncementTimeoutInMs))
		<< "Timed out waiting for About announcement";

	m_SecurityApplicationProxy = m_ServiceHelper->connectSecurityApplicationProxy(*m_DeviceAboutAnnouncement);

	LOG(INFO) << "test setUp done";
	CLOG(INFO, "raw") << "====================================================";
}

QStatus Security20TestSuite::SelfClaimManagerBus()
{
	ajn::BusAttachment* managerBus = m_ServiceHelper->getBusAttachmentMgr()->getBusAttachment();
	// Retrieve manager ApplicationState property
	ajn::PermissionConfigurator::ApplicationState applicationState;
	CHECK_RETURN(managerBus->GetPermissionConfigurator().GetApplicationState(applicationState))

	switch (applicationState)
	{
		case ajn::PermissionConfigurator::CLAIMED:
		{
			break;
		}
		case ajn::PermissionConfigurator::NOT_CLAIMABLE:
		{
			// Set manager manifestTemplate
			CHECK_RETURN(managerBus->GetPermissionConfigurator().SetManifestTemplateFromXml(s_defaultManifestTemplateXml));
			// Create SecurityApplicationProxy with itself
			ajn::SessionOpts opts1;
			ajn::SessionId managerToManagerSessionId;
			ajn::SessionPort managerSessionPort = 42;
			Security_SessionPortListener managerSessionPortListener;
			CHECK_RETURN(managerBus->BindSessionPort(managerSessionPort, opts1, managerSessionPortListener));
			CHECK_RETURN(managerBus->JoinSession(managerBus->GetUniqueName().c_str(), managerSessionPort, NULL, managerToManagerSessionId, opts1));
			ajn::SecurityApplicationProxy sapWithManager(*managerBus, managerBus->GetUniqueName().c_str(), managerToManagerSessionId);
			// Create all allowed manifest
			ajn::Manifest manifests[1];
			CHECK_RETURN(PermissionMgmtHelper::CreateAllInclusiveManifest(manifests[0]));
			// Get manager key
			qcc::KeyInfoNISTP256 managerKey;
			ajn::PermissionConfigurator& pcManager = managerBus->GetPermissionConfigurator();
			CHECK_RETURN(pcManager.GetSigningPublicKey(managerKey));
			// Create identityCert
			const size_t certChainSize = 1;
			qcc::IdentityCertificate identityCertChainMaster[certChainSize];
			CHECK_RETURN(PermissionMgmtHelper::CreateIdentityCert(*managerBus,
				"0",
				ADMIN_GROUP_GUID.ToString(),
				managerKey.GetPublicKey(),
				"ManagerAlias",
				3600,
				identityCertChainMaster[0]));
			// Set claimable state
			managerBus->GetPermissionConfigurator().SetApplicationState(ajn::PermissionConfigurator::CLAIMABLE);
			// Sign created manifest
			CHECK_RETURN(PermissionMgmtHelper::SignManifest(*managerBus, identityCertChainMaster[0], manifests[0]));
			// Self claim manager
			CHECK_RETURN(sapWithManager.Claim(managerKey,
				ADMIN_GROUP_GUID,
				managerKey,
				identityCertChainMaster, certChainSize,
				manifests, ArraySize(manifests)));
			// Check that manager was claimed successfully
			ListenSignalAndCheckState(managerBus->GetUniqueName().c_str(), ajn::PermissionConfigurator::CLAIMED);
			// Install Admin group membership on manager
			CHECK_RETURN(InstallMembershipOnManager());
			break;
		}
		default:
		{
			return ER_FAIL;
		}
	}	

	return ER_OK;
}

QStatus Security20TestSuite::InstallMembershipOnManager()
{
	ajn::BusAttachment* managerBus = m_ServiceHelper->getBusAttachmentMgr()->getBusAttachment();
	// Get manager key
	qcc::KeyInfoNISTP256 managerPublicKey;
	ajn::PermissionConfigurator& pcManager = managerBus->GetPermissionConfigurator();
	CHECK_RETURN(pcManager.GetSigningPublicKey(managerPublicKey))
	// Create manager admin group membership certificate
	qcc::String membershipSerial = "0";
	qcc::MembershipCertificate managerMembershipCertificate[1];
	CHECK_RETURN(PermissionMgmtHelper::CreateMembershipCert(membershipSerial,
		*managerBus,
		managerBus->GetUniqueName(),
		managerPublicKey.GetPublicKey(),
		ADMIN_GROUP_GUID,
		true,
		3600,
		managerMembershipCertificate[0]));
	// Install admin group membership certificate on manager
	CHECK_RETURN(pcManager.InstallMembership(managerMembershipCertificate, 1))
}

void Security20TestSuite::TearDown()
{
	CLOG(INFO, "raw") << "====================================================";
	LOG(INFO) << "test tearDown started";

	ajn::PermissionConfigurator::ApplicationState applicationState;
	EXPECT_EQ(ER_OK, status = m_SecurityApplicationProxy->GetApplicationState(applicationState))
		<< "Retrieving ApplicationState property returned status code: " << QCC_StatusText(status);

	if (ajn::PermissionConfigurator::CLAIMED == applicationState)
	{
		ResetDutSecurity();
	}

	releaseResources();

	LOG(INFO) << "test tearDown done";
	CLOG(INFO, "raw") << "====================================================";

	storeResults();
}

void Security20TestSuite::releaseResources()
{
	if (nullptr != m_ApplicationStateListener)
	{
		delete m_ApplicationStateListener;
		m_ApplicationStateListener = nullptr;
	}

	if (nullptr != m_ServiceHelper)
	{
		EXPECT_EQ(ER_OK, status = m_ServiceHelper->release()) << "serviceHelper Release() failed: " << QCC_StatusText(status);
		delete m_ServiceHelper;
		m_ServiceHelper = nullptr;
	}
}

void Security20TestSuite::ResetDutSecurity()
{
	LOG(INFO) << "Calling Reset method on ManagedApplication interface";
	EXPECT_EQ(ER_OK, status = m_SecurityApplicationProxy->Reset())
		<< "Calling Reset on SecurityApplicationProxy returned status code: " << QCC_StatusText(status);
}

TEST_F(Security20TestSuite, Security20_v1_01)
{
	ListenSignalAndCheckState(m_DeviceAboutAnnouncement->getServiceName(), ajn::PermissionConfigurator::CLAIMABLE);
}

void Security20TestSuite::ListenSignalAndCheckState(const std::string& t_BusName, const ajn::PermissionConfigurator::ApplicationState& t_ApplicationState)
{
	// Register an Application.State signal listener
	m_ApplicationStateListener = new Claim_ApplicationStateListener();
	EXPECT_EQ(ER_OK, status = m_ServiceHelper->getBusAttachmentMgr()->getBusAttachment()->RegisterApplicationStateListener(*m_ApplicationStateListener))
		<< "Registering Application.State signal listener returned status code: " << QCC_StatusText(status);

	// Wait for a maximum of 10 sec for the Application.State signal
	const clock_t beginTime = clock();
	while (!m_ApplicationStateListener->checkApplicationState(t_BusName.c_str(), t_ApplicationState) && (clock() - beginTime < 10000))
	{
		std::this_thread::sleep_for(std::chrono::milliseconds(WAIT_MSECS));
	}

	bool expectedStateReceived = m_ApplicationStateListener->checkApplicationState(t_BusName.c_str(), t_ApplicationState);
	EXPECT_EQ(ER_OK, status = m_ServiceHelper->getBusAttachmentMgr()->getBusAttachment()->UnregisterApplicationStateListener(*m_ApplicationStateListener))
		<< "Unregistering Application.State signal listener returned status code: " << QCC_StatusText(status);

	if (!expectedStateReceived)
	{
		FAIL() << "Expected Application.State signal was not received";
	}
}

TEST_F(Security20TestSuite, Security20_v1_02)
{
	ListenSignalAndCheckState(m_DeviceAboutAnnouncement->getServiceName(), ajn::PermissionConfigurator::CLAIMABLE);
	CallApplicationPropertiesAndCheckState(ajn::PermissionConfigurator::CLAIMABLE);
}

void Security20TestSuite::CallApplicationPropertiesAndCheckState(const ajn::PermissionConfigurator::ApplicationState& t_ApplicationState)
{
	LOG(INFO) << "Checking that each GetProperty call returns an appropriate value on Security.Application interface";

	// "Version"  Expected value=1
	LOG(INFO) << "Checking Version property";
	{
		uint16_t securityApplicationVersion = 0;
		EXPECT_EQ(ER_OK, status = m_SecurityApplicationProxy->GetSecurityApplicationVersion(securityApplicationVersion))
			<< "Retrieving Version property returned status code: " << QCC_StatusText(status);
		EXPECT_EQ(m_SecurityApplicationVersion, securityApplicationVersion)
			<< "Retrieved Version property (" << securityApplicationVersion << ") does not match IXITCO_ApplicationVersion ("
			<< m_SecurityApplicationVersion << ")";
	}
	
	// "ApplicationState"  Expected value=t_ApplicationState
	LOG(INFO) << "Checking ApplicationState property";
	{
		ajn::PermissionConfigurator::ApplicationState applicationState;
		EXPECT_EQ(ER_OK, status = m_SecurityApplicationProxy->GetApplicationState(applicationState))
			<< "Retrieving ApplicationState property returned status code: " << QCC_StatusText(status);
		EXPECT_EQ(t_ApplicationState, applicationState)
			<< "Retrieved ApplicationState property value (" << ajn::PermissionConfigurator::ToString(applicationState) << " is not "
			<< ajn::PermissionConfigurator::ToString(t_ApplicationState);
	}
	
	// "ManifestTemplateDigest"  Expected value=Unknown
	LOG(INFO) << "Checking ManifestTemplateDigest property";
	{
		uint8_t manifestTemplateDigest[qcc::Crypto_SHA256::DIGEST_SIZE];
		EXPECT_EQ(ER_OK, status = m_SecurityApplicationProxy->GetManifestTemplateDigest(manifestTemplateDigest, qcc::Crypto_SHA256::DIGEST_SIZE))
			<< "Retrieving ManifestTemplateDigest property returned status code: " << QCC_StatusText(status);
	}
	
	// "EccPublicKey"  Expected value=Public key of DUT
	LOG(INFO) << "Checking EccPublicKey property";
	{
		qcc::ECCPublicKey eccPublicKey;
		EXPECT_EQ(ER_OK, status = m_SecurityApplicationProxy->GetEccPublicKey(eccPublicKey))
			<< "Retrieving EccPublicKey property returned status code: " << QCC_StatusText(status);
		EXPECT_TRUE(m_ApplicationStateListener->checkEccPublicKey(m_DeviceAboutAnnouncement->getServiceName().c_str(), eccPublicKey))
			<< "Retrieved EccPublicKey does not match the received from Application.State signal";
	}
	
	// "ManufacturerCertificate"  Expected value=empty array
	LOG(INFO) << "Checking ManufacturerCertificate property";
	{
		ajn::MsgArg manufacturerCertificate;
		EXPECT_EQ(ER_OK, status = m_SecurityApplicationProxy->GetManufacturerCertificate(manufacturerCertificate))
			<< "Retrieving ManufacturerCertificate property returned status code: " << QCC_StatusText(status);
		EXPECT_EQ((size_t)0, manufacturerCertificate.v_array.GetNumElements())
			<< "ManufacturerCertificate property is not empty";
	}
	
	// "ManifestTemplate"  Expected value=Unknown
	LOG(INFO) << "Checking ManifestTemplate property";
	{
		ajn::MsgArg manifestTemplate;
		EXPECT_EQ(ER_OK, status = m_SecurityApplicationProxy->GetManifestTemplate(manifestTemplate))
			<< "Retrieving ManifestTemplate property returned status code " << QCC_StatusText(status);
	}
	
	// "ClaimCapabilities"  Expected value=Unknown
	LOG(INFO) << "Checking ClaimCapabilities property";
	{
		ajn::PermissionConfigurator::ClaimCapabilities claimCapabilities;
		EXPECT_EQ(ER_OK, status = m_SecurityApplicationProxy->GetClaimCapabilities(claimCapabilities))
			<< "Retrieving ClaimCapabilities property returned status code: " << QCC_StatusText(status);
	}
	
	LOG(INFO) << "Calling GetAllProperties method";
	{
		ajn::MsgArg props;
		ASSERT_EQ(ER_OK, m_SecurityApplicationProxy->GetAllProperties(ajn::org::alljoyn::Bus::Security::Application::InterfaceName, props))
			<< "Calling GetAllProperties method on ManagedApplication interface returned status code: " << QCC_StatusText(status);
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
}

TEST_F(Security20TestSuite, Security20_v1_03)
{
	ListenSignalAndCheckState(m_DeviceAboutAnnouncement->getServiceName(), ajn::PermissionConfigurator::CLAIMABLE);

	LOG(INFO) << "Checking that each SetProperty call returns ER_BUS_PROPERTY_ACCESS_DENIED on Security.Application interface";

	LOG(INFO) << "Trying to set Version property";
	{
		EXPECT_EQ(ER_BUS_PROPERTY_ACCESS_DENIED, status = m_SecurityApplicationProxy->SetApplicationVersion(1))
			<< "Setting Version property returned status code: " << QCC_StatusText(status);
	}
	
	LOG(INFO) << "Trying to set ApplicationState property";
	{
		EXPECT_EQ(ER_BUS_PROPERTY_ACCESS_DENIED, status = m_SecurityApplicationProxy->SetApplicationState(ajn::PermissionConfigurator::NOT_CLAIMABLE))
			<< "Setting ApplicationState property returned status code: " << QCC_StatusText(status);
	}
	
	LOG(INFO) << "Trying to set ManifestTemplateDigest property";
	{
		EXPECT_EQ(ER_BUS_PROPERTY_ACCESS_DENIED, status = m_SecurityApplicationProxy->SetManifestTemplateDigest(0, { 1, 2, 3, 4 }))
			<< "Setting ManifestTemplateDigest property returned status code: " << QCC_StatusText(status);
	}
	
	LOG(INFO) << "Trying to set EccPublicKey property";
	{
		EXPECT_EQ(ER_BUS_PROPERTY_ACCESS_DENIED, status = m_SecurityApplicationProxy->SetEccPublicKey(0, 0, { 1, 2, 3, 4 }, { 1, 2, 3, 4 }))
			<< "Setting ManifestTemplateDigest property returned status code: " << QCC_StatusText(status);
	}
	
	LOG(INFO) << "Trying to set ManufacturerCertificate property";
	{
		uint8_t certificateEncoding = 1;
		std::vector<uint8_t> certificateData = { 1, 2, 3, 4 };
		ajn::MsgArg certificateArg;
		EXPECT_EQ(ER_OK, status = certificateArg.Set("(yay)", certificateEncoding, certificateData.size(), certificateData.data()))
			<< "Setting MsgArg value returned status code: " << QCC_StatusText(status);
		EXPECT_EQ(ER_BUS_PROPERTY_ACCESS_DENIED, status = m_SecurityApplicationProxy->SetManufacturerCertificate({ certificateArg }))
			<< "Setting ManufacturerCertificate property returned status code: " << QCC_StatusText(status);
	}
	
	LOG(INFO) << "Trying to set ManifestTemplate property";
	{
		std::vector<ajn::MsgArg> members = { ajn::MsgArg("(syy)", "*", (uint8_t)0, (uint8_t)7) };
		ajn::MsgArg ruleArg;
		EXPECT_EQ(ER_OK, status = ruleArg.Set("(ssya(syy))", "com/security/test", "com.security.test.Interface", (uint8_t)0, members.size(), members.data()))
			<< "Setting MsgArg value returned status code: " << QCC_StatusText(status);
		EXPECT_EQ(ER_BUS_PROPERTY_ACCESS_DENIED, status = m_SecurityApplicationProxy->SetManifestTemplate({ ruleArg }))
			<< "Setting ManifestTemplate property returned status code: " << QCC_StatusText(status);
	}
	
	LOG(INFO) << "Trying to set ClaimCapabilities property";
	{
		EXPECT_EQ(ER_BUS_PROPERTY_ACCESS_DENIED, status = m_SecurityApplicationProxy->SetClaimCapabilities(ajn::PermissionConfigurator::CAPABLE_ECDHE_ECDSA))
			<< "Setting ClaimCapabilities property returned status code: " << QCC_StatusText(status);
	}
}

TEST_F(Security20TestSuite, Security20_v1_04)
{
	ClaimDutBus();
}

void Security20TestSuite::ClaimDutBus()
{
	ListenSignalAndCheckState(m_DeviceAboutAnnouncement->getServiceName(),
		ajn::PermissionConfigurator::CLAIMABLE);

	// Obtain the publicKey that will be used as Certification Authority and Admin Group Authority
	qcc::KeyInfoNISTP256 adminGroupAuthority;
	EXPECT_EQ(ER_OK, status = m_ServiceHelper->getBusAttachmentMgr()->getBusAttachment()->GetPermissionConfigurator().GetSigningPublicKey(adminGroupAuthority))
		<< "Retrieving SigningPublicKey returned status code: " << QCC_StatusText(status);

	// Retrieve public key from to-be-claimed app to create identity cert
	m_DutPublicKey = new qcc::ECCPublicKey();
	GetAppPublicKey(*m_DutPublicKey);

	// Generate an allow all manifest to give access to all interfaces, methods, properties and signals
	m_Manifests.clear();
	EXPECT_EQ(ER_OK, status = GenerateAllowAllManifest(m_Manifests))
		<< "Generating an allow-all manifest returned status code: " << QCC_StatusText(status);

	// Create identity certificate chain
	m_IdentityCertificateChain.clear();
	m_IdentityCertificateChain.resize(2);
	EXPECT_EQ(ER_OK, status = PermissionMgmtHelper::CreateIdentityCertChain(*m_ServiceHelper->getBusAttachmentMgr()->getBusAttachment(),
		*m_ServiceHelper->getBusAttachmentMgr()->getBusAttachment(), s_dutIdentityCertificateSerialNumber, "subject",
		m_DutPublicKey, "service alias", 3600, m_IdentityCertificateChain.data(), m_IdentityCertificateChain.size()))
		<< "Creating an identity certificate chain returned status code: " << QCC_StatusText(status);
	EXPECT_EQ(ER_OK, status = PermissionMgmtHelper::SignManifests(*m_ServiceHelper->getBusAttachmentMgr()->getBusAttachment(), 
		m_IdentityCertificateChain.at(0), m_Manifests))
		<< "Signing manifests returned status code: " << QCC_StatusText(status);

	EXPECT_EQ(ER_OK, status = m_SecurityApplicationProxy->Claim(adminGroupAuthority, ADMIN_GROUP_GUID, adminGroupAuthority,
		m_IdentityCertificateChain.data(), m_IdentityCertificateChain.size(), m_Manifests.data(), m_Manifests.size()))
		<< "Calling Claim method on SecurityApplicationProxy returned status code: " << QCC_StatusText(status);

	ListenSignalAndCheckState(m_DeviceAboutAnnouncement->getServiceName(),
		ajn::PermissionConfigurator::CLAIMED);

	ASSERT_EQ(ER_OK, status = EnableEcdheEcdsaInManager())
		<< "Calling EnablePeerSecurity returned status code: " << QCC_StatusText(status);

	InstallMembershipOnDut(m_DutPublicKey);
}

QStatus Security20TestSuite::EnableEcdheEcdsaInManager()
{
	return m_ServiceHelper->enableAuthentication("/Keystore",
		false, "", // SRP_KEYX is not needed
		false, "", "", // SRP_LOGON is not needed
		false,
		false, "",
		true, m_EcdheEcdsaPrivateKey, m_EcdheEcdsaCertChain,
		false, "");
}

QStatus Security20TestSuite::GenerateAllowAllManifest(std::vector<ajn::Manifest>& manifests)
{
	manifests.resize(1);

	return PermissionMgmtHelper::CreateAllInclusiveManifest(manifests[0]);
}

void Security20TestSuite::GetAppPublicKey(qcc::ECCPublicKey& publicKey)
{
	EXPECT_EQ(ER_OK, status = m_SecurityApplicationProxy->GetEccPublicKey(publicKey))
		<< "Retrieving EccPublicKey property returned status code: " << QCC_StatusText(status);
}

void Security20TestSuite::InstallMembershipOnDut(qcc::ECCPublicKey* dutPublicKey)
{
	ajn::BusAttachment* managerBus = m_ServiceHelper->getBusAttachmentMgr()->getBusAttachment();
	// Create DUT's admin group membership certificate
	qcc::MembershipCertificate peer1MembershipCertificate[1];
	EXPECT_EQ(ER_OK, status = PermissionMgmtHelper::CreateMembershipCert(s_dutAdminGroupMembershipSerialNumber,
		*managerBus,
		m_DeviceAboutAnnouncement->getServiceName().c_str(),
		dutPublicKey,
		ADMIN_GROUP_GUID,
		false,
		3600,
		peer1MembershipCertificate[0]
	)) << "Creating membership certificate returned status code: " << QCC_StatusText(status);

	LOG(INFO) << "Installing Admin group membership on DUT";
	EXPECT_EQ(ER_OK, status = m_SecurityApplicationProxy->InstallMembership(peer1MembershipCertificate, 1))
		<< "Installing membership certificate returned status code: " << QCC_StatusText(status);
}

TEST_F(Security20TestSuite, Security20_v1_05)
{
	ClaimDutBus();

	LOG(INFO) << "Checking that each GetProperty call returns an appropriate value on Security.ManagedApplication interface";

	// "Version"  Expected value=1
	LOG(INFO) << "Checking Version property";
	{
		uint16_t managedApplicationVersion = 0;
		EXPECT_EQ(ER_OK, status = m_SecurityApplicationProxy->GetManagedApplicationVersion(managedApplicationVersion))
			<< "Retrieving Version property returned status code: " << QCC_StatusText(status);
		EXPECT_EQ(m_SecurityManagedApplicationVersion, managedApplicationVersion)
			<< "Retrieved Version property (" << managedApplicationVersion << ") does not match IXITCO_ManagedApplicationVersion ("
			<< m_SecurityManagedApplicationVersion << ")";
	}

	// "Identity"  Expected value=Identity Certificate of DUT
	LOG(INFO) << "Checking Identity property";
	{
		ajn::MsgArg identityArg;
		EXPECT_EQ(ER_OK, status = m_SecurityApplicationProxy->GetIdentity(identityArg))
			<< "Retrieving Identity property returned status code: " << QCC_StatusText(status);
		qcc::IdentityCertificate identityCertChain[2];
		EXPECT_EQ(ER_OK, m_SecurityApplicationProxy->MsgArgToIdentityCertChain(identityArg, identityCertChain, 2))
			<< "Parsing MsgArg to IdentityCertificate returned status code: " << QCC_StatusText(status);

		for (int i = 0; i < m_IdentityCertificateChain.size(); ++i)
		{
			EXPECT_EQ(qcc::String(reinterpret_cast<const char*>(m_IdentityCertificateChain.at(i).GetSerial()), m_IdentityCertificateChain.at(i).GetSerialLen()),
				qcc::String(reinterpret_cast<const char*>(identityCertChain[i].GetSerial()), identityCertChain[i].GetSerialLen()))
				<< "Identity Certificates serial at position " << i << " does not match";
			EXPECT_EQ(qcc::String(reinterpret_cast<const char*>(m_IdentityCertificateChain.at(i).GetIssuerCN()), m_IdentityCertificateChain.at(i).GetIssuerCNLength()),
				qcc::String(reinterpret_cast<const char*>(identityCertChain[i].GetIssuerCN()), identityCertChain[i].GetIssuerCNLength()))
				<< "Identity Certificates serial at position " << i << " does not match";
			EXPECT_EQ(qcc::String(reinterpret_cast<const char*>(m_IdentityCertificateChain.at(i).GetSubjectCN()), m_IdentityCertificateChain.at(i).GetSubjectCNLength()),
				qcc::String(reinterpret_cast<const char*>(identityCertChain[i].GetSubjectCN()), identityCertChain[i].GetSubjectCNLength()))
				<< "Identity Certificates serial at position " << i << " does not match";
			EXPECT_EQ(*m_IdentityCertificateChain.at(i).GetSubjectPublicKey(), *identityCertChain[i].GetSubjectPublicKey())
				<< "Identity Certificates publicKey at position " << i << " does not match";
			EXPECT_EQ(m_IdentityCertificateChain.at(i).GetAlias(), identityCertChain[i].GetAlias())
				<< "Identity Certificates alias at position " << i << " does not match";
			EXPECT_EQ(m_IdentityCertificateChain.at(i).GetValidity()->validFrom, identityCertChain[i].GetValidity()->validFrom)
				<< "Identity Certificates validFrom at position " << i << " does not match";
			EXPECT_EQ(m_IdentityCertificateChain.at(i).GetValidity()->validTo, identityCertChain[i].GetValidity()->validTo)
				<< "Identity Certificates validTo at position " << i << " does not match";
		}
	}

	// "Manifests"  Expected value=Manifests installed by the Claimer app
	LOG(INFO) << "Checking Manifests property";
	{
		std::vector<ajn::Manifest> manifests;
		EXPECT_EQ(ER_OK, status = m_SecurityApplicationProxy->GetManifests(manifests))
			<< "Retrieving Manifests property returned status code: " << QCC_StatusText(status);

		EXPECT_EQ(manifests.size(), m_Manifests.size())
			<< "The number of manifests retrieved does not match the one set";

		if (manifests.size() == m_Manifests.size())
		{
			for (int i = 0; i < manifests.size(); ++i)
			{
				EXPECT_EQ(manifests.at(i), m_Manifests.at(i))
					<< "Manifests at position " << i << " does not match";
			}
		}
	}

	// "IdentityCertificateId"  Expected value=Serial number of the Identity Certificate of DUT
	LOG(INFO) << "Checking IdentityCertificateId property";
	{
		qcc::String serial;
		qcc::KeyInfoNISTP256 issuerKeyInfo;
		EXPECT_EQ(ER_OK, status = m_SecurityApplicationProxy->GetIdentityCertificateId(serial, issuerKeyInfo))
			<< "Retrieving IdentityCertificateId property returned status code: " << QCC_StatusText(status);

		EXPECT_STREQ(s_dutIdentityCertificateSerialNumber, serial.c_str())
			<< "Retrieved serial does not match the one set";

		qcc::KeyInfoNISTP256 adminGroupAuthority;
		EXPECT_EQ(ER_OK, status = m_ServiceHelper->getBusAttachmentMgr()->getBusAttachment()->GetPermissionConfigurator().GetSigningPublicKey(adminGroupAuthority))
			<< "Retrieving SigningPublicKey returned status code: " << QCC_StatusText(status);
		EXPECT_EQ(adminGroupAuthority, issuerKeyInfo)
			<< "Retrieved issuerKeyInfo does not match the actual one";
	}

	{	
		// "DefaultPolicy"  Expected value=Default policy
		LOG(INFO) << "Checking DefaultPolicy property";
		ajn::PermissionPolicy defaultPolicy;
		EXPECT_EQ(ER_OK, status = m_SecurityApplicationProxy->GetDefaultPolicy(defaultPolicy))
			<< "Retrieving DefaultPolicy property returned status code: " << QCC_StatusText(status);

		// "PolicyVersion"  Expected value=Version number of the default policy
		LOG(INFO) << "Checking PolicyVersion property";
		uint32_t policyVersion = 0;
		EXPECT_EQ(ER_OK, status = m_SecurityApplicationProxy->GetPolicyVersion(policyVersion))
			<< "Retrieving PolicyVersion property returned status code: " << QCC_StatusText(status);
		ASSERT_EQ(defaultPolicy.GetVersion(), policyVersion) << "PolicyVersion is not equal to DefaultPolicy version";

		// "Policy"  Expected value=Default policy
		LOG(INFO) << "Checking Policy and DefaultPolicy properties";
		ajn::PermissionPolicy policy;
		EXPECT_EQ(ER_OK, status = m_SecurityApplicationProxy->GetPolicy(policy))
			<< "Retrieving Version property returned status code: " << QCC_StatusText(status);

		ASSERT_EQ(policy, defaultPolicy) << "Policy and DefaultPolicy are not equal";
	}

	// "MembershipSummaries"  Expected value=Membership summary
	LOG(INFO) << "Checking MembershipSummaries property";
	{
		ajn::MsgArg membershipSummaries;
		EXPECT_EQ(ER_OK, status = m_SecurityApplicationProxy->GetMembershipSummaries(membershipSummaries))
			<< "Retrieving MembershipSummaries property returned status code: " << QCC_StatusText(status);
		
		EXPECT_EQ((size_t)1, membershipSummaries.v_array.GetNumElements())
			<< "Calling GetMembershipSummaries returned an array with unexpected size. Expected size: " << 1;

		if (1 == membershipSummaries.v_array.GetNumElements())
		{
			qcc::KeyInfoNISTP256* keyInfos = new qcc::KeyInfoNISTP256[1];
			qcc::String* serials = new qcc::String[1];
			EXPECT_EQ(ER_OK, ajn::SecurityApplicationProxy::MsgArgToCertificateIds(membershipSummaries, serials, keyInfos, 1))
				<< " MsgArgToCertificateIds failed.";

			EXPECT_STREQ(s_dutAdminGroupMembershipSerialNumber, serials[0].c_str())
				<< "Retrieved serial does not match the one set";

			qcc::KeyInfoNISTP256 adminGroupAuthority;
			EXPECT_EQ(ER_OK, status = m_ServiceHelper->getBusAttachmentMgr()->getBusAttachment()->GetPermissionConfigurator().GetSigningPublicKey(adminGroupAuthority))
				<< "Retrieving SigningPublicKey returned status code: " << QCC_StatusText(status);
			EXPECT_EQ(adminGroupAuthority, keyInfos[0])
				<< "Retrieved issuerKeyInfo does not match the actual one";
		}
	}

	LOG(INFO) << "Calling GetAllProperties method";
	{
		ajn::MsgArg props;
		ASSERT_EQ(ER_OK, status = m_SecurityApplicationProxy->GetAllProperties(ajn::org::alljoyn::Bus::Security::ManagedApplication::InterfaceName, props))
			<< "Calling GetAllProperties returned status code: " << QCC_StatusText(status);

		LOG(INFO) << "Checking that all properties are retrieved";
		ajn::MsgArg* propArg;
		EXPECT_EQ(ER_OK, status = props.GetElement("{sv}", "Version", &propArg)) << "Retrieving Version property returned status code: " << QCC_StatusText(status);
		EXPECT_EQ(ER_OK, status = props.GetElement("{sv}", "Identity", &propArg)) << "Retrieving Identity property returned status code: " << QCC_StatusText(status);
		EXPECT_EQ(ER_OK, status = props.GetElement("{sv}", "Manifests", &propArg)) << "Retrieving Manifests property returned status code: " << QCC_StatusText(status);
		EXPECT_EQ(ER_OK, status = props.GetElement("{sv}", "IdentityCertificateId", &propArg)) << "Retrieving IdentityCertificateId property returned status code: " << QCC_StatusText(status);
		EXPECT_EQ(ER_OK, status = props.GetElement("{sv}", "PolicyVersion", &propArg)) << "Retrieving PolicyVersion property returned status code: " << QCC_StatusText(status);
		EXPECT_EQ(ER_OK, status = props.GetElement("{sv}", "Policy", &propArg)) << "Retrieving Policy property returned status code: " << QCC_StatusText(status);
		EXPECT_EQ(ER_OK, status = props.GetElement("{sv}", "DefaultPolicy", &propArg)) << "Retrieving DefaultPolicy property returned status code: " << QCC_StatusText(status);
		EXPECT_EQ(ER_OK, status = props.GetElement("{sv}", "MembershipSummaries", &propArg)) << "Retrieving MembershipSummaries property returned status code: " << QCC_StatusText(status);
	}
}

TEST_F(Security20TestSuite, Security20_v1_06)
{
	ClaimDutBus();

	LOG(INFO) << "Checking that each SetProperty call returns ER_BUS_PROPERTY_ACCESS_DENIED on Security.ManagedApplication interface";
	ajn::MsgArg propValue;

	LOG(INFO) << "Trying to set Version property";
	{
		EXPECT_EQ(ER_BUS_PROPERTY_ACCESS_DENIED, status = m_SecurityApplicationProxy->SetManagedApplicationVersion(1))
			<< "Setting Version property returned status code: " << QCC_StatusText(status);
	}
	
	LOG(INFO) << "Trying to set Identity property";
	{
		ajn::MsgArg identityCertificateMsgArg;
		EXPECT_EQ(ER_OK, status = m_SecurityApplicationProxy->GetIdentity(identityCertificateMsgArg))
			<< "Getting Identity property returned status code: " << QCC_StatusText(status);

		EXPECT_EQ(ER_BUS_PROPERTY_ACCESS_DENIED, status = m_SecurityApplicationProxy->SetIdentity(identityCertificateMsgArg))
			<< "Setting Identity property returned status code: " << QCC_StatusText(status);
	}
	
	LOG(INFO) << "Trying to set Manifest property";
	{
		std::vector<ajn::MsgArg> manifestsVector;
		EXPECT_EQ(ER_OK, status = m_SecurityApplicationProxy->GetManifestsMsgArg(manifestsVector))
			<< "Getting Manifests property as MsgArg returned status code: " << QCC_StatusText(status);

		EXPECT_EQ(ER_BUS_PROPERTY_ACCESS_DENIED, status = m_SecurityApplicationProxy->SetManifests(manifestsVector))
			<< "Setting Manifest property returned status code: " << QCC_StatusText(status);
	}
	
	LOG(INFO) << "Trying to set IdentityCertificateId property";
	{
		ajn::MsgArg identityCertificateIdMsgArg;
		EXPECT_EQ(ER_OK,
			status = m_SecurityApplicationProxy->GetIdentityCertificateIdMsgArg(identityCertificateIdMsgArg))
			<< "Getting IdentityCertificateId property as MsgArg returned status code: " << QCC_StatusText(status);

		EXPECT_EQ(ER_BUS_PROPERTY_ACCESS_DENIED,
			status = m_SecurityApplicationProxy->SetIdentityCertificateId(identityCertificateIdMsgArg))
			<< "Setting IdentityCertificateId property returned status code: " << QCC_StatusText(status);
	}
	
	LOG(INFO) << "Trying to set PolicyVersion property";
	{
		EXPECT_EQ(ER_BUS_PROPERTY_ACCESS_DENIED, status = m_SecurityApplicationProxy->SetPolicyVersion(1))
			<< "Setting PolicyVersion property returned status code: " << QCC_StatusText(status);
	}
	
	{
		ajn::PermissionPolicy defaultPolicy;
		EXPECT_EQ(ER_OK, status = m_SecurityApplicationProxy->GetDefaultPolicy(defaultPolicy))
			<< "Retrieving DefaultPolicy property returned status code: " << QCC_StatusText(status);

		ajn::MsgArg defaultPolicyMsgArg;
		EXPECT_EQ(ER_OK, defaultPolicy.Export(defaultPolicyMsgArg))
			<< "Exporting DefaultPolicy to MsgArg returned status code: " << QCC_StatusText(status);

		LOG(INFO) << "Trying to set Policy property";
		EXPECT_EQ(ER_BUS_PROPERTY_ACCESS_DENIED,
			status = m_SecurityApplicationProxy->SetPolicy(defaultPolicyMsgArg))
			<< "Setting Policy property returned status code: " << QCC_StatusText(status);

		LOG(INFO) << "Trying to set DefaultPolicy property";
		EXPECT_EQ(ER_BUS_PROPERTY_ACCESS_DENIED,
			status = m_SecurityApplicationProxy->SetDefaultPolicy(defaultPolicyMsgArg))
			<< "Setting DefaultPolicy property returned status code: " << QCC_StatusText(status);
	}
	
	LOG(INFO) << "Trying to set MembershipSummaries property";
	{
		ajn::MsgArg membershipSummaries;
		EXPECT_EQ(ER_OK, status = m_SecurityApplicationProxy->GetMembershipSummaries(membershipSummaries))
			<< "Getting MembershipSummaries returned status code: " << QCC_StatusText(status);

		EXPECT_EQ(ER_BUS_PROPERTY_ACCESS_DENIED,
			status = m_SecurityApplicationProxy->SetMembershipSummaries(membershipSummaries))
			<< "Setting MembershipSummaries property returned status code: " << QCC_StatusText(status);
	}
}

TEST_F(Security20TestSuite, Security20_v1_07)
{
	ClaimDutBus();
	// Create temporal peer
	ajn::BusAttachment peerBus("peerBusAttachment", true);
	EXPECT_EQ(ER_OK, peerBus.Start());
	EXPECT_EQ(ER_OK, peerBus.Connect());
	// Enable security on temporal peer
	ECDHENullListener* authListener = new ECDHENullListener(new ECDHENullHandlerImpl());
	EXPECT_EQ(ER_OK, peerBus.EnablePeerSecurity("ALLJOYN_ECDHE_NULL", authListener, "PeerKeystore"));
	// Create SecurityApplicationProxy between temporal peer and DUT
	ajn::SessionOpts opts(ajn::SessionOpts::TRAFFIC_MESSAGES, false,
		ajn::SessionOpts::PROXIMITY_ANY, ajn::TRANSPORT_ANY);
	ajn::SessionId peerToDutSessionId;
	EXPECT_EQ(ER_OK, status = peerBus.JoinSession(m_DeviceAboutAnnouncement->getServiceName().c_str(),
		m_DeviceAboutAnnouncement->getPort(), NULL, peerToDutSessionId, opts))
		<< "Calling JoinSession returned status code: " << QCC_StatusText(status);
	ajn::SecurityApplicationProxy securityApplicationProxy(peerBus,
		m_DeviceAboutAnnouncement->getServiceName().c_str(), peerToDutSessionId);

	LOG(INFO) << "Checking that each GetProperty call returns an appropriate value on Security.Application interface";
	{
		// "Version"  Expected value=1
		LOG(INFO) << "Checking Version property";
		{
			uint16_t securityApplicationVersion = 0;
			EXPECT_EQ(ER_OK, status = securityApplicationProxy.GetSecurityApplicationVersion(securityApplicationVersion))
				<< "Retrieving Version property returned status code: " << QCC_StatusText(status);
			EXPECT_EQ(1, securityApplicationVersion)
				<< "Retrieved Version property (" << securityApplicationVersion << ") does not match IXIT (" << 1 << ")";
		}

		// "ApplicationState"  Expected value="Claimed"
		LOG(INFO) << "Checking ApplicationState property";
		{
			ajn::PermissionConfigurator::ApplicationState applicationState;
			EXPECT_EQ(ER_OK, status = securityApplicationProxy.GetApplicationState(applicationState))
				<< "Retrieving ApplicationState property returned status code: " << QCC_StatusText(status);
			EXPECT_EQ(ajn::PermissionConfigurator::CLAIMED, applicationState)
				<< "Retrieved ApplicationState property value (" << ajn::PermissionConfigurator::ToString(applicationState) << " is not CLAIMED";
		}

		// "ManifestTemplateDigest"  Expected status=ER_PERMISSION_DENIED
		LOG(INFO) << "Checking ManifestTemplateDigest property";
		{
			uint8_t manifestTemplateDigest[qcc::Crypto_SHA256::DIGEST_SIZE];
			EXPECT_EQ(ER_PERMISSION_DENIED, status = securityApplicationProxy
				.GetManifestTemplateDigest(manifestTemplateDigest, qcc::Crypto_SHA256::DIGEST_SIZE))
				<< "Retrieving ManifestTemplateDigest property returned status code: " << QCC_StatusText(status);
		}

		// "EccPublicKey"  Expected status=ER_PERMISSION_DENIED
		LOG(INFO) << "Checking EccPublicKey property";
		{
			qcc::ECCPublicKey eccPublicKey;
			EXPECT_EQ(ER_PERMISSION_DENIED, status = securityApplicationProxy.GetEccPublicKey(eccPublicKey))
				<< "Retrieving EccPublicKey property returned status code: " << QCC_StatusText(status);
		}

		// "ManufacturerCertificate"  Expected status=ER_PERMISSION_DENIED
		LOG(INFO) << "Checking ManufacturerCertificate property";
		{
			ajn::MsgArg manufacturerCertificate;
			EXPECT_EQ(ER_PERMISSION_DENIED, status = securityApplicationProxy.GetManufacturerCertificate(manufacturerCertificate))
				<< "Retrieving ManufacturerCertificate property returned status code: " << QCC_StatusText(status);
		}

		// "ManifestTemplate"  Expected status=ER_PERMISSION_DENIED
		LOG(INFO) << "Checking ManifestTemplate property";
		{
			ajn::MsgArg manifestTemplate;
			EXPECT_EQ(ER_PERMISSION_DENIED, status = securityApplicationProxy.GetManifestTemplate(manifestTemplate))
				<< "Retrieving ManifestTemplate property returned status code " << QCC_StatusText(status);
		}

		// "ClaimCapabilities"  Expected status=ER_PERMISSION_DENIED
		LOG(INFO) << "Checking ClaimCapabilities property";
		{
			ajn::PermissionConfigurator::ClaimCapabilities claimCapabilities;
			EXPECT_EQ(ER_PERMISSION_DENIED, status = securityApplicationProxy.GetClaimCapabilities(claimCapabilities))
				<< "Retrieving ClaimCapabilities property returned status code: " << QCC_StatusText(status);
		}

		// "GetAllProperties"	Expected status=ER_PERMISSION_DENIED
		LOG(INFO) << "Calling GetAllProperties method";
		{
			ajn::MsgArg props;
			ASSERT_EQ(ER_PERMISSION_DENIED, securityApplicationProxy
				.GetAllProperties(ajn::org::alljoyn::Bus::Security::Application::InterfaceName, props))
				<< "Calling GetAllProperties method on ManagedApplication interface returned status code: " << QCC_StatusText(status);
		}
	}

	// Stop temporal peer
	peerBus.ClearKeyStore();
	peerBus.Stop();
	peerBus.Join();
}

TEST_F(Security20TestSuite, Security20_v1_08)
{
	ClaimDutBus();
	CallApplicationPropertiesAndCheckState(ajn::PermissionConfigurator::CLAIMED);
}

TEST_F(Security20TestSuite, Security20_v1_09)
{
	ClaimDutBus();
	// Update DUT policy to deny GetAboutData to PEER_ALL
	ajn::PermissionPolicy dutPolicy;
	CreatePolicyDeny(dutPolicy);
	UpdatePolicy(dutPolicy, true, true, true);
	// Create temporal peer
	ajn::BusAttachment peerBus("peerBusAttachment", true);
	EXPECT_EQ(ER_OK, peerBus.Start());
	EXPECT_EQ(ER_OK, peerBus.Connect());
	// Enable security on temporal peer
	ECDHENullListener* authListener = new ECDHENullListener(new ECDHENullHandlerImpl());
	EXPECT_EQ(ER_OK, status = peerBus.EnablePeerSecurity("ALLJOYN_ECDHE_NULL", authListener, "PeerKeystore"))
		<< "Calling EnablePeerSecurity returned status code: " << QCC_StatusText(status);

	LOG(INFO) << "Checking that GetAboutData returns ER_PERMISSION_DENIED status";
	{
		// Create SecurityApplicationProxy between temporal peer and DUT
		ajn::SessionOpts opts(ajn::SessionOpts::TRAFFIC_MESSAGES, false,
			ajn::SessionOpts::PROXIMITY_ANY, ajn::TRANSPORT_ANY);
		ajn::SessionId peerToDutSessionId;
		EXPECT_EQ(ER_OK, status = peerBus.JoinSession(m_DeviceAboutAnnouncement->getServiceName().c_str(),
			m_DeviceAboutAnnouncement->getPort(), NULL, peerToDutSessionId, opts))
			<< "Calling JoinSession returned status code: " << QCC_StatusText(status);
		// Create AboutProxy between temporal peer and DUT
		ajn::AboutProxy aboutProxy(peerBus, m_DeviceAboutAnnouncement->getServiceName().c_str(),
			peerToDutSessionId);
		// Check that DUT implements org.alljoyn.About interface
		EXPECT_TRUE(aboutProxy.ImplementsInterface(ajn::org::alljoyn::About::InterfaceName))
			<< "ProxyBusObject does not implement org.alljoyn.About interface";
		// "GetAboutData"	Expected status=ER_PERMISSION_DENIED
		ajn::MsgArg aboutData;
		EXPECT_EQ(ER_PERMISSION_DENIED, status = aboutProxy.GetAboutData("en", aboutData))
			<< "Calling AboutData on AboutProxy returned status code: " << QCC_StatusText(status);
	}
	// Stop temporal peer
	peerBus.ClearKeyStore();
	peerBus.Stop();
	peerBus.Join();
}

void Security20TestSuite::CreatePolicyDeny(ajn::PermissionPolicy &policy)
{
	//Permission policy that will be installed on dut
	policy.SetVersion(1);
	{
		ajn::PermissionPolicy::Acl acls[1];
		{
			ajn::PermissionPolicy::Peer peers[1];
			peers[0].SetType(ajn::PermissionPolicy::Peer::PEER_ALL);
			acls[0].SetPeers(1, peers);
		}
		{
			ajn::PermissionPolicy::Rule rules[2];
			//rule 0
			rules[0].SetObjPath(ajn::org::alljoyn::About::ObjectPath);
			rules[0].SetInterfaceName(ajn::org::alljoyn::About::InterfaceName);
			{
				ajn::PermissionPolicy::Rule::Member members[1];
				members[0].Set("GetAboutData",
					ajn::PermissionPolicy::Rule::Member::METHOD_CALL,
					0 /*DENY*/);
				rules[0].SetMembers(1, members);
			}
			//rules 1
			rules[1].SetObjPath("*");
			rules[1].SetInterfaceName("*");
			{
				ajn::PermissionPolicy::Rule::Member members[1];
				members[0].Set("*",
					ajn::PermissionPolicy::Rule::Member::NOT_SPECIFIED,
					ajn::PermissionPolicy::Rule::Member::ACTION_MODIFY |
					ajn::PermissionPolicy::Rule::Member::ACTION_OBSERVE |
					ajn::PermissionPolicy::Rule::Member::ACTION_PROVIDE);
				rules[0].SetMembers(1, members);
			}
			acls[0].SetRules(2, rules);
		}
		policy.SetAcls(1, acls);
	}
}

void Security20TestSuite::UpdatePolicy(ajn::PermissionPolicy& policy, bool keepCAentry, bool keepAdminGroupEntry, bool keepInstallMembershipEntry)
{
	LOG(INFO) << "Updating policy data with values from default policy";
	{
		ajn::PermissionPolicy dutDefaultPolicy;
		EXPECT_EQ(ER_OK, m_SecurityApplicationProxy->GetDefaultPolicy(dutDefaultPolicy));
		UpdatePolicyWithValuesFromDefaultPolicy(dutDefaultPolicy, policy, keepCAentry, keepAdminGroupEntry, keepInstallMembershipEntry);
	}

	LOG(INFO) << "Trying to update policy and securing connection again";
	{
		EXPECT_EQ(ER_OK, status = m_SecurityApplicationProxy->UpdatePolicy(policy))
			<< "Calling UpdatePolicy method returned status code: " << QCC_StatusText(status);
		EXPECT_EQ(ER_OK, status = m_SecurityApplicationProxy->SecureConnection(true))
			<< "Calling SecureConnection method returned status code: " << QCC_StatusText(status);
	}
}

QStatus Security20TestSuite::UpdatePolicyWithValuesFromDefaultPolicy(const ajn::PermissionPolicy& defaultPolicy,
	ajn::PermissionPolicy& policy, bool keepCAentry, bool keepAdminGroupEntry, bool keepInstallMembershipEntry)
{
	size_t count = policy.GetAclsSize();

	if (keepCAentry) {
		++count;
	}
	if (keepAdminGroupEntry) {
		++count;
	}
	if (keepInstallMembershipEntry) {
		++count;
	}

	ajn::PermissionPolicy::Acl* acls = new ajn::PermissionPolicy::Acl[count];
	size_t idx = 0;
	for (size_t cnt = 0; cnt < defaultPolicy.GetAclsSize(); ++cnt) {
		if (defaultPolicy.GetAcls()[cnt].GetPeersSize() > 0) {
			if (defaultPolicy.GetAcls()[cnt].GetPeers()[0].GetType() == ajn::PermissionPolicy::Peer::PEER_FROM_CERTIFICATE_AUTHORITY) {
				if (keepCAentry) {
					acls[idx++] = defaultPolicy.GetAcls()[cnt];
				}
			}
			else if (defaultPolicy.GetAcls()[cnt].GetPeers()[0].GetType() == ajn::PermissionPolicy::Peer::PEER_WITH_MEMBERSHIP) {
				if (keepAdminGroupEntry) {
					acls[idx++] = defaultPolicy.GetAcls()[cnt];
				}
			}
			else if (defaultPolicy.GetAcls()[cnt].GetPeers()[0].GetType() == ajn::PermissionPolicy::Peer::PEER_WITH_PUBLIC_KEY) {
				if (keepInstallMembershipEntry) {
					acls[idx++] = defaultPolicy.GetAcls()[cnt];
				}
			}
		}
	}

	for (size_t cnt = 0; cnt < policy.GetAclsSize(); ++cnt) {
		QCC_ASSERT(idx <= count);
		acls[idx++] = policy.GetAcls()[cnt];
	}
	// Lines added to maintain the update as a modification of DefaultPolicy
	uint32_t version = policy.GetVersion();
	policy = defaultPolicy;
	policy.SetVersion(version);

	policy.SetAcls(count, acls);
	delete[] acls;
	return ER_OK;
}

TEST_F(Security20TestSuite, Security20_v1_11)
{
	ClaimDutBus();
	// Modify Policy
	ajn::PermissionPolicy dutPolicy;
	CreatePolicyDeny(dutPolicy);
	UpdatePolicy(dutPolicy, true, true, true);
	ComparePolicyAndDefaultPolicy(false);

	LOG(INFO) << "Checking that ResetPolicy resets back to the DefaultPolicy";
	{
		ajn::PermissionPolicy policy;
		EXPECT_EQ(ER_OK, m_SecurityApplicationProxy->ResetPolicy())
			<< "Calling ResetPolicy method returned status code: " << QCC_StatusText(status);
	}
	// After reset, check that Policy is back to DefaultPolicy
	ComparePolicyAndDefaultPolicy(true);
}

void Security20TestSuite::ComparePolicyAndDefaultPolicy(const bool equal)
{
	LOG(INFO) << "Comparing Policy and DefaultPolicy";
	{
		ajn::PermissionPolicy defaultPolicy;
		EXPECT_EQ(ER_OK, status = m_SecurityApplicationProxy->GetDefaultPolicy(defaultPolicy))
			<< "Calling GetDefaultPolicy on SecurityApplicationProxy returned status code: " << QCC_StatusText(status);

		ajn::PermissionPolicy policy;
		EXPECT_EQ(ER_OK, status = m_SecurityApplicationProxy->GetPolicy(policy))
			<< "Calling GetPolicy on SecurityApplicationProxy returned status code: " << QCC_StatusText(status);

		if (equal)
		{
			ASSERT_EQ(policy, defaultPolicy) << "Policy and DefaultPolicy are not equal";
		}
		else
		{
			ASSERT_NE(policy, defaultPolicy) << "Policy and DefaultPolicy are equal";
		}
	}
}

TEST_F(Security20TestSuite, Security20_v1_12)
{
	ClaimDutBus();
	// Modify Policy
	ajn::PermissionPolicy dutPolicy;
	CreatePolicyDeny(dutPolicy);
	UpdatePolicy(dutPolicy, true, true, true);
	ComparePolicyAndDefaultPolicy(false);
	// Reset DUT
	ResetDutSecurity();
	
	LOG(INFO) << "Checking that DUT returns to the CLAIMABLE state after Reset";
	{
		ajn::PermissionConfigurator::ApplicationState applicationState;
		EXPECT_EQ(ER_OK, status = m_SecurityApplicationProxy->GetApplicationState(applicationState))
			<< "Calling GetApplicationState method returned status code: " << QCC_StatusText(status);
		EXPECT_EQ(ajn::PermissionConfigurator::CLAIMABLE, applicationState)
			<< "Retrieved application State (" << ajn::PermissionConfigurator::ToString(applicationState) << ") is different from CLAIMABLE";
	}

	// Policy and DefaultPolicy cannot be compared at this point because it is not possible to access to DUT's PermissionConfigurator
	// Is claiming again and accessing to ManagedApplication interface the solution?
}