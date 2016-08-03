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
#include "AboutTestSuite.h"

#include <iomanip>
#include <sstream>
#include <regex>

#include "AboutAnnouncementDetails.h"
#include "ArrayParser.h"
#include "BusIntrospector.h"
#include "InterfaceValidator.h"
#include "ValidationResult.h"
#include "XMLParser.h"

#include <alljoyn\AllJoynStd.h>

using namespace ajn;
using namespace std;

const char* AboutTestSuite::BUS_APPLICATION_NAME = "AboutTestSuite";
const char* AboutTestSuite::DATE_FORMAT = "%Y-%m-%d";
const char* AboutTestSuite::URL_REGEX = R"(^(([^:\/?#]+):)?(//([^\/?#]*))?([^?#]*)(\?([^#]*))?(#(.*))?)";

AboutTestSuite::AboutTestSuite() : IOManager(ServiceFramework::CORE)
{

}

void AboutTestSuite::SetUp()
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
		m_IcsMap.at("ICSCO_RsaKeyX"), m_IxitMap.at("IXITCO_RsaKeyXPrivateKey"), m_IxitMap.at("IXITCO_RsaKeyXCertX509"),
		m_IcsMap.at("ICSCO_PinKeyX"), m_IxitMap.at("IXITCO_PinKeyXPincode"));
	ASSERT_EQ(status, ER_OK) << "serviceHelper Initialize() failed: " << QCC_StatusText(status);

	m_DeviceAboutAnnouncement = 
		m_ServiceHelper->waitForNextDeviceAnnouncement(atol(
		m_GeneralParameterMap.at("GPCO_AnnouncementTimeout").c_str()
		) * 1000);

	ASSERT_NE(m_DeviceAboutAnnouncement, nullptr) << "Timed out waiting for About announcement";

	SUCCEED();

	LOG(INFO) << "test setUp done";
	CLOG(INFO, "raw") << "====================================================";
}

void AboutTestSuite::TearDown()
{
	CLOG(INFO, "raw") << "====================================================";
	LOG(INFO) << "test tearDown started";

	releaseResources();

	LOG(INFO) << "test tearDown done";
	CLOG(INFO, "raw") << "====================================================";

	storeResults();
}

void AboutTestSuite::releaseResources()
{
	if (m_ServiceHelper != nullptr)
	{
		m_ServiceHelper->release();
		delete m_ServiceHelper;
	}
}

TEST_F(AboutTestSuite, About_v1_01)
{
	LOG(INFO) << "Retrieving About announcement's version parameter";

	if (m_DeviceAboutAnnouncement->getVersion() == ArrayParser::stringToUint16(m_IxitMap.at("IXITCO_AboutVersion").c_str()))
	{
		SUCCEED() << "About version obtained matches IXITCO_AboutVersion";
	}
	else
	{
		FAIL() << "About version obtained does not match IXITCO_AboutVersion";
	}

	validatePathIfAboutInterfacePresentInAnnouncement();
	verifyAboutData(*m_DeviceAboutAnnouncement->getAboutData());

	m_AboutProxy = m_ServiceHelper->connectAboutProxy(*m_DeviceAboutAnnouncement);

	ASSERT_NE(m_AboutProxy, nullptr) << "AboutProxy connection failed";
	SUCCEED() << "AboutProxy connected";
}

void AboutTestSuite::validatePathIfAboutInterfacePresentInAnnouncement()
{
	const char* aboutPath = getAboutInterfacePathFromAnnouncement();

	if (aboutPath != nullptr)
	{
		ASSERT_STREQ(aboutPath, org::alljoyn::About::ObjectPath)
			<< "About interface present at the wrong path";
		SUCCEED() << "About interface present at the right path";
	}
	else
	{
		LOG(INFO) << "About interface not present in announcement";
	}
}

const char* AboutTestSuite::getAboutInterfacePathFromAnnouncement()
{
	const char* aboutPath = nullptr;

	size_t numberOfPaths = m_DeviceAboutAnnouncement->getObjectDescriptions()->GetPaths(NULL, 0);
	const char** paths = new const char*[numberOfPaths];
	m_DeviceAboutAnnouncement->getObjectDescriptions()->GetPaths(paths, numberOfPaths);

	for (size_t i = 0; i < numberOfPaths; ++i)
	{
		size_t numberOfInterfaces = m_DeviceAboutAnnouncement->getObjectDescriptions()->GetInterfaces(paths[i], NULL, 0);
		const char** interfaces = new const char*[numberOfInterfaces];
		m_DeviceAboutAnnouncement->getObjectDescriptions()->GetInterfaces(paths[i], interfaces, numberOfInterfaces);

		for (size_t j = 0; j < numberOfInterfaces; ++j)
		{
			if (interfaces[j] == org::alljoyn::About::InterfaceName)
			{
				aboutPath = paths[i];
				break;
			}
		}
		delete[] interfaces;

		if (aboutPath != nullptr)
		{
			break;
		}
	}
	delete[] paths;

	return aboutPath;
}

void AboutTestSuite::verifyAboutData(AboutData& t_AboutData)
{
	string stringValue;
	verifyFieldIsPresent(AboutData::APP_ID, t_AboutData, stringValue);

	verifyFieldIsPresent(AboutData::DEFAULT_LANGUAGE, t_AboutData, stringValue);
	compareAbout(AboutData::DEFAULT_LANGUAGE, m_IxitMap.at("IXITCO_DefaultLanguage"),
		stringValue, "");

	if (m_IcsMap.at("ICSCO_DeviceName"))
	{
		verifyFieldIsPresent(AboutData::DEVICE_NAME, t_AboutData, stringValue);
		compareAbout(AboutData::DEVICE_NAME, m_IxitMap.at("IXITCO_DeviceName"),
			stringValue, "");
	}

	verifyFieldIsPresent(AboutData::DEVICE_ID, t_AboutData, stringValue);
	compareAbout(AboutData::DEVICE_ID, m_IxitMap.at("IXITCO_DeviceId"),
		stringValue, "");

	verifyFieldIsPresent(AboutData::APP_NAME, t_AboutData, stringValue);
	compareAbout(AboutData::APP_NAME, m_IxitMap.at("IXITCO_AppName"),
		stringValue, "");

	verifyFieldIsPresent(AboutData::MANUFACTURER, t_AboutData, stringValue);
	compareAbout(AboutData::MANUFACTURER, m_IxitMap.at("IXITCO_Manufacturer"),
		stringValue, "");

	verifyFieldIsPresent(AboutData::MODEL_NUMBER, t_AboutData, stringValue);
	compareAbout(AboutData::MODEL_NUMBER, m_IxitMap.at("IXITCO_ModelNumber"),
		stringValue, "");
}

