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
package org.alljoyn.validation.testing.utils.bus;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyShort;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.alljoyn.bus.BusAttachment;
import org.alljoyn.bus.BusAttachment.RemoteMessage;
import org.alljoyn.bus.BusException;
import org.alljoyn.bus.PasswordManager;
import org.alljoyn.bus.SessionOpts;
import org.alljoyn.bus.Status;
import org.alljoyn.bus.alljoyn.DaemonInit;
import org.alljoyn.validation.testing.utils.AllJoynLibraryLoader;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.content.Context;
import android.util.Log;

@RunWith(PowerMockRunner.class)
@PrepareForTest(
{ android.util.Log.class, org.alljoyn.bus.PasswordManager.class, org.alljoyn.bus.alljoyn.DaemonInit.class, org.alljoyn.validation.testing.utils.AllJoynLibraryLoader.class })
public class BusAttachmentMgrTest
{

    private static final String GUID = "myguid";
    private static final String APP_NAME = "appName";
    private BusAttachment mockBusAttachment = mock(BusAttachment.class);
    private BusAttachmentMgr busAttachmentMgr;
    private Context mockContext;

    @Before
    public void setup() throws Exception
    {
        PowerMockito.mockStatic(Log.class);
        PowerMockito.suppress(PowerMockito.methodsDeclaredIn(Log.class));
        PowerMockito.mockStatic(PasswordManager.class);

        when(mockBusAttachment.connect()).thenReturn(Status.OK);
        when(mockBusAttachment.requestName(anyString(), anyInt())).thenReturn(Status.OK);
        when(mockBusAttachment.advertiseName(anyString(), anyShort())).thenReturn(Status.OK);
        when(mockBusAttachment.getGlobalGUIDString()).thenReturn(GUID);
        when(mockBusAttachment.releaseName(anyString())).thenReturn(Status.OK);
        when(mockBusAttachment.cancelAdvertiseName(anyString(), anyShort())).thenReturn(Status.OK);

        mockContext = mock(Context.class);
        busAttachmentMgr = new BusAttachmentMgr()
        {
            @Override
            protected BusAttachment createBusAttachment(String applicationName, RemoteMessage policy)
            {
                assertEquals(APP_NAME, applicationName);
                assertEquals(RemoteMessage.Receive, policy);
                return mockBusAttachment;
            }
        };
    }

    @Test
    public void testInitialize() throws Exception
    {
        PowerMockito.suppress(PowerMockito.methodsDeclaredIn(AllJoynLibraryLoader.class));
        PowerMockito.mockStatic(AllJoynLibraryLoader.class);
        PowerMockito.doNothing().when(AllJoynLibraryLoader.class, PowerMockito.method(AllJoynLibraryLoader.class, "loadLibrary")).withNoArguments();

        PowerMockito.mockStatic(DaemonInit.class);
        PowerMockito.when(DaemonInit.class, PowerMockito.method(DaemonInit.class, "PrepareDaemon", Context.class)).withArguments(any(Context.class)).thenReturn(true);

        BusAttachmentMgr.init(mockContext);
        BusAttachmentMgr.init(mockContext);

        PowerMockito.verifyStatic();
        AllJoynLibraryLoader.loadLibrary();

        PowerMockito.verifyStatic();
        DaemonInit.PrepareDaemon(mockContext);

    }

    @Test
    public void testCreateRelease() throws BusException
    {
        busAttachmentMgr.create(APP_NAME, RemoteMessage.Receive);
        busAttachmentMgr.release();

        verify(mockBusAttachment, times(0)).cancelAdvertiseName("quiet@org.alljoyn.BusNode_myguid", SessionOpts.TRANSPORT_WLAN);
        verify(mockBusAttachment, times(0)).releaseName("org.alljoyn.BusNode_myguid");
        verify(mockBusAttachment).release();
        verify(mockBusAttachment).disconnect();
    }

    @Test
    public void testConnectRelease() throws Exception
    {
        busAttachmentMgr.create(APP_NAME, RemoteMessage.Receive);
        busAttachmentMgr.connect();
        busAttachmentMgr.advertise();
        verify(mockBusAttachment).connect();
        verify(mockBusAttachment).requestName("org.alljoyn.BusNode_myguid", BusAttachment.ALLJOYN_REQUESTNAME_FLAG_DO_NOT_QUEUE);
        verify(mockBusAttachment).advertiseName("quiet@org.alljoyn.BusNode_myguid", SessionOpts.TRANSPORT_WLAN);

        busAttachmentMgr.release();

        verify(mockBusAttachment).cancelAdvertiseName("quiet@org.alljoyn.BusNode_myguid", SessionOpts.TRANSPORT_WLAN);
        verify(mockBusAttachment).releaseName("org.alljoyn.BusNode_myguid");
        verify(mockBusAttachment).release();
        verify(mockBusAttachment).disconnect();
    }

