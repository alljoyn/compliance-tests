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
 * The Enum AudioServiceInterfaceName.
 */
public enum AudioServiceInterfaceName
{
    
    /** The Stream. */
    Stream("org.alljoyn.Stream"), 
 /** The Port. */
 Port("org.alljoyn.Stream.Port"), 
 /** The Audio sink. */
 AudioSink("org.alljoyn.Stream.Port.AudioSink"), 
 /** The Image sink. */
 ImageSink("org.alljoyn.Stream.Port.ImageSink"), 
 /** The Application metadata sink. */
 ApplicationMetadataSink(
            "org.alljoyn.Stream.Port.Application.MetadataSink");

    /** The value. */
    private String value;

    /**
     * Instantiates a new audio service interface name.
     *
     * @param value the value
     */
    AudioServiceInterfaceName(String value)
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