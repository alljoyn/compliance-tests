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
 *      THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL
 *      WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED
 *      WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE
 *      AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL
 *      DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR
 *      PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER
 *      TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 *      PERFORMANCE OF THIS SOFTWARE.
 *******************************************************************************/
package org.alljoyn.validation.simulator.controlpanel;

import org.alljoyn.bus.BusAttachment;
import org.alljoyn.bus.BusObject;
import org.alljoyn.bus.Status;
import org.alljoyn.ioe.controlpanelservice.communication.interfaces.ControlPanel;
import org.alljoyn.services.common.BusObjectDescription;

import android.util.Log;

public class ControlPanelInterfaceManager
{
    private static String TAG = "ControlPanelInterfaceManager";
    private static final String HTTP_CONTROL_PATH = "/ControlPanel/unit/HTTPControl";
    private static final String NOTIFICATION_PANEL_PATH = "/ControlPanel/unit/notification";
    private static final String CONTROL_PANEL_PATH = "/ControlPanel/unit/mainPanel";
    private BusAttachment busAttachment;
    private ActionBusObject actionBusObject;
    private ActionSecuredBusObject actionSecuredBusObject;
    private ContainerBusObject containerBusObject;
    private ContainerSecuredBusObject containerSecuredBusObject;
    private ContainerBusObject notificationActionContainerBusObject;
    private ControlPanelBusObject controlPanelBusObject;
    private DialogBusObject dialogBusObject;
    private DialogSecuredBusObject dialogSecuredBusObject;
    private HttpControlBusObject httpControlBusObject;
    private LabelPropertyBusObject labelPropertyBusObject;
    private ListPropertyBusObject listPropertyBusObject;
    private ListPropertySecuredBusObject listPropertySecuredBusObject;
    private NotificationActionBusObject notificationActionBusObject;
    private PropertyBusObject propertyBusObject;
    private PropertySecuredBusObject propertySecuredBusObject;

    public ControlPanelInterfaceManager(BusAttachment busAttachment)
    {
        this.busAttachment = busAttachment;
        instantiateUnsecuredBusObjects();
        instantiateSecuredBusObjects();
    }

    public BusObjectDescription getBusObjectDescriptionToBeAnnounced()
    {
        BusObjectDescription busObjectDescription = new BusObjectDescription();
        busObjectDescription.setInterfaces(new String[]
        { ControlPanel.IFNAME });
        busObjectDescription.setPath(CONTROL_PANEL_PATH);

        return busObjectDescription;
    }

    public void registerBusObjects()
    {
        registerUnsecuredBusObjects();
        registerSecuredBusObjects();
    }

    public void unregisterBusObjects()
    {
        unregisterUnsecuredBusObjects();
        unregisterSecuredBusObjects();
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

    private void instantiateUnsecuredBusObjects()
    {
        actionBusObject = new ActionBusObject();
        containerBusObject = new ContainerBusObject();
        notificationActionContainerBusObject = new ContainerBusObject();
        controlPanelBusObject = new ControlPanelBusObject();
        dialogBusObject = new DialogBusObject();
        httpControlBusObject = new HttpControlBusObject();
        labelPropertyBusObject = new LabelPropertyBusObject();
        listPropertyBusObject = new ListPropertyBusObject();
        notificationActionBusObject = new NotificationActionBusObject();
        propertyBusObject = new PropertyBusObject();
    }

    private void instantiateSecuredBusObjects()
    {
        actionSecuredBusObject = new ActionSecuredBusObject();
        containerSecuredBusObject = new ContainerSecuredBusObject();
        dialogSecuredBusObject = new DialogSecuredBusObject();
        listPropertySecuredBusObject = new ListPropertySecuredBusObject();
        propertySecuredBusObject = new PropertySecuredBusObject();
    }

    private void registerUnsecuredBusObjects()
    {
        registerBusObject(controlPanelBusObject, CONTROL_PANEL_PATH);
        registerBusObject(containerBusObject, CONTROL_PANEL_PATH + "/en");
        registerBusObject(actionBusObject, CONTROL_PANEL_PATH + "/en/action");
        registerBusObject(dialogBusObject, CONTROL_PANEL_PATH + "/en/action/dialog");
        registerBusObject(labelPropertyBusObject, CONTROL_PANEL_PATH + "/en/label");
        registerBusObject(listPropertyBusObject, CONTROL_PANEL_PATH + "/en/list");
        registerBusObject(propertyBusObject, CONTROL_PANEL_PATH + "/en/property");

        registerBusObject(httpControlBusObject, HTTP_CONTROL_PATH);
        registerBusObject(notificationActionBusObject, NOTIFICATION_PANEL_PATH);
        registerBusObject(notificationActionContainerBusObject, NOTIFICATION_PANEL_PATH + "/en");
    }

    private void registerSecuredBusObjects()
    {
        registerBusObject(containerSecuredBusObject, CONTROL_PANEL_PATH + "/en/list/securedcontainer");
        registerBusObject(actionSecuredBusObject, CONTROL_PANEL_PATH + "/en/list/securedcontainer/securedaction");
        registerBusObject(dialogSecuredBusObject, CONTROL_PANEL_PATH + "/en/list/securedcontainer/securedaction/secureddialog");
        registerBusObject(listPropertySecuredBusObject, CONTROL_PANEL_PATH + "/en/list/securedcontainer/securedlist");
        registerBusObject(propertySecuredBusObject, CONTROL_PANEL_PATH + "/en/list/securedcontainer/securedproperty");
    }

    private void unregisterUnsecuredBusObjects()
    {
        busAttachment.unregisterBusObject(actionBusObject);
        busAttachment.unregisterBusObject(containerBusObject);
        busAttachment.unregisterBusObject(notificationActionContainerBusObject);
        busAttachment.unregisterBusObject(controlPanelBusObject);
        busAttachment.unregisterBusObject(dialogBusObject);
        busAttachment.unregisterBusObject(httpControlBusObject);
        busAttachment.unregisterBusObject(labelPropertyBusObject);
        busAttachment.unregisterBusObject(listPropertyBusObject);
        busAttachment.unregisterBusObject(notificationActionBusObject);
        busAttachment.unregisterBusObject(propertyBusObject);
    }

    private void unregisterSecuredBusObjects()
    {
        busAttachment.unregisterBusObject(actionSecuredBusObject);
        busAttachment.unregisterBusObject(containerSecuredBusObject);
        busAttachment.unregisterBusObject(dialogSecuredBusObject);
        busAttachment.unregisterBusObject(listPropertySecuredBusObject);
        busAttachment.unregisterBusObject(propertySecuredBusObject);
    }
}