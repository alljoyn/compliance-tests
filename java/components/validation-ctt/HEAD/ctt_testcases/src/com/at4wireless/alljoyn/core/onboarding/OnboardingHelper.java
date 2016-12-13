/*
 * Copyright (c) 2016 Open Connectivity Foundation (OCF) and AllJoyn Open
 *    Source Project (AJOSP) Contributors and others.
 *
 *    SPDX-License-Identifier: Apache-2.0
 *
 *    All rights reserved. This program and the accompanying materials are
 *    made available under the terms of the Apache License, Version 2.0
 *    which accompanies this distribution, and is available at
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Copyright 2016 Open Connectivity Foundation and Contributors to
 *    AllSeen Alliance. All rights reserved.
 *
 *    Permission to use, copy, modify, and/or distribute this software for
 *    any purpose with or without fee is hereby granted, provided that the
 *    above copyright notice and this permission notice appear in all
 *    copies.
 *
 *     THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL
 *     WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED
 *     WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE
 *     AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL
 *     DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR
 *     PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER
 *     TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 *     PERFORMANCE OF THIS SOFTWARE.
 */
package  com.at4wireless.alljoyn.core.onboarding;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.alljoyn.about.client.AboutClient;
import org.alljoyn.bus.BusException;
import org.alljoyn.config.client.ConfigClient;
import org.alljoyn.onboarding.OnboardingService;
import org.alljoyn.onboarding.OnboardingService.AuthType;
import org.alljoyn.onboarding.client.OnboardingClient;
import org.alljoyn.onboarding.transport.OBLastError;
import org.alljoyn.onboarding.transport.OnboardingTransport;
import org.alljoyn.onboarding.transport.OnboardingTransport.ConfigureWifiMode;
import org.alljoyn.onboarding.transport.ScanInfo;

import com.at4wireless.alljoyn.core.about.AboutAnnouncementDetails;
import com.at4wireless.alljoyn.core.commons.ServiceHelper;
import com.at4wireless.alljoyn.core.commons.log.WindowsLoggerImpl;
import com.at4wireless.alljoyn.core.onboarding.ScanResult;
import com.at4wireless.alljoyn.core.onboarding.WifiHelper;
import com.at4wireless.alljoyn.core.onboarding.WifiNetworkConfig;




// TODO: Auto-generated Javadoc
/**
 * The Class OnboardingHelper.
 */
public class OnboardingHelper
{
    
    /** The Constant TIME_TO_WAIT_FOR_SCAN_RESULTS_IN_SECONDS. */
    private static final int TIME_TO_WAIT_FOR_SCAN_RESULTS_IN_SECONDS = 2;
    
    /** The Constant TAG. */
    private static final String TAG = "OnboardingHelper";
	
	/** The Constant logger. */
	private static final WindowsLoggerImpl logger =  new WindowsLoggerImpl(TAG);
    
    /** The onboarding client. */
    private OnboardingClient onboardingClient;
    
    /** The wifi helper. */
    private WifiHelper wifiHelper;
    
    /** The service helper. */
    private ServiceHelper serviceHelper;
    
    /** The Constant BUS_APPLICATION_NAME. */
    private static final String BUS_APPLICATION_NAME = "OnboardingTestSuite";
    
    /** The Constant NUMBER_OF_DEVICE_ID_DIGITS_IN_SOFT_AP. */
    private static final int NUMBER_OF_DEVICE_ID_DIGITS_IN_SOFT_AP = 7;
    
    /** The app id. */
    private UUID appId;
    
    /** The device id. */
    private String deviceId;

    /** The personal ap config. */
    private WifiNetworkConfig personalAPConfig;
    
    /** The soft ap config. */
    private WifiNetworkConfig softAPConfig;
    
    /** The device id suffix. */
    private String deviceIdSuffix;

    /** The key store path. */
    private String keyStorePath;
   
    /** The Constant TIME_TO_WAIT_FOR_SOFT_AP_IN_MS_SHORT. */
    protected static final long TIME_TO_WAIT_FOR_SOFT_AP_IN_MS_SHORT = 120000;
    
    /** The Constant TIME_TO_WAIT_TO_CONNECT_TO_PERSONAL_AP_IN_MS. */
    protected static final long TIME_TO_WAIT_TO_CONNECT_TO_PERSONAL_AP_IN_MS = 60000;
    
