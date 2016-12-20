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
package com.at4wireless.alljoyn.testcases.conf.controlpanel;


import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;

import org.alljoyn.about.client.AboutClient;
import org.alljoyn.bus.AnnotationBusException;
import org.alljoyn.bus.BusException;
import org.alljoyn.bus.ErrorReplyBusException;
import org.alljoyn.bus.Variant;
import org.alljoyn.ioe.controlpanelservice.communication.interfaces.ActionControl;
import org.alljoyn.ioe.controlpanelservice.communication.interfaces.ActionControlSecured;
import org.alljoyn.ioe.controlpanelservice.communication.interfaces.ActionControlSuper;
import org.alljoyn.ioe.controlpanelservice.communication.interfaces.AlertDialog;
import org.alljoyn.ioe.controlpanelservice.communication.interfaces.AlertDialogSecured;
import org.alljoyn.ioe.controlpanelservice.communication.interfaces.AlertDialogSuper;
import org.alljoyn.ioe.controlpanelservice.communication.interfaces.Container;
import org.alljoyn.ioe.controlpanelservice.communication.interfaces.ContainerSecured;
import org.alljoyn.ioe.controlpanelservice.communication.interfaces.ContainerSuper;
import org.alljoyn.ioe.controlpanelservice.communication.interfaces.ControlPanel;
import org.alljoyn.ioe.controlpanelservice.communication.interfaces.HTTPControl;
import org.alljoyn.ioe.controlpanelservice.communication.interfaces.Label;
import org.alljoyn.ioe.controlpanelservice.communication.interfaces.ListPropertyControl;
import org.alljoyn.ioe.controlpanelservice.communication.interfaces.ListPropertyControlSecured;
import org.alljoyn.ioe.controlpanelservice.communication.interfaces.ListPropertyControlSuper;
import org.alljoyn.ioe.controlpanelservice.communication.interfaces.NotificationAction;
import org.alljoyn.ioe.controlpanelservice.communication.interfaces.PropertyControl;
import org.alljoyn.ioe.controlpanelservice.communication.interfaces.PropertyControlSecured;
import org.alljoyn.ioe.controlpanelservice.communication.interfaces.PropertyControlSuper;
import org.alljoyn.ioe.controlpanelservice.ui.ajstruct.ListPropertyWidgetRecordAJ;
import org.alljoyn.ioe.controlpanelservice.ui.ajstruct.PropertyWidgetConstrainToValuesAJ;
import org.alljoyn.ioe.controlpanelservice.ui.ajstruct.PropertyWidgetRangeConstraintAJ;
import org.alljoyn.ioe.controlpanelservice.ui.ajstruct.PropertyWidgetThreeShortAJ;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.xml.sax.SAXException;














import static com.at4wireless.alljoyn.testcases.conf.controlpanel.InterfacePathPattern.ControlPanel;
import static com.at4wireless.alljoyn.testcases.conf.controlpanel.InterfacePathPattern.HttpControl;













import com.at4wireless.alljoyn.core.about.AboutAnnouncementDetails;
import com.at4wireless.alljoyn.core.commons.AllJoynErrorReplyCodes;
import com.at4wireless.alljoyn.core.commons.ServiceHelper;
import com.at4wireless.alljoyn.core.commons.log.WindowsLoggerImpl;
import com.at4wireless.alljoyn.core.introspection.BusIntrospector;
import com.at4wireless.alljoyn.core.introspection.bean.InterfaceDetail;

// TODO: Auto-generated Javadoc
/**
 * The Class ControlPanelService.
 */
public class ControlPanelService {

	/** The pass. */
	boolean pass=true;
	
	/** The inconc. */
	boolean inconc=false;
	
	/** The port. */
	short port =91;
	
	/** The about client. */
	private  AboutClient aboutClient;
	
	/** The device about announcement. */
	private  AboutAnnouncementDetails deviceAboutAnnouncement;
	
	/** The service helper. */
	private  ServiceHelper serviceHelper; 
	
	/** The bus introspector. */
	private  BusIntrospector busIntrospector;

	/** The Key store path. */
	private  String  KeyStorePath="/KeyStore";
	
	/** The dut app id. */
	private  UUID dutAppId;
	
	/** The dut device id. */
	private  String dutDeviceId;
	
	/** The time out. */
	private  int  timeOut=30;


	/** The tag. */
	private  final String TAG = "ControlPanelTestSuite";
	
	/** The logger. */
	private  final WindowsLoggerImpl logger =  new WindowsLoggerImpl(TAG);

	/** The bus application name. */
	private  final String BUS_APPLICATION_NAME = "ControlPanel";
	
	/** The two states list. */
	private  final List<Integer> TWO_STATES_LIST = Arrays.asList(new Integer[]
			{ 0, 1 });
	
	/** The property states list. */
	private  final List<Integer> PROPERTY_STATES_LIST = Arrays.asList(new Integer[]
			{ 0, 1, 2, 3 });
	
	/** The valid property control layout hints values. */
	private  final List<Short> VALID_PROPERTY_CONTROL_LAYOUT_HINTS_VALUES = Arrays.asList(new Short[]
			{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13 });
	
	/** The numeric signatures. */
	private  final List<String> NUMERIC_SIGNATURES = Arrays.asList(new String[]
			{ "y", "n", "q", "i", "u", "x", "t", "d" });


	// short INTERFACE_VERSION;



	/** The ICSC p_ control panel service framework. */
	boolean ICSCP_ControlPanelServiceFramework=false;
	
	/** The ICSC p_ control panel interface. */
	boolean ICSCP_ControlPanelInterface=false;
	
	/** The ICSC p_ container interface. */
	boolean ICSCP_ContainerInterface=false;
	
	/** The ICSC p_ secured container interface. */
	boolean ICSCP_SecuredContainerInterface=false;
	
	/** The ICSC p_ property interface. */
	boolean ICSCP_PropertyInterface=false;
	
	/** The ICSC p_ secured property interface. */
	boolean ICSCP_SecuredPropertyInterface=false;
	
	/** The ICSC p_ label property interface. */
	boolean ICSCP_LabelPropertyInterface=false;
	
	/** The ICSC p_ action interface. */
	boolean ICSCP_ActionInterface=false;
	
	/** The ICSC p_ secured action interface. */
	boolean ICSCP_SecuredActionInterface=false;
	
	/** The ICSC p_ notification action interface. */
	boolean ICSCP_NotificationActionInterface=false;
	
	/** The ICSC p_ dialog interface. */
	boolean ICSCP_DialogInterface=false;
	
	/** The ICSC p_ d i_ action2. */
	boolean ICSCP_DI_Action2=false;
	
	/** The ICSC p_ d i_ action3. */
	boolean ICSCP_DI_Action3=false;
	
	/** The ICSC p_ secured dialog interface. */
	boolean ICSCP_SecuredDialogInterface=false;
	
	/** The ICSC p_ sd i_ action2. */
	boolean ICSCP_SDI_Action2=false;
	
	/** The ICSC p_ sd i_ action3. */
	boolean ICSCP_SDI_Action3=false;
	
	/** The ICSC p_ list property interface. */
	boolean ICSCP_ListPropertyInterface=false;
	
	/** The ICSC p_ secured list property interface. */
	boolean ICSCP_SecuredListPropertyInterface=false;
	
	/** The ICSC p_ http control interface. */
	boolean ICSCP_HTTPControlInterface=false;
	//
	/** The IXITC o_ app id. */
	String IXITCO_AppId=null;
	
	/** The IXITC o_ default language. */
	String IXITCO_DefaultLanguage=null;
	
	/** The IXITC o_ device id. */
	String IXITCO_DeviceId=null;
	//
	/** The IXITC p_ control panel version. */
	String IXITCP_ControlPanelVersion=null;
	
	/** The IXITC p_ container version. */
	String IXITCP_ContainerVersion=null;
	
	/** The IXITC p_ property version. */
	String IXITCP_PropertyVersion=null;
	
	/** The IXITC p_ label property version. */
	String IXITCP_LabelPropertyVersion=null;
	
	/** The IXITC p_ action version. */
	String IXITCP_ActionVersion=null;
	
	/** The IXITC p_ notification action version. */
	String IXITCP_NotificationActionVersion=null;
	
	/** The IXITC p_ dialog version. */
	String IXITCP_DialogVersion=null;
	
	/** The IXITC p_ list property version. */
	String IXITCP_ListPropertyVersion=null;
	
	/** The IXITC p_ http control version. */
	String IXITCP_HTTPControlVersion=null;
	
