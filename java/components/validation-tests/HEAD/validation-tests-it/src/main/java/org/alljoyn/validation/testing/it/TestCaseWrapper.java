/*******************************************************************************
 *   *  Copyright (c) Open Connectivity Foundation (OCF) and AllJoyn Open
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
package org.alljoyn.validation.testing.it;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;

import org.alljoyn.about.AboutKeys;
import org.alljoyn.about.transport.IconTransport;
import org.alljoyn.bus.BusException;
import org.alljoyn.bus.Variant;
import org.alljoyn.ns.Notification;
import org.alljoyn.ns.NotificationMessageType;
import org.alljoyn.ns.NotificationText;
import org.alljoyn.ns.transport.TransportNotificationText;
import org.alljoyn.ns.transport.TransportRichAudioUrl;
import org.alljoyn.services.common.utils.TransportUtil;
import org.alljoyn.validation.framework.AppUnderTestDetails;
import org.alljoyn.validation.framework.UserInputDetails;
import org.alljoyn.validation.framework.UserResponse;
import org.alljoyn.validation.framework.ValidationBaseTestCase;
import org.alljoyn.validation.framework.ValidationTestCase;
import org.alljoyn.validation.simulator.DUTSimulator;
import org.alljoyn.validation.simulator.SimpleDeviceDetails;
import org.alljoyn.validation.testing.ValidationTestContextImpl;
import org.alljoyn.validation.testing.suites.about.AboutTestSuite;
import org.alljoyn.validation.testing.suites.audio.AudioTestSuite;
import org.alljoyn.validation.testing.suites.notification.NotificationConsumerTestSuite;
import org.alljoyn.validation.testing.suites.notification.NotificationProducerTestSuite;
import org.alljoyn.validation.testing.utils.notification.NotificationHandler;

import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.test.AndroidTestCase;
import android.util.Log;

public class TestCaseWrapper extends AndroidTestCase
{
    private static final String AUDIO_SINK_STREAM_OBJECT_PATH = "/Speaker/In";
    private static final String AUDIO_SINK_APPLICATION_DEVICE_ID = "Android";
    private static final String AUDIO_SINK_APPLICATION_ACTIVITY_CLASS_NAME = "org.alljoyn.services.audio.sink.EmptyActivity";
    private static final String AUDIO_SINK_APPLICATION_ACTIVITY_PACKAGE_NAME = "org.alljoyn.services.audio.sink";
    private static final int RICH_NOTIFICATION_ICON_URL = 0;
    private static final int RICH_NOTIFICATION_AUDIO_URL = 1;
    private static final int RESPONSE_OBJECT_PATH = 4;
    private static final String TAG = "TestCaseWrapper";

    protected TestCase testCase;
    private Bitmap iconBitmap;
    private byte[] imageByteArr;
    protected DUTSimulator simulator = null;
    private RegisterInterfaces registerInterfaces;
    private NotificationHandler notificationHandler;
    private int receivedMsgs = 0;

    public TestCaseWrapper(Class<? extends TestCase> clazz, String methodName) throws Exception
    {
        if (!ValidationTestCase.class.isAssignableFrom(clazz))
        {
            throw new RuntimeException("Class must implement ValidationTestCase");
        }
        testCase = (ValidationBaseTestCase) clazz.newInstance();
        setName(methodName);
    }

    public TestCaseWrapper(TestCase testCase) throws Exception
    {
        this.testCase = testCase;
        setName(testCase.getName());
    }

    private class MsgSet
    {
        public MsgSet(String text, NotificationMessageType priority)
        {
            this.text = text;
            this.priority = priority;
        }

        public String text;
        public NotificationMessageType priority;
    }

    @Override
    protected void setUp() throws Exception
    {
        try
        {
            launchAudioSinkApplication();
            loadIconBitmap();
            String absolutePath = getContext().getFileStreamPath("alljoyn_keystore").getAbsolutePath();

            SimpleDeviceDetails simpleDeviceDetails = new SimpleDeviceDetails();
            simpleDeviceDetails.setDeviceId(simpleDeviceDetails.getAppId().toString());
            setupSimulatorForTest(absolutePath, simpleDeviceDetails);

            ValidationTestContextImpl testContext = null;

            if (testCase instanceof NotificationConsumerTestSuite)
            {
                testContext = setupForNotificationConsumer();
            }
            else
            {
                testContext = new ValidationTestContextImpl()
                {
                    @Override
                    public UserResponse waitForUserInput(UserInputDetails userInputDetails) throws InterruptedException
                    {
                        Thread.sleep(5000);
                        return new UserResponse();
                    }
                };
            }

            simulator.start();

            setAppUnderTestDetails(testContext);

            testContext.setContext(getContext());

            setTestObjectPath(testContext);
            testContext.setKeyStorePath(absolutePath);
            ((ValidationTestCase) testCase).setValidationTestContext(testContext);
            testCase.setName(getName());

            if (testCase instanceof NotificationProducerTestSuite)
            {
                sendNotifications(simulator);
            }

            if ((testCase instanceof AboutTestSuite) && (getName().equals("About-v1-05")))
            {
                registerInterfaces = new RegisterInterfaces(simulator.getBusAttachment());
                registerInterfaces.registerOtherObjects();
            }
        }
        catch (Exception e)
        {
            try
            {
                releaseResources();
            }
            catch (Exception ex)
            {
                Log.w(TAG, "Exception calling releaseResources", ex);
            }
            throw e;
        }
    }

    protected ValidationTestContextImpl setupForNotificationConsumer()
    {
        ValidationTestContextImpl testContext;
        NotificationConsumerTestSuite notificationTestCase = (NotificationConsumerTestSuite) testCase;
        notificationHandler = new NotificationHandler();
        notificationHandler.initializeForDevice(notificationTestCase.getDeviceId(), notificationTestCase.getAppId());
        simulator.setListenForNotifications(true);
        simulator.setNotificationReceiver(notificationHandler);

        int msgCount = 1;
        if ("testNotification_Consumer_v1_05".equals(getName()))
        {
            msgCount = 3;
        }

        final int numMsg = msgCount;

        testContext = new ValidationTestContextImpl()
        {
            @Override
            public UserResponse waitForUserInput(UserInputDetails userInputDetails) throws InterruptedException
            {
                return determineUserResponse(numMsg, userInputDetails);
            }

        };
        return testContext;
    }

    protected UserResponse determineUserResponse(final int numMsg, UserInputDetails userInputDetails) throws InterruptedException
    {
        List<MsgSet> receivedMessages = new ArrayList<MsgSet>();
        while (receivedMsgs < numMsg)
        {
            Notification notificationMsg = notificationHandler.getReceivedNotification(5, TimeUnit.SECONDS);
            if (notificationMsg == null)
            {
                Log.e(TAG, "Timed out waiting for notification");
                return new UserResponse();
            }
            receivedMsgs++;
            List<NotificationText> texts = notificationMsg.getText();
            if (texts.size() > 0)
            {
                receivedMessages.add(new MsgSet(texts.get(0).getText(), notificationMsg.getMessageType()));
            }
        }
        UserResponse userResponse = null;
        if (numMsg == 1)
        {
            int optionSelected = -1;
            String textReceived = receivedMessages.get(0).text;
            for (int i = 0; i < userInputDetails.getOptions().length; i++)
            {
                if (userInputDetails.getOptions()[i].equals(textReceived))
                {
                    optionSelected = i;
                    break;
                }
            }
            userResponse = new UserResponse();
            userResponse.setOptionSelected(optionSelected);
        }
        else
        {
            int optionSelected = -1;
            for (int i = 0; i < userInputDetails.getOptions().length; i++)
            {
                String optionText = userInputDetails.getOptions()[i];
                int numMatches = 0;
                for (MsgSet receivedMsg : receivedMessages)
                {
                    String msgText = String.format("%s (%s)", receivedMsg.text, receivedMsg.priority);
                    if (optionText.contains(msgText))
                    {
                        numMatches++;
                    }
                }
                if (numMatches == numMsg)
                {
                    optionSelected = i;
                    break;
                }
            }
            userResponse = new UserResponse();
            userResponse.setOptionSelected(optionSelected);
        }
        return userResponse;
    }

    protected void setAboutInterfaceForSimulator()
    {
    }

    @Override
    protected void runTest() throws Throwable
    {
        testCase.runBare();
    }

    @Override
    protected void tearDown() throws Exception
    {
        releaseResources();
    }

    private void releaseResources()
    {
        if (registerInterfaces != null)
        {
            registerInterfaces.unregisterOtherObjects();
        }

        if (simulator != null)
        {
            simulator.stop();
            simulator = null;
        }
    }

    private void launchAudioSinkApplication()
    {
        if (testCase instanceof AudioTestSuite)
        {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setComponent(new ComponentName(AUDIO_SINK_APPLICATION_ACTIVITY_PACKAGE_NAME, AUDIO_SINK_APPLICATION_ACTIVITY_CLASS_NAME));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getContext().startActivity(intent);
        }
    }

    private void setAppUnderTestDetails(ValidationTestContextImpl testContext)
    {

        if (testCase instanceof AudioTestSuite)
        {
            byte[] byteArray =
            { (byte) 0xc8, 0x74, (byte) 0xb3, (byte) 0xa9, 0x6d, 0x56, 0x47, (byte) 0xe1, (byte) 0xb8, 0x1f, 0x47, 0x05, 0x48, (byte) 0xc2, 0x01, 0x5e };
            testContext.setAppUnderTestDetails(new AppUnderTestDetails(TransportUtil.byteArrayToUUID(byteArray), AUDIO_SINK_APPLICATION_DEVICE_ID));
        }
        else
        {
            testContext.setAppUnderTestDetails(new AppUnderTestDetails(simulator.getAppId(), simulator.getDeviceId()));
        }
    }

    private void setTestObjectPath(ValidationTestContextImpl testContext)
    {
        if (testCase instanceof AudioTestSuite)
        {
            testContext.setTestObjectPath(AUDIO_SINK_STREAM_OBJECT_PATH);
        }
    }

    private void loadIconBitmap() throws IOException
    {
        InputStream is = getClass().getClassLoader().getResourceAsStream("sample_app_icon.png");
        iconBitmap = BitmapFactory.decodeStream(is);
        is.close();

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        iconBitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
        imageByteArr = outStream.toByteArray();
        outStream.close();
    }

    @Override
    public String toString()
    {
        return testCase.toString();
    }

    private void sendNotifications(DUTSimulator simulator) throws BusException
    {
        int msgId = 1;
        byte appId[] = TransportUtil.uuidToByteArray(simulator.getAppId());
        String deviceId = simulator.getDeviceId();
        String deviceName = getDeviceName(simulator);
        String appName = getAppName(simulator);
        Map<Integer, Variant> attributes = new HashMap<Integer, Variant>();
        Map<String, String> customAttributes = new HashMap<String, String>();

        attributes.put(RESPONSE_OBJECT_PATH, new Variant("/About", "s"));
        attributes.put(RICH_NOTIFICATION_ICON_URL, new Variant("http://example.com/icon.png", "s"));

        TransportRichAudioUrl[] audioUrlArray = new TransportRichAudioUrl[1];
        audioUrlArray[0] = new TransportRichAudioUrl("en", "http://example.com/audio.wav");
        attributes.put(RICH_NOTIFICATION_AUDIO_URL, new Variant(audioUrlArray, "ar"));

        TransportNotificationText[] text = new TransportNotificationText[1];
        text[0] = new TransportNotificationText("en", "Emergency Msg 1");
        simulator.sendNotification(2, msgId++, (short) 0, deviceId, deviceName, appId, appName, attributes, customAttributes, text);

        text = new TransportNotificationText[1];
        text[0] = new TransportNotificationText("en", "Warning Msg 1");
        simulator.sendNotification(2, msgId++, (short) 1, deviceId, deviceName, appId, appName, attributes, customAttributes, text);

        text = new TransportNotificationText[1];
        text[0] = new TransportNotificationText("en", "Info Msg 1");
        simulator.sendNotification(2, msgId++, (short) 2, deviceId, deviceName, appId, appName, attributes, customAttributes, text);
    }

    private String getAppName(DUTSimulator simulator) throws BusException
    {
        return getAboutKeyValue(simulator, AboutKeys.ABOUT_APP_NAME);
    }

    private String getDeviceName(DUTSimulator simulator) throws BusException
    {
        return getAboutKeyValue(simulator, AboutKeys.ABOUT_DEVICE_NAME);
    }

    private String getAboutKeyValue(DUTSimulator simulator, String aboutKey) throws BusException
    {
        String keyValue = null;
        Map<String, Variant> aboutMap = simulator.getAbout("en");
        keyValue = aboutMap.get(aboutKey).getObject(String.class);
        return keyValue;

    }

    private void setupSimulatorForTest(String absolutePath, SimpleDeviceDetails simpleDeviceDetails)
    {
        simulator = new DUTSimulator(simpleDeviceDetails);
        simulator.setKeyStorePath(absolutePath);
        simulator.setSupportsConfig(true);
        simulator.setSupportsNotificationProducer(true);
        simulator.setControlPanelSupported(true);
        //simulator.setGWAgentSupported(true);
        setAboutInterfaceForSimulator();
        setAboutIconInterfaceForSimulator();
    }

    private void setAboutIconInterfaceForSimulator()
    {
        simulator.setAboutIconInterface(new IconTransport()
        {
            @Override
            public short getVersion() throws BusException
            {
                return 1;
            }

            @Override
            public String getMimeType() throws BusException
            {
                return "image/png";
            }

            @Override
            public int getSize() throws BusException
            {
                return imageByteArr.length;
            }

            @Override
            public String GetUrl() throws BusException
            {
                return "http://sample.com/icon.png";
            }

            @Override
            public byte[] GetContent() throws BusException
            {
                return imageByteArr;
            }
        });
    }
}