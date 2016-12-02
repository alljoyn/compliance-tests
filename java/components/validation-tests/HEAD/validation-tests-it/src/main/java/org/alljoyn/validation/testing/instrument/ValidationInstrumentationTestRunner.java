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
package org.alljoyn.validation.testing.instrument;

import junit.framework.TestSuite;
import android.os.Bundle;
import android.test.InstrumentationTestRunner;

public class ValidationInstrumentationTestRunner extends InstrumentationTestRunner
{
    private ValidationInstrumentationTestRunnerHelper validationInstrumentationTestRunnerHelper = new ValidationInstrumentationTestRunnerHelper();

    @Override
    public void onCreate(Bundle arguments)
    {
        validationInstrumentationTestRunnerHelper.onCreate(this, arguments);
        callingSuperOnCreate(arguments);
    }

    public void callingSuperOnCreate(Bundle arguments)
    {
        super.onCreate(arguments);
    }

    @Override
    public TestSuite getAllTests()
    {
        return validationInstrumentationTestRunnerHelper.getAllTests(this);
    }
}