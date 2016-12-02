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
 * This is for test scenarios that require manual interaction during the test
 * run.
 * 
 */
public interface UserInputHandler
{
    /**
     * This method waits for the user to provide some input details and then
     * responds with the information provided
     * 
     * @param userInputDetails
     *            provided by user
     * @return user response indicating whether a button was pressed or an
     *         option was selected
     * @throws InterruptedException
     *             when the test has been running too long
     */
    UserResponse waitForUserInput(UserInputDetails userInputDetails) throws InterruptedException;
}