    /** The Constant TIME_TO_WAIT_TO_CONNECT_TO_SOFT_AP_IN_MS. */
    protected static final long TIME_TO_WAIT_TO_CONNECT_TO_SOFT_AP_IN_MS = 60000;
    
    /** The Constant TIME_TO_WAIT_FOR_NEXT_DEVICE_ANNOUNCEMENT_IN_MS. */
    protected static final long TIME_TO_WAIT_FOR_NEXT_DEVICE_ANNOUNCEMENT_IN_MS = 180000;
    
    /** The Constant TIME_TO_WAIT_FOR_DISCONNECT_IN_MS. */
    protected static final long TIME_TO_WAIT_FOR_DISCONNECT_IN_MS = 30000;

    /** The Constant OBS_CONFIGURE_WIFI_RETURN_NO_CHANNEL_SWITCHING. */
    protected static final short OBS_CONFIGURE_WIFI_RETURN_NO_CHANNEL_SWITCHING = 1;
    
    /** The Constant OBS_CONFIGURE_WIFI_RETURN_SUPPORTS_CHANNEL_SWITCHING. */
    protected static final short OBS_CONFIGURE_WIFI_RETURN_SUPPORTS_CHANNEL_SWITCHING = 2;

    /**
     * Instantiates a new onboarding helper.
     */
    public OnboardingHelper()
    {
        
    }

    /**
     * Initialize.
     *
     * @param keyStorePath the key store path
     * @param deviceId the device id
     * @param appId the app id
     * @throws Exception the exception
     */
    public void initialize(String keyStorePath, String deviceId, UUID appId) throws Exception
    {
        this.deviceId = deviceId;
        this.appId = appId;
        this.keyStorePath = keyStorePath;

        try
        {
            initServiceHelper();

            wifiHelper = getWifiHelper();
            wifiHelper.initialize();
        }
        catch (Exception e)
        {
            releaseResources();
            throw e;
        }
    }

    /**
     * Release.
     */
    public void release()
    {
        releaseResources();
    }

    /**
     * Sets the passcode.
     *
     * @param aboutAnnouncement the about announcement
     * @param passcode the passcode
     */
    public void setPasscode(AboutAnnouncementDetails aboutAnnouncement, char[] passcode)
    {
        serviceHelper.setAuthPassword(aboutAnnouncement, passcode);
    }

    /**
     * Connect to dut on soft ap.
     *
     * @return the string
     * @throws Exception the exception
     */
    public String connectToDUTOnSoftAP() throws Exception
    {

    	releaseServiceHelper();
    	if(wifiHelper.getCurrentSSID()!=null){
    		wifiHelper.waitForDisconnect(5, TimeUnit.SECONDS);
    	}
    	boolean isSoftAPAvailable = false;
    	String ssid = getSoftAPSsid();

    	if (ssid == null)
    	{
            logger.info("Waiting to determine the soft AP name");
            // TODO remove this code later once onboarding is done by the
            // application and the soft AP will already be known
            long timeToWaitInMs = TIME_TO_WAIT_FOR_SOFT_AP_IN_MS_SHORT;
            long startTime = System.currentTimeMillis();

            while ((ssid == null) && (System.currentTimeMillis() < startTime + timeToWaitInMs))
            {
                long timeRemaining = startTime + timeToWaitInMs - System.currentTimeMillis();
                List<ScanResult> availableAccessPoints = wifiHelper.waitForScanResults(timeRemaining, TimeUnit.MILLISECONDS);
                if (availableAccessPoints != null)
                {
                    for (ScanResult scanResult : availableAccessPoints)
                    {
                        if (scanResult.SSID.startsWith("AJ_") && softAPMatchesDeviceId(scanResult.SSID, deviceId))
                        {
                            ssid = scanResult.SSID;
                            logger.info(String.format("Found matching ssid: %s for deviceId: %s", ssid, deviceId));
                            break;
                        }

                    }
                }

            }
            if (ssid != null)
            {
                softAPConfig.setSsid(ssid);
            }
        }

        if (ssid != null)
        {
            isSoftAPAvailable = wifiHelper.waitForNetworkAvailable(ssid, TIME_TO_WAIT_FOR_SOFT_AP_IN_MS_SHORT, TimeUnit.MILLISECONDS);
        }

        if (!isSoftAPAvailable)
        {
            throw new Exception(String.format("Timed out waiting for SoftAP to be available: %s", ssid));
        }

        boolean connectedToSoftAP = connectToSoftAPNetwork();
        if (!connectedToSoftAP)
        {
            // TODO authentication failure is only reason for failure, right?
            throw new Exception(String.format("Could not connect to SoftAP: %s", ssid));
        }

        initServiceHelper();

        return ssid;
    }

