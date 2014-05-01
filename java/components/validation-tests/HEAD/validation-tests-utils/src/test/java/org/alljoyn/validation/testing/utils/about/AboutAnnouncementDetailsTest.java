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
package org.alljoyn.validation.testing.utils.about;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.alljoyn.about.AboutKeys;
import org.alljoyn.bus.BusException;
import org.alljoyn.bus.Variant;
import org.alljoyn.services.common.BusObjectDescription;
import org.alljoyn.services.common.utils.TransportUtil;
import org.alljoyn.validation.testing.utils.about.AboutAnnouncementDetails;
import org.junit.Before;
import org.junit.Test;

public class AboutAnnouncementDetailsTest
{

    private String serviceName = "serviceName";
    private short port = 30;
    private BusObjectDescription[] objectDescriptions;
    private Map<String, Variant> aboutMap;
    private String deviceId = "deviceId";
    private UUID appId = UUID.randomUUID();
    private String deviceName = "deviceName";
    private String appName = "appName";
    private String manufacturuer = "manufacturuer";
    private String modelNumber = "modelNumber";
    private String description = "description";
    private String dateOfManufacture = "dateManufacture";
    private String softwareVersion = "softwareVersion";
    private String ajSoftwareVersion = "ajswVersion";
    private String hardwareVersion = "hwVersion";
    private String supportUrl = "supportUrl";
    private String defaultLanguage = "en";
    private String supportedLanguages[] = new String[]
    { "en", "fr" };

    @Before
    public void setup()
    {
        objectDescriptions = new BusObjectDescription[0];
        aboutMap = new HashMap<String, Variant>();
        aboutMap.put(AboutKeys.ABOUT_DEVICE_ID, new Variant(deviceId));

        aboutMap.put(AboutKeys.ABOUT_APP_ID, new Variant(TransportUtil.uuidToByteArray(appId), "ay"));

        aboutMap.put(AboutKeys.ABOUT_DEFAULT_LANGUAGE, new Variant(defaultLanguage));
        aboutMap.put(AboutKeys.ABOUT_DEVICE_NAME, new Variant(deviceName));
        aboutMap.put(AboutKeys.ABOUT_APP_NAME, new Variant(appName));
        aboutMap.put(AboutKeys.ABOUT_MANUFACTURER, new Variant(manufacturuer));
        aboutMap.put(AboutKeys.ABOUT_MODEL_NUMBER, new Variant(modelNumber));
        aboutMap.put(AboutKeys.ABOUT_SUPPORTED_LANGUAGES, new Variant(supportedLanguages));
        aboutMap.put(AboutKeys.ABOUT_DESCRIPTION, new Variant(description));
        aboutMap.put(AboutKeys.ABOUT_DATE_OF_MANUFACTURE, new Variant(dateOfManufacture));
        aboutMap.put(AboutKeys.ABOUT_SOFTWARE_VERSION, new Variant(softwareVersion));
        aboutMap.put(AboutKeys.ABOUT_AJ_SOFTWARE_VERSION, new Variant(ajSoftwareVersion));
        aboutMap.put(AboutKeys.ABOUT_HARDWARE_VERSION, new Variant(hardwareVersion));
        aboutMap.put(AboutKeys.ABOUT_SUPPORT_URL, new Variant(supportUrl));
    }

    @Test
    public void testAboutAnnouncement() throws Exception
    {
        AboutAnnouncementDetails aboutAnnouncementDetails = new AboutAnnouncementDetails(serviceName, port, objectDescriptions, aboutMap);
        assertEquals(serviceName, aboutAnnouncementDetails.getServiceName());
        assertEquals(port, aboutAnnouncementDetails.getPort());
        assertSame(objectDescriptions, aboutAnnouncementDetails.getObjectDescriptions());
        assertSame(aboutMap, aboutAnnouncementDetails.getAboutData());

        aboutAnnouncementDetails.convertAboutMap();

        assertEquals(deviceId, aboutAnnouncementDetails.getDeviceId());
        assertEquals(appId, aboutAnnouncementDetails.getAppId());
        assertEquals(defaultLanguage, aboutAnnouncementDetails.getDefaultLanguage());
        assertEquals(deviceName, aboutAnnouncementDetails.getDeviceName());
        assertEquals(appName, aboutAnnouncementDetails.getAppName());
        assertEquals(manufacturuer, aboutAnnouncementDetails.getManufacturer());
        assertEquals(modelNumber, aboutAnnouncementDetails.getModel());
        assertArrayEquals(supportedLanguages, aboutAnnouncementDetails.getSupportedLanguages());
        assertEquals(description, aboutAnnouncementDetails.getDescription());
        assertEquals(dateOfManufacture, aboutAnnouncementDetails.getManufactureDate());
        assertEquals(softwareVersion, aboutAnnouncementDetails.getSoftwareVersion());
        assertEquals(ajSoftwareVersion, aboutAnnouncementDetails.getAjSoftwareVersion());
        assertEquals(hardwareVersion, aboutAnnouncementDetails.getHardwareVersion());
        assertEquals(supportUrl, aboutAnnouncementDetails.getSupportUrl());
    }

