/******************************************************************************
*    Copyright (c) Open Connectivity Foundation (OCF), AllJoyn Open Source
*    Project (AJOSP) Contributors and others.
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
*    THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL
*    WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED
*    WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE
*    AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL
*    DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR
*    PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER
*    TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
*    PERFORMANCE OF THIS SOFTWARE.
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

uint32_t ServiceHelper::LINK_TIMEOUT_IN_SECONDS = 120;

static void DebugOut(DbgMsgType type, AJ_PCSTR module, AJ_PCSTR msg, void* context)
{
    QCC_UNUSED(type);
    QCC_UNUSED(module);
    QCC_UNUSED(msg);
    QCC_UNUSED(context);
    // Do nothing to suppress AJ errors and debug prints
}

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
    const string& t_DeviceId, const uint8_t* t_AppId, bool t_Listener)
{
    //--------------------------------------------------
    // ALLJOYN INITIALIZATION
    //--------------------------------------------------
    /*
        AllJoynInit() must be called prior to instantiating or using any AllJoyn functionality.
        AllJoynShutdown() must be called for each invocation of AllJoynInit().
    */
    QStatus status = AllJoynInit();
    if (ER_OK != status)
    {
        LOG(ERROR) << "AllJoynInit() failed: " << QCC_StatusText(status);
        return status;
    }

    /*
        For an application that is a routing node (either standalone or bundled),
        AllJoynRouterInit() must be called before using any AllJoyn router functionality and after AllJoynInit()
    */
    status = AllJoynRouterInit();
    if (ER_OK != status)
    {
        LOG(ERROR) << "AllJoynRouterInit() failed: " << QCC_StatusText(status);
        AllJoynShutdown();
        return status;
    }

#if NDEBUG
    QCC_RegisterOutputCallback(DebugOut, NULL);
#endif

    //--------------------------------------------------
    // BUS ATTACHMENT INITIALIZATION
    //--------------------------------------------------
    m_BusAttachmentMgr = new BusAttachmentMgr();
    m_BusAttachmentMgr->create(t_BusApplicationName, true);

    if (ER_OK != (status = m_BusAttachmentMgr->connect()))
    {
        return status;
    }

    if (t_Listener)
    {
        LOG(INFO) << "Adding AboutListener for About announcements";
        m_DeviceAnnouncementHandler = new DeviceAnnouncementHandler(t_DeviceId, t_AppId);
        m_BusAttachmentMgr->getBusAttachment()->RegisterAboutListener(*m_DeviceAnnouncementHandler);

        if (ER_OK != (status = m_BusAttachmentMgr->getBusAttachment()->WhoImplements(NULL)))
        {
            return status;
        }
    }

    status = m_BusAttachmentMgr->advertise();

    return status;
}

AboutAnnouncementDetails* ServiceHelper::waitForNextDeviceAnnouncement(long t_Timeout)
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

void ServiceHelper::joinSession(const string& t_BusName, uint16_t t_Port)
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
    if (ER_OK != status)
    {
        LOG(ERROR) << "AllJoynRouterShutdown() failed: " << status;
        return status;
    }

    status = AllJoynShutdown();
    if (ER_OK != status)
    {
        LOG(ERROR) << "AllJoynShutdown() failed: " << status;
        return status;
    }

    return status;
}

