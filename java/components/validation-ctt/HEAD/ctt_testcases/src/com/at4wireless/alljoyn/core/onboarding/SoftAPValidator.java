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