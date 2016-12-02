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
package org.alljoyn.validation.testing.utils.notification;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.alljoyn.ns.Notification;
import org.alljoyn.validation.testing.utils.MyRobolectricTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.annotation.Config;

@RunWith(MyRobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class NotificationHandlerTest
{

    @Mock
    Notification mockNotification = mock(Notification.class);

    private String deviceId1 = "deviceId1";
    private UUID appId1 = UUID.randomUUID();
    private String deviceId2 = "deviceId2";
    private UUID appId2 = UUID.randomUUID();
    private NotificationHandler notificationHandler;

    @Before
    public void setup()
    {
        MockitoAnnotations.initMocks(this);

        when(mockNotification.getAppId()).thenReturn(appId1);
        when(mockNotification.getDeviceId()).thenReturn(deviceId1);

        notificationHandler = new NotificationHandler();
        notificationHandler.initializeForDevice(deviceId1, appId1);

        // clear the interrupted state before running each test
        Thread.interrupted();
    }

    @Test
    public void notificationsFromAnotherDeviceShouldBeIgnored() throws Exception
    {
        when(mockNotification.getDeviceId()).thenReturn(deviceId2);
        notificationHandler.receive(mockNotification);
        assertNull(notificationHandler.getReceivedNotification(1, TimeUnit.MILLISECONDS));
    }

    @Test
    public void notificationsFromAnotherAppShouldBeIgnored() throws InterruptedException
    {
        when(mockNotification.getAppId()).thenReturn(appId2);
        notificationHandler.receive(mockNotification);
        assertNull(notificationHandler.getReceivedNotification(1, TimeUnit.MILLISECONDS));
    }

    @Test
    public void notificationsFromExceptedDeviceShouldBeReceived() throws InterruptedException
    {
        notificationHandler.receive(mockNotification);
        assertEquals(mockNotification, notificationHandler.getReceivedNotification(1, TimeUnit.MILLISECONDS));
    }

    @Test
    public void clearShouldClearAnyReceivedNotifications() throws Exception
    {
        notificationHandler.receive(mockNotification);
        notificationHandler.clearReceivedNotifications();
        assertNull(notificationHandler.getReceivedNotification(1, TimeUnit.MILLISECONDS));
    }

}