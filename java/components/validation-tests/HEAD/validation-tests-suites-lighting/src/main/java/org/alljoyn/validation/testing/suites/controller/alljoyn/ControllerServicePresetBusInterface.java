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
import org.alljoyn.bus.Variant;
import org.alljoyn.bus.annotation.BusInterface;
import org.alljoyn.bus.annotation.BusProperty;
import org.alljoyn.bus.annotation.BusMethod;
import org.alljoyn.bus.annotation.BusSignal;
import org.alljoyn.bus.annotation.Position;

@BusInterface(name = "org.allseen.LSF.ControllerService.Preset")
public interface ControllerServicePresetBusInterface
{
    /* from service_framework\standard_core_library\lighting_controller_service\src\ServiceDescription.cc */
    
    /*
    "   <property name="Version" type="u" access="read" />"
    */
    @BusProperty(signature="u")
    public int getVersion() throws BusException;

    public class PresetValues
    {
        @Position(0)
        public int responseCode;
        @Position(1)
        public String presetID;
    }

    /*
    "    <method name='GetDefaultLampState'>"
    "      <arg name='responseCode' type='u' direction='out'/>"
    "      <arg name='lampState' type='a{sv}' direction='out'/>"
    "    </method>"
    */
    public class GetDefaultLampStateValues
    {
        @Position(0)
        public int responseCode;
        @Position(1)
        public Map<String, Variant> lampState;
    }

    @BusMethod(replySignature = "ua{sv}")
    public GetDefaultLampStateValues GetDefaultLampState() throws BusException;

    /*
    "    <method name='SetDefaultLampState'>"
    "      <arg name='lampState' type='a{sv}' direction='in'/>"
    "      <arg name='responseCode' type='u' direction='out'/>"
    "    </method>"
    */
    @BusMethod(signature = "a{sv}", replySignature = "u")
    public int SetDefaultLampState(Map<String, Variant> lampState) throws BusException;

    /*
    "    <method name='GetAllPresetIDs'>"
    "      <arg name='responseCode' type='u' direction='out'/>"
    "      <arg name='presetIDs' type='as' direction='out'/>"
    "    </method>"
    */
    public class GetAllPresetIDsValues
    {
        @Position(0)
        public int responseCode;
        @Position(1)
        public String[] presetIDs;
    }

    @BusMethod(replySignature = "uas")
    public GetAllPresetIDsValues GetAllPresetIDs() throws BusException;

    /*
    "    <method name='GetPresetName'>"
    "      <arg name='presetID' type='s' direction='in'/>"
    "      <arg name='language' type='s' direction='in'/>"
    "      <arg name='responseCode' type='u' direction='out'/>"
    "      <arg name='presetID' type='s' direction='out'/>"
    "      <arg name='language' type='s' direction='out'/>"
    "      <arg name='presetName' type='s' direction='out'/>"
    "    </method>"
    */
    public class GetPresetNameValues
    {
        @Position(0)
        public int responseCode;
        @Position(1)
        public String presetID;
        @Position(2)
        public String language;
        @Position(3)
        public String presetName;
    }

    @BusMethod(signature = "ss", replySignature = "usss")
    public GetPresetNameValues GetPresetName(String presetID, String language) throws BusException;

    /*
    "    <method name='SetPresetName'>"
    "      <arg name='presetID' type='s' direction='in'/>"
    "      <arg name='presetName' type='s' direction='in'/>"
    "      <arg name='language' type='s' direction='in'/>"
    "      <arg name='responseCode' type='u' direction='out'/>"
    "      <arg name='presetID' type='s' direction='out'/>"
    "      <arg name='language' type='s' direction='out'/>"
    "    </method>"
    */
    public class SetPresetNameValues
    {
        @Position(0)
        public int responseCode;
        @Position(1)
        public String presetID;
        @Position(2)
        public String language;
    }

    @BusMethod(signature = "sss", replySignature = "uss")
    public SetPresetNameValues SetPresetName(String presetID, String presetName, String language) throws BusException;

    /*
    "    <method name='CreatePreset'>"
    "      <arg name='lampState' type='a{sv}' direction='in'/>"
    "      <arg name='presetName' type='s' direction='in'/>"
    "      <arg name='language' type='s' direction='in'/>"
    "      <arg name='responseCode' type='u' direction='out'/>"
    "      <arg name='presetID' type='s' direction='out'/>"
    "    </method>"
    */
    @BusMethod(signature = "a{sv}ss", replySignature = "us")
    public PresetValues CreatePreset(Map<String, Variant> lampState, String presetName, String language) throws BusException;

    /*
    "    <method name='UpdatePreset'>"
    "      <arg name='presetID' type='s' direction='in'/>"
    "      <arg name='lampState' type='a{sv}' direction='in'/>"
    "      <arg name='responseCode' type='u' direction='out'/>"
    "      <arg name='presetID' type='s' direction='out'/>"
    "    </method>"
    */
    @BusMethod(signature = "sa{sv}", replySignature = "us")
    public PresetValues UpdatePreset(String presetID, Map<String, Variant> lampState) throws BusException;

    /*
    "    <method name='DeletePreset'>"
    "      <arg name='presetID' type='s' direction='in'/>"
    "      <arg name='responseCode' type='u' direction='out'/>"
    "      <arg name='presetID' type='s' direction='out'/>"
    "    </method>"
    */
    @BusMethod(signature = "s", replySignature = "us")
    public PresetValues DeletePreset(String presetID) throws BusException;

    /*
    "    <method name='GetPreset'>"
    "      <arg name='presetID' type='s' direction='in'/>"
    "      <arg name='responseCode' type='u' direction='out'/>"
    "      <arg name='presetID' type='s' direction='out'/>"
    "      <arg name='lampState' type='a{sv}' direction='out'/>"
    "    </method>"
    */
    public class GetPresetValues
    {
        @Position(0)
        public int responseCode;
        @Position(1)
        public String presetID;
        @Position(2)
        public Map<String, Variant> lampState;
    }

    @BusMethod(signature = "s", replySignature = "usa{sv}")
    public GetPresetValues GetPreset(String presetID) throws BusException;

    /*
    "    <signal name='DefaultLampStateChanged'>"
    "    </signal>"
    */
    @BusSignal()
    public void DefaultLampStateChanged() throws BusException;

    /*
    "    <signal name='PresetsNameChanged'>"
    "      <arg name='presetIDs' type='as' direction='out'/>"
    "    </signal>"
    */
    @BusSignal()
    public void PresetsNameChanged(String[] presetIDs) throws BusException;

    /*
    "    <signal name='PresetsCreated'>"
    "      <arg name='presetsIDs' type='as' direction='out'/>"
    "    </signal>"
    */
    @BusSignal()
    public void PresetsCreated(String[] presetsIDs) throws BusException;

    /*
    "    <signal name='PresetsUpdated'>"
    "      <arg name='presetsIDs' type='as' direction='out'/>"
    "    </signal>"
    */
    @BusSignal()
    public void PresetsUpdated(String[] presetsIDs) throws BusException;

    /*
    "    <signal name='PresetsDeleted'>"
    "      <arg name='presetsIDs' type='as' direction='out'/>"
    "    </signal>"
    */
    @BusSignal()
    public void PresetsDeleted(String[] presetsIDs) throws BusException;
}