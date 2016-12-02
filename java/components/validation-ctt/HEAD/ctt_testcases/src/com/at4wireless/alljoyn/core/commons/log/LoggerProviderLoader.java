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
package com.at4wireless.alljoyn.core.commons.log;

public class LoggerProviderLoader
{
    public WindowsLoggerProvider loadLoggerProvider()
    {
        try
        {
            return loadWindowsLoggerProvider();
        }
        catch (Exception e)
        {
            throw new RuntimeException("Unable to load LoggerProvider", e);
        }
    }

public WindowsLoggerProvider loadWindowsLoggerProvider() throws InstantiationException, IllegalAccessException, ClassNotFoundException
    {
        return  new WindowsLoggerProvider();
        								
    }

}