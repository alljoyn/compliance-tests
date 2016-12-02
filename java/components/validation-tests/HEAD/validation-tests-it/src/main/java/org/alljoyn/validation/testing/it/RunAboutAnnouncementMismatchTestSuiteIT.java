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

import java.lang.reflect.Method;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.alljoyn.validation.framework.annotation.ValidationTest;
import org.alljoyn.validation.testing.suites.about.AboutTestSuite;

import android.test.AndroidTestCase;

public class RunAboutAnnouncementMismatchTestSuiteIT extends AndroidTestCase
{
    static
    {
        System.loadLibrary("alljoyn_java");
    }

    public static Test suite() throws Exception
    {
        TestSuite testSuite = new TestSuite();
        getTestCasesFromClass(testSuite, AboutTestSuite.class);
        return testSuite;
    }

    private static void getTestCasesFromClass(TestSuite testSuite, Class<? extends TestCase> clazz) throws Exception
    {
        Method[] methods = clazz.getMethods();
        for (Method method : methods)
        {
            ValidationTest validationTest = method.getAnnotation(ValidationTest.class);

            if (method.getName().startsWith("About-v1-04") && (validationTest != null))
            {
                testSuite.addTest(new AboutAnnouncementMismatchTestCaseWrapper(clazz, method.getName()));
            }
        }
    }
}