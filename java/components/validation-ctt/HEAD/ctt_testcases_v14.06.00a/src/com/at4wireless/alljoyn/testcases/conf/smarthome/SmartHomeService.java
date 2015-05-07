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

public class SmartHomeService {


	Boolean pass=true;
	protected  final String TAG = "SmartHomeTestSuite";
	private  final String BUS_APPLICATION_NAME = "SmartHome";
	public  String BUS_OBJECT_PATH = "/org/alljoyn/SmartHome/CentralizedManagement";
	private long SIGNAL_TIMEOUT_IN_SECONDS = 30;
	private  final WindowsLoggerImpl logger =  new WindowsLoggerImpl(TAG);
	protected  AboutAnnouncementDetails deviceAboutAnnouncement;
	private  ServiceHelper serviceHelper;
	private  UUID dutAppId;
	private  String dutDeviceId;
	private  BusIntrospector busIntrospector;
	private  AboutClient aboutClient;
	private  int  timeOut=30;


	boolean ICSSH_SmartHomeServiceFramework=false;
	boolean ICSSH_CentralizedManagementInterface=false;

	/*String  IXITCO_AppId=null;
	String IXITCO_DeviceId=null;
	String IXITCO_DefaultLanguage=null;

	String IXITSH_CentralizedManagementVersion=null;
	String IXITSH_WellKnownName=null;
	String IXITSH_UniqueName=null;
	String IXITSH_DeviceId=null;
	String IXITSH_HeartBeatInterval=null;*/

	Map<String,String> ixit;

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
	
	private  void testSmartHome_v1_03() {
		// TODO Auto-generated method stub

	}
	
	private  void testSmartHome_v1_04() {
		// TODO Auto-generated method stub

	}
	
	private  void testSmartHome_v1_05() {
		// TODO Auto-generated method stub

	}


	long getSignalTimeout()
	{
		return SIGNAL_TIMEOUT_IN_SECONDS;
	}

	private  void releaseResources() {

		if (serviceHelper != null) {
			serviceHelper.release();
			serviceHelper = null;
		}
	}



	private  void assertNotNull(String msg,
			Object notNull) {

		if(notNull==null){
			logger.error(msg);
			pass=false;
		}

	}



	protected  ServiceHelper getServiceHelper()
	{
		return new ServiceHelper();
	}



	private  void fail(String msg) {


		logger.error(msg);
		pass=false;

	}


	private  void assertEquals(String errorMsg, int i, int j) {
		if(i!=j){
			fail(errorMsg);



		}

	}


	private  void assertTrue(String errorMsg,
			boolean bool) {

		if(!bool){
			fail(errorMsg);

		}

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

}
