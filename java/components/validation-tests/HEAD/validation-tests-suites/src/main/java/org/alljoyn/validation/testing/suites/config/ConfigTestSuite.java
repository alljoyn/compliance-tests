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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.alljoyn.about.AboutKeys;
import org.alljoyn.about.client.AboutClient;
import org.alljoyn.bus.BusException;
import org.alljoyn.bus.ErrorReplyBusException;
import org.alljoyn.bus.Mutable.StringValue;
import org.alljoyn.bus.Status;
import org.alljoyn.config.client.ConfigClient;
import org.alljoyn.onboarding.transport.OnboardingTransport;
import org.alljoyn.services.android.security.SrpAnonymousKeyListener;
import org.alljoyn.services.android.utils.AndroidLogger;
import org.alljoyn.services.common.ServiceAvailabilityListener;
import org.alljoyn.validation.framework.AppUnderTestDetails;
import org.alljoyn.validation.framework.UserInputDetails;
import org.alljoyn.validation.framework.annotation.Disabled;
import org.alljoyn.validation.framework.annotation.ValidationSuite;
import org.alljoyn.validation.framework.annotation.ValidationTest;
import org.alljoyn.validation.testing.suites.AllJoynErrorReplyCodes;
import org.alljoyn.validation.testing.suites.BaseTestSuite;
import org.alljoyn.validation.testing.utils.about.AboutAnnouncementDetails;
import org.alljoyn.validation.testing.utils.log.Logger;
import org.alljoyn.validation.testing.utils.log.LoggerFactory;
import org.alljoyn.validation.testing.utils.services.ServiceAvailabilityHandler;
import org.alljoyn.validation.testing.utils.services.ServiceHelper;

@ValidationSuite(name = "Config-v1")
public class ConfigTestSuite extends BaseTestSuite implements ServiceAvailabilityListener
{
    private static final String TAG = "ConfigTestSuite";
    private static final Logger logger = LoggerFactory.getLogger(TAG);
    private static final String NEW_DEVICE_NAME = "newDeviceName";
    private static final String INVALID_LANGUAGE_CODE = "INVALID";
    private static final int CONFIG_CLIENT_RECONNECT_WAIT_TIME = 10000;
    private static final String BUS_APPLICATION_NAME = "ConfigTestSuite";
    public static final long SESSION_LOST_TIMEOUT_IN_SECONDS = 30;
    private static final long SESSION_CLOSE_TIMEOUT_IN_SECONDS = 5;

    private ConfigClient configClient = null;
    private ServiceHelper serviceHelper;
    private AboutClient aboutClient;

    private AboutAnnouncementDetails deviceAboutAnnouncement;

    private AppUnderTestDetails appUnderTestDetails;
    private UUID dutAppId;
    private String dutDeviceId;
    private ServiceAvailabilityHandler serviceAvailabilityHandler;
    private String keyStorePath;
    private static final String[] DEVICE_NAME_FIELD = new String[]
    { AboutKeys.ABOUT_DEVICE_NAME };
    private static final String[] DEFAULT_LANGUAGE_FIELD = new String[]
    { AboutKeys.ABOUT_DEFAULT_LANGUAGE };
    private static final String[] BOTH_FIELDS = new String[]
    { AboutKeys.ABOUT_DEVICE_NAME, AboutKeys.ABOUT_DEFAULT_LANGUAGE };
    private static final String[] INVALID_FIELD = new String[]
    { "INVALID" };
    private static final char[] NEW_PASSCODE = "111111".toCharArray();
    private static final char[] SINGLE_CHAR_PASSCODE = "1".toCharArray();
    private static final char[] DEFAULT_PINCODE = SrpAnonymousKeyListener.DEFAULT_PINCODE;
    private static final char[] SPECIAL_CHARS_PASSCODE = "!@#$%^".toCharArray();
    private long aboutAnnouncementTimeout;

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();

        logger.debug("test setUp started");

