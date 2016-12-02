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
package org.alljoyn.validation.testing.utils.log;

public class LoggerProviderLoader
{
    public LoggerProvider loadLoggerProvider()
    {
        try
        {
            return loadAndroidLoggerProvider();
        }
        catch (Exception e)
        {
            throw new RuntimeException("Unable to load LoggerProvider", e);
        }
    }

    LoggerProvider loadAndroidLoggerProvider() throws InstantiationException, IllegalAccessException, ClassNotFoundException
    {
        return (LoggerProvider) Class.forName("org.alljoyn.validation.testing.utils.log.android.AndroidLoggerProvider").newInstance();
    }

}