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

public class ValidationResult
{
    private boolean valid;
    private String failureReason;

    public ValidationResult(boolean valid, String failureReason)
    {
        this.valid = valid;
        this.failureReason = failureReason;
    }

    public boolean isValid()
    {
        return valid;
    }

    public String getFailureReason()
    {
        return failureReason;
    }
}