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
package org.alljoyn.validation.testing.suites.onboarding;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.alljoyn.about.AboutKeys;
import org.alljoyn.about.client.AboutClient;
import org.alljoyn.bus.BusException;
import org.alljoyn.bus.ErrorReplyBusException;
import org.alljoyn.config.client.ConfigClient;
import org.alljoyn.config.transport.ConfigTransport;
import org.alljoyn.onboarding.OnboardingService;
import org.alljoyn.onboarding.OnboardingService.AuthType;
import org.alljoyn.onboarding.transport.MyScanResult;
import org.alljoyn.onboarding.transport.OBLastError;
import org.alljoyn.onboarding.transport.ScanInfo;
import org.alljoyn.services.android.security.SrpAnonymousKeyListener;
import org.alljoyn.services.android.utils.AndroidLogger;
import org.alljoyn.validation.framework.AppUnderTestDetails;
import org.alljoyn.validation.framework.ValidationBaseTestCase;
import org.alljoyn.validation.framework.annotation.Disabled;
import org.alljoyn.validation.framework.annotation.ValidationSuite;
import org.alljoyn.validation.framework.annotation.ValidationTest;
import org.alljoyn.validation.testing.utils.about.AboutAnnouncementDetails;
import org.alljoyn.validation.testing.utils.log.Logger;
import org.alljoyn.validation.testing.utils.log.LoggerFactory;
import org.alljoyn.validation.testing.utils.onboarding.OnboardingHelper;
import org.alljoyn.validation.testing.utils.onboarding.SoftAPValidator;
import org.alljoyn.validation.testing.utils.onboarding.WifiHelper;
import org.alljoyn.validation.testing.utils.onboarding.WifiHelperImpl;
import org.alljoyn.validation.testing.utils.onboarding.WifiNetworkConfig;
import org.alljoyn.validation.testing.utils.services.ServiceHelper;

import android.content.Context;

@ValidationSuite(name = "Onboarding-v1")
public class OnboardingTestSuite extends ValidationBaseTestCase
{
    private static final String TAG = "OnboardingTestSuite";
    private static final Logger logger = LoggerFactory.getLogger(TAG);

    protected static final String INVALID_NETWORK_NAME = "InvalidPersonalAP";
    protected static final String INVALID_NETWORK_PASSPHRASE = "InvalidNetworkPassphrase";
    protected static final short INVALID_AUTH_TYPE = 9999;

    protected static final long TIME_TO_WAIT_FOR_SOFT_AP_IN_MS_SHORT = 120000;
    protected static final long TIME_TO_WAIT_TO_CONNECT_TO_SOFT_AP_IN_MS = 60000;
    protected static final long TIME_TO_WAIT_FOR_SOFT_AP_AFTER_OFFBOARD = 15000;
    protected static final long TIME_TO_WAIT_TO_CONNECT_TO_PERSONAL_AP_IN_MS = 60000;
    protected static final long TIME_TO_WAIT_FOR_DISCONNECT_IN_MS = 30000;
    protected static final long TIME_TO_WAIT_FOR_NEXT_DEVICE_ANNOUNCEMENT_IN_MS = 180000;

    public static final short OBS_STATE_PERSONAL_AP_NOT_CONFIGURED = OnboardingService.OnboardingState.PERSONAL_AP_NOT_CONFIGURED.getStateId();
    public static final short OBS_STATE_PERSONAL_AP_NOT_VALIDATED = OnboardingService.OnboardingState.PERSONAL_AP_CONFIGURED_NOT_VALIDATED.getStateId();
    public static final short OBS_STATE_PERSONAL_AP_CONFIGURED_VALIDATED = OnboardingService.OnboardingState.PERSONAL_AP_CONFIGURED_VALIDATED.getStateId();
    public static final short OBS_STATE_PERSONAL_AP_CONFIGURED_ERROR = OnboardingService.OnboardingState.PERSONAL_AP_CONFIGURED_ERROR.getStateId();