    @Test
    public void testConnectReturnsError() throws Exception
    {
        when(mockBusAttachment.connect()).thenReturn(Status.FAIL);

        busAttachmentMgr.create(APP_NAME, RemoteMessage.Receive);

        try
        {
            busAttachmentMgr.connect();
            fail();
        }
        catch (BusException e)
        {
            assertEquals("Unable to connect busAttachment: FAIL", e.getMessage());
        }

        verify(mockBusAttachment).connect();
        verify(mockBusAttachment, times(0)).requestName("org.alljoyn.BusNode_myguid", BusAttachment.ALLJOYN_REQUESTNAME_FLAG_DO_NOT_QUEUE);
        verify(mockBusAttachment, times(0)).advertiseName("quiet@org.alljoyn.BusNode_myguid", SessionOpts.TRANSPORT_WLAN);

        busAttachmentMgr.release();

        verify(mockBusAttachment, times(0)).cancelAdvertiseName("quiet@org.alljoyn.BusNode_myguid", SessionOpts.TRANSPORT_WLAN);
        verify(mockBusAttachment, times(0)).releaseName("org.alljoyn.BusNode_myguid");
        verify(mockBusAttachment).disconnect();
        verify(mockBusAttachment).release();
    }

    @Test
    public void testRequestNameReturnsError() throws Exception
    {
        when(mockBusAttachment.requestName(anyString(), anyInt())).thenReturn(Status.FAIL);

        busAttachmentMgr.create(APP_NAME, RemoteMessage.Receive);
        busAttachmentMgr.connect();

        try
        {
            busAttachmentMgr.advertise();
            fail();
        }
        catch (BusException e)
        {
            assertEquals("Failed to requestName 'org.alljoyn.BusNode_myguid': FAIL", e.getMessage());
        }

        verify(mockBusAttachment).connect();
        verify(mockBusAttachment).requestName("org.alljoyn.BusNode_myguid", BusAttachment.ALLJOYN_REQUESTNAME_FLAG_DO_NOT_QUEUE);
        verify(mockBusAttachment, times(0)).advertiseName("quiet@org.alljoyn.BusNode_myguid", SessionOpts.TRANSPORT_WLAN);

        busAttachmentMgr.release();

        verify(mockBusAttachment, times(0)).cancelAdvertiseName("quiet@org.alljoyn.BusNode_myguid", SessionOpts.TRANSPORT_WLAN);
        verify(mockBusAttachment, times(0)).releaseName("org.alljoyn.BusNode_myguid");
        verify(mockBusAttachment).release();
        verify(mockBusAttachment).disconnect();
    }

    @Test
    public void testAdvertiseNameReturnsError() throws Exception
    {
        when(mockBusAttachment.advertiseName(anyString(), anyShort())).thenReturn(Status.FAIL);

        busAttachmentMgr.create(APP_NAME, RemoteMessage.Receive);
        busAttachmentMgr.connect();

        try
        {
            busAttachmentMgr.advertise();
            fail();
        }
        catch (BusException e)
        {
            assertEquals("Failed to advertiseName: org.alljoyn.BusNode_myguid", e.getMessage());
        }

        verify(mockBusAttachment).connect();
        verify(mockBusAttachment).requestName("org.alljoyn.BusNode_myguid", BusAttachment.ALLJOYN_REQUESTNAME_FLAG_DO_NOT_QUEUE);
        verify(mockBusAttachment).advertiseName("quiet@org.alljoyn.BusNode_myguid", SessionOpts.TRANSPORT_WLAN);

        busAttachmentMgr.release();

        verify(mockBusAttachment, times(0)).cancelAdvertiseName("quiet@org.alljoyn.BusNode_myguid", SessionOpts.TRANSPORT_WLAN);
        verify(mockBusAttachment).releaseName("org.alljoyn.BusNode_myguid");
        verify(mockBusAttachment).release();
        verify(mockBusAttachment).disconnect();
    }

    @Test
    public void testCancelAdvertisedNameThrowsException() throws Exception
    {
        when(mockBusAttachment.cancelAdvertiseName(anyString(), anyShort())).thenReturn(Status.FAIL);

        busAttachmentMgr.create(APP_NAME, RemoteMessage.Receive);
        busAttachmentMgr.connect();
        busAttachmentMgr.advertise();
        busAttachmentMgr.release();
        verify(mockBusAttachment).cancelAdvertiseName("quiet@org.alljoyn.BusNode_myguid", SessionOpts.TRANSPORT_WLAN);
        verify(mockBusAttachment).releaseName("org.alljoyn.BusNode_myguid");
        verify(mockBusAttachment).release();
        verify(mockBusAttachment).disconnect();
    }

    @Test
    public void testReleaseNameThrowsException() throws Exception
    {
        when(mockBusAttachment.releaseName(anyString())).thenReturn(Status.FAIL);

        busAttachmentMgr.create(APP_NAME, RemoteMessage.Receive);
        busAttachmentMgr.connect();
        busAttachmentMgr.advertise();
        busAttachmentMgr.release();
        verify(mockBusAttachment).cancelAdvertiseName("quiet@org.alljoyn.BusNode_myguid", SessionOpts.TRANSPORT_WLAN);
        verify(mockBusAttachment).releaseName("org.alljoyn.BusNode_myguid");
        verify(mockBusAttachment).release();
        verify(mockBusAttachment).disconnect();
    }

    @Test
    public void testGetBusUniqueName() throws BusException
    {
        when(mockBusAttachment.getUniqueName()).thenReturn("busName");
        busAttachmentMgr.create(APP_NAME, RemoteMessage.Receive);
        assertEquals("busName", busAttachmentMgr.getBusUniqueName());
    }
}