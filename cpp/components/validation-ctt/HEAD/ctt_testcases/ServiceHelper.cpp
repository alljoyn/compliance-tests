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
#include "ServiceHelper.h"

#include "DeviceAnnouncementHandler.h"
#include "OnboardingSignalListenerImpl.h"

#include <alljoyn\Init.h>
#include <alljoyn\Session.h>
#include <alljoyn\notification\NotificationService.h>

using namespace ajn;
using namespace services;
using namespace std;

const char* ServiceHelper::AUTH_MECHANISMS = "ALLJOYN_SRP_KEYX ALLJOYN_ECDHE_NULL ALLJOYN_ECDHE_PSK ALLJOYN_ECDHE_ECDSA";
uint32_t ServiceHelper::LINK_TIMEOUT_IN_SECONDS = 120;

QStatus ServiceHelper::initializeClient(const string& t_BusApplicationName,
	const string& t_DeviceId, const uint8_t* t_AppId)
{
	return initialize(t_BusApplicationName, t_DeviceId, t_AppId, true);
}

QStatus ServiceHelper::initializeSender(const string& t_BusApplicationName,
	const string& t_DeviceId, const uint8_t* t_AppId)
{
	return initialize(t_BusApplicationName, t_DeviceId, t_AppId, false);
}

QStatus ServiceHelper::initialize(const string& t_BusApplicationName,
	const string& t_DeviceId, const uint8_t* t_AppId, const bool t_Listener)
{
	QStatus status = AllJoynInit();
	if (status != ER_OK)
	{
		LOG(ERROR) << "AllJoynInit() failed: " << QCC_StatusText(status);
		return status;
	}

	status = AllJoynRouterInit();
	if (status != ER_OK)
	{
		LOG(ERROR) << "AllJoynRouterInit() failed: " << QCC_StatusText(status);
		AllJoynShutdown();
		return status;
	}

	m_BusAttachmentMgr = new BusAttachmentMgr();

	m_PasswordStore = new PasswordStore();
	m_AuthPasswordHandlerImpl = new AuthPasswordHandlerImpl(m_PasswordStore);

	m_BusAttachmentMgr->create(t_BusApplicationName, true);
	status = m_BusAttachmentMgr->connect();
	if (status != ER_OK)
	{
		return status;
	}

	if (t_Listener)
	{
		LOG(INFO) << "Adding AboutListener for About announcements";
		m_DeviceAnnouncementHandler = new DeviceAnnouncementHandler(t_DeviceId, t_AppId);
		m_BusAttachmentMgr->getBusAttachment()->RegisterAboutListener(*m_DeviceAnnouncementHandler);
		status = m_BusAttachmentMgr->getBusAttachment()->WhoImplements(NULL);
		if (status != ER_OK)
		{
			return status;
		}
	}

	status = m_BusAttachmentMgr->advertise();

	return status;
}

AboutAnnouncementDetails* ServiceHelper::waitForNextDeviceAnnouncement(const long t_Timeout)
{
	return m_DeviceAnnouncementHandler->waitForNextDeviceAnnouncement(t_Timeout);
}

AboutProxy* ServiceHelper::connectAboutProxy(const AboutAnnouncementDetails& t_AboutAnnouncementDetails)
{
	LOG(INFO) << "Creating AboutProxy for serviceName: " << t_AboutAnnouncementDetails.getServiceName()
		<< "; port: " << t_AboutAnnouncementDetails.getPort()
		<< "; deviceName: " << t_AboutAnnouncementDetails.getDeviceName();

	joinSession(t_AboutAnnouncementDetails.getServiceName(), t_AboutAnnouncementDetails.getPort());

	AboutProxy* aboutProxy = new AboutProxy(*m_BusAttachmentMgr->getBusAttachment(), 
		t_AboutAnnouncementDetails.getServiceName().c_str(), m_SessionId);

	return aboutProxy;
}

