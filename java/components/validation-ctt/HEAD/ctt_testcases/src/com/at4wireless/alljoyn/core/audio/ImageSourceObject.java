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
package com.at4wireless.alljoyn.core.audio;

import org.alljoyn.bus.BusException;
import org.alljoyn.bus.BusObject;

import com.at4wireless.alljoyn.core.audio.AudioTransports.ImageSink;


// TODO: Auto-generated Javadoc
/**
 * The Class ImageSourceObject.
 */
public class ImageSourceObject implements ImageSink, BusObject
{
    
    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.audio.AudioTransports.ImageSink#getVersion()
     */
    @Override
    public short getVersion() throws BusException
    {
        return 1;
    }

    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.audio.AudioTransports.ImageSink#Data(byte[])
     */
    @Override
    public void Data(byte[] data) throws BusException
    {
    }
}