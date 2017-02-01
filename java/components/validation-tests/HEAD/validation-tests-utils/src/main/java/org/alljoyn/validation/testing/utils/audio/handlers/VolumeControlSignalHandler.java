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

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import org.alljoyn.bus.BusException;
import org.alljoyn.bus.BusObject;
import org.alljoyn.bus.annotation.BusSignalHandler;
import org.alljoyn.validation.testing.utils.audio.AudioTransports.Volume;
import org.alljoyn.validation.testing.utils.audio.AudioTransports.VolumeRange;
import org.alljoyn.validation.testing.utils.log.Logger;
import org.alljoyn.validation.testing.utils.log.LoggerFactory;

public class VolumeControlSignalHandler implements Volume, BusObject
{
    private static final String TAG = "VolControlSignalHandler";
    private static final Logger logger = LoggerFactory.getLogger(TAG);
    private LinkedBlockingDeque<Boolean> muteChangedSignalQueue = new LinkedBlockingDeque<Boolean>();
    private LinkedBlockingDeque<Short> volumeChangedSignalQueue = new LinkedBlockingDeque<Short>();

    @Override
    @BusSignalHandler(iface = "org.alljoyn.Control.Volume", signal = "MuteChanged")
    public void MuteChanged(boolean newMute) throws BusException
    {
        logger.debug("MuteChanged signal received: " + newMute);
        muteChangedSignalQueue.add(newMute);
    }

    @Override
    @BusSignalHandler(iface = "org.alljoyn.Control.Volume", signal = "VolumeChanged")
    public void VolumeChanged(short newVolume) throws BusException
    {
        logger.debug("VolumeChanged signal received: " + newVolume);
        volumeChangedSignalQueue.add(newVolume);
    }

    public Boolean waitForNextMuteChangedSignal(long timeout, TimeUnit unit) throws InterruptedException
    {
        logger.debug("Waiting for MuteChanged signal");

        return muteChangedSignalQueue.poll(timeout, unit);
    }

    public Short waitForNextVolumeChangedSignal(long timeout, TimeUnit unit) throws InterruptedException
    {
        logger.debug("Waiting for VolumeChanged signal");

        return volumeChangedSignalQueue.poll(timeout, unit);
    }

    @Override
    public short getVersion() throws BusException
    {
        return 0;
    }

    @Override
    public boolean getMute() throws BusException
    {
        return false;
    }

    @Override
    public void setMute(boolean isMuted) throws BusException
    {
    }

    @Override
    public short getVolume() throws BusException
    {
        return 0;
    }

    @Override
    public void setVolume(short level) throws BusException
    {
    }

    @Override
    public VolumeRange getVolumeRange() throws BusException
    {
        return null;
    }

    @Override
    public void AdjustVolume(short delta) throws BusException
    {
    }
}