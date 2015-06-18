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
package com.at4wireless.alljoyn.testcases.conf.lighting;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.ParserConfigurationException;

import org.alljoyn.about.AboutKeys;
import org.alljoyn.about.client.AboutClient;
import org.alljoyn.bus.AboutObjectDescription;
import org.alljoyn.bus.BusException;










import org.alljoyn.bus.ProxyBusObject;
import org.alljoyn.bus.Variant;
import org.xml.sax.SAXException;

import com.at4wireless.alljoyn.core.about.AboutAnnouncementDetails;
import com.at4wireless.alljoyn.core.commons.ServiceAvailabilityHandler;
import com.at4wireless.alljoyn.core.commons.ServiceHelper;
import com.at4wireless.alljoyn.core.commons.log.WindowsLoggerImpl;
import com.at4wireless.alljoyn.core.interfacevalidator.ValidationResult;
import com.at4wireless.alljoyn.core.introspection.BusIntrospector;
import com.at4wireless.alljoyn.core.introspection.bean.InterfaceDetail;
import com.at4wireless.alljoyn.core.introspection.bean.IntrospectionInterface;
import com.at4wireless.alljoyn.core.lighting.LampDetailsBusInterface;
import com.at4wireless.alljoyn.core.lighting.LampDetailsBusObject;
import com.at4wireless.alljoyn.core.lighting.LampParametersBusInterface;
import com.at4wireless.alljoyn.core.lighting.LampParametersBusObject;
import com.at4wireless.alljoyn.core.lighting.LampServiceBusInterface;
import com.at4wireless.alljoyn.core.lighting.LampServiceBusObject;
import com.at4wireless.alljoyn.core.lighting.LampStateBusInterface;
import com.at4wireless.alljoyn.core.lighting.LampStateBusObject;
import com.at4wireless.alljoyn.core.lighting.LampServiceBusInterface.Values;

// TODO: Auto-generated Javadoc
/**
 * The Class LightingService.
 */
public class LightingService {


	/** The port. */
	private  Short PORT = 91;
	
	/** The service helper. */
	private  ServiceHelper serviceHelper;
	
	/** The pass. */
	Boolean pass=true;
	
	/** The inconc. */
	boolean inconc=false;
	
	/** The tag. */
	protected  final String TAG = "LSF_LampTestSuite";
	
	/** The logger. */
	private  final WindowsLoggerImpl logger =  new WindowsLoggerImpl(TAG);
	
	/** The lamp object path. */
	private  String lampObjectPath;
	
	/** The dut app id. */
	private  UUID dutAppId;
	
	/** The dut device id. */
	private  String dutDeviceId;
	
	/** The bus object path. */
	public  String BUS_OBJECT_PATH = "/org/allseen/LSF/Lamp";
	
	/** The signal received. */
	private  boolean signalReceived = false;



	/** The bus application name. */
	private  final String BUS_APPLICATION_NAME = "LSF_LampTestSuite";
	
	/** The annoucement timeout in seconds. */
	public long ANNOUCEMENT_TIMEOUT_IN_SECONDS = 30;
	
	/** The session close timeout in seconds. */
	private long SESSION_CLOSE_TIMEOUT_IN_SECONDS = 5;

	/** The null string. */
	private  String NULL_STRING = "\\0";
	
	/** The lamp state field on off. */
	private  String LAMP_STATE_FIELD_ON_OFF = "OnOff";
	
	/** The lamp state field brightness. */
	private  String LAMP_STATE_FIELD_BRIGHTNESS = "Brightness";
	
	/** The lamp state field hue. */
	private  String LAMP_STATE_FIELD_HUE = "Hue";
	
	/** The lamp state field saturation. */
	private  String LAMP_STATE_FIELD_SATURATION = "Saturation";
	
	/** The lamp state field color temp. */
	private  String LAMP_STATE_FIELD_COLOR_TEMP = "ColorTemp";


	/** The lampservice interface name. */
	private  final String LAMPSERVICE_INTERFACE_NAME = "org.allseen.LSF.LampService";
	
	/** The lampparameters interface name. */
	private  final String LAMPPARAMETERS_INTERFACE_NAME = "org.allseen.LSF.LampParameters";
	
	/** The lampdetails interface name. */
	private  final String LAMPDETAILS_INTERFACE_NAME = "org.allseen.LSF.LampDetails";
	
	/** The lampstate interface name. */
	private  final String LAMPSTATE_INTERFACE_NAME = "org.allseen.LSF.LampState";

	/** The about client. */
	private  AboutClient aboutClient;
	
	/** The lamp state signal handler. */
	private  LampStateSignalHandler lampStateSignalHandler;
	
	/** The lamp state bus object. */
	private  LampStateBusObject lampStateBusObject;
	
	/** The lamp service bus object. */
	private  LampServiceBusObject lampServiceBusObject;
	
	/** The lamp parameters bus object. */
	private  LampParametersBusObject lampParametersBusObject;
	
	/** The lamp details bus object. */
	private  LampDetailsBusObject lampDetailsBusObject;
	
