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
package org.alljoyn.validation.testing.utils;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.alljoyn.validation.framework.annotation.ValidationTest;
import org.alljoyn.validation.testing.utils.ValidationTestComparator;
import org.junit.Test;

import edu.emory.mathcs.backport.java.util.Collections;

public class ValidationTestComparatorTest
{

    private static class SampleTest
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
    public void testSameOrder() throws Exception
    {
        List<Method> methods = new ArrayList<Method>();
        methods.add(SampleTest.class.getMethod("test1"));
        methods.add(SampleTest.class.getMethod("test2"));
        methods.add(SampleTest.class.getMethod("test3"));
        methods.add(SampleTest.class.getMethod("test4"));
        Collections.sort(methods, ValidationTestComparator.getInstance());
        assertEquals("test1", methods.get(0).getName());
        assertEquals("test4", methods.get(1).getName());
        assertEquals("test2", methods.get(2).getName());
        assertEquals("test3", methods.get(3).getName());
    }
}