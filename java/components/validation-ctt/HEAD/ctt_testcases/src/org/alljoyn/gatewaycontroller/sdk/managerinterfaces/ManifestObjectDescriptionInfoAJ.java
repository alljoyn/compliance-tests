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
 * Manifest object description with a full information
 */
public class ManifestObjectDescriptionInfoAJ {

    /**
     * Object path with full name
     */
    public static class ObjectPathInfoAJ {

        /**
         * Object path
         */
        @Position(0)
        public String objectPath;

        /**
         * Whether this object path is a prefix
         */
        @Position(1)
        public boolean isPrefix;

        /**
         * Object path friendly name
         */
        @Position(2)
        public String objectPathFriendlyName;
    }

    /**
     * Interfaces with the full name
     */
    public static class InterfaceInfoAJ {

        /**
         * Interface name
         */
        @Position(0)
        public String interfaceName;

        /**
         * Interface friendly name
         */
        @Position(1)
        public String friendlyName;

        /**
         * Whether the interface is secured
         */
        @Position(2)
        public boolean isSecured;
    }

    // =======================================//

    /**
     * Object path
     */
    @Position(0)
    public ObjectPathInfoAJ objPathInfo;

    /**
     * Interfaces
     */
    @Position(1)
    public InterfaceInfoAJ[] interfaces;
}