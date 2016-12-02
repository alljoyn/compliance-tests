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
package org.alljoyn.validation.simulator.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alljoyn.about.AboutKeys;
import org.alljoyn.services.common.PropertyStore;
import org.alljoyn.services.common.PropertyStoreException;

public class ConfigPropertyStoreImpl implements PropertyStore
{
    Map<String, Object> configDataMap = new HashMap<String, Object>();
    List<String> supportedLanguages = new ArrayList<String>();
    String defaultLang = "en";

    public String getDefaultLang()
    {
        return defaultLang;
    }

    public void setDefaultLang(String defaultLang)
    {
        this.defaultLang = defaultLang;
    }

    public ConfigPropertyStoreImpl()
    {
        configDataMap.put(AboutKeys.ABOUT_DEFAULT_LANGUAGE, "en");
        configDataMap.put(AboutKeys.ABOUT_DEVICE_NAME, "DUTSimulator");
        supportedLanguages.add(getDefaultLang());
    }

    @Override
    public void readAll(String languageTag, Filter filter, Map<String, Object> dataMap) throws PropertyStoreException
    {
        if ("".equals(languageTag))
        {
            languageTag = getDefaultLang();
        }

        if (supportedLanguages.contains(languageTag))
        {
            dataMap.putAll(configDataMap);
        }
        else
        {
            throw new PropertyStoreException(PropertyStoreException.UNSUPPORTED_LANGUAGE);
        }

    }

    @Override
    public void update(String key, String languageTag, Object newValue) throws PropertyStoreException
    {
        if ("".equals(languageTag))
        {
            languageTag = getDefaultLang();
        }

        if (supportedLanguages.contains(languageTag))
        {
            configDataMap.remove(key);
            configDataMap.put(key, newValue);
        }
        else
        {
            throw new PropertyStoreException(PropertyStoreException.UNSUPPORTED_LANGUAGE);
        }
    }

    @Override
    public void reset(String key, String languageTag) throws PropertyStoreException
    {
    }

    @Override
    public void resetAll() throws PropertyStoreException
    {
    }
}