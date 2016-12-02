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
package org.alljoyn.validation.simulator.notification;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.alljoyn.bus.BusAttachment;
import org.alljoyn.bus.Status;
import org.alljoyn.bus.Variant;
import org.alljoyn.bus.annotation.BusSignalHandler;
import org.alljoyn.ns.Notification;
import org.alljoyn.ns.NotificationMessageType;
import org.alljoyn.ns.NotificationReceiver;
import org.alljoyn.ns.NotificationServiceException;
import org.alljoyn.ns.NotificationText;
import org.alljoyn.ns.transport.TransportNotificationText;
import org.alljoyn.services.common.utils.TransportUtil;

import android.util.Log;

public class NotificationSignalHandler implements NotificationTransport
{

    private static final String TAG = "NotifSignalHandler";
    private NotificationReceiver notificationReceiver;
    private BusAttachment busAttachment;

    public NotificationSignalHandler(NotificationReceiver notificationReceiver, BusAttachment busAttachment)
    {
        this.notificationReceiver = notificationReceiver;
        this.busAttachment = busAttachment;
    }

    public void initialize()
    {
        Log.d(TAG, "Registering to receive notifications");

        Status status = busAttachment.registerBusObject(this, "/producerReceiver");
        checkStatus("registerBusObject /producerReceiver", status, true);

        status = busAttachment.registerSignalHandlers(this);
        checkStatus("registerSignalHandlers for receiving notifications", status, true);

        status = busAttachment.addMatch("type='signal'");
        checkStatus("addMatch", status, true);

        status = busAttachment.addMatch("sessionless='t',type='error'");
        checkStatus("addMatch", status, true);
    }

    private void checkStatus(String msg, Status status, boolean throwException)
    {
        if (!status.equals(Status.OK))
        {
            Log.e(TAG, msg + " returned status: " + status);
            if (throwException)
            {
                throw new RuntimeException(status.toString());
            }
        }
    }

    public void release()
    {
        busAttachment.unregisterSignalHandlers(this);
        busAttachment.unregisterBusObject(this);
        busAttachment.removeMatch("type='signal'");
        busAttachment.removeMatch("sessionless='t',type='error'");
    }

    @Override
    @BusSignalHandler(iface = "org.alljoyn.Notification", signal = "notify")
    public void notify(int version, final int msgId, short messageType, final String deviceId, final String deviceName, final byte[] appId, final String appName,
            Map<Integer, Variant> attributes, Map<String, String> customAttributes, TransportNotificationText[] text)
    {
        try
        {
            Log.d(TAG, "Receiving notification");

            NotificationMessageType msgType = NotificationMessageType.getMsgTypeById(messageType);

            List<NotificationText> msgTexts = new ArrayList<NotificationText>();
            for (TransportNotificationText notificationText : text)
            {
                msgTexts.add(new NotificationText(notificationText.language, notificationText.text));
            }

            final String sender = busAttachment.getMessageContext().sender;

            Notification notificationMsg = new Notification(msgType, msgTexts)
            {
                public int getMessageId()
                {
                    return msgId;
                }

                public String getDeviceId()
                {
                    return deviceId;
                }

                public UUID getAppId()
                {
                    return TransportUtil.byteArrayToUUID(appId);
                }

                public String getDeviceName()
                {
                    return deviceName;
                }

                public String getAppName()
                {
                    return appName;
                };

                public String getSenderBusName()
                {
                    return sender;
                };

            };

            notificationReceiver.receive(notificationMsg);
        }
        catch (NotificationServiceException e)
        {
            Log.w(TAG, "Exception receiving notification", e);
        }
    }
}