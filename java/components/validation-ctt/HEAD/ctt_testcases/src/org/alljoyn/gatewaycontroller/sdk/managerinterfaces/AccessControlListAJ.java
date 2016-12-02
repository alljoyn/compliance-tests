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

import java.util.Map;

import org.alljoyn.bus.annotation.Position;

// TODO: Auto-generated Javadoc
/**
 * Access Control List data
 */
public class AccessControlListAJ {

    /**
     * ACL Name
     */
    @Position(0)
    public String aclName;

    /**
     * Exposed services
     */
    @Position(1)
    public ManifestObjectDescriptionAJ[] exposedServices;

    /**
     * Remoted apps
     */
    @Position(2)
    public RemotedAppAJ[] remotedApps;

    /**
     * Internal metadata
     */
    @Position(3)
    public Map<String, String> internalMetadata;

    /**
     * Custom Metadata
     */
    @Position(4)
    public Map<String, String> customMetadata;
}