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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.ParserConfigurationException;

import org.alljoyn.about.AboutKeys;
import org.alljoyn.about.AboutService;
import org.alljoyn.about.client.AboutClient;
import org.alljoyn.about.icon.AboutIconClient;
import org.alljoyn.about.transport.AboutTransport;
import org.alljoyn.about.transport.IconTransport;
import org.alljoyn.bus.BusAttachment;
import org.alljoyn.bus.BusException;
import org.alljoyn.bus.ErrorReplyBusException;
import org.alljoyn.bus.Variant;
import org.alljoyn.services.common.BusObjectDescription;
import org.alljoyn.services.common.LanguageNotSupportedException;
import org.alljoyn.services.common.utils.TransportUtil;
import org.alljoyn.validation.framework.AppUnderTestDetails;
import org.alljoyn.validation.framework.ValidationTestContext;
import org.alljoyn.validation.framework.utils.introspection.BusIntrospector;
import org.alljoyn.validation.framework.utils.introspection.XmlBasedBusIntrospector;
import org.alljoyn.validation.framework.utils.introspection.bean.InterfaceDetail;
import org.alljoyn.validation.framework.utils.introspection.bean.IntrospectionInterface;
import org.alljoyn.validation.framework.utils.introspection.bean.IntrospectionNode;
import org.alljoyn.validation.framework.utils.introspection.bean.NodeDetail;
import org.alljoyn.validation.testing.suites.BaseTestSuiteTest;
import org.alljoyn.validation.testing.suites.MyRobolectricTestRunner;
import org.alljoyn.validation.testing.utils.InterfaceValidator;
import org.alljoyn.validation.testing.utils.ValidationResult;
import org.alljoyn.validation.testing.utils.about.AboutAnnouncementDetails;
import org.alljoyn.validation.testing.utils.services.ServiceHelper;
import org.apache.maven.artifact.ant.shaded.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.annotation.Config;
import org.xml.sax.SAXException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AndroidRuntimeException;

