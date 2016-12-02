/******************************************************************************
* * 
*    Copyright (c) 2016 Open Connectivity Foundation and AllJoyn Open
*    Source Project Contributors and others.
*    
*    All rights reserved. This program and the accompanying materials are
*    made available under the terms of the Apache License, Version 2.0
*    which accompanies this distribution, and is available at
*    http://www.apache.org/licenses/LICENSE-2.0

******************************************************************************/
#include "stdafx.h"
#include "NotifConsumerTestSuite.h"

#include <stdlib.h>

#include "ArrayParser.h"
#include "UserInputDetails.h"

#include <alljoyn\AllJoynStd.h>
#include <alljoyn\notification\Notification.h>
#include <alljoyn\notification\NotificationText.h>

using namespace std;
using namespace ajn;
using namespace services;

const char* NotifConsumerTestSuite::BUS_APPLICATION_NAME = "NotifConsumerTestSuite";

NotifConsumerTestSuite::NotifConsumerTestSuite() : IOManager(ServiceFramework::NOTIFICATION)
{

}

void NotifConsumerTestSuite::SetUp()
{
	CLOG(INFO, "raw") << "====================================================";
	LOG(INFO) << "test setUp started";

	m_DutDeviceId = m_IxitMap.at("IXITCO_DeviceId");
	m_DutAppId = ArrayParser::parseAppIdFromString(m_IxitMap.at("IXITCO_AppId"));
	m_TtlInSeconds = ArrayParser::stringToUint16(m_IxitMap.at("IXITN_TTL").c_str()) * 60;

	m_ServiceHelper = new ServiceHelper();

	QStatus status = m_ServiceHelper->initializeSender(BUS_APPLICATION_NAME, m_DutDeviceId, m_DutAppId);
	ASSERT_EQ(status, ER_OK) << "serviceHelper Initialize() failed: " << QCC_StatusText(status);

	m_AboutDataStore = getAboutDataStore();
	ASSERT_TRUE(m_AboutDataStore->IsValid()) << "AboutData is not valid";

	m_NotificationSender = m_ServiceHelper->initNotificationSender(m_AboutDataStore);
	ASSERT_NE(m_NotificationSender, nullptr) << "NotificationSender initialization failed";

	SUCCEED();

	LOG(INFO) << "test setUp done";
	CLOG(INFO, "raw") << "====================================================";
}

AboutData* NotifConsumerTestSuite::getAboutDataStore()
{
	AboutData* aboutDataStore = new AboutData("en");

	EXPECT_EQ(aboutDataStore->SetAppId(m_IxitMap.at("IXITCO_AppId").c_str()), ER_OK);
	EXPECT_EQ(aboutDataStore->SetDeviceId(m_DutDeviceId.c_str()), ER_OK);
	
	EXPECT_EQ(aboutDataStore->SetSupportedLanguage("en"), ER_OK);
	EXPECT_EQ(aboutDataStore->SetDefaultLanguage("en"), ER_OK);
	EXPECT_EQ(aboutDataStore->SetAppName("NotificationConsumerTest", "en"), ER_OK);
	EXPECT_EQ(aboutDataStore->SetModelNumber("1.0.0mn"), ER_OK);
	EXPECT_EQ(aboutDataStore->SetSoftwareVersion("1.0.0sv"), ER_OK);
	EXPECT_EQ(aboutDataStore->SetDeviceName("NotificationConsumerTest", "en"), ER_OK);
	EXPECT_EQ(aboutDataStore->SetDescription("Description", "en"), ER_OK);
	EXPECT_EQ(aboutDataStore->SetManufacturer("at4", "en"), ER_OK);

	return aboutDataStore;
}

void NotifConsumerTestSuite::TearDown()
{
	CLOG(INFO, "raw") << "====================================================";
	LOG(INFO) << "test tearDown started";

	releaseResources();

	LOG(INFO) << "test tearDown done";
	CLOG(INFO, "raw") << "====================================================";

	storeResults();
}

