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
 *      THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL
 *      WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED
 *      WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE
 *      AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL
 *      DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR
 *      PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER
 *      TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 *      PERFORMANCE OF THIS SOFTWARE.
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