void AboutTestSuite::verifyFieldIsPresent(const char* t_FieldName,
	AboutData& t_AboutData, std::string& t_StringValue)
{
	MsgArg* field_value_msg_arg;
	t_AboutData.GetField(t_FieldName, field_value_msg_arg);

	t_StringValue.clear();

	ASSERT_NE(field_value_msg_arg, nullptr) << t_FieldName << " is a required field";

	if (field_value_msg_arg->Signature() == "s")
	{
		const char* field_value;
		field_value_msg_arg->Get("s", &field_value);
		t_StringValue.append(field_value);
	}
	else if (field_value_msg_arg->Signature() == "as")
	{
		size_t field_value_array_size;
		MsgArg* field_value_array;
		field_value_msg_arg->Get("as", &field_value_array_size, &field_value_array);

		for (size_t j = 0; j < field_value_array_size; ++j)
		{
			const char* field_value;
			field_value_array[j].Get("s", &field_value);
			t_StringValue.append(field_value);
		}
	}
	else if (field_value_msg_arg->Signature() == "ay")
	{
		size_t field_value_array_size;
		uint8_t* field_value_array;
		field_value_msg_arg->Get("ay", &field_value_array_size, &field_value_array);

		t_StringValue.append(ArrayParser::parseAppId(field_value_array));
	}

	SUCCEED() << t_FieldName << " is present and is '" << t_StringValue << "'";
}

void AboutTestSuite::compareAbout(const char* t_FieldName, const string& t_ExpectedAboutFieldValue,
	const string& t_AboutFieldValue, string t_Language)
{
	if (t_Language.empty())
	{
		t_Language = "unspecified";
	}

	ASSERT_EQ(t_AboutFieldValue, t_ExpectedAboutFieldValue)
		<< t_FieldName << " value returned from the About Announcement: '"
		<< t_AboutFieldValue << "' does not match IXITCO_" << t_FieldName <<
		": '" << t_ExpectedAboutFieldValue << "' for language '" << t_Language << "'";

	SUCCEED() << t_FieldName << " value returned from the About Announcement: '"
		<< t_AboutFieldValue << "' matches IXITCO_" << t_FieldName <<
		": '" << t_ExpectedAboutFieldValue << "' for language '" << t_Language << "'";
}

TEST_F(AboutTestSuite, About_v1_02)
{
	m_AboutProxy = m_ServiceHelper->connectAboutProxy(*m_DeviceAboutAnnouncement);

	ASSERT_NE(m_AboutProxy, nullptr) << "AboutProxy connection failed";
	SUCCEED() << "AboutProxy connected";

	LOG(INFO) << "Verifying that the Version property retrieved from the application's "
		<< "About bus object matches the version parameter in its About announcement";

	uint16_t aboutProxyVersion;
	m_AboutProxy->GetVersion(aboutProxyVersion);

	ASSERT_EQ(aboutProxyVersion, m_DeviceAboutAnnouncement->getVersion())
		<< "About version does not match: " << m_DeviceAboutAnnouncement->getVersion()
		<< " is not equal to " << aboutProxyVersion;

	SUCCEED() << "About version parameter matches Version property";
}

TEST_F(AboutTestSuite, About_v1_03)
{
	m_AboutProxy = m_ServiceHelper->connectAboutProxy(*m_DeviceAboutAnnouncement);

	ASSERT_NE(m_AboutProxy, nullptr) << "AboutProxy connection failed";
	SUCCEED() << "AboutProxy connected";

	LOG(INFO) << "Retrieving paths and interfaces from objectDescription parameter";
	map<string, const char**> aboutObjectDescriptionMap;
	populateMap(m_DeviceAboutAnnouncement->getObjectDescriptions(), aboutObjectDescriptionMap);

	LOG(INFO) << "Calling GetObjectDescription() method";
	map<string, const char**> announcementObjectDescriptionMap;
	MsgArg announcement_object_desciption_msg_arg;
	QStatus status = m_AboutProxy->GetObjectDescription(announcement_object_desciption_msg_arg);
	ASSERT_EQ(status, ER_OK) << "Error calling GetObjectDescription() on AboutProxy";
	populateMap(announcement_object_desciption_msg_arg, announcementObjectDescriptionMap);

	if (compareMapKeys(aboutObjectDescriptionMap, announcementObjectDescriptionMap))
	{
		SUCCEED() << "GetObjectDescription() does contain the same set of paths as the announcement";
	}
	else
	{
		FAIL() << "GetObjectDescription() does not contain the same set of paths as the announcement";
	}

	for (auto mapEntry : aboutObjectDescriptionMap)
	{
		bool interfacesAreEqual = true;

		for (size_t i = 0; i < sizeof(mapEntry.second) / sizeof(*mapEntry.second); ++i)
		{
			if (string(mapEntry.second[i]) != string(announcementObjectDescriptionMap.at(mapEntry.first)[i]))
			{
				interfacesAreEqual = false;
				break;
			}
		}

		if (interfacesAreEqual)
		{
			SUCCEED() << "GetObjectDescription() is consistent for " << mapEntry.first;
		}
		else
		{
			FAIL() << "GetObjectDescription() is not consistent for " << mapEntry.first;
		}
	}
}

