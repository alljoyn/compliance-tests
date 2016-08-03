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
#include "EventsActionsTestSuite.h"

#include <regex>

#include "ArrayParser.h"

#include <alljoyn\AllJoynStd.h>

using namespace ajn;
using namespace std;

const char* EventsActionsTestSuite::BUS_APPLICATION_NAME = "EventsActionsTestSuite";
const char* EventsActionsTestSuite::INTROSPECTION_XML_DESC_EXPECTED = "<description></description>";
const char* EventsActionsTestSuite::INTROSPECTION_XML_DESC_REGEX = "<description.*?>.*?</description>";
const char* EventsActionsTestSuite::INTROSPECTION_XML_DESC_PLACEHOLDER = "<description></description>";

EventsActionsTestSuite::EventsActionsTestSuite() : IOManager(ServiceFramework::CORE)
{

}

void EventsActionsTestSuite::SetUp()
{
	CLOG(INFO, "raw") << "====================================================";
	LOG(INFO) << "test setUp started";

	m_DutDeviceId = m_IxitMap.at("IXITCO_DeviceId");
	m_DutAppId = ArrayParser::parseAppIdFromString(m_IxitMap.at("IXITCO_AppId"));

	m_ServiceHelper = new ServiceHelper();

	QStatus status = m_ServiceHelper->initializeClient(BUS_APPLICATION_NAME, m_DutDeviceId, m_DutAppId,
		m_IcsMap.at("ICSCO_SrpKeyX"), m_IxitMap.at("IXITCO_SrpKeyXPincode"),
		m_IcsMap.at("ICSCO_SrpLogon"), m_IxitMap.at("IXITCO_SrpLogonUser"), m_IxitMap.at("IXITCO_SrpLogonPass"),
		m_IcsMap.at("ICSCO_EcdheNull"),
		m_IcsMap.at("ICSCO_EcdhePsk"), m_IxitMap.at("IXITCO_EcdhePskPassword"),
		m_IcsMap.at("ICSCO_EcdheEcdsa"), m_IxitMap.at("IXITCO_EcdheEcdsaPrivateKey"), m_IxitMap.at("IXITCO_EcdheEcdsaCertChain"));
	ASSERT_EQ(status, ER_OK) << "serviceHelper Initialize() failed: " << QCC_StatusText(status);

	m_DeviceAboutAnnouncement =
		m_ServiceHelper->waitForNextDeviceAnnouncement(
		atol(m_GeneralParameterMap.at("GPCO_AnnouncementTimeout").c_str()) * 1000);

	ASSERT_NE(m_DeviceAboutAnnouncement, nullptr) << "Timed out waiting for About announcement";

	SUCCEED();

	LOG(INFO) << "test setUp done";
	CLOG(INFO, "raw") << "====================================================";
}

void EventsActionsTestSuite::TearDown()
{
	CLOG(INFO, "raw") << "====================================================";
	LOG(INFO) << "test tearDown started";

	releaseResources();

	LOG(INFO) << "test tearDown done";
	CLOG(INFO, "raw") << "====================================================";

	storeResults();
}

