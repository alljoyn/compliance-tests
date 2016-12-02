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
import org.alljoyn.ioe.controlpanelservice.communication.interfaces.ListPropertyControlSecured;
import org.alljoyn.ioe.controlpanelservice.ui.ListPropertyWidgetEnum;
import org.alljoyn.ioe.controlpanelservice.ui.ListPropertyWidgetHintsType;
import org.alljoyn.ioe.controlpanelservice.ui.ajstruct.ListPropertyWidgetRecordAJ;

public class ListPropertySecuredBusObject implements ListPropertyControlSecured, BusObject
{
    private static final int STATE = 1;
    private static final int BACKGROUND_COLOR = 13434879;
    private static final String LABEL = "secured list property label";

    @Override
    public void Add() throws BusException
    {
    }

    @Override
    public void Cancel() throws BusException
    {
    }

    @Override
    public void Confirm() throws BusException
    {
    }

    @Override
    public void Delete(short arg0) throws BusException
    {
    }

    @Override
    public void MetadataChanged() throws BusException
    {
    }

    @Override
    public void Update(short arg0) throws BusException
    {
    }

    @Override
    public void ValueChanged() throws BusException
    {
    }

    @Override
    public void View(short arg0) throws BusException
    {
    }

    @Override
    public Map<Short, Variant> getOptParams() throws BusException
    {
        Map<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put(ListPropertyWidgetEnum.LABEL.ID, new Variant(LABEL));
        parameters.put(ListPropertyWidgetEnum.BG_COLOR.ID, new Variant(BACKGROUND_COLOR, "u"));
        short[] shortArray = new short[]
        { ListPropertyWidgetHintsType.DYNAMIC_SPINNER.ID };
        parameters.put(ListPropertyWidgetEnum.HINTS.ID, new Variant(shortArray, "aq"));

        return parameters;
    }

    @Override
    public int getStates() throws BusException
    {
        return STATE;
    }

    @Override
    public ListPropertyWidgetRecordAJ[] getValue() throws BusException
    {
        return new ListPropertyWidgetRecordAJ[0];
    }

    @Override
    public short getVersion() throws BusException
    {
        return VERSION;
    }
}