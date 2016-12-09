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
package org.alljoyn.validation.testing.suites.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.alljoyn.about.AboutKeys;
import org.alljoyn.about.client.AboutClient;
import org.alljoyn.bus.BusAttachment;
import org.alljoyn.bus.BusException;
import org.alljoyn.bus.ErrorReplyBusException;
import org.alljoyn.bus.Mutable.StringValue;
import org.alljoyn.bus.Variant;
import org.alljoyn.config.ConfigService;
import org.alljoyn.config.client.ConfigClient;
import org.alljoyn.onboarding.transport.OnboardingTransport;
import org.alljoyn.services.common.BusObjectDescription;
import org.alljoyn.services.common.LanguageNotSupportedException;
import org.alljoyn.services.common.ServiceAvailabilityListener;
import org.alljoyn.services.common.utils.TransportUtil;
import org.alljoyn.validation.framework.AppUnderTestDetails;
import org.alljoyn.validation.framework.UserInputDetails;
import org.alljoyn.validation.framework.ValidationTestContext;
import org.alljoyn.validation.testing.suites.AllJoynErrorReplyCodes;
import org.alljoyn.validation.testing.suites.BaseTestSuiteTest;
import org.alljoyn.validation.testing.suites.MyRobolectricTestRunner;
import org.alljoyn.validation.testing.suites.about.AboutUtils;
import org.alljoyn.validation.testing.utils.about.AboutAnnouncementDetails;
import org.alljoyn.validation.testing.utils.services.ServiceAvailabilityHandler;
import org.alljoyn.validation.testing.utils.services.ServiceHelper;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.annotation.Config;

import android.util.Log;

