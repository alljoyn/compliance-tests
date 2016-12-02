/*
 *  * 
 *    Copyright (c) 2016 Open Connectivity Foundation and AllJoyn Open
 *    Source Project Contributors and others.
 *    
 *    All rights reserved. This program and the accompanying materials are
 *    made available under the terms of the Apache License, Version 2.0
 *    which accompanies this distribution, and is available at
 *    http://www.apache.org/licenses/LICENSE-2.0

 */

package org.alljoyn.gatewaycontroller.sdk.managerinterfaces;

import org.alljoyn.bus.annotation.Position;

// TODO: Auto-generated Javadoc
/**
 * This class stores data of the installed Gateway Connector Application. This
 * class is used only to received the unmarshalled data that is sent over
 * AllJoyn
 */
public class InstalledAppInfoAJ {

    /**
     * Application id
     */
    @Position(0)
    public String appId;

    /**
     * The application friendly name or description
     */
    @Position(1)
    public String friendlyName;

    /**
     * The identification of the application object
     */
    @Position(2)
    public String objectPath;

    /**
     * The application version
     */
    @Position(3)
    public String appVersion;
}