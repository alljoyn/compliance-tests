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
 * The Class ControllerServiceSceneSignalHandler.
 */
public class ControllerServiceSceneSignalHandler
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
     * Scenes name changed.
     *
     * @param sceneIDs the scene i ds
     */
    @BusSignalHandler(iface = "org.allseen.LSF.ControllerService.Scene", signal = "ScenesNameChanged")
    public void ScenesNameChanged(String[] sceneIDs)
    {
        if (signalListener != null)
        {
            signalListener.handleScenesNameChanged(sceneIDs);
        }
    }

    /**
     * Scenes created.
     *
     * @param sceneIDs the scene i ds
     */
    @BusSignalHandler(iface = "org.allseen.LSF.ControllerService.Scene", signal = "ScenesCreated")
    public void ScenesCreated(String[] sceneIDs)
    {
        if (signalListener != null)
        {
            signalListener.handleScenesCreated(sceneIDs);
        }
    }

    /**
     * Scenes updated.
     *
     * @param sceneIDs the scene i ds
     */
    @BusSignalHandler(iface = "org.allseen.LSF.ControllerService.Scene", signal = "ScenesUpdated")
    public void ScenesUpdated(String[] sceneIDs)
    {
        if (signalListener != null)
        {
            signalListener.handleScenesUpdated(sceneIDs);
        }
    }

    /**
     * Scenes deleted.
     *
     * @param sceneIDs the scene i ds
     */
    @BusSignalHandler(iface = "org.allseen.LSF.ControllerService.Scene", signal = "ScenesDeleted")
    public void ScenesDeleted(String[] sceneIDs)
    {
        if (signalListener != null)
        {
            signalListener.handleScenesDeleted(sceneIDs);
        }
    }

    /**
     * Scenes applied.
     *
     * @param sceneIDs the scene i ds
     */
    @BusSignalHandler(iface = "org.allseen.LSF.ControllerService.Scene", signal = "ScenesApplied")
    public void ScenesApplied(String[] sceneIDs)
    {
        if (signalListener != null)
        {
            signalListener.handleScenesApplied(sceneIDs);
        }
    }
}