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
package org.alljoyn.validation.framework.utils.introspection.bean;

import java.util.List;

public class InterfaceDetail
{
    private String path;
    private List<IntrospectionInterface> introspectionInterfaces;

    public InterfaceDetail(String path, List<IntrospectionInterface> introspectionInterfaces)
    {
        this.path = path;
        this.introspectionInterfaces = introspectionInterfaces;
    }

    public String getPath()
    {
        return path;
    }

    public List<IntrospectionInterface> getIntrospectionInterfaces()
    {
        return introspectionInterfaces;
    }
}