    public static final short OBS_LASTERROR_VALIDATED = 0;
    public static final short OBS_LASTERROR_UNREACHABLE = 1;
    public static final short OBS_LASTERROR_UNAUTHORIZED = 3;

    protected static final short OBS_CONFIGURE_WIFI_RETURN_NO_CHANNEL_SWITCHING = 1;
    protected static final short OBS_CONFIGURE_WIFI_RETURN_SUPPORTS_CHANNEL_SWITCHING = 2;

    protected static final String ALLJOYN_ERROR_FEATURE_NOT_AVAILABLE = "org.alljoyn.Error.FeatureNotAvailable";

    private ConfigClient configClient;
    private AboutClient aboutClient;
    private static final char[] VALID_DEFAULT_PASSCODE = SrpAnonymousKeyListener.DEFAULT_PINCODE;
    private static final char[] TEMP_PASSCODE = "111111".toCharArray();
    private static final char[] INVALID_PASSCODE = "123456".toCharArray();

    protected static final String NEW_DEVICE_NAME = "newDeviceName";

    private OnboardingHelper onboardingHelper;
    private WifiNetworkConfig personalAPConfig;
    private WifiNetworkConfig softAPConfig;
    private static String softAPssid;

    private AppUnderTestDetails appUnderTestDetails;
    private UUID dutAppId;
    private String dutDeviceId;

    @Override
    public void setUp() throws Exception
    {
        super.setUp();

        logger.debug("test setUp started");

        try
        {
            appUnderTestDetails = getValidationTestContext().getAppUnderTestDetails();
            dutDeviceId = appUnderTestDetails.getDeviceId();
            logger.debug(String.format("Running Onboarding test case against Device ID: %s", dutDeviceId));
            dutAppId = appUnderTestDetails.getAppId();
            logger.debug(String.format("Running Onboarding test case against App ID: %s", dutAppId));
            String keyStorePath = getValidationTestContext().getKeyStorePath();
            logger.debug(String.format("Running Onboarding test case using KeyStorePath: %s", keyStorePath));

            onboardingHelper = getOnboardingHelper();
            short personalApSecurity = getPersonalApSecurity();
            logger.debug(String.format("Running Onboarding test case using PersonalApSecurity: %s", personalApSecurity));
            String personalApSsid = getPersonalApSsid();
            logger.debug(String.format("Running Onboarding test case using PersonalApSsid: %s", personalApSsid));
            String personalApPassphrase = getPersonalApPassphrase();
            logger.debug(String.format("Running Onboarding test case using PersonalApPassphrase: %s", personalApPassphrase));
            String onboardeeSoftApSsid = getOnboardeeSoftApSsid();
            logger.debug(String.format("Running Onboarding test case against OnboardeeSoftApSecurityType: %s", onboardeeSoftApSsid));
            String onboardeeSoftApPassphrase = getOnboardeeSoftApPassphrase();
            logger.debug(String.format("Running Onboarding test case against OnboardeeSoftApPassphrase: %s", onboardeeSoftApPassphrase));
            String onboardeeSoftApSecurityType = getOnboardeeSoftApSecurityType();
            logger.debug(String.format("Running Onboarding test case against OnboardeeSoftApSecurityType: %s", onboardeeSoftApSecurityType));

            String authTypeString = onboardingHelper.mapAuthTypeToAuthTypeString(personalApSecurity);
            personalAPConfig = new WifiNetworkConfig(personalApSsid, personalApPassphrase, authTypeString);
            onboardingHelper.setPersonalAPConfig(personalAPConfig);

            softAPConfig = new WifiNetworkConfig(onboardeeSoftApSsid, onboardeeSoftApPassphrase, onboardeeSoftApSecurityType);
            onboardingHelper.setSoftAPConfig(softAPConfig);
            onboardingHelper.initialize(keyStorePath, dutDeviceId, dutAppId);
        }
        catch (Exception e)
        {
            releaseResources();
            throw new Exception(e);
        }
    }

    @Override
    public void tearDown() throws Exception
    {
        super.tearDown();

        logger.debug("test tearDown started");
        releaseResources();
        logger.debug("test tearDown done");
    }

