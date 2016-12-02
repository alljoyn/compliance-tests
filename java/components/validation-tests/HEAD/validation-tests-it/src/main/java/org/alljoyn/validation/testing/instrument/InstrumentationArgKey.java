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
package org.alljoyn.validation.testing.instrument;

public enum InstrumentationArgKey
{
    AppId("appId"), DeviceId("deviceId"), TestSuiteList("testSuiteList"), TestCaseName("testCaseName"), UserInputTimeoutValueInMS("userInputTimeoutValueInMS"), SoftAPSsid(
            "org.alljoyn.Onboarding.SoftApSsid"), SoftAPSecurity("org.alljoyn.Onboarding.SoftApSecurity"), SoftAPPassphrase("org.alljoyn.Onboarding.SoftApPassphrase"), PersonalAPSsid(
            "org.alljoyn.Onboarding.PersonalApSsid"), PersonalAPSecurity("org.alljoyn.Onboarding.PersonalApSecurity"), PersonalAPPassphrase(
            "org.alljoyn.Onboarding.PersonalApPassphrase"), EnableInteractive("enableInteractive"), AudioStreamObjectPath("audioStreamObjectPath"), AboutAnnouncementTimeoutInSeconds(
            "aboutAnnouncementTimeoutInSeconds");

    private String value;

    InstrumentationArgKey(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}