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
package com.at4wireless.alljoyn.core.commons;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.alljoyn.services.common.ServiceAvailabilityListener;

// TODO: Auto-generated Javadoc
/**
 * The Class ServiceAvailabilityHandler.
 */
public class ServiceAvailabilityHandler implements ServiceAvailabilityListener
{
    
    /** The count down latch. */
    private  CountDownLatch countDownLatch = new CountDownLatch(1);

    /* (non-Javadoc)
     * @see org.alljoyn.services.common.ServiceAvailabilityListener#connectionLost()
     */
    @Override
    public void connectionLost()
    {
        countDownLatch.countDown();
    }

    /**
     * Wait for session lost.
     *
     * @param timeout the timeout
     * @param unit the unit
     * @return true, if successful
     * @throws InterruptedException the interrupted exception
     */
    public  boolean waitForSessionLost(long timeout, TimeUnit unit) throws InterruptedException
    {
        return countDownLatch.await(timeout, unit);
    }

}