    protected OnboardingHelper getOnboardingHelper()
    {
        return new OnboardingHelper((Context) getValidationTestContext().getContext());
    }

    protected ServiceHelper getServiceHelper()
    {
        return new ServiceHelper(new AndroidLogger());
    }

    protected WifiHelper getWifiHelper()
    {
        return new WifiHelperImpl((Context) getValidationTestContext().getContext());
    }

    @ValidationTest(name = "Onboarding-v1-01", order = -1)
    public void testOnboarding_v1_01_OffboardDevice() throws Exception
    {
        softAPssid = null;

        onboardingHelper.connectToPersonalAPIfNeeded();

        checkDeviceIsInOnboardedStateAndWaitForAnnouncement();

        short version = onboardingHelper.retrieveVersionProperty();
        assertEquals("Onboarding interface version mismatch", 1, version);

        placeDUTInOffboardState();

        verifyOnboardingState(OBS_STATE_PERSONAL_AP_NOT_CONFIGURED);

        SoftAPValidator.validateSoftAP(softAPssid, dutDeviceId);
    }

    @ValidationTest(name = "Onboarding-v1-02", order = 99)
    public void testOnboarding_v1_02_OnboardDevice() throws Exception
    {
        makeSureDeviceIsInOffboardedState();

        placeDUTInOnboardState();
    }

    @ValidationTest(name = "Onboarding-v1-03")
    public void testOnboarding_v1_03_ConnectivityOverSoftAP() throws Exception
    {
        connectToDUTInOffboardedState();

        verifyOnboardingState(OBS_STATE_PERSONAL_AP_NOT_CONFIGURED);
    }

    @ValidationTest(name = "Onboarding-v1-04")
    @Disabled
    public void testOnboarding_v1_04_ConfigureWiFiWithOutOfRangeValue() throws Exception
    {
        connectToDUTInOffboardedState();
        try
        {
            onboardingHelper.callConfigureWiFi(new WifiNetworkConfig(personalAPConfig.getSsid(), personalAPConfig.getPassphrase(), Short.toString(INVALID_AUTH_TYPE)));

            fail("Calling configureWifi on the onboarding client interface with an invalid auth type must throw an 'org.alljoyn.Error.OutOfRange' ErrorReplyBusException");
        }
        catch (ErrorReplyBusException e)
        {
            logBusExceptionInfo(e);
            assertEquals("Wrong error received from configureWifi() method", "org.alljoyn.Error.OutOfRange", e.getErrorName());
        }

        verifyOnboardingState(OBS_STATE_PERSONAL_AP_NOT_CONFIGURED);
    }

    @ValidationTest(name = "Onboarding-v1-05")
    public void testOnboarding_v1_05_ConfigureWiFiWithWrongSSID() throws Exception
    {
        connectToDUTInOffboardedState();

        short configureWiFiRetval = onboardingHelper.callConfigureWiFi(new WifiNetworkConfig(INVALID_NETWORK_NAME, personalAPConfig.getPassphrase(), personalAPConfig
                .getSecurityType()));

        verifyConfigureWifiReturnValue(configureWiFiRetval);

        verifyOnboardingState(OBS_STATE_PERSONAL_AP_NOT_VALIDATED);

        onboardingHelper.callConnectWiFiAndWaitForSoftAPDisconnect();

        onboardingHelper.connectToDUTOnSoftAP();

        onboardingHelper.waitForAboutAnnouncementAndThenConnect();

        verifyOnboardingState(OBS_STATE_PERSONAL_AP_CONFIGURED_ERROR);
        verifyOnboardingErrorCode(OBS_LASTERROR_UNREACHABLE);
    }

