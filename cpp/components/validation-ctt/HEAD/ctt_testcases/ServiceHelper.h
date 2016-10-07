/******************************************************************************
*    Copyright (c) Open Connectivity Foundation (OCF) and AllJoyn Open
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
#pragma once

#include "AuthListeners.h"
#include "BusAttachmentMgr.h"
#include "DeviceAnnouncementHandler.h"
#include "ECDHENullHandlerImpl.h"
#include "ECDHEPskHandlerImpl.h"
#include "ECDHEPskStore.h"
#include "ECDHEEcdsaHandlerImpl.h"
#include "ECDHEEcdsaStore.h"
#include "PinKeyXHandlerImpl.h"
#include "PinKeyXStore.h"
#include "RsaKeyXHandlerImpl.h"
#include "RsaKeyXStore.h"
#include "ServiceAvailabilityHandler.h"
#include "SrpKeyXHandlerImpl.h"
#include "SrpKeyXStore.h"
#include "SrpLogonHandlerImpl.h"
#include "SrpLogonStore.h"
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
	void release();
	XMLBasedBusIntrospector getBusIntrospector(const AboutAnnouncementDetails&);
	ajn::SessionId getSessionId();

	// Events and Actions
	ajn::ProxyBusObject getProxyBusObject(const AboutAnnouncementDetails&, const std::string&);
	BusAttachmentMgr* getBusAttachmentMgr();

	// Notification
	ajn::services::NotificationSender* initNotificationSender(ajn::AboutData*);
	void initNotificationReceiver(ajn::services::NotificationReceiver*);
	

	// Configuration
	ajn::services::ConfigClient* connectConfigClient(ajn::SessionId&);
	void clearKeyStore();
	QStatus enableAuthentication(const std::string&,
		const bool, const std::string&,
		const bool, const std::string&, const std::string&,
		const bool,
		const bool, const std::string&,
		const bool, const std::string&, const std::string&,
		const bool, const std::string&, const std::string&,
		const bool, const std::string&);
	bool isPeerAuthenticationSuccessful(const AboutAnnouncementDetails&);
	void clearQueuedDeviceAnnouncements();

	void setSrpKeyXPincode(const AboutAnnouncementDetails&, const char*);
	void setSrpLogonPass(const AboutAnnouncementDetails&, const char*);
	void setEcdhePskPassword(const AboutAnnouncementDetails&, const char*);
	void setEcdheEcdsaCredentials(const AboutAnnouncementDetails&, const char*, const char*);
	void setRsaKeyXCredentials(const AboutAnnouncementDetails&, const char*, const char*);
	void setPinKeyXPincode(const AboutAnnouncementDetails&, const char*);

	// ControlPanel
	void clearPeerAuthenticationFlags(const AboutAnnouncementDetails&);

	// Onboarding
	ajn::services::OnboardingClient* connectOnboardingClient(const AboutAnnouncementDetails&);

	// LSF_Lamp
	//void waitForSessionToClose(const uint16_t);
	
private:
	static const char* AUTH_MECHANISMS;
	static uint32_t LINK_TIMEOUT_IN_SECONDS;

	BusAttachmentMgr* m_BusAttachmentMgr{ nullptr };
	//PasswordStore* m_PasswordStore{ nullptr };
	//AuthPasswordHandlerImpl* m_AuthPasswordHandlerImpl{ nullptr };
	DeviceAnnouncementHandler* m_DeviceAnnouncementHandler{ nullptr };
	ServiceAvailabilityHandler m_SessionListener;
	ajn::SessionId m_SessionId;
	ajn::services::NotificationService* m_NotificationService{ nullptr };
	//SrpAnonymousKeyListener* m_AuthListener{ nullptr };
	AuthListeners* m_AuthListener{ nullptr };

	// ALLJOYN_SRP_KEYX
	SrpKeyXStore* m_SrpKeyXStore{ nullptr };
	SrpKeyXHandlerImpl* m_SrpKeyXHandlerImpl{ nullptr };
	bool m_SupportsSrpKeyX;
	std::string m_DefaultSrpKeyXPincode = std::string("");
	// ALLJOYN_SRP_LOGON
	SrpLogonStore* m_SrpLogonStore{ nullptr };
	SrpLogonHandlerImpl* m_SrpLogonHandlerImpl{ nullptr };
	bool m_SupportsSrpLogon;
	std::string m_DefaultLogonUser = std::string("");
	std::string m_DefaultLogonPass = std::string("");
	// ALLJOYN_ECDHE_NULL
	ECDHENullHandlerImpl* m_ECDHENullHandlerImpl{ nullptr };
	bool m_SupportsEcdheNull;
	// ALLJOYN_ECDHE_PSK
	ECDHEPskStore* m_ECDHEPskStore{ nullptr };
	ECDHEPskHandlerImpl* m_ECDHEPskHandlerImpl{ nullptr };
	bool m_SupportsEcdhePsk;
	std::string m_DefaultECDHEPskPassword = std::string("");
	// ALLJOYN_ECDHE_ECDSA
	ECDHEEcdsaStore* m_ECDHEEcdsaStore{ nullptr };
	ECDHEEcdsaHandlerImpl* m_ECDHEEcdsaHandlerImpl{ nullptr };
	bool m_SupportsEcdheEcdsa;
	std::string m_DefaultECDHEEcdsaPrivateKey = std::string("");
	std::string m_DefaultECDHEEcdsaCertChain = std::string("");
	// ALLJOYN_PIN_KEYX
	PinKeyXStore* m_PinKeyXStore{ nullptr };
	PinKeyXHandlerImpl* m_PinKeyXHandlerImpl{ nullptr };
	bool m_SupportsPinKeyX;
	std::string m_DefaultPinKeyXPincode = std::string("");
	// ALLJOYN_RSA_KEYX
	RsaKeyXStore* m_RsaKeyXStore{ nullptr };
	RsaKeyXHandlerImpl* m_RsaKeyXHandlerImpl{ nullptr };
	bool m_SupportsRsaKeyX;
	std::string m_DefaultRsaKeyXPrivateKey = std::string("");
	std::string m_DefaultRsaKeyXCertX509 = std::string("");

	QStatus initialize(const std::string&, const std::string&, const uint8_t*, const bool);
	void disconnectBusAttachment();
	void shutdownNotificationService();
};