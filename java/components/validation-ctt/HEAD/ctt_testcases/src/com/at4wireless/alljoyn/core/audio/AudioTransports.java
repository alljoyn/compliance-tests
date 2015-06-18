/*
 *    Copyright (c) Open Connectivity Foundation (OCF) and AllJoyn Open
 *    Source Project (AJOSP) Contributors and others.
 *
 *    SPDX-License-Identifier: Apache-2.0
 *
 *    All rights reserved. This program and the accompanying materials are
 *    made available under the terms of the Apache License, Version 2.0
 *    which accompanies this distribution, and is available at
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Copyright (c) Open Connectivity Foundation and Contributors to AllSeen
 *    Alliance. All rights reserved.
 *
 *    Permission to use, copy, modify, and/or distribute this software for
 *    any purpose with or without fee is hereby granted, provided that the
 *    above copyright notice and this permission notice appear in all
 *    copies.
 *
 *    THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL
 *    WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED
 *    WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE
 *    AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL
 *    DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR
 *    PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER
 *    TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 *    PERFORMANCE OF THIS SOFTWARE.
 */
package com.at4wireless.alljoyn.core.audio;

import java.util.Map;

import org.alljoyn.bus.BusException;
import org.alljoyn.bus.Variant;
import org.alljoyn.bus.annotation.BusInterface;
import org.alljoyn.bus.annotation.BusMethod;
import org.alljoyn.bus.annotation.BusProperty;
import org.alljoyn.bus.annotation.BusSignal;
import org.alljoyn.bus.annotation.Position;
import org.alljoyn.bus.annotation.Signature;

// TODO: Auto-generated Javadoc
/**
 * The Class AudioTransports.
 */
public class AudioTransports
{
    
    /**
     * The Interface Stream.
     */
    @BusInterface(name = "org.alljoyn.Stream")
    public interface Stream
    {
        
        /**
         * Gets the version.
         *
         * @return the version
         * @throws BusException the bus exception
         */
        @BusProperty(signature = "q")
        public short getVersion() throws BusException;

        /**
         * Open.
         *
         * @throws BusException the bus exception
         */
        @BusMethod()
        public void Open() throws BusException;

        /**
         * Close.
         *
         * @throws BusException the bus exception
         */
        @BusMethod()
        public void Close() throws BusException;
    }

    /**
     * The Class Configuration.
     */
    public static class Configuration
    {
        
        /** The media type. */
        @Position(0)
        public String mediaType;
        
        /** The parameters. */
        @Position(1)
        public Map<String, Variant> parameters;
    }

    /**
     * The Interface Port.
     */
    @BusInterface(name = "org.alljoyn.Stream.Port")
    public interface Port
    {
        
        /**
         * Gets the version.
         *
         * @return the version
         * @throws BusException the bus exception
         */
        @BusProperty(signature = "q")
        public short getVersion() throws BusException;

        /**
         * Gets the direction.
         *
         * @return the direction
         * @throws BusException the bus exception
         */
        @BusProperty(signature = "y")
        public byte getDirection() throws BusException;

        /**
         * Gets the capabilities.
         *
         * @return the capabilities
         * @throws BusException the bus exception
         */
        @BusProperty(signature = "a(sa{sv})")
        public Configuration[] getCapabilities() throws BusException;

        /**
         * Ownership lost.
         *
         * @param newOwner the new owner
         * @throws BusException the bus exception
         */
        @BusSignal(signature = "s")
        public void OwnershipLost(String newOwner) throws BusException;

        /**
         * Connect.
         *
         * @param host the host
         * @param objectPath the object path
         * @param configuration the configuration
         * @throws BusException the bus exception
         */
        @BusMethod(signature = "so(sa{sv})")
        public void Connect(String host, String objectPath, Configuration configuration) throws BusException;
    }

    /**
     * The Class AudioSinkDelay.
     */
    public static class AudioSinkDelay
    {
        
        /** The position. */
        @Position(0)
        @Signature(value = "u")
        public int position;
        
        /** The size. */
        @Position(1)
        @Signature(value = "u")
        public int size;
    }

    /**
     * The Interface AudioSink.
     */
    @BusInterface(name = "org.alljoyn.Stream.Port.AudioSink")
    public interface AudioSink
    {
        
