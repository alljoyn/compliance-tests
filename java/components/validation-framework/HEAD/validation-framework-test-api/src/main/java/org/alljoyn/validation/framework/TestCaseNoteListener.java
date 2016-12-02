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
 * This interface defines a method for handling of adding a note during test
 * execution.
 */
public interface TestCaseNoteListener
{
    /**
     * This method handles the adding of a note during test execution.
     * 
     * For example, a note might be needed to add in a test when the test is not
     * relevant for the interface being tested, e.g.
     * {@code Device does not support AboutIcon interface}
     * 
     * @param note
     *            to be added
     */
    void addNote(String note);
}