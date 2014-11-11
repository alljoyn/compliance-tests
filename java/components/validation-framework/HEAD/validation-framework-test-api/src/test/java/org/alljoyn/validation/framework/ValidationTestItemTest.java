/*******************************************************************************
 *   *     Copyright (c) Open Connectivity Foundation (OCF) and AllJoyn Open
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
package org.alljoyn.validation.framework;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;

import org.junit.Test;

public class ValidationTestItemTest
{
    private static final int TIMEOUT_IN_MILLISECONDS = 500;
    private static final int DEFAULT_TIMEOUT_IN_MILLISECONDS = 120000;
    private static final String METHOD_NAME = "methodName";
    private static final String CLASS_NAME = "className";
    private static final String TEST_CASE_ID = "testCaseId";

    @Test
    public void constructWithoutTimeout()
    {
        ValidationTestItem validationTestItem = new ValidationTestItem(TEST_CASE_ID, CLASS_NAME, METHOD_NAME);

        validate(validationTestItem, DEFAULT_TIMEOUT_IN_MILLISECONDS);
        assertNull(validationTestItem.getTestGroup());
    }

    @Test
    public void constructWithTimeout()
    {
        ValidationTestItem validationTestItem = new ValidationTestItem(TEST_CASE_ID, CLASS_NAME, METHOD_NAME, TIMEOUT_IN_MILLISECONDS);

        validate(validationTestItem, TIMEOUT_IN_MILLISECONDS);
        assertNull(validationTestItem.getTestGroup());
    }

    @Test
    public void setTestGroup()
    {
        ValidationTestGroup testGroup = mock(ValidationTestGroup.class);
        ValidationTestItem validationTestItem = new ValidationTestItem(TEST_CASE_ID, CLASS_NAME, METHOD_NAME, TIMEOUT_IN_MILLISECONDS);
        validationTestItem.setTestGroup(testGroup);

        validate(validationTestItem, TIMEOUT_IN_MILLISECONDS);
        assertEquals(testGroup, validationTestItem.getTestGroup());
    }

    private void validate(ValidationTestItem validationTestItem, int timeout)
    {
        assertEquals(TEST_CASE_ID, validationTestItem.getTestCaseId());
        assertEquals(CLASS_NAME, validationTestItem.getClassName());
        assertEquals(METHOD_NAME, validationTestItem.getMethodName());
        assertEquals(timeout, validationTestItem.getTimeoutInMilliseconds());
    }
}