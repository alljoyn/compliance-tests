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

package org.alljoyn.gatewaycontroller.sdk;

import java.util.ArrayList;
import java.util.List;

import org.alljoyn.gatewaycontroller.sdk.RuleObjectDescription.RuleObjectPath;
import org.alljoyn.gatewaycontroller.sdk.managerinterfaces.ManifestObjectDescriptionAJ;
import org.alljoyn.gatewaycontroller.sdk.managerinterfaces.ManifestObjectDescriptionInfoAJ;
import org.alljoyn.gatewaycontroller.sdk.managerinterfaces.ManifestRulesAJ;

// TODO: Auto-generated Javadoc
/**
 * The manifest rules of the Gateway Connector Application
 */
public class ConnectorCapabilities {

    /**
     * The interfaces that the Gateway Connector Application exposes to its
     * clients
     */
    private final List<RuleObjectDescription> exposedServices;

    /**
     * The interfaces that the Gateway Connector Application allows to remote
     */
    private final List<RuleObjectDescription> remotedServices;

    /**
     * Constructor
     * 
     * @param manifRulesAJ
     */
    ConnectorCapabilities(ManifestRulesAJ manifRulesAJ) {
        exposedServices = new ArrayList<RuleObjectDescription>(manifRulesAJ.exposedServices.length);
        remotedServices = new ArrayList<RuleObjectDescription>(manifRulesAJ.remotedServices.length);

        convertList(manifRulesAJ.exposedServices, exposedServices);
        convertList(manifRulesAJ.remotedServices, remotedServices);
    }

    /**
     * The {@link RuleObjectDescription} objects that the Gateway Connector
     * Application exposes to its clients
     * 
     * @return List of exposed services
     */
    public List<RuleObjectDescription> getExposedServices() {
        return exposedServices;
    }

    /**
     * The {@link RuleObjectDescription} objects that the Gateway Connector
     * Application supports for being remoted
     * 
     * @return List of remoted interfaces
     */
    public List<RuleObjectDescription> getRemotedServices() {
        return remotedServices;
    }

    /**
     * Converts {@link ManifestObjectDescriptionAJ} into
     * {@link RuleObjectDescription} and fills with it the given list
     */
    private void convertList(ManifestObjectDescriptionInfoAJ[] from, List<RuleObjectDescription> to) {

        for (ManifestObjectDescriptionInfoAJ objDescInfoAJ : from) {

            RuleObjectDescription mod = new RuleObjectDescription(objDescInfoAJ);
            RuleObjectPath objPath = mod.getObjectPath();

            // If manifest object path isPrefix true, then isAllowedObjectPath is TRUE
            objPath.setPrefixAllowed(objPath.isPrefix());
            to.add(mod);
        }
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "ManifestRules [exposedServices='" + exposedServices + "', remotedServices='" + 
                    remotedServices + "']";
    }
}