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
package org.alljoyn.validation.testing.utils.notification;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.alljoyn.ns.Notification;
import org.alljoyn.ns.NotificationMessageType;
import org.alljoyn.ns.NotificationServiceException;
import org.alljoyn.ns.NotificationText;
import org.alljoyn.ns.RichAudioUrl;
import org.alljoyn.validation.framework.AppUnderTestDetails;
import org.alljoyn.validation.framework.ValidationTestContext;
import org.alljoyn.validation.framework.utils.introspection.BusIntrospector;
import org.alljoyn.validation.testing.utils.MyRobolectricTestRunner;
import org.alljoyn.validation.testing.utils.about.AboutAnnouncementDetails;
import org.alljoyn.validation.testing.utils.notification.NotificationValidator.NotificationValidationExceptionHandler;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.annotation.Config;

@RunWith(MyRobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class NotificationValidatorTest
{

    private static final int NOTIFICATION_SERVICE_VERSION = 2;
    private static final int INVALID_NOTIFICATION_SERVICE_VERSION = 1;
    @Mock
    private ValidationTestContext mockTestContext;
    @Mock
    private AboutAnnouncementDetails mockAboutAnnouncement;
    @Mock
    private BusIntrospector mockBusIntrospector;
    @Mock
    private NotificationHandler mockNotificationHandler;

    private Exception thrownException;
    private String appName = "appName";
    private String deviceName = "deviceName";

    private String deviceId = "deviceId";
    private UUID appId = UUID.randomUUID();
    private NotificationValidator notificationValidator;
    private AppUnderTestDetails appUnderTestDetails;

    @Before
    public void setup() throws Exception
    {
        MockitoAnnotations.initMocks(this);

        when(mockAboutAnnouncement.getAppId()).thenReturn(appId);
        when(mockAboutAnnouncement.getAppName()).thenReturn(appName);
        when(mockAboutAnnouncement.getDeviceId()).thenReturn(deviceId);
        when(mockAboutAnnouncement.getDeviceName()).thenReturn(deviceName);

        // provide the deviceId for the DUT to the test
        appUnderTestDetails = new AppUnderTestDetails(appId, deviceId);
        when(mockTestContext.getAppUnderTestDetails()).thenReturn(appUnderTestDetails);

        notificationValidator = new NotificationValidator(mockTestContext)
        {
            @Override
            protected NotificationHandler getNotificationHandler()
            {
                return mockNotificationHandler;
            }
        };

        notificationValidator.initializeForDevice(mockAboutAnnouncement, mockBusIntrospector, new NotificationValidationExceptionHandler()
        {

            @Override
            public void onNotificationValidationException(Exception exception)
            {
                exception.printStackTrace();
                thrownException = exception;
            }
        });

    }

    private Notification getBaseMockNotification() throws NotificationServiceException
    {
        Notification mockNotification = mock(Notification.class);

        when(mockNotification.getVersion()).thenReturn(NOTIFICATION_SERVICE_VERSION);
        when(mockNotification.getAppId()).thenReturn(appId);
        when(mockNotification.getDeviceName()).thenReturn(deviceName);
        when(mockNotification.getDeviceId()).thenReturn(deviceId);
        when(mockNotification.getAppName()).thenReturn(appName);
        when(mockNotification.getMessageType()).thenReturn(NotificationMessageType.EMERGENCY);
        when(mockNotification.getRichAudioUrl()).thenReturn(null);
        when(mockNotification.getRichAudioObjPath()).thenReturn(null);
        when(mockNotification.getResponseObjectPath()).thenReturn(null);

        List<NotificationText> text = new ArrayList<NotificationText>();
        text.add(new NotificationText("en", "Emergency Message"));

        Map<String, String> customAttributes = new HashMap<String, String>();
        customAttributes.put("type", "key");
        when(mockNotification.getCustomAttributes()).thenReturn(customAttributes);
        return mockNotification;
    }

    @Test
    public void testReceiveBasicTextNotification() throws Exception
    {
        List<Notification> notifications = new ArrayList<Notification>();
        List<String> notes = new ArrayList<String>();

        Notification mockNotification = getBaseMockNotification();
        notifications.add(mockNotification);

        notes.add("Notification Received: messageType: EMERGENCY; richIconUrl: false; richAudioUrl: false; responseObject: false");

        testWithNotifications(notifications, notes);

        assertEquals(1, notificationValidator.getNumberOfNotificationsReceived());
    }

    @Test
    public void testReceiveBasicTextNotificationWithWrongVersion() throws Exception
    {
        List<Notification> notifications = new ArrayList<Notification>();
        List<String> notes = new ArrayList<String>();
        Notification mockNotification = getBaseMockNotification();
        when(mockNotification.getVersion()).thenReturn(INVALID_NOTIFICATION_SERVICE_VERSION);
        notifications.add(mockNotification);

        testWithNotifications(notifications, notes);
        assertNotNull(thrownException);
        assertEquals(0, notificationValidator.getNumberOfNotificationsReceived());
    }

    @Test
    public void testNoNotifcations() throws Exception
    {
        List<Notification> notifications = new ArrayList<Notification>();
        List<String> notes = new ArrayList<String>();

        testWithNotifications(notifications, notes);

        assertEquals(0, notificationValidator.getNumberOfNotificationsReceived());
    }

    @Test
    public void testRichIconUrl() throws Exception
    {
        List<Notification> notifications = new ArrayList<Notification>();
        List<String> notes = new ArrayList<String>();

        Notification mockNotification = getBaseMockNotification();
        when(mockNotification.getRichIconUrl()).thenReturn("http://iamaiconurl.com");
        notifications.add(mockNotification);

        notes.add("Notification Received: messageType: EMERGENCY; richIconUrl: true; richAudioUrl: false; responseObject: false");

        testWithNotifications(notifications, notes);
    }

    @Test
    public void testRichIconUrlMalformed() throws Exception
    {
        List<Notification> notifications = new ArrayList<Notification>();
        List<String> notes = new ArrayList<String>();

        Notification mockNotification = getBaseMockNotification();
        when(mockNotification.getRichIconUrl()).thenReturn("malformedurl");
        notifications.add(mockNotification);

        testWithNotifications(notifications, notes);

        assertNotNull(thrownException);
        assertEquals("RichIconUrl malformed: 'malformedurl'", thrownException.getMessage());
    }

    @Test
    public void testRichAudioUrl() throws Exception
    {
        List<Notification> notifications = new ArrayList<Notification>();
        List<String> notes = new ArrayList<String>();

        Notification mockNotification = getBaseMockNotification();
        List<RichAudioUrl> audioUrl = new LinkedList<RichAudioUrl>();
        audioUrl.add(new RichAudioUrl("en", "http://iamaaudiourl.com?lang=en"));
        when(mockNotification.getRichAudioUrl()).thenReturn(audioUrl);
        notifications.add(mockNotification);

        notes.add("Notification Received: messageType: EMERGENCY; richIconUrl: false; richAudioUrl: true; responseObject: false");

        testWithNotifications(notifications, notes);
    }

    @Test
    public void testRichAudioUrlMalformed() throws Exception
    {
        List<Notification> notifications = new ArrayList<Notification>();
        List<String> notes = new ArrayList<String>();

        Notification mockNotification = getBaseMockNotification();
        List<RichAudioUrl> audioUrl = new LinkedList<RichAudioUrl>();
        audioUrl.add(new RichAudioUrl("en", "malformedurl"));
        when(mockNotification.getRichAudioUrl()).thenReturn(audioUrl);
        notifications.add(mockNotification);

        testWithNotifications(notifications, notes);

        assertNotNull(thrownException);
        assertEquals("RichAudioUrl malformed: 'malformedurl'", thrownException.getMessage());
    }

    @Test
    public void testRichAudioUrlEmptyList() throws Exception
    {
        List<Notification> notifications = new ArrayList<Notification>();
        List<String> notes = new ArrayList<String>();

        Notification mockNotification = getBaseMockNotification();
        List<RichAudioUrl> audioUrl = new LinkedList<RichAudioUrl>();
        when(mockNotification.getRichAudioUrl()).thenReturn(audioUrl);
        notifications.add(mockNotification);

        notes.add("Notification Received: messageType: EMERGENCY; richIconUrl: false; richAudioUrl: false; responseObject: false");

        testWithNotifications(notifications, notes);
    }

    @Test
    public void testResponseObjectPath() throws Exception
    {

        List<Notification> notifications = new ArrayList<Notification>();
        List<String> notes = new ArrayList<String>();

        Notification mockNotification = getBaseMockNotification();

        // TODO responseObjectPath - make sure that it is there on the bus
        when(mockNotification.getResponseObjectPath()).thenReturn("/CPS/OBJ/PATH");
        notifications.add(mockNotification);

        notes.add("Notification Received: messageType: EMERGENCY; richIconUrl: false; richAudioUrl: false; responseObject: true");

        testWithNotifications(notifications, notes);
    }

    private void testWithNotifications(final List<Notification> notifications, List<String> notes) throws Exception
    {
        when(mockNotificationHandler.getReceivedNotification()).then(new Answer<Notification>()
        {
            @Override
            public Notification answer(InvocationOnMock invocation) throws Throwable
            {
                Notification notification = null;
                if (notifications.size() > 0)
                {
                    notification = notifications.remove(0);
                }
                else
                {
                    throw new InterruptedException();
                }
                return notification;
            }
        });

        notificationValidator.run();

        verifyNotesAdded(notes);
    }

    private void verifyNotesAdded(List<String> expectedNotes)
    {
        if (expectedNotes.size() > 0)
        {
            ArgumentCaptor<String> stringCaptor = ArgumentCaptor.forClass(String.class);
            verify(mockTestContext).addNote(stringCaptor.capture());
            List<String> notes = stringCaptor.getAllValues();
            assertEquals(expectedNotes.size(), notes.size());
            for (int i = 0; i < expectedNotes.size(); i++)
            {
                assertEquals(expectedNotes.get(i), notes.get(i));
            }
        }
        else
        {
            verify(mockTestContext, times(0)).addNote(anyString());
        }
    }

}
