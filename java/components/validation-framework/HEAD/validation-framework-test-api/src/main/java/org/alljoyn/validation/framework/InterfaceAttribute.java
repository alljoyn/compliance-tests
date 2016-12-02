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
package org.alljoyn.validation.framework;

/**
 * {@code InterfaceAttribute} contains details of an interface attribute.
 * 
 */
public class InterfaceAttribute
{
    private String name;
    private String value;

    /**
     * @param name
     *            the name of the attribute
     * @param value
     *            the value of the attribute
     */
    public InterfaceAttribute(String name, String value)
    {
        this.name = name;
        this.value = value;
    }

    /**
     * @return the name of the attribute
     */
    public String getName()
    {
        return name;
    }

    /**
     * @param name
     *            the name of the attribute
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * @return the value of the attribute
     */
    public String getValue()
    {
        return value;
    }

    /**
     * @param value
     *            the value of the attribute
     */
    public void setValue(String value)
    {
        this.value = value;
    }
}