void EventsActionsTestSuite::releaseResources()
{
	if (m_ServiceHelper != nullptr)
	{
		m_ServiceHelper->release();

		delete m_ServiceHelper;
	}
}
/**
	Verifies that the object or one of its child objects implement the
	AllSeenIntrospectable interface and has the "description" tag. If
	an object has a description in multiple languages, the introspection XMLs
	of each object should be identical
*/
TEST_F(EventsActionsTestSuite, EventsActions_v1_01)
{
	m_AboutProxy = m_ServiceHelper->connectAboutProxy(*m_DeviceAboutAnnouncement);

	ASSERT_NE(m_AboutProxy, nullptr) << "AboutProxy connection failed";
	SUCCEED() << "AboutProxy connected";

	list<string> objectPaths = getAllSeenIntrospectableObjectPaths();

	ASSERT_GT(objectPaths.size(), 0)
		<< "Looks like this object doesn't implement the " << org::allseen::Introspectable::InterfaceName
		<< " interface, even though it states it does in the announcement";

	for (const auto& objectPath : objectPaths)
	{
		LOG(INFO) << "Testing announced object path: " << objectPath;
		if (testObjectValidity(objectPath))
		{
			SUCCEED() << "The test of the announced object path: '" << objectPath << "' passed successfully";
		}
		else
		{
			FAIL() << "The test of the announced object path: '" << objectPath << "' has failed";
		}	
	}
}
/**
	Searches in the received announcement object paths that implement the
	AllSeenIntrospectable interface

	@return list of the object paths
*/
list<string> EventsActionsTestSuite::getAllSeenIntrospectableObjectPaths()
{
	list<string> introspectableObjectPathsList;
	XMLBasedBusIntrospector busIntrospector = m_ServiceHelper->getBusIntrospector(*m_DeviceAboutAnnouncement);
	AboutObjectDescription* aboutObjectDescriptions = m_DeviceAboutAnnouncement->getObjectDescriptions();

	size_t numberOfPaths = aboutObjectDescriptions->GetPaths(NULL, 0);
	const char** paths = new const char*[numberOfPaths];
	aboutObjectDescriptions->GetPaths(paths, numberOfPaths);

	for (size_t i = 0; i < numberOfPaths; ++i)
	{
		size_t numberOfInterfaces = aboutObjectDescriptions->GetInterfaces(paths[i], NULL, 0);
		const char** interfaces = new const char*[numberOfInterfaces];
		aboutObjectDescriptions->GetInterfaces(paths[i], interfaces, numberOfInterfaces);

		for (size_t j = 0; j < numberOfInterfaces; ++j)
		{
			if (string(interfaces[j]) == string(org::allseen::Introspectable::InterfaceName))
			{
				introspectableObjectPathsList.push_front(paths[i]);
				break;
			}
		}
		delete[] interfaces;
	}
	//delete[] paths;

	if (!introspectableObjectPathsList.empty())
	{
		return introspectableObjectPathsList;
	}
	
	// If AllSeenIntrospectable is not present on Announcement, check if it
	// is present on Bus
	for (size_t i = 0; i < numberOfPaths; ++i)
	{
		NodeDetail* nodeDetail = busIntrospector.introspect(paths[i]);
		IntrospectionNode introspectionNode = nodeDetail->getIntrospectionNode();

		for (auto introspectionInterface : *introspectionNode.getInterfaces())
		{
			// The AllSeenIntrospectable interface was found => add the path
			// to the returned list
			if (introspectionInterface.getName().compare(org::allseen::Introspectable::InterfaceName) == 0)
			{
				introspectableObjectPathsList.push_front(paths[i]);
			}
		}
	}

	return introspectableObjectPathsList;
}

/**
	Runs the test for the received object path

	@param t_ObjectPath
	@return whether description was found at any place for this object path
			or its sub objects
*/
bool EventsActionsTestSuite::testObjectValidity(string t_ObjectPath)
{
	ProxyBusObject proxyBusObject = m_ServiceHelper->getProxyBusObject(*m_DeviceAboutAnnouncement, t_ObjectPath);
	vector<const char*> descriptionLanguages;
	getDescriptionLanguages(proxyBusObject, t_ObjectPath, descriptionLanguages);

	if (descriptionLanguages.size() == 0)
	{
		LOG(WARNING) << "No description languages found for the object path: '"
			<< t_ObjectPath << "'. Introspecting child objects with NO_LANGUAGE";

		string introspectionXml;
		getIntrospectionXml(proxyBusObject, t_ObjectPath, "NO_LANGUAGE", introspectionXml);

		EXPECT_NE(introspectionXml, "") << "Introspection XML is NULL object path: '"
			<< t_ObjectPath << "'";

		return testChildrenObjectValidity(t_ObjectPath, introspectionXml);
	}

	return testObjectValidityPerLanguages(proxyBusObject, t_ObjectPath, descriptionLanguages);
	return true;
}

void EventsActionsTestSuite::getDescriptionLanguages(ProxyBusObject t_ProxyBusObject,
	string t_ObjectPath, vector<const char*>& t_Languages)
{
	Message replyMessage(*m_ServiceHelper->getBusAttachmentMgr()->getBusAttachment());
	QStatus status = t_ProxyBusObject.MethodCall(ajn::org::allseen::Introspectable::InterfaceName,
		"GetDescriptionLanguages", NULL, 0, replyMessage);

	ASSERT_EQ(status, ER_OK) << "Failed to call GetDescriptionLanguages for the object path: "
		<< t_ObjectPath;

	MsgArg* introspectionLanguages = NULL;
	size_t introspectionLanguagesArraySize;
	replyMessage->GetArg(0)->Get("as", &introspectionLanguagesArraySize, &introspectionLanguages);

	for (size_t j = 0; j < introspectionLanguagesArraySize; ++j)
	{
		const char* fieldValue;
		introspectionLanguages[j].Get("s", &fieldValue);
		t_Languages.push_back(fieldValue);
	}
}