    @ValidationTest(name = "Onboarding-v1-06")
    public void testOnboarding_v1_06_ConfigureWiFiWithWrongPassword() throws Exception
    {
        connectToDUTInOffboardedState();

        short configureWiFiRetval = onboardingHelper.callConfigureWiFi(new WifiNetworkConfig(personalAPConfig.getSsid(), "0" + personalAPConfig.getPassphrase(), personalAPConfig
                .getSecurityType()));

        verifyConfigureWifiReturnValue(configureWiFiRetval);

        verifyOnboardingState(OBS_STATE_PERSONAL_AP_NOT_VALIDATED);

        onboardingHelper.callConnectWiFiAndWaitForSoftAPDisconnect();

        onboardingHelper.connectToDUTOnSoftAP();

        onboardingHelper.waitForAboutAnnouncementAndThenConnect();

        verifyOnboardingState(OBS_STATE_PERSONAL_AP_CONFIGURED_ERROR);
        verifyOnboardingErrorCode(OBS_LASTERROR_UNAUTHORIZED);
    }

    @ValidationTest(name = "Onboarding-v1-07")
    public void testOnboarding_v1_07_ConfigureWiFiAuthTypeOfAny() throws Exception
    {
        connectToDUTInOffboardedState();

        short configureWiFiRetval = onboardingHelper.callConfigureWiFi(new WifiNetworkConfig(personalAPConfig.getSsid(), personalAPConfig.getPassphrase(), "ANY"));

        verifyConfigureWifiReturnValue(configureWiFiRetval);

        verifyOnboardingState(OBS_STATE_PERSONAL_AP_NOT_VALIDATED);

        onboardingHelper.callConnectWiFiAndWaitForSoftAPDisconnect();

        onboardingHelper.connectToPersonalAP();

        onboardingHelper.waitForAboutAnnouncementAndThenConnect();

        verifyOnboardingState(OBS_STATE_PERSONAL_AP_CONFIGURED_VALIDATED);
        verifyOnboardingErrorCode(OBS_LASTERROR_VALIDATED);

        placeDUTInOffboardState();

        onboardingHelper.connectToDUTOnSoftAP();

        onboardingHelper.waitForAboutAnnouncementAndThenConnect();
    }

    @ValidationTest(name = "Onboarding-v1-08")
    public void testOnboarding_v1_08_GetScanInfo() throws Exception
    {
        connectToDUTInOffboardedState();

        try
        {
            boolean foundPersonalAP = false;

            String personalAPNetworkName = personalAPConfig.getSsid();
            ScanInfo scanInfo = onboardingHelper.callScanInfo();
            MyScanResult[] scanResults = scanInfo.getScanResults();
            for (MyScanResult myScanResult : scanResults)
            {
                validateScanResult(myScanResult);

                if (personalAPNetworkName.equals(myScanResult.m_ssid))
                {
                    foundPersonalAP = true;
                }
            }
            assertTrue(String.format("Onboarding.getScanInfo() did not include the personal AP network: %s", personalAPNetworkName), foundPersonalAP);
        }
        catch (ErrorReplyBusException e)
        {
            logBusExceptionInfo(e);

            if (ALLJOYN_ERROR_FEATURE_NOT_AVAILABLE.equals(e.getErrorName()))
            {
                getValidationTestContext().addNote("DUT does not support getScanInfo() method");
            }
            else
            {

                throw e;
            }
        }
    }

    @ValidationTest(name = "Onboarding-v1-09")
    public void testOnboarding_v1_09_WrongPasscode() throws Exception
    {
        AboutAnnouncementDetails deviceAboutAnnouncement = null;
        makeSureDeviceIsInOffboardedState();

        onboardingHelper.connectToDUTOnSoftAP();

        deviceAboutAnnouncement = onboardingHelper.waitForAboutAnnouncementAndThenConnect();

        onboardingHelper.setPasscode(deviceAboutAnnouncement, INVALID_PASSCODE);

        try
        {
            logger.info("Calling Onboarding.getVersion() with invalid passcode");
            onboardingHelper.retrieveVersionProperty();

            fail("An error must be received when attempting to call a method on the secured Onboarding interface with an invalid password");
        }
        catch (BusException be)
        {
            assertTrue("Did not receive callback from AuthListener indicating authentication failure", onboardingHelper.isAuthenticationFailed(deviceAboutAnnouncement));
            logger.debug("Received BusException when providing an incorrect password", be);
            logBusExceptionInfo(be);
        }

        // onboardingHelper.setPasscode(VALID_DEFAULT_PASSCODE);
    }

