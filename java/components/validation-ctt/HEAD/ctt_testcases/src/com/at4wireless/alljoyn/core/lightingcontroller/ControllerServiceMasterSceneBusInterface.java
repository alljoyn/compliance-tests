/*
 *    Copyright (c) Open Connectivity Foundation (OCF) and AllJoyn Open
 *    Source Project (AJOSP) Contributors and others.
 *
 *    SPDX-License-Identifier: Apache-2.0
 *
 *    All rights reserved. This program and the accompanying materials are
 *    made available under the terms of the Apache License, Version 2.0
 *    which accompanies this distribution, and is available at
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Copyright (c) Open Connectivity Foundation and Contributors to AllSeen
 *    Alliance. All rights reserved.
 *
 *    Permission to use, copy, modify, and/or distribute this software for
 *    any purpose with or without fee is hereby granted, provided that the
 *    above copyright notice and this permission notice appear in all
 *    copies.
 *
 *     THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL
 *     WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED
 *     WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE
 *     AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL
 *     DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR
 *     PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER
 *     TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 *     PERFORMANCE OF THIS SOFTWARE.
 */
package com.at4wireless.alljoyn.core.lightingcontroller;

import org.alljoyn.bus.BusException;
import org.alljoyn.bus.annotation.BusInterface;
import org.alljoyn.bus.annotation.BusProperty;
import org.alljoyn.bus.annotation.BusMethod;
import org.alljoyn.bus.annotation.BusSignal;
import org.alljoyn.bus.annotation.Position;

// TODO: Auto-generated Javadoc
/**
 * The Interface ControllerServiceMasterSceneBusInterface.
 */
@BusInterface(name = "org.allseen.LSF.ControllerService.MasterScene")
public interface ControllerServiceMasterSceneBusInterface
{
    /* from service_framework\standard_core_library\lighting_controller_service\src\ServiceDescription.cc */
    
    /*
    "   <property name="Version" type="u" access="read" />"
    */
    /**
     * Gets the version.
     *
     * @return the version
     * @throws BusException the bus exception
     */
    @BusProperty(signature="u")
    public int getVersion() throws BusException;

    /**
     * The Class MasterSceneValues.
     */
    public class MasterSceneValues
    {
        
        /** The response code. */
        @Position(0)
        public int responseCode;
        
        /** The master scene id. */
        @Position(1)
        public String masterSceneID;
    }

    /*
    <method name='GetAllMasterSceneIDs'>"
    "      <arg name='responseCode' type='u' direction='out'/>"
    "      <arg name='masterSceneIDs' type='as' direction='out'/>"
    "    </method>"
    */
    /**
     * The Class GetAllMasterSceneIDsValues.
     */
    public class GetAllMasterSceneIDsValues
    {
        
        /** The response code. */
        @Position(0)
        public int responseCode;
        
        /** The master scene i ds. */
        @Position(1)
        public String[] masterSceneIDs;
    }

    /**
     * Gets the all master scene i ds.
     *
     * @return the gets the all master scene i ds values
     * @throws BusException the bus exception
     */
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
    /**
     * The Class GetMasterSceneNameValues.
     */
    public class GetMasterSceneNameValues
    {
        
        /** The response code. */
        @Position(0)
        public int responseCode;
        
        /** The master scene id. */
        @Position(1)
        public String masterSceneID;
        
        /** The language. */
        @Position(2)
        public String language;
        
        /** The master scene name. */
        @Position(3)
        public String masterSceneName;
    }

    /**
     * Gets the master scene name.
     *
     * @param masterSceneID the master scene id
     * @param language the language
     * @return the gets the master scene name values
     * @throws BusException the bus exception
     */
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
    /**
     * The Class SetMasterSceneNameValues.
     */
    public class SetMasterSceneNameValues
    {
        
        /** The response code. */
        @Position(0)
        public int responseCode;
        
        /** The master scene id. */
        @Position(1)
        public String masterSceneID;
        
        /** The language. */
        @Position(2)
        public String language;
    }

