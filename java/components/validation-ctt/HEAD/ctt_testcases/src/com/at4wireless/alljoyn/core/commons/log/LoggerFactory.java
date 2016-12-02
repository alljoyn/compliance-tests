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
package com.at4wireless.alljoyn.core.commons.log;

// TODO: Auto-generated Javadoc
/**
 * A factory for creating Logger objects.
 */
public class LoggerFactory
{
    
    /** The logger provider. */
    private static WindowsLoggerProvider loggerProvider = new LoggerProviderLoader().loadLoggerProvider();

    /**
     * Gets the logger.
     *
     * @param tag the tag
     * @return the logger
     */
    public static WindowsLoggerImpl getLogger(String tag)
    {
        return loggerProvider.getLogger(tag);
    }
    
    
    
    /**
     * Gets the windows logger logger.
     *
     * @param tag the tag
     * @return the windows logger logger
     */
    public WindowsLoggerImpl getWindowsLoggerLogger(String tag)
    {
        return loggerProvider.getLogger(tag);
    }

}
 