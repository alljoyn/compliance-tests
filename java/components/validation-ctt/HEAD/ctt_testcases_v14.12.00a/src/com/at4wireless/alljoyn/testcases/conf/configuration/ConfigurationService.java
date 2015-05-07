package com.at4wireless.alljoyn.testcases.conf.configuration;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.alljoyn.about.AboutKeys;
import org.alljoyn.about.client.AboutClient;
import org.alljoyn.bus.BusException;
import org.alljoyn.bus.ErrorReplyBusException;
import org.alljoyn.config.client.ConfigClient;

import org.alljoyn.onboarding.transport.OnboardingTransport;

import com.at4wireless.alljoyn.core.about.AboutAnnouncementDetails;
import com.at4wireless.alljoyn.core.commons.AllJoynErrorReplyCodes;
import com.at4wireless.alljoyn.core.commons.ServiceAvailabilityHandler;
import com.at4wireless.alljoyn.core.commons.ServiceHelper;
import com.at4wireless.alljoyn.core.commons.SrpAnonymousKeyListener;
import com.at4wireless.alljoyn.core.commons.UserInputDetails;
import com.at4wireless.alljoyn.core.commons.log.Logger;
import com.at4wireless.alljoyn.core.commons.log.WindowsLoggerImpl;
import com.at4wireless.alljoyn.core.introspection.BusIntrospector;


public class ConfigurationService {


	boolean pass=true;
	boolean inconc=false;
	private  AboutClient aboutClient;
	private  ConfigClient configClient = null;
	private  ServiceAvailabilityHandler serviceAvailabilityHandler;
	private  AboutAnnouncementDetails deviceAboutAnnouncement;
	private  ServiceHelper serviceHelper; 
	private  BusIntrospector busIntrospector;

	private  String  keyStorePath="/KeyStore";
	private  UUID dutAppId;
	private  String dutDeviceId;
	private  int  timeOut=30;

	private  long aboutAnnouncementTimeout;

	private  final String BUS_APPLICATION_NAME = "ConfigTestSuite";
	private  final String TAG = "ConfigTestSuite";
	private  final WindowsLoggerImpl logger =  new WindowsLoggerImpl(TAG);

	private  final String NEW_DEVICE_NAME = "newDeviceName";
	private  final String INVALID_LANGUAGE_CODE = "INVALID";
	private  final String[] DEVICE_NAME_FIELD = new String[]
			{ AboutKeys.ABOUT_DEVICE_NAME };
	private  final String[] DEFAULT_LANGUAGE_FIELD = new String[]
			{ AboutKeys.ABOUT_DEFAULT_LANGUAGE };
	private  final String[] BOTH_FIELDS = new String[]
			{ AboutKeys.ABOUT_DEVICE_NAME, AboutKeys.ABOUT_DEFAULT_LANGUAGE };
	private  final String[] INVALID_FIELD = new String[]
			{ "INVALID" };
	private  final char[] NEW_PASSCODE = "111111".toCharArray();
	private  final char[] SINGLE_CHAR_PASSCODE = "1".toCharArray();
	private  final char[] DEFAULT_PINCODE = SrpAnonymousKeyListener.DEFAULT_PINCODE;
	private  final char[] SPECIAL_CHARS_PASSCODE = "!@#$%^".toCharArray();

	public long SESSION_LOST_TIMEOUT_IN_SECONDS = 30;
	private long SESSION_CLOSE_TIMEOUT_IN_SECONDS = 5;

	Map<String,Boolean> ics;
	Map<String,String> ixit;

	public ConfigurationService(String testCase,
			boolean iCSCF_ConfigurationServiceFramework,
			boolean iCSCF_ConfigurationInterface,
			boolean iCSCF_FactoryResetMethod, String iXITCO_AppId,
			String iXITCO_DeviceId, String iXITCO_DefaultLanguage,
			String iXITCF_ConfigVersion, String iXITCF_Passcode,
			String gPCO_AnnouncementTimeout, String gPCF_SessionLost,
			String gPCF_SessionClose) {

		ics = new HashMap<String,Boolean>();
		ics.put("ICSCF_ConfigurationServiceFramework",iCSCF_ConfigurationServiceFramework);
		ics.put("ICSCF_ConfigurationInterface",iCSCF_ConfigurationInterface);
		ics.put("ICSCF_FactoryResetMethod",iCSCF_FactoryResetMethod);
		
		ixit = new HashMap<String,String>();
		ixit.put("IXITCO_AppId",iXITCO_AppId);
		//ixit.put("IXITCO_DefaultLanguage",iXITCO_DefaultLanguage);
		ixit.put("IXITCO_DeviceId",iXITCO_DeviceId);
		ixit.put("IXITCF_ConfigVersion",iXITCF_ConfigVersion);
		ixit.put("IXITCF_Passcode", iXITCF_Passcode);

		timeOut = Integer.parseInt(gPCO_AnnouncementTimeout);
		SESSION_LOST_TIMEOUT_IN_SECONDS = Integer.parseInt(gPCF_SessionLost);
		SESSION_CLOSE_TIMEOUT_IN_SECONDS = Integer.parseInt(gPCF_SessionClose);

		try {
			runTestCase(testCase);
		} catch(Exception e) {
			if(e!=null){				
				if(e.getMessage().equals("Timed out waiting for About announcement")){
					fail("Timed out waiting for About announcement");
					logger.error("Timed out waiting for About announcement");
				}else{
					String errorMsg = "Exception: "+e.toString();
					inconc(errorMsg);
				}
			}
		}
	}

