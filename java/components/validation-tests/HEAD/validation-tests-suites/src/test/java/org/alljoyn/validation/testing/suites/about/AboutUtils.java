/*******************************************************************************
 *   *     Copyright (c) Open Connectivity Foundation (OCF) and AllJoyn Open
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
package org.alljoyn.validation.testing.suites.about;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.alljoyn.about.AboutKeys;
import org.alljoyn.bus.BusException;
import org.alljoyn.bus.Variant;
import org.alljoyn.services.common.BusObjectDescription;
import org.alljoyn.services.common.utils.TransportUtil;

public class AboutUtils
{

    public static Map<String, Variant> buildAboutDataMap(String defaultLanguage, UUID appId, String deviceId, String deviceName) throws BusException
    {
        HashMap<String, Object> aboutObjectMap = buildAboutMap(defaultLanguage, appId, deviceId, deviceName);
        Map<String, Variant> aboutDataMap = TransportUtil.toVariantMap(aboutObjectMap);
        return aboutDataMap;
    }

    public static HashMap<String, Object> buildAboutMap(String defaultLanguage, UUID appId, String deviceId, String deviceName)
    {
        String[] languages =
        { defaultLanguage };

        HashMap<String, Object> aboutObjectMap = new HashMap<String, Object>();
        aboutObjectMap.put(AboutKeys.ABOUT_APP_ID, appId);
        aboutObjectMap.put(AboutKeys.ABOUT_DEVICE_ID, deviceId);
        aboutObjectMap.put(AboutKeys.ABOUT_DEFAULT_LANGUAGE, defaultLanguage);
        aboutObjectMap.put(AboutKeys.ABOUT_DEVICE_NAME, deviceName);
        aboutObjectMap.put(AboutKeys.ABOUT_APP_NAME, "appName");
        aboutObjectMap.put(AboutKeys.ABOUT_MANUFACTURER, "manufacturer");
        aboutObjectMap.put(AboutKeys.ABOUT_MODEL_NUMBER, "ModelNumber");
        aboutObjectMap.put(AboutKeys.ABOUT_SUPPORTED_LANGUAGES, languages);
        aboutObjectMap.put(AboutKeys.ABOUT_DESCRIPTION, "description");
        aboutObjectMap.put(AboutKeys.ABOUT_SOFTWARE_VERSION, "1.2");
        aboutObjectMap.put(AboutKeys.ABOUT_AJ_SOFTWARE_VERSION, "3.3");
        return aboutObjectMap;
    }

    public static BusObjectDescription getBusObjectDescription(String path, String[] interfaces)
    {
        BusObjectDescription busObjectDescription = new BusObjectDescription();
        busObjectDescription.setPath(path);
        busObjectDescription.setInterfaces(interfaces);
        return busObjectDescription;
    }

}