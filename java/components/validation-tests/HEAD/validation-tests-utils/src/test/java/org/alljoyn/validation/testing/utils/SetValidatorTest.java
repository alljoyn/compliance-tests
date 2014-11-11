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
package org.alljoyn.validation.testing.utils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.alljoyn.validation.testing.utils.SetValidator;
import org.alljoyn.validation.testing.utils.ValidationResult;
import org.junit.Before;
import org.junit.Test;

public class SetValidatorTest
{
    private SetValidator<String> setValidator;
    private Set<String> expectedSet;
    private Set<String> foundSet;

    @Before
    public void setup()
    {
        expectedSet = new HashSet<String>();
        foundSet = new HashSet<String>();
        setValidator = new SetValidator<String>();
    }

    @Test
    public void validIfBothSetsAreEmpty()
    {
        ValidationResult validationResult = setValidator.validate(new HashSet<String>(), new HashSet<String>());

        assertTrue(validationResult.isValid());
        assertTrue(validationResult.getFailureReason().isEmpty());
    }

    @Test
    public void validIfSetsAreEqual()
    {
        expectedSet.add("string1");
        foundSet.add("string1");
        ValidationResult validationResult = setValidator.validate(expectedSet, foundSet);

        assertTrue(validationResult.isValid());
        assertTrue(validationResult.getFailureReason().isEmpty());
    }

    @Test
    public void invalidIfExpectedSetHasExtraElement()
    {
        expectedSet.add("string1");
        expectedSet.add("string2");
        foundSet.add("string1");
        ValidationResult validationResult = setValidator.validate(expectedSet, foundSet);

        assertFalse(validationResult.isValid());
        assertTrue(validationResult.getFailureReason().contains("Missing string2"));
    }

    @Test
    public void invalidIfFoundSetHasExtraElement()
    {
        expectedSet.add("string1");
        foundSet.add("string1");
        foundSet.add("string2");
        ValidationResult validationResult = setValidator.validate(expectedSet, foundSet);

        assertFalse(validationResult.isValid());
        assertTrue(validationResult.getFailureReason().contains("Undefined string2"));
    }

    @Test
    public void invalidIfExpectedSetHasExtraElementAndFoundSetHasExtraElement()
    {
        expectedSet.add("string1");
        expectedSet.add("string2");
        foundSet.add("string1");
        foundSet.add("string3");
        ValidationResult validationResult = setValidator.validate(expectedSet, foundSet);

        assertFalse(validationResult.isValid());
        assertTrue(validationResult.getFailureReason().contains("Missing string2"));
        assertTrue(validationResult.getFailureReason().contains("Undefined string3"));
    }
}