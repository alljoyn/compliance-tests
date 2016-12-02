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
 * Service Provider Application status information
 */
public class ApplicationStatusAJ {

    /**
     * Service Provider Application installation status
     */
    @Position(0)
    public short installStatus;

    /**
     * Service Provider Application installation description
     */
    @Position(1)
    public String installDesc;

    /**
     * Service Provider Application connection status
     */
    @Position(2)
    public short connectionStatus;

    /**
     * Service Provider Application operational status
     */
    @Position(3)
    public short operationalStatus;

}