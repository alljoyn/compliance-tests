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
package com.at4wireless.alljoyn.core.lightingcontroller;

import org.alljoyn.bus.annotation.BusSignalHandler;

public class ControllerServiceLampGroupSignalHandler
{
    /*static
    {
        System.loadLibrary("alljoyn_java");
    }*/

    private ControllerServiceSignalListener signalListener;

    public void setUpdateListener(ControllerServiceSignalListener listener)
    {
        signalListener = listener;
    }

    @BusSignalHandler(iface = "org.allseen.LSF.ControllerService.LampGroup", signal = "LampGroupsNameChanged")
    public void LampGroupsNameChanged(String[] lampGroupsIDs)
    {
        if (signalListener != null)
        {
            signalListener.handleLampGroupsNameChanged(lampGroupsIDs);
        }
    }

    @BusSignalHandler(iface = "org.allseen.LSF.ControllerService.LampGroup", signal = "LampGroupsCreated")
    public void LampGroupsCreated(String[] lampGroupsIDs)
    {
        if (signalListener != null)
        {
            signalListener.handleLampGroupsCreated(lampGroupsIDs);
        }
    }

    @BusSignalHandler(iface = "org.allseen.LSF.ControllerService.LampGroup", signal = "LampGroupsUpdated")
    public void LampGroupsUpdated(String[] lampGroupsIDs)
    {
        if (signalListener != null)
        {
            signalListener.handleLampGroupsUpdated(lampGroupsIDs);
        }
    }

    @BusSignalHandler(iface = "org.allseen.LSF.ControllerService.LampGroup", signal = "LampGroupsDeleted")
    public void LampGroupsDeleted(String[] lampGroupsIDs)
    {
        if (signalListener != null)
        {
            signalListener.handleLampGroupsDeleted(lampGroupsIDs);
        }
    }
}