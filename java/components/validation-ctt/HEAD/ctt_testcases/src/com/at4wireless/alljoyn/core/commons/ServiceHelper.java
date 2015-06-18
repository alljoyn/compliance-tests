/*
 *  *    Copyright (c) Open Connectivity Foundation (OCF) and AllJoyn Open
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
 */
package com.at4wireless.alljoyn.core.commons;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.alljoyn.about.AboutService;
import org.alljoyn.about.AboutServiceImpl;
import org.alljoyn.about.client.AboutClient;
import org.alljoyn.about.client.AboutClientImpl;
import org.alljoyn.about.icon.AboutIconClient;
import org.alljoyn.bus.BusAttachment;
import org.alljoyn.bus.BusAttachment.RemoteMessage;
import org.alljoyn.bus.BusException;
import org.alljoyn.bus.BusObject;
import org.alljoyn.bus.Mutable.IntegerValue;
import org.alljoyn.bus.ProxyBusObject;
import org.alljoyn.bus.Status;
import org.alljoyn.config.ConfigService;
import org.alljoyn.config.ConfigServiceImpl;
import org.alljoyn.config.client.ConfigClient;
import org.alljoyn.ns.NotificationReceiver;
import org.alljoyn.ns.NotificationSender;
import org.alljoyn.ns.NotificationService;
import org.alljoyn.ns.NotificationServiceException;
import org.alljoyn.onboarding.client.OnboardingClient;
import org.alljoyn.onboarding.client.OnboardingClientImpl;
import org.alljoyn.services.common.ClientBase;
import org.alljoyn.services.common.PropertyStore;
import org.alljoyn.services.common.ServiceAvailabilityListener;
import org.alljoyn.services.common.utils.GenericLogger;

import com.at4wireless.alljoyn.core.about.AboutAnnouncementDetails;
import com.at4wireless.alljoyn.core.commons.log.Logger;
import com.at4wireless.alljoyn.core.commons.log.WindowsLoggerImpl;
import com.at4wireless.alljoyn.core.introspection.BusIntrospector;
import com.at4wireless.alljoyn.core.introspection.XmlBasedBusIntrospector;



// TODO: Auto-generated Javadoc
/**
 * The Class ServiceHelper.
 */
public class ServiceHelper
{
    
    /** The TAG. */
    protected static final String TAG = "ServiceHelper";
	
	/** The logger. */
	private  final WindowsLoggerImpl logger =  new WindowsLoggerImpl(TAG);
    
    /** The Constant LINK_TIMEOUT_IN_SECONDS. */
    private static final int LINK_TIMEOUT_IN_SECONDS = 120;
    
    /** The Constant AUTH_MECHANISMS. */
    private static final String[] AUTH_MECHANISMS = new String[]
    { "ALLJOYN_SRP_KEYX", "ALLJOYN_PIN_KEYX", "ALLJOYN_ECDHE_PSK" };
    
    /** The about service. */
    private AboutService aboutService;
    
    /** The notification service. */
    private NotificationService notificationService;
    
    /** The device announcement handler. */
    private DeviceAnnouncementHandler deviceAnnouncementHandler;
    
    /** The generic logger. */
    private GenericLogger genericLogger;
    
    /** The auth listener. */
    private SrpAnonymousKeyListener authListener;
    
    /** The config service. */
    private ConfigService configService;
    
    /** The bus attachment mgr. */
    private BusAttachmentMgr busAttachmentMgr;
    /** The auth password handler impl. */
   private AuthPasswordHandlerImpl authPasswordHandlerImpl;
    
    /** The password store. */
    private PasswordStore passwordStore;

    /**
     * Instantiates a new service helper.
     */
    public ServiceHelper()
    {
        
    }

