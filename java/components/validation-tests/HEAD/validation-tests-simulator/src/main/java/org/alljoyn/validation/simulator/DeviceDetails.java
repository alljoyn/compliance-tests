/*******************************************************************************
 *  Copyright (c) 2013 - 2014, AllSeen Alliance. All rights reserved.
 *
 *     Permission to use, copy, modify, and/or distribute this software for any
 *     purpose with or without fee is hereby granted, provided that the above
 *     copyright notice and this permission notice appear in all copies.
 *
 *     THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 *     WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 *     MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 *     ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 *     WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 *     ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 *     OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 *******************************************************************************/
package org.alljoyn.validation.simulator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import org.alljoyn.about.AboutKeys;
import org.alljoyn.services.common.PropertyStoreException;
import org.alljoyn.validation.simulator.config.PropertyStoreExtendedException;

public class DeviceDetails
{
    private static short ABOUT_KEY_MAXLENGTH = 35;
    private UUID appId;
    public String defaultLanguage;
    private String deviceName;
    private String deviceId;

    private String modelNumber;
    private String dateOfManfucturer;
    private String softwareVersion;
    private String ajSoftwareVersion;
    private String hardwareVersion;
    private String supportUrl;
    Map<String, Map<String, String>> localizedValues = new HashMap<String, Map<String, String>>();
    Set<String> supportedLanguages = new HashSet<String>();
    public static String defaultDeviceName = "DUTSimulator";
    public static String defaultLang = "en";

    public DeviceDetails()
    {
        appId = UUID.randomUUID();
        deviceId = "dutSimulator_" + System.currentTimeMillis();

        modelNumber = "AB-123456";
        dateOfManfucturer = "2013-08-01";
        softwareVersion = "1.0.0.0";
        ajSoftwareVersion = "3.3.2";
        hardwareVersion = "hw-1234.10";
        supportUrl = "http://www.example.com/supportUrl";
    }

    public void addLanguageMap(String language, Map<String, String> fieldValues)
    {
        localizedValues.put(language, fieldValues);
        supportedLanguages.add(language);
    }

    public UUID getAppId()
    {
        return appId;
    }

    public void setAppId(UUID appId)
    {
        this.appId = appId;
    }

    public String getDefaultLanguage()
    {
        return defaultLanguage;
    }

    public void setDefaultLanguage(String defaultLanguage)
    {
        this.defaultLanguage = defaultLanguage;
    }

    public String getDeviceName()
    {
        return deviceName;
    }

    public void setDeviceName(String deviceName)
    {
        this.deviceName = deviceName;
    }

    public String getDeviceId()
    {
        return deviceId;
    }

    public void setDeviceId(String deviceId)
    {
        this.deviceId = deviceId;
    }

    public String getModelNumber()
    {
        return modelNumber;
    }

    public void setModelNumber(String modelNumber)
    {
        this.modelNumber = modelNumber;
    }

    public String getDateOfManfucturer()
    {
        return dateOfManfucturer;
    }

    public void setDateOfManfucturer(String dateOfManfucturer)
    {
        this.dateOfManfucturer = dateOfManfucturer;
    }

    public String getSoftwareVersion()
    {
        return softwareVersion;
    }

    public void setSoftwareVersion(String softwareVersion)
    {
        this.softwareVersion = softwareVersion;
    }

    public String getAjSoftwareVersion()
    {
        return ajSoftwareVersion;
    }

    public void setAjSoftwareVersion(String ajSoftwareVersion)
    {
        this.ajSoftwareVersion = ajSoftwareVersion;
    }

    public String getHardwareVersion()
    {
        return hardwareVersion;
    }

    public void setHardwareVersion(String hardwareVersion)
    {
        this.hardwareVersion = hardwareVersion;
    }

    public String getSupportUrl()
    {
        return supportUrl;
    }

    public void setSupportUrl(String supportUrl)
    {
        this.supportUrl = supportUrl;
    }

    public Map<String, Object> getAnnounceMap()
    {
        Map<String, Object> announceMap = new HashMap<String, Object>();

        announceMap.put(AboutKeys.ABOUT_APP_ID, getAppId());
        announceMap.put(AboutKeys.ABOUT_DEFAULT_LANGUAGE, getDefaultLanguage());
        announceMap.put(AboutKeys.ABOUT_DEVICE_NAME, getDeviceName());
        announceMap.put(AboutKeys.ABOUT_DEVICE_ID, getDeviceId());
        announceMap.put(AboutKeys.ABOUT_MODEL_NUMBER, getModelNumber());
        return announceMap;
    }

