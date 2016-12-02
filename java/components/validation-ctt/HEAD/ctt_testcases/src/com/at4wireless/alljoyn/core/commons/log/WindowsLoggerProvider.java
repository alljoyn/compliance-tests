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
 * The Class WindowsLoggerProvider.
 */
public class WindowsLoggerProvider implements LoggerProvider
{

    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.commons.log.LoggerProvider#getLogger(java.lang.String)
     */
    @Override
    public WindowsLoggerImpl getLogger(String tag)
    {
        return new WindowsLoggerImpl(tag);
    }
}