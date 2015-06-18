/*
 * Copyright (c) 2016 Open Connectivity Foundation (OCF) and AllJoyn Open
 *    Source Project (AJOSP) Contributors and others.
 *
 *    SPDX-License-Identifier: Apache-2.0
 *
 *    All rights reserved. This program and the accompanying materials are
 *    made available under the terms of the Apache License, Version 2.0
 *    which accompanies this distribution, and is available at
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Copyright 2016 Open Connectivity Foundation and Contributors to
 *    AllSeen Alliance. All rights reserved.
 *
 *    Permission to use, copy, modify, and/or distribute this software for
 *    any purpose with or without fee is hereby granted, provided that the
 *    above copyright notice and this permission notice appear in all
 *    copies.
 *
 *     THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL
 *     WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED
 *     WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE
 *     AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL
 *     DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR
 *     PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER
 *     TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 *     PERFORMANCE OF THIS SOFTWARE.
 */
package com.at4wireless.alljoyn.core.introspection.bean;

// TODO: Auto-generated Javadoc
/**
 * The Class IntrospectionProperty.
 */
public class IntrospectionProperty extends IntrospectionBaseTag implements Comparable<IntrospectionProperty>
{
    
    /** The type. */
    private String type;
    
    /** The access. */
    private String access;

    /**
     * Gets the type.
     *
     * @return the type
     */
    public String getType()
    {
        return type;
    }

    /**
     * Sets the type.
     *
     * @param type the new type
     */
    public void setType(String type)
    {
        this.type = type;
    }

    /**
     * Gets the access.
     *
     * @return the access
     */
    public String getAccess()
    {
        return access;
    }

    /**
     * Sets the access.
     *
     * @param access the new access
     */
    public void setAccess(String access)
    {
        this.access = access;
    }

    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(IntrospectionProperty introspectionProperty)
    {
        return getName().compareTo(introspectionProperty.getName());
    }

    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.introspection.bean.IntrospectionBaseTag#hashCode()
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((access == null) ? 0 : access.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());

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
        if (!(obj instanceof IntrospectionProperty))
        {
            return false;
        }
        IntrospectionProperty other = (IntrospectionProperty) obj;
        if (access == null)
        {
            if (other.access != null)
            {
                return false;
            }
        }
        else if (!access.equals(other.access))
        {
            return false;
        }
        if (type == null)
        {
            if (other.type != null)
            {
                return false;
            }
        }
        else if (!type.equals(other.type))
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
        builder.append("Property [name=");
        builder.append(getName());
        builder.append(", type=");
        builder.append(type);
        builder.append(", access=");
        builder.append(access);
        builder.append("]");

        return builder.toString();
    }
}