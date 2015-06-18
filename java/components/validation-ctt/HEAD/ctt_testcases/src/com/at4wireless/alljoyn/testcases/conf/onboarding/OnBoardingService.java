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
package com.at4wireless.alljoyn.testcases.conf.onboarding;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

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

import com.at4wireless.alljoyn.core.about.AboutAnnouncementDetails;
import com.at4wireless.alljoyn.core.commons.SrpAnonymousKeyListener;
import com.at4wireless.alljoyn.core.commons.log.WindowsLoggerImpl;
import com.at4wireless.alljoyn.core.onboarding.OnboardingHelper;
import com.at4wireless.alljoyn.core.onboarding.WifiNetworkConfig;

// TODO: Auto-generated Javadoc
/**
 * The Class OnBoardingService.
 */
public class OnBoardingService {

	/** The pass. */
	private  Boolean pass=true;
	
	/** The inconc. */
	private  Boolean inconc=false;
	
	/** The tag. */
	private  final String TAG = "OnboardingTestSuite";
	
	/** The logger. */
	private  final WindowsLoggerImpl logger =  new WindowsLoggerImpl(TAG);
	
	/** The time out. */
	private  int  timeOut=30;
	
	/** The port. */
	private  short port=91;

	/** The dut app id. */
	private  UUID dutAppId;
	
	/** The dut device id. */
	private  String dutDeviceId;
	
	/** The key store path. */
	private  String keyStorePath="/KeyStore";


	/** The personal ap config. */
	private  WifiNetworkConfig personalAPConfig;
	
	/** The soft ap config. */
	private  WifiNetworkConfig softAPConfig;
	
	/** The soft a pssid. */
	private  String softAPssid;

	/** The invalid network name. */
	protected  final String INVALID_NETWORK_NAME = "InvalidPersonalAP";
	
	/** The invalid network passphrase. */
	protected  final String INVALID_NETWORK_PASSPHRASE = "InvalidNetworkPassphrase";
	
	/** The invalid auth type. */
	protected  final short INVALID_AUTH_TYPE = 9999;

	/** The time to wait for soft ap in ms short. */
	protected  long TIME_TO_WAIT_FOR_SOFT_AP_IN_MS_SHORT = 120000;
	
	/** The time to wait to connect to soft ap in ms. */
	protected  long TIME_TO_WAIT_TO_CONNECT_TO_SOFT_AP_IN_MS = 60000;
	
	/** The time to wait for soft ap after offboard. */
	protected  long TIME_TO_WAIT_FOR_SOFT_AP_AFTER_OFFBOARD = 15000;
	
	/** The time to wait to connect to personal ap in ms. */
	protected  long TIME_TO_WAIT_TO_CONNECT_TO_PERSONAL_AP_IN_MS = 60000;
	
	/** The time to wait for disconnect in ms. */
	protected  long TIME_TO_WAIT_FOR_DISCONNECT_IN_MS = 30000;
	
	/** The time to wait for next device announcement in ms. */
	protected  long TIME_TO_WAIT_FOR_NEXT_DEVICE_ANNOUNCEMENT_IN_MS = 180000;

	/** The obs state personal ap not configured. */
	public  final short OBS_STATE_PERSONAL_AP_NOT_CONFIGURED = OnboardingService.OnboardingState.PERSONAL_AP_NOT_CONFIGURED.getStateId();
	
	/** The obs state personal ap not validated. */
	public  final short OBS_STATE_PERSONAL_AP_NOT_VALIDATED = OnboardingService.OnboardingState.PERSONAL_AP_CONFIGURED_NOT_VALIDATED.getStateId();
	
	/** The obs state personal ap configured validated. */
	public  final short OBS_STATE_PERSONAL_AP_CONFIGURED_VALIDATED = OnboardingService.OnboardingState.PERSONAL_AP_CONFIGURED_VALIDATED.getStateId();
	
	/** The obs state personal ap configured error. */
	public  final short OBS_STATE_PERSONAL_AP_CONFIGURED_ERROR = OnboardingService.OnboardingState.PERSONAL_AP_CONFIGURED_ERROR.getStateId();

