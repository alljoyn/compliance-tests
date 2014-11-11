/*******************************************************************************
 *     Copyright (c) Open Connectivity Foundation (OCF) and AllJoyn Open
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

import java.util.List;

/**
 * This interface defines a method to store details of the interface that is
 * tested. This is meant for enabling the feeding into some kind of test report.
 * 
 * This is NOT currently being used by the test framework.
 * 
 */
public interface InterfaceDetailsListener
{
    /**
     * This method handles the adding of interface details during test
     * execution.
     * 
     * @param interfaceName
     *            name of the interface tested, e.g. {@code org.alljoyn.About}
     * @param version
     *            version of the interface tested
     * @param objectPath
     *            path on the Alljoyn bus where the interface was found, e.g.
     *            {@code /About}
     * @param details
     *            any custom details to be noted
     * @param attributes
     *            the attributes of the interface tested
     */
    void addInterfaceDetails(String interfaceName, short version, String objectPath, String details, List<InterfaceAttribute> attributes);
}