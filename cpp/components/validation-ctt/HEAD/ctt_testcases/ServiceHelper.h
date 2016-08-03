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
#pragma once

#include "AuthPasswordHandlerImpl.h"
#include "BusAttachmentMgr.h"
#include "DeviceAnnouncementHandler.h"
#include "PasswordStore.h"
#include "ServiceAvailabilityHandler.h"
#include "SrpAnonymousKeyListener.h"
#include "XMLBasedBusIntrospector.h"

#include <alljoyn\AboutProxy.h>
#include <alljoyn\AboutIconProxy.h>
#include <alljoyn\config\ConfigClient.h>
#include <alljoyn\notification\NotificationReceiver.h>
#include <alljoyn\onboarding\OnboardingClient.h>

class ServiceHelper
{

public:
	// About
	QStatus initializeClient(const std::string&, const std::string&, const uint8_t*);
	QStatus initializeSender(const std::string&, const std::string&, const uint8_t*);
	AboutAnnouncementDetails* waitForNextDeviceAnnouncement(const long);
	ajn::AboutProxy* connectAboutProxy(const AboutAnnouncementDetails&);
	ajn::AboutIconProxy* connectAboutIconProxy(const AboutAnnouncementDetails&);
	void joinSession(const std::string&, const uint16_t);
	QStatus release();
	XMLBasedBusIntrospector getBusIntrospector(const AboutAnnouncementDetails&);
	ajn::SessionId getSessionId();

	// Events and Actions
	ajn::ProxyBusObject getProxyBusObject(const AboutAnnouncementDetails&, const std::string&);
	BusAttachmentMgr* getBusAttachmentMgr();

	// Notification
	ajn::services::NotificationSender* initNotificationSender(ajn::AboutData*);
	void initNotificationReceiver(ajn::services::NotificationReceiver*);
	void shutdownNotificationService();

	// Configuration
	ajn::services::ConfigClient* connectConfigClient(ajn::SessionId&);
	void setAuthPassword(const AboutAnnouncementDetails&, const char*);
	void clearKeyStore();
	QStatus enableAuthentication(const std::string&);
	bool isPeerAuthenticationSuccessful(const AboutAnnouncementDetails&);
	void clearQueuedDeviceAnnouncements();

	// ControlPanel
	void clearPeerAuthenticationFlags(const AboutAnnouncementDetails&);
	const char* getAuthPassword(const AboutAnnouncementDetails&);

	// Onboarding
	ajn::services::OnboardingClient* connectOnboardingClient(const AboutAnnouncementDetails&);

	// LSF_Lamp
	//void waitForSessionToClose(const uint16_t);
	
private:
	static const char* AUTH_MECHANISMS;
	static uint32_t LINK_TIMEOUT_IN_SECONDS;

	BusAttachmentMgr* m_BusAttachmentMgr{ nullptr };
	PasswordStore* m_PasswordStore{ nullptr };
	AuthPasswordHandlerImpl* m_AuthPasswordHandlerImpl{ nullptr };
	DeviceAnnouncementHandler* m_DeviceAnnouncementHandler{ nullptr };
	ServiceAvailabilityHandler m_SessionListener;
	ajn::SessionId m_SessionId;
	ajn::services::NotificationService* m_NotificationService{ nullptr };
	SrpAnonymousKeyListener* m_AuthListener{ nullptr };

	QStatus initialize(const std::string&, const std::string&, const uint8_t*, const bool);
	void disconnectBusAttachment();

	
};