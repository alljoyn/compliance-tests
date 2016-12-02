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
package org.alljoyn.validation.testing.utils.audio.handlers;

import static junit.framework.Assert.assertEquals;

import java.util.concurrent.CountDownLatch;

import org.alljoyn.bus.BusException;
import org.alljoyn.bus.BusObject;
import org.alljoyn.bus.annotation.BusSignalHandler;
import org.alljoyn.validation.testing.utils.audio.AudioTransports.Configuration;
import org.alljoyn.validation.testing.utils.audio.AudioTransports.Port;
import org.alljoyn.validation.testing.utils.log.Logger;
import org.alljoyn.validation.testing.utils.log.LoggerFactory;

public class OwnershipLostSignalHandler implements Port, BusObject
{
    private static final String TAG = "OwnershipLostHandler";
    private static final Logger logger = LoggerFactory.getLogger(TAG);
    private CountDownLatch countDownLatch;
    private String expectedNewOwner;

    public OwnershipLostSignalHandler(CountDownLatch countDownLatch)
    {
        this.countDownLatch = countDownLatch;
    }

    @BusSignalHandler(iface = "org.alljoyn.Stream.Port", signal = "OwnershipLost")
    public void handleOwnershipLostSignal(String newOwner)
    {
        logger.debug("New owner: " + newOwner);
        assertEquals("New owner name does not match", expectedNewOwner, newOwner);
        countDownLatch.countDown();
    }

    public void setExpectedNewOwner(String expectedNewOwner)
    {
        this.expectedNewOwner = expectedNewOwner;
    }

    public CountDownLatch getCountDownLatch()
    {
        return countDownLatch;
    }

    @Override
    public short getVersion() throws BusException
    {
        return 1;
    }

    @Override
    public byte getDirection() throws BusException
    {
        return 0;
    }

    @Override
    public Configuration[] getCapabilities() throws BusException
    {
        return null;
    }

    @Override
    public void OwnershipLost(String newOwner) throws BusException
    {
    }

    @Override
    public void Connect(String host, String objectPath, Configuration configuration)
    {
    }
}