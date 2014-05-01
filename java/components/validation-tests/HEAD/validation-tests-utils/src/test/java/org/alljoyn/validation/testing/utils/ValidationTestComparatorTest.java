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
