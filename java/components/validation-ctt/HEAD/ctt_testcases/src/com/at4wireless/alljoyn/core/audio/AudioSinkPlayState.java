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

// TODO: Auto-generated Javadoc
/**
 * The Enum AudioSinkPlayState.
 */
public enum AudioSinkPlayState
{
    
    /** The Idle. */
    Idle((byte) 0), 
 /** The Playing. */
 Playing((byte) 1), 
 /** The Paused. */
 Paused((byte) 2);

    /** The value. */
    private byte value;

    /**
     * Instantiates a new audio sink play state.
     *
     * @param value the value
     */
    AudioSinkPlayState(byte value)
    {
        this.value = value;
    }

    /**
     * Gets the value.
     *
     * @return the value
     */
    public byte getValue()
    {
        return value;
    }
}