        /**
         * Gets the version.
         *
         * @return the version
         * @throws BusException the bus exception
         */
        @BusProperty(signature = "q")
        public short getVersion() throws BusException;

        /**
         * Gets the fifo size.
         *
         * @return the fifo size
         * @throws BusException the bus exception
         */
        @BusProperty(signature = "u")
        public int getFifoSize() throws BusException;

        /**
         * Gets the fifo position.
         *
         * @return the fifo position
         * @throws BusException the bus exception
         */
        @BusProperty(signature = "u")
        public int getFifoPosition() throws BusException;

        /**
         * Gets the delay.
         *
         * @return the delay
         * @throws BusException the bus exception
         */
        @BusProperty(signature = "(uu)")
        public AudioSinkDelay getDelay() throws BusException;

        /**
         * Fifo position changed.
         *
         * @throws BusException the bus exception
         */
        @BusSignal()
        public void FifoPositionChanged() throws BusException;

        /**
         * Play state changed.
         *
         * @param oldState the old state
         * @param newState the new state
         * @throws BusException the bus exception
         */
        @BusSignal(signature = "yy")
        public void PlayStateChanged(byte oldState, byte newState) throws BusException;

        /**
         * Play.
         *
         * @throws BusException the bus exception
         */
        @BusMethod()
        public void Play() throws BusException;

        /**
         * Pause.
         *
         * @param timeNanos the time nanos
         * @throws BusException the bus exception
         */
        @BusMethod(signature = "t")
        public void Pause(long timeNanos) throws BusException;

        /**
         * Flush.
         *
         * @param timeNanos the time nanos
         * @return the int
         * @throws BusException the bus exception
         */
        @BusMethod(signature = "t", replySignature = "u")
        public int Flush(long timeNanos) throws BusException;

    }

    /**
     * The Interface AudioSource.
     */
    @BusInterface(name = "org.alljoyn.Stream.Port.AudioSource")
    public interface AudioSource
    {
        
        /**
         * Gets the version.
         *
         * @return the version
         * @throws BusException the bus exception
         */
        @BusProperty(signature = "q")
        public short getVersion() throws BusException;

        /**
         * Data.
         *
         * @param timestamp the timestamp
         * @param data the data
         * @throws BusException the bus exception
         */
        @BusSignal(signature = "tay")
        public void Data(long timestamp, byte[] data) throws BusException;
    }

    /**
     * The Interface ImageSink.
     */
    @BusInterface(name = "org.alljoyn.Stream.Port.ImageSink")
    public interface ImageSink
    {
        
        /**
         * Gets the version.
         *
         * @return the version
         * @throws BusException the bus exception
         */
        @BusProperty(signature = "q")
        public short getVersion() throws BusException;

        /**
         * Data.
         *
         * @param data the data
         * @throws BusException the bus exception
         */
        @BusSignal(signature = "ay")//At4Wireless 2014/11/28
        public void Data(byte[] data) throws BusException;
    }

    /**
     * The Interface ImageSource.
     */
    @BusInterface(name = "org.alljoyn.Stream.Port.ImageSource")
    public interface ImageSource
    {
        
        /**
         * Gets the version.
         *
         * @return the version
         * @throws BusException the bus exception
         */
        @BusProperty(signature = "q")
        public short getVersion() throws BusException;

        /**
         * Data.
         *
         * @param data the data
         * @throws BusException the bus exception
         */
        @BusSignal(signature = "ay")
        public void Data(byte[] data) throws BusException;

    }

    /**
     * The Interface MetadataSink.
     */
    @BusInterface(name = "org.alljoyn.Stream.Port.Application.MetadataSink")
    public interface MetadataSink
    {
        
        /**
         * Gets the version.
         *
         * @return the version
         * @throws BusException the bus exception
         */
        @BusProperty(signature = "q")
        public short getVersion() throws BusException;
    }

    /**
     * The Interface MetadataSource.
     */
    @BusInterface(name = "org.alljoyn.Stream.Port.Application.MetadataSource")
    public interface MetadataSource
    {
        
        /**
         * Gets the version.
         *
         * @return the version
         * @throws BusException the bus exception
         */
        @BusProperty(signature = "q")
        public short getVersion() throws BusException;

