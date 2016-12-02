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
 * This exception is thrown when a failure occurs in the Gateway Controller SDK
 */
public class GatewayControllerException extends Exception {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1939865232952438606L;

    /**
     * Instantiates a new gateway controller exception.
     */
    public GatewayControllerException() {
        super();
    }

    /**
     * Instantiates a new gateway controller exception.
     *
     * @param detailMessage the detail message
     * @param throwable the throwable
     */
    public GatewayControllerException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    /**
     * Instantiates a new gateway controller exception.
     *
     * @param detailMessage the detail message
     */
    public GatewayControllerException(String detailMessage) {
        super(detailMessage);
    }

    /**
     * Instantiates a new gateway controller exception.
     *
     * @param throwable the throwable
     */
    public GatewayControllerException(Throwable throwable) {
        super(throwable);
    }
}