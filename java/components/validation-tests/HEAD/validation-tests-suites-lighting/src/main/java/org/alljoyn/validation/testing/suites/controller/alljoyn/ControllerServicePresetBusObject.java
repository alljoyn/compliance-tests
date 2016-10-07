/*******************************************************************************
*  *     Copyright (c) Open Connectivity Foundation (OCF) and AllJoyn Open
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
import org.alljoyn.bus.annotation.BusProperty;
import org.alljoyn.bus.annotation.BusMethod;
import org.alljoyn.bus.annotation.BusSignal;

public class ControllerServicePresetBusObject implements BusObject, ControllerServicePresetBusInterface
{
    @Override
    @BusProperty(signature="u")
    public int getVersion() throws BusException
    {
        return 0;
    }

    @Override
    @BusMethod(replySignature = "ua{sv}")
    public GetDefaultLampStateValues GetDefaultLampState() throws BusException
    {
        return null;
    }

    @Override
    @BusMethod(signature = "a{sv}", replySignature = "u")
    public int SetDefaultLampState(Map<String, Variant> lampState) throws BusException
    {
        return 0;
    }

    @Override
    @BusMethod(replySignature = "uas")
    public GetAllPresetIDsValues GetAllPresetIDs() throws BusException
    {
        return null;
    }

    @Override
    @BusMethod(signature = "ss", replySignature = "usss")
    public GetPresetNameValues GetPresetName(String presetID, String language) throws BusException
    {
        return null;
    }

    @Override
    @BusMethod(signature = "sss", replySignature = "uss")
    public SetPresetNameValues SetPresetName(String presetID, String presetName, String language) throws BusException
    {
        return null;
    }

    @Override
    @BusMethod(signature = "a{sv}ss", replySignature = "us")
    public PresetValues CreatePreset(Map<String, Variant> lampState, String presetName, String language) throws BusException
    {
        return null;
    }

    @Override
    @BusMethod(signature = "sa{sv}", replySignature = "us")
    public PresetValues UpdatePreset(String presetID, Map<String, Variant> lampState) throws BusException
    {
        return null;
    }

    @Override
    @BusMethod(signature = "s", replySignature = "us")
    public PresetValues DeletePreset(String presetID) throws BusException
    {
        return null;
    }

    @Override
    @BusMethod(signature = "s", replySignature = "usa{sv}")
    public GetPresetValues GetPreset(String presetID) throws BusException
    {
        return null;
    }

    @Override
    @BusSignal()
    public void DefaultLampStateChanged() throws BusException
    {
    }

    @Override
    @BusSignal()
    public void PresetsNameChanged(String[] presetIDs) throws BusException
    {
    }

    @Override
    @BusSignal()
    public void PresetsCreated(String[] presetsIDs) throws BusException
    {
    }

    @Override
    @BusSignal()
    public void PresetsUpdated(String[] presetsIDs) throws BusException
    {
    }

    @Override
    @BusSignal()
    public void PresetsDeleted(String[] presetsIDs) throws BusException
    {
    }
}