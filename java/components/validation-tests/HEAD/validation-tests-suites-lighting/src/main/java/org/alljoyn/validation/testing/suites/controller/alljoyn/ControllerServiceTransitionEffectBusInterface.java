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
import org.alljoyn.bus.annotation.BusMethod;
import org.alljoyn.bus.annotation.BusSignal;
import org.alljoyn.bus.annotation.BusProperty;
import org.alljoyn.bus.annotation.Position;

@BusInterface(name = "org.allseen.LSF.ControllerService.TransitionEffect")
public interface ControllerServiceTransitionEffectBusInterface
{
    /*
    "   <property name="Version" type="u" access="read" />"
    */
    @BusProperty(signature="u")
    public int getVersion() throws BusException;

    public class TransitionEffectValues
    {
        @Position(0)
        public int responseCode;
        @Position(1)
        public String transitionEffectID;
    }

/* from service_framework\standard_core_library\lighting_controller_service\src\ServiceDescription.cc */

/*
    "    <method name='GetAllTransitionEffectIDs'>"
    "      <arg name='responseCode' type='u' direction='out'/>"
    "      <arg name='transitionEffectIDs' type='as' direction='out'/>"
    "    </method>"
 */

    public class GetAllTransitionEffectIDsValues
    {
        @Position(0)
        public int responseCode;
        @Position(1)
        public String[] transitionEffectIDs;
    }

    @BusMethod(replySignature = "uas")
    public GetAllTransitionEffectIDsValues GetAllTransitionEffectIDs() throws BusException;

/*
    "    <method name='GetTransitionEffectName'>"
    "      <arg name='transitionEffectID' type='s' direction='in'/>"
    "      <arg name='language' type='s' direction='in'/>"
    "      <arg name='responseCode' type='u' direction='out'/>"
    "      <arg name='transitionEffectID' type='s' direction='out'/>"
    "      <arg name='language' type='s' direction='out'/>"
    "      <arg name='transitionEffectName' type='s' direction='out'/>"
    "    </method>"
 */

    public class GetTransitionEffectNameValues
    {
        @Position(0)
        public int responseCode;
        @Position(1)
        public String transitionEffectID;
        @Position(2)
        public String language;
        @Position(3)
        public String transitionEffectName;
    }

    @BusMethod(signature = "ss", replySignature = "usss")
    public GetTransitionEffectNameValues GetTransitionEffectName(String transitionEffectID, String language) throws BusException;

/*
    "    <method name='SetTransitionEffectName'>"
    "      <arg name='transitionEffectID' type='s' direction='in'/>"
    "      <arg name='transitionEffectName' type='s' direction='in'/>"
    "      <arg name='language' type='s' direction='in'/>"
    "      <arg name='responseCode' type='u' direction='out'/>"
    "      <arg name='transitionEffectID' type='s' direction='out'/>"
    "      <arg name='language' type='s' direction='out'/>"
    "    </method>"
 */

    public class SetTransitionEffectNameValues
    {
        @Position(0)
        public int responseCode;
        @Position(1)
        public String transitionEffectID;
        @Position(2)
        public String language;
    }

    @BusMethod(signature = "sss", replySignature = "uss")
    public SetTransitionEffectNameValues SetTransitionEffectName(String transitionEffectID, String transitionEffectName, String language) throws BusException;

/*
    "    <method name='CreateTransitionEffect'>"
    "      <arg name='lampState' type='a{sv}' direction='in'/>"
    "      <arg name='presetID' type='s' direction='in'/>"
    "      <arg name='transitionPeriod' type='u' direction='in'/>"
    "      <arg name='transitionEffectName' type='s' direction='in'/>"
    "      <arg name='language' type='s' direction='in'/>"
    "      <arg name='responseCode' type='u' direction='out'/>"
    "      <arg name='transitionEffectID' type='s' direction='out'/>"
    "    </method>"
 */

    @BusMethod(signature = "a{sv}suss", replySignature = "us")
    public TransitionEffectValues CreateTransitionEffect(Map<String, Variant> lampState, String presetID, int transitionPeriod, String transitionEffectName, String language) throws BusException;

/*
    "    <method name='UpdateTransitionEffect'>"
    "      <arg name='transitionEffectID' type='s' direction='in'/>"
    "      <arg name='lampState' type='a{sv}' direction='in'/>"
    "      <arg name='presetID' type='s' direction='in'/>"
    "      <arg name='transitionPeriod' type='u' direction='in'/>"
    "      <arg name='responseCode' type='u' direction='out'/>"
    "      <arg name='transitionEffectID' type='s' direction='out'/>"
    "    </method>"
 */

