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
package com.at4wireless.alljoyn.core.interfacevalidator;

import java.util.HashSet;
import java.util.Set;

public class SetValidator<E>
{
    public ValidationResult validate(Set<E> expectedObjectSet, Set<E> foundObjectSet)
    {
        boolean valid = true;
        StringBuilder failureReasonBuilder = new StringBuilder();
        Set<Object> extraObjects = subtract(foundObjectSet, expectedObjectSet);
        Set<Object> missingObjects = subtract(expectedObjectSet, foundObjectSet);

        if (!missingObjects.isEmpty())
        {
            valid = false;

            for (Object object : missingObjects)
            {
                failureReasonBuilder.append(" - Missing ");
                failureReasonBuilder.append(object.toString());
            }
        }

        if (!extraObjects.isEmpty())
        {
            valid = false;

            for (Object object : extraObjects)
            {
                failureReasonBuilder.append(" - Undefined ");
                failureReasonBuilder.append(object.toString());
            }
        }

        return new ValidationResult(valid, failureReasonBuilder.toString());
    }

    private Set<Object> subtract(Set<E> expectedObjectSet, Set<E> foundObjectSet)
    {
        Set<Object> missingObjects = new HashSet<Object>();

        for (Object object : expectedObjectSet)
        {
            if (!foundObjectSet.contains(object))
            {
                missingObjects.add(object);
            }
        }

        return missingObjects;
    }
}