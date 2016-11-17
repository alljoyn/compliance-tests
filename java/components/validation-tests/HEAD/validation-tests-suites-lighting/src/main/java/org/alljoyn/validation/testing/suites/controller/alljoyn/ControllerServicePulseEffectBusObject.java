/*******************************************************************************
*  Copyright (c) Open Connectivity Foundation (OCF) and AllJoyn Open
*     Source Project (AJOSP) Contributors and others.
*
*     SPDX-License-Identifier: Apache-2.0
*
*     All rights reserved. This program and the accompanying materials are
*     made available under the terms of the Apache License, Version 2.0
*     which accompanies this distribution, and is available at
*     http://www.apache.org/licenses/LICENSE-2.0
*
*     Copyright (c) Open Connectivity Foundation and Contributors to AllSeen
*     Alliance. All rights reserved.
*
*     Permission to use, copy, modify, and/or distribute this software for
*     any purpose with or without fee is hereby granted, provided that the
*     above copyright notice and this permission notice appear in all
*     copies.
*
*      THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL
*      WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED
*      WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE
*      AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL
*      DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR
*      PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER
*      TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
*      PERFORMANCE OF THIS SOFTWARE.
*******************************************************************************/
package org.alljoyn.validation.testing.suites.controller.alljoyn;

import java.util.Map;

import org.alljoyn.bus.BusException;
import org.alljoyn.bus.BusObject;
import org.alljoyn.bus.Variant;
import org.alljoyn.bus.annotation.BusMethod;
import org.alljoyn.bus.annotation.BusSignal;
import org.alljoyn.bus.annotation.BusProperty;

public class ControllerServicePulseEffectBusObject implements BusObject, ControllerServicePulseEffectBusInterface
{
    @BusProperty(signature="u")
    public int getVersion() throws BusException
    {
        return 0;
    }

    @BusMethod(replySignature = "uas")
    public GetAllPulseEffectIDsValues GetAllPulseEffectIDs() throws BusException
    {
        return null;
    }

    @BusMethod(signature = "ss", replySignature = "usss")
    public GetPulseEffectNameValues GetPulseEffectName(String pulseEffectID, String language) throws BusException
    {
        return null;
    }

    @BusMethod(signature = "sss", replySignature = "uss")
    public SetPulseEffectNameValues SetPulseEffectName(String pulseEffectID, String pulseEffectName, String language) throws BusException
    {
        return null;
    }

    @BusMethod(signature = "a{sv}uuua{sv}ssss", replySignature = "us")
    public PulseEffectValues CreatePulseEffect(Map<String, Variant> toLampState, int pulsePeriod, int pulseDuration, int numPulses, Map<String, Variant> fromLampState, String toPresetID, String fromPresetID, String pulseEffectName, String language) throws BusException
    {
        return null;
    }

    @BusMethod(signature = "sa{sv}uuua{sv}ss", replySignature = "us")
    public PulseEffectValues UpdatePulseEffect(String pulseEffectID, Map<String, Variant> toLampState, int pulsePeriod, int pulseDuration, int numPulses, Map<String, Variant> fromLampState, String toPresetID, String fromPresetID)throws BusException
    {
        return null;
    }

    @BusMethod(signature = "s", replySignature = "us")
    public PulseEffectValues DeletePulseEffect(String pulseEffectID) throws BusException
    {
        return null;
    }

    @BusMethod(signature = "s", replySignature = "usa{sv}su")
    public GetPulseEffectValues GetPulseEffect(String pulseEffectID) throws BusException
    {
        return null;
    }

    @BusMethod(signature = "sas", replySignature = "usas")
    public ApplyPulseEffectOnLampsValues ApplyPulseEffectOnLamps(String pulseEffectID, String[] lampIDs) throws BusException
    {
        return null;
    }

    @BusMethod(signature = "sas", replySignature = "usas")
    public ApplyPulseEffectOnLampGroupsValues ApplyPulseEffectOnLampGroups(String pulseEffectID, String[] lampGroupIDs) throws BusException
    {
        return null;
    }

    @BusSignal()
    public void PulseEffectsNameChanged(String[] pulseEffectIDs) throws BusException
    {
    }

    @BusSignal()
    public void PulseEffectsCreated(String[] pulseEffectIDs) throws BusException
    {
    }

    @BusSignal()
    public void PulseEffectsUpdated(String[] pulseEffectIDs) throws BusException
    {
    }

    @BusSignal()
    public void PulseEffectsDeleted(String[] pulseEffectIDs) throws BusException
    {
    }
}