	/** The service availability handler. */
	private  ServiceAvailabilityHandler serviceAvailabilityHandler;
	
	/** The device about announcement. */
	private  AboutAnnouncementDetails deviceAboutAnnouncement;


	/** The ICS l_ lighting service framework. */
	boolean ICSL_LightingServiceFramework=false;

	/** The ICS l_ lamp service interface. */
	boolean ICSL_LampServiceInterface=false;

	/** The ICS l_ lamp parameters interface. */
	boolean ICSL_LampParametersInterface=false;

	/** The ICS l_ lamp details interface. */
	boolean ICSL_LampDetailsInterface=false;

	/** The ICS l_ dimmable. */
	boolean ICSL_Dimmable=false;

	/** The ICS l_ color. */
	boolean ICSL_Color=false;

	/** The ICS l_ color temperature. */
	boolean ICSL_ColorTemperature=false;


	/** The ICS l_ effects. */
	boolean ICSL_Effects=false;

	/** The ICS l_ lamp state interface. */
	boolean ICSL_LampStateInterface=false;



	////////////////
	/** The IXITC o_ app id. */
	String IXITCO_AppId=null;

	/** The IXITC o_ device id. */
	String IXITCO_DeviceId=null;

	/** The IXITC o_ default language. */
	String IXITCO_DefaultLanguage=null;



	////////////////


	/** The IXIT l_ lamp service version. */
	String IXITL_LampServiceVersion=null;

	/** The IXIT l_ lamp parameters version. */
	String IXITL_LampParametersVersion=null;

	/** The IXIT l_ lamp details version. */
	String IXITL_LampDetailsVersion=null;

	/** The IXIT l_ lamp state version. */
	String IXITL_LampStateVersion=null;