    /**
     * Gets the soft ap ssid.
     *
     * @return the soft ap ssid
     */
    private String getSoftAPSsid()
    {
        String softAPssid = null;
        if (getSoftAPConfig() != null)
        {
            softAPssid = getSoftAPConfig().getSsid();
        }
        return softAPssid;
    } 

    /**
     * Soft ap matches device id.
     *
     * @param ssid the ssid
     * @param deviceId the device id
     * @return true, if successful
     */
    private boolean softAPMatchesDeviceId(String ssid, String deviceId)
    {
        String deviceIdSuffix = ssid.substring(ssid.length() - NUMBER_OF_DEVICE_ID_DIGITS_IN_SOFT_AP);
        return deviceId.endsWith(deviceIdSuffix);
    }

    /**
     * Call configure wi fi.
     *
     * @param networkConfig the network config
     * @return the short
     * @throws Exception the exception
     */
    public short callConfigureWiFi(WifiNetworkConfig networkConfig) throws Exception
    {
        String ssid = networkConfig.getSsid();
        String passphrase = networkConfig.getPassphrase();
        AuthType authType = getOnboardingServiceAuthType(networkConfig.getSecurityType());

        String convertedPassphrase = convertToHex(passphrase);

        logger.info(String.format("Calling Onboarding.configureWiFi() method on DUT with ssid: %s and authType: %d", ssid, authType.getTypeId()));
        ConfigureWifiMode configureWifiMode = onboardingClient.configureWiFi(ssid, convertedPassphrase, authType);

        verifyOnboardingState(OnboardingService.OnboardingState.PERSONAL_AP_CONFIGURED_NOT_VALIDATED.getStateId());

        return configureWifiMode.getValue();
    }

    /**
     * Verify onboarding state.
     *
     * @param expectedState the expected state
     * @throws Exception the exception
     */
    private void verifyOnboardingState(short expectedState) throws Exception
    {
        logger.info("Retrieving the State property from the Onboarding interface");
        short clientState = onboardingClient.getState();
        if (expectedState != clientState)
        {
            throw new Exception(String.format("State property of Onboarding interface does not match expected value; was: %d expected: %d", clientState, expectedState));
        }
    }

    /**
     * Call connect wi fi and wait for soft ap disconnect.
     *
     * @throws Exception the exception
     */
    public void callConnectWiFiAndWaitForSoftAPDisconnect() throws Exception
    {
        logger.info("Calling Onboarding.connectWiFi() method");
        onboardingClient.connectWiFi();

        waitForSoftAPDisconnect();
    }

    /**
     * Connect to personal ap if needed.
     *
     * @throws Exception the exception
     */
    public void connectToPersonalAPIfNeeded() throws Exception
    {
        String personalAPNetworkName = personalAPConfig.getSsid();
        String currentSsid = wifiHelper.getCurrentSSID();
        if ((currentSsid == null) || (!currentSsid.equals(personalAPNetworkName)))
        {
            connectToPersonalAP();
        }
    }

    /**
     * Connect to personal ap.
     *
     * @throws Exception the exception
     */
    public void connectToPersonalAP() throws Exception
    {
    	
        releaseServiceHelper();
        if(wifiHelper.getCurrentSSID()!=null){
        wifiHelper.waitForDisconnect(5, TimeUnit.SECONDS);
        }
        String personalAPNetworkName = personalAPConfig.getSsid();

        String connectedSsid = null;
        int numTries = 0;
        while (((connectedSsid == null) || (!personalAPNetworkName.equals(connectedSsid))) && (numTries < 2))
        {
            logger.info("Attempting to connect to Personal AP network");
            connectedSsid = wifiHelper.connectToNetwork(personalAPConfig, false, TIME_TO_WAIT_TO_CONNECT_TO_PERSONAL_AP_IN_MS, TimeUnit.MILLISECONDS);
            numTries++;
                  }

        if (connectedSsid == null)
        {
            throw new Exception(String.format("Timed out attempting to connect to network %s", personalAPNetworkName));
        }
        if (!connectedSsid.equals(personalAPNetworkName))
        {
            throw new Exception(String.format("Connected network ssid is not personal AP; was: %s; expected: %s", connectedSsid, personalAPNetworkName));
        }
        initServiceHelper();
    }

