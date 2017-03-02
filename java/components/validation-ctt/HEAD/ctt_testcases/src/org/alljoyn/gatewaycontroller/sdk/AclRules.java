/*
 *    Copyright (c) Open Connectivity Foundation (OCF), AllJoyn Open Source
 *    Project (AJOSP) Contributors and others.
 *    
 *    SPDX-License-Identifier: Apache-2.0
 *    
 *    All rights reserved. This program and the accompanying materials are
 *    made available under the terms of the Apache License, Version 2.0
 *    which accompanies this distribution, and is available at
 *    http://www.apache.org/licenses/LICENSE-2.0
 *    
 *    Copyright (c) Open Connectivity Foundation and Contributors to AllSeen
 *    Alliance. All rights reserved.
 *    
 *    Permission to use, copy, modify, and/or distribute this software for
 *    any purpose with or without fee is hereby granted, provided that the
 *    above copyright notice and this permission notice appear in all
 *    copies.
 *    
 *    THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL
 *    WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED
 *    WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE
 *    AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL
 *    DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR
 *    PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER
 *    TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 *    PERFORMANCE OF THIS SOFTWARE.
*/

package org.alljoyn.gatewaycontroller.sdk;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * This class defines access rules for the {@link Acl}
 */
public class AclRules {

    /**
     * The interfaces that the Gateway Connector Application exposes to its
     * clients
     */
    private final List<RuleObjectDescription> exposedServices;

    /**
     * The applications that may be reached by the Gateway Connector Application
     * via the configured interfaces and object paths
     */
    private final List<RemotedApp> remotedApps;

    /**
     * This {@link AclRules} metadata
     */
    private Map<String, String> metadata;

    /**
     * Constructor
     * 
     * @param exposedServices
     *            The interfaces that the Gateway Connector Application exposes
     *            to its clients
     * @param remotedApps
     *            The applications that may be reached by the Gateway Connector
     *            Application via the configured interfaces and object paths
     * @throws IllegalArgumentException
     *             is thrown if bad arguments have been received
     */
    public AclRules(List<RuleObjectDescription> exposedServices, List<RemotedApp> remotedApps) {

        if (exposedServices == null) {
            throw new IllegalArgumentException("exposedServices is undefined");
        }

        if (remotedApps == null) {
            throw new IllegalArgumentException("remotedApps is undefined");
        }

        this.exposedServices = exposedServices;
        this.remotedApps     = remotedApps;
        metadata             = new HashMap<String, String>();
    }

    /**
     * The interfaces that Gateway Connector Application exposes to its clients
     * 
     * @return List of exposed services
     */
    public List<RuleObjectDescription> getExposedServices() {
        return exposedServices;
    }

    /**
     * The applications that may be reached by the Gateway Connector Application
     * via the configured interfaces and object paths
     * 
     * @return List of the remoted applications
     */
    public List<RemotedApp> getRemotedApps() {
        return remotedApps;
    }

    /**
     * Returns current metadata
     * 
     * @return metadata
     */
    public Map<String, String> getMetadata() {

        return metadata;
    }

    /**
     * Set the metadata
     * 
     * @param metadata
     */
    public void setMetadata(Map<String, String> metadata) {

        this.metadata = metadata;
    }

}