	/** The obs lasterror validated. */
	public  final short OBS_LASTERROR_VALIDATED = 0;
	
	/** The obs lasterror unreachable. */
	public  final short OBS_LASTERROR_UNREACHABLE = 1;
	
	/** The obs lasterror unauthorized. */
	public  final short OBS_LASTERROR_UNAUTHORIZED = 3;

	/** The obs configure wifi return no channel switching. */
	protected  final short OBS_CONFIGURE_WIFI_RETURN_NO_CHANNEL_SWITCHING = 1;
	
	/** The obs configure wifi return supports channel switching. */
	protected  final short OBS_CONFIGURE_WIFI_RETURN_SUPPORTS_CHANNEL_SWITCHING = 2;

	/** The alljoyn error feature not available. */
	protected  final String ALLJOYN_ERROR_FEATURE_NOT_AVAILABLE = "org.alljoyn.Error.FeatureNotAvailable";

	/** The config client. */
	private  ConfigClient configClient;
	
	/** The about client. */
	private  AboutClient aboutClient;
	
	/** The onboarding helper. */
	private  OnboardingHelper onboardingHelper;
	
	/** The valid default passcode. */
	private  final char[] VALID_DEFAULT_PASSCODE = SrpAnonymousKeyListener.DEFAULT_PINCODE;
	
	/** The temp passcode. */
	private  final char[] TEMP_PASSCODE = "111111".toCharArray();
	
	/** The invalid passcode. */
	private  final char[] INVALID_PASSCODE = "123456".toCharArray();

	/** The new device name. */
	protected  final String NEW_DEVICE_NAME = "newDeviceName";

	 /** The ICSO n_ onboarding service framework. */
 	boolean ICSON_OnboardingServiceFramework=false;
	 
 	/** The ICSO n_ onboarding interface. */
 	boolean ICSON_OnboardingInterface=false;
	 
 	/** The ICSO n_ channel switching. */
 	boolean ICSON_ChannelSwitching=false;
	 
 	/** The ICSO n_ get scan info method. */
 	boolean ICSON_GetScanInfoMethod=false;	

	//
	 /** The IXITC o_ app id. */
	String IXITCO_AppId=null;
	 
 	/** The IXITC o_ default language. */
 	String IXITCO_DefaultLanguage=null;
	 
 	/** The IXITC o_ device id. */
 	String IXITCO_DeviceId=null;
	//
	 /** The IXITO n_ onboarding version. */
	String IXITON_OnboardingVersion=null;
	 
 	/** The IXITO n_ soft ap. */
 	String IXITON_SoftAP=null;
	 
 	/** The IXITO n_ soft ap auth type. */
 	String IXITON_SoftAPAuthType=null;
	 
 	/** The IXITO n_ soft a ppassphrase. */
 	String IXITON_SoftAPpassphrase=null;
	 
 	/** The IXITO n_ personal ap. */
 	String IXITON_PersonalAP=null;
	 
 	/** The IXITO n_ personal ap auth type. */
 	String IXITON_PersonalAPAuthType=null;
	 
 	/** The IXITO n_ personal a ppassphrase. */
 	String IXITON_PersonalAPpassphrase=null;

