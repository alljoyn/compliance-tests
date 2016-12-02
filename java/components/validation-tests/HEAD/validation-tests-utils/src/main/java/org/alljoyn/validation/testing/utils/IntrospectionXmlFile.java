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
package org.alljoyn.validation.testing.utils;

public enum IntrospectionXmlFile
{
    About("introspection-xml/About.xml"), Introspectable("introspection-xml/Introspectable.xml"), Action("introspection-xml/Action.xml"), Audio("introspection-xml/Audio.xml"), Config(
            "introspection-xml/Config.xml"), Container("introspection-xml/Container.xml"), ControlPanel("introspection-xml/ControlPanel.xml"), DeviceIcon(
            "introspection-xml/DeviceIcon.xml"), Dialog("introspection-xml/Dialog.xml"), HTTPControl("introspection-xml/HTTPControl.xml"), LabelProperty(
            "introspection-xml/LabelProperty.xml"), ListProperty("introspection-xml/ListProperty.xml"), Notification("introspection-xml/Notification.xml"), NotificationAction(
            "introspection-xml/NotificationAction.xml"), Onboarding("introspection-xml/Onboarding.xml"), Peer("introspection-xml/Peer.xml"), Property(
            "introspection-xml/Property.xml"), Clock("introspection-xml/Clock.xml"), Alarm("introspection-xml/Alarm.xml"), AlarmFactory("introspection-xml/AlarmFactory.xml"), TimeAuthority(
            "introspection-xml/TimeAuthority.xml"), Timer("introspection-xml/Timer.xml"), TimerFactory("introspection-xml/TimerFactory.xml"), GWAgentCtrlAcl(
            "introspection-xml/GWAgentCtrlAcl.xml"), GWAgentCtrlAclMgmt("introspection-xml/GWAgentCtrlAclMgmt.xml"), GWAgentCtrlApp("introspection-xml/GWAgentCtrlApp.xml"), GWAgentCtrlAppMgmt(
            "introspection-xml/GWAgentCtrlAppMgmt.xml"), GWAgentConnectorApp("introspection-xml/GWAgentConnectorApp.xml");

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