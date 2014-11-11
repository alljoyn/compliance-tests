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
package org.alljoyn.validation.testing.utils;

public enum IntrospectionXmlFile
{
    About("introspection-xml/About.xml"), Action("introspection-xml/Action.xml"), Audio("introspection-xml/Audio.xml"), Config("introspection-xml/Config.xml"), Container(
            "introspection-xml/Container.xml"), ControlPanel("introspection-xml/ControlPanel.xml"), DeviceIcon("introspection-xml/DeviceIcon.xml"), Dialog(
            "introspection-xml/Dialog.xml"), HTTPControl("introspection-xml/HTTPControl.xml"), LabelProperty("introspection-xml/LabelProperty.xml"), ListProperty(
            "introspection-xml/ListProperty.xml"), Notification("introspection-xml/Notification.xml"), NotificationAction("introspection-xml/NotificationAction.xml"), Onboarding(
            "introspection-xml/Onboarding.xml"), Peer("introspection-xml/Peer.xml"), Property("introspection-xml/Property.xml");

    private String value;

    IntrospectionXmlFile(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}