void ServiceHelper::disconnectBusAttachment()
{
    if (nullptr != m_BusAttachmentMgr)
    {
        if (nullptr != m_DeviceAnnouncementHandler)
        {
            m_BusAttachmentMgr->getBusAttachment()->UnregisterAboutListener(*m_DeviceAnnouncementHandler);
            delete m_DeviceAnnouncementHandler;
            m_DeviceAnnouncementHandler = nullptr;
        }
        
        m_BusAttachmentMgr->release();
        delete m_BusAttachmentMgr;
        m_BusAttachmentMgr = nullptr;
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

void ServiceHelper::setSrpKeyXPincode(const AboutAnnouncementDetails& t_AboutAnnouncementDetails,
    AJ_PCSTR t_Pincode)
{
    AJ_PCSTR currentPasscode = m_SrpKeyXStore->getPincode(t_AboutAnnouncementDetails.getServiceName());

    if (currentPasscode == nullptr)
    {
        currentPasscode = m_DefaultSrpKeyXPincode.c_str();
    }

    if (t_Pincode == nullptr)
    {
        t_Pincode = m_DefaultSrpKeyXPincode.c_str();
    }

    LOG(INFO) << "Setting pincode that will be used when authenticating with SRP_KEYX to " << t_AboutAnnouncementDetails.getServiceName()
        << " from " << currentPasscode << " to " << t_Pincode;
    m_SrpKeyXStore->setPincode(t_AboutAnnouncementDetails.getServiceName(), t_Pincode);
}

void ServiceHelper::setSrpLogonPass(const AboutAnnouncementDetails& t_AboutAnnouncementDetails, AJ_PCSTR t_Pass)
{
    AJ_PCSTR currentUser = m_SrpLogonStore->getUser(t_AboutAnnouncementDetails.getServiceName());
    AJ_PCSTR currentPass = m_SrpLogonStore->getPass(t_AboutAnnouncementDetails.getServiceName());

    if (currentUser == nullptr)
    {
        currentUser = m_DefaultLogonUser.c_str();
    }

    if (currentPass == nullptr)
    {
        currentPass = m_DefaultLogonPass.c_str();
    }

    if (t_Pass == nullptr)
    {
        t_Pass = m_DefaultLogonPass.c_str();
    }

    LOG(INFO) << "Setting user and pass that will be used when authenticating with SRP_LOGON to " << t_AboutAnnouncementDetails.getServiceName()
        << " from (" << currentUser << ", " << currentPass << ") to (" << currentUser << ", " << t_Pass << ")";
    m_SrpLogonStore->setPass(t_AboutAnnouncementDetails.getServiceName(), t_Pass);
}

void ServiceHelper::setEcdhePskPassword(const AboutAnnouncementDetails& t_AboutAnnouncementDetails, AJ_PCSTR t_Password)
{
    AJ_PCSTR currentPassword = m_ECDHEPskStore->getPassword(t_AboutAnnouncementDetails.getServiceName());

    if (currentPassword == nullptr)
    {
        currentPassword = m_DefaultECDHEPskPassword.c_str();
    }

    if (t_Password == nullptr)
    {
        t_Password = m_DefaultECDHEPskPassword.c_str();
    }

    LOG(INFO) << "Setting password that will be used when authenticating with ECDHE_PSK to " << t_AboutAnnouncementDetails.getServiceName()
        << " from " << currentPassword << " to " << t_Password;
    m_ECDHEPskStore->setPassword(t_AboutAnnouncementDetails.getServiceName(), t_Password);
}

void ServiceHelper::setEcdheEcdsaCredentials(const AboutAnnouncementDetails& t_AboutAnnouncementDetails,
    AJ_PCSTR t_PrivateKey, AJ_PCSTR t_CertChain)
{
    AJ_PCSTR currentPrivateKey = m_ECDHEEcdsaStore->getPrivateKey(t_AboutAnnouncementDetails.getServiceName());
    AJ_PCSTR currentCertChain = m_ECDHEEcdsaStore->getCertChain(t_AboutAnnouncementDetails.getServiceName());

    if (currentPrivateKey == nullptr)
    {
        currentPrivateKey = m_DefaultECDHEEcdsaPrivateKey.c_str();
    }

    if (currentCertChain == nullptr)
    {
        currentCertChain = m_DefaultECDHEEcdsaCertChain.c_str();
    }

    if (t_PrivateKey == nullptr)
    {
        t_PrivateKey = m_DefaultECDHEEcdsaPrivateKey.c_str();
    }

    if (t_CertChain == nullptr)
    {
        t_CertChain = m_DefaultECDHEEcdsaCertChain.c_str();
    }

    LOG(INFO) << "Setting credentials that will be used when authenticating with ECDHE_ECDSA to " << t_AboutAnnouncementDetails.getServiceName()
        << " from (" << currentPrivateKey << ", " << currentCertChain << ") to (" << t_PrivateKey << ", " << t_CertChain << ")";
    m_ECDHEEcdsaStore->setPrivateKey(t_AboutAnnouncementDetails.getServiceName(), t_PrivateKey);
    m_ECDHEEcdsaStore->setCertChain(t_AboutAnnouncementDetails.getServiceName(), t_CertChain);
    //clearKeyStore();
}

void ServiceHelper::setEcdheSpekePassword(const AboutAnnouncementDetails& t_AboutAnnouncementDetails,
    AJ_PCSTR t_Password)
{
    AJ_PCSTR currentPassword = m_ECDHESpekeStore->getPassword();

    if (currentPassword == nullptr)
    {
        currentPassword = m_DefaultECDHESpekePassword.c_str();
    }

    if (t_Password == nullptr)
    {
        t_Password = m_DefaultECDHESpekePassword.c_str();
    }

    LOG(INFO) << "Setting password that will be used when authenticating with ECDHE_SPEKE to " << t_AboutAnnouncementDetails.getServiceName()
        << " from " << currentPassword << " to " << t_Password;
    m_ECDHESpekeStore->setPassword(t_Password);
}

void ServiceHelper::clearKeyStore()
{
    LOG(INFO) << "Calling ClearKeys";
    m_BusAttachmentMgr->getBusAttachment()->ClearKeyStore();

}

QStatus ServiceHelper::enableAuthentication(AJ_PCSTR t_KeyStoreFileName,
    bool t_SupportsSrpKeyX, const string& t_DefaultSrpXPincode,
    bool t_SupportsSrpLogon, const string& t_DefaultLogonUser, const string& t_DefaultLogonPass,
    bool t_SupportsEcdheNull,
    bool t_SupportsEcdhePsk, const string& t_DefaultECDHEPskPassword,
    bool t_SupportsEcdheEcdsa, const string& t_DefaultECDHEEcdsaPrivateKey, const string& t_DefaultECDHEEcdsaCertChain,
    bool t_SupportsEcdheSpeke, const string& t_DefaultECDHESpekePassword)
{
    releaseAuthenticationClasses();
    ////--------------------------------------------------
    //// AUTHENTICATION LISTENERS INITIALIZATION
    ////--------------------------------------------------
    if (m_SupportsSrpKeyX = t_SupportsSrpKeyX)
    {
        m_SrpKeyXStore = new SrpKeyXStore();
        m_SrpKeyXHandlerImpl = new SrpKeyXHandlerImpl(m_SrpKeyXStore, t_DefaultSrpXPincode);
        m_DefaultSrpKeyXPincode = t_DefaultSrpXPincode;
    }

    if (m_SupportsSrpLogon = t_SupportsSrpLogon)
    {
        m_SrpLogonStore = new SrpLogonStore();
        m_SrpLogonHandlerImpl = new SrpLogonHandlerImpl(m_SrpLogonStore, t_DefaultLogonUser, t_DefaultLogonPass);
        m_DefaultLogonUser = t_DefaultLogonUser;
        m_DefaultLogonPass = t_DefaultLogonPass;
    }

    if (m_SupportsEcdheNull = t_SupportsEcdheNull)
    {
        m_ECDHENullHandlerImpl = new ECDHENullHandlerImpl();
    }

    if (m_SupportsEcdhePsk = t_SupportsEcdhePsk)
    {
        m_ECDHEPskStore = new ECDHEPskStore();
        m_ECDHEPskHandlerImpl = new ECDHEPskHandlerImpl(m_ECDHEPskStore, t_DefaultECDHEPskPassword);
        m_DefaultECDHEPskPassword = t_DefaultECDHEPskPassword;
    }

    if (m_SupportsEcdheEcdsa = t_SupportsEcdheEcdsa)
    {
        m_ECDHEEcdsaStore = new ECDHEEcdsaStore();
        m_ECDHEEcdsaHandlerImpl = new ECDHEEcdsaHandlerImpl(m_ECDHEEcdsaStore, t_DefaultECDHEEcdsaPrivateKey, t_DefaultECDHEEcdsaCertChain);
        m_DefaultECDHEEcdsaPrivateKey = t_DefaultECDHEEcdsaPrivateKey;
        m_DefaultECDHEEcdsaCertChain = t_DefaultECDHEEcdsaCertChain;
    }

    if (m_SupportsEcdheSpeke = t_SupportsEcdheSpeke)
    {
        m_ECDHESpekeStore = new ECDHESpekeStore();
        m_ECDHESpekeHandlerImpl = new ECDHESpekeHandlerImpl(m_ECDHESpekeStore, t_DefaultECDHESpekePassword);
        m_DefaultECDHESpekePassword = t_DefaultECDHESpekePassword;
    }

    if (m_SupportsSrpKeyX || m_SupportsSrpLogon || m_SupportsEcdheNull || m_SupportsEcdhePsk || m_SupportsEcdheEcdsa || m_SupportsEcdheSpeke)
    {
        LOG(INFO) << "Registering an AuthListener";
        m_AuthListener = new AuthListeners(m_SupportsSrpKeyX, m_SrpKeyXHandlerImpl, m_DefaultSrpKeyXPincode,
            m_SupportsSrpLogon, m_SrpLogonHandlerImpl, m_DefaultLogonUser, m_DefaultLogonPass,
            m_SupportsEcdheNull, m_ECDHENullHandlerImpl,
            m_SupportsEcdhePsk, m_ECDHEPskHandlerImpl, m_DefaultECDHEPskPassword,
            m_SupportsEcdheEcdsa, m_ECDHEEcdsaHandlerImpl, m_DefaultECDHEEcdsaPrivateKey, m_DefaultECDHEEcdsaCertChain,
            m_SupportsEcdheSpeke, m_ECDHESpekeHandlerImpl, m_DefaultECDHESpekePassword);
    }

    return m_BusAttachmentMgr->getBusAttachment()->EnablePeerSecurity(
        m_AuthListener == nullptr ? "" : m_AuthListener->getAuthMechanismsAsString().c_str(), m_AuthListener, t_KeyStoreFileName);
}

void ServiceHelper::releaseAuthenticationClasses()
{
    if (m_SupportsSrpKeyX)
    {
        delete m_SrpKeyXStore;
        delete m_SrpKeyXHandlerImpl;
        m_DefaultSrpKeyXPincode.clear();
    }

    if (m_SupportsSrpLogon)
    {
        delete m_SrpLogonStore;
        delete m_SrpLogonHandlerImpl;
        m_DefaultLogonUser.clear();
        m_DefaultLogonPass.clear();
    }

    if (m_SupportsEcdheNull)
    {
        delete m_ECDHENullHandlerImpl;
    }

    if (m_SupportsEcdhePsk)
    {
        delete m_ECDHEPskStore;
        delete m_ECDHEPskHandlerImpl;
        m_DefaultECDHEPskPassword.clear();
    }

    if (m_SupportsEcdheEcdsa)
    {
        delete m_ECDHEEcdsaStore;
        delete m_ECDHEEcdsaHandlerImpl;
        m_DefaultECDHEEcdsaPrivateKey.clear();
        m_DefaultECDHEEcdsaCertChain.clear();
    }

    if (m_SupportsEcdheSpeke)
    {
        delete m_ECDHESpekeStore;
        delete m_ECDHESpekeHandlerImpl;
        m_DefaultECDHESpekePassword.clear();
    }

    if (nullptr != m_AuthListener)
    {
        delete m_AuthListener;
        m_AuthListener = nullptr;
    }
}

bool ServiceHelper::isPeerAuthenticationSuccessful(const AboutAnnouncementDetails& t_AboutAnnouncementDetails)
{
    bool isPeerAuthenticationSuccessful = false;

    if (m_SupportsSrpKeyX)
    {
        isPeerAuthenticationSuccessful = isPeerAuthenticationSuccessful ||
            m_SrpKeyXHandlerImpl->isPeerAuthenticationSuccessful(t_AboutAnnouncementDetails.getServiceName());
    }

    if (m_SupportsSrpLogon)
    {
        isPeerAuthenticationSuccessful = isPeerAuthenticationSuccessful ||
            m_SrpLogonHandlerImpl->isPeerAuthenticationSuccessful(t_AboutAnnouncementDetails.getServiceName());
    }

    if (m_SupportsEcdheNull)
    {
        isPeerAuthenticationSuccessful = isPeerAuthenticationSuccessful ||
            m_ECDHENullHandlerImpl->isPeerAuthenticationSuccessful(t_AboutAnnouncementDetails.getServiceName());
    }

    if (m_SupportsEcdhePsk)
    {
        isPeerAuthenticationSuccessful = isPeerAuthenticationSuccessful ||
            m_ECDHEPskHandlerImpl->isPeerAuthenticationSuccessful(t_AboutAnnouncementDetails.getServiceName());
    }

    if (m_SupportsEcdheEcdsa)
    {
        isPeerAuthenticationSuccessful = isPeerAuthenticationSuccessful ||
            m_ECDHEEcdsaHandlerImpl->isPeerAuthenticationSuccessful(t_AboutAnnouncementDetails.getServiceName());
    }

    if (m_SupportsEcdheSpeke)
    {
        isPeerAuthenticationSuccessful = isPeerAuthenticationSuccessful ||
            m_ECDHESpekeHandlerImpl->isPeerAuthenticationSuccessful(t_AboutAnnouncementDetails.getServiceName());
    }

    return isPeerAuthenticationSuccessful;
}

void ServiceHelper::clearQueuedDeviceAnnouncements()
{
    m_DeviceAnnouncementHandler->clearQueuedDeviceAnnouncements();
}

void ServiceHelper::clearPeerAuthenticationFlags(const AboutAnnouncementDetails& t_AboutAnnouncementDetails)
{
    if (m_SupportsSrpKeyX)
    {
        m_SrpKeyXHandlerImpl->resetAuthentication(t_AboutAnnouncementDetails.getServiceName());
    }
    
    if (m_SupportsSrpLogon)
    {
        m_SrpLogonHandlerImpl->resetAuthentication(t_AboutAnnouncementDetails.getServiceName());
    }
    
    if (m_SupportsEcdheNull)
    {
        m_ECDHENullHandlerImpl->resetAuthentication(t_AboutAnnouncementDetails.getServiceName());
    }

    if (m_SupportsEcdhePsk)
    {
        m_ECDHEPskHandlerImpl->resetAuthentication(t_AboutAnnouncementDetails.getServiceName());
    }
    
    if (m_SupportsEcdheEcdsa)
    {
        m_ECDHEEcdsaHandlerImpl->resetAuthentication(t_AboutAnnouncementDetails.getServiceName());
    }
    
    if (m_SupportsEcdheSpeke)
    {
        m_ECDHESpekeHandlerImpl->resetAuthentication(t_AboutAnnouncementDetails.getServiceName());
    }
}

OnboardingClient* ServiceHelper::connectOnboardingClient(const AboutAnnouncementDetails& t_AboutAnnouncementDetails)
{
    LOG(INFO) << "Creating OnboardingClient for serviceName: " << t_AboutAnnouncementDetails.getServiceName()
        << "; port: " << t_AboutAnnouncementDetails.getPort() << "; deviceName: "
        << t_AboutAnnouncementDetails.getDeviceName();

    joinSession(t_AboutAnnouncementDetails.getServiceName(), t_AboutAnnouncementDetails.getPort());

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

ExtendedSecurityApplicationProxy* ServiceHelper::connectSecurityApplicationProxy(const AboutAnnouncementDetails& t_AboutAnnouncementDetails)
{
    LOG(INFO) << "Creating SecurityApplicationProxy for serviceName: " << t_AboutAnnouncementDetails.getServiceName()
        << "; port: " << t_AboutAnnouncementDetails.getPort()
        << "; deviceName: " << t_AboutAnnouncementDetails.getDeviceName();

    joinSession(t_AboutAnnouncementDetails.getServiceName(), t_AboutAnnouncementDetails.getPort());

    ExtendedSecurityApplicationProxy* securityApplicationProxy = new ExtendedSecurityApplicationProxy(*m_BusAttachmentMgr->getBusAttachment(),
        t_AboutAnnouncementDetails.getServiceName().c_str(), 0);

    return securityApplicationProxy;
}