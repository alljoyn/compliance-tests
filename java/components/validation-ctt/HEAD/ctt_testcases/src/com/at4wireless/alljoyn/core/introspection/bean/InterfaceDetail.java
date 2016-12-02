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
package com.at4wireless.alljoyn.core.introspection.bean;

import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * The Class InterfaceDetail.
 */
public class InterfaceDetail
{
    
    /** The path. */
    private String path;
    
    /** The introspection interfaces. */
    private List<IntrospectionInterface> introspectionInterfaces;

    /**
     * Instantiates a new interface detail.
     *
     * @param path the path
     * @param introspectionInterfaces the introspection interfaces
     */
    public InterfaceDetail(String path, List<IntrospectionInterface> introspectionInterfaces)
    {
        this.path = path;
        this.introspectionInterfaces = introspectionInterfaces;
    }

    /**
     * Gets the path.
     *
     * @return the path
     */
    public String getPath()
    {
        return path;
    }

    /**
     * Gets the introspection interfaces.
     *
     * @return the introspection interfaces
     */
    public List<IntrospectionInterface> getIntrospectionInterfaces()
    {
        return introspectionInterfaces;
    }
}