	/**
	 * Instantiates a new control panel service.
	 *
	 * @param testCase the test case
	 * @param iCSCP_ControlPanelServiceFramework the i csc p_ control panel service framework
	 * @param iCSCP_ControlPanelInterface the i csc p_ control panel interface
	 * @param iCSCP_ContainerInterface the i csc p_ container interface
	 * @param iCSCP_SecuredContainerInterface the i csc p_ secured container interface
	 * @param iCSCP_PropertyInterface the i csc p_ property interface
	 * @param iCSCP_SecuredPropertyInterface the i csc p_ secured property interface
	 * @param iCSCP_LabelPropertyInterface the i csc p_ label property interface
	 * @param iCSCP_ActionInterface the i csc p_ action interface
	 * @param iCSCP_SecuredActionInterface the i csc p_ secured action interface
	 * @param iCSCP_NotificationActionInterface the i csc p_ notification action interface
	 * @param iCSCP_DialogInterface the i csc p_ dialog interface
	 * @param iCSCP_DI_Action2 the i csc p_ d i_ action2
	 * @param iCSCP_DI_Action3 the i csc p_ d i_ action3
	 * @param iCSCP_SecuredDialogInterface the i csc p_ secured dialog interface
	 * @param iCSCP_SDI_Action2 the i csc p_ sd i_ action2
	 * @param iCSCP_SDI_Action3 the i csc p_ sd i_ action3
	 * @param iCSCP_ListPropertyInterface the i csc p_ list property interface
	 * @param iCSCP_SecuredListPropertyInterface the i csc p_ secured list property interface
	 * @param iCSCP_HTTPControlInterface the i csc p_ http control interface
	 * @param iXITCO_AppId the i xitc o_ app id
	 * @param iXITCO_DeviceId the i xitc o_ device id
	 * @param iXITCO_DefaultLanguage the i xitc o_ default language
	 * @param iXITCP_ControlPanelVersion the i xitc p_ control panel version
	 * @param iXITCP_ContainerVersion the i xitc p_ container version
	 * @param iXITCP_PropertyVersion the i xitc p_ property version
	 * @param iXITCP_LabelPropertyVersion the i xitc p_ label property version
	 * @param iXITCP_ActionVersion the i xitc p_ action version
	 * @param iXITCP_NotificationActionVersion the i xitc p_ notification action version
	 * @param iXITCP_DialogVersion the i xitc p_ dialog version
	 * @param iXITCP_ListPropertyVersion the i xitc p_ list property version
	 * @param iXITCP_HTTPControlVersion the i xitc p_ http control version
	 * @param gPCO_AnnouncementTimeout the g pc o_ announcement timeout
	 */
	public ControlPanelService(String testCase,
			boolean iCSCP_ControlPanelServiceFramework,
			boolean iCSCP_ControlPanelInterface,
			boolean iCSCP_ContainerInterface,
			boolean iCSCP_SecuredContainerInterface,
			boolean iCSCP_PropertyInterface,
			boolean iCSCP_SecuredPropertyInterface,
			boolean iCSCP_LabelPropertyInterface,
			boolean iCSCP_ActionInterface,
			boolean iCSCP_SecuredActionInterface,
			boolean iCSCP_NotificationActionInterface,
			boolean iCSCP_DialogInterface, boolean iCSCP_DI_Action2,
			boolean iCSCP_DI_Action3, boolean iCSCP_SecuredDialogInterface,
			boolean iCSCP_SDI_Action2, boolean iCSCP_SDI_Action3,
			boolean iCSCP_ListPropertyInterface,
			boolean iCSCP_SecuredListPropertyInterface,
			boolean iCSCP_HTTPControlInterface, String iXITCO_AppId,
			String iXITCO_DeviceId, String iXITCO_DefaultLanguage,
			String iXITCP_ControlPanelVersion, String iXITCP_ContainerVersion,
			String iXITCP_PropertyVersion, String iXITCP_LabelPropertyVersion,
			String iXITCP_ActionVersion,
			String iXITCP_NotificationActionVersion,
			String iXITCP_DialogVersion, String iXITCP_ListPropertyVersion,
			String iXITCP_HTTPControlVersion, String gPCO_AnnouncementTimeout) {

		ICSCP_ControlPanelServiceFramework=iCSCP_ControlPanelServiceFramework;
		ICSCP_ControlPanelInterface=iCSCP_ControlPanelInterface;
		ICSCP_ContainerInterface=iCSCP_ContainerInterface;
		ICSCP_SecuredContainerInterface=iCSCP_SecuredContainerInterface;
		ICSCP_PropertyInterface=iCSCP_PropertyInterface;
		ICSCP_SecuredPropertyInterface=iCSCP_SecuredPropertyInterface;
		ICSCP_LabelPropertyInterface=iCSCP_LabelPropertyInterface;
		ICSCP_ActionInterface=iCSCP_ActionInterface;
		ICSCP_SecuredActionInterface=iCSCP_SecuredActionInterface;
		ICSCP_NotificationActionInterface=iCSCP_NotificationActionInterface;
		ICSCP_DialogInterface=iCSCP_DialogInterface;
		ICSCP_DI_Action2=iCSCP_DI_Action2;
		ICSCP_DI_Action3=iCSCP_DI_Action3;
		ICSCP_SecuredDialogInterface=iCSCP_SecuredDialogInterface;
		ICSCP_SDI_Action2=iCSCP_SDI_Action2;
		ICSCP_SDI_Action3=iCSCP_SDI_Action3;
		ICSCP_ListPropertyInterface=iCSCP_ListPropertyInterface;
		ICSCP_SecuredListPropertyInterface=iCSCP_SecuredListPropertyInterface;
		ICSCP_HTTPControlInterface=iCSCP_HTTPControlInterface;
		//
		IXITCO_AppId=iXITCO_AppId;
		IXITCO_DefaultLanguage=iXITCO_DefaultLanguage;
		IXITCO_DeviceId=iXITCO_DeviceId;
		//
		IXITCP_ControlPanelVersion=iXITCP_ControlPanelVersion;
		IXITCP_ContainerVersion=iXITCP_ContainerVersion;
		IXITCP_PropertyVersion=iXITCP_PropertyVersion;
		IXITCP_LabelPropertyVersion=iXITCP_LabelPropertyVersion;
		IXITCP_ActionVersion=iXITCP_ActionVersion;
		IXITCP_NotificationActionVersion=iXITCP_NotificationActionVersion;
		IXITCP_DialogVersion=iXITCP_DialogVersion;
		IXITCP_ListPropertyVersion=iXITCP_ListPropertyVersion;
		IXITCP_HTTPControlVersion=iXITCP_HTTPControlVersion;
		
		timeOut = Integer.parseInt(gPCO_AnnouncementTimeout);
		
		try{
			runTestCase(testCase);
		}catch(Exception e){
			inconc=true;
			if(e!=null){
				if(e.getMessage().equals("Timed out waiting for About announcement")){
					logger.error("Timed out waiting for About announcement");
					pass=false;
				}else{
					logger.error("Exception: "+e.toString());
					pass=false;
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
		if(testCase.equals("ControlPanel-v1-01")){
			testControlPanel_v1_01_ValidateControlPanelBusObjects();
		}else if(testCase.equals("ControlPanel-v1-02")){
			testControlPanel_v1_02_ValidateContainerBusObjects();
		}else if(testCase.equals("ControlPanel-v1-03")){
			testControlPanel_v1_03_ValidatePropertyBusObjects();
		}else if(testCase.equals("ControlPanel-v1-04")){
			testControlPanel_v1_04_ValidateLabelPropertyBusObjects();
		}else if(testCase.equals("ControlPanel-v1-05")){
			testControlPanel_v1_05_ValidateActionBusObjects();
		}else if(testCase.equals("ControlPanel-v1-06")){
			testControlPanel_v1_06_ValidateDialogBusObjects();
		}else if(testCase.equals("ControlPanel-v1-07")){
			testControlPanel_v1_07_ValidateListPropertyBusObjects();
		}else if(testCase.equals("ControlPanel-v1-08")){
			testControlPanel_v1_08_ValidateNotificationActionBusObjects();
		}else if(testCase.equals("ControlPanel-v1-09")){
			testControlPanel_v1_09_ValidateHttpControlBusObjects();
		}else if(testCase.equals("ControlPanel-v1-10")){
			testControlPanel_v1_10_ValidateSecuredControlPanelBusObjects();
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
	private  void setUp() throws Exception {

		try
		{
			System.out.println("====================================================");
			logger.debug("test setUp started");
			dutDeviceId = IXITCO_DeviceId;
			logger.debug(String.format("Running ControlPanel test case against Device ID: %s", dutDeviceId));
			dutAppId = UUID.fromString(IXITCO_AppId);
			logger.debug(String.format("Running ControlPanel test case against App ID: %s", dutAppId));
			String keyStorePath =KeyStorePath;
			logger.debug(String.format("Running Config test case using KeyStorePath: %s", keyStorePath));
			serviceHelper = getServiceHelper();
			serviceHelper.initialize(BUS_APPLICATION_NAME, dutDeviceId, dutAppId);
			deviceAboutAnnouncement = serviceHelper.waitForNextDeviceAnnouncement(timeOut, TimeUnit.SECONDS);
			assertNotNull("Timed out waiting for About announcement", deviceAboutAnnouncement);
			aboutClient = serviceHelper.connectAboutClient(deviceAboutAnnouncement);
			serviceHelper.enableAuthentication(keyStorePath);

			busIntrospector = getIntrospector();
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
				logger.debug("Exception releasing resources", newException);
			}

			throw exception;
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
	 * Test control panel_v1_01_ validate control panel bus objects.
	 *
	 * @throws Exception the exception
	 */
	public  void testControlPanel_v1_01_ValidateControlPanelBusObjects() throws Exception
	{
		List<InterfaceDetail> controlPanelInterfaceDetailListExposedOnBus = busIntrospector
				.getInterfacesExposedOnBusBasedOnName(org.alljoyn.ioe.controlpanelservice.communication.interfaces.ControlPanel.IFNAME);

		if (controlPanelInterfaceDetailListExposedOnBus.isEmpty())
		{
			fail("No bus objects implement ControlPanel interface");

			return;
		}

		validateControlPanelInterfaceDetailList(controlPanelInterfaceDetailListExposedOnBus);
	}





	/**
	 * Test control panel_v1_02_ validate container bus objects.
	 *
	 * @throws Exception the exception
	 */
	public  void testControlPanel_v1_02_ValidateContainerBusObjects() throws Exception
	{
		List<InterfaceDetail> containerInterfaceDetailListExposedOnBus = busIntrospector.getInterfacesExposedOnBusBasedOnName(Container.IFNAME);
		List<InterfaceDetail> securedContainerInterfaceDetailListExposedOnBus = busIntrospector.getInterfacesExposedOnBusBasedOnName(ContainerSecured.IFNAME);

		if (containerInterfaceDetailListExposedOnBus.isEmpty() && securedContainerInterfaceDetailListExposedOnBus.isEmpty())
		{
			fail("No bus objects implement Container nor SecuredContainer interfaces");
			return;
		}

		validateContainerInterfaceDetailList(containerInterfaceDetailListExposedOnBus, false);
		validateContainerInterfaceDetailList(securedContainerInterfaceDetailListExposedOnBus, true);
	}






	/**
	 * Test control panel_v1_03_ validate property bus objects.
	 *
	 * @throws Exception the exception
	 */
	public  void testControlPanel_v1_03_ValidatePropertyBusObjects() throws Exception
	{
		List<InterfaceDetail> propertyInterfaceDetailListExposedOnBus = busIntrospector.getInterfacesExposedOnBusBasedOnName(PropertyControl.IFNAME);
		List<InterfaceDetail> securedPropertyInterfaceDetailListExposedOnBus = busIntrospector.getInterfacesExposedOnBusBasedOnName(PropertyControlSecured.IFNAME);

		if (propertyInterfaceDetailListExposedOnBus.isEmpty() && securedPropertyInterfaceDetailListExposedOnBus.isEmpty())
		{
			fail("No bus objects implement Property nor SecuredProperty interfaces");

			return;
		}

		validatePropertyInterfaceDetailList(propertyInterfaceDetailListExposedOnBus, false);
		validatePropertyInterfaceDetailList(securedPropertyInterfaceDetailListExposedOnBus, true);
	}






	/**
	 * Test control panel_v1_04_ validate label property bus objects.
	 *
	 * @throws Exception the exception
	 */
	public  void testControlPanel_v1_04_ValidateLabelPropertyBusObjects() throws Exception
	{
		List<InterfaceDetail> labelPropertyInterfaceDetailListExposedOnBus = busIntrospector.getInterfacesExposedOnBusBasedOnName(Label.IFNAME);

		if (labelPropertyInterfaceDetailListExposedOnBus.isEmpty())
		{
			fail("No bus objects implement LabelProperty interface");

			return;
		}

		validateLabelPropertyInterfaceDetailList(labelPropertyInterfaceDetailListExposedOnBus);
	}





	/**
	 * Test control panel_v1_05_ validate action bus objects.
	 *
	 * @throws Exception the exception
	 */
	public  void testControlPanel_v1_05_ValidateActionBusObjects() throws Exception
	{
		List<InterfaceDetail> actionInterfaceDetailListExposedOnBus = busIntrospector.getInterfacesExposedOnBusBasedOnName(ActionControl.IFNAME);
		List<InterfaceDetail> securedActionInterfaceDetailListExposedOnBus = busIntrospector.getInterfacesExposedOnBusBasedOnName(ActionControlSecured.IFNAME);

		if (actionInterfaceDetailListExposedOnBus.isEmpty() && securedActionInterfaceDetailListExposedOnBus.isEmpty())
		{
			fail("No bus objects implement Action nor SecuredAction interfaces");

			return;
		}

		validateActionInterfaceDetailList(actionInterfaceDetailListExposedOnBus, false);
		validateActionInterfaceDetailList(securedActionInterfaceDetailListExposedOnBus, true);
	}



	/**
	 * Test control panel_v1_06_ validate dialog bus objects.
	 *
	 * @throws Exception the exception
	 */
	public  void testControlPanel_v1_06_ValidateDialogBusObjects() throws Exception
	{
		List<InterfaceDetail> dialogInterfaceDetailListExposedOnBus = busIntrospector.getInterfacesExposedOnBusBasedOnName(AlertDialog.IFNAME);
		List<InterfaceDetail> securedDialogInterfaceDetailListExposedOnBus = busIntrospector.getInterfacesExposedOnBusBasedOnName(AlertDialogSecured.IFNAME);

		if (dialogInterfaceDetailListExposedOnBus.isEmpty() && securedDialogInterfaceDetailListExposedOnBus.isEmpty())
		{
			fail("No bus objects implement Dialog nor SecuredDialog interfaces");

			return;
		}

		validateDialogInterfaceDetailList(dialogInterfaceDetailListExposedOnBus, false);
		validateDialogInterfaceDetailList(securedDialogInterfaceDetailListExposedOnBus, true);
	}



	/**
	 * Test control panel_v1_07_ validate list property bus objects.
	 *
	 * @throws Exception the exception
	 */
	public  void testControlPanel_v1_07_ValidateListPropertyBusObjects() throws Exception
	{
		List<InterfaceDetail> listPropertyInterfaceDetailListExposedOnBus = busIntrospector.getInterfacesExposedOnBusBasedOnName(ListPropertyControl.IFNAME);
		List<InterfaceDetail> securedListPropertyInterfaceDetailListExposedOnBus = busIntrospector.getInterfacesExposedOnBusBasedOnName(ListPropertyControlSecured.IFNAME);

		if (listPropertyInterfaceDetailListExposedOnBus.isEmpty() && securedListPropertyInterfaceDetailListExposedOnBus.isEmpty())
		{
			fail("No bus objects implement ListProperty nor SecuredListProperty interfaces");

			return;
		}

		validateListPropertyInterfaceDetailList(listPropertyInterfaceDetailListExposedOnBus, false);
		validateListPropertyInterfaceDetailList(securedListPropertyInterfaceDetailListExposedOnBus, true);
	}



	/**
	 * Test control panel_v1_08_ validate notification action bus objects.
	 *
	 * @throws Exception the exception
	 */
	public  void testControlPanel_v1_08_ValidateNotificationActionBusObjects() throws Exception
	{
		List<InterfaceDetail> notificationActionInterfaceDetailListExposedOnBus = busIntrospector.getInterfacesExposedOnBusBasedOnName(NotificationAction.IFNAME);

		if (notificationActionInterfaceDetailListExposedOnBus.isEmpty())
		{
			fail("No bus objects implement NotificationAction interface");
			return;
		}

		validateNotificationActionInterfaceDetailList(notificationActionInterfaceDetailListExposedOnBus);
	}



	/**
	 * Test control panel_v1_09_ validate http control bus objects.
	 *
	 * @throws Exception the exception
	 */
	public  void testControlPanel_v1_09_ValidateHttpControlBusObjects() throws Exception
	{
		List<InterfaceDetail> httpControlInterfaceDetailListExposedOnBus = busIntrospector.getInterfacesExposedOnBusBasedOnName(HTTPControl.IFNAME);

		if (httpControlInterfaceDetailListExposedOnBus.isEmpty())
		{
			fail("No bus objects implement HTTPControl interface");
			return;
		}

		validateHttpControlInterfaceDetailList(httpControlInterfaceDetailListExposedOnBus);
	}





	/**
	 * Test control panel_v1_10_ validate secured control panel bus objects.
	 *
	 * @throws Exception the exception
	 */
	public  void testControlPanel_v1_10_ValidateSecuredControlPanelBusObjects() throws Exception
	{
		List<InterfaceDetail> securedContainerInterfaceDetailListExposedOnBus = busIntrospector.getInterfacesExposedOnBusBasedOnName(ContainerSecured.IFNAME);
		List<InterfaceDetail> securedPropertyInterfaceDetailListExposedOnBus = busIntrospector.getInterfacesExposedOnBusBasedOnName(PropertyControlSecured.IFNAME);
		List<InterfaceDetail> securedActionInterfaceDetailListExposedOnBus = busIntrospector.getInterfacesExposedOnBusBasedOnName(ActionControlSecured.IFNAME);
		List<InterfaceDetail> securedDialogInterfaceDetailListExposedOnBus = busIntrospector.getInterfacesExposedOnBusBasedOnName(AlertDialogSecured.IFNAME);
		List<InterfaceDetail> securedListPropertyInterfaceDetailListExposedOnBus = busIntrospector.getInterfacesExposedOnBusBasedOnName(ListPropertyControlSecured.IFNAME);

		if (isEmpty(securedContainerInterfaceDetailListExposedOnBus, securedPropertyInterfaceDetailListExposedOnBus, securedActionInterfaceDetailListExposedOnBus,
				securedDialogInterfaceDetailListExposedOnBus, securedListPropertyInterfaceDetailListExposedOnBus))
		{
			fail("No bus objects implement one of the secured ControlPanel interfaces");
			return;
		}

		validateSecuredContainerInvalidPasscodeInterfaceDetailList(securedContainerInterfaceDetailListExposedOnBus);
		validateSecuredPropertyInvalidPasscodeInterfaceDetailList(securedPropertyInterfaceDetailListExposedOnBus);
		validateSecuredActionInvalidPasscodeInterfaceDetailList(securedActionInterfaceDetailListExposedOnBus);
		validateSecuredDialogInvalidPasscodeInterfaceDetailList(securedDialogInterfaceDetailListExposedOnBus);
		validateSecuredListPropertyInvalidPasscodeInterfaceDetailList(securedListPropertyInterfaceDetailListExposedOnBus);
	}



	/**
	 * Validate secured container invalid passcode interface detail list.
	 *
	 * @param securedContainerInterfaceDetailListExposedOnBus the secured container interface detail list exposed on bus
	 * @throws BusException the bus exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private  void validateSecuredContainerInvalidPasscodeInterfaceDetailList(List<InterfaceDetail> securedContainerInterfaceDetailListExposedOnBus) throws BusException,
	IOException
	{
		for (InterfaceDetail interfaceDetail : securedContainerInterfaceDetailListExposedOnBus)
		{
			String path = interfaceDetail.getPath();
			validateSecuredContainerInvalidPasscodeBusObject(path);
		}
	}

	/**
	 * Validate secured property invalid passcode interface detail list.
	 *
	 * @param securedPropertyInterfaceDetailListExposedOnBus the secured property interface detail list exposed on bus
	 * @throws BusException the bus exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private  void validateSecuredPropertyInvalidPasscodeInterfaceDetailList(List<InterfaceDetail> securedPropertyInterfaceDetailListExposedOnBus) throws BusException, IOException

	{
		for (InterfaceDetail interfaceDetail : securedPropertyInterfaceDetailListExposedOnBus)
		{
			String path = interfaceDetail.getPath();
			validateSecuredPropertyControlInvalidPasscodeBusObject(path);
		}
	}

	/**
	 * Validate secured action invalid passcode interface detail list.
	 *
	 * @param actionInterfaceDetailListExposedOnBus the action interface detail list exposed on bus
	 * @throws BusException the bus exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private  void validateSecuredActionInvalidPasscodeInterfaceDetailList(List<InterfaceDetail> actionInterfaceDetailListExposedOnBus) throws BusException, IOException

	{
		for (InterfaceDetail interfaceDetail : actionInterfaceDetailListExposedOnBus)
		{
			String path = interfaceDetail.getPath();
			validateSecuredActionInvalidPasscodeBusObject(path);
		}
	}

	/**
	 * Validate secured dialog invalid passcode interface detail list.
	 *
	 * @param securedDialogInterfaceDetailListExposedOnBus the secured dialog interface detail list exposed on bus
	 * @throws BusException the bus exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private  void validateSecuredDialogInvalidPasscodeInterfaceDetailList(List<InterfaceDetail> securedDialogInterfaceDetailListExposedOnBus) throws BusException, IOException

	{
		for (InterfaceDetail interfaceDetail : securedDialogInterfaceDetailListExposedOnBus)
		{
			String path = interfaceDetail.getPath();
			validateSecuredDialogInvalidPasscodeBusObject(path);
		}
	}

	/**
	 * Validate secured list property invalid passcode interface detail list.
	 *
	 * @param listPropertyInterfaceDetailListExposedOnBus the list property interface detail list exposed on bus
	 * @throws BusException the bus exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private  void validateSecuredListPropertyInvalidPasscodeInterfaceDetailList(List<InterfaceDetail> listPropertyInterfaceDetailListExposedOnBus) throws BusException, IOException

	{
		for (InterfaceDetail interfaceDetail : listPropertyInterfaceDetailListExposedOnBus)
		{
			String path = interfaceDetail.getPath();
			validateSecuredListPropertyInvalidPasscodeBusObject(path);
		}
	}





	/**
	 * Validate secured container invalid passcode bus object.
	 *
	 * @param path the path
	 * @throws BusException the bus exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private  void validateSecuredContainerInvalidPasscodeBusObject(String path) throws BusException, IOException
	{
		ContainerSecured securedContainer = busIntrospector.getInterface(path, ContainerSecured.class);
		setInvalidPassword();

		try
		{
			char[] currentPasscode = serviceHelper.getAuthPassword(deviceAboutAnnouncement);
			logger.debug(String.format("Connecting with invalid password: %s", currentPasscode));
			securedContainer.getVersion();
			invalidPasswordAccessFailureToThrowException("container");
		}
		catch (BusException busException)
		{
			handleBusExceptionOnInvalidPasswordBusObjectAccess(busException);
		}
	}

	/**
	 * Validate secured property control invalid passcode bus object.
	 *
	 * @param path the path
	 * @throws BusException the bus exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private  void validateSecuredPropertyControlInvalidPasscodeBusObject(String path) throws BusException, IOException
	{
		PropertyControlSecured propertyControl = busIntrospector.getInterface(path, PropertyControlSecured.class);
		setInvalidPassword();

		try
		{
			char[] currentPasscode = serviceHelper.getAuthPassword(deviceAboutAnnouncement);
			logger.debug(String.format("Connecting with invalid password: %s", currentPasscode));
			propertyControl.getVersion();
			invalidPasswordAccessFailureToThrowException("property");
		}
		catch (BusException be)
		{
			handleBusExceptionOnInvalidPasswordBusObjectAccess(be);
		}
	}

	/**
	 * Validate secured action invalid passcode bus object.
	 *
	 * @param path the path
	 * @throws BusException the bus exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private  void validateSecuredActionInvalidPasscodeBusObject(String path) throws BusException, IOException
	{
		ActionControlSecured action = busIntrospector.getInterface(path, ActionControlSecured.class);
		setInvalidPassword();

		try
		{
			char[] currentPasscode = serviceHelper.getAuthPassword(deviceAboutAnnouncement);
			logger.debug(String.format("Connecting with invalid password: %s", currentPasscode));
			action.getVersion();
			invalidPasswordAccessFailureToThrowException("action");
		}
		catch (BusException be)
		{
			handleBusExceptionOnInvalidPasswordBusObjectAccess(be);
		}
	}

	/**
	 * Validate secured dialog invalid passcode bus object.
	 *
	 * @param path the path
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws BusException the bus exception
	 */
	private  void validateSecuredDialogInvalidPasscodeBusObject(String path) throws IOException, BusException
	{
		AlertDialogSecured securedDialog = busIntrospector.getInterface(path, AlertDialogSecured.class);
		setInvalidPassword();

		try
		{
			char[] currentPasscode = serviceHelper.getAuthPassword(deviceAboutAnnouncement);
			logger.debug(String.format("Connecting with invalid password: %s", currentPasscode));
			securedDialog.getVersion();
			invalidPasswordAccessFailureToThrowException("dialog");
		}
		catch (BusException be)
		{
			handleBusExceptionOnInvalidPasswordBusObjectAccess(be);
		}
	}

	/**
	 * Validate secured list property invalid passcode bus object.
	 *
	 * @param path the path
	 * @throws BusException the bus exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private  void validateSecuredListPropertyInvalidPasscodeBusObject(String path) throws BusException, IOException
	{
		ListPropertyControlSecured listPropertyControlSecured = busIntrospector.getInterface(path, ListPropertyControlSecured.class);
		setInvalidPassword();

		try
		{
			char[] currentPasscode = serviceHelper.getAuthPassword(deviceAboutAnnouncement);
			logger.debug(String.format("Connecting with invalid password: %s", currentPasscode));
			listPropertyControlSecured.getVersion();
			invalidPasswordAccessFailureToThrowException("list property");
		}
		catch (BusException be)
		{
			handleBusExceptionOnInvalidPasswordBusObjectAccess(be);
		}
	}


	/**
	 * Handle bus exception on invalid password bus object access.
	 *
	 * @param busException the bus exception
	 */
	private  void handleBusExceptionOnInvalidPasswordBusObjectAccess(BusException busException)
	{
		logger.debug("Exception thrown on setting WrongPasscode", busException);
		boolean isPeerAuthenticatedAttempted = serviceHelper.isPeerAuthenticationAttempted(deviceAboutAnnouncement);
		boolean isPeerAuthenticationSuccessful = serviceHelper.isPeerAuthenticationSuccessful(deviceAboutAnnouncement);
		assertTrue("Authentication should have failed", isPeerAuthenticatedAttempted && !isPeerAuthenticationSuccessful);
	}


	/**
	 * Sets the invalid password.
	 */
	private  void setInvalidPassword()
	{
		serviceHelper.clearPeerAuthenticationFlags(deviceAboutAnnouncement);
		serviceHelper.setAuthPassword(deviceAboutAnnouncement, "123456".toCharArray());
	}


	/**
	 * Invalid password access failure to throw exception.
	 *
	 * @param interfaceName the interface name
	 */
	private  void invalidPasswordAccessFailureToThrowException(String interfaceName)
	{
		fail("Exception should be thrown on connecting with the wrong password to retrieve " + interfaceName + " version!");
	}



	/**
	 * Checks if is empty.
	 *
	 * @param securedContainerInterfaceDetailListExposedOnBus the secured container interface detail list exposed on bus
	 * @param securedPropertyInterfaceDetailListExposedOnBus the secured property interface detail list exposed on bus
	 * @param securedActionInterfaceDetailListExposedOnBus the secured action interface detail list exposed on bus
	 * @param securedDialogInterfaceDetailListExposedOnBus the secured dialog interface detail list exposed on bus
	 * @param securedListPropertyInterfaceDetailListExposedOnBus the secured list property interface detail list exposed on bus
	 * @return true, if is empty
	 */
	private  boolean isEmpty(List<InterfaceDetail> securedContainerInterfaceDetailListExposedOnBus, List<InterfaceDetail> securedPropertyInterfaceDetailListExposedOnBus,
			List<InterfaceDetail> securedActionInterfaceDetailListExposedOnBus, List<InterfaceDetail> securedDialogInterfaceDetailListExposedOnBus,
			List<InterfaceDetail> securedListPropertyInterfaceDetailListExposedOnBus)
	{
		return securedContainerInterfaceDetailListExposedOnBus.isEmpty() && securedPropertyInterfaceDetailListExposedOnBus.isEmpty()
				&& securedActionInterfaceDetailListExposedOnBus.isEmpty() && securedDialogInterfaceDetailListExposedOnBus.isEmpty()
				&& securedListPropertyInterfaceDetailListExposedOnBus.isEmpty();
	}



	/**
	 * Validate http control interface detail list.
	 *
	 * @param httpControlInterfaceDetailListExposedOnBus the http control interface detail list exposed on bus
	 * @throws BusException the bus exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private  void validateHttpControlInterfaceDetailList(List<InterfaceDetail> httpControlInterfaceDetailListExposedOnBus) throws BusException, IOException
	{
		for (InterfaceDetail interfaceDetail : httpControlInterfaceDetailListExposedOnBus)
		{
			String path = interfaceDetail.getPath();
			logger.debug(String.format("Validating HTTPControl object at: %s", path));
			assertTrue(String.format("%s does not match the expected pattern /ControlPanel/{unit}/HTTPControl", path), isValidPath(HttpControl.getValue(), path));
			validateHttpControlBusObject(path);
			// getValidationTestContext().addInterfaceDetails(HTTPControl.IFNAME, INTERFACE_VERSION, path, null, null);
		}
	}

	/**
	 * Validate http control bus object.
	 *
	 * @param path the path
	 * @throws BusException the bus exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private  void validateHttpControlBusObject(String path) throws BusException, IOException
	{
		HTTPControl httpControl = busIntrospector.getInterface(path, HTTPControl.class);
		assertEquals("Interface version does not match", Short.parseShort(IXITCP_HTTPControlVersion), httpControl.getVersion());
		validateRootUrl(httpControl.GetRootURL());
	}


	/**
	 * Validate root url.
	 *
	 * @param rootUrl the root url
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private  void validateRootUrl(String rootUrl) throws IOException
	{
		assertNotNull("Root URL returned is null", rootUrl);
		assertTrue(String.format("%s is not a valid URL", rootUrl), isValidUrl(rootUrl));
		HttpGet httpGet = new HttpGet(rootUrl);
		HttpResponse httpResponse = getHttpClient().execute(httpGet);
		assertNotNull("Root URL is not responding",httpResponse.getStatusLine());
	}


	 /**
 	 * Gets the http client.
 	 *
 	 * @return the http client
 	 */
 	HttpClient getHttpClient()
	{
		HttpClient httpClient = HttpClientBuilder.create().build();
		return httpClient;
	}

	/**
	 * Checks if is valid url.
	 *
	 * @param supportUrl the support url
	 * @return true, if is valid url
	 */
	boolean isValidUrl(String supportUrl)
	{
		try
		{
			new URL(supportUrl).toURI();
		}
		catch (MalformedURLException malformedURLException)
		{
			logger.debug("Invalid URL", malformedURLException);
			return false;
		}
		catch (URISyntaxException e)
		{
			logger.debug("Invalid URL", e);
			return false;
		}

		return true;
	}


	/**
	 * Validate notification action interface detail list.
	 *
	 * @param notificationActionInterfaceDetailListExposedOnBus the notification action interface detail list exposed on bus
	 * @throws Exception the exception
	 */
	private  void validateNotificationActionInterfaceDetailList(List<InterfaceDetail> notificationActionInterfaceDetailListExposedOnBus) throws Exception
	{
		for (InterfaceDetail interfaceDetail : notificationActionInterfaceDetailListExposedOnBus)
		{
			String path = interfaceDetail.getPath();
			logger.debug(String.format("Validating NotificationAction object at: %s", path));
			assertTrue(String.format("%s does not match the expected pattern /ControlPanel/{unit}/{actionPanelName}", path), isValidPath(ControlPanel.getValue(), path));
			validateNotificationActionBusObject(path);
			//   getValidationTestContext().addInterfaceDetails(NotificationAction.IFNAME, INTERFACE_VERSION, path, null, null);
		}
	}


	/**
	 * Validate notification action bus object.
	 *
	 * @param path the path
	 * @throws Exception the exception
	 */
	private  void validateNotificationActionBusObject(String path) throws Exception
	{
		NotificationAction notificationAction = busIntrospector.getInterface(path, NotificationAction.class);
		assertEquals("Interface version does not match", Short.parseShort(IXITCP_NotificationActionVersion), notificationAction.getVersion());
		assertContainerOrDialogObjectExists(path);
	}


	/**
	 * Assert container or dialog object exists.
	 *
	 * @param path the path
	 * @throws BusException the bus exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ParserConfigurationException the parser configuration exception
	 * @throws SAXException the SAX exception
	 */
	private  void assertContainerOrDialogObjectExists(String path) throws BusException, IOException, ParserConfigurationException, SAXException
	{
		List<InterfaceDetail> containerInterfaceDetailList = busIntrospector.getInterfacesExposedOnBusUnderSpecifiedPathBasedOnName(path, Container.IFNAME);
		List<InterfaceDetail> securedContainerInterfaceDetailList = busIntrospector.getInterfacesExposedOnBusUnderSpecifiedPathBasedOnName(path, ContainerSecured.IFNAME);
		List<InterfaceDetail> dialogInterfaceDetailList = busIntrospector.getInterfacesExposedOnBusUnderSpecifiedPathBasedOnName(path, AlertDialog.IFNAME);
		List<InterfaceDetail> securedDialogInterfaceDetailList = busIntrospector.getInterfacesExposedOnBusUnderSpecifiedPathBasedOnName(path, AlertDialogSecured.IFNAME);

		assertTrue(String.format("No object implementing org.alljoyn.ControlPanel.Container nor org.alljoyn.ControlPanel.SecuredContainer nor "
				+ "org.alljoyn.ControlPanel.Dialog nor org.alljoyn.ControlPanel.SecuredDialog is found under path %s", path),
				isRootContainerPresent(containerInterfaceDetailList, securedContainerInterfaceDetailList, dialogInterfaceDetailList, securedDialogInterfaceDetailList));
	}



	/**
	 * Checks if is root container present.
	 *
	 * @param containerInterfaceDetailList the container interface detail list
	 * @param securedContainerInterfaceDetailList the secured container interface detail list
	 * @param dialogInterfaceDetailList the dialog interface detail list
	 * @param securedDialogInterfaceDetailList the secured dialog interface detail list
	 * @return true, if is root container present
	 */
	private  boolean isRootContainerPresent(List<InterfaceDetail> containerInterfaceDetailList, List<InterfaceDetail> securedContainerInterfaceDetailList,
			List<InterfaceDetail> dialogInterfaceDetailList, List<InterfaceDetail> securedDialogInterfaceDetailList)
	{
		return !(containerInterfaceDetailList.isEmpty() && securedContainerInterfaceDetailList.isEmpty() && dialogInterfaceDetailList.isEmpty() && securedDialogInterfaceDetailList
				.isEmpty());
	}







	/**
	 * Validate list property interface detail list.
	 *
	 * @param listPropertyInterfaceDetailListExposedOnBus the list property interface detail list exposed on bus
	 * @param isSecured the is secured
	 * @throws BusException the bus exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ParserConfigurationException the parser configuration exception
	 * @throws SAXException the SAX exception
	 */
	private  void validateListPropertyInterfaceDetailList(List<InterfaceDetail> listPropertyInterfaceDetailListExposedOnBus, boolean isSecured) throws BusException, IOException,
	ParserConfigurationException, SAXException
	{
		for (InterfaceDetail interfaceDetail : listPropertyInterfaceDetailListExposedOnBus)
		{
			String path = interfaceDetail.getPath();
			logger.debug(String.format("Validating ControlPanel.ListProperty object at: %s", path));
			assertAncestorContainerIsPresent(path);
			validateListPropertyBusObject(path, isSecured);
			// addListPropertyInterfaceDetailsToTestContext(isSecured, path);
		}
	}


	/**
	 * Validate list property bus object.
	 *
	 * @param path the path
	 * @param isSecured the is secured
	 * @throws BusException the bus exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private  void validateListPropertyBusObject(String path, boolean isSecured) throws BusException, IOException
	{
		ListPropertyControlSuper listPropertyControl = null;

		if (isSecured)
		{
			listPropertyControl = busIntrospector.getInterface(path, ListPropertyControlSecured.class);
		}
		else
		{
			listPropertyControl = busIntrospector.getInterface(path, ListPropertyControl.class);
		}

		assertEquals("Interface version does not match", Short.parseShort(IXITCP_ListPropertyVersion), listPropertyControl.getVersion());
		assertTrue(String.format("Interface state %d does not equals expected value of 0 or 1", listPropertyControl.getStates()),
				isValidState(listPropertyControl.getStates(), TWO_STATES_LIST));
		validateListPropertyParameters(listPropertyControl.getOptParams());
		validateListPropertyValue(listPropertyControl);
	}


	/**
	 * Validate list property value.
	 *
	 * @param listPropertyControl the list property control
	 * @throws BusException the bus exception
	 */
	private  void validateListPropertyValue(ListPropertyControlSuper listPropertyControl) throws BusException
	{
		ListPropertyWidgetRecordAJ[] listPropertyWidgetRecordAJs = listPropertyControl.getValue();
		assertNotNull("Interface value cannot be null", listPropertyWidgetRecordAJs);

		Set<Short> recordIds = new HashSet<Short>();

		for (ListPropertyWidgetRecordAJ listPropertyWidgetRecordAJ : listPropertyWidgetRecordAJs)
		{
			assertNotNull("Label cannot be null", listPropertyWidgetRecordAJ.label);
			assertFalse("Label cannot be empty", listPropertyWidgetRecordAJ.label.isEmpty());
			recordIds.add(listPropertyWidgetRecordAJ.recordId);
		}

		assertTrue("Record IDs need to be unique", listPropertyWidgetRecordAJs.length == recordIds.size());
	}


	/**
	 * Validate list property parameters.
	 *
	 * @param parameters the parameters
	 * @throws BusException the bus exception
	 */
	private  void validateListPropertyParameters(Map<Short, Variant> parameters) throws BusException
	{
		validateOptionalParameter0(parameters);
		validateOptionalParameter1(parameters);
		validateParameterLayoutHints(parameters);
	}


	/**
	 * Validate dialog interface detail list.
	 *
	 * @param dialogInterfaceDetailListExposedOnBus the dialog interface detail list exposed on bus
	 * @param isSecured the is secured
	 * @throws BusException the bus exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ParserConfigurationException the parser configuration exception
	 * @throws SAXException the SAX exception
	 */
	private  void validateDialogInterfaceDetailList(List<InterfaceDetail> dialogInterfaceDetailListExposedOnBus, boolean isSecured) throws BusException, IOException,
	ParserConfigurationException, SAXException
	{
		boolean action3=false;
		boolean action2=false;
		for (InterfaceDetail interfaceDetail : dialogInterfaceDetailListExposedOnBus)
		{
			String path = interfaceDetail.getPath();
			logger.debug(String.format("Validating ControlPanel.Dialog object at: %s", path));
			assertValidAncestorIsPresentForDialog(path);
			validateDialogBusObject(path, isSecured);


			if(action2IsPresent(path,isSecured)){
				action2=true;
			}

			if(action3IsPresent(path,isSecured)){
				action3=true;
			}



			// addDialogInterfaceDetailsToTestContext(isSecured, path);
		}

		if((ICSCP_DI_Action2||ICSCP_SDI_Action2) && !action2){
			fail("Method Action2 is not present"); 

		}

		if((ICSCP_DI_Action3||ICSCP_SDI_Action3) && !action3){
			fail("Method Action3 is not present");

		}


	}


	/**
	 * Action2 is present.
	 *
	 * @param path the path
	 * @param isSecured the is secured
	 * @return true, if successful
	 * @throws BusException the bus exception
	 */
	private  boolean action2IsPresent(String path,Boolean isSecured) throws BusException {
		AlertDialogSuper dialog = null;

		boolean ispresent=false;

		if (isSecured)
		{
			dialog = busIntrospector.getInterface(path, AlertDialogSecured.class);
		}
		else
		{
			dialog = busIntrospector.getInterface(path, AlertDialog.class);
		}

		short numberOfActions = dialog.getNumActions();
		if (numberOfActions == 2){
			ispresent=true;
		}

		return ispresent;
	}



	/**
	 * Action3 is present.
	 *
	 * @param path the path
	 * @param isSecured the is secured
	 * @return true, if successful
	 * @throws BusException the bus exception
	 */
	private  boolean action3IsPresent(String path,Boolean isSecured) throws BusException {
		AlertDialogSuper dialog = null;

		boolean ispresent=false;

		if (isSecured)
		{
			dialog = busIntrospector.getInterface(path, AlertDialogSecured.class);
		}
		else
		{
			dialog = busIntrospector.getInterface(path, AlertDialog.class);
		}

		short numberOfActions = dialog.getNumActions();

		if (numberOfActions == 3){
			ispresent=true;
		}

		return ispresent;
	}



	/**
	 * Assert valid ancestor is present for dialog.
	 *
	 * @param path the path
	 * @throws BusException the bus exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ParserConfigurationException the parser configuration exception
	 * @throws SAXException the SAX exception
	 */
	void assertValidAncestorIsPresentForDialog(String path) throws BusException, IOException, ParserConfigurationException, SAXException
	{
		assertTrue("No parent bus object that implements Action nor SecuredAction nor NotificationAction interface found",
				busIntrospector.isAncestorInterfacePresent(path, ActionControl.IFNAME) || busIntrospector.isAncestorInterfacePresent(path, ActionControlSecured.IFNAME)
				|| busIntrospector.isAncestorInterfacePresent(path, NotificationAction.IFNAME));
	}







	/**
	 * Validate dialog bus object.
	 *
	 * @param path the path
	 * @param isSecured the is secured
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws BusException the bus exception
	 */
	private  void validateDialogBusObject(String path, boolean isSecured) throws IOException, BusException
	{
		AlertDialogSuper dialog = null;

		if (isSecured)
		{
			dialog = busIntrospector.getInterface(path, AlertDialogSecured.class);
		}
		else
		{
			dialog = busIntrospector.getInterface(path, AlertDialog.class);
		}

		assertEquals("Interface version does not match", Short.parseShort(IXITCP_DialogVersion), dialog.getVersion());
		assertTrue(String.format("Interface state %d does not equals expected value of 0 or 1", dialog.getStates()), isValidState(dialog.getStates(), TWO_STATES_LIST));
		short numberOfActions = dialog.getNumActions();
		validateDialogParameters(dialog.getOptParams(), numberOfActions);
		logger.debug(String.format("Dialog message: %s", dialog.getMessage()));
		validateDialogActions(dialog, numberOfActions);
	}


	/**
	 * Validate dialog actions.
	 *
	 * @param dialog the dialog
	 * @param numberOfActions the number of actions
	 * @throws BusException the bus exception
	 */
	private  void validateDialogActions(AlertDialogSuper dialog, short numberOfActions) throws BusException
	{
		if (numberOfActions == 1)
		{
			validateInvokingDialogAction2ThrowsException(dialog);
			validateInvokingDialogAction3ThrowsException(dialog);
		}

		if (numberOfActions == 2)
		{
			validateInvokingDialogAction3ThrowsException(dialog);
		}
	}

	/**
	 * Validate invoking dialog action2 throws exception.
	 *
	 * @param dialog the dialog
	 * @throws BusException the bus exception
	 */
	private  void validateInvokingDialogAction2ThrowsException(AlertDialogSuper dialog) throws BusException
	{
		try
		{
			dialog.Action2();
			fail("Invoking Action2() must throw exception");
		}
		catch (ErrorReplyBusException errorReplyBusException)
		{
			assertErrorIsMethodNotAllowed(errorReplyBusException);
		}
	}

	/**
	 * Validate invoking dialog action3 throws exception.
	 *
	 * @param dialog the dialog
	 * @throws BusException the bus exception
	 */
	private  void validateInvokingDialogAction3ThrowsException(AlertDialogSuper dialog) throws BusException
	{
		try
		{
			dialog.Action3();
			fail("Invoking Action3() must throw exception");
		}
		catch (ErrorReplyBusException errorReplyBusException)
		{
			assertErrorIsMethodNotAllowed(errorReplyBusException);
		}
	}


	/**
	 * Assert error is method not allowed.
	 *
	 * @param errorReplyBusException the error reply bus exception
	 */
	private  void assertErrorIsMethodNotAllowed(ErrorReplyBusException errorReplyBusException)
	{
		logger.debug("Expected exception caught while involing Action()", errorReplyBusException);
		assertEquals("Error name does not match", AllJoynErrorReplyCodes.METHOD_NOT_ALLOWED, errorReplyBusException.getErrorName());
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
	 * Validate dialog parameters.
	 *
	 * @param parameters the parameters
	 * @param numberOfActions the number of actions
	 * @throws BusException the bus exception
	 */
	private  void validateDialogParameters(Map<Short, Variant> parameters, short numberOfActions) throws BusException
	{
		validateOptionalParameter0(parameters);
		validateOptionalParameter1(parameters);
		validateParameterLayoutHints(parameters);
		validateDialogParameterActionLabelIds(parameters, numberOfActions);
	}


	/**
	 * Validate dialog parameter action label ids.
	 *
	 * @param parameters the parameters
	 * @param numberOfActions the number of actions
	 * @throws AnnotationBusException the annotation bus exception
	 */
	private  void validateDialogParameterActionLabelIds(Map<Short, Variant> parameters, short numberOfActions) throws AnnotationBusException
	{
		if (parameters.containsKey((short) 6))
		{
			validateDialogParameterActionLabelId(parameters.get((short) 6), "6");
		}

		if (numberOfActions >= 2)
		{
			validateDialogParameterActionLabelId(parameters.get((short) 7), "7");
		}

		if (numberOfActions == 3)
		{
			validateDialogParameterActionLabelId(parameters.get((short) 8), "8");
		}
	}

	/**
	 * Validate dialog parameter action label id.
	 *
	 * @param actionLabelIdVariant the action label id variant
	 * @param key the key
	 * @throws AnnotationBusException the annotation bus exception
	 */
	private  void validateDialogParameterActionLabelId(Variant actionLabelIdVariant, String key) throws AnnotationBusException
	{
		assertNotNull(String.format("Key %s is missing", key), actionLabelIdVariant);
		assertEquals(String.format("Signature does not match for key %s", key), "s", actionLabelIdVariant.getSignature());
	}


	/**
	 * Validate action interface detail list.
	 *
	 * @param actionInterfaceDetailListExposedOnBus the action interface detail list exposed on bus
	 * @param isSecured the is secured
	 * @throws BusException the bus exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ParserConfigurationException the parser configuration exception
	 * @throws SAXException the SAX exception
	 */
	private  void validateActionInterfaceDetailList(List<InterfaceDetail> actionInterfaceDetailListExposedOnBus, boolean isSecured) throws BusException, IOException,
	ParserConfigurationException, SAXException
	{
		for (InterfaceDetail interfaceDetail : actionInterfaceDetailListExposedOnBus)
		{
			String path = interfaceDetail.getPath();
			logger.debug(String.format("Validating ControlPanel.Action object at: %s", path));
			assertAncestorContainerIsPresent(path);
			validateActionBusObject(path, isSecured);
			// addActionInterfaceDetailsToTestContext(isSecured, path);
		}
	}


	/**
	 * Validate action bus object.
	 *
	 * @param path the path
	 * @param isSecured the is secured
	 * @throws BusException the bus exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private  void validateActionBusObject(String path, boolean isSecured) throws BusException, IOException
	{
		ActionControlSuper action = null;

		if (isSecured)
		{
			action = busIntrospector.getInterface(path, ActionControlSecured.class);
		}
		else
		{
			action = busIntrospector.getInterface(path, ActionControl.class);
		}

		assertEquals("Interface version does not match", Short.parseShort(IXITCP_ActionVersion), action.getVersion());
		assertTrue(String.format("Interface state %d does not equals expected value of 0 or 1", action.getStates()), isValidState(action.getStates(), TWO_STATES_LIST));
		validateActionParameters(action.getOptParams());
	}




	/**
	 * Validate action parameters.
	 *
	 * @param parameters the parameters
	 * @throws BusException the bus exception
	 */
	private  void validateActionParameters(Map<Short, Variant> parameters) throws BusException
	{
		validateOptionalParameter0(parameters);
		validateOptionalParameter1(parameters);
		validateParameterLayoutHints(parameters);
	}




	/**
	 * Validate label property interface detail list.
	 *
	 * @param labelPropertyInterfaceDetailListExposedOnBus the label property interface detail list exposed on bus
	 * @throws BusException the bus exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ParserConfigurationException the parser configuration exception
	 * @throws SAXException the SAX exception
	 */
	private  void validateLabelPropertyInterfaceDetailList(List<InterfaceDetail> labelPropertyInterfaceDetailListExposedOnBus) throws BusException, IOException,
	ParserConfigurationException, SAXException
	{
		for (InterfaceDetail interfaceDetail : labelPropertyInterfaceDetailListExposedOnBus)
		{
			String path = interfaceDetail.getPath();
			logger.debug(String.format("Validating ControlPanel.LabelProperty object at: %s", path));
			assertAncestorContainerIsPresent(path);
			validateLabelPropertyBusObject(path);
			//getValidationTestContext().addInterfaceDetails(Label.IFNAME, INTERFACE_VERSION, path, null, null);
		}
	}



	/**
	 * Validate label property bus object.
	 *
	 * @param path the path
	 * @throws BusException the bus exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private  void validateLabelPropertyBusObject(String path) throws BusException, IOException
	{
		Label label = busIntrospector.getInterface(path, Label.class);
		assertEquals("Interface version does not match", Short.parseShort(IXITCP_LabelPropertyVersion), label.getVersion());
		assertTrue(String.format("Interface state %d does not equals expected value of 0 or 1", label.getStates()), isValidState(label.getStates(), TWO_STATES_LIST));
		assertTrue("Label property must be a String", label.getLabel() instanceof String);
		validateLabelPropertyParameters(label.getOptParams());
	}


	/**
	 * Validate label property parameters.
	 *
	 * @param parameters the parameters
	 * @throws BusException the bus exception
	 */
	private  void validateLabelPropertyParameters(Map<Short, Variant> parameters) throws BusException
	{
		validateOptionalParameter1(parameters);
		validateParameterLayoutHints(parameters);
	}



	/**
	 * Validate parameter layout hints.
	 *
	 * @param parameters the parameters
	 * @throws BusException the bus exception
	 */
	private  void validateParameterLayoutHints(Map<Short, Variant> parameters) throws BusException
	{
		if (parameters.containsKey((short) 2))
		{
			Variant layoutHintsVariant = parameters.get((short) 2);
			validateLayoutHintsSignature(layoutHintsVariant);
			short[] layoutHints = layoutHintsVariant.getObject(short[].class);
			assertEquals("Key 2 contains more than one value", 1, layoutHints.length);
			assertEquals("Value does not match for key 2", 1, layoutHints[0]);
		}
	}






	/**
	 * Validate property interface detail list.
	 *
	 * @param propertyInterfaceDetailListExposedOnBus the property interface detail list exposed on bus
	 * @param isSecured the is secured
	 * @throws BusException the bus exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ParserConfigurationException the parser configuration exception
	 * @throws SAXException the SAX exception
	 */
	private  void validatePropertyInterfaceDetailList(List<InterfaceDetail> propertyInterfaceDetailListExposedOnBus, boolean isSecured) throws BusException, IOException,
	ParserConfigurationException, SAXException
	{
		for (InterfaceDetail interfaceDetail : propertyInterfaceDetailListExposedOnBus)
		{
			String path = interfaceDetail.getPath();
			logger.debug(String.format("Validating ControlPanel.Property object at: %s", path));
			assertAncestorContainerIsPresent(path);
			validatePropertyBusObject(path, isSecured);
			//addPropertyInterfaceDetailsToTestContext(isSecured, path);
		}
	}


	/**
	 * Assert ancestor container is present.
	 *
	 * @param path the path
	 * @throws BusException the bus exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ParserConfigurationException the parser configuration exception
	 * @throws SAXException the SAX exception
	 */
	void assertAncestorContainerIsPresent(String path) throws BusException, IOException, ParserConfigurationException, SAXException
	{
		assertTrue("No parent bus object that implements Container nor SecuredContainer interface found", busIntrospector.isAncestorInterfacePresent(path, Container.IFNAME)
				|| busIntrospector.isAncestorInterfacePresent(path, ContainerSecured.IFNAME));
	}


	/**
	 * Validate property bus object.
	 *
	 * @param path the path
	 * @param isSecured the is secured
	 * @throws BusException the bus exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private  void validatePropertyBusObject(String path, boolean isSecured) throws BusException, IOException
	{
		PropertyControlSuper propertyControl = null;

		if (isSecured)
		{
			propertyControl = busIntrospector.getInterface(path, PropertyControlSecured.class);
		}
		else
		{
			propertyControl = busIntrospector.getInterface(path, PropertyControl.class);
		}

		assertEquals("Interface version does not match", Short.parseShort(IXITCP_PropertyVersion), propertyControl.getVersion());
		assertTrue(String.format("Interface state %d does not equals expected value of 0, 1, 2 or 3", propertyControl.getStates()),
				isValidState(propertyControl.getStates(), PROPERTY_STATES_LIST));
		validatePropertyControlParameters(propertyControl.getOptParams(), propertyControl.getValue());
	}



	/**
	 * Validate property control parameters.
	 *
	 * @param parameters the parameters
	 * @param propertyValue the property value
	 * @throws BusException the bus exception
	 */
	private  void validatePropertyControlParameters(Map<Short, Variant> parameters, Variant propertyValue) throws BusException
	{
		validateOptionalParameter0(parameters);
		validateOptionalParameter1(parameters);
		validatePropertyControlParameterLayoutHints(parameters, propertyValue);
		validatePropertyControlOptionalParameter3(parameters);
		validatePropertyControlOptionalParameter4(parameters, propertyValue);
		validatePropertyControlOptionalParameter5(parameters, propertyValue);
	}




	/**
	 * Validate property control optional parameter3.
	 *
	 * @param parameters the parameters
	 * @throws AnnotationBusException the annotation bus exception
	 */
	private  void validatePropertyControlOptionalParameter3(Map<Short, Variant> parameters) throws AnnotationBusException
	{
		if (parameters.containsKey((short) 3))
		{
			assertEquals("Signature does not match for key 3", "s", parameters.get((short) 3).getSignature());
		}
	}

	/**
	 * Validate property control optional parameter4.
	 *
	 * @param parameters the parameters
	 * @param propertyValue the property value
	 * @throws AnnotationBusException the annotation bus exception
	 * @throws BusException the bus exception
	 */
	private  void validatePropertyControlOptionalParameter4(Map<Short, Variant> parameters, Variant propertyValue) throws AnnotationBusException, BusException
	{
		if (parameters.containsKey((short) 4))
		{
			assertEquals("Signature does not match for key 4", "a(vs)", parameters.get((short) 4).getSignature());
			PropertyWidgetConstrainToValuesAJ[] propertyWidgetConstrainToValuesAJArray = parameters.get((short) 4).getObject(PropertyWidgetConstrainToValuesAJ[].class);

			for (PropertyWidgetConstrainToValuesAJ propertyWidgetConstrainToValuesAJ : propertyWidgetConstrainToValuesAJArray)
			{
				assertEquals("Signature does not match for value Variant instance in value for key 4", propertyValue.getSignature(),
						propertyWidgetConstrainToValuesAJ.value.getSignature());
			}
		}
	}

	/**
	 * Validate property control optional parameter5.
	 *
	 * @param parameters the parameters
	 * @param propertyValue the property value
	 * @throws AnnotationBusException the annotation bus exception
	 * @throws BusException the bus exception
	 */
	private  void validatePropertyControlOptionalParameter5(Map<Short, Variant> parameters, Variant propertyValue) throws AnnotationBusException, BusException
	{
		if (parameters.containsKey((short) 5))
		{
			assertEquals("Signature does not match for key 5", "(vvv)", parameters.get((short) 5).getSignature());
			PropertyWidgetRangeConstraintAJ propertyWidgetRangeConstraintAJ = parameters.get((short) 5).getObject(PropertyWidgetRangeConstraintAJ.class);
			assertEquals("Signature does not match for min Variant instance in value for key 5", propertyValue.getSignature(), propertyWidgetRangeConstraintAJ.min.getSignature());
			assertEquals("Signature does not match for max Variant instance in value for key 5", propertyValue.getSignature(), propertyWidgetRangeConstraintAJ.max.getSignature());
			assertEquals("Signature does not match for increment Variant instance in value for key 5", propertyValue.getSignature(),
					propertyWidgetRangeConstraintAJ.increment.getSignature());
		}
	}



	/**
	 * Validate property control parameter layout hints.
	 *
	 * @param parameters the parameters
	 * @param propertyValue the property value
	 * @throws BusException the bus exception
	 */
	private  void validatePropertyControlParameterLayoutHints(Map<Short, Variant> parameters, Variant propertyValue) throws BusException
	{
		if (parameters.containsKey((short) 2))
		{
			Variant layoutHintsVariant = parameters.get((short) 2);
			validateLayoutHintsSignature(layoutHintsVariant);
			short[] layoutHints = layoutHintsVariant.getObject(short[].class);
			logger.debug(String.format("LayoutHints: %s", Arrays.toString(layoutHints)));
			assertEquals("Key 2 contains more than one value", 1, layoutHints.length);
			short layoutHintId = layoutHints[0];
			assertTrue(String.format("%d is not a valid value for key 2", layoutHintId), VALID_PROPERTY_CONTROL_LAYOUT_HINTS_VALUES.contains(layoutHintId));

			validateBasedOnLayoutHintId(parameters, propertyValue, layoutHintId);
		}
	}


	/**
	 * Assert equals.
	 *
	 * @param msg the msg
	 * @param i the i
	 * @param k the k
	 */
	private  void assertEquals(String msg, int i, int k) {

		if(k!=i){
			logger.error(msg);
			pass=false;
		}

	}






	/**
	 * Validate based on layout hint id.
	 *
	 * @param parameters the parameters
	 * @param propertyValue the property value
	 * @param layoutHintId the layout hint id
	 * @throws AnnotationBusException the annotation bus exception
	 * @throws BusException the bus exception
	 */
	private  void validateBasedOnLayoutHintId(Map<Short, Variant> parameters, Variant propertyValue, short layoutHintId) throws AnnotationBusException, BusException
	{
		if (layoutHintId == (short) 1)
		{
			assertEquals("Signature does not match for property Value when hint id is 1", "b", propertyValue.getSignature());
		}
		else if (layoutHintId == (short) 2 || layoutHintId == (short) 3 || layoutHintId == (short) 4)
		{
			assertOptionalParameter4IsPresent(parameters, layoutHintId);
		}
		else if (layoutHintId == (short) 6)
		{
			validateDateTimeHint(layoutHintId, propertyValue, 1);
		}
		else if (layoutHintId == (short) 7)
		{
			validateDateTimeHint(layoutHintId, propertyValue, 0);
		}
		else if (layoutHintId == (short) 5 || layoutHintId == (short) 8 || layoutHintId == (short) 9 || layoutHintId == (short) 10 || layoutHintId == (short) 12)
		{
			assertTrue(String.format("Property Value type needs to be numeric when hint id is %d. Found signature: %s", layoutHintId, propertyValue.getSignature()),
					NUMERIC_SIGNATURES.contains(propertyValue.getSignature()));
		}
		else if (layoutHintId == (short) 11 || layoutHintId == (short) 13)
		{
			assertEquals(String.format("Signature does not match for property Value when hint id is %d", layoutHintId), "s", propertyValue.getSignature());
		}
	}


	/**
	 * Validate date time hint.
	 *
	 * @param layoutHintId the layout hint id
	 * @param propertyValue the property value
	 * @param compositeType the composite type
	 * @throws AnnotationBusException the annotation bus exception
	 * @throws BusException the bus exception
	 */
	private  void validateDateTimeHint(short layoutHintId, Variant propertyValue, int compositeType) throws AnnotationBusException, BusException
	{
		assertEquals(String.format("Signature does not match for property Value when hint id is %d", layoutHintId), "q(qqq)", propertyValue.getSignature());
		PropertyWidgetThreeShortAJ propertyWidgetThreeShortAJ = propertyValue.getObject(PropertyWidgetThreeShortAJ.class);
		assertEquals(String.format("The first value in the composite type does not match when hint id is %d", layoutHintId), compositeType, propertyWidgetThreeShortAJ.dataType);
	}

	/**
	 * Assert optional parameter4 is present.
	 *
	 * @param parameters the parameters
	 * @param layoutHintId the layout hint id
	 * @throws AnnotationBusException the annotation bus exception
	 * @throws BusException the bus exception
	 */
	private  void assertOptionalParameter4IsPresent(Map<Short, Variant> parameters, short layoutHintId) throws AnnotationBusException, BusException
	{
		assertTrue(String.format("Parameters should contain key 4 when hint id is %d", layoutHintId), parameters.containsKey((short) 4));
		assertEquals("Signature does not match for key 4", "a(vs)", parameters.get((short) 4).getSignature());
		PropertyWidgetConstrainToValuesAJ[] propertyWidgetConstrainToValuesAJArray = parameters.get((short) 4).getObject(PropertyWidgetConstrainToValuesAJ[].class);
		assertNotNull("Values array for key 4 cannot be null", propertyWidgetConstrainToValuesAJArray);
		assertTrue("Values array for key 4 cannot be empty", propertyWidgetConstrainToValuesAJArray.length >= 1);
	}





	/**
	 * Validate container interface detail list.
	 *
	 * @param containerInterfaceDetailListExposedOnBus the container interface detail list exposed on bus
	 * @param isSecured the is secured
	 * @throws BusException the bus exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ParserConfigurationException the parser configuration exception
	 * @throws SAXException the SAX exception
	 */
	private  void validateContainerInterfaceDetailList(List<InterfaceDetail> containerInterfaceDetailListExposedOnBus, boolean isSecured) throws BusException, IOException,
	ParserConfigurationException, SAXException
	{
		for (InterfaceDetail interfaceDetail : containerInterfaceDetailListExposedOnBus)
		{
			String path = interfaceDetail.getPath();
			logger.debug(String.format("Validating ControlPanel.Container object at: %s", path));
			assertValidAncestorIsPresentForContainer(path);
			validateContainerBusObject(path, isSecured);
			// addContainerInterfaceDetailsToTestContext(isSecured, path);
		}
	}






	/**
	 * Assert valid ancestor is present for container.
	 *
	 * @param path the path
	 * @throws BusException the bus exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ParserConfigurationException the parser configuration exception
	 * @throws SAXException the SAX exception
	 */
	void assertValidAncestorIsPresentForContainer(String path) throws BusException, IOException, ParserConfigurationException, SAXException
	{

		assertTrue(
				"No parent bus object that implements ControlPanel nor NotificationAction nor Container nor SecuredContainer nor ListProperty nor SecuredListProperty interface found",
				busIntrospector.isAncestorInterfacePresent(path, org.alljoyn.ioe.controlpanelservice.communication.interfaces.ControlPanel.IFNAME)
				|| busIntrospector.isAncestorInterfacePresent(path, NotificationAction.IFNAME) || busIntrospector.isAncestorInterfacePresent(path, Container.IFNAME)
				|| busIntrospector.isAncestorInterfacePresent(path, ContainerSecured.IFNAME)
				|| busIntrospector.isAncestorInterfacePresent(path, ListPropertyControl.IFNAME)
				|| busIntrospector.isAncestorInterfacePresent(path, ListPropertyControlSecured.IFNAME));
	}






	/**
	 * Validate container bus object.
	 *
	 * @param path the path
	 * @param isSecured the is secured
	 * @throws BusException the bus exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private  void validateContainerBusObject(String path, boolean isSecured) throws BusException, IOException
	{
		ContainerSuper container = null;

		if (isSecured)
		{
			container = busIntrospector.getInterface(path, ContainerSecured.class);
		}
		else
		{
			container = busIntrospector.getInterface(path, Container.class);
		}

		assertEquals("Interface version does not match", Short.parseShort(IXITCP_ContainerVersion), container.getVersion());
		assertTrue(String.format("Interface state %d does not equals expected value of 0 or 1", container.getStates()), isValidState(container.getStates(), TWO_STATES_LIST));
		validateContainerParameters(container.getOptParams());
	}

	/**
	 * Checks if is valid state.
	 *
	 * @param state the state
	 * @param validStates the valid states
	 * @return true, if is valid state
	 * @throws BusException the bus exception
	 */
	private  boolean isValidState(int state, List<Integer> validStates) throws BusException
	{
		return validStates.contains(state);
	}


	/**
	 * Validate container parameters.
	 *
	 * @param parameters the parameters
	 * @throws AnnotationBusException the annotation bus exception
	 * @throws BusException the bus exception
	 */
	private  void validateContainerParameters(Map<Short, Variant> parameters) throws AnnotationBusException, BusException
	{
		validateOptionalParameter0(parameters);
		validateOptionalParameter1(parameters);
		validateContainerParameterLayoutHints(parameters);
	}


	/**
	 * Validate optional parameter0.
	 *
	 * @param parameters the parameters
	 * @throws AnnotationBusException the annotation bus exception
	 */
	private  void validateOptionalParameter0(Map<Short, Variant> parameters) throws AnnotationBusException
	{
		if (parameters.containsKey((short) 0))
		{
			assertEquals("Signature does not match for key 0", "s", parameters.get((short) 0).getSignature());
		}
	}


	/**
	 * Validate container parameter layout hints.
	 *
	 * @param parameters the parameters
	 * @throws BusException the bus exception
	 */
	private  void validateContainerParameterLayoutHints(Map<Short, Variant> parameters) throws BusException
	{
		if (parameters.containsKey((short) 2))
		{
			Variant layoutHintsVariant = parameters.get((short) 2);
			validateLayoutHintsSignature(layoutHintsVariant);
			short[] layoutHints = layoutHintsVariant.getObject(short[].class);
			logger.debug(String.format("LayoutHints: %s", Arrays.toString(layoutHints)));
			assertTrue("Key 2 contains no value", layoutHints.length > 0);

			for (short layoutHint : layoutHints)
			{
				assertTrue(String.format("%d does not match expected value of 1 or 2 for key 2", layoutHint), layoutHint == 1 || layoutHint == 2);
			}
		}
	}



	/**
	 * Validate layout hints signature.
	 *
	 * @param layoutHintsVariant the layout hints variant
	 * @throws AnnotationBusException the annotation bus exception
	 */
	private  void validateLayoutHintsSignature(Variant layoutHintsVariant) throws AnnotationBusException
	{
		assertNotNull("Key 2 for LayoutHints is missing", layoutHintsVariant);
		assertEquals("Signature does not match for key 2", "aq", layoutHintsVariant.getSignature());
	}








	/**
	 * Assert equals.
	 *
	 * @param msg the msg
	 * @param sig the sig
	 * @param signature the signature
	 */
	private  void assertEquals(String msg, String sig, String signature) {


		if(!signature.equals(sig)){
			pass=false;
			logger.error(msg);
		}


	}






	/**
	 * Validate optional parameter1.
	 *
	 * @param parameters the parameters
	 * @throws AnnotationBusException the annotation bus exception
	 */
	private  void validateOptionalParameter1(Map<Short, Variant> parameters) throws AnnotationBusException
	{
		if (parameters.containsKey((short) 1))
		{
			assertEquals("Signature does not match for key 1", "u", parameters.get((short) 1).getSignature());
		}
	}


	/**
	 * Validate control panel interface detail list.
	 *
	 * @param controlPanelInterfaceDetailListExposedOnBus the control panel interface detail list exposed on bus
	 * @throws Exception the exception
	 */
	private  void validateControlPanelInterfaceDetailList(List<InterfaceDetail> controlPanelInterfaceDetailListExposedOnBus) throws Exception
	{
		for (InterfaceDetail interfaceDetail : controlPanelInterfaceDetailListExposedOnBus)
		{
			String path = interfaceDetail.getPath();
			logger.debug(String.format("Validating ControlPanel object at: %s", path));
			assertTrue(String.format("%s does not match the expected pattern /ControlPanel/{unit}/{panelName}", path), isValidPath(ControlPanel.getValue(), path));
			validateControlPanelBusObject(path);
			// getValidationTestContext().addInterfaceDetails(org.alljoyn.ioe.controlpanelservice.communication.interfaces.ControlPanel.IFNAME, INTERFACE_VERSION, path, null, null);
		}
	}



	/**
	 * Assert true.
	 *
	 * @param msg the msg
	 * @param bool the bool
	 */
	private  void assertTrue(String msg, boolean bool) {

		if(!bool){
			logger.error(msg);
			pass=false;
		}

	}






	/**
	 * Checks if is valid path.
	 *
	 * @param pathPattern the path pattern
	 * @param path the path
	 * @return true, if is valid path
	 */
	boolean isValidPath(String pathPattern, String path)
	{
		Pattern pattern = Pattern.compile(pathPattern);
		Matcher matcher = pattern.matcher(path);

		return matcher.matches();
	}

	/**
	 * Validate control panel bus object.
	 *
	 * @param path the path
	 * @throws Exception the exception
	 */
	private  void validateControlPanelBusObject(String path) throws Exception
	{
		ControlPanel controlPanel = busIntrospector.getInterface(path, ControlPanel.class);
		assertEquals("Interface version does not match", Short.parseShort(IXITCP_ControlPanelVersion), controlPanel.getVersion());
		assertContainerObjectExists(path);
	}


	/**
	 * Assert equals.
	 *
	 * @param msg the msg
	 * @param interfaceVersion the interface version
	 * @param version the version
	 */
	private  void assertEquals(String msg, short interfaceVersion,
			short version) {

		if(interfaceVersion!=version){
			logger.error(msg);
			pass=false;
		}

	}






	/**
	 * Assert container object exists.
	 *
	 * @param path the path
	 * @throws BusException the bus exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ParserConfigurationException the parser configuration exception
	 * @throws SAXException the SAX exception
	 */
	private  void assertContainerObjectExists(String path) throws BusException, IOException, ParserConfigurationException, SAXException
	{
		List<InterfaceDetail> containerInterfaceDetailList = busIntrospector.getInterfacesExposedOnBusUnderSpecifiedPathBasedOnName(path, Container.IFNAME);

		if (containerInterfaceDetailList.isEmpty())
		{
			containerInterfaceDetailList = busIntrospector.getInterfacesExposedOnBusUnderSpecifiedPathBasedOnName(path, ContainerSecured.IFNAME);
			assertFalse(String.format("No object implementing org.alljoyn.ControlPanel.Container nor org.alljoyn.ControlPanel.SecuredContainer is under path %s", path),
					containerInterfaceDetailList.isEmpty());
		}
	}



	/**
	 * Assert false.
	 *
	 * @param msg the msg
	 * @param bool the bool
	 */
	private  void assertFalse(String msg, boolean bool) {


		if(bool){
			logger.error(msg);
			pass=false;
		}

	}

	/**
	 * Tear down.
	 *
	 * @throws Exception the exception
	 */
	public  void tearDown() throws Exception
	{	
		System.out.println("====================================================");
		logger.debug("test tearDown started");
		releaseResources();
		logger.debug("test tearDown done");
		System.out.println("====================================================");
	}


	/**
	 * Release resources.
	 */
	private  void releaseResources()
	{
		disconnectFromAboutClient();

		if (serviceHelper != null)
		{
			serviceHelper.release();
			serviceHelper = null;
		}
	}

	/**
	 * Disconnect from about client.
	 */
	private  void disconnectFromAboutClient()
	{
		if (aboutClient != null)
		{
			aboutClient.disconnect();
			aboutClient = null;
		}
	}



	/**
	 * Gets the service helper.
	 *
	 * @return the service helper
	 */
	protected  ServiceHelper getServiceHelper()
	{
		return new ServiceHelper( );
	}



	/**
	 * Gets the introspector.
	 *
	 * @return the introspector
	 * @throws Exception the exception
	 */
	BusIntrospector getIntrospector() throws Exception
	{
		return serviceHelper.getBusIntrospector(aboutClient);
	}



	/**
	 * Gets the verdict.
	 *
	 * @return the verdict
	 */
	public String getVerdict() {

		String verdict=null;

		if(inconc){
			verdict="INCONC";
		}else if(pass){
			verdict="PASS";
		}else if(!pass){
			verdict="FAIL";
		}


		return verdict;
	}

}