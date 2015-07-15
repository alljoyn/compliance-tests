/*******************************************************************************
 *  Copyright AllSeen Alliance. All rights reserved.
 *
 *     Permission to use, copy, modify, and/or distribute this software for any
 *     purpose with or without fee is hereby granted, provided that the above
 *     copyright notice and this permission notice appear in all copies.
 *
 *     THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 *     WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 *     MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 *     ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 *     WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 *     ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 *     OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 *******************************************************************************/
package com.at4wireless.alljoyn.core.lightingcontroller;

import org.alljoyn.bus.BusException;
import org.alljoyn.bus.BusObject;
import org.alljoyn.bus.annotation.BusProperty;
import org.alljoyn.bus.annotation.BusMethod;
import org.alljoyn.bus.annotation.BusSignal;

public class ControllerServiceMasterSceneBusObject implements BusObject, ControllerServiceMasterSceneBusInterface
{
    @Override
    @BusProperty(signature="u")
    public int getVersion() throws BusException
    {
        return 0;
    }

    @Override
    @BusMethod(replySignature = "uas")
    public GetAllMasterSceneIDsValues GetAllMasterSceneIDs() throws BusException
    {
        return null;
    }

    @Override
    @BusMethod(signature = "ss", replySignature = "usss")
    public GetMasterSceneNameValues GetMasterSceneName(String masterSceneID, String language) throws BusException
    {
        return null;
    }

    @Override
    @BusMethod(signature = "sss", replySignature = "uss")
    public SetMasterSceneNameValues SetMasterSceneName(String masterSceneID, String masterSceneName, String language) throws BusException
    {
        return null;
    }

    @Override
    @BusMethod(signature = "asss", replySignature = "us")
    public MasterSceneValues CreateMasterScene(String[] scenes, String masterSceneName, String language) throws BusException
    {
        return null;
    }

    @Override
    @BusMethod(signature = "sas", replySignature = "us")
    public MasterSceneValues UpdateMasterScene(String masterSceneID, String[] scenes) throws BusException
    {
        return null;
    }

    @Override
    @BusMethod(signature = "s", replySignature = "us")
    public MasterSceneValues DeleteMasterScene(String masterSceneID) throws BusException
    {
        return null;
    }

    @Override
    @BusMethod(signature = "s", replySignature = "usas")
    public GetMasterSceneValues GetMasterScene(String masterSceneID) throws BusException
    {
        return null;
    }

    @Override
    @BusMethod(signature = "s", replySignature = "us")
    public MasterSceneValues ApplyMasterScene(String masterSceneID) throws BusException
    {
        return null;
    }

    @Override
    @BusSignal()
    public void MasterScenesNameChanged(String[] masterSceneIDs) throws BusException
    {
    }

    @Override
    @BusSignal()
    public void MasterScenesCreated(String[] masterSceneIDs) throws BusException
    {
    }

    @Override
    @BusSignal()
    public void MasterScenesUpdated(String[] masterSceneIDs) throws BusException
    {
    }

    @Override
    @BusSignal()
    public void MasterScenesDeleted(String[] masterSceneIDs) throws BusException
    {
    }

    @Override
    @BusSignal()
    public void MasterScenesApplied(String[] masterSceneIDs) throws BusException
    {
    }
}