void NotifConsumerTestSuite::releaseResources()
{
	if (m_ServiceHelper != nullptr)
	{
		m_ServiceHelper->release();

		delete m_ServiceHelper;
	}
}

TEST_F(NotifConsumerTestSuite, Notification_Consumer_v1_01)
{
	vector<string> messageVector = { "Test Msg 1", "Test Msg 2", "Test Msg 3" };
	string messageToSend = messageVector[getRandomNumber(messageVector.size() - 1)];
	vector<NotificationText> notificationTextList{ NotificationText("en", messageToSend.c_str()) };
	Notification notification(NotificationMessageType::INFO, notificationTextList);

	QStatus status = m_NotificationSender->send(notification, m_TtlInSeconds);
	ASSERT_EQ(ER_OK, status) << "Calling Send() method returned status code: " << QCC_StatusText(status);

	LOG(INFO) << "Notification message sent";

	checkUserInput(messageVector, messageToSend, 1);
}

// Assumes 0 <= max <= RAND_MAX
// Returns in the half-open interval [0, max]
int NotifConsumerTestSuite::getRandomNumber(const size_t t_Range)
{
	unsigned long
		// max <= RAND_MAX < ULONG_MAX, so this is okay.
		numBins = static_cast<unsigned long>(t_Range)+1,
		numRand = static_cast<unsigned long>(RAND_MAX) + 1,
		binSize = numRand / numBins,
		defect = numRand % numBins;

	long x;
	do {
		x = rand();
	}
	// This is carefully written not to overflow
	while (numRand - defect <= static_cast<unsigned long>(x));

	// Truncated division is intentional
	return x / binSize;
}

void NotifConsumerTestSuite::checkUserInput(const vector<string>& t_MessageVector,
	const string& t_MessageToSend, const int t_TestCase)
{
	LOG(INFO) << "Waiting for user response...";

	UserInputDetails userResponse = UserInputDetails(GetModuleHandle(NULL),
		t_TestCase, GetConsoleWindow());

	ASSERT_TRUE(userResponse.getOptionSelected() >= 0 && userResponse.getOptionSelected() < t_MessageVector.size())
	<< "A message option was not selected";

	LOG(INFO) << "Option sent: " << t_MessageToSend;
	LOG(INFO) << "Option selected: " << t_MessageVector[userResponse.getOptionSelected()];
	ASSERT_EQ(t_MessageToSend, t_MessageVector[userResponse.getOptionSelected()])
		<< "Incorrect message option selected";

	SUCCEED() << "Correct message option selected";
}

TEST_F(NotifConsumerTestSuite, Notification_Consumer_v1_02)
{
	vector<string> messageVector = { "Two languages Msg 1", "Two languages Msg 2", "Two languages Msg 3" };
	string messageToSend = messageVector[getRandomNumber(messageVector.size() - 1)];
	vector<NotificationText> notificationTextList;
	notificationTextList.push_back(NotificationText("en", messageToSend.c_str()));
	notificationTextList.push_back(NotificationText("fr", messageToSend.c_str()));

	Notification notification(NotificationMessageType::INFO, notificationTextList);

	QStatus status = m_NotificationSender->send(notification, m_TtlInSeconds);
	ASSERT_EQ(ER_OK, status) << "Calling Send() method returned status code: " << QCC_StatusText(status);

	LOG(INFO) << "Notification message sent";

	checkUserInput(messageVector, messageToSend, 2);
}

TEST_F(NotifConsumerTestSuite, Notification_Consumer_v1_04)
{
	vector<string> messageVector = { "Invalid langTag Msg 1", "Invalid langTag Msg 2", "Invalid langTag Msg 3" };
	string messageToSend = messageVector[getRandomNumber(messageVector.size() - 1)];
	vector<NotificationText> notificationTextList{ NotificationText("INVALID", messageToSend.c_str()) };
	Notification notification(NotificationMessageType::INFO, notificationTextList);

	QStatus status = m_NotificationSender->send(notification, m_TtlInSeconds);
	ASSERT_EQ(ER_OK, status) << "Calling Send() method returned status code: " << QCC_StatusText(status);

	LOG(INFO) << "Notification message sent";

	checkUserInput(messageVector, messageToSend, 4);
}