    @ValidationTest(name = "Onboarding-v1-10")
    public void testOnboarding_v1_10_AuthenticateAfterChangingPasscode() throws Exception
    {
        String daemonName = "";
        AboutAnnouncementDetails deviceAboutAnnouncement = connectToDUTInOffboardedState();

        if (!deviceAboutAnnouncement.supportsInterface(ConfigTransport.INTERFACE_NAME))
        {
            getValidationTestContext().addNote("DUT does not support Config interface--exiting test case.");
            return;
        }

        configClient = onboardingHelper.connectConfigClient(deviceAboutAnnouncement);

        logger.info(String.format("Calling Config.setPasscode() to change passcode to: %s", TEMP_PASSCODE));
        configClient.setPasscode(daemonName, TEMP_PASSCODE);

        disconnectConfigClient();

        onboardingHelper.connectToDUTOnSoftAP();

        deviceAboutAnnouncement = onboardingHelper.waitForAboutAnnouncementAndThenConnect();

        onboardingHelper.setPasscode(deviceAboutAnnouncement, TEMP_PASSCODE);

        logger.info("Calling Onboarding.getVerion() after changing passcode");
        onboardingHelper.retrieveVersionProperty();

        configClient = onboardingHelper.connectConfigClient(deviceAboutAnnouncement);

        logger.info("Calling Config.setPasscode() to change passcode back to default passcode");
        configClient.setPasscode(daemonName, VALID_DEFAULT_PASSCODE);

        // onboardingHelper.setPasscode(VALID_DEFAULT_PASSCODE);
    }

