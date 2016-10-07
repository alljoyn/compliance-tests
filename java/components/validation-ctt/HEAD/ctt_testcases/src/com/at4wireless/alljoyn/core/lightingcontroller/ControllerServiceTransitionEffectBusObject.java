/*******************************************************************************
*  *  Copyright (c) Open Connectivity Foundation (OCF) and AllJoyn Open
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
package com.at4wireless.alljoyn.core.lightingcontroller;

import java.util.Map;

import org.alljoyn.bus.BusException;
import org.alljoyn.bus.BusObject;
import org.alljoyn.bus.Variant;
import org.alljoyn.bus.annotation.BusMethod;
import org.alljoyn.bus.annotation.BusSignal;
import org.alljoyn.bus.annotation.BusProperty;

public class ControllerServiceTransitionEffectBusObject implements BusObject, ControllerServiceTransitionEffectBusInterface
{
    @BusProperty(signature="u")
    public int getVersion() throws BusException
    {
        return 0;
    }

    @BusMethod(replySignature = "uas")
    public GetAllTransitionEffectIDsValues GetAllTransitionEffectIDs() throws BusException
    {
        return null;
    }

    @BusMethod(signature = "ss", replySignature = "usss")
    public GetTransitionEffectNameValues GetTransitionEffectName(String transitionEffectID, String language) throws BusException
    {
        return null;
    }

    @BusMethod(signature = "sss", replySignature = "uss")
    public SetTransitionEffectNameValues SetTransitionEffectName(String transitionEffectID, String transitionEffectName, String language) throws BusException
    {
        return null;
    }

    @BusMethod(signature = "a{sv}suss", replySignature = "us")
    public TransitionEffectValues CreateTransitionEffect(Map<String, Variant> lampState, String presetID, int transitionPeriod, String transitionEffectName, String language) throws BusException{
        return null;
    }

    @BusMethod(signature = "sa{sv}su", replySignature = "us")
    public TransitionEffectValues UpdateTransitionEffect(String transitionEffectID, Map<String, Variant> lampState, String presetID, int transitionPeriod) throws BusException
    {
        return null;
    }

    @BusMethod(signature = "s", replySignature = "us")
    public TransitionEffectValues DeleteTransitionEffect(String transitionEffectID) throws BusException
    {
        return null;
    }

    @BusMethod(signature = "s", replySignature = "usa{sv}su")
    public GetTransitionEffectValues GetTransitionEffect(String transitionEffectID) throws BusException
    {
        return null;
    }

    @BusMethod(signature = "sas", replySignature = "usas")
    public ApplyTransitionEffectOnLampsValues ApplyTransitionEffectOnLamps(String transitionEffectID, String[] lampIDs) throws BusException
    {
        return null;
    }

    @BusMethod(signature = "sas", replySignature = "usas")
    public ApplyTransitionEffectOnLampGroupsValues ApplyTransitionEffectOnLampGroups(String transitionEffectID, String[] lampGroupIDs) throws BusException
    {
        return null;
    }

    @BusSignal()
    public void TransitionEffectsNameChanged(String[] transitionEffectIDs) throws BusException
    {
    }

    @BusSignal()
    public void TransitionEffectsCreated(String[] transitionEffectIDs) throws BusException
    {
    }

    @BusSignal()
    public void TransitionEffectsUpdated(String[] transitionEffectIDs) throws BusException
    {
    }

    @BusSignal()
    public void TransitionEffectsDeleted(String[] transitionEffectIDs) throws BusException
    {
    }

}