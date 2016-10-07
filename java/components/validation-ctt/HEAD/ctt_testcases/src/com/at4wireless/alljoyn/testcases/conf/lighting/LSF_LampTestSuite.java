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
package com.at4wireless.alljoyn.testcases.conf.lighting;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.ParserConfigurationException;

import org.alljoyn.bus.AboutKeys;
import org.alljoyn.bus.AboutObjectDescription;
import org.alljoyn.bus.AboutProxy;
import org.alljoyn.bus.BusException;
import org.alljoyn.bus.ProxyBusObject;
import org.alljoyn.bus.Variant;
import org.xml.sax.SAXException;

import com.at4wireless.alljoyn.core.about.AboutAnnouncementDetails;
import com.at4wireless.alljoyn.core.commons.ServiceAvailabilityHandler;
import com.at4wireless.alljoyn.core.commons.ServiceHelper;
import com.at4wireless.alljoyn.core.commons.log.Logger;
import com.at4wireless.alljoyn.core.commons.log.WindowsLoggerImpl;
import com.at4wireless.alljoyn.core.interfacevalidator.ValidationResult;
import com.at4wireless.alljoyn.core.introspection.BusIntrospector;
import com.at4wireless.alljoyn.core.introspection.bean.InterfaceDetail;
import com.at4wireless.alljoyn.core.introspection.bean.IntrospectionInterface;
import com.at4wireless.alljoyn.core.lighting.AllJoynManager;
import com.at4wireless.alljoyn.core.lighting.LampDetailsBusInterface;
import com.at4wireless.alljoyn.core.lighting.LampDetailsBusObject;
import com.at4wireless.alljoyn.core.lighting.LampInterfaceValidator;
import com.at4wireless.alljoyn.core.lighting.LampParametersBusInterface;
import com.at4wireless.alljoyn.core.lighting.LampParametersBusObject;
import com.at4wireless.alljoyn.core.lighting.LampServiceBusInterface;
import com.at4wireless.alljoyn.core.lighting.LampServiceBusInterface.Values;
import com.at4wireless.alljoyn.core.lighting.LampServiceBusObject;
import com.at4wireless.alljoyn.core.lighting.LampStateBusInterface;
import com.at4wireless.alljoyn.core.lighting.LampStateBusObject;
import com.at4wireless.alljoyn.core.lighting.LampStateSignalHandler;
import com.at4wireless.alljoyn.testcases.parameter.GeneralParameter;
import com.at4wireless.alljoyn.testcases.parameter.Ics;
import com.at4wireless.alljoyn.testcases.parameter.Ixit;

public class LSF_LampTestSuite
{
	private static final Logger logger = new WindowsLoggerImpl(LSF_LampTestSuite.class.getSimpleName());
	private static final String BUS_APPLICATION_NAME = "LSF_LampTestSuite";
	public long ANNOUNCEMENT_TIMEOUT_IN_SECONDS = 30;
	private long SESSION_CLOSE_TIMEOUT_IN_SECONDS = 5;
	
	private ServiceHelper serviceHelper;
	//private AboutClient aboutClient;
	private AboutProxy aboutProxy;
	private LampStateSignalHandler lampStateSignalHandler;
    private LampStateBusObject lampStateBusObject;
    private LampServiceBusObject lampServiceBusObject;
    private LampParametersBusObject lampParametersBusObject;
    private LampDetailsBusObject lampDetailsBusObject;
    private String lampObjectPath;
    private static final String SLASH_CHARACTER = "/";
    private static final String LAMPSERVICE_INTERFACE_NAME = "org.allseen.LSF.LampService";
    private static final String LAMPPARAMETERS_INTERFACE_NAME = "org.allseen.LSF.LampParameters";
	private static final String LAMPDETAILS_INTERFACE_NAME = "org.allseen.LSF.LampDetails";
	private static final String LAMPSTATE_INTERFACE_NAME = "org.allseen.LSF.LampState";

    private AboutAnnouncementDetails deviceAboutAnnouncement;

    //private AppUnderTestDetails appUnderTestDetails;
    private UUID dutAppId;
    private String dutDeviceId;
    //private ServiceAvailabilityHandler serviceAvailabilityHandler;

    private static boolean signalReceived = false;

    private static String NULL_STRING = "\\0";
    private static String LAMP_STATE_FIELD_ON_OFF = "OnOff";
    private static String LAMP_STATE_FIELD_BRIGHTNESS = "Brightness";
    private static String LAMP_STATE_FIELD_HUE = "Hue";
    private static String LAMP_STATE_FIELD_SATURATION = "Saturation";
    private static String LAMP_STATE_FIELD_COLOR_TEMP = "ColorTemp";

    public static String BUS_OBJECT_PATH = "/org/allseen/LSF/Lamp";

	public static final int LAMP_SERVICE_INTERFACE_VERSION = 1;
	public static final int LAMP_STATE_INTERFACE_VERSION = 1;
	public static final int LAMP_PARAMETERS_INTERFACE_VERSION = 1;
	public static final int LAMP_DETAILS_INTERFACE_VERSION = 1;

	public static final int LAMP_SERVICE_VERSION = 1;
	
	/** 
	 * [AT4] Added attributes to perform the test cases
	 * 
	 * pass	stores the final verdict of the test case
	 * inconc indicates abnormal behaviours
	 * ics	map that stores ICS values	
	 * ixit	map that stores IXIT values
	 * 
	 * */
	boolean pass = true;
	boolean inconc = false;
	private Ics icsList;
	private Ixit ixitList;

	public LSF_LampTestSuite(String testCase, Ics icsList, Ixit ixitList, GeneralParameter gpList)
	{
		this.icsList = icsList;
		this.ixitList = ixitList;
		
		ANNOUNCEMENT_TIMEOUT_IN_SECONDS = gpList.GPCO_AnnouncementTimeout;
		SESSION_CLOSE_TIMEOUT_IN_SECONDS = gpList.GPL_SessionClose;

		try
		{
			runTestCase(testCase);
		}
		catch (Exception e)
		{
			inconc = true;
		}
	}

