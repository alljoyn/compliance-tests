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
 * Implement this interface to be notified about the Announcement signals
 * received from a Gateway Management App. Avoid blocking the thread on which the methods of this
 * interface are invoked.
 */
public interface GatewayMgmtAppListener {

    /**
     * Announcement signal has been received from the {@link GatewayMgmtApp}
     */
    void gatewayMgmtAppAnnounced();
}