    /**
     * Initialize.
     *
     * @param busApplicationName the bus application name
     * @param deviceId the device id
     * @param appId the app id
     * @throws Exception the exception
     */
    public void initialize(String busApplicationName, String deviceId, UUID appId) throws Exception
    {
        busAttachmentMgr = getBusAttachmentMgr();

        passwordStore = getPasswordStore();
        authPasswordHandlerImpl = getAuthPasswordHandlerImpl();

        busAttachmentMgr.create(busApplicationName, RemoteMessage.Receive);
        busAttachmentMgr.connect();

        aboutService = getAboutService();
        configService = getConfigService();
        startAboutClient();

        logger.info("Adding AnnouncementHandler for About");
        deviceAnnouncementHandler = getDeviceAnnouncementHandler(deviceId, appId);
        aboutService.addAnnouncementHandler(deviceAnnouncementHandler, null);

        busAttachmentMgr.advertise();
    }

    /**
     * Inits the notification receiver.
     *
     * @param receiver the receiver
     * @throws NotificationServiceException the notification service exception
     */
    public void initNotificationReceiver(NotificationReceiver receiver) throws NotificationServiceException
    {
        notificationService = getNotificationService();
        logger.info("Calling initReceive on NotificationService");
        notificationService.initReceive(busAttachmentMgr.getBusAttachment(), receiver);
    }

    /**
     * Inits the notification sender.
     *
     * @param propertyStore the property store
     * @return the notification sender
     * @throws NotificationServiceException the notification service exception
     */
    public NotificationSender initNotificationSender(PropertyStore propertyStore) throws NotificationServiceException
    {
        notificationService = getNotificationService();
        logger.info("Calling initReceive on NotificationService");
        return notificationService.initSend(busAttachmentMgr.getBusAttachment(), propertyStore);
    }

    /**
     * Start about server.
     *
     * @param port the port
     * @param propertyStore the property store
     * @throws Exception the exception
     */
    public void startAboutServer(short port, PropertyStore propertyStore) throws Exception
    {
        if (aboutService.isServerRunning())
        {
            logger.info("Stopping AboutServer");
            aboutService.stopAboutServer();
        }

        logger.info("Starting AboutServer");
        aboutService.startAboutServer(port, propertyStore, busAttachmentMgr.getBusAttachment());
    }

    /**
     * Start about client.
     *
     * @throws Exception the exception
     */
    public void startAboutClient() throws Exception
    {
        if (aboutService.isClientRunning())
        {
            logger.info("Stopping AboutClient");
            aboutService.stopAboutClient();
        }

        logger.info("Starting AboutClient");
        aboutService.startAboutClient(busAttachmentMgr.getBusAttachment());
    }

    /**
     * Start config client.
     *
     * @throws Exception the exception
     */
    public void startConfigClient() throws Exception
    {
        if (configService.isClientRunning())
        {
            logger.info("Stopping ConfigClient");
            configService.stopConfigClient();
        }

        logger.info("Starting ConfigClient");
        configService.startConfigClient(busAttachmentMgr.getBusAttachment());
    }

    /**
     * Clear queued device announcements.
     */
    public void clearQueuedDeviceAnnouncements()
    {
        deviceAnnouncementHandler.clearQueuedDeviceAnnouncements();
    }

    /**
     * Wait for next device announcement.
     *
     * @param timeout the timeout waiting for next device announcement
     * @param unit the unit
     * @return the about announcement details
     * @throws Exception the exception
     */
    public AboutAnnouncementDetails waitForNextDeviceAnnouncement(long timeout, TimeUnit unit) throws Exception
    {
        return waitForNextDeviceAnnouncement(timeout, unit, false);
    }

    /**
     * Wait for next device announcement.
     *
     * @param timeout the timeout
     * @param unit the unit
     * @param throwException the throw exception
     * @return the about announcement details
     * @throws Exception the exception
     */
    public AboutAnnouncementDetails waitForNextDeviceAnnouncement(long timeout, TimeUnit unit, boolean throwException) throws Exception
    {
        AboutAnnouncementDetails aboutAnnouncementDetails = deviceAnnouncementHandler.waitForNextDeviceAnnouncement(timeout, unit);

        if ((aboutAnnouncementDetails == null) && (throwException))
        {
            throw new BusException("Timed out waiting for About announcement");
        }

        return aboutAnnouncementDetails;
    }

    /**
     * Wait for session to close.
     *
     * @param timeout the timeout
     * @param unit the unit
     * @throws Exception the exception
     */
    public void waitForSessionToClose(long timeout, TimeUnit unit) throws Exception
    {
        deviceAnnouncementHandler.waitForSessionToClose(timeout, unit);
        deviceAnnouncementHandler = null;
    }

