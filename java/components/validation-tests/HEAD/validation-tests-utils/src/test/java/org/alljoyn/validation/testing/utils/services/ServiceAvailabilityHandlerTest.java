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
package org.alljoyn.validation.testing.utils.services;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

public class ServiceAvailabilityHandlerTest
{

    @Test
    public void testSessionLost() throws Exception
    {
        Thread.interrupted(); // clear interrupted state to avoid test from failing
        ServiceAvailabilityHandler serviceAvailabilityHandler = new ServiceAvailabilityHandler();
        serviceAvailabilityHandler.connectionLost();
        assertTrue(serviceAvailabilityHandler.waitForSessionLost(1, TimeUnit.MICROSECONDS));
    }

    @Test
    public void testSessionNotLost() throws Exception
    {
        ServiceAvailabilityHandler serviceAvailabilityHandler = new ServiceAvailabilityHandler();
        assertFalse(serviceAvailabilityHandler.waitForSessionLost(1, TimeUnit.MICROSECONDS));
    }

}
