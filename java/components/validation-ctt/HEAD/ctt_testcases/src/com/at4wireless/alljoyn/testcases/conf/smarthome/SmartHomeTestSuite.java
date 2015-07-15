/*******************************************************************************
 *  Copyright AllSeen Alliance. All rights reserved.
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
package com.at4wireless.alljoyn.testcases.conf.smarthome;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.alljoyn.about.client.AboutClient;
import org.alljoyn.bus.BusException;
import org.alljoyn.bus.ProxyBusObject;
import org.alljoyn.smarthome.centralizedmanagement.client.SmartHomeClient;

import com.at4wireless.alljoyn.core.about.AboutAnnouncementDetails;
import com.at4wireless.alljoyn.core.commons.ServiceHelper;
import com.at4wireless.alljoyn.core.commons.log.WindowsLoggerImpl;

public class SmartHomeTestSuite
{
	private static final String TAG = "SmartHomeTestSuite";
	private static final WindowsLoggerImpl logger =  new WindowsLoggerImpl(TAG);
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
	Map<String, Boolean> ics;
	Map<String, String> ixit;
	private long ANNOUNCEMENT_TIMEOUT_IN_SECONDS = 30;

	public SmartHomeTestSuite(String testCase,
			boolean iCSSH_SmartHomeServiceFramework,
			boolean iCSSH_CentralizedManagementInterface, String iXITCO_AppId,
			String iXITCO_DeviceId, String iXITCO_DefaultLanguage,
			String iXITSH_CentralizedManagementVersion,
			String iXITSH_WellKnownName, String iXITSH_UniqueName,
			String iXITSH_DeviceId, String iXITSH_HeartBeatInterval,
			String gPCO_AnnouncementTimeout, String gPSH_Signal)
	{
		ics = new HashMap<String, Boolean>();
		ixit = new HashMap<String,String>();
		
		ics.put("ICSSH_SmartHomeServiceFramework", iCSSH_SmartHomeServiceFramework);
		ics.put("ICSSH_CentralizedManagementInterface", iCSSH_CentralizedManagementInterface);

		ixit.put("IXITCO_AppId", iXITCO_AppId);
		ixit.put("IXITCO_DeviceId", iXITCO_DeviceId);
		ixit.put("IXITSH_CentralizedManagementVersion", iXITSH_CentralizedManagementVersion);
		ixit.put("IXITSH_WellKnownName", iXITSH_WellKnownName);
		ixit.put("IXITSH_UniqueName", iXITSH_UniqueName);
		ixit.put("IXITSH_DeviceId", iXITSH_DeviceId);
		ixit.put("IXITSH_HeartBeatInterval", iXITSH_HeartBeatInterval);
		
		ANNOUNCEMENT_TIMEOUT_IN_SECONDS = Integer.parseInt(gPCO_AnnouncementTimeout);
		SIGNAL_TIMEOUT_IN_SECONDS = Integer.parseInt(gPSH_Signal);
		
		try
		{
			runTestCase(testCase);
		}
		catch (Exception e)
		{
			if (e.getMessage().equals("Timed out waiting for About announcement"))
			{
				fail("Timed out waiting for About announcement");
			}
			else
			{
				fail("Exception: "+e.toString());
			}
		}
	}

	private void runTestCase(String testCase) throws Exception
	{
		setUp();

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

		tearDown();
	}
	
	private void setUp() throws Exception
	{		
		try
		{
			logger.noTag("====================================================");
			logger.info("test setUp started");


			dutDeviceId = ixit.get("iXITCO_DeviceId");
			logger.info(String.format("Running ControlPanel test case against Device ID: %s", dutDeviceId));
			dutAppId = UUID.fromString(ixit.get("iXITCO_AppId"));
			logger.info(String.format("Running ControlPanel test case against App ID: %s", dutAppId));

			serviceHelper = getServiceHelper();
			serviceHelper.initialize(BUS_APPLICATION_NAME, dutDeviceId, dutAppId);

			deviceAboutAnnouncement = serviceHelper.waitForNextDeviceAnnouncement(ANNOUNCEMENT_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS);
			assertNotNull("Timed out waiting for About announcement", deviceAboutAnnouncement);
			//aboutClient = serviceHelper.connectAboutClient(deviceAboutAnnouncement);

			logger.info("test setUp done");
			logger.noTag("====================================================");
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
		logger.noTag("====================================================");
		logger.debug("test tearDown started");
		releaseResources();
		logger.noTag("test tearDown done");
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
				Integer.parseInt(ixit.get("IXITSH_CentralizedManagementVersion")), interfaceVersion);
	}

	private void testSmartHome_v1_02() throws Exception
	{
		ProxyBusObject proxy = serviceHelper.getProxyBusObject(deviceAboutAnnouncement, BUS_OBJECT_PATH,
				new Class[] { SmartHomeClient.class });


		proxy.getInterface(SmartHomeClient.class).ApplianceRegistration(ixit.get("IXITSH_WellKnownName"), 
				ixit.get("IXITSH_UniqueName"), ixit.get("IXITSH_DeviceId"));

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
		return new ServiceHelper( logger);
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
