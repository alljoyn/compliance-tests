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

public class OnBoardingService {

	private  Boolean pass=true;
	private  Boolean inconc=false;
	private  final String TAG = "OnboardingTestSuite";
	private  final WindowsLoggerImpl logger =  new WindowsLoggerImpl(TAG);
	private  int  timeOut=30;
	private  short port=91;

	private  UUID dutAppId;
	private  String dutDeviceId;
	private  String keyStorePath="/KeyStore";


	private  WifiNetworkConfig personalAPConfig;
	private  WifiNetworkConfig softAPConfig;
	private  String softAPssid;

	protected  final String INVALID_NETWORK_NAME = "InvalidPersonalAP";
	protected  final String INVALID_NETWORK_PASSPHRASE = "InvalidNetworkPassphrase";
	protected  final short INVALID_AUTH_TYPE = 9999;

	protected  long TIME_TO_WAIT_FOR_SOFT_AP_IN_MS_SHORT = 120000;
	protected  long TIME_TO_WAIT_TO_CONNECT_TO_SOFT_AP_IN_MS = 60000;
	protected  long TIME_TO_WAIT_FOR_SOFT_AP_AFTER_OFFBOARD = 15000;
	protected  long TIME_TO_WAIT_TO_CONNECT_TO_PERSONAL_AP_IN_MS = 60000;
	protected  long TIME_TO_WAIT_FOR_DISCONNECT_IN_MS = 30000;
	protected  long TIME_TO_WAIT_FOR_NEXT_DEVICE_ANNOUNCEMENT_IN_MS = 180000;

	public  final short OBS_STATE_PERSONAL_AP_NOT_CONFIGURED = OnboardingService.OnboardingState.PERSONAL_AP_NOT_CONFIGURED.getStateId();
	public  final short OBS_STATE_PERSONAL_AP_NOT_VALIDATED = OnboardingService.OnboardingState.PERSONAL_AP_CONFIGURED_NOT_VALIDATED.getStateId();
	public  final short OBS_STATE_PERSONAL_AP_CONFIGURED_VALIDATED = OnboardingService.OnboardingState.PERSONAL_AP_CONFIGURED_VALIDATED.getStateId();
	public  final short OBS_STATE_PERSONAL_AP_CONFIGURED_ERROR = OnboardingService.OnboardingState.PERSONAL_AP_CONFIGURED_ERROR.getStateId();

	public  final short OBS_LASTERROR_VALIDATED = 0;
	public  final short OBS_LASTERROR_UNREACHABLE = 1;
	public  final short OBS_LASTERROR_UNAUTHORIZED = 3;

	protected  final short OBS_CONFIGURE_WIFI_RETURN_NO_CHANNEL_SWITCHING = 1;
	protected  final short OBS_CONFIGURE_WIFI_RETURN_SUPPORTS_CHANNEL_SWITCHING = 2;

	protected  final String ALLJOYN_ERROR_FEATURE_NOT_AVAILABLE = "org.alljoyn.Error.FeatureNotAvailable";

	private  ConfigClient configClient;
	private  AboutClient aboutClient;
	private  OnboardingHelper onboardingHelper;
	private  final char[] VALID_DEFAULT_PASSCODE = SrpAnonymousKeyListener.DEFAULT_PINCODE;
	private  final char[] TEMP_PASSCODE = "111111".toCharArray();
	private  final char[] INVALID_PASSCODE = "123456".toCharArray();

	protected  final String NEW_DEVICE_NAME = "newDeviceName";

	 boolean ICSON_OnboardingServiceFramework=false;
	 boolean ICSON_OnboardingInterface=false;
	 boolean ICSON_ChannelSwitching=false;
	 boolean ICSON_GetScanInfoMethod=false;	

	//
	 String IXITCO_AppId=null;
	 String IXITCO_DefaultLanguage=null;
	 String IXITCO_DeviceId=null;
	//
	 String IXITON_OnboardingVersion=null;
	 String IXITON_SoftAP=null;
	 String IXITON_SoftAPAuthType=null;
	 String IXITON_SoftAPpassphrase=null;
	 String IXITON_PersonalAP=null;
	 String IXITON_PersonalAPAuthType=null;
	 String IXITON_PersonalAPpassphrase=null;

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




