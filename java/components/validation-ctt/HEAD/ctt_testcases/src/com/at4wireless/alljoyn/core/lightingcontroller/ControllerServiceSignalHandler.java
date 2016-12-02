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
package com.at4wireless.alljoyn.core.lightingcontroller;

import org.alljoyn.bus.annotation.BusSignalHandler;

// TODO: Auto-generated Javadoc
/**
 * The Class ControllerServiceSignalHandler.
 */
public class ControllerServiceSignalHandler
{
    static
    {
      //  System.loadLibrary("alljoyn_java");
    }

    /** The signal listener. */
    private ControllerServiceSignalListener signalListener;

    /**
     * Sets the update listener.
     *
     * @param listener the new update listener
     */
    public void setUpdateListener(ControllerServiceSignalListener listener)
    {
        signalListener = listener;
    }

    /**
     * Handle controller service lighting reset.
     */
    @BusSignalHandler(iface = "org.allseen.LSF.ControllerService", signal = "ControllerServiceLightingReset")
    public void handleControllerServiceLightingReset()
    {
        if (signalListener != null)
        {
            signalListener.handleLightingReset();
        }
    }
}	
 