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

import org.alljoyn.bus.BusException;
import org.alljoyn.bus.BusObject;
import org.alljoyn.bus.annotation.BusMethod;
import org.alljoyn.bus.annotation.BusProperty;
import org.alljoyn.bus.annotation.BusSignal;

// TODO: Auto-generated Javadoc
/**
 * The Class ControllerServiceBusObject.
 */
public class ControllerServiceBusObject implements BusObject, ControllerServiceBusInterface
{
    
    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceBusInterface#getVersion()
     */
    @Override
    @BusProperty(signature="u")
    public int getVersion() throws BusException
    {
        return 0;
    }

    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceBusInterface#LightingResetControllerService()
     */
    @Override
    @BusMethod(replySignature = "u")
    public int LightingResetControllerService() throws BusException
    {
        return 0;
    }

    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceBusInterface#GetControllerServiceVersion()
     */
    @Override
    @BusMethod(replySignature = "u")
    public int GetControllerServiceVersion() throws BusException
    {
        return 0;
    }

    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceBusInterface#ControllerServiceLightingReset()
     */
    @Override
    @BusSignal
    public void ControllerServiceLightingReset() throws BusException
    {
    }
}