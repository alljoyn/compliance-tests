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
package org.alljoyn.validation.testing.utils.log.android;

import org.alljoyn.validation.testing.utils.log.Logger;
import org.alljoyn.validation.testing.utils.log.android.AndroidLoggerProvider;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.util.Log;

@RunWith(PowerMockRunner.class)
@PrepareForTest(
{ android.util.Log.class })
public class AndroidLoggerProviderTest
{
    private static final String MSG = "message";
    private static final String TAG = "tag";

    @Test
    public void testGetLogger()
    {
        PowerMockito.mockStatic(Log.class);

        AndroidLoggerProvider loggerProvider = new AndroidLoggerProvider();
        Logger logger = loggerProvider.getLogger(TAG);

        logger.error(MSG);
        PowerMockito.verifyStatic();
        Log.e(TAG, MSG);

    }
}