    /**
     * Connect about client.
     *
     * @param aboutAnnouncementDetails the about announcement details
     * @return the about client
     * @throws Exception the exception
     */
    public AboutClient connectAboutClient(AboutAnnouncementDetails aboutAnnouncementDetails) throws Exception
    {
        return connectAboutClient(aboutAnnouncementDetails, null);
    }

    /**
     * Connect about client.
     *
     * @param aboutAnnouncementDetails the about announcement details
     * @param serviceAvailabilityListener the service availability listener
     * @return the about client
     * @throws Exception the exception
     */
    public AboutClient connectAboutClient(AboutAnnouncementDetails aboutAnnouncementDetails, ServiceAvailabilityListener serviceAvailabilityListener) throws Exception
    {
        logger.info(String.format("Creating AboutClient for serviceName: %s; port: %d; deviceName: %s", aboutAnnouncementDetails.getServiceName(),
                aboutAnnouncementDetails.getPort(), aboutAnnouncementDetails.getDeviceName()));
        AboutClient aboutClient = aboutService.createAboutClient(aboutAnnouncementDetails.getServiceName(), serviceAvailabilityListener, aboutAnnouncementDetails.getPort());

        logger.info(String.format("Connecting AboutClient for serviceName: %s", aboutAnnouncementDetails.getServiceName()));
        Status status = connectClient(aboutClient);
        if (status != Status.OK)
        {
            throw new BusException(String.format("Failed to connect AboutClient to client: %s", status));
        }
        logger.info("Session established");

        return aboutClient;
    }

    /**
     * Gets the proxy bus object.
     *
     * @param aboutClient the about client
     * @param path the path
     * @param classes the classes
     * @return the proxy bus object
     */
    public ProxyBusObject getProxyBusObject(AboutClient aboutClient, String path, Class<?>[] classes)
    {
        AboutClientImpl aboutClientImpl = (AboutClientImpl) aboutClient;
        int sessionId = aboutClient.getSessionId();
        String peerName = aboutClientImpl.getPeerName();
        logger.info(String.format("Creating ProxyBusObject for peerName: %s; sessionid: %d; path: %s", peerName, sessionId, path));
        return busAttachmentMgr.getBusAttachment().getProxyBusObject(peerName, path, sessionId, classes);
    }

    /**
     * Connect about icon client.
     *
     * @param aboutAnnouncementDetails the about announcement details
     * @return the about icon client
     * @throws Exception the exception
     */
    public AboutIconClient connectAboutIconClient(AboutAnnouncementDetails aboutAnnouncementDetails) throws Exception
    {
        logger.info(String.format("Creating AboutIconClient for serviceName: %s; port: %d; deviceName: %s", aboutAnnouncementDetails.getServiceName(),
                aboutAnnouncementDetails.getPort(), aboutAnnouncementDetails.getDeviceName()));
        AboutIconClient aboutIconClient = aboutService.createAboutIconClient(aboutAnnouncementDetails.getServiceName(), null, aboutAnnouncementDetails.getPort());

        logger.info(String.format("Connecting AboutIconClient for serviceName: %s", aboutAnnouncementDetails.getServiceName()));
        Status status = connectClient(aboutIconClient);
        if (status != Status.OK)
        {
            throw new BusException(String.format("Failed to connect AboutIconClient to client: %s", status));
        }

        return aboutIconClient;
    }

    /**
     * Connect config client.
     *
     * @param aboutAnnouncementDetails the about announcement details
     * @return the config client
     * @throws Exception the exception
     */
    public ConfigClient connectConfigClient(AboutAnnouncementDetails aboutAnnouncementDetails) throws Exception
    {
        logger.info(String.format("Creating ConfigClient for serviceName: %s; port: %d; deviceName: %s", aboutAnnouncementDetails.getServiceName(),
                aboutAnnouncementDetails.getPort(), aboutAnnouncementDetails.getDeviceName()));
        ConfigClient configClient = configService.createFeatureConfigClient(aboutAnnouncementDetails.getServiceName(), null, aboutAnnouncementDetails.getPort());

        logger.info(String.format("Connecting ConfigClient for serviceName: %s", aboutAnnouncementDetails.getServiceName()));
        Status status = connectClient(configClient);
        if (status != Status.OK)
        {
            throw new BusException(String.format("Failed to connect ConfigClient to client: %s", status));
        }
        return configClient;
    }