@RunWith(MyRobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class ConfigTestSuiteTest extends BaseTestSuiteTest
{
    private static final String NEW_DEVICE_NAME = "newDeviceName";

    private static final String WRONG_ERROR_NAME = "org.alljoyn.Error.WrongError";

    private ConfigTestSuite configTestSuite;

    private static final String INVALID_LANGUAGE_CODE = "INVALID";

    @Mock
    private ValidationTestContext mockTestContext;

    @Mock
    private AboutAnnouncementDetails deviceAboutAnnouncement;
    @Mock
    protected ServiceHelper mockServiceHelper;
    @Mock
    private ConfigService mockConfigService;
    @Mock
    private AboutClient mockAboutClient;
    @Mock
    private ConfigClient mockConfigClient;
    @Mock
    private BusAttachment mockBusAttachment;
    @Mock
    private ServiceAvailabilityHandler mockServiceAvailabilityHandler;
    @Mock
    private UserInputDetails mockUserInputDetails;

    private Thread testThread;

    protected Exception exceptionThrownByTest;

    private Map<String, Object> aboutData;
    private Map<String, Object> configData;
    private Map<String, Object> resetAboutData;

    private CountDownLatch waitForWaitForNextDeviceAnnouncementCall = new CountDownLatch(1);
    private UUID appId = UUID.fromString("bba0289e-4ad9-4f9a-847c-3df0a98ae9f5");
    private String deviceId = appId.toString();
    private String deviceName = "deviceName";
    private String defaultLanguage = "en";
    private String serviceName = "serviceName";
    private short PORT = 30;

    private AppUnderTestDetails appUnderTestDetails;

    private String[] deviceNameField = new String[]
    { AboutKeys.ABOUT_DEVICE_NAME };
    private String[] defaultLanguageField = new String[]
    { AboutKeys.ABOUT_DEFAULT_LANGUAGE };

    @Before
    public void setup() throws Exception
    {
        MockitoAnnotations.initMocks(this);

        aboutData = AboutUtils.buildAboutMap(defaultLanguage, appId, deviceId, deviceName);
        configData = new HashMap<String, Object>();
        configData.put(AboutKeys.ABOUT_DEVICE_NAME, aboutData.get(AboutKeys.ABOUT_DEVICE_NAME));
        configData.put(AboutKeys.ABOUT_DEFAULT_LANGUAGE, aboutData.get(AboutKeys.ABOUT_DEFAULT_LANGUAGE));

        aboutData.put("MaxLength", new Variant(new Integer(100)));

        resetAboutData = new HashMap<String, Object>();
        resetAboutData.putAll(aboutData);

        // provide the deviceId for the DUT to the test
        appUnderTestDetails = new AppUnderTestDetails(appId, deviceId);
        when(mockTestContext.getAppUnderTestDetails()).thenReturn(appUnderTestDetails);

        configTestSuite = new ConfigTestSuite()
        {

            @Override
            protected ServiceHelper createServiceHelper()
            {
                return mockServiceHelper;
            }

            @Override
            protected StringValue createStringValue()
            {
                return mock(StringValue.class);
            }

            @Override
            protected ServiceAvailabilityHandler createServiceAvailabilityHandler()
            {
                return mockServiceAvailabilityHandler;
            }
            @Override
            protected UserInputDetails createUserInputDetails()
            {
                return mockUserInputDetails;
            }
        };

        // provide the ValidationTestContext to the test
        appUnderTestDetails = new AppUnderTestDetails(appId, deviceId);
        when(mockTestContext.getAppUnderTestDetails()).thenReturn(appUnderTestDetails);
        configTestSuite.setValidationTestContext(mockTestContext);

        updateAboutAnnouncement();

        // when(mockConfigService.isClientRunning()).thenReturn(true);

        when(mockAboutClient.getAbout("")).thenReturn(aboutData);
        when(mockAboutClient.getAbout(defaultLanguage)).thenReturn(aboutData);

        when(mockConfigClient.getVersion()).thenReturn((short) 1);
        when(mockConfigClient.getConfig(defaultLanguage)).thenReturn(configData);
        when(mockConfigClient.getConfig("")).thenReturn(configData);

        // when(mockBusAttachment.getPeerGUID(serviceName,
        // any(StringValue.class))).thenAnswer(new Answer<Status>()
        // {
        //
        // @Override
        // public Status answer(InvocationOnMock invocation) throws Throwable
        // {
        // StringValue stringValue = (StringValue) invocation.getArguments()[1];
        // stringValue.value = peerGUID;
        // return Status.OK;
        // }
        // });

        // Create mock for the ConfigClient.setConfig()
        createSetConfigMock();

        // Create mock for the ConfigClient.ResetConfigurations()
        prepareResetMock();
    }

    @Test
    public void test_01_WhenAppIdEqualsDeviceId() throws Throwable
    {
        executeTestMethod(getTestWrapperFor_v1_01());
    }

    @Test
    public void test_01_WhenAppIdDoesNotEqualDeviceId() throws Throwable
    {
        String appId = "bba0289e-4ad9-4f9a-847c-3df0a98ae9f6";

        aboutData.put(AboutKeys.ABOUT_APP_ID, UUID.fromString(appId));

        updateAboutAnnouncement();

        executeTestMethod(getTestWrapperFor_v1_01());

        verify(mockTestContext).addNote("System App AppId: bba0289e-4ad9-4f9a-847c-3df0a98ae9f6 is not equal to DeviceId: bba0289e-4ad9-4f9a-847c-3df0a98ae9f5");
    }

    @Test
    public void test_02_ConnectWithWrongPasscode() throws Throwable
    {
        when(mockServiceHelper.isPeerAuthenticationAttempted(deviceAboutAnnouncement)).thenReturn(true);
        when(mockServiceHelper.isPeerAuthenticationSuccessful(deviceAboutAnnouncement)).thenReturn(false);
        // when(mockConfigClient.getVersion()).thenThrow(new BusException());
        when(mockConfigClient.getConfig("")).thenReturn(null).thenThrow(new BusException());

        executeTestMethod(getTestWrapperFor_v1_02());

        // verify(mockConfigClient).getVersion();
        verify(mockConfigClient, times(2)).getConfig("");
        verify(mockServiceHelper).isPeerAuthenticationAttempted(deviceAboutAnnouncement);
        verify(mockServiceHelper).isPeerAuthenticationSuccessful(deviceAboutAnnouncement);
    }

    @Test
    public void test_02_ConnectWithWrongPasscodeDoesNotThrowException() throws Throwable
    {

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_02(), "A call to a Config interface method with the wrong passcode must return an error");

        // verify(mockConfigClient).getVersion();
        verify(mockConfigClient, times(2)).getConfig("");
    }

    @Test
    public void test_02_AuthenticationNotAttempted() throws Throwable
    {
        when(mockServiceHelper.isPeerAuthenticationAttempted(deviceAboutAnnouncement)).thenReturn(false);
        when(mockServiceHelper.isPeerAuthenticationSuccessful(deviceAboutAnnouncement)).thenReturn(false);
        // when(mockConfigClient.getVersion()).thenThrow(new BusException());
        when(mockConfigClient.getConfig("")).thenReturn(null).thenThrow(new BusException());

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_02(), "A call to a Config interface method must require authentication");

        // verify(mockConfigClient).getVersion();
        verify(mockConfigClient, times(2)).getConfig("");
        verify(mockServiceHelper).isPeerAuthenticationAttempted(deviceAboutAnnouncement);
    }

    @Test
    public void test_02_AuthenticationRecordedAsSuccessful() throws Throwable
    {
        when(mockServiceHelper.isPeerAuthenticationAttempted(deviceAboutAnnouncement)).thenReturn(true);
        when(mockServiceHelper.isPeerAuthenticationSuccessful(deviceAboutAnnouncement)).thenReturn(true);
        // when(mockConfigClient.getVersion()).thenThrow(new BusException());
        when(mockConfigClient.getConfig("")).thenReturn(null).thenThrow(new BusException());

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_02(), "A call to a Config interface method with the wrong passcode must fail authentication");

        // verify(mockConfigClient).getVersion();
        verify(mockConfigClient, times(2)).getConfig("");
        verify(mockServiceHelper).isPeerAuthenticationAttempted(deviceAboutAnnouncement);
        verify(mockServiceHelper).isPeerAuthenticationSuccessful(deviceAboutAnnouncement);
    }

    @Test
    public void test_03_versionIsValid() throws Throwable
    {
        executeTestMethod(getTestWrapperFor_v1_03());

        verify(mockConfigClient).getVersion();
    }

    @Test
    public void test_03_versionIsNotValid() throws Throwable
    {
        when(mockConfigClient.getVersion()).thenReturn((short) 0);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_03(), "Config version does not match expected:<1> but was:<0>");

        verify(mockConfigClient).getVersion();
    }

    @Test
    public void test_04_GetConfigurationsWithDefaultLanguage() throws Throwable
    {
        executeTestMethod(getTestWrapperFor_v1_04());

        verify(mockConfigClient).getConfig(defaultLanguage);
    }

    @Test
    public void test_04_DeviceNameMissing() throws Throwable
    {
        configData.remove(AboutKeys.ABOUT_DEVICE_NAME);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_04(), "Required DeviceName field not present in map");

        verify(mockConfigClient).getConfig(defaultLanguage);
    }

    @Test
    public void test_04_DefaultLanguageMissing() throws Throwable
    {
        configData.remove(AboutKeys.ABOUT_DEFAULT_LANGUAGE);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_04(), "Required DefaultLanguage field not present in map");

        verify(mockConfigClient).getConfig(defaultLanguage);
    }

    @Test
    public void test_04_DeviceNameMismatch() throws Throwable
    {
        configData.put(AboutKeys.ABOUT_DEVICE_NAME, "differentName");
        executeTestMethodFailsAssertion(getTestWrapperFor_v1_04(),
                "DeviceName from GetConfigurations() does not match About announcement expected:<deviceName> but was:<differentName>");

        verify(mockConfigClient).getConfig(defaultLanguage);
    }

    @Test
    public void test_04_DefaultLanguageMismatch() throws Throwable
    {
        configData.put(AboutKeys.ABOUT_DEFAULT_LANGUAGE, "fr");

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_04(), "DefaultLanguage from GetConfigurations() does not match About announcement expected:<en> but was:<fr>");

        verify(mockConfigClient).getConfig(defaultLanguage);
    }

    @Test
    public void test_05_UnspecifiedLanguage() throws Throwable
    {
        executeTestMethod(getTestWrapperFor_v1_05());

        verify(mockConfigClient).getConfig(defaultLanguage);
        verify(mockConfigClient, times(2)).getConfig("");
    }

    @Test
    public void test_05_DeviceNameMismatch() throws Throwable
    {
        Map<String, Object> configMistmatchData = new HashMap<String, Object>();
        configMistmatchData.putAll(configData);
        configMistmatchData.put(AboutKeys.ABOUT_DEVICE_NAME, "differentName");
        when(mockConfigClient.getConfig(defaultLanguage)).thenReturn(configData);
        when(mockConfigClient.getConfig("")).thenReturn(configMistmatchData);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_05(), "DeviceName does not match expected:<deviceName> but was:<differentName>");

        verify(mockConfigClient).getConfig(defaultLanguage);
        verify(mockConfigClient, times(2)).getConfig("");
    }

    @Test
    public void test_05_DefaultLanguageMismatch() throws Throwable
    {
        Map<String, Object> configMistmatchData = new HashMap<String, Object>();
        configMistmatchData.putAll(configData);
        configMistmatchData.put(AboutKeys.ABOUT_DEFAULT_LANGUAGE, "fr");
        when(mockConfigClient.getConfig(defaultLanguage)).thenReturn(configData);
        when(mockConfigClient.getConfig("")).thenReturn(configMistmatchData);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_05(), "DefaultLanguage does not match expected:<en> but was:<fr>");

        verify(mockConfigClient).getConfig(defaultLanguage);
        verify(mockConfigClient, times(2)).getConfig("");
    }

    @Test
    public void test_06_LanguageConsistencyWithOneLanguage() throws Throwable
    {
        executeTestMethod(getTestWrapperFor_v1_06());
    }

    @Test
    public void test_06_LanguageConsistencyWithMultipleLanguages() throws Throwable
    {
        String secondLanguage = "fr";
        aboutData.put(AboutKeys.ABOUT_SUPPORTED_LANGUAGES, new String[]
        { defaultLanguage, secondLanguage });

        when(mockAboutClient.getAbout(secondLanguage)).thenReturn(aboutData);
        when(mockConfigClient.getConfig(secondLanguage)).thenReturn(configData);

        updateAboutAnnouncement();

        executeTestMethod(getTestWrapperFor_v1_06());
    }

    @Test
    public void test_07_UnsupportedLanguageReturnsError() throws Exception
    {

        LanguageNotSupportedException lnse = new LanguageNotSupportedException();
        when(mockConfigClient.getConfig(INVALID_LANGUAGE_CODE)).thenThrow(lnse);

        executeTestMethod(getTestWrapperFor_v1_07());

        verify(mockConfigClient).getConfig(INVALID_LANGUAGE_CODE);
    }

    @Test
    public void test_07_UnsupportedLanguageDoesNotReturnError() throws Exception
    {

        when(mockConfigClient.getConfig(INVALID_LANGUAGE_CODE)).thenReturn(configData);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_07(),
                "Calling GetConfigurations() on the Config interface with an unsupported language did not return an error, it must return an org.alljoyn.Error.LanguageNotSupported error");

        verify(mockConfigClient).getConfig(INVALID_LANGUAGE_CODE);
    }

    @Test
    public void test_07_UnsupportedLanguageReturnsWrongError() throws Exception
    {

        ErrorReplyBusException erbe = new ErrorReplyBusException(WRONG_ERROR_NAME);
        when(mockConfigClient.getConfig(INVALID_LANGUAGE_CODE)).thenThrow(erbe);

        executeTestMethodFailsAssertion(
                getTestWrapperFor_v1_07(),
                "Calling GetConfigurations() on the Config interface with an unsupported language did not return the expected error expected:<org.alljoyn.Error.[LanguageNotSupported]> but was:<org.alljoyn.Error.[WrongError]>");

        verify(mockConfigClient).getConfig(INVALID_LANGUAGE_CODE);
    }

    @Test
    public void test_08_success() throws Exception
    {
        testUpdateDeviceName(getTestWrapperFor_v1_08(), NEW_DEVICE_NAME);
    }

    private void testUpdateDeviceName(TestWrapper testWrapper, String newDevivceName) throws BusException, Exception
    {
        Map<String, Object> updatedConfigData = new HashMap<String, Object>(configData);
        Map<String, Object> updatedAboutData = new HashMap<String, Object>(aboutData);
        updatedAboutData.put(AboutKeys.ABOUT_DEVICE_NAME, newDevivceName);
        updatedConfigData.put(AboutKeys.ABOUT_DEVICE_NAME, newDevivceName);

        AboutAnnouncementDetails updatedAboutAnnouncement = createAboutAnnouncement(updatedAboutData);

        when(mockServiceHelper.waitForNextDeviceAnnouncement(anyLong(), any(TimeUnit.class), eq(true))).thenReturn(deviceAboutAnnouncement).thenReturn(updatedAboutAnnouncement)
                .thenReturn(deviceAboutAnnouncement);

        when(mockAboutClient.getAbout(defaultLanguage)).thenReturn(updatedAboutData).thenReturn(aboutData);
        when(mockConfigClient.getConfig(defaultLanguage)).thenReturn(updatedConfigData).thenReturn(configData);
        executeTestMethod(testWrapper);

        verify(mockAboutClient, times(2)).getAbout(defaultLanguage);
        verify(mockConfigClient, times(2)).getConfig(defaultLanguage);
        verify(mockServiceHelper, times(3)).waitForNextDeviceAnnouncement(anyLong(), any(TimeUnit.class), eq(true));
    }

    @Test
    public void test_08_failure_AnnouncementNotUpdated() throws Exception
    {
        when(mockServiceHelper.waitForNextDeviceAnnouncement(anyLong(), any(TimeUnit.class), eq(true))).thenReturn(deviceAboutAnnouncement);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_08(),
                "Received About announcement did not contain expected DeviceName expected:<[newD]eviceName> but was:<[d]eviceName>");

        verify(mockServiceHelper, times(2)).waitForNextDeviceAnnouncement(anyLong(), any(TimeUnit.class), eq(true));
    }

    @Test
    public void test_08_failure_GetAboutNotUpdated() throws Exception
    {
        Map<String, Object> updatedAboutData = new HashMap<String, Object>(aboutData);
        updatedAboutData.put(AboutKeys.ABOUT_DEVICE_NAME, NEW_DEVICE_NAME);

        AboutAnnouncementDetails updatedAboutAnnouncement = createAboutAnnouncement(updatedAboutData);

        when(mockServiceHelper.waitForNextDeviceAnnouncement(anyLong(), any(TimeUnit.class), eq(true))).thenReturn(deviceAboutAnnouncement).thenReturn(updatedAboutAnnouncement)
                .thenReturn(deviceAboutAnnouncement);

        when(mockAboutClient.getAbout(defaultLanguage)).thenReturn(aboutData);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_08(),
                "Value for DeviceName retrieved from GetConfigurations() does not match expected value expected:<newDeviceName> but was:<deviceName>");

        verify(mockAboutClient, times(1)).getAbout(defaultLanguage);
        verify(mockServiceHelper, times(2)).waitForNextDeviceAnnouncement(anyLong(), any(TimeUnit.class), eq(true));
    }

    @Test
    public void test_08_failure_GetConfigNotUpdated() throws Exception
    {
        Map<String, Object> updatedAboutData = new HashMap<String, Object>(aboutData);
        updatedAboutData.put(AboutKeys.ABOUT_DEVICE_NAME, NEW_DEVICE_NAME);

        AboutAnnouncementDetails updatedAboutAnnouncement = createAboutAnnouncement(updatedAboutData);

        when(mockServiceHelper.waitForNextDeviceAnnouncement(anyLong(), any(TimeUnit.class), eq(true))).thenReturn(deviceAboutAnnouncement).thenReturn(updatedAboutAnnouncement)
                .thenReturn(deviceAboutAnnouncement);

        when(mockAboutClient.getAbout(defaultLanguage)).thenReturn(updatedAboutData).thenReturn(aboutData);
        when(mockConfigClient.getConfig(defaultLanguage)).thenReturn(configData);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_08(),
                "Value for DeviceName retrieved from GetConfigurations() does not match expected value expected:<newDeviceName> but was:<deviceName>");

        verify(mockAboutClient, times(1)).getAbout(defaultLanguage);
        verify(mockConfigClient, times(1)).getConfig(defaultLanguage);
        verify(mockServiceHelper, times(2)).waitForNextDeviceAnnouncement(anyLong(), any(TimeUnit.class), eq(true));
    }

    @Test
    public void test_08_failure_AnnouncementNotReset() throws Exception
    {
        Map<String, Object> updatedConfigData = new HashMap<String, Object>(configData);
        Map<String, Object> updatedAboutData = new HashMap<String, Object>(aboutData);
        updatedAboutData.put(AboutKeys.ABOUT_DEVICE_NAME, NEW_DEVICE_NAME);
        updatedConfigData.put(AboutKeys.ABOUT_DEVICE_NAME, NEW_DEVICE_NAME);

        AboutAnnouncementDetails updatedAboutAnnouncement = createAboutAnnouncement(updatedAboutData);

        when(mockServiceHelper.waitForNextDeviceAnnouncement(anyLong(), any(TimeUnit.class), eq(true))).thenReturn(deviceAboutAnnouncement).thenReturn(updatedAboutAnnouncement)
                .thenReturn(updatedAboutAnnouncement);

        when(mockAboutClient.getAbout(defaultLanguage)).thenReturn(updatedAboutData);
        when(mockConfigClient.getConfig(defaultLanguage)).thenReturn(updatedConfigData);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_08(),
                "Received About announcement did not contain expected DeviceName expected:<[d]eviceName> but was:<[newD]eviceName>");

        verify(mockAboutClient, times(1)).getAbout(defaultLanguage);
        verify(mockConfigClient, times(1)).getConfig(defaultLanguage);
        verify(mockServiceHelper, times(3)).waitForNextDeviceAnnouncement(anyLong(), any(TimeUnit.class), eq(true));
    }

    @Test
    public void test_08_failure_AboutNotReset() throws Exception
    {
        Map<String, Object> updatedConfigData = new HashMap<String, Object>(configData);
        Map<String, Object> updatedAboutData = new HashMap<String, Object>(aboutData);
        updatedAboutData.put(AboutKeys.ABOUT_DEVICE_NAME, NEW_DEVICE_NAME);
        updatedConfigData.put(AboutKeys.ABOUT_DEVICE_NAME, NEW_DEVICE_NAME);

        AboutAnnouncementDetails updatedAboutAnnouncement = createAboutAnnouncement(updatedAboutData);

        when(mockServiceHelper.waitForNextDeviceAnnouncement(anyLong(), any(TimeUnit.class), eq(true))).thenReturn(deviceAboutAnnouncement).thenReturn(updatedAboutAnnouncement)
                .thenReturn(deviceAboutAnnouncement);

        when(mockAboutClient.getAbout(defaultLanguage)).thenReturn(updatedAboutData).thenReturn(updatedAboutData);
        when(mockConfigClient.getConfig(defaultLanguage)).thenReturn(updatedConfigData);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_08(),
                "Value for DeviceName retrieved from GetAboutData() does not match expected value expected:<deviceName> but was:<newDeviceName>");

        verify(mockAboutClient, times(2)).getAbout(defaultLanguage);
        verify(mockConfigClient, times(2)).getConfig(defaultLanguage);
        verify(mockServiceHelper, times(3)).waitForNextDeviceAnnouncement(anyLong(), any(TimeUnit.class), eq(true));
    }

    @Test
    public void test_08_failure_ConfigNotReset() throws Exception
    {
        Map<String, Object> updatedConfigData = new HashMap<String, Object>(configData);
        Map<String, Object> updatedAboutData = new HashMap<String, Object>(aboutData);
        updatedAboutData.put(AboutKeys.ABOUT_DEVICE_NAME, NEW_DEVICE_NAME);
        updatedConfigData.put(AboutKeys.ABOUT_DEVICE_NAME, NEW_DEVICE_NAME);

        AboutAnnouncementDetails updatedAboutAnnouncement = createAboutAnnouncement(updatedAboutData);

        when(mockServiceHelper.waitForNextDeviceAnnouncement(anyLong(), any(TimeUnit.class), eq(true))).thenReturn(deviceAboutAnnouncement).thenReturn(updatedAboutAnnouncement)
                .thenReturn(deviceAboutAnnouncement);

        when(mockAboutClient.getAbout(defaultLanguage)).thenReturn(updatedAboutData).thenReturn(aboutData);
        when(mockConfigClient.getConfig(defaultLanguage)).thenReturn(updatedConfigData).thenReturn(updatedConfigData);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_08(),
                "Value for DeviceName retrieved from GetConfigurations() does not match expected value expected:<deviceName> but was:<newDeviceName>");

        verify(mockAboutClient, times(2)).getAbout(defaultLanguage);
        verify(mockConfigClient, times(2)).getConfig(defaultLanguage);
        verify(mockServiceHelper, times(3)).waitForNextDeviceAnnouncement(anyLong(), any(TimeUnit.class), eq(true));
    }

    @Test
    @Ignore
    public void testConfig_v1_09UpdateConfigurationsMaxLengthEqDeviceName() throws Exception
    {
        aboutData.put("MaxLength", 30); // added to about data another key
        executeTestMethod(getTestWrapperFor_v1_09());
        verify(mockServiceHelper).release();
        aboutData.remove("MaxLength");
        verify(mockServiceHelper).release();
        aboutData.remove("MaxLength");
    }

    @Test
    @Ignore
    public void testConfig_v1_10DeviceNameExceedsMaxLength() throws Exception
    {
        final int MAX_LENGTH = 29;
        aboutData.put("MaxLength", MAX_LENGTH); // added to about data another
                                                // key , giving the MAX_LENGTH
                                                // character limit

        Mockito.doAnswer(new Answer<Void>()
        {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable
            {
                @SuppressWarnings("unchecked")
                Map<String, Object> confSettings = (Map<String, Object>) invocation.getArguments()[0];
                assertNotNull(confSettings);

                if (confSettings.get(AboutKeys.ABOUT_DEVICE_NAME).toString().length() > MAX_LENGTH)
                {
                    throw new ErrorReplyBusException("org.alljoyn.Error.MaxSizeExceeded", "Max length exceeded");
                }

                return null;
            }
        }).when(mockConfigClient).setConfig(anyMap(), any(String.class));

        executeTestMethod(getTestWrapperFor_v1_10());

        aboutData.remove("MaxLength");

        verify(mockServiceHelper).release();
    }

    @Test
    @Ignore
    public void testConfig_v1_11ChangeDeviceNametoEmpty() throws Exception
    {

        Mockito.doAnswer(new Answer<Void>()
        {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable
            {
                @SuppressWarnings("unchecked")
                Map<String, Object> confSettings = (Map<String, Object>) invocation.getArguments()[0];
                assertNotNull(confSettings);

                if (confSettings.get(AboutKeys.ABOUT_DEVICE_NAME).toString().length() == 0)
                {
                    throw new ErrorReplyBusException("org.alljoyn.Error.InvalidValue", "Invalid value");
                }

                return null;
            }
        }).when(mockConfigClient).setConfig(anyMap(), any(String.class));

        executeTestMethod(getTestWrapperFor_v1_11());

        verify(mockServiceHelper).release();
    }

    @Test
    public void test_12_success() throws Exception
    {
        testUpdateDeviceName(getTestWrapperFor_v1_12(), "!\"#$%&'()*+,-.:;<=>?[\\]^_{|}");
    }

    @Test
    public void test_13_success() throws Exception
    {
        doThrow(new LanguageNotSupportedException()).when(mockConfigClient).setConfig(anyMap(), eq(INVALID_LANGUAGE_CODE));

        executeTestMethod(getTestWrapperFor_v1_13());

        verify(mockConfigClient).setConfig(anyMap(), eq(INVALID_LANGUAGE_CODE));
    }

    @Test
    public void test_13_noExceptionThrown() throws Exception
    {
        executeTestMethodFailsAssertion(getTestWrapperFor_v1_13(),
                "Calling UpdateConfigurations() on the Config interface with an unsupported language must return a LanguageNotSupported error");

        verify(mockConfigClient).setConfig(anyMap(), eq(INVALID_LANGUAGE_CODE));
    }

    @Test
    public void test_13_WrongExceptionThrown() throws Exception
    {
        doThrow(new ErrorReplyBusException(WRONG_ERROR_NAME, "")).when(mockConfigClient).setConfig(anyMap(), eq(INVALID_LANGUAGE_CODE));

        executeTestMethodFailsAssertion(
                getTestWrapperFor_v1_13(),
                "Calling UpdateConfigurations() on the Config interface with an unsupported language did not return the expected error expected:<org.alljoyn.Error.[LanguageNotSupported]> but was:<org.alljoyn.Error.[WrongError]>");

        verify(mockConfigClient).setConfig(anyMap(), eq(INVALID_LANGUAGE_CODE));
    }

    @Test
    public void test_14_onlyOneLanguage() throws Exception
    {
        executeTestMethod(getTestWrapperFor_v1_14());

        verify(mockTestContext).addNote("Only one language is supported");
    }

    @Test
    public void test_14_success() throws Exception
    {
        setupTwoSupportedLanguages();

        String newDefaultLanguage = "fr";

        Map<String, Object> updatedConfigData = new HashMap<String, Object>(configData);
        Map<String, Object> updatedAboutData = new HashMap<String, Object>(aboutData);
        updatedAboutData.put(AboutKeys.ABOUT_DEFAULT_LANGUAGE, newDefaultLanguage);
        updatedConfigData.put(AboutKeys.ABOUT_DEFAULT_LANGUAGE, newDefaultLanguage);

        AboutAnnouncementDetails updatedAboutAnnouncement = createAboutAnnouncement(updatedAboutData);

        when(mockServiceHelper.waitForNextDeviceAnnouncement(anyLong(), any(TimeUnit.class), eq(true))).thenReturn(deviceAboutAnnouncement).thenReturn(updatedAboutAnnouncement)
                .thenReturn(deviceAboutAnnouncement);

        when(mockAboutClient.getAbout(defaultLanguage)).thenReturn(aboutData);
        when(mockConfigClient.getConfig(defaultLanguage)).thenReturn(configData);
        when(mockAboutClient.getAbout(newDefaultLanguage)).thenReturn(updatedAboutData);
        when(mockConfigClient.getConfig(newDefaultLanguage)).thenReturn(updatedConfigData);
        executeTestMethod(getTestWrapperFor_v1_14());

        verify(mockAboutClient, times(2)).getAbout(defaultLanguage);
        verify(mockConfigClient).getConfig(defaultLanguage);
        verify(mockAboutClient).getAbout(newDefaultLanguage);
        verify(mockConfigClient).getConfig(newDefaultLanguage);
        verify(mockServiceHelper, times(3)).waitForNextDeviceAnnouncement(anyLong(), any(TimeUnit.class), eq(true));
    }

    private void setupTwoSupportedLanguages() throws Exception
    {
        aboutData.put(AboutKeys.ABOUT_SUPPORTED_LANGUAGES, new String[]
        { "en", "fr" });

        updateAboutAnnouncement();
    }

    @Test
    public void test_14_duplicateSupportedLanguages() throws Exception
    {
        aboutData.put(AboutKeys.ABOUT_SUPPORTED_LANGUAGES, new String[]
        { "en", "en" });

        updateAboutAnnouncement();

        when(mockServiceHelper.waitForNextDeviceAnnouncement(anyLong(), any(TimeUnit.class), eq(true))).thenReturn(deviceAboutAnnouncement);

        when(mockAboutClient.getAbout(defaultLanguage)).thenReturn(aboutData);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_14(), "SupportedLanguages field contains duplicate languages");

        verify(mockAboutClient).getAbout(defaultLanguage);
        verify(mockServiceHelper).waitForNextDeviceAnnouncement(anyLong(), any(TimeUnit.class), eq(true));
        verify(mockServiceHelper).release();
    }

    @Test
    public void test_14_failure_AnnouncementNotUpdated() throws Exception
    {
        setupTwoSupportedLanguages();

        when(mockServiceHelper.waitForNextDeviceAnnouncement(anyLong(), any(TimeUnit.class), eq(true))).thenReturn(deviceAboutAnnouncement);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_14(), "Received About announcement did not contain expected DefaultLanguage expected:<[fr]> but was:<[en]>");

        verify(mockServiceHelper, times(2)).waitForNextDeviceAnnouncement(anyLong(), any(TimeUnit.class), eq(true));
    }

    @Test
    public void test_14_failure_GetAboutNotUpdated() throws Exception
    {
        setupTwoSupportedLanguages();

        String newDefaultLanguage = "fr";

        Map<String, Object> updatedAboutData = new HashMap<String, Object>(aboutData);
        updatedAboutData.put(AboutKeys.ABOUT_DEFAULT_LANGUAGE, newDefaultLanguage);

        AboutAnnouncementDetails updatedAboutAnnouncement = createAboutAnnouncement(updatedAboutData);

        when(mockServiceHelper.waitForNextDeviceAnnouncement(anyLong(), any(TimeUnit.class), eq(true))).thenReturn(deviceAboutAnnouncement).thenReturn(updatedAboutAnnouncement)
                .thenReturn(deviceAboutAnnouncement);

        when(mockAboutClient.getAbout(newDefaultLanguage)).thenReturn(aboutData);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_14(),
                "Value for DefaultLanguage retrieved from GetConfigurations() does not match expected value expected:<fr> but was:<null>");

        verify(mockAboutClient).getAbout(newDefaultLanguage);
        verify(mockServiceHelper, times(2)).waitForNextDeviceAnnouncement(anyLong(), any(TimeUnit.class), eq(true));
    }

    @Test
    public void test_14_failure_GetConfigNotUpdated() throws Exception
    {
        setupTwoSupportedLanguages();

        String newDefaultLanguage = "fr";

        Map<String, Object> updatedAboutData = new HashMap<String, Object>(aboutData);
        updatedAboutData.put(AboutKeys.ABOUT_DEFAULT_LANGUAGE, newDefaultLanguage);

        AboutAnnouncementDetails updatedAboutAnnouncement = createAboutAnnouncement(updatedAboutData);

        when(mockServiceHelper.waitForNextDeviceAnnouncement(anyLong(), any(TimeUnit.class), eq(true))).thenReturn(deviceAboutAnnouncement).thenReturn(updatedAboutAnnouncement)
                .thenReturn(deviceAboutAnnouncement);

        when(mockAboutClient.getAbout(newDefaultLanguage)).thenReturn(updatedAboutData);
        when(mockConfigClient.getConfig(newDefaultLanguage)).thenReturn(configData);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_14(),
                "Value for DefaultLanguage retrieved from GetConfigurations() does not match expected value expected:<fr> but was:<en>");

        verify(mockAboutClient).getAbout(newDefaultLanguage);
        verify(mockConfigClient).getConfig(newDefaultLanguage);
        verify(mockServiceHelper, times(2)).waitForNextDeviceAnnouncement(anyLong(), any(TimeUnit.class), eq(true));
    }

    @Test
    public void test_14_failure_AnnouncementNotReset() throws Exception
    {
        setupTwoSupportedLanguages();

        String newDefaultLanguage = "fr";

        Map<String, Object> updatedConfigData = new HashMap<String, Object>(configData);
        Map<String, Object> updatedAboutData = new HashMap<String, Object>(aboutData);
        updatedAboutData.put(AboutKeys.ABOUT_DEFAULT_LANGUAGE, newDefaultLanguage);
        updatedConfigData.put(AboutKeys.ABOUT_DEFAULT_LANGUAGE, newDefaultLanguage);

        AboutAnnouncementDetails updatedAboutAnnouncement = createAboutAnnouncement(updatedAboutData);

        when(mockServiceHelper.waitForNextDeviceAnnouncement(anyLong(), any(TimeUnit.class), eq(true))).thenReturn(deviceAboutAnnouncement).thenReturn(updatedAboutAnnouncement)
                .thenReturn(updatedAboutAnnouncement);

        when(mockAboutClient.getAbout(newDefaultLanguage)).thenReturn(aboutData);
        when(mockConfigClient.getConfig(newDefaultLanguage)).thenReturn(configData);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_14(),
                "Value for DefaultLanguage retrieved from GetConfigurations() does not match expected value expected:<fr> but was:<en>");

        verify(mockAboutClient).getAbout(newDefaultLanguage);
        verify(mockConfigClient).getConfig(newDefaultLanguage);
        verify(mockServiceHelper, times(2)).waitForNextDeviceAnnouncement(anyLong(), any(TimeUnit.class), eq(true));
    }

    @Test
    public void test_14_failure_AboutNotReset() throws Exception
    {
        setupTwoSupportedLanguages();

        String newLanguage = "fr";

        Map<String, Object> updatedConfigData = new HashMap<String, Object>(configData);
        Map<String, Object> updatedAboutData = new HashMap<String, Object>(aboutData);
        updatedAboutData.put(AboutKeys.ABOUT_DEFAULT_LANGUAGE, newLanguage);
        updatedConfigData.put(AboutKeys.ABOUT_DEFAULT_LANGUAGE, newLanguage);

        AboutAnnouncementDetails updatedAboutAnnouncement = createAboutAnnouncement(updatedAboutData);

        when(mockServiceHelper.waitForNextDeviceAnnouncement(anyLong(), any(TimeUnit.class), eq(true))).thenReturn(deviceAboutAnnouncement).thenReturn(updatedAboutAnnouncement)
                .thenReturn(deviceAboutAnnouncement);

        when(mockAboutClient.getAbout(defaultLanguage)).thenReturn(updatedAboutData);
        when(mockAboutClient.getAbout(newLanguage)).thenReturn(updatedAboutData);
        when(mockConfigClient.getConfig(newLanguage)).thenReturn(updatedConfigData);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_14(),
                "Value for DefaultLanguage retrieved from GetAboutData() does not match expected value expected:<en> but was:<fr>");

        verify(mockAboutClient, times(2)).getAbout(defaultLanguage);
        verify(mockAboutClient).getAbout(newLanguage);
        verify(mockConfigClient).getConfig(newLanguage);
        verify(mockServiceHelper, times(3)).waitForNextDeviceAnnouncement(anyLong(), any(TimeUnit.class), eq(true));
    }

    @Test
    public void test_14_failure_ConfigNotReset() throws Exception
    {
        setupTwoSupportedLanguages();

        String newLanguage = "fr";

        Map<String, Object> updatedConfigData = new HashMap<String, Object>(configData);
        Map<String, Object> updatedAboutData = new HashMap<String, Object>(aboutData);
        updatedAboutData.put(AboutKeys.ABOUT_DEFAULT_LANGUAGE, newLanguage);
        updatedConfigData.put(AboutKeys.ABOUT_DEFAULT_LANGUAGE, newLanguage);

        AboutAnnouncementDetails updatedAboutAnnouncement = createAboutAnnouncement(updatedAboutData);

        when(mockServiceHelper.waitForNextDeviceAnnouncement(anyLong(), any(TimeUnit.class), eq(true))).thenReturn(deviceAboutAnnouncement).thenReturn(updatedAboutAnnouncement)
                .thenReturn(deviceAboutAnnouncement);

        when(mockAboutClient.getAbout(newLanguage)).thenReturn(updatedAboutData);
        when(mockConfigClient.getConfig(newLanguage)).thenReturn(updatedConfigData);
        when(mockAboutClient.getAbout(defaultLanguage)).thenReturn(aboutData);
        when(mockConfigClient.getConfig(defaultLanguage)).thenReturn(updatedConfigData);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_14(),
                "Value for DefaultLanguage retrieved from GetConfigurations() does not match expected value expected:<en> but was:<fr>");

        verify(mockAboutClient).getAbout(newLanguage);
        verify(mockConfigClient).getConfig(newLanguage);
        verify(mockAboutClient, times(2)).getAbout(defaultLanguage);
        verify(mockConfigClient).getConfig(defaultLanguage);
        verify(mockServiceHelper, times(3)).waitForNextDeviceAnnouncement(anyLong(), any(TimeUnit.class), eq(true));
    }

    @Test
    public void test_15_success() throws Exception
    {
        doThrow(new LanguageNotSupportedException()).when(mockConfigClient).setConfig(anyMap(), eq(defaultLanguage));

        executeTestMethod(getTestWrapperFor_v1_15());

        verify(mockConfigClient).setConfig(anyMap(), eq(defaultLanguage));
    }

    @Test
    public void test_15_noExceptionThrown() throws Exception
    {
        executeTestMethodFailsAssertion(getTestWrapperFor_v1_15(),
                "Calling UpdateConfigurations() to set the DefaultLanguage to an unsupported language must return an error, org.alljoyn.Error.LanguageNotSupported");

        verify(mockConfigClient).setConfig(anyMap(), eq(defaultLanguage));
    }

    @Test
    public void test_15_WrongExceptionThrown() throws Exception
    {
        doThrow(new ErrorReplyBusException(WRONG_ERROR_NAME, "")).when(mockConfigClient).setConfig(anyMap(), eq(INVALID_LANGUAGE_CODE));

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_15(),
                "Calling UpdateConfigurations() to set the DefaultLanguage to an unsupported language must return an error, org.alljoyn.Error.LanguageNotSupported");

        verify(mockConfigClient).setConfig(anyMap(), eq(defaultLanguage));
    }

    @Test
    public void test_16_success() throws Exception
    {
        doThrow(new LanguageNotSupportedException()).when(mockConfigClient).setConfig(anyMap(), eq(defaultLanguage));

        executeTestMethod(getTestWrapperFor_v1_16());

        verify(mockConfigClient).setConfig(anyMap(), eq(defaultLanguage));
    }

    @Test
    public void test_16_noExceptionThrown() throws Exception
    {
        executeTestMethodFailsAssertion(
                getTestWrapperFor_v1_16(),
                "Calling UpdateConfigurations() on the Config interface to update the DefaultLanguage field to an unspecified language did not return an error, it must return an org.alljoyn.Error.LanguageNotSupported error");

        verify(mockConfigClient).setConfig(anyMap(), eq(defaultLanguage));
    }

    @Test
    public void test_16_WrongExceptionThrown() throws Exception
    {
        doThrow(new ErrorReplyBusException(WRONG_ERROR_NAME, "")).when(mockConfigClient).setConfig(anyMap(), eq(INVALID_LANGUAGE_CODE));

        executeTestMethodFailsAssertion(
                getTestWrapperFor_v1_16(),
                "Calling UpdateConfigurations() on the Config interface to update the DefaultLanguage field to an unspecified language did not return an error, it must return an org.alljoyn.Error.LanguageNotSupported error");

        verify(mockConfigClient).setConfig(anyMap(), eq(defaultLanguage));
    }

    @Test
    public void test_18_success() throws Exception
    {
        doThrow(new ErrorReplyBusException("org.alljoyn.Error.UpdateNotAllowed")).when(mockConfigClient).setConfig(anyMap(), eq(defaultLanguage));

        executeTestMethod(getTestWrapperFor_v1_18());

        verify(mockConfigClient).setConfig(anyMap(), eq(defaultLanguage));
    }

    @Test
    public void test_18_noExceptionThrown() throws Exception
    {
        executeTestMethodFailsAssertion(getTestWrapperFor_v1_18(),
                "Calling UpdateConfigurations() to update the DeviceId did not return an error, expecting UpdateNotAllowed (since DeviceId is a read only field)");

        verify(mockConfigClient).setConfig(anyMap(), eq(defaultLanguage));
    }

    @Test
    public void test_18_WrongExceptionThrown() throws Exception
    {
        doThrow(new ErrorReplyBusException(WRONG_ERROR_NAME, "")).when(mockConfigClient).setConfig(anyMap(), eq(INVALID_LANGUAGE_CODE));

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_18(),
                "Calling UpdateConfigurations() to update the DeviceId did not return an error, expecting UpdateNotAllowed (since DeviceId is a read only field)");

        verify(mockConfigClient).setConfig(anyMap(), eq(defaultLanguage));
    }

    @Test
    public void test_19_success() throws Exception
    {
        ErrorReplyBusException ex = new ErrorReplyBusException("org.alljoyn.Error.InvalidValue", "Invalid field can't be updated");
        doThrow(ex).when(mockConfigClient).setConfig(anyMap(), eq(defaultLanguage));

        executeTestMethod(getTestWrapperFor_v1_19());

        verify(mockServiceHelper).release();
        verify(mockConfigClient).setConfig(anyMap(), eq(defaultLanguage));
    }

    @Test
    public void test_19_noExceptionThrown() throws Exception
    {
        executeTestMethodFailsAssertion(getTestWrapperFor_v1_19(),
                "Calling UpdateConfigurations() on the Config interface to update an invalid field did not return an error, it must return an org.alljoyn.Error.InvalidValue error");

        verify(mockConfigClient).setConfig(anyMap(), eq(defaultLanguage));
    }

    @Test
    public void test_19_WrongExceptionThrown() throws Exception
    {
        doThrow(new ErrorReplyBusException(WRONG_ERROR_NAME, "")).when(mockConfigClient).setConfig(anyMap(), eq(INVALID_LANGUAGE_CODE));

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_19(),
                "Calling UpdateConfigurations() on the Config interface to update an invalid field did not return an error, it must return an org.alljoyn.Error.InvalidValue error");

        verify(mockConfigClient).setConfig(anyMap(), eq(defaultLanguage));
    }

    @Test
    public void test_20_success() throws Exception
    {
        Map<String, Object> updatedConfigData = new HashMap<String, Object>(configData);
        Map<String, Object> updatedAboutData = new HashMap<String, Object>(aboutData);
        updatedAboutData.put(AboutKeys.ABOUT_DEVICE_NAME, NEW_DEVICE_NAME);
        updatedConfigData.put(AboutKeys.ABOUT_DEVICE_NAME, NEW_DEVICE_NAME);
        AboutAnnouncementDetails updatedAboutAnnouncement = createAboutAnnouncement(updatedAboutData);

        when(mockConfigClient.getConfig(defaultLanguage)).thenReturn(updatedConfigData).thenReturn(configData);
        when(mockAboutClient.getAbout(defaultLanguage)).thenReturn(aboutData).thenReturn(updatedAboutData).thenReturn(aboutData);

        when(mockServiceHelper.waitForNextDeviceAnnouncement(anyLong(), any(TimeUnit.class), eq(true))).thenReturn(deviceAboutAnnouncement).thenReturn(deviceAboutAnnouncement)
                .thenReturn(updatedAboutAnnouncement).thenReturn(deviceAboutAnnouncement);

        executeTestMethod(getTestWrapperFor_v1_20());

        verify(mockConfigClient, Mockito.times(2)).ResetConfigurations(eq(defaultLanguage), eq(deviceNameField));
        verify(mockConfigClient).setConfig(anyMap(), eq(defaultLanguage));
        verify(mockServiceHelper).release();
    }

    @Test
    public void test_20_firstAnnouncementDoesNotHaveUpdatedDeviceName() throws Exception
    {
        when(mockAboutClient.getAbout(defaultLanguage)).thenReturn(aboutData);

        when(mockServiceHelper.waitForNextDeviceAnnouncement(anyLong(), any(TimeUnit.class), eq(true))).thenReturn(deviceAboutAnnouncement).thenReturn(deviceAboutAnnouncement);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_20(),
                "Received About announcement did not contain expected DeviceName expected:<[newD]eviceName> but was:<[d]eviceName>");

        verify(mockConfigClient).ResetConfigurations(eq(defaultLanguage), eq(deviceNameField));
        verify(mockConfigClient).setConfig(anyMap(), eq(defaultLanguage));
        verify(mockServiceHelper).release();
    }

    @Test
    public void test_20_announcementAfterResetDoesNotHaveOriginalDeviceName() throws Exception
    {
        Map<String, Object> updatedConfigData = new HashMap<String, Object>(configData);
        Map<String, Object> updatedAboutData = new HashMap<String, Object>(aboutData);
        updatedAboutData.put(AboutKeys.ABOUT_DEVICE_NAME, NEW_DEVICE_NAME);
        updatedConfigData.put(AboutKeys.ABOUT_DEVICE_NAME, NEW_DEVICE_NAME);
        AboutAnnouncementDetails updatedAboutAnnouncement = createAboutAnnouncement(updatedAboutData);

        when(mockConfigClient.getConfig(defaultLanguage)).thenReturn(updatedConfigData);
        when(mockAboutClient.getAbout(defaultLanguage)).thenReturn(aboutData).thenReturn(updatedAboutData);

        when(mockServiceHelper.waitForNextDeviceAnnouncement(anyLong(), any(TimeUnit.class), eq(true))).thenReturn(deviceAboutAnnouncement).thenReturn(updatedAboutAnnouncement)
                .thenReturn(updatedAboutAnnouncement);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_20(),
                "Received About announcement did not contain expected DeviceName expected:<[d]eviceName> but was:<[newD]eviceName>");

        verify(mockConfigClient, Mockito.times(2)).ResetConfigurations(eq(defaultLanguage), eq(deviceNameField));
        verify(mockConfigClient).setConfig(anyMap(), eq(defaultLanguage));
        verify(mockServiceHelper).release();
    }

    @Test
    public void test_20_configDataNotReset() throws Exception
    {
        Map<String, Object> updatedConfigData = new HashMap<String, Object>(configData);
        Map<String, Object> updatedAboutData = new HashMap<String, Object>(aboutData);
        updatedAboutData.put(AboutKeys.ABOUT_DEVICE_NAME, NEW_DEVICE_NAME);
        updatedConfigData.put(AboutKeys.ABOUT_DEVICE_NAME, NEW_DEVICE_NAME);
        AboutAnnouncementDetails updatedAboutAnnouncement = createAboutAnnouncement(updatedAboutData);

        when(mockConfigClient.getConfig(defaultLanguage)).thenReturn(updatedConfigData).thenReturn(updatedConfigData);
        when(mockAboutClient.getAbout(defaultLanguage)).thenReturn(aboutData).thenReturn(updatedAboutData).thenReturn(aboutData);

        when(mockServiceHelper.waitForNextDeviceAnnouncement(anyLong(), any(TimeUnit.class), eq(true))).thenReturn(deviceAboutAnnouncement).thenReturn(deviceAboutAnnouncement)
                .thenReturn(updatedAboutAnnouncement).thenReturn(deviceAboutAnnouncement);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_20(),
                "Value for DeviceName retrieved from GetConfigurations() does not match expected value expected:<deviceName> but was:<newDeviceName>");

        verify(mockConfigClient, Mockito.times(2)).ResetConfigurations(eq(defaultLanguage), eq(deviceNameField));
        verify(mockConfigClient).setConfig(anyMap(), eq(defaultLanguage));
        verify(mockServiceHelper).release();
    }

    @Test
    public void test_20_aboutDataNotReset() throws Exception
    {
        Map<String, Object> updatedConfigData = new HashMap<String, Object>(configData);
        Map<String, Object> updatedAboutData = new HashMap<String, Object>(aboutData);
        updatedAboutData.put(AboutKeys.ABOUT_DEVICE_NAME, NEW_DEVICE_NAME);
        updatedConfigData.put(AboutKeys.ABOUT_DEVICE_NAME, NEW_DEVICE_NAME);
        AboutAnnouncementDetails updatedAboutAnnouncement = createAboutAnnouncement(updatedAboutData);

        when(mockConfigClient.getConfig(defaultLanguage)).thenReturn(updatedConfigData).thenReturn(configData);
        when(mockAboutClient.getAbout(defaultLanguage)).thenReturn(aboutData).thenReturn(updatedAboutData).thenReturn(updatedAboutData);

        when(mockServiceHelper.waitForNextDeviceAnnouncement(anyLong(), any(TimeUnit.class), eq(true))).thenReturn(deviceAboutAnnouncement).thenReturn(deviceAboutAnnouncement)
                .thenReturn(updatedAboutAnnouncement).thenReturn(deviceAboutAnnouncement);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_20(),
                "Value for DeviceName retrieved from GetAboutData() does not match expected value expected:<deviceName> but was:<newDeviceName>");

        verify(mockConfigClient, Mockito.times(2)).ResetConfigurations(eq(defaultLanguage), eq(deviceNameField));
        verify(mockConfigClient).setConfig(anyMap(), eq(defaultLanguage));
        verify(mockServiceHelper).release();
    }

    @Test
    public void test_21_success() throws Exception
    {
        when(mockConfigClient.getConfig(defaultLanguage)).thenReturn(configData);
        when(mockAboutClient.getAbout(defaultLanguage)).thenReturn(aboutData);

        executeTestMethod(getTestWrapperFor_v1_21());

        verify(mockConfigClient).ResetConfigurations(eq(defaultLanguage), eq(defaultLanguageField));
        verify(mockServiceHelper).release();
    }

    @Test
    public void test_21_defaultLanguageDoesNotMatch() throws Exception
    {
        Map<String, Object> updatedConfigData = new HashMap<String, Object>(configData);
        updatedConfigData.put(AboutKeys.ABOUT_DEFAULT_LANGUAGE, "fr");

        when(mockConfigClient.getConfig(defaultLanguage)).thenReturn(updatedConfigData);
        when(mockAboutClient.getAbout(defaultLanguage)).thenReturn(aboutData);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_21(), "DefaultLanguage does not match expected:<en> but was:<fr>");

        verify(mockConfigClient).ResetConfigurations(eq(defaultLanguage), eq(defaultLanguageField));
        verify(mockServiceHelper).release();
    }

    @Test
    public void test_22_success() throws Exception
    {
        setupTwoSupportedLanguages();

        String newDefaultLanguage = "fr";

        Map<String, Object> updatedConfigData = new HashMap<String, Object>(configData);
        Map<String, Object> updatedAboutData = new HashMap<String, Object>(aboutData);
        updatedAboutData.put(AboutKeys.ABOUT_DEFAULT_LANGUAGE, newDefaultLanguage);
        updatedConfigData.put(AboutKeys.ABOUT_DEFAULT_LANGUAGE, newDefaultLanguage);

        AboutAnnouncementDetails updatedAboutAnnouncement = createAboutAnnouncement(updatedAboutData);

        when(mockServiceHelper.waitForNextDeviceAnnouncement(anyLong(), any(TimeUnit.class), eq(true))).thenReturn(deviceAboutAnnouncement).thenReturn(updatedAboutAnnouncement)
                .thenReturn(deviceAboutAnnouncement);

        when(mockAboutClient.getAbout(defaultLanguage)).thenReturn(aboutData);

        when(mockAboutClient.getAbout("")).thenReturn(aboutData).thenReturn(updatedAboutData).thenReturn(aboutData);
        when(mockConfigClient.getConfig("")).thenReturn(configData).thenReturn(configData).thenReturn(updatedConfigData).thenReturn(configData);

        executeTestMethod(getTestWrapperFor_v1_22());

        verify(mockAboutClient).getAbout(defaultLanguage);
        verify(mockAboutClient, times(3)).getAbout("");
        verify(mockConfigClient, times(4)).getConfig("");
        verify(mockServiceHelper, times(3)).waitForNextDeviceAnnouncement(anyLong(), any(TimeUnit.class), eq(true));
    }

    @Test
    public void test_22_onlyOneLanguage() throws Exception
    {
        executeTestMethod(getTestWrapperFor_v1_22());

        verify(mockTestContext).addNote("Only one language is supported");
    }

    @Test
    public void test_22_duplicateSupportedLanguages() throws Exception
    {
        aboutData.put(AboutKeys.ABOUT_SUPPORTED_LANGUAGES, new String[]
        { "en", "en" });

        updateAboutAnnouncement();

        when(mockServiceHelper.waitForNextDeviceAnnouncement(anyLong(), any(TimeUnit.class), eq(true))).thenReturn(deviceAboutAnnouncement);

        when(mockAboutClient.getAbout(defaultLanguage)).thenReturn(aboutData);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_22(), "SupportedLanguages field contains duplicate languages");

        verify(mockAboutClient).getAbout(defaultLanguage);
        verify(mockServiceHelper).waitForNextDeviceAnnouncement(anyLong(), any(TimeUnit.class), eq(true));
        verify(mockServiceHelper).release();
    }

    @Test
    public void test_22_failure_AnnouncementNotUpdated() throws Exception
    {
        setupTwoSupportedLanguages();

        when(mockServiceHelper.waitForNextDeviceAnnouncement(anyLong(), any(TimeUnit.class), eq(true))).thenReturn(deviceAboutAnnouncement).thenReturn(deviceAboutAnnouncement);

        when(mockAboutClient.getAbout(defaultLanguage)).thenReturn(aboutData);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_22(), "Received About announcement did not contain expected DefaultLanguage expected:<[fr]> but was:<[en]>");

        verify(mockAboutClient).getAbout(defaultLanguage);
        verify(mockServiceHelper, times(2)).waitForNextDeviceAnnouncement(anyLong(), any(TimeUnit.class), eq(true));
    }

    @Test
    public void test_22_failure_GetAboutNotUpdated() throws Exception
    {
        setupTwoSupportedLanguages();

        String newDefaultLanguage = "fr";

        Map<String, Object> updatedAboutData = new HashMap<String, Object>(aboutData);
        updatedAboutData.put(AboutKeys.ABOUT_DEFAULT_LANGUAGE, newDefaultLanguage);

        AboutAnnouncementDetails updatedAboutAnnouncement = createAboutAnnouncement(updatedAboutData);

        when(mockServiceHelper.waitForNextDeviceAnnouncement(anyLong(), any(TimeUnit.class), eq(true))).thenReturn(deviceAboutAnnouncement).thenReturn(updatedAboutAnnouncement);

        when(mockAboutClient.getAbout(defaultLanguage)).thenReturn(aboutData);

        when(mockAboutClient.getAbout("")).thenReturn(aboutData).thenReturn(aboutData);
        when(mockConfigClient.getConfig("")).thenReturn(configData);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_22(),
                "Value for DefaultLanguage retrieved from GetConfigurations() does not match expected value expected:<fr> but was:<en>");

        verify(mockAboutClient).getAbout(defaultLanguage);
        verify(mockAboutClient, times(2)).getAbout("");
        verify(mockServiceHelper, times(2)).waitForNextDeviceAnnouncement(anyLong(), any(TimeUnit.class), eq(true));
    }

    @Test
    public void test_22_failure_GetConfigNotUpdated() throws Exception
    {
        setupTwoSupportedLanguages();

        String newDefaultLanguage = "fr";

        Map<String, Object> updatedConfigData = new HashMap<String, Object>(configData);
        Map<String, Object> updatedAboutData = new HashMap<String, Object>(aboutData);
        updatedAboutData.put(AboutKeys.ABOUT_DEFAULT_LANGUAGE, newDefaultLanguage);
        updatedConfigData.put(AboutKeys.ABOUT_DEFAULT_LANGUAGE, newDefaultLanguage);

        AboutAnnouncementDetails updatedAboutAnnouncement = createAboutAnnouncement(updatedAboutData);

        when(mockServiceHelper.waitForNextDeviceAnnouncement(anyLong(), any(TimeUnit.class), eq(true))).thenReturn(deviceAboutAnnouncement).thenReturn(updatedAboutAnnouncement);

        when(mockAboutClient.getAbout(defaultLanguage)).thenReturn(aboutData);

        when(mockAboutClient.getAbout("")).thenReturn(aboutData).thenReturn(updatedAboutData);
        when(mockConfigClient.getConfig("")).thenReturn(configData).thenReturn(configData);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_22(),
                "Value for DefaultLanguage retrieved from GetConfigurations() does not match expected value expected:<fr> but was:<en>");

        verify(mockAboutClient).getAbout(defaultLanguage);
        verify(mockAboutClient, times(2)).getAbout("");
        verify(mockConfigClient, times(3)).getConfig("");
        verify(mockServiceHelper, times(2)).waitForNextDeviceAnnouncement(anyLong(), any(TimeUnit.class), eq(true));
    }

    @Test
    public void test_22_failure_AnnouncementNotReset() throws Exception
    {
        setupTwoSupportedLanguages();

        String newDefaultLanguage = "fr";

        Map<String, Object> updatedConfigData = new HashMap<String, Object>(configData);
        Map<String, Object> updatedAboutData = new HashMap<String, Object>(aboutData);
        updatedAboutData.put(AboutKeys.ABOUT_DEFAULT_LANGUAGE, newDefaultLanguage);
        updatedConfigData.put(AboutKeys.ABOUT_DEFAULT_LANGUAGE, newDefaultLanguage);

        AboutAnnouncementDetails updatedAboutAnnouncement = createAboutAnnouncement(updatedAboutData);

        when(mockServiceHelper.waitForNextDeviceAnnouncement(anyLong(), any(TimeUnit.class), eq(true))).thenReturn(deviceAboutAnnouncement).thenReturn(updatedAboutAnnouncement)
                .thenReturn(updatedAboutAnnouncement);

        when(mockAboutClient.getAbout(defaultLanguage)).thenReturn(aboutData);

        when(mockAboutClient.getAbout("")).thenReturn(aboutData).thenReturn(updatedAboutData);
        when(mockConfigClient.getConfig("")).thenReturn(configData).thenReturn(configData).thenReturn(updatedConfigData);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_22(), "Received About announcement did not contain expected DefaultLanguage expected:<[en]> but was:<[fr]>");

        verify(mockAboutClient).getAbout(defaultLanguage);
        verify(mockAboutClient, times(2)).getAbout("");
        verify(mockConfigClient, times(3)).getConfig("");
        verify(mockServiceHelper, times(3)).waitForNextDeviceAnnouncement(anyLong(), any(TimeUnit.class), eq(true));
    }

    @Test
    public void test_22_failure_AboutNotReset() throws Exception
    {
        setupTwoSupportedLanguages();

        String newDefaultLanguage = "fr";

        Map<String, Object> updatedConfigData = new HashMap<String, Object>(configData);
        Map<String, Object> updatedAboutData = new HashMap<String, Object>(aboutData);
        updatedAboutData.put(AboutKeys.ABOUT_DEFAULT_LANGUAGE, newDefaultLanguage);
        updatedConfigData.put(AboutKeys.ABOUT_DEFAULT_LANGUAGE, newDefaultLanguage);

        AboutAnnouncementDetails updatedAboutAnnouncement = createAboutAnnouncement(updatedAboutData);

        when(mockServiceHelper.waitForNextDeviceAnnouncement(anyLong(), any(TimeUnit.class), eq(true))).thenReturn(deviceAboutAnnouncement).thenReturn(updatedAboutAnnouncement)
                .thenReturn(deviceAboutAnnouncement);

        when(mockAboutClient.getAbout(defaultLanguage)).thenReturn(aboutData);

        when(mockAboutClient.getAbout("")).thenReturn(aboutData).thenReturn(updatedAboutData).thenReturn(updatedAboutData);
        when(mockConfigClient.getConfig("")).thenReturn(configData).thenReturn(configData).thenReturn(updatedConfigData).thenReturn(configData);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_22(),
                "Value for DefaultLanguage retrieved from GetAboutData() does not match expected value expected:<en> but was:<fr>");

        verify(mockAboutClient).getAbout(defaultLanguage);
        verify(mockAboutClient, times(3)).getAbout("");
        verify(mockConfigClient, times(4)).getConfig("");
        verify(mockServiceHelper, times(3)).waitForNextDeviceAnnouncement(anyLong(), any(TimeUnit.class), eq(true));
    }

    @Test
    public void test_22_failure_ConfigNotReset() throws Exception
    {
        setupTwoSupportedLanguages();

        String newDefaultLanguage = "fr";

        Map<String, Object> updatedConfigData = new HashMap<String, Object>(configData);
        Map<String, Object> updatedAboutData = new HashMap<String, Object>(aboutData);
        updatedAboutData.put(AboutKeys.ABOUT_DEFAULT_LANGUAGE, newDefaultLanguage);
        updatedConfigData.put(AboutKeys.ABOUT_DEFAULT_LANGUAGE, newDefaultLanguage);

        AboutAnnouncementDetails updatedAboutAnnouncement = createAboutAnnouncement(updatedAboutData);

        when(mockServiceHelper.waitForNextDeviceAnnouncement(anyLong(), any(TimeUnit.class), eq(true))).thenReturn(deviceAboutAnnouncement).thenReturn(updatedAboutAnnouncement)
                .thenReturn(deviceAboutAnnouncement);

        when(mockAboutClient.getAbout(defaultLanguage)).thenReturn(aboutData);

        when(mockAboutClient.getAbout("")).thenReturn(aboutData).thenReturn(updatedAboutData).thenReturn(aboutData);
        when(mockConfigClient.getConfig("")).thenReturn(configData).thenReturn(configData).thenReturn(updatedConfigData).thenReturn(updatedConfigData);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_22(),
                "Value for DefaultLanguage retrieved from GetConfigurations() does not match expected value expected:<en> but was:<fr>");

        verify(mockAboutClient).getAbout(defaultLanguage);
        verify(mockAboutClient, times(3)).getAbout("");
        verify(mockConfigClient, times(4)).getConfig("");
        verify(mockServiceHelper, times(3)).waitForNextDeviceAnnouncement(anyLong(), any(TimeUnit.class), eq(true));
    }

    @Test
    @Ignore
    public void testConfig_v1_23FailResetDeviceId() throws Exception
    {

        Mockito.doThrow(new ErrorReplyBusException("org.alljoyn.Error.InvalidValue", "The reset for a forbidden field has been called")).when(mockConfigClient)
                .ResetConfigurations(any(String.class), any(String[].class));

        executeTestMethod(getTestWrapperFor_v1_23());

        verify(mockServiceHelper).release();

        verify(mockAboutClient, Mockito.times(2)).getAbout(any(String.class));
        verify(mockConfigClient, Mockito.times(1)).ResetConfigurations(any(String.class), (any(String[].class)));
        verify(mockConfigService).stopConfigClient();
    }// testConfig_v1_23FailResetDeviceId

    @Test
    public void test_24_success() throws Exception
    {
        doThrow(new LanguageNotSupportedException()).when(mockConfigClient).ResetConfigurations(eq(INVALID_LANGUAGE_CODE), any(String[].class));

        executeTestMethod(getTestWrapperFor_v1_24());

        verify(mockConfigClient).ResetConfigurations(eq(INVALID_LANGUAGE_CODE), any(String[].class));
        verify(mockServiceHelper).waitForNextDeviceAnnouncement(anyLong(), any(TimeUnit.class), eq(true));
    }

    @Test
    public void test_24_throwsWrongException() throws Exception
    {
        doThrow(new ErrorReplyBusException(WRONG_ERROR_NAME)).when(mockConfigClient).ResetConfigurations(eq(INVALID_LANGUAGE_CODE), any(String[].class));

        executeTestMethodFailsAssertion(
                getTestWrapperFor_v1_24(),
                "Calling ResetConfigurations() with an unsupported language did not return the expected error expected:<org.alljoyn.Error.[LanguageNotSupported]> but was:<org.alljoyn.Error.[WrongError]>");

        verify(mockConfigClient).ResetConfigurations(eq(INVALID_LANGUAGE_CODE), any(String[].class));
        verify(mockServiceHelper).waitForNextDeviceAnnouncement(anyLong(), any(TimeUnit.class), eq(true));
    }

    @Test
    public void test_24_NoExceptionThrown() throws Exception
    {
        executeTestMethodFailsAssertion(getTestWrapperFor_v1_24(),
                "Calling ResetConfigurations() with an unsupported language must return an error, org.alljoyn.Error.LanguageNotSupported");

        verify(mockConfigClient).ResetConfigurations(eq(INVALID_LANGUAGE_CODE), any(String[].class));
        verify(mockServiceHelper).waitForNextDeviceAnnouncement(anyLong(), any(TimeUnit.class), eq(true));
    }

    @Test
    public void test_25_success() throws Exception
    {
        doThrow(new ErrorReplyBusException(AllJoynErrorReplyCodes.INVALID_VALUE)).when(mockConfigClient).ResetConfigurations(eq(defaultLanguage), any(String[].class));

        executeTestMethod(getTestWrapperFor_v1_25());

        verify(mockConfigClient).ResetConfigurations(eq(defaultLanguage), any(String[].class));
        verify(mockServiceHelper).waitForNextDeviceAnnouncement(anyLong(), any(TimeUnit.class), eq(true));
    }

    @Test
    public void test_25_throwsWrongException() throws Exception
    {
        doThrow(new ErrorReplyBusException(WRONG_ERROR_NAME)).when(mockConfigClient).ResetConfigurations(eq(defaultLanguage), any(String[].class));

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_25(),
                "Calling ResetConfigurations() with an invalid field did not return the expected error expected:<org.alljoyn.Error.[InvalidValue]> but was:<org.alljoyn.Error.[WrongError]>");

        verify(mockConfigClient).ResetConfigurations(eq(defaultLanguage), any(String[].class));
        verify(mockServiceHelper).waitForNextDeviceAnnouncement(anyLong(), any(TimeUnit.class), eq(true));
    }

    @Test
    public void test_25_NoExceptionThrown() throws Exception
    {
        executeTestMethodFailsAssertion(getTestWrapperFor_v1_25(), "Calling ResetConfigurations() with an invalid field must return an error, org.alljoyn.Error.InvalidValue");

        verify(mockConfigClient).ResetConfigurations(eq(defaultLanguage), any(String[].class));
        verify(mockServiceHelper).waitForNextDeviceAnnouncement(anyLong(), any(TimeUnit.class), eq(true));
    }

    @Test
    public void test_26_success() throws Exception
    {
        when(mockServiceAvailabilityHandler.waitForSessionLost(anyLong(), any(TimeUnit.class))).thenReturn(true);

        executeTestMethod(getTestWrapperFor_v1_26());

        verify(mockServiceAvailabilityHandler).waitForSessionLost(anyLong(), any(TimeUnit.class));
        verify(mockServiceHelper, times(3)).waitForNextDeviceAnnouncement(anyLong(), any(TimeUnit.class), eq(true));
        verify(mockServiceHelper, times(3)).connectAboutClient(eq(deviceAboutAnnouncement), any(ServiceAvailabilityListener.class));
        verify(mockConfigClient).restart();
        verify(mockServiceHelper, times(2)).release();
    }

    @Test
    public void test_26_timedOutWaitingForSessionLost() throws Exception
    {
        when(mockServiceAvailabilityHandler.waitForSessionLost(anyLong(), any(TimeUnit.class))).thenReturn(false);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_26(), "Timed out waiting for session to be lost");

        verify(mockServiceAvailabilityHandler).waitForSessionLost(anyLong(), any(TimeUnit.class));
        verify(mockServiceHelper, times(2)).waitForNextDeviceAnnouncement(anyLong(), any(TimeUnit.class), eq(true));
        verify(mockServiceHelper, times(2)).connectAboutClient(eq(deviceAboutAnnouncement), any(ServiceAvailabilityListener.class));
        verify(mockConfigClient).restart();
        verify(mockServiceHelper, times(2)).release();
    }

    @Test
    public void test_26_timedOutWaitingForAboutAnnouncement() throws Exception
    {
        BusException be = new BusException("Timed out waiting for About announcement");
        when(mockServiceAvailabilityHandler.waitForSessionLost(anyLong(), any(TimeUnit.class))).thenReturn(true);
        when(mockServiceHelper.waitForNextDeviceAnnouncement(anyLong(), any(TimeUnit.class), eq(true))).thenReturn(deviceAboutAnnouncement).thenReturn(deviceAboutAnnouncement)
                .thenThrow(be);

        executeTestMethodThrowsException(getTestWrapperFor_v1_26(), "Timed out waiting for About announcement");

        verify(mockServiceAvailabilityHandler).waitForSessionLost(anyLong(), any(TimeUnit.class));
        verify(mockServiceHelper, times(3)).waitForNextDeviceAnnouncement(anyLong(), any(TimeUnit.class), eq(true));
        verify(mockServiceHelper, times(2)).connectAboutClient(any(AboutAnnouncementDetails.class), any(ServiceAvailabilityListener.class));
        verify(mockConfigClient).restart();
        verify(mockServiceHelper, times(2)).release();
    }

    @Test
    public void test_27_success() throws Exception
    {
        Map<String, Object> updatedConfigData = new HashMap<String, Object>(configData);
        Map<String, Object> updatedAboutData = new HashMap<String, Object>(aboutData);
        updatedAboutData.put(AboutKeys.ABOUT_DEVICE_NAME, NEW_DEVICE_NAME);
        updatedConfigData.put(AboutKeys.ABOUT_DEVICE_NAME, NEW_DEVICE_NAME);

        AboutAnnouncementDetails updatedAboutAnnouncement = createAboutAnnouncement(updatedAboutData);

        when(mockServiceHelper.waitForNextDeviceAnnouncement(anyLong(), any(TimeUnit.class), eq(true))).thenReturn(deviceAboutAnnouncement).thenReturn(deviceAboutAnnouncement)
                .thenReturn(updatedAboutAnnouncement).thenReturn(updatedAboutAnnouncement).thenReturn(updatedAboutAnnouncement).thenReturn(deviceAboutAnnouncement);

        when(mockAboutClient.getAbout(defaultLanguage)).thenReturn(updatedAboutData).thenReturn(updatedAboutData).thenReturn(aboutData);
        when(mockConfigClient.getConfig(defaultLanguage)).thenReturn(updatedConfigData).thenReturn(updatedConfigData).thenReturn(configData);

        when(mockServiceAvailabilityHandler.waitForSessionLost(anyLong(), any(TimeUnit.class))).thenReturn(true);

        when(mockServiceHelper.connectAboutClient(eq(updatedAboutAnnouncement), any(ServiceAvailabilityListener.class))).thenReturn(mockAboutClient);
        when(mockServiceHelper.connectConfigClient(updatedAboutAnnouncement)).thenReturn(mockConfigClient);

        executeTestMethod(getTestWrapperFor_v1_27());

        verify(mockConfigClient).restart();
        verify(mockServiceHelper, times(2)).connectAboutClient(eq(deviceAboutAnnouncement), any(ServiceAvailabilityListener.class));
        verify(mockServiceHelper).connectAboutClient(eq(updatedAboutAnnouncement), any(ServiceAvailabilityListener.class));
        verify(mockServiceHelper).connectConfigClient(eq(updatedAboutAnnouncement));
        verify(mockAboutClient, times(3)).getAbout(defaultLanguage);
        verify(mockConfigClient, times(3)).getConfig(defaultLanguage);
        verify(mockServiceHelper, times(6)).waitForNextDeviceAnnouncement(anyLong(), any(TimeUnit.class), eq(true));
    }

    @Test
    public void test_27_timedOutWaitingForSessionLost() throws Exception
    {
        Map<String, Object> updatedConfigData = new HashMap<String, Object>(configData);
        Map<String, Object> updatedAboutData = new HashMap<String, Object>(aboutData);
        updatedAboutData.put(AboutKeys.ABOUT_DEVICE_NAME, NEW_DEVICE_NAME);
        updatedConfigData.put(AboutKeys.ABOUT_DEVICE_NAME, NEW_DEVICE_NAME);

        AboutAnnouncementDetails updatedAboutAnnouncement = createAboutAnnouncement(updatedAboutData);

        when(mockServiceHelper.waitForNextDeviceAnnouncement(anyLong(), any(TimeUnit.class), eq(true))).thenReturn(deviceAboutAnnouncement).thenReturn(deviceAboutAnnouncement)
                .thenReturn(updatedAboutAnnouncement);

        when(mockAboutClient.getAbout(defaultLanguage)).thenReturn(updatedAboutData);
        when(mockConfigClient.getConfig(defaultLanguage)).thenReturn(updatedConfigData);

        when(mockServiceAvailabilityHandler.waitForSessionLost(anyLong(), any(TimeUnit.class))).thenReturn(false);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_27(), "Timed out waiting for session to be lost");

        verify(mockConfigClient).restart();
        verify(mockAboutClient).getAbout(defaultLanguage);
        verify(mockConfigClient).getConfig(defaultLanguage);
        verify(mockServiceHelper, times(3)).waitForNextDeviceAnnouncement(anyLong(), any(TimeUnit.class), eq(true));
    }

    @Test
    public void test_27_configurationNotRetainedOnRestart() throws Exception
    {
        Map<String, Object> updatedConfigData = new HashMap<String, Object>(configData);
        Map<String, Object> updatedAboutData = new HashMap<String, Object>(aboutData);
        updatedAboutData.put(AboutKeys.ABOUT_DEVICE_NAME, NEW_DEVICE_NAME);
        updatedConfigData.put(AboutKeys.ABOUT_DEVICE_NAME, NEW_DEVICE_NAME);

        AboutAnnouncementDetails updatedAboutAnnouncement = createAboutAnnouncement(updatedAboutData);

        when(mockServiceHelper.waitForNextDeviceAnnouncement(anyLong(), any(TimeUnit.class), eq(true))).thenReturn(deviceAboutAnnouncement).thenReturn(deviceAboutAnnouncement)
                .thenReturn(updatedAboutAnnouncement).thenReturn(deviceAboutAnnouncement);

        when(mockAboutClient.getAbout(defaultLanguage)).thenReturn(updatedAboutData);
        when(mockConfigClient.getConfig(defaultLanguage)).thenReturn(updatedConfigData);

        when(mockServiceAvailabilityHandler.waitForSessionLost(anyLong(), any(TimeUnit.class))).thenReturn(true);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_27(),
                "Received About announcement did not contain expected DeviceName expected:<[newD]eviceName> but was:<[d]eviceName>");

        verify(mockServiceHelper, times(2)).connectAboutClient(eq(deviceAboutAnnouncement), any(ServiceAvailabilityListener.class));
        verify(mockAboutClient).getAbout(defaultLanguage);
        verify(mockConfigClient).getConfig(defaultLanguage);
        verify(mockServiceHelper, times(4)).waitForNextDeviceAnnouncement(anyLong(), any(TimeUnit.class), eq(true));
    }

    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    // TODO

    @Test
    @Ignore
    public void testConfig_v1_27DeviceRestartRememberConfData() throws Exception
    {

        executeTestMethod(getTestWrapperFor_v1_27());

        verify(mockServiceHelper).release();

        verify(mockConfigClient, Mockito.times(1)).restart();
        verify(mockAboutClient, Mockito.times(3)).getAbout(any(String.class));
        verify(mockConfigClient, Mockito.times(4)).getConfig(any(String.class));
        verify(mockConfigClient, Mockito.times(2)).setConfig(anyMap(), (any(String.class)));
        verify(mockConfigService).stopConfigClient();

    }// testConfig_v1_27DeviceRestartRememberConfData

    @Test
    @Ignore
    public void testConfig_v1_28EmptyPasscodeNotChanged() throws Exception
    {

        final String INVALID_VALUE = "org.alljoyn.Error.InvalidValue";

        Mockito.doAnswer(new Answer<Void>()
        {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable
            {
                char[] passCode = (char[]) invocation.getArguments()[1];
                if (passCode.length == 0)
                {
                    throw new ErrorReplyBusException(INVALID_VALUE, "Invalid value");
                }
                return null;
            }
        }).when(mockConfigClient).setPasscode(any(String.class), any(char[].class));

        executeTestMethod(getTestWrapperFor_v1_28());

        verify(mockServiceHelper).release();

        verify(mockConfigClient).getConfig(any(String.class));
        verify(mockConfigClient).setPasscode(any(String.class), (any(char[].class)));
        verify(mockConfigService).stopConfigClient();
    }// testConfig_v1_28EmptyPasscodeNotChanged

    @Test
    @Ignore
    public void testConfig_v1_29PasscodeChanged() throws Exception
    {

        executeTestMethod(getTestWrapperFor_v1_29());

        verify(mockServiceHelper).release();

        verify(mockConfigClient, Mockito.times(2)).getConfig(any(String.class));
        verify(mockConfigClient, Mockito.times(2)).setPasscode(any(String.class), (any(char[].class)));
        verify(mockConfigService).stopConfigClient();
    }// testConfig_v1_29PasscodeChanged

    @Test
    @Ignore
    public void testConfig_v1_30PasscodeChangedSingleChar() throws Exception
    {

        executeTestMethod(getTestWrapperFor_v1_30());

        verify(mockServiceHelper).release();

        verify(mockConfigClient, Mockito.times(2)).getConfig(any(String.class));
        verify(mockConfigClient, Mockito.times(2)).setPasscode(any(String.class), (any(char[].class)));
        verify(mockConfigService).stopConfigClient();
    }// testConfig_v1_30PasscodeChangedSingleChar

    @Test
    @Ignore
    public void testConfig_v1_31PasscodeChangedSpecialChars() throws Exception
    {

        executeTestMethod(getTestWrapperFor_v1_31());

        verify(mockServiceHelper).release();

        verify(mockConfigClient, Mockito.times(2)).getConfig(any(String.class));
        verify(mockConfigClient, Mockito.times(2)).setPasscode(any(String.class), (any(char[].class)));
        verify(mockConfigService).stopConfigClient();
    }// testConfig_v1_31PasscodeChangedSpecialChars

	

    @Test
    public void testConfig_v1_32PasscodeChangedPersistOnRestart() throws Exception
    {
    	when(mockServiceAvailabilityHandler.waitForSessionLost(anyLong(), any(TimeUnit.class))).thenReturn(true);
        executeTestMethod(getTestWrapperFor_v1_32());

        verify(mockServiceAvailabilityHandler).waitForSessionLost(anyLong(), any(TimeUnit.class));
        verify(mockServiceHelper, Mockito.times(4)).release();
        verify(mockServiceHelper, Mockito.times(4)).startAboutClient();
        verify(mockServiceHelper, Mockito.times(4)).startConfigClient();
        verify(mockServiceHelper, Mockito.times(5)).waitForNextDeviceAnnouncement(anyLong(),any(TimeUnit.class),Mockito.anyBoolean());
        verify(mockServiceHelper,Mockito.times(4)).enableAuthentication(Mockito.anyString());
        verify(mockServiceHelper, Mockito.times(3)).clearKeyStore();
        
        
        verify(mockConfigClient, Mockito.times(4)).getConfig(any(String.class));
        verify(mockConfigClient, Mockito.times(2)).setPasscode(any(String.class), (any(char[].class)));
        verify(mockAboutClient, Mockito.times(4)).disconnect();
        verify(mockConfigClient, Mockito.times(4)).disconnect();
        verify(mockConfigClient, Mockito.times(1)).restart();
        
    }


    @Test
    public void testConfig_v1_33FactoryResetNoUpdateConfiguratins() throws Exception
    {
        when(mockServiceAvailabilityHandler.waitForSessionLost(anyLong(), any(TimeUnit.class))).thenReturn(true);
        executeTestMethod(getTestWrapperFor_v1_33());
        verify(mockTestContext).waitForUserInput(mockUserInputDetails);
        verify(mockServiceHelper, Mockito.times(3)).waitForNextDeviceAnnouncement(anyLong(),any(TimeUnit.class),Mockito.anyBoolean());
        Map<String, Object> configMap=new HashMap<String, Object>();
        configMap.put(AboutKeys.ABOUT_DEVICE_NAME, "testdevice");
        configMap.put(AboutKeys.ABOUT_DEFAULT_LANGUAGE, "testdefaultlang");
        when(mockConfigClient.getConfig("")).thenReturn(configMap);
    }
    
    @Test
    public void testConfig_v1_33FactoryResetNoUpdateConfiguratinsThrowsException() throws Exception
    {
        ErrorReplyBusException toBeThrown = new ErrorReplyBusException(AllJoynErrorReplyCodes.FEATURE_NOT_AVAILABLE);
        doThrow(toBeThrown).when(mockConfigClient).factoryReset();

        executeTestMethod(getTestWrapperFor_v1_33());
       
    }
    
    @Test
    public void testConfig_v1_33FactoryResetNoUpdateConfiguratinsRethrowsException() throws Exception
    {
        ErrorReplyBusException toBeThrown = new ErrorReplyBusException("");
        doThrow(toBeThrown).when(mockConfigClient).factoryReset();

        try{
        executeTestMethod(getTestWrapperFor_v1_33());
        }catch(ErrorReplyBusException erbe){
            assertEquals(toBeThrown, erbe);
            
        }
       
    }
    

    @Test
    public void testConfig_v1_34FactoryResetAfterUpdateConfigurations() throws Exception
    {
        when(mockServiceAvailabilityHandler.waitForSessionLost(anyLong(), any(TimeUnit.class))).thenReturn(true);
        
        Map<String, Object> configMap=new HashMap<String, Object>();
        configMap.put(AboutKeys.ABOUT_DEVICE_NAME, "deviceName");
        
        Map<String, Object> newConfigMap=new HashMap<String, Object>();
        newConfigMap.put(AboutKeys.ABOUT_DEVICE_NAME, "newDeviceName");
        newConfigMap.put(AboutKeys.ABOUT_DEFAULT_LANGUAGE, defaultLanguage);
        
        when(mockConfigClient.getConfig("")).thenReturn(configMap);
         when(mockConfigClient.getConfig(defaultLanguage)).thenReturn(newConfigMap);
         
         
         Map<String, Object> aboutMap=new HashMap<String, Object>();
         aboutMap.put(AboutKeys.ABOUT_DEVICE_NAME, "deviceName");
         
         when(mockAboutClient.getAbout("")).thenReturn(aboutMap);
        
        AboutAnnouncementDetails newDeviceAboutAnnouncement =Mockito.mock(AboutAnnouncementDetails.class);
        when(newDeviceAboutAnnouncement.getDeviceName()).thenReturn(NEW_DEVICE_NAME);
        when(newDeviceAboutAnnouncement.getDefaultLanguage()).thenReturn(defaultLanguage);
        
        AboutAnnouncementDetails reconnectDeviceAboutAnnouncement =Mockito.mock(AboutAnnouncementDetails.class);
        when(reconnectDeviceAboutAnnouncement.getDeviceName()).thenReturn("deviceName");
        when(reconnectDeviceAboutAnnouncement.getDefaultLanguage()).thenReturn("");
        
       when(mockServiceHelper.waitForNextDeviceAnnouncement(anyLong(), any(TimeUnit.class), eq(true))).thenReturn(deviceAboutAnnouncement).thenReturn(newDeviceAboutAnnouncement).thenReturn(deviceAboutAnnouncement).thenReturn(deviceAboutAnnouncement);
        
        executeTestMethod(getTestWrapperFor_v1_34());
        verify(mockTestContext).waitForUserInput(mockUserInputDetails);
       
    }

    @Test
    public void testConfig_v1_35FactoryResetResetsPasscode() throws Exception
    {
       
        when(mockServiceAvailabilityHandler.waitForSessionLost(anyLong(), any(TimeUnit.class))).thenReturn(true);
        executeTestMethod(getTestWrapperFor_v1_35());
        verify(mockTestContext).waitForUserInput(mockUserInputDetails);
        verify(mockServiceHelper, Mockito.times(4)).waitForNextDeviceAnnouncement(anyLong(),any(TimeUnit.class),Mockito.anyBoolean());
        Map<String, Object> configMap=new HashMap<String, Object>();
        configMap.put(AboutKeys.ABOUT_DEVICE_NAME, "testdevice");
        configMap.put(AboutKeys.ABOUT_DEFAULT_LANGUAGE, "testdefaultlang");
        when(mockConfigClient.getConfig("")).thenReturn(configMap);
    }

    /**
     * Mock the announcement sending
     * 
     * @throws Exception
     */
    private void updateAboutAnnouncement() throws Exception
    {
        configData.put(AboutKeys.ABOUT_DEVICE_NAME, aboutData.get(AboutKeys.ABOUT_DEVICE_NAME));
        configData.put(AboutKeys.ABOUT_DEFAULT_LANGUAGE, aboutData.get(AboutKeys.ABOUT_DEFAULT_LANGUAGE));

        deviceAboutAnnouncement = createAboutAnnouncement(aboutData);

        when(mockServiceHelper.waitForNextDeviceAnnouncement(anyLong(), any(TimeUnit.class), eq(true))).thenReturn(deviceAboutAnnouncement);

        when(mockServiceHelper.connectAboutClient(eq(deviceAboutAnnouncement), any(ServiceAvailabilityListener.class))).thenReturn(mockAboutClient);
        when(mockServiceHelper.connectConfigClient(deviceAboutAnnouncement)).thenReturn(mockConfigClient);
    }

    private AboutAnnouncementDetails createAboutAnnouncement(Map<String, Object> aboutDataMap) throws BusException
    {
        List<BusObjectDescription> busObjectDescriptions = new ArrayList<BusObjectDescription>();
        busObjectDescriptions.add(AboutUtils.getBusObjectDescription("/About", new String[]
        { "org.alljoyn.About" }));
        busObjectDescriptions.add(AboutUtils.getBusObjectDescription("/Config", new String[]
        { "org.alljoyn.Config" }));

        AboutAnnouncementDetails aboutAnnouncement = new AboutAnnouncementDetails(serviceName, PORT, busObjectDescriptions.toArray(new BusObjectDescription[busObjectDescriptions
                .size()]), TransportUtil.toVariantMap(aboutDataMap));
        aboutAnnouncement.convertAboutMap();
        return aboutAnnouncement;
    }

    private void createSetConfigMock() throws BusException
    {
        // Create mock for the setConfig call. Catch the setConfig arguments and
        // update the aboutData map with the values from config
        Mockito.doAnswer(new Answer<Void>()
        {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable
            {
                @SuppressWarnings("unchecked")
                Map<String, Object> confSettings = (Map<String, Object>) invocation.getArguments()[0];
                assertNotNull(confSettings);
                aboutData.putAll(confSettings);
                // TODO?
                // updateAnnouncement();
                return null;
            }
        }).when(mockConfigClient).setConfig(anyMap(), any(String.class));
    }// createSetConfigMock

    /**
     * Prepare mock for the configClient.ResetConfigurations() method call
     * Retrieve the list of keys to be reset
     * 
     * @throws BusException
     */
    private void prepareResetMock() throws BusException
    {
        Mockito.doAnswer(new Answer<Void>()
        {

            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable
            {
                String[] fields = (String[]) invocation.getArguments()[1];

                // Reset the ConfigTestSuite internal aboutData with the default
                // values for the
                // keys requested to be reset
                for (String key : fields)
                {
                    aboutData.put(key, resetAboutData.get(key));
                }

                // TODO?
                // updateAnnouncement();
                return null;
            }// answer

        }).when(mockConfigClient).ResetConfigurations(any(String.class), any(String[].class));
    }// prepareResetMock

    @Test
    public void testDeviceNameWithSpecialCharacters()
    {
        String value = configTestSuite.getDeviceNameWithSpecialCharacters();
        assertEquals("!\"#$%&'()*+,-.:;<=>?[\\]^_{|}", value);
    }

    protected void waitForNextDeviceAnnouncementAndThenJoin() throws InterruptedException
    {
        // wait for the test to call waitForNextDeviceAnnouncement
        waitForWaitForNextDeviceAnnouncementCall.await(5, TimeUnit.SECONDS);

        testThread.join();
    }

    // TODO test setup failures, etc.
    // e.g.:
    // verify(mockAboutClient).disconnect();
    // verify(mockConfigClient).disconnect();
    // verify(mockServiceHelper).release();

    @Test
    public void resourcesAreReleasedIfConnectingToAboutClientThrowsException() throws Exception
    {
        Exception exception = new Exception();
        when(mockServiceHelper.connectAboutClient(eq(deviceAboutAnnouncement), any(ServiceAvailabilityListener.class))).thenThrow(exception);

        try
        {
            configTestSuite.setUp();
            fail("setup should have thrown exception");
        }
        catch (Exception e)
        {
            assertEquals(exception, e);
        }
        verify(mockServiceHelper).release();
        verify(mockAboutClient, times(0)).disconnect();
    }

    @Test
    public void resourcesAreReleasedIfConnectingToConfigClientThrowsException() throws Exception
    {
        Exception exception = new Exception();
        when(mockServiceHelper.connectConfigClient(deviceAboutAnnouncement)).thenThrow(exception);
        try
        {
            configTestSuite.setUp();
            fail("setup should have thrown exception");
        }
        catch (Exception e)
        {
            assertEquals(exception, e);
        }
        verify(mockServiceHelper).release();
        verify(mockAboutClient).disconnect();
        verify(mockConfigClient, times(0)).disconnect();
    }

    protected TestWrapper getTestWrapperFor_v1_01()
    {
        return new TestWrapper()
        {
            @Override
            public void executeTestMethod() throws Exception
            {
                configTestSuite.testConfig_v1_01AppIdEqualsDeviceId();
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
                configTestSuite.testConfig_v1_02ConnectWithWrongPasscode();
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
                configTestSuite.testConfig_v1_03_ValidateVersion();
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
                configTestSuite.testConfig_v1_04GetConfigurationsWithDefaultLanguage();
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
                configTestSuite.testConfig_v1_05UnspecifiedLanguage();
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
                configTestSuite.testConfig_v1_06LangConsistence();
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
                configTestSuite.testConfig_v1_07UnsupportedLanguage();
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
                configTestSuite.testConfig_v1_08UpdateConfigurationsWithANewDeviceName();
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
                configTestSuite.testConfig_v1_09UpdateConfigurationsMaxLengthEqDeviceName();
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
                configTestSuite.testConfig_v1_10DeviceNameExceedsMaxLength();
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
                configTestSuite.testConfig_v1_11ChangeDeviceNametoEmpty();
            }
        };
    }

    protected TestWrapper getTestWrapperFor_v1_12()
    {
        return new TestWrapper()
        {
            @Override
            public void executeTestMethod() throws Exception
            {
                configTestSuite.testConfig_v1_12DeviceNameSpecial();
            }
        };
    }

    protected TestWrapper getTestWrapperFor_v1_13()
    {
        return new TestWrapper()
        {
            @Override
            public void executeTestMethod() throws Exception
            {
                configTestSuite.testConfig_v1_13UpdateUnsupportedLanguage();
            }
        };
    }

    protected TestWrapper getTestWrapperFor_v1_14()
    {
        return new TestWrapper()
        {
            @Override
            public void executeTestMethod() throws Exception
            {
                configTestSuite.testConfig_v1_14UpdateDefaultLang();
            }
        };
    }

    protected TestWrapper getTestWrapperFor_v1_15()
    {
        return new TestWrapper()
        {
            @Override
            public void executeTestMethod() throws Exception
            {
                configTestSuite.testConfig_v1_15UpdateDefaultLanguageToUnsupportedLanguage();
            }
        };
    }

    protected TestWrapper getTestWrapperFor_v1_16()
    {
        return new TestWrapper()
        {
            @Override
            public void executeTestMethod() throws Exception
            {
                configTestSuite.testConfig_v1_16TestChangetoUnspecifiedLanguage();
            }
        };
    }

    protected TestWrapper getTestWrapperFor_v1_18()
    {
        return new TestWrapper()
        {
            @Override
            public void executeTestMethod() throws Exception
            {
                configTestSuite.testConfig_v1_18TestUpdateReadOnlyField();
            }
        };
    }

    protected TestWrapper getTestWrapperFor_v1_19()
    {
        return new TestWrapper()
        {
            @Override
            public void executeTestMethod() throws Exception
            {
                configTestSuite.testConfig_v1_19TestUpdateInvalidField();
            }
        };
    }

    protected TestWrapper getTestWrapperFor_v1_20()
    {
        return new TestWrapper()
        {
            @Override
            public void executeTestMethod() throws Exception
            {
                configTestSuite.testConfig_v1_20TestResetDeviceName();
            }
        };
    }

    protected TestWrapper getTestWrapperFor_v1_21()
    {
        return new TestWrapper()
        {
            @Override
            public void executeTestMethod() throws Exception
            {
                configTestSuite.testConfig_v1_21ResetDefaultLanguage();
            }
        };
    }

    protected TestWrapper getTestWrapperFor_v1_22()
    {
        return new TestWrapper()
        {
            @Override
            public void executeTestMethod() throws Exception
            {
                configTestSuite.testConfig_v1_22ResetDefaultMultiLanguage();
            }
        };
    }

    protected TestWrapper getTestWrapperFor_v1_23()
    {
        return new TestWrapper()
        {
            @Override
            public void executeTestMethod() throws Exception
            {
                configTestSuite.testConfig_v1_23FailResetDeviceId();
            }
        };
    }

    protected TestWrapper getTestWrapperFor_v1_24()
    {
        return new TestWrapper()
        {
            @Override
            public void executeTestMethod() throws Exception
            {
                configTestSuite.testConfig_v1_24FailResetUnsupportedLang();
            }
        };
    }

    protected TestWrapper getTestWrapperFor_v1_25()
    {
        return new TestWrapper()
        {
            @Override
            public void executeTestMethod() throws Exception
            {
                configTestSuite.testConfig_v1_25FailResetInvalidField();
            }
        };
    }

    protected TestWrapper getTestWrapperFor_v1_26()
    {
        return new TestWrapper()
        {
            @Override
            public void executeTestMethod() throws Exception
            {
                configTestSuite.testConfig_v1_26DeviceRestart();
            }
        };
    }

    protected TestWrapper getTestWrapperFor_v1_27()
    {
        return new TestWrapper()
        {
            @Override
            public void executeTestMethod() throws Exception
            {
                configTestSuite.testConfig_v1_27DeviceRestartRememberConfData();
            }
        };
    }

    protected TestWrapper getTestWrapperFor_v1_28()
    {
        return new TestWrapper()
        {
            @Override
            public void executeTestMethod() throws Exception
            {
                configTestSuite.testConfig_v1_28EmptyPasscodeNotChanged();
            }
        };
    }

    protected TestWrapper getTestWrapperFor_v1_29()
    {
        return new TestWrapper()
        {
            @Override
            public void executeTestMethod() throws Exception
            {
                configTestSuite.testConfig_v1_29PasscodeChanged();
            }
        };
    }

    protected TestWrapper getTestWrapperFor_v1_30()
    {
        return new TestWrapper()
        {
            @Override
            public void executeTestMethod() throws Exception
            {
                configTestSuite.testConfig_v1_30PasscodeChangedSingleChar();
            }
        };
    }

    protected TestWrapper getTestWrapperFor_v1_31()
    {
        return new TestWrapper()
        {
            @Override
            public void executeTestMethod() throws Exception
            {
                configTestSuite.testConfig_v1_31PasscodeChangedSpecialChars();
            }
        };
    }

    protected TestWrapper getTestWrapperFor_v1_32()
    {
        return new TestWrapper()
        {
            @Override
            public void executeTestMethod() throws Exception
            {
                configTestSuite.testConfig_v1_32PasscodeChangedPersistOnRestart();
            }
        };
    }

    protected TestWrapper getTestWrapperFor_v1_33()
    {
        return new TestWrapper()
        {
            @Override
            public void executeTestMethod() throws Exception
            {
                configTestSuite.testConfig_v1_33FactoryResetNoUpdateConfiguratins();
            }
        };
    }

    protected TestWrapper getTestWrapperFor_v1_34()
    {
        return new TestWrapper()
        {
            @Override
            public void executeTestMethod() throws Exception
            {
                configTestSuite.testConfig_v1_34FactoryResetAfterUpdateConfigurations();
            }
        };
    }

    protected TestWrapper getTestWrapperFor_v1_35()
    {
        return new TestWrapper()
        {
            @Override
            public void executeTestMethod() throws Exception
            {
                configTestSuite.testConfig_v1_35FactoryResetResetsPasscode();
            }
        };
    }

    @Override
    protected void executeTestMethod(TestWrapper testWrapper) throws Exception
    {
        configTestSuite.setUp();
        try
        {
            testWrapper.executeTestMethod();
        }
        finally
        {
            configTestSuite.tearDown();
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> anyMap()
    {
        return (Map<String, Object>) any(Map.class);
    }

}