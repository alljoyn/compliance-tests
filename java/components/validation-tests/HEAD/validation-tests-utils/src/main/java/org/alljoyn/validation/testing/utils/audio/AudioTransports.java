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

import java.util.Map;

import org.alljoyn.bus.BusException;
import org.alljoyn.bus.Variant;
import org.alljoyn.bus.annotation.BusInterface;
import org.alljoyn.bus.annotation.BusMethod;
import org.alljoyn.bus.annotation.BusProperty;
import org.alljoyn.bus.annotation.BusSignal;
import org.alljoyn.bus.annotation.Position;
import org.alljoyn.bus.annotation.Signature;

public class AudioTransports
{
    @BusInterface(name = "org.alljoyn.Stream")
    public interface Stream
    {
        @BusProperty(signature = "q")
        public short getVersion() throws BusException;

        @BusMethod()
        public void Open() throws BusException;

        @BusMethod()
        public void Close() throws BusException;
    }

    public static class Configuration
    {
        @Position(0)
        public String mediaType;
        @Position(1)
        public Map<String, Variant> parameters;
    }

    @BusInterface(name = "org.alljoyn.Stream.Port")
    public interface Port
    {
        @BusProperty(signature = "q")
        public short getVersion() throws BusException;

        @BusProperty(signature = "y")
        public byte getDirection() throws BusException;

        @BusProperty(signature = "a(sa{sv})")
        public Configuration[] getCapabilities() throws BusException;

        @BusSignal(signature = "s")
        public void OwnershipLost(String newOwner) throws BusException;

        @BusMethod(signature = "so(sa{sv})")
        public void Connect(String host, String objectPath, Configuration configuration) throws BusException;
    }

    public static class AudioSinkDelay
    {
        @Position(0)
        @Signature(value = "u")
        public int position;
        @Position(1)
        @Signature(value = "u")
        public int size;
    }

    @BusInterface(name = "org.alljoyn.Stream.Port.AudioSink")
    public interface AudioSink
    {
        @BusProperty(signature = "q")
        public short getVersion() throws BusException;

        @BusProperty(signature = "u")
        public int getFifoSize() throws BusException;

        @BusProperty(signature = "u")
        public int getFifoPosition() throws BusException;

        @BusProperty(signature = "(uu)")
        public AudioSinkDelay getDelay() throws BusException;

        @BusSignal()
        public void FifoPositionChanged() throws BusException;

        @BusSignal(signature = "yy")
        public void PlayStateChanged(byte oldState, byte newState) throws BusException;

        @BusMethod()
        public void Play() throws BusException;

        @BusMethod(signature = "t")
        public void Pause(long timeNanos) throws BusException;

        @BusMethod(signature = "t", replySignature = "u")
        public int Flush(long timeNanos) throws BusException;

    }

    @BusInterface(name = "org.alljoyn.Stream.Port.AudioSource")
    public interface AudioSource
    {
        @BusProperty(signature = "q")
        public short getVersion() throws BusException;

        @BusSignal(signature = "tay")
        public void Data(long timestamp, byte[] data) throws BusException;
    }

    @BusInterface(name = "org.alljoyn.Stream.Port.ImageSink")
    public interface ImageSink
    {
        @BusProperty(signature = "q")
        public short getVersion() throws BusException;
    }

    @BusInterface(name = "org.alljoyn.Stream.Port.ImageSource")
    public interface ImageSource
    {
        @BusProperty(signature = "q")
        public short getVersion() throws BusException;

        @BusSignal(signature = "ay")
        public void Data(byte[] data) throws BusException;

    }

    @BusInterface(name = "org.alljoyn.Stream.Port.Application.MetadataSink")
    public interface MetadataSink
    {
        @BusProperty(signature = "q")
        public short getVersion() throws BusException;
    }

    @BusInterface(name = "org.alljoyn.Stream.Port.Application.MetadataSource")
    public interface MetadataSource
    {
        @BusProperty(signature = "q")
        public short getVersion() throws BusException;

        @BusSignal(signature = "a{sv}")
        public void Data(Map<String, Variant> dictionary) throws BusException;
    }

    public static class VolumeRange
    {
        @Position(0)
        @Signature(value = "n")
        public short low;
        @Position(1)
        @Signature(value = "n")
        public short high;
        @Position(2)
        @Signature(value = "n")
        public short step;
    }

    @BusInterface(name = "org.alljoyn.Control.Volume")
    public interface Volume
    {
        @BusProperty(signature = "q")
        public short getVersion() throws BusException;

        @BusProperty(signature = "b")
        public boolean getMute() throws BusException;

        @BusProperty(signature = "b")
        public void setMute(boolean isMuted) throws BusException;

        @BusProperty(signature = "n")
        public short getVolume() throws BusException;

        @BusProperty(signature = "n")
        public void setVolume(short level) throws BusException;

        @BusProperty(signature = "(nnn)")
        public VolumeRange getVolumeRange() throws BusException;

        @BusProperty(signature = "b")
        public boolean getEnabled() throws BusException;

        @BusProperty(signature = "b")
        public void setEnabled(boolean enabled) throws BusException;

        @BusMethod(signature = "n")
        public void AdjustVolume(short delta) throws BusException;

        @BusMethod(signature = "d")
        public void AdjustVolumePercent(double change) throws BusException;

        @BusSignal(signature = "b")
        public void MuteChanged(boolean newMute) throws BusException;

        @BusSignal(signature = "n")
        public void VolumeChanged(short newVolume) throws BusException;

        @BusSignal(signature = "b")
        public void EnabledChanged(boolean enabled) throws BusException;
    }

    @BusInterface(name = "org.alljoyn.Stream.Clock")
    public interface Clock
    {
        @BusProperty(signature = "q")
        public short getVersion() throws BusException;

        @BusMethod(signature = "t")
        public void SetTime(long timeNanos) throws BusException;

        @BusMethod(signature = "x")
        public void AdjustTime(long timeNanos) throws BusException;
    }
}