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
package org.alljoyn.validation.testing.suites;

import org.junit.runners.model.InitializationError;
import org.robolectric.RobolectricTestRunner;

public class MyRobolectricTestRunner extends RobolectricTestRunner
{

    static
    {
        // write Log statements to stdout
        System.setProperty("robolectric.logging", "stdout");
    }

    public MyRobolectricTestRunner(Class<?> testClass) throws InitializationError
    {
        super(testClass);
    }

}