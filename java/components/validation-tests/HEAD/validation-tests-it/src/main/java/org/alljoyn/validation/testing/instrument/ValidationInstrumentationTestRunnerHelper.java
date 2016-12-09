/*******************************************************************************
 *  Copyright (c) 2016 Open Connectivity Foundation (OCF) and AllJoyn Open
 *     Source Project (AJOSP) Contributors and others.
 *
 *     SPDX-License-Identifier: Apache-2.0
 *
 *     All rights reserved. This program and the accompanying materials are
 *     made available under the terms of the Apache License, Version 2.0
 *     which accompanies this distribution, and is available at
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Copyright 2016 Open Connectivity Foundation and Contributors to
 *     AllSeen Alliance. All rights reserved.
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.alljoyn.validation.framework.annotation.Disabled;
import org.alljoyn.validation.framework.annotation.ValidationTest;
import org.alljoyn.validation.testing.utils.AllJoynLibraryLoader;
import org.alljoyn.validation.testing.utils.ValidationTestComparator;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.util.Log;

public class ValidationInstrumentationTestRunnerHelper
{
    private final static String TAG = "ValidationInstrumentationTestRunnerHelper";

    public void onCreate(ValidationInstrumentationTestRunner testRunner, Bundle arguments)
    {
        AllJoynLibraryLoader.loadLibrary();
        Context context = testRunner.getContext();
        logApkVersion(context);
        ValidationInstrumentationApplication instrumentApp = (ValidationInstrumentationApplication) context.getApplicationContext();

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
        Log.e(TAG, "testCaseKeyWords: " + testCaseKeyWords);

        List<Class<? extends TestCase>> testGroupClazzList = getTestGroupFromContext(testSuiteList);

        for (Class<? extends TestCase> suiteClazz : testGroupClazzList)
        {
            List<String> testCaseKeywordList = mapToList(testCaseKeyWords);

            try
            {
                getTestCasesFromClass(testSuite, suiteClazz, testCaseKeywordList);
            }
            catch (Exception e)
            {
                Log.e(TAG, "exception", e);
            }
        }

        return testSuite;
    }

    private void logApkVersion(Context context)
    {
        try
        {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            Log.d(TAG, "APK Version : " + packageInfo.versionName);
        }
        catch (NameNotFoundException nameNotFoundException)
        {
            Log.e(TAG, nameNotFoundException.getMessage(), nameNotFoundException);
        }
    }

    private List<String> mapToList(String testCaseKeyWords)
    {
        List<String> testCaseKeywordList = null;

        if (testCaseKeyWords != null && !testCaseKeyWords.isEmpty())
        {
            String[] testCaseKeyWordArray = testCaseKeyWords.split(",");

            if (testCaseKeyWordArray != null && testCaseKeyWordArray.length > 0)
            {
                testCaseKeywordList = new ArrayList<String>();

                for (String testCaseKeyWord : testCaseKeyWordArray)
                {
                    testCaseKeywordList.add(testCaseKeyWord.trim());
                }
            }
        }

        return testCaseKeywordList;
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

    private void getTestCasesFromClass(TestSuite testSuite, Class<? extends TestCase> clazz, List<String> testCaseKeywords) throws Exception
    {
        Method[] methods = clazz.getMethods();
        List<Method> methodList = new ArrayList<Method>();

        for (Method method : methods)
        {
            ValidationTest validationTest = method.getAnnotation(ValidationTest.class);
            Disabled disabled = method.getAnnotation(Disabled.class);

            if (validationTest != null && disabled == null)
            {
                methodList.add(method);
            }
        }

        Collections.sort(methodList, ValidationTestComparator.getInstance());

        for (Method method : methodList)
        {
            ValidationTest validationTest = method.getAnnotation(ValidationTest.class);

            if (testCaseKeywords == null || testCaseKeywords.size() == 0 || testCaseKeywords.contains(validationTest.name()))
            {
                InstrumentationTestCaseWrapper instrumentationTestCaseWrapper = new InstrumentationTestCaseWrapper(clazz, method.getName());
                testSuite.addTest(instrumentationTestCaseWrapper);
            }
        }
    }
}