/*
 * Copyright AllSeen Alliance. All rights reserved.
 *
 *    Permission to use, copy, modify, and/or distribute this software for any
 *    purpose with or without fee is hereby granted, provided that the above
 *    copyright notice and this permission notice appear in all copies.
 *
 *    THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 *    WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 *    MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 *    ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 *    WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 *    ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 *    OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */
package com.at4wireless.alljoyn.testcases.conf.gateway;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.ParserConfigurationException;

import org.alljoyn.about.AboutKeys;
import org.alljoyn.about.client.AboutClient;
import org.alljoyn.gatewaycontroller.sdk.GatewayController;
import org.xml.sax.SAXException;

import com.at4wireless.alljoyn.core.about.AboutAnnouncementDetails;
import com.at4wireless.alljoyn.core.commons.ServiceAvailabilityHandler;
import com.at4wireless.alljoyn.core.commons.log.WindowsLoggerImpl;
import com.at4wireless.alljoyn.core.gwagent.GWAgentHelper;
import com.at4wireless.alljoyn.core.gwagent.GWAgentInterfaceValidator;
import com.at4wireless.alljoyn.core.interfacevalidator.ValidationResult;
import com.at4wireless.alljoyn.core.introspection.BusIntrospector;
import com.at4wireless.alljoyn.core.introspection.bean.InterfaceDetail;
import com.at4wireless.alljoyn.core.introspection.bean.IntrospectionInterface;

// TODO: Auto-generated Javadoc
/**
 * The Class GatewayService.
 */
public class GatewayService { 



	 /** The pass. */
 	Boolean pass=true;
	
	/** The tag. */
	private  final String TAG = "GWAgentTestSuite";
	
	/** The logger. */
	private  final WindowsLoggerImpl logger =  new WindowsLoggerImpl(TAG);
	
	/** The bus application name. */
	private  final String BUS_APPLICATION_NAME = "GWAgentTestSuite";
	
	/** The annoucement timeout in seconds. */
	public  long ANNOUCEMENT_TIMEOUT_IN_SECONDS = 30;
	
	/** The session close timeout in seconds. */
	private  long SESSION_CLOSE_TIMEOUT_IN_SECONDS = 5;

	/** The gwagent helper. */
	private  GWAgentHelper gwagentHelper;
	
	/** The about client. */
	private  AboutClient aboutClient;
	
	/** The gateway controller. */
	private  GatewayController gatewayController;

	/** The device about announcement. */
	private  AboutAnnouncementDetails deviceAboutAnnouncement;


	/** The dut app id. */
	private  UUID dutAppId;
	
	/** The dut device id. */
	private  String dutDeviceId;
	
	/** The service availability handler. */
	private  ServiceAvailabilityHandler serviceAvailabilityHandler;
	
	/** The key store path. */
	private  String keyStorePath="/KeyStore";




	 /** The ICS g_ gateway service framework. */
 	boolean ICSG_GatewayServiceFramework=false;
	 
 	/** The ICS g_ profile management interface. */
 	boolean ICSG_ProfileManagementInterface=false;
	 
 	/** The ICS g_ app access interface. */
 	boolean ICSG_AppAccessInterface=false;
	 
 	/** The ICS g_ app management interface. */
 	boolean ICSG_AppManagementInterface=false;


	////////////////
	 /** The IXITC o_ app id. */
	String IXITCO_AppId=null;

	 /** The IXITC o_ device id. */
 	String IXITCO_DeviceId=null;

	 /** The IXITC o_ default language. */
 	String IXITCO_DefaultLanguage=null;



	////////////////



	 /** The IXIT g_ app mgmt version. */
	String IXITG_AppMgmtVersion=null;
	 
 	/** The IXIT g_ ctrl app version. */
 	String IXITG_CtrlAppVersion=null;
	 
 	/** The IXIT g_ ctrl access version. */
 	String IXITG_CtrlAccessVersion=null;
	 