	/**
	 * Instantiates a new on boarding service.
	 *
	 * @param testCase the test case
	 * @param iCSON_OnboardingServiceFramework the i cso n_ onboarding service framework
	 * @param iCSON_OnboardingInterface the i cso n_ onboarding interface
	 * @param iCSON_ChannelSwitching the i cso n_ channel switching
	 * @param iCSON_GetScanInfoMethod the i cso n_ get scan info method
	 * @param iXITCO_AppId the i xitc o_ app id
	 * @param iXITCO_DeviceId the i xitc o_ device id
	 * @param iXITCO_DefaultLanguage the i xitc o_ default language
	 * @param iXITON_OnboardingVersion the i xito n_ onboarding version
	 * @param iXITON_SoftAP the i xito n_ soft ap
	 * @param iXITON_SoftAPAuthType the i xito n_ soft ap auth type
	 * @param iXITON_SoftAPpassphrase the i xito n_ soft a ppassphrase
	 * @param iXITON_PersonalAP the i xito n_ personal ap
	 * @param iXITON_PersonalAPAuthType the i xito n_ personal ap auth type
	 * @param iXITON_PersonalAPpassphrase the i xito n_ personal a ppassphrase
	 * @param gPCO_AnnouncementTimeout the g pc o_ announcement timeout
	 * @param gPON_WaitSoftAP the g po n_ wait soft ap
	 * @param gPON_ConnectSoftAP the g po n_ connect soft ap
	 * @param gPON_WaitSoftAPAfterOffboard the g po n_ wait soft ap after offboard
	 * @param gPON_ConnectPersonalAP the g po n_ connect personal ap
	 * @param gPON_Disconnect the g po n_ disconnect
	 * @param gPON_NextAnnouncement the g po n_ next announcement
	 */
	public OnBoardingService(String testCase,
			boolean iCSON_OnboardingServiceFramework,
			boolean iCSON_OnboardingInterface, boolean iCSON_ChannelSwitching,
			boolean iCSON_GetScanInfoMethod, String iXITCO_AppId,
			String iXITCO_DeviceId, String iXITCO_DefaultLanguage,
			String iXITON_OnboardingVersion, String iXITON_SoftAP,
			String iXITON_SoftAPAuthType, String iXITON_SoftAPpassphrase,
			String iXITON_PersonalAP, String iXITON_PersonalAPAuthType,
			String iXITON_PersonalAPpassphrase,
			String gPCO_AnnouncementTimeout, String gPON_WaitSoftAP,
			String gPON_ConnectSoftAP, String gPON_WaitSoftAPAfterOffboard,
			String gPON_ConnectPersonalAP, String gPON_Disconnect,
			String gPON_NextAnnouncement) {


		ICSON_OnboardingServiceFramework=iCSON_OnboardingServiceFramework;
		ICSON_OnboardingInterface=iCSON_OnboardingInterface;
		ICSON_ChannelSwitching=iCSON_ChannelSwitching;
		ICSON_GetScanInfoMethod=iCSON_GetScanInfoMethod;	

		//
		IXITCO_AppId=iXITCO_AppId;
		IXITCO_DefaultLanguage=iXITCO_DefaultLanguage;
		IXITCO_DeviceId=iXITCO_DeviceId;
		//
		IXITON_OnboardingVersion=iXITON_OnboardingVersion;
		IXITON_SoftAP=iXITON_SoftAP;
		IXITON_SoftAPAuthType=iXITON_SoftAPAuthType;
		IXITON_SoftAPpassphrase=iXITON_SoftAPpassphrase;
		IXITON_PersonalAP=iXITON_PersonalAP;
		IXITON_PersonalAPAuthType=iXITON_PersonalAPAuthType;
		IXITON_PersonalAPpassphrase=iXITON_PersonalAPpassphrase;

		timeOut = Integer.parseInt(gPCO_AnnouncementTimeout);
		TIME_TO_WAIT_FOR_SOFT_AP_IN_MS_SHORT = Integer.parseInt(gPON_WaitSoftAP);;
		TIME_TO_WAIT_TO_CONNECT_TO_SOFT_AP_IN_MS = Integer.parseInt(gPON_ConnectSoftAP);;
		TIME_TO_WAIT_FOR_SOFT_AP_AFTER_OFFBOARD = Integer.parseInt(gPON_WaitSoftAPAfterOffboard);;
		TIME_TO_WAIT_TO_CONNECT_TO_PERSONAL_AP_IN_MS = Integer.parseInt(gPON_ConnectPersonalAP);;
		TIME_TO_WAIT_FOR_DISCONNECT_IN_MS = Integer.parseInt(gPON_Disconnect);;
		TIME_TO_WAIT_FOR_NEXT_DEVICE_ANNOUNCEMENT_IN_MS = Integer.parseInt(gPON_NextAnnouncement);;

		try{

			runTestCase(testCase);


		}catch(Exception e){

			if(e!=null){

				if(e.getMessage().equals("Timed out waiting for About announcement")){


					fail("Timed out waiting for About announcement");
					inconc=true;


				}else{


					fail("Exception: "+e.getMessage());

					inconc=true;


				}

			}}
	}




