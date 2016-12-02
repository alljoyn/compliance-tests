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

import java.lang.reflect.Method;
import java.util.Comparator;

import org.alljoyn.validation.framework.annotation.ValidationTest;

public class ValidationTestComparator implements Comparator<Method>
{
    private static final ValidationTestComparator INSTANCE = new ValidationTestComparator();

    private ValidationTestComparator()
    {
    }

    public static ValidationTestComparator getInstance()
    {
        return INSTANCE;
    }

    @Override
    public int compare(Method o1, Method o2)
    {
        ValidationTest validationTest1 = o1.getAnnotation(ValidationTest.class);
        ValidationTest validationTest2 = o2.getAnnotation(ValidationTest.class);
        if (validationTest1.order() == validationTest2.order())
        {
            return validationTest1.name().compareTo(validationTest2.name());
        }
        else
        {
            return Integer.valueOf(validationTest1.order()).compareTo(validationTest2.order());
        }
    }
}