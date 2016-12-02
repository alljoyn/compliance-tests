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

import static org.junit.Assert.assertEquals;

import org.alljoyn.validation.testing.utils.log.LoggerProvider;
import org.alljoyn.validation.testing.utils.log.LoggerProviderLoader;
import org.alljoyn.validation.testing.utils.log.android.AndroidLoggerProvider;
import org.junit.Test;

public class LoggerProviderLoaderTest
{

    @Test
    public void testLoadLoggerProvider() throws InstantiationException, IllegalAccessException, ClassNotFoundException
    {
        LoggerProviderLoader loggerProviderLoader = new LoggerProviderLoader();
        LoggerProvider loggerProvider = loggerProviderLoader.loadLoggerProvider();
        assertEquals(AndroidLoggerProvider.class, loggerProvider.getClass());
    }

    @Test(expected = RuntimeException.class)
    public void testLoadLoggerProviderThrowsException() throws InstantiationException, IllegalAccessException, ClassNotFoundException
    {
        LoggerProviderLoader loggerProviderLoader = new LoggerProviderLoader()
        {
            @Override
            LoggerProvider loadAndroidLoggerProvider() throws InstantiationException, IllegalAccessException, ClassNotFoundException
            {
                throw new ClassNotFoundException();
            }
        };
        LoggerProvider loggerProvider = loggerProviderLoader.loadLoggerProvider();
        assertEquals(AndroidLoggerProvider.class, loggerProvider.getClass());
    }

}