void AboutTestSuite::populateMap(AboutObjectDescription* t_AboutObjectDescription, map<string, const char**>& t_ObjectDescriptionMap)
{
	size_t numberOfPaths = t_AboutObjectDescription->GetPaths(NULL, 0);
	const char** paths = new const char*[numberOfPaths];

	t_AboutObjectDescription->GetPaths(paths, numberOfPaths);

	for (size_t i = 0; i < numberOfPaths; ++i)
	{
		size_t numberOfInterfaces = t_AboutObjectDescription->GetInterfaces(paths[i], NULL, 0);
		const char** interfaces = new const char*[numberOfInterfaces];

		t_AboutObjectDescription->GetInterfaces(paths[i], interfaces, numberOfInterfaces);
		t_ObjectDescriptionMap.insert(pair<string, const char**>(string(paths[i]), interfaces));
	}
}

void AboutTestSuite::populateMap(MsgArg& t_MsgArg, map<string, const char**>& t_ObjectDescriptionMap)
{
	AboutObjectDescription* aboutObjectDescription = new AboutObjectDescription();
	aboutObjectDescription->CreateFromMsgArg(t_MsgArg);

	populateMap(aboutObjectDescription, t_ObjectDescriptionMap);
}

TEST_F(AboutTestSuite, About_v1_04)
{
	m_AboutProxy = m_ServiceHelper->connectAboutProxy(*m_DeviceAboutAnnouncement);

	ASSERT_NE(m_AboutProxy, nullptr) << "AboutProxy connection failed";
	SUCCEED() << "AboutProxy connected";

	XMLBasedBusIntrospector busIntrospector = m_ServiceHelper->getBusIntrospector(*m_DeviceAboutAnnouncement);

	set<string> announcementPathInterfaceSet;
	set<string> busIntrospectPathInterfaceSet;

	AboutObjectDescription* aboutObjectDescriptions = m_DeviceAboutAnnouncement->getObjectDescriptions();

	size_t numberOfPaths = aboutObjectDescriptions->GetPaths(NULL, 0);
	const char** paths = new const char*[numberOfPaths];

	aboutObjectDescriptions->GetPaths(paths, numberOfPaths);

	for (size_t i = 0; i < numberOfPaths; ++i)
	{
		populateAnnouncementPathInterfaceSet(announcementPathInterfaceSet, aboutObjectDescriptions, paths[i]);
		
		try
		{
			populateBusIntrospectPathInterfaceSet(busIntrospector, busIntrospectPathInterfaceSet, paths[i]);
		}
		catch (const xercesc::SAXParseException&)
		{
			FAIL() << "Introspecting XML path '" << paths[i] << "' failed";
		}
	}

	for (auto announcementKey : announcementPathInterfaceSet)
	{
		vector<string> pathAndInterfaces = split<string>(announcementKey, ":");

		if (pathAndInterfaces[0] != org::allseen::Introspectable::InterfaceName)
		{
			if (busIntrospectPathInterfaceSet.find(announcementKey) == busIntrospectPathInterfaceSet.end())
			{
				FAIL() << "AboutAnnouncement advertises interface " << pathAndInterfaces[1]
					<< " at path " << pathAndInterfaces[0] << ", but does not contain such interface at that path";
			}
			else
			{
				SUCCEED() << "Interface " << pathAndInterfaces[1] << " found at path " << pathAndInterfaces[0];
			}
		}
	}
}

void AboutTestSuite::populateAnnouncementPathInterfaceSet(set<string>& t_AnnouncementPathInterfaceSet,
	AboutObjectDescription* t_AboutObjectDescription, const string& t_Path)
{
	size_t numberOfInterfaces = t_AboutObjectDescription->GetInterfaces(t_Path.c_str(), NULL, 0);
	const char** interfaces = new const char*[numberOfInterfaces];

	t_AboutObjectDescription->GetInterfaces(t_Path.c_str(), interfaces, numberOfInterfaces);

	for (size_t j = 0; j < numberOfInterfaces; ++j)
	{
		string strToAppend = t_Path;
		strToAppend.append(":");
		strToAppend.append(interfaces[j]);
		t_AnnouncementPathInterfaceSet.insert(strToAppend);
	}

	delete[] interfaces;
}

void AboutTestSuite::populateBusIntrospectPathInterfaceSet(XMLBasedBusIntrospector& t_BusIntrospector,
	set<string>& t_BusIntrospectPathInterfaceSet, const string& t_Path)
{
	NodeDetail* nodeDetail = t_BusIntrospector.introspect(t_Path);
	IntrospectionNode introspectionNode = nodeDetail->getIntrospectionNode();

	for (auto introspectionInterface : *introspectionNode.getInterfaces())
	{
		string interfaceName = introspectionInterface.getName();
		string key = t_Path;
		key.append(":");
		key.append(interfaceName);
		LOG(INFO) << "Bus Introspection contains interface " << interfaceName
			<< " at path " << t_Path;
		t_BusIntrospectPathInterfaceSet.insert(key);
	}
}

TEST_F(AboutTestSuite, About_v1_05)
{
	m_AboutProxy = m_ServiceHelper->connectAboutProxy(*m_DeviceAboutAnnouncement);

	ASSERT_NE(m_AboutProxy, nullptr) << "AboutProxy connection failed";
	SUCCEED() << "AboutProxy connected";

	list<InterfaceDetail> standardizedIntrospectionInterfacesExposedOnBus;
	try
	{
		standardizedIntrospectionInterfacesExposedOnBus =
			m_ServiceHelper->getBusIntrospector(*m_DeviceAboutAnnouncement).getStandardizedInterfacesExposedOnBus();
	}
	catch (const xercesc::SAXParseException&)
	{
		FAIL() << "Retrieving standardized introspection interfaces exposed on bus failed";
	}

	for (auto objectDetail : standardizedIntrospectionInterfacesExposedOnBus)
	{
		for (auto interfaceName : objectDetail.getIntrospectionInterfaces())
		{
			LOG(INFO) << "Found object at " << objectDetail.getPath() << " implementing "
				<< interfaceName.getName();
		}
	}

	ValidationResult validationResult = (new InterfaceValidator())->validate(standardizedIntrospectionInterfacesExposedOnBus);

	if (!validationResult.isValid())
	{
		FAIL() << validationResult.getFailureReason();
	}
	else
	{
		SUCCEED() << "Standardized introspection interfaces exposed on bus are valid";
	}
}