    @ValidationTest(name = "Onboarding-v1-11")
    public void testOnboarding_v1_11_FactoryResetClearsConfiguration() throws Exception
    {
        makeSureDeviceIsInOffboardedState();

        AboutAnnouncementDetails aboutAnnouncementDetails = placeDUTInOnboardState();

        if (!aboutAnnouncementDetails.supportsInterface(ConfigTransport.INTERFACE_NAME))
        {
            getValidationTestContext().addNote("DUT does not support Config interface--exiting test case.");
            return;
        }

        String defaultLanguage = aboutAnnouncementDetails.getDefaultLanguage();
        logger.debug(String.format("Default language is: %s", defaultLanguage));

        aboutClient = onboardingHelper.connectAboutClient(aboutAnnouncementDetails);
        configClient = onboardingHelper.connectConfigClient(aboutAnnouncementDetails);

        configClient.ResetConfigurations(defaultLanguage, new String[]
        { AboutKeys.ABOUT_DEVICE_NAME });

        Map<String, Object> configMap = configClient.getConfig("");
        Map<String, Object> aboutMap = aboutClient.getAbout("");
        String defaultDeviceName = (String) aboutMap.get(AboutKeys.ABOUT_DEVICE_NAME);

        assertEquals("GetConfigurations() returns the same DeviceName as GetAboutData()", defaultDeviceName, configMap.get(AboutKeys.ABOUT_DEVICE_NAME));

        String modifiedDeviceName = NEW_DEVICE_NAME;

        onboardingHelper.clearQueuedDeviceAnnouncements();

        configMap = new HashMap<String, Object>();
        configMap.put(AboutKeys.ABOUT_DEVICE_NAME, modifiedDeviceName);
        configClient.setConfig(configMap, defaultLanguage);

        disconnectAboutClient();
        disconnectConfigClient();

        aboutAnnouncementDetails = onboardingHelper.waitForAboutAnnouncementAndThenConnect();

        assertEquals("Device name in announcement was not modified as expected", modifiedDeviceName, aboutAnnouncementDetails.getDeviceName());

        aboutClient = onboardingHelper.connectAboutClient(aboutAnnouncementDetails);
        configClient = onboardingHelper.connectConfigClient(aboutAnnouncementDetails);

        aboutMap = aboutClient.getAbout("");
        assertEquals("Device name in aboutMap was not modified as expected", modifiedDeviceName, (String) aboutMap.get(AboutKeys.ABOUT_DEVICE_NAME));

        configMap = configClient.getConfig("");
        assertEquals("GetConfigurations() returns the same DeviceName as GetAboutData()", modifiedDeviceName, configMap.get(AboutKeys.ABOUT_DEVICE_NAME));

        try
        {
            logger.info("calling factory reset");
            configClient.factoryReset();
        }
        catch (ErrorReplyBusException e)
        {
            logBusExceptionInfo(e);
            assertEquals("Unexpected error received from factoryReset call", ALLJOYN_ERROR_FEATURE_NOT_AVAILABLE, e.getErrorName());

            getValidationTestContext().addNote("Factory reset method is not supported by device");

            configClient.ResetConfigurations(defaultLanguage, new String[]
            { AboutKeys.ABOUT_DEVICE_NAME });

            return;
        }

        disconnectConfigClient();
        disconnectAboutClient();

        onboardingHelper.connectToDUTOnSoftAP();

        aboutAnnouncementDetails = onboardingHelper.waitForAboutAnnouncementAndThenConnect();

        assertEquals("Device name in announcement was not the default as expected", defaultDeviceName, aboutAnnouncementDetails.getDeviceName());

        defaultLanguage = aboutAnnouncementDetails.getDefaultLanguage();
        logger.debug(String.format("Default language is: %s", defaultLanguage));

        aboutClient = onboardingHelper.connectAboutClient(aboutAnnouncementDetails);
        configClient = onboardingHelper.connectConfigClient(aboutAnnouncementDetails);

        aboutMap = aboutClient.getAbout("");
        logger.debug(String.format("After factoryReset, the deviceName is: %s", aboutMap.get(AboutKeys.ABOUT_DEVICE_NAME).toString()));

        assertEquals("DeviceName not reset to default value", defaultDeviceName, aboutMap.get(AboutKeys.ABOUT_DEVICE_NAME).toString());

        configMap = configClient.getConfig("");
        assertEquals("GetConfigurations() returns the same DeviceName as GetAboutData()", defaultDeviceName, configMap.get(AboutKeys.ABOUT_DEVICE_NAME));
    }

    @ValidationTest(name = "Onboarding-v1-12")
    public void testOnboarding_v1_12_FactoryResetResetsPasscode() throws Exception
    {
        String daemonName = "";
        AboutAnnouncementDetails deviceAboutAnnouncement = connectToDUTInOffboardedState();

        if (!deviceAboutAnnouncement.supportsInterface(ConfigTransport.INTERFACE_NAME))
        {
            getValidationTestContext().addNote("DUT does not support Config interface--exiting test case.");
            return;
        }

        configClient = onboardingHelper.connectConfigClient(deviceAboutAnnouncement);

        configClient.setPasscode(daemonName, TEMP_PASSCODE);

        disconnectConfigClient();

        onboardingHelper.connectToDUTOnSoftAP();

        deviceAboutAnnouncement = onboardingHelper.waitForAboutAnnouncementAndThenConnect();

        onboardingHelper.setPasscode(deviceAboutAnnouncement, TEMP_PASSCODE);

        onboardingHelper.retrieveVersionProperty();

        configClient = onboardingHelper.connectConfigClient(deviceAboutAnnouncement);
        try
        {
            configClient.factoryReset();
        }
        catch (ErrorReplyBusException e)
        {
            logBusExceptionInfo(e);

            assertEquals("Unexpected error received from factoryReset call", ALLJOYN_ERROR_FEATURE_NOT_AVAILABLE, e.getErrorName());

            getValidationTestContext().addNote("Factory reset method is not supported by device");

            configClient.setPasscode(daemonName, VALID_DEFAULT_PASSCODE);

            // onboardingHelper.setPasscode(VALID_DEFAULT_PASSCODE);

            return;
        }
        onboardingHelper.waitForSoftAPDisconnect();

        disconnectConfigClient();

        onboardingHelper.connectToDUTOnSoftAP();

        deviceAboutAnnouncement = onboardingHelper.waitForAboutAnnouncementAndThenConnect();

        onboardingHelper.setPasscode(deviceAboutAnnouncement, VALID_DEFAULT_PASSCODE);

        logger.info("Calling Onboarding.getVersion() method");
        onboardingHelper.retrieveVersionProperty();
    }

