/*******************************************************************************
 *  Copyright (c) 2016 Open Connectivity Foundation (OCF) and AllJoyn Open
 *     Source Project (AJOSP) Contributors and others.
 *
 *     SPDX-License-Identifier: Apache-2.0
 *
 *     All rights reserved. This program and the accompanying materials are
 *     made available under the terms of the Apache License, Version 2.0
 *     which accompanies this distribution, and is available at
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Copyright 2016 Open Connectivity Foundation and Contributors to
 *     AllSeen Alliance. All rights reserved.
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
import org.alljoyn.bus.Variant;
import org.alljoyn.bus.annotation.BusInterface;
import org.alljoyn.bus.annotation.BusProperty;
import org.alljoyn.bus.annotation.BusMethod;
import org.alljoyn.bus.annotation.BusSignal;
import org.alljoyn.bus.annotation.Position;

@BusInterface(name = "org.allseen.LSF.ControllerService.LampGroup")
public interface ControllerServiceLampGroupBusInterface
{
    /* from service_framework\standard_core_library\lighting_controller_service\src\ServiceDescription.cc */
    
    /*
    "   <property name="Version" type="u" access="read" />"
    */
    @BusProperty(signature="u")
    public int getVersion() throws BusException;

    public class Values
    {
        @Position(0)
        public int responseCode;

        @Position(1)
        public String lampGroupID;
    }

    /*
    "    <method name='GetAllLampGroupIDs'>"
    "      <arg name='responseCode' type='u' direction='out'/>"
    "      <arg name='lampGroupIDs' type='as' direction='out'/>"
    "    </method>"
    */
    public class GetAllLampGroupIDsValues
    {
        @Position(0)
        public int responseCode;

        @Position(1)
        public String[] lampGroupIDs;
    }

    @BusMethod(replySignature = "uas")
    public GetAllLampGroupIDsValues GetAllLampGroupIDs() throws BusException;

    /*
    "    <method name='GetLampGroupName'>"
    "      <arg name='lampGroupID' type='s' direction='in'/>"
    "      <arg name='language' type='s' direction='in'/>"
    "      <arg name='responseCode' type='u' direction='out'/>"
    "      <arg name='lampGroupID' type='s' direction='out'/>"
    "      <arg name='language' type='s' direction='out'/>"
    "      <arg name='lampGroupName' type='s' direction='out'/>"
    "    </method>"
    */
    public class GetLampGroupNameValues
    {
        @Position(0)
        public int responseCode;

        @Position(1)
        public String lampGroupID;

        @Position(2)
        public String language;

        @Position(3)
        public String lampGroupName;
    }

    @BusMethod(signature = "ss", replySignature = "usss")
    public GetLampGroupNameValues GetLampGroupName(String lampGroupID, String language) throws BusException;

    /*
    "    <method name='SetLampGroupName'>"
    "      <arg name='lampGroupID' type='s' direction='in'/>"
    "      <arg name='lampGroupName' type='s' direction='in'/>"
    "      <arg name='language' type='s' direction='in'/>"
    "      <arg name='responseCode' type='u' direction='out'/>"
    "      <arg name='lampGroupID' type='s' direction='out'/>"
    "      <arg name='language' type='s' direction='out'/>"
    "    </method>"
    */
    public class SetLampGroupNameValues
    {
        @Position(0)
        public int responseCode;

        @Position(1)
        public String lampGroupID;

        @Position(2)
        public String language;
    }

    @BusMethod(signature = "sss", replySignature = "uss")
    public SetLampGroupNameValues SetLampGroupName(String lampGroupID, String lampGroupName, String language) throws BusException;

    /*
    "    <method name='CreateLampGroup'>"
    "      <arg name='lampIDs' type='as' direction='in'/>"
    "      <arg name='lampGroupIDs' type='as' direction='in'/>"
    "      <arg name='lampGroupName' type='s' direction='in'/>"
    "      <arg name='language' type='s' direction='in'/>"
    "      <arg name='responseCode' type='u' direction='out'/>"
    "      <arg name='lampGroupID' type='s' direction='out'/>"
    "    </method>"
    */
    public class CreateLampGroupValues
    {
        @Position(0)
        public int responseCode;

        @Position(1)
        public String lampGroupID;
    }

    @BusMethod(signature = "asasss", replySignature = "us")
    public CreateLampGroupValues CreateLampGroup(String[] lampIDs, String[] lampGroupIDs, String lampGroupName, String language) throws BusException;

    /*
    "    <method name='UpdateLampGroup'>"
    "      <arg name='lampGroupID' type='s' direction='in'/>"
    "      <arg name='lampIDs' type='as' direction='in'/>"
    "      <arg name='lampGroupIDs' type='as' direction='in'/>"
    "      <arg name='responseCode' type='u' direction='out'/>"
    "      <arg name='lampGroupID' type='s' direction='out'/>"
    "    </method>"
    */
    public class UpdateLampGroupValues
    {
        @Position(0)
        public int responseCode;

        @Position(1)
        public String lampGroupID;
    }

    @BusMethod(signature = "sasas", replySignature = "us")
    public UpdateLampGroupValues UpdateLampGroup(String lampGroupID, String[] lampIDs, String[] lampGroupIDs) throws BusException;

    /*
    "    <method name='DeleteLampGroup'>"
    "      <arg name='lampGroupID' type='s' direction='in'/>"
    "      <arg name='responseCode' type='u' direction='out'/>"
    "      <arg name='lampGroupID' type='s' direction='out'/>"
    "    </method>"
    */
    @BusMethod(signature = "s", replySignature = "us")
    public Values DeleteLampGroup(String lampGroupID) throws BusException;

    /*
    "    <method name='GetLampGroup'>"
    "      <arg name='lampGroupID' type='s' direction='in'/>"
    "      <arg name='responseCode' type='u' direction='out'/>"
    "      <arg name='lampGroupID' type='s' direction='out'/>"
    "      <arg name='lampIDs' type='as' direction='out'/>"
    "      <arg name='lampGroupIDs' type='as' direction='out'/>"
    "    </method>"
    */
    public class GetLampGroupValues
    {
        @Position(0)
        public int responseCode;
        
        @Position(1)
        public String lampGroupID;

        @Position(2)
        public String[] lampIDs;

        @Position(3)
        public String[] lampGroupIDs;
    }

    @BusMethod(signature = "s", replySignature = "usasas")
    public GetLampGroupValues GetLampGroup(String lampGroupID) throws BusException;

    /*
    "    <method name='TransitionLampGroupState'>"
    "      <arg name='lampGroupID' type='s' direction='in'/>"
    "      <arg name='lampState' type='a{sv}' direction='in'/>"
    "	   <arg name='transitionPeriod' type='u' direction='in'/>"
    "      <arg name='responseCode' type='u' direction='out'/>"
    "      <arg name='lampGroupID' type='s' direction='out'/>"
    "    </method>"
    */
    public class TransitionLampGroupStateValues
    {
        @Position(0)
        public int responseCode;

        @Position(1)
        public String lampGroupID;
    }

    @BusMethod(signature = "sa{sv}u", replySignature = "us")
    public TransitionLampGroupStateValues TransitionLampGroupState(String lampGroupID, Map<String, Variant> lampState, int transitionPeriod) throws BusException;

    /*
    "    <method name='PulseLampGroupWithState'>"
    "      <arg name='lampGroupID' type='s' direction='in'/>"
    "      <arg name='fromLampGroupState' type='a{sv}' direction='in'/>"
    "      <arg name='toLampGroupState' type='a{sv}' direction='in'/>"
    "      <arg name='period' type='u' direction='in'/>"
    "      <arg name='duration' type='u' direction='in'/>"
    "      <arg name='numPulses' type='u' direction='in'/>"
    "      <arg name='responseCode' type='u' direction='out'/>"
    "      <arg name='lampGroupID' type='s' direction='out'/>"
    "    </method>"
    */
    @BusMethod(signature = "sa{sv}a{sv}uuu", replySignature = "us")
    public Values PulseLampGroupWithState(String lampGroupID, Map<String, Variant> fromLampGroupState, Map<String, Variant> toLampGroupState, int period, int duration, int numPulses) throws BusException;

    /*
    "    <method name='PulseLampGroupWithPreset'>"
    "      <arg name='lampGroupID' type='s' direction='in'/>"
    "      <arg name='fromPresetID' type='s' direction='in'/>"
    "      <arg name='toPresetID' type='s' direction='in'/>"
    "      <arg name='period' type='u' direction='in'/>"
    "      <arg name='duration' type='u' direction='in'/>"
    "      <arg name='numPulses' type='u' direction='in'/>"
    "      <arg name='responseCode' type='u' direction='out'/>"
    "      <arg name='lampGroupID' type='s' direction='out'/>"
    "    </method>"
    */
    @BusMethod(signature = "sssuuu", replySignature = "us")
    public Values PulseLampGroupWithPreset(String lampGroupID, String fromPresetID, String toPresetID, int period, int duration, int numPulses) throws BusException;

    /*
    "    <method name='TransitionLampGroupStateToPreset'>"
    "      <arg name='lampGroupID' type='s' direction='in'/>"
    "      <arg name='presetID' type='s' direction='in'/>"
    "      <arg name='transitionPeriod' type='u' direction='in'/>"
    "      <arg name='responseCode' type='u' direction='out'/>"
    "      <arg name='lampGroupID' type='s' direction='out'/>"
    "    </method>"
    */
    @BusMethod(signature = "ssu", replySignature = "us")
    public Values TransitionLampGroupStateToPreset(String lampGroupID, String presetID, int transitionPeriod) throws BusException;

    /*
    "    <method name='TransitionLampGroupStateField'>"
    "      <arg name='lampGroupID' type='s' direction='in'/>"
    "      <arg name='lampGroupStateFieldName' type='s' direction='in'/>"
    "      <arg name='lampGroupStateFieldValue' type='v' direction='in'/>"
    "      <arg name='transitionPeriod' type='u' direction='in'/>"
    "      <arg name='responseCode' type='u' direction='out'/>"
    "      <arg name='lampGroupID' type='s' direction='out'/>"
    "      <arg name='lampGroupStateFieldName' type='s' direction='out'/>"
    "    </method>"
    */
    public class TransitionLampGroupStateFieldValues
    {
        @Position(0)
        public int responseCode;

        @Position(1)
        public String lampGroupID;

        @Position(2)
        public String lampGroupStateFieldName;
    }

    @BusMethod(signature = "ssvu", replySignature = "uss")
    public TransitionLampGroupStateFieldValues TransitionLampGroupStateField(String lampGroupID, String lampGroupStateFieldName, Variant lampGroupStateFieldValue, int transitionPeriod) throws BusException;

    /*
    "    <method name='ResetLampGroupState'>"
    "      <arg name='lampGroupID' type='s' direction='in'/>"
    "      <arg name='responseCode' type='u' direction='out'/>"
    "      <arg name='lampGroupID' type='s' direction='out'/>"
    "    </method>"
    */
    @BusMethod(signature = "s", replySignature = "us")
    public Values ResetLampGroupState(String lampGroupID) throws BusException;

    /*
    "    <method name='ResetLampGroupFieldState'>"
    "      <arg name='lampGroupID' type='s' direction='in'/>"
    "      <arg name='lampGroupStateFieldName' type='s' direction='in'/>"
    "      <arg name='responseCode' type='u' direction='out'/>"
    "      <arg name='lampGroupID' type='s' direction='out'/>"
    "      <arg name='lampGroupStateFieldName' type='s' direction='out'/>"
    "    </method>"
    */
    public class ResetLampGroupStateFieldValues
    {
        @Position(0)
        public int responseCode;

        @Position(1)
        public String lampGroupID;

        @Position(2)
        public String lampGroupStateFieldName;
    }

    @BusMethod(signature = "ss", replySignature = "uss")
    public ResetLampGroupStateFieldValues ResetLampGroupStateField(String lampGroupID, String lampGroupStateFieldName) throws BusException;

    /*
    "    <signal name='LampGroupsNameChanged'>"
    "      <arg name='lampGroupsIDs' type='as' direction='out'/>"
    "    </signal>"
    */
    @BusSignal(signature = "as")
    public void LampGroupsNameChanged(String[] lampGroupsIDs) throws BusException;

    /*
    "    <signal name='LampGroupsCreated'>"
    "      <arg name='lampGroupsIDs' type='as' direction='out'/>"
    "    </signal>"
    */
    @BusSignal(signature = "as")
    public void LampGroupsCreated(String[] lampGroupsIDs) throws BusException;

    /*
    "    <signal name='LampGroupsUpdated'>"
    "      <arg name='lampGroupsIDs' type='as' direction='out'/>"
    "    </signal>"
    */
    @BusSignal(signature = "as")
    public void LampGroupsUpdated(String[] lampGroupsIDs) throws BusException;

    /*
    "    <signal name='LampGroupsDeleted'>"
    "      <arg name='lampGroupsIDs' type='as' direction='out'/>"
    "    </signal>"
    */
    @BusSignal(signature = "as")
    public void LampGroupsDeleted(String[] lampGroupsIDs) throws BusException;
}