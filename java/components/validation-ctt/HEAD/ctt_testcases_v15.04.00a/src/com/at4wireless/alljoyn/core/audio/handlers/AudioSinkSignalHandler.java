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


// TODO: Auto-generated Javadoc
/**
 * The Class AudioSinkSignalHandler.
 */
public class AudioSinkSignalHandler implements AudioSink, BusObject
{
    
    /** The Constant TAG. */
    private static final String TAG = "AudioSinkSignalHandler";
    
    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger(TAG);
    
    /** The play state changed signal queue. */
    private LinkedBlockingDeque<AudioSinkPlayStateChangedSignal> playStateChangedSignalQueue = new LinkedBlockingDeque<AudioSinkPlayStateChangedSignal>();
    
    /** The fifo position changed signal queue. */
    private LinkedBlockingDeque<Integer> fifoPositionChangedSignalQueue = new LinkedBlockingDeque<Integer>();

    /**
     * Handle play state changed signal.
     *
     * @param oldState the old state
     * @param newState the new state
     */
    @BusSignalHandler(iface = "org.alljoyn.Stream.Port.AudioSink", signal = "PlayStateChanged")
    public void handlePlayStateChangedSignal(byte oldState, byte newState)
    {
        logger.debug(String.format("PlayStateChanged signal received. Old state: %d New state: %d", oldState, newState));
        playStateChangedSignalQueue.add(new AudioSinkPlayStateChangedSignal(oldState, newState));
    }

    /**
     * Handle fifo position changed signal.
     */
    @BusSignalHandler(iface = "org.alljoyn.Stream.Port.AudioSink", signal = "FifoPositionChanged")
    public void handleFifoPositionChangedSignal()
    {
        logger.debug("FifoPositionChanged signal received");
        fifoPositionChangedSignalQueue.add(0);
    }

    /**
     * Wait for next play state changed signal.
     *
     * @param timeout the timeout
     * @param unit the unit
     * @return the audio sink play state changed signal
     * @throws InterruptedException the interrupted exception
     */
    public AudioSinkPlayStateChangedSignal waitForNextPlayStateChangedSignal(long timeout, TimeUnit unit) throws InterruptedException
    {
        logger.debug("Waiting for PlayStateChanged signal");

        return playStateChangedSignalQueue.poll(timeout, unit);
    }

    /**
     * Wait for next fifo position changed signal.
     *
     * @param timeout the timeout
     * @param unit the unit
     * @return the integer
     * @throws InterruptedException the interrupted exception
     */
    public Integer waitForNextFifoPositionChangedSignal(long timeout, TimeUnit unit) throws InterruptedException
    {
        logger.debug("Waiting for FifoPositionChanged signal");

        return fifoPositionChangedSignalQueue.poll(timeout, unit);
    }

    /**
     * Clear fifo position changed signal queue.
     */
    public void clearFifoPositionChangedSignalQueue()
    {
        fifoPositionChangedSignalQueue.clear();
    }

    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.audio.AudioTransports.AudioSink#getVersion()
     */
    @Override
    public short getVersion() throws BusException
    {
        return 0;
    }

    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.audio.AudioTransports.AudioSink#getFifoSize()
     */
    @Override
    public int getFifoSize() throws BusException
    {
        return 0;
    }

    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.audio.AudioTransports.AudioSink#getFifoPosition()
     */
    @Override
    public int getFifoPosition() throws BusException
    {
        return 0;
    }

    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.audio.AudioTransports.AudioSink#getDelay()
     */
    @Override
    public AudioSinkDelay getDelay() throws BusException
    {
        return null;
    }

    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.audio.AudioTransports.AudioSink#FifoPositionChanged()
     */
    @Override
    public void FifoPositionChanged() throws BusException
    {
    }

    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.audio.AudioTransports.AudioSink#PlayStateChanged(byte, byte)
     */
    @Override
    public void PlayStateChanged(byte oldState, byte newState) throws BusException
    {
    }

    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.audio.AudioTransports.AudioSink#Play()
     */
    @Override
    public void Play() throws BusException
    {
    }

    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.audio.AudioTransports.AudioSink#Pause(long)
     */
    @Override
    public void Pause(long timeNanos) throws BusException
    {
    }

    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.audio.AudioTransports.AudioSink#Flush(long)
     */
    @Override
    public int Flush(long timeNanos) throws BusException
    {
        return 0;
    }
}