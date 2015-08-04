/*******************************************************************************
 *  Copyright AllSeen Alliance. All rights reserved.
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
package com.at4wireless.alljoyn.core.commons;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.alljoyn.about.AboutService;
import org.alljoyn.about.AboutServiceImpl;
import org.alljoyn.about.client.AboutClient;
import org.alljoyn.about.client.AboutClientImpl;
import org.alljoyn.about.icon.AboutIconClient;
import org.alljoyn.bus.AboutIconProxy;
import org.alljoyn.bus.AboutProxy;
import org.alljoyn.bus.BusAttachment;
import org.alljoyn.bus.BusAttachment.RemoteMessage;
import org.alljoyn.bus.BusException;
import org.alljoyn.bus.BusObject;
import org.alljoyn.bus.Mutable;
import org.alljoyn.bus.Mutable.IntegerValue;
import org.alljoyn.bus.ProxyBusObject;
import org.alljoyn.bus.SessionListener;
import org.alljoyn.bus.SessionOpts;
import org.alljoyn.bus.Status;
import org.alljoyn.bus.ifaces.About;
import org.alljoyn.config.ConfigService;
import org.alljoyn.config.ConfigServiceImpl;
import org.alljoyn.config.client.ConfigClient;
import org.alljoyn.ns.NotificationReceiver;
import org.alljoyn.ns.NotificationSender;
import org.alljoyn.ns.NotificationService;
import org.alljoyn.ns.NotificationServiceException;
import org.alljoyn.ns.commons.GenericLogger;
import org.alljoyn.ns.commons.NativePlatformFactoryException;
import org.alljoyn.onboarding.client.OnboardingClient;
import org.alljoyn.onboarding.client.OnboardingClientImpl;
import org.alljoyn.services.common.ClientBase;
import org.alljoyn.services.common.PropertyStore;
import org.alljoyn.services.common.ServiceAvailabilityListener;






import com.at4wireless.alljoyn.core.about.AboutAnnouncementDetails;
import com.at4wireless.alljoyn.core.commons.log.Logger;
import com.at4wireless.alljoyn.core.commons.log.WindowsLoggerImpl;
import com.at4wireless.alljoyn.core.introspection.BusIntrospector;
import com.at4wireless.alljoyn.core.introspection.XmlBasedBusIntrospector;

public class ServiceHelper
{
    protected static final String TAG = "ServiceHelper";
    //private static final Logger logger = LoggerFactory.getLogger(TAG);
	private static final WindowsLoggerImpl logger =  new WindowsLoggerImpl(TAG);
    private static final int LINK_TIMEOUT_IN_SECONDS = 120;
    private static final String[] AUTH_MECHANISMS = new String[]
    //{ "ALLJOYN_SRP_KEYX", "ALLJOYN_PIN_KEYX", "ALLJOYN_ECDHE_PSK" }; //[AT4] PIN_KEYX is deprecated
    { "ALLJOYN_SRP_KEYX", "ALLJOYN_ECDHE_PSK" };
    private AboutService aboutService;
    private NotificationService notificationService;
    private DeviceAnnouncementHandler deviceAnnouncementHandler;
    //private GenericLogger genericLogger;
    private Logger genericLogger;
    //private GenericLogger genericLogger=new GenericLoggerImp();
    private SrpAnonymousKeyListener authListener;
    private ConfigService configService;
    private BusAttachmentMgr busAttachmentMgr;
    private GenericLogger serviceLogger;
    private AuthPasswordHandlerImpl authPasswordHandlerImpl;
    private PasswordStore passwordStore;
    
    /** 
     * [AT4] Added attributes to support AboutProxy instead of AboutClient
     * 
     * sessionId
     * */
    
    Mutable.IntegerValue sessionId;
   
	public ServiceHelper(Logger logger)
    {
        this.genericLogger = logger;
    }

    public void initialize(String busApplicationName, String deviceId, UUID appId, Boolean aboutListener) throws Exception //[AT4]
    {
    	busAttachmentMgr = getBusAttachmentMgr();

    	passwordStore = getPasswordStore();
        authPasswordHandlerImpl = getAuthPasswordHandlerImpl();

        busAttachmentMgr.create(busApplicationName, RemoteMessage.Receive);
        busAttachmentMgr.connect();

        aboutService = getAboutService();
        configService = getConfigService();
        startAboutClient();
        
        /*logger.debug("Adding AnnouncementHandler for About");
        deviceAnnouncementHandler = getDeviceAnnouncementHandler(deviceId, appId);
        aboutService.addAnnouncementHandler(deviceAnnouncementHandler, null);*/
       
	    if (aboutListener) {
	    	logger.info("Adding AboutListener for About announcements");
	        deviceAnnouncementHandler = getDeviceAnnouncementHandler(deviceId, appId);
	        busAttachmentMgr.getBusAttachment().registerAboutListener(deviceAnnouncementHandler);
	       
	        /*String ifaces[] = {About.INTERFACE_NAME};
	        busAttachmentMgr.getBusAttachment().whoImplements(ifaces);*/ //[AT4] Since v14.12, all interfaces must be scanned
	        busAttachmentMgr.getBusAttachment().whoImplements(null);    
	    }
        
        busAttachmentMgr.advertise();  
    }
    
    public void initialize(String busApplicationName, String deviceId, UUID appId) throws Exception
    {
    	initialize(busApplicationName, deviceId, appId ,true); //[AT4]
    }
    
    public void initializeSender(String busApplicationName, String deviceId, UUID appId) throws Exception
    {
    	initialize(busApplicationName, deviceId, appId ,false); //[AT4]
    }
    
    public void initNotificationReceiver(NotificationReceiver receiver) throws NotificationServiceException, NativePlatformFactoryException
    {
        notificationService = getNotificationService();
        logger.info("Calling initReceive on NotificationService");
        notificationService.initReceive(busAttachmentMgr.getBusAttachment(), receiver);
    }
    
    public NotificationSender initNotificationSender(PropertyStore propertyStore) throws NotificationServiceException, NativePlatformFactoryException
    {
        notificationService = getNotificationService();
        logger.info("Calling initReceive on NotificationService");
        return notificationService.initSend(busAttachmentMgr.getBusAttachment(), propertyStore);
    }
    
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

    public ProxyBusObject getProxyBusObject(AboutClient aboutClient, String path, Class<?>[] classes)
    {
        AboutClientImpl aboutClientImpl = (AboutClientImpl) aboutClient;
        int sessionId = aboutClient.getSessionId();
        String peerName = aboutClientImpl.getPeerName();
        logger.info(String.format("Creating ProxyBusObject for peerName: %s; sessionid: %d; path: %s", peerName, sessionId, path));
        return busAttachmentMgr.getBusAttachment().getProxyBusObject(peerName, path, sessionId, classes);
    }

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
        authListener = getSrpAnonymousListener(authPasswordHandlerImpl, logger);
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
    	logger.info(String.format("Registering BusObject: "+busObject.toString()+" at: %s", objectPath));
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
        	//logger.info("disconnecting BusAttachment"); //[AT4] Duplicated message
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

    public void shutdownAboutService()
    {
        if (aboutService != null)
        {
            try
            {
            	logger.info("Stopping aboutClient");
                //aboutService.removeAnnouncementHandler(deviceAnnouncementHandler, null); [AT4]
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

    protected NotificationService getNotificationService() throws NotificationServiceException, NativePlatformFactoryException
    {
        NotificationService notificationServiceImpl = NotificationService.getInstance();
        notificationServiceImpl.setLogger(new GenericLoggerImp());
        return notificationServiceImpl;
    }

    protected AboutService getAboutService()
    {
        AboutService aboutServiceImpl = AboutServiceImpl.getInstance();
        return aboutServiceImpl;
    }

    protected ConfigService getConfigService()
    {
        ConfigService configServiceImpl = ConfigServiceImpl.getInstance();
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

    //protected SrpAnonymousKeyListener getSrpAnonymousListener(AuthPasswordHandler passwordHandler, GenericLogger genericLogger)
    protected SrpAnonymousKeyListener getSrpAnonymousListener(AuthPasswordHandler passwordHandler, Logger genericLogger)
    {
        return new SrpAnonymousKeyListener(passwordHandler, genericLogger, AUTH_MECHANISMS);
    }

    protected OnboardingClientImpl getOnboardingClient(String serviceName, BusAttachment bus, ServiceAvailabilityListener serviceAvailabilityListener, short port)
    {
        return new OnboardingClientImpl(serviceName, bus, null, port);
    }
    
    /** 
     * [AT4] Added methods to support AboutProxy instead of AboutClient
     * 
     * connectAboutProxy
     * connectAboutIconProxy
     * joinSession
     * getProxyBusObject
     * getBusIntrospector
     * 
     * */
    
    public AboutProxy connectAboutProxy(AboutAnnouncementDetails aboutAnnouncementDetails) throws Exception
    {
		logger.info(String.format("Creating AboutProxy for serviceName: %s; port: %d; deviceName: %s", 
			aboutAnnouncementDetails.getServiceName(),
            aboutAnnouncementDetails.getPort(),
            aboutAnnouncementDetails.getDeviceName()));
    	
    	joinSession(aboutAnnouncementDetails.getServiceName(),aboutAnnouncementDetails.getPort());
    	
    	AboutProxy aboutProxy = new AboutProxy(getBusAttachment(), aboutAnnouncementDetails.getServiceName(), sessionId.value);
    	/*if(aboutProxy!=null){
    		logger.info("AboutProxy connected");
    		logger.info("Partial Verdict: PASS");
    	}else {
    		logger.error("AboutProxy not connected");
    		logger.info("Partial Verdict: FAIL");
    	}*/
    	logger.info("AboutProxy connected");

        return aboutProxy;
    }

    public AboutIconProxy connectAboutIconProxy(AboutAnnouncementDetails aboutAnnouncementDetails) throws Exception
    {
    	logger.info(String.format("Creating AboutIconProxy for serviceName: %s; port: %d; deviceName: %s", 
    			aboutAnnouncementDetails.getServiceName(),
                aboutAnnouncementDetails.getPort(),
                aboutAnnouncementDetails.getDeviceName()));
    	
    	joinSession(aboutAnnouncementDetails.getServiceName(), aboutAnnouncementDetails.getPort());
    	
    	AboutIconProxy aboutProxy = new AboutIconProxy(getBusAttachment(), aboutAnnouncementDetails.getServiceName(), sessionId.value);
    	/*if(aboutProxy!=null){
    		logger.info("AboutIconProxy connected");
    	}else{
    		logger.error("AboutIconProxy not connected");
    	}*/
    	logger.info("AboutIconProxy connected");

        return aboutProxy;
    }

    public void joinSession(String busName, Short port){
    	  	   
        SessionOpts sessionOpts = new SessionOpts();
        sessionOpts.traffic = SessionOpts.TRAFFIC_MESSAGES;
        sessionOpts.isMultipoint = false;
        sessionOpts.proximity = SessionOpts.PROXIMITY_ANY;
        sessionOpts.transports = SessionOpts.TRANSPORT_ANY;
        sessionId = new Mutable.IntegerValue();
        getBusAttachment().enableConcurrentCallbacks();
      
        Status status = getBusAttachment().joinSession(busName, port, sessionId, sessionOpts, new SessionListener());
        if (status != Status.OK) {
            return;
        }
        else
        {
        	logger.info("Session joined");
        }
    }
        
    public ProxyBusObject getProxyBusObject(AboutAnnouncementDetails announcement, String path, Class<?>[] classes) throws Exception
    {
        int sessionId = this.sessionId.value;

        String peerName = announcement.getServiceName();
        logger.info(String.format("Creating ProxyBusObject for peerName: %s; sessionid: %d; path: %s", peerName, sessionId, path));
        return busAttachmentMgr.getBusAttachment().getProxyBusObject(peerName, path, sessionId, classes);
    }
    
    public BusIntrospector getBusIntrospector(AboutAnnouncementDetails deviceAboutAnnouncement)
    {
        return new XmlBasedBusIntrospector(busAttachmentMgr.getBusAttachment(), deviceAboutAnnouncement.getServiceName(), sessionId.value);
    }
}