	public  void runTestCase(String testCase) throws Exception{
		setUp();
		logger.info("Running testcase: "+testCase);
		if(testCase.equals("Config-v1-01")){
			testConfig_v1_01AppIdEqualsDeviceId();
		}else if(testCase.equals("Config-v1-02")){
			testConfig_v1_02ConnectWithWrongPasscode();
		/*}else if(testCase.equals("Config-v1-03")){
			testConfig_v1_03_ValidateVersion();*/
		}else if(testCase.equals("Config-v1-04")){
			testConfig_v1_04GetConfigurationsWithDefaultLanguage();
		}else if(testCase.equals("Config-v1-05")){
			testConfig_v1_05UnspecifiedLanguage();
		}else if(testCase.equals("Config-v1-06")){
			testConfig_v1_06LangConsistence();
		}else if(testCase.equals("Config-v1-07")){
			testConfig_v1_07UnsupportedLanguage();
		}else if(testCase.equals("Config-v1-08")){
			testConfig_v1_08UpdateConfigurationsWithANewDeviceName();
		}else if(testCase.equals("Config-v1-12")){
			testConfig_v1_12DeviceNameSpecial();
		}else if(testCase.equals("Config-v1-13")){
			testConfig_v1_13UpdateUnsupportedLanguage();
		}else if(testCase.equals("Config-v1-14")){
			testConfig_v1_14UpdateDefaultLang();
		}else if(testCase.equals("Config-v1-15")){
			testConfig_v1_15UpdateDefaultLanguageToUnsupportedLanguage();
		}else if(testCase.equals("Config-v1-16")){
			testConfig_v1_16TestChangetoUnspecifiedLanguage();	
		}else if(testCase.equals("Config-v1-19")){
			testConfig_v1_19TestUpdateInvalidField();
		}else if(testCase.equals("Config-v1-20")){
			testConfig_v1_20TestResetDeviceName();
		}else if(testCase.equals("Config-v1-21")){
			testConfig_v1_21ResetDefaultLanguage();
		}else if(testCase.equals("Config-v1-22")){
			testConfig_v1_22ResetDefaultMultiLanguage();		
		}else if(testCase.equals("Config-v1-24")){
			testConfig_v1_24FailResetUnsupportedLang();
		}else if(testCase.equals("Config-v1-25")){
			testConfig_v1_25FailResetInvalidField();
		}else if(testCase.equals("Config-v1-26")){
			testConfig_v1_26DeviceRestart();
		}else if(testCase.equals("Config-v1-27")){
			testConfig_v1_27DeviceRestartRememberConfData();
		}else if(testCase.equals("Config-v1-29")){
			testConfig_v1_29PasscodeChanged();
		}else if(testCase.equals("Config-v1-30")){
			testConfig_v1_30PasscodeChangedSingleChar();
		}else if(testCase.equals("Config-v1-31")){
			testConfig_v1_31PasscodeChangedSpecialChars();
		}else if(testCase.equals("Config-v1-32")){
			testConfig_v1_32PasscodeChangedPersistOnRestart();
		}else if(testCase.equals("Config-v1-33")){
			testConfig_v1_33FactoryResetNoUpdateConfiguratins();
		/*}else if(testCase.equals("Config-v1-34")){
			testConfig_v1_34FactoryResetAfterUpdateConfigurations();
		}else if(testCase.equals("Config-v1-35")){
			testConfig_v1_35FactoryResetResetsPasscode();*/ //Not valid TCs in 14.12
		}else{
			fail("TestCase not valid");
		}
		tearDown();

	}
	
