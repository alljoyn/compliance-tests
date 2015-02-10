/*******************************************************************************
 *  Copyright AllSeen Alliance. All rights reserved.
 *
 *     Permission to use, copy, modify, and/or distribute this software for any
 *     purpose with or without fee is hereby granted, provided that the above
 *     copyright notice and this permission notice appear in all copies.
 *
 *     THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 *     WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 *     MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 *     ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 *     WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 *     ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 *     OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
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
