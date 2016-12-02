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

public enum AudioSinkPlayState
{
    Idle((byte) 0), Playing((byte) 1), Paused((byte) 2);

    private byte value;

    AudioSinkPlayState(byte value)
    {
        this.value = value;
    }

    public byte getValue()
    {
        return value;
    }
}