/*******************************************************************************
 *     Copyright (c) Open Connectivity Foundation (OCF) and AllJoyn Open
 *     Source Project (AJOSP) Contributors and others.
 *
 *     SPDX-License-Identifier: Apache-2.0
 *
 *     All rights reserved. This program and the accompanying materials are
 *     made available under the terms of the Apache License, Version 2.0
 *     which accompanies this distribution, and is available at
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Copyright (c) Open Connectivity Foundation and Contributors to AllSeen
 *     Alliance. All rights reserved.
 *
 *     Permission to use, copy, modify, and/or distribute this software for
 *     any purpose with or without fee is hereby granted, provided that the
 *     above copyright notice and this permission notice appear in all
 *     copies.
 *
 *     THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL
 *     WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED
 *     WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE
 *     AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL
 *     DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR
 *     PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER
 *     TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 *     PERFORMANCE OF THIS SOFTWARE.
 *******************************************************************************/
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
import com.at4wireless.alljoyn.core.commons.log.Logger;
import com.at4wireless.alljoyn.core.commons.log.WindowsLoggerImpl;
import com.at4wireless.alljoyn.core.introspection.BusIntrospector;

public class NotificationValidator implements NotificationReceiver, Runnable
{
	//private static final int NOTIFICATION_SERVICE_VERSION = 2;
	private static final Logger logger = new WindowsLoggerImpl(NotificationValidator.class.getSimpleName());
    private AtomicInteger notificationCounter = new AtomicInteger();
    private BusIntrospector busIntrospector;
    private NotificationValidationExceptionHandler notificationValidationExceptionHandler;
    private AboutAnnouncementDetails deviceAboutAnnouncement;
    //private AppUnderTestDetails appUnderTestDetails; //[AT4] Not needed
    private NotificationHandler notificationHandler;
    
    /** *
     * [AT4] Added attributes to check ICS and IXIT
     * 
     */
    private int IXITN_NotificationVersion;
    private boolean ICSN_RichIconUrl;
    private boolean ICSN_RichAudioUrl;
    private boolean ICSN_RespObjectPath;
    
    public interface NotificationValidationExceptionHandler
    {
        void onNotificationValidationException(Exception exception);
    }

    //public NotificationValidator(ValidationTestContext validationTestContext)
    public NotificationValidator(String dutDeviceId, UUID dutAppId)
    {
        notificationHandler = getNotificationHandler();
        //notificationHandler.initializeForDevice(appUnderTestDetails.getDeviceId(), appUnderTestDetails.getAppId());
        notificationHandler.initializeForDevice(dutDeviceId, dutAppId);
   
    }

    protected NotificationHandler getNotificationHandler()
    {
        return new NotificationHandler();
    }

    public void initializeForDevice(AboutAnnouncementDetails deviceAboutAnnouncement, BusIntrospector busIntrospector,
            NotificationValidationExceptionHandler notificationValidationExceptionHandler)
    {
        this.deviceAboutAnnouncement = deviceAboutAnnouncement;
        this.busIntrospector = busIntrospector;
        this.notificationValidationExceptionHandler = notificationValidationExceptionHandler;
    }

    @Override
    public void receive(Notification notification)
    {
        notificationHandler.receive(notification);
    }

    @Override
    public void dismiss(int arg0, UUID arg1)
    {
    }

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
                	notificationCounter.getAndIncrement(); //[AT4] Moved to the top of the code to avoid non-execution when Exeption thrown
                    boolean includedRichIconUrl = false;
                    boolean includedRichAudioUrl = false;
                    boolean includesResponseObjectPath = false;
                    
                    logger.info("Checking Version field of received notification message");
                    //assertEquals(String.format("Notification version must be %s", NOTIFICATION_SERVICE_VERSION), NOTIFICATION_SERVICE_VERSION, notification.getVersion());
                    assertEquals(String.format("Notification version received (%s) does not match IXITN_NotificationVersion (%s)",
                    		notification.getVersion(), IXITN_NotificationVersion), IXITN_NotificationVersion, notification.getVersion());
                    
