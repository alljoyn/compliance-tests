/*******************************************************************************
 *     Copyright (c) Open Connectivity Foundation (OCF), AllJoyn Open Source
 *     Project (AJOSP) Contributors and others.
 *     
 *     SPDX-License-Identifier: Apache-2.0
 *     
 *     All rights reserved. This program and the accompanying materials are
 *     made available under the terms of the Apache License, Version 2.0
 *     which accompanies this distribution, and is available at
 *     http://www.apache.org/licenses/LICENSE-2.0
 *     
 *     Copyright (c) Open Connectivity Foundation and Contributors to AllSeen
 *     Alliance. All rights reserved.
 *     
 *     Permission to use, copy, modify, and/or distribute this software for
 *     any purpose with or without fee is hereby granted, provided that the
 *     above copyright notice and this permission notice appear in all
 *     copies.
 *     
 *     THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL
 *     WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED
 *     WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE
 *     AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL
 *     DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR
 *     PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER
 *     TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 *     PERFORMANCE OF THIS SOFTWARE.
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