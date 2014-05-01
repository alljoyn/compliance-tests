/*******************************************************************************
 *  Copyright (c) 2013 - 2014, AllSeen Alliance. All rights reserved.
 *
 *     Permission to use, copy, modify, and/or distribute this software for any
 *     purpose with or without fee is hereby granted, provided that the above
 *     copyright notice and this permission notice appear in all copies.
 *
 *     THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 *     WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 *     MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 *     ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 *     WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 *     ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 *     OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 *******************************************************************************/
package org.alljoyn.validation.testing.instrument;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.alljoyn.validation.framework.annotation.ValidationTest;
import org.alljoyn.validation.testing.utils.AllJoynLibraryLoader;

import android.os.Bundle;
import android.util.Log;

public class ValidationInstrumentationTestRunnerHelper
{
    private final static String TAG = "ValidationInstrumentationTestRunnerHelper";

    public void onCreate(ValidationInstrumentationTestRunner testRunner, Bundle arguments)
    {
        AllJoynLibraryLoader.loadLibrary();

        ValidationInstrumentationApplication instrumentApp = (ValidationInstrumentationApplication) testRunner.getContext().getApplicationContext();
        if (arguments != null)
        {
            for (InstrumentationArgKey argKey : InstrumentationArgKey.values())
            {
                String paramkey = argKey.getValue();
                String paramValue = arguments.getString(paramkey);
                Log.d(TAG, paramkey + " : " + paramValue);
                instrumentApp.setInstrumentParameter(paramkey, paramValue);
            }
        }
    }

    public TestSuite getAllTests(ValidationInstrumentationTestRunner testRunner)
    {
        TestSuite testSuite = new TestSuite();
        ValidationInstrumentationApplication instrumentApp = (ValidationInstrumentationApplication) testRunner.getContext().getApplicationContext();
        String testSuiteList = instrumentApp.getInstrumentParameter(InstrumentationArgKey.TestSuiteList.getValue());
        String testCaseKeyWords = instrumentApp.getInstrumentParameter(InstrumentationArgKey.TestCaseName.getValue());

        List<Class<? extends TestCase>> testGroupClazzList = getTestGroupFromContext(testSuiteList);

        for (Class<? extends TestCase> suiteClazz : testGroupClazzList)
        {
            if (testCaseKeyWords == null || testCaseKeyWords.equals(""))
            {
                try
                {
                    getTestCasesFromClass(testSuite, suiteClazz, null);
                }
                catch (Exception e)
                {
                    Log.e(TAG, "exception", e);
                }
            }
            else
            {
                String[] testCaseKeywordArray = testCaseKeyWords.split(",");
                for (String testCaseKeyword : testCaseKeywordArray)
                {
                    try
                    {
                        getTestCasesFromClass(testSuite, suiteClazz, testCaseKeyword.trim());
                    }
                    catch (Exception e)
                    {
                        Log.e(TAG, "exception", e);
                    }
                }
            }
        }
        return testSuite;
    }

    @SuppressWarnings("unchecked")
    private List<Class<? extends TestCase>> getTestGroupFromContext(String testSuiteList)
    {
        String[] testSuites = testSuiteList.split(",");
        List<Class<? extends TestCase>> testSuiteClassList = new ArrayList<Class<? extends TestCase>>();

        for (String testSuite : testSuites)
        {
            try
            {
                Class<? extends TestCase> suiteClass = (Class<? extends TestCase>) Class.forName(testSuite.trim());
                testSuiteClassList.add(suiteClass);
            }
            catch (ClassNotFoundException e)
            {
                Log.e(TAG, "Invalid test group class name.");
            }
        }
        return testSuiteClassList;
    }

    private void getTestCasesFromClass(TestSuite testSuite, Class<? extends TestCase> clazz, String testCaseKeyword) throws Exception
    {
        Method[] methods = clazz.getMethods();
        for (Method method : methods)
        {
            ValidationTest validationTest = method.getAnnotation(ValidationTest.class);

            if (validationTest != null)
            {
                if (testCaseKeyword == null || (testCaseKeyword != null && testCaseKeyword.equals(validationTest.name())))
                {
                    InstrumentationTestCaseWrapper tcw = new InstrumentationTestCaseWrapper(clazz, method.getName());
                    testSuite.addTest(tcw);
                }
            }
        }
    }
}
