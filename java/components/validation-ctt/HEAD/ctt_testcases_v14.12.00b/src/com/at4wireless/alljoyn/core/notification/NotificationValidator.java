/*
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
 */
package com.at4wireless.alljoyn.core.notification;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import org.alljoyn.bus.ErrorReplyBusException;
import org.alljoyn.ns.Notification;
import org.alljoyn.ns.NotificationMessageType;
import org.alljoyn.ns.NotificationReceiver;
import org.alljoyn.ns.RichAudioUrl;

import com.at4wireless.alljoyn.core.about.AboutAnnouncementDetails;
import com.at4wireless.alljoyn.core.commons.log.WindowsLoggerImpl;
import com.at4wireless.alljoyn.core.introspection.BusIntrospector;



// TODO: Auto-generated Javadoc
/**
 * The Class NotificationValidator.
 */
public class NotificationValidator implements NotificationReceiver, Runnable
{
	
    
    /** The IXIT n_ notification version. */
    private int IXITN_NotificationVersion;
    
    /** The ICS n_ rich icon url. */
    private boolean ICSN_RichIconUrl;
    
    /** The ICS n_ rich audio url. */
    private boolean  ICSN_RichAudioUrl;
    
    /** The ICS n_ resp object path. */
    private boolean  ICSN_RespObjectPath;
    
    /** The Constant TAG. */
    private static final String TAG = "NotificationValidator";
	
	/** The Constant logger. */
	private static final WindowsLoggerImpl logger =  new WindowsLoggerImpl(TAG);
    
    /** The notification counter. */
    private AtomicInteger notificationCounter = new AtomicInteger();
    
    /** The bus introspector. */
    private BusIntrospector busIntrospector;
    
    /** The device id. */
    String deviceId;
    
    /** The app id. */
    UUID appId;
    
    /** The notification validation exception handler. */
    private NotificationValidationExceptionHandler notificationValidationExceptionHandler;
    
    /** The device about announcement. */
    private AboutAnnouncementDetails deviceAboutAnnouncement;
    
    /** The notification handler. */
    private NotificationHandler notificationHandler;

    /**
     * The Interface NotificationValidationExceptionHandler.
     */
    public interface NotificationValidationExceptionHandler
    {
        
        /**
         * On notification validation exception.
         *
         * @param exception the exception
         */
        void onNotificationValidationException(Exception exception);
    }

    /**
     * Instantiates a new notification validator.
     *
     * @param dutDeviceId the dut device id
     * @param dutAppIddeviceId the dut app iddevice id
     */
    public NotificationValidator(String dutDeviceId, UUID dutAppIddeviceId)
    {
    	deviceId=dutDeviceId;
    			appId=	dutAppIddeviceId;
        notificationHandler = getNotificationHandler();
        notificationHandler.initializeForDevice(dutDeviceId, dutAppIddeviceId);
   
    }

    /**
     * Gets the notification handler.
     *
     * @return the notification handler
     */
    protected NotificationHandler getNotificationHandler()
    {
        return new NotificationHandler();
    }

    /**
     * Initialize for device.
     *
     * @param deviceAboutAnnouncement the device about announcement
     * @param busIntrospector the bus introspector
     * @param notificationValidationExceptionHandler the notification validation exception handler
     */
    public void initializeForDevice(AboutAnnouncementDetails deviceAboutAnnouncement, BusIntrospector busIntrospector,
            NotificationValidationExceptionHandler notificationValidationExceptionHandler)
    {
        this.deviceAboutAnnouncement = deviceAboutAnnouncement;
        this.busIntrospector = busIntrospector;
        this.notificationValidationExceptionHandler = notificationValidationExceptionHandler;
    }

    /* (non-Javadoc)
     * @see org.alljoyn.ns.NotificationReceiver#receive(org.alljoyn.ns.Notification)
     */
    @Override
    public void receive(Notification notification)
    {
        notificationHandler.receive(notification);
    }

