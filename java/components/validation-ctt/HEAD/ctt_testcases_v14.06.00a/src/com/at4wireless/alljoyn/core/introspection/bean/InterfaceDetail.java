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