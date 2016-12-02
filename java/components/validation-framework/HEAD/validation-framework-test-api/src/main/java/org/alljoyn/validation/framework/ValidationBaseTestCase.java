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
package org.alljoyn.validation.framework;

/**
 * This is base test case class that can be extended by other test case classes.
 * A test case class must extend {@code junit.framework.TestCase} and also must
 * implement {@code ValidationTestCase}.
 */
public class ValidationBaseTestCase extends junit.framework.TestCase implements ValidationTestCase
{
    private ValidationTestContext testContext;

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.alljoyn.validation.framework.ValidationTestCase#setValidationTestContext
     * (org.alljoyn.validation.framework.ValidationTestContext)
     */
    @Override
    public void setValidationTestContext(ValidationTestContext testContext)
    {
        this.testContext = testContext;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.alljoyn.validation.framework.ValidationTestCase#getValidationTestContext
     * ()
     */
    @Override
    public ValidationTestContext getValidationTestContext()
    {
        return testContext;
    }
}