/*******************************************************************************
 *   *  
 *     Copyright (c) 2016 Open Connectivity Foundation and AllJoyn Open
 *     Source Project Contributors and others.
 *     
 *     All rights reserved. This program and the accompanying materials are
 *     made available under the terms of the Apache License, Version 2.0
 *     which accompanies this distribution, and is available at
 *     http://www.apache.org/licenses/LICENSE-2.0

 *******************************************************************************/
package com.at4wireless.alljoyn.testcases.conf.smarthome;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.alljoyn.bus.BusException;
import org.alljoyn.bus.ProxyBusObject;
import org.alljoyn.smarthome.centralizedmanagement.client.SmartHomeClient;

import com.at4wireless.alljoyn.core.about.AboutAnnouncementDetails;
import com.at4wireless.alljoyn.core.commons.ServiceHelper;
import com.at4wireless.alljoyn.core.commons.log.Logger;
import com.at4wireless.alljoyn.core.commons.log.WindowsLoggerImpl;
import com.at4wireless.alljoyn.testcases.parameter.GeneralParameter;
import com.at4wireless.alljoyn.testcases.parameter.Ics;
import com.at4wireless.alljoyn.testcases.parameter.Ixit;

public class SmartHomeTestSuite
{
	private static final Logger logger = new WindowsLoggerImpl(SmartHomeTestSuite.class.getSimpleName());
	private static final String BUS_APPLICATION_NAME = "SmartHome";
	public static String BUS_OBJECT_PATH = "/org/alljoyn/SmartHome/CentralizedManagement";
	private long SIGNAL_TIMEOUT_IN_SECONDS = 30;
	
	protected  AboutAnnouncementDetails deviceAboutAnnouncement;
	private  ServiceHelper serviceHelper;
	private  UUID dutAppId;
	private  String dutDeviceId;
	//private  BusIntrospector busIntrospector;
	//private  AboutClient aboutClient;
	
	/** 
	 * [AT4] Added attributes to perform the test cases
	 * 
	 * pass
	 * 		stores the final verdict of the test case
	 * ics								
	 * 		map that stores ICS values	
	 * ixit								
	 * 		map that stores IXIT values
	 * ANNOUNCEMENT_TIMEOUT_IN_SECONDS 	
	 * 		timeout waiting for about announcement
	 * 
	 * */
	boolean pass = true;
	boolean inconc = false;
	private long ANNOUNCEMENT_TIMEOUT_IN_SECONDS = 30;
	private Ics icsList;
	private Ixit ixitList;

	public SmartHomeTestSuite(String testCase, Ics icsList, Ixit ixitList, GeneralParameter gpList)
	{
		this.icsList = icsList;
		this.ixitList = ixitList;
		
		ANNOUNCEMENT_TIMEOUT_IN_SECONDS = gpList.GPCO_AnnouncementTimeout;
		SIGNAL_TIMEOUT_IN_SECONDS = gpList.GPSH_Signal;
		
		try
		{
			runTestCase(testCase);
		}
		catch(Exception e)
		{
			inconc = true;
		}
	}

