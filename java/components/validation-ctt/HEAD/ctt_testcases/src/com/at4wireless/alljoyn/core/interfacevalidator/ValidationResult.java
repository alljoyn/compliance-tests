/*
 *  * 
 *    Copyright (c) 2016 Open Connectivity Foundation and AllJoyn Open
 *    Source Project Contributors and others.
 *    
 *    All rights reserved. This program and the accompanying materials are
 *    made available under the terms of the Apache License, Version 2.0
 *    which accompanies this distribution, and is available at
 *    http://www.apache.org/licenses/LICENSE-2.0

 */
package com.at4wireless.alljoyn.core.interfacevalidator;

// TODO: Auto-generated Javadoc
/**
 * The Class ValidationResult.
 */
public class ValidationResult
{
    
    /** The valid. */
    private boolean valid;
    
    /** The failure reason. */
    private String failureReason;

    /**
     * Instantiates a new validation result.
     *
     * @param valid the valid
     * @param failureReason the failure reason
     */
    public ValidationResult(boolean valid, String failureReason)
    {
        this.valid = valid;
        this.failureReason = failureReason;
    }

    /**
     * Checks if is valid.
     *
     * @return true, if is valid
     */
    public boolean isValid()
    {
        return valid;
    }

    /**
     * Gets the failure reason.
     *
     * @return the failure reason
     */
    public String getFailureReason()
    {
        return failureReason;
    }
}