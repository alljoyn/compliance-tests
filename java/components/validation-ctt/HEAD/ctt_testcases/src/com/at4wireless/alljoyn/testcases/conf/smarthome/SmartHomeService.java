/*
 *    Copyright (c) Open Connectivity Foundation (OCF) and AllJoyn Open
 *    Source Project (AJOSP) Contributors and others.
 *
 *    SPDX-License-Identifier: Apache-2.0
 *
 *    All rights reserved. This program and the accompanying materials are
 *    made available under the terms of the Apache License, Version 2.0
 *    which accompanies this distribution, and is available at
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Copyright (c) Open Connectivity Foundation and Contributors to AllSeen
 *    Alliance. All rights reserved.
 *
 *    Permission to use, copy, modify, and/or distribute this software for
 *    any purpose with or without fee is hereby granted, provided that the
 *    above copyright notice and this permission notice appear in all
 *    copies.
 *
 *    THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL
 *    WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED
 *    WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE
 *    AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL
 *    DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR
 *    PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER
 *    TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 *    PERFORMANCE OF THIS SOFTWARE.
 */
package com.at4wireless.alljoyn.testcases.conf.smarthome;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.alljoyn.about.client.AboutClient;
import org.alljoyn.bus.BusException;
import org.alljoyn.bus.ProxyBusObject;
import org.alljoyn.bus.Status;
import org.alljoyn.smarthome.centralizedmanagement.client.ObjectInfo;
import org.alljoyn.smarthome.centralizedmanagement.client.ReturnValueSignalHandler;
import org.alljoyn.smarthome.centralizedmanagement.client.SmartHomeClient;

import com.at4wireless.alljoyn.core.about.AboutAnnouncementDetails;
import com.at4wireless.alljoyn.core.commons.ServiceHelper;
import com.at4wireless.alljoyn.core.commons.log.WindowsLoggerImpl;
import com.at4wireless.alljoyn.core.introspection.BusIntrospector;
import com.at4wireless.alljoyn.core.lighting.LampServiceBusInterface;

// TODO: Auto-generated Javadoc
/**
 * The Class SmartHomeService.
 */
public class SmartHomeService {


	/** The pass. */
	Boolean pass=true;
	
	/** The tag. */
	protected  final String TAG = "SmartHomeTestSuite";
	
	/** The bus application name. */
	private  final String BUS_APPLICATION_NAME = "SmartHome";
	
	/** The bus object path. */
	public  String BUS_OBJECT_PATH = "/org/alljoyn/SmartHome/CentralizedManagement";
	
	/** The signal timeout in seconds. */
	private long SIGNAL_TIMEOUT_IN_SECONDS = 30;
	
	/** The logger. */
	private  final WindowsLoggerImpl logger =  new WindowsLoggerImpl(TAG);
	
	/** The device about announcement. */
	protected  AboutAnnouncementDetails deviceAboutAnnouncement;
	
	/** The service helper. */
	private  ServiceHelper serviceHelper;
	
	/** The dut app id. */
	private  UUID dutAppId;
	
	/** The dut device id. */
	private  String dutDeviceId;
	
	/** The bus introspector. */
	private  BusIntrospector busIntrospector;
	
	/** The about client. */
	private  AboutClient aboutClient;
	
	/** The time out. */
	private  int  timeOut=30;


	/** The ICSS h_ smart home service framework. */
	boolean ICSSH_SmartHomeServiceFramework=false;
	
	/** The ICSS h_ centralized management interface. */
	boolean ICSSH_CentralizedManagementInterface=false;

	/*String  IXITCO_AppId=null;
	String IXITCO_DeviceId=null;
	String IXITCO_DefaultLanguage=null;

	String IXITSH_CentralizedManagementVersion=null;
	String IXITSH_WellKnownName=null;
	String IXITSH_UniqueName=null;
	String IXITSH_DeviceId=null;
	String IXITSH_HeartBeatInterval=null;*/

	/** The ixit. */
	Map<String,String> ixit;