	private  void tearDown() {
		System.out.println("====================================================");
		logger.debug("test tearDown started");
		releaseResources();
		logger.debug("test tearDown done");
		System.out.println("====================================================");
	}




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

	private  void disconnectConfigClient()
	{
		if (configClient != null)
		{
			logger.debug("Disconnecting config client");
			configClient.disconnect();
			configClient = null;
		}
	}

	private  void disconnectAboutClient()
	{
		if (aboutClient != null)
		{
			logger.debug("Disconnecting about client");
			aboutClient.disconnect();
			aboutClient = null;
		}
	}




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




	public  void testOnboarding_v1_02_OnboardDevice() throws Exception
	{
		makeSureDeviceIsInOffboardedState();

		placeDUTInOnboardState();
	}



	public  void testOnboarding_v1_03_ConnectivityOverSoftAP() throws Exception
	{
		connectToDUTInOffboardedState();

		verifyOnboardingState(OBS_STATE_PERSONAL_AP_NOT_CONFIGURED);
	}


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
	 
	 
	 



	private  void validateScanResult(MyScanResult myScanResult)
	{
		validateAuthType(myScanResult.m_authType);
	}

	private  void validateAuthType(short authType)
	{
		assertTrue(String.format("AuthType must not be less than %d", getMinAuthTypeId()), authType >= getMinAuthTypeId());
		assertTrue(String.format("AuthType must not be greater than %d", getMaxAuthTypeId()), authType <= getMaxAuthTypeId());
	}


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



	private  AboutAnnouncementDetails connectToDUTInOffboardedState() throws Exception
	{
		makeSureDeviceIsInOffboardedState();

		onboardingHelper.connectToDUTOnSoftAP();

		return onboardingHelper.waitForAboutAnnouncementAndThenConnect();
	}





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

	private  void verifyOnboardingErrorCode(short errorCode) throws BusException
	{
		logger.info("Retrieving the ErrorCode property from the Onboarding interface");
		OBLastError lastError = onboardingHelper.retrieveLastErrorProperty();
		assertEquals("ErrorCode property does not match expected value: "+lastError.getErrorCode() , errorCode, lastError.getErrorCode());
	}



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


	private  void validateSoftAP(String softAPssid2) {
		// TODO Auto-generated method stub
		 

	}




	private  void verifyOnboardingState(short state) throws BusException
	{
		logger.info("Retrieving the State property from the Onboarding interface");
		short actualState = onboardingHelper.retrieveStateProperty();
		
		assertEquals("State property does not match expected value", state, actualState);
	
	}

	private  void checkDeviceIsInOnboardedStateAndWaitForAnnouncement() throws Exception
	{
		assertTrue("DUT does not appear to be Onboarded!", onboardingHelper.isDeviceInOnboardedState());
	}


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





	protected  OnboardingHelper getOnboardingHelper()
	{
		return new OnboardingHelper();
	}


	private  void fail(String msg) {


		logger.error(msg);
		pass=false;

	}


	private  void assertEquals(String errorMsg,
			String Version, short version) {
		

		if(Short.parseShort(Version)!=version){
			fail(errorMsg);



		}

	}



	private  void assertEquals(String errorMsg, int i, int j) {
		if(i!=j){
			fail(errorMsg);



		}

	}

	private  void assertEquals(String errorMsg,
			String string1, String string2) {
		

		if(!string1.equals(string2)){
			fail(errorMsg);


		}

	}


	private  void assertEquals(String errorMsg, byte byte1,
			byte byte2) {
		if(!(byte1==byte2)){
			fail(errorMsg);


		}

	}



	private  void assertEquals(String errorMsg, boolean bool,
			boolean booleanValue) {
		if(bool!=booleanValue){
			fail(errorMsg);
		}

	}

	private  void assertTrue(String errorMsg,
			boolean bool) {
	
		if(!bool){
			fail(errorMsg);

		}

	}


	private  void assertFalse(String errorMsg,
			boolean bool) {
		
		if(bool){
			fail(errorMsg);

		}

	}


	private  void assertNotNull(String msgError, Object notNull) {

		if(notNull==null){
			fail(msgError);
		}

	}


	private  void assertNull(String msgError,
			Object Null) {
		if(Null!=null){
			fail(msgError);
		}

	}

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
