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
package org.alljoyn.validation.testing.utils.about;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import org.alljoyn.about.AboutKeys;
import org.alljoyn.bus.BusException;
import org.alljoyn.bus.Variant;
import org.alljoyn.services.common.BusObjectDescription;
import org.alljoyn.services.common.utils.TransportUtil;
import org.alljoyn.validation.framework.AboutAnnouncement;

public class AboutAnnouncementDetails extends AboutAnnouncement
{
    private Map<String, Object> aboutDataObjectMap = null;

    public AboutAnnouncementDetails(String serviceName, short port, BusObjectDescription[] objectDescriptions, Map<String, Variant> aboutData)
    {
        super(serviceName, port, objectDescriptions, aboutData);
    }

    public AboutAnnouncementDetails(AboutAnnouncement aboutAnnouncement)
    {
        this(aboutAnnouncement.getServiceName(), aboutAnnouncement.getPort(), aboutAnnouncement.getObjectDescriptions(), aboutAnnouncement.getAboutData());
    }

    public void convertAboutMap() throws BusException
    {
        BusException busException = null;
        if (getAboutData() == null)
        {
            throw new BusException("aboutData is null!");
        }
        aboutDataObjectMap = new HashMap<String, Object>(getAboutData().size());
        for (Entry<String, Variant> entry : getAboutData().entrySet())
        {
            try
            {
                String key = entry.getKey();
                Variant variant = entry.getValue();
                String signature = variant.getSignature();
                Object value = null;
                checkForCorrectType(key, signature);
                if ("ay".equals(signature))
                {
                    byte[] byteArray = variant.getObject(new byte[]
                    {}.getClass());
                    value = TransportUtil.byteArrayToUUID(byteArray);
                }
                else if ("as".equals(signature))
                {
                    value = variant.getObject(new String[]
                    {}.getClass());
                }
                else
                {
                    value = variant.getObject(Object.class);
                }
                aboutDataObjectMap.put(key, value);
            }
            catch (BusException e)
            {
                if (busException == null)
                {
                    busException = e;
                }
            }
        }
        if (busException != null)
        {
            throw busException;
        }
    }

    private static final Map<String, String> KEY_SIGNATURE_MAP = Collections.unmodifiableMap(new HashMap<String, String>()
    {
        private static final long serialVersionUID = 1L;
        {
            put(AboutKeys.ABOUT_DEVICE_ID, "s");
            put(AboutKeys.ABOUT_DEVICE_NAME, "s");
            put(AboutKeys.ABOUT_DEFAULT_LANGUAGE, "s");
            put(AboutKeys.ABOUT_APP_NAME, "s");
            put(AboutKeys.ABOUT_MANUFACTURER, "s");
            put(AboutKeys.ABOUT_MODEL_NUMBER, "s");
            put(AboutKeys.ABOUT_DESCRIPTION, "s");
            put(AboutKeys.ABOUT_DATE_OF_MANUFACTURE, "s");
            put(AboutKeys.ABOUT_SOFTWARE_VERSION, "s");
            put(AboutKeys.ABOUT_AJ_SOFTWARE_VERSION, "s");
            put(AboutKeys.ABOUT_HARDWARE_VERSION, "s");
            put(AboutKeys.ABOUT_SUPPORT_URL, "s");
            put(AboutKeys.ABOUT_APP_ID, "ay");
            put(AboutKeys.ABOUT_SUPPORTED_LANGUAGES, "as");
        }
    });

    private boolean checkForCorrectType(String key, String signature) throws BusException
    {
        String expectedSignature = KEY_SIGNATURE_MAP.get(key);
        if ((expectedSignature != null) && (!expectedSignature.equals(signature)))
        {
            throw new BusException(key + " has an incorrect signature: '" + signature + "' expected: '" + expectedSignature + "'");
        }
        return false;
    }

    public String getDeviceId()
    {
        return (String) aboutDataObjectMap.get(AboutKeys.ABOUT_DEVICE_ID);
    }

    public UUID getAppId()
    {
        return (UUID) aboutDataObjectMap.get(AboutKeys.ABOUT_APP_ID);
    }

    public String getDeviceName()
    {
        return (String) aboutDataObjectMap.get(AboutKeys.ABOUT_DEVICE_NAME);
    }

    public String getAppName()
    {
        return (String) aboutDataObjectMap.get(AboutKeys.ABOUT_APP_NAME);
    }

    public String getManufacturer()
    {
        return (String) aboutDataObjectMap.get(AboutKeys.ABOUT_MANUFACTURER);
    }

    public String getModel()
    {
        return (String) aboutDataObjectMap.get(AboutKeys.ABOUT_MODEL_NUMBER);
    }

    public String[] getSupportedLanguages()
    {
        return (String[]) aboutDataObjectMap.get(AboutKeys.ABOUT_SUPPORTED_LANGUAGES);
    }

    public String getDescription()
    {
        return (String) aboutDataObjectMap.get(AboutKeys.ABOUT_DESCRIPTION);
    }

    public String getManufactureDate()
    {
        return (String) aboutDataObjectMap.get(AboutKeys.ABOUT_DATE_OF_MANUFACTURE);
    }

    public String getSoftwareVersion()
    {
        return (String) aboutDataObjectMap.get(AboutKeys.ABOUT_SOFTWARE_VERSION);
    }

    public String getAjSoftwareVersion()
    {
        return (String) aboutDataObjectMap.get(AboutKeys.ABOUT_AJ_SOFTWARE_VERSION);
    }

    public String getHardwareVersion()
    {
        return (String) aboutDataObjectMap.get(AboutKeys.ABOUT_HARDWARE_VERSION);
    }

    public String getSupportUrl()
    {
        return (String) aboutDataObjectMap.get(AboutKeys.ABOUT_SUPPORT_URL);
    }

    public boolean supportsInterface(String interfaceName)
    {
        for (BusObjectDescription busObjDesc : getObjectDescriptions())
        {
            for (String intf : busObjDesc.interfaces)
            {
                if (intf.equals(interfaceName))
                {
                    return true;
                }
            }
        }
        return false;
    }

    public Set<String> getObjectPaths(String interfaceName)
    {
        Set<String> objectPaths = new HashSet<String>();

        for (BusObjectDescription busObjectDescription : getObjectDescriptions())
        {
            for (String intf : busObjectDescription.interfaces)
            {
                if (intf.equals(interfaceName))
                {
                    objectPaths.add(busObjectDescription.getPath());
                }
            }
        }

        return objectPaths;
    }

    public String getDefaultLanguage()
    {
        return (String) aboutDataObjectMap.get(AboutKeys.ABOUT_DEFAULT_LANGUAGE);
    }
}