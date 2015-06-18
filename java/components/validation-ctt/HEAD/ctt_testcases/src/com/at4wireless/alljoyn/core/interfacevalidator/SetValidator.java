/*
 * Copyright (c) Open Connectivity Foundation (OCF) and AllJoyn Open
 *    Source Project (AJOSP) Contributors and others.
 *
 *    SPDX-License-Identifier: Apache-2.0
 *
 *    All rights reserved. This program and the accompanying materials are
 *    made available under the terms of the Apache License, Version 2.0
 *    which accompanies this distribution, and is available at
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Copyright (c) Open Connectivity Foundation and Contributors to AllSeen
 *    Alliance. All rights reserved.
 *
 *    Permission to use, copy, modify, and/or distribute this software for
 *    any purpose with or without fee is hereby granted, provided that the
 *    above copyright notice and this permission notice appear in all
 *    copies.
 *
 *     THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL
 *     WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED
 *     WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE
 *     AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL
 *     DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR
 *     PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER
 *     TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 *     PERFORMANCE OF THIS SOFTWARE.
 */
package com.at4wireless.alljoyn.core.interfacevalidator;

import java.util.HashSet;
import java.util.Set;



// TODO: Auto-generated Javadoc
/**
 * The Class SetValidator.
 *
 * @param <E> the element type
 */
public class SetValidator<E>
{
    
    /**
     * Validate.
     *
     * @param expectedObjectSet the expected object set
     * @param foundObjectSet the found object set
     * @return the validation result
     */
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

    /**
     * Subtract.
     *
     * @param expectedObjectSet the expected object set
     * @param foundObjectSet the found object set
     * @return the sets the
     */
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