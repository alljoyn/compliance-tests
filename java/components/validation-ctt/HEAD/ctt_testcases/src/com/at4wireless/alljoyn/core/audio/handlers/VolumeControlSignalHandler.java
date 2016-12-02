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
package com.at4wireless.alljoyn.core.audio.handlers;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import org.alljoyn.bus.BusException;
import org.alljoyn.bus.BusObject;
import org.alljoyn.bus.annotation.BusSignalHandler;

import com.at4wireless.alljoyn.core.audio.AudioTransports.Volume;
import com.at4wireless.alljoyn.core.audio.AudioTransports.VolumeRange;
import com.at4wireless.alljoyn.core.commons.log.Logger;
import com.at4wireless.alljoyn.core.commons.log.LoggerFactory;



// TODO: Auto-generated Javadoc
/**
 * The Class VolumeControlSignalHandler.
 */
public class VolumeControlSignalHandler implements Volume, BusObject
{
    
    /** The Constant TAG. */
    private static final String TAG = "VolControlSignalHandler";
    
    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger(TAG);
    
    /** The mute changed signal queue. */
    private LinkedBlockingDeque<Boolean> muteChangedSignalQueue = new LinkedBlockingDeque<Boolean>();
    
    /** The enabled changed signal queue. */
    private LinkedBlockingDeque<Boolean> enabledChangedSignalQueue = new LinkedBlockingDeque<Boolean>();
    
    /** The volume changed signal queue. */
    private LinkedBlockingDeque<Short> volumeChangedSignalQueue = new LinkedBlockingDeque<Short>();

    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.audio.AudioTransports.Volume#MuteChanged(boolean)
     */
    @Override
    @BusSignalHandler(iface = "org.alljoyn.Control.Volume", signal = "MuteChanged")
    public void MuteChanged(boolean newMute) throws BusException
    {
        logger.debug("MuteChanged signal received: " + newMute);
        muteChangedSignalQueue.add(newMute);
    }

    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.audio.AudioTransports.Volume#VolumeChanged(short)
     */
    @Override
    @BusSignalHandler(iface = "org.alljoyn.Control.Volume", signal = "VolumeChanged")
    public void VolumeChanged(short newVolume) throws BusException
    {
        logger.debug("VolumeChanged signal received: " + newVolume);
        volumeChangedSignalQueue.add(newVolume);
    }

    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.audio.AudioTransports.Volume#EnabledChanged(boolean)
     */
    @Override
    @BusSignalHandler(iface = "org.alljoyn.Control.Volume", signal = "EnabledChanged")
    public void EnabledChanged(boolean newEnabled) throws BusException
    {
        logger.debug("EnabledChanged signal received: " + newEnabled);
        muteChangedSignalQueue.add(newEnabled);
    }

    /**
     * Wait for next enabled changed signal.
     *
     * @param timeout the timeout
     * @param unit the unit
     * @return the boolean
     * @throws InterruptedException the interrupted exception
     */
    public Boolean waitForNextEnabledChangedSignal(long timeout, TimeUnit unit) throws InterruptedException
    {
        logger.debug("Waiting for EnabledChanged signal");

        return enabledChangedSignalQueue.poll(timeout, unit);
    }

    /**
     * Wait for next mute changed signal.
     *
     * @param timeout the timeout
     * @param unit the unit
     * @return the boolean
     * @throws InterruptedException the interrupted exception
     */
    public Boolean waitForNextMuteChangedSignal(long timeout, TimeUnit unit) throws InterruptedException
    {
        logger.debug("Waiting for MuteChanged signal");

        return muteChangedSignalQueue.poll(timeout, unit);
    }

    /**
     * Wait for next volume changed signal.
     *
     * @param timeout the timeout
     * @param unit the unit
     * @return the short
     * @throws InterruptedException the interrupted exception
     */
    public Short waitForNextVolumeChangedSignal(long timeout, TimeUnit unit) throws InterruptedException
    {
        logger.debug("Waiting for VolumeChanged signal");

        return volumeChangedSignalQueue.poll(timeout, unit);
    }

    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.audio.AudioTransports.Volume#getVersion()
     */
    @Override
    public short getVersion() throws BusException
    {
        return 0;
    }

    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.audio.AudioTransports.Volume#getMute()
     */
    @Override
    public boolean getMute() throws BusException
    {
        return false;
    }

    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.audio.AudioTransports.Volume#setMute(boolean)
     */
    @Override
    public void setMute(boolean isMuted) throws BusException
    {
    }

    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.audio.AudioTransports.Volume#getVolume()
     */
    @Override
    public short getVolume() throws BusException
    {
        return 0;
    }

    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.audio.AudioTransports.Volume#setVolume(short)
     */
    @Override
    public void setVolume(short level) throws BusException
    {
    }

    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.audio.AudioTransports.Volume#getVolumeRange()
     */
    @Override
    public VolumeRange getVolumeRange() throws BusException
    {
        return null;
    }

    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.audio.AudioTransports.Volume#getEnabled()
     */
    @Override
    public boolean getEnabled() throws BusException
    {
        return false;
    }

    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.audio.AudioTransports.Volume#setEnabled(boolean)
     */
    @Override
    public void setEnabled(boolean isMuted) throws BusException
    {
    }

    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.audio.AudioTransports.Volume#AdjustVolume(short)
     */
    @Override
    public void AdjustVolume(short delta) throws BusException
    {
    }

    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.audio.AudioTransports.Volume#AdjustVolumePercent(double)
     */
    @Override
    public void AdjustVolumePercent(double change) throws BusException
    {
    }
}
