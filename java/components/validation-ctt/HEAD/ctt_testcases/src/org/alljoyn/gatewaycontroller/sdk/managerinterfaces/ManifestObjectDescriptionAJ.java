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
import org.alljoyn.gatewaycontroller.sdk.RuleObjectDescription;
import org.alljoyn.gatewaycontroller.sdk.RuleObjectDescription.RuleInterface;

// TODO: Auto-generated Javadoc
/**
 * This class stores object description provided with the manifest of the
 * Service Provider Application
 */
public class ManifestObjectDescriptionAJ {

    /**
     * Object path
     */
    @Position(0)
    public String objectPath;

    /**
     * Whether the object path is prefix
     */
    @Position(1)
    public boolean isPrefix;

    /**
     * The list of interfaces
     */
    @Position(2)
    public String[] interfaces;

    /**
     * Constructor
     */
    public ManifestObjectDescriptionAJ() {
    }

    /**
     * Constructor
     * 
     * @param capObjDesc
     */
    public ManifestObjectDescriptionAJ(RuleObjectDescription capObjDesc) {

        objectPath = capObjDesc.getObjectPath().getPath();
        isPrefix   = capObjDesc.getObjectPath().isPrefix();

        int size   = capObjDesc.getInterfaces().size();
        interfaces = new String[size];

        int i = 0;
        for (RuleInterface ifaceObj : capObjDesc.getInterfaces()) {
            interfaces[i++] = ifaceObj.getName();
        }
    }
}