void EventsActionsTestSuite::getIntrospectionXml(ProxyBusObject t_ProxyBusObject, string t_ObjectPath,
	string t_Language, string& t_IntrospectionXml)
{
	MsgArg languageMsgArg("s", t_Language.c_str());
	Message replyMessage(*m_ServiceHelper->getBusAttachmentMgr()->getBusAttachment());
	QStatus status = t_ProxyBusObject.MethodCall(ajn::org::allseen::Introspectable::InterfaceName,
		"IntrospectWithDescription", &languageMsgArg, 1, replyMessage);

	ASSERT_EQ(status, ER_OK)
		<< "Failed to call IntrospectWithDescription() method for object path: "
		<< t_ObjectPath;

	replyMessage->GetArg(0)->Get("s", t_IntrospectionXml.c_str());
}

bool EventsActionsTestSuite::testChildrenObjectValidity(std::string t_ParentObjectPath, std::string t_ParentIntroXml)
{
	/*EvAcIntrospectionNode introspectionNode = new EvAcIntrospectionNode(t_ParentObjectPath);
	introspectionNode.parse(t_ParentIntroXml);*/

	XMLBasedBusIntrospector busIntrospector = m_ServiceHelper->getBusIntrospector(*m_DeviceAboutAnnouncement);
	NodeDetail* nodeDetail = busIntrospector.introspect(t_ParentObjectPath);

	LOG(INFO) << "Testing child objects of the parent object: '"
		<< t_ParentObjectPath << "'";

	//list<EvAcIntrospectionNode> childrenNodes = introspectNode.getChildren();
	list<IntrospectionSubNode> childrenNodes = *nodeDetail->getIntrospectionNode().getSubNodes();
	bool descFoundBroth = false;

	if (childrenNodes.empty())
	{
		LOG(WARNING) << "The object " << t_ParentObjectPath << " doesn't have any child object";
		return descFoundBroth;
	}

	for (auto childNode : childrenNodes)
	{
		bool descFoundChild = testObjectValidity(childNode.getName());
		string logMsg = descFoundChild ? "contains a description tag" : "doesn't contain any description tag";

		LOG(INFO) << "The object or its offspring: '" << childNode.getName() << "' " << logMsg;

		if (!descFoundBroth)
		{
			descFoundBroth = descFoundChild;
		}
	}

	string logMsg = descFoundBroth ? "contain a description tag" : "doesn't contain any description tag";
	LOG(INFO) << "Child objects of the parent: '" << t_ParentObjectPath << "' " << logMsg;
	return descFoundBroth;
}

bool EventsActionsTestSuite::testObjectValidityPerLanguages(ProxyBusObject t_ProxyBusObject,
	string t_ParentObjectPath, vector<const char*> t_DescriptionLanguages)
{
	string firstLanguageXml;
	string firstLanguage;
	bool descriptionFound = false;

	for (auto language : t_DescriptionLanguages)
	{
		string currentXml;
		getIntrospectionXml(t_ProxyBusObject, t_ParentObjectPath, language, currentXml);
		EXPECT_FALSE(currentXml.empty()) << "Introspection XML is NULL object path: '"
			<< t_ParentObjectPath << "'";
		removeXmlDescription(currentXml);

		LOG(INFO) << "Testing language validity for the object path: '"
			<< t_ParentObjectPath << "', language: '" << language << "'";

		if (firstLanguageXml.empty())
		{
			EXPECT_TRUE(currentXml.find(INTROSPECTION_XML_DESC_EXPECTED) != std::string::npos)
				<< "The description tag wasn't found in the XML for the description language: '"
				<< language << "'";

			if (t_DescriptionLanguages.size() == 1)
			{
				LOG(INFO) << "The object '" << t_ParentObjectPath
					<< "' supports a single description language: '" << language << "'";
				return true;
			}

			firstLanguage = language;
			firstLanguageXml = currentXml;
			descriptionFound = true;
			continue;
		}

		LOG(INFO) << "Test identity of the XMLs in the first language: '"
			<< firstLanguage << "' and the current language: '"
			<< language << "', object path: '" << t_ParentObjectPath << "'";

		EXPECT_EQ(firstLanguageXml, currentXml)
			<< "The XML in the first language: '" << firstLanguage
			<< "' is not identical to the XML in the current language '"
			<< language << "', object path: '" << t_ParentObjectPath << "'";
	}

	testChildrenObjectValidity(t_ParentObjectPath, firstLanguageXml);
	return descriptionFound;
}

string EventsActionsTestSuite::removeXmlDescription(string t_Introspection)
{
	return regex_replace(t_Introspection, regex(INTROSPECTION_XML_DESC_REGEX),
		INTROSPECTION_XML_DESC_PLACEHOLDER);
}