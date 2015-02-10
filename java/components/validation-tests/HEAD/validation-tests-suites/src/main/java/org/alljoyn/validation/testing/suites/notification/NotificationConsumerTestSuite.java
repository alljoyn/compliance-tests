/*******************************************************************************
 *  Copyright AllSeen Alliance. All rights reserved.
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
package org.alljoyn.validation.testing.suites.notification;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.alljoyn.about.AboutKeys;
import org.alljoyn.ns.NotificationMessageType;
import org.alljoyn.ns.NotificationSender;
import org.alljoyn.ns.NotificationText;
import org.alljoyn.services.android.storage.Property;
import org.alljoyn.services.android.storage.PropertyStoreImpl;
import org.alljoyn.services.android.utils.AndroidLogger;
import org.alljoyn.services.common.PropertyStore;
import org.alljoyn.validation.framework.UserInputDetails;
import org.alljoyn.validation.framework.UserResponse;
import org.alljoyn.validation.framework.ValidationBaseTestCase;
import org.alljoyn.validation.framework.annotation.Disabled;
import org.alljoyn.validation.framework.annotation.ValidationSuite;
import org.alljoyn.validation.framework.annotation.ValidationTest;
import org.alljoyn.validation.testing.utils.log.Logger;
import org.alljoyn.validation.testing.utils.log.LoggerFactory;
import org.alljoyn.validation.testing.utils.services.ServiceHelper;

import android.content.Context;

@ValidationSuite(name = "Notification-Consumer-v1")
public class NotificationConsumerTestSuite extends ValidationBaseTestCase
{
    private PropertyStore propertyStore;
    private NotificationSender nSender;

    private org.alljoyn.ns.Notification notif;

    private ServiceHelper serviceHelper;

    private UUID consumerAppId = UUID.randomUUID();
    private String consumerDeviceId = consumerAppId.toString();

    private static final String BUS_APPLICATION_NAME = "NotificationConsumerTestSuite";
    private static final String TAG = "NotifConsumerTestSuite";
    private static final Logger logger = LoggerFactory.getLogger(TAG);

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();

        try
        {
            logger.debug(String.format("Running NotificationConsumer test case using Device ID: %s", consumerDeviceId));
            logger.debug(String.format("Running NotificationConsumer test case using App ID: %s", consumerAppId));

            propertyStore = getPropertyStoreImpl();

            serviceHelper = getServiceHelper();
            serviceHelper.initialize(BUS_APPLICATION_NAME, null, null);

            serviceHelper.startAboutServer((short) 1080, propertyStore);

            nSender = serviceHelper.initNotificationSender(propertyStore);
        }
        catch (Exception e)
        {
            logger.error("Received exception during setup", e);
            releaseResources();
            throw e;
        }
    }

    @Override
    protected void tearDown() throws Exception
    {
        super.tearDown();

        releaseResources();
    }

    @ValidationTest(name = "Notification-Consumer-v1-01")
    public void testNotification_Consumer_v1_01() throws Exception
    {
        String[] msgArray =
        { "Test Msg 1", "Test Msg 2", "Test Msg 3" };

        String msgToSend = msgArray[getRandomNumber(msgArray.length)];

        List<NotificationText> text = new LinkedList<NotificationText>();

        text.add(new NotificationText("en", msgToSend));
        notif = new org.alljoyn.ns.Notification(NotificationMessageType.INFO, text);

        nSender.send(notif, 120);

        checkUserInput(msgArray, msgToSend);
    }

    @ValidationTest(name = "Notification-Consumer-v1-02")
    public void testNotification_Consumer_v1_02() throws Exception
    {
        String[] msgArray =
        { "Two languages Msg 1", "Two languages Msg 2", "Two languages Msg 3" };

        String msgToSend = msgArray[getRandomNumber(msgArray.length)];

        List<NotificationText> text = new LinkedList<NotificationText>();

        text.add(new NotificationText("en", msgToSend));
        text.add(new NotificationText("fr", msgToSend));
        notif = new org.alljoyn.ns.Notification(NotificationMessageType.INFO, text);

        nSender.send(notif, 120);

        checkUserInput(msgArray, msgToSend);
    }

    @Disabled
    @ValidationTest(name = "Notification-Consumer-v1-03")
    public void testNotification_Consumer_v1_03() throws Exception
    {
        String[] msgArray =
        { "No langTag Msg 1", "No langTag Msg 2", "No langTag Msg 3" };

        final String msgToSend = msgArray[getRandomNumber(msgArray.length)];

        List<NotificationText> text = new LinkedList<NotificationText>();

        text.add(new NotificationText()
        {
            @Override
            public String getLanguage()
            {
                return "";
            }

            @Override
            public String getText()
            {
                return msgToSend;
            }
        });

        notif = new org.alljoyn.ns.Notification(NotificationMessageType.INFO, text);

        nSender.send(notif, 120);
        checkUserInput(msgArray, msgToSend);
    }

    @ValidationTest(name = "Notification-Consumer-v1-04")
    public void testNotification_Consumer_v1_04() throws Exception
    {
        String[] msgArray =
        { "Invalid langTag Msg 1", "Invalid langTag Msg 2", "Invalid langTag Msg 3" };

        String msgToSend = msgArray[getRandomNumber(msgArray.length)];

        List<NotificationText> text = new LinkedList<NotificationText>();

        text.add(new NotificationText("INVALID", msgToSend));

        notif = new org.alljoyn.ns.Notification(NotificationMessageType.INFO, text);

        nSender.send(notif, 120);

        checkUserInput(msgArray, msgToSend);
    }

    @ValidationTest(name = "Notification-Consumer-v1-05")
    public void testNotification_Consumer_v1_05() throws Exception
    {
        List<List<MsgSet>> msgSets = new ArrayList<List<MsgSet>>();

        List<MsgSet> msgSet = new ArrayList<MsgSet>();
        msgSet.add(new MsgSet("Priority Msg 1", NotificationMessageType.EMERGENCY));
        msgSet.add(new MsgSet("Priority Msg 2", NotificationMessageType.WARNING));
        msgSet.add(new MsgSet("Priority Msg 3", NotificationMessageType.INFO));
        msgSets.add(msgSet);

        msgSet = new ArrayList<MsgSet>();
        msgSet.add(new MsgSet("Priority Msg 1", NotificationMessageType.EMERGENCY));
        msgSet.add(new MsgSet("Priority Msg 2", NotificationMessageType.INFO));
        msgSet.add(new MsgSet("Priority Msg 3", NotificationMessageType.WARNING));
        msgSets.add(msgSet);

        msgSet = new ArrayList<MsgSet>();
        msgSet.add(new MsgSet("Priority Msg 1", NotificationMessageType.WARNING));
        msgSet.add(new MsgSet("Priority Msg 2", NotificationMessageType.EMERGENCY));
        msgSet.add(new MsgSet("Priority Msg 3", NotificationMessageType.INFO));
        msgSets.add(msgSet);

        msgSet = new ArrayList<MsgSet>();
        msgSet.add(new MsgSet("Priority Msg 1", NotificationMessageType.WARNING));
        msgSet.add(new MsgSet("Priority Msg 2", NotificationMessageType.INFO));
        msgSet.add(new MsgSet("Priority Msg 3", NotificationMessageType.EMERGENCY));
        msgSets.add(msgSet);

        msgSet = new ArrayList<MsgSet>();
        msgSet.add(new MsgSet("Priority Msg 1", NotificationMessageType.INFO));
        msgSet.add(new MsgSet("Priority Msg 2", NotificationMessageType.EMERGENCY));
        msgSet.add(new MsgSet("Priority Msg 3", NotificationMessageType.WARNING));
        msgSets.add(msgSet);

        msgSet = new ArrayList<MsgSet>();
        msgSet.add(new MsgSet("Priority Msg 1", NotificationMessageType.INFO));
        msgSet.add(new MsgSet("Priority Msg 2", NotificationMessageType.WARNING));
        msgSet.add(new MsgSet("Priority Msg 3", NotificationMessageType.EMERGENCY));
        msgSets.add(msgSet);

        List<MsgSet> messagesToSend = msgSets.get(getRandomNumber(msgSets.size()));

        for (MsgSet messageToSend : messagesToSend)
        {
            List<NotificationText> text = new LinkedList<NotificationText>();

            text.add(new NotificationText("en", messageToSend.text));

            notif = new org.alljoyn.ns.Notification(messageToSend.priority, text);

            nSender.send(notif, 120);
        }

        List<String> selectionOptions = new ArrayList<String>();
        for (List<MsgSet> messageSet : msgSets)
        {
            selectionOptions.add(buildPromptText(messageSet));
        }

        String msgSent = buildPromptText(messagesToSend);

        checkUserInput(selectionOptions.toArray(new String[selectionOptions.size()]), msgSent);
    }

    @ValidationTest(name = "Notification-Consumer-v1-06")
    public void testNotification_Consumer_v1_06() throws Exception
    {
        String[] msgArray =
        { "Msg w/ attributes 1", "Msg w/ attributes 2", "Msg w/ attributes 3" };

        String msgToSend = msgArray[getRandomNumber(msgArray.length)];

        Map<String, String> customAttributes = new HashMap<String, String>();
        customAttributes.put("org.alljoyn.validation.test", "value");

        List<NotificationText> text = new LinkedList<NotificationText>();

        text.add(new NotificationText("en", msgToSend));

        notif = new org.alljoyn.ns.Notification(NotificationMessageType.INFO, text);
        notif.setCustomAttributes(customAttributes);
        nSender.send(notif, 120);

        checkUserInput(msgArray, msgToSend);
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

    protected String buildPromptText(List<MsgSet> messagesToSend)
    {
        StringBuilder msgSent = new StringBuilder();
        for (MsgSet messageToSend : messagesToSend)
        {
            if (msgSent.length() > 0)
            {
                msgSent.append("; ");
            }
            msgSent.append(messageToSend.text);
            msgSent.append(" (");
            msgSent.append(messageToSend.priority);
            msgSent.append(")");
        }
        return msgSent.toString();
    }

    private void checkUserInput(String[] msgArray, String msgToSend) throws InterruptedException
    {
        UserInputDetails userInputDetails = new UserInputDetails("Select the message(s) received", msgArray);

        UserResponse userResponse = getValidationTestContext().waitForUserInput(userInputDetails);

        assertTrue("A message option was not selected", userResponse.getOptionSelected() >= 0 && userResponse.getOptionSelected() < msgArray.length);
        String messageSelected = msgArray[userResponse.getOptionSelected()];
        assertEquals("Incorrect message option selected", msgToSend, messageSelected);
    }

    protected int getRandomNumber(int n)
    {
        Random r = new Random();
        return r.nextInt(n);
    }

    private void releaseResources()
    {
        if (serviceHelper != null)
        {
            serviceHelper.release();
        }
    }

    protected ServiceHelper getServiceHelper()
    {
        return new ServiceHelper(new AndroidLogger());
    }

    protected PropertyStore getPropertyStoreImpl()
    {
        PropertyStoreImpl propertyStoreImpl = new PropertyStoreImpl((Context) getValidationTestContext().getContext());
        propertyStoreImpl.setValue(AboutKeys.ABOUT_DEVICE_NAME, "NotificationConsumerTest", Property.NO_LANGUAGE);
        propertyStoreImpl.setValue(AboutKeys.ABOUT_APP_NAME, "NotificationConsumerTest", Property.NO_LANGUAGE);
        propertyStoreImpl.setValue(AboutKeys.ABOUT_APP_ID, getAppId(), Property.NO_LANGUAGE);
        propertyStoreImpl.setValue(AboutKeys.ABOUT_DEVICE_ID, getDeviceId(), Property.NO_LANGUAGE);

        return propertyStoreImpl;
    }

    public String getDeviceId()
    {
        return consumerDeviceId;
    }

    public UUID getAppId()
    {
        return consumerAppId;
    }

}