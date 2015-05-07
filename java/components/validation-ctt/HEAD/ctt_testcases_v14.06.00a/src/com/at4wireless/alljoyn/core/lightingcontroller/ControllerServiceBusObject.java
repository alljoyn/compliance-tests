package com.at4wireless.alljoyn.core.lightingcontroller;

import org.alljoyn.bus.BusException;
import org.alljoyn.bus.BusObject;
import org.alljoyn.bus.annotation.BusMethod;
import org.alljoyn.bus.annotation.BusProperty;
import org.alljoyn.bus.annotation.BusSignal;

public class ControllerServiceBusObject implements BusObject, ControllerServiceBusInterface
{
    @Override
    @BusProperty(signature="u")
    public int getVersion() throws BusException
    {
        return 0;
    }

    @Override
    @BusMethod(replySignature = "u")
    public int LightingResetControllerService() throws BusException
    {
        return 0;
    }

    @Override
    @BusMethod(replySignature = "u")
    public int GetControllerServiceVersion() throws BusException
    {
        return 0;
    }

    @Override
    @BusSignal
    public void ControllerServiceLightingReset() throws BusException
    {
    }
}