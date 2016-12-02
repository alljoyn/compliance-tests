/*******************************************************************************
 *   *  
 *     Copyright (c) 2016 Open Connectivity Foundation and AllJoyn Open
 *     Source Project Contributors and others.
 *     
 *     All rights reserved. This program and the accompanying materials are
 *     made available under the terms of the Apache License, Version 2.0
 *     which accompanies this distribution, and is available at
 *     http://www.apache.org/licenses/LICENSE-2.0

 *******************************************************************************/
package com.at4wireless.alljoyn.core.lightingcontroller;

import org.alljoyn.bus.BusException;
import org.alljoyn.bus.BusObject;
import org.alljoyn.bus.annotation.BusProperty;
import org.alljoyn.bus.annotation.BusMethod;
import org.alljoyn.bus.annotation.BusSignal;

public class ControllerServiceSceneBusObject implements BusObject, ControllerServiceSceneBusInterface
{
    @Override
    @BusProperty(signature="u")
    public int getVersion() throws BusException
    {
        return 0;
    }

    @Override
    @BusMethod(replySignature = "uas")
    public GetAllSceneIDsValues GetAllSceneIDs() throws BusException
    {
        return null;
    }

    @Override
    @BusMethod(signature = "ss", replySignature = "usss")
    public GetSceneNameValues GetSceneName(String sceneID, String language) throws BusException
    {
        return null;
    }

    @Override
    @BusMethod(signature = "sss", replySignature = "uss")
    public SetSceneNameValues SetSceneName(String sceneID, String sceneName, String language) throws BusException
    {
        return null;
    }

    @Override
    @BusMethod(signature = "a(asasa{sv}u)a(asassu)a(asasa{sv}a{sv}uuu)a(asasssuuu)ss", replySignature = "us")
    public SceneValues CreateScene(CreateSceneTransitionlampsLampGroupsToState[] transitionlampsLampGroupsToState, CreateSceneTransitionlampsLampGroupsToPreset[] transitionlampsLampGroupsToPreset, CreateScenePulselampsLampGroupsWithState[] pulselampsLampGroupsWithState, CreateScenePulselampsLampGroupsWithPreset[] pulselampsLampGroupsWithPreset, String sceneName, String language) throws BusException
    {
        return null;
    }

    @Override
    @BusMethod(signature = "sa(asasa{sv}u)a(asassu)a(asasa{sv}a{sv}uuu)a(asasssuuu)", replySignature = "us")
    public SceneValues UpdateScene(String sceneID, CreateSceneTransitionlampsLampGroupsToState[] transitionlampsLampGroupsToState, CreateSceneTransitionlampsLampGroupsToPreset[] transitionlampsLampGroupsToPreset, CreateScenePulselampsLampGroupsWithState[] pulselampsLampGroupsWithState, CreateScenePulselampsLampGroupsWithPreset[] pulselampsLampGroupsWithPreset) throws BusException
    {
        return null;
    }

    @Override
    @BusMethod(signature = "s", replySignature = "us")
    public SceneValues DeleteScene(String sceneID) throws BusException
    {
        return null;
    }

    @Override
    @BusMethod(signature = "s", replySignature = "usa(asasa{sv}u)a(asassu)a(asasa{sv}a{sv}uuu)a(asasssuuu)")
    public GetSceneValues GetScene(String sceneID) throws BusException
    {
        return null;
    }

    @Override
    @BusMethod(signature = "s", replySignature = "us")
    public SceneValues ApplyScene(String sceneID) throws BusException
    {
        return null;
    }

    @Override
    @BusSignal(signature = "as")
    public void ScenesNameChanged(String[] sceneIDs) throws BusException
    {
    }

    @Override
    @BusSignal(signature = "as")
    public void ScenesCreated(String[] sceneIDs) throws BusException
    {
    }

    @Override
    @BusSignal(signature = "as")
    public void ScenesUpdated(String[] sceneIDs) throws BusException
    {
    }

    @Override
    @BusSignal(signature = "as")
    public void ScenesDeleted(String[] sceneIDs) throws BusException
    {
    }

    @Override
    @BusSignal(signature = "as")
    public void ScenesApplied(String[] sceneIDs) throws BusException
    {
    }
}