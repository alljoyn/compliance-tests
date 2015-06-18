/*******************************************************************************
 *   *     Copyright (c) Open Connectivity Foundation (OCF) and AllJoyn Open
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

import java.util.UUID;

/**
 * This class contains information about the device/app being tested.
 * 
 */
public class AppUnderTestDetails
{
    private String deviceId;
    private UUID appId;

    /**
     * @param appId
     *            the globally unique identifier for the application given by
     *            its About interface’s AppId metadata field
     * @param deviceId
     *            the Device identifier set by platform-specific means found in
     *            About interface’s DeviceId metadata field
     */
    public AppUnderTestDetails(UUID appId, String deviceId)
    {
        this.appId = appId;
        this.deviceId = deviceId;
    }

    /**
     * @return the Device identifier set by platform-specific means found in
     *         About interface’s DeviceId metadata field
     */
    public String getDeviceId()
    {
        return deviceId;
    }

    /**
     * @return the globally unique identifier for the application given by its
     *         About interface’s AppId metadata field
     */
    public UUID getAppId()
    {
        return appId;
    }
}