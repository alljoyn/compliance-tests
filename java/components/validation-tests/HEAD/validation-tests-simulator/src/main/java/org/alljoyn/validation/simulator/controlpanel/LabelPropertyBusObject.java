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
import org.alljoyn.ioe.controlpanelservice.communication.interfaces.Label;
import org.alljoyn.ioe.controlpanelservice.ui.LabelWidgetEnum;
import org.alljoyn.ioe.controlpanelservice.ui.LabelWidgetHintsType;

public class LabelPropertyBusObject implements Label, BusObject
{
    private static final int STATE = 0;
    private static final int BACKGROUND_COLOR = 13434879;
    private static final String LABEL_PROPERTY_VALUE = "label property value";

    @Override
    public void MetadataChanged() throws BusException
    {
    }

    @Override
    public String getLabel() throws BusException
    {
        return LABEL_PROPERTY_VALUE;
    }

    @Override
    public Map<Short, Variant> getOptParams() throws BusException
    {
        Map<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put(LabelWidgetEnum.BG_COLOR.ID, new Variant(BACKGROUND_COLOR, "u"));
        short[] shortArray = new short[]
        { LabelWidgetHintsType.TEXT_LABEL.ID };
        parameters.put(LabelWidgetEnum.HINTS.ID, new Variant(shortArray, "aq"));

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