    /**
     * Wait for about announcement and then connect.
     *
     * @return the about announcement details
     * @throws Exception the exception
     */
    public AboutAnnouncementDetails waitForAboutAnnouncementAndThenConnect() throws Exception
    {
        boolean foundMatch = false;
        AboutAnnouncementDetails deviceAboutAnnouncement = null;
        long timeToWaitInMs = TIME_TO_WAIT_FOR_NEXT_DEVICE_ANNOUNCEMENT_IN_MS;
        long startTime = System.currentTimeMillis();

        String currentSsid = wifiHelper.getCurrentSSID();
        String personalAPNetworkName = personalAPConfig.getSsid();
        boolean connectedToPersonalAP = personalAPNetworkName.equals(currentSsid);

        String softAPSsid = getSoftApSsid();
       

        while ((!foundMatch) && (System.currentTimeMillis() < startTime + timeToWaitInMs))
        {
            deviceAboutAnnouncement = waitForNextAboutAnnouncementFromDevice(1000);
            if (deviceAboutAnnouncement != null)
            {
                foundMatch = true;
            }
            else if ((connectedToPersonalAP) && (softAPSsid != null))
            {
                boolean isSoftAPAvailable = wifiHelper.waitForNetworkAvailable(softAPSsid, TIME_TO_WAIT_FOR_SCAN_RESULTS_IN_SECONDS, TimeUnit.SECONDS);
                if (isSoftAPAvailable)
                {
                    String message;
                    if ((deviceId != null) && (appId != null))
                    {
                        message = String.format("Soft AP became available while waiting to receive About announcement from app with deviceId: %s; appId: %s", deviceId, appId);
                    }
                    else
                    {
                        message = String.format(
                                "Soft AP became available while waiting to receive About announcement from app  supporting Onboarding and having a deviceId ending in: %s",
                                deviceIdSuffix);
                    }
                    throw new Exception(message);
                }
            }
            else if ((!connectedToPersonalAP) && (softAPSsid != null))
            {
                if (!softAPSsid.equals(wifiHelper.getCurrentSSID()))
                {
                    if (false == connectToSoftAPNetwork())
                    {
                        String message = String.format("Soft AP became unavailable while waiting to receive About announcement from app");
                        throw new Exception(message);
                    }
                }
            }
        }

        if (deviceAboutAnnouncement == null)
        {
            String message;
            if ((deviceId != null) && (appId != null))
            {
                message = String.format("Timed out waiting for About announcement from app with deviceId: %s; appId: %s", deviceId, appId);
            }
            else
            {
                message = String.format("Timed out waiting for About announcement app supporting Onboarding and having a deviceId ending in: %s", deviceIdSuffix);
            }
            throw new Exception(message);
        }

        onboardingClient = serviceHelper.connectOnboardingClient(deviceAboutAnnouncement);
        return deviceAboutAnnouncement;
    }

    /**
     * Gets the soft ap ssid.
     *
     * @return the soft ap ssid
     */
    protected String getSoftApSsid()
    {
        String softAPSsid = null;
        if (getSoftAPConfig() != null)
        {
            softAPSsid = getSoftAPConfig().getSsid();
        }
        return softAPSsid;
    }

    /**
     * Wait for next about announcement from device.
     *
     * @param timeRemaining the time remaining
     * @return the about announcement details
     * @throws Exception the exception
     */
    private AboutAnnouncementDetails waitForNextAboutAnnouncementFromDevice(long timeRemaining) throws Exception
    {
        AboutAnnouncementDetails deviceAboutAnnouncement = serviceHelper.waitForNextDeviceAnnouncement(timeRemaining, TimeUnit.MILLISECONDS);
        if (deviceAboutAnnouncement != null)
        {
            if (!haveDeviceIdAndAppId() && !announcementMatchesDeviceIdSuffix(deviceAboutAnnouncement))
            {
                deviceAboutAnnouncement = null;
            }
        }
        return deviceAboutAnnouncement;
    }

