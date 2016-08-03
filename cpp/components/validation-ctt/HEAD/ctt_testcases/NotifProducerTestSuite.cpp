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
#include "NotifProducerTestSuite.h"

#include <stdlib.h>

#include "ArrayParser.h"
#include "UserInputDetails.h"

#include <alljoyn\AllJoynStd.h>
#include <alljoyn\notification\Notification.h>
#include <alljoyn\notification\NotificationText.h>


using namespace std;
using namespace ajn;
using namespace services;


const char* NotifProducerTestSuite::BUS_APPLICATION_NAME = "NotifProducerTestSuite";

NotifProducerTestSuite::NotifProducerTestSuite() : IOManager(ServiceFramework::NOTIFICATION)
{

}

void NotifProducerTestSuite::SetUp()
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

	m_NotificationValidator = new NotificationValidator(m_DutDeviceId, m_DutAppId,
		m_IcsMap.at("ICSN_RichIconUrl"), m_IcsMap.at("ICSN_RichAudioUrl"),
		m_IcsMap.at("ICSN_RespObjectPath"),
		ArrayParser::stringToUint16(m_IxitMap.at("IXITN_NotificationVersion").c_str()));

	m_ServiceHelper->initNotificationReceiver(m_NotificationValidator);
	ASSERT_EQ(status, ER_OK) << "serviceHelper Initialize() failed: " << QCC_StatusText(status);

	m_DeviceAboutAnnouncement =
		m_ServiceHelper->waitForNextDeviceAnnouncement(
		atol(m_GeneralParameterMap.at("GPCO_AnnouncementTimeout").c_str()) * 1000);

	ASSERT_NE(m_DeviceAboutAnnouncement, nullptr) << "Timed out waiting for About announcement";

	SUCCEED();

	LOG(INFO) << "test setUp done";
	CLOG(INFO, "raw") << "====================================================";
}

void NotifProducerTestSuite::TearDown()
{
	CLOG(INFO, "raw") << "====================================================";
	LOG(INFO) << "test tearDown started";

	releaseResources();

	LOG(INFO) << "test tearDown done";
	CLOG(INFO, "raw") << "====================================================";

	storeResults();
}

void NotifProducerTestSuite::releaseResources()
{
	if (m_ServiceHelper != nullptr)
	{
		m_ServiceHelper->release();

		delete m_ServiceHelper;
	}
}

TEST_F(NotifProducerTestSuite, Notification_v1_01)
{
	XMLBasedBusIntrospector busIntrospector = m_ServiceHelper->getBusIntrospector(*m_DeviceAboutAnnouncement);

	m_NotificationValidator->initializeForDevice(*m_DeviceAboutAnnouncement, busIntrospector);

	LOG(INFO) << "Waiting for user input";
	MessageBox(GetConsoleWindow(),
		L"Please initiate the sending of notifications from the DUT and click OK when all notifications that you want to test have been sent",
		L"Notification-v1-01",
		MB_OK | MB_ICONINFORMATION | MB_APPLMODAL);

	unsigned int numberOfNotificationsReceived = m_NotificationValidator->getNumberOfNotificationsReceived();

	ASSERT_NE(numberOfNotificationsReceived, 0) << "No notifications received!";

	LOG(INFO) << "Received " << numberOfNotificationsReceived << " notifications";
}