	/**
	 * Run test case.
	 *
	 * @param testCase the test case
	 * @throws Exception the exception
	 */
	public  void runTestCase(String testCase) throws Exception {
		
		setUp();
		logger.info("Running testcase: "+testCase);
		
		if(testCase.equals("Onboarding-v1-01")){
			testOnboarding_v1_01_OffboardDevice();
		}else if(testCase.equals("Onboarding-v1-02")){
			testOnboarding_v1_02_OnboardDevice();
		}else if(testCase.equals("Onboarding-v1-03")){
			testOnboarding_v1_03_ConnectivityOverSoftAP();
		
		}else if(testCase.equals("Onboarding-v1-04")){
			testOnboarding_v1_04_ConfigureWiFiWithOutOfRangeValue();
		}else if(testCase.equals("Onboarding-v1-05")){
			testOnboarding_v1_05_ConfigureWiFiWithWrongSSID();
		}else if(testCase.equals("Onboarding-v1-06")){
			testOnboarding_v1_06_ConfigureWiFiWithWrongPassword();
		}else if(testCase.equals("Onboarding-v1-07")){
			testOnboarding_v1_07_ConfigureWiFiAuthTypeOfAny();
		}else if(testCase.equals("Onboarding-v1-08")){
			testOnboarding_v1_08_GetScanInfo();
		}else if(testCase.equals("Onboarding-v1-09")){
			testOnboarding_v1_09_WrongPasscode();
		}else if(testCase.equals("Onboarding-v1-10")){
			testOnboarding_v1_10_AuthenticateAfterChangingPasscode();
		}else if(testCase.equals("Onboarding-v1-11")){
			testOnboarding_v1_11_FactoryResetClearsConfiguration();
		}else if(testCase.equals("Onboarding-v1-12")){
			testOnboarding_v1_12_FactoryResetResetsPasscode();

		}else {


			fail("TestCase not valid");
		}



		tearDown();


	}




	/**
	 * Tear down.
	 */
	private  void tearDown() {
		System.out.println("====================================================");
		logger.debug("test tearDown started");
		releaseResources();
		logger.debug("test tearDown done");
		System.out.println("====================================================");
	}




