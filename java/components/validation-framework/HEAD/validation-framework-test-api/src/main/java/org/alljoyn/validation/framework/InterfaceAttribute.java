/*******************************************************************************
 *  Copyright (c) Open Connectivity Foundation (OCF) and AllJoyn Open
 *     Source Project (AJOSP) Contributors and others.
 *
 *     SPDX-License-Identifier: Apache-2.0
 *
 *     All rights reserved. This program and the accompanying materials are
 *     made available under the terms of the Apache License, Version 2.0
 *     which accompanies this distribution, and is available at
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Copyright (c) Open Connectivity Foundation and Contributors to AllSeen
 *     Alliance. All rights reserved.
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
package org.alljoyn.validation.framework;

/**
 * {@code InterfaceAttribute} contains details of an interface attribute.
 * 
 */
public class InterfaceAttribute
{
    private String name;
    private String value;

    /**
     * @param name
     *            the name of the attribute
     * @param value
     *            the value of the attribute
     */
    public InterfaceAttribute(String name, String value)
    {
        this.name = name;
        this.value = value;
    }

    /**
     * @return the name of the attribute
     */
    public String getName()
    {
        return name;
    }

    /**
     * @param name
     *            the name of the attribute
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * @return the value of the attribute
     */
    public String getValue()
    {
        return value;
    }

    /**
     * @param value
     *            the value of the attribute
     */
    public void setValue(String value)
    {
        this.value = value;
    }
}