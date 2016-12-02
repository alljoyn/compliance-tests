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

import java.util.Map;

import org.alljoyn.bus.BusObject;
import org.alljoyn.bus.Variant;
import org.alljoyn.bus.annotation.BusInterface;
import org.alljoyn.bus.annotation.BusSignal;
import org.alljoyn.ns.transport.TransportNotificationText;

@BusInterface(name = NotificationTransport.IF_NAME)
public interface NotificationTransport extends BusObject
{
    public static final String IF_NAME = "org.alljoyn.Notification";

    @BusSignal(signature = "qiqssaysa{iv}a{ss}ar")
    public void notify(int version, int msgId, short messageType, String deviceId, String deviceName, byte[] appId, String appName, Map<Integer, Variant> attributes,
            Map<String, String> customAttributes, TransportNotificationText[] text);
}