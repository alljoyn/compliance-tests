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



/**
 * The Class LoggerProviderLoader.
 */
public class LoggerProviderLoader
{
    
    /**
     * Load logger provider.
     *
     * @return the windows logger provider
     */
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

  


/**
 * Load windows logger provider.
 *
 * @return the windows logger provider
 * @throws InstantiationException the instantiation exception
 * @throws IllegalAccessException the illegal access exception
 * @throws ClassNotFoundException the class not found exception
 */
public WindowsLoggerProvider loadWindowsLoggerProvider() throws InstantiationException, IllegalAccessException, ClassNotFoundException
    {
        return  new WindowsLoggerProvider();
        								
    }

}