/*******************************************************************************
 *   *  Copyright (c) Open Connectivity Foundation (OCF) and AllJoyn Open
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
package com.at4wireless.alljoyn.testcases.conf.controlpanel;

import static com.at4wireless.alljoyn.core.controlpanel.InterfacePathPattern.ControlPanel;
import static com.at4wireless.alljoyn.core.controlpanel.InterfacePathPattern.HttpControl;

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

import org.alljoyn.bus.AboutProxy;
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
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.xml.sax.SAXException;

import com.at4wireless.alljoyn.core.about.AboutAnnouncementDetails;
import com.at4wireless.alljoyn.core.commons.AllJoynErrorReplyCodes;
import com.at4wireless.alljoyn.core.commons.ServiceHelper;
import com.at4wireless.alljoyn.core.commons.log.Logger;
import com.at4wireless.alljoyn.core.commons.log.WindowsLoggerImpl;
import com.at4wireless.alljoyn.core.introspection.BusIntrospector;
import com.at4wireless.alljoyn.core.introspection.bean.InterfaceDetail;
import com.at4wireless.alljoyn.testcases.parameter.GeneralParameter;
import com.at4wireless.alljoyn.testcases.parameter.Ics;
import com.at4wireless.alljoyn.testcases.parameter.Ixit;

public class ControlPanelTestSuite
{
	private static final Logger logger = new WindowsLoggerImpl(ControlPanelTestSuite.class.getSimpleName());
	//private static final short INTERFACE_VERSION = 1;
	private static final String BUS_APPLICATION_NAME = "ControlPanel";
	private static final List<Integer> TWO_STATES_LIST = Arrays.asList(new Integer[]
	{ 0, 1 });
	private static final List<Integer> PROPERTY_STATES_LIST = Arrays.asList(new Integer[]
	{ 0, 1, 2, 3 });
	private static final List<Short> VALID_PROPERTY_CONTROL_LAYOUT_HINTS_VALUES = Arrays.asList(new Short[]
	{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13 });
	private static final List<String> NUMERIC_SIGNATURES = Arrays.asList(new String[]
	{ "y", "n", "q", "i", "u", "x", "t", "d" });
	//private AboutClient aboutClient;
	private AboutProxy aboutProxy;
	private AboutAnnouncementDetails deviceAboutAnnouncement;
	private ServiceHelper serviceHelper; 
	private BusIntrospector busIntrospector;
	
	//private AppUnderTestDetails appUnderTestDetails;
	private  UUID dutAppId;
	private  String dutDeviceId;

	/** 
	 * [AT4] Added attributes to perform the test cases
	 * 
	 * */
	private String KeyStorePath = "/KeyStore";
	private long ANNOUNCEMENT_TIMEOUT_IN_SECONDS;
	
	boolean pass = true;
	boolean inconc = false;
	private Ics icsList;
	private Ixit ixitList;

	public ControlPanelTestSuite(String testCase, Ics icsList, Ixit ixitList, GeneralParameter gpList)
	{
		/** 
		 * [AT4] Attributes initialization
		 * */
		this.icsList = icsList;
		this.ixitList = ixitList;
		ANNOUNCEMENT_TIMEOUT_IN_SECONDS = gpList.GPCO_AnnouncementTimeout;
		
		try
		{
			runTestCase(testCase);
		}
		catch(Exception e)
		{
			inconc = true;
		}
	}

	public void runTestCase(String testCase) throws Exception
	{
		setUp();
		
		try
		{
			logger.info(String.format("Running testcase: %s", testCase));
		
			if (testCase.equals("ControlPanel-v1-01"))
			{
				testControlPanel_v1_01_ValidateControlPanelBusObjects();
			}
			else if(testCase.equals("ControlPanel-v1-02"))
			{
				testControlPanel_v1_02_ValidateContainerBusObjects();
			}
			else if(testCase.equals("ControlPanel-v1-03"))
			{
				testControlPanel_v1_03_ValidatePropertyBusObjects();
			}
			else if(testCase.equals("ControlPanel-v1-04"))
			{
				testControlPanel_v1_04_ValidateLabelPropertyBusObjects();
			}
			else if(testCase.equals("ControlPanel-v1-05"))
			{
				testControlPanel_v1_05_ValidateActionBusObjects();
			}
			else if(testCase.equals("ControlPanel-v1-06"))
			{
				testControlPanel_v1_06_ValidateDialogBusObjects();
			}
			else if(testCase.equals("ControlPanel-v1-07"))
			{
				testControlPanel_v1_07_ValidateListPropertyBusObjects();
			}
			else if(testCase.equals("ControlPanel-v1-08"))
			{
				testControlPanel_v1_08_ValidateNotificationActionBusObjects();
			}
			else if(testCase.equals("ControlPanel-v1-09"))
			{
				testControlPanel_v1_09_ValidateHttpControlBusObjects();
			}
			else if(testCase.equals("ControlPanel-v1-10"))
			{
				testControlPanel_v1_10_ValidateSecuredControlPanelBusObjects();
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
		try
		{
			System.out.println("====================================================");
			logger.info("test setUp started");
			
			//appUnderTestDetails = getValidationTestContext().getAppUnderTestDetails(); //[AT4] Not needed
			//dutDeviceId = appUnderTestDetails.getDeviceId();
			dutDeviceId = ixitList.IXITCO_DeviceId;
			logger.info(String.format("Running ControlPanel test case against Device ID: %s", dutDeviceId));
			//dutAppId = appUnderTestDetails.getAppId();
			dutAppId = ixitList.IXITCO_AppId;
			logger.info(String.format("Running ControlPanel test case against App ID: %s", dutAppId));
			//String keyStorePath = getValidationTestContext().getKeyStorePath();
			String keyStorePath = KeyStorePath;
			logger.info(String.format("Running Config test case using KeyStorePath: %s", keyStorePath));
			
			serviceHelper = getServiceHelper();
			serviceHelper.initialize(BUS_APPLICATION_NAME, dutDeviceId, dutAppId);
			
            //deviceAboutAnnouncement = serviceHelper.waitForNextDeviceAnnouncement(determineAboutAnnouncementTimeout(), TimeUnit.SECONDS);
			deviceAboutAnnouncement = serviceHelper.waitForNextDeviceAnnouncement(ANNOUNCEMENT_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS); //[AT4]
			assertNotNull("Timed out waiting for About announcement", deviceAboutAnnouncement);
			//aboutClient = serviceHelper.connectAboutClient(deviceAboutAnnouncement);
			aboutProxy = serviceHelper.connectAboutProxy(deviceAboutAnnouncement);
			serviceHelper.enableAuthentication(keyStorePath);
			busIntrospector = getIntrospector();
			
			logger.info("test setUp done");
			System.out.println("====================================================");
		}
		catch (Exception exception)
		{
			try
			{
				//releaseResources();
				tearDown(); //[AT4] Modified to include logs
			}
			catch (Exception newException)
			{
				logger.info("Exception releasing resources", newException);
			}

			throw exception;
		}
	}
	
	public void tearDown() throws Exception
	{	
		//super.tearDown();
		System.out.println("====================================================");
		logger.debug("test tearDown started");
		releaseResources();
		logger.debug("test tearDown done");
		System.out.println("====================================================");
	}

	public void testControlPanel_v1_01_ValidateControlPanelBusObjects() throws Exception
	{
		List<InterfaceDetail> controlPanelInterfaceDetailListExposedOnBus = busIntrospector
				.getInterfacesExposedOnBusBasedOnName(org.alljoyn.ioe.controlpanelservice.communication.interfaces.ControlPanel.IFNAME);

		if (controlPanelInterfaceDetailListExposedOnBus.isEmpty())
		{
			//getValidationTestContext().addNote("No bus objects implement ControlPanel interface");
			fail("No bus objects implement ControlPanel interface");
			return;
		}

		validateControlPanelInterfaceDetailList(controlPanelInterfaceDetailListExposedOnBus);
	}

	public void testControlPanel_v1_02_ValidateContainerBusObjects() throws Exception
	{
		List<InterfaceDetail> containerInterfaceDetailListExposedOnBus = busIntrospector.getInterfacesExposedOnBusBasedOnName(Container.IFNAME);
		List<InterfaceDetail> securedContainerInterfaceDetailListExposedOnBus = busIntrospector.getInterfacesExposedOnBusBasedOnName(ContainerSecured.IFNAME);

		if (containerInterfaceDetailListExposedOnBus.isEmpty() && securedContainerInterfaceDetailListExposedOnBus.isEmpty())
		{
			//getValidationTestContext().addNote("No bus objects implement Container nor SecuredContainer interfaces");
			fail("No bus objects implement Container nor SecuredContainer interfaces");
			return;
		}

		validateContainerInterfaceDetailList(containerInterfaceDetailListExposedOnBus, false);
		validateContainerInterfaceDetailList(securedContainerInterfaceDetailListExposedOnBus, true);
	}

	public void testControlPanel_v1_03_ValidatePropertyBusObjects() throws Exception
	{
		List<InterfaceDetail> propertyInterfaceDetailListExposedOnBus = busIntrospector.getInterfacesExposedOnBusBasedOnName(PropertyControl.IFNAME);
		List<InterfaceDetail> securedPropertyInterfaceDetailListExposedOnBus = busIntrospector.getInterfacesExposedOnBusBasedOnName(PropertyControlSecured.IFNAME);

		if (propertyInterfaceDetailListExposedOnBus.isEmpty() && securedPropertyInterfaceDetailListExposedOnBus.isEmpty())
		{
			//getValidationTestContext().addNote("No bus objects implement Container nor SecuredContainer interfaces");
			fail("No bus objects implement Property nor SecuredProperty interfaces");
			return;
		}

		validatePropertyInterfaceDetailList(propertyInterfaceDetailListExposedOnBus, false);
		validatePropertyInterfaceDetailList(securedPropertyInterfaceDetailListExposedOnBus, true);
	}

	public void testControlPanel_v1_04_ValidateLabelPropertyBusObjects() throws Exception
	{
		List<InterfaceDetail> labelPropertyInterfaceDetailListExposedOnBus = busIntrospector.getInterfacesExposedOnBusBasedOnName(Label.IFNAME);

		if (labelPropertyInterfaceDetailListExposedOnBus.isEmpty())
		{
			//getValidationTestContext().addNote("No bus objects implement LabelProperty interface");
			fail("No bus objects implement LabelProperty interface");
			return;
		}

		validateLabelPropertyInterfaceDetailList(labelPropertyInterfaceDetailListExposedOnBus);
	}

	public void testControlPanel_v1_05_ValidateActionBusObjects() throws Exception
	{
		List<InterfaceDetail> actionInterfaceDetailListExposedOnBus = busIntrospector.getInterfacesExposedOnBusBasedOnName(ActionControl.IFNAME);
		List<InterfaceDetail> securedActionInterfaceDetailListExposedOnBus = busIntrospector.getInterfacesExposedOnBusBasedOnName(ActionControlSecured.IFNAME);

		if (actionInterfaceDetailListExposedOnBus.isEmpty() && securedActionInterfaceDetailListExposedOnBus.isEmpty())
		{
			//getValidationTestContext().addNote("No bus objects implement Action nor SecuredAction interfaces");
			fail("No bus objects implement Action nor SecuredAction interfaces");
			return;
		}

		validateActionInterfaceDetailList(actionInterfaceDetailListExposedOnBus, false);
		validateActionInterfaceDetailList(securedActionInterfaceDetailListExposedOnBus, true);
	}

	public void testControlPanel_v1_06_ValidateDialogBusObjects() throws Exception
	{
		List<InterfaceDetail> dialogInterfaceDetailListExposedOnBus = busIntrospector.getInterfacesExposedOnBusBasedOnName(AlertDialog.IFNAME);
		List<InterfaceDetail> securedDialogInterfaceDetailListExposedOnBus = busIntrospector.getInterfacesExposedOnBusBasedOnName(AlertDialogSecured.IFNAME);
		int numberOfActionsFound=1;
		
		if (dialogInterfaceDetailListExposedOnBus.isEmpty() && securedDialogInterfaceDetailListExposedOnBus.isEmpty())
		{
			//getValidationTestContext().addNote("No bus objects implement Dialog nor SecuredDialog interfaces");
			fail("No bus objects implement Dialog nor SecuredDialog interfaces");
			return;
		}

		// [ASACOMP-64] DialogInterface. Action2 and Action3 methods are not supported
		// [ASACOMP-64] Changes start. Methods are also modified
		numberOfActionsFound = validateDialogInterfaceDetailList(dialogInterfaceDetailListExposedOnBus, false, numberOfActionsFound);
		validateDialogInterfaceDetailList(securedDialogInterfaceDetailListExposedOnBus, true, numberOfActionsFound);
		// [ASACOMP-64] changes end
	}

	public void testControlPanel_v1_07_ValidateListPropertyBusObjects() throws Exception
	{
		List<InterfaceDetail> listPropertyInterfaceDetailListExposedOnBus = busIntrospector.getInterfacesExposedOnBusBasedOnName(ListPropertyControl.IFNAME);
		List<InterfaceDetail> securedListPropertyInterfaceDetailListExposedOnBus = busIntrospector.getInterfacesExposedOnBusBasedOnName(ListPropertyControlSecured.IFNAME);

		if (listPropertyInterfaceDetailListExposedOnBus.isEmpty() && securedListPropertyInterfaceDetailListExposedOnBus.isEmpty())
		{
			//getValidationTestContext().addNote("No bus objects implement ListProperty nor SecuredListProperty interfaces");
			fail("No bus objects implement ListProperty nor SecuredListProperty interfaces");
			return;
		}

		validateListPropertyInterfaceDetailList(listPropertyInterfaceDetailListExposedOnBus, false);
		validateListPropertyInterfaceDetailList(securedListPropertyInterfaceDetailListExposedOnBus, true);
	}

	public void testControlPanel_v1_08_ValidateNotificationActionBusObjects() throws Exception
	{
		List<InterfaceDetail> notificationActionInterfaceDetailListExposedOnBus = busIntrospector.getInterfacesExposedOnBusBasedOnName(NotificationAction.IFNAME);

		if (notificationActionInterfaceDetailListExposedOnBus.isEmpty())
		{
			//getValidationTestContext().addNote("No bus objects implement NotificationAction interface");
			fail("No bus objects implement NotificationAction interface");
			return;
		}

		validateNotificationActionInterfaceDetailList(notificationActionInterfaceDetailListExposedOnBus);
	}

	public void testControlPanel_v1_09_ValidateHttpControlBusObjects() throws Exception
	{
		List<InterfaceDetail> httpControlInterfaceDetailListExposedOnBus = busIntrospector.getInterfacesExposedOnBusBasedOnName(HTTPControl.IFNAME);

		if (httpControlInterfaceDetailListExposedOnBus.isEmpty())
		{
			//getValidationTestContext().addNote("No bus objects implement HTTPControl interface");
			fail("No bus objects implement HTTPControl interface");
			return;
		}

		validateHttpControlInterfaceDetailList(httpControlInterfaceDetailListExposedOnBus);
	}

	public void testControlPanel_v1_10_ValidateSecuredControlPanelBusObjects() throws Exception
	{
		List<InterfaceDetail> securedContainerInterfaceDetailListExposedOnBus = busIntrospector.getInterfacesExposedOnBusBasedOnName(ContainerSecured.IFNAME);
		List<InterfaceDetail> securedPropertyInterfaceDetailListExposedOnBus = busIntrospector.getInterfacesExposedOnBusBasedOnName(PropertyControlSecured.IFNAME);
		List<InterfaceDetail> securedActionInterfaceDetailListExposedOnBus = busIntrospector.getInterfacesExposedOnBusBasedOnName(ActionControlSecured.IFNAME);
		List<InterfaceDetail> securedDialogInterfaceDetailListExposedOnBus = busIntrospector.getInterfacesExposedOnBusBasedOnName(AlertDialogSecured.IFNAME);
		List<InterfaceDetail> securedListPropertyInterfaceDetailListExposedOnBus = busIntrospector.getInterfacesExposedOnBusBasedOnName(ListPropertyControlSecured.IFNAME);

		if (isEmpty(securedContainerInterfaceDetailListExposedOnBus, securedPropertyInterfaceDetailListExposedOnBus, securedActionInterfaceDetailListExposedOnBus,
				securedDialogInterfaceDetailListExposedOnBus, securedListPropertyInterfaceDetailListExposedOnBus))
		{
			//getValidationTestContext().addNote("No bus objects implement one of the secured ControlPanel interfaces");
			fail("No bus objects implement one of the secured ControlPanel interfaces");
			return;
		}

		validateSecuredContainerInvalidPasscodeInterfaceDetailList(securedContainerInterfaceDetailListExposedOnBus);
		validateSecuredPropertyInvalidPasscodeInterfaceDetailList(securedPropertyInterfaceDetailListExposedOnBus);
		validateSecuredActionInvalidPasscodeInterfaceDetailList(securedActionInterfaceDetailListExposedOnBus);
		validateSecuredDialogInvalidPasscodeInterfaceDetailList(securedDialogInterfaceDetailListExposedOnBus);
		validateSecuredListPropertyInvalidPasscodeInterfaceDetailList(securedListPropertyInterfaceDetailListExposedOnBus);
	}
	
	boolean isValidUrl(String supportUrl)
	{
		try
		{
			new URL(supportUrl).toURI();
		}
		catch (MalformedURLException malformedURLException)
		{
			logger.info("Invalid URL", malformedURLException);
			return false;
		}
		catch (URISyntaxException e)
		{
			logger.info("Invalid URL", e);
			return false;
		}

		return true;
	}
	
	boolean isValidPath(String pathPattern, String path)
	{
		Pattern pattern = Pattern.compile(pathPattern);
		Matcher matcher = pattern.matcher(path);

		return matcher.matches();
	}
	
	HttpClient getHttpClient()
	{
		//return new DefaultHttpClient(); //[AT4]
		HttpClient httpClient = HttpClientBuilder.create().build();
		return httpClient;
	}
	
	BusIntrospector getIntrospector() throws Exception
	{
		//return serviceHelper.getBusIntrospector(aboutClient);
		return serviceHelper.getBusIntrospector(deviceAboutAnnouncement);
	}
	
	protected ServiceHelper getServiceHelper()
	{
		return new ServiceHelper();
	}
	
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
	
	void assertValidAncestorIsPresentForDialog(String path) throws BusException, IOException, ParserConfigurationException, SAXException
	{
		assertTrue("No parent bus object that implements Action nor SecuredAction nor NotificationAction interface found",
				busIntrospector.isAncestorInterfacePresent(path, ActionControl.IFNAME) || busIntrospector.isAncestorInterfacePresent(path, ActionControlSecured.IFNAME)
						|| busIntrospector.isAncestorInterfacePresent(path, NotificationAction.IFNAME));
	}
	
	void assertAncestorContainerIsPresent(String path) throws BusException, IOException, ParserConfigurationException, SAXException
	{
		assertTrue("No parent bus object that implements Container nor SecuredContainer interface found", busIntrospector.isAncestorInterfacePresent(path, Container.IFNAME)
				|| busIntrospector.isAncestorInterfacePresent(path, ContainerSecured.IFNAME));
	}
	
	private void releaseResources()
	{
		//disconnectFromAboutClient();
		if (aboutProxy != null)
		{
			aboutProxy = null;
		}

		if (serviceHelper != null)
		{
			serviceHelper.release();
			serviceHelper = null;
		}
	}

	/*private void disconnectFromAboutClient()
	{
		if (aboutClient != null)
		{
			aboutClient.disconnect();
			aboutClient = null;
		}
	}*/
	
	private void validateControlPanelInterfaceDetailList(List<InterfaceDetail> controlPanelInterfaceDetailListExposedOnBus) throws Exception
	{
		for (InterfaceDetail interfaceDetail : controlPanelInterfaceDetailListExposedOnBus)
		{
			String path = interfaceDetail.getPath();
			logger.info(String.format("Validating ControlPanel object at: %s", path));
			assertTrue(String.format("%s does not match the expected pattern /ControlPanel/{unit}/{panelName}", path), isValidPath(ControlPanel.getValue(), path));
			validateControlPanelBusObject(path);
			// getValidationTestContext().addInterfaceDetails(org.alljoyn.ioe.controlpanelservice.communication.interfaces.ControlPanel.IFNAME, INTERFACE_VERSION, path, null, null);
		}
	}
	
	private void validateContainerInterfaceDetailList(List<InterfaceDetail> containerInterfaceDetailListExposedOnBus, boolean isSecured) throws BusException, IOException,
			ClientProtocolException, ParserConfigurationException, SAXException
	{
		for (InterfaceDetail interfaceDetail : containerInterfaceDetailListExposedOnBus)
		{
			String path = interfaceDetail.getPath();
			logger.info(String.format("Validating ControlPanel.Container object at: %s", path));
			assertValidAncestorIsPresentForContainer(path);
			validateContainerBusObject(path, isSecured);
			// addContainerInterfaceDetailsToTestContext(isSecured, path);
		}
	}
	
	private void validatePropertyInterfaceDetailList(List<InterfaceDetail> propertyInterfaceDetailListExposedOnBus, boolean isSecured) throws BusException, IOException,
			ClientProtocolException, ParserConfigurationException, SAXException
	{
		for (InterfaceDetail interfaceDetail : propertyInterfaceDetailListExposedOnBus)
		{
			String path = interfaceDetail.getPath();
			logger.info(String.format("Validating ControlPanel.Property object at: %s", path));
			assertAncestorContainerIsPresent(path);
			validatePropertyBusObject(path, isSecured);
			//addPropertyInterfaceDetailsToTestContext(isSecured, path);
		}
	}
	
	private void validateLabelPropertyInterfaceDetailList(List<InterfaceDetail> labelPropertyInterfaceDetailListExposedOnBus) throws BusException, IOException,
			ClientProtocolException, ParserConfigurationException, SAXException
	{
		for (InterfaceDetail interfaceDetail : labelPropertyInterfaceDetailListExposedOnBus)
		{
			String path = interfaceDetail.getPath();
			logger.info(String.format("Validating ControlPanel.LabelProperty object at: %s", path));
			assertAncestorContainerIsPresent(path);
			validateLabelPropertyBusObject(path);
			//getValidationTestContext().addInterfaceDetails(Label.IFNAME, INTERFACE_VERSION, path, null, null);
		}
	}
	
	private void validateActionInterfaceDetailList(List<InterfaceDetail> actionInterfaceDetailListExposedOnBus, boolean isSecured) throws BusException, IOException,
			ClientProtocolException, ParserConfigurationException, SAXException
	{
		for (InterfaceDetail interfaceDetail : actionInterfaceDetailListExposedOnBus)
		{
			String path = interfaceDetail.getPath();
			logger.info(String.format("Validating ControlPanel.Action object at: %s", path));
			assertAncestorContainerIsPresent(path);
			validateActionBusObject(path, isSecured);
			// addActionInterfaceDetailsToTestContext(isSecured, path);
		}
	}
	
	private int validateDialogInterfaceDetailList(List<InterfaceDetail> dialogInterfaceDetailListExposedOnBus, boolean isSecured,
			int actionsFound) throws BusException, IOException, 
			ClientProtocolException, ParserConfigurationException, SAXException
	{
		int nActions = actionsFound; //[AT4]
		
		for (InterfaceDetail interfaceDetail : dialogInterfaceDetailListExposedOnBus)
		{
			String path = interfaceDetail.getPath();
			logger.info(String.format("Validating ControlPanel.Dialog object at: %s", path));
			assertValidAncestorIsPresentForDialog(path);
			validateDialogBusObject(path, isSecured);

			/** 
			 * [AT4] Code added to check the presence of Action2 and Action3 methods
			 * */
			AlertDialogSuper dialog = null;
			if (isSecured) {
				dialog = busIntrospector.getInterface(path, AlertDialogSecured.class);
			} else {
				dialog = busIntrospector.getInterface(path, AlertDialog.class);
			}

			short numberOfActions = dialog.getNumActions();
			
			if (nActions < numberOfActions) {
				nActions = numberOfActions;
			}
		}
		
		/** 
		 * [AT4] Code added to compare results with ICS
		 * */
		if (((icsList.ICSCP_DI_Action2 && (!isSecured)) || (icsList.ICSCP_SDI_Action2 && isSecured)) && (nActions<2))
		{
			fail("Method Action2() is not present"); 
		}

		if (((icsList.ICSCP_DI_Action3 && (!isSecured)) || (icsList.ICSCP_SDI_Action3 && isSecured)) && (nActions<3))
		{
			fail("Method Action3() is not present");
		}
		
		return nActions;
	}
	
	/*private void addDialogInterfaceDetailsToTestContext(boolean isSecured, String path)
    {
        if (isSecured)
        {
            getValidationTestContext().addInterfaceDetails(AlertDialogSecured.IFNAME, INTERFACE_VERSION, path, null, null);
        }
        else
        {
            getValidationTestContext().addInterfaceDetails(AlertDialog.IFNAME, INTERFACE_VERSION, path, null, null);
        }
    }*/ //[AT4] Not needed

	private void validateListPropertyInterfaceDetailList(List<InterfaceDetail> listPropertyInterfaceDetailListExposedOnBus, boolean isSecured) throws BusException, IOException,
			ClientProtocolException, ParserConfigurationException, SAXException
	{
		for (InterfaceDetail interfaceDetail : listPropertyInterfaceDetailListExposedOnBus)
		{
			String path = interfaceDetail.getPath();
			logger.info(String.format("Validating ControlPanel.ListProperty object at: %s", path));
			assertAncestorContainerIsPresent(path);
			validateListPropertyBusObject(path, isSecured);
			// addListPropertyInterfaceDetailsToTestContext(isSecured, path);
		}
	}
	
	private void validateNotificationActionInterfaceDetailList(List<InterfaceDetail> notificationActionInterfaceDetailListExposedOnBus) throws Exception
	{
		for (InterfaceDetail interfaceDetail : notificationActionInterfaceDetailListExposedOnBus)
		{
			String path = interfaceDetail.getPath();
			logger.info(String.format("Validating NotificationAction object at: %s", path));
			assertTrue(String.format("%s does not match the expected pattern /ControlPanel/{unit}/{actionPanelName}", path), isValidPath(ControlPanel.getValue(), path));
			validateNotificationActionBusObject(path);
			//getValidationTestContext().addInterfaceDetails(NotificationAction.IFNAME, INTERFACE_VERSION, path, null, null);
		}
	}
	
	private void validateHttpControlInterfaceDetailList(List<InterfaceDetail> httpControlInterfaceDetailListExposedOnBus) throws BusException, IOException, ClientProtocolException
	{
		for (InterfaceDetail interfaceDetail : httpControlInterfaceDetailListExposedOnBus)
		{
			String path = interfaceDetail.getPath();
			logger.info(String.format("Validating HTTPControl object at: %s", path));
			assertTrue(String.format("%s does not match the expected pattern /ControlPanel/{unit}/HTTPControl", path), isValidPath(HttpControl.getValue(), path));
			validateHttpControlBusObject(path);
			//getValidationTestContext().addInterfaceDetails(HTTPControl.IFNAME, INTERFACE_VERSION, path, null, null);
		}
	}
	
	private void validateSecuredContainerInvalidPasscodeInterfaceDetailList(List<InterfaceDetail> securedContainerInterfaceDetailListExposedOnBus) throws BusException,
			IOException, ClientProtocolException
	{
		for (InterfaceDetail interfaceDetail : securedContainerInterfaceDetailListExposedOnBus)
		{
			String path = interfaceDetail.getPath();
			validateSecuredContainerInvalidPasscodeBusObject(path);
		}
	}

	private void validateSecuredPropertyInvalidPasscodeInterfaceDetailList(List<InterfaceDetail> securedPropertyInterfaceDetailListExposedOnBus) throws BusException, IOException,
			ClientProtocolException
	{
		for (InterfaceDetail interfaceDetail : securedPropertyInterfaceDetailListExposedOnBus)
		{
			String path = interfaceDetail.getPath();
			validateSecuredPropertyControlInvalidPasscodeBusObject(path);
		}
	}

	private void validateSecuredActionInvalidPasscodeInterfaceDetailList(List<InterfaceDetail> actionInterfaceDetailListExposedOnBus) throws BusException, IOException,
			ClientProtocolException
	{
		for (InterfaceDetail interfaceDetail : actionInterfaceDetailListExposedOnBus)
		{
			String path = interfaceDetail.getPath();
			validateSecuredActionInvalidPasscodeBusObject(path);
		}
	}

	private void validateSecuredDialogInvalidPasscodeInterfaceDetailList(List<InterfaceDetail> securedDialogInterfaceDetailListExposedOnBus) throws BusException, IOException,
			ClientProtocolException
	{
		for (InterfaceDetail interfaceDetail : securedDialogInterfaceDetailListExposedOnBus)
		{
			String path = interfaceDetail.getPath();
			validateSecuredDialogInvalidPasscodeBusObject(path);
		}
	}

	private void validateSecuredListPropertyInvalidPasscodeInterfaceDetailList(List<InterfaceDetail> listPropertyInterfaceDetailListExposedOnBus) throws BusException, IOException,
			ClientProtocolException
	{
		for (InterfaceDetail interfaceDetail : listPropertyInterfaceDetailListExposedOnBus)
		{
			String path = interfaceDetail.getPath();
			validateSecuredListPropertyInvalidPasscodeBusObject(path);
		}
	}
	
	/*private void addContainerInterfaceDetailsToTestContext(boolean isSecured, String path)
    {
        if (isSecured)
        {
            getValidationTestContext().addInterfaceDetails(ContainerSecured.IFNAME, INTERFACE_VERSION, path, null, null);
        }
        else
        {
            getValidationTestContext().addInterfaceDetails(Container.IFNAME, INTERFACE_VERSION, path, null, null);
        }
    }*/ //[AT4] Not needed

    /*private void addPropertyInterfaceDetailsToTestContext(boolean isSecured, String path)
    {
        if (isSecured)
        {
            getValidationTestContext().addInterfaceDetails(PropertyControlSecured.IFNAME, INTERFACE_VERSION, path, null, null);
        }
        else
        {
            getValidationTestContext().addInterfaceDetails(PropertyControl.IFNAME, INTERFACE_VERSION, path, null, null);
        }
    }*/ //[AT4] Not needed

    /*private void addActionInterfaceDetailsToTestContext(boolean isSecured, String path)
    {
        if (isSecured)
        {
            getValidationTestContext().addInterfaceDetails(ActionControlSecured.IFNAME, INTERFACE_VERSION, path, null, null);
        }
        else
        {
            getValidationTestContext().addInterfaceDetails(ActionControl.IFNAME, INTERFACE_VERSION, path, null, null);
        }
    }*/ //[AT4] Not needed

    /*private void addListPropertyInterfaceDetailsToTestContext(boolean isSecured, String path)
    {
        if (isSecured)
        {
            getValidationTestContext().addInterfaceDetails(ListPropertyControlSecured.IFNAME, INTERFACE_VERSION, path, null, null);
        }
        else
        {
            getValidationTestContext().addInterfaceDetails(ListPropertyControl.IFNAME, INTERFACE_VERSION, path, null, null);
        }
    }*/ //[AT4] Not needed
	
	private void validateControlPanelBusObject(String path) throws Exception
	{
		ControlPanel controlPanel = busIntrospector.getInterface(path, ControlPanel.class);
		//assertEquals("Interface version does not match", INTERFACE_VERSION, controlPanel.getVersion());
		assertEquals("Interface version does not match", ixitList.IXITCP_ControlPanelVersion, controlPanel.getVersion());
		assertContainerObjectExists(path);
	}
	
	private void validateContainerBusObject(String path, boolean isSecured) throws BusException, IOException, ClientProtocolException
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

		//assertEquals("Interface version does not match", INTERFACE_VERSION, container.getVersion());
		assertEquals("Interface version does not match", ixitList.IXITCP_ContainerVersion, container.getVersion());
		assertTrue(String.format("Interface state %d does not equals expected value of 0 or 1", container.getStates()), isValidState(container.getStates(), TWO_STATES_LIST));
		validateContainerParameters(container.getOptParams());
	}
	
	private void validatePropertyBusObject(String path, boolean isSecured) throws BusException, IOException, ClientProtocolException
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

		//assertEquals("Interface version does not match", INTERFACE_VERSION, propertyControl.getVersion());
		assertEquals("Interface version does not match", ixitList.IXITCP_PropertyVersion, propertyControl.getVersion());
		assertTrue(String.format("Interface state %d does not equals expected value of 0, 1, 2 or 3", propertyControl.getStates()),
				isValidState(propertyControl.getStates(), PROPERTY_STATES_LIST));
		validatePropertyControlParameters(propertyControl.getOptParams(), propertyControl.getValue());
	}
	
	private void validateLabelPropertyBusObject(String path) throws BusException, IOException, ClientProtocolException
	{
		Label label = busIntrospector.getInterface(path, Label.class);
		//assertEquals("Interface version does not match", INTERFACE_VERSION, label.getVersion());
		assertEquals("Interface version does not match", ixitList.IXITCP_LabelPropertyVersion, label.getVersion());
		assertTrue(String.format("Interface state %d does not equals expected value of 0 or 1", label.getStates()), isValidState(label.getStates(), TWO_STATES_LIST));
		assertTrue("Label property must be a String", label.getLabel() instanceof String);
		validateLabelPropertyParameters(label.getOptParams());
	}
	
	private void validateActionBusObject(String path, boolean isSecured) throws BusException, IOException, ClientProtocolException
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

		//assertEquals("Interface version does not match", INTERFACE_VERSION, action.getVersion());
		assertEquals("Interface version does not match", ixitList.IXITCP_ActionVersion, action.getVersion());
		assertTrue(String.format("Interface state %d does not equals expected value of 0 or 1", action.getStates()), isValidState(action.getStates(), TWO_STATES_LIST));
		validateActionParameters(action.getOptParams());
	}
	
	private void validateDialogBusObject(String path, boolean isSecured) throws IOException, ClientProtocolException, BusException
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

        //assertEquals("Interface version does not match", INTERFACE_VERSION, dialog.getVersion());
		assertEquals("Interface version does not match", ixitList.IXITCP_DialogVersion, dialog.getVersion());
		assertTrue(String.format("Interface state %d does not equals expected value of 0 or 1", dialog.getStates()), isValidState(dialog.getStates(), TWO_STATES_LIST));
		short numberOfActions = dialog.getNumActions();
		validateDialogParameters(dialog.getOptParams(), numberOfActions);
		logger.info(String.format("Dialog message: %s", dialog.getMessage()));
		validateDialogActions(dialog, numberOfActions);
	}
	
	private void validateListPropertyBusObject(String path, boolean isSecured) throws BusException, IOException, ClientProtocolException
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

		//assertEquals("Interface version does not match", INTERFACE_VERSION, listPropertyControl.getVersion());
		assertEquals("Interface version does not match", ixitList.IXITCP_ListPropertyVersion, listPropertyControl.getVersion());
		assertTrue(String.format("Interface state %d does not equals expected value of 0 or 1", listPropertyControl.getStates()),
				isValidState(listPropertyControl.getStates(), TWO_STATES_LIST));
		validateListPropertyParameters(listPropertyControl.getOptParams());
		validateListPropertyValue(listPropertyControl);
	}
	
	private void validateNotificationActionBusObject(String path) throws Exception
	{
		NotificationAction notificationAction = busIntrospector.getInterface(path, NotificationAction.class);
		//assertEquals("Interface version does not match", INTERFACE_VERSION, notificationAction.getVersion());
		assertEquals("Interface version does not match", ixitList.IXITCP_NotificationActionVersion, notificationAction.getVersion());
		assertContainerOrDialogObjectExists(path);
	}
	
	private void validateHttpControlBusObject(String path) throws BusException, IOException, ClientProtocolException
	{
		HTTPControl httpControl = busIntrospector.getInterface(path, HTTPControl.class);
		//assertEquals("Interface version does not match", INTERFACE_VERSION, httpControl.getVersion());
		assertEquals("Interface version does not match", ixitList.IXITCP_HTTPControlVersion, httpControl.getVersion());
		validateRootUrl(httpControl.GetRootURL());
	}
	
	private void validateSecuredContainerInvalidPasscodeBusObject(String path) throws BusException, IOException, ClientProtocolException
	{
		ContainerSecured securedContainer = busIntrospector.getInterface(path, ContainerSecured.class);
		setInvalidPassword();

		try
		{
			char[] currentPasscode = serviceHelper.getAuthPassword(deviceAboutAnnouncement);
			logger.info(String.format("Connecting with invalid password: %s", currentPasscode));
			securedContainer.getVersion();
			invalidPasswordAccessFailureToThrowException("container");
		}
		catch (BusException busException)
		{
			handleBusExceptionOnInvalidPasswordBusObjectAccess(busException);
		}
	}

	private void validateSecuredPropertyControlInvalidPasscodeBusObject(String path) throws BusException, IOException, ClientProtocolException
	{
		PropertyControlSecured propertyControl = busIntrospector.getInterface(path, PropertyControlSecured.class);
		setInvalidPassword();

		try
		{
			char[] currentPasscode = serviceHelper.getAuthPassword(deviceAboutAnnouncement);
			logger.info(String.format("Connecting with invalid password: %s", currentPasscode));
			propertyControl.getVersion();
			invalidPasswordAccessFailureToThrowException("property");
		}
		catch (BusException be)
		{
			handleBusExceptionOnInvalidPasswordBusObjectAccess(be);
		}
	}

	private void validateSecuredActionInvalidPasscodeBusObject(String path) throws BusException, IOException, ClientProtocolException
	{
		ActionControlSecured action = busIntrospector.getInterface(path, ActionControlSecured.class);
		setInvalidPassword();

		try
		{
			char[] currentPasscode = serviceHelper.getAuthPassword(deviceAboutAnnouncement);
			logger.info(String.format("Connecting with invalid password: %s", currentPasscode));
			action.getVersion();
			invalidPasswordAccessFailureToThrowException("action");
		}
		catch (BusException be)
		{
			handleBusExceptionOnInvalidPasswordBusObjectAccess(be);
		}
	}

	private void validateSecuredDialogInvalidPasscodeBusObject(String path) throws IOException, ClientProtocolException, BusException
	{
		AlertDialogSecured securedDialog = busIntrospector.getInterface(path, AlertDialogSecured.class);
		setInvalidPassword();

		try
		{
			char[] currentPasscode = serviceHelper.getAuthPassword(deviceAboutAnnouncement);
			logger.info(String.format("Connecting with invalid password: %s", currentPasscode));
			securedDialog.getVersion();
			invalidPasswordAccessFailureToThrowException("dialog");
		}
		catch (BusException be)
		{
			handleBusExceptionOnInvalidPasswordBusObjectAccess(be);
		}
	}

	private void validateSecuredListPropertyInvalidPasscodeBusObject(String path) throws BusException, IOException
	{
		ListPropertyControlSecured listPropertyControlSecured = busIntrospector.getInterface(path, ListPropertyControlSecured.class);
		setInvalidPassword();

		try
		{
			char[] currentPasscode = serviceHelper.getAuthPassword(deviceAboutAnnouncement);
			logger.info(String.format("Connecting with invalid password: %s", currentPasscode));
			listPropertyControlSecured.getVersion();
			invalidPasswordAccessFailureToThrowException("list property");
		}
		catch (BusException be)
		{
			handleBusExceptionOnInvalidPasswordBusObjectAccess(be);
		}
	}
	
	private void assertContainerObjectExists(String path) throws BusException, IOException, ParserConfigurationException, SAXException
	{
		List<InterfaceDetail> containerInterfaceDetailList = busIntrospector.getInterfacesExposedOnBusUnderSpecifiedPathBasedOnName(path, Container.IFNAME);

		if (containerInterfaceDetailList.isEmpty())
		{
			containerInterfaceDetailList = busIntrospector.getInterfacesExposedOnBusUnderSpecifiedPathBasedOnName(path, ContainerSecured.IFNAME);
			assertFalse(String.format("No object implementing org.alljoyn.ControlPanel.Container nor org.alljoyn.ControlPanel.SecuredContainer is under path %s", path),
					containerInterfaceDetailList.isEmpty());
		}
	}
	
	private void assertContainerOrDialogObjectExists(String path) throws BusException, IOException, ParserConfigurationException, SAXException
	{
		List<InterfaceDetail> containerInterfaceDetailList = busIntrospector.getInterfacesExposedOnBusUnderSpecifiedPathBasedOnName(path, Container.IFNAME);
		List<InterfaceDetail> securedContainerInterfaceDetailList = busIntrospector.getInterfacesExposedOnBusUnderSpecifiedPathBasedOnName(path, ContainerSecured.IFNAME);
		List<InterfaceDetail> dialogInterfaceDetailList = busIntrospector.getInterfacesExposedOnBusUnderSpecifiedPathBasedOnName(path, AlertDialog.IFNAME);
		List<InterfaceDetail> securedDialogInterfaceDetailList = busIntrospector.getInterfacesExposedOnBusUnderSpecifiedPathBasedOnName(path, AlertDialogSecured.IFNAME);

		assertTrue(String.format("No object implementing org.alljoyn.ControlPanel.Container nor org.alljoyn.ControlPanel.SecuredContainer nor "
				+ "org.alljoyn.ControlPanel.Dialog nor org.alljoyn.ControlPanel.SecuredDialog is found under path %s", path),
				isRootContainerPresent(containerInterfaceDetailList, securedContainerInterfaceDetailList, dialogInterfaceDetailList, securedDialogInterfaceDetailList));
	}
	

	private boolean isRootContainerPresent(List<InterfaceDetail> containerInterfaceDetailList, List<InterfaceDetail> securedContainerInterfaceDetailList,
			List<InterfaceDetail> dialogInterfaceDetailList, List<InterfaceDetail> securedDialogInterfaceDetailList)
	{
		return !(containerInterfaceDetailList.isEmpty() && securedContainerInterfaceDetailList.isEmpty() && dialogInterfaceDetailList.isEmpty() && securedDialogInterfaceDetailList
				.isEmpty());
	}
	
	private void validateContainerParameters(Map<Short, Variant> parameters) throws AnnotationBusException, BusException
	{
		validateOptionalParameter0(parameters);
		validateOptionalParameter1(parameters);
		validateContainerParameterLayoutHints(parameters);
	}
	
	private void validatePropertyControlParameters(Map<Short, Variant> parameters, Variant propertyValue) throws BusException
	{
		validateOptionalParameter0(parameters);
		validateOptionalParameter1(parameters);
		validatePropertyControlParameterLayoutHints(parameters, propertyValue);
		validatePropertyControlOptionalParameter3(parameters);
		validatePropertyControlOptionalParameter4(parameters, propertyValue);
		validatePropertyControlOptionalParameter5(parameters, propertyValue);
	}
	
	private void validateLabelPropertyParameters(Map<Short, Variant> parameters) throws BusException
	{
		validateOptionalParameter1(parameters);
		validateParameterLayoutHints(parameters);
	}
	
	private void validateActionParameters(Map<Short, Variant> parameters) throws BusException
	{
		validateOptionalParameter0(parameters);
		validateOptionalParameter1(parameters);
		validateParameterLayoutHints(parameters);
	}
	
	private void validateDialogParameters(Map<Short, Variant> parameters, short numberOfActions) throws BusException
	{
		validateOptionalParameter0(parameters);
		validateOptionalParameter1(parameters);
		validateParameterLayoutHints(parameters);
		validateDialogParameterActionLabelIds(parameters, numberOfActions);
	}
	
	private void validateListPropertyParameters(Map<Short, Variant> parameters) throws BusException
	{
		validateOptionalParameter0(parameters);
		validateOptionalParameter1(parameters);
		validateParameterLayoutHints(parameters);
	}
	
	private void validateOptionalParameter0(Map<Short, Variant> parameters) throws AnnotationBusException
	{
		if (parameters.containsKey((short) 0))
		{
			assertEquals("Signature does not match for key 0", "s", parameters.get((short) 0).getSignature());
		}
	}
	
	private void validateOptionalParameter1(Map<Short, Variant> parameters) throws AnnotationBusException
	{
		if (parameters.containsKey((short) 1))
		{
			assertEquals("Signature does not match for key 1", "u", parameters.get((short) 1).getSignature());
		}
	}
	
	private void validatePropertyControlOptionalParameter3(Map<Short, Variant> parameters) throws AnnotationBusException
	{
		if (parameters.containsKey((short) 3))
		{
			assertEquals("Signature does not match for key 3", "s", parameters.get((short) 3).getSignature());
		}
	}

	private void validatePropertyControlOptionalParameter4(Map<Short, Variant> parameters, Variant propertyValue) throws AnnotationBusException, BusException
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

	private void validatePropertyControlOptionalParameter5(Map<Short, Variant> parameters, Variant propertyValue) throws AnnotationBusException, BusException
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
	
	private void validateContainerParameterLayoutHints(Map<Short, Variant> parameters) throws BusException
	{
		if (parameters.containsKey((short) 2))
		{
			Variant layoutHintsVariant = parameters.get((short) 2);
			validateLayoutHintsSignature(layoutHintsVariant);
			short[] layoutHints = layoutHintsVariant.getObject(short[].class);
			logger.info(String.format("LayoutHints: %s", Arrays.toString(layoutHints)));
			assertTrue("Key 2 contains no value", layoutHints.length > 0);

			for (short layoutHint : layoutHints)
			{
				assertTrue(String.format("%d does not match expected value of 1 or 2 for key 2", layoutHint), layoutHint == 1 || layoutHint == 2);
			}
		}
	}
	
	private void validatePropertyControlParameterLayoutHints(Map<Short, Variant> parameters, Variant propertyValue) throws BusException
	{
		if (parameters.containsKey((short) 2))
		{
			Variant layoutHintsVariant = parameters.get((short) 2);
			validateLayoutHintsSignature(layoutHintsVariant);
			short[] layoutHints = layoutHintsVariant.getObject(short[].class);
			logger.info(String.format("LayoutHints: %s", Arrays.toString(layoutHints)));
			assertEquals("Key 2 contains more than one value", 1, layoutHints.length);
			short layoutHintId = layoutHints[0];
			assertTrue(String.format("%d is not a valid value for key 2", layoutHintId), VALID_PROPERTY_CONTROL_LAYOUT_HINTS_VALUES.contains(layoutHintId));

			validateBasedOnLayoutHintId(parameters, propertyValue, layoutHintId);
		}
	}
	
	private void validateBasedOnLayoutHintId(Map<Short, Variant> parameters, Variant propertyValue, short layoutHintId) throws AnnotationBusException, BusException
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
	
	private void assertOptionalParameter4IsPresent(Map<Short, Variant> parameters, short layoutHintId) throws AnnotationBusException, BusException
	{
		assertTrue(String.format("Parameters should contain key 4 when hint id is %d", layoutHintId), parameters.containsKey((short) 4));
		assertEquals("Signature does not match for key 4", "a(vs)", parameters.get((short) 4).getSignature());
		PropertyWidgetConstrainToValuesAJ[] propertyWidgetConstrainToValuesAJArray = parameters.get((short) 4).getObject(PropertyWidgetConstrainToValuesAJ[].class);
		assertNotNull("Values array for key 4 cannot be null", propertyWidgetConstrainToValuesAJArray);
		assertTrue("Values array for key 4 cannot be empty", propertyWidgetConstrainToValuesAJArray.length >= 1);
	}
	
	private void validateDateTimeHint(short layoutHintId, Variant propertyValue, int compositeType) throws AnnotationBusException, BusException
	{
		assertEquals(String.format("Signature does not match for property Value when hint id is %d", layoutHintId), "q(qqq)", propertyValue.getSignature());
		PropertyWidgetThreeShortAJ propertyWidgetThreeShortAJ = propertyValue.getObject(PropertyWidgetThreeShortAJ.class);
		assertEquals(String.format("The first value in the composite type does not match when hint id is %d", layoutHintId), compositeType, propertyWidgetThreeShortAJ.dataType);
	}
	
	private void validateParameterLayoutHints(Map<Short, Variant> parameters) throws BusException
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
	
	private void validateLayoutHintsSignature(Variant layoutHintsVariant) throws AnnotationBusException
	{
		assertNotNull("Key 2 for LayoutHints is missing", layoutHintsVariant);
		assertEquals("Signature does not match for key 2", "aq", layoutHintsVariant.getSignature());
	}

	private boolean isValidState(int state, List<Integer> validStates) throws BusException
	{
		return validStates.contains(state);
	}
	
	private void validateListPropertyValue(ListPropertyControlSuper listPropertyControl) throws BusException
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
	
	private boolean isEmpty(List<InterfaceDetail> securedContainerInterfaceDetailListExposedOnBus, List<InterfaceDetail> securedPropertyInterfaceDetailListExposedOnBus,
			List<InterfaceDetail> securedActionInterfaceDetailListExposedOnBus, List<InterfaceDetail> securedDialogInterfaceDetailListExposedOnBus,
			List<InterfaceDetail> securedListPropertyInterfaceDetailListExposedOnBus)
	{
		return securedContainerInterfaceDetailListExposedOnBus.isEmpty() && securedPropertyInterfaceDetailListExposedOnBus.isEmpty()
				&& securedActionInterfaceDetailListExposedOnBus.isEmpty() && securedDialogInterfaceDetailListExposedOnBus.isEmpty()
				&& securedListPropertyInterfaceDetailListExposedOnBus.isEmpty();
	}
	
	private void validateRootUrl(String rootUrl) throws IOException
	{
		assertNotNull("Root URL returned is null", rootUrl);
		assertTrue(String.format("%s is not a valid URL", rootUrl), isValidUrl(rootUrl));
		HttpGet httpGet = new HttpGet(rootUrl);
		HttpResponse httpResponse = getHttpClient().execute(httpGet);
		assertNotNull("Root URL is not responding",httpResponse.getStatusLine());
	}
	
	private void validateDialogParameterActionLabelIds(Map<Short, Variant> parameters, short numberOfActions) throws AnnotationBusException
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
	
	private void validateDialogParameterActionLabelId(Variant actionLabelIdVariant, String key) throws AnnotationBusException
	{
		assertNotNull(String.format("Key %s is missing", key), actionLabelIdVariant);
		assertEquals(String.format("Signature does not match for key %s", key), "s", actionLabelIdVariant.getSignature());
	}
	
	private void validateDialogActions(AlertDialogSuper dialog, short numberOfActions) throws BusException
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
	
	private void validateInvokingDialogAction2ThrowsException(AlertDialogSuper dialog) throws BusException
	{
		try
		{
			dialog.Action2();
			fail("Invoking Action2() must throw exception");
		}
		catch (ErrorReplyBusException errorReplyBusException)
		{
			//assertTrue(true);
			assertErrorIsMethodNotAllowed(errorReplyBusException);
		}
	}
	
	private void validateInvokingDialogAction3ThrowsException(AlertDialogSuper dialog) throws BusException
	{
		try
		{
			dialog.Action3();
			fail("Invoking Action3() must throw exception");
		}
		catch (ErrorReplyBusException errorReplyBusException)
		{
			//assertTrue(true);
			assertErrorIsMethodNotAllowed(errorReplyBusException);
		}
	}
	
	private void invalidPasswordAccessFailureToThrowException(String interfaceName)
	{
		fail("Exception should be thrown on connecting with the wrong password to retrieve " + interfaceName + " version!");
	}
	
	private void handleBusExceptionOnInvalidPasswordBusObjectAccess(BusException busException)
	{
		logger.info("Exception thrown on setting WrongPasscode", busException);
		boolean isPeerAuthenticatedAttempted = serviceHelper.isPeerAuthenticationAttempted(deviceAboutAnnouncement);
		boolean isPeerAuthenticationSuccessful = serviceHelper.isPeerAuthenticationSuccessful(deviceAboutAnnouncement);
		assertTrue("Authentication should have failed", isPeerAuthenticatedAttempted && !isPeerAuthenticationSuccessful);
	}

	private void setInvalidPassword()
	{
		serviceHelper.clearPeerAuthenticationFlags(deviceAboutAnnouncement);
		serviceHelper.setAuthPassword(deviceAboutAnnouncement, "123456".toCharArray());
	}
	
	/** 
	 * [AT4] Added methods to emulate JUnit behaviour
	 * 
	 * assertEquals
	 * assertTrue
	 * assertFalse
	 * assertNotNull
	 * assertErrorIsMethodNotAllowed
	 * 
	 * */
	
	private void assertEquals(String errorMessage, int first, int second)
	{
		if (first != second) {
			/*logger.error(msg);
			pass=false;*/
			fail(errorMessage);
		}
	}

	private void assertEquals(String errorMessage, String first, String second)
	{
		if (!first.equals(second)) {
			/*pass=false;
			logger.error(msg);*/
			fail(errorMessage);
		}
	}
	
	private void assertEquals(String errorMessage, short first, short second)
	{
		if (first !=second) {
			/*logger.error(msg);
			pass=false;*/
			fail(errorMessage);
		}
	}
	
	private void assertTrue(String errorMessage, boolean condition)
	{
		if (!condition) {
			/*logger.error(msg);
			pass=false;*/
			fail(errorMessage);
		}
	}

	private void assertFalse(String errorMessage, boolean condition)
	{
		if (condition) {
			/*logger.error(msg);
			pass=false;*/
			fail(errorMessage);
		}

	}
	
	private void assertNotNull(String errorMessage, Object object)
	{
		if (object == null) {
			/*logger.error(msg);
			pass=false;*/
			fail(errorMessage);
		}
	}

	private void assertErrorIsMethodNotAllowed(ErrorReplyBusException errorReplyBusException)
	{
		logger.info("Expected exception caught while involing Action()", errorReplyBusException);
		assertEquals("Error name does not match", AllJoynErrorReplyCodes.METHOD_NOT_ALLOWED, errorReplyBusException.getErrorName());
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
		pass = false;
	}

	public String getFinalVerdict()
	{
		if (inconc) {
			return "INCONC";
		}
		
		if (pass) {
			return "PASS";
		} else {
			return "FAIL";
		}
	}

}