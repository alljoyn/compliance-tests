/*******************************************************************************
 *   *     Copyright (c) Open Connectivity Foundation (OCF) and AllJoyn Open
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
 *      THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL
 *      WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED
 *      WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE
 *      AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL
 *      DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR
 *      PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER
 *      TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 *      PERFORMANCE OF THIS SOFTWARE.
 *******************************************************************************/
package org.alljoyn.validation.testing.instrument;

import java.lang.reflect.Method;
import java.util.UUID;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.alljoyn.validation.framework.AppUnderTestDetails;
import org.alljoyn.validation.framework.TestCaseNoteListener;
import org.alljoyn.validation.framework.UserInputDetails;
import org.alljoyn.validation.framework.UserResponse;
import org.alljoyn.validation.framework.ValidationBaseTestCase;
import org.alljoyn.validation.framework.ValidationTestCase;
import org.alljoyn.validation.framework.annotation.ValidationTest;
import org.alljoyn.validation.testing.ValidationTestContextImpl;

import android.test.AndroidTestCase;
import android.util.Log;

public class ValidationInstrumentationTests extends AndroidTestCase
{
    private final static String TAG = "ValidationInstrumentationTests";
    private static ValidationInstrumentationApplication instrumentApp;

    static
    {
        System.loadLibrary("alljoyn_java");
    }

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        instrumentApp = (ValidationInstrumentationApplication) getContext().getApplicationContext();
    }

    public static Test suite() throws Exception
    {
        TestSuite testSuite = new TestSuite();
        Class<? extends TestCase> testGroupClazz = getTestGroupFromContext();
        if (testGroupClazz != null)
        {
            getTestCasesFromClass(testSuite, testGroupClazz);
        }

        return testSuite;
    }

    @SuppressWarnings("unchecked")
    private static Class<? extends TestCase> getTestGroupFromContext()
    {
        String testGroupClassName = instrumentApp.getInstrumentParameter(InstrumentationArgKey.TestSuiteList.getValue());

        try
        {
            return (Class<? extends TestCase>) Class.forName(testGroupClassName);
        }
        catch (ClassNotFoundException e)
        {
            Log.e(TAG, "Invalid test group class name.");
            return null;
        }
    }

    private static void getTestCasesFromClass(TestSuite testSuite, Class<? extends TestCase> clazz) throws Exception
    {
        Method[] methods = clazz.getMethods();
        for (Method method : methods)
        {
            ValidationTest validationTest = method.getAnnotation(ValidationTest.class);

            if (method.getName().startsWith("testConfig_v1_01AppIdEqualsDeviceId") && (validationTest != null))
            {
                InstrumentationTestCaseWrapper tcw = new InstrumentationTestCaseWrapper(clazz, method.getName());
                testSuite.addTest(tcw);
            }
        }
    }

    public static class InstrumentationTestCaseWrapper extends AndroidTestCase
    {
        protected ValidationBaseTestCase testCase;

        public InstrumentationTestCaseWrapper(Class<? extends TestCase> clazz, String methodName) throws Exception
        {
            if (!ValidationTestCase.class.isAssignableFrom(clazz))
            {
                throw new RuntimeException("Class must implement ValidationTestCase");
            }
            testCase = (ValidationBaseTestCase) clazz.newInstance();
            testCase.setName(methodName);
            setName(methodName);
        }

        @Override
        protected void setUp() throws Exception
        {
            String absolutePath = getContext().getFileStreamPath("alljoyn_keystore").getAbsolutePath();

            ValidationInstrumentationApplication instrumentApp = (ValidationInstrumentationApplication) getContext().getApplicationContext();

            ValidationTestContextImpl testContext = new ValidationTestContextImpl()
            {
                @Override
                public UserResponse waitForUserInput(UserInputDetails userInputDetails) throws InterruptedException
                {
                    Thread.sleep(5000);
                    return new UserResponse();
                }
            };

            testContext.setAppUnderTestDetails(new AppUnderTestDetails(UUID.fromString(instrumentApp.getInstrumentParameter(InstrumentationArgKey.AppId.getValue())), instrumentApp
                    .getInstrumentParameter(InstrumentationArgKey.DeviceId.getValue())));

            testContext.setKeyStorePath(absolutePath);
            testContext.setTestCaseNoteListener(new TestCaseNoteListener()
            {
                @Override
                public void addNote(String note)
                {
                    Log.d("InstrumentationTest", String.format("Note added: %s", note));
                }
            });

            String audioStreamObjectPath = instrumentApp.getInstrumentParameter(InstrumentationArgKey.AudioStreamObjectPath.getValue());

            if (audioStreamObjectPath != null && !audioStreamObjectPath.isEmpty())
            {
                testContext.setTestObjectPath(audioStreamObjectPath);
            }

            testCase.setValidationTestContext(testContext);
        }

        @Override
        protected void runTest() throws Throwable
        {
            testCase.runBare();
        }

        @Override
        protected void tearDown() throws Exception
        {
        }

        @Override
        public String toString()
        {
            return testCase.toString();
        }
    }

}