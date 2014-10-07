/*******************************************************************************
 *  Copyright (c) 2013 - 2014, AllSeen Alliance. All rights reserved.
 *
 *     Permission to use, copy, modify, and/or distribute this software for any
 *     purpose with or without fee is hereby granted, provided that the above
 *     copyright notice and this permission notice appear in all copies.
 *
 *     THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 *     WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 *     MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 *     ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 *     WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 *     ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 *     OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 *******************************************************************************/
package org.alljoyn.validation.testing.utils.services;

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
import org.alljoyn.services.android.security.AuthPasswordHandler;
import org.alljoyn.services.android.security.SrpAnonymousKeyListener;
import org.alljoyn.services.android.utils.AndroidLogger;
import org.alljoyn.services.common.ClientBase;
import org.alljoyn.services.common.PropertyStore;
import org.alljoyn.services.common.ServiceAvailabilityListener;
import org.alljoyn.services.common.utils.GenericLogger;
import org.alljoyn.validation.framework.utils.introspection.BusIntrospector;
import org.alljoyn.validation.framework.utils.introspection.XmlBasedBusIntrospector;
import org.alljoyn.validation.testing.utils.about.AboutAnnouncementDetails;
import org.alljoyn.validation.testing.utils.bus.BusAttachmentMgr;
import org.alljoyn.validation.testing.utils.log.Logger;
import org.alljoyn.validation.testing.utils.log.LoggerFactory;

public class ServiceHelper
{
    protected static final String TAG = "ServiceHelper";
    private static final Logger logger = LoggerFactory.getLogger(TAG);
    private static final int LINK_TIMEOUT_IN_SECONDS = 120;
    private static final String[] AUTH_MECHANISMS = new String[]
    { "ALLJOYN_SRP_KEYX", "ALLJOYN_PIN_KEYX", "ALLJOYN_ECDHE_PSK" };
    private AboutService aboutService;
    private NotificationService notificationService;
    private DeviceAnnouncementHandler deviceAnnouncementHandler;
    private GenericLogger genericLogger;
    private SrpAnonymousKeyListener authListener;
    private ConfigService configService;
    private BusAttachmentMgr busAttachmentMgr;
    private GenericLogger serviceLogger = new AndroidLogger();
    private AuthPasswordHandlerImpl authPasswordHandlerImpl;
    private PasswordStore passwordStore;

    public ServiceHelper(GenericLogger logger)
    {
        this.genericLogger = logger;
    }

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

        logger.debug("Adding AnnouncementHandler for About");
        deviceAnnouncementHandler = getDeviceAnnouncementHandler(deviceId, appId);
        aboutService.addAnnouncementHandler(deviceAnnouncementHandler, null);

