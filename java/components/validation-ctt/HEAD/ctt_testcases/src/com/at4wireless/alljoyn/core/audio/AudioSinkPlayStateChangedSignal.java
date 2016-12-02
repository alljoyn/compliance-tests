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
 * The Class AudioSinkPlayStateChangedSignal.
 */
public class AudioSinkPlayStateChangedSignal
{
    
    /** The old state. */
    private byte oldState;
    
    /** The new state. */
    private byte newState;

    /**
     * Instantiates a new audio sink play state changed signal.
     *
     * @param oldState the old state
     * @param newState the new state
     */
    public AudioSinkPlayStateChangedSignal(byte oldState, byte newState)
    {
        this.oldState = oldState;
        this.newState = newState;
    }

    /**
     * Gets the old state.
     *
     * @return the old state
     */
    public byte getOldState()
    {
        return oldState;
    }

    /**
     * Gets the new state.
     *
     * @return the new state
     */
    public byte getNewState()
    {
        return newState;
    }
}