    /**
     * Announcement matches device id suffix.
     *
     * @param deviceAboutAnnouncement the device about announcement
     * @return true, if successful
     * @throws BusException the bus exception
     */
    protected boolean announcementMatchesDeviceIdSuffix(AboutAnnouncementDetails deviceAboutAnnouncement) throws BusException
    {
        return deviceAboutAnnouncement.getDeviceId() != null && deviceAboutAnnouncement.getDeviceId().endsWith(deviceIdSuffix)
                && deviceAboutAnnouncement.supportsInterface(OnboardingTransport.INTERFACE_NAME);
    }

    /**
     * Have device id and app id.
     *
     * @return true, if successful
     */
    protected boolean haveDeviceIdAndAppId()
    {
        return (deviceId != null) && (appId != null);
    }

    /**
     * Call offboard.
     *
     * @throws Exception the exception
     */
    public void callOffboard() throws Exception
    {
        logger.info("Retrieving the Version property of the Onboarding interfaces");
        onboardingClient.getVersion();

        logger.info("Calling Onboarding.Offboard() method on DUT");
        onboardingClient.offboard();
      
        
    }

    /**
     * Retrieve version property.
     *
     * @return the short
     * @throws BusException the bus exception
     */
    public short retrieveVersionProperty() throws BusException
    {
        logger.info("Calling Onboarding.getVersion() method");
        return onboardingClient.getVersion();
    }

    /**
     * Retrieve state property.
     *
     * @return the short
     * @throws BusException the bus exception
     */
    public short retrieveStateProperty() throws BusException
    {
        logger.info("Calling Onboarding.getState() method");
        return onboardingClient.getState();
    }

    /**
     * Retrieve last error property.
     *
     * @return the OB last error
     * @throws BusException the bus exception
     */
    public OBLastError retrieveLastErrorProperty() throws BusException
    {
        logger.info("Calling Onboarding.GetLastError() method");
        return onboardingClient.GetLastError();
    }

    /**
     * Call scan info.
     *
     * @return the scan info
     * @throws BusException the bus exception
     */
    public ScanInfo callScanInfo() throws BusException
    {
        logger.info("Calling Onboarding.getScanInfo() method");
        return onboardingClient.getScanInfo();
    }

    /**
     * Checks if is device in onboarded state.
     *
     * @return true, if is device in onboarded state
     * @throws Exception the exception
     */
    public boolean isDeviceInOnboardedState() throws Exception
    {
        boolean isDeviceInOnboardedState = false;

        logger.debug("Checking if DUT is in Onboarded state");

        String personalAPNetworkName = personalAPConfig.getSsid();
        String softAPSsid = getSoftApSsid();

        String currentSsid = null;

        while (currentSsid == null)
        {
            currentSsid = wifiHelper.getCurrentSSID();
            if (currentSsid == null)
            {
                connectToPersonalAP();
            }
            else if (personalAPNetworkName.equals(currentSsid))
            {
                long startTime = System.currentTimeMillis();

                long timeToWaitInMs = TIME_TO_WAIT_FOR_NEXT_DEVICE_ANNOUNCEMENT_IN_MS;

                boolean isSoftAPAvailable = false;
                AboutAnnouncementDetails deviceAboutAnnouncement = null;

                while ((!isSoftAPAvailable) && (deviceAboutAnnouncement == null) && (System.currentTimeMillis() < startTime + timeToWaitInMs))
                {
                    deviceAboutAnnouncement = serviceHelper.waitForNextDeviceAnnouncement(1, TimeUnit.SECONDS);

                    if ((deviceAboutAnnouncement == null) && (softAPSsid != null))
                    {
                        isSoftAPAvailable = wifiHelper.waitForNetworkAvailable(softAPSsid, TIME_TO_WAIT_FOR_SCAN_RESULTS_IN_SECONDS, TimeUnit.SECONDS);
                    }
                }

                if (deviceAboutAnnouncement != null)
                {
                    isDeviceInOnboardedState = true;
                    onboardingClient = serviceHelper.connectOnboardingClient(deviceAboutAnnouncement);
                }
                else if (!isSoftAPAvailable)
                {
                    throw new Exception("Unable to determine current device state");
                }
            }
            else if (currentSsid.equals(softAPSsid))
            {
                isDeviceInOnboardedState = false;
            }
        }
        if (isDeviceInOnboardedState)
        {
            logger.debug("DUT is in Onboarded state");
        }
        else
        {
            logger.debug("DUT is in Offboarded state");
        }
        return isDeviceInOnboardedState;
    }

