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

import java.util.HashMap;
import java.util.Map;

import org.alljoyn.bus.BusException;
import org.alljoyn.bus.BusObject;
import org.alljoyn.bus.Variant;
import org.alljoyn.ioe.controlpanelservice.communication.interfaces.ActionControlSecured;
import org.alljoyn.ioe.controlpanelservice.ui.ActionWidgetEnum;
import org.alljoyn.ioe.controlpanelservice.ui.ActionWidgetHintsType;

public class ActionSecuredBusObject implements ActionControlSecured, BusObject
{
    private static final int STATE = 1;
    private static final int BACKGROUND_COLOR = 13434879;
    private static final String LABEL = "action label";

    @Override
    public void Exec() throws BusException
    {
    }

    @Override
    public void MetadataChanged() throws BusException
    {
    }

    @Override
    public Map<Short, Variant> getOptParams() throws BusException
    {
        Map<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put(ActionWidgetEnum.LABEL.ID, new Variant(LABEL));
        parameters.put(ActionWidgetEnum.BG_COLOR.ID, new Variant(BACKGROUND_COLOR, "u"));
        short[] shortArray = new short[]
        { ActionWidgetHintsType.ACTION_BUTTON.ID };
        parameters.put(ActionWidgetEnum.HINTS.ID, new Variant(shortArray, "aq"));

        return parameters;
    }

    @Override
    public int getStates() throws BusException
    {
        return STATE;
    }

    @Override
    public short getVersion() throws BusException
    {
        return VERSION;
    }
}