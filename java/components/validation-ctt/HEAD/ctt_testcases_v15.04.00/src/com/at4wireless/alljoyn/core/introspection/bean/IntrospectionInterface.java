/*
 * Copyright AllSeen Alliance. All rights reserved.
 *
 *    Permission to use, copy, modify, and/or distribute this software for any
 *    purpose with or without fee is hereby granted, provided that the above
 *    copyright notice and this permission notice appear in all copies.
 *
 *    THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 *    WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 *    MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 *    ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 *    WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 *    ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 *    OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */
package com.at4wireless.alljoyn.core.introspection.bean;

import java.util.Set;
import java.util.TreeSet;

// TODO: Auto-generated Javadoc
/**
 * The Class IntrospectionInterface.
 */
public class IntrospectionInterface extends IntrospectionBaseTag
{
    
    /** The methods. */
    private Set<IntrospectionMethod> methods = new TreeSet<IntrospectionMethod>();
    
    /** The properties. */
    private Set<IntrospectionProperty> properties = new TreeSet<IntrospectionProperty>();
    
    /** The signals. */
    private Set<IntrospectionSignal> signals = new TreeSet<IntrospectionSignal>();
    
    /** The annotations. */
    private Set<IntrospectionAnnotation> annotations = new TreeSet<IntrospectionAnnotation>();

    /**
     * Gets the methods.
     *
     * @return the methods
     */
    public Set<IntrospectionMethod> getMethods()
    {
        return methods;
    }

    /**
     * Gets the properties.
     *
     * @return the properties
     */
    public Set<IntrospectionProperty> getProperties()
    {
        return properties;
    }

    /**
     * Gets the signals.
     *
     * @return the signals
     */
    public Set<IntrospectionSignal> getSignals()
    {
        return signals;
    }

    /**
     * Gets the annotations.
     *
     * @return the annotations
     */
    public Set<IntrospectionAnnotation> getAnnotations()
    {
        return annotations;
    }

    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.introspection.bean.IntrospectionBaseTag#hashCode()
     */
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

    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.introspection.bean.IntrospectionBaseTag#equals(java.lang.Object)
     */
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