    private void placeDUTInOffboardState() throws Exception
    {
        onboardingHelper.callOffboard();

        String ssid = onboardingHelper.connectToDUTOnSoftAP();
        if (softAPssid == null)
        {
            softAPssid = ssid;
        }

        onboardingHelper.waitForAboutAnnouncementAndThenConnect();
    }

    protected String getOnboardeeSoftApSsid()
    {

        String softApSsid = (String) getValidationTestContext().getTestParameter(OnboardingTestParameters.SOFT_AP_SSID);
        assertNotNull("Soft AP network name is required", softApSsid);
        return softApSsid;
    }

    protected String getOnboardeeSoftApSecurityType()
    {
        return (String) getValidationTestContext().getTestParameter(OnboardingTestParameters.SOFT_AP_SECURITY);
    }

    protected String getOnboardeeSoftApPassphrase()
    {
        return (String) getValidationTestContext().getTestParameter(OnboardingTestParameters.SOFT_AP_PASSPHRASE);
    }

    protected String getPersonalApSsid()
    {
        String personalApSsid = (String) getValidationTestContext().getTestParameter(OnboardingTestParameters.PERSONAL_AP_SSID);
        assertNotNull("Personal AP network name is required", personalApSsid);
        return personalApSsid;
    }

    protected String getPersonalApPassphrase()
    {
        String personalApPassphrase = (String) getValidationTestContext().getTestParameter(OnboardingTestParameters.PERSONAL_AP_PASSPHRASE);
        assertNotNull("Personal AP password is required", personalApPassphrase);
        return personalApPassphrase;
    }

    protected short getPersonalApSecurity()
    {
        String personalApSecurity = (String) getValidationTestContext().getTestParameter(OnboardingTestParameters.PERSONAL_AP_SECURITY);
        logger.debug(String.format("Running Onboarding test case using PersonalApSecurityString: %s", personalApSecurity));
        short networkAuthType = onboardingHelper.getAuthType(personalApSecurity);
        validateAuthType(networkAuthType);
        return networkAuthType;
    }

    private void verifyOnboardingState(short state) throws BusException
    {
        logger.info("Retrieving the State property from the Onboarding interface");
        short actualState = onboardingHelper.retrieveStateProperty();
        assertEquals("State property does not match expected value", state, actualState);
    }

    private void verifyOnboardingErrorCode(short errorCode) throws BusException
    {
        logger.info("Retrieving the ErrorCode property from the Onboarding interface");
        OBLastError lastError = onboardingHelper.retrieveLastErrorProperty();
        assertEquals("ErrorCode property does not match expected value", errorCode, lastError.getErrorCode());
    }

    private void checkDeviceIsInOnboardedStateAndWaitForAnnouncement() throws Exception
    {
        assertTrue("DUT does not appear to be Onboarded!", onboardingHelper.isDeviceInOnboardedState());
    }

    private void makeSureDeviceIsInOffboardedState() throws Exception
    {
        assertNotNull("Soft AP SSID can not be null. You have to provide the soft AP SSID to run the Onboarding tests", softAPConfig.getSsid());
        assertTrue("Soft AP SSID can not be empty. You have to provide the soft AP SSID to run the Onboarding tests", !softAPConfig.getSsid().isEmpty());

        if (onboardingHelper.isDeviceInOnboardedState())
        {
            logger.debug("Device currently in Onboarded state, so offboarding it");

            placeDUTInOffboardState();
        }
    }

