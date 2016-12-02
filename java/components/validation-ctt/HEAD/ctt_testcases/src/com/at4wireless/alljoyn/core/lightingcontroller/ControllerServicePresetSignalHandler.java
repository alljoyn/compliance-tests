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
 * The Class ControllerServicePresetSignalHandler.
 */
public class ControllerServicePresetSignalHandler
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
     * Default lamp state changed.
     */
    @BusSignalHandler(iface = "org.allseen.LSF.ControllerService.Preset", signal = "DefaultLampStateChanged")
    public void DefaultLampStateChanged()
    {
        if (signalListener != null)
        {
            signalListener.handleDefaultLampStateChanged();
        }
    }

    /**
     * Presets created.
     *
     * @param presetsIDs the presets i ds
     */
    @BusSignalHandler(iface = "org.allseen.LSF.ControllerService.Preset", signal = "PresetsCreated")
    public void PresetsCreated(String[] presetsIDs)
    {
        if (signalListener != null)
        {
            signalListener.handlePresetsCreated(presetsIDs);
        }
    }

    /**
     * Presets updated.
     *
     * @param presetsIDs the presets i ds
     */
    @BusSignalHandler(iface = "org.allseen.LSF.ControllerService.Preset", signal = "PresetsUpdated")
    public void PresetsUpdated(String[] presetsIDs)
    {
        if (signalListener != null)
        {
            signalListener.handlePresetsUpdated(presetsIDs);
        }
    }

    /**
     * Presets deleted.
     *
     * @param presetsIDs the presets i ds
     */
    @BusSignalHandler(iface = "org.allseen.LSF.ControllerService.Preset", signal = "PresetsDeleted")
    public void PresetsDeleted(String[] presetsIDs)
    {
        if (signalListener != null)
        {
            signalListener.handlePresetsDeleted(presetsIDs);
        }
    }

    /**
     * Presets name changed.
     *
     * @param presetIDs the preset i ds
     */
    @BusSignalHandler(iface = "org.allseen.LSF.ControllerService.Preset", signal = "PresetsNameChanged")
    public void PresetsNameChanged(String[] presetIDs)
    {
        if (signalListener != null)
        {
            signalListener.handlePresetsNameChanged(presetIDs);
        }
    }
}