	/**
	 * Instantiates a new lighting service.
	 *
	 * @param testCase the test case
	 * @param iCSL_LightingServiceFramework the i cs l_ lighting service framework
	 * @param iCSL_LampServiceInterface the i cs l_ lamp service interface
	 * @param iCSL_LampParametersInterface the i cs l_ lamp parameters interface
	 * @param iCSL_LampDetailsInterface the i cs l_ lamp details interface
	 * @param iCSL_Dimmable the i cs l_ dimmable
	 * @param iCSL_Color the i cs l_ color
	 * @param iCSL_ColorTemperature the i cs l_ color temperature
	 * @param iCSL_Effects the i cs l_ effects
	 * @param iCSL_LampStateInterface the i cs l_ lamp state interface
	 * @param iXITCO_AppId the i xitc o_ app id
	 * @param iXITCO_DeviceId the i xitc o_ device id
	 * @param iXITCO_DefaultLanguage the i xitc o_ default language
	 * @param iXITL_LampServiceVersion the i xit l_ lamp service version
	 * @param iXITL_LampParametersVersion the i xit l_ lamp parameters version
	 * @param iXITL_LampDetailsVersion the i xit l_ lamp details version
	 * @param iXITL_LampStateVersion the i xit l_ lamp state version
	 * @param gPCO_AnnouncementTimeout the g pc o_ announcement timeout
	 * @param gPL_SessionClose the g p l_ session close
	 */
	public LightingService(String testCase,
			boolean iCSL_LightingServiceFramework,
			boolean iCSL_LampServiceInterface,
			boolean iCSL_LampParametersInterface,
			boolean iCSL_LampDetailsInterface, boolean iCSL_Dimmable,
			boolean iCSL_Color, boolean iCSL_ColorTemperature,
			boolean iCSL_Effects, boolean iCSL_LampStateInterface,
			String iXITCO_AppId, String iXITCO_DeviceId,
			String iXITCO_DefaultLanguage, String iXITL_LampServiceVersion,
			String iXITL_LampParametersVersion,
			String iXITL_LampDetailsVersion, String iXITL_LampStateVersion,
			String gPCO_AnnouncementTimeout, String gPL_SessionClose)  {


		ICSL_LightingServiceFramework=iCSL_LightingServiceFramework;
		ICSL_LampServiceInterface=iCSL_LampServiceInterface;
		ICSL_LampParametersInterface=iCSL_LampParametersInterface;
		ICSL_LampDetailsInterface=iCSL_LampDetailsInterface;
		ICSL_Dimmable=iCSL_Dimmable;
		ICSL_Color=iCSL_Color;
		ICSL_ColorTemperature=iCSL_ColorTemperature;
		ICSL_Effects=iCSL_Effects;
		ICSL_LampStateInterface=iCSL_LampStateInterface;
		IXITCO_AppId=iXITCO_AppId;
		IXITCO_DeviceId=iXITCO_DeviceId;
		IXITCO_DefaultLanguage=iXITCO_DefaultLanguage;
		IXITL_LampServiceVersion=iXITL_LampServiceVersion;
		IXITL_LampParametersVersion=iXITL_LampParametersVersion;
		IXITL_LampDetailsVersion=iXITL_LampDetailsVersion;
		IXITL_LampStateVersion=iXITL_LampStateVersion;
		
		ANNOUCEMENT_TIMEOUT_IN_SECONDS = Integer.parseInt(gPCO_AnnouncementTimeout);
		SESSION_CLOSE_TIMEOUT_IN_SECONDS = Integer.parseInt(gPL_SessionClose);


		try{

			runTestCase(testCase);


		}catch(Exception e){
			if(e!=null){
				if(e.getMessage().equals("Timed out waiting for About announcement")){
					fail("Timed out waiting for About announcement");
				}else{
					inconc = true;
					fail("Exception: "+e.toString());
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
		//setUp(IXITCO_DeviceId,IXITCO_AppId);
		setUp();
		logger.info("Running testcase: "+testCase);
		if(testCase.equals("LSF_Lamp-v1-01")){
			testLSF_Lamp_v1_01_InterfaceVersion();			
		}else if(testCase.equals("LSF_Lamp-v1-02")){
			testLSF_Lamp_v1_02_ServiceVersion();
		}else if(testCase.equals("LSF_Lamp-v1-03")){
			testLSF_Lamp_v1_03_ClearLampFault();
		}else if(testCase.equals("LSF_Lamp-v1-04")){
			testLSF_Lamp_v1_04_SetOnOff();
		}else if(testCase.equals("LSF_Lamp-v1-05")){
			testLSF_Lamp_v1_05_SetHue();
		}else if(testCase.equals("LSF_Lamp-v1-06")){
			testLSF_Lamp_v1_06_SetSaturation();
		}else if(testCase.equals("LSF_Lamp-v1-07")){
			testLSF_Lamp_v1_07_SetColorTemp();
		}else if(testCase.equals("LSF_Lamp-v1-08")){
			testLSF_Lamp_v1_08_SetBrightness();
		}else if(testCase.equals("LSF_Lamp-v1-09")){
			testLSF_Lamp_v1_09_TransitionLampState();
		}else if(testCase.equals("LSF_Lamp-v1-10")){
			testLSF_Lamp_v1_10_ApplyPulseEffect();
		}else if(testCase.equals("LSF_Lamp-v1-11")){
			testLSF_Lamp_v1_11_StandardizedInterfacesMatchDefinitions();
		}else if(testCase.equals("LSF_Lamp-v1-12")){
			testLSF_Lamp_v1_12_ParametersInterfaceVersion();
		}else if(testCase.equals("LSF_Lamp-v1-13")){
			testLSF_Lamp_v1_13_GetEnergy_Usage_Milliwatts();
		}else if(testCase.equals("LSF_Lamp-v1-14")){
			testLSF_Lamp_v1_14_GetBrightness_Lumens();
		}else if(testCase.equals("LSF_Lamp-v1-15")){
			testLSF_Lamp_v1_15_GetInterfaceVersion();
		}else if(testCase.equals("LSF_Lamp-v1-16")){
			testLSF_Lamp_v1_16_GetMake();
		}else if(testCase.equals("LSF_Lamp-v1-17")){
			testLSF_Lamp_v1_17_GetModel();
		}else if(testCase.equals("LSF_Lamp-v1-18")){
			testLSF_Lamp_v1_18_GetType();
		}else if(testCase.equals("LSF_Lamp-v1-19")){
			testLSF_Lamp_v1_19_GetLampType();
		}else if(testCase.equals("LSF_Lamp-v1-20")){
			testLSF_Lamp_v1_20_GetLampBaseType();
		}else if(testCase.equals("LSF_Lamp-v1-21")){
			testLSF_Lamp_v1_21_GetLampBeamAngle();
		}else if(testCase.equals("LSF_Lamp-v1-22")){
			testLSF_Lamp_v1_22_GetDimmable();
		}else if(testCase.equals("LSF_Lamp-v1-23")){
			testLSF_Lamp_v1_23_GetColor();
		}else if(testCase.equals("LSF_Lamp-v1-24")){
			testLSF_Lamp_v1_24_GetVariableColorTemp();
		}else if(testCase.equals("LSF_Lamp-v1-25")){
			testLSF_Lamp_v1_25_GetLampID();
		}else if(testCase.equals("LSF_Lamp-v1-26")){
			testLSF_Lamp_v1_26_GetHasEffects();
		}else if(testCase.equals("LSF_Lamp-v1-27")){
			testLSF_Lamp_v1_27_GetMinVoltage();
		}else if(testCase.equals("LSF_Lamp-v1-28")){
			testLSF_Lamp_v1_28_GetMaxVoltage();
		}else if(testCase.equals("LSF_Lamp-v1-29")){
			testLSF_Lamp_v1_29_GetWattage();
		}else if(testCase.equals("LSF_Lamp-v1-30")){
			testLSF_Lamp_v1_30_GetIncandescentEquivalent();
		}else if(testCase.equals("LSF_Lamp-v1-31")){
			testLSF_Lamp_v1_31_GetMaxLumens();
		}else if(testCase.equals("LSF_Lamp-v1-32")){
			testLSF_Lamp_v1_32_GetMinTemperature();
		}else if(testCase.equals("LSF_Lamp-v1-33")){
			testLSF_Lamp_v1_33_GetMaxTemperature();
		}else if(testCase.equals("LSF_Lamp-v1-34")){
			testLSF_Lamp_v1_34_GetColorRenderingIndex();
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
	private void setUp() throws Exception {

		System.out.println("====================================================");
		logger.info("test setUp started");

		try
		{

			dutDeviceId = IXITCO_DeviceId;
			logger.info(String.format("Running LSF_Lamp test case against Device ID: %s", dutDeviceId));
			dutAppId = UUID.fromString(IXITCO_AppId);
			logger.info(String.format("Running LSF_Lamp test case against App ID: %s", dutAppId));

			lampObjectPath = getTestObjectPath();
			logger.info(String.format("Executing lamp test against Stream object found at %s", lampObjectPath));

			signalReceived = false;

			initServiceHelper();

			logger.info("test setUp done");
		}
		catch (Exception e)
		{
			inconc=true;
			logger.info("test setUp thrown an exception: "+ e.getMessage());
			releaseResources();
			throw e;
		}
		System.out.println("====================================================");
	}

	/**
	 * Gets the test object path.
	 *
	 * @return the test object path
	 */
	private  String getTestObjectPath() {
		return BUS_OBJECT_PATH;
	}


	/**
	 * Inits the service helper.
	 *
	 * @throws BusException the bus exception
	 * @throws Exception the exception
	 */
	protected void initServiceHelper() throws BusException, Exception
	{
		releaseServiceHelper();
		serviceHelper = createServiceHelper();

		serviceHelper.initialize(BUS_APPLICATION_NAME, dutDeviceId, dutAppId);

		lampStateBusObject = new LampStateBusObject();
		serviceHelper.registerBusObject(lampStateBusObject, BUS_OBJECT_PATH);

		lampServiceBusObject = new LampServiceBusObject();
		serviceHelper.registerBusObject(lampServiceBusObject, BUS_OBJECT_PATH);

		lampParametersBusObject = new LampParametersBusObject();
		serviceHelper.registerBusObject(lampParametersBusObject, BUS_OBJECT_PATH);

		lampDetailsBusObject = new LampDetailsBusObject();
		serviceHelper.registerBusObject(lampDetailsBusObject, BUS_OBJECT_PATH);

		deviceAboutAnnouncement = waitForNextDeviceAnnouncement();
		logger.info("Partial Verdict: PASS");
		connectAboutClient(deviceAboutAnnouncement);
		logger.info("Partial Verdict: PASS");
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
		return serviceHelper.waitForNextDeviceAnnouncement(ANNOUCEMENT_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS, true);
	}


	/**
	 * Connect about client.
	 *
	 * @param aboutAnnouncement the about announcement
	 * @throws Exception the exception
	 */
	private void connectAboutClient(AboutAnnouncementDetails aboutAnnouncement) throws Exception
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
	 * Creates the service helper.
	 *
	 * @return the service helper
	 */
	protected  ServiceHelper createServiceHelper()
	{
		return new ServiceHelper(logger);
	}


	/**
	 * Release service helper.
	 */
	private  void releaseServiceHelper()
	{
		try
		{
			if (lampStateSignalHandler != null)
			{
				lampStateSignalHandler = null;
			}

			if (aboutClient != null)
			{
				aboutClient.disconnect();
				aboutClient = null;
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
	 * Wait for next announcement and check field value.
	 *
	 * @param fieldName the field name
	 * @param fieldValue the field value
	 * @return the about announcement details
	 * @throws Exception the exception
	 */
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

	/**
	 * Tear down.
	 */
	private  void tearDown() {

		System.out.println("====================================================");
		logger.info("test tearDown started");
		releaseResources();
		logger.info("test tearDown done");
		System.out.println("====================================================");
	}


	/**
	 * Release resources.
	 */
	private  void releaseResources()
	{
		releaseServiceHelper();
	}







	/************************** Test Cases ********************************************/










	public void testLSF_Lamp_v1_01_InterfaceVersion() throws Exception
	{
		int interfaceVersion = 0;
		ProxyBusObject proxy = serviceHelper.getProxyBusObject(aboutClient, BUS_OBJECT_PATH,
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
		assertEquals("LSF_Lamp service interface version does not match", Integer.parseInt(IXITL_LampServiceVersion), interfaceVersion);
		/*if(pass){
	   logger.info("LSF_Lamp service interface version matches IXITL_LampServiceVersion: "+IXITL_LampServiceVersion);
	   logger.info("Partial Verdict: PASS");
   }*/
	}


	/**
	 * Test ls f_ lamp_v1_02_ service version.
	 *
	 * @throws Exception the exception
	 */
	public void testLSF_Lamp_v1_02_ServiceVersion() throws Exception
	{
		int lampServiceVersion = 0;
		ProxyBusObject proxy = serviceHelper.getProxyBusObject(aboutClient, BUS_OBJECT_PATH,
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
		assertEquals("LSF_Lamp service version does not match", Integer.parseInt(IXITL_LampServiceVersion), lampServiceVersion);
	}





	/**
	 * Test ls f_ lamp_v1_03_ clear lamp fault.
	 *
	 * @throws Exception the exception
	 */
	public  void testLSF_Lamp_v1_03_ClearLampFault() throws Exception
	{
		int[] lampFaults = {0};
		int numFaults = 0;
		ProxyBusObject proxy = serviceHelper.getProxyBusObject(aboutClient, BUS_OBJECT_PATH,
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





	/**
	 * Test ls f_ lamp_v1_04_ set on off.
	 *
	 * @throws Exception the exception
	 */
	public void testLSF_Lamp_v1_04_SetOnOff() throws Exception
	{
		boolean onOff = true;
		boolean newOnOff = false;
		ProxyBusObject proxy = serviceHelper.getProxyBusObject(aboutClient, BUS_OBJECT_PATH,
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





	/**
	 * Test ls f_ lamp_v1_05_ set hue.
	 *
	 * @throws Exception the exception
	 */
	public  void testLSF_Lamp_v1_05_SetHue() throws Exception
	{
		int hue = 100;
		int newHue = 0;
		ProxyBusObject proxy = serviceHelper.getProxyBusObject(aboutClient, BUS_OBJECT_PATH,
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






	/**
	 * Test ls f_ lamp_v1_06_ set saturation.
	 *
	 * @throws Exception the exception
	 */
	public  void testLSF_Lamp_v1_06_SetSaturation() throws Exception
	{
		int saturation = 100;
		int newSaturation = 0;
		ProxyBusObject proxy = serviceHelper.getProxyBusObject(aboutClient, BUS_OBJECT_PATH,
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




	/**
	 * Test ls f_ lamp_v1_07_ set color temp.
	 *
	 * @throws Exception the exception
	 */
	public  void testLSF_Lamp_v1_07_SetColorTemp() throws Exception
	{
		if (getVariableColorTemp())
		{
			int colorTemp = 100;
			int newColorTemp = 0;
			ProxyBusObject proxy = serviceHelper.getProxyBusObject(aboutClient, BUS_OBJECT_PATH,
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

	/**
	 * Gets the variable color temp.
	 *
	 * @return the variable color temp
	 * @throws Exception the exception
	 */
	private  boolean getVariableColorTemp() throws Exception
	{
		boolean variableColorTemp = false;
		ProxyBusObject proxy = serviceHelper.getProxyBusObject(aboutClient, BUS_OBJECT_PATH,
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





	/**
	 * Test ls f_ lamp_v1_08_ set brightness.
	 *
	 * @throws Exception the exception
	 */
	public  void testLSF_Lamp_v1_08_SetBrightness() throws Exception
	{
		if (GetDimmable())
		{
			int brightness = 100;
			int newBrightness = 0;
			ProxyBusObject proxy = serviceHelper.getProxyBusObject(aboutClient, BUS_OBJECT_PATH,
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



	/**
	 * Gets the dimmable.
	 *
	 * @return true, if successful
	 * @throws Exception the exception
	 */
	private  boolean GetDimmable() throws Exception
	{
		boolean dimmable = false;
		ProxyBusObject proxy = serviceHelper.getProxyBusObject(aboutClient, BUS_OBJECT_PATH,
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









	/**
	 * Test ls f_ lamp_v1_09_ transition lamp state.
	 *
	 * @throws Exception the exception
	 */
	public  void testLSF_Lamp_v1_09_TransitionLampState() throws Exception
	{


		lampStateSignalHandler = new LampStateSignalHandler();


		serviceHelper.registerSignalHandler(lampStateSignalHandler);
		/// See if we are able to recieve that signal
		///ajMan.createAllJoynSession(aboutClient.getPeerName(), AllJoynManager.LAMP_SERVICE_PORT);

		boolean onOffValue = true;
		int brightnessValue = 10;
		int hueValue 		= 20;
		int saturationValue	= 30;
		int colorTempValue	= 40;
		long timestamp = System.currentTimeMillis() / 1000L;
		int transitionPeriod = 10;
		Map<String,Variant> lampState;

		ProxyBusObject proxy = serviceHelper.getProxyBusObject(aboutClient, BUS_OBJECT_PATH,
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
			//TODO what should correct expected behavior be here?
			//assertEquals("TransitionLampState getOnOff failed ", onOffValue, newOnOff);

			/******************************************************************************/

			Thread.sleep(5000);
			// there should have been enough time for signal to be received
			assertEquals("LSF_Lamp TransitionLampState await signal returns failure. ", true, lampStateSignalHandler.isSignalReceived());
		}
		catch (BusException e)
		{
			logger.info("Exception caught while trying to transition lamp state");
			e.printStackTrace();
			fail("Exception caught while trying to transition lamp state");
		}


	}



	/**
	 * Test ls f_ lamp_v1_10_ apply pulse effect.
	 *
	 * @throws Exception the exception
	 */
	public  void testLSF_Lamp_v1_10_ApplyPulseEffect() throws Exception
	{
		boolean fromOnOffValue 	= true;
		int fromBrightnessValue 	= 10;
		int fromHueValue			= 20;
		int fromSaturationValue	= 30;
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

		ProxyBusObject proxy = serviceHelper.getProxyBusObject(aboutClient, BUS_OBJECT_PATH,
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

	/**
	 * Test ls f_ lamp_v1_11_ standardized interfaces match definitions.
	 *
	 * @throws Exception the exception
	 */
	public  void testLSF_Lamp_v1_11_StandardizedInterfacesMatchDefinitions() throws Exception
	{//TODO Falla
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
			//logger.info("after validate");
			assertTrue(validationResult.getFailureReason(), validationResult.isValid());

			interfacesExposedOnBusBasedOnName = getIntrospector().getInterfacesExposedOnBusBasedOnName(LAMPSERVICE_INTERFACE_NAME);
			for (InterfaceDetail objectDetail : interfacesExposedOnBusBasedOnName)
			{
				for (IntrospectionInterface intfName : objectDetail.getIntrospectionInterfaces())
				{
					logger.info(String.format("Found object at %s implementing %s", objectDetail.getPath(), intfName.getName()));
				}
			}
			//logger.info("before validate");
			logger.info("Checking that LampService interface XML matches");
			validationResult = getInterfaceValidator().validate(interfacesExposedOnBusBasedOnName);
			//logger.info("after validate");
			assertTrue(validationResult.getFailureReason(), validationResult.isValid());

			interfacesExposedOnBusBasedOnName = getIntrospector().getInterfacesExposedOnBusBasedOnName(LAMPDETAILS_INTERFACE_NAME);
			for (InterfaceDetail objectDetail : interfacesExposedOnBusBasedOnName)
			{
				for (IntrospectionInterface intfName : objectDetail.getIntrospectionInterfaces())
				{
					logger.info(String.format("Found object at %s implementing %s", objectDetail.getPath(), intfName.getName()));
				}
			}
			//logger.info("before validate");
			logger.info("Checking that LampDetails interface XML matches");
			validationResult = getInterfaceValidator().validate(interfacesExposedOnBusBasedOnName);
			//logger.info("after validate");
			assertTrue(validationResult.getFailureReason(), validationResult.isValid());

			interfacesExposedOnBusBasedOnName = getIntrospector().getInterfacesExposedOnBusBasedOnName(LAMPSTATE_INTERFACE_NAME);
			for (InterfaceDetail objectDetail : interfacesExposedOnBusBasedOnName)
			{
				for (IntrospectionInterface intfName : objectDetail.getIntrospectionInterfaces())
				{
					logger.info(String.format("Found object at %s implementing %s", objectDetail.getPath(), intfName.getName()));
				}
			}
			//logger.info("before validate");
			logger.info("Checking that LampState interface XML matches");
			validationResult = getInterfaceValidator().validate(interfacesExposedOnBusBasedOnName);
			//logger.info("after validate");
			assertTrue(validationResult.getFailureReason(), validationResult.isValid());

		}
		catch (BusException e)
		{
			logger.info("Exception caught in StandardizedInterfacesMatchDefinitions ");
			e.printStackTrace();
			fail(String.format("Exception caught in StandardizedInterfacesMatchDefinitions"));
		}
	}











	/**
	 * Test ls f_ lamp_v1_12_ parameters interface version.
	 *
	 * @throws Exception the exception
	 */
	public  void testLSF_Lamp_v1_12_ParametersInterfaceVersion() throws Exception
	{
		int interfaceVersion = 0;
		ProxyBusObject proxy = serviceHelper.getProxyBusObject(aboutClient, BUS_OBJECT_PATH,
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
		assertEquals("LSF_Lamp parameters interface version does not match",Integer.parseInt(IXITL_LampParametersVersion) , interfaceVersion);
	}





	/**
	 * Test ls f_ lamp_v1_13_ get energy_ usage_ milliwatts.
	 *
	 * @throws Exception the exception
	 */
	public  void testLSF_Lamp_v1_13_GetEnergy_Usage_Milliwatts() throws Exception
	{
		int energy_Usage_Milliwatts = 0;
		ProxyBusObject proxy = serviceHelper.getProxyBusObject(aboutClient, BUS_OBJECT_PATH,
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

	/**
	 * Test ls f_ lamp_v1_14_ get brightness_ lumens.
	 *
	 * @throws Exception the exception
	 */
	public  void testLSF_Lamp_v1_14_GetBrightness_Lumens() throws Exception
	{
		int brightness_Lumens = 0;
		ProxyBusObject proxy = serviceHelper.getProxyBusObject(aboutClient, BUS_OBJECT_PATH,
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





	/**
	 * Test ls f_ lamp_v1_15_ get interface version.
	 *
	 * @throws Exception the exception
	 */
	public   void testLSF_Lamp_v1_15_GetInterfaceVersion() throws Exception
	{
		int interfaceVersion = 0;
		ProxyBusObject proxy = serviceHelper.getProxyBusObject(aboutClient, BUS_OBJECT_PATH,
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
		assertEquals("LSF_Lamp details interface version does not match", Integer.parseInt(IXITL_LampDetailsVersion), interfaceVersion);
	}





	/**
	 * Test ls f_ lamp_v1_16_ get make.
	 *
	 * @throws Exception the exception
	 */
	public  void testLSF_Lamp_v1_16_GetMake() throws Exception
	{
		int make = 0;
		ProxyBusObject proxy = serviceHelper.getProxyBusObject(aboutClient, BUS_OBJECT_PATH,
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





	/**
	 * Test ls f_ lamp_v1_17_ get model.
	 *
	 * @throws Exception the exception
	 */
	public  void testLSF_Lamp_v1_17_GetModel() throws Exception
	{
		int model = 0;
		ProxyBusObject proxy = serviceHelper.getProxyBusObject(aboutClient, BUS_OBJECT_PATH,
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







	/**
	 * Test ls f_ lamp_v1_18_ get type.
	 *
	 * @throws Exception the exception
	 */
	public  void testLSF_Lamp_v1_18_GetType() throws Exception
	{
		int detailsType = 0;
		ProxyBusObject proxy = serviceHelper.getProxyBusObject(aboutClient, BUS_OBJECT_PATH,
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






	/**
	 * Test ls f_ lamp_v1_19_ get lamp type.
	 *
	 * @throws Exception the exception
	 */
	public  void testLSF_Lamp_v1_19_GetLampType() throws Exception
	{
		int lampType = 0;
		ProxyBusObject proxy = serviceHelper.getProxyBusObject(aboutClient, BUS_OBJECT_PATH,
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





	/**
	 * Test ls f_ lamp_v1_20_ get lamp base type.
	 *
	 * @throws Exception the exception
	 */
	public  void testLSF_Lamp_v1_20_GetLampBaseType() throws Exception
	{
		int lampBaseType = 0;
		ProxyBusObject proxy = serviceHelper.getProxyBusObject(aboutClient, BUS_OBJECT_PATH,
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



	/**
	 * Test ls f_ lamp_v1_21_ get lamp beam angle.
	 *
	 * @throws Exception the exception
	 */
	public  void testLSF_Lamp_v1_21_GetLampBeamAngle() throws Exception
	{
		int lampBeamAngle = 0;
		ProxyBusObject proxy = serviceHelper.getProxyBusObject(aboutClient, BUS_OBJECT_PATH,
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





	/**
	 * Test ls f_ lamp_v1_22_ get dimmable.
	 *
	 * @throws Exception the exception
	 */
	public  void testLSF_Lamp_v1_22_GetDimmable() throws Exception
	{
		GetDimmable();
	}




	/**
	 * Test ls f_ lamp_v1_23_ get color.
	 *
	 * @throws Exception the exception
	 */
	public  void testLSF_Lamp_v1_23_GetColor() throws Exception
	{
		boolean color = false;
		ProxyBusObject proxy = serviceHelper.getProxyBusObject(aboutClient, BUS_OBJECT_PATH,
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


	/**
	 * Test ls f_ lamp_v1_24_ get variable color temp.
	 *
	 * @throws Exception the exception
	 */
	public  void testLSF_Lamp_v1_24_GetVariableColorTemp() throws Exception
	{
		getVariableColorTemp();
	}




	/**
	 * Test ls f_ lamp_v1_25_ get lamp id.
	 *
	 * @throws Exception the exception
	 */
	public  void testLSF_Lamp_v1_25_GetLampID() throws Exception
	{
		String lampID = "";
		ProxyBusObject proxy = serviceHelper.getProxyBusObject(aboutClient, BUS_OBJECT_PATH,
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


	/**
	 * Test ls f_ lamp_v1_26_ get has effects.
	 *
	 * @throws Exception the exception
	 */
	public  void testLSF_Lamp_v1_26_GetHasEffects() throws Exception
	{
		boolean hasEffects = false;
		ProxyBusObject proxy = serviceHelper.getProxyBusObject(aboutClient, BUS_OBJECT_PATH,
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





	/**
	 * Test ls f_ lamp_v1_27_ get min voltage.
	 *
	 * @throws Exception the exception
	 */
	public  void testLSF_Lamp_v1_27_GetMinVoltage() throws Exception
	{
		int minVoltage = 0;
		ProxyBusObject proxy = serviceHelper.getProxyBusObject(aboutClient, BUS_OBJECT_PATH,
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


	/**
	 * Test ls f_ lamp_v1_28_ get max voltage.
	 *
	 * @throws Exception the exception
	 */
	public  void testLSF_Lamp_v1_28_GetMaxVoltage() throws Exception
	{
		int maxVoltage = 0;
		ProxyBusObject proxy = serviceHelper.getProxyBusObject(aboutClient, BUS_OBJECT_PATH,
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




	/**
	 * Test ls f_ lamp_v1_29_ get wattage.
	 *
	 * @throws Exception the exception
	 */
	public  void testLSF_Lamp_v1_29_GetWattage() throws Exception
	{
		int wattage = 0;
		ProxyBusObject proxy = serviceHelper.getProxyBusObject(aboutClient, BUS_OBJECT_PATH,
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






	/**
	 * Test ls f_ lamp_v1_30_ get incandescent equivalent.
	 *
	 * @throws Exception the exception
	 */
	public  void testLSF_Lamp_v1_30_GetIncandescentEquivalent() throws Exception
	{
		int incandescentEquivalent = 0;
		ProxyBusObject proxy = serviceHelper.getProxyBusObject(aboutClient, BUS_OBJECT_PATH,
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


	/**
	 * Test ls f_ lamp_v1_31_ get max lumens.
	 *
	 * @throws Exception the exception
	 */
	public  void testLSF_Lamp_v1_31_GetMaxLumens() throws Exception
	{
		int maxLumens = 0;
		ProxyBusObject proxy = serviceHelper.getProxyBusObject(aboutClient, BUS_OBJECT_PATH,
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


	/**
	 * Test ls f_ lamp_v1_32_ get min temperature.
	 *
	 * @throws Exception the exception
	 */
	public  void testLSF_Lamp_v1_32_GetMinTemperature() throws Exception
	{
		int minTemperature = 0;
		ProxyBusObject proxy = serviceHelper.getProxyBusObject(aboutClient, BUS_OBJECT_PATH,
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


	/**
	 * Test ls f_ lamp_v1_33_ get max temperature.
	 *
	 * @throws Exception the exception
	 */
	public  void testLSF_Lamp_v1_33_GetMaxTemperature() throws Exception
	{
		int maxTemperature = 0;
		ProxyBusObject proxy = serviceHelper.getProxyBusObject(aboutClient, BUS_OBJECT_PATH,
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


	/**
	 * Test ls f_ lamp_v1_34_ get color rendering index.
	 *
	 * @throws Exception the exception
	 */
	public  void testLSF_Lamp_v1_34_GetColorRenderingIndex() throws Exception
	{
		int colorRenderingIndex = 0;
		ProxyBusObject proxy = serviceHelper.getProxyBusObject(aboutClient, BUS_OBJECT_PATH,
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

	/**
	 * Gets the interface validator.
	 *
	 * @return the interface validator
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ParserConfigurationException the parser configuration exception
	 * @throws SAXException the SAX exception
	 */
	protected  LampInterfaceValidator getInterfaceValidator() throws IOException, ParserConfigurationException, SAXException
	{
		return new LampInterfaceValidator();
	}


	// Examine interfaces found at our bus object for any that do NOT appear in our XML file.
	/**
	 * Check for unknown interfaces from about announcement.
	 *
	 * @throws Exception the exception
	 */
	private  void checkForUnknownInterfacesFromAboutAnnouncement() throws Exception
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
	 * Gets the introspector.
	 *
	 * @return the introspector
	 */
	BusIntrospector getIntrospector()
	{
		return serviceHelper.getBusIntrospector(aboutClient);
	}

	/**
	 * Gets the lamp state map.
	 *
	 * @param onOffString the on off string
	 * @param brightnessString the brightness string
	 * @param hueString the hue string
	 * @param saturationString the saturation string
	 * @param colorTempString the color temp string
	 * @return the lamp state map
	 */
	private  Map<String, Variant> getLampStateMap(String onOffString, String brightnessString,  String hueString, String saturationString, String colorTempString)
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


	/**
	 * Gets the variant.
	 *
	 * @param data the data
	 * @param isOnOff the is on off
	 * @return the variant
	 */
	private  Variant getVariant(String data, boolean isOnOff)
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



	/**
	 * Fail.
	 *
	 * @param msg the msg
	 */
	private  void fail(String msg) {
		// TODO Auto-generated method stub

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
		} else {
			logger.info("Partial Verdict: PASS");
		}
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
		// TODO Auto-generated method stub

		if(!string1.equals(string2)){
			fail(errorMsg);
		} else {
			logger.info("Partial Verdict: PASS");
		}
	}

	/**
	 * Assert equals.
	 *
	 * @param errorMsg the error msg
	 * @param bool the bool
	 * @param booleanValue the boolean value
	 */
	private  void assertEquals(String errorMsg, boolean bool,
			boolean booleanValue) {
		if(bool!=booleanValue){
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
		// TODO Auto-generated method stub
		if(!bool){
			fail(errorMsg);
		} else {
			logger.info("Partial Verdict: PASS");
		}

	}

	/**
	 * Gets the verdict.
	 *
	 * @return the verdict
	 */
	public String getVerdict() {
		String verdict=null;
		if(inconc==true){
			verdict="INCONC";
		}else if(pass){
			verdict="PASS";
		}else if(!pass){
			verdict="FAIL";
		}
		return verdict;
	}
}