    /**
     * Connect about client.
     *
     * @param deviceAboutAnnouncement the device about announcement
     * @return the about client
     * @throws Exception the exception
     */
    public AboutClient connectAboutClient(AboutAnnouncementDetails deviceAboutAnnouncement) throws Exception
    {
        return serviceHelper.connectAboutClient(deviceAboutAnnouncement);
    }

    /**
     * Connect config client.
     *
     * @param deviceAboutAnnouncement the device about announcement
     * @return the config client
     * @throws Exception the exception
     */
    public ConfigClient connectConfigClient(AboutAnnouncementDetails deviceAboutAnnouncement) throws Exception
    {
        return serviceHelper.connectConfigClient(deviceAboutAnnouncement);
    }

    /**
     * Inits the service helper.
     *
     * @throws Exception the exception
     */
    private void initServiceHelper() throws Exception
    {
        releaseServiceHelper();
        serviceHelper = getServiceHelper();

        serviceHelper.initialize(BUS_APPLICATION_NAME, deviceId, appId);
        serviceHelper.startConfigClient();

        serviceHelper.enableAuthentication(keyStorePath);
    }

    /**
     * Release resources.
     */
    private void releaseResources()
    {
        releaseWifiHelper();
        releaseServiceHelper();
    }

    /**
     * Gets the service helper.
     *
     * @return the service helper
     */
    protected ServiceHelper getServiceHelper()
    {
    	return new ServiceHelper();
    }

    /**
     * Gets the wifi helper.
     *
     * @return the wifi helper
     */
    protected WifiHelper getWifiHelper()
    {
        return new WifiHelperImpl();
    }

    /**
     * Connect to soft ap network.
     *
     * @return true, if successful
     * @throws Exception the exception
     */
    private boolean connectToSoftAPNetwork() throws Exception
    {
        String ssid = getSoftAPConfig().getSsid();
        String connectedSsid = null;
        int numTries = 0;
        while (((connectedSsid == null) || (!ssid.equals(connectedSsid))) && (numTries < 2))
        {
            logger.info("Attempting to connect to Soft AP network");

            connectedSsid = wifiHelper.connectToNetwork(getSoftAPConfig(), true, TIME_TO_WAIT_TO_CONNECT_TO_SOFT_AP_IN_MS, TimeUnit.MILLISECONDS);
            numTries++;
        }
        return ssid.equals(connectedSsid);
    }

    /**
     * Gets the auth type.
     *
     * @param securityType the security type
     * @return the auth type
     */
    public short getAuthType(String securityType)
    {
        short authType = -1;

        try
        {
            authType = (short) Integer.parseInt(securityType);
        }
        catch (NumberFormatException e)
        {
            if (AuthType.WPA2_AUTO.name().equalsIgnoreCase(securityType))
            {
                authType = AuthType.WPA2_AUTO.getTypeId();
            }
            else if ("WPA2".equalsIgnoreCase(securityType))
            {
                authType = AuthType.WPA2_AUTO.getTypeId();
            }
            else if (AuthType.WPA_AUTO.name().equalsIgnoreCase(securityType))
            {
                authType = AuthType.WPA_AUTO.getTypeId();
            }
            else if ("WPA".equalsIgnoreCase(securityType))
            {
                authType = AuthType.WPA_AUTO.getTypeId();
            }
            else if (AuthType.ANY.name().equalsIgnoreCase(securityType))
            {
                authType = AuthType.ANY.getTypeId();
            }
            else if (AuthType.OPEN.name().equalsIgnoreCase(securityType))
            {
                authType = AuthType.OPEN.getTypeId();
            }
            else if ("NONE".equalsIgnoreCase(securityType))
            {
                authType = AuthType.OPEN.getTypeId();
            }
            else if (AuthType.WEP.name().equalsIgnoreCase(securityType))
            {
                authType = AuthType.WEP.getTypeId();
            }
            else if (AuthType.WPA_TKIP.name().equalsIgnoreCase(securityType))
            {
                authType = AuthType.WPA_TKIP.getTypeId();
            }
            else if (AuthType.WPA_CCMP.name().equalsIgnoreCase(securityType))
            {
                authType = AuthType.WPA_CCMP.getTypeId();
            }
            else if (AuthType.WPA2_TKIP.name().equalsIgnoreCase(securityType))
            {
                authType = AuthType.WPA2_TKIP.getTypeId();
            }
            else if (AuthType.WPA2_CCMP.name().equalsIgnoreCase(securityType))
            {
                authType = AuthType.WPA2_CCMP.getTypeId();
            }
            else if (AuthType.WPS.name().equalsIgnoreCase(securityType))
            {
                authType = AuthType.WPS.getTypeId();
            }
        }

        return authType;
    }

