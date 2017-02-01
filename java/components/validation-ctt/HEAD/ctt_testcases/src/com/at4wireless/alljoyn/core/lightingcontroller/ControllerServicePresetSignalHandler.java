/*
 *    Copyright (c) Open Connectivity Foundation (OCF), AllJoyn Open Source
 *    Project (AJOSP) Contributors and others.
 *    
 *    SPDX-License-Identifier: Apache-2.0
 *    
 *    All rights reserved. This program and the accompanying materials are
 *    made available under the terms of the Apache License, Version 2.0
 *    which accompanies this distribution, and is available at
 *    http://www.apache.org/licenses/LICENSE-2.0
 *    
 *    Copyright (c) Open Connectivity Foundation and Contributors to AllSeen
 *    Alliance. All rights reserved.
 *    
 *    Permission to use, copy, modify, and/or distribute this software for
 *    any purpose with or without fee is hereby granted, provided that the
 *    above copyright notice and this permission notice appear in all
 *    copies.
 *    
 *    THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL
 *    WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED
 *    WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE
 *    AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL
 *    DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR
 *    PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER
 *    TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 *    PERFORMANCE OF THIS SOFTWARE.
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