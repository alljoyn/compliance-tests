
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
package com.at4wireless.alljoyn.core.lightingcontroller;

import java.util.Map;

import org.alljoyn.bus.BusException;
import org.alljoyn.bus.BusObject;
import org.alljoyn.bus.Variant;
import org.alljoyn.bus.annotation.BusProperty;
import org.alljoyn.bus.annotation.BusMethod;
import org.alljoyn.bus.annotation.BusSignal;

public class ControllerServiceLampBusObject implements BusObject, ControllerServiceLampBusInterface
{
    @Override
    @BusProperty(signature="u")
    public int getVersion() throws BusException
    {
        return 0;
    }

    @Override
    @BusMethod(replySignature = "uas")
    public GetAllLampIDsValues GetAllLampIDs() throws BusException
    {
        return null;
    }

    @Override
    @BusMethod(signature = "s", replySignature = "usas")
    public GetLampSupportedLanguagesValues GetLampSupportedLanguages(String lampID) throws BusException
    {
        return null;
    }

    @Override
    @BusMethod(signature = "ss", replySignature = "usss")
    public GetLampManufacturerValues GetLampManufacturer(String lampID, String language) throws BusException
    {
        return null;
    }

    @Override
    @BusMethod(signature = "ss", replySignature = "usss")
    public GetLampNameValues GetLampName(String lampID, String language) throws BusException
    {
        return null;
    }

    @Override
    @BusMethod(signature = "sss", replySignature = "uss")
    public SetLampNameValues SetLampName(String lampID, String lampName, String language) throws BusException
    {
        return null;
    }

    @Override
    @BusMethod(signature = "s", replySignature = "usa{sv}")
    public GetLampDetailsValues GetLampDetails(String lampID) throws BusException
    {
        return null;
    }

    @Override
    @BusMethod(signature = "s", replySignature = "usa{sv}")
    public GetLampParametersValues GetLampParameters(String lampID) throws BusException
    {
        return null;
    }

    @Override
    @BusMethod(signature = "ss", replySignature = "ussv")
    public GetLampParametersFieldValues GetLampParametersField(String lampID, String lampParameterFieldName) throws BusException
    {
        return null;
    }

    @Override
    @BusMethod(signature = "s", replySignature = "usa{sv}")
    public GetLampStateValues GetLampState(String lampID) throws BusException
    {
        return null;
    }

    @Override
    @BusMethod(signature = "ss", replySignature = "ussv")
    public GetLampStateFieldValues GetLampStateField(String lampID, String lampStateFieldName) throws BusException
    {
        return null;
    }

    @Override
    @BusMethod(signature = "sa{sv}u", replySignature = "us")
    public TransitionLampStateValues TransitionLampState(String lampID, Map<String, Variant> lampState, int transitionPeriod) throws BusException
    {
        return null;
    }

    @Override
    @BusMethod(signature = "sa{sv}a{sv}uuu", replySignature = "us")
    public PulseLampWithStateValues PulseLampWithState(String lampID, Map<String, Variant> fromLampState, Map<String, Variant> toLampState, int period, int duration, int numPulses) throws BusException
    {
        return null;
    }

    @Override
    @BusMethod(signature = "sssuuu", replySignature = "us")
    public PulseLampWithPresetValues PulseLampWithPreset(String lampID, String fromPresetID, String toPresetID, int period, int duration, int numPulses) throws BusException
    {
        return null;
    }

    @Override
    @BusMethod(signature = "ssu", replySignature = "us")
    public TransitionLampStateToPresetValues TransitionLampStateToPreset(String lampID, String presetID, int transitionPeriod) throws BusException
    {
        return null;
    }

    @Override
    @BusMethod(signature = "ssvu", replySignature = "uss")
    public TransitionLampStateFieldValues TransitionLampStateField(String lampID, String lampStateFieldName, Variant lampStateFieldValue, int transitionPeriod) throws BusException
    {
        return null;
    }

    @Override
    @BusMethod(signature = "s", replySignature = "us")
    public ResetLampStateValues ResetLampState(String lampID) throws BusException
    {
        return null;
    }

    @Override
    @BusMethod(signature = "ss", replySignature = "uss")
    public ResetLampStateFieldValues ResetLampStateField(String lampID, String lampStateFieldName) throws BusException
    {
        return null;
    }

    @Override
    @BusMethod(signature = "s", replySignature = "usau")
    public GetLampFaultsValues GetLampFaults(String lampID) throws BusException
    {
        return null;
    }

    @Override
    @BusMethod(signature = "su", replySignature = "usu")
    public ClearLampFaultValues ClearLampFault(String lampID, int lampFault) throws BusException
    {
        return null;
    }

    @Override
    @BusMethod(signature = "s", replySignature = "usu")
    public GetLampServiceVersionValues GetLampServiceVersion(String lampID) throws BusException
    {
        return null;
    }

    @Override
    @BusSignal(signature = "ss")
    public void LampNameChanged(String lampID, String lampName) throws BusException
    {
    }

    @Override
    @BusSignal(signature = "sa{sv}")
    public void LampStateChanged(String lampID, Map<String, Variant> lampState) throws BusException
    {
    }

    @Override
    @BusSignal(signature = "as")
    public void LampsFound(String[] lampIDs)
    {
    }

    @Override
    @BusSignal(signature = "as")
    public void LampsLost(String[] lampIDs)
    {
    }
}