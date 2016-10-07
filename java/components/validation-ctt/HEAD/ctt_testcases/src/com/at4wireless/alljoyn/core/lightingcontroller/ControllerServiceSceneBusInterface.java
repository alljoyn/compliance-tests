/*******************************************************************************
 *   *  Copyright (c) Open Connectivity Foundation (OCF) and AllJoyn Open
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
import org.alljoyn.bus.Variant;
import org.alljoyn.bus.annotation.BusInterface;
import org.alljoyn.bus.annotation.BusProperty;
import org.alljoyn.bus.annotation.BusMethod;
import org.alljoyn.bus.annotation.BusSignal;
import org.alljoyn.bus.annotation.Position;

@BusInterface(name = "org.allseen.LSF.ControllerService.Scene")
public interface ControllerServiceSceneBusInterface
{
    /* from service_framework\standard_core_library\lighting_controller_service\src\ServiceDescription.cc */
    
    /*
    "   <property name="Version" type="u" access="read" />"
    */
    @BusProperty(signature="u")
    public int getVersion() throws BusException;

    public class SceneValues
    {
        @Position(0)
        public int responseCode;

        @Position(1)
        public String sceneID;
    }

    /*
    "    <method name='GetAllSceneIDs'>"
    "      <arg name='responseCode' type='u' direction='out'/>"
    "      <arg name='sceneIDs' type='as' direction='out'/>"
    "    </method>"
    */
    public class GetAllSceneIDsValues
    {
        @Position(0)
        public int responseCode;

        @Position(1)
        public String[] sceneIDs;
    }

    @BusMethod(replySignature = "uas")
    public GetAllSceneIDsValues GetAllSceneIDs() throws BusException;

    /*
    "    <method name='GetSceneName'>"
    "      <arg name='sceneID' type='s' direction='in'/>"
    "      <arg name='language' type='s' direction='in'/>"
    "      <arg name='responseCode' type='u' direction='out'/>"
    "      <arg name='sceneID' type='s' direction='out'/>"
    "      <arg name='language' type='s' direction='out'/>"
    "      <arg name='sceneName' type='s' direction='out'/>"
    "    </method>"
    */
    public class GetSceneNameValues
    {
        @Position(0)
        public int responseCode;

        @Position(1)
        public String sceneID;

        @Position(2)
        public String language;

        @Position(3)
        public String sceneName;
    }

    @BusMethod(signature = "ss", replySignature = "usss")
    public GetSceneNameValues GetSceneName(String sceneID, String language) throws BusException;

    /*
    "    <method name='SetSceneName'>"
    "      <arg name='sceneID' type='s' direction='in'/>"
    "      <arg name='SceneName' type='s' direction='in'/>"
    "      <arg name='language' type='s' direction='in'/>"
    "      <arg name='responseCode' type='u' direction='out'/>"
    "      <arg name='sceneID' type='s' direction='out'/>"
    "      <arg name='language' type='s' direction='out'/>"
    "    </method>"
    */
    public class SetSceneNameValues
    {
        @Position(0)
        public int responseCode;

        @Position(1)
        public String sceneID;

        @Position(2)
        public String language;
    }

    @BusMethod(signature = "sss", replySignature = "uss")
    public SetSceneNameValues SetSceneName(String sceneID, String sceneName, String language) throws BusException;

    /*
    "    <method name='CreateScene'>"
    "      <arg name='transitionlampsLampGroupsToState' type='a(asasa{sv}u)' direction='in'/>"
    "      <arg name='transitionlampsLampGroupsToPreset' type='a(asassu)' direction='in'/>"
    "      <arg name='pulselampsLampGroupsWithState' type='a(asasa{sv}a{sv}uuu)' direction='in'/>"
    "      <arg name='pulselampsLampGroupsWithPreset' type='a(asasssuuu)' direction='in'/>"
    "      <arg name='sceneName' type='s' direction='in'/>"
    "      <arg name='language' type='s' direction='in'/>"
    "      <arg name='responseCode' type='u' direction='out'/>"
    "      <arg name='sceneID' type='s' direction='out'/>"
    "    </method>"

    */

    public class CreateSceneTransitionlampsLampGroupsToState
    {
        @Position(0)
        public String[] lampIDs;

        @Position(1)
        public String[] lampGroupIDs;

        @Position(2)
        public Map<String, Variant> lampState;

        @Position(3)
        public int transitionPeriod;
    }

    public class CreateSceneTransitionlampsLampGroupsToPreset
    {
        @Position(0)
        public String[] lampIDs;

        @Position(1)
        public String[] lampGroupIDs;

        @Position(2)
        public String presetID;

        @Position(3)
        public int transitionPeriod;
    }

    public class CreateScenePulselampsLampGroupsWithState
    {
        @Position(0)
        public String[] lampIDs;

        @Position(1)
        public String[] lampGroupIDs;

        @Position(2)
        public Map<String, Variant> toState;

        @Position(3)
        public Map<String, Variant> fromState;

        @Position(4)
        public int period;

        @Position(5)
        public int duration;

        @Position(6)
        public int numPulses;
    }

    public class CreateScenePulselampsLampGroupsWithPreset
    {
        @Position(0)
        public String[] lampIDs;

        @Position(1)
        public String[] lampGroupIDs;

        @Position(2)
        public String toPresetID;

        @Position(3)
        public String fromPresetID;

        @Position(4)
        public int period;

        @Position(5)
        public int duration;

        @Position(6)
        public int numPulses;
    }

    @BusMethod(signature = "a(asasa{sv}u)a(asassu)a(asasa{sv}a{sv}uuu)a(asasssuuu)ss", replySignature = "us")
    public SceneValues CreateScene(CreateSceneTransitionlampsLampGroupsToState[] transitionlampsLampGroupsToState, CreateSceneTransitionlampsLampGroupsToPreset[] transitionlampsLampGroupsToPreset, CreateScenePulselampsLampGroupsWithState[] pulselampsLampGroupsWithState, CreateScenePulselampsLampGroupsWithPreset[] pulselampsLampGroupsWithPreset, String sceneName, String language) throws BusException;

    /*"    <method name='UpdateScene'>"
    "      <arg name='sceneID' type='s' direction='in'/>"
    "      <arg name='transitionlampsLampGroupsToState' type='a(asasa{sv}u)' direction='in'/>"
    "      <arg name='transitionlampsLampGroupsToPreset' type='a(asassu)' direction='in'/>"
    "      <arg name='pulselampsLampGroupsWithState' type='a(asasa{sv}a{sv}uuu)' direction='in'/>"
    "      <arg name='pulselampsLampGroupsWithPreset' type='a(asasssuuu)' direction='in'/>"
    "      <arg name='responseCode' type='u' direction='out'/>"
    "      <arg name='sceneID' type='s' direction='out'/>"
    "    </method>"
    */
    @BusMethod(signature = "sa(asasa{sv}u)a(asassu)a(asasa{sv}a{sv}uuu)a(asasssuuu)", replySignature = "us")
    public SceneValues UpdateScene(String sceneID, CreateSceneTransitionlampsLampGroupsToState[] transitionlampsLampGroupsToState, CreateSceneTransitionlampsLampGroupsToPreset[] transitionlampsLampGroupsToPreset, CreateScenePulselampsLampGroupsWithState[] pulselampsLampGroupsWithState, CreateScenePulselampsLampGroupsWithPreset[] pulselampsLampGroupsWithPreset) throws BusException;

    /*
    "    <method name='DeleteScene'>"
    "      <arg name='sceneID' type='s' direction='in'/>"
    "      <arg name='responseCode' type='u' direction='out'/>"
    "      <arg name='sceneID' type='s' direction='out'/>"
    "    </method>"
    */
    @BusMethod(signature = "s", replySignature = "us")
    public SceneValues DeleteScene(String sceneID) throws BusException;

    /*
    "    <method name='GetScene'>"
    "      <arg name='sceneID' type='s' direction='in'/>"
    "      <arg name='responseCode' type='u' direction='out'/>"
    "      <arg name='sceneID' type='s' direction='out'/>"
    "      <arg name='transitionlampsLampGroupsToState' type='a(asasa{sv}u)' direction='out'/>"
    "      <arg name='transitionlampsLampGroupsToPreset' type='a(asassu)' direction='out'/>"
    "      <arg name='pulselampsLampGroupsWithState' type='a(asasa{sv}a{sv}uuu)' direction='out'/>"
    "      <arg name='pulselampsLampGroupsWithPreset' type='a(asasssuuu)' direction='out'/>"
    "    </method>"
    */
    public class GetSceneValues
    {
        @Position(0)
        public int responseCode;

        @Position(1)
        public String sceneID;

        @Position(2)
        public CreateSceneTransitionlampsLampGroupsToState[] transitionlampsLampGroupsToState;

        @Position(3)
        public CreateSceneTransitionlampsLampGroupsToPreset[] transitionlampsLampGroupsToPreset;

        @Position(4)
        public CreateScenePulselampsLampGroupsWithState[] pulselampsLampGroupsWithState;

        @Position(5)
        public CreateScenePulselampsLampGroupsWithPreset[] pulselampsLampGroupsWithPreset;
    }

    @BusMethod(signature = "s", replySignature = "usa(asasa{sv}u)a(asassu)a(asasa{sv}a{sv}uuu)a(asasssuuu)")
    public GetSceneValues GetScene(String sceneID) throws BusException;

    /*
    "    <method name='ApplyScene'>"
    "      <arg name='sceneID' type='s' direction='in'/>"
    "      <arg name='responseCode' type='u' direction='out'/>"
    "      <arg name='sceneID' type='s' direction='out'/>"
    "    </method>"
    */

    @BusMethod(signature = "s", replySignature = "us")
    public SceneValues ApplyScene(String sceneID) throws BusException;

    /*
    "    <signal name='ScenesNameChanged'>"
    "      <arg name='sceneIDs' type='as' direction='out'/>"
    "    </signal>"
    */
    @BusSignal(signature = "as")
    public void ScenesNameChanged(String[] sceneIDs) throws BusException;

    /*
    "    <signal name='ScenesCreated'>"
    "      <arg name='sceneIDs' type='as' direction='out'/>"
    "    </signal>"
    */
    @BusSignal(signature = "as")
    public void ScenesCreated(String[] sceneIDs) throws BusException;

    /*
    "    <signal name='ScenesUpdated'>"
    "      <arg name='sceneIDs' type='as' direction='out'/>"
    "    </signal>"
    */
    @BusSignal(signature = "as")
    public void ScenesUpdated(String[] sceneIDs) throws BusException;

    /*
    "    <signal name='ScenesDeleted'>"
    "      <arg name='sceneIDs' type='as' direction='out'/>"
    "    </signal>"
    */
    @BusSignal(signature = "as")
    public void ScenesDeleted(String[] sceneIDs) throws BusException;

    /*
    "    <signal name='ScenesApplied'>"
    "      <arg name='sceneIDs' type='as' direction='out'/>"
    "    </signal>"
    */
    @BusSignal(signature = "as")
    public void ScenesApplied(String[] sceneIDs) throws BusException;
}