TEST_F(AboutTestSuite, About_v1_06)
{
	m_AboutProxy = m_ServiceHelper->connectAboutProxy(*m_DeviceAboutAnnouncement);

	ASSERT_NE(m_AboutProxy, nullptr) << "AboutProxy connection failed";
	SUCCEED() << "AboutProxy connected";

	LOG(INFO) << "Calling GetAboutData on About interface with default language: " << m_DefaultLanguage;

	QStatus status = ER_OK;
	MsgArg msgArg;
	ASSERT_EQ(ER_OK, status = m_AboutProxy->GetAboutData(m_DefaultLanguage.c_str(), msgArg))
		<< "Calling GetAboutData method returned status code: " << QCC_StatusText(status);

	AboutData aboutData(msgArg, m_DefaultLanguage.c_str());

	verifyAboutData(aboutData, m_DefaultLanguage);
}

void AboutTestSuite::verifyAboutData(AboutData t_AboutData, string t_Language)
{
	LOG(INFO) << "Checking that all required fields are present and match IXIT";
	checkForNull(t_AboutData, t_Language);

	LOG(INFO) << "Validating signatures";
	validateSignature(t_AboutData, t_Language);

	if (m_IcsMap.at("ICSCO_DateOfManufacture"))
	{
		validateDateOfManufacture(t_AboutData, t_Language);
	}

	if (m_IcsMap.at("ICSCO_SupportUrl"))
	{
		validateSupportUrl(t_AboutData, t_Language);
	}
}

void AboutTestSuite::checkForNull(AboutData t_AboutData, string t_Language)
{
	checkForNull(t_AboutData, AboutData::APP_ID, t_Language);
	checkForNull(t_AboutData, AboutData::DEFAULT_LANGUAGE, t_Language);
	checkForNull(t_AboutData, AboutData::DEVICE_ID, t_Language);
	checkForNull(t_AboutData, AboutData::APP_NAME, t_Language);
	checkForNull(t_AboutData, AboutData::MANUFACTURER, t_Language);
	checkForNull(t_AboutData, AboutData::MODEL_NUMBER, t_Language);
	checkForNull(t_AboutData, AboutData::SUPPORTED_LANGUAGES, t_Language);
	checkForNull(t_AboutData, AboutData::DESCRIPTION, t_Language);
	checkForNull(t_AboutData, AboutData::SOFTWARE_VERSION, t_Language);
	checkForNull(t_AboutData, AboutData::AJ_SOFTWARE_VERSION, t_Language);

	if (m_IcsMap.at("ICSCO_DateOfManufacture"))
	{
		checkForNull(t_AboutData, AboutData::DATE_OF_MANUFACTURE, t_Language);
	}

	if (m_IcsMap.at("ICSCO_HardwareVersion"))
	{
		checkForNull(t_AboutData, AboutData::HARDWARE_VERSION, t_Language);
	}

	if (m_IcsMap.at("ICSCO_SupportUrl"))
	{
		checkForNull(t_AboutData, AboutData::SUPPORT_URL, t_Language);
	}
}

void AboutTestSuite::checkForNull(AboutData t_AboutData, string t_FieldName, string t_Language)
{
	QStatus status = ER_OK;
	MsgArg* value;
	ASSERT_EQ(ER_OK, status = t_AboutData.GetField(t_FieldName.c_str(), value))
		<< "Retrieving " << t_FieldName << " field from AboutData returned status code: " << QCC_StatusText(status);

	if ((value == nullptr) /*|| (value->typeId == ajn::ALLJOYN_INVALID)*/) // [JTF] TBF for next mayor release
	{
		FAIL() << t_FieldName << " is a required field for language " << t_Language;
	}
	else
	{
		LOG(INFO) << t_FieldName << " is present for language " << t_Language;

		if (t_Language.compare(m_DefaultLanguage) == 0)
		{
			if (t_FieldName.compare(AboutData::SUPPORTED_LANGUAGES) == 0)
			{
				// add supported languages checking
			}
			else
			{
				LOG(INFO) << "Checking if received " << t_FieldName << " is equal to IXITCO_" << t_FieldName;

				if (t_FieldName.compare(AboutData::APP_ID) == 0)
				{
					size_t appIdFieldSize;
					uint8_t* appIdField;
					t_AboutData.GetAppId(&appIdField, &appIdFieldSize);

					compareAbout(AboutData::APP_ID, m_IxitMap.at("IXITCO_AppId").c_str(),
						ArrayParser::parseAppId(appIdField).c_str(), "");
				}
				else
				{
					/*MsgArg* msgArg;
					t_AboutData.GetField(t_FieldName.c_str(), msgArg);*/
					const char* fieldValue;
					ASSERT_EQ(ER_OK, status = value->Get("s", &fieldValue))
						<< "Retrieving " << t_FieldName << " field from AboutData returned status code: " << QCC_StatusText(status);

					compareAbout(t_FieldName.c_str(), m_IxitMap.at(string("IXITCO_").append(t_FieldName)).c_str(),
						fieldValue, "");
				}
			}
		}
	}
}

