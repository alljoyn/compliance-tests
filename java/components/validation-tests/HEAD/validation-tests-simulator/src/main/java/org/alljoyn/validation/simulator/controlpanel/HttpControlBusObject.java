/*******************************************************************************
 *   *  
 *     Copyright (c) 2016 Open Connectivity Foundation and AllJoyn Open
 *     Source Project Contributors and others.
 *     
 *     All rights reserved. This program and the accompanying materials are
 *     made available under the terms of the Apache License, Version 2.0
 *     which accompanies this distribution, and is available at
 *     http://www.apache.org/licenses/LICENSE-2.0

 *******************************************************************************/
package org.alljoyn.validation.simulator.controlpanel;

import org.alljoyn.bus.BusException;
import org.alljoyn.bus.BusObject;
import org.alljoyn.ioe.controlpanelservice.communication.interfaces.HTTPControl;

public class HttpControlBusObject implements HTTPControl, BusObject
{
    @Override
    public String GetRootURL() throws BusException
    {
        return "https://www.alljoyn.org/";
    }

    @Override
    public short getVersion() throws BusException
    {
        return VERSION;
    }
}