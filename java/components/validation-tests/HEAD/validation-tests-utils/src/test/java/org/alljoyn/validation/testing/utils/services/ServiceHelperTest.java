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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyShort;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import org.alljoyn.bus.Status;
import org.alljoyn.config.ConfigService;
import org.alljoyn.config.client.ConfigClient;
import org.alljoyn.ns.NotificationReceiver;
import org.alljoyn.ns.NotificationService;
import org.alljoyn.ns.NotificationServiceException;
import org.alljoyn.onboarding.client.OnboardingClient;
import org.alljoyn.onboarding.client.OnboardingClientImpl;
import org.alljoyn.services.android.security.AuthPasswordHandler;
import org.alljoyn.services.android.security.SrpAnonymousKeyListener;
import org.alljoyn.services.android.utils.AndroidLogger;
import org.alljoyn.services.common.PropertyStore;
import org.alljoyn.services.common.ServiceAvailabilityListener;
import org.alljoyn.services.common.utils.GenericLogger;
import org.alljoyn.validation.testing.utils.MyRobolectricTestRunner;
import org.alljoyn.validation.testing.utils.about.AboutAnnouncementDetails;
import org.alljoyn.validation.testing.utils.bus.BusAttachmentMgr;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.annotation.Config;

@RunWith(MyRobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class ServiceHelperTest
{
    private static final String KEY_STORE_FILE_NAME = "keyStoreFileName";
    private static final String DEVICE_ID = "deviceId";
    private static final UUID APP_ID = UUID.randomUUID();
    private static final Integer SESSION_ID = 10;
    private static final int LINK_TIMEOUT_IN_SECONDS = 120;

    @Mock
    private BusAttachment mockBusAttachment;
    @Mock
    private AboutServiceImpl mockAboutServiceImpl;
    @Mock
    private NotificationService mockNotificationService;
    @Mock
    private NotificationReceiver mockNotificationReceiver;
    @Mock
    private AboutAnnouncementDetails mockAboutAnnouncementDetails;
    @Mock
    private AboutAnnouncementDetails mockAboutAnnouncementDetails2;
    @Mock
    private AboutClientImpl mockAboutClientImpl;
    @Mock
    private AboutIconClient mockAboutIconClient;
    @Mock
    private ConfigService mockConfigService;
    @Mock
    private ConfigClient mockConfigClient;
    @Mock
    private SrpAnonymousKeyListener mockSrpAnonymousKeyListener;
    @Mock
    private DeviceAnnouncementHandler mockDeviceAnnouncementHandler;
    @Mock
    private PropertyStore mockPropertyStore;
    @Mock
    private OnboardingClientImpl mockOnboardingClient;
    @Mock
    private BusAttachmentMgr mockBusAttachmentMgr;
    @Mock
    private AuthPasswordHandlerImpl mockAuthPasswordHandlerImpl;
    @Mock
    private PasswordStore mockPasswordStore;

    private ServiceHelper serviceHelper;
    private String serviceName = "serviceName";
    private short port = 30;
    private String deviceName = "deviceName";
    private String myApplicationName = "appName";
    private char[] currentPassword = SrpAnonymousKeyListener.DEFAULT_PINCODE;

    @Before
    public void setup() throws Exception
    {
        MockitoAnnotations.initMocks(this);

        when(mockBusAttachmentMgr.getBusAttachment()).thenReturn(mockBusAttachment);

        serviceHelper = new ServiceHelper(new AndroidLogger())
        {

            @Override
            protected BusAttachmentMgr getBusAttachmentMgr()
            {
                return mockBusAttachmentMgr;
            }

            @Override
            protected NotificationService getNotificationService()
            {
                return mockNotificationService;
            }

            @Override
            protected AboutService getAboutService()
            {
                return mockAboutServiceImpl;
            }

            @Override
            protected ConfigService getConfigService()
            {
                return mockConfigService;
            }

            @Override
            protected DeviceAnnouncementHandler getDeviceAnnouncementHandler(String deviceId, UUID appId)
            {
                assertEquals(DEVICE_ID, deviceId);
                assertEquals(APP_ID, appId);
                return mockDeviceAnnouncementHandler;
            }

            @Override
            protected AuthPasswordHandlerImpl getAuthPasswordHandlerImpl()
            {
                return mockAuthPasswordHandlerImpl;
            }

            @Override
            protected SrpAnonymousKeyListener getSrpAnonymousListener(AuthPasswordHandler passwordHandler, GenericLogger genericLogger)
            {
                assertEquals(mockAuthPasswordHandlerImpl, passwordHandler);
                return mockSrpAnonymousKeyListener;
            }

            @Override
            protected OnboardingClientImpl getOnboardingClient(String serviceName, BusAttachment bus, ServiceAvailabilityListener serviceAvailabilityListener, short port)
            {
                return mockOnboardingClient;
            }

            @Override
            protected PasswordStore getPasswordStore()
            {
                return mockPasswordStore;
            }
        };
        when(mockBusAttachment.connect()).thenReturn(Status.OK);
        when(mockBusAttachment.setLinkTimeout(anyInt(), (IntegerValue) anyObject())).thenReturn(Status.OK);

        when(mockAboutAnnouncementDetails.getServiceName()).thenReturn(serviceName);
        when(mockAboutAnnouncementDetails.getDeviceName()).thenReturn(deviceName);
        when(mockAboutAnnouncementDetails.getPort()).thenReturn(port);

        when(mockDeviceAnnouncementHandler.waitForNextDeviceAnnouncement(anyLong(), any(TimeUnit.class))).thenReturn(mockAboutAnnouncementDetails);

        when(mockBusAttachment.addMatch(anyString())).thenReturn(Status.OK);
        when(mockBusAttachment.removeMatch(anyString())).thenReturn(Status.OK);

        when(mockAboutServiceImpl.createAboutClient(anyString(), any(ServiceAvailabilityListener.class), anyShort())).thenReturn(mockAboutClientImpl);

        when(mockAboutServiceImpl.createAboutIconClient(anyString(), any(ServiceAvailabilityListener.class), anyShort())).thenReturn(mockAboutIconClient);

        when(mockConfigService.createFeatureConfigClient(anyString(), any(ServiceAvailabilityListener.class), anyShort())).thenReturn(mockConfigClient);

        when(mockAboutClientImpl.connect()).thenReturn(Status.OK);
        when(mockAboutClientImpl.getSessionId()).thenReturn(SESSION_ID);
        when(mockAboutClientImpl.getPeerName()).thenReturn(serviceName);
        when(mockAboutIconClient.connect()).thenReturn(Status.OK);
        when(mockAboutIconClient.getSessionId()).thenReturn(SESSION_ID);
        when(mockConfigClient.connect()).thenReturn(Status.OK);
        when(mockConfigClient.getSessionId()).thenReturn(SESSION_ID);
        when(mockOnboardingClient.getSessionId()).thenReturn(SESSION_ID);

        when(mockBusAttachment.registerAuthListener("ALLJOYN_SRP_KEYX ALLJOYN_PIN_KEYX", mockSrpAnonymousKeyListener)).thenReturn(Status.OK);
    }

    @Test
    public void testInitialize() throws Exception
    {
        serviceHelper.initialize(myApplicationName, DEVICE_ID, APP_ID);

        verify(mockBusAttachmentMgr).create(myApplicationName, RemoteMessage.Receive);
        verify(mockBusAttachmentMgr).connect();
        verify(mockAboutServiceImpl).addAnnouncementHandler(mockDeviceAnnouncementHandler);
    }

    @Test
    public void testInitializeWhenCreateThrowsException() throws Exception
    {
        doThrow(new BusException()).when(mockBusAttachmentMgr).create(myApplicationName, RemoteMessage.Receive);

        try
        {
            serviceHelper.initialize(myApplicationName, DEVICE_ID, APP_ID);
            fail();
        }
        catch (BusException e)
        {
        }
        serviceHelper.release();
        verify(mockBusAttachmentMgr).release();
    }

    @Test
    public void testInitializeWhenConnectThrowsException() throws Exception
    {
        doThrow(new BusException()).when(mockBusAttachmentMgr).connect();

        try
        {
            serviceHelper.initialize(myApplicationName, DEVICE_ID, APP_ID);
            fail();
        }
        catch (BusException e)
        {
        }
        serviceHelper.release();
        verify(mockBusAttachmentMgr).release();
    }

    @Test
    public void testInitNotificationReceiver() throws Exception
    {
        serviceHelper.initialize(myApplicationName, DEVICE_ID, APP_ID);
        serviceHelper.initNotificationReceiver(mockNotificationReceiver);

        verify(mockNotificationService).initReceive(mockBusAttachment, mockNotificationReceiver);
    }

    @Test
    public void testInitNotificationReceiverThrowsException() throws Exception
    {
        NotificationServiceException nse = new NotificationServiceException();
        doThrow(nse).when(mockNotificationService).initReceive(mockBusAttachment, mockNotificationReceiver);

        serviceHelper.initialize(myApplicationName, DEVICE_ID, APP_ID);
        try
        {
            serviceHelper.initNotificationReceiver(mockNotificationReceiver);
            fail();
        }
        catch (NotificationServiceException e)
        {
            assertEquals(nse, e);
        }

        verify(mockNotificationService).initReceive(mockBusAttachment, mockNotificationReceiver);
    }

    @Test
    public void testInitNotificationSender() throws Exception
    {
        serviceHelper.initialize(myApplicationName, DEVICE_ID, APP_ID);
        serviceHelper.initNotificationSender(mockPropertyStore);

        verify(mockNotificationService).initSend(mockBusAttachment, mockPropertyStore);
    }

    @Test
    public void testInitNotificationSenderThrowsException() throws Exception
    {
        NotificationServiceException nse = new NotificationServiceException();
        doThrow(nse).when(mockNotificationService).initSend(mockBusAttachment, mockPropertyStore);

        serviceHelper.initialize(myApplicationName, DEVICE_ID, APP_ID);
        try
        {
            serviceHelper.initNotificationSender(mockPropertyStore);
            fail();
        }
        catch (NotificationServiceException e)
        {
            assertEquals(nse, e);
        }

        verify(mockNotificationService).initSend(mockBusAttachment, mockPropertyStore);
    }

    @Test
    public void testStartAboutClient() throws Exception
    {
        serviceHelper.initialize(myApplicationName, DEVICE_ID, APP_ID);
        serviceHelper.startAboutClient();

        verify(mockAboutServiceImpl).startAboutClient(mockBusAttachment);
    }

    @Test
    public void testStartAboutClientStopsTheClientFirstIfItsAlreadyRunning() throws Exception
    {
        when(mockAboutServiceImpl.isClientRunning()).thenReturn(true);
        serviceHelper.initialize(myApplicationName, DEVICE_ID, APP_ID);
        serviceHelper.startAboutClient();

        verify(mockAboutServiceImpl).startAboutClient(mockBusAttachment);
        verify(mockAboutServiceImpl).stopAboutClient();
    }

    @Test
    public void testStartAboutServer() throws Exception
    {
        serviceHelper.initialize(myApplicationName, DEVICE_ID, APP_ID);
        serviceHelper.startAboutServer(port, mockPropertyStore);

        verify(mockAboutServiceImpl).startAboutServer(port, mockPropertyStore, mockBusAttachment);
    }

    @Test
    public void testStartAboutServerStopsTheServerFirstIfItsAlreadyRunning() throws Exception
    {
        when(mockAboutServiceImpl.isServerRunning()).thenReturn(true);
        serviceHelper.initialize(myApplicationName, DEVICE_ID, APP_ID);
        serviceHelper.startAboutServer(port, mockPropertyStore);

        verify(mockAboutServiceImpl).startAboutServer(port, mockPropertyStore, mockBusAttachment);
        verify(mockAboutServiceImpl).stopAboutServer();
    }

    @Test
    public void testStartAboutClient_ThrowsException() throws Exception
    {
        doThrow(new Exception()).when(mockAboutServiceImpl).startAboutClient(mockBusAttachment);

        serviceHelper.initialize(myApplicationName, DEVICE_ID, APP_ID);
        try
        {
            serviceHelper.startAboutClient();
            fail();
        }
        catch (Exception e)
        {

        }
        serviceHelper.release();

        verify(mockAboutServiceImpl).startAboutClient(mockBusAttachment);
    }

    @Test
    public void testStartConfigClient() throws Exception
    {
        serviceHelper.initialize(myApplicationName, DEVICE_ID, APP_ID);
        serviceHelper.startConfigClient();

        verify(mockConfigService).startConfigClient(mockBusAttachment);
    }

    @Test
    public void testStartConfigClientStopsTheClientFirstIfItsAlreadyRunning() throws Exception
    {
        when(mockConfigService.isClientRunning()).thenReturn(true);
        serviceHelper.initialize(myApplicationName, DEVICE_ID, APP_ID);
        serviceHelper.startConfigClient();

        verify(mockConfigService).startConfigClient(mockBusAttachment);
        verify(mockConfigService).stopConfigClient();
    }

    @Test
    public void testStartConfigClient_ThrowsException() throws Exception
    {
        doThrow(new Exception()).when(mockConfigService).startConfigClient(mockBusAttachment);

        serviceHelper.initialize(myApplicationName, DEVICE_ID, APP_ID);
        try
        {
            serviceHelper.startConfigClient();
            fail();
        }
        catch (Exception e)
        {

        }
        serviceHelper.release();
        verify(mockConfigService).startConfigClient(mockBusAttachment);
    }

    @Test
    public void testWaitForNextDeviceAnnouncement() throws Exception
    {
        serviceHelper.initialize(myApplicationName, DEVICE_ID, APP_ID);
        serviceHelper.startAboutClient();

        AboutAnnouncementDetails aboutAnnouncementDetails = serviceHelper.waitForNextDeviceAnnouncement(10, TimeUnit.MILLISECONDS);
        assertSame(mockAboutAnnouncementDetails, aboutAnnouncementDetails);

        verify(mockAboutServiceImpl).startAboutClient(mockBusAttachment);
        verify(mockBusAttachment).addMatch("sessionless='t',type='error'");

        serviceHelper.release();
        verify(mockBusAttachment).removeMatch("sessionless='t',type='error'");
    }

    @Test
    public void testWaitForNextDeviceAnnouncement_TwoAnnouncements() throws Exception
    {
        when(mockDeviceAnnouncementHandler.waitForNextDeviceAnnouncement(anyLong(), any(TimeUnit.class))).thenReturn(mockAboutAnnouncementDetails).thenReturn(
                mockAboutAnnouncementDetails2);

        serviceHelper.initialize(myApplicationName, DEVICE_ID, APP_ID);

        AboutAnnouncementDetails aboutAnnouncementDetails = serviceHelper.waitForNextDeviceAnnouncement(10, TimeUnit.MILLISECONDS);
        assertSame(mockAboutAnnouncementDetails, aboutAnnouncementDetails);

        when(mockAboutServiceImpl.isClientRunning()).thenReturn(true);

        aboutAnnouncementDetails = serviceHelper.waitForNextDeviceAnnouncement(10, TimeUnit.MILLISECONDS);
        assertSame(mockAboutAnnouncementDetails2, aboutAnnouncementDetails);
    }

    @Test
    public void testWaitForNextDeviceAnnouncement_addMatch_Fails() throws Exception
    {
        when(mockBusAttachment.addMatch(anyString())).thenReturn(Status.FAIL);

        serviceHelper.initialize(myApplicationName, DEVICE_ID, APP_ID);

        try
        {
            serviceHelper.waitForNextDeviceAnnouncement(10, TimeUnit.MILLISECONDS);
            fail();
        }
        catch (BusException e)
        {
            assertEquals("Call to addMatch rule for sessionsless returned failure: FAIL", e.getMessage());
        }
        serviceHelper.release();
        verify(mockBusAttachment, times(0)).removeMatch("sessionless='t',type='error'");
    }

    @Test
    public void testWaitForNextDeviceAnnouncementWhenThrowsException() throws Exception
    {
        when(mockDeviceAnnouncementHandler.waitForNextDeviceAnnouncement(anyLong(), any(TimeUnit.class))).thenReturn(null).thenReturn(null);

        serviceHelper.initialize(myApplicationName, DEVICE_ID, APP_ID);
        serviceHelper.startAboutClient();

        serviceHelper.waitForNextDeviceAnnouncement(10, TimeUnit.MILLISECONDS, false);

        try
        {
            serviceHelper.waitForNextDeviceAnnouncement(10, TimeUnit.MILLISECONDS, true);
            fail();
        }
        catch (BusException be)
        {
            assertEquals("Timed out waiting for About announcement", be.getMessage());
        }

        verify(mockAboutServiceImpl).startAboutClient(mockBusAttachment);
        verify(mockBusAttachment).addMatch("sessionless='t',type='error'");

        serviceHelper.release();
    }

    @Test
    public void testConnectAboutClient() throws Exception
    {
        answerSetLinkTimeout(SESSION_ID);
        serviceHelper.initialize(myApplicationName, DEVICE_ID, APP_ID);
        AboutClient aboutClient = serviceHelper.connectAboutClient(mockAboutAnnouncementDetails);

        serviceHelper.release();

        assertEquals(mockAboutClientImpl, aboutClient);
        verify(mockAboutServiceImpl).createAboutClient(serviceName, null, port);
        verify(mockAboutClientImpl).connect();
        verify(mockBusAttachment).setLinkTimeout(anyInt(), (IntegerValue) anyObject());
    }

    @Test
    public void testConnectAboutClient_SetLinkTimeoutFails() throws Exception
    {
        when(mockBusAttachment.setLinkTimeout(anyInt(), (IntegerValue) anyObject())).thenReturn(Status.FAIL);
        serviceHelper.initialize(myApplicationName, DEVICE_ID, APP_ID);

        try
        {
            serviceHelper.connectAboutClient(mockAboutAnnouncementDetails);
            fail();
        }
        catch (BusException e)
        {
            assertEquals("Failed to set link timeout value on bus attachment for session (10): FAIL", e.getMessage());
        }
    }

    @Test
    public void testConnectAboutClient_CreateAboutClientFails() throws Exception
    {
        when(mockAboutServiceImpl.createAboutClient(anyString(), any(ServiceAvailabilityListener.class), anyShort())).thenThrow(new Exception());

        serviceHelper.initialize(myApplicationName, DEVICE_ID, APP_ID);

        try
        {
            serviceHelper.connectAboutClient(mockAboutAnnouncementDetails);
            fail();
        }
        catch (Exception e)
        {
        }
    }

    @Test
    public void testConnectAboutClient_ConnectFails() throws Exception
    {
        when(mockAboutClientImpl.connect()).thenReturn(Status.FAIL);

        serviceHelper.initialize(myApplicationName, DEVICE_ID, APP_ID);

        try
        {
            serviceHelper.connectAboutClient(mockAboutAnnouncementDetails);
            fail();
        }
        catch (BusException e)
        {
            assertEquals("Failed to connect AboutClient to client: FAIL", e.getMessage());
        }
        verify(mockAboutClientImpl).connect();
    }

    @Test
    public void testConnectAboutClient_ConnectReturnsAlreadyJoined() throws Exception
    {
        when(mockAboutClientImpl.connect()).thenReturn(Status.ALLJOYN_JOINSESSION_REPLY_ALREADY_JOINED);

        serviceHelper.initialize(myApplicationName, DEVICE_ID, APP_ID);

        serviceHelper.connectAboutClient(mockAboutAnnouncementDetails);

        verify(mockAboutClientImpl).connect();
    }

    @Test
    public void testConnectAboutIconClient() throws Exception
    {
        answerSetLinkTimeout(SESSION_ID);
        serviceHelper.initialize(myApplicationName, DEVICE_ID, APP_ID);
        AboutIconClient aboutIconClient = serviceHelper.connectAboutIconClient(mockAboutAnnouncementDetails);

        serviceHelper.release();

        assertEquals(mockAboutIconClient, aboutIconClient);
        verify(mockAboutServiceImpl).createAboutIconClient(serviceName, null, port);
        verify(mockAboutIconClient).connect();
        verify(mockBusAttachment).setLinkTimeout(anyInt(), (IntegerValue) anyObject());
    }

    @Test
    public void testConnectAboutIconClient_CreateAboutIconClientFails() throws Exception
    {
        when(mockAboutServiceImpl.createAboutIconClient(anyString(), any(ServiceAvailabilityListener.class), anyShort())).thenThrow(new BusException());

        serviceHelper.initialize(myApplicationName, DEVICE_ID, APP_ID);

        try
        {
            serviceHelper.connectAboutIconClient(mockAboutAnnouncementDetails);
            fail();
        }
        catch (Exception e)
        {
        }
    }

    @Test
    public void testConnectAboutIconClient_ConnectFails() throws Exception
    {
        when(mockAboutIconClient.connect()).thenReturn(Status.FAIL);

        serviceHelper.initialize(myApplicationName, DEVICE_ID, APP_ID);

        try
        {
            serviceHelper.connectAboutIconClient(mockAboutAnnouncementDetails);
            fail();
        }
        catch (BusException e)
        {
            assertEquals("Failed to connect AboutIconClient to client: FAIL", e.getMessage());
        }
        verify(mockAboutIconClient).connect();
    }

    @Test
    public void testConnectAboutIconClient_SetLinkTimeoutFails() throws Exception
    {
        when(mockBusAttachment.setLinkTimeout(anyInt(), (IntegerValue) anyObject())).thenReturn(Status.FAIL);
        serviceHelper.initialize(myApplicationName, DEVICE_ID, APP_ID);

        try
        {
            serviceHelper.connectAboutIconClient(mockAboutAnnouncementDetails);
            fail();
        }
        catch (BusException e)
        {
            assertEquals("Failed to set link timeout value on bus attachment for session (10): FAIL", e.getMessage());
        }
    }

    @Test
    public void testConnectConfigClient() throws Exception
    {
        answerSetLinkTimeout(SESSION_ID);
        serviceHelper.initialize(myApplicationName, DEVICE_ID, APP_ID);
        ConfigClient configClient = serviceHelper.connectConfigClient(mockAboutAnnouncementDetails);

        serviceHelper.release();

        assertEquals(mockConfigClient, configClient);
        verify(mockConfigService).createFeatureConfigClient(serviceName, null, port);
        verify(mockConfigClient).connect();
        verify(mockBusAttachment).setLinkTimeout(anyInt(), (IntegerValue) anyObject());
    }

    @Test
    public void testConnectConfigClient_CreateConfigClientFails() throws Exception
    {
        when(mockConfigService.createFeatureConfigClient(anyString(), any(ServiceAvailabilityListener.class), anyShort())).thenThrow(new Exception());

        serviceHelper.initialize(myApplicationName, DEVICE_ID, APP_ID);

        try
        {
            serviceHelper.connectConfigClient(mockAboutAnnouncementDetails);
            fail();
        }
        catch (Exception e)
        {
        }
        verify(mockConfigClient, times(0)).connect();
    }

    @Test
    public void testConnectConfigClient_ConnectFails() throws Exception
    {
        when(mockConfigClient.connect()).thenReturn(Status.FAIL);

        serviceHelper.initialize(myApplicationName, DEVICE_ID, APP_ID);

        try
        {
            serviceHelper.connectConfigClient(mockAboutAnnouncementDetails);
            fail();
        }
        catch (BusException e)
        {
            assertEquals("Failed to connect ConfigClient to client: FAIL", e.getMessage());
        }
        verify(mockConfigClient).connect();

    }

    @Test
    public void testConnectConfigClient_SetLinkTimeoutFails() throws Exception
    {
        when(mockBusAttachment.setLinkTimeout(anyInt(), (IntegerValue) anyObject())).thenReturn(Status.FAIL);
        serviceHelper.initialize(myApplicationName, DEVICE_ID, APP_ID);

        try
        {
            serviceHelper.connectConfigClient(mockAboutAnnouncementDetails);
            fail();
        }
        catch (BusException e)
        {
            assertEquals("Failed to set link timeout value on bus attachment for session (10): FAIL", e.getMessage());
        }

    }

    @Test
    public void testConnectOnboardingClient() throws Exception
    {
        answerSetLinkTimeout(SESSION_ID);
        when(mockOnboardingClient.connect()).thenReturn(Status.OK);

        serviceHelper.initialize(myApplicationName, DEVICE_ID, APP_ID);
        OnboardingClient onboardingClient = serviceHelper.connectOnboardingClient(mockAboutAnnouncementDetails);

        serviceHelper.release();

        assertEquals(mockOnboardingClient, onboardingClient);
        verify(mockOnboardingClient).connect();
        verify(mockBusAttachment).setLinkTimeout(anyInt(), (IntegerValue) anyObject());
    }

    @Test
    public void testConnectOnboardingClient_ConnectFails() throws Exception
    {
        when(mockOnboardingClient.connect()).thenReturn(Status.FAIL);

        serviceHelper.initialize(myApplicationName, DEVICE_ID, APP_ID);

        try
        {
            serviceHelper.connectOnboardingClient(mockAboutAnnouncementDetails);
            fail();
        }
        catch (BusException e)
        {
            assertEquals("Failed to connect OnboardingClient to client: FAIL", e.getMessage());
        }
        verify(mockOnboardingClient).connect();
    }

    @Test
    public void testConnectOnboardingClient_SetLinkTimeoutFails() throws Exception
    {
        when(mockBusAttachment.setLinkTimeout(anyInt(), (IntegerValue) anyObject())).thenReturn(Status.FAIL);
        when(mockOnboardingClient.connect()).thenReturn(Status.OK);
        serviceHelper.initialize(myApplicationName, DEVICE_ID, APP_ID);

        try
        {
            serviceHelper.connectOnboardingClient(mockAboutAnnouncementDetails);
            fail();
        }
        catch (BusException e)
        {
            assertEquals("Failed to set link timeout value on bus attachment for session (10): FAIL", e.getMessage());
        }
    }

    @Test
    public void testSetupAuthentication() throws Exception
    {
        when(mockBusAttachment.registerAuthListener("ALLJOYN_SRP_KEYX ALLJOYN_PIN_KEYX", mockSrpAnonymousKeyListener, KEY_STORE_FILE_NAME)).thenReturn(Status.OK);

        serviceHelper.initialize(myApplicationName, DEVICE_ID, APP_ID);
        serviceHelper.enableAuthentication(KEY_STORE_FILE_NAME);

        verify(mockBusAttachment).registerAuthListener("ALLJOYN_SRP_KEYX ALLJOYN_PIN_KEYX", mockSrpAnonymousKeyListener, KEY_STORE_FILE_NAME);
    }

    @Test
    public void testSetupAuthenticationFails() throws Exception
    {
        when(mockBusAttachment.registerAuthListener("ALLJOYN_SRP_KEYX ALLJOYN_PIN_KEYX", mockSrpAnonymousKeyListener, KEY_STORE_FILE_NAME)).thenReturn(Status.FAIL);

        serviceHelper.initialize(myApplicationName, DEVICE_ID, APP_ID);
        try
        {
            serviceHelper.enableAuthentication(KEY_STORE_FILE_NAME);
        }
        catch (BusException e)
        {
            assertEquals("Call to registerAuthListener returned failure: FAIL", e.getMessage());
        }

        verify(mockBusAttachment).registerAuthListener("ALLJOYN_SRP_KEYX ALLJOYN_PIN_KEYX", mockSrpAnonymousKeyListener, KEY_STORE_FILE_NAME);

    }

    @Test
    public void testRelease() throws Exception
    {
        answerSetLinkTimeout(SESSION_ID);
        serviceHelper.initialize(myApplicationName, DEVICE_ID, APP_ID);
        serviceHelper.initNotificationReceiver(mockNotificationReceiver);
        serviceHelper.waitForNextDeviceAnnouncement(10, TimeUnit.MILLISECONDS);
        serviceHelper.connectAboutClient(mockAboutAnnouncementDetails);
        serviceHelper.release();

        verify(mockAboutServiceImpl).stopAboutClient();
        verify(mockConfigService).stopConfigClient();
        verify(mockNotificationService).shutdown();
        verify(mockBusAttachmentMgr).release();
    }

    @Test
    public void testReleaseWithoutInitialize() throws Exception
    {
        serviceHelper.release();
    }

    @Test
    public void testReleaseWhenExceptionsThrown() throws Exception
    {
        answerSetLinkTimeout(SESSION_ID);
        serviceHelper.initialize(myApplicationName, DEVICE_ID, APP_ID);
        serviceHelper.initNotificationReceiver(mockNotificationReceiver);
        serviceHelper.waitForNextDeviceAnnouncement(10, TimeUnit.MILLISECONDS);
        serviceHelper.connectAboutClient(mockAboutAnnouncementDetails);

        when(mockBusAttachment.removeMatch(anyString())).thenReturn(Status.FAIL);
        doThrow(new NotificationServiceException()).when(mockNotificationService).shutdown();
        doThrow(new Exception()).when(mockAboutServiceImpl).stopAboutClient();
        doThrow(new Exception()).when(mockConfigService).stopConfigClient();

        serviceHelper.release();

        verify(mockAboutServiceImpl).stopAboutClient();
        verify(mockConfigService).stopConfigClient();
        verify(mockNotificationService).shutdown();
        verify(mockBusAttachmentMgr).release();
    }

    @Test
    public void testGetBusUniqueName() throws BusException
    {
        when(mockBusAttachmentMgr.getBusUniqueName()).thenReturn("busName");
        serviceHelper.initialize(myApplicationName, DEVICE_ID, APP_ID);
        assertEquals("busName", serviceHelper.getBusUniqueName());
    }

    @SuppressWarnings("rawtypes")
    private void answerSetLinkTimeout(final int sessionId)
    {
        doAnswer(new Answer()
        {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable
            {
                Object[] objects = invocation.getArguments();
                assertEquals(Integer.valueOf(sessionId), (Integer) objects[0]);
                assertEquals(Integer.valueOf(LINK_TIMEOUT_IN_SECONDS), Integer.valueOf(((IntegerValue) objects[1]).value));

                return Status.OK;
            }
        }).when(mockBusAttachment).setLinkTimeout(anyInt(), (IntegerValue) anyObject());
    }

    @Test
    public void testClearQueuedDeviceAnnouncements() throws Exception
    {
        serviceHelper.initialize(myApplicationName, DEVICE_ID, APP_ID);
        serviceHelper.clearQueuedDeviceAnnouncements();
        verify(mockDeviceAnnouncementHandler).clearQueuedDeviceAnnouncements();
    }

    @Test
    public void testSetAuthPassword() throws Exception
    {
        when(mockPasswordStore.getPassword(serviceName)).thenReturn(currentPassword);

        serviceHelper.initialize(myApplicationName, DEVICE_ID, APP_ID);
        AboutAnnouncementDetails aboutAnnouncementDetails = serviceHelper.waitForNextDeviceAnnouncement(10, TimeUnit.MILLISECONDS);
        char[] newPassword = "111111".toCharArray();

        serviceHelper.setAuthPassword(aboutAnnouncementDetails, newPassword);

        verify(mockPasswordStore).setPassword(serviceName, newPassword);
        verify(mockBusAttachment).clearKeyStore();
    }

    @Test
    public void testSetAuthPasswordWithNull() throws Exception
    {
        when(mockPasswordStore.getPassword(serviceName)).thenReturn(null);

        serviceHelper.initialize(myApplicationName, DEVICE_ID, APP_ID);
        AboutAnnouncementDetails aboutAnnouncementDetails = serviceHelper.waitForNextDeviceAnnouncement(10, TimeUnit.MILLISECONDS);

        serviceHelper.setAuthPassword(aboutAnnouncementDetails, null);

        verify(mockPasswordStore).setPassword(serviceName, SrpAnonymousKeyListener.DEFAULT_PINCODE);
        verify(mockBusAttachment).clearKeyStore();
    }

    @Test
    public void testGetAuthPassword() throws Exception
    {
        char[] newPassword = "111111".toCharArray();
        when(mockPasswordStore.getPassword(serviceName)).thenReturn(newPassword);

        serviceHelper.initialize(myApplicationName, DEVICE_ID, APP_ID);
        AboutAnnouncementDetails aboutAnnouncementDetails = serviceHelper.waitForNextDeviceAnnouncement(10, TimeUnit.MILLISECONDS);

        char[] authPassword = serviceHelper.getAuthPassword(aboutAnnouncementDetails);
        assertEquals(newPassword, authPassword);

        verify(mockPasswordStore).getPassword(serviceName);
    }

    @Test
    public void testGetAuthPasswordWhenNull() throws Exception
    {
        when(mockPasswordStore.getPassword(serviceName)).thenReturn(null);

        serviceHelper.initialize(myApplicationName, DEVICE_ID, APP_ID);
        AboutAnnouncementDetails aboutAnnouncementDetails = serviceHelper.waitForNextDeviceAnnouncement(10, TimeUnit.MILLISECONDS);

        char[] authPassword = serviceHelper.getAuthPassword(aboutAnnouncementDetails);
        assertEquals(SrpAnonymousKeyListener.DEFAULT_PINCODE, authPassword);

        verify(mockPasswordStore).getPassword(serviceName);
    }

    @Test
    public void testIsPeerAuthenticationAttempted() throws Exception
    {
        when(mockAuthPasswordHandlerImpl.isPeerAuthenticated(serviceName)).thenReturn(true).thenReturn(false);

        serviceHelper.initialize(myApplicationName, DEVICE_ID, APP_ID);
        AboutAnnouncementDetails aboutAnnouncementDetails = serviceHelper.waitForNextDeviceAnnouncement(10, TimeUnit.MILLISECONDS);

        assertTrue(serviceHelper.isPeerAuthenticationAttempted(aboutAnnouncementDetails));
        assertFalse(serviceHelper.isPeerAuthenticationAttempted(aboutAnnouncementDetails));
    }

    @Test
    public void testIsPeerAuthenticationSuccessful() throws Exception
    {
        when(mockAuthPasswordHandlerImpl.isPeerAuthenticationSuccessful(serviceName)).thenReturn(true).thenReturn(false);

        serviceHelper.initialize(myApplicationName, DEVICE_ID, APP_ID);
        AboutAnnouncementDetails aboutAnnouncementDetails = serviceHelper.waitForNextDeviceAnnouncement(10, TimeUnit.MILLISECONDS);

        assertTrue(serviceHelper.isPeerAuthenticationSuccessful(aboutAnnouncementDetails));
        assertFalse(serviceHelper.isPeerAuthenticationSuccessful(aboutAnnouncementDetails));
    }

    @Test
    public void testClearPeerAuthenticationFlags() throws Exception
    {
        serviceHelper.initialize(myApplicationName, DEVICE_ID, APP_ID);
        AboutAnnouncementDetails aboutAnnouncementDetails = serviceHelper.waitForNextDeviceAnnouncement(10, TimeUnit.MILLISECONDS);
        serviceHelper.clearPeerAuthenticationFlags(aboutAnnouncementDetails);

        verify(mockAuthPasswordHandlerImpl).resetAuthentication(serviceName);
    }

    @Test
    public void testGetProxyBusObject() throws Exception
    {
        serviceHelper.initialize(myApplicationName, DEVICE_ID, APP_ID);
        AboutAnnouncementDetails aboutAnnouncementDetails = serviceHelper.waitForNextDeviceAnnouncement(10, TimeUnit.MILLISECONDS);

        AboutClient aboutClient = serviceHelper.connectAboutClient(aboutAnnouncementDetails);

        String path = "path";
        Class<?>[] classes = new Class<?>[]
        { ServiceHelperTest.class };
        serviceHelper.getProxyBusObject(aboutClient, path, classes);

        verify(mockBusAttachment).getProxyBusObject(serviceName, path, SESSION_ID, classes);
    }

    @Test
    public void testRegisterSignalHandler() throws Exception
    {
        serviceHelper.initialize(myApplicationName, DEVICE_ID, APP_ID);
        Object mockObject = mock(Object.class);

        serviceHelper.registerSignalHandler(mockObject);

        verify(mockBusAttachmentMgr).registerSignalHandler(mockObject);
    }

    @Test
    public void testRegisterBusObject() throws Exception
    {
        serviceHelper.initialize(myApplicationName, DEVICE_ID, APP_ID);
        BusObject mockBusObject = mock(BusObject.class);

        String path = "path";
        serviceHelper.registerBusObject(mockBusObject, path);

        verify(mockBusAttachmentMgr).registerBusObject(mockBusObject, path);
    }

    @Test
    public void testClearKeyStore() throws Exception
    {
        serviceHelper.initialize(myApplicationName, DEVICE_ID, APP_ID);
        serviceHelper.clearKeyStore();

        verify(mockBusAttachment).clearKeyStore();
    }

}