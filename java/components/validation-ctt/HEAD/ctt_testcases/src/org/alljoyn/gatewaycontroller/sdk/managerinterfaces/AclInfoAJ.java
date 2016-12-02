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
 * Information about the Access Control List of a Service Provider Application
 */
public class AclInfoAJ {

    /**
     * The Access Control List Id
     */
    @Position(0)
    public String aclId;

    /**
     * The name of the Access Control List
     */
    @Position(1)
    public String aclName;

    /**
     * The ACL status
     */
    @Position(2)
    public short aclStatus;

    /**
     * The object path to access the Access Control List on the Service Provider
     * Application it belongs to
     */
    @Position(3)
    public String objectPath;
}