	protected  void setUp() throws Exception
	{


		System.out.println("====================================================");
		logger.info("test setUp started");

		try
		{
			dutDeviceId = ixit.get("IXITCO_DeviceId");
			logger.info(String.format("Running Config test case against Device ID: %s", dutDeviceId));
			dutAppId = UUID.fromString(ixit.get("IXITCO_AppId"));
			logger.info(String.format("Running Config test case against App ID: %s", dutAppId));
			keyStorePath="/KeyStore";
			logger.info(String.format("Running Config test case using KeyStorePath: %s", keyStorePath));
			aboutAnnouncementTimeout = timeOut;

			initServiceHelper();
			resetPasscodeIfNeeded();

			logger.info("test setUp done");
		}
		catch (Exception e)
		{
			inconc("test setUp thrown an exception: "+e.toString());
			releaseResources();
			throw e;
		}

		System.out.println("====================================================");
	}
	
	protected  void tearDown() throws Exception
	{
		System.out.println("====================================================");
		logger.info("test tearDown started");
		releaseResources();
		logger.info("test tearDown done");
		System.out.println("====================================================");
	}
	
	/*TestCases*/

	public  void testConfig_v1_01AppIdEqualsDeviceId() throws Exception
	{
		UUID dutAppId = deviceAboutAnnouncement.getAppId();
		String dutDeviceId = deviceAboutAnnouncement.getDeviceId();

		if (isAppIdEqualToDeviceId(dutAppId, dutDeviceId))
		{
			logger.info(String.format("System App AppId is equal to the DeviceId"));
			logger.info("Partial Verdict: PASS");
		}
		else
		{
			fail(String.format("System App AppId: %s is not equal to DeviceId: %s", dutAppId.toString().replace("-", ""), dutDeviceId.replace("-", "")));
		}
	}

