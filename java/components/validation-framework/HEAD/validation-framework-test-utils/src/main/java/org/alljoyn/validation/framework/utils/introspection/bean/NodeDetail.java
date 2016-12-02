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

public class NodeDetail
{
    private String path;
    private IntrospectionNode introspectionNode;

    public NodeDetail(String path, IntrospectionNode introspectionNode)
    {
        this.path = path;
        this.introspectionNode = introspectionNode;
    }

    public String getPath()
    {
        return path;
    }

    public IntrospectionNode getIntrospectionNode()
    {
        return introspectionNode;
    }
}