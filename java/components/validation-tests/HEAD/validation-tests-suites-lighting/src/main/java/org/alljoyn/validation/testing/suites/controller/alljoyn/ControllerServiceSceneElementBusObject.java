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
import org.alljoyn.bus.BusObject;
import org.alljoyn.bus.annotation.BusMethod;
import org.alljoyn.bus.annotation.BusSignal;
import org.alljoyn.bus.annotation.BusProperty;

public class ControllerServiceSceneElementBusObject  implements BusObject, ControllerServiceSceneElementBusInterface
{
    @Override
    @BusProperty(signature="u")
    public int getVersion() throws BusException
    {
        return 0;
    }

    @Override
    @BusMethod(replySignature = "uas")
    public GetAllSceneElementIDsValues GetAllSceneElementIDs() throws BusException
    {
        return null;
    }

    @Override
    @BusMethod(signature = "ss", replySignature = "usss")
    public GetSceneElementNameValues GetSceneElementName(String SceneElementID, String language) throws BusException
    {
        return null;
    }

    @Override
    @BusMethod(signature = "sss", replySignature = "uss")
    public SetSceneElementNameValues SetSceneElementName(String SceneElementID, String SceneElementName, String language) throws BusException
    {
        return null;
    }

    @Override
    @BusMethod(signature = "sasasss", replySignature = "us")
    public SceneElementValues CreateSceneElement(String[] lampList, String[] lampGroupList, String effectID, String SceneElementName, String language) throws BusException
    {
        return null;
    }

    @Override
    @BusMethod(signature = "sasass", replySignature = "us")
    public SceneElementValues UpdateSceneElement(String sceneElementID, String[] lampList, String[] lampGroupList, String effectID) throws BusException
    {
        return null;
    }

    @Override
    @BusMethod(signature = "s", replySignature = "us")
    public SceneElementValues DeleteSceneElement(String SceneElementID) throws BusException
    {
        return null;
    }

    @Override
    @BusMethod(signature = "s", replySignature = "usasass")
    public GetSceneElementValues GetSceneElement(String SceneElementID) throws BusException
    {
        return null;
    }

    @Override
    @BusMethod(signature = "sas", replySignature = "us")
    public SceneElementValues ApplySceneElement(String SceneElementIDs) throws BusException
    {
        return null;
    }

    @BusSignal()
    public void SceneElementsNameChanged(String[] SceneElementIDs) throws BusException
    {
    }

    @BusSignal()
    public void SceneElementsCreated(String[] SceneElementIDs) throws BusException
    {
    }

    @BusSignal()
    public void SceneElementsUpdated(String[] SceneElementIDs) throws BusException
    {
    }

    @BusSignal()
    public void SceneElementsDeleted(String[] SceneElementIDs) throws BusException
    {
    }

    @BusSignal()
    public void SceneElementsApplied(String[] SceneElementIDs) throws BusException
    {
    }
}