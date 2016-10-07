/*******************************************************************************
 *  Copyright (c) Open Connectivity Foundation (OCF) and AllJoyn Open
 *     Source Project (AJOSP) Contributors and others.
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
 *      THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL
 *      WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED
 *      WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE
 *      AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL
 *      DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR
 *      PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER
 *      TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 *      PERFORMANCE OF THIS SOFTWARE.
 *******************************************************************************/
package com.at4wireless.alljoyn.core.audio.handlers;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import org.alljoyn.bus.BusException;
import org.alljoyn.bus.BusObject;
import org.alljoyn.bus.annotation.BusSignalHandler;

import com.at4wireless.alljoyn.core.audio.AudioSinkPlayStateChangedSignal;
import com.at4wireless.alljoyn.core.audio.AudioTransports.AudioSink;
import com.at4wireless.alljoyn.core.audio.AudioTransports.AudioSinkDelay;
import com.at4wireless.alljoyn.core.commons.log.Logger;
import com.at4wireless.alljoyn.core.commons.log.LoggerFactory;

public class AudioSinkSignalHandler implements AudioSink, BusObject
{
    private static final String TAG = "AudioSinkSignalHandler";
    private static final Logger logger = LoggerFactory.getLogger(TAG);
    private LinkedBlockingDeque<AudioSinkPlayStateChangedSignal> playStateChangedSignalQueue = new LinkedBlockingDeque<AudioSinkPlayStateChangedSignal>();
    private LinkedBlockingDeque<Integer> fifoPositionChangedSignalQueue = new LinkedBlockingDeque<Integer>();

    @BusSignalHandler(iface = "org.alljoyn.Stream.Port.AudioSink", signal = "PlayStateChanged")
    public void handlePlayStateChangedSignal(byte oldState, byte newState)
    {
        logger.debug(String.format("PlayStateChanged signal received. Old state: %d New state: %d", oldState, newState));
        playStateChangedSignalQueue.add(new AudioSinkPlayStateChangedSignal(oldState, newState));
    }

    @BusSignalHandler(iface = "org.alljoyn.Stream.Port.AudioSink", signal = "FifoPositionChanged")
    public void handleFifoPositionChangedSignal()
    {
        logger.debug("FifoPositionChanged signal received");
        fifoPositionChangedSignalQueue.add(0);
    }

    public AudioSinkPlayStateChangedSignal waitForNextPlayStateChangedSignal(long timeout, TimeUnit unit) throws InterruptedException
    {
        logger.debug("Waiting for PlayStateChanged signal");

        return playStateChangedSignalQueue.poll(timeout, unit);
    }

    public Integer waitForNextFifoPositionChangedSignal(long timeout, TimeUnit unit) throws InterruptedException
    {
        logger.debug("Waiting for FifoPositionChanged signal");

        return fifoPositionChangedSignalQueue.poll(timeout, unit);
    }

    public void clearFifoPositionChangedSignalQueue()
    {
        fifoPositionChangedSignalQueue.clear();
    }

    @Override
    public short getVersion() throws BusException
    {
        return 0;
    }

    @Override
    public int getFifoSize() throws BusException
    {
        return 0;
    }

    @Override
    public int getFifoPosition() throws BusException
    {
        return 0;
    }

    @Override
    public AudioSinkDelay getDelay() throws BusException
    {
        return null;
    }

    @Override
    public void FifoPositionChanged() throws BusException
    {
    }

    @Override
    public void PlayStateChanged(byte oldState, byte newState) throws BusException
    {
    }

    @Override
    public void Play() throws BusException
    {
    }

    @Override
    public void Pause(long timeNanos) throws BusException
    {
    }

    @Override
    public int Flush(long timeNanos) throws BusException
    {
        return 0;
    }
}