        /**
         * Data.
         *
         * @param dictionary the dictionary
         * @throws BusException the bus exception
         */
        @BusSignal(signature = "a{sv}")
        public void Data(Map<String, Variant> dictionary) throws BusException;
    }

    /**
     * The Class VolumeRange.
     */
    public static class VolumeRange
    {
        
        /** The low. */
        @Position(0)
        @Signature(value = "n")
        public short low;
        
        /** The high. */
        @Position(1)
        @Signature(value = "n")
        public short high;
        
        /** The step. */
        @Position(2)
        @Signature(value = "n")
        public short step;
    }

    /**
     * The Interface Volume.
     */
    @BusInterface(name = "org.alljoyn.Control.Volume")
    public interface Volume
    {
        
        /**
         * Gets the version.
         *
         * @return the version
         * @throws BusException the bus exception
         */
        @BusProperty(signature = "q")
        public short getVersion() throws BusException;

        /**
         * Gets the mute.
         *
         * @return the mute
         * @throws BusException the bus exception
         */
        @BusProperty(signature = "b")
        public boolean getMute() throws BusException;

        /**
         * Sets the mute.
         *
         * @param isMuted the new mute
         * @throws BusException the bus exception
         */
        @BusProperty(signature = "b")
        public void setMute(boolean isMuted) throws BusException;

        /**
         * Gets the volume.
         *
         * @return the volume
         * @throws BusException the bus exception
         */
        @BusProperty(signature = "n")
        public short getVolume() throws BusException;

        /**
         * Sets the volume.
         *
         * @param level the new volume
         * @throws BusException the bus exception
         */
        @BusProperty(signature = "n")
        public void setVolume(short level) throws BusException;

        /**
         * Gets the volume range.
         *
         * @return the volume range
         * @throws BusException the bus exception
         */
        @BusProperty(signature = "(nnn)")
        public VolumeRange getVolumeRange() throws BusException;

        /**
         * Gets the enabled.
         *
         * @return the enabled
         * @throws BusException the bus exception
         */
        @BusProperty(signature = "b")
        public boolean getEnabled() throws BusException;

        /**
         * Sets the enabled.
         *
         * @param enabled the new enabled
         * @throws BusException the bus exception
         */
        @BusProperty(signature = "b")
        public void setEnabled(boolean enabled) throws BusException;

        /**
         * Adjust volume.
         *
         * @param delta the delta
         * @throws BusException the bus exception
         */
        @BusMethod(signature = "n")
        public void AdjustVolume(short delta) throws BusException;

        /**
         * Adjust volume percent.
         *
         * @param change the change
         * @throws BusException the bus exception
         */
        @BusMethod(signature = "d")
        public void AdjustVolumePercent(double change) throws BusException;

        /**
         * Mute changed.
         *
         * @param newMute the new mute
         * @throws BusException the bus exception
         */
        @BusSignal(signature = "b")
        public void MuteChanged(boolean newMute) throws BusException;

        /**
         * Volume changed.
         *
         * @param newVolume the new volume
         * @throws BusException the bus exception
         */
        @BusSignal(signature = "n")
        public void VolumeChanged(short newVolume) throws BusException;

        /**
         * Enabled changed.
         *
         * @param enabled the enabled
         * @throws BusException the bus exception
         */
        @BusSignal(signature = "b")
        public void EnabledChanged(boolean enabled) throws BusException;
    }

    /**
     * The Interface Clock.
     */
    @BusInterface(name = "org.alljoyn.Stream.Clock")
    public interface Clock
    {
        
        /**
         * Gets the version.
         *
         * @return the version
         * @throws BusException the bus exception
         */
        @BusProperty(signature = "q")
        public short getVersion() throws BusException;

        /**
         * Sets the time.
         *
         * @param timeNanos the time nanos
         * @throws BusException the bus exception
         */
        @BusMethod(signature = "t")
        public void SetTime(long timeNanos) throws BusException;

        /**
         * Adjust time.
         *
         * @param timeNanos the time nanos
         * @throws BusException the bus exception
         */
        @BusMethod(signature = "x")
        public void AdjustTime(long timeNanos) throws BusException;
    }
}