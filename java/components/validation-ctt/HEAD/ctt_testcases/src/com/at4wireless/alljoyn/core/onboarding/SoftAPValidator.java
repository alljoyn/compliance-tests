/*******************************************************************************
 *     Copyright (c) Open Connectivity Foundation (OCF) and AllJoyn Open
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
package com.at4wireless.alljoyn.core.onboarding;


import com.at4wireless.alljoyn.core.commons.log.Logger;
import com.at4wireless.alljoyn.core.commons.log.WindowsLoggerImpl;

public class SoftAPValidator
{
	private static final Logger logger = new WindowsLoggerImpl(SoftAPValidator.class.getSimpleName());
	
    private static final String SOFT_AP_ASSERT_MESSAGE = "Soft AP name must start with AJ_ or end with _AJ string";
    private static final String SOFT_AP_PREFIX = "AJ_";
    private static final String SOFT_AP_SUFFIX = "_AJ";

    public static boolean validateSoftAP(String softAPName) //[AT4] returns boolean to change verdict after checking
    {
    	boolean condition = softAPName.startsWith(SOFT_AP_PREFIX) || softAPName.endsWith(SOFT_AP_SUFFIX);
        assertTrue(SOFT_AP_ASSERT_MESSAGE, condition);
        return condition;
    }
    
    private static void assertTrue(String errorMessage, boolean condition)
    {
    	if (!condition)
    	{
    		logger.error(errorMessage);
    	}
    	else
    	{
    		logger.info("Partial Verdict: PASS");
    	}
    }
}