void AboutTestSuite::validateSignature(AboutData t_AboutData, string t_Language)
{
	validateSignature(t_AboutData, AboutData::APP_ID, "ay", t_Language);
	validateSignature(t_AboutData, AboutData::DEFAULT_LANGUAGE, "s", t_Language);
	validateSignature(t_AboutData, AboutData::DEVICE_ID, "s", t_Language);
	validateSignature(t_AboutData, AboutData::APP_NAME, "s", t_Language);
	validateSignature(t_AboutData, AboutData::MANUFACTURER, "s", t_Language);
	validateSignature(t_AboutData, AboutData::MODEL_NUMBER, "s", t_Language);
	validateSignature(t_AboutData, AboutData::SUPPORTED_LANGUAGES, "as", t_Language);
	validateSignature(t_AboutData, AboutData::DESCRIPTION, "s", t_Language);
	validateSignature(t_AboutData, AboutData::SOFTWARE_VERSION, "s", t_Language);
	validateSignature(t_AboutData, AboutData::AJ_SOFTWARE_VERSION, "s", t_Language);
	validateSignatureForNonRequiredFields(t_AboutData, t_Language);
}

void AboutTestSuite::validateSignature(AboutData t_AboutData, string t_FieldName, const char* t_Signature, string t_Language)
{
	QStatus status = ER_OK;
	MsgArg* value;
	ASSERT_EQ(ER_OK, status = t_AboutData.GetField(t_FieldName.c_str(), value, t_Language.c_str()))
		<< "Retrieving " << t_FieldName << " from AboutData returned status code: " << QCC_StatusText(status);

	ASSERT_TRUE(value->typeId != ajn::ALLJOYN_INVALID) << t_FieldName << " is a required field for language " << t_Language;
	const qcc::String valueSignature = value->Signature();
	ASSERT_STREQ(t_Signature, valueSignature.c_str()) << "Signature (" << valueSignature.c_str() << ") does not match (" << t_Signature << ") for required field : "
		<< t_FieldName << " for language " << t_Language;

	SUCCEED() << "Signature (" << valueSignature.c_str() << ") matches (" << t_Signature << ") for required field "
		<< t_FieldName << " for language " << t_Language;
}

void AboutTestSuite::validateSignatureForNonRequiredFields(AboutData t_AboutData, string t_Language)
{
	if (m_IcsMap.at("ICSCO_DeviceName"))
	{
		validateSignature(t_AboutData, AboutData::DEVICE_NAME, "s", t_Language);
	}

	if (m_IcsMap.at("ICSCO_DateOfManufacture"))
	{
		validateSignature(t_AboutData, AboutData::DATE_OF_MANUFACTURE, "s", t_Language);
	}

	if (m_IcsMap.at("ICSCO_HardwareVersion"))
	{
		validateSignature(t_AboutData, AboutData::HARDWARE_VERSION, "s", t_Language);
	}

	if (m_IcsMap.at("ICSCO_SupportUrl"))
	{
		validateSignature(t_AboutData, AboutData::SUPPORT_URL, "s", t_Language);
	}
}

void AboutTestSuite::validateDateOfManufacture(AboutData t_AboutData, string t_Language)
{
	MsgArg *value;
	char* dateOfManufacture;

	QStatus status = ER_OK;
	ASSERT_EQ(ER_OK, status = t_AboutData.GetField(AboutData::DATE_OF_MANUFACTURE, value))
		<< "Retrieving DateOfManufacture field from AboutData returned status code: " << QCC_StatusText(status);
	ASSERT_EQ(ER_OK, status = value->Get("s", &dateOfManufacture))
		<< "Retrieving DateOfManufacture field from AboutData returned status code: " << QCC_StatusText(status);

	LOG(INFO) << "Validating date of manufacture: " << dateOfManufacture;

	tm t = {};
	istringstream stringStream(dateOfManufacture);
	stringStream >> get_time(&t, DATE_FORMAT);
	ASSERT_FALSE(stringStream.fail()) << "DateOfManufacture field value "
		<< dateOfManufacture << " does not match expected date pattern YYYY-MM-DD";

	SUCCEED() << "DateOfManufacture field matches expected date pattern YYYY-MM-DD";
}

void AboutTestSuite::validateSupportUrl(AboutData t_AboutData, string t_Language)
{
	MsgArg *value;
	char* supportUrl;

	QStatus status = ER_OK;
	ASSERT_EQ(ER_OK, status = t_AboutData.GetField(AboutData::SUPPORT_URL, value))
		<< "Retrieving SupportUrl field from AboutData returned status code: " << QCC_StatusText(status);
	ASSERT_EQ(ER_OK, status = value->Get("s", &supportUrl))
		<< "Retrieving SupportUrl field from AboutData returned status code: " << QCC_StatusText(status);

	validateUrl(supportUrl);
}

void AboutTestSuite::validateUrl(const string& t_Url)
{
	regex urlRegex(URL_REGEX, regex::extended);
	smatch urlMatchResult;

	ASSERT_TRUE(regex_match(t_Url, urlMatchResult, urlRegex))
		<< "Malformed url.";
	SUCCEED() << "Url format is correct";
}

TEST_F(AboutTestSuite, About_v1_07)
{
	m_AboutProxy = m_ServiceHelper->connectAboutProxy(*m_DeviceAboutAnnouncement);

	ASSERT_NE(m_AboutProxy, nullptr) << "AboutProxy connection failed";
	SUCCEED() << "AboutProxy connected";

	LOG(INFO) << "Calling GetAboutData on About interface with default language " << m_DefaultLanguage;

	MsgArg aboutDataDefaultLanguageMsgArg;
	m_AboutProxy->GetAboutData(m_DefaultLanguage.c_str(), aboutDataDefaultLanguageMsgArg);

	AboutData aboutDataDefaultLanguage(aboutDataDefaultLanguageMsgArg, m_DefaultLanguage.c_str());

	MsgArg* supportedLanguagesMsgArg;
	aboutDataDefaultLanguage.GetField(AboutData::SUPPORTED_LANGUAGES, supportedLanguagesMsgArg);

	size_t supportedLanguagesArraySize;
	MsgArg* supportedLanguagesArray;
	supportedLanguagesMsgArg->Get("as", &supportedLanguagesArraySize, &supportedLanguagesArray);

	validateSupportedLanguagesContainsDefaultLanguage(supportedLanguagesArray, supportedLanguagesArraySize);

	if (supportedLanguagesArraySize == 1)
	{
		LOG(INFO) << "Device only supports one language";
	}
	else
	{
		validateSupportedLanguagesAboutMap(aboutDataDefaultLanguage, supportedLanguagesArray, supportedLanguagesArraySize);
	}
}

