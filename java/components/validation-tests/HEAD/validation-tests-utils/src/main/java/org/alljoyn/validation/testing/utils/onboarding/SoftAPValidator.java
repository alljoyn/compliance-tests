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
package org.alljoyn.validation.testing.utils.onboarding;

import junit.framework.Assert;

public class SoftAPValidator extends Assert
{
    private static final String SOFT_AP_ASSERT_MESSAGE = "Soft AP name must start with AJ_ or end with _AJ string";
    private static final String SOFT_AP_PREFIX = "AJ_";
    private static final String SOFT_AP_SUFFIX = "_AJ";

    public static void validateSoftAP(String softAPName)
    {
        assertTrue(SOFT_AP_ASSERT_MESSAGE, softAPName.startsWith(SOFT_AP_PREFIX) || softAPName.endsWith(SOFT_AP_SUFFIX));
    }
}