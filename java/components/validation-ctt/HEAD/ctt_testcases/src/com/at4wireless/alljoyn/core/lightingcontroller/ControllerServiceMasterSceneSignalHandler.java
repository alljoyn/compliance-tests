/*
 * Copyright AllSeen Alliance. All rights reserved.
 *
 *    Permission to use, copy, modify, and/or distribute this software for any
 *    purpose with or without fee is hereby granted, provided that the above
 *    copyright notice and this permission notice appear in all copies.
 *
 *    THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 *    WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 *    MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 *    ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 *    WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 *    ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 *    OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */
package com.at4wireless.alljoyn.core.lightingcontroller;

import org.alljoyn.bus.annotation.BusSignalHandler;

// TODO: Auto-generated Javadoc
/**
 * The Class ControllerServiceMasterSceneSignalHandler.
 */
public class ControllerServiceMasterSceneSignalHandler
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
     * Master scenes name changed.
     *
     * @param masterSceneIDs the master scene i ds
     */
    @BusSignalHandler(iface = "org.allseen.LSF.ControllerService.MasterScene", signal = "MasterScenesNameChanged")
    public void MasterScenesNameChanged(String[] masterSceneIDs)
    {
        if (signalListener != null)
        {
            signalListener.handleMasterScenesNameChanged(masterSceneIDs);
        }
    }

    /**
     * Master scenes created.
     *
     * @param masterSceneIDs the master scene i ds
     */
    @BusSignalHandler(iface = "org.allseen.LSF.ControllerService.MasterScene", signal = "MasterScenesCreated")
    public void MasterScenesCreated(String[] masterSceneIDs)
    {
        if (signalListener != null)
        {
            signalListener.handleMasterScenesCreated(masterSceneIDs);
        }
    }

    /**
     * Master scenes updated.
     *
     * @param masterSceneIDs the master scene i ds
     */
    @BusSignalHandler(iface = "org.allseen.LSF.ControllerService.MasterScene", signal = "MasterScenesUpdated")
    public void MasterScenesUpdated(String[] masterSceneIDs)
    {
        if (signalListener != null)
        {
            signalListener.handleMasterScenesUpdated(masterSceneIDs);
        }
    }

    /**
     * Master scenes deleted.
     *
     * @param masterSceneIDs the master scene i ds
     */
    @BusSignalHandler(iface = "org.allseen.LSF.ControllerService.MasterScene", signal = "MasterScenesDeleted")
    public void MasterScenesDeleted(String[] masterSceneIDs)
    {
        if (signalListener != null)
        {
            signalListener.handleMasterScenesDeleted(masterSceneIDs);
        }
    }

    /**
     * Master scenes applied.
     *
     * @param masterSceneIDs the master scene i ds
     */
    @BusSignalHandler(iface = "org.allseen.LSF.ControllerService.MasterScene", signal = "MasterScenesApplied")
    public void MasterScenesApplied(String[] masterSceneIDs)
    {
        if (signalListener != null)
        {
            signalListener.handleMasterScenesApplied(masterSceneIDs);
        }
    }
}
