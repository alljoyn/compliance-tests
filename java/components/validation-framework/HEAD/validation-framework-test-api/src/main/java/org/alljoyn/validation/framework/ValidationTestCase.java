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
 * An implementation of {@code ValidationTestCase} can be executed to test an
 * Alljoyn service.
 * 
 */
public interface ValidationTestCase
{
    /**
     * This method is used to set the {@code ValidationTestContext} which can be
     * used by the tests.
     * 
     * @param testContext
     *            thats contains information used in the tests
     */
    void setValidationTestContext(ValidationTestContext testContext);

    /**
     * @return {@link ValidationTestContext} thats contains information used in
     *         the tests
     */
    ValidationTestContext getValidationTestContext();
}