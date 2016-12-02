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
package com.at4wireless.alljoyn.core.controlpanel;

// TODO: Auto-generated Javadoc
/**
 * The Enum InterfacePathPattern.
 */
public enum InterfacePathPattern
{
    
    /** The Control panel. */
    ControlPanel("/ControlPanel/[^/]+/[^/]+"), 
 /** The Http control. */
 HttpControl("/ControlPanel/[^/]+/HTTPControl");

    /** The value. */
    private String value;

    /**
     * Instantiates a new interface path pattern.
     *
     * @param value the value
     */
    InterfacePathPattern(String value)
    {
        this.value = value;
    }

    /**
     * Gets the value.
     *
     * @return the value
     */
    public String getValue()
    {
        return value;
    }
}