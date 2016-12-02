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

import java.util.List;

/**
 * This is an Alljoyn device which contains details pertaining to its About
 * announcements.
 * 
 */
public interface AllJoynAnnouncedDevice
{
    /**
     * @return the Device identifier set by platform-specific means found in
     *         About interface’s DeviceId metadata field.
     */
    String getDeviceId();

    /**
     * Provides all the About announcements for the device.
     * 
     * @return list of About announcement details
     */
    List<AboutAnnouncement> getAnnouncements();
}