    /* (non-Javadoc)
     * @see org.alljoyn.ns.NotificationReceiver#dismiss(int, java.util.UUID)
     */
    @Override
    public void dismiss(int arg0, UUID arg1)
    {
    }

    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run()
    {
        Notification notification;

        try
        {
            while ((notification = notificationHandler.getReceivedNotification()) != null)
            {
                logger.info(String.format("Validating notification from DUT with messageId: %d", notification.getMessageId()));

                try
                {
                	 notificationCounter.getAndIncrement();
                    boolean includedRichIconUrl = false;
                    boolean includedRichAudioUrl = false;
                    boolean includesResponseObjectPath = false;

                    assertEquals(String.format("Notification version received: "+notification.getVersion()+" does not match IXIT: "
                    		+IXITN_NotificationVersion), IXITN_NotificationVersion, notification.getVersion());
                    logger.info("Notification version received: "+notification.getVersion()+" matches IXIT: "
                    		+IXITN_NotificationVersion);
                    logger.info("Partial Verdict: PASS");
                    //String notifAppName = notification.getAppName();
                    assertEquals("AppName parameter received in notification: "+notification.getAppName()+" does not match About Announcement parameter: "
                    		+deviceAboutAnnouncement.getAppName(), deviceAboutAnnouncement.getAppName(), notification.getAppName());
                    logger.info("AppName parameter received: "+notification.getAppName()+" matches About Announcement parameter: "
                    		+deviceAboutAnnouncement.getAppName());
                    logger.info("Partial Verdict: PASS");
                    //String notifdeviceName = notification.getDeviceName();
                    assertEquals("DeviceName parameter received in notification: "+notification.getDeviceName()+" does not match About Announcement parameter: "
                    		+deviceAboutAnnouncement.getDeviceName(), deviceAboutAnnouncement.getDeviceName(), notification.getDeviceName());
                    logger.info("DeviceName parameter received in notification: "+notification.getDeviceName()+" matches About Announcement parameter: "
                    		+deviceAboutAnnouncement.getDeviceName());
                    logger.info("Partial Verdict: PASS");
                    assertNotNull("Notification messageType value not valid!", notification.getMessageType());
                    logger.info("Valid notification messageType value: "+notification.getMessageType());
                    logger.info("Partial Verdict: PASS");
                    logger.info("The received Message Id: " + notification.getMessageId());

                    String richIconUrlString = notification.getRichIconUrl();

                    if (ICSN_RichIconUrl && richIconUrlString != null)
                    {
                        try
                        {
                            logger.info("Rich icon URL: '" + richIconUrlString + "'");
                            new URL(richIconUrlString);
                            includedRichIconUrl = true;
                        }
                        catch (MalformedURLException e)
                        {
                            throw new Exception("RichIconUrl malformed: '" + richIconUrlString + "'", e);
                        }
                    }

                    List<RichAudioUrl> richAudioUrls = notification.getRichAudioUrl();

                    if (ICSN_RichAudioUrl && richAudioUrls != null)
                    {
                        for (RichAudioUrl richAudioUrl : richAudioUrls)
                        {
                            String urlString = richAudioUrl.getUrl();
                            try
                            {
                                new URL(urlString);
                                logger.info("Rich audio URL: '" + urlString + "'");
                                includedRichAudioUrl = true;
                            }
                            catch (MalformedURLException e)
                            {
                                throw new Exception("RichAudioUrl malformed: '" + urlString + "'", e);
                            }
                        }
                    }

                    String responseObjectPath = notification.getResponseObjectPath();
                    if (ICSN_RespObjectPath && responseObjectPath != null)
                    {
                        logger.info("Control Panel Service response Object Path: '" + responseObjectPath + "'");

                        try
                        {
                            busIntrospector.introspect(responseObjectPath);
                        }
                        catch (ErrorReplyBusException e)
                        {
                            throw new Exception("Exception retrieving responseObject", e);
                        }

                        includesResponseObjectPath = true;
                    }
                   

                    StringBuilder noteMsg = new StringBuilder();
                    noteMsg.append("Notification Received: ");
                    noteMsg.append("messageType: ");
                    noteMsg.append(notification.getMessageType());
                    noteMsg.append("; richIconUrl: ");
                    noteMsg.append(includedRichIconUrl);
                    noteMsg.append("; richAudioUrl: ");
                    noteMsg.append(includedRichAudioUrl);
                    noteMsg.append("; responseObject: ");
                    noteMsg.append(includesResponseObjectPath);
                    String noteMsgString = noteMsg.toString();
                    logger.addNote(noteMsgString);
                    logger.info(noteMsgString);
                }
                catch (Throwable throwable)
                {
                    if (throwable instanceof Exception)
                    {
                        notificationValidationExceptionHandler.onNotificationValidationException((Exception) throwable);
                    }
                    else
                    {
                        notificationValidationExceptionHandler.onNotificationValidationException(new Exception(throwable));
                    }
                }
            }
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
        }
    }
    
    

    /**
     * Assert not null.
     *
     * @param errorMsg the error msg
     * @param messageType the message type
     * @throws Exception the exception
     */
    private void assertNotNull(String errorMsg,
			NotificationMessageType messageType) throws Exception {
		// TODO Auto-generated method stub
    	if(messageType==null)
    	{
    		logger.error(errorMsg);
    		throw new Exception(errorMsg);
    	}
    	
		
	}

	/**
	 * Assert equals.
	 *
	 * @param errorMsg the error msg
	 * @param appName the app name
	 * @param notifAppName the notif app name
	 * @throws Exception the exception
	 */
	private void assertEquals(String errorMsg, String appName, String notifAppName) throws Exception {
		// TODO Auto-generated method stub
		if(!appName.equals(notifAppName)){
			
			logger.error(errorMsg);
			throw new Exception(errorMsg);
			
		} else {
			logger.info("Partial Verdict: PASS");
		}
	}

	/**
	 * Assert equals.
	 *
	 * @param errorMsg the error msg
	 * @param notificationServiceVersion the notification service version
	 * @param version the version
	 * @throws Exception the exception
	 */
	private void assertEquals(String errorMsg, int notificationServiceVersion,
			int version) throws Exception {
		// TODO Auto-generated method stub
		
		if(notificationServiceVersion!=version){
		logger.error(errorMsg);
		throw new Exception(errorMsg);
		} else {
			logger.info("Partial Verdict: PASS");
		}
	}

	/**
	 * Gets the number of notifications received.
	 *
	 * @return the number of notifications received
	 */
	public int getNumberOfNotificationsReceived()
    {
        return notificationCounter.get();
    }

	/**
	 * Sets the test parameters.
	 *
	 * @param iCSN_RichIconUrl the i cs n_ rich icon url
	 * @param iCSN_RichAudioUrl the i cs n_ rich audio url
	 * @param iCSN_RespObjectPath the i cs n_ resp object path
	 * @param iXITN_NotificationVersion the i xit n_ notification version
	 */
	public void setTestParameters(boolean iCSN_RichIconUrl ,boolean iCSN_RichAudioUrl,boolean iCSN_RespObjectPath, String iXITN_NotificationVersion) {
		// TODO Auto-generated method stub
		
		ICSN_RichIconUrl=iCSN_RichIconUrl;
		ICSN_RichAudioUrl=iCSN_RichAudioUrl;
		ICSN_RespObjectPath=iCSN_RespObjectPath;
		
		IXITN_NotificationVersion=Integer.parseInt(iXITN_NotificationVersion);
		
	}

	
}