void AboutTestSuite::validateSupportedLanguagesContainsDefaultLanguage(MsgArg* t_SupportedLanguagesArray,
	size_t t_SupportedLanguagesArraySize)
{
	ASSERT_NE(t_SupportedLanguagesArray, nullptr) << "No supported language found in About announcement";
	ASSERT_GT(t_SupportedLanguagesArraySize, 0) << "No supported language found in About Announcement";
	ASSERT_TRUE(isDefaultLanguagePresent(t_SupportedLanguagesArray, t_SupportedLanguagesArraySize))
		<< "Default language not found in supported languages list of About announcement";
}

bool AboutTestSuite::isDefaultLanguagePresent(MsgArg* t_SupportedLanguagesArray,
	size_t t_SupportedLanguagesArraySize)
{
	bool isPresent = false;

	for (size_t i = 0; i < t_SupportedLanguagesArraySize; ++i)
	{
		const char* currentLanguage;
		t_SupportedLanguagesArray[i].Get("s", &currentLanguage);

		if (m_DefaultLanguage.compare(currentLanguage) == 0)
		{
			isPresent = true;
			break;
		}
	}

	return isPresent;
}

void AboutTestSuite::validateSupportedLanguagesAboutMap(AboutData t_AboutDataDefaultLanguage,
	MsgArg* t_SupportedLanguagesArray, size_t t_SupportedLanguagesArraySize)
{
	for (size_t i = 0; i < t_SupportedLanguagesArraySize; ++i)
	{
		const char* currentLanguage;
		t_SupportedLanguagesArray[i].Get("s", &currentLanguage);

		if (m_DefaultLanguage.compare(currentLanguage) != 0)
		{
			LOG(INFO) << "Calling getAbout on About interface with language " << currentLanguage;

			MsgArg aboutDataCurrentLanguageMsgArg;
			m_AboutProxy->GetAboutData(currentLanguage, aboutDataCurrentLanguageMsgArg);
			AboutData aboutDataCurrentLanguage(aboutDataCurrentLanguageMsgArg, currentLanguage);

			verifyAboutData(aboutDataCurrentLanguage, currentLanguage);
			compareNonLocalizedFieldsInAboutMap(t_AboutDataDefaultLanguage,
				aboutDataCurrentLanguage, currentLanguage);
		}
	}
}

void AboutTestSuite::compareNonLocalizedFieldsInAboutMap(AboutData t_AboutDataDefaultLanguage,
	AboutData t_AboutDataSupportedLanguage, string t_SupportedLanguage)
{
	compareRequiredFieldsInAboutMap(t_AboutDataDefaultLanguage, t_AboutDataSupportedLanguage,
		t_SupportedLanguage);
	compareNonRequiredFieldsInAboutMap(t_AboutDataDefaultLanguage, t_AboutDataSupportedLanguage,
		t_SupportedLanguage);
}

void AboutTestSuite::compareRequiredFieldsInAboutMap(AboutData t_AboutDataDefaultLanguage,
	AboutData t_AboutDataSupportedLanguage, string t_SupportedLanguage)
{
	size_t defaultAppIdSize;
	uint8_t* defaultAppId;
	t_AboutDataDefaultLanguage.GetAppId(&defaultAppId, &defaultAppIdSize);

	uint8_t* currentAppId;
	size_t currentAppIdSize;
	t_AboutDataSupportedLanguage.GetAppId(&currentAppId, &currentAppIdSize);

	compareAbout(AboutData::APP_ID, ArrayParser::parseAppId(defaultAppId).c_str(),
		ArrayParser::parseAppId(currentAppId).c_str(), t_SupportedLanguage);

	char* defaultDefaultLanguage;
	t_AboutDataDefaultLanguage.GetDefaultLanguage(&defaultDefaultLanguage);

	char* currentDefaultLanguage;
	t_AboutDataDefaultLanguage.GetDefaultLanguage(&currentDefaultLanguage);

	compareAbout(AboutData::DEFAULT_LANGUAGE, defaultDefaultLanguage, currentDefaultLanguage, t_SupportedLanguage);

	char* defaultDeviceId;
	t_AboutDataDefaultLanguage.GetDeviceId(&defaultDeviceId);

	char* currentDeviceId;
	t_AboutDataSupportedLanguage.GetDeviceId(&currentDeviceId);

	compareAbout(AboutData::DEVICE_ID, defaultDeviceId, currentDeviceId, t_SupportedLanguage);

	char* defaultModelNumber;
	t_AboutDataDefaultLanguage.GetModelNumber(&defaultModelNumber);

	char* currentModelNumber;
	t_AboutDataSupportedLanguage.GetModelNumber(&currentModelNumber);

	compareAbout(AboutData::MODEL_NUMBER, defaultModelNumber, currentModelNumber, t_SupportedLanguage);


	size_t defaultSupportedLanguagesSize = t_AboutDataDefaultLanguage.GetSupportedLanguages(NULL, 0);
	const char** defaultSupportedLanguages = new const char*[defaultSupportedLanguagesSize];
	t_AboutDataDefaultLanguage.GetSupportedLanguages(defaultSupportedLanguages, defaultSupportedLanguagesSize);

	size_t currentSupportedLanguagesSize = t_AboutDataSupportedLanguage.GetSupportedLanguages(NULL, 0);
	const char** currentSupportedLanguages = new const char*[currentSupportedLanguagesSize];
	t_AboutDataSupportedLanguage.GetSupportedLanguages(currentSupportedLanguages, currentSupportedLanguagesSize);

	compareAbout(AboutData::SUPPORTED_LANGUAGES, defaultSupportedLanguages,
		defaultSupportedLanguagesSize, currentSupportedLanguages,
		currentSupportedLanguagesSize, t_SupportedLanguage);

	char* defaultSoftwareVersion;
	t_AboutDataDefaultLanguage.GetSoftwareVersion(&defaultSoftwareVersion);

	char* currentSoftwareVersion;
	t_AboutDataSupportedLanguage.GetSoftwareVersion(&currentSoftwareVersion);

	compareAbout(AboutData::SOFTWARE_VERSION, defaultSoftwareVersion, currentSoftwareVersion, t_SupportedLanguage);

	char* defaultAjSoftwareVersion;
	t_AboutDataDefaultLanguage.GetAJSoftwareVersion(&defaultAjSoftwareVersion);

	char* currentAjSoftwareVersion;
	t_AboutDataSupportedLanguage.GetAJSoftwareVersion(&currentAjSoftwareVersion);

	compareAbout(AboutData::AJ_SOFTWARE_VERSION, defaultAjSoftwareVersion, currentAjSoftwareVersion, t_SupportedLanguage);
}