        try
        {
            appUnderTestDetails = getValidationTestContext().getAppUnderTestDetails();
            dutDeviceId = appUnderTestDetails.getDeviceId();
            logger.debug(String.format("Running Config test case against Device ID: %s", dutDeviceId));
            dutAppId = appUnderTestDetails.getAppId();
            logger.debug(String.format("Running Config test case against App ID: %s", dutAppId));
            keyStorePath = getValidationTestContext().getKeyStorePath();
            logger.debug(String.format("Running Config test case using KeyStorePath: %s", keyStorePath));
            aboutAnnouncementTimeout = determineAboutAnnouncementTimeout();

            initServiceHelper();
            resetPasscodeIfNeeded();

            logger.debug("test setUp done");
        }
        catch (Exception e)
        {
            logger.debug("test setUp thrown an exception", e);
            releaseResources();
            throw e;
        }
    }

    protected void initServiceHelper() throws BusException, Exception
    {
        releaseServiceHelper();
        serviceHelper = createServiceHelper();

        serviceHelper.initialize(BUS_APPLICATION_NAME, dutDeviceId, dutAppId);

        serviceHelper.startConfigClient();

        deviceAboutAnnouncement = waitForNextDeviceAnnouncement();

        connectAboutClient(deviceAboutAnnouncement);
        connectConfigClient(deviceAboutAnnouncement);

        serviceHelper.enableAuthentication(keyStorePath);
    }

    private void releaseServiceHelper()
    {
        try
        {
            if (aboutClient != null)
            {
                aboutClient.disconnect();
                aboutClient = null;
            }
            if (configClient != null)
            {

                configClient.disconnect();
                configClient = null;
            }
            if (serviceHelper != null)
            {
                serviceHelper.release();
                waitForSessionToClose();
                serviceHelper = null;
            }
        }
        catch (Exception ex)
        {
            logger.debug("Exception releasing resources", ex);
        }
    }

    private void reconnectClients() throws Exception
    {
        releaseServiceHelper();
        initServiceHelper();
    }

    private void connectConfigClient(AboutAnnouncementDetails aboutAnnouncement) throws Exception
    {
        configClient = serviceHelper.connectConfigClient(aboutAnnouncement);
    }

    private void connectAboutClient(AboutAnnouncementDetails aboutAnnouncement) throws Exception
    {
        serviceAvailabilityHandler = createServiceAvailabilityHandler();
        aboutClient = serviceHelper.connectAboutClient(aboutAnnouncement, serviceAvailabilityHandler);
    }

    private AboutAnnouncementDetails waitForNextDeviceAnnouncement() throws Exception
    {
        logger.info("Waiting for About announcement");
        return serviceHelper.waitForNextDeviceAnnouncement(aboutAnnouncementTimeout, TimeUnit.SECONDS, true);
    }

    private void waitForSessionToClose() throws Exception
    {
        logger.info("Waiting for session to close");
        serviceHelper.waitForSessionToClose(SESSION_CLOSE_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS);
    }

    @Override
    protected void tearDown() throws Exception
    {
        super.tearDown();
        logger.debug("test tearDown started");
        releaseResources();
        logger.debug("test tearDown done");
    }

    @ValidationTest(name = "Config-v1-01")
    public void testConfig_v1_01AppIdEqualsDeviceId() throws Exception
    {
        UUID dutAppId = deviceAboutAnnouncement.getAppId();
        String dutDeviceId = deviceAboutAnnouncement.getDeviceId();

        if (isAppIdEqualToDeviceId(dutAppId, dutDeviceId))
        {
            logger.info(String.format("System App AppId is equal to the DeviceId"));
        }
        else
        {
            getValidationTestContext().addNote(String.format("System App AppId: %s is not equal to DeviceId: %s", dutAppId.toString(), dutDeviceId));
        }
    }

    @ValidationTest(name = "Config-v1-02")
    public void testConfig_v1_02ConnectWithWrongPasscode() throws Exception
    {
        reconnectClients();
        char[] wrongPasscode = "123456".toCharArray();

        serviceHelper.clearKeyStore();
        serviceHelper.setAuthPassword(deviceAboutAnnouncement, wrongPasscode);

        boolean exceptionThrown = false;
        try
        {
            logger.info(String.format("Attempting to retrieve Version property from Config interface using the passcode: %s", Arrays.toString(wrongPasscode)));
            callMethodToCheckAuthentication();
        }
        catch (BusException be)
        {
            exceptionThrown = true;
            logger.info("Expected exception thrown");
            logger.debug("Expection details:", be);
            assertTrue("A call to a Config interface method must require authentication", serviceHelper.isPeerAuthenticationAttempted(deviceAboutAnnouncement));
            assertFalse("A call to a Config interface method with the wrong passcode must fail authentication",
                    serviceHelper.isPeerAuthenticationSuccessful(deviceAboutAnnouncement));
        }
        if (!exceptionThrown)
        {
            logger.info("Expected exception not thrown");
            fail("A call to a Config interface method with the wrong passcode must return an error");
        }
    }

    @ValidationTest(name = "Config-v1-03")
    @Disabled
    public void testConfig_v1_03_ValidateVersion() throws Exception
    {
        short version = callGetVersionOnConfig();
        assertEquals("Config version does not match", 1, version);
    }

    @ValidationTest(name = "Config-v1-04")
    public void testConfig_v1_04GetConfigurationsWithDefaultLanguage() throws Exception
    {
        Map<String, Object> configMap = callGetConfigurationsWithDefaultLanguage();
        checkMapForRequiredFields(configMap);
        checkConsistencyWithAboutAnnouncement(configMap);
    }

    @ValidationTest(name = "Config-v1-05")
    public void testConfig_v1_05UnspecifiedLanguage() throws Exception
    {
        Map<String, Object> configMapWithDefaultLanguage = callGetConfigurationsWithDefaultLanguage();
        Map<String, Object> configMapWithUnspecifiedLanguage = callGetConfigurations("");

        checkMapForRequiredFields(configMapWithDefaultLanguage);
        checkMapForRequiredFields(configMapWithUnspecifiedLanguage);

        logger.info("Checking that DeviceName and DefaultLanguage from the two GetConfigurations() calls match");
        compareMaps(configMapWithDefaultLanguage, configMapWithUnspecifiedLanguage);
    }

    @ValidationTest(name = "Config-v1-06")
    public void testConfig_v1_06LangConsistence() throws Exception
    {
        Map<String, Object> aboutMap = callGetAboutForDefaultLanguage();

        String[] suppLangs = getSupportedLanguages(aboutMap);

        if (suppLangs.length > 1)
        {
            for (String lang : suppLangs)
            {
                Map<String, Object> configMapForLang = callGetConfigurations(lang);
                Map<String, Object> aboutMapForLang = callGetAbout(lang);

                checkMapForRequiredFields(configMapForLang);
                checkMapForRequiredFields(aboutMapForLang);

                logger.info(String.format("Comparing config and about maps for the language: %s", lang));
                compareMaps(configMapForLang, aboutMapForLang);
            }
        }
        else
        {
            getValidationTestContext().addNote("Only one language is supported");
        }
    }

    @ValidationTest(name = "Config-v1-07")
    public void testConfig_v1_07UnsupportedLanguage() throws Exception
    {
        String expectedError = AllJoynErrorReplyCodes.LANGUAGE_NOT_SUPPORTED;

        boolean exceptionThrown = false;
        try
        {
            callGetConfigurations(INVALID_LANGUAGE_CODE);
        }
        catch (ErrorReplyBusException erbe)
        {
            exceptionThrown = true;
            logger.debug("Received exception from GetConfigurations() with INVALID language", erbe);

            assertEquals("Calling GetConfigurations() on the Config interface with an unsupported language did not return the expected error",
                    AllJoynErrorReplyCodes.LANGUAGE_NOT_SUPPORTED, erbe.getErrorName());
        }
        if (!exceptionThrown)
        {
            fail(String.format("Calling GetConfigurations() on the Config interface with an unsupported language did not return an error, it must return an %s error",
                    expectedError));
        }
    }

    @ValidationTest(name = "Config-v1-08")
    public void testConfig_v1_08UpdateConfigurationsWithANewDeviceName() throws Exception
    {
        testUpdateConfigurations(AboutKeys.ABOUT_DEVICE_NAME, NEW_DEVICE_NAME);
    }

    @ValidationTest(name = "Config-v1-09")
    @Disabled
    public void testConfig_v1_09UpdateConfigurationsMaxLengthEqDeviceName() throws Exception
    {
        Map<String, Object> aboutMap = aboutClient.getAbout("");
        String originalDeviceName = getOriginalDeviceName(aboutMap);
        Short maxLength = (Short) aboutMap.get("MaxLength");
        logger.debug("About map returned max length as : " + maxLength);

        if (maxLength == null || maxLength == 0)
        {
            logger.debug("Max length does not exist, adding a note and returning");
            getValidationTestContext().addNote("MAX_LENGTH DOES NOT EXIST!");
            return;
        }

        Map<String, Object> mapToUpdateConfig = new HashMap<String, Object>();
        String newGeneratedDeviceName = generateDeviceName(maxLength);
        logger.debug("Update original device name : " + originalDeviceName + " to new device name : " + newGeneratedDeviceName);
        mapToUpdateConfig.put(AboutKeys.ABOUT_DEVICE_NAME, newGeneratedDeviceName);
        configClient.setConfig(mapToUpdateConfig, "");

        AboutAnnouncementDetails nextDeviceAnnouncement = serviceHelper.waitForNextDeviceAnnouncement(aboutAnnouncementTimeout, TimeUnit.SECONDS, true);

        logger.debug("Device Name returned from the new about announcement : " + nextDeviceAnnouncement.getDeviceName() + " and it should be : " + newGeneratedDeviceName);

        assertEquals(newGeneratedDeviceName, nextDeviceAnnouncement.getDeviceName());

        aboutMap = aboutClient.getAbout("");
        Map<String, Object> configMap = configClient.getConfig("");
        verifyValueForAboutAndConfig(aboutMap, configMap, AboutKeys.ABOUT_DEVICE_NAME, newGeneratedDeviceName);

        logger.debug("Reverting back to original device Name");
        mapToUpdateConfig = new HashMap<String, Object>();
        mapToUpdateConfig.put(AboutKeys.ABOUT_DEVICE_NAME, originalDeviceName);
        configClient.setConfig(mapToUpdateConfig, "");

        nextDeviceAnnouncement = serviceHelper.waitForNextDeviceAnnouncement(aboutAnnouncementTimeout, TimeUnit.SECONDS, true);

        logger.debug("Device Name returned from the new about announcement : " + nextDeviceAnnouncement.getDeviceName() + " and it should be : " + originalDeviceName);

        assertEquals(originalDeviceName, nextDeviceAnnouncement.getDeviceName());

        aboutMap = aboutClient.getAbout("");
        configMap = configClient.getConfig("");
        verifyValueForAboutAndConfig(aboutMap, configMap, AboutKeys.ABOUT_DEVICE_NAME, originalDeviceName);
    }

    @ValidationTest(name = "Config-v1-10")
    @Disabled
    public void testConfig_v1_10DeviceNameExceedsMaxLength() throws Exception
    {
        Map<String, Object> aboutMap = aboutClient.getAbout("");
        String originalDeviceName = getOriginalDeviceName(aboutMap);

        Short maxLength = (Short) aboutMap.get("MaxLength");

        logger.debug("About map returned max length as : " + maxLength);

        if (maxLength == null || maxLength == 0)
        {
            logger.debug("Max length does not exist, adding a note and returning");
            getValidationTestContext().addNote("MAX_LENGTH DOES NOT EXIST!");
            return;
        }

        try
        {
            Map<String, Object> mapToUpdateConfig = new HashMap<String, Object>();
            logger.debug("Setting device name to exceed max length");
            String newGeneratedDeviceName = generateDeviceName(maxLength + 1);
            mapToUpdateConfig.put(AboutKeys.ABOUT_DEVICE_NAME, newGeneratedDeviceName);
            configClient.setConfig(mapToUpdateConfig, "");
            fail("Calling setConfig with deviceName exceeding the Max length should throw an exception");

        }
        catch (ErrorReplyBusException erbe)
        {
            aboutMap = aboutClient.getAbout("");
            Map<String, Object> configMap = configClient.getConfig("");
            verifyValueForAboutAndConfig(aboutMap, configMap, AboutKeys.ABOUT_DEVICE_NAME, originalDeviceName);
        }

    }

    @ValidationTest(name = "Config-v1-11")
    @Disabled
    public void testConfig_v1_11ChangeDeviceNametoEmpty() throws Exception
    {
        logger.debug("Test change the DeviceName to empty");

        String originalDeviceName = deviceAboutAnnouncement.getDeviceName();
        logger.debug("Original Device Name : " + originalDeviceName);

        try
        {
            Map<String, Object> mapConfig = new HashMap<String, Object>();
            mapConfig.put(AboutKeys.ABOUT_DEVICE_NAME, "");
            configClient.setConfig(mapConfig, deviceAboutAnnouncement.getDefaultLanguage());
            fail("Calling UpdateConfigurations() to set DeviceName to an empty string must return an error");
        }
        catch (ErrorReplyBusException erbe)
        {
            assertEquals(AllJoynErrorReplyCodes.INVALID_VALUE, erbe.getErrorName());

            Map<String, Object> configMap = configClient.getConfig(deviceAboutAnnouncement.getDefaultLanguage());
            Map<String, Object> aboutMap = aboutClient.getAbout(deviceAboutAnnouncement.getDefaultLanguage());

            verifyValueForAboutAndConfig(aboutMap, configMap, AboutKeys.ABOUT_DEVICE_NAME, originalDeviceName);
            return;
        }

    }

    @ValidationTest(name = "Config-v1-12")
    public void testConfig_v1_12DeviceNameSpecial() throws Exception
    {
        testUpdateConfigurations(AboutKeys.ABOUT_DEVICE_NAME, getDeviceNameWithSpecialCharacters());
    }

    @ValidationTest(name = "Config-v1-13")
    public void testConfig_v1_13UpdateUnsupportedLanguage() throws Exception
    {
        String origDeviceName = deviceAboutAnnouncement.getDeviceName();

        boolean exceptionThrown = false;
        try
        {
            Map<String, Object> mapConfig = new HashMap<String, Object>();
            mapConfig.put(AboutKeys.ABOUT_DEVICE_NAME, origDeviceName);

            callUpdateConfigurations(INVALID_LANGUAGE_CODE, mapConfig);
        }
        catch (ErrorReplyBusException erbe)
        {
            exceptionThrown = true;
            logger.debug("Received exception from UpdateConfigurations() with INVALID language", erbe);

            assertEquals("Calling UpdateConfigurations() on the Config interface with an unsupported language did not return the expected error",
                    AllJoynErrorReplyCodes.LANGUAGE_NOT_SUPPORTED, erbe.getErrorName());
        }
        if (!exceptionThrown)
        {
            fail("Calling UpdateConfigurations() on the Config interface with an unsupported language must return a LanguageNotSupported error");
        }

    }

    @ValidationTest(name = "Config-v1-14")
    public void testConfig_v1_14UpdateDefaultLang() throws Exception
    {
        String newLang = getAnotherSupportedLanguage();
        if (newLang != null)
        {
            testUpdateConfigurations(AboutKeys.ABOUT_DEFAULT_LANGUAGE, newLang);
        }
        else
        {
            getValidationTestContext().addNote("Only one language is supported");
        }
    }

    private String getAnotherSupportedLanguage() throws BusException
    {
        String defaultLanguage = deviceAboutAnnouncement.getDefaultLanguage();

        Map<String, Object> aboutMap = callGetAboutForDefaultLanguage();

        String newLang = null;
        String[] suppLangs = getSupportedLanguages(aboutMap);
        if (suppLangs.length > 1)
        {
            for (String lang : suppLangs)
            {
                if (!lang.equals(defaultLanguage))
                {
                    newLang = lang;
                    logger.info(String.format("Found a supported language: %s", newLang));
                    break;
                }
            }
            assertNotNull("SupportedLanguages field contains duplicate languages", newLang);
        }
        return newLang;
    }

    @ValidationTest(name = "Config-v1-15")
    public void testConfig_v1_15UpdateDefaultLanguageToUnsupportedLanguage() throws Exception
    {
        String defaultLanguage = deviceAboutAnnouncement.getDefaultLanguage();

        boolean exceptionThrown = false;
        try
        {
            Map<String, Object> updateConfigData = new HashMap<String, Object>();
            updateConfigData.put(AboutKeys.ABOUT_DEFAULT_LANGUAGE, INVALID_LANGUAGE_CODE);
            callUpdateConfigurations(defaultLanguage, updateConfigData);
        }
        catch (ErrorReplyBusException ex)
        {
            exceptionThrown = true;
            logger.debug("Received exception from UpdateConfigurations() with unsupported language", ex);

            assertEquals("Calling UpdateConfigurations() on the Config interface to set the DefaultLanguage to an unsupported language did not return the expected error",
                    AllJoynErrorReplyCodes.LANGUAGE_NOT_SUPPORTED, ex.getErrorName());
        }
        if (!exceptionThrown)
        {
            fail(String.format("Calling UpdateConfigurations() to set the DefaultLanguage to an unsupported language must return an error, %s",
                    AllJoynErrorReplyCodes.LANGUAGE_NOT_SUPPORTED));
        }
    }

    @ValidationTest(name = "Config-v1-16")
    public void testConfig_v1_16TestChangetoUnspecifiedLanguage() throws Exception
    {
        String expectedError = AllJoynErrorReplyCodes.LANGUAGE_NOT_SUPPORTED;
        String defaultLanguage = deviceAboutAnnouncement.getDefaultLanguage();

        boolean exceptionThrown = false;
        try
        {
            Map<String, Object> updateConfigData = new HashMap<String, Object>();
            updateConfigData.put(AboutKeys.ABOUT_DEFAULT_LANGUAGE, "");
            callUpdateConfigurations(defaultLanguage, updateConfigData);
        }
        catch (ErrorReplyBusException erbe)
        {
            exceptionThrown = true;
            logger.debug("Received exception from UpdateConfigurations() with DefaultLanguage set to an unspecified language", erbe);

            assertEquals("Calling UpdateConfigurations() on the Config interface to update the DefaultLanguage field to an unsupported language did not return the expected error",
                    AllJoynErrorReplyCodes.LANGUAGE_NOT_SUPPORTED, erbe.getErrorName());
        }
        if (!exceptionThrown)
        {
            fail(String
                    .format("Calling UpdateConfigurations() on the Config interface to update the DefaultLanguage field to an unspecified language did not return an error, it must return an %s error",
                            expectedError));
        }
    }

    @ValidationTest(name = "Config-v1-18")
    @Disabled
    public void testConfig_v1_18TestUpdateReadOnlyField() throws Exception
    {
        String defaultLanguage = deviceAboutAnnouncement.getDefaultLanguage();
        boolean exceptionThrown = false;
        try
        {
            Map<String, Object> updateConfigData = new HashMap<String, Object>();
            updateConfigData.put(AboutKeys.ABOUT_DEVICE_ID, INVALID_LANGUAGE_CODE);

            callUpdateConfigurations(defaultLanguage, updateConfigData);
        }
        catch (ErrorReplyBusException ex)
        {
            exceptionThrown = true;
            logger.debug("Received exception from UpdateConfigurations() when trying to update DeviceId", ex);
            assertEquals("Calling UpdateConfigurations() to update the DeviceId did not return the expected error", AllJoynErrorReplyCodes.UPDATE_NOT_ALLOWED, ex.getErrorName());
        }
        if (!exceptionThrown)
        {
            fail("Calling UpdateConfigurations() to update the DeviceId did not return an error, expecting UpdateNotAllowed (since DeviceId is a read only field)");
        }
    }

    @ValidationTest(name = "Config-v1-19")
    public void testConfig_v1_19TestUpdateInvalidField() throws Exception
    {
        String defaultLanguage = deviceAboutAnnouncement.getDefaultLanguage();
        String expectedError = AllJoynErrorReplyCodes.INVALID_VALUE;

        boolean exceptionThrown = false;
        try
        {
            Map<String, Object> updateConfigData = new HashMap<String, Object>();
            updateConfigData.put("INVALID", "INVALID");
            callUpdateConfigurations(defaultLanguage, updateConfigData);
        }
        catch (ErrorReplyBusException ex)
        {
            exceptionThrown = true;

            logger.debug("Received an ErrorReplyBusException", ex);

            assertEquals("Calling UpdateConfigurations() to update an invalid field did not return the expected error", AllJoynErrorReplyCodes.INVALID_VALUE, ex.getErrorName());
        }
        if (!exceptionThrown)
        {
            fail(String.format("Calling UpdateConfigurations() on the Config interface to update an invalid field did not return an error, it must return an %s error",
                    expectedError));
        }
    }

    @ValidationTest(name = "Config-v1-20")
    public void testConfig_v1_20TestResetDeviceName() throws Exception
    {
        String defaultLanguage = deviceAboutAnnouncement.getDefaultLanguage();

        callResetConfigurations(defaultLanguage, DEVICE_NAME_FIELD);
        deviceAboutAnnouncement = waitForNextAnnouncementAndCheckFieldValue(AboutKeys.ABOUT_DEFAULT_LANGUAGE, defaultLanguage);

        Map<String, Object> aboutMap = callGetAboutForDefaultLanguage();
        String originalDeviceName = (String) aboutMap.get(AboutKeys.ABOUT_DEVICE_NAME);

        logger.debug(String.format("Original Device Name: %s", originalDeviceName));

        String newDeviceName = NEW_DEVICE_NAME;

        serviceHelper.clearQueuedDeviceAnnouncements();

        Map<String, Object> updatedConfigMap = new HashMap<String, Object>();
        updatedConfigMap.put(AboutKeys.ABOUT_DEVICE_NAME, newDeviceName);
        callUpdateConfigurations(defaultLanguage, updatedConfigMap);

        deviceAboutAnnouncement = waitForNextAnnouncementAndCheckFieldValue(AboutKeys.ABOUT_DEVICE_NAME, newDeviceName);

        Map<String, Object> configMap = callGetConfigurationsWithDefaultLanguage();
        aboutMap = callGetAboutForDefaultLanguage();
        verifyValueForAboutAndConfig(aboutMap, configMap, AboutKeys.ABOUT_DEVICE_NAME, newDeviceName);

        callResetConfigurations(defaultLanguage, DEVICE_NAME_FIELD);

        deviceAboutAnnouncement = waitForNextAnnouncementAndCheckFieldValue(AboutKeys.ABOUT_DEVICE_NAME, originalDeviceName);

        configMap = callGetConfigurationsWithDefaultLanguage();
        aboutMap = callGetAboutForDefaultLanguage();
        verifyValueForAboutAndConfig(aboutMap, configMap, AboutKeys.ABOUT_DEVICE_NAME, originalDeviceName);
    }

    private String getOriginalDeviceName(Map<String, Object> aboutMap)
    {
        return (String) aboutMap.get(AboutKeys.ABOUT_DEVICE_NAME);

    }

    @ValidationTest(name = "Config-v1-21")
    public void testConfig_v1_21ResetDefaultLanguage() throws Exception
    {
        String defaultLanguage = deviceAboutAnnouncement.getDefaultLanguage();

        callResetConfigurations(defaultLanguage, DEFAULT_LANGUAGE_FIELD);

        Map<String, Object> configMap = callGetConfigurationsWithDefaultLanguage();
        Map<String, Object> aboutMap = callGetAboutForDefaultLanguage();

        compareMapsForField(AboutKeys.ABOUT_DEFAULT_LANGUAGE, aboutMap, configMap);
    }

    @ValidationTest(name = "Config-v1-22")
    public void testConfig_v1_22ResetDefaultMultiLanguage() throws Exception
    {
        String newDefaultLanguage = getAnotherSupportedLanguage();
        if (newDefaultLanguage != null)
        {
            String defaultLanguage = deviceAboutAnnouncement.getDefaultLanguage();

            callResetConfigurations(defaultLanguage, DEFAULT_LANGUAGE_FIELD);

            Map<String, Object> aboutMap = callGetAbout("");
            String originalDefaultLanguage = (String) aboutMap.get(AboutKeys.ABOUT_DEFAULT_LANGUAGE);
            Map<String, Object> configMap = callGetConfigurations("");

            compareMapsForField(AboutKeys.ABOUT_DEFAULT_LANGUAGE, aboutMap, configMap);

            logger.debug(String.format("Original DefaultLanguage: %s", originalDefaultLanguage));

            serviceHelper.clearQueuedDeviceAnnouncements();

            Map<String, Object> updatedConfigMap = new HashMap<String, Object>();
            updatedConfigMap.put(AboutKeys.ABOUT_DEFAULT_LANGUAGE, newDefaultLanguage);
            callUpdateConfigurations(defaultLanguage, updatedConfigMap);

            deviceAboutAnnouncement = waitForNextAnnouncementAndCheckFieldValue(AboutKeys.ABOUT_DEFAULT_LANGUAGE, newDefaultLanguage);

            aboutMap = callGetAbout("");
            configMap = callGetConfigurations("");
            verifyValueForAboutAndConfig(aboutMap, configMap, AboutKeys.ABOUT_DEFAULT_LANGUAGE, newDefaultLanguage);

            defaultLanguage = deviceAboutAnnouncement.getDefaultLanguage();

            callResetConfigurations(defaultLanguage, DEFAULT_LANGUAGE_FIELD);

            deviceAboutAnnouncement = waitForNextAnnouncementAndCheckFieldValue(AboutKeys.ABOUT_DEFAULT_LANGUAGE, originalDefaultLanguage);

            aboutMap = callGetAbout("");
            configMap = callGetConfigurations("");
            verifyValueForAboutAndConfig(aboutMap, configMap, AboutKeys.ABOUT_DEFAULT_LANGUAGE, originalDefaultLanguage);
        }
        else
        {
            getValidationTestContext().addNote("Only one language is supported");
        }
    }

    @ValidationTest(name = "Config-v1-23")
    @Disabled
    public void testConfig_v1_23FailResetDeviceId() throws Exception
    {
        logger.debug("Verify impossibility to reset the DeviceId");

        final String EXP_EXCEPTION = "org.alljoyn.Error.InvalidValue";
        Map<String, Object> aboutSett = aboutClient.getAbout(deviceAboutAnnouncement.getDefaultLanguage());
        String origDeviceId = (String) aboutSett.get(AboutKeys.ABOUT_DEVICE_ID);

        testFailReset(EXP_EXCEPTION, deviceAboutAnnouncement.getDefaultLanguage(), new String[]
        { AboutKeys.ABOUT_DEVICE_ID });

        logger.debug("Verify with getAbout() that the DeviceId hasn't changed");
        aboutSett = aboutClient.getAbout(deviceAboutAnnouncement.getDefaultLanguage());
        String deviceId = (String) aboutSett.get(AboutKeys.ABOUT_DEVICE_ID);
        assertEquals(origDeviceId, deviceId);
    }

    @ValidationTest(name = "Config-v1-24")
    public void testConfig_v1_24FailResetUnsupportedLang() throws Exception
    {
        boolean exceptionThrown = false;
        try
        {
            callResetConfigurations(INVALID_LANGUAGE_CODE, DEVICE_NAME_FIELD);
        }
        catch (ErrorReplyBusException erbe)
        {
            exceptionThrown = true;
            logger.debug("Received exception from ResetConfigurations() with an unsupported language", erbe);

            assertEquals("Calling ResetConfigurations() with an unsupported language did not return the expected error", AllJoynErrorReplyCodes.LANGUAGE_NOT_SUPPORTED,
                    erbe.getErrorName());
        }
        if (!exceptionThrown)
        {
            fail(String.format("Calling ResetConfigurations() with an unsupported language must return an error, %s", AllJoynErrorReplyCodes.LANGUAGE_NOT_SUPPORTED));
        }
    }

    @ValidationTest(name = "Config-v1-25")
    public void testConfig_v1_25FailResetInvalidField() throws Exception
    {
        boolean exceptionThrown = false;
        try
        {
            String defaultLanguage = deviceAboutAnnouncement.getDefaultLanguage();
            callResetConfigurations(defaultLanguage, INVALID_FIELD);
        }
        catch (ErrorReplyBusException erbe)
        {
            exceptionThrown = true;
            logger.debug("Received exception from ResetConfigurations() with an invalid field", erbe);

            assertEquals("Calling ResetConfigurations() with an invalid field did not return the expected error", AllJoynErrorReplyCodes.INVALID_VALUE, erbe.getErrorName());
        }
        if (!exceptionThrown)
        {
            fail(String.format("Calling ResetConfigurations() with an invalid field must return an error, %s", AllJoynErrorReplyCodes.INVALID_VALUE));
        }
    }

    @ValidationTest(name = "Config-v1-26")
    public void testConfig_v1_26DeviceRestart() throws Exception
    {
        reconnectClients();
        callRestartOnConfig();

        assertTrue("Timed out waiting for session to be lost", serviceAvailabilityHandler.waitForSessionLost(SESSION_LOST_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS));

        deviceAboutAnnouncement = waitForNextDeviceAnnouncement();

        connectAboutClient(deviceAboutAnnouncement);
    }

    @ValidationTest(name = "Config-v1-27")
    public void testConfig_v1_27DeviceRestartRememberConfData() throws Exception
    {
        reconnectClients();
        String originalDeviceName = deviceAboutAnnouncement.getDeviceName();

        updateConfigurationsAndVerifyResult(AboutKeys.ABOUT_DEVICE_NAME, NEW_DEVICE_NAME);

        callRestartOnConfig();

        assertTrue("Timed out waiting for session to be lost", serviceAvailabilityHandler.waitForSessionLost(SESSION_LOST_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS));

        deviceAboutAnnouncement = waitForNextAnnouncementAndCheckFieldValue(AboutKeys.ABOUT_DEVICE_NAME, NEW_DEVICE_NAME);

        reconnectClients();

        Map<String, Object> configMap = callGetConfigurationsWithDefaultLanguage();
        Map<String, Object> aboutMap = callGetAboutForDefaultLanguage();
        verifyValueForAboutAndConfig(aboutMap, configMap, AboutKeys.ABOUT_DEVICE_NAME, NEW_DEVICE_NAME);

        updateConfigurationsAndVerifyResult(AboutKeys.ABOUT_DEVICE_NAME, originalDeviceName);
    }

    @ValidationTest(name = "Config-v1-28")
    @Disabled
    public void testConfig_v1_28EmptyPasscodeNotChanged() throws Exception
    {
        logger.debug("start testConfig_v1_28EmptyPasscodeNotChanged");
        String realm = "";
        try
        {
            logger.debug("Call setPasscode for the Realm: '" + realm + "', and the empty Passcode");
            configClient.setPasscode(realm, new char[]
            {});
            fail("Calling SetPasscode() with an empty value for the passcode must return an error, InvalidValue");
        }
        catch (BusException be)
        {
            logger.debug("ReceivedBusException", be);

            if (!(be instanceof ErrorReplyBusException))
            {
                fail("The received exception is not ErrorReplyBusException");
            }

            ErrorReplyBusException erbe = (ErrorReplyBusException) be;
            if (erbe.getErrorName().equals(AllJoynErrorReplyCodes.FEATURE_NOT_AVAILABLE))
            {

                logger.debug("setPasscode feature is not supported");
                getValidationTestContext().addNote("Set passcode feature is not supported");

                serviceHelper.clearKeyStore();
                configClient.disconnect();

                Thread.sleep(CONFIG_CLIENT_RECONNECT_WAIT_TIME);

                Status status = configClient.connect();
                if (status != Status.OK)
                {
                    fail("Fail to connect to the Config server");
                }

                logger.debug("Call getConfig to verify authentication with an old password");
                try
                {
                    assertNotNull(configClient.getConfig(deviceAboutAnnouncement.getDefaultLanguage()));
                }
                catch (BusException beGetConf)
                {
                    fail("Failed to call getConfig for the defaultLanguage: '" + deviceAboutAnnouncement.getDefaultLanguage() + "'");
                }
            }
        }

    }

    @ValidationTest(name = "Config-v1-29")
    public void testConfig_v1_29PasscodeChanged() throws Exception
    {
        testChangePasscode(NEW_PASSCODE);
    }

    @ValidationTest(name = "Config-v1-30")
    public void testConfig_v1_30PasscodeChangedSingleChar() throws Exception
    {
        testChangePasscode(SINGLE_CHAR_PASSCODE);
    }

    @ValidationTest(name = "Config-v1-31")
    public void testConfig_v1_31PasscodeChangedSpecialChars() throws Exception
    {
        testChangePasscode(SPECIAL_CHARS_PASSCODE);
    }

    @ValidationTest(name = "Config-v1-32")
    public void testConfig_v1_32PasscodeChangedPersistOnRestart() throws Exception
    {
        changePasscodeAndReconnect(NEW_PASSCODE);

        callRestartOnConfig();

        assertTrue("Timed out waiting for session to be lost", serviceAvailabilityHandler.waitForSessionLost(SESSION_LOST_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS));

        deviceAboutAnnouncement = waitForNextDeviceAnnouncement();

        reconnectClients();
        serviceHelper.clearKeyStore();

        serviceHelper.setAuthPassword(deviceAboutAnnouncement, NEW_PASSCODE);

        callMethodToCheckAuthentication();

        changePasscodeAndReconnect(DEFAULT_PINCODE);
    }

    @ValidationTest(name = "Config-v1-33")
    public void testConfig_v1_33FactoryResetNoUpdateConfiguratins() throws Exception
    {
        String deviceNameBeforeReset = null;
        String defaultLanguageBeforeReset = null;
        if (deviceAboutAnnouncement.supportsInterface(OnboardingTransport.INTERFACE_NAME))
        {
            getValidationTestContext().addNote("The device supports Onboarding so this Test Case is Not Applicable");

        }
        else
        {
            callResetConfigurations(deviceAboutAnnouncement.getDefaultLanguage(), BOTH_FIELDS);

            Map<String, Object> configMap = callGetConfigurations("");
            deviceNameBeforeReset = (String) configMap.get(AboutKeys.ABOUT_DEVICE_NAME);
            defaultLanguageBeforeReset = (String) configMap.get(AboutKeys.ABOUT_DEFAULT_LANGUAGE);

            serviceHelper.clearQueuedDeviceAnnouncements();

            boolean factoryResetSupport = true;
            try
            {
                callFactoryResetOnConfig();
            }
            catch (ErrorReplyBusException erbe)
            {
                if (erbe.getErrorName().equals(AllJoynErrorReplyCodes.FEATURE_NOT_AVAILABLE))
                {
                    factoryResetSupport = false;
                }
                else
                {
                    throw erbe;
                }
            }

            if (!factoryResetSupport)
            {
                getValidationTestContext().addNote("FactoryReset method is not a supported feature!");
            }
            else
            {
                assertTrue("Timed out waiting for session to be lost", serviceAvailabilityHandler.waitForSessionLost(SESSION_LOST_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS));

                UserInputDetails userInputDetails = createUserInputDetails();
                // always continue, we ignore the response here
                getValidationTestContext().waitForUserInput(userInputDetails);

                waitForNextDeviceAnnouncement();

                reconnectClients();

                configMap = callGetConfigurations("");
                String deviceNameAfterReset = (String) configMap.get(AboutKeys.ABOUT_DEVICE_NAME);
                String defaultLanguageAfterReset = (String) configMap.get(AboutKeys.ABOUT_DEFAULT_LANGUAGE);

                assertEquals("FactoryReset() set the DeviceName to a different value than ResetConfigurations()", deviceNameBeforeReset, deviceNameAfterReset);
                assertEquals("FactoryReset() set the DefaultLanguage to a different value than ResetConfigurations()", defaultLanguageBeforeReset, defaultLanguageAfterReset);
            }
        }
    }

    protected UserInputDetails createUserInputDetails()
    {
        return new UserInputDetails("Onboard DUT (if needed)",
                "FactoryReset() has been called on the DUT. Please Onboard the device to the Personal AP (if needed) and then click Continue", "Continue");
    }

    @ValidationTest(name = "Config-v1-34")
    public void testConfig_v1_34FactoryResetAfterUpdateConfigurations() throws Exception
    {
        String defaultDeviceName = null;
        if (deviceAboutAnnouncement.supportsInterface(OnboardingTransport.INTERFACE_NAME))
        {
            getValidationTestContext().addNote("The device supports Onboarding so this Test Case is Not Applicable");

        }
        else
        {
            callResetConfigurations(deviceAboutAnnouncement.getDefaultLanguage(), DEVICE_NAME_FIELD);

            Map<String, Object> configMap = callGetConfigurations("");
            Map<String, Object> aboutMap = callGetAbout("");
            compareMapsForField(AboutKeys.ABOUT_DEVICE_NAME, aboutMap, configMap);

            defaultDeviceName = (String) configMap.get(AboutKeys.ABOUT_DEVICE_NAME);

            serviceHelper.clearQueuedDeviceAnnouncements();

            updateConfigurationsAndVerifyResult(AboutKeys.ABOUT_DEVICE_NAME, NEW_DEVICE_NAME);

            boolean factoryResetSupport = true;
            try
            {
                callFactoryResetOnConfig();
            }
            catch (ErrorReplyBusException erbe)
            {
                if (erbe.getErrorName().equals(AllJoynErrorReplyCodes.FEATURE_NOT_AVAILABLE))
                {
                    factoryResetSupport = false;
                }
                else
                {
                    throw erbe;
                }
            }

            if (!factoryResetSupport)
            {
                getValidationTestContext().addNote("FactoryReset method is not a supported feature!");

                updateConfigurationsAndVerifyResult(AboutKeys.ABOUT_DEVICE_NAME, defaultDeviceName);
            }
            else
            {
                assertTrue("Timed out waiting for session to be lost", serviceAvailabilityHandler.waitForSessionLost(SESSION_LOST_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS));

                UserInputDetails userInputDetails = createUserInputDetails();
                // always continue, we ignore the response here
                getValidationTestContext().waitForUserInput(userInputDetails);

                deviceAboutAnnouncement = waitForNextDeviceAnnouncement();

                reconnectClients();

                configMap = callGetConfigurations("");
                aboutMap = callGetAbout("");
                verifyValueForAboutAndConfig(aboutMap, configMap, AboutKeys.ABOUT_DEVICE_NAME, defaultDeviceName);
            }
        }
    }

    @ValidationTest(name = "Config-v1-35")
    public void testConfig_v1_35FactoryResetResetsPasscode() throws Exception
    {
        if (deviceAboutAnnouncement.supportsInterface(OnboardingTransport.INTERFACE_NAME))
        {
            getValidationTestContext().addNote("The device supports Onboarding so this Test Case is Not Applicable");
        }
        else
        {
            changePasscodeAndReconnect(NEW_PASSCODE);

            boolean factoryResetSupport = true;
            try
            {
                callFactoryResetOnConfig();
            }
            catch (ErrorReplyBusException erbe)
            {
                if (erbe.getErrorName().equals(AllJoynErrorReplyCodes.FEATURE_NOT_AVAILABLE))
                {
                    factoryResetSupport = false;
                }
                else
                {
                    throw erbe;
                }
            }

            if (!factoryResetSupport)
            {
                getValidationTestContext().addNote("FactoryReset method is not a supported feature!");

                changePasscodeAndReconnect(DEFAULT_PINCODE);
            }
            else
            {
                assertTrue("Timed out waiting for session to be lost", serviceAvailabilityHandler.waitForSessionLost(SESSION_LOST_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS));

                UserInputDetails userInputDetails = createUserInputDetails();
                // always continue, we ignore the response here
                getValidationTestContext().waitForUserInput(userInputDetails);

                deviceAboutAnnouncement = waitForNextDeviceAnnouncement();

                reconnectClients();
                serviceHelper.clearKeyStore();

                serviceHelper.setAuthPassword(deviceAboutAnnouncement, DEFAULT_PINCODE);

                callMethodToCheckAuthentication();
            }
        }

    }

    private void testUpdateConfigurations(String fieldName, String newFieldValue) throws BusException, Exception
    {
        String defaultLanguage = deviceAboutAnnouncement.getDefaultLanguage();

        String originalValue;
        if (fieldName.equals(AboutKeys.ABOUT_DEVICE_NAME))
        {
            originalValue = deviceAboutAnnouncement.getDeviceName();
        }
        else
        {
            originalValue = defaultLanguage;
        }
        updateConfigurationsAndVerifyResult(fieldName, newFieldValue);

        updateConfigurationsAndVerifyResult(fieldName, originalValue);
    }

    private void updateConfigurationsAndVerifyResult(String fieldName, String newFieldValue) throws BusException, Exception
    {
        String defaultLanguage = deviceAboutAnnouncement.getDefaultLanguage();

        Map<String, Object> mapConfig = new HashMap<String, Object>();
        mapConfig.put(fieldName, newFieldValue);
        callUpdateConfigurations(defaultLanguage, mapConfig);

        watiForNextAnnouncementAndVerifyFieldValue(fieldName, newFieldValue);
    }

    private void watiForNextAnnouncementAndVerifyFieldValue(String fieldName, String newFieldValue) throws Exception, BusException
    {
        deviceAboutAnnouncement = waitForNextAnnouncementAndCheckFieldValue(fieldName, newFieldValue);

        Map<String, Object> configMap = callGetConfigurationsWithDefaultLanguage();
        Map<String, Object> aboutMap = callGetAboutForDefaultLanguage();
        verifyValueForAboutAndConfig(aboutMap, configMap, fieldName, newFieldValue);
    }

    @Override
    public void connectionLost()
    {
        logger.debug("The connection with the remote device has lost");
    }

    private String generateDeviceName(int length)
    {

        StringBuilder sb = new StringBuilder();

        for (int i = 1; i <= length; ++i)
        {
            sb.append("A");
        }

        return sb.toString();
    }

    protected ServiceHelper createServiceHelper()
    {
        return new ServiceHelper(new AndroidLogger());
    }

    protected ServiceAvailabilityHandler createServiceAvailabilityHandler()
    {
        return new ServiceAvailabilityHandler();
    }

    public String buildNewDeviceNameWithSpecialChars(int maxLength)
    {

        String specChars = "! @ # $ % ^ & * ( ) - _ + = [ ] { } | \\ \" ' : ; , . < > ~";
        StringBuilder newGeneratedDeviceNameBuilder = new StringBuilder("newDeviceName_").append(specChars);
        int newDeviceNameLength = newGeneratedDeviceNameBuilder.length();
        logger.debug("Generated Device name with special characters : " + newGeneratedDeviceNameBuilder.toString());
        if (maxLength > 0 && newDeviceNameLength > maxLength)
        {
            newGeneratedDeviceNameBuilder.delete(maxLength, newDeviceNameLength);
            logger.debug("Truncated device name since length was greater than maxlength : " + newGeneratedDeviceNameBuilder.toString());

        }

        logger.debug("Length of truncated device name : " + newGeneratedDeviceNameBuilder.length());
        String newGeneratedDeviceName = newGeneratedDeviceNameBuilder.toString().trim();
        logger.debug("Length of trimmed device name : " + newGeneratedDeviceName.length());
        return newGeneratedDeviceName;
    }

    private void verifyValueForAboutAndConfig(Map<String, Object> aboutMap, Map<String, Object> configMap, String key, String verifyValue)
    {
        assertEquals(String.format("Value for %s retrieved from GetAboutData() does not match expected value", key), verifyValue, aboutMap.get(key));
        assertEquals(String.format("Value for %s retrieved from GetConfigurations() does not match expected value", key), verifyValue, configMap.get(key));
    }

    private void testFailReset(String exceptionName, String resetLang, String[] fieldsToReset)
    {
        try
        {
            configClient.ResetConfigurations(resetLang, fieldsToReset);
            fail("The expected exception hasn't being thrown while resetting the illegal fields for a language: '" + resetLang + "'");
        }
        catch (BusException be)
        {

            if (be instanceof ErrorReplyBusException)
            {
                ErrorReplyBusException erbe = (ErrorReplyBusException) be;

                String name = erbe.getErrorName();
                String msg = erbe.getErrorMessage();
                logger.debug("Received the expected exception, name: '" + name + "', msg: '" + msg + "'");
                logger.debug("Verifying that the received exception name is as expected: '" + exceptionName + "'");
                assertEquals(exceptionName, name);
            }
            return;
        }

    }

    protected StringValue createStringValue()
    {
        return new StringValue();
    }

    protected String getDeviceNameWithSpecialCharacters()
    {
        StringBuilder builder = new StringBuilder();
        appendChars(builder, 33, 47);
        appendChars(builder, 58, 64);
        appendChars(builder, 91, 96);
        appendChars(builder, 123, 126);
        return builder.toString();
    }

    private void appendChars(StringBuilder builder, int startIdx, int endIdx)
    {
        for (int asciiCode = startIdx; asciiCode < endIdx; asciiCode++)
        {
            builder.append((char) asciiCode);
        }
    }

    private boolean isAppIdEqualToDeviceId(UUID dutAppId, String dutDeviceId)
    {
        logger.info(String.format("Comparing DeviceId: %s to AppId: %s", dutDeviceId, dutAppId));
        return dutDeviceId.equals(dutAppId.toString());
    }

    private void checkMapForRequiredFields(Map<String, Object> map)
    {
        assertTrue("Required DeviceName field not present in map", map.containsKey(AboutKeys.ABOUT_DEVICE_NAME));
        assertTrue("Required DefaultLanguage field not present in map", map.containsKey(AboutKeys.ABOUT_DEFAULT_LANGUAGE));
    }

    private void checkConsistencyWithAboutAnnouncement(Map<String, Object> configMap)
    {
        logger.info("Checking that DeviceName and DefaultLanguage from GetConfigurations() matches the values in About announcemment");
        assertEquals("DeviceName from GetConfigurations() does not match About announcement", deviceAboutAnnouncement.getDeviceName(), configMap.get(AboutKeys.ABOUT_DEVICE_NAME));
        assertEquals("DefaultLanguage from GetConfigurations() does not match About announcement", deviceAboutAnnouncement.getDefaultLanguage(),
                configMap.get(AboutKeys.ABOUT_DEFAULT_LANGUAGE));
    }

    private void compareMaps(Map<String, Object> map1, Map<String, Object> map2) throws Exception
    {
        compareMapsForField(AboutKeys.ABOUT_DEVICE_NAME, map1, map2);
        compareMapsForField(AboutKeys.ABOUT_DEFAULT_LANGUAGE, map1, map2);
    }

    private void compareMapsForField(String fieldName, Map<String, Object> map1, Map<String, Object> map2) throws Exception
    {
        assertEquals(String.format("%s does not match", fieldName), map1.get(fieldName), map2.get(fieldName));
    }

    private Map<String, Object> callGetConfigurationsWithDefaultLanguage() throws BusException
    {
        String defaultLanguage = deviceAboutAnnouncement.getDefaultLanguage();
        Map<String, Object> configMap = callGetConfigurations(defaultLanguage);
        return configMap;
    }

    private Map<String, Object> callGetConfigurations(String languageTag) throws BusException
    {
        logger.info(String.format("Calling GetConfigurations() with language: \"%s\"", languageTag));
        Map<String, Object> configMap = configClient.getConfig(languageTag);
        return configMap;
    }

    private void callUpdateConfigurations(String languageTag, Map<String, Object> configMap) throws BusException
    {
        logger.info(String.format("Calling UpdateConfigurations() with language: \"%s\" and %s", languageTag, configMap));
        configClient.setConfig(configMap, languageTag);
    }

    private void callResetConfigurations(String languageTag, String[] fields) throws BusException
    {
        logger.info(String.format("Calling ResetConfigurations() with language: \"%s\" and %s", languageTag, Arrays.toString(fields)));
        configClient.ResetConfigurations(languageTag, fields);
    }

    private Map<String, Object> callGetAboutForDefaultLanguage() throws BusException
    {
        String defaultLanguage = deviceAboutAnnouncement.getDefaultLanguage();
        return callGetAbout(defaultLanguage);
    }

    private Map<String, Object> callGetAbout(String language) throws BusException
    {
        logger.info(String.format("Calling getAbout() with the language: \"%s\"", language));
        return aboutClient.getAbout(language);
    }

    private String[] getSupportedLanguages(Map<String, Object> aboutMap)
    {
        String[] suppLangs = (String[]) aboutMap.get(AboutKeys.ABOUT_SUPPORTED_LANGUAGES);
        logger.debug(String.format("Supported languages: %s", Arrays.toString(suppLangs)));
        return suppLangs;
    }

    private short callGetVersionOnConfig() throws BusException
    {
        short version = configClient.getVersion();
        logger.info(String.format("Call to getVersion() returns: %d", version));
        return version;
    }

    private void callMethodToCheckAuthentication() throws BusException
    {
        callGetConfigurations("");
        // TODO add this back once getVersion() issue is resolved
        // short version = configClient.getVersion();
        // logger.info(String.format("Call to getVersion() returns: %d",
        // version));
        // return version;
    }

    private void callRestartOnConfig() throws BusException
    {
        logger.info("Calling Restart() on Config");
        configClient.restart();
    }

    private void callFactoryResetOnConfig() throws BusException
    {
        logger.info("Calling FactoryReset() on Config");
        configClient.factoryReset();
    }

    private void callSetPasscodeOnConfig(char[] newPasscode) throws BusException
    {
        String realm = "";
        logger.info(String.format("Calling SetPasscode() on Config with realm: %s; and passcode: %s", realm, Arrays.toString(newPasscode)));
        configClient.setPasscode(realm, newPasscode);

    }

    protected AboutAnnouncementDetails waitForNextAnnouncementAndCheckFieldValue(String fieldName, String fieldValue) throws Exception
    {
        logger.info("Waiting for updating About announcement");
        AboutAnnouncementDetails nextDeviceAnnouncement = waitForNextDeviceAnnouncement();
        if (fieldName.equals(AboutKeys.ABOUT_DEVICE_NAME))
        {
            assertEquals("Received About announcement did not contain expected DeviceName", fieldValue, nextDeviceAnnouncement.getDeviceName());
        }
        else
        {
            assertEquals("Received About announcement did not contain expected DefaultLanguage", fieldValue, nextDeviceAnnouncement.getDefaultLanguage());
        }
        return nextDeviceAnnouncement;
    }

    private void testChangePasscode(char[] newPasscode) throws BusException, Exception
    {
        changePasscodeAndReconnect(newPasscode);

        changePasscodeAndReconnect(DEFAULT_PINCODE);
    }

    private void changePasscodeAndReconnect(char[] newPasscode) throws BusException, Exception
    {
        callSetPasscodeOnConfig(newPasscode);

        reconnectClients();
        serviceHelper.clearKeyStore();

        serviceHelper.setAuthPassword(deviceAboutAnnouncement, newPasscode);

        callMethodToCheckAuthentication();
    }

    private void releaseResources()
    {
        releaseServiceHelper();
    }

    private void resetPasscodeIfNeeded() throws Exception
    {
        try
        {
            callMethodToCheckAuthentication();
        }
        catch (Exception exception)
        {
            try
            {
                setPasscode(NEW_PASSCODE);
            }
            catch (Exception e)
            {
                try
                {
                    setPasscode(SINGLE_CHAR_PASSCODE);
                }
                catch (Exception e1)
                {
                    setPasscode(SPECIAL_CHARS_PASSCODE);
                }
            }
        }
    }

    private void setPasscode(char[] pwd) throws Exception
    {
        serviceHelper.clearKeyStore();
        serviceHelper.setAuthPassword(deviceAboutAnnouncement, pwd);

        callMethodToCheckAuthentication();
        changePasscodeAndReconnect(DEFAULT_PINCODE);
    }
}