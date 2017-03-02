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