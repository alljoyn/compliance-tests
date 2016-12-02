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
 * The Enum MediaType.
 */
public enum MediaType
{
    
    /** The Audio prefix. */
    AudioPrefix("audio/"), 
 /** The Image prefix. */
 ImagePrefix("image/"), 
 /** The Audio x alac. */
 AudioXAlac("audio/x-alac"), 
 /** The Audio x raw. */
 AudioXRaw("audio/x-raw"), 
 /** The Audio x unknown. */
 AudioXUnknown("audio/x-unknown"), 
 /** The Application x metadata. */
 ApplicationXMetadata(
            "application/x-metadata"),
/** The Image jpeg. */
ImageJpeg("image/jpeg");

    /** The value. */
    private String value;

    /**
     * Instantiates a new media type.
     *
     * @param value the value
     */
    MediaType(String value)
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