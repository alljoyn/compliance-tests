/*
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
 *    THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL
 *    WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED
 *    WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE
 *    AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL
 *    DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR
 *    PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER
 *    TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 *    PERFORMANCE OF THIS SOFTWARE.
 */
package com.at4wireless.alljoyn.core.notification;

import java.util.UUID;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import org.alljoyn.ns.Notification;
import org.alljoyn.ns.NotificationReceiver;


import com.at4wireless.alljoyn.core.commons.log.LoggerFactory;
import com.at4wireless.alljoyn.core.commons.log.Logger;


// TODO: Auto-generated Javadoc
/**
 * The Class NotificationHandler.
 */
public class NotificationHandler implements NotificationReceiver
{
    
    /** The Constant TAG. */
    private static final String TAG = "NotificationHandler";
    
    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger(TAG);

    /** The received notifications. */
    private LinkedBlockingDeque<Notification> receivedNotifications = new LinkedBlockingDeque<Notification>();

    /** The sender device id. */
    private String senderDeviceId;
    
    /** The sender app id. */
    private UUID senderAppId;

    /**
     * Initialize for device.
     *
     * @param deviceId the device id
     * @param appId the app id
     */
    public void initializeForDevice(String deviceId, UUID appId)
    {
        this.senderDeviceId = deviceId;
        this.senderAppId = appId;
    }

    /* (non-Javadoc)
     * @see org.alljoyn.ns.NotificationReceiver#receive(org.alljoyn.ns.Notification)
     */
    @Override
    public void receive(Notification notification)
    {
        UUID appId = notification.getAppId();
        String deviceId = notification.getDeviceId();

        if (senderDeviceId.equals(deviceId) && senderAppId.equals(appId))
        {
            logger.debug(String.format("Received notification from DUT with messageId: %d", notification.getMessageId()));
            receivedNotifications.add(notification);
        }
        else
        {
            logger.debug(String.format("Ignoring notification received from: deviceId: %s; appId: %s", deviceId, appId));
        }
    }

    /* (non-Javadoc)
     * @see org.alljoyn.ns.NotificationReceiver#dismiss(int, java.util.UUID)
     */
    @Override
    public void dismiss(int arg0, UUID arg1)
    {
    }

    /**
     * Gets the received notification.
     *
     * @return the received notification
     * @throws InterruptedException the interrupted exception
     */
    public Notification getReceivedNotification() throws InterruptedException
    {
        return receivedNotifications.take();
    }

    /**
     * Gets the received notification.
     *
     * @param timeout the timeout
     * @param unit the unit
     * @return the received notification
     * @throws InterruptedException the interrupted exception
     */
    public Notification getReceivedNotification(long timeout, TimeUnit unit) throws InterruptedException
    {
        return receivedNotifications.poll(timeout, unit);
    }

    /**
     * Clear received notifications.
     *
     * @throws InterruptedException the interrupted exception
     */
    public void clearReceivedNotifications() throws InterruptedException
    {
        receivedNotifications.clear();
    }
}