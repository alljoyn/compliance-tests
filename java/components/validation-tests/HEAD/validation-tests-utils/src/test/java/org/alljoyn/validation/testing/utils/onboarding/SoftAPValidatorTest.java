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

import static org.junit.Assert.fail;
import junit.framework.AssertionFailedError;

import org.junit.Test;

public class SoftAPValidatorTest
{
    @Test
    public void testValidSoftAPPrefix()
    {
        SoftAPValidator.validateSoftAP("AJ_AC_C737664260");
    }

    @Test
    public void testValidSoftAPSuffix()
    {
        SoftAPValidator.validateSoftAP("CA_C737664260_AJ");
    }

    @Test(expected = AssertionFailedError.class)
    public void testInvalidSoftAP()
    {
        SoftAPValidator.validateSoftAP("AC_C737664260");
        fail();
    }
}