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
package com.at4wireless.alljoyn.core.introspection.bean;

// TODO: Auto-generated Javadoc
/**
 * The Class NodeDetail.
 */
public class NodeDetail
{
    
    /** The path. */
    private String path;
    
    /** The introspection node. */
    private IntrospectionNode introspectionNode;

    /**
     * Instantiates a new node detail.
     *
     * @param path the path
     * @param introspectionNode the introspection node
     */
    public NodeDetail(String path, IntrospectionNode introspectionNode)
    {
        this.path = path;
        this.introspectionNode = introspectionNode;
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
     * Gets the introspection node.
     *
     * @return the introspection node
     */
    public IntrospectionNode getIntrospectionNode()
    {
        return introspectionNode;
    }
}