	/**
	 * Instantiates a new smart home service.
	 *
	 * @param testCase the test case
	 * @param iCSSH_SmartHomeServiceFramework the i css h_ smart home service framework
	 * @param iCSSH_CentralizedManagementInterface the i css h_ centralized management interface
	 * @param iXITCO_AppId the i xitc o_ app id
	 * @param iXITCO_DeviceId the i xitc o_ device id
	 * @param iXITCO_DefaultLanguage the i xitc o_ default language
	 * @param iXITSH_CentralizedManagementVersion the i xits h_ centralized management version
	 * @param iXITSH_WellKnownName the i xits h_ well known name
	 * @param iXITSH_UniqueName the i xits h_ unique name
	 * @param iXITSH_DeviceId the i xits h_ device id
	 * @param iXITSH_HeartBeatInterval the i xits h_ heart beat interval
	 * @param gPCO_AnnouncementTimeout the g pc o_ announcement timeout
	 * @param gPSH_Signal the g ps h_ signal
	 */
	public SmartHomeService(String testCase,
			boolean iCSSH_SmartHomeServiceFramework,
			boolean iCSSH_CentralizedManagementInterface, String iXITCO_AppId,
			String iXITCO_DeviceId, String iXITCO_DefaultLanguage,
			String iXITSH_CentralizedManagementVersion,
			String iXITSH_WellKnownName, String iXITSH_UniqueName,
			String iXITSH_DeviceId, String iXITSH_HeartBeatInterval,
			String gPCO_AnnouncementTimeout, String gPSH_Signal) {


		ICSSH_SmartHomeServiceFramework=false;
		ICSSH_CentralizedManagementInterface=false;
		
		/*IXITCO_AppId=iXITCO_AppId;
		IXITCO_DeviceId=iXITCO_DeviceId;
		IXITCO_DefaultLanguage=iXITCO_DefaultLanguage;

		IXITSH_CentralizedManagementVersion=null;
		IXITSH_WellKnownName=null;
		IXITSH_UniqueName=null;
		IXITSH_DeviceId=null;
		IXITSH_HeartBeatInterval=null;*/
		
		ixit = new HashMap<String,String>();
		ixit.put("IXITCO_AppId", iXITCO_AppId);
		ixit.put("IXITCO_DeviceId", iXITCO_DeviceId);
		ixit.put("IXITSH_CentralizedManagementVersion", iXITSH_CentralizedManagementVersion);
		ixit.put("IXITSH_WellKnownName", iXITSH_WellKnownName);
		ixit.put("IXITSH_UniqueName", iXITSH_UniqueName);
		ixit.put("IXITSH_DeviceId", iXITSH_DeviceId);
		ixit.put("IXITSH_HeartBeatInterval", iXITSH_HeartBeatInterval);
		
		timeOut = Integer.parseInt(gPCO_AnnouncementTimeout);
		SIGNAL_TIMEOUT_IN_SECONDS = Integer.parseInt(gPSH_Signal);
		

		try {
			runTestCase(testCase);
		} catch (Exception e) {
			if(e.getMessage().equals("Timed out waiting for About announcement")){
				fail("Timed out waiting for About announcement");
			} else {
				fail("Exception: "+e.toString());
			}
		}
	}

	/**
	 * Run test case.
	 *
	 * @param testCase the test case
	 * @throws Exception the exception
	 */
	private  void runTestCase(String testCase) throws Exception {

		//setUp(IXITCO_DeviceId,IXITCO_AppId);
		setUp();

		if(testCase.equals("SmartHome-v1-01")){
			testSmartHome_v1_01();
		}else if(testCase.equals("SmartHome-v1-02")){
			testSmartHome_v1_02();
		}else if(testCase.equals("SmartHome-v1-03")){
			testSmartHome_v1_03();
		}else if(testCase.equals("SmartHome-v1-04")){
			testSmartHome_v1_04();
		}else if(testCase.equals("SmartHome-v1-05")){
			testSmartHome_v1_05();
		}else {
			fail("TestCase not valid");
		}

		tearDown();
	}
	
