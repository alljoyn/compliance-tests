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

import java.util.Arrays;

import org.alljoyn.config.server.SetPasswordHandler;
import org.alljoyn.validation.simulator.DUTSimulator;

import android.util.Log;

public class DUTSimulatorPasswordHandler implements SetPasswordHandler
{
    private static final String TAG = "DUTSimPassHandler";
    DUTSimulator dutSimulator;

    public DUTSimulatorPasswordHandler(DUTSimulator dutSimulator)
    {
        this.dutSimulator = dutSimulator;
    }

    @Override
    public void setPassword(String peerName, char[] password)
    {
        Log.d(TAG, String.format("Setting passcode to %s", Arrays.toString(password)));
        dutSimulator.setSecuredSessionPassword(password);
        dutSimulator.getBusAttachment().clearKeyStore();
    }
}