@RunWith(MyRobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class AboutTestSuiteTest extends BaseTestSuiteTest
{
    private static final String STANDARDIZED_INTERFACE_NAME = "org.alljoyn.About";
    private static final String PATH = "/About";
    private static final String UNDEFINED_STANDARDIZED_INTERFACE_NAME = "org.alljoyn.Undefined";
    private static final String PEER_NAME = "peer";
    private static final Integer SESSION_ID = 1;
    private static final String UNSUPPORTED_LANGUAGE = "INVALID";

    private AboutTestSuite aboutTestSuite;

    @Mock
    private ValidationTestContext mockTestContext;
    @Mock
    private AboutAnnouncementDetails mockAboutAnnouncement;
    @Mock
    protected ServiceHelper mockServiceHelper;
    @Mock
    private AboutClient mockAboutClient;
    @Mock
    private AboutIconClient mockAboutIconClient;
    @Mock
    private XmlBasedBusIntrospector mockIntrospector;
    @Mock
    private BusAttachment mockBusAttachment;
    @Mock
    private InterfaceValidator mockInterfaceValidator;
    @Mock
    private NodeDetail mockNodeDetail;
    @Mock
    private IntrospectionInterface mockIntrospectionInterface;
    @Mock
    private IntrospectionNode mockIntrospectionNode;

    private Bitmap iconBitmap;
    private int iconSize;
    private String deviceId = "deviceId";
    private UUID appId = UUID.fromString("4f672c8f-9ade-414d-89e1-a99739836e48");

    private Map<String, Variant> appAboutDataMap;
    private String deviceName = "deviceName";
    private String defaultLanguage = "en";
    private AppUnderTestDetails appUnderTestDetails;

    @Before
    public void setup() throws Exception
    {
        MockitoAnnotations.initMocks(this);

        appUnderTestDetails = new AppUnderTestDetails(appId, deviceId);
        when(mockTestContext.getAppUnderTestDetails()).thenReturn(appUnderTestDetails);

        when(mockAboutAnnouncement.supportsInterface(AboutTransport.INTERFACE_NAME)).thenReturn(true);
        when(mockAboutAnnouncement.supportsInterface(IconTransport.INTERFACE_NAME)).thenReturn(true);
        when(mockAboutAnnouncement.getDefaultLanguage()).thenReturn("en");

        aboutTestSuite = new AboutTestSuite()
        {
            @Override
            protected InterfaceValidator getInterfaceValidator() throws IOException, ParserConfigurationException, SAXException
            {
                return mockInterfaceValidator;
            }

            @Override
            protected XmlBasedBusIntrospector getIntrospector()
            {
                return mockIntrospector;
            }

            @Override
            protected ServiceHelper getServiceHelper()
            {
                return mockServiceHelper;
            }
        };

        aboutTestSuite.setValidationTestContext(mockTestContext);

        when(mockServiceHelper.waitForNextDeviceAnnouncement(anyLong(), any(TimeUnit.class))).thenReturn(mockAboutAnnouncement);

        when(mockServiceHelper.connectAboutClient(mockAboutAnnouncement)).thenReturn(mockAboutClient);
        when(mockServiceHelper.connectAboutIconClient(mockAboutAnnouncement)).thenReturn(mockAboutIconClient);
        when(mockServiceHelper.getBusIntrospector(mockAboutClient)).thenReturn(mockIntrospector);

        appAboutDataMap = AboutUtils.buildAboutDataMap(defaultLanguage, appId, deviceId, deviceName);
    }

    @Test
    public void testAboutAnnouncementSuccess() throws Exception
    {
        when(mockAboutAnnouncement.getObjectDescriptions()).thenReturn(new BusObjectDescription[]
        { getBusObjectDescription() });

        appAboutDataMap.put("longvalue", new Variant(StringUtils.repeat("a", 129)));

        when(mockAboutAnnouncement.getAboutData()).thenReturn(appAboutDataMap);

        executeTestMethod(getTestWrapperFor_v1_01());

        verify(mockAboutAnnouncement).getAboutData();
        verify(mockAboutAnnouncement).getObjectDescriptions();
    }

    @Test
    public void testAboutAnnouncementPassesEvenIfAboutInterfaceIsAbsent() throws Exception
    {
        BusObjectDescription busObjectDescription = getBusObjectDescription();
        busObjectDescription.setInterfaces(new String[]
        { "org.alljoyn.Config" });
        when(mockAboutAnnouncement.getObjectDescriptions()).thenReturn(new BusObjectDescription[]
        { busObjectDescription });
        when(mockAboutAnnouncement.getAboutData()).thenReturn(appAboutDataMap);

        executeTestMethod(getTestWrapperFor_v1_01());

        verify(mockAboutAnnouncement).getAboutData();
        verify(mockAboutAnnouncement).getObjectDescriptions();
    }

    @Test
    public void testAboutAnnouncementFailsIfAboutInterfaceIsPresentAtWrongPath() throws Exception
    {
        BusObjectDescription busObjectDescription = getBusObjectDescription();
        busObjectDescription.setPath("path");
        when(mockAboutAnnouncement.getObjectDescriptions()).thenReturn(new BusObjectDescription[]
        { busObjectDescription });

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_01(), "About interface present at the wrong path expected:<[/About]> but was:<[path]>");
    }

    @Test
    public void testAboutAnnouncementFailsIfAppIdFieldIsMissingInAboutMap() throws Exception
    {
        testAboutAnnouncementFailsIfFieldIsMissingInAboutMap(AboutKeys.ABOUT_APP_ID);
    }

    @Test
    public void testAboutAnnouncementFailsIfDefaultLanguageFieldIsMissingInAboutMap() throws Exception
    {
        testAboutAnnouncementFailsIfFieldIsMissingInAboutMap(AboutKeys.ABOUT_DEFAULT_LANGUAGE);
    }

    @Test
    public void testAboutAnnouncementPassesIfDeviceNameFieldIsMissingInAboutMap() throws Exception
    {
        when(mockAboutAnnouncement.getObjectDescriptions()).thenReturn(new BusObjectDescription[]
        { getBusObjectDescription() });
        Map<String, Variant> aboutMap = appAboutDataMap;
        aboutMap.remove(AboutKeys.ABOUT_DEVICE_NAME);
        when(mockAboutAnnouncement.getAboutData()).thenReturn(aboutMap);

        executeTestMethod(getTestWrapperFor_v1_01());

        verify(mockAboutAnnouncement).getAboutData();
        verify(mockAboutAnnouncement).getObjectDescriptions();
    }

    @Test
    public void testAboutAnnouncementFailsIfDeviceIdFieldIsMissingInAboutMap() throws Exception
    {
        testAboutAnnouncementFailsIfFieldIsMissingInAboutMap(AboutKeys.ABOUT_DEVICE_ID);
    }

    @Test
    public void testAboutAnnouncementFailsIfAppNameFieldIsMissingInAboutMap() throws Exception
    {
        testAboutAnnouncementFailsIfFieldIsMissingInAboutMap(AboutKeys.ABOUT_APP_NAME);
    }

    @Test
    public void testAboutAnnouncementFailsIfManufacturerFieldIsMissingInAboutMap() throws Exception
    {
        testAboutAnnouncementFailsIfFieldIsMissingInAboutMap(AboutKeys.ABOUT_MANUFACTURER);
    }

    @Test
    public void testAboutAnnouncementFailsIfModelNumberFieldIsMissingInAboutMap() throws Exception
    {
        testAboutAnnouncementFailsIfFieldIsMissingInAboutMap(AboutKeys.ABOUT_MODEL_NUMBER);
    }

    @Test
    public void testAboutVersionSuccess() throws Exception
    {
        when(mockAboutClient.getVersion()).thenReturn((short) AboutService.PROTOCOL_VERSION);
        executeTestMethod(getTestWrapperFor_v1_02());

        verify(mockAboutClient).getVersion();
    }

    @Test
    public void testAboutVersionFailure() throws Exception
    {
        executeTestMethodFailsAssertion(getTestWrapperFor_v1_02(), "About version does not match expected:<1> but was:<0>");
    }

    @Test
    public void testGetObjectDescriptionSuccess() throws Exception
    {
        when(mockAboutAnnouncement.getObjectDescriptions()).thenReturn(new BusObjectDescription[]
        { getBusObjectDescription() });
        when(mockAboutClient.getBusObjectDescriptions()).thenReturn(new BusObjectDescription[]
        { getBusObjectDescription() });

        executeTestMethod(getTestWrapperFor_v1_03());

        verify(mockAboutAnnouncement).getObjectDescriptions();
        verify(mockAboutClient).getBusObjectDescriptions();
    }

    @Test
    public void testGetObjectDescriptionFailsIfKeysDontMatch() throws Exception
    {
        when(mockAboutAnnouncement.getObjectDescriptions()).thenReturn(new BusObjectDescription[]
        { getBusObjectDescription() });
        when(mockAboutClient.getBusObjectDescriptions()).thenReturn(new BusObjectDescription[]
        {});

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_03(),
                "GetObjectDescription does not contain the same set of paths as the announcement expected:<[]> but was:<[/About]>");
    }

    @Test
    public void testGetObjectDescriptionFailsIfValuesDontMatch() throws Exception
    {
        when(mockAboutAnnouncement.getObjectDescriptions()).thenReturn(new BusObjectDescription[]
        { getBusObjectDescription() });
        BusObjectDescription busObjectDescription = new BusObjectDescription();
        busObjectDescription.setPath(getBusObjectDescription().getPath());
        busObjectDescription.setInterfaces(new String[]
        {});
        when(mockAboutClient.getBusObjectDescriptions()).thenReturn(new BusObjectDescription[]
        { busObjectDescription });

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_03(), "GetObjectDescription not consistent for " + getBusObjectDescription().getPath());
    }

    @Test
    public void testAboutAnnouncementConsistentWithBusObjectsSuccessful() throws Exception
    {
        setupConditionsForAboutAnnouncementConsistentWithBusObjectsTest();
        when(mockIntrospectionInterface.getName()).thenReturn(STANDARDIZED_INTERFACE_NAME);

        setupMockIntrospector(STANDARDIZED_INTERFACE_NAME, PATH);

        executeTestMethod(getTestWrapperFor_v1_04());

        verify(mockAboutAnnouncement).getObjectDescriptions();
        verify(mockIntrospector).introspect(PATH);
    }

    @Test
    public void testAboutAnnouncementConsistentWithBusObjectsMismatch() throws Exception
    {
        setupConditionsForAboutAnnouncementConsistentWithBusObjectsTest();
        when(mockIntrospectionInterface.getName()).thenReturn(UNDEFINED_STANDARDIZED_INTERFACE_NAME);

        setupMockIntrospector(STANDARDIZED_INTERFACE_NAME, PATH);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_04(),
                "AboutAnnouncement advertises interface org.alljoyn.About at path /About, but bus does not contain such interface at that path.");
    }

    @Test
    public void testAboutAnnouncementConsistentWithBusObjectsWithErrorReplyBusException() throws Exception
    {
        internalAnnouncementConsistentWithBusExceptionTest(new ErrorReplyBusException("org.freedesktop.DBus.Error.ServiceUnknown"),
                "AboutAnnouncement has the path /About, but it is not found on the Bus Intropsection.");
    }

    @Test
    public void testAboutAnnouncementConsistentWithBusObjectsWithErrorReplyBusExceptionWrongErrorName() throws Exception
    {
        internalAnnouncementConsistentWithBusExceptionTest(new ErrorReplyBusException("org.freedesktop.DBus.Error.ServiceUnknown.mock"),
                "Encountered exception while trying to introspect the bus");
    }

    @Test
    public void testAboutAnnouncementConsistentWithBusObjectsWithBusException() throws Exception
    {
        internalAnnouncementConsistentWithBusExceptionTest(new BusException(), "Encountered exception while trying to introspect the bus");
    }

    @Test
    public void testAboutAnnouncementConsistentWithBusObjectsWithIOException() throws Exception
    {
        internalAnnouncementConsistentWithBusExceptionTest(new IOException(), "Encountered exception while trying to parse the introspection xml");
    }

    @Test
    public void testAboutAnnouncementConsistentWithBusObjectsWithSAXException() throws Exception
    {
        internalAnnouncementConsistentWithBusExceptionTest(new SAXException(), "Encountered exception while trying to parse the introspection xml");
    }

    @Test
    public void testAboutAnnouncementConsistentWithBusObjectsWithParserConfigurationException() throws Exception
    {
        internalAnnouncementConsistentWithBusExceptionTest(new ParserConfigurationException(), "Encountered exception while trying to parse the introspection xml");
    }

    @Test
    public void testStandardizedInterfacesMatchDefinitions() throws Exception
    {
        List<InterfaceDetail> interfaceDetailList = new ArrayList<InterfaceDetail>();
        ValidationResult validationResult = Mockito.mock(ValidationResult.class);
        when(mockServiceHelper.getBusIntrospector(mockAboutClient)).thenReturn(mockIntrospector);
        List<IntrospectionInterface> interfaces = new ArrayList<IntrospectionInterface>();
        IntrospectionInterface introspectionInterface = new IntrospectionInterface();
        introspectionInterface.setName("interfaceName");
        interfaces.add(introspectionInterface);
        InterfaceDetail interfaceDetail = new InterfaceDetail("path", interfaces);
        interfaceDetailList.add(interfaceDetail);
        when(mockIntrospector.getStandardizedInterfacesExposedOnBus()).thenReturn(interfaceDetailList);
        when(mockInterfaceValidator.validate(interfaceDetailList)).thenReturn(validationResult);
        when(validationResult.isValid()).thenReturn(true);

        executeTestMethod(getTestWrapperFor_v1_05());

        verify(mockIntrospector).getStandardizedInterfacesExposedOnBus();
        verify(mockInterfaceValidator).validate(interfaceDetailList);
        verify(validationResult).isValid();
    }

    @Test
    public void getAboutForDefaultLanguageTestCaseShouldPass() throws Exception
    {
        when(mockAboutClient.getAbout(defaultLanguage)).thenReturn(TransportUtil.fromVariantMap(appAboutDataMap));

        executeTestMethod(getTestWrapperFor_v1_06());

        verify(mockAboutClient).getAbout(defaultLanguage);
    }

    @Test
    public void getAboutForDefaultLanguageTestCaseMissingAppIdShouldFail() throws Exception
    {
        String key = AboutKeys.ABOUT_APP_ID;
        verifyAssertFailureOnMissingField(key);
    }

    @Test
    public void getAboutForDefaultLanguageTestCaseMissingDeviceIdShouldFail() throws Exception
    {
        String key = AboutKeys.ABOUT_DEVICE_ID;
        verifyAssertFailureOnMissingField(key);
    }

    @Test
    public void getAboutForDefaultLanguageTestCaseMissingDefaultLanguageShouldFail() throws Exception
    {
        String key = AboutKeys.ABOUT_DEFAULT_LANGUAGE;
        verifyAssertFailureOnMissingField(key);
    }

    @Test
    public void getAboutForDefaultLanguageTestCaseMissingDeviceNameShouldPass() throws Exception
    {
        Map<String, Object> copiedAboutMap = new HashMap<String, Object>();
        copiedAboutMap.putAll(TransportUtil.fromVariantMap(appAboutDataMap));
        copiedAboutMap.remove(AboutKeys.ABOUT_DEVICE_NAME);
        when(mockAboutClient.getAbout(defaultLanguage)).thenReturn(copiedAboutMap);

        executeTestMethod(getTestWrapperFor_v1_06());

        verify(mockAboutClient).getAbout(defaultLanguage);
    }

    @Test
    public void getAboutForDefaultLanguageTestCaseMissingAppNameShouldFail() throws Exception
    {
        String key = AboutKeys.ABOUT_APP_NAME;
        verifyAssertFailureOnMissingField(key);
    }

    @Test
    public void getAboutForDefaultLanguageTestCaseMissingManufacturerShouldFail() throws Exception
    {
        String key = AboutKeys.ABOUT_MANUFACTURER;
        verifyAssertFailureOnMissingField(key);
    }

    @Test
    public void getAboutForDefaultLanguageTestCaseMissingModelNumberShouldFail() throws Exception
    {
        String key = AboutKeys.ABOUT_MODEL_NUMBER;
        verifyAssertFailureOnMissingField(key);
    }

    @Test
    public void getAboutForDefaultLanguageTestCaseMissingSupportedLanguageShouldFail() throws Exception
    {
        String key = AboutKeys.ABOUT_SUPPORTED_LANGUAGES;
        verifyAssertFailureOnMissingField(key);
    }

    @Test
    public void getAboutForDefaultLanguageTestCaseMissingDescriptionShouldFail() throws Exception
    {
        String key = AboutKeys.ABOUT_DESCRIPTION;
        verifyAssertFailureOnMissingField(key);
    }

    @Test
    public void getAboutForDefaultLanguageTestCaseMissingSoftwareVersionShouldFail() throws Exception
    {
        String key = AboutKeys.ABOUT_SOFTWARE_VERSION;
        verifyAssertFailureOnMissingField(key);
    }

    @Test
    public void getAboutForDefaultLanguageTestCaseMissingAJSoftwareVersionShouldFail() throws Exception
    {
        String key = AboutKeys.ABOUT_AJ_SOFTWARE_VERSION;
        verifyAssertFailureOnMissingField(key);
    }

    @Test
    public void testGetAboutForDefaultLanguageFailsIfAppIdFieldValueSignatureIsInvalid() throws Exception
    {
        testGetAboutForDefaultLanguageFailsIfAnyFieldValueSignatureIsInvalid(AboutKeys.ABOUT_APP_ID, "app id", "ay", "s");
    }

    @Test
    public void testGetAboutForDefaultLanguageFailsIfDefaultLanguageFieldValueSignatureIsInvalid() throws Exception
    {
        testGetAboutForDefaultLanguageFailsIfAnyFieldValueSignatureIsInvalid(AboutKeys.ABOUT_DEFAULT_LANGUAGE, new Integer(10), "s", "i");
    }

    @Test
    public void testGetAboutForDefaultLanguageFailsIfDeviceNameFieldValueSignatureIsInvalid() throws Exception
    {
        testGetAboutForDefaultLanguageFailsIfAnyFieldValueSignatureIsInvalid(AboutKeys.ABOUT_DEVICE_NAME, new Integer(10), "s", "i");
    }

    @Test
    public void testGetAboutForDefaultLanguageFailsIfDeviceIdFieldValueSignatureIsInvalid() throws Exception
    {
        testGetAboutForDefaultLanguageFailsIfAnyFieldValueSignatureIsInvalid(AboutKeys.ABOUT_DEVICE_ID, new Integer(10), "s", "i");
    }

    @Test
    public void testGetAboutForDefaultLanguageFailsIfAppNameFieldValueSignatureIsInvalid() throws Exception
    {
        testGetAboutForDefaultLanguageFailsIfAnyFieldValueSignatureIsInvalid(AboutKeys.ABOUT_APP_NAME, new Integer(10), "s", "i");
    }

    @Test
    public void testGetAboutForDefaultLanguageFailsIfManufacturerFieldValueSignatureIsInvalid() throws Exception
    {
        testGetAboutForDefaultLanguageFailsIfAnyFieldValueSignatureIsInvalid(AboutKeys.ABOUT_MANUFACTURER, new Integer(10), "s", "i");
    }

    @Test
    public void testGetAboutForDefaultLanguageFailsIfModelNumberFieldValueSignatureIsInvalid() throws Exception
    {
        testGetAboutForDefaultLanguageFailsIfAnyFieldValueSignatureIsInvalid(AboutKeys.ABOUT_MODEL_NUMBER, new Integer(10), "s", "i");
    }

    @Test
    public void testGetAboutForDefaultLanguageFailsIfSupportedLanguagesFieldValueSignatureIsInvalid() throws Exception
    {
        testGetAboutForDefaultLanguageFailsIfAnyFieldValueSignatureIsInvalid(AboutKeys.ABOUT_SUPPORTED_LANGUAGES, new Integer(10), "as", "i");
    }

    @Test
    public void testGetAboutForDefaultLanguageFailsIfDescriptionFieldValueSignatureIsInvalid() throws Exception
    {
        testGetAboutForDefaultLanguageFailsIfAnyFieldValueSignatureIsInvalid(AboutKeys.ABOUT_DESCRIPTION, new Integer(10), "s", "i");
    }

    @Test
    public void testGetAboutForDefaultLanguageFailsIfSoftwareVersionFieldValueSignatureIsInvalid() throws Exception
    {
        testGetAboutForDefaultLanguageFailsIfAnyFieldValueSignatureIsInvalid(AboutKeys.ABOUT_SOFTWARE_VERSION, new Integer(10), "s", "i");
    }

    @Test
    public void testGetAboutForDefaultLanguageFailsIfAJSoftwareVersionFieldValueSignatureIsInvalid() throws Exception
    {
        testGetAboutForDefaultLanguageFailsIfAnyFieldValueSignatureIsInvalid(AboutKeys.ABOUT_AJ_SOFTWARE_VERSION, new Integer(10), "s", "i");
    }

    @Test
    public void testGetAboutForDefaultLanguageFailsIfDateOfManufactureFieldValueSignatureIsInvalid() throws Exception
    {
        testGetAboutForDefaultLanguageFailsIfAnyFieldValueSignatureIsInvalid(AboutKeys.ABOUT_DATE_OF_MANUFACTURE, new Integer(10), "s", "i");
    }

    @Test
    public void testGetAboutForDefaultLanguageFailsIfHardwareVersionFieldValueSignatureIsInvalid() throws Exception
    {
        testGetAboutForDefaultLanguageFailsIfAnyFieldValueSignatureIsInvalid(AboutKeys.ABOUT_HARDWARE_VERSION, new Integer(10), "s", "i");
    }

    @Test
    public void testGetAboutForDefaultLanguageFailsIfSupportUrlFieldValueSignatureIsInvalid() throws Exception
    {
        testGetAboutForDefaultLanguageFailsIfAnyFieldValueSignatureIsInvalid(AboutKeys.ABOUT_SUPPORT_URL, new Integer(10), "s", "i");
    }

    @Test
    public void testGetAboutForDefaultLanguageFailsIfDateOfManufactureFieldValueIsInvalid() throws Exception
    {
        Map<String, Object> copiedAboutMap = new HashMap<String, Object>();
        copiedAboutMap.putAll(TransportUtil.fromVariantMap(appAboutDataMap));
        copiedAboutMap.put(AboutKeys.ABOUT_DATE_OF_MANUFACTURE, "date");

        when(mockAboutClient.getAbout(defaultLanguage)).thenReturn(copiedAboutMap);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_06(), "DateOfManufacture field value date does not match expected date pattern YYYY-MM-DD");
    }

    @Test
    public void testGetAboutForDefaultLanguageFailsIfSupportUrlFieldValueIsInvalid() throws Exception
    {
        Map<String, Object> copiedAboutMap = new HashMap<String, Object>();
        copiedAboutMap.putAll(TransportUtil.fromVariantMap(appAboutDataMap));
        copiedAboutMap.put(AboutKeys.ABOUT_SUPPORT_URL, "url");

        when(mockAboutClient.getAbout(defaultLanguage)).thenReturn(copiedAboutMap);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_06(), "url is not a valid URL");
    }

    @Test
    public void getAboutForDefaultLanguageTestCaseThrowsBusExceptionOnGetAbout() throws Exception
    {
        BusException busException = new BusException();
        when(mockAboutClient.getAbout(defaultLanguage)).thenThrow(busException);

        executeTestMethod(getTestWrapperFor_v1_06(), busException);

        verify(mockAboutClient).getAbout(defaultLanguage);
    }

    @Test
    public void getAboutForDefaultLanguageTestCaseTimesOutWaiting() throws Exception
    {
        when(mockServiceHelper.waitForNextDeviceAnnouncement(anyLong(), any(TimeUnit.class))).thenReturn(null);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_06(), "Timed out waiting for About announcement");
    }

    @Test
    public void isValidDateTest()
    {
        assertFalse(aboutTestSuite.isValidDate("date"));
        assertFalse(aboutTestSuite.isValidDate("12/31/2012"));
        assertFalse(aboutTestSuite.isValidDate("31/12/2012"));
        assertFalse(aboutTestSuite.isValidDate("2012/12/31"));
        assertFalse(aboutTestSuite.isValidDate("12-31-2012"));
        assertFalse(aboutTestSuite.isValidDate("31-12-2012"));
        assertFalse(aboutTestSuite.isValidDate("2012-31-31"));
        assertTrue(aboutTestSuite.isValidDate("2012-12-31"));
    }

    @Test
    public void isValidUrlTest()
    {
        assertFalse(aboutTestSuite.isValidUrl("date"));
        assertFalse(aboutTestSuite.isValidUrl("http://"));
        assertFalse(aboutTestSuite.isValidUrl("tmp://allseenalliance.org"));
        assertFalse(aboutTestSuite.isValidUrl("http://allseenalliance.org:8989890987789"));
        assertTrue(aboutTestSuite.isValidUrl("http://allseenalliance.org/index.html"));
    }

    @Test
    public void testGetAboutForSupportedLanguagesFailsWhenSupportedLanguagesIsNull() throws Exception
    {
        executeTestMethodFailsAssertion(getTestWrapperFor_v1_07(), "No supported language found in About announcement");

    }

    @Test
    public void testGetAboutForSupportedLanguagesFailsWhenSupportedLanguagesIsEmpty() throws Exception
    {
        String[] supportedLanguages =
        {};
        setupAboutClientForSupportedLanguagesTest(supportedLanguages);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_07(), "No supported language found in About announcement");
    }

    @Test
    public void testGetAboutForSupportedLanguagesFailsWhenSupportedLanguagesDoesNotContainDefaultLanguage() throws Exception
    {
        String[] supportedLanguages =
        { "hi" };
        setupAboutClientForSupportedLanguagesTest(supportedLanguages);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_07(), "Default language not found in supported languages list of About announcement");
    }

    @Test
    public void testGetAboutForSupportedLanguagesPassesWhenSupportedLanguagesContainOnlyDefaultLanguage() throws Exception
    {
        String[] supportedLanguages =
        { "en" };
        setupAboutClientForSupportedLanguagesTest(supportedLanguages);

        executeTestMethod(getTestWrapperFor_v1_07());

        verify(mockTestContext).addNote("Device only supports one language");
    }

    @Test
    public void testGetAboutForSupportedLanguagesPassesIfAllFieldValuesAreSameForAllLanguages() throws Exception
    {
        String[] supportedLanguages =
        { "en", "hi" };
        setupAboutClientForSupportedLanguagesTest(supportedLanguages);
        setupAboutClientForSupportedLanguagesTest("hi", supportedLanguages);

        executeTestMethod(getTestWrapperFor_v1_07());

        verify(mockAboutClient).getAbout("en");
        verify(mockAboutClient).getAbout("hi");
    }

    @Test
    public void testGetAboutForSupportedLanguagesFailsIfDeviceIdFieldValueIsNotPresentForAnyLanguage() throws Exception
    {
        testGetAboutForSupportedLanguagesFailsIfAnyFieldValueIsNotPresentForAnyLanguage(AboutKeys.ABOUT_DEVICE_ID);
    }

    @Test
    public void testGetAboutForSupportedLanguagesFailsIfAppNameFieldValueIsNotPresentForAnyLanguage() throws Exception
    {
        testGetAboutForSupportedLanguagesFailsIfAnyFieldValueIsNotPresentForAnyLanguage(AboutKeys.ABOUT_APP_NAME);
    }

    @Test
    public void testGetAboutForSupportedLanguagesFailsIfManufacturerFieldValueIsNotPresentForAnyLanguage() throws Exception
    {
        testGetAboutForSupportedLanguagesFailsIfAnyFieldValueIsNotPresentForAnyLanguage(AboutKeys.ABOUT_MANUFACTURER);
    }

    @Test
    public void testGetAboutForSupportedLanguagesFailsIfDescriptionFieldValueIsNotPresentForAnyLanguage() throws Exception
    {
        testGetAboutForSupportedLanguagesFailsIfAnyFieldValueIsNotPresentForAnyLanguage(AboutKeys.ABOUT_DESCRIPTION);
    }

    @Test
    public void testGetAboutForSupportedLanguagesFailsIfAppIdFieldValueIsNotPresentForAnyLanguage() throws Exception
    {
        testGetAboutForSupportedLanguagesFailsIfAnyFieldValueIsNotPresentForAnyLanguage(AboutKeys.ABOUT_APP_ID);
    }

    @Test
    public void testGetAboutForSupportedLanguagesFailsIfDefaultLanguageFieldValueIsNotPresentForAnyLanguage() throws Exception
    {
        testGetAboutForSupportedLanguagesFailsIfAnyFieldValueIsNotPresentForAnyLanguage(AboutKeys.ABOUT_DEFAULT_LANGUAGE);
    }

    @Test
    public void testGetAboutForSupportedLanguagesFailsIfModelNumberFieldValueIsNotPresentForAnyLanguage() throws Exception
    {
        testGetAboutForSupportedLanguagesFailsIfAnyFieldValueIsNotPresentForAnyLanguage(AboutKeys.ABOUT_MODEL_NUMBER);
    }

    @Test
    public void testGetAboutForSupportedLanguagesFailsIfSupportedLanguagesFieldValueIsNotPresentForAnyLanguage() throws Exception
    {
        testGetAboutForSupportedLanguagesFailsIfAnyFieldValueIsNotPresentForAnyLanguage(AboutKeys.ABOUT_SUPPORTED_LANGUAGES);
    }

    @Test
    public void testGetAboutForSupportedLanguagesFailsIfSoftwareVersionFieldValueIsNotPresentForAnyLanguage() throws Exception
    {
        testGetAboutForSupportedLanguagesFailsIfAnyFieldValueIsNotPresentForAnyLanguage(AboutKeys.ABOUT_SOFTWARE_VERSION);
    }

    @Test
    public void testGetAboutForSupportedLanguagesFailsIfAJSoftwareVersionFieldValueIsNotPresentForAnyLanguage() throws Exception
    {
        testGetAboutForSupportedLanguagesFailsIfAnyFieldValueIsNotPresentForAnyLanguage(AboutKeys.ABOUT_AJ_SOFTWARE_VERSION);
    }

    @Test
    public void testGetAboutForSupportedLanguagesFailsIfAppIdValueDoesNotMatchDefaultLanguageValue() throws Exception
    {
        String[] supportedLanguages =
        { "en", "hi" };
        Map<String, Object> defaultLanguageAboutMap = TransportUtil.fromVariantMap(appAboutDataMap);
        defaultLanguageAboutMap.put(AboutKeys.ABOUT_SUPPORTED_LANGUAGES, supportedLanguages);
        when(mockAboutClient.getAbout("en")).thenReturn(defaultLanguageAboutMap);

        Map<String, Object> aboutMap = TransportUtil.fromVariantMap(appAboutDataMap);
        String fieldValue = aboutMap.get(AboutKeys.ABOUT_APP_ID).toString();
        Object newFieldValue = UUID.fromString("2121c207-639c-4760-ac9a-eaa2149f2cb4");
        aboutMap.put(AboutKeys.ABOUT_APP_ID, newFieldValue);
        aboutMap.put(AboutKeys.ABOUT_SUPPORTED_LANGUAGES, supportedLanguages);
        when(mockAboutClient.getAbout("hi")).thenReturn(aboutMap);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_07(), AboutKeys.ABOUT_APP_ID
                + " value returned from the GetAbout for language hi does not match default language value; expected:<[" + fieldValue + "]> but was:<[" + newFieldValue + "]>");
    }

    @Test
    public void testGetAboutForSupportedLanguagesFailsIfDefaultLanguageValueDoesNotMatchDefaultLanguageValue() throws Exception
    {
        testGetAboutForSupportedLanguagesFailsIfAnyNonLocalizedFieldValueDoesNotMatchDefaultLanguageValue(AboutKeys.ABOUT_DEFAULT_LANGUAGE, "hi");
    }

    @Test
    public void testGetAboutForSupportedLanguagesFailsIfDeviceIdValueDoesNotMatchDefaultLanguageValue() throws Exception
    {
        testGetAboutForSupportedLanguagesFailsIfAnyNonLocalizedFieldValueDoesNotMatchDefaultLanguageValue(AboutKeys.ABOUT_DEVICE_ID, "new device");
    }

    @Test
    public void testGetAboutForSupportedLanguagesFailsIfModelNumberValueDoesNotMatchDefaultLanguageValue() throws Exception
    {
        testGetAboutForSupportedLanguagesFailsIfAnyNonLocalizedFieldValueDoesNotMatchDefaultLanguageValue(AboutKeys.ABOUT_MODEL_NUMBER, "new model");
    }

    @Test
    public void testGetAboutForSupportedLanguagesFailsIfSoftwareVersionValueDoesNotMatchDefaultLanguageValue() throws Exception
    {
        testGetAboutForSupportedLanguagesFailsIfAnyNonLocalizedFieldValueDoesNotMatchDefaultLanguageValue(AboutKeys.ABOUT_SOFTWARE_VERSION, "new software version");
    }

    @Test
    public void testGetAboutForSupportedLanguagesFailsIfAJSoftwareVersionValueDoesNotMatchDefaultLanguageValue() throws Exception
    {
        testGetAboutForSupportedLanguagesFailsIfAnyNonLocalizedFieldValueDoesNotMatchDefaultLanguageValue(AboutKeys.ABOUT_AJ_SOFTWARE_VERSION, "new aj software version");
    }

    @Test
    public void testGetAboutForSupportedLanguagesPassesEvenIfDeviceNameValueDoesNotMatchDefaultLanguageValue() throws Exception
    {
        testGetAboutForSupportedLanguagesPassesEvenIfAnyLocalizedFieldValueDoesNotMatchDefaultLanguageValue(AboutKeys.ABOUT_DEVICE_NAME, "new device name");
    }

    @Test
    public void testGetAboutForSupportedLanguagesPassesEvenIfAppNameValueDoesNotMatchDefaultLanguageValue() throws Exception
    {
        testGetAboutForSupportedLanguagesPassesEvenIfAnyLocalizedFieldValueDoesNotMatchDefaultLanguageValue(AboutKeys.ABOUT_APP_NAME, "new app name");
    }

    @Test
    public void testGetAboutForSupportedLanguagesPassesEvenIfManufacturereValueDoesNotMatchDefaultLanguageValue() throws Exception
    {
        testGetAboutForSupportedLanguagesPassesEvenIfAnyLocalizedFieldValueDoesNotMatchDefaultLanguageValue(AboutKeys.ABOUT_MANUFACTURER, "new manufacturer");
    }

    @Test
    public void testGetAboutForSupportedLanguagesPassesEvenIfDescriptionValueDoesNotMatchDefaultLanguageValue() throws Exception
    {
        testGetAboutForSupportedLanguagesPassesEvenIfAnyLocalizedFieldValueDoesNotMatchDefaultLanguageValue(AboutKeys.ABOUT_DESCRIPTION, "new description");
    }

    @Test
    public void testGetAboutForSupportedLanguagesFailsIfDateOfManufactureValueDoesNotExistForDefaultLanguageAndExistsForSupportedLanguage() throws Exception
    {
        String[] supportedLanguages =
        { "en", "hi" };
        Map<String, Object> defaultLanguageAboutMap = TransportUtil.fromVariantMap(appAboutDataMap);
        defaultLanguageAboutMap.put(AboutKeys.ABOUT_SUPPORTED_LANGUAGES, supportedLanguages);
        when(mockAboutClient.getAbout("en")).thenReturn(defaultLanguageAboutMap);

        Map<String, Object> aboutMap = TransportUtil.fromVariantMap(appAboutDataMap);

        aboutMap.put(AboutKeys.ABOUT_DATE_OF_MANUFACTURE, "2012-12-31");
        aboutMap.put(AboutKeys.ABOUT_SUPPORTED_LANGUAGES, supportedLanguages);
        when(mockAboutClient.getAbout("hi")).thenReturn(aboutMap);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_07(), AboutKeys.ABOUT_DATE_OF_MANUFACTURE
                + " value returned from the GetAbout for language hi does not match default language value;");
    }

    @Test
    public void testGetAboutForSupportedLanguagesFailsIfDateOfManufactureValueExistsForDefaultLanguageAndDoesNotExistForSupportedLanguage() throws Exception
    {
        String[] supportedLanguages =
        { "en", "hi" };
        Map<String, Object> defaultLanguageAboutMap = TransportUtil.fromVariantMap(appAboutDataMap);
        defaultLanguageAboutMap.put(AboutKeys.ABOUT_SUPPORTED_LANGUAGES, supportedLanguages);
        defaultLanguageAboutMap.put(AboutKeys.ABOUT_DATE_OF_MANUFACTURE, "2012-12-31");
        when(mockAboutClient.getAbout("en")).thenReturn(defaultLanguageAboutMap);

        Map<String, Object> aboutMap = TransportUtil.fromVariantMap(appAboutDataMap);
        aboutMap.put(AboutKeys.ABOUT_SUPPORTED_LANGUAGES, supportedLanguages);
        when(mockAboutClient.getAbout("hi")).thenReturn(aboutMap);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_07(), AboutKeys.ABOUT_DATE_OF_MANUFACTURE
                + " value returned from the GetAbout for language hi does not match default language value;");
    }

    @Test
    public void testGetAboutForSupportedLanguagesFailsIfDateOfManufactureValueDoesNotMatchDefaultLanguageValue() throws Exception
    {
        String[] supportedLanguages =
        { "en", "hi" };
        Map<String, Object> defaultLanguageAboutMap = TransportUtil.fromVariantMap(appAboutDataMap);
        defaultLanguageAboutMap.put(AboutKeys.ABOUT_SUPPORTED_LANGUAGES, supportedLanguages);
        defaultLanguageAboutMap.put(AboutKeys.ABOUT_DATE_OF_MANUFACTURE, "2012-12-31");
        when(mockAboutClient.getAbout("en")).thenReturn(defaultLanguageAboutMap);

        Map<String, Object> aboutMap = TransportUtil.fromVariantMap(appAboutDataMap);
        aboutMap.put(AboutKeys.ABOUT_SUPPORTED_LANGUAGES, supportedLanguages);
        aboutMap.put(AboutKeys.ABOUT_DATE_OF_MANUFACTURE, "2012-12-30");
        when(mockAboutClient.getAbout("hi")).thenReturn(aboutMap);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_07(), AboutKeys.ABOUT_DATE_OF_MANUFACTURE
                + " value returned from the GetAbout for language hi does not match default language value; expected:<2012-12-3[1]> but was:<2012-12-3[0]>");
    }

    @Test
    public void testGetAboutForSupportedLanguagesPassesIfDateOfManufactureValueMatchesDefaultLanguageValue() throws Exception
    {
        String[] supportedLanguages =
        { "en", "hi" };
        Map<String, Object> defaultLanguageAboutMap = TransportUtil.fromVariantMap(appAboutDataMap);
        defaultLanguageAboutMap.put(AboutKeys.ABOUT_SUPPORTED_LANGUAGES, supportedLanguages);
        defaultLanguageAboutMap.put(AboutKeys.ABOUT_DATE_OF_MANUFACTURE, "2012-12-31");
        when(mockAboutClient.getAbout("en")).thenReturn(defaultLanguageAboutMap);

        Map<String, Object> aboutMap = TransportUtil.fromVariantMap(appAboutDataMap);
        aboutMap.put(AboutKeys.ABOUT_SUPPORTED_LANGUAGES, supportedLanguages);
        aboutMap.put(AboutKeys.ABOUT_DATE_OF_MANUFACTURE, "2012-12-31");
        when(mockAboutClient.getAbout("hi")).thenReturn(aboutMap);

        executeTestMethod(getTestWrapperFor_v1_07());
    }

    @Test
    public void testGetAboutForSupportedLanguagesFailsIfHardwareVersionValueDoesNotExistForDefaultLanguageAndExistsForSupportedLanguage() throws Exception
    {
        String[] supportedLanguages =
        { "en", "hi" };
        Map<String, Object> defaultLanguageAboutMap = TransportUtil.fromVariantMap(appAboutDataMap);
        defaultLanguageAboutMap.put(AboutKeys.ABOUT_SUPPORTED_LANGUAGES, supportedLanguages);
        when(mockAboutClient.getAbout("en")).thenReturn(defaultLanguageAboutMap);

        Map<String, Object> aboutMap = TransportUtil.fromVariantMap(appAboutDataMap);

        aboutMap.put(AboutKeys.ABOUT_HARDWARE_VERSION, "hv");
        aboutMap.put(AboutKeys.ABOUT_SUPPORTED_LANGUAGES, supportedLanguages);
        when(mockAboutClient.getAbout("hi")).thenReturn(aboutMap);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_07(), AboutKeys.ABOUT_HARDWARE_VERSION
                + " value returned from the GetAbout for language hi does not match default language value;");
    }

    @Test
    public void testGetAboutForSupportedLanguagesFailsIfHardwareVersionValueExistsForDefaultLanguageAndDoesNotExistForSupportedLanguage() throws Exception
    {
        String[] supportedLanguages =
        { "en", "hi" };
        Map<String, Object> defaultLanguageAboutMap = TransportUtil.fromVariantMap(appAboutDataMap);
        defaultLanguageAboutMap.put(AboutKeys.ABOUT_SUPPORTED_LANGUAGES, supportedLanguages);
        defaultLanguageAboutMap.put(AboutKeys.ABOUT_HARDWARE_VERSION, "hv");
        when(mockAboutClient.getAbout("en")).thenReturn(defaultLanguageAboutMap);

        Map<String, Object> aboutMap = TransportUtil.fromVariantMap(appAboutDataMap);
        aboutMap.put(AboutKeys.ABOUT_SUPPORTED_LANGUAGES, supportedLanguages);
        when(mockAboutClient.getAbout("hi")).thenReturn(aboutMap);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_07(), AboutKeys.ABOUT_HARDWARE_VERSION
                + " value returned from the GetAbout for language hi does not match default language value;");
    }

    @Test
    public void testGetAboutForSupportedLanguagesFailsIfHardwareVersionValueDoesNotMatchDefaultLanguageValue() throws Exception
    {
        String[] supportedLanguages =
        { "en", "hi" };
        Map<String, Object> defaultLanguageAboutMap = TransportUtil.fromVariantMap(appAboutDataMap);
        defaultLanguageAboutMap.put(AboutKeys.ABOUT_SUPPORTED_LANGUAGES, supportedLanguages);
        defaultLanguageAboutMap.put(AboutKeys.ABOUT_HARDWARE_VERSION, "hv");
        when(mockAboutClient.getAbout("en")).thenReturn(defaultLanguageAboutMap);

        Map<String, Object> aboutMap = TransportUtil.fromVariantMap(appAboutDataMap);
        aboutMap.put(AboutKeys.ABOUT_SUPPORTED_LANGUAGES, supportedLanguages);
        aboutMap.put(AboutKeys.ABOUT_HARDWARE_VERSION, "new hv");
        when(mockAboutClient.getAbout("hi")).thenReturn(aboutMap);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_07(), AboutKeys.ABOUT_HARDWARE_VERSION
                + " value returned from the GetAbout for language hi does not match default language value; expected:<[]hv> but was:<[new ]hv>");
    }

    @Test
    public void testGetAboutForSupportedLanguagesPassesIfHardwareVersionValueMatchesDefaultLanguageValue() throws Exception
    {
        String[] supportedLanguages =
        { "en", "hi" };
        Map<String, Object> defaultLanguageAboutMap = TransportUtil.fromVariantMap(appAboutDataMap);
        defaultLanguageAboutMap.put(AboutKeys.ABOUT_SUPPORTED_LANGUAGES, supportedLanguages);
        defaultLanguageAboutMap.put(AboutKeys.ABOUT_HARDWARE_VERSION, "hv");
        when(mockAboutClient.getAbout("en")).thenReturn(defaultLanguageAboutMap);

        Map<String, Object> aboutMap = TransportUtil.fromVariantMap(appAboutDataMap);
        aboutMap.put(AboutKeys.ABOUT_SUPPORTED_LANGUAGES, supportedLanguages);
        aboutMap.put(AboutKeys.ABOUT_HARDWARE_VERSION, "hv");
        when(mockAboutClient.getAbout("hi")).thenReturn(aboutMap);

        executeTestMethod(getTestWrapperFor_v1_07());
    }

    @Test
    public void testGetAboutForSupportedLanguagesFailsIfSupportUrlValueDoesNotExistForDefaultLanguageAndExistsForSupportedLanguage() throws Exception
    {
        String[] supportedLanguages =
        { "en", "hi" };
        Map<String, Object> defaultLanguageAboutMap = TransportUtil.fromVariantMap(appAboutDataMap);
        defaultLanguageAboutMap.put(AboutKeys.ABOUT_SUPPORTED_LANGUAGES, supportedLanguages);
        when(mockAboutClient.getAbout("en")).thenReturn(defaultLanguageAboutMap);

        Map<String, Object> aboutMap = TransportUtil.fromVariantMap(appAboutDataMap);

        aboutMap.put(AboutKeys.ABOUT_SUPPORT_URL, "http://url");
        aboutMap.put(AboutKeys.ABOUT_SUPPORTED_LANGUAGES, supportedLanguages);
        when(mockAboutClient.getAbout("hi")).thenReturn(aboutMap);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_07(), AboutKeys.ABOUT_SUPPORT_URL
                + " value returned from the GetAbout for language hi does not match default language value;");
    }

    @Test
    public void testGetAboutForSupportedLanguagesFailsIfSupportUrlValueExistsForDefaultLanguageAndDoesNotExistForSupportedLanguage() throws Exception
    {
        String[] supportedLanguages =
        { "en", "hi" };
        Map<String, Object> defaultLanguageAboutMap = TransportUtil.fromVariantMap(appAboutDataMap);
        defaultLanguageAboutMap.put(AboutKeys.ABOUT_SUPPORTED_LANGUAGES, supportedLanguages);
        defaultLanguageAboutMap.put(AboutKeys.ABOUT_SUPPORT_URL, "http://url");
        when(mockAboutClient.getAbout("en")).thenReturn(defaultLanguageAboutMap);

        Map<String, Object> aboutMap = TransportUtil.fromVariantMap(appAboutDataMap);
        aboutMap.put(AboutKeys.ABOUT_SUPPORTED_LANGUAGES, supportedLanguages);
        when(mockAboutClient.getAbout("hi")).thenReturn(aboutMap);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_07(), AboutKeys.ABOUT_SUPPORT_URL
                + " value returned from the GetAbout for language hi does not match default language value;");
    }

    @Test
    public void testGetAboutForSupportedLanguagesFailsIfSupportUrlValueDoesNotMatchDefaultLanguageValue() throws Exception
    {
        String[] supportedLanguages =
        { "en", "hi" };
        Map<String, Object> defaultLanguageAboutMap = TransportUtil.fromVariantMap(appAboutDataMap);
        defaultLanguageAboutMap.put(AboutKeys.ABOUT_SUPPORTED_LANGUAGES, supportedLanguages);
        defaultLanguageAboutMap.put(AboutKeys.ABOUT_SUPPORT_URL, "http://url");
        when(mockAboutClient.getAbout("en")).thenReturn(defaultLanguageAboutMap);

        Map<String, Object> aboutMap = TransportUtil.fromVariantMap(appAboutDataMap);
        aboutMap.put(AboutKeys.ABOUT_SUPPORTED_LANGUAGES, supportedLanguages);
        aboutMap.put(AboutKeys.ABOUT_SUPPORT_URL, "http://newurl");
        when(mockAboutClient.getAbout("hi")).thenReturn(aboutMap);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_07(), AboutKeys.ABOUT_SUPPORT_URL
                + " value returned from the GetAbout for language hi does not match default language value; expected:<http://[]url> but was:<http://[new]url>");
    }

    @Test
    public void testGetAboutForSupportedLanguagesPassesIfSupportUrlValueMatchesDefaultLanguageValue() throws Exception
    {
        String[] supportedLanguages =
        { "en", "hi" };
        Map<String, Object> defaultLanguageAboutMap = TransportUtil.fromVariantMap(appAboutDataMap);
        defaultLanguageAboutMap.put(AboutKeys.ABOUT_SUPPORTED_LANGUAGES, supportedLanguages);
        defaultLanguageAboutMap.put(AboutKeys.ABOUT_SUPPORT_URL, "http://url");
        when(mockAboutClient.getAbout("en")).thenReturn(defaultLanguageAboutMap);

        Map<String, Object> aboutMap = TransportUtil.fromVariantMap(appAboutDataMap);
        aboutMap.put(AboutKeys.ABOUT_SUPPORTED_LANGUAGES, supportedLanguages);
        aboutMap.put(AboutKeys.ABOUT_SUPPORT_URL, "http://url");
        when(mockAboutClient.getAbout("hi")).thenReturn(aboutMap);

        executeTestMethod(getTestWrapperFor_v1_07());
    }

    @Test
    public void getAboutTestCaseUnspecifiedLanguage() throws Throwable
    {
        when(mockAboutClient.getAbout("")).thenReturn(TransportUtil.fromVariantMap(appAboutDataMap));
        when(mockAboutClient.getAbout("en")).thenReturn(TransportUtil.fromVariantMap(appAboutDataMap));

        executeTestMethod(getTestWrapperFor_v1_08());

        verify(mockAboutClient).getAbout(defaultLanguage);
        verify(mockAboutClient).getAbout("");
        verify(mockServiceHelper).release();
    }

    @Test
    public void getAboutTestCaseUnsupportedLanguage() throws Exception
    {
        when(mockAboutClient.getAbout(UNSUPPORTED_LANGUAGE)).thenThrow(new LanguageNotSupportedException());

        executeTestMethod(getTestWrapperFor_v1_09());

        verify(mockAboutClient).getAbout(UNSUPPORTED_LANGUAGE);
    }

    @Test
    public void testGetAboutForUnsupportedLanguageFailsIfLanguageNotSupportedExceptionNotThrown() throws Exception
    {
        ErrorReplyBusException busException = Mockito.mock(ErrorReplyBusException.class);
        when(busException.getErrorName()).thenReturn("bus exception");
        when(mockAboutClient.getAbout(UNSUPPORTED_LANGUAGE)).thenThrow(busException);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_09(), "Wrong errorName expected:<[org.alljoyn.Error.LanguageNotSupported]> but was:<[bus exception]>");

        verify(mockAboutClient).getAbout(UNSUPPORTED_LANGUAGE);
    }

    @Test
    public void testGetAboutForUnsupportedLanguageFailsIfExceptionNotThrown() throws Exception
    {
        when(mockAboutClient.getAbout(UNSUPPORTED_LANGUAGE)).thenReturn(TransportUtil.fromVariantMap(appAboutDataMap));

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_09(), "Calling getAbout on the About interface with an unsupported language must throw an exception");

        verify(mockAboutClient).getAbout(UNSUPPORTED_LANGUAGE);
    }

    @Test
    public void getDeviceIcon() throws Exception
    {
        loadIconBitmap();
        byte[] imageByteArr = getImageByteArray();

        when(mockAboutIconClient.getSize()).thenReturn(imageByteArr.length);
        when(mockAboutIconClient.getMimeType()).thenReturn("image/png");
        when(mockAboutIconClient.GetContent()).thenReturn(imageByteArr);

        executeTestMethod(getTestWrapperFor_v1_10());

        verify(mockAboutIconClient).getSize();
        verify(mockAboutIconClient).getMimeType();
        verify(mockServiceHelper).release();
    }

    @Test
    public void getDeviceIconSizeEmpty() throws Exception
    {
        loadIconBitmap();
        byte[] imageByteArr = getImageByteArray();

        when(mockAboutIconClient.getSize()).thenReturn(0);
        when(mockAboutIconClient.getMimeType()).thenReturn("image/png");
        when(mockAboutIconClient.GetContent()).thenReturn(imageByteArr);

        executeTestMethod(getTestWrapperFor_v1_10());

        verify(mockAboutIconClient).getSize();
        verify(mockAboutIconClient).getMimeType();
        verify(mockServiceHelper).release();
        verify(mockTestContext).addNote("GetContent() method is not supported");
    }

    @Test
    public void getDeviceIconEmptyMimeType() throws Exception
    {
        loadIconBitmap();
        byte[] imageByteArr = getImageByteArray();

        when(mockAboutIconClient.getSize()).thenReturn(iconSize);
        when(mockAboutIconClient.getMimeType()).thenReturn("");
        when(mockAboutIconClient.GetContent()).thenReturn(imageByteArr);

        executeTestMethod(getTestWrapperFor_v1_10());

        verify(mockAboutIconClient).getMimeType();
        verify(mockServiceHelper).release();
        verify(mockTestContext).addNote("GetContent() method is not supported");
    }

    @Test
    public void getDeviceIconNullMimeType() throws Exception
    {
        loadIconBitmap();
        byte[] imageByteArr = getImageByteArray();

        when(mockAboutIconClient.getSize()).thenReturn(iconSize);
        when(mockAboutIconClient.getMimeType()).thenReturn(null);
        when(mockAboutIconClient.GetContent()).thenReturn(imageByteArr);

        executeTestMethod(getTestWrapperFor_v1_10());

        verify(mockAboutIconClient).getMimeType();
        verify(mockServiceHelper).release();
        verify(mockTestContext).addNote("GetContent() method is not supported");
    }

    @Test
    public void getDeviceIconPassesWhenInterfaceIsNotSupported() throws Exception
    {
        when(mockAboutAnnouncement.supportsInterface(IconTransport.INTERFACE_NAME)).thenReturn(false);

        executeTestMethod(getTestWrapperFor_v1_10());

        verify(mockTestContext).addNote("Device does not support AboutIcon");
    }

    @Test
    public void getDeviceIconFailsIfIconSizeIsNotLessThanMaxArrayLength() throws Exception
    {

        when(mockAboutIconClient.getSize()).thenReturn(BusAttachment.ALLJOYN_MAX_ARRAY_LEN);
        when(mockAboutIconClient.getMimeType()).thenReturn("image/png");

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_10(), "Icon size should be less than " + BusAttachment.ALLJOYN_MAX_ARRAY_LEN);
    }

    @Test
    public void getDeviceIconFailsIfMimeTypeIsInvalid() throws Exception
    {

        when(mockAboutIconClient.getSize()).thenReturn(BusAttachment.ALLJOYN_MAX_ARRAY_LEN);
        when(mockAboutIconClient.getMimeType()).thenReturn("audio/png");

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_10(), "Mime type should match pattern image/*");
    }

    @Test
    public void getDeviceIconFailsIfIconSizesDontMatch() throws Exception
    {
        loadIconBitmap();
        byte[] imageByteArr = getImageByteArray();

        when(mockAboutIconClient.getSize()).thenReturn(iconSize + 1);
        when(mockAboutIconClient.getMimeType()).thenReturn("image/png");
        when(mockAboutIconClient.GetContent()).thenReturn(imageByteArr);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_10(), "Size of GetContent does not match iconSize expected:<3248> but was:<41>");
    }

    @Test
    public void getDeviceIconUrlPassesWhenInterfaceIsNotSupported() throws Exception
    {
        when(mockAboutAnnouncement.supportsInterface(IconTransport.INTERFACE_NAME)).thenReturn(false);

        executeTestMethod(getTestWrapperFor_v1_11());

        verify(mockTestContext).addNote("Device does not support AboutIcon");
    }

    @Test
    public void getValidUrl() throws Exception
    {
        when(mockAboutIconClient.GetUrl()).thenReturn("http://sample.com/icon.png");
        verifyUrl(mockAboutIconClient);
    }

    @Test
    public void getEmptyUrl() throws Exception
    {
        when(mockAboutIconClient.GetUrl()).thenReturn("");
        verifyUrl(mockAboutIconClient);

        verify(mockTestContext).addNote("URL returned is null/empty");
    }

    @Test
    public void getNullUrl() throws Exception
    {
        when(mockAboutIconClient.GetUrl()).thenReturn(null);
        verifyUrl(mockAboutIconClient);

        verify(mockTestContext).addNote("URL returned is null/empty");
    }

    @Test
    public void getMalformedUrl() throws Exception
    {
        when(mockAboutIconClient.GetUrl()).thenReturn("MalfomedUrl.png");

        verifyUrl(mockAboutIconClient, "The received icon URL: 'MalfomedUrl.png' isn't valid");
    }

    @Test
    public void resourcesAreReleasedIfConnectingToAboutClientThrowsException() throws Exception
    {
        when(mockServiceHelper.connectAboutClient(mockAboutAnnouncement)).thenThrow(new RuntimeException());

        try
        {
            aboutTestSuite.setUp();
            fail("setup should have thrown exception");
        }
        catch (RuntimeException runtimeException)
        {
            verify(mockServiceHelper).release();
            verify(mockAboutClient, times(0)).disconnect();
        }
    }

    @Test
    public void resourcesAreReleasedIfConnectingToAboutIconClientThrowsException() throws Exception
    {
        when(mockServiceHelper.connectAboutIconClient(mockAboutAnnouncement)).thenThrow(new RuntimeException());

        try
        {
            aboutTestSuite.setUp();
            fail("setup should have thrown exception");
        }
        catch (RuntimeException runtimeException)
        {
            verify(mockServiceHelper).release();
            verify(mockAboutClient).disconnect();
        }
    }

    @Test(expected = ClassCastException.class)
    public void exceptionCaughtInSetupIsThrownBackEvenIfAnotherExceptionIsCaughtWhenReleasingResources() throws Exception
    {
        when(mockServiceHelper.connectAboutClient(mockAboutAnnouncement)).thenThrow(new ClassCastException());
        doThrow(new AndroidRuntimeException()).when(mockServiceHelper).release();
        aboutTestSuite.setUp();
    }

    @Test
    public void getIntrospectorTest() throws Exception
    {
        aboutTestSuite.setUp();
        BusIntrospector busIntrospector = aboutTestSuite.getIntrospector();
        assertNotNull(busIntrospector);
        assertEquals(mockIntrospector, busIntrospector);
        assertEquals(busIntrospector, aboutTestSuite.getIntrospector());
    }

    @Test
    public void getInterfaceValidatorTest() throws Exception
    {
        aboutTestSuite = new AboutTestSuite();
        InterfaceValidator interfaceValidator = aboutTestSuite.getInterfaceValidator();
        assertNotNull(interfaceValidator);
        assertFalse(interfaceValidator.equals(aboutTestSuite.getInterfaceValidator()));
    }

    @Test
    public void getServiceHelperTest() throws Exception
    {
        aboutTestSuite = new AboutTestSuite();
        ServiceHelper serviceHelper = aboutTestSuite.getServiceHelper();
        assertNotNull(serviceHelper);
        assertFalse(serviceHelper.equals(aboutTestSuite.getServiceHelper()));
    }

    private void testGetAboutForDefaultLanguageFailsIfAnyFieldValueSignatureIsInvalid(String fieldName, Object newFieldValue, String signature, String newSignature)
            throws Exception
    {
        Map<String, Object> copiedAboutMap = new HashMap<String, Object>();
        copiedAboutMap.putAll(TransportUtil.fromVariantMap(appAboutDataMap));
        copiedAboutMap.put(fieldName, newFieldValue);

        when(mockAboutClient.getAbout(defaultLanguage)).thenReturn(copiedAboutMap);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_06(),
                String.format("Signature does not match for required field %s for language %s expected:<[%s]> but was:<[%s]>", fieldName, defaultLanguage, signature, newSignature));
    }

    private void testAboutAnnouncementFailsIfFieldIsMissingInAboutMap(String fieldName) throws Exception, InterruptedException
    {
        when(mockAboutAnnouncement.getObjectDescriptions()).thenReturn(new BusObjectDescription[]
        { getBusObjectDescription() });
        Map<String, Variant> aboutMap = appAboutDataMap;
        aboutMap.remove(fieldName);
        when(mockAboutAnnouncement.getAboutData()).thenReturn(aboutMap);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_01(), fieldName + " is a required field");

    }

    private void internalAnnouncementConsistentWithBusExceptionTest(Exception encounteredException, String expectedMessage) throws Exception
    {
        setupConditionsForAboutAnnouncementConsistentWithBusObjectsTest();
        when(mockIntrospectionInterface.getName()).thenReturn(STANDARDIZED_INTERFACE_NAME);
        doThrow(encounteredException).when(mockIntrospector).introspect(PATH);

        setupMockIntrospector(STANDARDIZED_INTERFACE_NAME, PATH);

        executeTestMethodThrowsException(getTestWrapperFor_v1_04(), expectedMessage);
    }

    private void setupConditionsForAboutAnnouncementConsistentWithBusObjectsTest() throws BusException, IOException, ParserConfigurationException, SAXException
    {
        when(mockAboutAnnouncement.getObjectDescriptions()).thenReturn(new BusObjectDescription[]
        { getBusObjectDescription() });
        when(mockIntrospector.introspect(PATH)).thenReturn(mockNodeDetail);
        when(mockNodeDetail.getIntrospectionNode()).thenReturn(mockIntrospectionNode);
        List<IntrospectionInterface> introspectionInterfaceList = new ArrayList<IntrospectionInterface>();
        introspectionInterfaceList.add(mockIntrospectionInterface);
        when(mockIntrospectionNode.getInterfaces()).thenReturn(introspectionInterfaceList);
    }

    private BusObjectDescription getBusObjectDescription()
    {
        BusObjectDescription busObjectDescription = new BusObjectDescription();
        busObjectDescription.setPath(PATH);
        busObjectDescription.setInterfaces(new String[]
        { "org.alljoyn.About" });
        return busObjectDescription;
    }

    private void setupMockIntrospector(String interfaceName, String path) throws BusException, IOException, ParserConfigurationException, SAXException
    {
        when(mockServiceHelper.getBusIntrospector(mockAboutClient)).thenReturn(mockIntrospector);
        when(mockAboutClient.getPeerName()).thenReturn(PEER_NAME);
        when(mockAboutClient.getSessionId()).thenReturn(SESSION_ID);
    }

    private void setupAboutClientForSupportedLanguagesTest(String[] supportedLanguages) throws BusException, Exception
    {
        setupAboutClientForSupportedLanguagesTest("en", supportedLanguages);
    }

    private void setupAboutClientForSupportedLanguagesTest(String language, String[] supportedLanguages) throws BusException, Exception
    {
        Map<String, Object> aboutMap = TransportUtil.fromVariantMap(appAboutDataMap);
        aboutMap.put(AboutKeys.ABOUT_SUPPORTED_LANGUAGES, supportedLanguages);
        when(mockAboutClient.getAbout(language)).thenReturn(aboutMap);
    }

    private void testGetAboutForSupportedLanguagesFailsIfAnyFieldValueIsNotPresentForAnyLanguage(String missingFieldName) throws BusException, Exception, InterruptedException
    {
        String[] supportedLanguages =
        { "en", "hi" };
        Map<String, Object> defaultLanguageAboutMap = TransportUtil.fromVariantMap(appAboutDataMap);
        defaultLanguageAboutMap.put(AboutKeys.ABOUT_SUPPORTED_LANGUAGES, supportedLanguages);
        when(mockAboutClient.getAbout("en")).thenReturn(defaultLanguageAboutMap);

        Map<String, Object> aboutMap = TransportUtil.fromVariantMap(appAboutDataMap);
        aboutMap.remove(missingFieldName);

        if (!AboutKeys.ABOUT_SUPPORTED_LANGUAGES.equals(missingFieldName))
        {
            aboutMap.put(AboutKeys.ABOUT_SUPPORTED_LANGUAGES, supportedLanguages);
        }

        when(mockAboutClient.getAbout("hi")).thenReturn(aboutMap);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_07(), missingFieldName + " is a required field for language hi");

    }

    private void testGetAboutForSupportedLanguagesFailsIfAnyNonLocalizedFieldValueDoesNotMatchDefaultLanguageValue(String fieldName, String newFieldValue) throws BusException,
            Exception, InterruptedException
    {
        String[] supportedLanguages =
        { "en", "hi" };
        Map<String, Object> defaultLanguageAboutMap = TransportUtil.fromVariantMap(appAboutDataMap);
        defaultLanguageAboutMap.put(AboutKeys.ABOUT_SUPPORTED_LANGUAGES, supportedLanguages);
        when(mockAboutClient.getAbout("en")).thenReturn(defaultLanguageAboutMap);

        Map<String, Object> aboutMap = TransportUtil.fromVariantMap(appAboutDataMap);
        String fieldValue = aboutMap.get(fieldName).toString();
        aboutMap.put(fieldName, newFieldValue);
        aboutMap.put(AboutKeys.ABOUT_SUPPORTED_LANGUAGES, supportedLanguages);
        when(mockAboutClient.getAbout("hi")).thenReturn(aboutMap);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_07(), fieldName
                + " value returned from the GetAbout for language hi does not match default language value; expected:<[" + fieldValue + "]> but was:<[" + newFieldValue + "]>");
    }

    private void testGetAboutForSupportedLanguagesPassesEvenIfAnyLocalizedFieldValueDoesNotMatchDefaultLanguageValue(String fieldName, String newFieldValue) throws BusException,
            Exception, InterruptedException
    {
        String[] supportedLanguages =
        { "en", "hi" };
        Map<String, Object> defaultLanguageAboutMap = TransportUtil.fromVariantMap(appAboutDataMap);
        defaultLanguageAboutMap.put(AboutKeys.ABOUT_SUPPORTED_LANGUAGES, supportedLanguages);
        when(mockAboutClient.getAbout("en")).thenReturn(defaultLanguageAboutMap);

        Map<String, Object> aboutMap = TransportUtil.fromVariantMap(appAboutDataMap);
        aboutMap.put(fieldName, newFieldValue);
        aboutMap.put(AboutKeys.ABOUT_SUPPORTED_LANGUAGES, supportedLanguages);
        when(mockAboutClient.getAbout("hi")).thenReturn(aboutMap);

        executeTestMethod(getTestWrapperFor_v1_07());
    }

    private void loadIconBitmap() throws IOException
    {
        InputStream is = getClass().getClassLoader().getResourceAsStream("sample_app_icon.png");
        iconSize = is.available();
        iconBitmap = BitmapFactory.decodeStream(is);
        is.close();
    }

    private byte[] getImageByteArray() throws IOException
    {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        iconBitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
        byte[] imageByteArr = outStream.toByteArray();
        outStream.close();

        return imageByteArr;
    }

    private void verifyUrl(AboutIconClient mockAboutIconClient) throws Exception
    {
        executeTestMethod(getTestWrapperFor_v1_11());
        verify(mockAboutIconClient).GetUrl();
    }

    private void verifyUrl(AboutIconClient mockAboutIconClient, String assertionMessage) throws Exception
    {
        executeTestMethodFailsAssertion(getTestWrapperFor_v1_11(), assertionMessage);
        verify(mockAboutIconClient).GetUrl();
    }

    protected TestWrapper getTestWrapperFor_v1_01()
    {
        return new TestWrapper()
        {
            @Override
            public void executeTestMethod() throws Exception
            {
                aboutTestSuite.testAbout_v1_01_AboutAnnouncement();
            }
        };
    }

    protected TestWrapper getTestWrapperFor_v1_02()
    {
        return new TestWrapper()
        {
            @Override
            public void executeTestMethod() throws Exception
            {
                aboutTestSuite.testAbout_v1_02_AboutVersion();
            }
        };
    }

    protected TestWrapper getTestWrapperFor_v1_03()
    {
        return new TestWrapper()
        {
            @Override
            public void executeTestMethod() throws Exception
            {
                aboutTestSuite.testAbout_v1_03_GetObjectDescription();
            }
        };
    }

    protected TestWrapper getTestWrapperFor_v1_04()
    {
        return new TestWrapper()
        {
            @Override
            public void executeTestMethod() throws Exception
            {
                aboutTestSuite.testAbout_v1_04_AboutAnnouncementConsistentWithBusObjects();
            }
        };
    }

    protected TestWrapper getTestWrapperFor_v1_05()
    {
        return new TestWrapper()
        {
            @Override
            public void executeTestMethod() throws Exception
            {
                aboutTestSuite.testAbout_v1_05_StandardizedInterfacesMatchDefinitions();
            }
        };
    }

    protected TestWrapper getTestWrapperFor_v1_06()
    {
        return new TestWrapper()
        {
            @Override
            public void executeTestMethod() throws Exception
            {
                aboutTestSuite.testAbout_v1_06_GetAboutForDefaultLanguage();
            }
        };
    }

    protected TestWrapper getTestWrapperFor_v1_07()
    {
        return new TestWrapper()
        {
            @Override
            public void executeTestMethod() throws Exception
            {
                aboutTestSuite.testAbout_v1_07_GetAboutForSupportedLanguages();
            }
        };
    }

    protected TestWrapper getTestWrapperFor_v1_08()
    {
        return new TestWrapper()
        {
            @Override
            public void executeTestMethod() throws Exception
            {
                aboutTestSuite.testAbout_v1_08_GetAboutForUnspecifiedLanguage();
            }
        };
    }

    protected TestWrapper getTestWrapperFor_v1_09()
    {
        return new TestWrapper()
        {
            @Override
            public void executeTestMethod() throws Exception
            {
                aboutTestSuite.testAbout_v1_09_GetAboutForUnsupportedLanguage();
            }
        };
    }

    protected TestWrapper getTestWrapperFor_v1_10()
    {
        return new TestWrapper()
        {
            @Override
            public void executeTestMethod() throws Exception
            {
                aboutTestSuite.testAbout_v1_10_GetAboutIcon();
            }
        };
    }

    protected TestWrapper getTestWrapperFor_v1_11()
    {
        return new TestWrapper()
        {
            @Override
            public void executeTestMethod() throws Exception
            {
                aboutTestSuite.testAbout_v1_11_GetAboutIconValidUrl();
            }
        };
    }

    private void verifyAssertFailureOnMissingField(String key) throws InterruptedException, Exception, BusException
    {
        Map<String, Object> copiedAboutMap = new HashMap<String, Object>();
        copiedAboutMap.putAll(TransportUtil.fromVariantMap(appAboutDataMap));
        copiedAboutMap.remove(key);

        when(mockAboutClient.getAbout(defaultLanguage)).thenReturn(copiedAboutMap);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_06(), key + " is a required field for language en");

        verify(mockAboutClient).getAbout(defaultLanguage);
    }

    @Override
    protected void executeTestMethod(TestWrapper testWrapper) throws Exception
    {
        aboutTestSuite.setUp();
        try
        {
            testWrapper.executeTestMethod();
        }
        finally
        {
            aboutTestSuite.tearDown();
        }
    }

}