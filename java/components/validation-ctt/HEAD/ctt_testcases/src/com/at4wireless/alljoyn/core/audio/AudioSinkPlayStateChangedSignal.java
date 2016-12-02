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

public class AudioSinkPlayStateChangedSignal
{
    private byte oldState;
    private byte newState;

    public AudioSinkPlayStateChangedSignal(byte oldState, byte newState)
    {
        this.oldState = oldState;
        this.newState = newState;
    }

    public byte getOldState()
    {
        return oldState;
    }

    public byte getNewState()
    {
        return newState;
    }
}