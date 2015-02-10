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