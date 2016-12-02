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
 * This class stores a status of an ACL creation
 */
public class CreateAclStatusAJ {

    /**
     * ACL response code
     */
    @Position(0)
    public short aclResponseCode;

    /**
     * Id of the created ACL
     */
    @Position(1)
    public String aclId;

    /**
     * ACL object path
     */
    @Position(2)
    public String objPath;
}