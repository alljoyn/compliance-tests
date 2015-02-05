/*******************************************************************************
*  Copyright (c) 2013 - 2014, AllSeen Alliance. All rights reserved.
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

public class ControllerServiceSignalHandler
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

    @BusSignalHandler(iface = "org.allseen.LSF.ControllerService", signal = "ControllerServiceLightingReset")
    public void handleControllerServiceLightingReset()
    {
        if (signalListener != null)
        {
            signalListener.handleLightingReset();
        }
    }
}
