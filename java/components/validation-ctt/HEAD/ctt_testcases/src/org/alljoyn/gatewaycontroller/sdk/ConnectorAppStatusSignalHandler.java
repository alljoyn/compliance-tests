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

// TODO: Auto-generated Javadoc
/**
 * Implement this interface to be notified about changes in the status of the
 * Gateway Connector Application
 */
public interface ConnectorAppStatusSignalHandler {

    /**
     * The event is emitted when the status of the Gateway Connector Application
     * changes. Avoid blocking the thread on which the method is called.
     *
     * @param connectorAppId
     *            The {@link ConnectorApp} id
     * @param status
     *            {@link ConnectorAppStatus}
     */
    public void onStatusChanged(String connectorAppId, ConnectorAppStatus status);
}