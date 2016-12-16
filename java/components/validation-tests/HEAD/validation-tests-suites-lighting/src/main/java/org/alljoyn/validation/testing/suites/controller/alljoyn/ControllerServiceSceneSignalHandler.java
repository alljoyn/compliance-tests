/*******************************************************************************
*     Copyright (c) Open Connectivity Foundation (OCF) and AllJoyn Open
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
*     THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL
*     WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED
*     WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE
*     AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL
*     DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR
*     PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER
*     TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
*     PERFORMANCE OF THIS SOFTWARE.
*******************************************************************************/
package org.alljoyn.validation.testing.suites.controller.alljoyn;

import org.alljoyn.bus.annotation.BusSignalHandler;
import org.alljoyn.validation.testing.suites.controller.ControllerServiceSignalListener;

public class ControllerServiceSceneSignalHandler
{
    static
    {
        System.loadLibrary("alljoyn_java");
    }

    private ControllerServiceSignalListener signalListener;

    public void setUpdateListener(ControllerServiceSignalListener listener)
    {
        signalListener = listener;
    }

    @BusSignalHandler(iface = "org.allseen.LSF.ControllerService.Scene", signal = "ScenesNameChanged")
    public void ScenesNameChanged(String[] sceneIDs)
    {
        if (signalListener != null)
        {
            signalListener.handleScenesNameChanged(sceneIDs);
        }
    }

    @BusSignalHandler(iface = "org.allseen.LSF.ControllerService.Scene", signal = "ScenesCreated")
    public void ScenesCreated(String[] sceneIDs)
    {
        if (signalListener != null)
        {
            signalListener.handleScenesCreated(sceneIDs);
        }
    }

    @BusSignalHandler(iface = "org.allseen.LSF.ControllerService.Scene", signal = "ScenesUpdated")
    public void ScenesUpdated(String[] sceneIDs)
    {
        if (signalListener != null)
        {
            signalListener.handleScenesUpdated(sceneIDs);
        }
    }

    @BusSignalHandler(iface = "org.allseen.LSF.ControllerService.Scene", signal = "ScenesDeleted")
    public void ScenesDeleted(String[] sceneIDs)
    {
        if (signalListener != null)
        {
            signalListener.handleScenesDeleted(sceneIDs);
        }
    }

    @BusSignalHandler(iface = "org.allseen.LSF.ControllerService.Scene", signal = "ScenesApplied")
    public void ScenesApplied(String[] sceneIDs)
    {
        if (signalListener != null)
        {
            signalListener.handleScenesApplied(sceneIDs);
        }
    }
}