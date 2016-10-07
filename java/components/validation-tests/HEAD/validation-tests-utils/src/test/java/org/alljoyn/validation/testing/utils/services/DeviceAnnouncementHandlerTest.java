/*******************************************************************************
 *  Copyright (c) Open Connectivity Foundation (OCF) and AllJoyn Open
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
package org.alljoyn.validation.testing.utils.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.alljoyn.about.AboutKeys;
import org.alljoyn.bus.Variant;
import org.alljoyn.services.common.BusObjectDescription;
import org.alljoyn.services.common.utils.TransportUtil;
import org.alljoyn.validation.testing.utils.MyRobolectricTestRunner;
import org.alljoyn.validation.testing.utils.about.AboutAnnouncementDetails;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

@RunWith(MyRobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class DeviceAnnouncementHandlerTest
{

    private static final String DEVICE_ID = "deviceId";
    private static final UUID APP_ID = UUID.randomUUID();
    private String serviceName = "serviceName";
    private short port = 30;
    private short port2 = 20;
    private BusObjectDescription[] objectDescriptions = new BusObjectDescription[0];
    private Map<String, Variant> aboutData;
    private DeviceAnnouncementHandler handler;

    @Before
    public void setup() throws Exception
    {
        aboutData = new HashMap<String, Variant>();
        aboutData.put(AboutKeys.ABOUT_DEVICE_ID, new Variant(DEVICE_ID));
        aboutData.put(AboutKeys.ABOUT_APP_ID, new Variant(TransportUtil.uuidToByteArray(APP_ID), "ay"));

        handler = new DeviceAnnouncementHandler(DEVICE_ID, APP_ID);
        Thread.currentThread().interrupted();
    }

    @Test
    public void testReceivedAnnouncement() throws Exception
    {
        handler.onAnnouncement(serviceName, port, objectDescriptions, aboutData);

        AboutAnnouncementDetails aboutAnnouncement = handler.waitForNextDeviceAnnouncement(10, TimeUnit.MILLISECONDS);
        assertNotNull(aboutAnnouncement);
        assertEquals(aboutData, aboutAnnouncement.getAboutData());
        assertSame(objectDescriptions, aboutAnnouncement.getObjectDescriptions());
        assertEquals(serviceName, aboutAnnouncement.getServiceName());
        assertEquals(port, aboutAnnouncement.getPort());
    }

    @Test
    public void testReceivedTwoAnnouncements() throws Exception
    {
        handler.onAnnouncement(serviceName, port, objectDescriptions, aboutData);
        handler.onAnnouncement(serviceName, port2, objectDescriptions, aboutData);

        AboutAnnouncementDetails aboutAnnouncement = handler.waitForNextDeviceAnnouncement(10, TimeUnit.MILLISECONDS);
        assertNotNull(aboutAnnouncement);
        assertEquals(port, aboutAnnouncement.getPort());

        aboutAnnouncement = handler.waitForNextDeviceAnnouncement(10, TimeUnit.MILLISECONDS);
        assertNotNull(aboutAnnouncement);
        assertEquals(port2, aboutAnnouncement.getPort());
    }

    @Test
    public void testReceiveOthers() throws Exception
    {
        aboutData = new HashMap<String, Variant>();
        aboutData.put(AboutKeys.ABOUT_DEVICE_ID, new Variant("deviceId2"));
        aboutData.put(AboutKeys.ABOUT_APP_ID, new Variant(TransportUtil.uuidToByteArray(UUID.randomUUID()), "ay"));

        handler.onAnnouncement(serviceName, port, objectDescriptions, aboutData);

        AboutAnnouncementDetails aboutAnnouncement = handler.waitForNextDeviceAnnouncement(10, TimeUnit.MILLISECONDS);
        assertNull(aboutAnnouncement);
    }

    @Test
    public void testReceiveAll() throws Exception
    {
        handler = new DeviceAnnouncementHandler(null, null);

        aboutData = new HashMap<String, Variant>();
        aboutData.put(AboutKeys.ABOUT_DEVICE_ID, new Variant("deviceId2"));
        aboutData.put(AboutKeys.ABOUT_APP_ID, new Variant(TransportUtil.uuidToByteArray(UUID.randomUUID()), "ay"));

        handler.onAnnouncement(serviceName, port, objectDescriptions, aboutData);

        aboutData.put(AboutKeys.ABOUT_APP_ID, new Variant(TransportUtil.uuidToByteArray(UUID.randomUUID()), "ay"));
        handler.onAnnouncement(serviceName, port, objectDescriptions, aboutData);

        AboutAnnouncementDetails aboutAnnouncement = handler.waitForNextDeviceAnnouncement(10, TimeUnit.MILLISECONDS);
        assertNotNull(aboutAnnouncement);
        aboutAnnouncement = handler.waitForNextDeviceAnnouncement(10, TimeUnit.MILLISECONDS);
        assertNotNull(aboutAnnouncement);
    }

    @Test
    public void testTimeout() throws Exception
    {
        AboutAnnouncementDetails aboutAnnouncement = handler.waitForNextDeviceAnnouncement(10, TimeUnit.MILLISECONDS);
        assertNull(aboutAnnouncement);
    }

    @Test
    public void testDeviceLost() throws Exception
    {
        handler.onDeviceLost(serviceName);
    }

    @Test
    public void testInvalidAnnouncements() throws Exception
    {
        // send some bad announcements
        HashMap<String, Variant> badAboutData = new HashMap<String, Variant>();
        badAboutData.put(AboutKeys.ABOUT_DEVICE_ID, new Variant(TransportUtil.uuidToByteArray(APP_ID), "ay"));
        badAboutData.put(AboutKeys.ABOUT_APP_ID, new Variant(DEVICE_ID));
        handler.onAnnouncement(serviceName, port, objectDescriptions, badAboutData);

        badAboutData = new HashMap<String, Variant>();
        badAboutData.put(AboutKeys.ABOUT_APP_ID, new Variant(TransportUtil.uuidToByteArray(APP_ID), "ay"));
        badAboutData.put(AboutKeys.ABOUT_DEVICE_ID, new Variant(TransportUtil.uuidToByteArray(APP_ID), "ay"));
        handler.onAnnouncement(serviceName, port, objectDescriptions, badAboutData);

        badAboutData = new HashMap<String, Variant>();
        badAboutData.put(AboutKeys.ABOUT_APP_ID, new Variant(DEVICE_ID));
        badAboutData.put(AboutKeys.ABOUT_DEVICE_ID, new Variant(DEVICE_ID));
        handler.onAnnouncement(serviceName, port, objectDescriptions, badAboutData);

        AboutAnnouncementDetails aboutAnnouncement = handler.waitForNextDeviceAnnouncement(10, TimeUnit.MILLISECONDS);
        assertNull(aboutAnnouncement);
    }

}