void AboutTestSuite::compareAbout(string t_FieldName, const char** t_DefaultSupportedLanguages,
	size_t t_DefaultSupportedLanguagesSize, const char** t_CurrentSupportedLanguages,
	size_t t_CurrentSupportedLanguagesSize, string t_Language)
{
	if (t_Language.empty())
	{
		t_Language = "unspecified language";
	}

	ASSERT_EQ(t_DefaultSupportedLanguagesSize, t_CurrentSupportedLanguagesSize)
		<< t_FieldName << " array size for default language: " << t_DefaultSupportedLanguagesSize
		<< " does not match array size for language '" << t_Language << "': " << t_CurrentSupportedLanguagesSize;

	bool arraysAreEqual = true;
	size_t i = 0;

	do
	{
		arraysAreEqual = string(t_DefaultSupportedLanguages[i]).compare(t_CurrentSupportedLanguages[i]) == 0;
		++i;
	} while (arraysAreEqual && (i < t_DefaultSupportedLanguagesSize));

	ASSERT_TRUE(arraysAreEqual) << t_FieldName
		<< " array for default language does not match array for language '"
		<< t_Language << "'";

	SUCCEED() << t_FieldName
		<< " array for default language matches array for language '"
		<< t_Language << "'";
}

void AboutTestSuite::compareNonRequiredFieldsInAboutMap(AboutData t_AboutDataDefaultLanguage,
	AboutData t_AboutDataSupportedLanguage, string t_SupportedLanguage)
{
	if (m_IcsMap.at("ICSCO_DateOfManufacture"))
	{
		compareAboutNonRequired(t_AboutDataDefaultLanguage, t_AboutDataSupportedLanguage,
			t_SupportedLanguage, AboutData::DATE_OF_MANUFACTURE);
	}
	
	if (m_IcsMap.at("ICSCO_HardwareVersion"))
	{
		compareAboutNonRequired(t_AboutDataDefaultLanguage, t_AboutDataSupportedLanguage,
			t_SupportedLanguage, AboutData::HARDWARE_VERSION);
	}
	
	if (m_IcsMap.at("ICSCO_SupportUrl"))
	{
		compareAboutNonRequired(t_AboutDataDefaultLanguage, t_AboutDataSupportedLanguage,
			t_SupportedLanguage, AboutData::SUPPORT_URL);
	}
}

void AboutTestSuite::compareAboutNonRequired(AboutData t_AboutDataDefaultLanguage,
	AboutData t_AboutDataSupportedLanguage, string t_SupportedLanguage, string t_FieldName)
{
	LOG(INFO) << "Comparing " << t_FieldName << " of default language and language " << t_SupportedLanguage;

	MsgArg* defaultFieldValueMsgArg;
	t_AboutDataDefaultLanguage.GetField(t_FieldName.c_str(), defaultFieldValueMsgArg);

	if (defaultFieldValueMsgArg != nullptr)
	{
		MsgArg* currentFieldValueMsgArg;
		t_AboutDataSupportedLanguage.GetField(t_FieldName.c_str(), currentFieldValueMsgArg);

		if ((currentFieldValueMsgArg != nullptr) && (currentFieldValueMsgArg->typeId != ALLJOYN_INVALID))
		{
			const char* defaultFieldValue;
			defaultFieldValueMsgArg->Get("s", &defaultFieldValue);
			const char* currentFieldValue;
			currentFieldValueMsgArg->Get("s", &currentFieldValue);

			compareAbout(t_FieldName.c_str(), defaultFieldValue, currentFieldValue, t_SupportedLanguage);
		}
		else
		{
			FAIL() << prepareAssertionFailureResponse(t_FieldName, t_SupportedLanguage);
		}
	}
	else
	{
		MsgArg* currentFieldValueMsgArg;
		t_AboutDataSupportedLanguage.GetField(t_FieldName.c_str(), currentFieldValueMsgArg);

		ASSERT_FALSE(currentFieldValueMsgArg != nullptr) <<
			prepareAssertionFailureResponse(t_FieldName, t_SupportedLanguage);
	}
}

string AboutTestSuite::prepareAssertionFailureResponse(string t_FieldName, string t_Language)
{
	string message(t_FieldName);
	message.append(" value returned from GetAbout for language: '");
	message.append(t_Language);
	message.append("' does not match default language value");

	return message;
}

