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
 * The interfaces of the Service Provider Application manifest
 */
public class ManifestRulesAJ {

    /**
     * The interfaces that the Service Provider Application exposes to its
     * clients
     */
    @Position(0)
    public ManifestObjectDescriptionInfoAJ[] exposedServices;

    /**
     * The interfaces that the Service Provider Application exposes outside of
     * the network proximity
     */
    @Position(1)
    public ManifestObjectDescriptionInfoAJ[] remotedServices;

}