    /**
     * Connect onboarding client.
     *
     * @param aboutAnnouncementDetails the about announcement details
     * @return the onboarding client
     * @throws Exception the exception
     */
    public OnboardingClient connectOnboardingClient(AboutAnnouncementDetails aboutAnnouncementDetails) throws Exception
    {
        logger.info(String.format("Creating OnboardingClient for serviceName: %s; port: %d; deviceName: %s", aboutAnnouncementDetails.getServiceName(),
                aboutAnnouncementDetails.getPort(), aboutAnnouncementDetails.getDeviceName()));
        OnboardingClient onboardingClient = getOnboardingClient(aboutAnnouncementDetails.getServiceName(), busAttachmentMgr.getBusAttachment(), null,
                aboutAnnouncementDetails.getPort());

        logger.info(String.format("Connecting OnboardingClient for serviceName: %s", aboutAnnouncementDetails.getServiceName()));
        Status status = connectClient(onboardingClient);
        if (status != Status.OK)
        {
            throw new BusException(String.format("Failed to connect OnboardingClient to client: %s", status));
        }

        return onboardingClient;
    }

    /**
     * Sets the authentication password.
     *
     * @param aboutAnnouncementDetails the about announcement details
     * @param password the password
     */
    public void setAuthPassword(AboutAnnouncementDetails aboutAnnouncementDetails, char[] password)
    {
        char[] currentPasscode = passwordStore.getPassword(aboutAnnouncementDetails.getServiceName());
        if (currentPasscode == null)
        {
            currentPasscode = SrpAnonymousKeyListener.DEFAULT_PINCODE;
        }
        if (password == null)
        {
            password = SrpAnonymousKeyListener.DEFAULT_PINCODE;
        }
        logger.info(String.format("setting passcode that will be used when authenticating to %s from %s to %s ", aboutAnnouncementDetails.getServiceName(),
                Arrays.toString(currentPasscode), Arrays.toString(password)));
        passwordStore.setPassword(aboutAnnouncementDetails.getServiceName(), password);
        clearKeyStore();
    }

    /**
     * Gets the authentication password.
     *
     * @param aboutAnnouncementDetails the about announcement details
     * @return the authentication password
     */
    public char[] getAuthPassword(AboutAnnouncementDetails aboutAnnouncementDetails)
    {
        char[] currentPasscode = passwordStore.getPassword(aboutAnnouncementDetails.getServiceName());
        if (currentPasscode == null)
        {
            currentPasscode = SrpAnonymousKeyListener.DEFAULT_PINCODE;
        }
        return currentPasscode;
    }

    /**
     * Checks if is peer authentication attempted.
     *
     * @param aboutAnnouncementDetails the about announcement details
     * @return true, if is peer authentication attempted
     */
    public boolean isPeerAuthenticationAttempted(AboutAnnouncementDetails aboutAnnouncementDetails)
    {
        return authPasswordHandlerImpl.isPeerAuthenticated(aboutAnnouncementDetails.getServiceName());
    }

    /**
     * Checks if is peer authentication successful.
     *
     * @param aboutAnnouncementDetails the about announcement details
     * @return true, if is peer authentication successful
     */
    public boolean isPeerAuthenticationSuccessful(AboutAnnouncementDetails aboutAnnouncementDetails)
    {
        return authPasswordHandlerImpl.isPeerAuthenticationSuccessful(aboutAnnouncementDetails.getServiceName());
    }

    /**
     * Clear peer authentication flags.
     *
     * @param aboutAnnouncementDetails the about announcement details
     */
    public void clearPeerAuthenticationFlags(AboutAnnouncementDetails aboutAnnouncementDetails)
    {
        authPasswordHandlerImpl.resetAuthentication(aboutAnnouncementDetails.getServiceName());
    }