 	/** The IXIT g_ ctrl acl version. */
 	String IXITG_CtrlAclVersion=null;
	 
 	/** The IXIT g_ conn app version. */
 	String IXITG_ConnAppVersion=null;




	/**
	 * Instantiates a new gateway service.
	 *
	 * @param testCase the test case
	 * @param iCSG_GatewayServiceFramework the i cs g_ gateway service framework
	 * @param iCSG_ProfileManagementInterface the i cs g_ profile management interface
	 * @param iCSG_AppAccessInterface the i cs g_ app access interface
	 * @param iCSG_AppManagementInterface the i cs g_ app management interface
	 * @param iXITCO_AppId the i xitc o_ app id
	 * @param iXITCO_DeviceId the i xitc o_ device id
	 * @param iXITCO_DefaultLanguage the i xitc o_ default language
	 * @param iXITG_AppMgmtVersion the i xit g_ app mgmt version
	 * @param iXITG_CtrlAppVersion the i xit g_ ctrl app version
	 * @param iXITG_CtrlAccessVersion the i xit g_ ctrl access version
	 * @param iXITG_CtrlAclVersion the i xit g_ ctrl acl version
	 * @param iXITG_ConnAppVersion the i xit g_ conn app version
	 * @param gPCO_AnnouncementTimeout the g pc o_ announcement timeout
	 * @param gPG_SessionClose the g p g_ session close
	 */
	public GatewayService(String testCase,
			boolean iCSG_GatewayServiceFramework,
			boolean iCSG_ProfileManagementInterface,
			boolean iCSG_AppAccessInterface,
			boolean iCSG_AppManagementInterface, String iXITCO_AppId,
			String iXITCO_DeviceId, String iXITCO_DefaultLanguage,
			String iXITG_AppMgmtVersion, String iXITG_CtrlAppVersion,
			String iXITG_CtrlAccessVersion, String iXITG_CtrlAclVersion,
			String iXITG_ConnAppVersion, String gPCO_AnnouncementTimeout,
			String gPG_SessionClose) {




		ICSG_GatewayServiceFramework=iCSG_GatewayServiceFramework;
		ICSG_ProfileManagementInterface=iCSG_ProfileManagementInterface;
		ICSG_AppAccessInterface=iCSG_AppAccessInterface;
		ICSG_AppManagementInterface=iCSG_AppManagementInterface;


		////////////////
		IXITCO_AppId=iXITCO_AppId;

		IXITCO_DeviceId=iXITCO_DeviceId;

		IXITCO_DefaultLanguage=iXITCO_DefaultLanguage;



		////////////////



		IXITG_AppMgmtVersion=iXITG_AppMgmtVersion;
		IXITG_CtrlAppVersion=iXITG_CtrlAppVersion;
		IXITG_CtrlAccessVersion=iXITG_CtrlAccessVersion;
		IXITG_CtrlAclVersion=iXITG_CtrlAclVersion;
		IXITG_ConnAppVersion=iXITG_ConnAppVersion;
		
		ANNOUCEMENT_TIMEOUT_IN_SECONDS = Integer.parseInt(gPCO_AnnouncementTimeout);
		SESSION_CLOSE_TIMEOUT_IN_SECONDS = Integer.parseInt(gPG_SessionClose);

		try{
			runTestCase(testCase);
		}catch(Exception e){
			if(e.getMessage().equals("Timed out waiting for About announcement")){
				fail("Timed out waiting for About announcement");
			}else{
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
	public  void runTestCase(String testCase) throws Exception{
		//setUp(IXITCO_DeviceId,IXITCO_AppId);		
		setUp();
		logger.info("Running testcase: "+testCase);
		if(testCase.equals("GWAgent-v1-01")){
			testGWAgent_v1_01_GWAgentInterfacesMatchDefinitions();
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

		System.out.println("====================================================");
		logger.debug("test setUp started");

		try
		{
			dutDeviceId = IXITCO_DeviceId;
			logger.debug(String.format("Running GWAgent test case against Device ID: %s", dutDeviceId));
			dutAppId = UUID.fromString(IXITCO_AppId);
			logger.debug(String.format("Running GWAgent test case against App ID: %s", dutAppId));

			logger.debug(String.format("Running GWAgent test case using KeyStorePath: %s", keyStorePath));

			initGWAgentHelper();

			logger.debug("test setUp done");
			System.out.println("====================================================");
		}
		catch (Exception e)
		{
			logger.debug("test setUp thrown an exception "+ e.toString());
			releaseResources();
			throw e;
		}
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
	 * Test gw agent_v1_01_ gw agent interfaces match definitions.
	 *
	 * @throws Exception the exception
	 */
	public void testGWAgent_v1_01_GWAgentInterfacesMatchDefinitions() throws Exception
	{
		List<InterfaceDetail> gwagentCtrlIntrospectionInterfacesExposedOnBus = new ArrayList<InterfaceDetail>();
		List<InterfaceDetail> gwagentCtrlAppMgmtIntrospectionInterfacesExposedOnBus = getIntrospector().getInterfacesExposedOnBusUnderSpecifiedPathBasedOnName("/",
				"org.alljoyn.gwagent.ctrl.AppMgmt");
		for (InterfaceDetail gwObjectDetail : gwagentCtrlAppMgmtIntrospectionInterfacesExposedOnBus)
		{
			if (gwObjectDetail.getPath().equals("/gw"))
			{
				gwagentCtrlIntrospectionInterfacesExposedOnBus.add(gwObjectDetail);
				List<InterfaceDetail> gwagentCtrlAppIntrospectionInterfacesExposedOnBus = getIntrospector().getInterfacesExposedOnBusUnderSpecifiedPathBasedOnName(
						gwObjectDetail.getPath(), "org.alljoyn.gwagent.ctrl.App");
				for (InterfaceDetail gwAppObjectDetail : gwagentCtrlAppIntrospectionInterfacesExposedOnBus)
				{
					gwagentCtrlIntrospectionInterfacesExposedOnBus.add(gwAppObjectDetail);
				}
				List<InterfaceDetail> gwagentCtrlAclMgmtIntrospectionInterfacesExposedOnBus = getIntrospector().getInterfacesExposedOnBusUnderSpecifiedPathBasedOnName(
						gwObjectDetail.getPath(), "org.alljoyn.gwagent.ctrl.AclMgmt");
				for (InterfaceDetail gwAclMgmtObjectDetail : gwagentCtrlAclMgmtIntrospectionInterfacesExposedOnBus)
				{
					gwagentCtrlIntrospectionInterfacesExposedOnBus.add(gwAclMgmtObjectDetail);
				}
				for (InterfaceDetail gwAppObjectDetail : gwagentCtrlAppIntrospectionInterfacesExposedOnBus)
				{
					boolean matched = false;
					for (InterfaceDetail gwAclMgmtObjectDetail : gwagentCtrlAclMgmtIntrospectionInterfacesExposedOnBus)
					{
						if (gwAppObjectDetail.getPath().equals(gwAclMgmtObjectDetail.getPath()))
						{
							matched = true;
							List<InterfaceDetail> gwagentCtrlAclIntrospectionInterfacesExposedOnBus = getIntrospector().getInterfacesExposedOnBusUnderSpecifiedPathBasedOnName(
									gwObjectDetail.getPath(), "org.alljoyn.gwagent.ctrl.Acl");
							for (InterfaceDetail gwAclObjectDetail : gwagentCtrlAclIntrospectionInterfacesExposedOnBus)
							{
								gwagentCtrlIntrospectionInterfacesExposedOnBus.add(gwAclObjectDetail);
							}
						}
					}
					assertTrue(String.format("Application at path %s does NOT have ALL relevant interfaces", gwAppObjectDetail.getPath()), matched);
				}
			}
		}
		for (InterfaceDetail objectDetail : gwagentCtrlIntrospectionInterfacesExposedOnBus)
		{
			for (IntrospectionInterface intfName : objectDetail.getIntrospectionInterfaces())
			{
				logger.info(String.format("Found object at %s implementing %s", objectDetail.getPath(), intfName.getName()));
			}
		}
		ValidationResult validationResult = getInterfaceValidator().validate(gwagentCtrlIntrospectionInterfacesExposedOnBus);

		assertTrue(validationResult.getFailureReason(), validationResult.isValid());
	}




	/**
	 * Gets the interface validator.
	 *
	 * @return the interface validator
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ParserConfigurationException the parser configuration exception
	 * @throws SAXException the SAX exception
	 */
	protected GWAgentInterfaceValidator getInterfaceValidator() throws IOException, ParserConfigurationException, SAXException
	{
		return new GWAgentInterfaceValidator();
	}

	/**
	 * Gets the introspector.
	 *
	 * @return the introspector
	 */
	protected BusIntrospector getIntrospector()
	{
		return gwagentHelper.getBusIntrospector(aboutClient);
	}

	/**
	 * Release resources.
	 */
	private  void releaseResources()
	{
		releaseGWAgentHelper();
	}

	/**
	 * Inits the gw agent helper.
	 *
	 * @throws Exception the exception
	 */
	private  void initGWAgentHelper() throws Exception {
		releaseGWAgentHelper();
		gwagentHelper = createGWAgentHelper();


		gwagentHelper.initialize(BUS_APPLICATION_NAME, dutDeviceId, dutAppId);


		deviceAboutAnnouncement = waitForNextAnnouncementAndCheckFieldValue(AboutKeys.ABOUT_DEVICE_NAME, "AllJoyn Gateway Agent"); // waitForNextDeviceAnnouncement();

		connectAboutClient(deviceAboutAnnouncement);
		connectGatewayController(deviceAboutAnnouncement);


		gwagentHelper.enableAuthentication(keyStorePath);

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
	 * Wait for next device announcement.
	 *
	 * @return the about announcement details
	 * @throws Exception the exception
	 */
	private  AboutAnnouncementDetails waitForNextDeviceAnnouncement() throws Exception
	{
		logger.info("Waiting for About announcement");
		return gwagentHelper.waitForNextDeviceAnnouncement(ANNOUCEMENT_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS, true);
	}

	/**
	 * Connect gateway controller.
	 *
	 * @param aboutAnnouncement the about announcement
	 * @throws Exception the exception
	 */
	private  void connectGatewayController(
			AboutAnnouncementDetails aboutAnnouncement) throws Exception {
		gatewayController = gwagentHelper.connectGatewayController(aboutAnnouncement);

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
	//	aboutClient = gwagentHelper.connectAboutClient(aboutAnnouncement, serviceAvailabilityHandler);
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
	 * Creates the gw agent helper.
	 *
	 * @return the GW agent helper
	 */
	protected  GWAgentHelper createGWAgentHelper()
	{
		return new GWAgentHelper(logger);
	}

	/**
	 * Release gw agent helper.
	 */
	private  void releaseGWAgentHelper() {
		try
		{
			if (aboutClient != null)
			{
				aboutClient.disconnect();
				aboutClient = null;
			}
			if (gatewayController != null)
			{
				gatewayController.shutdown();
				gatewayController = null;
			}
			if (gwagentHelper != null)
			{
				gwagentHelper.release();
				waitForSessionToClose();
				gwagentHelper = null;
			}
		}
		catch (Exception ex)
		{
			logger.debug("Exception releasing resources", ex);
		}

	}

	/**
	 * Wait for session to close.
	 *
	 * @throws Exception the exception
	 */
	private  void waitForSessionToClose() throws Exception {
		logger.info("Waiting for session to close");
		gwagentHelper.waitForSessionToClose(SESSION_CLOSE_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS);
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
	 * Fail.
	 *
	 * @param msg the msg
	 */
	private  void fail(String msg) {

		logger.error(msg);
		logger.info("Partial Verdict: FAIL");
		pass=false;
	}
}
