/*******************************************************************************
*     Copyright (c) Open Connectivity Foundation (OCF), AllJoyn Open Source
*     Project (AJOSP) Contributors and others.
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
*     THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL
*     WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED
*     WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE
*     AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL
*     DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR
*     PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER
*     TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
*     PERFORMANCE OF THIS SOFTWARE.
*******************************************************************************/
package com.at4wireless.alljoyn.core.lightingcontroller;

import org.alljoyn.bus.BusException;
import org.alljoyn.bus.annotation.BusInterface;
import org.alljoyn.bus.annotation.BusMethod;
import org.alljoyn.bus.annotation.BusSignal;
import org.alljoyn.bus.annotation.BusProperty;
import org.alljoyn.bus.annotation.Position;

@BusInterface(name = "org.allseen.LSF.ControllerService.SceneElement")
public interface ControllerServiceSceneElementBusInterface
{
/*
    "   <property name="Version" type="u" access="read" />"
*/
    @BusProperty(signature="u")
    public int getVersion() throws BusException;

    public class SceneElementValues
    {
        @Position(0)
        public int responseCode;
        @Position(1)
        public String sceneElementID;
    }
/* from service_framework\standard_core_library\lighting_controller_service\src\ServiceDescription.cc */

/*
    "    <method name='GetAllSceneElementIDs'>"
    "      <arg name='responseCode' type='u' direction='out'/>"
    "      <arg name='sceneElementIDs' type='as' direction='out'/>"
    "    </method>"
 */

    public class GetAllSceneElementIDsValues
    {
        @Position(0)
        public int responseCode;
        @Position(1)
        public String[] sceneElementIDs;
    }

    @BusMethod(replySignature = "uas")
    public GetAllSceneElementIDsValues GetAllSceneElementIDs() throws BusException;

/*
    "    <method name='GetSceneElementName'>"
    "      <arg name='sceneElementID' type='s' direction='in'/>"
    "      <arg name='language' type='s' direction='in'/>"
    "      <arg name='responseCode' type='u' direction='out'/>"
    "      <arg name='sceneElementID' type='s' direction='out'/>"
    "      <arg name='language' type='s' direction='out'/>"
    "      <arg name='sceneElementName' type='s' direction='out'/>"
    "    </method>"
 */

    public class GetSceneElementNameValues
    {
        @Position(0)
        public int responseCode;
        @Position(1)
        public String sceneElementID;
        @Position(2)
        public String language;
        @Position(3)
        public String sceneElementName;
    }

    @BusMethod(signature = "ss", replySignature = "usss")
    public GetSceneElementNameValues GetSceneElementName(String SceneElementID, String language) throws BusException;

/*
    "    <method name='SetSceneElementName'>"
    "      <arg name='sceneElementID' type='s' direction='in'/>"
    "      <arg name='sceneElementName' type='s' direction='in'/>"
    "      <arg name='language' type='s' direction='in'/>"
    "      <arg name='responseCode' type='u' direction='out'/>"
    "      <arg name='sceneElementID' type='s' direction='out'/>"
    "      <arg name='language' type='s' direction='out'/>"
    "    </method>"
 */

    public class SetSceneElementNameValues
    {
        @Position(0)
        public int responseCode;
        @Position(1)
        public String sceneElementID;
        @Position(2)
        public String language;
    }

    @BusMethod(signature = "sss", replySignature = "uss")
    public SetSceneElementNameValues SetSceneElementName(String SceneElementID, String SceneElementName, String language) throws BusException;

/*
    "    <method name='CreateSceneElement'>"
    "      <arg name='SceneElement' type='asass' direction='in'/>"
    "      <arg name='sceneElementName' type='s' direction='in'/>"
    "      <arg name='language' type='s' direction='in'/>"
    "      <arg name='responseCode' type='u' direction='out'/>"
    "      <arg name='sceneElementID' type='s' direction='out'/>"
    "    </method>"
 */

    @BusMethod(signature = "asassss", replySignature = "us")
    public SceneElementValues CreateSceneElement(String[] lampList, String[] lampGroupList, String effectID, String SceneElementName, String language) throws BusException;

/*
    "    <method name='UpdateSceneElement'>"
    "      <arg name='sceneElementID' type='s' direction='in'/>"
    "      <arg name='SceneElement' type='asass' direction='in'/>"
    "      <arg name='responseCode' type='u' direction='out'/>"
    "      <arg name='sceneElementID' type='s' direction='out'/>"
    "    </method>"
 */

    @BusMethod(signature = "sasass", replySignature = "us")
    public SceneElementValues UpdateSceneElement(String sceneElementID, String[] lampList, String[] lampGroupList, String effectID) throws BusException;

/*
    "    <method name='DeleteSceneElement'>"
    "      <arg name='sceneElementID' type='s' direction='in'/>"
    "      <arg name='responseCode' type='u' direction='out'/>"
    "      <arg name='sceneElementID' type='s' direction='out'/>"
    "    </method>"
 */

    @BusMethod(signature = "s", replySignature = "us")
    public SceneElementValues DeleteSceneElement(String SceneElementID) throws BusException;

/*
    "    <method name='GetSceneElement'>"
    "      <arg name='sceneElementID' type='s' direction='in'/>"
    "      <arg name='responseCode' type='u' direction='out'/>"
    "      <arg name='sceneElementID' type='s' direction='out'/>"
    "      <arg name='SceneElement' type='asass' direction='out'/>"
    "    </method>"
 */

    public class GetSceneElementValues
    {
        @Position(0)
        public int responseCode;
        @Position(1)
        public String sceneElementID;
        @Position(2)
        public String[] lampList;
        @Position(3)
        public String[] lampGroupList;
        @Position(4)
        public String effectID;
    }

    @BusMethod(signature = "s", replySignature = "usasass")
    public GetSceneElementValues GetSceneElement(String SceneElementID) throws BusException;

/*
    "    <method name='ApplySceneElement'>"
    "      <arg name='sceneElementID' type='s' direction='in'/>"
    "      <arg name='responseCode' type='u' direction='out'/>"
    "      <arg name='sceneElementID' type='s' direction='out'/>"
    "    </method>"
 */

    @BusMethod(signature = "sas", replySignature = "us")
    public SceneElementValues ApplySceneElement(String SceneElementID) throws BusException;

/*
    "    <signal name='SceneElementsNameChanged'>"
    "      <arg name='sceneElementIDs' type='as' direction='out'/>"
    "    </signal>"
 */

    @BusSignal()
    public void SceneElementsNameChanged(String[] SceneElementIDs) throws BusException;

/*
    "    <signal name='SceneElementsCreated'>"
    "      <arg name='SceneElementIDs' type='as' direction='out'/>"
    "    </signal>"
 */

    @BusSignal()
    public void SceneElementsCreated(String[] SceneElementIDs) throws BusException;

/*
    "    <signal name='SceneElementsUpdated'>"
    "      <arg name='SceneElementIDs' type='as' direction='out'/>"
    "    </signal>"
 */

    @BusSignal()
    public void SceneElementsUpdated(String[] SceneElementIDs) throws BusException;

/*
    "    <signal name='SceneElementsDeleted'>"
    "      <arg name='SceneElementIDs' type='as' direction='out'/>"
    "    </signal>"
 */

    @BusSignal()
    public void SceneElementsDeleted(String[] SceneElementIDs) throws BusException;

/*
    "    </signal>"
    "    <signal name='SceneElementsApplied'>"
    "      <arg name='sceneElementIDs' type='as' direction='out'/>"
    "    </signal>"
 */

    @BusSignal()
    public void SceneElementsApplied(String[] SceneElementIDs) throws BusException;
}