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
#include "NotificationValidator.h"

#include <regex>

#include "MessageSet.h"

const char* NotificationValidator::URL_REGEX = R"(^(([^:\/?#]+):)?(//([^\/?#]*))?([^?#]*)(\?([^#]*))?(#(.*))?)";

NotificationValidator::NotificationValidator(std::string t_DutDeviceId, uint8_t* t_DutAppId,
	bool t_ExpectedRichIconUrl, bool t_ExpectedRichAudioUrl, bool t_ExpectedResponseObjectPath,
	uint16_t t_ExpectedNotificationVersion) : 
	m_ExpectedRichIconUrl(t_ExpectedRichIconUrl),
	m_ExpectedRichAudioUrl(t_ExpectedRichAudioUrl), 
	m_ExpectedResponseObjectPath(t_ExpectedResponseObjectPath),
	m_ExpectedNotificationVersion(t_ExpectedNotificationVersion),
	m_ReadingNotifications(false)
{
	m_NotificationHandler = new NotificationHandler(t_DutDeviceId, t_DutAppId);
}

void NotificationValidator::initializeForDevice(AboutAnnouncementDetails& t_DeviceAboutAnnouncement,
	 XMLBasedBusIntrospector& t_BusIntrospector)
{
	m_DeviceAboutAnnouncement = &t_DeviceAboutAnnouncement;
	m_BusIntrospector = &t_BusIntrospector;

	m_ReadingNotifications = true;
}

void NotificationValidator::Receive(ajn::services::Notification const& t_Notification)
{
	if (m_ReadingNotifications)
	{
		m_NotificationHandler->Receive(t_Notification);

		readNotification();
	}
}

void NotificationValidator::Dismiss(const int32_t arg0, const qcc::String arg1) {}

void NotificationValidator::readNotification()
{
	ajn::services::Notification* notification;

	while ((notification = m_NotificationHandler->getReceivedNotification()) != nullptr)
	{
		LOG(INFO) << "Validating notification from DUT with messageId: "
			<< notification->getMessageId();

		m_NotificationCounter++;

		bool includedRichIconUrl = false;
		bool includedRichAudioUrl = false;
		bool includedResponseObjectPath = false;

		LOG(INFO) << "Checking Version field of received notification message";
		EXPECT_EQ(notification->getVersion(), m_ExpectedNotificationVersion)
			<< "Notification version received (" << notification->getVersion()
			<< ") does not match IXITN_NotificationVersion ("
			<< m_ExpectedNotificationVersion << ")";

		LOG(INFO) << "Checking AppName field of received notification message";
		EXPECT_EQ(std::string(notification->getAppName()), std::string(m_DeviceAboutAnnouncement->getAppName()))
			<< "AppName parameter received in notification: " << notification->getAppName()
			<< " does not match About Announcement parameter: "
			<< m_DeviceAboutAnnouncement->getAppName();

		LOG(INFO) << "Checking DeviceName field of received notification message";
		EXPECT_EQ(std::string(notification->getDeviceName()), std::string(m_DeviceAboutAnnouncement->getDeviceName()))
			<< "DeviceName parameter received in notification: " << notification->getDeviceName()
			<< " does not match About Announcement parameter: "
			<< m_DeviceAboutAnnouncement->getDeviceName();

		LOG(INFO) << "Checking notification message type";
		EXPECT_NE(notification->getMessageType(), ajn::services::NotificationMessageType::UNSET);

		const char* richIconUrl = notification->getRichIconUrl();
		if (m_ExpectedRichIconUrl && richIconUrl != nullptr)
		{
			validateUrl(richIconUrl);
		}
		else if ((m_ExpectedRichIconUrl && richIconUrl == nullptr)
			|| (!m_ExpectedRichIconUrl && richIconUrl != nullptr))
		{
			FAIL() << "RichIconUrl support is not the defined by ICSN_RichIconUrl";
		}
		
		const std::vector<ajn::services::RichAudioUrl> richAudioUrls = notification->getRichAudioUrl();
		if (m_ExpectedRichAudioUrl && !richAudioUrls.empty())
		{
			for (auto richAudioUrl : richAudioUrls)
			{
				const qcc::String url_string = richAudioUrl.getUrl();
				validateUrl(url_string.c_str());
			}
		}
		else if ((m_ExpectedRichAudioUrl && richAudioUrls.empty())
			|| (!m_ExpectedRichAudioUrl && !richAudioUrls.empty()))
		{
			FAIL() << "RichAudioUrl support is not the defined by ICSN_RichAudioUrl";
		}

		const char* responseObjectPath = notification->getControlPanelServiceObjectPath();
		if (m_ExpectedResponseObjectPath && responseObjectPath != nullptr)
		{
			LOG(INFO) << "Control Panel Service response object path: " << responseObjectPath;

			m_BusIntrospector->introspect(responseObjectPath);
		}
		else if ((m_ExpectedResponseObjectPath && responseObjectPath == nullptr)
			|| (!m_ExpectedResponseObjectPath && responseObjectPath != nullptr))
		{
			FAIL() << "RespObjectPath support is not the defined by ICSN_RespObjectPath";
		}

		std::string message("Notification Received: ");
		message.append("messageType: ");
		message.append(MessageSet("", notification->getMessageType()).getPriorityAsString());
		message.append("; richIconUrl: ");
		message.append(includedRichIconUrl ? "true" : "false");
		message.append("; richAudioUrl: ");
		message.append(includedRichAudioUrl ? "true" : "false");
		message.append("; responseObject: ");
		message.append(includedResponseObjectPath ? "true" : "false");
		LOG(INFO) << message;
	}
}

unsigned int NotificationValidator::getNumberOfNotificationsReceived()
{
	return static_cast<unsigned int>(m_NotificationCounter);
}

void NotificationValidator::validateUrl(const std::string& t_Url)
{
	std::regex urlRegex(URL_REGEX, std::regex::extended);
	std::smatch urlMatchResult;

	ASSERT_TRUE(std::regex_match(t_Url, urlMatchResult, urlRegex))
		<< "Malformed url.";
	SUCCEED() << "Url format is correct";
}