	public void runTestCase(String testCase) throws Exception
	{
		setUp();
		
		try
		{
			logger.info("Running testcase: %s", testCase);
			
			if (testCase.equals("LSF_Lamp-v1-01"))
			{
				testLSF_Lamp_v1_01_InterfaceVersion();			
			}
			else if (testCase.equals("LSF_Lamp-v1-02"))
			{
				testLSF_Lamp_v1_02_ServiceVersion();
			}
			else if (testCase.equals("LSF_Lamp-v1-03"))
			{
				testLSF_Lamp_v1_03_ClearLampFault();
			}
			else if (testCase.equals("LSF_Lamp-v1-04"))
			{
				testLSF_Lamp_v1_04_SetOnOff();
			}
			else if (testCase.equals("LSF_Lamp-v1-05"))
			{
				testLSF_Lamp_v1_05_SetHue();
			}
			else if (testCase.equals("LSF_Lamp-v1-06"))
			{
				testLSF_Lamp_v1_06_SetSaturation();
			}
			else if (testCase.equals("LSF_Lamp-v1-07"))
			{
				testLSF_Lamp_v1_07_SetColorTemp();
			}
			else if (testCase.equals("LSF_Lamp-v1-08"))
			{
				testLSF_Lamp_v1_08_SetBrightness();
			}
			else if (testCase.equals("LSF_Lamp-v1-09"))
			{
				testLSF_Lamp_v1_09_TransitionLampState();
			}
			else if (testCase.equals("LSF_Lamp-v1-10"))
			{
				testLSF_Lamp_v1_10_ApplyPulseEffect();
			}
			else if (testCase.equals("LSF_Lamp-v1-11"))
			{
				testLSF_Lamp_v1_11_StandardizedInterfacesMatchDefinitions();
			}
			else if (testCase.equals("LSF_Lamp-v1-12"))
			{
				testLSF_Lamp_v1_12_ParametersInterfaceVersion();
			}
			else if (testCase.equals("LSF_Lamp-v1-13"))
			{
				testLSF_Lamp_v1_13_GetEnergy_Usage_Milliwatts();
			}
			else if (testCase.equals("LSF_Lamp-v1-14"))
			{
				testLSF_Lamp_v1_14_GetBrightness_Lumens();
			}
			else if (testCase.equals("LSF_Lamp-v1-15"))
			{
				testLSF_Lamp_v1_15_GetInterfaceVersion();
			}
			else if (testCase.equals("LSF_Lamp-v1-16"))
			{
				testLSF_Lamp_v1_16_GetMake();
			}
			else if (testCase.equals("LSF_Lamp-v1-17"))
			{
				testLSF_Lamp_v1_17_GetModel();
			}
			else if (testCase.equals("LSF_Lamp-v1-18"))
			{
				testLSF_Lamp_v1_18_GetType();
			}
			else if (testCase.equals("LSF_Lamp-v1-19"))
			{
				testLSF_Lamp_v1_19_GetLampType();
			}
			else if (testCase.equals("LSF_Lamp-v1-20"))
			{
				testLSF_Lamp_v1_20_GetLampBaseType();
			}
			else if (testCase.equals("LSF_Lamp-v1-21"))
			{
				testLSF_Lamp_v1_21_GetLampBeamAngle();
			}
			else if (testCase.equals("LSF_Lamp-v1-22"))
			{
				testLSF_Lamp_v1_22_GetDimmable();
			}
			else if (testCase.equals("LSF_Lamp-v1-23"))
			{
				testLSF_Lamp_v1_23_GetColor();
			}
			else if (testCase.equals("LSF_Lamp-v1-24"))
			{
				testLSF_Lamp_v1_24_GetVariableColorTemp();
			}
			else if (testCase.equals("LSF_Lamp-v1-25"))
			{
				testLSF_Lamp_v1_25_GetLampID();
			}
			else if (testCase.equals("LSF_Lamp-v1-26"))
			{
				testLSF_Lamp_v1_26_GetHasEffects();
			}
			else if (testCase.equals("LSF_Lamp-v1-27"))
			{
				testLSF_Lamp_v1_27_GetMinVoltage();
			}
			else if (testCase.equals("LSF_Lamp-v1-28"))
			{
				testLSF_Lamp_v1_28_GetMaxVoltage();
			}
			else if (testCase.equals("LSF_Lamp-v1-29"))
			{
				testLSF_Lamp_v1_29_GetWattage();
			}
			else if (testCase.equals("LSF_Lamp-v1-30"))
			{
				testLSF_Lamp_v1_30_GetIncandescentEquivalent();
			}
			else if (testCase.equals("LSF_Lamp-v1-31"))
			{
				testLSF_Lamp_v1_31_GetMaxLumens();
			}
			else if (testCase.equals("LSF_Lamp-v1-32"))
			{
				testLSF_Lamp_v1_32_GetMinTemperature();
			}
			else if (testCase.equals("LSF_Lamp-v1-33"))
			{
				testLSF_Lamp_v1_33_GetMaxTemperature();
			}
			else if (testCase.equals("LSF_Lamp-v1-34"))
			{
				testLSF_Lamp_v1_34_GetColorRenderingIndex();
			}
			else
			{
				fail("Test Case not valid");
			}
		}
		catch (Exception exception)
		{
			logger.error("Exception executing Test Case: %s", exception.getMessage()); //[AT4]
			
			try 
			{
				tearDown();
			} 
			catch (Exception newException) 
			{
				logger.error("Exception releasing resources: %s", newException.getMessage());
			}
			
			throw exception;
		}

		tearDown();
	}

	private void setUp() throws Exception
	{
		//super.setUp();
		System.out.println("====================================================");
		logger.info("test setUp started");

		try
		{
			//appUnderTestDetails = getValidationTestContext().getAppUnderTestDetails();
			//dutDeviceId = appUnderTestDetails.getDeviceId();
			dutDeviceId = ixitList.IXITCO_DeviceId;
			logger.info(String.format("Running LSF_Lamp test case against Device ID: %s", dutDeviceId));
			//dutAppId = appUnderTestDetails.getAppId();
			dutAppId = ixitList.IXITCO_AppId;
			logger.info(String.format("Running LSF_Lamp test case against App ID: %s", dutAppId));
			
			//lampObjectPath = getValidationTestContext().getTestObjectPath();
			lampObjectPath = getTestObjectPath();
			logger.info(String.format("Executing lamp test against Stream object found at %s", lampObjectPath));
			
			signalReceived = false;
			
			initServiceHelper();
			
			logger.info("test setUp done");
		}
		catch (Exception e)
		{
			inconc = true;
			logger.info("test setUp thrown an exception: "+ e.getMessage());
			//releaseResources();
			tearDown(); //[AT4]
			throw e;
		}
		System.out.println("====================================================");
	}



	protected void initServiceHelper() throws BusException, Exception
	{
		releaseServiceHelper();
		serviceHelper = createServiceHelper();

		serviceHelper.initialize(BUS_APPLICATION_NAME, dutDeviceId, dutAppId);

		lampStateBusObject = new LampStateBusObject();
		//serviceHelper.registerBusObject(lampStateBusObject, BUS_OBJECT_PATH); 
		serviceHelper.registerBusObject(lampStateBusObject, BUS_OBJECT_PATH+"/LampStateBusObject");

		lampServiceBusObject = new LampServiceBusObject();
		//serviceHelper.registerBusObject(lampServiceBusObject, BUS_OBJECT_PATH);
		serviceHelper.registerBusObject(lampServiceBusObject, BUS_OBJECT_PATH+"/LampServiceBusObject");
		
		lampParametersBusObject = new LampParametersBusObject();
		//serviceHelper.registerBusObject(lampParametersBusObject, BUS_OBJECT_PATH);
		serviceHelper.registerBusObject(lampParametersBusObject, BUS_OBJECT_PATH+"/LampParametersBusObject");
		
		lampDetailsBusObject = new LampDetailsBusObject();
		//serviceHelper.registerBusObject(lampDetailsBusObject, BUS_OBJECT_PATH);
		serviceHelper.registerBusObject(lampDetailsBusObject, BUS_OBJECT_PATH+"/LampDetailsBusObject");
		
		deviceAboutAnnouncement = waitForNextDeviceAnnouncement();
		logger.info("Partial Verdict: PASS");
		
		connectAboutClient(deviceAboutAnnouncement);
		logger.info("Partial Verdict: PASS");
	}
	
