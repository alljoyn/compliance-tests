/*******************************************************************************
 *     Copyright (c) Open Connectivity Foundation (OCF) and AllJoyn Open
 *     Source Project (AJOSP) Contributors and others.
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

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.alljoyn.validation.testing.utils.AllJoynLibraryLoader;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

@RunWith(PowerMockRunner.class)
@PrepareForTest(
{ Bundle.class, Log.class, AllJoynLibraryLoader.class, InstrumentationTestCaseWrapper.class, TestCase.class })
public class ValidationInstrumentationTestRunnerHelperTest
{
    private static final String PACKAGE_NAME = "packageName";
    private ValidationInstrumentationTestRunnerHelper helper;
    @Mock
    private ValidationInstrumentationTestRunner runner;
    @Mock
    private Context context;
    @Mock
    private Bundle arguments;
    @Mock
    private ValidationInstrumentationApplication application;
    @Mock
    private InstrumentationTestCaseWrapper instrumentationTestCaseWrapper;
    @Mock
    private PackageManager packageManager;
    @Mock
    private PackageInfo packageInfo;

    @Test
    public void testOnCreate() throws Exception
    {
        PowerMockito.mockStatic(Log.class);
        PowerMockito.suppress(PowerMockito.method(AllJoynLibraryLoader.class, "loadLibrary"));

        when(runner.getContext()).thenReturn(context);
        when(context.getApplicationContext()).thenReturn(application);
        when(context.getPackageManager()).thenReturn(packageManager);
        when(context.getPackageName()).thenReturn(PACKAGE_NAME);
        when(packageManager.getPackageInfo(PACKAGE_NAME, 0)).thenReturn(packageInfo);
        helper = new ValidationInstrumentationTestRunnerHelper();

        helper.onCreate(runner, arguments);
        verify(packageManager).getPackageInfo(PACKAGE_NAME, 0);
    }

    @Test
    public void testOnCreateWithNullArguments() throws Exception
    {
        PowerMockito.mockStatic(Log.class);
        PowerMockito.suppress(PowerMockito.method(AllJoynLibraryLoader.class, "loadLibrary"));

        when(runner.getContext()).thenReturn(context);
        when(context.getApplicationContext()).thenReturn(application);
        when(context.getPackageManager()).thenReturn(packageManager);
        when(context.getPackageName()).thenReturn(PACKAGE_NAME);
        when(packageManager.getPackageInfo(PACKAGE_NAME, 0)).thenReturn(packageInfo);
        helper = new ValidationInstrumentationTestRunnerHelper();

        helper.onCreate(runner, null);
        verify(packageManager).getPackageInfo(PACKAGE_NAME, 0);
    }

    @Test
    public void testGetAllTests() throws Exception
    {
        PowerMockito.mockStatic(Log.class);
        PowerMockito.suppress(PowerMockito.method(AllJoynLibraryLoader.class, "loadLibrary"));
        PowerMockito.suppress(PowerMockito.constructor(InstrumentationTestCaseWrapper.class, Class.class, String.class));

        when(runner.getContext()).thenReturn(context);
        when(context.getApplicationContext()).thenReturn(application);
        when(application.getInstrumentParameter(InstrumentationArgKey.TestSuiteList.getValue())).thenReturn(
                "org.alljoyn.validation.testing.suites.config.ConfigTestSuite,org.alljoyn.validation.testing.suites.about.AboutTestSuite");
        when(application.getInstrumentParameter(InstrumentationArgKey.TestCaseName.getValue())).thenReturn("About-v1-01,Config-v1-01");

        helper = new ValidationInstrumentationTestRunnerHelper();

        TestSuite testSuite = helper.getAllTests(runner);
        assertNotNull(testSuite);
    }

    @Test
    public void testGetAllTestsWithTestCaseNameNull() throws Exception
    {
        PowerMockito.mockStatic(Log.class);
        PowerMockito.suppress(PowerMockito.method(AllJoynLibraryLoader.class, "loadLibrary"));

        when(runner.getContext()).thenReturn(context);
        when(context.getApplicationContext()).thenReturn(application);
        when(application.getInstrumentParameter(InstrumentationArgKey.TestSuiteList.getValue())).thenReturn(
                "org.alljoyn.validation.testing.suites.config.ConfigTestSuite,org.alljoyn.validation.testing.suites.about.AboutTestSuite");
        when(application.getInstrumentParameter(InstrumentationArgKey.TestCaseName.getValue())).thenReturn(null);

        helper = new ValidationInstrumentationTestRunnerHelper();

        TestSuite testSuite = helper.getAllTests(runner);
        assertNotNull(testSuite);
    }
}