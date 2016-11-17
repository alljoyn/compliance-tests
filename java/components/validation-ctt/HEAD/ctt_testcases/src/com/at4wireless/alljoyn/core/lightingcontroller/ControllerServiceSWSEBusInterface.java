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

import org.alljoyn.bus.BusException;
import org.alljoyn.bus.annotation.BusInterface;
import org.alljoyn.bus.annotation.BusMethod;
import org.alljoyn.bus.annotation.BusProperty;
import org.alljoyn.bus.annotation.Position;

@BusInterface(name = "org.allseen.LSF.ControllerService.SceneWithSceneElements")
public interface ControllerServiceSWSEBusInterface
{
    /*
    "   <property name="Version" type="u" access="read" />"
    */
    @BusProperty(signature="u")
    public int getVersion() throws BusException;

    public class SceneWithSceneElementsValues
    {
        @Position(0)
        public int responseCode;
        @Position(1)
        public String sceneWithSceneElementsID;
    }

/*
    "    <method name='CreateSceneWithSceneElements'>"
    "      <arg name='sceneElementIDs' type='as' direction='in'/>"
    "      <arg name='sceneName' type='s' direction='in'/>"
    "      <arg name='language' type='s' direction='in'/>"
    "      <arg name='responseCode' type='u' direction='out'/>"
    "      <arg name='sceneID' type='s' direction='out'/>"
    "    </method>"
 */

    @BusMethod(signature= "asss" , replySignature = "us")
    public SceneWithSceneElementsValues CreateSceneWithSceneElements(String[] sceneElementIDs, String sceneName, String language) throws BusException;

/*
    "    <method name='UpdateSceneWithSceneElements'>"
    "      <arg name='sceneID' type='s' direction='in'/>"
    "      <arg name='sceneElementIDs' type='as' direction='in'/>"
    "      <arg name='responseCode' type='u' direction='out'/>"
    "      <arg name='sceneID' type='s' direction='out'/>"
    "    </method>"
 */

    @BusMethod(signature= "sas" , replySignature = "us")
    public SceneWithSceneElementsValues UpdateSceneWithSceneElements(String sceneID, String[] SceneElementIDs) throws BusException;

/*
    "    <method name='GetSceneWithSceneElements'>"
    "      <arg name='sceneID' type='s' direction='in'/>"
    "      <arg name='responseCode' type='u' direction='out'/>"
    "      <arg name='sceneID' type='s' direction='out'/>"
    "      <arg name='sceneElementIDs' type='as' direction='out'/>"
    "    </method>"
 */

    public class GetSceneWithSceneElementsValues
    {
        @Position(0)
        public int responseCode;
        @Position(1)
        public String sceneID;
        @Position(2)
        public String[] sceneElementIDs;
    }

    @BusMethod(signature= "s" , replySignature = "usas")
    public GetSceneWithSceneElementsValues GetSceneWithSceneElements(String sceneID) throws BusException;

}