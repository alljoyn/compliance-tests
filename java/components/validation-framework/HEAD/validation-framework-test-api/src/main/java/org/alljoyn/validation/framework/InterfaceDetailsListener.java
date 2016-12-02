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