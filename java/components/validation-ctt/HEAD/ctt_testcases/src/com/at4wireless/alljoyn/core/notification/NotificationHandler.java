/*******************************************************************************
 *   *  
 *     Copyright (c) 2016 Open Connectivity Foundation and AllJoyn Open
 *     Source Project Contributors and others.
 *     
 *     All rights reserved. This program and the accompanying materials are
 *     made available under the terms of the Apache License, Version 2.0
 *     which accompanies this distribution, and is available at
 *     http://www.apache.org/licenses/LICENSE-2.0

 *******************************************************************************/
package com.at4wireless.alljoyn.core.notification;

import java.util.UUID;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import org.alljoyn.ns.Notification;
import org.alljoyn.ns.NotificationReceiver;

import com.at4wireless.alljoyn.core.commons.log.Logger;
import com.at4wireless.alljoyn.core.commons.log.WindowsLoggerImpl;

public class NotificationHandler implements NotificationReceiver
{
	private static final Logger logger = new WindowsLoggerImpl(NotificationHandler.class.getSimpleName());
    
    private LinkedBlockingDeque<Notification> receivedNotifications = new LinkedBlockingDeque<Notification>();
    
    private String senderDeviceId;
    private UUID senderAppId;

    public void initializeForDevice(String deviceId, UUID appId)
    {
        this.senderDeviceId = deviceId;
        this.senderAppId = appId;
    }

    @Override
    public void receive(Notification notification)
    {
        UUID appId = notification.getAppId();
        String deviceId = notification.getDeviceId();

        if (senderDeviceId.equals(deviceId) && senderAppId.equals(appId))
        {
            logger.info(String.format("Received notification from DUT with messageId: %d", notification.getMessageId()));
            receivedNotifications.add(notification);
        }
        else
        {
            logger.info(String.format("Ignoring notification received from: deviceId: %s; appId: %s", deviceId, appId));
        }
    }

    @Override
    public void dismiss(int arg0, UUID arg1)
    {
    }

    public Notification getReceivedNotification() throws InterruptedException
    {
        return receivedNotifications.take();
    }

    public Notification getReceivedNotification(long timeout, TimeUnit unit) throws InterruptedException
    {
        return receivedNotifications.poll(timeout, unit);
    }

    public void clearReceivedNotifications() throws InterruptedException
    {
        receivedNotifications.clear();
    }
}