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
package org.alljoyn.validation.testing.suites.controller.alljoyn;

import org.alljoyn.bus.BusException;
import org.alljoyn.bus.annotation.BusInterface;
import org.alljoyn.bus.annotation.BusProperty;
import org.alljoyn.bus.annotation.BusMethod;
import org.alljoyn.bus.annotation.BusSignal;
import org.alljoyn.bus.annotation.Position;

@BusInterface(name = "org.allseen.LSF.ControllerService.MasterScene")
public interface ControllerServiceMasterSceneBusInterface
{
    /* from service_framework\standard_core_library\lighting_controller_service\src\ServiceDescription.cc */
    
    /*
    "   <property name="Version" type="u" access="read" />"
    */
    @BusProperty(signature="u")
    public int getVersion() throws BusException;

    public class MasterSceneValues
    {
        @Position(0)
        public int responseCode;
        @Position(1)
        public String masterSceneID;
    }

    /*
    <method name='GetAllMasterSceneIDs'>"
    "      <arg name='responseCode' type='u' direction='out'/>"
    "      <arg name='masterSceneIDs' type='as' direction='out'/>"
    "    </method>"
    */
    public class GetAllMasterSceneIDsValues
    {
        @Position(0)
        public int responseCode;
        @Position(1)
        public String[] masterSceneIDs;
    }

    @BusMethod(replySignature = "uas")
    public GetAllMasterSceneIDsValues GetAllMasterSceneIDs() throws BusException;

    /*
    "    <method name='GetMasterSceneName'>"
    "      <arg name='masterSceneID' type='s' direction='in'/>"
    "      <arg name='language' type='s' direction='in'/>"
    "      <arg name='responseCode' type='u' direction='out'/>"
    "      <arg name='masterSceneID' type='s' direction='out'/>"
    "      <arg name='language' type='s' direction='out'/>"
    "      <arg name='masterSceneName' type='s' direction='out'/>"
    "    </method>"
    */
    public class GetMasterSceneNameValues
    {
        @Position(0)
        public int responseCode;
        @Position(1)
        public String masterSceneID;
        @Position(2)
        public String language;
        @Position(3)
        public String masterSceneName;
    }

    @BusMethod(signature = "ss", replySignature = "usss")
    public GetMasterSceneNameValues GetMasterSceneName(String masterSceneID, String language) throws BusException;

    /*
    "    <method name='SetMasterSceneName'>"
    "      <arg name='masterSceneID' type='s' direction='in'/>"
    "      <arg name='masterSceneName' type='s' direction='in'/>"
    "      <arg name='language' type='s' direction='in'/>"
    "      <arg name='responseCode' type='u' direction='out'/>"
    "      <arg name='masterSceneID' type='s' direction='out'/>"
    "      <arg name='language' type='s' direction='out'/>"
    "    </method>"
    */
    public class SetMasterSceneNameValues
    {
        @Position(0)
        public int responseCode;
        @Position(1)
        public String masterSceneID;
        @Position(2)
        public String language;
    }

    @BusMethod(signature = "sss", replySignature = "uss")
    public SetMasterSceneNameValues SetMasterSceneName(String masterSceneID, String masterSceneName, String language) throws BusException;

    /*
    "    <method name='CreateMasterScene'>"
    "      <arg name='scenes' type='as' direction='in'/>"
    "      <arg name='masterSceneName' type='s' direction='in'/>"
    "      <arg name='language' type='s' direction='in'/>"
    "      <arg name='responseCode' type='u' direction='out'/>"
    "      <arg name='masterSceneID' type='s' direction='out'/>"
    "    </method>"
    */
    @BusMethod(signature = "asss", replySignature = "us")
    public MasterSceneValues CreateMasterScene(String[] scenes, String masterSceneName, String language) throws BusException;

    /*
    "    <method name='UpdateMasterScene'>"
    "      <arg name='masterSceneID' type='s' direction='in'/>"
    "      <arg name='scenes' type='as' direction='in'/>"
    "      <arg name='responseCode' type='u' direction='out'/>"
    "      <arg name='masterSceneID' type='s' direction='out'/>"
    "    </method>"
    */
    @BusMethod(signature = "sas", replySignature = "us")
    public MasterSceneValues UpdateMasterScene(String masterSceneID, String[] scenes) throws BusException;

    /*
    "    <method name='DeleteMasterScene'>"
    "      <arg name='masterSceneID' type='s' direction='in'/>"
    "      <arg name='responseCode' type='u' direction='out'/>"
    "      <arg name='masterSceneID' type='s' direction='out'/>"
    "    </method>"
    */
    @BusMethod(signature = "s", replySignature = "us")
    public MasterSceneValues DeleteMasterScene(String masterSceneID) throws BusException;

    /*
    "    <method name='GetMasterScene'>"
    "      <arg name='masterSceneID' type='s' direction='in'/>"
    "      <arg name='responseCode' type='u' direction='out'/>"
    "      <arg name='masterSceneID' type='s' direction='out'/>"
    "      <arg name='scenes' type='as' direction='out'/>"
    "    </method>"
    */
    public class GetMasterSceneValues
    {
        @Position(0)
        public int responseCode;
        @Position(1)
        public String masterSceneID;
        @Position(2)
        public String[] scenes;
    }

    @BusMethod(signature = "s", replySignature = "usas")
    public GetMasterSceneValues GetMasterScene(String masterSceneID) throws BusException;

    /*
    "    <method name='ApplyMasterScene'>"
    "      <arg name='masterSceneID' type='s' direction='in'/>"
    "      <arg name='responseCode' type='u' direction='out'/>"
    "      <arg name='masterSceneID' type='s' direction='out'/>"
    "    </method>"
    */
    @BusMethod(signature = "s", replySignature = "us")
    public MasterSceneValues ApplyMasterScene(String masterSceneID) throws BusException;

    /*
    "    <signal name='MasterScenesNameChanged'>"
    "      <arg name='masterSceneIDs' type='as' direction='out'/>"
    "    </signal>"
    */
    @BusSignal()
    public void MasterScenesNameChanged(String[] masterSceneIDs) throws BusException;

    /*
    "    <signal name='MasterScenesCreated'>"
    "      <arg name='masterSceneIDs' type='as' direction='out'/>"
    "    </signal>"
    */
    @BusSignal()
    public void MasterScenesCreated(String[] masterSceneIDs) throws BusException;

    /*
    "    <signal name='MasterScenesUpdated'>"
    "      <arg name='masterSceneIDs' type='as' direction='out'/>"
    "    </signal>"
    */
    @BusSignal()
    public void MasterScenesUpdated(String[] masterSceneIDs) throws BusException;

    /*
    "    <signal name='MasterScenesDeleted'>"
    "      <arg name='masterSceneIDs' type='as' direction='out'/>"
    "    </signal>"
    */
    @BusSignal()
    public void MasterScenesDeleted(String[] masterSceneIDs) throws BusException;

    /*
    "    <signal name='MasterScenesApplied'>"
    "      <arg name='masterSceneIDs' type='as' direction='out'/>"
    "    </signal>"
    */
    @BusSignal()
    public void MasterScenesApplied(String[] masterSceneIDs) throws BusException;
}