	//private  void setUp(String iXITCO_DeviceId, String iXITCO_AppId) throws Exception {
	/**
	 * Sets the up.
	 *
	 * @throws Exception the exception
	 */
	private  void setUp() throws Exception {		
		try
		{
			System.out.println("====================================================");
			logger.debug("test setUp started");


			/*dutDeviceId = iXITCO_DeviceId;
			logger.debug(String.format("Running ControlPanel test case against Device ID: %s", dutDeviceId));
			dutAppId = UUID.fromString(iXITCO_AppId);
			logger.debug(String.format("Running ControlPanel test case against App ID: %s", dutAppId));*/
			logger.info(String.format("Running test case against Device ID: %s", ixit.get("IXITCO_DeviceId")));
			logger.info(String.format("Running test case against App ID: %s", UUID.fromString(ixit.get("IXITCO_AppId"))));

			serviceHelper = getServiceHelper();
			serviceHelper.initialize(BUS_APPLICATION_NAME, dutDeviceId, dutAppId);

			deviceAboutAnnouncement = serviceHelper.waitForNextDeviceAnnouncement(timeOut, TimeUnit.SECONDS);
			assertNotNull("Timed out waiting for About announcement", deviceAboutAnnouncement);
			aboutClient = serviceHelper.connectAboutClient(deviceAboutAnnouncement);

			logger.debug("test setUp done");
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
				logger.debug("Exception releasing resources "+ newException.toString());
			}

			throw exception;
		}
	}
	
	/**
	 * Tear down.
	 *
	 * @throws Exception the exception
	 */
	protected  void tearDown() throws Exception {
		System.out.println("====================================================");
		logger.debug("test tearDown started");
		releaseResources();
		logger.debug("test tearDown done");
		System.out.println("====================================================");
	}
	
	/*
	 * TestCases
	 */
	
	/**
	 * Test smart home_v1_01.
	 *
	 * @throws Exception the exception
	 */
	private  void testSmartHome_v1_01() throws Exception {

		
		ProxyBusObject proxy = serviceHelper.getProxyBusObject(aboutClient, BUS_OBJECT_PATH,
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
				Integer.parseInt(ixit.get("IXITSH_CentralizedManagementVersion")), interfaceVersion);
	}


	/**
	 * Test smart home_v1_02.
	 *
	 * @throws Exception the exception
	 */
	private  void testSmartHome_v1_02() throws Exception {
		ProxyBusObject proxy = serviceHelper.getProxyBusObject(aboutClient, BUS_OBJECT_PATH,
				new Class[] { SmartHomeClient.class });


		proxy.getInterface(SmartHomeClient.class).ApplianceRegistration(ixit.get("IXITSH_WellKnownName"), 
				ixit.get("IXITSH_UniqueName"), ixit.get("IXITSH_DeviceId"));

		CountDownLatch countDownLatch = new CountDownLatch(1);
		ReturnValueHandler signalHandler=new ReturnValueHandler(countDownLatch);
		serviceHelper.registerSignalHandler(signalHandler);

		assertTrue("Did not receive expected ReturnValue signal", countDownLatch.await(getSignalTimeout(), TimeUnit.SECONDS));


		//TODO Continue with the implementation of the test cases, 
		//now we don´t have the correct inferface
		// proxy.getInterface(SmartHomeClient.class).


	}
	
	/**
	 * Test smart home_v1_03.
	 */
	private  void testSmartHome_v1_03() {
		// TODO Auto-generated method stub

	}
	
	/**
	 * Test smart home_v1_04.
	 */
	private  void testSmartHome_v1_04() {
		// TODO Auto-generated method stub

	}
	
	/**
	 * Test smart home_v1_05.
	 */
	private  void testSmartHome_v1_05() {
		// TODO Auto-generated method stub

	}


	/**
	 * Gets the signal timeout.
	 *
	 * @return the signal timeout
	 */
	long getSignalTimeout()
	{
		return SIGNAL_TIMEOUT_IN_SECONDS;
	}

	/**
	 * Release resources.
	 */
	private  void releaseResources() {

		if (serviceHelper != null) {
			serviceHelper.release();
			serviceHelper = null;
		}
	}



	/**
	 * Assert not null.
	 *
	 * @param msg the msg
	 * @param notNull the not null
	 */
	private  void assertNotNull(String msg,
			Object notNull) {

		if(notNull==null){
			logger.error(msg);
			pass=false;
		}

	}



	/**
	 * Gets the service helper.
	 *
	 * @return the service helper
	 */
	protected  ServiceHelper getServiceHelper()
	{
		return new ServiceHelper();
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
	 * @param i the i
	 * @param j the j
	 */
	private  void assertEquals(String errorMsg, int i, int j) {
		if(i!=j){
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
	 * Gets the verdict.
	 *
	 * @return the verdict
	 */
	public String getVerdict() {

		String verdict=null;
		if(pass){
			verdict="PASS";
		}else if(!pass){
			verdict="FAIL";
		}


		return verdict;
	}

}