    public AboutAnnouncementDetails placeDUTInOnboardState() throws Exception, BusException
    {
        onboardingHelper.connectToDUTOnSoftAP();

        onboardingHelper.waitForAboutAnnouncementAndThenConnect();

        short configureWiFiRetval = onboardingHelper.callConfigureWiFi(personalAPConfig);

        verifyConfigureWifiReturnValue(configureWiFiRetval);

        verifyOnboardingState(OBS_STATE_PERSONAL_AP_NOT_VALIDATED);

        onboardingHelper.callConnectWiFiAndWaitForSoftAPDisconnect();

        onboardingHelper.connectToPersonalAP();

        AboutAnnouncementDetails aboutAnnouncementDetails = onboardingHelper.waitForAboutAnnouncementAndThenConnect();

        verifyOnboardingState(OBS_STATE_PERSONAL_AP_CONFIGURED_VALIDATED);
        verifyOnboardingErrorCode(OBS_LASTERROR_VALIDATED);

        return aboutAnnouncementDetails;
    }

    private AboutAnnouncementDetails connectToDUTInOffboardedState() throws Exception
    {
        makeSureDeviceIsInOffboardedState();

        onboardingHelper.connectToDUTOnSoftAP();

        return onboardingHelper.waitForAboutAnnouncementAndThenConnect();
    }

    private void verifyConfigureWifiReturnValue(short configureWiFiRetval)
    {
        if (OBS_CONFIGURE_WIFI_RETURN_SUPPORTS_CHANNEL_SWITCHING == configureWiFiRetval)
        {
            fail("fast-channel switching is presently not supported on any platform, so no ConnectionResult signal is being sent");
        }
        else if (OBS_CONFIGURE_WIFI_RETURN_NO_CHANNEL_SWITCHING != configureWiFiRetval)
        {
            fail("Got unexpected retval from configureWiFi(): " + configureWiFiRetval);
        }

    }

    private void validateScanResult(MyScanResult myScanResult)
    {
        validateAuthType(myScanResult.m_authType);
    }

    private void validateAuthType(short authType)
    {
        assertTrue(String.format("AuthType must not be less than %d", getMinAuthTypeId()), authType >= getMinAuthTypeId());
        assertTrue(String.format("AuthType must not be greater than %d", getMaxAuthTypeId()), authType <= getMaxAuthTypeId());
    }

    private void logBusExceptionInfo(BusException be)
    {
        if (be instanceof ErrorReplyBusException)
        {
            ErrorReplyBusException ex = (ErrorReplyBusException) be;
            logger.debug("ErrorStatus: " + ex.getErrorStatus());
            logger.debug("ErrorName: " + ex.getErrorName());
            logger.debug("ErrorMessage: " + ex.getErrorMessage());
        }
    }

    private short getMinAuthTypeId()
    {
        short retval = Short.MAX_VALUE;
        for (AuthType authType : AuthType.values())
        {
            if (authType.getTypeId() < retval)
            {
                retval = authType.getTypeId();
            }
        }
        return retval;
    }

    private int getMaxAuthTypeId()
    {
        short retval = Short.MIN_VALUE;
        for (AuthType authType : AuthType.values())
        {
            if (authType.getTypeId() > retval)
            {
                retval = authType.getTypeId();
            }
        }
        return retval;
    }

    private void releaseResources()
    {
        try
        {
            disconnectAboutClient();
            disconnectConfigClient();
            if (onboardingHelper != null)
            {
                onboardingHelper.release();
                onboardingHelper = null;
            }
        }
        catch (Exception ex)
        {
            logger.debug("Exception releasing resources", ex);
        }
    }

    private void disconnectConfigClient()
    {
        if (configClient != null)
        {
            logger.debug("Disconnecting config client");
            configClient.disconnect();
            configClient = null;
        }
    }

    private void disconnectAboutClient()
    {
        if (aboutClient != null)
        {
            logger.debug("Disconnecting about client");
            aboutClient.disconnect();
            aboutClient = null;
        }
    }
}