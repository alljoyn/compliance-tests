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
package org.alljoyn.validation.testing.it;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.alljoyn.validation.testing.instrument.ValidationTestCaseBuilder;
import org.alljoyn.validation.testing.suites.about.AboutTestSuite;
import org.alljoyn.validation.testing.suites.config.ConfigTestSuite;
import org.alljoyn.validation.testing.suites.controlpanel.ControlPanelTestSuite;
import org.alljoyn.validation.testing.suites.notification.NotificationConsumerTestSuite;
import org.alljoyn.validation.testing.suites.notification.NotificationProducerTestSuite;

import android.test.AndroidTestCase;
import android.util.Log;

public class RunTestSuitesIT extends AndroidTestCase
{
    public static Test suite() throws Exception
    {
        TestSuite testSuite = new TestSuite();

        TestCase startup = new AllJoynInitializer();
        startup.setName("testStartup");
        testSuite.addTest(startup);

        getTestCasesFromClass(testSuite, AboutTestSuite.class);
        getTestCasesFromClass(testSuite, ConfigTestSuite.class);
        getTestCasesFromClass(testSuite, NotificationProducerTestSuite.class);
        getTestCasesFromClass(testSuite, NotificationConsumerTestSuite.class);
        getTestCasesFromClass(testSuite, ControlPanelTestSuite.class);

        return testSuite;
    }

    private static void getTestCasesFromClass(TestSuite testSuite, Class<? extends TestCase> clazz) throws Exception
    {
        getTestCasesFromClass(testSuite, clazz, new ArrayList<String>());
    }

    private static void getTestCasesFromClass(TestSuite testSuite, Class<? extends TestCase> clazz, List<String> excludedTests) throws Exception
    {
        for (TestCase testCase : ValidationTestCaseBuilder.allTestCasesFromClass(clazz).build())
        {
            if (!excludedTests.contains(testCase.getName()))
            {
                Log.d("RunTestSuitesIT", "Adding test case for: " + testCase.getClass() + "; " + testCase.getName());
                testSuite.addTest(new TestCaseWrapper(testCase));
            }
        }
    }
}