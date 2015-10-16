/*******************************************************************************
*  Copyright (c) AllSeen Alliance. All rights reserved.
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
package org.alljoyn.validation.testing.suites.controller.alljoyn;

import org.alljoyn.bus.annotation.BusSignalHandler;
import org.alljoyn.validation.testing.suites.controller.ControllerServiceSignalListener;

public class ControllerServicePulseEffectSignalHandler
{
    static
    {
        System.loadLibrary("alljoyn_java");
    }

    private ControllerServiceSignalListener signalListener;

    public void setUpdateListener(ControllerServiceSignalListener listener)
    {
        signalListener = listener;
    }

    @BusSignalHandler(iface = "org.allseen.LSF.ControllerService.PulseEffect", signal = "PulseEffectsNameChanged")
    public void PulseEffectsNameChanged(String[] pulseEffectIDs)
    {
        if (signalListener != null)
        {
            signalListener.handlePulseEffectsNameChanged(pulseEffectIDs);
        }
    }

    @BusSignalHandler(iface = "org.allseen.LSF.ControllerService.PulseEffect", signal = "PulseEffectsCreated")
    public void PulseEffectsCreated(String[] pulseEffectIDs)
    {
        if (signalListener != null)
        {
            signalListener.handlePulseEffectsCreated(pulseEffectIDs);
        }
    }

    @BusSignalHandler(iface = "org.allseen.LSF.ControllerService.PulseEffect", signal = "PulseEffectsUpdated")
    public void PulseEffectsUpdated(String[] pulseEffectIDs)
    {
        if (signalListener != null)
        {
            signalListener.handlePulseEffectsUpdated(pulseEffectIDs);
        }
    }

    @BusSignalHandler(iface = "org.allseen.LSF.ControllerService.PulseEffect", signal = "PulseEffectsDeleted")
    public void PulseEffectsDeleted(String[] pulseEffectIDs)
    {
        if (signalListener != null)
        {
            signalListener.handlePulseEffectsDeleted(pulseEffectIDs);
        }
    }
}