        busAttachmentMgr.advertise();
    }

    public void initNotificationReceiver(NotificationReceiver receiver) throws NotificationServiceException
    {
        notificationService = getNotificationService();
        logger.debug("Calling initReceive on NotificationService");
        notificationService.initReceive(busAttachmentMgr.getBusAttachment(), receiver);
    }

    public NotificationSender initNotificationSender(PropertyStore propertyStore) throws NotificationServiceException
    {
        notificationService = getNotificationService();
        logger.debug("Calling initReceive on NotificationService");
        return notificationService.initSend(busAttachmentMgr.getBusAttachment(), propertyStore);
    }

    public void startAboutServer(short port, PropertyStore propertyStore) throws Exception
    {
        if (aboutService.isServerRunning())
        {
            logger.debug("Stopping AboutServer");
            aboutService.stopAboutServer();
        }

        logger.debug("Starting AboutServer");
        aboutService.startAboutServer(port, propertyStore, busAttachmentMgr.getBusAttachment());
    }

    public void startAboutClient() throws Exception
    {
        if (aboutService.isClientRunning())
        {
            logger.debug("Stopping AboutClient");
            aboutService.stopAboutClient();
        }

        logger.debug("Starting AboutClient");
        aboutService.startAboutClient(busAttachmentMgr.getBusAttachment());
    }

    public void startConfigClient() throws Exception
    {
        if (configService.isClientRunning())
        {
            logger.debug("Stopping ConfigClient");
            configService.stopConfigClient();
        }

        logger.debug("Starting ConfigClient");
        configService.startConfigClient(busAttachmentMgr.getBusAttachment());
    }

    public void clearQueuedDeviceAnnouncements()
    {
        deviceAnnouncementHandler.clearQueuedDeviceAnnouncements();
    }

    public AboutAnnouncementDetails waitForNextDeviceAnnouncement(long timeout, TimeUnit unit) throws Exception
    {
        return waitForNextDeviceAnnouncement(timeout, unit, false);
    }

    public AboutAnnouncementDetails waitForNextDeviceAnnouncement(long timeout, TimeUnit unit, boolean throwException) throws Exception
    {
        AboutAnnouncementDetails aboutAnnouncementDetails = deviceAnnouncementHandler.waitForNextDeviceAnnouncement(timeout, unit);

        if ((aboutAnnouncementDetails == null) && (throwException))
        {
            throw new BusException("Timed out waiting for About announcement");
        }

        return aboutAnnouncementDetails;
    }

    public void waitForSessionToClose(long timeout, TimeUnit unit) throws Exception
    {
        deviceAnnouncementHandler.waitForSessionToClose(timeout, unit);
        deviceAnnouncementHandler = null;
    }

    public AboutClient connectAboutClient(AboutAnnouncementDetails aboutAnnouncementDetails) throws Exception
    {
        return connectAboutClient(aboutAnnouncementDetails, null);
    }

    public AboutClient connectAboutClient(AboutAnnouncementDetails aboutAnnouncementDetails, ServiceAvailabilityListener serviceAvailabilityListener) throws Exception
    {
        logger.debug(String.format("Creating AboutClient for serviceName: %s; port: %d; deviceName: %s", aboutAnnouncementDetails.getServiceName(),
                aboutAnnouncementDetails.getPort(), aboutAnnouncementDetails.getDeviceName()));
        AboutClient aboutClient = aboutService.createAboutClient(aboutAnnouncementDetails.getServiceName(), serviceAvailabilityListener, aboutAnnouncementDetails.getPort());

        logger.debug(String.format("Connecting AboutClient for serviceName: %s", aboutAnnouncementDetails.getServiceName()));
        Status status = connectClient(aboutClient);
        if (status != Status.OK)
        {
            throw new BusException(String.format("Failed to connect AboutClient to client: %s", status));
        }
        logger.debug("Session established");

        return aboutClient;
    }

    public ProxyBusObject getProxyBusObject(AboutClient aboutClient, String path, Class<?>[] classes)
    {
        AboutClientImpl aboutClientImpl = (AboutClientImpl) aboutClient;
        int sessionId = aboutClient.getSessionId();
        String peerName = aboutClientImpl.getPeerName();
        logger.debug(String.format("Creating ProxyBusObject for peerName: %s; sessionid: %d; path: %s", peerName, sessionId, path));
        return busAttachmentMgr.getBusAttachment().getProxyBusObject(peerName, path, sessionId, classes);
    }

    public AboutIconClient connectAboutIconClient(AboutAnnouncementDetails aboutAnnouncementDetails) throws Exception
    {
        logger.debug(String.format("Creating AboutIconClient for serviceName: %s; port: %d; deviceName: %s", aboutAnnouncementDetails.getServiceName(),
                aboutAnnouncementDetails.getPort(), aboutAnnouncementDetails.getDeviceName()));
        AboutIconClient aboutIconClient = aboutService.createAboutIconClient(aboutAnnouncementDetails.getServiceName(), null, aboutAnnouncementDetails.getPort());

        logger.debug(String.format("Connecting AboutIconClient for serviceName: %s", aboutAnnouncementDetails.getServiceName()));
        Status status = connectClient(aboutIconClient);
        if (status != Status.OK)
        {
            throw new BusException(String.format("Failed to connect AboutIconClient to client: %s", status));
        }

        return aboutIconClient;
    }

    public ConfigClient connectConfigClient(AboutAnnouncementDetails aboutAnnouncementDetails) throws Exception
    {
        logger.debug(String.format("Creating ConfigClient for serviceName: %s; port: %d; deviceName: %s", aboutAnnouncementDetails.getServiceName(),
                aboutAnnouncementDetails.getPort(), aboutAnnouncementDetails.getDeviceName()));
        ConfigClient configClient = configService.createFeatureConfigClient(aboutAnnouncementDetails.getServiceName(), null, aboutAnnouncementDetails.getPort());

        logger.debug(String.format("Connecting ConfigClient for serviceName: %s", aboutAnnouncementDetails.getServiceName()));
        Status status = connectClient(configClient);
        if (status != Status.OK)
        {
            throw new BusException(String.format("Failed to connect ConfigClient to client: %s", status));
        }
        return configClient;
    }

    public OnboardingClient connectOnboardingClient(AboutAnnouncementDetails aboutAnnouncementDetails) throws Exception
    {
        logger.debug(String.format("Creating OnboardingClient for serviceName: %s; port: %d; deviceName: %s", aboutAnnouncementDetails.getServiceName(),
                aboutAnnouncementDetails.getPort(), aboutAnnouncementDetails.getDeviceName()));
        OnboardingClient onboardingClient = getOnboardingClient(aboutAnnouncementDetails.getServiceName(), busAttachmentMgr.getBusAttachment(), null,
                aboutAnnouncementDetails.getPort());

        logger.debug(String.format("Connecting OnboardingClient for serviceName: %s", aboutAnnouncementDetails.getServiceName()));
        Status status = connectClient(onboardingClient);
        if (status != Status.OK)
        {
            throw new BusException(String.format("Failed to connect OnboardingClient to client: %s", status));
        }

        return onboardingClient;
    }

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
        logger.debug(String.format("setting passcode that will be used when authenticating to %s from %s to %s ", aboutAnnouncementDetails.getServiceName(),
                Arrays.toString(currentPasscode), Arrays.toString(password)));
        passwordStore.setPassword(aboutAnnouncementDetails.getServiceName(), password);
        clearKeyStore();
    }

    public char[] getAuthPassword(AboutAnnouncementDetails aboutAnnouncementDetails)
    {
        char[] currentPasscode = passwordStore.getPassword(aboutAnnouncementDetails.getServiceName());
        if (currentPasscode == null)
        {
            currentPasscode = SrpAnonymousKeyListener.DEFAULT_PINCODE;
        }
        return currentPasscode;
    }

    public boolean isPeerAuthenticationAttempted(AboutAnnouncementDetails aboutAnnouncementDetails)
    {
        return authPasswordHandlerImpl.isPeerAuthenticated(aboutAnnouncementDetails.getServiceName());
    }

    public boolean isPeerAuthenticationSuccessful(AboutAnnouncementDetails aboutAnnouncementDetails)
    {
        return authPasswordHandlerImpl.isPeerAuthenticationSuccessful(aboutAnnouncementDetails.getServiceName());
    }

    public void clearPeerAuthenticationFlags(AboutAnnouncementDetails aboutAnnouncementDetails)
    {
        authPasswordHandlerImpl.resetAuthentication(aboutAnnouncementDetails.getServiceName());
    }

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

    public void clearKeyStore()
    {
        logger.info("Calling clearKeyStore");
        busAttachmentMgr.getBusAttachment().clearKeyStore();
    }

    public void release()
    {
        shutdownNotificationService();
        shutdownAboutService();
        shutdownConfigService();
        disconnectBusAttachment();
    }

    public void registerSignalHandler(Object signalHandler) throws BusException
    {
        busAttachmentMgr.registerSignalHandler(signalHandler);
    }

    public void registerBusObject(BusObject busObject, String objectPath) throws BusException
    {
        logger.debug(String.format("Registering BusObject at: %s", objectPath));
        busAttachmentMgr.registerBusObject(busObject, objectPath);
    }

    private void setLinkTimeout(ClientBase clientBase) throws BusException
    {
        Status linkTimeoutstatus = busAttachmentMgr.getBusAttachment().setLinkTimeout(clientBase.getSessionId(), new IntegerValue(LINK_TIMEOUT_IN_SECONDS));
        if (linkTimeoutstatus != Status.OK)
        {
            throw new BusException(String.format("Failed to set link timeout value on bus attachment for session (%d): %s", clientBase.getSessionId(), linkTimeoutstatus));
        }

    }

    private void disconnectBusAttachment()
    {
        if (busAttachmentMgr != null)
        {
            logger.debug("disconnecting BusAttachment");
            busAttachmentMgr.release();
            busAttachmentMgr = null;
        }
    }

    private void shutdownNotificationService()
    {
        if (notificationService != null)
        {
            try
            {
                logger.debug("Shutting down notificationService");
                notificationService.shutdown();
            }
            catch (NotificationServiceException e)
            {
                logger.error("Exception calling shutdownReceiver()", e);
            }
            notificationService = null;
        }
    }

    public void shutdownAboutService()
    {
        if (aboutService != null)
        {
            try
            {
                logger.debug("Stopping aboutClient");
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
                    logger.debug("Stopping aboutServer");
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

    public BusAttachment getBusAttachment()
    {
        return busAttachmentMgr.getBusAttachment();
    }

    public String getBusUniqueName()
    {
        return busAttachmentMgr.getBusUniqueName();
    }

    private void shutdownConfigService()
    {
        if (configService != null)
        {
            try
            {
                logger.debug("Stopping configClient");
                configService.stopConfigClient();
            }
            catch (Exception e)
            {
                logger.error("Exception calling stopConfigClient()", e);
            }
            configService = null;
        }

    }

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

    protected BusAttachmentMgr getBusAttachmentMgr()
    {
        return new BusAttachmentMgr();
    }

    protected AuthPasswordHandlerImpl getAuthPasswordHandlerImpl()
    {
        return new AuthPasswordHandlerImpl(passwordStore);
    }

    protected PasswordStore getPasswordStore()
    {
        return new PasswordStore();
    }

    protected NotificationService getNotificationService() throws NotificationServiceException
    {
        NotificationService notificationServiceImpl = NotificationService.getInstance();
        notificationServiceImpl.setLogger(new org.alljoyn.ns.nativeplatform.AndroidLogger());
        return notificationServiceImpl;
    }

    protected AboutService getAboutService()
    {
        AboutService aboutServiceImpl = AboutServiceImpl.getInstance();
        aboutServiceImpl.setLogger(serviceLogger);
        return aboutServiceImpl;
    }

    protected ConfigService getConfigService()
    {
        ConfigService configServiceImpl = ConfigServiceImpl.getInstance();
        configServiceImpl.setLogger(serviceLogger);
        return configServiceImpl;
    }

    protected DeviceAnnouncementHandler getDeviceAnnouncementHandler(String deviceId, UUID appId)
    {
        return new DeviceAnnouncementHandler(deviceId, appId);
    }

    public BusIntrospector getBusIntrospector(AboutClient aboutClient)
    {
        return new XmlBasedBusIntrospector(busAttachmentMgr.getBusAttachment(), aboutClient.getPeerName(), aboutClient.getSessionId());
    }

    protected SrpAnonymousKeyListener getSrpAnonymousListener(AuthPasswordHandler passwordHandler, GenericLogger genericLogger)
    {
        return new SrpAnonymousKeyListener(passwordHandler, genericLogger, AUTH_MECHANISMS);
    }

    protected OnboardingClientImpl getOnboardingClient(String serviceName, BusAttachment bus, ServiceAvailabilityListener serviceAvailabilityListener, short port)
    {
        return new OnboardingClientImpl(serviceName, bus, null, port);
    }
}