TEST_F(AboutTestSuite, About_v1_08)
{
	m_AboutProxy = m_ServiceHelper->connectAboutProxy(*m_DeviceAboutAnnouncement);

	ASSERT_NE(m_AboutProxy, nullptr) << "AboutProxy connection failed";
	SUCCEED() << "AboutProxy connected";

	LOG(INFO) << "Calling getAboutData on About Interface";

	MsgArg aboutDataDefaultLanguageMsgArg;
	ASSERT_EQ(m_AboutProxy->GetAboutData(m_DefaultLanguage.c_str(), aboutDataDefaultLanguageMsgArg),
		ER_OK);

	AboutData aboutDataDefaultLanguage(aboutDataDefaultLanguageMsgArg, m_DefaultLanguage.c_str());

	MsgArg aboutDataNoLanguageMsgArg;
	ASSERT_EQ(m_AboutProxy->GetAboutData("", aboutDataNoLanguageMsgArg), ER_OK);

	AboutData aboutDataNoLanguage(aboutDataNoLanguageMsgArg, NULL);

	compareFieldsInAboutData(aboutDataDefaultLanguage, aboutDataNoLanguage, "");
}

void AboutTestSuite::compareFieldsInAboutData(AboutData t_AboutDataDefaultLanguage,
	AboutData t_AboutDataSuportedLanguage, string t_Language)
{
	if (m_IcsMap.at("ICSCO_DeviceName"))
	{
		compareAboutNonRequired(t_AboutDataDefaultLanguage, t_AboutDataSuportedLanguage,
			t_Language, AboutData::DEVICE_NAME);
	}
	
	char* default_app_name;
	t_AboutDataDefaultLanguage.GetAppName(&default_app_name);

	char* current_app_name;
	t_AboutDataSuportedLanguage.GetAppName(&current_app_name);

	compareAbout(AboutData::APP_NAME, default_app_name, current_app_name, t_Language);

	char* default_manufacturer;
	t_AboutDataDefaultLanguage.GetManufacturer(&default_manufacturer);

	char* current_manufacturer;
	t_AboutDataSuportedLanguage.GetManufacturer(&current_manufacturer);

	compareAbout(AboutData::MANUFACTURER, default_manufacturer, current_manufacturer, t_Language);

	char* default_description;
	t_AboutDataDefaultLanguage.GetDescription(&default_description);

	char* current_description;
	t_AboutDataSuportedLanguage.GetDescription(&current_description);

	compareAbout(AboutData::DESCRIPTION, default_description, current_description, t_Language);

	compareNonLocalizedFieldsInAboutMap(t_AboutDataDefaultLanguage, t_AboutDataSuportedLanguage, t_Language);
}

TEST_F(AboutTestSuite, About_v1_09)
{
	m_AboutProxy = m_ServiceHelper->connectAboutProxy(*m_DeviceAboutAnnouncement);

	LOG(INFO) << "Calling GetAboutData() on About interface with an INVALID language";
	MsgArg aboutDataMsgArg;
	QStatus status = m_AboutProxy->GetAboutData("INVALID", aboutDataMsgArg);
	ASSERT_NE(ER_OK, status) << "Calling GetAboutData() must return an error status code";
	ASSERT_STREQ("ER_LANGUAGE_NOT_SUPPORTED", QCC_StatusText(status)) << "Calling GetAboutData() returned a wrong error status code: " << QCC_StatusText(status); // [JTF] This comparison is done as STREQ because ASSERT_EQ(ER_LANGUAGE_NOT_SUPPORTED, status) fails
}

TEST_F(AboutTestSuite, About_v1_10)
{
	ASSERT_TRUE(m_IcsMap.at("ICSCO_IconInterface"))
		<< "Test case not applicable. ICSCO_IconInterface is set to false";

	ASSERT_TRUE(m_DeviceAboutAnnouncement->supportsInterface((char*)org::alljoyn::Icon::InterfaceName))
		<< "Device does not support AboutIcon";

	m_AboutIconProxy = m_ServiceHelper->connectAboutIconProxy(*m_DeviceAboutAnnouncement);

	LOG(INFO) << "Calling GetIcon to retrieve icon data";
	AboutIcon aboutIcon;
	QStatus status = m_AboutIconProxy->GetIcon(aboutIcon);
	ASSERT_EQ(ER_OK, status) << "Calling GetIcon() method returned status code: " << QCC_StatusText(status);

	EXPECT_GT(aboutIcon.contentSize, 0) << "Icon size is zero";
	ASSERT_FALSE(aboutIcon.mimetype.empty()) << "Mimetype is empty";
	EXPECT_TRUE(aboutIcon.mimetype.compare(0, 6, "image/") == 0)
		<< "Mime type should match pattern image/*";
	EXPECT_LT(aboutIcon.contentSize, ALLJOYN_MAX_ARRAY_LEN)
		<< "Icon size should be less than " << ALLJOYN_MAX_ARRAY_LEN;
	
	// IMAGE PARSER
}

TEST_F(AboutTestSuite, About_v1_11)
{
	ASSERT_TRUE(m_IcsMap.at("ICSCO_IconInterface"))
		<< "Test case not applicable. ICSCO_IconInterface is set to false";

	ASSERT_TRUE(m_DeviceAboutAnnouncement->supportsInterface((char*)org::alljoyn::Icon::InterfaceName))
		<< "Device does not support AboutIcon";

	m_AboutIconProxy = m_ServiceHelper->connectAboutIconProxy(*m_DeviceAboutAnnouncement);
	AboutIcon aboutIcon;
	QStatus status = m_AboutIconProxy->GetIcon(aboutIcon);
	ASSERT_EQ(ER_OK, status) << "Calling GetIcon() method returned status code: " << QCC_StatusText(status);

	//ASSERT_FALSE(aboutIcon.url.empty()) << "Url is empty";
	if (aboutIcon.url.empty())
	{
		LOG(INFO) << "NOTE ADDED: Url is empty";
	}
	else
	{
		validateUrl(aboutIcon.url.c_str());
	}
}
