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
package com.at4wireless.alljoyn.core.audio;

import java.util.Map;

import org.alljoyn.bus.BusException;
import org.alljoyn.bus.BusObject;
import org.alljoyn.bus.Variant;

import com.at4wireless.alljoyn.core.audio.AudioTransports.MetadataSource;

public class MetadataSourceObject implements MetadataSource, BusObject
{
    @Override
    public short getVersion() throws BusException
    {
        return 1;
    }

    @Override
    public void Data(Map<String, Variant> dictionary) throws BusException
    {
    }
}