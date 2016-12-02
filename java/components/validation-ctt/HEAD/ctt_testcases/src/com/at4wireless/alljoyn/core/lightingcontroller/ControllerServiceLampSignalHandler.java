
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

import java.util.Map;

import org.alljoyn.bus.Variant;
import org.alljoyn.bus.annotation.BusSignalHandler;

// TODO: Auto-generated Javadoc
/**
 * The Class ControllerServiceLampSignalHandler.
 */
public class ControllerServiceLampSignalHandler
{
    static
    {
        System.loadLibrary("alljoyn_java");
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
     * Lamp name changed.
     *
     * @param lampID the lamp id
     * @param lampName the lamp name
     */
    @BusSignalHandler(iface = "org.allseen.LSF.ControllerService.Lamp", signal = "LampNameChanged")
    public void LampNameChanged(String lampID, String lampName)
    {
        System.out.println("LampNameChanged signal");
        if (signalListener != null)
        {
            signalListener.handleLampNameChanged(lampID, lampName);
        }
    }

    /**
     * Lamp state changed.
     *
     * @param lampID the lamp id
     * @param lampState the lamp state
     */
    @BusSignalHandler(iface = "org.allseen.LSF.ControllerService.Lamp", signal = "LampStateChanged")
    public void LampStateChanged(String lampID, Map<String, Variant> lampState)
    {
        if (signalListener != null)
        {
            signalListener.handleLampStateChanged(lampID, lampState);
        }
    }

    /**
     * Lamps found.
     *
     * @param lampIDs the lamp i ds
     */
    @BusSignalHandler(iface = "org.allseen.LSF.ControllerService.Lamp", signal = "LampsFound")
    public void LampsFound(String[] lampIDs)
    {
        if (signalListener != null)
        {
            signalListener.handleLampsFound(lampIDs);
        }
    }

    /**
     * Lamps lost.
     *
     * @param lampIDs the lamp i ds
     */
    @BusSignalHandler(iface = "org.allseen.LSF.ControllerService.Lamp", signal = "LampsLost")
    public void LampsLost(String[] lampIDs)
    {
        if (signalListener != null)
        {
            signalListener.handleLampsLost(lampIDs);
        }
    }
}