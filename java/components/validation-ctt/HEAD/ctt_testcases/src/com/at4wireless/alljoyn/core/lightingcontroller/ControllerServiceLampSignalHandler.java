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

import java.util.Map;

import org.alljoyn.bus.Variant;
import org.alljoyn.bus.annotation.BusSignalHandler;

public class ControllerServiceLampSignalHandler
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

    @BusSignalHandler(iface = "org.allseen.LSF.ControllerService.Lamp", signal = "LampNameChanged")
    public void LampNameChanged(String lampID, String lampName)
    {
        System.out.println("LampNameChanged signal");
        if (signalListener != null)
        {
            signalListener.handleLampNameChanged(lampID, lampName);
        }
    }

    @BusSignalHandler(iface = "org.allseen.LSF.ControllerService.Lamp", signal = "LampStateChanged")
    public void LampStateChanged(String lampID, Map<String, Variant> lampState)
    {
        if (signalListener != null)
        {
            signalListener.handleLampStateChanged(lampID, lampState);
        }
    }

    @BusSignalHandler(iface = "org.allseen.LSF.ControllerService.Lamp", signal = "LampsFound")
    public void LampsFound(String[] lampIDs)
    {
        if (signalListener != null)
        {
            signalListener.handleLampsFound(lampIDs);
        }
    }

    @BusSignalHandler(iface = "org.allseen.LSF.ControllerService.Lamp", signal = "LampsLost")
    public void LampsLost(String[] lampIDs)
    {
        if (signalListener != null)
        {
            signalListener.handleLampsLost(lampIDs);
        }
    }
}