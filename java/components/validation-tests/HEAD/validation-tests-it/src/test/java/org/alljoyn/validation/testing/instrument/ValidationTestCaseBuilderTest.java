/*******************************************************************************
 *  Copyright (c) Open Connectivity Foundation (OCF) and AllJoyn Open
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import junit.framework.TestCase;
import junit.framework.TestFailure;
import junit.framework.TestResult;

import org.alljoyn.validation.framework.ValidationBaseTestCase;
import org.alljoyn.validation.framework.ValidationTestCase;
import org.alljoyn.validation.framework.ValidationTestContext;
import org.alljoyn.validation.framework.annotation.Disabled;
import org.alljoyn.validation.framework.annotation.ValidationTest;
import org.alljoyn.validation.testing.instrument.ValidationTestCaseBuilder;
import org.junit.Test;

public class ValidationTestCaseBuilderTest
{

    private static final String NON_EXISTENT_CLASS = "org.alljoyn.validation.NoSuchClass";

    private static class TestCaseWithNoTests extends ValidationBaseTestCase
    {
        public void test02()
        {
        }

        @ValidationTest(name = "test03")
        @Disabled
        public void test03()
        {
            test02();
        }

        @ValidationTest(name = "notTest04")
        public void notTest04()
        {
        }
    }

    private static class TestCaseThatCannotBeInstantiated extends ValidationBaseTestCase
    {
        @ValidationTest(name = "test01")
        public void test01()
        {
        }
    }

    private static class TestCaseThatDoesNotImplementTestCase implements ValidationTestCase
    {
        @ValidationTest(name = "test01")
        public void test01()
        {
        }

        @Override
        public void setValidationTestContext(ValidationTestContext testContext)
        {
        }

        @Override
        public ValidationTestContext getValidationTestContext()
        {
            return null;
        }
    }

    private static class TestCaseThatDoesNotImplementValidationTestCase extends TestCase
    {
        @ValidationTest(name = "test01")
        public void test01()
        {
        }
    }

    public static class TestCaseOrderWithOrder extends ValidationBaseTestCase
    {
        @ValidationTest(name = "Sample-v1-01", order = -2)
        public void test1()
        {
        }

        @ValidationTest(name = "Sample-v1-02")
        public void test2()
        {
        }

        @ValidationTest(name = "Sample-v1-03")
        public void test3()
        {
        }

        @ValidationTest(name = "Sample-v1-04", order = -1)
        public void test4()
        {
        }
    }

    @Test
    public void testAllTestCasesFromClassWithClass() throws Throwable
    {
        List<TestCase> testCases = ValidationTestCaseBuilder.allTestCasesFromClass(ExampleTestCase.class).build();
        assertEquals(2, testCases.size());
        TestResult testResult = testCases.get(0).run();
        assertTrue(testResult.wasSuccessful());

        testResult = testCases.get(1).run();
        assertTrue(testResult.wasSuccessful());
    }

    @Test
    public void testAllTestCasesFromClassWithOrder() throws Throwable
    {
        List<TestCase> testCases = ValidationTestCaseBuilder.allTestCasesFromClass(TestCaseOrderWithOrder.class).build();
        assertEquals(4, testCases.size());
        assertEquals("test1", testCases.get(0).getName());
        assertEquals("test4", testCases.get(1).getName());
        assertEquals("test2", testCases.get(2).getName());
        assertEquals("test3", testCases.get(3).getName());
    }

    @Test
    public void testAllTestCasesFromClass() throws Throwable
    {
        List<TestCase> testCases = ValidationTestCaseBuilder.allTestCasesFromClass(ExampleTestCase.class.getName()).build();
        assertEquals(2, testCases.size());
        TestResult testResult = testCases.get(0).run();
        assertTrue(testResult.wasSuccessful());

        testResult = testCases.get(1).run();
        assertTrue(testResult.wasSuccessful());
    }

    @Test
    public void testAllTestCasesFromClassWhenNoTestCasesInClass() throws Throwable
    {
        List<TestCase> testCases = ValidationTestCaseBuilder.allTestCasesFromClass(TestCaseWithNoTests.class.getName()).build();

        assertEquals(1, testCases.size());
        TestResult testResult = testCases.get(0).run();

        TestFailure testFailure = testResult.failures().nextElement();
        assertEquals("No test cases annotated with ValidationTest in org.alljoyn.validation.testing.instrument.ValidationTestCaseBuilderTest$TestCaseWithNoTests",
                testFailure.exceptionMessage());
    }

    @Test
    public void testAllTestCasesFromClassWhenClassNotFound() throws Throwable
    {
        List<TestCase> testCases = ValidationTestCaseBuilder.allTestCasesFromClass(NON_EXISTENT_CLASS).build();
        assertEquals(1, testCases.size());
        TestResult testResult = testCases.get(0).run();

        TestFailure testFailure = testResult.errors().nextElement();
        assertEquals(ClassNotFoundException.class, testFailure.thrownException().getClass());
        assertEquals(NON_EXISTENT_CLASS, testFailure.thrownException().getMessage());
    }

    @Test
    public void testAllTestCasesFromClassWhenIllegalAccessException() throws Throwable
    {

        List<TestCase> testCases = ValidationTestCaseBuilder.allTestCasesFromClass(TestCaseThatCannotBeInstantiated.class.getName()).build();

        assertEquals(1, testCases.size());
        TestResult testResult = testCases.get(0).run();

        TestFailure testFailure = testResult.errors().nextElement();
        assertEquals(IllegalAccessException.class, testFailure.thrownException().getClass());
        assertEquals(
                "Class org.alljoyn.validation.testing.instrument.ValidationTestCaseBuilder can not access a member of class org.alljoyn.validation.testing.instrument.ValidationTestCaseBuilderTest$TestCaseThatCannotBeInstantiated with modifiers \"private\"",
                testFailure.thrownException().getMessage());
    }

    @Test
    public void testAllTestCasesFromClassWhenTestCaseDoesNotImplementTestCase() throws Throwable
    {
        List<TestCase> testCases = ValidationTestCaseBuilder.allTestCasesFromClass(TestCaseThatDoesNotImplementTestCase.class.getName()).build();

        assertEquals(1, testCases.size());
        TestResult testResult = testCases.get(0).run();

        TestFailure testFailure = testResult.failures().nextElement();
        assertEquals("Test case must extend from junit.framework.TestCase", testFailure.exceptionMessage());
    }

    @Test
    public void testAllTestCasesFromClassWhenTestCaseDoesNotImplementValidationTestCase() throws Throwable
    {
        List<TestCase> testCases = ValidationTestCaseBuilder.allTestCasesFromClass(TestCaseThatDoesNotImplementValidationTestCase.class.getName()).build();
        assertEquals(1, testCases.size());
        TestResult testResult = testCases.get(0).run();

        TestFailure testFailure = testResult.failures().nextElement();
        assertEquals("Test case must implement org.alljoyn.validation.framework.ValidationTestCase", testFailure.exceptionMessage());
    }

    @Test
    public void testCaseByMethodNameWithClass() throws Throwable
    {
        List<TestCase> testCases = ValidationTestCaseBuilder.testCaseByMethodName(ExampleTestCase.class, "test01").build();
        TestResult testResult = testCases.get(0).run();

        assertTrue(testResult.wasSuccessful());
    }

    @Test
    public void testCaseByMethodName() throws Throwable
    {
        List<TestCase> testCases = ValidationTestCaseBuilder.testCaseByMethodName(ExampleTestCase.class.getName(), "test01").build();
        TestResult testResult = testCases.get(0).run();

        assertTrue(testResult.wasSuccessful());
    }

    @Test
    public void testCaseByMethodNameWhenInstanceClassNotFound() throws Throwable
    {
        List<TestCase> testCases = ValidationTestCaseBuilder.testCaseByMethodName(NON_EXISTENT_CLASS, "test01").build();

        TestResult testResult = testCases.get(0).run();

        TestFailure testFailure = testResult.errors().nextElement();
        assertEquals(ClassNotFoundException.class, testFailure.thrownException().getClass());
        assertEquals(NON_EXISTENT_CLASS, testFailure.thrownException().getMessage());
    }

    @Test
    public void testCaseByMethodNameWhenNoSuchMethod() throws Throwable
    {
        List<TestCase> testCases = ValidationTestCaseBuilder.testCaseByMethodName(ExampleTestCase.class.getName(), "test10").build();

        TestResult testResult = testCases.get(0).run();

        TestFailure testFailure = testResult.errors().nextElement();
        assertEquals(NoSuchMethodException.class, testFailure.thrownException().getClass());
        assertEquals("org.alljoyn.validation.testing.instrument.ExampleTestCase.test10()", testFailure.thrownException().getMessage());
    }

    @Test
    public void testCaseByMethodNameWhenMethodDoesNotStartWithTest() throws Throwable
    {
        List<TestCase> testCases = ValidationTestCaseBuilder.testCaseByMethodName(ExampleTestCase.class.getName(), "notTest04").build();

        TestResult testResult = testCases.get(0).run();

        TestFailure testFailure = testResult.failures().nextElement();
        assertEquals("The test case method name notTest04 in org.alljoyn.validation.testing.instrument.ExampleTestCase does not start with test", testFailure.exceptionMessage());
    }

    @Test
    public void testCaseByMethodNameWhenMethodIsNotAnnotatedWithValidationTest() throws Throwable
    {
        List<TestCase> testCases = ValidationTestCaseBuilder.testCaseByMethodName(ExampleTestCase.class.getName(), "test02").build();

        TestResult testResult = testCases.get(0).run();

        TestFailure testFailure = testResult.failures().nextElement();
        assertEquals("The test case method name test02 in org.alljoyn.validation.testing.instrument.ExampleTestCase is not annotated with ValidationTest",
                testFailure.exceptionMessage());
    }

    @Test
    public void testCaseByMethodNameWhenMethodIsDisabled() throws Throwable
    {
        List<TestCase> testCases = ValidationTestCaseBuilder.testCaseByMethodName(ExampleTestCase.class.getName(), "test03").build();

        TestResult testResult = testCases.get(0).run();

        TestFailure testFailure = testResult.failures().nextElement();
        assertEquals("The test case method name test03 in org.alljoyn.validation.testing.instrument.ExampleTestCase is annotated with Disabled", testFailure.exceptionMessage());
    }

    @Test
    public void testCaseByMethodNameWithClassAndMethod() throws Throwable
    {
        List<TestCase> testCases = ValidationTestCaseBuilder.testCaseByMethod(ExampleTestCase.class, ExampleTestCase.class.getMethod("test01")).build();
        TestResult testResult = testCases.get(0).run();

        assertTrue(testResult.wasSuccessful());
    }

    @Test
    public void testCaseByTestNameWithClass() throws Throwable
    {
        List<TestCase> testCases = ValidationTestCaseBuilder.testCaseByTestName(ExampleTestCase.class, "Example-v1-01").build();

        TestResult testResult = testCases.get(0).run();

        assertTrue(testResult.wasSuccessful());
    }

    @Test
    public void testCaseByTestName() throws Throwable
    {
        List<TestCase> testCases = ValidationTestCaseBuilder.testCaseByTestName(ExampleTestCase.class.getName(), "Example-v1-01").build();

        TestResult testResult = testCases.get(0).run();

        assertTrue(testResult.wasSuccessful());
    }

    @Test
    public void testCaseByTestNameWhenNotFound() throws Throwable
    {
        List<TestCase> testCases = ValidationTestCaseBuilder.testCaseByTestName(ExampleTestCase.class.getName(), "Example-v1-10").build();

        TestResult testResult = testCases.get(0).run();

        TestFailure testFailure = testResult.failures().nextElement();
        assertEquals("No test case annotated as 'Example-v1-10' found in org.alljoyn.validation.testing.instrument.ExampleTestCase", testFailure.exceptionMessage());
    }

    @Test
    public void testCaseByTestNameWhenClassNotFound() throws Throwable
    {
        List<TestCase> testCases = ValidationTestCaseBuilder.testCaseByTestName(NON_EXISTENT_CLASS, "Example-v1-10").build();

        TestResult testResult = testCases.get(0).run();

        TestFailure testFailure = testResult.errors().nextElement();
        assertEquals(ClassNotFoundException.class, testFailure.thrownException().getClass());
        assertEquals(NON_EXISTENT_CLASS, testFailure.thrownException().getMessage());
    }

    @Test
    public void testCaseByTestNameWhenDisabled() throws Throwable
    {
        List<TestCase> testCases = ValidationTestCaseBuilder.testCaseByTestName(ExampleTestCase.class.getName(), "Example-v1-03").build();

        TestResult testResult = testCases.get(0).run();

        TestFailure testFailure = testResult.failures().nextElement();
        assertEquals("The test case method name test03 in org.alljoyn.validation.testing.instrument.ExampleTestCase is annotated with Disabled", testFailure.exceptionMessage());
    }

    @Test
    public void testCaseByTestNameWhenMethodDoesNotStartWithTest() throws Throwable
    {
        List<TestCase> testCases = ValidationTestCaseBuilder.testCaseByTestName(ExampleTestCase.class.getName(), "Example-v1-04").build();

        TestResult testResult = testCases.get(0).run();

        TestFailure testFailure = testResult.failures().nextElement();
        assertEquals("The test case method name notTest04 in org.alljoyn.validation.testing.instrument.ExampleTestCase does not start with test", testFailure.exceptionMessage());
    }

}