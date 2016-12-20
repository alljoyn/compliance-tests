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
 *     THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL
 *     WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED
 *     WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE
 *     AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL
 *     DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR
 *     PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER
 *     TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 *     PERFORMANCE OF THIS SOFTWARE.
 *******************************************************************************/
package org.alljoyn.validation.framework;

import java.util.List;

/**
 * This interface provides method to determine applicable
 * {@link ValidationTestGroup}.
 * 
 */
public interface ValidationTestSuite
{
    /**
     * This method provides a list of {@code ValidationTestGroup} that are
     * applicable for the test suite based on About announcements.
     * 
     * For example, for a device which contains multiple applications, the list
     * would contain a {@code ValidationTestGroup} for About interface of each
     * application and {@code ValidationTestGroup} for any other service like
     * Config.
     * 
     * @param allJoynAnnouncedDevice
     *            the object containing device id and About announcements for
     *            the device
     * @return List of {@code ValidationTestGroup} that are applicable for the
     *         test suite based on About announcements
     */
    List<ValidationTestGroup> getApplicableTests(AllJoynAnnouncedDevice allJoynAnnouncedDevice);
}