/*
 *  * 
 *    Copyright (c) 2016 Open Connectivity Foundation and AllJoyn Open
 *    Source Project Contributors and others.
 *    
 *    All rights reserved. This program and the accompanying materials are
 *    made available under the terms of the Apache License, Version 2.0
 *    which accompanies this distribution, and is available at
 *    http://www.apache.org/licenses/LICENSE-2.0

 */
package com.at4wireless.alljoyn.core.lightingcontroller;

import org.alljoyn.bus.BusException;
import org.alljoyn.bus.BusObject;
import org.alljoyn.bus.annotation.BusProperty;
import org.alljoyn.bus.annotation.BusMethod;
import org.alljoyn.bus.annotation.BusSignal;

// TODO: Auto-generated Javadoc
/**
 * The Class ControllerServiceMasterSceneBusObject.
 */
public class ControllerServiceMasterSceneBusObject implements BusObject, ControllerServiceMasterSceneBusInterface
{
    
    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceMasterSceneBusInterface#getVersion()
     */
    @Override
    @BusProperty(signature="u")
    public int getVersion() throws BusException
    {
        return 0;
    }

    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceMasterSceneBusInterface#GetAllMasterSceneIDs()
     */
    @Override
    @BusMethod(replySignature = "uas")
    public GetAllMasterSceneIDsValues GetAllMasterSceneIDs() throws BusException
    {
        return null;
    }

    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceMasterSceneBusInterface#GetMasterSceneName(java.lang.String, java.lang.String)
     */
    @Override
    @BusMethod(signature = "ss", replySignature = "usss")
    public GetMasterSceneNameValues GetMasterSceneName(String masterSceneID, String language) throws BusException
    {
        return null;
    }

    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceMasterSceneBusInterface#SetMasterSceneName(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    @BusMethod(signature = "sss", replySignature = "uss")
    public SetMasterSceneNameValues SetMasterSceneName(String masterSceneID, String masterSceneName, String language) throws BusException
    {
        return null;
    }

    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceMasterSceneBusInterface#CreateMasterScene(java.lang.String[], java.lang.String, java.lang.String)
     */
    @Override
    @BusMethod(signature = "asss", replySignature = "us")
    public MasterSceneValues CreateMasterScene(String[] scenes, String masterSceneName, String language) throws BusException
    {
        return null;
    }

    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceMasterSceneBusInterface#UpdateMasterScene(java.lang.String, java.lang.String[])
     */
    @Override
    @BusMethod(signature = "sas", replySignature = "us")
    public MasterSceneValues UpdateMasterScene(String masterSceneID, String[] scenes) throws BusException
    {
        return null;
    }

    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceMasterSceneBusInterface#DeleteMasterScene(java.lang.String)
     */
    @Override
    @BusMethod(signature = "s", replySignature = "us")
    public MasterSceneValues DeleteMasterScene(String masterSceneID) throws BusException
    {
        return null;
    }

    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceMasterSceneBusInterface#GetMasterScene(java.lang.String)
     */
    @Override
    @BusMethod(signature = "s", replySignature = "usas")
    public GetMasterSceneValues GetMasterScene(String masterSceneID) throws BusException
    {
        return null;
    }

    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceMasterSceneBusInterface#ApplyMasterScene(java.lang.String)
     */
    @Override
    @BusMethod(signature = "s", replySignature = "us")
    public MasterSceneValues ApplyMasterScene(String masterSceneID) throws BusException
    {
        return null;
    }

    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceMasterSceneBusInterface#MasterScenesNameChanged(java.lang.String[])
     */
    @Override
    @BusSignal()
    public void MasterScenesNameChanged(String[] masterSceneIDs) throws BusException
    {
    }

    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceMasterSceneBusInterface#MasterScenesCreated(java.lang.String[])
     */
    @Override
    @BusSignal()
    public void MasterScenesCreated(String[] masterSceneIDs) throws BusException
    {
    }

    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceMasterSceneBusInterface#MasterScenesUpdated(java.lang.String[])
     */
    @Override
    @BusSignal()
    public void MasterScenesUpdated(String[] masterSceneIDs) throws BusException
    {
    }

    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceMasterSceneBusInterface#MasterScenesDeleted(java.lang.String[])
     */
    @Override
    @BusSignal()
    public void MasterScenesDeleted(String[] masterSceneIDs) throws BusException
    {
    }

    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.lightingcontroller.ControllerServiceMasterSceneBusInterface#MasterScenesApplied(java.lang.String[])
     */
    @Override
    @BusSignal()
    public void MasterScenesApplied(String[] masterSceneIDs) throws BusException
    {
    }
}