AboutIconProxy* ServiceHelper::connectAboutIconProxy(const AboutAnnouncementDetails& t_AboutAnnouncementDetails)
{
	LOG(INFO) << "Creating AboutIconProxy for serviceName: " << t_AboutAnnouncementDetails.getServiceName()
		<< "; port: " << t_AboutAnnouncementDetails.getPort()
		<< "; deviceName: " << t_AboutAnnouncementDetails.getDeviceName();

	joinSession(t_AboutAnnouncementDetails.getServiceName(), t_AboutAnnouncementDetails.getPort());

	AboutIconProxy* aboutIconProxy = new AboutIconProxy(*m_BusAttachmentMgr->getBusAttachment(),
		t_AboutAnnouncementDetails.getServiceName().c_str(), m_SessionId);

	return aboutIconProxy;
}

void ServiceHelper::joinSession(const string& t_BusName, const uint16_t t_Port)
{
	SessionOpts sessionOpts(SessionOpts::TRAFFIC_MESSAGES, false, 
		SessionOpts::PROXIMITY_ANY, TRANSPORT_ANY);
	//m_BusAttachmentMgr->getBusAttachment()->EnableConcurrentCallbacks();

	QStatus status = m_BusAttachmentMgr->getBusAttachment()->JoinSession(t_BusName.c_str(),
		t_Port, &m_SessionListener, m_SessionId, sessionOpts);

	if (status != QStatus::ER_OK)
	{
		LOG(INFO) << QCC_StatusText(status);
	}
	else
	{
		LOG(INFO) << "Session joined";
	}
}

QStatus ServiceHelper::release()
{
	shutdownNotificationService();
	disconnectBusAttachment();

	QStatus status = AllJoynRouterShutdown();
	if (status != ER_OK)
	{
		LOG(ERROR) << "AllJoynRouterShutdown() failed: " << status;
		return status;
	}

	status = AllJoynShutdown();
	if (status != ER_OK)
	{
		LOG(ERROR) << "AllJoynShutdown() failed: " << status;
		return status;
	}

	return status;
}

void ServiceHelper::disconnectBusAttachment()
{
	if (m_BusAttachmentMgr != nullptr)
	{
		m_BusAttachmentMgr->getBusAttachment()->UnregisterAboutListener(*m_DeviceAnnouncementHandler);
		m_BusAttachmentMgr->release();
		delete m_BusAttachmentMgr;
	}
}

XMLBasedBusIntrospector ServiceHelper::getBusIntrospector(const AboutAnnouncementDetails& t_DeviceAboutAnnouncement)
{
	return XMLBasedBusIntrospector(m_BusAttachmentMgr->getBusAttachment(),
		t_DeviceAboutAnnouncement.getServiceName(), m_SessionId);
}

ProxyBusObject ServiceHelper::getProxyBusObject(const AboutAnnouncementDetails& t_DeviceAboutAnnouncement,
	const string& t_Path)
{
	ProxyBusObject proxyBusObject(*m_BusAttachmentMgr->getBusAttachment(),
		t_DeviceAboutAnnouncement.getServiceName().c_str(), t_Path.c_str(), m_SessionId);
	proxyBusObject.IntrospectRemoteObject();

	return proxyBusObject;
}

BusAttachmentMgr* ServiceHelper::getBusAttachmentMgr()
{
	return m_BusAttachmentMgr;
}

NotificationSender* ServiceHelper::initNotificationSender(AboutData* t_DataStore)
{
	m_NotificationService = NotificationService::getInstance();
	LOG(INFO) << "Calling initSend on NotificationService";
	return m_NotificationService->initSend(m_BusAttachmentMgr->getBusAttachment(), t_DataStore);
}

void ServiceHelper::initNotificationReceiver(NotificationReceiver* m_Receiver)
{
	m_NotificationService = NotificationService::getInstance();
	LOG(INFO) << "Calling initReceive on NotificationService";
	m_NotificationService->initReceive(m_BusAttachmentMgr->getBusAttachment(), m_Receiver);
}

void ServiceHelper::shutdownNotificationService()
{
	if (m_NotificationService != nullptr)
	{
		LOG(INFO) << "Shutting down NotificationService";
		m_NotificationService->shutdown();

		m_NotificationService = nullptr;
	}
}