    /**
     * Enable authentication.
     *
     * @param keyStoreFileName the key store file name
     * @throws Exception the exception
     */
    public void enableAuthentication(String keyStoreFileName) throws Exception
    {
        authListener = getSrpAnonymousListener(authPasswordHandlerImpl, genericLogger);
        logger.info("Registering an AuthListener");
        Status status = busAttachmentMgr.getBusAttachment().registerAuthListener(authListener.getAuthMechanismsAsString(), authListener, keyStoreFileName);

        if (status != Status.OK)
        {
            throw new BusException(String.format("Call to registerAuthListener returned failure: %s", status));
        }
    }

    /**
     * Clear key store.
     */
    public void clearKeyStore()
    {
        logger.info("Calling clearKeyStore");
        busAttachmentMgr.getBusAttachment().clearKeyStore();
    }

    /**
     * Release.
     */
    public void release()
    {
        shutdownNotificationService();
        shutdownAboutService();
        shutdownConfigService();
        disconnectBusAttachment();
    }

    /**
     * Register signal handler.
     *
     * @param signalHandler the signal handler
     * @throws BusException the bus exception
     */
    public void registerSignalHandler(Object signalHandler) throws BusException
    {
        busAttachmentMgr.registerSignalHandler(signalHandler);
    }

    /**
     * Register bus object.
     *
     * @param busObject the bus object
     * @param objectPath the object path
     * @throws BusException the bus exception
     */
    public void registerBusObject(BusObject busObject, String objectPath) throws BusException
    {
        logger.info(String.format("Registering BusObject at: %s", objectPath));
        busAttachmentMgr.registerBusObject(busObject, objectPath);
    }

    /**
     * Sets the link timeout.
     *
     * @param clientBase the new link timeout
     * @throws BusException the bus exception
     */
    private void setLinkTimeout(ClientBase clientBase) throws BusException
    {
        Status linkTimeoutstatus = busAttachmentMgr.getBusAttachment().setLinkTimeout(clientBase.getSessionId(), new IntegerValue(LINK_TIMEOUT_IN_SECONDS));
        if (linkTimeoutstatus != Status.OK)
        {
            throw new BusException(String.format("Failed to set link timeout value on bus attachment for session (%d): %s", clientBase.getSessionId(), linkTimeoutstatus));
        }

    }

    /**
     * Disconnect bus attachment.
     */
    private void disconnectBusAttachment()
    {
        if (busAttachmentMgr != null)
        {
            logger.info("disconnecting BusAttachment");
            busAttachmentMgr.release();
            busAttachmentMgr = null;
        }
    }

    /**
     * Shutdown notification service.
     */
    private void shutdownNotificationService()
    {
        if (notificationService != null)
        {
            try
            {
                logger.info("Shutting down notificationService");
                notificationService.shutdown();
            }
            catch (NotificationServiceException e)
            {
                logger.error("Exception calling shutdownReceiver()", e);
            }
            notificationService = null;
        }
    }

    /**
     * Shutdown about service.
     */
    public void shutdownAboutService()
    {
        if (aboutService != null)
        {
            try
            {
                logger.info("Stopping aboutClient");
                aboutService.removeAnnouncementHandler(deviceAnnouncementHandler, null);
                aboutService.stopAboutClient();
            }
            catch (Exception e)
            {
                logger.error("Exception calling stopAboutClient()", e);
            }
            if (aboutService.isServerRunning())
            {
                try
                {
                    logger.info("Stopping aboutServer");
                    aboutService.stopAboutServer();
                }
                catch (Exception e)
                {
                    logger.error("Exception calling stopAboutClient()", e);
                }
            }
            aboutService = null;
        }
    }

    /**
     * Gets the bus attachment.
     *
     * @return the bus attachment
     */
    public BusAttachment getBusAttachment()
    {
        return busAttachmentMgr.getBusAttachment();
    }

    /**
     * Gets the bus unique name.
     *
     * @return the bus unique name
     */
    public String getBusUniqueName()
    {
        return busAttachmentMgr.getBusUniqueName();
    }

