/*******************************************************************************
 *     Copyright (c) Open Connectivity Foundation (OCF), AllJoyn Open Source
 *     Project (AJOSP) Contributors and others.
 *     
 *     SPDX-License-Identifier: Apache-2.0
 *     
 *     All rights reserved. This program and the accompanying materials are
 *     made available under the terms of the Apache License, Version 2.0
 *     which accompanies this distribution, and is available at
 *     http://www.apache.org/licenses/LICENSE-2.0
 *     
 *     Copyright (c) Open Connectivity Foundation and Contributors to AllSeen
 *     Alliance. All rights reserved.
 *     
 *     Permission to use, copy, modify, and/or distribute this software for
 *     any purpose with or without fee is hereby granted, provided that the
 *     above copyright notice and this permission notice appear in all
 *     copies.
 *     
 *     THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL
 *     WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED
 *     WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE
 *     AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL
 *     DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR
 *     PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER
 *     TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 *     PERFORMANCE OF THIS SOFTWARE.
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