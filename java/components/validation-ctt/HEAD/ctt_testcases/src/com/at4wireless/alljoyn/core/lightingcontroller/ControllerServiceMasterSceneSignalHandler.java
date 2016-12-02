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

public class ControllerServiceMasterSceneSignalHandler
{
   /* static
    {
        System.loadLibrary("alljoyn_java");
    }*/

	private ControllerServiceSignalListener signalListener;

    public void setUpdateListener(ControllerServiceSignalListener listener)
    {
        signalListener = listener;
    }

    @BusSignalHandler(iface = "org.allseen.LSF.ControllerService.MasterScene", signal = "MasterScenesNameChanged")
    public void MasterScenesNameChanged(String[] masterSceneIDs)
    {
        if (signalListener != null)
        {
            signalListener.handleMasterScenesNameChanged(masterSceneIDs);
        }
    }

    @BusSignalHandler(iface = "org.allseen.LSF.ControllerService.MasterScene", signal = "MasterScenesCreated")
    public void MasterScenesCreated(String[] masterSceneIDs)
    {
        if (signalListener != null)
        {
            signalListener.handleMasterScenesCreated(masterSceneIDs);
        }
    }

    @BusSignalHandler(iface = "org.allseen.LSF.ControllerService.MasterScene", signal = "MasterScenesUpdated")
    public void MasterScenesUpdated(String[] masterSceneIDs)
    {
        if (signalListener != null)
        {
            signalListener.handleMasterScenesUpdated(masterSceneIDs);
        }
    }

    @BusSignalHandler(iface = "org.allseen.LSF.ControllerService.MasterScene", signal = "MasterScenesDeleted")
    public void MasterScenesDeleted(String[] masterSceneIDs)
    {
        if (signalListener != null)
        {
            signalListener.handleMasterScenesDeleted(masterSceneIDs);
        }
    }

    @BusSignalHandler(iface = "org.allseen.LSF.ControllerService.MasterScene", signal = "MasterScenesApplied")
    public void MasterScenesApplied(String[] masterSceneIDs)
    {
        if (signalListener != null)
        {
            signalListener.handleMasterScenesApplied(masterSceneIDs);
        }
    }
}