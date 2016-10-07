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
 *      THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL
 *      WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED
 *      WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE
 *      AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL
 *      DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR
 *      PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER
 *      TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 *      PERFORMANCE OF THIS SOFTWARE.
 *******************************************************************************/
package org.alljoyn.validation.testing.it;

import java.util.List;
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
import org.alljoyn.validation.testing.ValidationTestContextImpl;
import org.alljoyn.validation.testing.instrument.ValidationTestCaseBuilder;
import org.alljoyn.validation.testing.suites.about.AboutTestSuite;
import org.alljoyn.validation.testing.suites.config.ConfigTestSuite;

import android.test.AndroidTestCase;
import android.util.Log;

public class SampleTest extends AndroidTestCase
{
    private static String appId = "c734260a-8610-99b7-2c2e-e6379de7a82c";
    private static String deviceId = "C734260A861099B72C2EE6379DE7A82C";

    static
    {
        System.loadLibrary("alljoyn_java");
    }

    public static Test suite() throws Exception
    {
        TestSuite testSuite = new TestSuite();
        getTestCasesFromClass(testSuite, AboutTestSuite.class);
        // getTestCasesFromClass(testSuite, ControlPanelTestSuite.class);
        // getTestCasesFromClass(testSuite, ConfigTestSuite.class);
        // getTestCasesFromClass(testSuite,
        // NotificationProducerTestSuite.class);

        getTestCasesFromClassAndMethod(testSuite, AboutTestSuite.class, "testAbout_v1_01_AboutAnnouncement");

        getTestCasesFromClassByTestName(testSuite, ConfigTestSuite.class, "Config-v1-01");
        getTestCasesFromClassByTestName(testSuite, ConfigTestSuite.class, "Config-v1-02");
        getTestCasesFromClassByTestName(testSuite, ConfigTestSuite.class, "Config-v1-04");
        getTestCasesFromClassByTestName(testSuite, ConfigTestSuite.class, "Config-v1-05");
        getTestCasesFromClassByTestName(testSuite, ConfigTestSuite.class, "Config-v1-06");
        getTestCasesFromClassByTestName(testSuite, ConfigTestSuite.class, "Config-v1-07");
        getTestCasesFromClassByTestName(testSuite, ConfigTestSuite.class, "Config-v1-08");
        getTestCasesFromClassByTestName(testSuite, ConfigTestSuite.class, "Config-v1-12");
        getTestCasesFromClassByTestName(testSuite, ConfigTestSuite.class, "Config-v1-13");
        getTestCasesFromClassByTestName(testSuite, ConfigTestSuite.class, "Config-v1-14");

        return testSuite;
    }

    private static void getTestCasesFromClassByTestName(TestSuite testSuite, Class<? extends TestCase> clazz, String testName) throws Exception
    {
        TestCase testCase = ValidationTestCaseBuilder.testCaseByTestName(clazz, testName).build().get(0);

        testSuite.addTest(wrapTestCase(testCase));
    }

    protected static AndroidTestCase wrapTestCase(TestCase testCase) throws Exception
    {
        MyTestCaseWrapper tcw = new MyTestCaseWrapper(testCase);
        tcw.setDeviceId(deviceId);
        tcw.setAppId(appId);
        return tcw;
    }

    private static void getTestCasesFromClass(TestSuite testSuite, Class<? extends TestCase> clazz) throws Exception
    {
        List<TestCase> testCases = ValidationTestCaseBuilder.allTestCasesFromClass(clazz).build();
        for (TestCase testCase : testCases)
        {
            testSuite.addTest(wrapTestCase(testCase));
        }
    }

    private static void getTestCasesFromClassAndMethod(TestSuite testSuite, Class<? extends TestCase> clazz, String methodName) throws Exception
    {
        TestCase testCase = ValidationTestCaseBuilder.testCaseByMethodName(clazz, methodName).build().get(0);

        testSuite.addTest(wrapTestCase(testCase));
    }

    public static class MyTestCaseWrapper extends AndroidTestCase
    {
        protected TestCase testCase;
        private String appId = "";
        private String deviceId = "";

        public MyTestCaseWrapper(Class<? extends TestCase> clazz, String methodName) throws Exception
        {
            if (!ValidationTestCase.class.isAssignableFrom(clazz))
            {
                throw new RuntimeException("Class must implement ValidationTestCase");
            }
            testCase = (ValidationBaseTestCase) clazz.newInstance();
            testCase.setName(methodName);
            setName(methodName);
        }

        public MyTestCaseWrapper(TestCase testCase) throws Exception
        {
            if (!ValidationTestCase.class.isAssignableFrom(testCase.getClass()))
            {
                throw new RuntimeException("Class must implement ValidationTestCase");
            }
            this.testCase = testCase;
            setName(testCase.getName());
        }

        @Override
        protected void setUp() throws Exception
        {
            String absolutePath = getContext().getFileStreamPath("alljoyn_keystore").getAbsolutePath();

            ValidationTestContextImpl testContext = new ValidationTestContextImpl()
            {
                @Override
                public UserResponse waitForUserInput(UserInputDetails userInputDetails) throws InterruptedException
                {
                    Thread.sleep(5000);
                    return new UserResponse();
                }
            };

            testContext.setAppUnderTestDetails(new AppUnderTestDetails(UUID.fromString(appId), deviceId));

            testContext.setKeyStorePath(absolutePath);
            testContext.setTestCaseNoteListener(new TestCaseNoteListener()
            {
                @Override
                public void addNote(String note)
                {
                    Log.d("SampleTest", String.format("Note added: %s", note));
                }
            });

            ((ValidationTestCase) testCase).setValidationTestContext(testContext);
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

        public String getAppId()
        {
            return appId;
        }

        public void setAppId(String appId)
        {
            this.appId = appId;
        }

        public String getDeviceId()
        {
            return deviceId;
        }

        public void setDeviceId(String deviceId)
        {
            this.deviceId = deviceId;
        }

        @Override
        public String toString()
        {
            return testCase.toString();
        }
    }
}