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
package org.alljoyn.validation.simulator.config;

import org.alljoyn.config.server.RestartHandler;
import org.alljoyn.validation.simulator.DUTSimulator;

import android.util.Log;

public class DUTSimulatorRestartHandler implements RestartHandler
{
    DUTSimulator dutSimulator;
    protected static final String TAG = "DUTSimulatorRestartHandler";

    public DUTSimulatorRestartHandler(DUTSimulator dutSimulator)
    {
        this.dutSimulator = dutSimulator;
    }

    @Override
    public void restart() 
    {
        Log.d(TAG, "Stopping DUTSimulator");
        dutSimulator.stop();
        Log.d(TAG, "Stopped DUTSimulator");
        dutSimulator.start();
        Log.d(TAG, "Start DUTSimulator");
    }

}