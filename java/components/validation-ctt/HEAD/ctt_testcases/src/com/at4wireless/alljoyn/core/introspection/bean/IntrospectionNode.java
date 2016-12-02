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

import java.util.ArrayList;
import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * The Class IntrospectionNode.
 */
public class IntrospectionNode extends IntrospectionBaseTag
{
    
    /** The interfaces. */
    private List<IntrospectionInterface> interfaces = new ArrayList<IntrospectionInterface>();
    
    /** The sub nodes. */
    private List<IntrospectionSubNode> subNodes = new ArrayList<IntrospectionSubNode>();

    /**
     * Gets the interfaces.
     *
     * @return the interfaces
     */
    public List<IntrospectionInterface> getInterfaces()
    {
        return interfaces;
    }

    /**
     * Gets the sub nodes.
     *
     * @return the sub nodes
     */
    public List<IntrospectionSubNode> getSubNodes()
    {
        return subNodes;
    }
}