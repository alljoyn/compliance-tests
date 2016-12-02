
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

import java.util.Map;

import org.alljoyn.bus.BusException;
import org.alljoyn.bus.BusObject;
import org.alljoyn.bus.Variant;
import org.alljoyn.bus.annotation.BusProperty;
import org.alljoyn.bus.annotation.BusMethod;
import org.alljoyn.bus.annotation.BusSignal;

// TODO: Auto-generated Javadoc
/**
 * The Class ControllerServiceLampBusObject.
 */
public class ControllerServiceLampBusObject implements BusObject, ControllerServiceLampBusInterface
{
    
    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceLampBusInterface#getVersion()
     */
    @Override
    @BusProperty(signature="u")
    public int getVersion() throws BusException
    {
        return 0;
    }

    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceLampBusInterface#GetAllLampIDs()
     */
    @Override
    @BusMethod(replySignature = "uas")
    public GetAllLampIDsValues GetAllLampIDs() throws BusException
    {
        return null;
    }

    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceLampBusInterface#GetLampSupportedLanguages(java.lang.String)
     */
    @Override
    @BusMethod(signature = "s", replySignature = "usas")
    public GetLampSupportedLanguagesValues GetLampSupportedLanguages(String lampID) throws BusException
    {
        return null;
    }

    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceLampBusInterface#GetLampManufacturer(java.lang.String, java.lang.String)
     */
    @Override
    @BusMethod(signature = "ss", replySignature = "usss")
    public GetLampManufacturerValues GetLampManufacturer(String lampID, String language) throws BusException
    {
        return null;
    }

    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceLampBusInterface#GetLampName(java.lang.String, java.lang.String)
     */
    @Override
    @BusMethod(signature = "ss", replySignature = "usss")
    public GetLampNameValues GetLampName(String lampID, String language) throws BusException
    {
        return null;
    }

    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceLampBusInterface#SetLampName(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    @BusMethod(signature = "sss", replySignature = "uss")
    public SetLampNameValues SetLampName(String lampID, String lampName, String language) throws BusException
    {
        return null;
    }

    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceLampBusInterface#GetLampDetails(java.lang.String)
     */
    @Override
    @BusMethod(signature = "s", replySignature = "usa{sv}")
    public GetLampDetailsValues GetLampDetails(String lampID) throws BusException
    {
        return null;
    }

    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceLampBusInterface#GetLampParameters(java.lang.String)
     */
    @Override
    @BusMethod(signature = "s", replySignature = "usa{sv}")
    public GetLampParametersValues GetLampParameters(String lampID) throws BusException
    {
        return null;
    }

    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceLampBusInterface#GetLampParametersField(java.lang.String, java.lang.String)
     */
    @Override
    @BusMethod(signature = "ss", replySignature = "ussv")
    public GetLampParametersFieldValues GetLampParametersField(String lampID, String lampParameterFieldName) throws BusException
    {
        return null;
    }

    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceLampBusInterface#GetLampState(java.lang.String)
     */
    @Override
    @BusMethod(signature = "s", replySignature = "usa{sv}")
    public GetLampStateValues GetLampState(String lampID) throws BusException
    {
        return null;
    }

    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceLampBusInterface#GetLampStateField(java.lang.String, java.lang.String)
     */
    @Override
    @BusMethod(signature = "ss", replySignature = "ussv")
    public GetLampStateFieldValues GetLampStateField(String lampID, String lampStateFieldName) throws BusException
    {
        return null;
    }

    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceLampBusInterface#TransitionLampState(java.lang.String, java.util.Map, int)
     */
    @Override
    @BusMethod(signature = "sa{sv}u", replySignature = "us")
    public TransitionLampStateValues TransitionLampState(String lampID, Map<String, Variant> lampState, int transitionPeriod) throws BusException
    {
        return null;
    }

    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceLampBusInterface#PulseLampWithState(java.lang.String, java.util.Map, java.util.Map, int, int, int)
     */
    @Override
    @BusMethod(signature = "sa{sv}a{sv}uuu", replySignature = "us")
    public PulseLampWithStateValues PulseLampWithState(String lampID, Map<String, Variant> fromLampState, Map<String, Variant> toLampState, int period, int duration, int numPulses) throws BusException
    {
        return null;
    }

    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceLampBusInterface#PulseLampWithPreset(java.lang.String, java.lang.String, java.lang.String, int, int, int)
     */
    @Override
    @BusMethod(signature = "sssuuu", replySignature = "us")
    public PulseLampWithPresetValues PulseLampWithPreset(String lampID, String fromPresetID, String toPresetID, int period, int duration, int numPulses) throws BusException
    {
        return null;
    }

    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceLampBusInterface#TransitionLampStateToPreset(java.lang.String, java.lang.String, int)
     */
    @Override
    @BusMethod(signature = "ssu", replySignature = "us")
    public TransitionLampStateToPresetValues TransitionLampStateToPreset(String lampID, String presetID, int transitionPeriod) throws BusException
    {
        return null;
    }

    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceLampBusInterface#TransitionLampStateField(java.lang.String, java.lang.String, org.alljoyn.bus.Variant, int)
     */
    @Override
    @BusMethod(signature = "ssvu", replySignature = "uss")
    public TransitionLampStateFieldValues TransitionLampStateField(String lampID, String lampStateFieldName, Variant lampStateFieldValue, int transitionPeriod) throws BusException
    {
        return null;
    }

    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceLampBusInterface#ResetLampState(java.lang.String)
     */
    @Override
    @BusMethod(signature = "s", replySignature = "us")
    public ResetLampStateValues ResetLampState(String lampID) throws BusException
    {
        return null;
    }

    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceLampBusInterface#ResetLampStateField(java.lang.String, java.lang.String)
     */
    @Override
    @BusMethod(signature = "ss", replySignature = "uss")
    public ResetLampStateFieldValues ResetLampStateField(String lampID, String lampStateFieldName) throws BusException
    {
        return null;
    }

    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceLampBusInterface#GetLampFaults(java.lang.String)
     */
    @Override
    @BusMethod(signature = "s", replySignature = "usau")
    public GetLampFaultsValues GetLampFaults(String lampID) throws BusException
    {
        return null;
    }

    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceLampBusInterface#ClearLampFault(java.lang.String, int)
     */
    @Override
    @BusMethod(signature = "su", replySignature = "usu")
    public ClearLampFaultValues ClearLampFault(String lampID, int lampFault) throws BusException
    {
        return null;
    }

    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceLampBusInterface#GetLampServiceVersion(java.lang.String)
     */
    @Override
    @BusMethod(signature = "s", replySignature = "usu")
    public GetLampServiceVersionValues GetLampServiceVersion(String lampID) throws BusException
    {
        return null;
    }

    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceLampBusInterface#LampNameChanged(java.lang.String, java.lang.String)
     */
    @Override
    @BusSignal(signature = "ss")
    public void LampNameChanged(String lampID, String lampName) throws BusException
    {
    }

    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceLampBusInterface#LampStateChanged(java.lang.String, java.util.Map)
     */
    @Override
    @BusSignal(signature = "sa{sv}")
    public void LampStateChanged(String lampID, Map<String, Variant> lampState) throws BusException
    {
    }

    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceLampBusInterface#LampsFound(java.lang.String[])
     */
    @Override
    @BusSignal(signature = "as")
    public void LampsFound(String[] lampIDs)
    {
    }

    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceLampBusInterface#LampsLost(java.lang.String[])
     */
    @Override
    @BusSignal(signature = "as")
    public void LampsLost(String[] lampIDs)
    {
    }
}