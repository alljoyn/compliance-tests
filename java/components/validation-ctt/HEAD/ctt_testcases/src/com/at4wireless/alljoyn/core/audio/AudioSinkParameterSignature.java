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
 * The Enum AudioSinkParameterSignature.
 */
public enum AudioSinkParameterSignature
{
    
    /** The Channels. */
    Channels("ay"), 
 /** The Format. */
 Format("as"), 
 /** The Rate. */
 Rate("aq"), ;

    /** The value. */
    private String value;

    /**
     * Instantiates a new audio sink parameter signature.
     *
     * @param value the value
     */
    AudioSinkParameterSignature(String value)
    {
        this.value = value;
    }

    /**
     * Gets the value.
     *
     * @return the value
     */
    public String getValue()
    {
        return value;
    }
}