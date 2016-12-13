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


// TODO: Auto-generated Javadoc
/**
 * The Class ConfigurationService.
 */
public class ConfigurationService {


	/** The pass. */
	boolean pass=true;
	
	/** The inconc. */
	boolean inconc=false;
	
	/** The about client. */
	private  AboutClient aboutClient;
	
	/** The config client. */
	private  ConfigClient configClient = null;
	
	/** The service availability handler. */
	private  ServiceAvailabilityHandler serviceAvailabilityHandler;
	
	/** The device about announcement. */
	private  AboutAnnouncementDetails deviceAboutAnnouncement;
	
	/** The service helper. */
	private  ServiceHelper serviceHelper; 
	
	/** The bus introspector. */
	private  BusIntrospector busIntrospector;

	/** The key store path. */
	private  String  keyStorePath="/KeyStore";
	
	/** The dut app id. */
	private  UUID dutAppId;
	
	/** The dut device id. */
	private  String dutDeviceId;
	
	/** The time out. */
	private  int  timeOut=30;

	/** The about announcement timeout. */
	private  long aboutAnnouncementTimeout;

	/** The bus application name. */
	private  final String BUS_APPLICATION_NAME = "ConfigTestSuite";
	
	/** The tag. */
	private  final String TAG = "ConfigTestSuite";
	
	/** The logger. */
	private  final WindowsLoggerImpl logger =  new WindowsLoggerImpl(TAG);

	/** The new device name. */
	private  final String NEW_DEVICE_NAME = "newDeviceName";
	
	/** The invalid language code. */
	private  final String INVALID_LANGUAGE_CODE = "INVALID";
	
	/** The device name field. */
	private  final String[] DEVICE_NAME_FIELD = new String[]
			{ AboutKeys.ABOUT_DEVICE_NAME };
	
	/** The default language field. */
	private  final String[] DEFAULT_LANGUAGE_FIELD = new String[]
			{ AboutKeys.ABOUT_DEFAULT_LANGUAGE };
	
	/** The both fields. */
	private  final String[] BOTH_FIELDS = new String[]
			{ AboutKeys.ABOUT_DEVICE_NAME, AboutKeys.ABOUT_DEFAULT_LANGUAGE };
	
	/** The invalid field. */
	private  final String[] INVALID_FIELD = new String[]
			{ "INVALID" };
	
	/** The new passcode. */
	private  final char[] NEW_PASSCODE = "111111".toCharArray();
	
	/** The single char passcode. */
	private  final char[] SINGLE_CHAR_PASSCODE = "1".toCharArray();
	
	/** The default pincode. */
	private  final char[] DEFAULT_PINCODE = SrpAnonymousKeyListener.DEFAULT_PINCODE;
	
	/** The special chars passcode. */
	private  final char[] SPECIAL_CHARS_PASSCODE = "!@#$%^".toCharArray();

	/** The session lost timeout in seconds. */
	public long SESSION_LOST_TIMEOUT_IN_SECONDS = 30;
	
	/** The session close timeout in seconds. */
	private long SESSION_CLOSE_TIMEOUT_IN_SECONDS = 5;

	/** The ics. */
	Map<String,Boolean> ics;
	
	/** The ixit. */
	Map<String,String> ixit;

	/**
	 * Instantiates a new configuration service.
	 *
	 * @param testCase the test case
	 * @param iCSCF_ConfigurationServiceFramework the i csc f_ configuration service framework
	 * @param iCSCF_ConfigurationInterface the i csc f_ configuration interface
	 * @param iCSCF_FactoryResetMethod the i csc f_ factory reset method
	 * @param iXITCO_AppId the i xitc o_ app id
	 * @param iXITCO_DeviceId the i xitc o_ device id
	 * @param iXITCO_DefaultLanguage the i xitc o_ default language
	 * @param iXITCF_ConfigVersion the i xitc f_ config version
	 * @param iXITCF_Passcode the i xitc f_ passcode
	 * @param gPCO_AnnouncementTimeout the g pc o_ announcement timeout
	 * @param gPCF_SessionLost the g pc f_ session lost
	 * @param gPCF_SessionClose the g pc f_ session close
	 */
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

