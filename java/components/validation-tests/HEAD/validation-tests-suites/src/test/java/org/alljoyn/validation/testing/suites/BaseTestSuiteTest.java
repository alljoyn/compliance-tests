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
package org.alljoyn.validation.testing.suites;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import junit.framework.AssertionFailedError;

import org.alljoyn.bus.BusException;

public abstract class BaseTestSuiteTest
{
    public interface TestWrapper
    {
        void executeTestMethod() throws Exception;
    }

    protected void executeTestMethod(TestWrapper testWrapper, BusException expectedException) throws AssertionFailedError, Exception
    {
        BusException actualException = null;
        try
        {
            executeTestMethod(testWrapper);
            fail(String.format("No Exception thrown, expecting BusException: %s", expectedException.toString()));
        }
        catch (BusException e)
        {
            actualException = e;
        }
        assertEquals(expectedException, actualException);
    }

    protected void executeTestMethodThrowsException(TestWrapper testWrapper, String expectedExceptionMessage) throws AssertionFailedError, Exception
    {
        String actualExceptionMessage = null;
        try
        {
            executeTestMethod(testWrapper);
            fail(String.format("No Exception thrown, expecting Exception with message: %s", expectedExceptionMessage));
        }
        catch (Exception e)
        {
            actualExceptionMessage = e.getMessage();
        }
        assertEquals(expectedExceptionMessage, actualExceptionMessage);
    }

    protected void executeTestMethodFailsAssertion(TestWrapper testWrapper, String expectedAssertionFailure) throws Exception
    {
        String assertionFailureMessage = null;
        try
        {
            executeTestMethod(testWrapper);
            fail(String.format("No assertion failure, expecting assertionFailure with message: %s", expectedAssertionFailure));
        }
        catch (AssertionFailedError e)
        {
            assertionFailureMessage = e.getMessage();
        }
        assertEquals(expectedAssertionFailure, assertionFailureMessage);
    }

    abstract protected void executeTestMethod(TestWrapper testWrapper) throws Exception;

}