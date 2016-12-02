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