	/**
	 * Run test case.
	 *
	 * @param testCase the test case
	 * @throws Exception the exception
	 */
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
		}else if(testCase.equals("Config-v1-34")){
			testConfig_v1_34FactoryResetAfterUpdateConfigurations();
		}else if(testCase.equals("Config-v1-35")){
			testConfig_v1_35FactoryResetResetsPasscode();
			
		}else{
			fail("TestCase not valid");
		}
		tearDown();

	}
	
	/**
	 * Sets the up.
	 *
	 * @throws Exception the exception
	 */
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
			inconc("test setUp thrown an exception: "+e.getMessage());
			releaseResources();
			throw e;
		}

		System.out.println("====================================================");
	}
	
	/**
	 * Tear down.
	 *
	 * @throws Exception the exception
	 */
	protected  void tearDown() throws Exception
	{
		System.out.println("====================================================");
		logger.info("test tearDown started");
		releaseResources();
		logger.info("test tearDown done");
		System.out.println("====================================================");
	}
	
	/*TestCases*/

	/**
	 * Test config_v1_01 app id equals device id.
	 *
	 * @throws Exception the exception
	 */
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

	/**
	 * Test config_v1_02 connect with wrong passcode.
	 *
	 * @throws Exception the exception
	 */
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



	/**
	 * Test config_v1_04 get configurations with default language.
	 *
	 * @throws Exception the exception
	 */
	public  void testConfig_v1_04GetConfigurationsWithDefaultLanguage() throws Exception
	{
		logger.info("Partial Verdict: PASS");
		Map<String, Object> configMap = callGetConfigurationsWithDefaultLanguage();
		checkMapForRequiredFields(configMap);
		checkConsistencyWithAboutAnnouncement(configMap);
	}


	/**
	 * Test config_v1_05 unspecified language.
	 *
	 * @throws Exception the exception
	 */
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



	/**
	 * Test config_v1_06 lang consistence.
	 *
	 * @throws Exception the exception
	 */
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




	/**
	 * Test config_v1_07 unsupported language.
	 *
	 * @throws Exception the exception
	 */
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


	/**
	 * Test config_v1_08 update configurations with a new device name.
	 *
	 * @throws Exception the exception
	 */
	public  void testConfig_v1_08UpdateConfigurationsWithANewDeviceName() throws Exception
	{
		testUpdateConfigurations(AboutKeys.ABOUT_DEVICE_NAME, NEW_DEVICE_NAME);
	}



	/**
	 * Test config_v1_12 device name special.
	 *
	 * @throws Exception the exception
	 */
	public  void testConfig_v1_12DeviceNameSpecial() throws Exception
	{
		testUpdateConfigurations(AboutKeys.ABOUT_DEVICE_NAME, getDeviceNameWithSpecialCharacters());
	}


	/**
	 * Test config_v1_13 update unsupported language.
	 *
	 * @throws Exception the exception
	 */
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


	/**
	 * Test config_v1_14 update default lang.
	 *
	 * @throws Exception the exception
	 */
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




	/**
	 * Test config_v1_15 update default language to unsupported language.
	 *
	 * @throws Exception the exception
	 */
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



	/**
	 * Test config_v1_16 test changeto unspecified language.
	 *
	 * @throws Exception the exception
	 */
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

	/**
	 * Test config_v1_19 test update invalid field.
	 *
	 * @throws Exception the exception
	 */
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




	/**
	 * Test config_v1_20 test reset device name.
	 *
	 * @throws Exception the exception
	 */
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

	/**
	 * Test config_v1_21 reset default language.
	 *
	 * @throws Exception the exception
	 */
	public  void testConfig_v1_21ResetDefaultLanguage() throws Exception
	{
		String defaultLanguage = deviceAboutAnnouncement.getDefaultLanguage();

		callResetConfigurations(defaultLanguage, DEFAULT_LANGUAGE_FIELD);

		Map<String, Object> configMap = callGetConfigurationsWithDefaultLanguage();
		Map<String, Object> aboutMap = callGetAboutForDefaultLanguage();

		compareMapsForField(AboutKeys.ABOUT_DEFAULT_LANGUAGE, aboutMap, configMap);
	}


	/**
	 * Test config_v1_22 reset default multi language.
	 *
	 * @throws Exception the exception
	 */
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

	/**
	 * Test config_v1_24 fail reset unsupported lang.
	 *
	 * @throws Exception the exception
	 */
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

	/**
	 * Test config_v1_25 fail reset invalid field.
	 *
	 * @throws Exception the exception
	 */
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

	/**
	 * Test config_v1_26 device restart.
	 *
	 * @throws Exception the exception
	 */
	public  void testConfig_v1_26DeviceRestart() throws Exception
	{
		reconnectClients();
		callRestartOnConfig();
		//TODO not ready
		//assertTrue("Timed out waiting for session to be lost", ServiceAvailabilityHandler.waitForSessionLost(SESSION_LOST_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS));

		deviceAboutAnnouncement = waitForNextDeviceAnnouncement();

		connectAboutClient(deviceAboutAnnouncement);
	}

	
	/**
	 * Test config_v1_27 device restart remember conf data.
	 *
	 * @throws Exception the exception
	 */
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


	/**
	 * Test config_v1_29 passcode changed.
	 *
	 * @throws Exception the exception
	 */
	public  void testConfig_v1_29PasscodeChanged() throws Exception
	{
		testChangePasscode(NEW_PASSCODE);
	}


	/**
	 * Test config_v1_30 passcode changed single char.
	 *
	 * @throws Exception the exception
	 */
	public  void testConfig_v1_30PasscodeChangedSingleChar() throws Exception
	{
		testChangePasscode(SINGLE_CHAR_PASSCODE);
	}


	/**
	 * Test config_v1_31 passcode changed special chars.
	 *
	 * @throws Exception the exception
	 */
	public  void testConfig_v1_31PasscodeChangedSpecialChars() throws Exception
	{
		testChangePasscode(SPECIAL_CHARS_PASSCODE);
	}


	/**
	 * Test config_v1_32 passcode changed persist on restart.
	 *
	 * @throws Exception the exception
	 */
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



	/**
	 * Test config_v1_33 factory reset no update configuratins.
	 *
	 * @throws Exception the exception
	 */
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



	/**
	 * Test config_v1_34 factory reset after update configurations.
	 *
	 * @throws Exception the exception
	 */
	public  void testConfig_v1_34FactoryResetAfterUpdateConfigurations() throws Exception
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
	}




	/**
	 * Test config_v1_35 factory reset resets passcode.
	 *
	 * @throws Exception the exception
	 */
	public  void  testConfig_v1_35FactoryResetResetsPasscode() throws Exception
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

	}


	/*
	 *  Utilities
	 */

	/**
	 * Creates the user input details.
	 *
	 * @return the user input details
	 */
	protected  UserInputDetails createUserInputDetails()
	{
		String[] msg={"Continue"};
		return new UserInputDetails("Onboard DUT (if needed)",
				"FactoryReset() has been called on the DUT. Please Onboard the device to the Personal AP (if needed) and then click Continue", msg);
	}


	/**
	 * Call factory reset on config.
	 *
	 * @throws BusException the bus exception
	 */
	private  void callFactoryResetOnConfig() throws BusException
	{
		logger.info("Calling FactoryReset() on Config");
		configClient.factoryReset();
	}



	/**
	 * Test change passcode.
	 *
	 * @param newPasscode the new passcode
	 * @throws BusException the bus exception
	 * @throws Exception the exception
	 */
	private  void testChangePasscode(char[] newPasscode) throws BusException, Exception
	{
		changePasscodeAndReconnect(newPasscode);

		changePasscodeAndReconnect(DEFAULT_PINCODE);
	}


	/**
	 * Call restart on config.
	 *
	 * @throws BusException the bus exception
	 */
	private  void callRestartOnConfig() throws BusException
	{
		logger.info("Calling Restart() on Config");
		configClient.restart();
	}


	/**
	 * Call reset configurations.
	 *
	 * @param languageTag the language tag
	 * @param fields the fields
	 * @throws BusException the bus exception
	 */
	private  void callResetConfigurations(String languageTag, String[] fields) throws BusException
	{
		logger.info(String.format("Calling ResetConfigurations() with language: \"%s\" and %s", languageTag, Arrays.toString(fields)));
		configClient.ResetConfigurations(languageTag, fields);
	}

	/**
	 * Gets the another supported language.
	 *
	 * @return the another supported language
	 * @throws BusException the bus exception
	 */
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

	/**
	 * Gets the device name with special characters.
	 *
	 * @return the device name with special characters
	 */
	protected  String getDeviceNameWithSpecialCharacters()
	{
		StringBuilder builder = new StringBuilder();
		appendChars(builder, 33, 47);
		appendChars(builder, 58, 64);
		appendChars(builder, 91, 96);
		appendChars(builder, 123, 126);
		return builder.toString();
	}


	/**
	 * Append chars.
	 *
	 * @param builder the builder
	 * @param startIdx the start idx
	 * @param endIdx the end idx
	 */
	private  void appendChars(StringBuilder builder, int startIdx, int endIdx)
	{
		for (int asciiCode = startIdx; asciiCode < endIdx; asciiCode++)
		{
			builder.append((char) asciiCode);
		}
	}

	/**
	 * Test update configurations.
	 *
	 * @param fieldName the field name
	 * @param newFieldValue the new field value
	 * @throws BusException the bus exception
	 * @throws Exception the exception
	 */
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



	/**
	 * Update configurations and verify result.
	 *
	 * @param fieldName the field name
	 * @param newFieldValue the new field value
	 * @throws BusException the bus exception
	 * @throws Exception the exception
	 */
	private  void updateConfigurationsAndVerifyResult(String fieldName, String newFieldValue) throws BusException, Exception
	{
		String defaultLanguage = deviceAboutAnnouncement.getDefaultLanguage();
		Map<String, Object> mapConfig = new HashMap<String, Object>();
		mapConfig.put(fieldName, newFieldValue);
		callUpdateConfigurations(defaultLanguage, mapConfig);
		watiForNextAnnouncementAndVerifyFieldValue(fieldName, newFieldValue);
	}

	/**
	 * Call update configurations.
	 *
	 * @param languageTag the language tag
	 * @param configMap the config map
	 * @throws BusException the bus exception
	 */
	private  void callUpdateConfigurations(String languageTag, Map<String, Object> configMap) throws BusException
	{
		logger.info(String.format("Calling UpdateConfigurations() with language: \"%s\" and %s", languageTag, configMap));
		configClient.setConfig(configMap, languageTag);
	}


	/**
	 * Wati for next announcement and verify field value.
	 *
	 * @param fieldName the field name
	 * @param newFieldValue the new field value
	 * @throws Exception the exception
	 * @throws BusException the bus exception
	 */
	private  void watiForNextAnnouncementAndVerifyFieldValue(String fieldName, String newFieldValue) throws Exception, BusException
	{
		deviceAboutAnnouncement = waitForNextAnnouncementAndCheckFieldValue(fieldName, newFieldValue);

		Map<String, Object> configMap = callGetConfigurationsWithDefaultLanguage();
		Map<String, Object> aboutMap = callGetAboutForDefaultLanguage();
		verifyValueForAboutAndConfig(aboutMap, configMap, fieldName, newFieldValue);
	}

	/**
	 * Verify value for about and config.
	 *
	 * @param aboutMap the about map
	 * @param configMap the config map
	 * @param key the key
	 * @param verifyValue the verify value
	 */
	private  void verifyValueForAboutAndConfig(Map<String, Object> aboutMap, Map<String, Object> configMap, String key, String verifyValue)
	{
		logger.info("Checking if "+key+" from GetConfigurations() matches expected value");
		assertEquals(String.format("Value for %s retrieved from GetConfigurations() does not match expected value", key), verifyValue,(String)  configMap.get(key));
		logger.info("Checking if "+key+" from GetAboutData() matches expected value");
		assertEquals(String.format("Value for %s retrieved from GetAboutData() does not match expected value", key), verifyValue, (String) aboutMap.get(key));
	}

	/**
	 * Wait for next announcement and check field value.
	 *
	 * @param fieldName the field name
	 * @param fieldValue the field value
	 * @return the about announcement details
	 * @throws Exception the exception
	 */
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


	/**
	 * Call get about for default language.
	 *
	 * @return the map
	 * @throws BusException the bus exception
	 */
	private  Map<String, Object> callGetAboutForDefaultLanguage() throws BusException
	{
		String defaultLanguage = deviceAboutAnnouncement.getDefaultLanguage();
		return callGetAbout(defaultLanguage);
	}

	/**
	 * Call get about.
	 *
	 * @param language the language
	 * @return the map
	 * @throws BusException the bus exception
	 */
	private  Map<String, Object> callGetAbout(String language) throws BusException
	{
		logger.info(String.format("Calling getAboutData() with the language: \"%s\"", language));
		return aboutClient.getAbout(language);
	}

	/**
	 * Gets the supported languages.
	 *
	 * @param aboutMap the about map
	 * @return the supported languages
	 */
	private  String[] getSupportedLanguages(Map<String, Object> aboutMap)
	{
		String[] suppLangs = (String[]) aboutMap.get(AboutKeys.ABOUT_SUPPORTED_LANGUAGES);
		logger.info(String.format("Supported languages: %s", Arrays.toString(suppLangs)));
		return suppLangs;
	}

	/**
	 * Compare maps.
	 *
	 * @param map1 the map1
	 * @param map2 the map2
	 * @throws Exception the exception
	 */
	private  void compareMaps(Map<String, Object> map1, Map<String, Object> map2) throws Exception
	{
		logger.info("Comparing DeviceName");
		compareMapsForField(AboutKeys.ABOUT_DEVICE_NAME, map1, map2);
		logger.info("Comparing DefaultLanguage");
		compareMapsForField(AboutKeys.ABOUT_DEFAULT_LANGUAGE, map1, map2);
	}

	/**
	 * Compare maps for field.
	 *
	 * @param fieldName the field name
	 * @param map1 the map1
	 * @param map2 the map2
	 * @throws Exception the exception
	 */
	private  void compareMapsForField(String fieldName, Map<String, Object> map1, Map<String, Object> map2) throws Exception
	{
		logger.info("Comparing GetConfigurations() and GetAbout() "+fieldName+" field");
		assertEquals(String.format("%s does not match", fieldName), (String) map1.get(fieldName),(String) map2.get(fieldName));
	}


	/**
	 * Call get configurations with default language.
	 *
	 * @return the map
	 * @throws BusException the bus exception
	 */
	private  Map<String, Object> callGetConfigurationsWithDefaultLanguage() throws BusException
	{
		String defaultLanguage = deviceAboutAnnouncement.getDefaultLanguage();
		Map<String, Object> configMap = callGetConfigurations(defaultLanguage);
		return configMap;
	}

	/**
	 * Check map for required fields.
	 *
	 * @param map the map
	 */
	private  void checkMapForRequiredFields(Map<String, Object> map)
	{
		logger.info("Checking that DeviceName field is present");
		assertTrue("Required DeviceName field not present in map", map.containsKey(AboutKeys.ABOUT_DEVICE_NAME));
		logger.info("Checking that DefaultLanguage field is present");
		assertTrue("Required DefaultLanguage field not present in map", map.containsKey(AboutKeys.ABOUT_DEFAULT_LANGUAGE));
	}

	/**
	 * Check consistency with about announcement.
	 *
	 * @param configMap the config map
	 * @throws BusException the bus exception
	 */
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

	/**
	 * Call get version on config.
	 *
	 * @return the short
	 * @throws BusException the bus exception
	 */
	private  short callGetVersionOnConfig() throws BusException
	{
		short version = configClient.getVersion();
		logger.info(String.format("Call to getVersion() returns: %d", version));
		return version;
	}


	/**
	 * Checks if is app id equal to device id.
	 *
	 * @param dutAppId the dut app id
	 * @param dutDeviceId the dut device id
	 * @return true, if is app id equal to device id
	 */
	private  boolean isAppIdEqualToDeviceId(UUID dutAppId, String dutDeviceId)
	{
		logger.info(String.format("Comparing DeviceId: %s to AppId: %s", dutDeviceId, dutAppId.toString().replace("-", "")));
		return dutDeviceId.equals(dutAppId.toString().replace("-", ""));
	}

	/**
	 * Inits the service helper.
	 *
	 * @throws BusException the bus exception
	 * @throws Exception the exception
	 */
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

	/**
	 * Creates the service helper.
	 *
	 * @return the service helper
	 */
	protected  ServiceHelper createServiceHelper()
	{
		return new ServiceHelper();
	}


	/**
	 * Release resources.
	 *
	 * @throws Exception the exception
	 */
	private  void releaseResources() throws Exception
	{
		releaseServiceHelper();
	} 

	/**
	 * Release service helper.
	 *
	 * @throws Exception the exception
	 */
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

	/**
	 * Reconnect clients.
	 *
	 * @throws Exception the exception
	 */
	private  void reconnectClients() throws Exception
	{
		releaseServiceHelper();
		initServiceHelper();
	}

	/**
	 * Connect config client.
	 *
	 * @param aboutAnnouncement the about announcement
	 * @throws Exception the exception
	 */
	private  void connectConfigClient(AboutAnnouncementDetails aboutAnnouncement) throws Exception
	{
		configClient = serviceHelper.connectConfigClient(aboutAnnouncement);
	}

	/**
	 * Connect about client.
	 *
	 * @param aboutAnnouncement the about announcement
	 * @throws Exception the exception
	 */
	private  void connectAboutClient(AboutAnnouncementDetails aboutAnnouncement) throws Exception
	{
		serviceAvailabilityHandler = createServiceAvailabilityHandler();
		aboutClient = serviceHelper.connectAboutClient(aboutAnnouncement, serviceAvailabilityHandler);
	}

	/**
	 * Creates the service availability handler.
	 *
	 * @return the service availability handler
	 */
	protected  ServiceAvailabilityHandler createServiceAvailabilityHandler()
	{
		return new ServiceAvailabilityHandler();
	}

	/**
	 * Wait for next device announcement.
	 *
	 * @return the about announcement details
	 * @throws Exception the exception
	 */
	private  AboutAnnouncementDetails waitForNextDeviceAnnouncement() throws Exception
	{
		logger.info("Waiting for About announcement");
		return serviceHelper.waitForNextDeviceAnnouncement(aboutAnnouncementTimeout, TimeUnit.SECONDS, true);
	}

	/**
	 * Wait for session to close.
	 *
	 * @throws Exception the exception
	 */
	private  void waitForSessionToClose() throws Exception 
	{
		logger.info("Waiting for session to close");

		serviceHelper.waitForSessionToClose(SESSION_CLOSE_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS);

	}

	/**
	 * Reset passcode if needed.
	 *
	 * @throws Exception the exception
	 */
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



	/**
	 * Sets the passcode.
	 *
	 * @param pwd the new passcode
	 * @throws Exception the exception
	 */
	private  void setPasscode(char[] pwd) throws Exception
	{
		serviceHelper.clearKeyStore();
		serviceHelper.setAuthPassword(deviceAboutAnnouncement, pwd);
		callMethodToCheckAuthentication();
		changePasscodeAndReconnect(DEFAULT_PINCODE);
	}

	/**
	 * Change passcode and reconnect.
	 *
	 * @param newPasscode the new passcode
	 * @throws BusException the bus exception
	 * @throws Exception the exception
	 */
	private  void changePasscodeAndReconnect(char[] newPasscode) throws BusException, Exception
	{
		callSetPasscodeOnConfig(newPasscode);

		reconnectClients();
		serviceHelper.clearKeyStore();

		serviceHelper.setAuthPassword(deviceAboutAnnouncement, newPasscode);

		callMethodToCheckAuthentication();
	}

	/**
	 * Call set passcode on config.
	 *
	 * @param newPasscode the new passcode
	 * @throws BusException the bus exception
	 */
	private  void callSetPasscodeOnConfig(char[] newPasscode) throws BusException
	{
		String realm = "";
		logger.info(String.format("Calling SetPasscode() on Config with realm: %s; and passcode: %s", realm, Arrays.toString(newPasscode)));
		configClient.setPasscode(realm, newPasscode);

	}

	/**
	 * Call method to check authentication.
	 *
	 * @return the short
	 * @throws BusException the bus exception
	 */
	private short callMethodToCheckAuthentication() throws BusException
	{
		//callGetConfigurations("");
		short version = configClient.getVersion();
		logger.info(String.format("Call to getVersion() returns: %d",
		version));
		return version;
	}



	/**
	 * Call get configurations.
	 *
	 * @param languageTag the language tag
	 * @return the map
	 * @throws BusException the bus exception
	 */
	private  Map<String, Object> callGetConfigurations(String languageTag) throws BusException
	{
		logger.info(String.format("Calling GetConfigurations() with language: \"%s\"", languageTag));
		Map<String, Object> configMap = configClient.getConfig(languageTag);
		return configMap;
	}


	/*
	 * Verdicts
	 */
	

	/**
	 * Fail.
	 *
	 * @param msg the msg
	 */
	private  void fail(String msg) {
		logger.error(msg);
		logger.info("Partial Verdict: FAIL");
		pass=false;

	}

	/**
	 * Inconc.
	 *
	 * @param msg the msg
	 */
	private void inconc(String msg) {
		logger.error(msg);
		logger.info("Partial Verdict: INCONC");
		inconc = true;
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
		} else {
			logger.info("Partial Verdict: PASS");
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
		} else {
			logger.info("Partial Verdict: PASS");
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
		} else {
			logger.info("Partial Verdict: PASS");
		}

	}

	/**
	 * Assert not null.
	 *
	 * @param msgError the msg error
	 * @param notNull the not null
	 */
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

	/**
	 * Gets the verdict.
	 *
	 * @return the verdict
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