ConfigClient* ServiceHelper::connectConfigClient(ajn::SessionId& t_SessionId)
{
	LOG(INFO) << "Creating ConfigClient";
	t_SessionId = m_SessionId;
	return new ConfigClient(*m_BusAttachmentMgr->getBusAttachment());
}

void ServiceHelper::setAuthPassword(const AboutAnnouncementDetails& t_AboutAnnouncementDetails,
	const char* t_Password)
{
	const char* currentPasscode = m_PasswordStore->getPassword(t_AboutAnnouncementDetails.getServiceName());

	if (currentPasscode == nullptr)
	{
		currentPasscode = SrpAnonymousKeyListener::DEFAULT_PINCODE;
	}

	if (t_Password == nullptr)
	{
		t_Password = SrpAnonymousKeyListener::DEFAULT_PINCODE;
	}

	LOG(INFO) << "Setting passcode that will be used when authenticating to " << t_AboutAnnouncementDetails.getServiceName()
		<< " from " << currentPasscode << " to " << t_Password;
	m_PasswordStore->setPassword(t_AboutAnnouncementDetails.getServiceName(), t_Password);
	clearKeyStore();
}

void ServiceHelper::clearKeyStore()
{
	LOG(INFO) << "Calling ClearKeys";
	m_BusAttachmentMgr->getBusAttachment()->ClearKeyStore();

}

QStatus ServiceHelper::enableAuthentication(const std::string& t_KeyStoreFileName)
{
	m_AuthListener = new SrpAnonymousKeyListener(m_AuthPasswordHandlerImpl);
	LOG(INFO) << "Registering an AuthListener";
	return m_BusAttachmentMgr->getBusAttachment()->EnablePeerSecurity(AUTH_MECHANISMS, m_AuthListener, t_KeyStoreFileName.c_str());
}

bool ServiceHelper::isPeerAuthenticationSuccessful(const AboutAnnouncementDetails& t_AboutAnnouncementDetails)
{
	return m_AuthPasswordHandlerImpl->isPeerAuthenticationSuccessful(t_AboutAnnouncementDetails.getServiceName());
}

void ServiceHelper::clearQueuedDeviceAnnouncements()
{
	m_DeviceAnnouncementHandler->clearQueuedDeviceAnnouncements();
}

void ServiceHelper::clearPeerAuthenticationFlags(const AboutAnnouncementDetails& t_AboutAnnouncementDetails)
{
	m_AuthPasswordHandlerImpl->resetAuthentication(t_AboutAnnouncementDetails.getServiceName());
}

const char* ServiceHelper::getAuthPassword(const AboutAnnouncementDetails& t_AboutAnnouncementDetails)
{
	const char* currentPasscode = m_PasswordStore->getPassword(t_AboutAnnouncementDetails.getServiceName());

	if (currentPasscode == nullptr)
	{
		currentPasscode = SrpAnonymousKeyListener::DEFAULT_PINCODE;
	}

	return currentPasscode;
}

OnboardingClient* ServiceHelper::connectOnboardingClient(const AboutAnnouncementDetails& t_AboutAnnouncementDetails)
{
	LOG(INFO) << "Creating OnboardingClient for serviceName: " << t_AboutAnnouncementDetails.getServiceName()
		<< "; port: " << t_AboutAnnouncementDetails.getPort() << "; deviceName: "
		<< t_AboutAnnouncementDetails.getDeviceName();

	OnboardingSignalListenerImpl* signalListener = new OnboardingSignalListenerImpl();
	OnboardingClient* onboardingClient = new OnboardingClient(*m_BusAttachmentMgr->getBusAttachment(), *signalListener);

	LOG(INFO) << "Session established to peer: " << t_AboutAnnouncementDetails.getServiceName();
	QStatus status = m_BusAttachmentMgr->getBusAttachment()->SetLinkTimeout(m_SessionId, LINK_TIMEOUT_IN_SECONDS);

	return onboardingClient;
}

ajn::SessionId ServiceHelper::getSessionId()
{
	return m_SessionId;
}

/*void ServiceHelper::waitForSessionToClose(const uint16_t t_Timeout)
{
	m_DeviceAnnouncementHandler->waitForSessionToClose(t_Timeout);
	m_DeviceAnnouncementHandler = nullptr;
}*/