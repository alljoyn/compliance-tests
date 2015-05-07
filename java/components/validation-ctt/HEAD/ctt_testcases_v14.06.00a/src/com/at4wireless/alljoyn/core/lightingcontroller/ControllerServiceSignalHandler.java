package com.at4wireless.alljoyn.core.lightingcontroller;

import org.alljoyn.bus.annotation.BusSignalHandler;

public class ControllerServiceSignalHandler
{
    static
    {
      //  System.loadLibrary("alljoyn_java");
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
 