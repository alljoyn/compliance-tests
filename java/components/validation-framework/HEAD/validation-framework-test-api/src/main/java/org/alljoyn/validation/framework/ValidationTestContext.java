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

/**
 * This interface defines methods that provide information between the tests and
 * framework classes.
 */
public interface ValidationTestContext extends TestCaseNoteListener, UserInputHandler, InterfaceDetailsListener
{
    /**
     * @return Android test context
     */
    Object getContext();

    /**
     * @return object that contains details about the app that is being tested
     */
    AppUnderTestDetails getAppUnderTestDetails();

    /**
     * @return path of the object being tested on the Alljoyn bus, e.g.
     *         {@code /About}
     */
    String getTestObjectPath();

    /**
     * @return path of the KeyStore location
     */
    String getKeyStorePath();

    /**
     * Provides the value of the provided parameter name
     * 
     * @param parameterName
     *            name of the parameter, e.g.
     *            {@code org.alljoyn.Onboarding.PersonalApSsid}
     * @return value of parameter
     */
    Object getTestParameter(String parameterName);
}