    /**
     * Sets the master scene name.
     *
     * @param masterSceneID the master scene id
     * @param masterSceneName the master scene name
     * @param language the language
     * @return the sets the master scene name values
     * @throws BusException the bus exception
     */
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
    /**
     * Creates the master scene.
     *
     * @param scenes the scenes
     * @param masterSceneName the master scene name
     * @param language the language
     * @return the master scene values
     * @throws BusException the bus exception
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
    /**
     * Update master scene.
     *
     * @param masterSceneID the master scene id
     * @param scenes the scenes
     * @return the master scene values
     * @throws BusException the bus exception
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
    /**
     * Delete master scene.
     *
     * @param masterSceneID the master scene id
     * @return the master scene values
     * @throws BusException the bus exception
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
    /**
     * The Class GetMasterSceneValues.
     */
    public class GetMasterSceneValues
    {
        
        /** The response code. */
        @Position(0)
        public int responseCode;
        
        /** The master scene id. */
        @Position(1)
        public String masterSceneID;
        
        /** The scenes. */
        @Position(2)
        public String[] scenes;
    }

    /**
     * Gets the master scene.
     *
     * @param masterSceneID the master scene id
     * @return the gets the master scene values
     * @throws BusException the bus exception
     */
    @BusMethod(signature = "s", replySignature = "usas")
    public GetMasterSceneValues GetMasterScene(String masterSceneID) throws BusException;

    /*
    "    <method name='ApplyMasterScene'>"
    "      <arg name='masterSceneID' type='s' direction='in'/>"
    "      <arg name='responseCode' type='u' direction='out'/>"
    "      <arg name='masterSceneID' type='s' direction='out'/>"
    "    </method>"
    */
    /**
     * Apply master scene.
     *
     * @param masterSceneID the master scene id
     * @return the master scene values
     * @throws BusException the bus exception
     */
    @BusMethod(signature = "s", replySignature = "us")
    public MasterSceneValues ApplyMasterScene(String masterSceneID) throws BusException;

    /*
    "    <signal name='MasterScenesNameChanged'>"
    "      <arg name='masterSceneIDs' type='as' direction='out'/>"
    "    </signal>"
    */
    /**
     * Master scenes name changed.
     *
     * @param masterSceneIDs the master scene i ds
     * @throws BusException the bus exception
     */
    @BusSignal()
    public void MasterScenesNameChanged(String[] masterSceneIDs) throws BusException;

    /*
    "    <signal name='MasterScenesCreated'>"
    "      <arg name='masterSceneIDs' type='as' direction='out'/>"
    "    </signal>"
    */
    /**
     * Master scenes created.
     *
     * @param masterSceneIDs the master scene i ds
     * @throws BusException the bus exception
     */
    @BusSignal()
    public void MasterScenesCreated(String[] masterSceneIDs) throws BusException;

    /*
    "    <signal name='MasterScenesUpdated'>"
    "      <arg name='masterSceneIDs' type='as' direction='out'/>"
    "    </signal>"
    */
    /**
     * Master scenes updated.
     *
     * @param masterSceneIDs the master scene i ds
     * @throws BusException the bus exception
     */
    @BusSignal()
    public void MasterScenesUpdated(String[] masterSceneIDs) throws BusException;

    /*
    "    <signal name='MasterScenesDeleted'>"
    "      <arg name='masterSceneIDs' type='as' direction='out'/>"
    "    </signal>"
    */
    /**
     * Master scenes deleted.
     *
     * @param masterSceneIDs the master scene i ds
     * @throws BusException the bus exception
     */
    @BusSignal()
    public void MasterScenesDeleted(String[] masterSceneIDs) throws BusException;

    /*
    "    <signal name='MasterScenesApplied'>"
    "      <arg name='masterSceneIDs' type='as' direction='out'/>"
    "    </signal>"
    */
    /**
     * Master scenes applied.
     *
     * @param masterSceneIDs the master scene i ds
     * @throws BusException the bus exception
     */
    @BusSignal()
    public void MasterScenesApplied(String[] masterSceneIDs) throws BusException;
}