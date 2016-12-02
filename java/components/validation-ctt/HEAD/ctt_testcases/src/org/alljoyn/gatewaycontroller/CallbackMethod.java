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

package org.alljoyn.gatewaycontroller;

import java.lang.reflect.Method;

// TODO: Auto-generated Javadoc
/**
 * Utility class for callbacks
 */
public class CallbackMethod {

    /**
     * {@link Method} to be invoked
     */
    private final Method method;

    /**
     * The method args
     */
    private final Object[] args;

    /**
     * Constructor
     * 
     * @param method
     *            {@link Method} to be invoked
     * @param args
     *            The method args
     */
    public CallbackMethod(Method method, Object... args) {

        this.method = method;
        this.args   = args;
    }

    /**
     * @return Callback method
     */
    public Method getMethod() {
        return method;
    }

    /**
     * @return Method args
     */
    public Object[] getArgs() {
        return args;
    }
}