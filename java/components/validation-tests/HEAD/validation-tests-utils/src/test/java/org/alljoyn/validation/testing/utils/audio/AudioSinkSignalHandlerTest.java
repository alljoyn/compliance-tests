/*******************************************************************************
 *   *     Copyright (c) Open Connectivity Foundation (OCF) and AllJoyn Open
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
package org.alljoyn.validation.testing.utils.audio;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.concurrent.TimeUnit;

import org.alljoyn.validation.testing.utils.MyRobolectricTestRunner;
import org.alljoyn.validation.testing.utils.audio.handlers.AudioSinkSignalHandler;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

@RunWith(MyRobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class AudioSinkSignalHandlerTest
{
    private AudioSinkSignalHandler audioSinkSignalHandler;

    @Before
    public void setup()
    {
        audioSinkSignalHandler = new AudioSinkSignalHandler();
    }

    @Test
    public void handlePlayStateChangedSignalTest() throws InterruptedException
    {
        assertNull(audioSinkSignalHandler.waitForNextPlayStateChangedSignal(1, TimeUnit.MILLISECONDS));
        audioSinkSignalHandler.handlePlayStateChangedSignal((byte) 0, (byte) 1);
        AudioSinkPlayStateChangedSignal audioSinkPlayStateChangedSignal = audioSinkSignalHandler.waitForNextPlayStateChangedSignal(1, TimeUnit.SECONDS);
        assertEquals((byte) 0, audioSinkPlayStateChangedSignal.getOldState());
        assertEquals((byte) 1, audioSinkPlayStateChangedSignal.getNewState());
    }

    @Test
    public void handleFifoPositionChangedSignalTest() throws InterruptedException
    {
        assertNull(audioSinkSignalHandler.waitForNextFifoPositionChangedSignal(1, TimeUnit.MILLISECONDS));
        audioSinkSignalHandler.handleFifoPositionChangedSignal();
        assertNotNull(audioSinkSignalHandler.waitForNextFifoPositionChangedSignal(1, TimeUnit.SECONDS));
    }

    @Test
    public void clearFifoPositionChangedSignalQueueTest() throws InterruptedException
    {
        audioSinkSignalHandler.handleFifoPositionChangedSignal();
        audioSinkSignalHandler.clearFifoPositionChangedSignalQueue();
        assertNull(audioSinkSignalHandler.waitForNextFifoPositionChangedSignal(1, TimeUnit.MILLISECONDS));
    }
}