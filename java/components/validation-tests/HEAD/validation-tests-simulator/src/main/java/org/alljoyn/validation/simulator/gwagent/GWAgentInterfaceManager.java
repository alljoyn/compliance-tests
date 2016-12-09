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
package org.alljoyn.validation.simulator.gwagent;

import org.alljoyn.bus.BusAttachment;
import org.alljoyn.bus.BusObject;
import org.alljoyn.bus.Status;
import org.alljoyn.gatewaycontroller.sdk.managerinterfaces.ApplicationManagement;
import org.alljoyn.services.common.BusObjectDescription;

import android.util.Log;

public class GWAgentInterfaceManager
{
    private static final String GWAGENT_APPMGMT_PATH = "/gw";

    private static String TAG = "GwAgentInterfaceManager";

    private BusAttachment busAttachment;
    private ApplicationManagementBusObject appMgmtBusObject;
    private ApplicationBusObject appBusObject;
    private AccessControlListIfaceBusObject aclBusObject;

    public GWAgentInterfaceManager(BusAttachment busAttachment)
    {
        this.busAttachment = busAttachment;
        appMgmtBusObject = new ApplicationManagementBusObject();
        appBusObject = new ApplicationBusObject();
        aclBusObject = new AccessControlListIfaceBusObject();
    }

    public void registerBusObjects()
    {
        registerBusObject(appMgmtBusObject, GWAGENT_APPMGMT_PATH);
        registerBusObject(appBusObject, GWAGENT_APPMGMT_PATH + "/connectorApp1");
        registerBusObject(aclBusObject, GWAGENT_APPMGMT_PATH + "/connectorApp1/acl");

    }

    public void unregisterBusObjects()
    {
        busAttachment.unregisterBusObject(appMgmtBusObject);
        busAttachment.unregisterBusObject(appBusObject);
        busAttachment.unregisterBusObject(aclBusObject);
    }

    private void registerBusObject(BusObject busObject, String path)
    {
        Status status = busAttachment.registerBusObject(busObject, path);
        checkStatus("registerBusObject " + busObject.getClass().getName(), status);
    }

    private void checkStatus(String message, Status status)
    {
        if (!status.equals(Status.OK))
        {
            Log.e(TAG, message + " returned status: " + status);
            throw new RuntimeException(status.toString());
        }
    }

    public BusObjectDescription getBusObjectDescriptionToBeAnnounced()
    {
        BusObjectDescription busObjectDescription = new BusObjectDescription();
        busObjectDescription.setInterfaces(new String[]
        { ApplicationManagement.IFNAME });
        busObjectDescription.setPath(GWAGENT_APPMGMT_PATH);

        return busObjectDescription;
    }

}