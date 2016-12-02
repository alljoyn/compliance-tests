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
package com.at4wireless.alljoyn.core.audio.handlers;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import org.alljoyn.bus.BusException;
import org.alljoyn.bus.BusObject;
import org.alljoyn.bus.annotation.BusSignalHandler;

import com.at4wireless.alljoyn.core.audio.AudioTransports.Volume;
import com.at4wireless.alljoyn.core.audio.AudioTransports.VolumeRange;
import com.at4wireless.alljoyn.core.commons.log.Logger;
import com.at4wireless.alljoyn.core.commons.log.WindowsLoggerImpl;

public class VolumeControlSignalHandler implements Volume, BusObject
{
	private static final Logger logger = new WindowsLoggerImpl("VolControlSignalHandler");
    private LinkedBlockingDeque<Boolean> muteChangedSignalQueue = new LinkedBlockingDeque<Boolean>();
    private LinkedBlockingDeque<Boolean> enabledChangedSignalQueue = new LinkedBlockingDeque<Boolean>();
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

    @Override
    @BusSignalHandler(iface = "org.alljoyn.Control.Volume", signal = "EnabledChanged")
    public void EnabledChanged(boolean newEnabled) throws BusException
    {
        logger.debug("EnabledChanged signal received: " + newEnabled);
        muteChangedSignalQueue.add(newEnabled);
    }

    public Boolean waitForNextEnabledChangedSignal(long timeout, TimeUnit unit) throws InterruptedException
    {
        logger.debug("Waiting for EnabledChanged signal");

        return enabledChangedSignalQueue.poll(timeout, unit);
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
    public boolean getEnabled() throws BusException
    {
        return false;
    }

    @Override
    public void setEnabled(boolean isMuted) throws BusException
    {
    }

    @Override
    public void AdjustVolume(short delta) throws BusException
    {
    }

    @Override
    public void AdjustVolumePercent(double change) throws BusException
    {
    }
}
