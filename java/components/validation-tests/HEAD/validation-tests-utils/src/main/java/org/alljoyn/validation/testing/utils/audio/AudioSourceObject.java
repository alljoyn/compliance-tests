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
package org.alljoyn.validation.testing.utils.audio;

import org.alljoyn.bus.BusException;
import org.alljoyn.bus.BusObject;
import org.alljoyn.validation.testing.utils.audio.AudioTransports.AudioSource;

public class AudioSourceObject implements AudioSource, BusObject
{
    @Override
    public short getVersion() throws BusException
    {
        return 1;
    }

    @Override
    public void Data(long timestamp, byte[] data) throws BusException
    {
    }
}