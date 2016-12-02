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
package org.alljoyn.validation.simulator;

import java.util.HashMap;
import java.util.Map;

import org.alljoyn.about.AboutKeys;

public class SimpleDeviceDetails extends DeviceDetails
{

    private String manufacturerName = "Manufacturer Name";
    private String appName = "DUTSimulator App";
    private String description = "Device description";

    public SimpleDeviceDetails()
    {
        this("Manufacturer Name", "DUTSimulator App", "Device description", defaultDeviceName);
    }

    public SimpleDeviceDetails(String manufacturerName, String appName, String description, String deviceName)
    {
        this.manufacturerName = manufacturerName;
        this.appName = appName;
        this.description = description;
        defaultDeviceName = deviceName;

        setDefaultLanguage(defaultLang);
        setDeviceName(deviceName);

        Map<String, String> languageMap = new HashMap<String, String>();
        languageMap.put(AboutKeys.ABOUT_APP_NAME, appName);
        languageMap.put(AboutKeys.ABOUT_MANUFACTURER, manufacturerName);
        languageMap.put(AboutKeys.ABOUT_DESCRIPTION, description);

        addLanguageMap("en", languageMap);

    }

    public String getAppName()
    {
        return appName;
    }

    @Override
    public Map<String, Object> getAnnounceMap()
    {
        Map<String, Object> announceMap = super.getAnnounceMap();
        announceMap.put(AboutKeys.ABOUT_APP_NAME, appName);
        announceMap.put(AboutKeys.ABOUT_MANUFACTURER, manufacturerName);
        return announceMap;
    }

    @Override
    public Map<String, Object> getAbout(String languageTag)
    {
        Map<String, Object> aboutMap = super.getAbout(languageTag);

        if (aboutMap != null)
        {
            aboutMap.put(AboutKeys.ABOUT_APP_NAME, appName);
            aboutMap.put(AboutKeys.ABOUT_MANUFACTURER, manufacturerName);
            aboutMap.put(AboutKeys.ABOUT_DESCRIPTION, description);
        }
        return aboutMap;
    }

}