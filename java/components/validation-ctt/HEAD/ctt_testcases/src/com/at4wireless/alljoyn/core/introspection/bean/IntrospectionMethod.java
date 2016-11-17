/*******************************************************************************
 *  Copyright (c) 2016 Open Connectivity Foundation (OCF) and AllJoyn Open
 *     Source Project (AJOSP) Contributors and others.
 *
 *     SPDX-License-Identifier: Apache-2.0
 *
 *     All rights reserved. This program and the accompanying materials are
 *     made available under the terms of the Apache License, Version 2.0
 *     which accompanies this distribution, and is available at
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Copyright 2016 Open Connectivity Foundation and Contributors to
 *     AllSeen Alliance. All rights reserved.
 *
 *     Permission to use, copy, modify, and/or distribute this software for
 *     any purpose with or without fee is hereby granted, provided that the
 *     above copyright notice and this permission notice appear in all
 *     copies.
 *
 *      THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL
 *      WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED
 *      WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE
 *      AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL
 *      DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR
 *      PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER
 *      TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 *      PERFORMANCE OF THIS SOFTWARE.
 *******************************************************************************/
package com.at4wireless.alljoyn.core.introspection.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

// TODO: Auto-generated Javadoc
/**
 * The Class IntrospectionMethod.
 */
public class IntrospectionMethod extends IntrospectionBaseTag implements Comparable<IntrospectionMethod>
{
    
    /** The args. */
    private List<IntrospectionArg> args = new ArrayList<IntrospectionArg>();
    
    /** The annotations. */
    private Set<IntrospectionAnnotation> annotations = new TreeSet<IntrospectionAnnotation>();

    /**
     * Gets the args.
     *
     * @return the args
     */
    public List<IntrospectionArg> getArgs()
    {
        return args;
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
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(IntrospectionMethod introspectionMethod)
    {
        return getName().compareTo(introspectionMethod.getName());
    }

    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.introspection.bean.IntrospectionBaseTag#hashCode()
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((annotations == null) ? 0 : annotations.hashCode());
        result = prime * result + ((args == null) ? 0 : args.hashCode());

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
        if (!(obj instanceof IntrospectionMethod))
        {
            return false;
        }
        IntrospectionMethod other = (IntrospectionMethod) obj;
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
        if (args == null)
        {
            if (other.args != null)
            {
                return false;
            }
        }
        else if (!args.equals(other.args))
        {
            return false;
        }

        return true;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("Method [name=");
        builder.append(getName());
        builder.append(", args=");
        builder.append(args);
        builder.append(", annotations=");
        builder.append(annotations);
        builder.append("]");

        return builder.toString();
    }
}