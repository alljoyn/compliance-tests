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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.os.Bundle;
import android.test.InstrumentationTestRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(
{ ValidationInstrumentationTestRunner.class, ValidationInstrumentationTestRunnerHelper.class, InstrumentationTestRunner.class })
public class ValidationInstrumentationTestRunnerTest
{
    private ValidationInstrumentationTestRunner instrumentTestRunner;

    @Test
    public void testOnCreate() throws Exception
    {
        PowerMockito.suppress(PowerMockito.constructor(InstrumentationTestRunner.class));
        PowerMockito.suppress(PowerMockito.method(ValidationInstrumentationTestRunnerHelper.class, "onCreate", ValidationInstrumentationTestRunner.class, Bundle.class));
        instrumentTestRunner = new ValidationInstrumentationTestRunner()
        {
            @Override
            public void callingSuperOnCreate(Bundle arguments)
            {
            }
        };

        instrumentTestRunner.onCreate(null);
    }

    @Test
    public void testGetAllTests() throws Exception
    {
        PowerMockito.suppress(PowerMockito.constructor(InstrumentationTestRunner.class));
        PowerMockito.suppress(PowerMockito.method(ValidationInstrumentationTestRunnerHelper.class, "getAllTests", ValidationInstrumentationTestRunner.class));
        instrumentTestRunner = new ValidationInstrumentationTestRunner();

        instrumentTestRunner.getAllTests();
    }
}