/*******************************************************************************
 *   *  
 *     Copyright (c) 2016 Open Connectivity Foundation and AllJoyn Open
 *     Source Project Contributors and others.
 *     
 *     All rights reserved. This program and the accompanying materials are
 *     made available under the terms of the Apache License, Version 2.0
 *     which accompanies this distribution, and is available at
 *     http://www.apache.org/licenses/LICENSE-2.0

 *******************************************************************************/
package org.alljoyn.validation.testing.utils.controlpanel;

public enum InterfacePathPattern
{
    ControlPanel("/ControlPanel/[^/]+/[^/]+"), HttpControl("/ControlPanel/[^/]+/HTTPControl");

    private String value;

    InterfacePathPattern(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}