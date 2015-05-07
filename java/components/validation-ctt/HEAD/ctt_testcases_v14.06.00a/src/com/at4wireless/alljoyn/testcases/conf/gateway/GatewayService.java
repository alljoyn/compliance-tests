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

public class GatewayService { 



	 Boolean pass=true;
	private  final String TAG = "GWAgentTestSuite";
	private  final WindowsLoggerImpl logger =  new WindowsLoggerImpl(TAG);
	private  final String BUS_APPLICATION_NAME = "GWAgentTestSuite";
	public  long ANNOUCEMENT_TIMEOUT_IN_SECONDS = 30;
	private  long SESSION_CLOSE_TIMEOUT_IN_SECONDS = 5;

	private  GWAgentHelper gwagentHelper;
	private  AboutClient aboutClient;
	private  GatewayController gatewayController;

	private  AboutAnnouncementDetails deviceAboutAnnouncement;


	private  UUID dutAppId;
	private  String dutDeviceId;
	private  ServiceAvailabilityHandler serviceAvailabilityHandler;
	private  String keyStorePath="/KeyStore";




	 boolean ICSG_GatewayServiceFramework=false;
	 boolean ICSG_ProfileManagementInterface=false;
	 boolean ICSG_AppAccessInterface=false;
	 boolean ICSG_AppManagementInterface=false;


	////////////////
	 String IXITCO_AppId=null;

	 String IXITCO_DeviceId=null;

	 String IXITCO_DefaultLanguage=null;



	////////////////



	 String IXITG_AppMgmtVersion=null;
	 String IXITG_CtrlAppVersion=null;
	 String IXITG_CtrlAccessVersion=null;
	 String IXITG_CtrlAclVersion=null;
	 String IXITG_ConnAppVersion=null;




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
	
	private  void tearDown() {
		System.out.println("====================================================");
		logger.debug("test tearDown started");
		releaseResources();
		logger.debug("test tearDown done");
		System.out.println("====================================================");

	}

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




	protected GWAgentInterfaceValidator getInterfaceValidator() throws IOException, ParserConfigurationException, SAXException
	{
		return new GWAgentInterfaceValidator();
	}

	protected BusIntrospector getIntrospector()
	{
		return gwagentHelper.getBusIntrospector(aboutClient);
	}

	private  void releaseResources()
	{
		releaseGWAgentHelper();
	}

	private  void initGWAgentHelper() throws Exception {
		releaseGWAgentHelper();
		gwagentHelper = createGWAgentHelper();


		gwagentHelper.initialize(BUS_APPLICATION_NAME, dutDeviceId, dutAppId);


		deviceAboutAnnouncement = waitForNextAnnouncementAndCheckFieldValue(AboutKeys.ABOUT_DEVICE_NAME, "AllJoyn Gateway Agent"); // waitForNextDeviceAnnouncement();

		connectAboutClient(deviceAboutAnnouncement);
		connectGatewayController(deviceAboutAnnouncement);


		gwagentHelper.enableAuthentication(keyStorePath);

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

	private  AboutAnnouncementDetails waitForNextDeviceAnnouncement() throws Exception
	{
		logger.info("Waiting for About announcement");
		return gwagentHelper.waitForNextDeviceAnnouncement(ANNOUCEMENT_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS, true);
	}

	private  void connectGatewayController(
			AboutAnnouncementDetails aboutAnnouncement) throws Exception {
		gatewayController = gwagentHelper.connectGatewayController(aboutAnnouncement);

	}

	private  void connectAboutClient(AboutAnnouncementDetails aboutAnnouncement) throws Exception
	{
		serviceAvailabilityHandler = createServiceAvailabilityHandler();
	//	aboutClient = gwagentHelper.connectAboutClient(aboutAnnouncement, serviceAvailabilityHandler);
	}

	protected  ServiceAvailabilityHandler createServiceAvailabilityHandler()
	{
		return new ServiceAvailabilityHandler();
	}

	protected  GWAgentHelper createGWAgentHelper()
	{
		return new GWAgentHelper(logger);
	}

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

	private  void waitForSessionToClose() throws Exception {
		logger.info("Waiting for session to close");
		gwagentHelper.waitForSessionToClose(SESSION_CLOSE_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS);
	}

	public String getVerdict() {

		String verdict=null;
		if(pass){
			verdict="PASS";
		}else if(!pass){
			verdict="FAIL";
		}
		return verdict;
	}

	private  void assertEquals(String errorMsg,
			String string1, String string2) {
		
		if(!string1.equals(string2)){
			fail(errorMsg);
		}
	}

	private  void assertTrue(String errorMsg,
			boolean bool) {
		
		if(!bool){
			fail(errorMsg);
		}
	}

	private  void fail(String msg) {

		logger.error(msg);
		logger.info("Partial Verdict: FAIL");
		pass=false;
	}
}
