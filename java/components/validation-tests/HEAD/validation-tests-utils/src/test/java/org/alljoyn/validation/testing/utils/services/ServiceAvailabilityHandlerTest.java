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