	private void releaseServiceHelper()
	{
		try
		{
			if (lampStateSignalHandler != null)
			{
				lampStateSignalHandler = null;
			}

			/*if (aboutClient != null)
			{
				aboutClient.disconnect();
				aboutClient = null;
			}*/
			
			if (aboutProxy != null)
			{
				aboutProxy = null;
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
			logger.info("Exception releasing resources", ex);
		}
	}
	
	/*private void reconnectClients() throws Exception
    {
        releaseServiceHelper();
        initServiceHelper();
    }*/
	
	private void connectAboutClient(AboutAnnouncementDetails aboutAnnouncement) throws Exception
	{
		//serviceAvailabilityHandler = createServiceAvailabilityHandler();
		//aboutClient = serviceHelper.connectAboutClient(aboutAnnouncement, serviceAvailabilityHandler);
		aboutProxy = serviceHelper.connectAboutProxy(aboutAnnouncement);
	}

	private AboutAnnouncementDetails waitForNextDeviceAnnouncement() throws Exception
	{
		logger.info("Waiting for About announcement");
		return serviceHelper.waitForNextDeviceAnnouncement(ANNOUNCEMENT_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS, true);
	}
	
	private void waitForSessionToClose() throws Exception
	{
		logger.info("Waiting for session to close");
		serviceHelper.waitForSessionToClose(SESSION_CLOSE_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS);
	}
	
	private void tearDown() {

		System.out.println("====================================================");
		logger.info("test tearDown started");
		releaseResources();
		logger.info("test tearDown done");
		System.out.println("====================================================");
	}
	
	/*private String getOriginalDeviceName(Map<String, Object> aboutMap)
    {
        return (String) aboutMap.get(AboutKeys.ABOUT_DEVICE_NAME);

    }*/
	
	/*private void watiForNextAnnouncementAndVerifyFieldValue(String fieldName, String newFieldValue) throws Exception, BusException
    {
        deviceAboutAnnouncement = waitForNextAnnouncementAndCheckFieldValue(fieldName, newFieldValue);
    }*/
	
	//@Override
    public void connectionLost()
    {
        logger.info("The connection with the remote device has been lost");
    }

	protected ServiceHelper createServiceHelper()
	{
		//return new ServiceHelper(new AndroidLogger());
		return new ServiceHelper();
	}

	protected ServiceAvailabilityHandler createServiceAvailabilityHandler()
	{
		return new ServiceAvailabilityHandler();
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

	private void releaseResources()
	{
		releaseServiceHelper();
	}

	
	/************************** Test Cases ********************************************/

	
	public void testLSF_Lamp_v1_01_InterfaceVersion() throws Exception
	{
		int interfaceVersion = 0;
		/*ProxyBusObject proxy = serviceHelper.getProxyBusObject(aboutClient, BUS_OBJECT_PATH,
				new Class[] { LampServiceBusInterface.class });*/
		ProxyBusObject proxy = serviceHelper.getProxyBusObject(deviceAboutAnnouncement, BUS_OBJECT_PATH,
				new Class[] { LampServiceBusInterface.class });
		try
		{
			// get interface version
			interfaceVersion = proxy.getInterface(LampServiceBusInterface.class).getVersion();

			logger.info("Get lamp service interface version returned " + interfaceVersion);
		}
		catch (BusException e)
		{
			logger.info("Exception caught while trying to get interface version");
			e.printStackTrace();

			fail("Exception caught while trying to get interface version");
		}
		
		logger.info("Checking if Interface version matches IXIT");
		assertEquals("LSF_Lamp service interface version does not match", ixitList.IXITL_LampServiceVersion, interfaceVersion);
	}

	public void testLSF_Lamp_v1_02_ServiceVersion() throws Exception
	{
		int lampServiceVersion = 0;
		ProxyBusObject proxy = serviceHelper.getProxyBusObject(deviceAboutAnnouncement, BUS_OBJECT_PATH,
				new Class[] { LampServiceBusInterface.class });
		try
		{
			// get lamp service version
			lampServiceVersion = proxy.getInterface(LampServiceBusInterface.class).getLampServiceVersion();

			logger.info("Get lamp service version returned " + lampServiceVersion);
		}
		catch (BusException e)
		{
			logger.info("Exception caught while trying to get service version");
			e.printStackTrace();

			fail("Exception caught while trying to get service version");
		}
		
		logger.info("Checking LampService version matches IXIT");
		assertEquals("LSF_Lamp service version does not match", ixitList.IXITL_LampServiceVersion, lampServiceVersion);
	}

	public void testLSF_Lamp_v1_03_ClearLampFault() throws Exception
	{
		int[] lampFaults = {0};
		int numFaults = 0;
		ProxyBusObject proxy = serviceHelper.getProxyBusObject(deviceAboutAnnouncement, BUS_OBJECT_PATH,
				new Class[] { LampServiceBusInterface.class });

		try
		{
			lampFaults = proxy.getInterface(LampServiceBusInterface.class).getLampFaults();
			numFaults = lampFaults.length;
			if (numFaults >= 1)
			{
				Values returnValue = proxy.getInterface(LampServiceBusInterface.class).ClearLampFault(lampFaults[0]);
				assertEquals("LSF_Lamp ClearLampFaults returns failure", 0, returnValue.LampResponseCode);
				if (returnValue.LampResponseCode == 0)
				{
					lampFaults = proxy.getInterface(LampServiceBusInterface.class).getLampFaults();
					//TODO what should correct expected behavior be here? Evidently different implementations clear (don't clear) differently.
					//assertEquals("LSF_Lamp ClearLampFaults did not clear", numFaults - 1, lampFaults.length);
				}
				logger.info("Clear lamp fault returned " + returnValue.LampResponseCode + " for fault code " + returnValue.LampFaultCode);
			}
		}
		catch (BusException e)
		{
			logger.info("Exception caught while trying to get/clear lamp fault");
			e.printStackTrace();
			fail("Exception caught while trying to get/clear lamp fault");
		}
	}

	public void testLSF_Lamp_v1_04_SetOnOff() throws Exception
	{
		boolean onOff = true;
		boolean newOnOff = false;
		ProxyBusObject proxy = serviceHelper.getProxyBusObject(deviceAboutAnnouncement, BUS_OBJECT_PATH,
				new Class[] { LampStateBusInterface.class });

		try
		{
			logger.info("Calling setOnOff() method with the value: "+onOff);
			proxy.getInterface(LampStateBusInterface.class).setOnOff(onOff);
			logger.info("Partial Verdict: PASS");
			logger.info("Calling getOnOff() method");
			newOnOff = proxy.getInterface(LampStateBusInterface.class).getOnOff();
			//logger.info("LSF_Lamp service returned " + newOnOff);
		}
		catch (BusException e)
		{
			logger.info("Exception caught while trying to set OnOff");
			e.printStackTrace();

			fail("Exception caught while trying to get/set OnOff");
		}
		logger.info("Checking if set and get values are equal");
		assertEquals("LSF_Lamp setOnOff returns failure", onOff, newOnOff);
	}

	public void testLSF_Lamp_v1_05_SetHue() throws Exception
	{
		int hue = 100;
		int newHue = 0;
		ProxyBusObject proxy = serviceHelper.getProxyBusObject(deviceAboutAnnouncement, BUS_OBJECT_PATH,
				new Class[] { LampStateBusInterface.class });

		try
		{
			logger.info("Calling setHue() method with the value: "+hue);
			proxy.getInterface(LampStateBusInterface.class).setHue(hue);
			logger.info("Partial Verdict: PASS");
			logger.info("Calling getHue() method");
			newHue = proxy.getInterface(LampStateBusInterface.class).getHue();

			//logger.info("LSF_Lamp service getHue returned " + newHue);
		}
		catch (BusException e)
		{
			logger.info("Exception caught while trying to set/get hue");
			e.printStackTrace();
			fail("Exception caught while trying to get/get hue");
		}
		logger.info("Checking if set and get values are equal");
		assertEquals("LSF_Lamp getHue returns failure", hue, newHue);
	}

	public void testLSF_Lamp_v1_06_SetSaturation() throws Exception
	{
		int saturation = 100;
		int newSaturation = 0;
		ProxyBusObject proxy = serviceHelper.getProxyBusObject(deviceAboutAnnouncement, BUS_OBJECT_PATH,
				new Class[] { LampStateBusInterface.class });

		try
		{
			logger.info("Calling setSaturation() method with the value: "+saturation);
			proxy.getInterface(LampStateBusInterface.class).setSaturation(saturation);
			logger.info("Partial Verdict: PASS");
			logger.info("Calling getSaturation() method");
			newSaturation = proxy.getInterface(LampStateBusInterface.class).getSaturation();

			logger.info("LSF_Lamp service getSaturation returned " + newSaturation);
		}
		catch (BusException e)
		{
			logger.info("Exception caught while trying to set/get saturation");
			e.printStackTrace();

			fail("Exception caught while trying to get/get saturation");
		}
		logger.info("Checking if set and get values are equal");
		assertEquals("LSF_Lamp setSaturation returns failure", saturation, newSaturation);
	}

	public void testLSF_Lamp_v1_07_SetColorTemp() throws Exception
	{
		if (getVariableColorTemp())
		{
			int colorTemp = 100;
			int newColorTemp = 0;
			ProxyBusObject proxy = serviceHelper.getProxyBusObject(deviceAboutAnnouncement, BUS_OBJECT_PATH,
					new Class[] { LampStateBusInterface.class });

			try
			{
				logger.info("Calling setColorTemp() method with the value: "+colorTemp);
				proxy.getInterface(LampStateBusInterface.class).setColorTemp(colorTemp);
				logger.info("Partial Verdict: PASS");
				logger.info("Calling getColorTemp() method");
				newColorTemp = proxy.getInterface(LampStateBusInterface.class).getColorTemp();

				logger.info("LSF_Lamp service getColorTemp returned " + newColorTemp);
			}
			catch (BusException e)
			{
				logger.info("Exception caught while trying to set/get colorTemp");
				e.printStackTrace();

				fail("Exception caught while trying to set/get colorTemp");
			}
			logger.info("Checking if set and get values are equal");
			assertEquals("LSF_Lamp setColorTemp returns failure", colorTemp, newColorTemp);
		}
	}

	private boolean getVariableColorTemp() throws Exception
	{
		boolean variableColorTemp = false;
		ProxyBusObject proxy = serviceHelper.getProxyBusObject(deviceAboutAnnouncement, BUS_OBJECT_PATH,
				new Class[] { LampDetailsBusInterface.class });
		try
		{
			variableColorTemp = proxy.getInterface(LampDetailsBusInterface.class).getVariableColorTemp();

			logger.info("Get lamp details variableColorTemp returned " + variableColorTemp);
			logger.info("Partial Verdict: PASS");
		}
		catch (BusException e)
		{
			logger.info("Exception caught while trying to get details variableColorTemp");
			e.printStackTrace();

			fail("Exception caught while trying to get details variableColorTemp");
		}

		return variableColorTemp;
	}

	public void testLSF_Lamp_v1_08_SetBrightness() throws Exception
	{
		if (GetDimmable())
		{
			int brightness = 100;
			int newBrightness = 0;
			ProxyBusObject proxy = serviceHelper.getProxyBusObject(deviceAboutAnnouncement, BUS_OBJECT_PATH,
					new Class[] { LampStateBusInterface.class });

			try
			{
				logger.info("Calling setBrightness method with value: "+brightness);
				proxy.getInterface(LampStateBusInterface.class).setBrightness(brightness);
				logger.info("Partial Verdict: PASS");
				logger.info("Calling getBrightness() method");
				newBrightness = proxy.getInterface(LampStateBusInterface.class).getBrightness();

				logger.info("LSF_Lamp service getBrightness returned " + newBrightness);
			}
			catch (BusException e)
			{
				logger.info("Exception caught while trying to set/get brightness");
				e.printStackTrace();

				fail("Exception caught while trying to set/get brightness");
			}
			logger.info("Checking if set and get values are equal");
			assertEquals("LSF_Lamp setBrightness returns failure", brightness, newBrightness);
		}
	}

	public void testLSF_Lamp_v1_09_TransitionLampState() throws Exception
	{
		//AllJoynManager ajMan = new AllJoynManager(null);
		AllJoynManager ajMan = new AllJoynManager();
		ajMan.initialize();
		
		lampStateSignalHandler = new LampStateSignalHandler();
		lampStateSignalHandler.setUpdateListener(this);
		ajMan.registerSignalHandler(lampStateSignalHandler);
		ajMan.createAllJoynSession(deviceAboutAnnouncement.getServiceName(), AllJoynManager.LAMP_SERVICE_PORT);
		
		Thread.sleep(1000);

		boolean onOffValue = true;
		int brightnessValue = 10;
		int hueValue 		= 20;
		int saturationValue	= 30;
		int colorTempValue	= 40;
		long timestamp = System.currentTimeMillis() / 1000L;
		int transitionPeriod = 10;
		Map<String,Variant> lampState;

		ProxyBusObject proxy = serviceHelper.getProxyBusObject(deviceAboutAnnouncement, BUS_OBJECT_PATH,
				new Class[] { LampStateBusInterface.class });

		lampState = getLampStateMap(String.valueOf(onOffValue), String.valueOf(brightnessValue), String.valueOf(hueValue), String.valueOf(saturationValue), String.valueOf(colorTempValue));

		try
		{
			int result = proxy.getInterface(LampStateBusInterface.class).TransitionLampState(timestamp, lampState, transitionPeriod);
			assertEquals("LSF_Lamp transitionLampState returns failure", 0, result);

			/**********************************************************************************************
			 ******** Use the following if validating semantics, in addition to wire-protocol, ************/

			// now get the properties and verify that we set the state
			int newHue = proxy.getInterface(LampStateBusInterface.class).getHue();
			logger.info("LSF_Lamp getHue returned " + newHue);
			assertEquals("TransitionLampState getHue failed ", hueValue, newHue);

			int newSaturation = proxy.getInterface(LampStateBusInterface.class).getSaturation();
			logger.info("LSF_Lamp service  getSaturation returned " + newSaturation);
			assertEquals("TransitionLampState getSaturation failed ", saturationValue, newSaturation);

			int newColorTemp = proxy.getInterface(LampStateBusInterface.class).getColorTemp();
			logger.info("LSF_Lamp service getColorTemp returned " + newColorTemp);
			assertEquals("TransitionLampState getColorTemp failed ", colorTempValue, newColorTemp);

			int newBrightness = proxy.getInterface(LampStateBusInterface.class).getBrightness();
			logger.info("LSF_Lamp service getBrightness returned " + newBrightness);
			assertEquals("TransitionLampState getBrightness failed ", brightnessValue, newBrightness);

			boolean newOnOff = proxy.getInterface(LampStateBusInterface.class).getOnOff();
			logger.info("LSF_Lamp service getOnOff returned " + newOnOff);
			assertEquals("TransitionLampState getOnOff failed ", onOffValue, newOnOff);

			/******************************************************************************/

			logger.info("Waiting for TransitionLampState signal return");
			Thread.sleep(5000);
			// there should have been enough time for signal to be received
			assertEquals("LSF_Lamp TransitionLampState await signal returns failure. ", true, signalReceived);
		}
		catch (BusException e)
		{
			logger.info("Exception caught while trying to transition lamp state");
			e.printStackTrace();
			fail("Exception caught while trying to transition lamp state");
		}

		ajMan.destroy();
		ajMan = null;
	}

	public void testLSF_Lamp_v1_10_ApplyPulseEffect() throws Exception
	{
		boolean fromOnOffValue 		= true;
		int fromBrightnessValue 	= 10;
		int fromHueValue			= 20;
		int fromSaturationValue		= 30;
		int fromColorTempValue		= 40;

		boolean toOnOffValue 	= true;
		int toBrightnessValue 	= 11;
		int toHueValue			= 22;
		int toSaturationValue	= 33;
		int toColorTempValue	= 44;

		long startTimestamp = System.currentTimeMillis() / 1000L;
		int period 	= 100;
		int duration 	= 200;
		int numPulses	= 3;

		Map<String,Variant> fromState;
		Map<String,Variant> toState;

		ProxyBusObject proxy = serviceHelper.getProxyBusObject(deviceAboutAnnouncement, BUS_OBJECT_PATH,
				new Class[] { LampStateBusInterface.class });

		fromState = getLampStateMap(String.valueOf(fromOnOffValue), String.valueOf(fromBrightnessValue), String.valueOf(fromHueValue), String.valueOf(fromSaturationValue), String.valueOf(fromColorTempValue));
		toState = getLampStateMap(String.valueOf(toOnOffValue), String.valueOf(toBrightnessValue), String.valueOf(toHueValue), String.valueOf(toSaturationValue), String.valueOf(toColorTempValue));
		try
		{
			logger.info("Calling ApplyPulseEffect() method");
			int result = proxy.getInterface(LampStateBusInterface.class).ApplyPulseEffect(fromState, toState, period, duration, numPulses, startTimestamp);
			logger.info("Checking if method returns success");
			assertEquals("LSF_Lamp ApplyPulseEffect returns failure", 0, result);
		}
		catch (BusException e)
		{
			logger.info("Exception caught while trying to ApplyPulseEffect ");
			e.printStackTrace();
			fail("Exception caught while trying to ApplyPulseEffect ");
		}
	}

	// This test case uses introspection to determine the interface of the lamp service. It compares that to a known
	// XML file for the lamp service (Lamp.xml).  Differences are treated as failures of the test.
	// The lamp service XML file should be kept in the directory
	// ...\validation-tests\HEAD\validation-tests-suites-lighting\src\main\resources\introspection-xml
	// If it is not available, do not include this test in the pom.xml file.
	public void testLSF_Lamp_v1_11_StandardizedInterfacesMatchDefinitions() throws Exception
	{
		try
		{
			checkForUnknownInterfacesFromAboutAnnouncement();

			// now see if any KNOWN interfaces are missing from object, or do not match our XML file
			List<InterfaceDetail> interfacesExposedOnBusBasedOnName = getIntrospector().getInterfacesExposedOnBusBasedOnName(LAMPPARAMETERS_INTERFACE_NAME);
			for (InterfaceDetail objectDetail : interfacesExposedOnBusBasedOnName)
			{
				for (IntrospectionInterface intfName : objectDetail.getIntrospectionInterfaces())
				{
					logger.info(String.format("Found object at %s implementing %s", objectDetail.getPath(), intfName.getName()));
				}
			}
			
			logger.info("Checking that LampParameters interface XML matches");
			ValidationResult validationResult = getInterfaceValidator().validate(interfacesExposedOnBusBasedOnName);
			logger.debug("after validate");
			assertTrue(validationResult.getFailureReason(), validationResult.isValid());

			interfacesExposedOnBusBasedOnName = getIntrospector().getInterfacesExposedOnBusBasedOnName(LAMPSERVICE_INTERFACE_NAME);
			for (InterfaceDetail objectDetail : interfacesExposedOnBusBasedOnName)
			{
				for (IntrospectionInterface intfName : objectDetail.getIntrospectionInterfaces())
				{
					logger.info(String.format("Found object at %s implementing %s", objectDetail.getPath(), intfName.getName()));
				}
			}
			logger.debug("before validate");
			logger.info("Checking that LampService interface XML matches");
			validationResult = getInterfaceValidator().validate(interfacesExposedOnBusBasedOnName);
			logger.debug("after validate");
			assertTrue(validationResult.getFailureReason(), validationResult.isValid());

			interfacesExposedOnBusBasedOnName = getIntrospector().getInterfacesExposedOnBusBasedOnName(LAMPDETAILS_INTERFACE_NAME);
			for (InterfaceDetail objectDetail : interfacesExposedOnBusBasedOnName)
			{
				for (IntrospectionInterface intfName : objectDetail.getIntrospectionInterfaces())
				{
					logger.info(String.format("Found object at %s implementing %s", objectDetail.getPath(), intfName.getName()));
				}
			}
			logger.debug("before validate");
			logger.info("Checking that LampDetails interface XML matches");
			validationResult = getInterfaceValidator().validate(interfacesExposedOnBusBasedOnName);
			logger.debug("after validate");
			assertTrue(validationResult.getFailureReason(), validationResult.isValid());

			interfacesExposedOnBusBasedOnName = getIntrospector().getInterfacesExposedOnBusBasedOnName(LAMPSTATE_INTERFACE_NAME);
			for (InterfaceDetail objectDetail : interfacesExposedOnBusBasedOnName)
			{
				for (IntrospectionInterface intfName : objectDetail.getIntrospectionInterfaces())
				{
					logger.info(String.format("Found object at %s implementing %s", objectDetail.getPath(), intfName.getName()));
				}
			}
			logger.debug("before validate");
			logger.info("Checking that LampState interface XML matches");
			validationResult = getInterfaceValidator().validate(interfacesExposedOnBusBasedOnName);
			logger.debug("after validate");
			assertTrue(validationResult.getFailureReason(), validationResult.isValid());

		}
		catch (BusException e)
		{
			logger.info("Exception caught in StandardizedInterfacesMatchDefinitions ");
			e.printStackTrace();
			fail(String.format("Exception caught in StandardizedInterfacesMatchDefinitions"));
		}
	}

	public void testLSF_Lamp_v1_12_ParametersInterfaceVersion() throws Exception
	{
		int interfaceVersion = 0;
		ProxyBusObject proxy = serviceHelper.getProxyBusObject(deviceAboutAnnouncement, BUS_OBJECT_PATH,
				new Class[] { LampParametersBusInterface.class });
		try
		{
			// get interface version
			interfaceVersion = proxy.getInterface(LampParametersBusInterface.class).getVersion();

			logger.info("Get lamp parameters interface version returned " + interfaceVersion);
		}
		catch (BusException e)
		{
			logger.info("Exception caught while trying to get parameters interface version");
			e.printStackTrace();

			fail("Exception caught while trying to get parameters interface version");
		}
		
		logger.info("Checking if LampParameters interface version matches IXIT");
		assertEquals("LSF_Lamp parameters interface version does not match", ixitList.IXITL_LampParametersVersion, interfaceVersion);
	}

	public void testLSF_Lamp_v1_13_GetEnergy_Usage_Milliwatts() throws Exception
	{
		int energy_Usage_Milliwatts = 0;
		ProxyBusObject proxy = serviceHelper.getProxyBusObject(deviceAboutAnnouncement, BUS_OBJECT_PATH,
				new Class[] { LampParametersBusInterface.class });
		try
		{
			energy_Usage_Milliwatts = proxy.getInterface(LampParametersBusInterface.class).getEnergy_Usage_Milliwatts();

			logger.info("Get lamp parameters energy_Usage_Milliwatts returned " + energy_Usage_Milliwatts);
			logger.info("Partial Verdict: PASS");
		}
		catch (BusException e)
		{
			logger.info("Exception caught while trying to get parameters energy_Usage_Milliwatts");
			e.printStackTrace();

			fail("Exception caught while trying to get parameters energy_Usage_Milliwatts");
		}
	}

	public void testLSF_Lamp_v1_14_GetBrightness_Lumens() throws Exception
	{
		int brightness_Lumens = 0;
		ProxyBusObject proxy = serviceHelper.getProxyBusObject(deviceAboutAnnouncement, BUS_OBJECT_PATH,
				new Class[] { LampParametersBusInterface.class });
		try
		{
			brightness_Lumens = proxy.getInterface(LampParametersBusInterface.class).getBrightness_Lumens();

			logger.info("Get lamp parameters brightness_Lumens returned " + brightness_Lumens);
			logger.info("Partial Verdict: PASS");
		}
		catch (BusException e)
		{
			logger.info("Exception caught while trying to get parameters brightness_Lumens");
			e.printStackTrace();

			fail("Exception caught while trying to get parameters brightness_Lumens");
		}
	}

	public void testLSF_Lamp_v1_15_GetInterfaceVersion() throws Exception
	{
		int interfaceVersion = 0;
		ProxyBusObject proxy = serviceHelper.getProxyBusObject(deviceAboutAnnouncement, BUS_OBJECT_PATH,
				new Class[] { LampDetailsBusInterface.class });
		try
		{
			interfaceVersion = proxy.getInterface(LampDetailsBusInterface.class).getVersion();

			logger.info("Get lamp details interface version returned " + interfaceVersion);
		}
		catch (BusException e)
		{
			logger.info("Exception caught while trying to get details interface version");
			e.printStackTrace();

			fail("Exception caught while trying to get details interface version");
		}
		logger.info("Checking if LampDetails interface version matches IXIT");
		assertEquals("LSF_Lamp details interface version does not match", ixitList.IXITL_LampDetailsVersion, interfaceVersion);
	}

	public void testLSF_Lamp_v1_16_GetMake() throws Exception
	{
		int make = 0;
		ProxyBusObject proxy = serviceHelper.getProxyBusObject(deviceAboutAnnouncement, BUS_OBJECT_PATH,
				new Class[] { LampDetailsBusInterface.class });
		try
		{
			make = proxy.getInterface(LampDetailsBusInterface.class).getMake();

			logger.info("Get lamp details make returned " + make);
			logger.info("Partial Verdict: PASS");
		}
		catch (BusException e)
		{
			logger.info("Exception caught while trying to get details make");
			e.printStackTrace();

			fail("Exception caught while trying to get details make");
		}
	}

	public void testLSF_Lamp_v1_17_GetModel() throws Exception
	{
		int model = 0;
		ProxyBusObject proxy = serviceHelper.getProxyBusObject(deviceAboutAnnouncement, BUS_OBJECT_PATH,
				new Class[] { LampDetailsBusInterface.class });
		try
		{
			model = proxy.getInterface(LampDetailsBusInterface.class).getModel();

			logger.info("Get lamp details model returned " + model);
			logger.info("Partial Verdict: PASS");
		}
		catch (BusException e)
		{
			logger.info("Exception caught while trying to get details model");
			e.printStackTrace();

			fail("Exception caught while trying to get details model");
		}
	}

	public void testLSF_Lamp_v1_18_GetType() throws Exception
	{
		int detailsType = 0;
		ProxyBusObject proxy = serviceHelper.getProxyBusObject(deviceAboutAnnouncement, BUS_OBJECT_PATH,
				new Class[] { LampDetailsBusInterface.class });

		try
		{
			detailsType = proxy.getInterface(LampDetailsBusInterface.class).getType();

			logger.info("Get lamp details detailsType returned " + detailsType);
			logger.info("Partial Verdict: PASS");
		}
		catch (BusException e)
		{
			logger.info("Exception caught while trying to get details detailsType");
			e.printStackTrace();

			fail("Exception caught while trying to get details detailsType");
		}
	}

	public void testLSF_Lamp_v1_19_GetLampType() throws Exception
	{
		int lampType = 0;
		ProxyBusObject proxy = serviceHelper.getProxyBusObject(deviceAboutAnnouncement, BUS_OBJECT_PATH,
				new Class[] { LampDetailsBusInterface.class });
		try
		{
			lampType = proxy.getInterface(LampDetailsBusInterface.class).getLampType();

			logger.info("Get lamp details lampType returned " + lampType);
			logger.info("Partial Verdict: PASS");
		}
		catch (BusException e)
		{
			logger.info("Exception caught while trying to get details lampType");
			e.printStackTrace();

			fail("Exception caught while trying to get details lampType");
		}
	}

	public void testLSF_Lamp_v1_20_GetLampBaseType() throws Exception
	{
		int lampBaseType = 0;
		ProxyBusObject proxy = serviceHelper.getProxyBusObject(deviceAboutAnnouncement, BUS_OBJECT_PATH,
				new Class[] { LampDetailsBusInterface.class });
		try
		{
			lampBaseType = proxy.getInterface(LampDetailsBusInterface.class).getLampBaseType();

			logger.info("Get lamp details lampBaseType returned " + lampBaseType);
			logger.info("Partial Verdict: PASS");
		}
		catch (BusException e)
		{
			logger.info("Exception caught while trying to get details lampBaseType");
			e.printStackTrace();

			fail("Exception caught while trying to get details lampBaseType");
		}
	}

	public void testLSF_Lamp_v1_21_GetLampBeamAngle() throws Exception
	{
		int lampBeamAngle = 0;
		ProxyBusObject proxy = serviceHelper.getProxyBusObject(deviceAboutAnnouncement, BUS_OBJECT_PATH,
				new Class[] { LampDetailsBusInterface.class });
		try
		{
			lampBeamAngle = proxy.getInterface(LampDetailsBusInterface.class).getLampBeamAngle();

			logger.info("Get lamp details lampBeamAngle returned " + lampBeamAngle);
			logger.info("Partial Verdict: PASS");
		}
		catch (BusException e)
		{
			logger.info("Exception caught while trying to get details lampBeamAngle");
			e.printStackTrace();

			fail("Exception caught while trying to get details lampBeamAngle");
		}
	}

	public void testLSF_Lamp_v1_22_GetDimmable() throws Exception
	{
		GetDimmable();
	}
	
	private boolean GetDimmable() throws Exception
	{
		boolean dimmable = false;
		ProxyBusObject proxy = serviceHelper.getProxyBusObject(deviceAboutAnnouncement, BUS_OBJECT_PATH,
				new Class[] { LampDetailsBusInterface.class });
		try
		{
			dimmable = proxy.getInterface(LampDetailsBusInterface.class).getDimmable();

			logger.info("Get lamp details dimmable returned " + dimmable);
			logger.info("Partial Verdict: PASS");
		}
		catch (BusException e)
		{
			logger.info("Exception caught while trying to get details dimmable");
			e.printStackTrace();

			fail("Exception caught while trying to get details dimmable");
		}

		return dimmable;
	}

	public void testLSF_Lamp_v1_23_GetColor() throws Exception
	{
		boolean color = false;
		ProxyBusObject proxy = serviceHelper.getProxyBusObject(deviceAboutAnnouncement, BUS_OBJECT_PATH,
				new Class[] { LampDetailsBusInterface.class });
		try
		{
			color = proxy.getInterface(LampDetailsBusInterface.class).getColor();

			logger.info("Get lamp details color returned " + color);
			logger.info("Partial Verdict: PASS");
		}
		catch (BusException e)
		{
			logger.info("Exception caught while trying to get details color");
			e.printStackTrace();

			fail("Exception caught while trying to get details color");
		}
	}

	public void testLSF_Lamp_v1_24_GetVariableColorTemp() throws Exception
	{
		getVariableColorTemp();
	}

	public void testLSF_Lamp_v1_25_GetLampID() throws Exception
	{
		String lampID = "";
		ProxyBusObject proxy = serviceHelper.getProxyBusObject(deviceAboutAnnouncement, BUS_OBJECT_PATH,
				new Class[] { LampDetailsBusInterface.class });
		try
		{
			lampID = proxy.getInterface(LampDetailsBusInterface.class).getLampID();

			logger.info("Get lamp details lampID returned " + lampID);
			logger.info("Partial Verdict: PASS");

		}
		catch (BusException e)
		{
			logger.info("Exception caught while trying to get details lampID");
			e.printStackTrace();

			fail("Exception caught while trying to get details lampID");
		}
	}

	public void testLSF_Lamp_v1_26_GetHasEffects() throws Exception
	{
		boolean hasEffects = false;
		ProxyBusObject proxy = serviceHelper.getProxyBusObject(deviceAboutAnnouncement, BUS_OBJECT_PATH,
				new Class[] { LampDetailsBusInterface.class });
		try
		{
			hasEffects = proxy.getInterface(LampDetailsBusInterface.class).getHasEffects();

			logger.info("Get lamp details hasEffects returned " + hasEffects);
			logger.info("Partial Verdict: PASS");

		}
		catch (BusException e)
		{
			logger.info("Exception caught while trying to get details hasEffects");
			e.printStackTrace();

			fail("Exception caught while trying to get details hasEffects");
		}
	}

	public void testLSF_Lamp_v1_27_GetMinVoltage() throws Exception
	{
		int minVoltage = 0;
		ProxyBusObject proxy = serviceHelper.getProxyBusObject(deviceAboutAnnouncement, BUS_OBJECT_PATH,
				new Class[] { LampDetailsBusInterface.class });
		try
		{
			minVoltage = proxy.getInterface(LampDetailsBusInterface.class).getMinVoltage();

			logger.info("Get lamp details minVoltage returned " + minVoltage);
			logger.info("Partial Verdict: PASS");
		}
		catch (BusException e)
		{
			logger.info("Exception caught while trying to get details minVoltage");
			e.printStackTrace();

			fail("Exception caught while trying to get details minVoltage");
		}
	}

	public void testLSF_Lamp_v1_28_GetMaxVoltage() throws Exception
	{
		int maxVoltage = 0;
		ProxyBusObject proxy = serviceHelper.getProxyBusObject(deviceAboutAnnouncement, BUS_OBJECT_PATH,
				new Class[] { LampDetailsBusInterface.class });
		try
		{
			maxVoltage = proxy.getInterface(LampDetailsBusInterface.class).getMaxVoltage();

			logger.info("Get lamp details maxVoltage returned " + maxVoltage);
			logger.info("Partial Verdict: PASS");
		}
		catch (BusException e)
		{
			logger.info("Exception caught while trying to get details maxVoltage");
			e.printStackTrace();

			fail("Exception caught while trying to get details maxVoltage");
		}
	}

	public void testLSF_Lamp_v1_29_GetWattage() throws Exception
	{
		int wattage = 0;
		ProxyBusObject proxy = serviceHelper.getProxyBusObject(deviceAboutAnnouncement, BUS_OBJECT_PATH,
				new Class[] { LampDetailsBusInterface.class });
		try
		{
			wattage = proxy.getInterface(LampDetailsBusInterface.class).getWattage();

			logger.info("Get lamp details wattage returned " + wattage);
			logger.info("Partial Verdict: PASS");
		}
		catch (BusException e)
		{
			logger.info("Exception caught while trying to get details wattage");
			e.printStackTrace();

			fail("Exception caught while trying to get details wattage");
		}
	}

	public void testLSF_Lamp_v1_30_GetIncandescentEquivalent() throws Exception
	{
		int incandescentEquivalent = 0;
		ProxyBusObject proxy = serviceHelper.getProxyBusObject(deviceAboutAnnouncement, BUS_OBJECT_PATH,
				new Class[] { LampDetailsBusInterface.class });
		try
		{
			incandescentEquivalent = proxy.getInterface(LampDetailsBusInterface.class).getIncandescentEquivalent();

			logger.info("Get lamp details incandescentEquivalent returned " + incandescentEquivalent);
			logger.info("Partial Verdict: PASS");
		}
		catch (BusException e)
		{
			logger.info("Exception caught while trying to get details incandescentEquivalent");
			e.printStackTrace();

			fail("Exception caught while trying to get details incandescentEquivalent");
		}
	}

	public void testLSF_Lamp_v1_31_GetMaxLumens() throws Exception
	{
		int maxLumens = 0;
		ProxyBusObject proxy = serviceHelper.getProxyBusObject(deviceAboutAnnouncement, BUS_OBJECT_PATH,
				new Class[] { LampDetailsBusInterface.class });
		try
		{
			maxLumens = proxy.getInterface(LampDetailsBusInterface.class).getMaxLumens();

			logger.info("Get lamp details maxLumens returned " + maxLumens);
			logger.info("Partial Verdict: PASS");
		}
		catch (BusException e)
		{
			logger.info("Exception caught while trying to get details maxLumens");
			e.printStackTrace();

			fail("Exception caught while trying to get details maxLumens");
		}
	}

	public void testLSF_Lamp_v1_32_GetMinTemperature() throws Exception
	{
		int minTemperature = 0;
		ProxyBusObject proxy = serviceHelper.getProxyBusObject(deviceAboutAnnouncement, BUS_OBJECT_PATH,
				new Class[] { LampDetailsBusInterface.class });
		try
		{
			minTemperature = proxy.getInterface(LampDetailsBusInterface.class).getMinTemperature();

			logger.info("Get lamp details minTemperature returned " + minTemperature);
			logger.info("Partial Verdict: PASS");
		}
		catch (BusException e)
		{
			logger.info("Exception caught while trying to get details minTemperature");
			e.printStackTrace();

			fail("Exception caught while trying to get details minTemperature");
		}
	}

	public void testLSF_Lamp_v1_33_GetMaxTemperature() throws Exception
	{
		int maxTemperature = 0;
		ProxyBusObject proxy = serviceHelper.getProxyBusObject(deviceAboutAnnouncement, BUS_OBJECT_PATH,
				new Class[] { LampDetailsBusInterface.class });
		try
		{
			maxTemperature = proxy.getInterface(LampDetailsBusInterface.class).getMaxTemperature();

			logger.info("Get lamp details maxTemperature returned " + maxTemperature);
			logger.info("Partial Verdict: PASS");
		}
		catch (BusException e)
		{
			logger.info("Exception caught while trying to get details maxTemperature");
			e.printStackTrace();

			fail("Exception caught while trying to get details maxTemperature");
		}
	}

	public void testLSF_Lamp_v1_34_GetColorRenderingIndex() throws Exception
	{
		int colorRenderingIndex = 0;
		ProxyBusObject proxy = serviceHelper.getProxyBusObject(deviceAboutAnnouncement, BUS_OBJECT_PATH,
				new Class[] { LampDetailsBusInterface.class });
		try
		{
			colorRenderingIndex = proxy.getInterface(LampDetailsBusInterface.class).getColorRenderingIndex();

			logger.info("Get lamp details colorRenderingIndex returned " + colorRenderingIndex);
			logger.info("Partial Verdict: PASS");
		}
		catch (BusException e)
		{
			logger.info("Exception caught while trying to get details colorRenderingIndex");
			e.printStackTrace();

			fail("Exception caught while trying to get details colorRenderingIndex");
		}

	}
	
	
	/************************** End Test Cases ********************************************/
	
	public void handleLampStateChanged(String LampID)
	{
		logger.info("LSF_Lamp signal LampStateChanged for " + LampID);
		
		signalReceived = true;
	}
	
	BusIntrospector getIntrospector()
	{
		return serviceHelper.getBusIntrospector(deviceAboutAnnouncement);
	}
	
	/*BusIntrospector getIntrospector(BusAttachment busAttachment, AboutClient newAboutClient)
    {
        return new XmlBasedBusIntrospector(busAttachment, newAboutClient.getPeerName(), newAboutClient.getSessionId());
    }

    BusIntrospector getIntrospector(ServiceHelper serviceHelper, AboutClient aboutClient)
    {
        return serviceHelper.getBusIntrospector(aboutClient);
    }*/
	
	protected LampInterfaceValidator getInterfaceValidator() throws IOException, ParserConfigurationException, SAXException
	{
		//return new LampInterfaceValidator(getValidationTestContext());
		return new LampInterfaceValidator();
	}
	
	private Variant getVariant(String data, boolean isOnOff)
	{
		try
		{
			int intValue = Integer.valueOf(data);
			//Log.d("LampServiceTest", "Parsing string did not throw an exception. intValue = " + intValue);

			if (isOnOff)
			{
				if (intValue == 0)
				{
					//Log.d("LampServiceTest", "Returning boolean variant with false argument");
					return new Variant(false, "b");
				}
				else if (intValue == 1)
				{
					//Log.d("LampServiceTest", "Returning boolean variant with true argument");
					return new Variant(true, "b");
				}
				else
				{
					//Log.d("LampServiceTest", "Returning unsigned int variant with argument " + intValue);
					return new Variant(Integer.valueOf(intValue), "u");
				}
			}
			else
			{
				//Log.d("LampServiceTest", "Returning unsigned int variant with argument " + intValue);
				return new Variant(Integer.valueOf(intValue), "u");
			}
		}
		catch (NumberFormatException ex)
		{
			//Log.d("LampServiceTest", "NumberFormatException caught when data String = " + data);

			if (data.equals(NULL_STRING))
			{
				//Log.d("LampServiceTest", "Null string found. Return null variant");
				return new Variant(null);
			}
			else
			{
				//Log.d("LampServiceTest", "Returning string variant using string: " + data);
				return new Variant(data, "s");
			}
		}
	}
	
	private Map<String, Variant> getLampStateMap(String onOffString, String brightnessString,  String hueString, String saturationString, String colorTempString)
	{
		Variant onOffVariant = getVariant(onOffString, true);
		Variant brightnessVariant = getVariant(brightnessString, false);
		Variant hueVariant = getVariant(hueString, false);
		Variant saturationVariant = getVariant(saturationString, false);
		Variant colorTempVariant = getVariant(colorTempString, false);

		final Map<String, Variant> newLampState = new HashMap<String, Variant>();
		newLampState.put(LAMP_STATE_FIELD_ON_OFF, onOffVariant);
		newLampState.put(LAMP_STATE_FIELD_BRIGHTNESS, brightnessVariant);
		newLampState.put(LAMP_STATE_FIELD_HUE, hueVariant);
		newLampState.put(LAMP_STATE_FIELD_SATURATION, saturationVariant);
		newLampState.put(LAMP_STATE_FIELD_COLOR_TEMP, colorTempVariant);

		return newLampState;
	}

	// Examine interfaces found at our bus object for any that do NOT appear in our XML file.
	private void checkForUnknownInterfacesFromAboutAnnouncement() throws Exception
	{
		for (AboutObjectDescription busObjectDescription : deviceAboutAnnouncement.getObjectDescriptions())
		{
			logger.info("BusObjectDescription: " + busObjectDescription.path + " supports " + Arrays.toString(busObjectDescription.interfaces));

			if (busObjectDescription.path.equals(BUS_OBJECT_PATH))
			{
				for (String interfaceName : busObjectDescription.interfaces)
				{
					// found an interface on our bus object
					logger.info(String.format("Found on our object interface  %s", interfaceName));

					// see if we can validate found interface
					List<InterfaceDetail> interfacesExposedOnBusBasedOnName = getIntrospector().getInterfacesExposedOnBusBasedOnName(interfaceName);
					for (InterfaceDetail objectDetail : interfacesExposedOnBusBasedOnName)
					{
						for (IntrospectionInterface intfName : objectDetail.getIntrospectionInterfaces())
						{
							logger.info(String.format("Found (unknown?) object at %s implementing %s", objectDetail.getPath(), intfName.getName()));
						}
					}

					try
					{
						ValidationResult validationResult = getInterfaceValidator().validate(interfacesExposedOnBusBasedOnName);
						logger.info("after found interface validate %s", validationResult.isValid());
						assertTrue(validationResult.getFailureReason(), validationResult.isValid());
					}
					catch (Exception e)
					{
						logger.info("Exception caught in checkForUnknownInterfacesFromAboutAnnouncement ");
						e.printStackTrace();
						fail(String.format("Exception caught in checkForUnknownInterfacesFromAboutAnnouncement"));
					}
				}
			}
		}
	}
	
	/** 
	 * [AT4] Added methods to emulate JUnit behaviour
	 * 
	 * assertEquals
	 * assertTrue
	 * 
	 * */
	private void assertEquals(String errorMessage, int first, int second)
	{
		if (first != second)
		{
			fail(errorMessage);
		}
		else
		{
			logger.info("Partial Verdict: PASS");
		}
	}

	private void assertEquals(String errorMessage, String first, String second)
	{
		if (!first.equals(second))
		{
			fail(errorMessage);
		}
		else
		{
			logger.info("Partial Verdict: PASS");
		}
	}

	private void assertEquals(String errorMessage, boolean first, boolean second)
	{
		if (first != second)
		{
			fail(errorMessage);
		}
		else
		{
			logger.info("Partial Verdict: PASS");
		}
	}

	private void assertTrue(String errorMessage, boolean condition)
	{
		if (!condition)
		{
			fail(errorMessage);
		}
		else
		{
			logger.info("Partial Verdict: PASS");
		}
	}
	
	/**
	 * [AT4] Added methods to manage the verdict
	 * 
	 * fail
	 * getFinalVerdict
	 * 
	 *  */
	private void fail(String msg)
	{
		logger.error(msg);
		logger.info("Partial Verdict: FAIL");
		pass = false;
	}

	public String getFinalVerdict()
	{
		if (inconc)
		{
			return "INCONC";
		}
		
		if (pass)
		{
			return "PASS";
		}
		else
		{
			return "FAIL";
		}
	}
	
	private String getTestObjectPath()
	{
		return BUS_OBJECT_PATH; //[AT4] Should this be changed to an IXIT?
	}
}