    /**
     * Shutdown config service.
     */
    private void shutdownConfigService()
    {
        if (configService != null)
        {
            try
            {
                logger.info("Stopping configClient");
                configService.stopConfigClient();
            }
            catch (Exception e)
            {
                logger.error("Exception calling stopConfigClient()", e);
            }
            configService = null;
        }

    }

    /**
     * Connect client.
     *
     * @param client the client
     * @return the status
     * @throws BusException the bus exception
     */
    private Status connectClient(ClientBase client) throws BusException
    {
        Status status = client.connect();
        if (status == Status.ALLJOYN_JOINSESSION_REPLY_ALREADY_JOINED)
        {
            logger.info("Ignoring ALLJOYN_JOINSESSION_REPLY_ALREADY_JOINED error code");
            status = Status.OK;
        }
        else if (status == Status.OK)
        {
            logger.info(String.format("Session established to peer: %s", client.getPeerName()));
            setLinkTimeout(client);
        }
        return status;
    }

    /**
     * Gets the bus attachment mgr.
     *
     * @return the bus attachment mgr
     */
    protected BusAttachmentMgr getBusAttachmentMgr()
    {
        return new BusAttachmentMgr();
    }

    /**
     * Gets the auth password handler impl.
     *
     * @return the auth password handler impl
     */
    protected AuthPasswordHandlerImpl getAuthPasswordHandlerImpl()
    {
        return new AuthPasswordHandlerImpl(passwordStore);
    }

    /**
     * Gets the password store.
     *
     * @return the password store
     */
    protected PasswordStore getPasswordStore()
    {
        return new PasswordStore();
    }

    /**
     * Gets the notification service.
     *
     * @return the notification service
     * @throws NotificationServiceException the notification service exception
     */
    protected NotificationService getNotificationService() throws NotificationServiceException
    {
        NotificationService notificationServiceImpl = NotificationService.getInstance();
        //notificationServiceImpl.setLogger(new org.alljoyn.ns.nativeplatform.AndroidLogger());
        return notificationServiceImpl;
    }

    /**
     * Gets the about service.
     *
     * @return the about service
     */
    protected AboutService getAboutService()
    {
        AboutService aboutServiceImpl = AboutServiceImpl.getInstance();
       // aboutServiceImpl.setLogger(serviceLogger);
        return aboutServiceImpl;
    }

    /**
     * Gets the config service.
     *
     * @return the config service
     */
    protected ConfigService getConfigService()
    {
        ConfigService configServiceImpl = ConfigServiceImpl.getInstance();
      //  configServiceImpl.setLogger(serviceLogger);
        return configServiceImpl;
    }

    /**
     * Gets the device announcement handler.
     *
     * @param deviceId the device id
     * @param appId the app id
     * @return the device announcement handler
     */
    protected DeviceAnnouncementHandler getDeviceAnnouncementHandler(String deviceId, UUID appId)
    {
        return new DeviceAnnouncementHandler(deviceId, appId);
    }

    /**
     * Gets the bus introspector.
     *
     * @param aboutClient the about client
     * @return the bus introspector
     */
    public BusIntrospector getBusIntrospector(AboutClient aboutClient)
    {
        return new XmlBasedBusIntrospector(busAttachmentMgr.getBusAttachment(), aboutClient.getPeerName(), aboutClient.getSessionId());
    }

    /**
     * Gets the srp anonymous listener.
     *
     * @param passwordHandler the password handler
     * @param genericLogger the generic logger
     * @return the srp anonymous listener
     */
    protected SrpAnonymousKeyListener getSrpAnonymousListener(AuthPasswordHandler passwordHandler, GenericLogger genericLogger)
    {
        return new SrpAnonymousKeyListener(passwordHandler, logger, AUTH_MECHANISMS);
    }

    /**
     * Gets the onboarding client.
     *
     * @param serviceName the service name
     * @param bus the bus
     * @param serviceAvailabilityListener the service availability listener
     * @param port the port
     * @return the onboarding client
     */
    protected OnboardingClientImpl getOnboardingClient(String serviceName, BusAttachment bus, ServiceAvailabilityListener serviceAvailabilityListener, short port)
    {
        return new OnboardingClientImpl(serviceName, bus, null, port);
    }
}