    public Map<String, Object> getAbout(String languageTag)
    {
        if ("".equals(languageTag))
        {
            languageTag = getDefaultLanguage();
        }

        if (!supportedLanguages.contains(languageTag))
        {
            return null;
        }

        Map<String, Object> aboutMap = getAnnounceMap();

        aboutMap.put(AboutKeys.ABOUT_SOFTWARE_VERSION, getSoftwareVersion());
        aboutMap.put(AboutKeys.ABOUT_AJ_SOFTWARE_VERSION, getAjSoftwareVersion());
        aboutMap.put(AboutKeys.ABOUT_HARDWARE_VERSION, getHardwareVersion());
        aboutMap.put(AboutKeys.ABOUT_SUPPORT_URL, getSupportUrl());
        aboutMap.put(AboutKeys.ABOUT_SUPPORTED_LANGUAGES, supportedLanguages);
        aboutMap.put(AboutKeys.ABOUT_DATE_OF_MANUFACTURE, getDateOfManfucturer());
        aboutMap.put("MaxLength", ABOUT_KEY_MAXLENGTH);

        Map<String, String> localizedMap = localizedValues.get(languageTag);
        if (localizedMap != null)
        {
            aboutMap.putAll(localizedMap);
        }

        return aboutMap;
    }

    public Map<String, Object> getConfigMapObject(String languageTag) throws PropertyStoreException
    {
        Map<String, Object> configMap = new HashMap<String, Object>();
        if ("".equals(languageTag))
        {
            languageTag = getDefaultLanguage();
        }

        if (supportedLanguages.contains(languageTag))
        {
            configMap.put(AboutKeys.ABOUT_DEFAULT_LANGUAGE, getDefaultLanguage());
            configMap.put(AboutKeys.ABOUT_DEVICE_NAME, getDeviceName());

            Map<String, String> localizedMap = localizedValues.get(languageTag);
            if (localizedMap != null)
            {
                configMap.putAll(localizedMap);
            }
        }
        else
        {
            throw new PropertyStoreException(PropertyStoreException.UNSUPPORTED_LANGUAGE);
        }

        return configMap;
    }

    public void updateConfig(String languageTag, Entry<String, Object> entry) throws PropertyStoreException
    {
        if ("".equals(languageTag))
        {
            languageTag = getDefaultLanguage();
        }

        if (supportedLanguages.contains(languageTag))
        {

            if (entry.getKey().equals(AboutKeys.ABOUT_DEFAULT_LANGUAGE))
            {
                if (entry.getValue().equals("") || !supportedLanguages.contains(entry.getValue()))
                {
                    throw new PropertyStoreException(PropertyStoreException.UNSUPPORTED_LANGUAGE);
                }
                else
                {

                    setDefaultLanguage((String) entry.getValue());
                }
            }

            else if (entry.getKey().equals(AboutKeys.ABOUT_DEVICE_NAME))
            {
                if (entry.getValue().equals(""))
                {
                    throw new PropertyStoreException(PropertyStoreException.INVALID_VALUE);
                }
                else
                {
                    String aboutDeviceName = (String) entry.getValue();
                    if (aboutDeviceName.length() <= ABOUT_KEY_MAXLENGTH)
                    {
                        setDeviceName((String) entry.getValue());
                    }
                    else
                    {
                        throw new PropertyStoreExtendedException(PropertyStoreExtendedException.MAX_SIZE_EXCEEDED);
                    }
                }
            }
            else if (entry.getKey().equals(AboutKeys.ABOUT_DEVICE_ID))
            {
                throw new PropertyStoreException(PropertyStoreException.ILLEGAL_ACCESS);
            }
            else
            {
                throw new PropertyStoreException(PropertyStoreException.INVALID_VALUE);
            }
        }
        else
        {
            throw new PropertyStoreException(PropertyStoreException.UNSUPPORTED_LANGUAGE);
        }
    }

    public void resetConfiguration(String language, String fieldToReset) throws PropertyStoreException
    {
        if ("".equals(language))
        {
            language = getDefaultLanguage();
        }

        if (supportedLanguages.contains(language))
        {

            if (fieldToReset.equals(AboutKeys.ABOUT_DEVICE_NAME))
            {
                setDeviceName(defaultDeviceName);
            }
            else if (fieldToReset.equals(AboutKeys.ABOUT_DEFAULT_LANGUAGE))
            {
                setDefaultLanguage(defaultLang);
            }
            else if (fieldToReset.equals(AboutKeys.ABOUT_DEVICE_ID))
            {
                throw new PropertyStoreException(PropertyStoreException.ILLEGAL_ACCESS);
            }
            else
            {
                throw new PropertyStoreException(PropertyStoreException.INVALID_VALUE);
            }
        }
        else
        {
            throw new PropertyStoreException(PropertyStoreException.UNSUPPORTED_LANGUAGE);
        }
    }

    public void factoryReset()
    {
        setDeviceName(defaultDeviceName);

        setDefaultLanguage(defaultLang);
    }
}