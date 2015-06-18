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
 *    THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL
 *    WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED
 *    WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE
 *    AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL
 *    DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR
 *    PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER
 *    TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 *    PERFORMANCE OF THIS SOFTWARE.
 */
package com.at4wireless.alljoyn.core.lightingcontroller;

import org.alljoyn.bus.BusException;
import org.alljoyn.bus.BusObject;
import org.alljoyn.bus.annotation.BusProperty;
import org.alljoyn.bus.annotation.BusMethod;
import org.alljoyn.bus.annotation.BusSignal;

// TODO: Auto-generated Javadoc
/**
 * The Class ControllerServiceSceneBusObject.
 */
public class ControllerServiceSceneBusObject implements BusObject, ControllerServiceSceneBusInterface
{
    
    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceSceneBusInterface#getVersion()
     */
    @Override
    @BusProperty(signature="u")
    public int getVersion() throws BusException
    {
        return 0;
    }

    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceSceneBusInterface#GetAllSceneIDs()
     */
    @Override
    @BusMethod(replySignature = "uas")
    public GetAllSceneIDsValues GetAllSceneIDs() throws BusException
    {
        return null;
    }

    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceSceneBusInterface#GetSceneName(java.lang.String, java.lang.String)
     */
    @Override
    @BusMethod(signature = "ss", replySignature = "usss")
    public GetSceneNameValues GetSceneName(String sceneID, String language) throws BusException
    {
        return null;
    }

    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceSceneBusInterface#SetSceneName(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    @BusMethod(signature = "sss", replySignature = "uss")
    public SetSceneNameValues SetSceneName(String sceneID, String sceneName, String language) throws BusException
    {
        return null;
    }

    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceSceneBusInterface#CreateScene(com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceSceneBusInterface.CreateSceneTransitionlampsLampGroupsToState[], com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceSceneBusInterface.CreateSceneTransitionlampsLampGroupsToPreset[], com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceSceneBusInterface.CreateScenePulselampsLampGroupsWithState[], com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceSceneBusInterface.CreateScenePulselampsLampGroupsWithPreset[], java.lang.String, java.lang.String)
     */
    @Override
    @BusMethod(signature = "a(asasa{sv}u)a(asassu)a(asasa{sv}a{sv}uuu)a(asasssuuu)ss", replySignature = "us")
    public SceneValues CreateScene(CreateSceneTransitionlampsLampGroupsToState[] transitionlampsLampGroupsToState, CreateSceneTransitionlampsLampGroupsToPreset[] transitionlampsLampGroupsToPreset, CreateScenePulselampsLampGroupsWithState[] pulselampsLampGroupsWithState, CreateScenePulselampsLampGroupsWithPreset[] pulselampsLampGroupsWithPreset, String sceneName, String language) throws BusException
    {
        return null;
    }

    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceSceneBusInterface#UpdateScene(java.lang.String, com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceSceneBusInterface.CreateSceneTransitionlampsLampGroupsToState[], com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceSceneBusInterface.CreateSceneTransitionlampsLampGroupsToPreset[], com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceSceneBusInterface.CreateScenePulselampsLampGroupsWithState[], com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceSceneBusInterface.CreateScenePulselampsLampGroupsWithPreset[])
     */
    @Override
    @BusMethod(signature = "sa(asasa{sv}u)a(asassu)a(asasa{sv}a{sv}uuu)a(asasssuuu)", replySignature = "us")
    public SceneValues UpdateScene(String sceneID, CreateSceneTransitionlampsLampGroupsToState[] transitionlampsLampGroupsToState, CreateSceneTransitionlampsLampGroupsToPreset[] transitionlampsLampGroupsToPreset, CreateScenePulselampsLampGroupsWithState[] pulselampsLampGroupsWithState, CreateScenePulselampsLampGroupsWithPreset[] pulselampsLampGroupsWithPreset) throws BusException
    {
        return null;
    }

    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceSceneBusInterface#DeleteScene(java.lang.String)
     */
    @Override
    @BusMethod(signature = "s", replySignature = "us")
    public SceneValues DeleteScene(String sceneID) throws BusException
    {
        return null;
    }

    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceSceneBusInterface#GetScene(java.lang.String)
     */
    @Override
    @BusMethod(signature = "s", replySignature = "usa(asasa{sv}u)a(asassu)a(asasa{sv}a{sv}uuu)a(asasssuuu)")
    public GetSceneValues GetScene(String sceneID) throws BusException
    {
        return null;
    }

    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceSceneBusInterface#ApplyScene(java.lang.String)
     */
    @Override
    @BusMethod(signature = "s", replySignature = "us")
    public SceneValues ApplyScene(String sceneID) throws BusException
    {
        return null;
    }

    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceSceneBusInterface#ScenesNameChanged(java.lang.String[])
     */
    @Override
    @BusSignal(signature = "as")
    public void ScenesNameChanged(String[] sceneIDs) throws BusException
    {
    }

    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceSceneBusInterface#ScenesCreated(java.lang.String[])
     */
    @Override
    @BusSignal(signature = "as")
    public void ScenesCreated(String[] sceneIDs) throws BusException
    {
    }

    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceSceneBusInterface#ScenesUpdated(java.lang.String[])
     */
    @Override
    @BusSignal(signature = "as")
    public void ScenesUpdated(String[] sceneIDs) throws BusException
    {
    }

    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceSceneBusInterface#ScenesDeleted(java.lang.String[])
     */
    @Override
    @BusSignal(signature = "as")
    public void ScenesDeleted(String[] sceneIDs) throws BusException
    {
    }

    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceSceneBusInterface#ScenesApplied(java.lang.String[])
     */
    @Override
    @BusSignal(signature = "as")
    public void ScenesApplied(String[] sceneIDs) throws BusException
    {
    }
}