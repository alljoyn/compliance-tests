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

import org.alljoyn.services.android.security.AuthPasswordHandler;
import org.alljoyn.services.android.security.SrpAnonymousKeyListener;
import org.alljoyn.services.android.utils.AndroidLogger;
import org.alljoyn.services.common.utils.GenericLogger;
import org.alljoyn.validation.simulator.DUTSimulator;

public class DUTSimulatorAuthPasswordHandler implements AuthPasswordHandler
{
    private static String TAG = "DUTSimulatorAuthPasswordHandler";

    private GenericLogger logger = new AndroidLogger();
    private DUTSimulator dutSimulator;

    public DUTSimulatorAuthPasswordHandler(DUTSimulator dutSimulator)
    {
        this.dutSimulator = dutSimulator;
    }

    @Override
    public void completed(String mechanism, String authPeer, boolean authenticated)
    {

        if (!authenticated)
            logger.info(TAG, " ** " + authPeer + " failed to authenticate");
        else
            logger.info(TAG, " ** " + authPeer + " successfully authenticated");

    }

    @Override
    public char[] getPassword(String peerName)
    {
        char[] securedSessionPassword = dutSimulator.getSecuredSessionPassword();
        logger.info(TAG, String.format("Providing passcode %s for %s", Arrays.toString(securedSessionPassword), peerName));
        return securedSessionPassword != null ? securedSessionPassword : SrpAnonymousKeyListener.DEFAULT_PINCODE;
    }

}