    /**
     * Gets the onboarding service auth type.
     *
     * @param securityType the security type
     * @return the onboarding service auth type
     */
    public AuthType getOnboardingServiceAuthType(String securityType)
    {
        AuthType authType = AuthType.ANY;

        try
        {
            short authTypeShortValue = (short) Integer.parseInt(securityType);
            authType = AuthType.getAuthTypeById(authTypeShortValue);
            if(authType==null){
            	//authType=AuthType.INVALID; 
            }
        }
        catch (NumberFormatException e)
        {
            if (AuthType.WPA2_AUTO.name().equalsIgnoreCase(securityType))
            {
                authType = AuthType.WPA2_AUTO;
            }
            else if ("WPA2".equalsIgnoreCase(securityType))
            {
                authType = AuthType.WPA2_AUTO;
            }
            else if (AuthType.WPA_AUTO.name().equalsIgnoreCase(securityType))
            {
                authType = AuthType.WPA_AUTO;
            }
            else if ("WPA".equalsIgnoreCase(securityType))
            {
                authType = AuthType.WPA_AUTO;
            }
            else if (AuthType.ANY.name().equalsIgnoreCase(securityType))
            {
                authType = AuthType.ANY;
            }
            else if (AuthType.OPEN.name().equalsIgnoreCase(securityType))
            {
                authType = AuthType.OPEN;
            }
            else if ("NONE".equalsIgnoreCase(securityType))
            {
                authType = AuthType.OPEN;
            }
            else if (AuthType.WEP.name().equalsIgnoreCase(securityType))
            {
                authType = AuthType.WEP;
            }
            else if (AuthType.WPA_TKIP.name().equalsIgnoreCase(securityType))
            {
                authType = AuthType.WPA_TKIP;
            }
            else if (AuthType.WPA_CCMP.name().equalsIgnoreCase(securityType))
            {
                authType = AuthType.WPA_CCMP;
            }
            else if (AuthType.WPA2_TKIP.name().equalsIgnoreCase(securityType))
            {
                authType = AuthType.WPA2_TKIP;
            }
            else if (AuthType.WPA2_CCMP.name().equalsIgnoreCase(securityType))
            {
                authType = AuthType.WPA2_CCMP;
            }
            else if (AuthType.WPS.name().equalsIgnoreCase(securityType))
            {
                authType = AuthType.WPS;
            }
           // else if (AuthType.INVALID.name().equalsIgnoreCase(securityType))
           // {
            //    authType = AuthType.INVALID;
            //}
        }

        return authType;
    }

    /**
     * Map auth type to auth type string.
     *
     * @param authType the auth type
     * @return the string
     */
    public String mapAuthTypeToAuthTypeString(short authType)
    {
        String authTypeString;

        if (authType == AuthType.WPA2_AUTO.getTypeId())
        {
            authTypeString = AuthType.WPA2_AUTO.name();
        }
        else if (authType == AuthType.WPA_AUTO.getTypeId())
        {
            authTypeString = AuthType.WPA_AUTO.name();
        }
        else if (authType == AuthType.ANY.getTypeId())
        {
            authTypeString = AuthType.ANY.name();
        }
        else if (authType == AuthType.OPEN.getTypeId())
        {
            authTypeString = AuthType.OPEN.name();
        }
        else if (authType == AuthType.WEP.getTypeId())
        {
            authTypeString = AuthType.WEP.name();
        }
        else if (authType == AuthType.WPA_TKIP.getTypeId())
        {
            authTypeString = AuthType.WPA_TKIP.name();
        }
        else if (authType == AuthType.WPA_CCMP.getTypeId())
        {
            authTypeString = AuthType.WPA_CCMP.name();
        }
        else if (authType == AuthType.WPA2_TKIP.getTypeId())
        {
            authTypeString = AuthType.WPA2_TKIP.name();
        }
        else if (authType == AuthType.WPA2_CCMP.getTypeId())
        {
            authTypeString = AuthType.WPA2_CCMP.name();
        }
        else if (authType == AuthType.WPS.getTypeId())
        {
            authTypeString = AuthType.WPS.name();
        }
        else
        {
            authTypeString = AuthType.OPEN.name();
        }

        return authTypeString;
    }

