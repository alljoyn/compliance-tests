/*******************************************************************************
 *  Copyright AllSeen Alliance. All rights reserved.
 *
 *     Permission to use, copy, modify, and/or distribute this software for any
 *     purpose with or without fee is hereby granted, provided that the above
 *     copyright notice and this permission notice appear in all copies.
 *
 *     THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 *     WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 *     MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 *     ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 *     WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 *     ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 *     OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 *******************************************************************************/
package com.at4wireless.alljoyn.core.lightingcontroller;

import org.alljoyn.bus.annotation.BusSignalHandler;

// TODO: Auto-generated Javadoc
/**
 * The Class ControllerServiceSceneSignalHandler.
 */
public class ControllerServiceSceneSignalHandler
{
   /* static
    {
        System.loadLibrary("alljoyn_java");
    }*/

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