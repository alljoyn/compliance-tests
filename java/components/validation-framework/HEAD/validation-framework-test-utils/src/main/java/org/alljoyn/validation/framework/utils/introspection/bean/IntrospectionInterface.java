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

import java.util.Set;
import java.util.TreeSet;

public class IntrospectionInterface extends IntrospectionBaseTag
{
    private Set<IntrospectionMethod> methods = new TreeSet<IntrospectionMethod>();
    private Set<IntrospectionProperty> properties = new TreeSet<IntrospectionProperty>();
    private Set<IntrospectionSignal> signals = new TreeSet<IntrospectionSignal>();
    private Set<IntrospectionAnnotation> annotations = new TreeSet<IntrospectionAnnotation>();

    public Set<IntrospectionMethod> getMethods()
    {
        return methods;
    }

    public Set<IntrospectionProperty> getProperties()
    {
        return properties;
    }

    public Set<IntrospectionSignal> getSignals()
    {
        return signals;
    }

    public Set<IntrospectionAnnotation> getAnnotations()
    {
        return annotations;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((methods == null) ? 0 : methods.hashCode());
        result = prime * result + ((properties == null) ? 0 : properties.hashCode());
        result = prime * result + ((signals == null) ? 0 : signals.hashCode());
        result = prime * result + ((annotations == null) ? 0 : annotations.hashCode());

        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (!super.equals(obj))
        {
            return false;
        }
        if (!(obj instanceof IntrospectionInterface))
        {
            return false;
        }
        IntrospectionInterface other = (IntrospectionInterface) obj;
        if (methods == null)
        {
            if (other.methods != null)
            {
                return false;
            }
        }
        else if (!methods.equals(other.methods))
        {
            return false;
        }
        if (properties == null)
        {
            if (other.properties != null)
            {
                return false;
            }
        }
        else if (!properties.equals(other.properties))
        {
            return false;
        }
        if (signals == null)
        {
            if (other.signals != null)
            {
                return false;
            }
        }
        else if (!signals.equals(other.signals))
        {
            return false;
        }
        if (annotations == null)
        {
            if (other.annotations != null)
            {
                return false;
            }
        }
        else if (!annotations.equals(other.annotations))
        {
            return false;
        }

        return true;
    }
}