    @Test
    public void testInvalidAboutAnnouncement() throws Exception
    {
        HashMap<String, Variant> aboutData2;
        String[] stringKeys = new String[]
        { AboutKeys.ABOUT_DEFAULT_LANGUAGE, AboutKeys.ABOUT_DEVICE_NAME, AboutKeys.ABOUT_APP_NAME, AboutKeys.ABOUT_MANUFACTURER, AboutKeys.ABOUT_MODEL_NUMBER,
                AboutKeys.ABOUT_DESCRIPTION, AboutKeys.ABOUT_DATE_OF_MANUFACTURE, AboutKeys.ABOUT_SOFTWARE_VERSION, AboutKeys.ABOUT_AJ_SOFTWARE_VERSION,
                AboutKeys.ABOUT_HARDWARE_VERSION, AboutKeys.ABOUT_SUPPORT_URL };

        for (String key : stringKeys)
        {
            aboutData2 = new HashMap<String, Variant>();
            aboutData2.put(key, new Variant(false, "b"));
            AboutAnnouncementDetails aboutAnnouncementDetails = new AboutAnnouncementDetails(serviceName, port, objectDescriptions, aboutData2);
            checkType(key, "b", "s", aboutAnnouncementDetails);
        }

        aboutData2 = new HashMap<String, Variant>();
        aboutData2.put(AboutKeys.ABOUT_APP_ID, new Variant("stringValue"));
        AboutAnnouncementDetails aboutAnnouncementDetails = new AboutAnnouncementDetails(serviceName, port, objectDescriptions, aboutData2);
        checkType(AboutKeys.ABOUT_APP_ID, "s", "ay", aboutAnnouncementDetails);

        aboutData2 = new HashMap<String, Variant>();
        aboutData2.put(AboutKeys.ABOUT_SUPPORTED_LANGUAGES, new Variant("stringValue"));
        aboutAnnouncementDetails = new AboutAnnouncementDetails(serviceName, port, objectDescriptions, aboutData2);
        checkType(AboutKeys.ABOUT_SUPPORTED_LANGUAGES, "s", "as", aboutAnnouncementDetails);

    }

    @Test
    public void testAboutAnnouncementWithNullAboutMap() throws Exception
    {
        AboutAnnouncementDetails aboutAnnouncementDetails = new AboutAnnouncementDetails(serviceName, port, objectDescriptions, null);

        try
        {
            aboutAnnouncementDetails.convertAboutMap();
            fail();
        }
        catch (BusException b)
        {
            assertEquals("aboutData is null!", b.getMessage());
        }

    }

    @Test
    public void testUnsupportedType() throws Exception
    {
        HashMap<String, Variant> aboutData2;
        aboutData2 = new HashMap<String, Variant>();
        aboutData2.put("blah", new Variant(false, "b"));
        AboutAnnouncementDetails aboutAnnouncementDetails = new AboutAnnouncementDetails(serviceName, port, objectDescriptions, aboutData2);
        aboutAnnouncementDetails.convertAboutMap();
    }

    @Test
    public void testMultipleInvalidValues() throws Exception
    {
        HashMap<String, Variant> aboutData2;
        aboutData2 = new HashMap<String, Variant>();
        aboutData2.put(AboutKeys.ABOUT_APP_ID, new Variant("stringValue"));
        aboutData2.put(AboutKeys.ABOUT_SUPPORTED_LANGUAGES, new Variant("stringValue"));
        AboutAnnouncementDetails aboutAnnouncementDetails = new AboutAnnouncementDetails(serviceName, port, objectDescriptions, aboutData2);
        checkType(AboutKeys.ABOUT_APP_ID, "s", "ay", aboutAnnouncementDetails);
    }

    @Test
    public void testSupportsInterface() throws Exception
    {
        objectDescriptions = new BusObjectDescription[1];
        BusObjectDescription busObjDesc = new BusObjectDescription();
        busObjDesc.setInterfaces(new String[]
        { "org.alljoyn.About" });
        busObjDesc.setPath("/");
        objectDescriptions[0] = busObjDesc;

        AboutAnnouncementDetails aboutAnnouncementDetails = new AboutAnnouncementDetails(serviceName, port, objectDescriptions, aboutMap);
        assertTrue(aboutAnnouncementDetails.supportsInterface("org.alljoyn.About"));
        assertFalse(aboutAnnouncementDetails.supportsInterface("org.alljoyn.Config"));

    }

    private void checkType(String key, String signature, String expectedSignature, AboutAnnouncementDetails aboutAnnouncementDetails)
    {
        try
        {
            aboutAnnouncementDetails.convertAboutMap();
            fail("Expected exception for " + key + " not of type " + signature);
        }
        catch (BusException e)
        {
            assertEquals(key + " has an incorrect signature: '" + signature + "' expected: '" + expectedSignature + "'", e.getMessage());
        }
    }

}