	/**
	 * Release resources.
	 */
	private  void releaseResources() {

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

	/**
	 * Disconnect config client.
	 */
	private  void disconnectConfigClient()
	{
		if (configClient != null)
		{
			logger.debug("Disconnecting config client");
			configClient.disconnect();
			configClient = null;
		}
	}

	/**
	 * Disconnect about client.
	 */
	private  void disconnectAboutClient()
	{
		if (aboutClient != null)
		{
			logger.debug("Disconnecting about client");
			aboutClient.disconnect();
			aboutClient = null;
		}
	}




	/**
	 * Sets the up.
	 *
	 * @throws Exception the exception
	 */
	private   void setUp() throws Exception {
		System.out.println("====================================================");
		logger.debug("test setUp started");

		try
		{

			dutDeviceId = IXITCO_DeviceId;
			logger.debug(String.format("Running Onboarding test case against Device ID: %s", dutDeviceId));
			dutAppId = UUID.fromString(IXITCO_AppId);
			logger.debug(String.format("Running Onboarding test case against App ID: %s", dutAppId));

			logger.debug(String.format("Running Onboarding test case using KeyStorePath: %s", keyStorePath));

			onboardingHelper = getOnboardingHelper();
			
			//short personalApSecurity = Short.parseShort(IXITON_PersonalAPAuthType);
			logger.debug(String.format("Running Onboarding test case using PersonalApSecurity: %s", IXITON_PersonalAPAuthType));
			String personalApSsid = IXITON_PersonalAP;
			logger.debug(String.format("Running Onboarding test case using PersonalApSsid: %s", personalApSsid));
			String personalApPassphrase = IXITON_PersonalAPpassphrase;
			logger.debug(String.format("Running Onboarding test case using PersonalApPassphrase: %s", personalApPassphrase));
			String onboardeeSoftApSsid = IXITON_SoftAP;
			logger.debug(String.format("Running Onboarding test case against onboardeeSoftApSsid: %s", onboardeeSoftApSsid));
			String onboardeeSoftApPassphrase = IXITON_SoftAPpassphrase;
			logger.debug(String.format("Running Onboarding test case against OnboardeeSoftApPassphrase: %s", onboardeeSoftApPassphrase));
			String onboardeeSoftApSecurityType = IXITON_SoftAPAuthType;
			logger.debug(String.format("Running Onboarding test case against OnboardeeSoftApSecurityType: %s", onboardeeSoftApSecurityType));

			//String authTypeString = onboardingHelper.mapAuthTypeToAuthTypeString(personalApSecurity);
			personalAPConfig = new WifiNetworkConfig(personalApSsid, personalApPassphrase, IXITON_PersonalAPAuthType);
			onboardingHelper.setPersonalAPConfig(personalAPConfig);

			softAPConfig = new WifiNetworkConfig(onboardeeSoftApSsid, onboardeeSoftApPassphrase, onboardeeSoftApSecurityType);
			onboardingHelper.setSoftAPConfig(softAPConfig);
			
			onboardingHelper.initialize(keyStorePath, dutDeviceId, dutAppId);
			logger.debug("test setUp done");
			System.out.println("====================================================");
		}
		catch (Exception e)
		{
			inconc=true;
			releaseResources();
			throw new Exception(e);
		}

	}

	/**
	 * Test onboarding_v1_01_ offboard device.
	 *
	 * @throws Exception the exception
	 */
	public  void testOnboarding_v1_01_OffboardDevice() throws Exception
	{
		softAPssid = null;

		onboardingHelper.connectToPersonalAPIfNeeded();
		
		checkDeviceIsInOnboardedStateAndWaitForAnnouncement();
	
		short version = onboardingHelper.retrieveVersionProperty();
		assertEquals("Onboarding interface version mismatch", IXITON_OnboardingVersion, version);

		placeDUTInOffboardState();
		
		verifyOnboardingState(OBS_STATE_PERSONAL_AP_NOT_CONFIGURED);
		
		validateSoftAP(softAPssid);
	}




	/**
	 * Test onboarding_v1_02_ onboard device.
	 *
	 * @throws Exception the exception
	 */
	public  void testOnboarding_v1_02_OnboardDevice() throws Exception
	{
		makeSureDeviceIsInOffboardedState();

		placeDUTInOnboardState();
	}



	/**
	 * Test onboarding_v1_03_ connectivity over soft ap.
	 *
	 * @throws Exception the exception
	 */
	public  void testOnboarding_v1_03_ConnectivityOverSoftAP() throws Exception
	{
		connectToDUTInOffboardedState();

		verifyOnboardingState(OBS_STATE_PERSONAL_AP_NOT_CONFIGURED);
	}


	/**
	 * Test onboarding_v1_04_ configure wi fi with out of range value.
	 *
	 * @throws Exception the exception
	 */
	public void testOnboarding_v1_04_ConfigureWiFiWithOutOfRangeValue() throws Exception
    {
        connectToDUTInOffboardedState();
        try
        {
            onboardingHelper.callConfigureWiFi(new WifiNetworkConfig(personalAPConfig.getSsid(), personalAPConfig.getPassphrase(), "10"));

            fail("Calling configureWifi on the onboarding client interface with an invalid auth type must throw an 'org.alljoyn.Error.OutOfRange' ErrorReplyBusException");
        }
        catch (ErrorReplyBusException e)
        {
            logBusExceptionInfo(e);
            assertEquals("Wrong error received from configureWifi() method", "org.alljoyn.Error.OutOfRange", e.getErrorName());
        }

        verifyOnboardingState(OBS_STATE_PERSONAL_AP_NOT_CONFIGURED);
    }
	 
	/**
	 * Test onboarding_v1_05_ configure wi fi with wrong ssid.
	 *
	 * @throws Exception the exception
	 */
	public  void testOnboarding_v1_05_ConfigureWiFiWithWrongSSID() throws Exception
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




	/**
	 * Test onboarding_v1_06_ configure wi fi with wrong password.
	 *
	 * @throws Exception the exception
	 */
	public   void testOnboarding_v1_06_ConfigureWiFiWithWrongPassword() throws Exception
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






	/**
	 * Test onboarding_v1_07_ configure wi fi auth type of any.
	 *
	 * @throws Exception the exception
	 */
	public   void testOnboarding_v1_07_ConfigureWiFiAuthTypeOfAny() throws Exception
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





	/**
	 * Test onboarding_v1_08_ get scan info.
	 *
	 * @throws Exception the exception
	 */
	public  void testOnboarding_v1_08_GetScanInfo() throws Exception
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
				logger.addNote("DUT does not support getScanInfo() method");
			}
			else
			{

				throw e;
			}
		}
	}




	 /**
 	 * Test onboarding_v1_09_ wrong passcode.
 	 *
 	 * @throws Exception the exception
 	 */
 	public  void testOnboarding_v1_09_WrongPasscode() throws Exception
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
	            logger.debug("Received BusException when providing an incorrect password "+ be.getMessage());
	            logBusExceptionInfo(be);
	        }
	        //TODO
	        onboardingHelper.setPasscode(deviceAboutAnnouncement, VALID_DEFAULT_PASSCODE);
	    }




	 
	 /**
 	 * Test onboarding_v1_10_ authenticate after changing passcode.
 	 *
 	 * @throws Exception the exception
 	 */
 	public  void testOnboarding_v1_10_AuthenticateAfterChangingPasscode() throws Exception
	    {
	        String daemonName = "";
	        AboutAnnouncementDetails deviceAboutAnnouncement = connectToDUTInOffboardedState();

	        if (!deviceAboutAnnouncement.supportsInterface(ConfigTransport.INTERFACE_NAME))
	        {
	           logger.addNote("DUT does not support Config interface--exiting test case.");
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

	         onboardingHelper.setPasscode(deviceAboutAnnouncement, VALID_DEFAULT_PASSCODE);
	    }
	 
	 
	 
	 
	 /**
 	 * Test onboarding_v1_11_ factory reset clears configuration.
 	 *
 	 * @throws Exception the exception
 	 */
 	public   void testOnboarding_v1_11_FactoryResetClearsConfiguration() throws Exception
	    {
	        makeSureDeviceIsInOffboardedState();

	        AboutAnnouncementDetails aboutAnnouncementDetails = placeDUTInOnboardState();

	        if (!aboutAnnouncementDetails.supportsInterface(ConfigTransport.INTERFACE_NAME))
	        {
	            logger.addNote("DUT does not support Config interface--exiting test case.");
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

	        assertEquals("GetConfigurations() returns the same DeviceName as GetAboutData()", defaultDeviceName, (String) configMap.get(AboutKeys.ABOUT_DEVICE_NAME));

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
	        assertEquals("GetConfigurations() returns the same DeviceName as GetAboutData()", modifiedDeviceName, (String) configMap.get(AboutKeys.ABOUT_DEVICE_NAME));

	        try
	        {
	            logger.info("calling factory reset");
	            configClient.factoryReset();
	        }
	        catch (ErrorReplyBusException e)
	        {
	            logBusExceptionInfo(e);
	            assertEquals("Unexpected error received from factoryReset call", ALLJOYN_ERROR_FEATURE_NOT_AVAILABLE, e.getErrorName());

	            logger.addNote("Factory reset method is not supported by device");

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
	        assertEquals("GetConfigurations() returns the same DeviceName as GetAboutData()", defaultDeviceName, configMap.get(AboutKeys.ABOUT_DEVICE_NAME).toString());
	    }
	 
	 
	 
	 
	 /**
 	 * Test onboarding_v1_12_ factory reset resets passcode.
 	 *
 	 * @throws Exception the exception
 	 */
 	public   void testOnboarding_v1_12_FactoryResetResetsPasscode() throws Exception
	    {
	        String daemonName = "";
	        AboutAnnouncementDetails deviceAboutAnnouncement = connectToDUTInOffboardedState();

	        if (!deviceAboutAnnouncement.supportsInterface(ConfigTransport.INTERFACE_NAME))
	        {
	        	logger.addNote("DUT does not support Config interface--exiting test case.");
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

	            logger.addNote("Factory reset method is not supported by device");

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
	 
	 
	 



	/**
	 * Validate scan result.
	 *
	 * @param myScanResult the my scan result
	 */
	private  void validateScanResult(MyScanResult myScanResult)
	{
		validateAuthType(myScanResult.m_authType);
	}

	/**
	 * Validate auth type.
	 *
	 * @param authType the auth type
	 */
	private  void validateAuthType(short authType)
	{
		assertTrue(String.format("AuthType must not be less than %d", getMinAuthTypeId()), authType >= getMinAuthTypeId());
		assertTrue(String.format("AuthType must not be greater than %d", getMaxAuthTypeId()), authType <= getMaxAuthTypeId());
	}


	/**
	 * Log bus exception info.
	 *
	 * @param be the be
	 */
	private  void logBusExceptionInfo(BusException be)
	{
		if (be instanceof ErrorReplyBusException)
		{
			ErrorReplyBusException ex = (ErrorReplyBusException) be;
			logger.debug("ErrorStatus: " + ex.getErrorStatus());
			logger.debug("ErrorName: " + ex.getErrorName());
			logger.debug("ErrorMessage: " + ex.getErrorMessage());
		}
	}

	/**
	 * Gets the min auth type id.
	 *
	 * @return the min auth type id
	 */
	private  short getMinAuthTypeId()
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

	/**
	 * Gets the max auth type id.
	 *
	 * @return the max auth type id
	 */
	private  int getMaxAuthTypeId()
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



	/**
	 * Connect to dut in offboarded state.
	 *
	 * @return the about announcement details
	 * @throws Exception the exception
	 */
	private  AboutAnnouncementDetails connectToDUTInOffboardedState() throws Exception
	{
		makeSureDeviceIsInOffboardedState();

		onboardingHelper.connectToDUTOnSoftAP();

		return onboardingHelper.waitForAboutAnnouncementAndThenConnect();
	}





	/**
	 * Place dut in onboard state.
	 *
	 * @return the about announcement details
	 * @throws Exception the exception
	 * @throws BusException the bus exception
	 */
	public  AboutAnnouncementDetails placeDUTInOnboardState() throws Exception, BusException
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

	/**
	 * Verify onboarding error code.
	 *
	 * @param errorCode the error code
	 * @throws BusException the bus exception
	 */
	private  void verifyOnboardingErrorCode(short errorCode) throws BusException
	{
		logger.info("Retrieving the ErrorCode property from the Onboarding interface");
		OBLastError lastError = onboardingHelper.retrieveLastErrorProperty();
		assertEquals("ErrorCode property does not match expected value: "+lastError.getErrorCode() , errorCode, lastError.getErrorCode());
	}



	/**
	 * Verify configure wifi return value.
	 *
	 * @param configureWiFiRetval the configure wi fi retval
	 */
	private  void verifyConfigureWifiReturnValue(short configureWiFiRetval)
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

	/**
	 * Make sure device is in offboarded state.
	 *
	 * @throws Exception the exception
	 */
	private  void makeSureDeviceIsInOffboardedState() throws Exception
	{
		assertNotNull("Soft AP SSID can not be null. You have to provide the soft AP SSID to run the Onboarding tests", softAPConfig.getSsid());
		assertTrue("Soft AP SSID can not be empty. You have to provide the soft AP SSID to run the Onboarding tests", !softAPConfig.getSsid().isEmpty());

		if (onboardingHelper.isDeviceInOnboardedState())
		{
			logger.debug("Device currently in Onboarded state, so offboarding it");

			placeDUTInOffboardState();
		}
	}


	/**
	 * Validate soft ap.
	 *
	 * @param softAPssid2 the soft a pssid2
	 */
	private  void validateSoftAP(String softAPssid2) {
		// TODO Auto-generated method stub
		 

	}




	/**
	 * Verify onboarding state.
	 *
	 * @param state the state
	 * @throws BusException the bus exception
	 */
	private  void verifyOnboardingState(short state) throws BusException
	{
		logger.info("Retrieving the State property from the Onboarding interface");
		short actualState = onboardingHelper.retrieveStateProperty();
		
		assertEquals("State property does not match expected value", state, actualState);
	
	}

	/**
	 * Check device is in onboarded state and wait for announcement.
	 *
	 * @throws Exception the exception
	 */
	private  void checkDeviceIsInOnboardedStateAndWaitForAnnouncement() throws Exception
	{
		assertTrue("DUT does not appear to be Onboarded!", onboardingHelper.isDeviceInOnboardedState());
	}


	/**
	 * Place dut in offboard state.
	 *
	 * @throws Exception the exception
	 */
	private  void placeDUTInOffboardState() throws Exception
	{
		onboardingHelper.callOffboard();
		
		
		String ssid = onboardingHelper.connectToDUTOnSoftAP();
		if (softAPssid == null)
		{
			softAPssid = ssid;
		}

		onboardingHelper.waitForAboutAnnouncementAndThenConnect();
	}





	/**
	 * Gets the onboarding helper.
	 *
	 * @return the onboarding helper
	 */
	protected  OnboardingHelper getOnboardingHelper()
	{
		return new OnboardingHelper();
	}


	/**
	 * Fail.
	 *
	 * @param msg the msg
	 */
	private  void fail(String msg) {


		logger.error(msg);
		pass=false;

	}


	/**
	 * Assert equals.
	 *
	 * @param errorMsg the error msg
	 * @param Version the version
	 * @param version the version
	 */
	private  void assertEquals(String errorMsg,
			String Version, short version) {
		

		if(Short.parseShort(Version)!=version){
			fail(errorMsg);



		}

	}



	/**
	 * Assert equals.
	 *
	 * @param errorMsg the error msg
	 * @param i the i
	 * @param j the j
	 */
	private  void assertEquals(String errorMsg, int i, int j) {
		if(i!=j){
			fail(errorMsg);



		}

	}

	/**
	 * Assert equals.
	 *
	 * @param errorMsg the error msg
	 * @param string1 the string1
	 * @param string2 the string2
	 */
	private  void assertEquals(String errorMsg,
			String string1, String string2) {
		

		if(!string1.equals(string2)){
			fail(errorMsg);


		}

	}


	/**
	 * Assert equals.
	 *
	 * @param errorMsg the error msg
	 * @param byte1 the byte1
	 * @param byte2 the byte2
	 */
	private  void assertEquals(String errorMsg, byte byte1,
			byte byte2) {
		if(!(byte1==byte2)){
			fail(errorMsg);


		}

	}



	/**
	 * Assert equals.
	 *
	 * @param errorMsg the error msg
	 * @param bool the bool
	 * @param booleanValue the boolean value
	 */
	private  void assertEquals(String errorMsg, boolean bool,
			boolean booleanValue) {
		if(bool!=booleanValue){
			fail(errorMsg);
		}

	}

	/**
	 * Assert true.
	 *
	 * @param errorMsg the error msg
	 * @param bool the bool
	 */
	private  void assertTrue(String errorMsg,
			boolean bool) {
	
		if(!bool){
			fail(errorMsg);

		}

	}


	/**
	 * Assert false.
	 *
	 * @param errorMsg the error msg
	 * @param bool the bool
	 */
	private  void assertFalse(String errorMsg,
			boolean bool) {
		
		if(bool){
			fail(errorMsg);

		}

	}


	/**
	 * Assert not null.
	 *
	 * @param msgError the msg error
	 * @param notNull the not null
	 */
	private  void assertNotNull(String msgError, Object notNull) {

		if(notNull==null){
			fail(msgError);
		}

	}


	/**
	 * Assert null.
	 *
	 * @param msgError the msg error
	 * @param Null the null
	 */
	private  void assertNull(String msgError,
			Object Null) {
		if(Null!=null){
			fail(msgError);
		}

	}

	/**
	 * Gets the verdict.
	 *
	 * @return the verdict
	 */
	public String getVerdict() {
		String verdict=null;
		if(inconc){
			verdict="INCONC";
		}else if(pass){
			verdict="PASS";
		}else if(!pass){
			verdict="FAIL";
		}


		return verdict;
	}
}