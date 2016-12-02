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
//import org.alljoyn.validation.testing.suites.gwagent.GWAgentTestSuite;
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
        //getTestCasesFromClass(testSuite, GWAgentTestSuite.class);

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