TEST_F(NotifConsumerTestSuite, Notification_Consumer_v1_05)
{
	vector<vector<MessageSet>> messageSets = {
		{
			MessageSet("Priority Msg 1", NotificationMessageType::EMERGENCY),
			MessageSet("Priority Msg 2", NotificationMessageType::WARNING),
			MessageSet("Priority Msg 3", NotificationMessageType::INFO)
		},
		{
			MessageSet("Priority Msg 1", NotificationMessageType::EMERGENCY),
			MessageSet("Priority Msg 2", NotificationMessageType::INFO),
			MessageSet("Priority Msg 3", NotificationMessageType::WARNING)
		},
		{
			MessageSet("Priority Msg 1", NotificationMessageType::WARNING),
			MessageSet("Priority Msg 2", NotificationMessageType::EMERGENCY),
			MessageSet("Priority Msg 3", NotificationMessageType::INFO)
		},
		{
			MessageSet("Priority Msg 1", NotificationMessageType::WARNING),
			MessageSet("Priority Msg 2", NotificationMessageType::INFO),
			MessageSet("Priority Msg 3", NotificationMessageType::EMERGENCY)
		},
		{
			MessageSet("Priority Msg 1", NotificationMessageType::INFO),
			MessageSet("Priority Msg 2", NotificationMessageType::EMERGENCY),
			MessageSet("Priority Msg 3", NotificationMessageType::WARNING)
		},
		{
			MessageSet("Priority Msg 1", NotificationMessageType::INFO),
			MessageSet("Priority Msg 2", NotificationMessageType::WARNING),
			MessageSet("Priority Msg 3", NotificationMessageType::EMERGENCY)
		},
	};

	vector<MessageSet> messagesToSend = messageSets[getRandomNumber(messageSets.size() - 1)];

	for (auto messageToSend : messagesToSend)
	{
		vector<NotificationText> notificationTextList = { NotificationText("en", messageToSend.getText().c_str()) };
		Notification notification(messageToSend.getPriority(), notificationTextList);

		QStatus status = m_NotificationSender->send(notification, m_TtlInSeconds);
		ASSERT_EQ(ER_OK, status) << "Calling Send() method returned status code: " << QCC_StatusText(status);

		LOG(INFO) << "Notification message sent";
	}

	vector<string> selectionOptions;

	for (auto messageSet : messageSets)
	{
		selectionOptions.push_back(buildPromptText(messageSet));
	}

	checkUserInput(selectionOptions, buildPromptText(messagesToSend), 5);
}

string NotifConsumerTestSuite::buildPromptText(const vector<MessageSet>& t_MessagesToSend)
{
	string messageSent;

	for (auto messageToSend : t_MessagesToSend)
	{
		if (!messageSent.empty())
		{
			messageSent.append("; ");
		}

		messageSent.append(messageToSend.getText());
		messageSent.append(" (");
		messageSent.append(messageToSend.getPriorityAsString());
		messageSent.append(")");
	}

	return messageSent;
}

TEST_F(NotifConsumerTestSuite, Notification_Consumer_v1_06)
{
	vector<string> messageVector = { "Msg w/ attributes 1", "Msg w/ attributes 2", "Msg w/ attributes 3" };
	string messageToSend = messageVector[getRandomNumber(messageVector.size() - 1)];
	vector<NotificationText> notificationTextList{ NotificationText("en", messageToSend.c_str()) };
	Notification notification(NotificationMessageType::INFO, notificationTextList);

	map<qcc::String, qcc::String> customAttributes = { { "org.alljoyn.validation.test", "value" } };
	notification.setCustomAttributes(customAttributes);

	QStatus status = m_NotificationSender->send(notification, m_TtlInSeconds);
	ASSERT_EQ(ER_OK, status) << "Calling Send() method returned status code: " << QCC_StatusText(status);

	LOG(INFO) << "Notification message sent";

	checkUserInput(messageVector, messageToSend, 6);
}