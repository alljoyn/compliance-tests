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