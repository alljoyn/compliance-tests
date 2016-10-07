/*******************************************************************************
 *  Copyright (c) Open Connectivity Foundation (OCF) and AllJoyn Open
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
package com.at4wireless.alljoyn.core.onboarding;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.alljoyn.bus.AboutProxy;
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
import com.at4wireless.alljoyn.core.commons.log.Logger;
import com.at4wireless.alljoyn.core.commons.log.WindowsLoggerImpl;
import com.at4wireless.alljoyn.wifiapi.ScanResult;

public class OnboardingHelper
{
    //private static final int TIME_TO_WAIT_FOR_SCAN_RESULTS_IN_SECONDS = 2;
	private int TIME_TO_WAIT_FOR_SCAN_RESULTS_IN_SECONDS = 2;
	private static final Logger logger = new WindowsLoggerImpl(OnboardingHelper.class.getSimpleName());
    private OnboardingClient onboardingClient;
    private WifiHelper wifiHelper;
    private ServiceHelper serviceHelper;
    private static final String BUS_APPLICATION_NAME = "OnboardingTestSuite";
    private static final int NUMBER_OF_DEVICE_ID_DIGITS_IN_SOFT_AP = 7;
    private UUID appId;
    private String deviceId;

    private WifiNetworkConfig personalAPConfig;
    private WifiNetworkConfig softAPConfig;
    private String deviceIdSuffix;

    private String keyStorePath;
    //private Context context;
   
    protected static final long TIME_TO_WAIT_FOR_SOFT_AP_IN_MS_SHORT = 120000;
    protected static final long TIME_TO_WAIT_TO_CONNECT_TO_PERSONAL_AP_IN_MS = 60000;
    protected static final long TIME_TO_WAIT_TO_CONNECT_TO_SOFT_AP_IN_MS = 60000;
    protected static final long TIME_TO_WAIT_FOR_NEXT_DEVICE_ANNOUNCEMENT_IN_MS = 180000;
    protected static final long TIME_TO_WAIT_FOR_DISCONNECT_IN_MS = 30000;
    
    protected static final short OBS_CONFIGURE_WIFI_RETURN_NO_CHANNEL_SWITCHING = 1;
    protected static final short OBS_CONFIGURE_WIFI_RETURN_SUPPORTS_CHANNEL_SWITCHING = 2;

    //public OnboardingHelper(Context context)
    public OnboardingHelper(int GPON_TimeToWaitForScanResults)
    {
        //this.context = context;
    	TIME_TO_WAIT_FOR_SCAN_RESULTS_IN_SECONDS = GPON_TimeToWaitForScanResults;
    }

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

    public void release()
    {
        releaseResources();
    }

    public void setPasscode(AboutAnnouncementDetails aboutAnnouncement, char[] passcode)
    {
        serviceHelper.setAuthPassword(aboutAnnouncement, passcode);
    }

    public String connectToDUTOnSoftAP() throws Exception
    {

    	releaseServiceHelper();
    	
    	/*if (wifiHelper.getCurrentSSID() != null) { //[AT4]
    		wifiHelper.waitForDisconnect(5, TimeUnit.SECONDS);
    	}*/
    	
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
                        if ((scanResult.SSID.startsWith("AJ_") || scanResult.SSID.endsWith("_AJ")) //[AT4]
                        		&& softAPMatchesDeviceId(scanResult.SSID, deviceId)) 
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

    private String getSoftAPSsid()
    {
        String softAPssid = null;
        if (getSoftAPConfig() != null)
        {
            softAPssid = getSoftAPConfig().getSsid();
        }
        return softAPssid;
    } 

    private boolean softAPMatchesDeviceId(String ssid, String deviceId)
    {
        String deviceIdSuffix = ssid.substring(ssid.length() - NUMBER_OF_DEVICE_ID_DIGITS_IN_SOFT_AP);
        return deviceId.endsWith(deviceIdSuffix);
    }

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

    private void verifyOnboardingState(short expectedState) throws Exception
    {
        logger.info("Retrieving the State property from the Onboarding interface");
        short clientState = onboardingClient.getState();
        if (expectedState != clientState)
        {
            throw new Exception(String.format("State property of Onboarding interface does not match expected value; was: %d expected: %d", clientState, expectedState));
        }
    }

    public void callConnectWiFiAndWaitForSoftAPDisconnect() throws Exception
    {
        logger.info("Calling Onboarding.connectWiFi() method");
        onboardingClient.connectWiFi();

        waitForSoftAPDisconnect();
    }

    public void connectToPersonalAPIfNeeded() throws Exception
    {
        String personalAPNetworkName = personalAPConfig.getSsid();
        String currentSsid = wifiHelper.getCurrentSSID();
        if ((currentSsid == null) || (!currentSsid.equals(personalAPNetworkName)))
        {
            connectToPersonalAP();
        }
    }

    public void connectToPersonalAP() throws Exception
    {	
        releaseServiceHelper();
        String personalAPNetworkName = personalAPConfig.getSsid();
        
        //System.out.println(wifiHelper.getCurrentSSID());
        /*if (wifiHelper.getCurrentSSID() != null) { //[AT4]
        	wifiHelper.waitForDisconnect(5, TimeUnit.SECONDS);
        }*/
        
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
            deviceAboutAnnouncement = waitForNextAboutAnnouncementFromDevice(1000); //[AT4]
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
                                "Soft AP became available while waiting to receive About announcement from app supporting Onboarding and having a deviceId ending in: %s",
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

    protected String getSoftApSsid()
    {
        String softAPSsid = null;
        if (getSoftAPConfig() != null)
        {
            softAPSsid = getSoftAPConfig().getSsid();
        }
        return softAPSsid;
    }

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

    protected boolean announcementMatchesDeviceIdSuffix(AboutAnnouncementDetails deviceAboutAnnouncement) throws BusException
    {
        return deviceAboutAnnouncement.getDeviceId() != null && deviceAboutAnnouncement.getDeviceId().endsWith(deviceIdSuffix)
                && deviceAboutAnnouncement.supportsInterface(OnboardingTransport.INTERFACE_NAME);
    }

    protected boolean haveDeviceIdAndAppId()
    {
        return (deviceId != null) && (appId != null);
    }

    public void callOffboard() throws Exception
    {
        logger.info("Retrieving the Version property of the Onboarding interfaces");
        onboardingClient.getVersion();

        logger.info("Calling Onboarding.Offboard() method on DUT");
        onboardingClient.offboard();
    }

    public short retrieveVersionProperty() throws BusException
    {
        logger.info("Calling Onboarding.getVersion() method");
        return onboardingClient.getVersion();
    }

    public short retrieveStateProperty() throws BusException
    {
        logger.info("Calling Onboarding.getState() method");
        return onboardingClient.getState();
    }

    public OBLastError retrieveLastErrorProperty() throws BusException
    {
        logger.info("Calling Onboarding.GetLastError() method");
        return onboardingClient.GetLastError();
    }

    public ScanInfo callScanInfo() throws BusException
    {
        logger.info("Calling Onboarding.getScanInfo() method");
        return onboardingClient.getScanInfo();
    }

    public boolean isDeviceInOnboardedState() throws Exception
    {
        boolean isDeviceInOnboardedState = false;

        logger.info("Checking if DUT is in Onboarded state");

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
            logger.info("DUT is in Onboarded state");
        }
        else
        {
            logger.info("DUT is in Offboarded state");
        }
        return isDeviceInOnboardedState;
    }

    /*public AboutClient connectAboutClient(AboutAnnouncementDetails deviceAboutAnnouncement) throws Exception
    {
        return serviceHelper.connectAboutClient(deviceAboutAnnouncement);
    }*/
    
    public AboutProxy connectAboutProxy(AboutAnnouncementDetails deviceAboutAnnouncement) throws Exception
    {
    	return serviceHelper.connectAboutProxy(deviceAboutAnnouncement);
    }

    public ConfigClient connectConfigClient(AboutAnnouncementDetails deviceAboutAnnouncement) throws Exception
    {
        return serviceHelper.connectConfigClient(deviceAboutAnnouncement);
    }

    private void initServiceHelper() throws Exception
    {
        releaseServiceHelper();
        serviceHelper = getServiceHelper();

        serviceHelper.initialize(BUS_APPLICATION_NAME, deviceId, appId);
        serviceHelper.startConfigClient();

        serviceHelper.enableAuthentication(keyStorePath);
    }

    private void releaseResources()
    {
        releaseWifiHelper();
        releaseServiceHelper();
    }

    protected ServiceHelper getServiceHelper()
    {
    	return new ServiceHelper();
    }

    protected WifiHelper getWifiHelper()
    {
        return new WifiHelperImpl();
    }

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

    public AuthType getOnboardingServiceAuthType(String securityType)
    {
        AuthType authType = AuthType.ANY;

        try
        {
            short authTypeShortValue = (short) Integer.parseInt(securityType);
            authType = AuthType.getAuthTypeById(authTypeShortValue);
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
        }

        return authType;
    }

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

    public void waitForSoftAPDisconnect() throws Exception
    {

        String disconnectedSsid = wifiHelper.waitForDisconnect(TIME_TO_WAIT_FOR_DISCONNECT_IN_MS, TimeUnit.MILLISECONDS);
        if (disconnectedSsid == null)
        {
            throw new Exception("Timed out waiting for soft AP to disconnect");
        }
    }

    private void releaseWifiHelper()
    {
        if (wifiHelper != null)
        {
            wifiHelper.release();
            wifiHelper = null;
        }
    }

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
            logger.info("Exception releasing resources: "+ ex.getMessage());
        }
    }

    private void disconnectOnboardingClient()
    {
        if (onboardingClient != null)
        {
            logger.info("Disconnecting onboarding client");
            onboardingClient.disconnect();
            onboardingClient = null;
        }
    }

    public WifiNetworkConfig getPersonalAPConfig()
    {
        return personalAPConfig;
    }

    public void setPersonalAPConfig(WifiNetworkConfig personalAPConfig)
    {
        this.personalAPConfig = personalAPConfig;
    }

    public WifiNetworkConfig getSoftAPConfig()
    {
        return softAPConfig;
    }

    public void setSoftAPConfig(WifiNetworkConfig softAPConfig)
    {
        this.softAPConfig = softAPConfig;
        String softApSsid = getSoftAPConfig().getSsid();
        if (softApSsid != null)
        {
            deviceIdSuffix = softApSsid.substring(softApSsid.length() - NUMBER_OF_DEVICE_ID_DIGITS_IN_SOFT_AP);
        }
    }

    public boolean isAuthenticationFailed(AboutAnnouncementDetails aboutAnnouncementDetails)
    {
        return !serviceHelper.isPeerAuthenticationSuccessful(aboutAnnouncementDetails);
    }

    public void clearQueuedDeviceAnnouncements()
    {
        serviceHelper.clearQueuedDeviceAnnouncements();

    }

}