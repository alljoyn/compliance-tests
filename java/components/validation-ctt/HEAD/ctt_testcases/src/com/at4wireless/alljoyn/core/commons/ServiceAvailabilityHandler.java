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
    private CountDownLatch countDownLatch = new CountDownLatch(1);

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
    public boolean waitForSessionLost(long timeout, TimeUnit unit) throws InterruptedException
    {
        return countDownLatch.await(timeout, unit);
    }

}