/*
 * Copyright (c) 2016 Open Connectivity Foundation (OCF) and AllJoyn Open
 *    Source Project (AJOSP) Contributors and others.
 *
 *    SPDX-License-Identifier: Apache-2.0
 *
 *    All rights reserved. This program and the accompanying materials are
 *    made available under the terms of the Apache License, Version 2.0
 *    which accompanies this distribution, and is available at
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Copyright 2016 Open Connectivity Foundation and Contributors to
 *    AllSeen Alliance. All rights reserved.
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
package com.at4wireless.alljoyn.core.interfacevalidator;


/**
 * The Enum IntrospectionXmlFile.
 */
public enum IntrospectionXmlFile 
{
    
    /** The About. */
    About("introspection-xml/About.xml"), 
 /** The Action. */
 Action("introspection-xml/Action.xml"), 
 /** The Audio. */
 Audio("introspection-xml/Audio.xml"), 
 /** The Config. */
 Config("introspection-xml/Config.xml"), 
 /** The Container. */
 Container(
            "introspection-xml/Container.xml"), 
 /** The Controller service. */
 ControllerService("introspection-xml/ControllerService.xml"), 
 /** The Control panel. */
 ControlPanel("introspection-xml/ControlPanel.xml"), 
 /** The Device icon. */
 DeviceIcon("introspection-xml/DeviceIcon.xml"), 
 /** The Dialog. */
 Dialog(
            "introspection-xml/Dialog.xml"),
/** The GW agent ctrl acl. */
GWAgentCtrlAcl("introspection-xml/GWAgentCtrlAcl.xml"), 
 /** The GW agent ctrl acl mgmt. */
 GWAgentCtrlAclMgmt("introspection-xml/GWAgentCtrlAclMgmt.xml"),
            
            /** The GW agent ctrl app. */
            GWAgentCtrlApp("introspection-xml/GWAgentCtrlApp.xml"),
/** The GW agent ctrl app mgmt. */
GWAgentCtrlAppMgmt("introspection-xml/GWAgentCtrlAppMgmt.xml"),
            
            /** The HTTP control. */
            HTTPControl("introspection-xml/HTTPControl.xml"), 
 /** The Label property. */
 LabelProperty("introspection-xml/LabelProperty.xml"),
            
            /** The Lamp. */
            Lamp("introspection-xml/Lamp.xml"),
/** The Leader election and state sync. */
LeaderElectionAndStateSync("introspection-xml/LeaderElectionAndStateSync.xml"), 
            
            /** The List property. */
            ListProperty("introspection-xml/ListProperty.xml"), 
 /** The Notification. */
 Notification("introspection-xml/Notification.xml"), 
 /** The Notification action. */
 NotificationAction("introspection-xml/NotificationAction.xml"), 
 /** The Onboarding. */
 Onboarding(
            "introspection-xml/Onboarding.xml"), 
 /** The Peer. */
 Peer("introspection-xml/Peer.xml"), 
 /** The Property. */
 Property("introspection-xml/Property.xml");

    /** The value. */
    private String value;

    /**
     * Instantiates a new introspection xml file.
     *
     * @param value the value
     */
    IntrospectionXmlFile(String value)
    {
        this.value = value;
    }

    /**
     * Gets the value.
     *
     * @return the value
     */
    public String getValue()
    {
        return value;
    }
}