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
package com.at4wireless.alljoyn.core.lightingcontroller;

import java.util.Map;

import org.alljoyn.bus.BusException;
import org.alljoyn.bus.BusObject;
import org.alljoyn.bus.Variant;
import org.alljoyn.bus.annotation.BusMethod;
import org.alljoyn.bus.annotation.BusProperty;
import org.alljoyn.bus.annotation.BusSignal;

public class ControllerServiceLampGroupBusObject implements BusObject, ControllerServiceLampGroupBusInterface
{
    @Override
    @BusProperty(signature="u")
    public int getVersion() throws BusException
    {
        return 0;
    }

    @Override
    @BusMethod(replySignature = "uas")
    public GetAllLampGroupIDsValues GetAllLampGroupIDs() throws BusException
    {
        return null;
    }

    @Override
    @BusMethod(signature = "ss", replySignature = "usss")
    public GetLampGroupNameValues GetLampGroupName(String lampGroupID, String language) throws BusException
    {
        return null;
    }

    @Override
    @BusMethod(signature = "sss", replySignature = "uss")
    public SetLampGroupNameValues SetLampGroupName(String lampGroupID, String lampGroupName, String language) throws BusException
    {
        return null;
    }

    @Override
    @BusMethod(signature = "asas", replySignature = "us")
    public CreateLampGroupValues CreateLampGroup(String[] lampIDs, String[] lampGroupIDs, String lampGroupName, String language) throws BusException
    {
        return null;
    }

    @Override
    @BusMethod(signature = "sasas", replySignature = "us")
    public UpdateLampGroupValues UpdateLampGroup(String lampGroupID, String[] lampIDs, String[] lampGroupIDs) throws BusException
    {
        return null;
    }

    @Override
    @BusMethod(signature = "s", replySignature = "us")
    public Values DeleteLampGroup(String lampGroupID) throws BusException
    {
        return null;
    }

    @Override
    @BusMethod(signature = "s", replySignature = "usasas")
    public GetLampGroupValues GetLampGroup(String lampGroupID) throws BusException
    {
        return null;
    }

    @Override
    @BusMethod(signature = "sa{sv}u", replySignature = "us")
    public TransitionLampGroupStateValues TransitionLampGroupState(String lampGroupID, Map<String, Variant> lampState, int transitionPeriod) throws BusException
    {
        return null;
    }

    @Override
    @BusMethod(signature = "sa{sv}a{sv}uuu", replySignature = "us")
    public Values PulseLampGroupWithState(String lampGroupID, Map<String, Variant> fromLampGroupState, Map<String, Variant> toLampGroupState, int period, int duration, int numPulses) throws BusException
    {
        return null;
    }

    @Override
    @BusMethod(signature = "sssuuu", replySignature = "us")
    public Values PulseLampGroupWithPreset(String lampGroupID, String fromPresetID, String toPresetID, int period, int duration, int numPulses) throws BusException
    {
        return null;
    }

    @Override
    @BusMethod(signature = "ssu", replySignature = "us")
    public Values TransitionLampGroupStateToPreset(String lampGroupID, String presetID, int transtionPeriod) throws BusException
    {
        return null;
    }

    @Override
    @BusMethod(signature = "ssvu", replySignature = "uss")
    public TransitionLampGroupStateFieldValues TransitionLampGroupStateField(String lampGroupID, String lampGroupStateFieldName, Variant lampGroupStateFieldValue, int transitionPeriod) throws BusException
    {
        return null;
    }

    @Override
    @BusMethod(signature = "s", replySignature = "us")
    public Values ResetLampGroupState(String lampGroupID) throws BusException
    {
        return null;
    }

    @Override
    @BusMethod(signature = "ss", replySignature = "uss")
    public ResetLampGroupStateFieldValues ResetLampGroupStateField(String lampGroupID, String lampGroupStateFieldName) throws BusException
    {
        return null;
    }

    @Override
    @BusSignal(signature = "as")
    public void LampGroupsNameChanged(String[] lampGroupsIDs) throws BusException
    {
    }

    @Override
    @BusSignal(signature = "as")
    public void LampGroupsCreated(String[] lampGroupsIDs) throws BusException
    {
    }

    @Override
    @BusSignal(signature = "as")
    public void LampGroupsUpdated(String[] lampGroupsIDs) throws BusException
    {
    }

    @Override
    @BusSignal(signature = "as")
    public void LampGroupsDeleted(String[] lampGroupsIDs) throws BusException
    {
    }
}