	public  void testConfig_v1_02ConnectWithWrongPasscode() throws Exception
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
			logger.info("Exception details:"+ be);
			logger.info("Checking if authentication was attempted");
			assertTrue("A call to a Config interface method must require authentication", serviceHelper.isPeerAuthenticationAttempted(deviceAboutAnnouncement));
			logger.info("Checking if authentication was not successful");
			assertFalse("A call to a Config interface method with the wrong passcode must fail authentication",
					serviceHelper.isPeerAuthenticationSuccessful(deviceAboutAnnouncement));
		}
		if (!exceptionThrown)
		{
			logger.info("Expected exception not thrown");
			fail("A call to a Config interface method with the wrong passcode must return an error");
		}
	}


	/*public  void testConfig_v1_03_ValidateVersion() throws Exception
	{
		short version = callGetVersionOnConfig();
		assertEquals("Config version does not match", ixit.get("IXITCF_ConfigVersion"), version);
	}*/



	public  void testConfig_v1_04GetConfigurationsWithDefaultLanguage() throws Exception
	{
		logger.info("Partial Verdict: PASS");
		Map<String, Object> configMap = callGetConfigurationsWithDefaultLanguage();
		checkMapForRequiredFields(configMap);
		checkConsistencyWithAboutAnnouncement(configMap);
	}


	public  void testConfig_v1_05UnspecifiedLanguage() throws Exception
	{
		Map<String, Object> configMapWithDefaultLanguage = callGetConfigurationsWithDefaultLanguage();
		Map<String, Object> configMapWithUnspecifiedLanguage = callGetConfigurations("");
		
		logger.info("Checking received parameters when default language");
		checkMapForRequiredFields(configMapWithDefaultLanguage);
		logger.info("Checking received parameters when unspecified language");
		checkMapForRequiredFields(configMapWithUnspecifiedLanguage);

		logger.info("Checking that DeviceName and DefaultLanguage from the two GetConfigurations() calls match");
		compareMaps(configMapWithDefaultLanguage, configMapWithUnspecifiedLanguage);
	}



	public  void testConfig_v1_06LangConsistence() throws Exception
	{
		Map<String, Object> aboutMap = callGetAboutForDefaultLanguage();

		String[] suppLangs = getSupportedLanguages(aboutMap);

		if (suppLangs.length > 1)
		{
			for (String lang : suppLangs)
			{
				Map<String, Object> configMapForLang = callGetConfigurations(lang);
				//Map<String, Object> aboutMapForLang = callGetAbout(lang);
				checkMapForRequiredFields(configMapForLang);
				
				Map<String, Object> aboutMapForLang = callGetAbout(lang);
				checkMapForRequiredFields(aboutMapForLang);

				logger.info(String.format("Comparing config and about maps for the language: %s", lang));
				compareMaps(configMapForLang, aboutMapForLang);
			}
		}
		else
		{
			logger.addNote("Only one language is supported");
		}
	}




	public  void testConfig_v1_07UnsupportedLanguage() throws Exception
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
			logger.info("Received exception from GetConfigurations() with INVALID language"+ erbe);

			assertEquals("Calling GetConfigurations() on the Config interface with an unsupported language did not return the expected error",
					AllJoynErrorReplyCodes.LANGUAGE_NOT_SUPPORTED, erbe.getErrorName());
		}
		if (!exceptionThrown)
		{
			fail(String.format("Calling GetConfigurations() on the Config interface with an unsupported language did not return an error, it must return an %s error",
					expectedError));
		}
	}


	public  void testConfig_v1_08UpdateConfigurationsWithANewDeviceName() throws Exception
	{
		testUpdateConfigurations(AboutKeys.ABOUT_DEVICE_NAME, NEW_DEVICE_NAME);
	}



	public  void testConfig_v1_12DeviceNameSpecial() throws Exception
	{
		testUpdateConfigurations(AboutKeys.ABOUT_DEVICE_NAME, getDeviceNameWithSpecialCharacters());
	}


	public  void testConfig_v1_13UpdateUnsupportedLanguage() throws Exception
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
			logger.info("Received exception from UpdateConfigurations() with INVALID language", erbe);

			assertEquals("Calling UpdateConfigurations() on the Config interface with an unsupported language did not return the expected error",
					AllJoynErrorReplyCodes.LANGUAGE_NOT_SUPPORTED, erbe.getErrorName());
		}
		if (!exceptionThrown)
		{
			fail("Calling UpdateConfigurations() on the Config interface with an unsupported language must return a LanguageNotSupported error");
		}

	}


	public  void testConfig_v1_14UpdateDefaultLang() throws Exception
	{
		String newLang = getAnotherSupportedLanguage();
		if (newLang != null)
		{
			testUpdateConfigurations(AboutKeys.ABOUT_DEFAULT_LANGUAGE, newLang);
		}
		else
		{
			logger.addNote("Only one language is supported");
		}
	}




	public  void testConfig_v1_15UpdateDefaultLanguageToUnsupportedLanguage() throws Exception
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
			logger.info("Received exception from UpdateConfigurations() with unsupported language", ex);

			assertEquals("Calling UpdateConfigurations() on the Config interface to set the DefaultLanguage to an unsupported language did not return the expected error",
					AllJoynErrorReplyCodes.LANGUAGE_NOT_SUPPORTED, ex.getErrorName());
		}
		if (!exceptionThrown)
		{
			fail(String.format("Calling UpdateConfigurations() to set the DefaultLanguage to an unsupported language must return an error, %s",
					AllJoynErrorReplyCodes.LANGUAGE_NOT_SUPPORTED));
		}
	}



	public  void testConfig_v1_16TestChangetoUnspecifiedLanguage() throws Exception
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
			logger.info("Received exception from UpdateConfigurations() with DefaultLanguage set to an unspecified language", erbe);

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

	public  void testConfig_v1_19TestUpdateInvalidField() throws Exception
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

			logger.info("Received an ErrorReplyBusException", ex);

			assertEquals("Calling UpdateConfigurations() to update an invalid field did not return the expected error", AllJoynErrorReplyCodes.INVALID_VALUE, ex.getErrorName());
		}
		if (!exceptionThrown)
		{
			fail(String.format("Calling UpdateConfigurations() on the Config interface to update an invalid field did not return an error, it must return an %s error",
					expectedError));
		}
	}




	public  void testConfig_v1_20TestResetDeviceName() throws Exception
	{
		String defaultLanguage = deviceAboutAnnouncement.getDefaultLanguage();

		callResetConfigurations(defaultLanguage, DEVICE_NAME_FIELD);
		deviceAboutAnnouncement = waitForNextAnnouncementAndCheckFieldValue(AboutKeys.ABOUT_DEFAULT_LANGUAGE, defaultLanguage);

		Map<String, Object> aboutMap = callGetAboutForDefaultLanguage();
		String originalDeviceName = (String) aboutMap.get(AboutKeys.ABOUT_DEVICE_NAME);

		logger.info(String.format("Original Device Name: %s", originalDeviceName));

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

	public  void testConfig_v1_21ResetDefaultLanguage() throws Exception
	{
		String defaultLanguage = deviceAboutAnnouncement.getDefaultLanguage();

		callResetConfigurations(defaultLanguage, DEFAULT_LANGUAGE_FIELD);

		Map<String, Object> configMap = callGetConfigurationsWithDefaultLanguage();
		Map<String, Object> aboutMap = callGetAboutForDefaultLanguage();

		compareMapsForField(AboutKeys.ABOUT_DEFAULT_LANGUAGE, aboutMap, configMap);
	}


	public  void testConfig_v1_22ResetDefaultMultiLanguage() throws Exception
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

			logger.info(String.format("Original DefaultLanguage: %s", originalDefaultLanguage));

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
			logger.addNote("Only one language is supported");
		}
	}

	public  void testConfig_v1_24FailResetUnsupportedLang() throws Exception
	{
		boolean exceptionThrown = false;
		try
		{
			callResetConfigurations(INVALID_LANGUAGE_CODE, DEVICE_NAME_FIELD);
		}
		catch (ErrorReplyBusException erbe)
		{
			exceptionThrown = true;
			logger.info("Received exception from ResetConfigurations() with an unsupported language", erbe);

			assertEquals("Calling ResetConfigurations() with an unsupported language did not return the expected error", AllJoynErrorReplyCodes.LANGUAGE_NOT_SUPPORTED,
					erbe.getErrorName());
		}
		if (!exceptionThrown)
		{
			fail(String.format("Calling ResetConfigurations() with an unsupported language must return an error, %s", AllJoynErrorReplyCodes.LANGUAGE_NOT_SUPPORTED));
		}
	}

	public  void testConfig_v1_25FailResetInvalidField() throws Exception
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
			logger.info("Received exception from ResetConfigurations() with an invalid field", erbe);

			assertEquals("Calling ResetConfigurations() with an invalid field did not return the expected error", AllJoynErrorReplyCodes.INVALID_VALUE, erbe.getErrorName());
		}
		if (!exceptionThrown)
		{
			fail(String.format("Calling ResetConfigurations() with an invalid field must return an error, %s", AllJoynErrorReplyCodes.INVALID_VALUE));
		}
	}

	public  void testConfig_v1_26DeviceRestart() throws Exception
	{
		reconnectClients();
		callRestartOnConfig();
		//TODO not ready
		//assertTrue("Timed out waiting for session to be lost", ServiceAvailabilityHandler.waitForSessionLost(SESSION_LOST_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS));

		deviceAboutAnnouncement = waitForNextDeviceAnnouncement();

		connectAboutClient(deviceAboutAnnouncement);
	}

	
	public  void testConfig_v1_27DeviceRestartRememberConfData() throws Exception
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


	public  void testConfig_v1_29PasscodeChanged() throws Exception
	{
		testChangePasscode(NEW_PASSCODE);
	}


	public  void testConfig_v1_30PasscodeChangedSingleChar() throws Exception
	{
		testChangePasscode(SINGLE_CHAR_PASSCODE);
	}


	public  void testConfig_v1_31PasscodeChangedSpecialChars() throws Exception
	{
		testChangePasscode(SPECIAL_CHARS_PASSCODE);
	}


	public  void testConfig_v1_32PasscodeChangedPersistOnRestart() throws Exception
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



	public  void testConfig_v1_33FactoryResetNoUpdateConfiguratins() throws Exception
	{
		String deviceNameBeforeReset = null;
		String defaultLanguageBeforeReset = null;
		if (deviceAboutAnnouncement.supportsInterface(OnboardingTransport.INTERFACE_NAME))
		{
			logger.addNote("The device supports Onboarding so this Test Case is Not Applicable");

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
				fail("FactoryReset method is not a supported feature!");
			}
			else
			{
				assertTrue("Timed out waiting for session to be lost", serviceAvailabilityHandler.waitForSessionLost(SESSION_LOST_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS));

				UserInputDetails userInputDetails = createUserInputDetails();
				// always continue, we ignore the response here
				// getValidationTestContext().waitForUserInput(userInputDetails);

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



	/*public  void testConfig_v1_34FactoryResetAfterUpdateConfigurations() throws Exception
	{
		String defaultDeviceName = null;
		if (deviceAboutAnnouncement.supportsInterface(OnboardingTransport.INTERFACE_NAME))
		{
			logger.addNote("The device supports Onboarding so this Test Case is Not Applicable");

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
				fail("FactoryReset method is not a supported feature!");

				updateConfigurationsAndVerifyResult(AboutKeys.ABOUT_DEVICE_NAME, defaultDeviceName);
			}
			else
			{
				assertTrue("Timed out waiting for session to be lost", serviceAvailabilityHandler.waitForSessionLost(SESSION_LOST_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS));

				UserInputDetails userInputDetails = createUserInputDetails();
				// always continue, we ignore the response here


				deviceAboutAnnouncement = waitForNextDeviceAnnouncement();

				reconnectClients();

				configMap = callGetConfigurations("");
				aboutMap = callGetAbout("");
				verifyValueForAboutAndConfig(aboutMap, configMap, AboutKeys.ABOUT_DEVICE_NAME, defaultDeviceName);
			}
		}
	}*/ 




	/*public  void  testConfig_v1_35FactoryResetResetsPasscode() throws Exception
	{
		if (deviceAboutAnnouncement.supportsInterface(OnboardingTransport.INTERFACE_NAME))
		{
			logger.addNote("The device supports Onboarding so this Test Case is Not Applicable");
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
				fail("FactoryReset method is not a supported feature!");

				changePasscodeAndReconnect(DEFAULT_PINCODE);
			}
			else
			{
				assertTrue("Timed out waiting for session to be lost", serviceAvailabilityHandler.waitForSessionLost(SESSION_LOST_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS));

				UserInputDetails userInputDetails = createUserInputDetails();
				// always continue, we ignore the response here


				deviceAboutAnnouncement = waitForNextDeviceAnnouncement();

				reconnectClients();
				serviceHelper.clearKeyStore();

				serviceHelper.setAuthPassword(deviceAboutAnnouncement, DEFAULT_PINCODE);

				callMethodToCheckAuthentication();
			}
		}

	}*/


	/*
	 *  Utilities
	 */

	protected  UserInputDetails createUserInputDetails()
	{
		String[] msg={"Continue"};
		return new UserInputDetails("Onboard DUT (if needed)",
				"FactoryReset() has been called on the DUT. Please Onboard the device to the Personal AP (if needed) and then click Continue", msg);
	}


	private  void callFactoryResetOnConfig() throws BusException
	{
		logger.info("Calling FactoryReset() on Config");
		configClient.factoryReset();
	}



	private  void testChangePasscode(char[] newPasscode) throws BusException, Exception
	{
		changePasscodeAndReconnect(newPasscode);

		changePasscodeAndReconnect(DEFAULT_PINCODE);
	}


	private  void callRestartOnConfig() throws BusException
	{
		logger.info("Calling Restart() on Config");
		configClient.restart();
	}


	private  void callResetConfigurations(String languageTag, String[] fields) throws BusException
	{
		logger.info(String.format("Calling ResetConfigurations() with language: \"%s\" and %s", languageTag, Arrays.toString(fields)));
		configClient.ResetConfigurations(languageTag, fields);
	}

	private  String getAnotherSupportedLanguage() throws BusException
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

	protected  String getDeviceNameWithSpecialCharacters()
	{
		StringBuilder builder = new StringBuilder();
		appendChars(builder, 33, 47);
		appendChars(builder, 58, 64);
		appendChars(builder, 91, 96);
		appendChars(builder, 123, 126);
		return builder.toString();
	}


	private  void appendChars(StringBuilder builder, int startIdx, int endIdx)
	{
		for (int asciiCode = startIdx; asciiCode < endIdx; asciiCode++)
		{
			builder.append((char) asciiCode);
		}
	}

	private  void testUpdateConfigurations(String fieldName, String newFieldValue) throws BusException, Exception
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



	private  void updateConfigurationsAndVerifyResult(String fieldName, String newFieldValue) throws BusException, Exception
	{
		String defaultLanguage = deviceAboutAnnouncement.getDefaultLanguage();
		Map<String, Object> mapConfig = new HashMap<String, Object>();
		mapConfig.put(fieldName, newFieldValue);
		callUpdateConfigurations(defaultLanguage, mapConfig);
		watiForNextAnnouncementAndVerifyFieldValue(fieldName, newFieldValue);
	}

	private  void callUpdateConfigurations(String languageTag, Map<String, Object> configMap) throws BusException
	{
		logger.info(String.format("Calling UpdateConfigurations() with language: \"%s\" and %s", languageTag, configMap));
		configClient.setConfig(configMap, languageTag);
	}


	private  void watiForNextAnnouncementAndVerifyFieldValue(String fieldName, String newFieldValue) throws Exception, BusException
	{
		deviceAboutAnnouncement = waitForNextAnnouncementAndCheckFieldValue(fieldName, newFieldValue);

		Map<String, Object> configMap = callGetConfigurationsWithDefaultLanguage();
		Map<String, Object> aboutMap = callGetAboutForDefaultLanguage();
		verifyValueForAboutAndConfig(aboutMap, configMap, fieldName, newFieldValue);
	}

	private  void verifyValueForAboutAndConfig(Map<String, Object> aboutMap, Map<String, Object> configMap, String key, String verifyValue)
	{
		logger.info("Checking if "+key+" from GetConfigurations() matches expected value");
		assertEquals(String.format("Value for %s retrieved from GetConfigurations() does not match expected value", key), verifyValue,(String)  configMap.get(key));
		logger.info("Checking if "+key+" from GetAboutData() matches expected value");
		assertEquals(String.format("Value for %s retrieved from GetAboutData() does not match expected value", key), verifyValue, (String) aboutMap.get(key));
	}

	protected  AboutAnnouncementDetails waitForNextAnnouncementAndCheckFieldValue(String fieldName, String fieldValue) throws Exception
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


	private  Map<String, Object> callGetAboutForDefaultLanguage() throws BusException
	{
		String defaultLanguage = deviceAboutAnnouncement.getDefaultLanguage();
		return callGetAbout(defaultLanguage);
	}

	private  Map<String, Object> callGetAbout(String language) throws BusException
	{
		logger.info(String.format("Calling getAboutData() with the language: \"%s\"", language));
		return aboutClient.getAbout(language);
	}

	private  String[] getSupportedLanguages(Map<String, Object> aboutMap)
	{
		String[] suppLangs = (String[]) aboutMap.get(AboutKeys.ABOUT_SUPPORTED_LANGUAGES);
		logger.info(String.format("Supported languages: %s", Arrays.toString(suppLangs)));
		return suppLangs;
	}

	private  void compareMaps(Map<String, Object> map1, Map<String, Object> map2) throws Exception
	{
		logger.info("Comparing DeviceName");
		compareMapsForField(AboutKeys.ABOUT_DEVICE_NAME, map1, map2);
		logger.info("Comparing DefaultLanguage");
		compareMapsForField(AboutKeys.ABOUT_DEFAULT_LANGUAGE, map1, map2);
	}

	private  void compareMapsForField(String fieldName, Map<String, Object> map1, Map<String, Object> map2) throws Exception
	{
		logger.info("Comparing GetConfigurations() and GetAbout() "+fieldName+" field");
		assertEquals(String.format("%s does not match", fieldName), (String) map1.get(fieldName),(String) map2.get(fieldName));
	}


	private  Map<String, Object> callGetConfigurationsWithDefaultLanguage() throws BusException
	{
		String defaultLanguage = deviceAboutAnnouncement.getDefaultLanguage();
		Map<String, Object> configMap = callGetConfigurations(defaultLanguage);
		return configMap;
	}

	private  void checkMapForRequiredFields(Map<String, Object> map)
	{
		logger.info("Checking that DeviceName field is present");
		assertTrue("Required DeviceName field not present in map", map.containsKey(AboutKeys.ABOUT_DEVICE_NAME));
		logger.info("Checking that DefaultLanguage field is present");
		assertTrue("Required DefaultLanguage field not present in map", map.containsKey(AboutKeys.ABOUT_DEFAULT_LANGUAGE));
	}

	private  void checkConsistencyWithAboutAnnouncement(Map<String, Object> configMap) throws BusException
	{
		logger.info("Checking that DeviceName and DefaultLanguage from GetConfigurations() matches the values in About announcemment");
		assertEquals("DeviceName from GetConfigurations() does not match About announcement", deviceAboutAnnouncement.getDeviceName(), (String) configMap.get(AboutKeys.ABOUT_DEVICE_NAME));
		logger.info("Checking that DefaultLanguage from GetConfigurations() matches About announcement");
		assertEquals("DefaultLanguage from GetConfigurations() does not match About announcement", deviceAboutAnnouncement.getDefaultLanguage(),
				(String) configMap.get(AboutKeys.ABOUT_DEFAULT_LANGUAGE));
		/*if(pass){
			logger.info("Partial Verdict: PASS");

		}*/
	}

	private  short callGetVersionOnConfig() throws BusException
	{
		short version = configClient.getVersion();
		logger.info(String.format("Call to getVersion() returns: %d", version));
		return version;
	}


	private  boolean isAppIdEqualToDeviceId(UUID dutAppId, String dutDeviceId)
	{
		logger.info(String.format("Comparing DeviceId: %s to AppId: %s", dutDeviceId, dutAppId.toString().replace("-", "")));
		return dutDeviceId.equals(dutAppId.toString().replace("-", ""));
	}

	protected  void initServiceHelper() throws BusException, Exception
	{
		releaseServiceHelper();
		serviceHelper = createServiceHelper();

		serviceHelper.initialize(BUS_APPLICATION_NAME, dutDeviceId, dutAppId);

		serviceHelper.startConfigClient();

		deviceAboutAnnouncement = waitForNextDeviceAnnouncement();

		logger.info("Partial Verdict: PASS");

		connectAboutClient(deviceAboutAnnouncement);
		connectConfigClient(deviceAboutAnnouncement);

		logger.info("Partial Verdict: PASS");

		serviceHelper.enableAuthentication(keyStorePath);
	}

	protected  ServiceHelper createServiceHelper()
	{
		return new ServiceHelper(logger);
	}


	private  void releaseResources() throws Exception
	{
		releaseServiceHelper();
	} 

	private  void releaseServiceHelper() throws Exception
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

	private  void reconnectClients() throws Exception
	{
		releaseServiceHelper();
		initServiceHelper();
	}

	private  void connectConfigClient(AboutAnnouncementDetails aboutAnnouncement) throws Exception
	{
		configClient = serviceHelper.connectConfigClient(aboutAnnouncement);
	}

	private  void connectAboutClient(AboutAnnouncementDetails aboutAnnouncement) throws Exception
	{
		serviceAvailabilityHandler = createServiceAvailabilityHandler();
		aboutClient = serviceHelper.connectAboutClient(aboutAnnouncement, serviceAvailabilityHandler);
	}

	protected  ServiceAvailabilityHandler createServiceAvailabilityHandler()
	{
		return new ServiceAvailabilityHandler();
	}

	private  AboutAnnouncementDetails waitForNextDeviceAnnouncement() throws Exception
	{
		logger.info("Waiting for About announcement");
		return serviceHelper.waitForNextDeviceAnnouncement(aboutAnnouncementTimeout, TimeUnit.SECONDS, true);
	}

	private  void waitForSessionToClose() throws Exception 
	{
		logger.info("Waiting for session to close");

		serviceHelper.waitForSessionToClose(SESSION_CLOSE_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS);

	}

	private  void resetPasscodeIfNeeded() throws Exception
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



	private  void setPasscode(char[] pwd) throws Exception
	{
		serviceHelper.clearKeyStore();
		serviceHelper.setAuthPassword(deviceAboutAnnouncement, pwd);
		callMethodToCheckAuthentication();
		changePasscodeAndReconnect(DEFAULT_PINCODE);
	}

	private  void changePasscodeAndReconnect(char[] newPasscode) throws BusException, Exception
	{
		callSetPasscodeOnConfig(newPasscode);

		reconnectClients();
		serviceHelper.clearKeyStore();

		serviceHelper.setAuthPassword(deviceAboutAnnouncement, newPasscode);

		callMethodToCheckAuthentication();
	}

	private  void callSetPasscodeOnConfig(char[] newPasscode) throws BusException
	{
		String realm = "";
		logger.info(String.format("Calling SetPasscode() on Config with realm: %s; and passcode: %s", realm, Arrays.toString(newPasscode)));
		configClient.setPasscode(realm, newPasscode);

	}

	private short callMethodToCheckAuthentication() throws BusException
	{
		//callGetConfigurations("");
		short version = configClient.getVersion();
		logger.info(String.format("Call to getVersion() returns: %d",
		version));
		return version;
	}



	private  Map<String, Object> callGetConfigurations(String languageTag) throws BusException
	{
		logger.info(String.format("Calling GetConfigurations() with language: \"%s\"", languageTag));
		Map<String, Object> configMap = configClient.getConfig(languageTag);
		return configMap;
	}


	/*
	 * Verdicts
	 */
	

	private  void fail(String msg) {
		logger.error(msg);
		logger.info("Partial Verdict: FAIL");
		pass=false;

	}

	private void inconc(String msg) {
		logger.error(msg);
		logger.info("Partial Verdict: INCONC");
		inconc = true;
	}

	private  void assertEquals(String errorMsg,
			String string1, String string2) {
		if(!string1.equals(string2)){
			fail(errorMsg);
		} else {
			logger.info("Partial Verdict: PASS");
		}

	}

	private  void assertTrue(String errorMsg,
			boolean bool) {
		if(!bool){
			fail(errorMsg);
		} else {
			logger.info("Partial Verdict: PASS");
		}

	}


	private  void assertFalse(String errorMsg,
			boolean bool) {
		if(bool){
			fail(errorMsg);
		} else {
			logger.info("Partial Verdict: PASS");
		}

	}

	private  void assertNotNull(String msgError, String notNull) {

		if(notNull==null){
			fail(msgError);
		}else {
			logger.info("Partial Verdict: PASS");
		}

	}
	
	/*
	 * Verdict
	 */

	public String getVerdict() {
		String verdict=null;
		if(inconc){
			verdict="INCONC";
		} else if(pass){
			verdict="PASS";
		}else if(!pass){
			verdict="FAIL";
		}
		return verdict;
	}

}