    /**
     * Convert to hex.
     *
     * @param networkPassword the network password
     * @return the string
     */
    private String convertToHex(String networkPassword)
    {
        StringBuilder sb = new StringBuilder();
        try
        {
            for (byte chr : networkPassword.getBytes("UTF-8"))
            {
                sb.append(Integer.toHexString(chr));
            }
        }
        catch (UnsupportedEncodingException e)
        {
            throw new RuntimeException(e);
        }
        return sb.toString();
    }

    /**
     * Wait for soft ap disconnect.
     *
     * @throws Exception the exception
     */
    public void waitForSoftAPDisconnect() throws Exception
    {

        String disconnectedSsid = wifiHelper.waitForDisconnect(TIME_TO_WAIT_FOR_DISCONNECT_IN_MS, TimeUnit.MILLISECONDS);
        if (disconnectedSsid == null)
        {
            throw new Exception("Timed out waiting for soft AP to disconnect");
        }
    }

    /**
     * Release wifi helper.
     */
    private void releaseWifiHelper()
    {
        if (wifiHelper != null)
        {
            wifiHelper.release();
            wifiHelper = null;
        }
    }

    /**
     * Release service helper.
     */
    private void releaseServiceHelper()
    {
        try
        {
            disconnectOnboardingClient();
            if (serviceHelper != null)
            {
                serviceHelper.release();
            }
            serviceHelper = null;
        }
        catch (Exception ex)
        {
            logger.debug("Exception releasing resources: "+ ex.getMessage());
        }
    }

    /**
     * Disconnect onboarding client.
     */
    private void disconnectOnboardingClient()
    {
        if (onboardingClient != null)
        {
            logger.debug("Disconnecting onboarding client");
            onboardingClient.disconnect();
            onboardingClient = null;
        }
    }

    /**
     * Gets the personal ap config.
     *
     * @return the personal ap config
     */
    public WifiNetworkConfig getPersonalAPConfig()
    {
        return personalAPConfig;
    }

    /**
     * Sets the personal ap config.
     *
     * @param personalAPConfig the new personal ap config
     */
    public void setPersonalAPConfig(WifiNetworkConfig personalAPConfig)
    {
        this.personalAPConfig = personalAPConfig;
    }

    /**
     * Gets the soft ap config.
     *
     * @return the soft ap config
     */
    public WifiNetworkConfig getSoftAPConfig()
    {
        return softAPConfig;
    }

    /**
     * Sets the soft ap config.
     *
     * @param softAPConfig the new soft ap config
     */
    public void setSoftAPConfig(WifiNetworkConfig softAPConfig)
    {
        this.softAPConfig = softAPConfig;
        String softApSsid = getSoftAPConfig().getSsid();
        if (softApSsid != null)
        {
            deviceIdSuffix = softApSsid.substring(softApSsid.length() - NUMBER_OF_DEVICE_ID_DIGITS_IN_SOFT_AP);
        }
    }

    /**
     * Checks if is authentication failed.
     *
     * @param aboutAnnouncementDetails the about announcement details
     * @return true, if is authentication failed
     */
    public boolean isAuthenticationFailed(AboutAnnouncementDetails aboutAnnouncementDetails)
    {
        return !serviceHelper.isPeerAuthenticationSuccessful(aboutAnnouncementDetails);
    }

    /**
     * Clear queued device announcements.
     */
    public void clearQueuedDeviceAnnouncements()
    {
        serviceHelper.clearQueuedDeviceAnnouncements();

    }

}