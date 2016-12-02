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

import java.util.ArrayList;
import java.util.List;

public class IntrospectionNode extends IntrospectionBaseTag
{
    private List<IntrospectionInterface> interfaces = new ArrayList<IntrospectionInterface>();
    private List<IntrospectionSubNode> subNodes = new ArrayList<IntrospectionSubNode>();

    public List<IntrospectionInterface> getInterfaces()
    {
        return interfaces;
    }

    public List<IntrospectionSubNode> getSubNodes()
    {
        return subNodes;
    }
}