    @BusMethod(signature = "sa{sv}su", replySignature = "us")
    public TransitionEffectValues UpdateTransitionEffect(String transitionEffectID, Map<String, Variant> lampState, String presetID, int transitionPeriod) throws BusException;

/*
    "    <method name='DeleteTransitionEffect'>"
    "      <arg name='transitionEffectID' type='s' direction='in'/>"
    "      <arg name='responseCode' type='u' direction='out'/>"
    "      <arg name='transitionEffectID' type='s' direction='out'/>"
    "    </method>"
 */

    @BusMethod(signature = "s", replySignature = "us")
    public TransitionEffectValues DeleteTransitionEffect(String transitionEffectID) throws BusException;

/*
    "    <method name='GetTransitionEffect'>"
    "      <arg name='transitionEffectID' type='s' direction='in'/>"
    "      <arg name='responseCode' type='u' direction='out'/>"
    "      <arg name='transitionEffectID' type='s' direction='out'/>"
    "      <arg name='lampState' type='a{sv}' direction='out'/>"
    "      <arg name='presetID' type='s' direction='out'/>"
    "      <arg name='transitionPeriod' type='u' direction='out'/>"
    "    </method>"
 */

    public class GetTransitionEffectValues
    {
        @Position(0)
        public int responseCode;
        @Position(1)
        public String transitionEffectID;
        @Position(2)
        public Map<String, Variant> lampState;
        @Position(3)
        public String presetID;
        @Position(4)
        public int transitionPeriod;
    }

    @BusMethod(signature = "s", replySignature = "usa{sv}su")
    public GetTransitionEffectValues GetTransitionEffect(String transitionEffectID) throws BusException;

/*
    "    <method name='ApplyTransitionEffectOnLamps'>"
    "      <arg name='transitionEffectID' type='s' direction='in'/>"
    "      <arg name='lampIDs' type='as' direction='in'/>"
    "      <arg name='responseCode' type='u' direction='out'/>"
    "      <arg name='transitionEffectID' type='s' direction='out'/>"
    "      <arg name='lampIDs' type='as' direction='out'/>"
    "    </method>"
 */

    public class ApplyTransitionEffectOnLampsValues
    {
        @Position(0)
        public int responseCode;
        @Position(1)
        public String transitionEffectID;
        @Position(2)
        public String[] lampIDs;
    }

    @BusMethod(signature = "sas", replySignature = "usas")
    public ApplyTransitionEffectOnLampsValues ApplyTransitionEffectOnLamps(String transitionEffectID, String[] lampIDs) throws BusException;

/*
    "    <method name='ApplyTransitionEffectOnLampGroups'>"
    "      <arg name='transitionEffectID' type='s' direction='in'/>"
    "      <arg name='lampGroupIDs' type='as' direction='in'/>"
    "      <arg name='responseCode' type='u' direction='out'/>"
    "      <arg name='transitionEffectID' type='s' direction='out'/>"
    "      <arg name='lampGroupIDs' type='as' direction='out'/>"
    "    </method>"
 */

    public class ApplyTransitionEffectOnLampGroupsValues
    {
        @Position(0)
        public int responseCode;
        @Position(1)
        public String transitionEffectID;
        @Position(2)
        public String[] lampGroupIDs;
    }

    @BusMethod(signature = "sas", replySignature = "usas")
    public ApplyTransitionEffectOnLampGroupsValues ApplyTransitionEffectOnLampGroups(String transitionEffectID, String[] lampGroupIDs) throws BusException;

/*
    "    <signal name='TransitionEffectsNameChanged'>"
    "      <arg name='transitionEffectIDs' type='as' direction='out'/>"
    "    </signal>"
 */

    @BusSignal()
    public void TransitionEffectsNameChanged(String[] transitionEffectIDs) throws BusException;

/*
    "    <signal name='TransitionEffectsCreated'>"
    "      <arg name='transitionEffectsIDs' type='as' direction='out'/>"
    "    </signal>"
 */

    @BusSignal()
    public void TransitionEffectsCreated(String[] transitionEffectIDs) throws BusException;

/*
    "    <signal name='TransitionEffectsUpdated'>"
    "      <arg name='transitionEffectsIDs' type='as' direction='out'/>"
    "    </signal>"
 */

    @BusSignal()
    public void TransitionEffectsUpdated(String[] transitionEffectIDs) throws BusException;

/*
    "    <signal name='TransitionEffectsDeleted'>"
    "      <arg name='transitionEffectsIDs' type='as' direction='out'/>"
    "    </signal>"
 */

    @BusSignal()
    public void TransitionEffectsDeleted(String[] transitionEffectIDs) throws BusException;
}