	private void runTestCase(String testCase) throws Exception
	{
		setUp();

		try
		{
			if (testCase.equals("SmartHome-v1-01"))
			{
				testSmartHome_v1_01();
			}
			else if (testCase.equals("SmartHome-v1-02"))
			{
				testSmartHome_v1_02();
			}
			else if (testCase.equals("SmartHome-v1-03"))
			{
				testSmartHome_v1_03();
			}
			else if (testCase.equals("SmartHome-v1-04"))
			{
				testSmartHome_v1_04();
			}
			else if (testCase.equals("SmartHome-v1-05"))
			{
				testSmartHome_v1_05();
			}
			else
			{
				fail("TestCase not valid");
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
		try
		{
			System.out.println("====================================================");
			logger.info("test setUp started");


			dutDeviceId = ixitList.IXITCO_DeviceId;
			logger.info(String.format("Running ControlPanel test case against Device ID: %s", dutDeviceId));
			dutAppId = ixitList.IXITCO_AppId;
			logger.info(String.format("Running ControlPanel test case against App ID: %s", dutAppId));

			serviceHelper = getServiceHelper();
			serviceHelper.initialize(BUS_APPLICATION_NAME, dutDeviceId, dutAppId);

			deviceAboutAnnouncement = serviceHelper.waitForNextDeviceAnnouncement(ANNOUNCEMENT_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS);
			assertNotNull("Timed out waiting for About announcement", deviceAboutAnnouncement);
			//aboutClient = serviceHelper.connectAboutClient(deviceAboutAnnouncement);

			logger.info("test setUp done");
			System.out.println("====================================================");
		}
		catch (Exception exception)
		{
			try
			{
				releaseResources();
			}
			catch (Exception newException)
			{
				logger.error("Exception releasing resources "+ newException.toString());
			}

			throw exception;
		}
	}
	
	protected void tearDown() throws Exception
	{
		System.out.println("====================================================");
		logger.debug("test tearDown started");
		releaseResources();
		System.out.println("test tearDown done");
		System.out.println("====================================================");
	}
	
	private void testSmartHome_v1_01() throws Exception
	{
		ProxyBusObject proxy = serviceHelper.getProxyBusObject(deviceAboutAnnouncement, BUS_OBJECT_PATH,
				new Class[] { SmartHomeClient.class });

		int interfaceVersion=0;
		try
		{
			// get interface version
			interfaceVersion = proxy.getInterface(SmartHomeClient.class).getVersion();

			logger.info("Get Centralized Management interface version returned " + interfaceVersion);
		}
		catch (BusException e)
		{
			logger.info("Exception caught while trying to get interface version");
			e.printStackTrace();

			fail("Exception caught while trying to get interface version");
		}

		assertEquals("Centralized Management service interface version does not match", 
				ixitList.IXITSH_CentralizedManagementVersion, interfaceVersion);
	}

	private void testSmartHome_v1_02() throws Exception
	{
		ProxyBusObject proxy = serviceHelper.getProxyBusObject(deviceAboutAnnouncement, BUS_OBJECT_PATH,
				new Class[] { SmartHomeClient.class });


		proxy.getInterface(SmartHomeClient.class).ApplianceRegistration(ixitList.IXITSH_WellKnownName, 
				ixitList.IXITSH_UniqueName, ixitList.IXITSH_DeviceId);

		CountDownLatch countDownLatch = new CountDownLatch(1);
		ReturnValueHandler signalHandler=new ReturnValueHandler(countDownLatch);
		serviceHelper.registerSignalHandler(signalHandler);

		assertTrue("Did not receive expected ReturnValue signal", countDownLatch.await(getSignalTimeout(), TimeUnit.SECONDS));


		//TODO Continue with the implementation of the test cases, 
		//now we don´t have the correct interface
		// proxy.getInterface(SmartHomeClient.class).


	}
	
	private void testSmartHome_v1_03()
	{
		// TODO Auto-generated method stub

	}
	
	private void testSmartHome_v1_04()
	{
		// TODO Auto-generated method stub

	}
	
	private void testSmartHome_v1_05()
	{
		// TODO Auto-generated method stub

	}

	long getSignalTimeout()
	{
		return SIGNAL_TIMEOUT_IN_SECONDS;
	}

	private void releaseResources()
	{
		if (serviceHelper != null)
		{
			serviceHelper.release();
			serviceHelper = null;
		}
	}

	protected ServiceHelper getServiceHelper()
	{
		return new ServiceHelper();
	}

	/** 
	 * [AT4] Added methods to emulate JUnit behaviour
	 * 
	 * assertEquals
	 * assertTrue
	 * assertNotNull
	 * 
	 * */
	private void assertEquals(String errorMessage, int first, int second)
	{
		if (first != second)
		{
			fail(errorMessage);
		}
	}

	private void assertTrue(String errorMessage, boolean condition)
	{
		if (!condition)
		{
			fail(errorMessage);
		}
	}
	
	private void assertNotNull(String errorMessage, Object object)
	{
		if (object == null)
		{
			fail(errorMessage);
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
		if (pass)
		{
			return "PASS";
		}
		else
		{
			return "FAIL";
		}
	}

}