                    logger.info("Checking AppName field of received notification message");
                    String notifAppName = notification.getAppName();
                    //assertEquals("AppName in notification does not match AboutAnnouncement", deviceAboutAnnouncement.getAppName(), notifAppName);
                    assertEquals("AppName parameter received in notification: "+notifAppName+" does not match About Announcement parameter: "
                    		+deviceAboutAnnouncement.getAppName(), deviceAboutAnnouncement.getAppName(), notifAppName);

                    logger.info("Checking DeviceName field of received notification message");
                    String notifdeviceName = notification.getDeviceName();
                    //assertEquals("DeviceName in notification does not match AboutAnnouncement", deviceAboutAnnouncement.getDeviceName(), notifdeviceName);
                    assertEquals("DeviceName parameter received in notification: "+notifdeviceName+" does not match About Announcement parameter: "
                    		+deviceAboutAnnouncement.getDeviceName(), deviceAboutAnnouncement.getDeviceName(), notifdeviceName);
                    
                    assertNotNull("Notification messageType value not valid!", notification.getMessageType());
      
                    logger.info("The received Message Id: " + notification.getMessageId());

                    String richIconUrlString = notification.getRichIconUrl();

                    //if (richIconUrlString != null) //[AT4] Added ICS condition
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
                            throw new Exception("Malformed RichIconUrl: '" + richIconUrlString + "'", e);
                        }
                    }
                    else if ((ICSN_RichIconUrl && richIconUrlString == null) ||
                    		(!ICSN_RichIconUrl && richIconUrlString != null))
                    {
                    	throw new Exception("RichIconUrl support is not the defined by ICSN_RichIconUrl");
                    }

                    List<RichAudioUrl> richAudioUrls = notification.getRichAudioUrl();

                    //if(richAudioUrls != null) //[AT4] Added ICS condition
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
                    else if ((ICSN_RichAudioUrl && richAudioUrls == null) ||
                    		(!ICSN_RichAudioUrl && richAudioUrls != null))
                    {
                    	throw new Exception("RichAudioUrl support is not the defined by ICSN_RichAudioUrl");
                    }

                    String responseObjectPath = notification.getResponseObjectPath();
                    //if(responseObjectPath != null)
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
                    else if ((ICSN_RespObjectPath && responseObjectPath == null) ||
                    		(!ICSN_RespObjectPath && responseObjectPath != null))
                    {
                    	throw new Exception("RespObjectPath support is not the defined by ICSN_RespObjectPath");
                    }
                    
                    //notificationCounter.getAndIncrement();

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
    
	public int getNumberOfNotificationsReceived()
    {
        return notificationCounter.get();
    }
	
	/** 
	 * [AT4] Added methods to emulate JUnit behaviour
	 * 
	 * assertEquals
	 * assertNotNull
	 * 
	 * */
	private void assertEquals(String errorMessage, String first, String second) throws Exception
	{
		if (!first.equals(second)) {	
			logger.error(errorMessage);
			throw new Exception(errorMessage);
		} else {
			logger.info("Partial Verdict: PASS");
		}
	}

	private void assertEquals(String errorMessage, int first, int second) throws Exception
	{
		if (first != second) {
			logger.error(errorMessage);
			throw new Exception(errorMessage);
		} else {
			logger.info("Partial Verdict: PASS");
		}
	}
    
    private void assertNotNull(String errorMessage, NotificationMessageType messageType) throws Exception
    {
    	if (messageType == null) {
    		logger.error(errorMessage);
    		throw new Exception(errorMessage);
    	}
	}
    
    /**
     * [AT4] Sets the necessary ICS and IXIT to validate the notification messages
     * 
     * @param iCSN_RichIconUrl
     * @param iCSN_RichAudioUrl
     * @param iCSN_RespObjectPath
     * @param iXITN_NotificationVersion
     */
	public void setTestParameters(boolean iCSN_RichIconUrl ,boolean iCSN_RichAudioUrl,boolean iCSN_RespObjectPath, int iXITN_NotificationVersion)
	{
		ICSN_RichIconUrl = iCSN_RichIconUrl;
		ICSN_RichAudioUrl = iCSN_RichAudioUrl;
		ICSN_RespObjectPath = iCSN_RespObjectPath;
		
		IXITN_NotificationVersion = iXITN_NotificationVersion;
	}
}