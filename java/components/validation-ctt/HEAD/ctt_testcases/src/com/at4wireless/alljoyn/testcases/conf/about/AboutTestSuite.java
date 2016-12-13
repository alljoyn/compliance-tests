/*******************************************************************************
 *  Copyright (c) 2016 Open Connectivity Foundation (OCF) and AllJoyn Open
 *     Source Project (AJOSP) Contributors and others.
 *
 *     SPDX-License-Identifier: Apache-2.0
 *
 *     All rights reserved. This program and the accompanying materials are
 *     made available under the terms of the Apache License, Version 2.0
 *     which accompanies this distribution, and is available at
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Copyright 2016 Open Connectivity Foundation and Contributors to
 *     AllSeen Alliance. All rights reserved.
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
package com.at4wireless.alljoyn.testcases.conf.about;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.xml.parsers.ParserConfigurationException;

import org.alljoyn.bus.AboutIconProxy;
/*import org.alljoyn.about.AboutKeys;
import org.alljoyn.about.transport.AboutTransport;*/
import org.alljoyn.bus.AboutKeys;
import org.alljoyn.bus.AboutObjectDescription;
import org.alljoyn.bus.AboutProxy;
import org.alljoyn.bus.AnnotationBusException;
import org.alljoyn.bus.BusAttachment;
import org.alljoyn.bus.BusException;
import org.alljoyn.bus.ErrorReplyBusException;
import org.alljoyn.bus.ProxyBusObject;
import org.alljoyn.bus.Variant;
import org.alljoyn.bus.annotation.BusInterface;
import org.alljoyn.bus.ifaces.About;
import org.alljoyn.bus.ifaces.AllSeenIntrospectable;
import org.alljoyn.bus.ifaces.Icon;
import org.xml.sax.SAXException;

import com.at4wireless.alljoyn.core.about.AboutAnnouncementDetails;
import com.at4wireless.alljoyn.core.about.EvAcIntrospectionNode;
import com.at4wireless.alljoyn.core.audio.MediaType;
import com.at4wireless.alljoyn.core.commons.CommonUtils;
import com.at4wireless.alljoyn.core.commons.ServiceHelper;
import com.at4wireless.alljoyn.core.commons.log.Logger;
import com.at4wireless.alljoyn.core.commons.log.WindowsLoggerImpl;
import com.at4wireless.alljoyn.core.interfacevalidator.InterfaceValidator;
import com.at4wireless.alljoyn.core.interfacevalidator.ValidationResult;
import com.at4wireless.alljoyn.core.introspection.BusIntrospector;
import com.at4wireless.alljoyn.core.introspection.bean.InterfaceDetail;
import com.at4wireless.alljoyn.core.introspection.bean.IntrospectionInterface;
import com.at4wireless.alljoyn.core.introspection.bean.IntrospectionNode;
import com.at4wireless.alljoyn.core.introspection.bean.NodeDetail;
import com.at4wireless.alljoyn.testcases.parameter.GeneralParameter;
import com.at4wireless.alljoyn.testcases.parameter.Ics;
import com.at4wireless.alljoyn.testcases.parameter.Ixit;

/**
 * Manages execution of Core Test Cases
 */
public class AboutTestSuite
{ 
	private static final String INTROSPECTABLE_INTERFACE_NAME = "org.allseen.Introspectable";
	private static final Logger logger = new WindowsLoggerImpl(AboutTestSuite.class.getSimpleName());
	private static final String BUS_APPLICATION_NAME = "AboutTestSuite";
	private static final String ERROR_MSG_BUS_INTROSPECTION = "Encountered exception while trying to introspect the bus";
	private static final String DBUS_ERROR_SERVICE_UNKNOWN = "org.freedesktop.DBus.Error.ServiceUnknown";
	private String defaultLanguage;
	private AboutProxy aboutProxy;
	private AboutIconProxy aboutIconProxy;
	protected AboutAnnouncementDetails deviceAboutAnnouncement;
	private ServiceHelper serviceHelper;
	private SimpleDateFormat simpleDateFormat;
	private final String DATE_FORMAT = "yyyy-MM-dd";

	private UUID dutAppId;
	private String dutDeviceId;
	
	/**
     * This regular expression is used to replace description tags with the
     * INTROSPECTION_XML_DESC_PLACEHOLDER
     */
	private static final String INTROSPECTION_XML_DESC_REGEX = "<description.*?>.*?</description>";
	
	/**
     * This placeholder is used to change the description tags in the
     * introspected XML
     */
    private static final String INTROSPECTION_XML_DESC_PLACEHOLDER = "<description></description>";
	
	/**
     * The expected result after the introspection XML will be modified as a
     * result of applying the
     * {@link EventsActionsTestSuite#INTROSPECTION_XML_DESC_REGEX}
     */
    private static final String INTROSPECTION_XML_DESC_EXPECTED = "<description></description>";
    
    /**
     * Announcement time out
     */
    private static long ANNOUNCEMENT_TIMEOUT_IN_SECONDS = 60;
	
	/** 
	 * [AT4] Added attributes to perform the test cases
	 * 
	 * pass	stores the final verdict of the test case
	 * ics	map that stores ICS values	
	 * ixit	map that stores IXIT values
	 * 
	 * */
	private boolean pass = true;
	private boolean inconcluse = false;
	private Ics icsList;
	private Ixit ixitList;

	public AboutTestSuite(String testCase, Ics icsList, Ixit ixitList, GeneralParameter gpList)
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
		catch (Exception e)
		{
			inconcluse = true;
		}
	}

	public void runTestCase(String test) throws Exception
	{
		setUp();
		
		try
		{
			logger.info("Running testcase: %s", test);
			
			if (test.equals("About-v1-01"))
			{
				testAbout_v1_01_AboutAnnouncement();
			}
			else if (test.equals("About-v1-02"))
			{
				testAbout_v1_02_AboutVersion();
			}
			else if (test.equals("About-v1-03"))
			{
				testAbout_v1_03_GetObjectDescription();
			}
			else if (test.equals("About-v1-04"))
			{
				testAbout_v1_04_AboutAnnouncementConsistentWithBusObjects();
			}
			else if (test.equals("About-v1-05"))
			{
				testAbout_v1_05_StandardizedInterfacesMatchDefinitions();
			}
			else if (test.equals("About-v1-06"))
			{
				testAbout_v1_06_GetAboutForDefaultLanguage();
			}
			else if (test.equals("About-v1-07"))
			{
				testAbout_v1_07_GetAboutForSupportedLanguages();
			}
			else if (test.equals("About-v1-08"))
			{
				testAbout_v1_08_GetAboutForUnspecifiedLanguage();
			}
			else if (test.equals("About-v1-09"))
			{
				testAbout_v1_09_GetAboutForUnsupportedLanguage();
			}
			else if (test.equals("About-v1-10"))
			{
				testAbout_v1_10_GetAboutIcon();
			}
			else if (test.equals("About-v1-11"))
			{
				testAbout_v1_11_GetAboutIconValidUrl();
			}
			else if (test.equals("EventsActions-v1-01"))
			{
				testEventsActions_v1_01();
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

	protected void setUp() throws Exception
	{
		logger.raw("====================================================");
		logger.info("test setUp started");
		
		try 
		{
			dutDeviceId = ixitList.IXITCO_DeviceId;
			logger.info("Running About test case against Device ID: %s", dutDeviceId);
			dutAppId = ixitList.IXITCO_AppId;
			logger.info("Running About test case against App ID: %s", dutAppId);
			
			serviceHelper = getServiceHelper();
			serviceHelper.initialize(BUS_APPLICATION_NAME, dutDeviceId, dutAppId);

			deviceAboutAnnouncement = serviceHelper.waitForNextDeviceAnnouncement(ANNOUNCEMENT_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS);
			
			if (deviceAboutAnnouncement == null)
			{
				throw new Exception("Timed out waiting for About announcement");
			}	
			
			logger.info("Partial Verdict: PASS");
			
			defaultLanguage = ixitList.IXITCO_DefaultLanguage;
			
			logger.info("test setUp done");
		}
		catch (Exception exception)
		{
			logger.error("Exception setting up resources: %s", exception.getMessage()); //[AT4]
			
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
		
		logger.raw("====================================================");
	}

	protected void tearDown() throws Exception
	{
		logger.raw("====================================================");
		logger.info("test tearDown started");
		
		releaseResources();
		
		logger.info("test tearDown done");
		logger.raw("====================================================");
	}

	public void testAbout_v1_01_AboutAnnouncement() throws Exception
	{	
		logger.info("Testing if version matches IXITCO_AboutVersion");

		if (ixitList.IXITCO_AboutVersion != deviceAboutAnnouncement.getVersion())
		{
			fail(String.format("About version does not match: %s is not equal to %s", deviceAboutAnnouncement.getVersion(), 
					ixitList.IXITCO_AboutVersion));
		}
		else
		{
			logger.info("About version matches IXITCO_AboutVersion: %s", ixitList.IXITCO_AboutVersion);
			logger.info("Partial Verdict: PASS");
		} //[AT4] Checks if IXIT interface version is equal to device interface version

		validatePathIfAboutInterfacePresentInAnnouncement(); //[AT4]
		verifyAboutData(deviceAboutAnnouncement.getAboutData());
		
		aboutProxy = serviceHelper.connectAboutProxy(deviceAboutAnnouncement); //[AT4] Establishes session after About validation
	}

	public void testAbout_v1_02_AboutVersion() throws Exception
	{
		aboutProxy = serviceHelper.connectAboutProxy(deviceAboutAnnouncement);

		logger.info("Verifying that the Version property retrieved from the application's About bus object matches the version parameter in its About announcement");

		if (aboutProxy.getVersion() != deviceAboutAnnouncement.getVersion())
		{
			fail(String.format("About version does not match: %s is not equal to %s", deviceAboutAnnouncement.getVersion(), aboutProxy.getVersion()));
		}
		else
		{
			logger.info("About version parameter matches Version property");
			logger.info("Partial Verdict: PASS");
		}
	}

	public void testAbout_v1_03_GetObjectDescription() throws Exception
	{
		aboutProxy = serviceHelper.connectAboutProxy(deviceAboutAnnouncement);

		Map<String, String[]> aboutObjectDescriptionMap = new HashMap<String, String[]>();
		Map<String, String[]> announcementObjectDescriptionMap = new HashMap<String, String[]>();
		
		populateMap(deviceAboutAnnouncement.getObjectDescriptions(), announcementObjectDescriptionMap);
		populateMap(aboutProxy.getObjectDescription(), aboutObjectDescriptionMap);
		
		Set<String> aboutSet = aboutObjectDescriptionMap.keySet();
		Set<String> announceSet = announcementObjectDescriptionMap.keySet();

		logger.debug("About: %s", aboutSet);
		logger.debug("Announce: %s", announceSet);

		if (!aboutSet.equals(announceSet))
		{
			fail("GetObjectDescription does not contain the same set of paths as the announcement");
		}
		else
		{
			logger.info("GetObjectDescription does contain the same set of paths as the announcement");
			logger.info("Partial Verdict: PASS");
		}

		for (String key : aboutObjectDescriptionMap.keySet())
		{
			if (!Arrays.equals(aboutObjectDescriptionMap.get(key), announcementObjectDescriptionMap.get(key)))
			{
				fail(String.format("GetObjectDescription not consistent for %s", key));
			}
			else
			{
				logger.info(String.format("GetObjectDescription is consistent for %s", key));
				logger.info("Partial Verdict: PASS");
			}
		}
	}

	public void testAbout_v1_04_AboutAnnouncementConsistentWithBusObjects() throws Exception
	{
		aboutProxy = serviceHelper.connectAboutProxy(deviceAboutAnnouncement);

		BusIntrospector busIntrospector = getIntrospector();

		Set<String> announcementPathInterfaceSet = new HashSet<String>();
		Set<String> busIntrospectPathInterfaceSet = new HashSet<String>();

		for (AboutObjectDescription busObjectDescription : deviceAboutAnnouncement.getObjectDescriptions())
		{
			String path = busObjectDescription.path;
			populateAnnouncementPathInterfaceSet(announcementPathInterfaceSet, busObjectDescription, path);
			populateBusIntrospectPathInterfaceSet(busIntrospector, busIntrospectPathInterfaceSet, path);
		}

		for (String announcementKey : announcementPathInterfaceSet)
		{
			String[] pathAndInterfaces = announcementKey.split(":");

			if (!pathAndInterfaces[1].equals(INTROSPECTABLE_INTERFACE_NAME))
			{
				if (!busIntrospectPathInterfaceSet.contains(announcementKey))
				{
					fail(String.format("AboutAnnouncement advertises interface %s at path %s, but does not contain such interface at that path",
							pathAndInterfaces[1], pathAndInterfaces[0]));
				}
				else
				{
					logger.info(String.format("Interface %s found at path %s", pathAndInterfaces[1], pathAndInterfaces[0]));
					logger.info("Partial Verdict: PASS");
				}
			}
		}
	}

	public void testAbout_v1_05_StandardizedInterfacesMatchDefinitions() throws Exception
	{
		aboutProxy = serviceHelper.connectAboutProxy(deviceAboutAnnouncement);

		List<InterfaceDetail> standardizedIntrospectionInterfacesExposedOnBus = getIntrospector().getStandardizedInterfacesExposedOnBus();
		for (InterfaceDetail objectDetail : standardizedIntrospectionInterfacesExposedOnBus)
		{
			for (IntrospectionInterface intfName : objectDetail.getIntrospectionInterfaces())
			{
				logger.info(String.format("Found object at %s implementing %s", objectDetail.getPath(), intfName.getName()));
			}
		}
		
		//[ASACOMP-48] start
		ValidationResult validationResult = null;
		try
		{
			validationResult = getInterfaceValidator().validate(standardizedIntrospectionInterfacesExposedOnBus);
		}
		catch (Exception e)
		{
			fail(e.getMessage());
			return;
		}
		//[ASACOMP-48] end

		if (!validationResult.isValid())
		{
			fail(validationResult.getFailureReason());
		}
		else
		{
			logger.info("Partial Verdict: PASS");
		}
	}

	public void testAbout_v1_06_GetAboutForDefaultLanguage() throws Exception
	{
		aboutProxy = serviceHelper.connectAboutProxy(deviceAboutAnnouncement);

		logger.info("Calling getAboutData on About interface with language: %s", defaultLanguage);

		Map<String, Variant> aboutMap = aboutProxy.getAboutData(defaultLanguage);
		
		logger.info("Partial Verdict: PASS");
		logger.info("Checking that all required fields are present");
		
		verifyAboutMap(aboutMap, defaultLanguage);
	}

	public void testAbout_v1_07_GetAboutForSupportedLanguages() throws Exception
	{
		aboutProxy = serviceHelper.connectAboutProxy(deviceAboutAnnouncement);

		logger.info("Calling getAboutData on About interface with language %s", defaultLanguage);

		Map<String, Variant> aboutMapDefaultLanguage = aboutProxy.getAboutData(defaultLanguage);

		logger.info("Partial Verdict: PASS");

		String[] supportedLanguages =  aboutMapDefaultLanguage.get(AboutKeys.ABOUT_SUPPORTED_LANGUAGES).getObject(String[].class);
		validateSupportedLanguagesContainsDefaultLanguage(supportedLanguages);

		if (supportedLanguages.length == 1)
		{
			logger.info("Device only supports one language");
		}
		else
		{
			validateSupportedLanguagesAboutMap(supportedLanguages, aboutMapDefaultLanguage);
		}
	}

	public void testAbout_v1_08_GetAboutForUnspecifiedLanguage() throws Exception
	{
		aboutProxy = serviceHelper.connectAboutProxy(deviceAboutAnnouncement);

		logger.info("Calling getAboutData on About Interface");

		Map<String, Variant> aboutMapDefaultLanguage = aboutProxy.getAboutData(defaultLanguage);
		Map<String, Variant> aboutMapNoLanguage = aboutProxy.getAboutData("");

		logger.info("Partial Verdict: PASS");

		compareFieldsInAboutMap(aboutMapDefaultLanguage, aboutMapNoLanguage, "");
	}

	public void testAbout_v1_09_GetAboutForUnsupportedLanguage() throws Exception
	{
		aboutProxy = serviceHelper.connectAboutProxy(deviceAboutAnnouncement);

		boolean exceptionThrown = false;
		logger.info("Calling getAbout on About interface with an unsupported language INVALID");

		logger.info("Calling getAboutData");

		try
		{
			aboutProxy.getAboutData("INVALID");
		}
		catch (ErrorReplyBusException ex)
		{
			exceptionThrown = true;
			assertEquals("Wrong errorName", "org.alljoyn.Error.LanguageNotSupported", ex.getErrorName());
		}
		
		assertTrue("Calling getAbout on the About interface with an unsupported language must throw an exception", exceptionThrown);
	}

	public void testAbout_v1_10_GetAboutIcon() throws Exception
	{
		if (!icsList.ICSCO_IconInterface)
		{
			fail("Test Case not applicable. ICSCO_IconInterface is set to false");
		}
		else
		{
			if (!deviceAboutAnnouncement.supportsInterface(Icon.INTERFACE_NAME))
			{
				fail("Device does not support AboutIcon");
			}
			else
			{
				aboutProxy =serviceHelper.connectAboutProxy(deviceAboutAnnouncement);
				aboutIconProxy = serviceHelper.connectAboutIconProxy(deviceAboutAnnouncement);

				logger.info("Calling get About Icon retrieve Icon data");
				String mimeType = aboutIconProxy.getMimeType();
				int iconSize = aboutIconProxy.getSize();

				if (mimeType == null || mimeType.isEmpty())
				{
					logger.info("mimetype is empty");
					logger.info("GetContent() method is not supported");
					pass = false;
				}
				else if (iconSize == 0)
				{
					logger.info("The icon size is zero");
					logger.info("GetContent() method is not supported");
					pass = false;
				}
				else
				{
					assertTrue("Mime type should match pattern image/*", mimeType.startsWith(MediaType.ImagePrefix.getValue()));
					assertTrue("Icon size should be less than " + BusAttachment.ALLJOYN_MAX_ARRAY_LEN, iconSize < BusAttachment.ALLJOYN_MAX_ARRAY_LEN);

					byte[] iconContent = aboutIconProxy.getContent();
					assertEquals("Size of GetContent does not match iconSize", iconSize, iconContent.length);

					InputStream in = new ByteArrayInputStream(iconContent); //[AT4] Added code to receive image stream on Windows platform
					BufferedImage iconBitmap = ImageIO.read(in);

					// Bitmap iconBitmap = BitmapFactory.decodeByteArray(iconContent, 0, iconSize); [AT4] Android Implementation
					logger.info("The IconBitMap size: '" + iconSize + "', Icon Height: '" + iconBitmap.getHeight() + "', Icon Width: '" + iconBitmap.getWidth() + "', toString(): "
							+ iconBitmap);
				}
			}
		}
	}

	public void testAbout_v1_11_GetAboutIconValidUrl() throws Exception
	{
		if (!icsList.ICSCO_IconInterface)
		{
			fail("Test Case not applicable. ICSCO_IconInterface is set to false");
		}
		else
		{
			if (!deviceAboutAnnouncement.supportsInterface(Icon.INTERFACE_NAME))
			{
				fail("Device does not support AboutIcon");
			} 
			else
			{
				aboutIconProxy = serviceHelper.connectAboutIconProxy(deviceAboutAnnouncement);
				validateIconUrl();
			}
		}
	}
	
	protected void validateIconUrl() throws BusException
	{
		logger.info("Creating about client and testing the Icon url validity");
		String iconUrl = aboutIconProxy.getUrl();

		if (iconUrl == null || iconUrl.isEmpty())
		{
			logger.info("Url is empty");
		}
		else
		{
			MalformedURLException exception = null;
			try
			{
				new URL(iconUrl);
			}
			catch (MalformedURLException malformedUrlException)
			{
				fail(String.format("Icon URL is malformed: %s", malformedUrlException));
				exception = malformedUrlException;
			}
			
			assertNull(String.format("The received icon URL '%s'isn't valid", iconUrl), exception);
		}
	}
	
	protected BusIntrospector getIntrospector()
	{
		return serviceHelper.getBusIntrospector(deviceAboutAnnouncement);
	}
	
	protected InterfaceValidator getInterfaceValidator() throws IOException, ParserConfigurationException, SAXException
	{
		return new InterfaceValidator();
	}
	
	boolean isValidDate(String date)
	{
		boolean isValid = true;
		
		try
		{
			simpleDateFormat.parse(date);
		}
		catch (ParseException parseException)
		{
			isValid = false;
		}
		
		return isValid;
	}
	
	boolean isValidUrl(String supportUrl)
	{
		boolean isValid = true;
		
		try
		{
			new URL(supportUrl).toURI();
		}
		catch (MalformedURLException malformedURLException)
		{
			isValid = false;
		}
		catch (URISyntaxException e)
		{
			isValid = false;
		}
		
		return isValid;
	}
	
	private void validatePathIfAboutInterfacePresentInAnnouncement()
    {
        String aboutPath = getAboutInterfacePathFromAnnouncement();

        if (aboutPath != null)
        {
            assertEquals("About interface present at the wrong path", About.OBJ_PATH, aboutPath); //[AT4] AboutTransport is deprecated
        }
        else
        {
        	logger.info("About interface not present in announcement");
        }
    }
	
	private void releaseResources()
	{
		disconnectFromAboutProxy();
		disconnectFromAboutIconProxy();
		
		if (serviceHelper != null)
		{
			serviceHelper.release();
			serviceHelper = null;
		}
	}
	
	private void disconnectFromAboutProxy()
	{
		if (aboutProxy != null)
		{
			aboutProxy = null;
		}
	}
	
	private void disconnectFromAboutIconProxy()
	{
		if (aboutIconProxy != null)
		{
			aboutIconProxy = null;
		}
	}
	
	private String getAboutInterfacePathFromAnnouncement()
	{
		String aboutPath = null;

		for (AboutObjectDescription busObjectDescription : deviceAboutAnnouncement.getObjectDescriptions())
		{
			for (String interfaceName : busObjectDescription.interfaces)
			{
				if (interfaceName.equals(About.INTERFACE_NAME))
				{
					aboutPath = busObjectDescription.path;
				}
			}
			logger.info("BusObjectDescription: %s supports %s", busObjectDescription.path, Arrays.toString(busObjectDescription.interfaces));
		}
		return aboutPath;
	}
	
	private void validateSupportedLanguagesContainsDefaultLanguage(String[] supportedLanguages)
	{
		assertTrue("No supported language found in About announcement", supportedLanguages != null);
		assertTrue("No supported language found in About announcement", supportedLanguages.length > 0);
		assertTrue("Default language not found in supported languages list of About announcement", isDefaultLanguagePresent(supportedLanguages));
	}
	
	private boolean isDefaultLanguagePresent(String[] supportedLanguages)
	{
		boolean isPresent = false;
		
		for (String supportedLanguage : supportedLanguages)
		{
			if (defaultLanguage.equals(supportedLanguage))
			{
				isPresent = true;
				break;
			}
		}
		return isPresent;
	}
	
	private void validateSupportedLanguagesAboutMap(String[] supportedLanguages, Map<String, Variant> aboutMapDefaultLanguage) throws BusException, Exception
	{
		for (String supportedLanguage : supportedLanguages)
		{
			if (!supportedLanguage.equals(defaultLanguage))
			{
				logger.info("Calling getAbout on About interface with language ", supportedLanguage);
				aboutProxy = serviceHelper.connectAboutProxy(deviceAboutAnnouncement);
				Map<String, Variant> aboutMapSupportedLanguage = null;

				try
				{
					aboutMapSupportedLanguage = aboutProxy.getAboutData(supportedLanguage);
				}
				catch (BusException e)
				{
					fail(String.format("Unexpected BusException: %s", e.toString()));
				}
				
				verifyAboutMap(aboutMapSupportedLanguage, supportedLanguage);
				compareNonLocalizedFieldsInAboutMap(aboutMapDefaultLanguage, aboutMapSupportedLanguage, supportedLanguage);
			}
		}
	}
	
	private void populateMap(AboutObjectDescription[] aboutObjectDescriptions, Map<String, String[]> objectDescriptionMap)
	{
		for (AboutObjectDescription busObjectDescription : aboutObjectDescriptions)
		{
			objectDescriptionMap.put(busObjectDescription.path, busObjectDescription.interfaces);
		}
	}
	
	private void verifyAboutData(Map<String, Variant> aboutData) throws Exception
	{
		verifyFieldIsPresent(AboutKeys.ABOUT_APP_ID, aboutData);
		
		verifyFieldIsPresent(AboutKeys.ABOUT_DEFAULT_LANGUAGE, aboutData);
		compareAbout(AboutKeys.ABOUT_DEFAULT_LANGUAGE, defaultLanguage,
				aboutData.get(AboutKeys.ABOUT_DEFAULT_LANGUAGE).getObject(String.class), "");

		if (icsList.ICSCO_DeviceName)
		{
			verifyFieldIsPresent(AboutKeys.ABOUT_DEVICE_NAME, aboutData);
			compareAbout(AboutKeys.ABOUT_DEVICE_NAME, ixitList.IXITCO_DeviceName, aboutData.get(AboutKeys.ABOUT_DEVICE_NAME).getObject(String.class), "");
		} //[AT4]: Need to add case ICSCO_DeviceName is false and field is present

		verifyFieldIsPresent(AboutKeys.ABOUT_DEVICE_ID, aboutData);
		compareAbout(AboutKeys.ABOUT_DEVICE_ID, ixitList.IXITCO_DeviceId, (String) aboutData.get(AboutKeys.ABOUT_DEVICE_ID).getObject(String.class), "");

		verifyFieldIsPresent(AboutKeys.ABOUT_APP_NAME, aboutData);
		compareAbout(AboutKeys.ABOUT_APP_NAME, ixitList.IXITCO_AppName, aboutData.get(AboutKeys.ABOUT_APP_NAME).getObject(String.class), "");

		verifyFieldIsPresent(AboutKeys.ABOUT_MANUFACTURER, aboutData);
		compareAbout(AboutKeys.ABOUT_MANUFACTURER, ixitList.IXITCO_Manufacturer, aboutData.get(AboutKeys.ABOUT_MANUFACTURER).getObject(String.class), "");

		verifyFieldIsPresent(AboutKeys.ABOUT_MODEL_NUMBER, aboutData);
		compareAbout(AboutKeys.ABOUT_MODEL_NUMBER, ixitList.IXITCO_ModelNumber, aboutData.get(AboutKeys.ABOUT_MODEL_NUMBER).getObject(String.class), "");
	}
	
	private void verifyFieldIsPresent(String key, Map<String, Variant> aboutData) throws BusException
	{
		if (aboutData.get(key) == null)
		{
			fail(String.format("%s is a required field", key));
		}
		else
		{
			if (key.equals(AboutKeys.ABOUT_APP_ID))
			{
				logger.info("%s is present and matches IXIT %s", key, ixitList.IXITCO_AppId);
				logger.info("Partial Verdict: PASS");
			}
			else
			{	
				logger.info("%s is present and is %s", key, aboutData.get(key).getObject(String.class));
				logger.info("Partial Verdict: PASS");
			}
		}
	}
	
	private void verifyAboutMap(Map<String, Variant> aboutMap, String language) throws Exception
	{
		checkForNull(aboutMap, language);
		validateSignature(aboutMap, language);

		if (icsList.ICSCO_DateOfManufacture) //[AT4] Added ICS checking
		{
			validateDateOfManufacture(aboutMap, language);
		}
		
		if (icsList.ICSCO_SupportUrl) //[AT4] Added ICS checking
		{
			validateSupportUrl(aboutMap, language);
		}
	}
	
	private void checkForNull(Map<String, Variant> aboutMap, String language)
	{
		checkForNull(aboutMap, AboutKeys.ABOUT_APP_ID, language);
		checkForNull(aboutMap, AboutKeys.ABOUT_DEFAULT_LANGUAGE, language);
		checkForNull(aboutMap, AboutKeys.ABOUT_DEVICE_ID, language);
		checkForNull(aboutMap, AboutKeys.ABOUT_APP_NAME, language);
		checkForNull(aboutMap, AboutKeys.ABOUT_MANUFACTURER, language);
		checkForNull(aboutMap, AboutKeys.ABOUT_MODEL_NUMBER, language);
		checkForNull(aboutMap, AboutKeys.ABOUT_SUPPORTED_LANGUAGES, language);
		checkForNull(aboutMap, AboutKeys.ABOUT_DESCRIPTION, language);
		checkForNull(aboutMap, AboutKeys.ABOUT_SOFTWARE_VERSION, language);
		checkForNull(aboutMap, AboutKeys.ABOUT_AJ_SOFTWARE_VERSION, language);
		if (icsList.ICSCO_DateOfManufacture) checkForNull(aboutMap, AboutKeys.ABOUT_DATE_OF_MANUFACTURE, language); //[AT4]
		if (icsList.ICSCO_HardwareVersion) checkForNull(aboutMap, AboutKeys.ABOUT_HARDWARE_VERSION, language); //[AT4]
		if (icsList.ICSCO_SupportUrl) checkForNull(aboutMap, AboutKeys.ABOUT_SUPPORT_URL, language); //[AT4]
	}
	
	private void checkForNull(Map<String, Variant> aboutMap, String fieldName, String language)
	{
		if (aboutMap.get(fieldName) == null)
		{
			fail(String.format("%s is a required field for language %s", fieldName, language));
		}
		else
		{
			logger.info("%s is present", fieldName);
			
			if (language == defaultLanguage)
			{
				try
				{
					if (fieldName == AboutKeys.ABOUT_SUPPORTED_LANGUAGES)
					{
						//TODO supported languages checking
					}
					else
					{
						logger.info("Checking if received %s is equal to IXITCO_%s", fieldName, fieldName);
						
						if (fieldName == AboutKeys.ABOUT_APP_ID)
						{
							compareAbout(fieldName, ((UUID) ixitList.get("IXITCO_" + fieldName)).toString(), 
									CommonUtils.getUuidFromByteArray(((byte[]) aboutMap.get(fieldName).getObject(byte[].class))).toString(), "");
						}
						else
						{
							compareAbout(fieldName, (String) ixitList.get("IXITCO_" + fieldName), (String) aboutMap.get(fieldName).getObject(String.class), "");
						}
					}
				}
				catch (BusException e)
				{
					logger.error(e.getMessage());
				}
				catch (Exception e)
				{
					logger.error(e.getMessage());
				}
			}
		}
	}
	
	private void validateSignature(Map<String, Variant> aboutMap, String language) throws AnnotationBusException
	{
		Map<String, Variant> aboutVariantMap = aboutMap;
		validateSignature(aboutVariantMap, AboutKeys.ABOUT_APP_ID, "ay", language);
		validateSignature(aboutVariantMap, AboutKeys.ABOUT_DEFAULT_LANGUAGE, "s", language);
		validateSignature(aboutVariantMap, AboutKeys.ABOUT_DEVICE_ID, "s", language);
		validateSignature(aboutVariantMap, AboutKeys.ABOUT_APP_NAME, "s", language);
		validateSignature(aboutVariantMap, AboutKeys.ABOUT_MANUFACTURER, "s", language);
		validateSignature(aboutVariantMap, AboutKeys.ABOUT_MODEL_NUMBER, "s", language);
		validateSignature(aboutVariantMap, AboutKeys.ABOUT_SUPPORTED_LANGUAGES, "as", language);
		validateSignature(aboutVariantMap, AboutKeys.ABOUT_DESCRIPTION, "s", language);
		validateSignature(aboutVariantMap, AboutKeys.ABOUT_SOFTWARE_VERSION, "s", language);
		validateSignature(aboutVariantMap, AboutKeys.ABOUT_AJ_SOFTWARE_VERSION, "s", language);
		validateSignatureForNonRequiredFields(language, aboutVariantMap);
	}
	
	private void validateSignatureForNonRequiredFields(String language, Map<String, Variant> aboutVariantMap) throws AnnotationBusException
	{
		if (aboutVariantMap.containsKey(AboutKeys.ABOUT_DEVICE_NAME))
		{
			validateSignature(aboutVariantMap, AboutKeys.ABOUT_DEVICE_NAME, "s", language);
		}

		if (aboutVariantMap.containsKey(AboutKeys.ABOUT_DATE_OF_MANUFACTURE))
		{
			validateSignature(aboutVariantMap, AboutKeys.ABOUT_DATE_OF_MANUFACTURE, "s", language);
		}

		if (aboutVariantMap.containsKey(AboutKeys.ABOUT_HARDWARE_VERSION))
		{
			validateSignature(aboutVariantMap, AboutKeys.ABOUT_HARDWARE_VERSION, "s", language);
		}

		if (aboutVariantMap.containsKey(AboutKeys.ABOUT_SUPPORT_URL))
		{
			validateSignature(aboutVariantMap, AboutKeys.ABOUT_SUPPORT_URL, "s", language);
		}
	}
	
	private void validateSignature(Map<String, Variant> aboutVariantMap, String fieldName, String signature, String language) throws AnnotationBusException
	{
		if (!signature.equals(aboutVariantMap.get(fieldName).getSignature()))
		{
			fail(String.format("Signature does not match for required field: %s for language %s", fieldName, language));
		}
		else
		{
			logger.info(String.format("Signature matches for required field %s for language %s", fieldName, language));
			logger.info("Partial Verdict: PASS");
		}
	}
	
	private void validateDateOfManufacture(Map<String, Variant> aboutMap, String language) throws BusException
	{
		if (aboutMap.containsKey(AboutKeys.ABOUT_DATE_OF_MANUFACTURE))
		{
			simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);
			simpleDateFormat.setLenient(false);

			String dateOfManufacture = aboutMap.get(AboutKeys.ABOUT_DATE_OF_MANUFACTURE).getObject(String.class );
			logger.info("Validating Date Of Manufacture: %s", dateOfManufacture);

			if (!isValidDate(dateOfManufacture))
			{
				fail(String.format("DateOfManufacture field value %s does not match expected date pattern YYYY-MM-DD", dateOfManufacture));
			}
		}
	}
	
	private void validateSupportUrl(Map<String, Variant> aboutMap, String language) throws BusException
	{
		if (aboutMap.containsKey(AboutKeys.ABOUT_SUPPORT_URL))
		{
			logger.info("Validating Support Url: %s", aboutMap.get(AboutKeys.ABOUT_SUPPORT_URL).getObject(String.class));

			String supportUrl = aboutMap.get(AboutKeys.ABOUT_SUPPORT_URL).getObject(String.class);

			if (!isValidUrl(supportUrl))
			{
				fail(String.format("%s is not a valid URL", supportUrl));
			}

		}
	}
	
	private void compareFieldsInAboutMap(Map<String, Variant> aboutMapDefaultLanguage, Map<String, Variant> aboutMapNoLanguage, String language) throws Exception
	{
		compareAboutNonRequired(aboutMapDefaultLanguage, aboutMapNoLanguage, language, AboutKeys.ABOUT_DEVICE_NAME);
		compareAbout(AboutKeys.ABOUT_APP_NAME, aboutMapDefaultLanguage.get(AboutKeys.ABOUT_APP_NAME).getObject(String.class), aboutMapNoLanguage.get(AboutKeys.ABOUT_APP_NAME).getObject(String.class), language);
		compareAbout(AboutKeys.ABOUT_MANUFACTURER, aboutMapDefaultLanguage.get(AboutKeys.ABOUT_MANUFACTURER).getObject(String.class), aboutMapNoLanguage.get(AboutKeys.ABOUT_MANUFACTURER).getObject(String.class), language);
		compareAbout(AboutKeys.ABOUT_DESCRIPTION, aboutMapDefaultLanguage.get(AboutKeys.ABOUT_DESCRIPTION).getObject(String.class), aboutMapNoLanguage.get(AboutKeys.ABOUT_DESCRIPTION).getObject(String.class), language);

		compareNonLocalizedFieldsInAboutMap(aboutMapDefaultLanguage, aboutMapNoLanguage, language);
	}
	
	private void compareNonLocalizedFieldsInAboutMap(Map<String, Variant> aboutMapDefaultLanguage, Map<String, Variant> aboutMapSupportedLanguage, String language) throws Exception
	{
		compareRequiredFieldsInAboutMap(aboutMapDefaultLanguage, aboutMapSupportedLanguage, language);
		compareNonRequiredFieldsInAboutMap(aboutMapDefaultLanguage, aboutMapSupportedLanguage, language);
	}
	
	private void compareRequiredFieldsInAboutMap(Map<String, Variant> aboutMapDefaultLanguage, Map<String, Variant> aboutMapSupportedLanguage, String language) throws Exception
	{
		compareAbout(AboutKeys.ABOUT_APP_ID, aboutMapDefaultLanguage.get(AboutKeys.ABOUT_APP_ID).getClass(), aboutMapSupportedLanguage.get(AboutKeys.ABOUT_APP_ID).getClass(), language);
		compareAbout(AboutKeys.ABOUT_DEFAULT_LANGUAGE, aboutMapDefaultLanguage.get(AboutKeys.ABOUT_DEFAULT_LANGUAGE).getClass(),
				aboutMapSupportedLanguage.get(AboutKeys.ABOUT_DEFAULT_LANGUAGE).getClass(), language);
		compareAbout(AboutKeys.ABOUT_DEVICE_ID, aboutMapDefaultLanguage.get(AboutKeys.ABOUT_DEVICE_ID).getClass(), aboutMapSupportedLanguage.get(AboutKeys.ABOUT_DEVICE_ID).getClass(), language);
		compareAbout(AboutKeys.ABOUT_MODEL_NUMBER, aboutMapDefaultLanguage.get(AboutKeys.ABOUT_MODEL_NUMBER).getClass(), aboutMapSupportedLanguage.get(AboutKeys.ABOUT_MODEL_NUMBER).getClass(), language);
		assertTrue("The SupportedLanguages value returned from the GetAbout for language " + language + " does not match default language value",
				Arrays.equals( aboutMapDefaultLanguage.get(AboutKeys.ABOUT_SUPPORTED_LANGUAGES).getObject(String[].class),  aboutMapSupportedLanguage.get(AboutKeys.ABOUT_SUPPORTED_LANGUAGES).getObject(String[].class)));
		compareAbout(AboutKeys.ABOUT_SOFTWARE_VERSION, aboutMapDefaultLanguage.get(AboutKeys.ABOUT_SOFTWARE_VERSION).getClass(),
				aboutMapSupportedLanguage.get(AboutKeys.ABOUT_SOFTWARE_VERSION).getClass(), language);
		compareAbout(AboutKeys.ABOUT_AJ_SOFTWARE_VERSION, aboutMapDefaultLanguage.get(AboutKeys.ABOUT_AJ_SOFTWARE_VERSION).getClass(), aboutMapSupportedLanguage.get(AboutKeys.ABOUT_AJ_SOFTWARE_VERSION)
				.getClass(), language);
	}
	
	private void compareNonRequiredFieldsInAboutMap(Map<String, Variant> aboutMapDefaultLanguage, Map<String, Variant> aboutMapSupportedLanguage, String language) throws Exception
	{
		compareAboutNonRequired(aboutMapDefaultLanguage, aboutMapSupportedLanguage, language, AboutKeys.ABOUT_DATE_OF_MANUFACTURE);
		compareAboutNonRequired(aboutMapDefaultLanguage, aboutMapSupportedLanguage, language, AboutKeys.ABOUT_HARDWARE_VERSION);
		compareAboutNonRequired(aboutMapDefaultLanguage, aboutMapSupportedLanguage, language, AboutKeys.ABOUT_SUPPORT_URL);
	}
	
	private void compareAboutNonRequired(Map<String, Variant> aboutMapDefaultLanguage, Map<String, Variant> aboutMapSupportedLanguage, String language, String fieldName) throws Exception
	{
		logger.info("Comparing %s of default language and language: %s", fieldName, language);
		
		if (aboutMapDefaultLanguage.containsKey(fieldName))
		{
			if (aboutMapSupportedLanguage.containsKey(fieldName))
			{
				compareAbout(fieldName, aboutMapDefaultLanguage.get(fieldName).getObject(String.class), aboutMapSupportedLanguage.get(fieldName).getObject(String.class), language);
			}
			else
			{
				fail(prepareAssertionFailureResponse(fieldName, language));
			}
		}
		else
		{
			assertFalse(prepareAssertionFailureResponse(fieldName, language), aboutMapSupportedLanguage.containsKey(fieldName));
		}
	}
	
	private void compareAbout(String fieldName, String expectedAboutFieldValue, String aboutFieldValue, String language) throws Exception
	{
		String assertionFailureResponse = prepareAssertionFailureResponse(fieldName, expectedAboutFieldValue, aboutFieldValue, language);
		
		if (language.equals(""))
		{
			language = "unspecified language";
		}

		if (expectedAboutFieldValue.equals(aboutFieldValue))
		{
			logger.info("Both fields are equal");
		}
		
		assertEquals(assertionFailureResponse, expectedAboutFieldValue, aboutFieldValue);
	}
	
	private void compareAbout(String fieldName, Class<? extends Variant> class1,
			Class<? extends Variant> class2, String language)
	{
		assertEquals(String.format("%s value returned from the GetAbout for language %s does not match default language value", fieldName, language), class1, class2);
		
		if (class1.equals(class2))
		{
			logger.info("%s is the same for the default language and the localized field", fieldName);
		}
	} //[AT4]
	
	private String prepareAssertionFailureResponse(String fieldName, String language)
	{
		return String.format("%s value returned from the GetAbout for language: '%s' does not match default language value", fieldName, language);
	}

	private String prepareAssertionFailureResponse(String fieldName ,String ixit ,String value,String language)
	{
		return String.format("%s value returned from the About announcement: '%s' does not match IXIT_%s: '%s' for language '%s'", fieldName, value, fieldName, ixit, language);
	} //[AT4]
	
	private void populateBusIntrospectPathInterfaceSet(BusIntrospector busIntrospector, Set<String> busIntrospectPathInterfaceSet, String path) throws Exception
	{
		NodeDetail nodeDetail = null;
		try
		{
			nodeDetail = busIntrospector.introspect(path);
		}
		catch (BusException e)
		{
			handleIntrospectionBusException(path, e);
		}
		catch (Exception ex)
		{
			fail("Encountered exception while trying to parse the introspection xml: "+ex.getMessage());
			throw new Exception("Encountered exception while trying to parse the introspection xml", ex);
		}

		IntrospectionNode introspectionNode = nodeDetail.getIntrospectionNode();

		for (IntrospectionInterface introspectionInterface : introspectionNode.getInterfaces())
		{
			String ifacename = introspectionInterface.getName();
			String key = new StringBuilder(path).append(":").append(ifacename).toString();
			busIntrospectPathInterfaceSet.add(key);
			String message = new StringBuilder("Bus Introspection contains interface ").append(ifacename).append(" at path ").append(path).toString();
			logger.info(message);
		}
	}
	
	private void handleIntrospectionBusException(String path, BusException e) throws Exception
	{
		String msg = ERROR_MSG_BUS_INTROSPECTION;
		if (e instanceof ErrorReplyBusException && DBUS_ERROR_SERVICE_UNKNOWN.equals(((ErrorReplyBusException) e).getErrorName()))
		{
			msg = new StringBuilder("AboutAnnouncement has the path ").append(path).append(", but it is not found on the Bus Introspection.").toString();
		}

		fail(msg);
		throw new Exception(msg, e);
	}
	
	private void populateAnnouncementPathInterfaceSet(Set<String> announcementPathInterfaceSet, AboutObjectDescription aboutObjectDescription, String path)
	{
		for (String ifacename : aboutObjectDescription.interfaces)
		{
			String key = new StringBuilder(path).append(":").append(ifacename).toString();
			announcementPathInterfaceSet.add(key);
			String message = new StringBuilder("AboutAnnouncement contains interface ").append(ifacename).append(" at path ").append(path).toString();
			logger.info(message);
		}
	}
	
	protected ServiceHelper getServiceHelper()
	{
		return new ServiceHelper();
	}
	
    /**
     * Verifies that the object or one of its child objects, implement the
     * {@link AllSeenIntrospectable} interface and has the "description" tag. If
     * an object has a description in multiple languages, the introspection XMLs
     * of each object should be identical.
     */
	public void testEventsActions_v1_01() throws Exception
	{
		aboutProxy = serviceHelper.connectAboutProxy(deviceAboutAnnouncement);
		
		logger.info("Executing the test");
		logger.info("Received announcement from device: '%s' app: '%s', bus '%s'", deviceAboutAnnouncement.getDeviceId(), deviceAboutAnnouncement.getAppId(),
				deviceAboutAnnouncement.getServiceName());

		List<String> objectPaths = getAllSeenIntrospectablObjectPaths();
		assertTrue(String.format("Looks like this object doesn't implement the: '%s interface, even though it states it does in the announcement", getIntrospectableInterfaceName()), 
				objectPaths.size() > 0);

		logger.info("Object paths to be tested: %s", objectPaths);
		
		for (String objectPath : objectPaths)
		{
			logger.info("==> Testing Announced Object Path: %s", objectPath);

			assertTrue(String.format("The test of the Announced Object Path: '%s has failed", objectPath), testObjectValidity(objectPath));
			logger.info("==> The test of the Announced Object Path: %s passed successfully", objectPath);
		}
	}

    /**
     * Runs the test for the received object path
     * 
     * @param objectPath
     * @return whether description was found at any place for this object path
     *         or its sub objects
     */
	private boolean testObjectValidity(String objectPath) throws Exception
	{
		logger.info("Testing Object Path: '%s'", objectPath);
		
		ProxyBusObject proxyObj = serviceHelper.getProxyBusObject(deviceAboutAnnouncement, objectPath,
				new Class<?>[]{AllSeenIntrospectable.class});
		String[] descLangs = getDescriptionLanguages(proxyObj, objectPath);
		
		if (descLangs.length == 0)
		{
			logger.warn("No description languages found for the Object Path: '%s'. Introspecting child objects with NO_LANGUAGE", objectPath);

			String introXML = getIntrospectionXML(proxyObj, objectPath, "NO_LANGUAGE");
			assertNotNull(String.format("Introspection XML is NULL Object Path: '%s'", objectPath), introXML);
			return testChildrenObjectValidity(objectPath, introXML);
		}
		
		return testObjectValidityPerLanguages(proxyObj, objectPath, descLangs);
	}
	
    /**
     * Parses parent's introspection XML and calls
     * {@link EventsActionsTestSuite#testObjectValidity(String)} for each child
     * object.
     * 
     * @param parentObjectPath
     *            Parent object path that was introspected
     * @param parentIntroXML
     *            Parent introspection XML
     * @return TRUE whether at least one of the child objects has a description
     *         tag
     */
	private boolean testChildrenObjectValidity(String parentObjectPath, String parentIntroXML) throws Exception
	{
		EvAcIntrospectionNode introspectNode = null;

		try
		{
			introspectNode = new EvAcIntrospectionNode(parentObjectPath);
			introspectNode.parse(parentIntroXML);
		}
		catch (Exception e)
		{
			fail("Failed to parse the introspection XML, object path: "+ parentObjectPath);
		}

		logger.info("Testing child objects of the parent object: '%s'", parentObjectPath);

		List<EvAcIntrospectionNode> childrenNodes = introspectNode.getChidren();
		boolean descFoundBroth = false;

		if (childrenNodes == null || childrenNodes.size() == 0)
		{
			logger.warn("The object '%s' doesn't have any child object", parentObjectPath);
			return false;
		}

		for (EvAcIntrospectionNode childNode : introspectNode.getChidren())
		{
			boolean descFoundChild = testObjectValidity(childNode.getPath());
			String logMsg = descFoundChild ? "contains a description tag" : "doesn't contain any description tag";

			logger.info("The object or its offspring: '%s' %s", childNode.getPath(), logMsg);
			
			if (!descFoundBroth)
			{
				descFoundBroth = descFoundChild;
			}
		}
		
		String logMsg  = descFoundBroth ? "contain a description tag" : "doesn't contain any description tag";
		logger.info("Child objects of the parent: '%s' %s", parentObjectPath, logMsg);
		return descFoundBroth;
	}

	/**
     * Verifies that for each description language the introspected XML contains
     * a description tag. Verifies that introspection XMLs in different
     * description languages are identical. The verification is performed after
     * the description content is cut by the
     * {@link EventsActionsTestSuite#removeXMLDesc(String)} method. Afterwards
     * the verification algorithm is applied on the child objects by the call to
     * the
     * {@link EventsActionsTestSuite#testChildrenObjectValidity(String, String)}
     * 
     * @param proxyObj
     *            {@link ProxyBusObject}
     * @param parentObjectPath
     *            The object path of the parent object
     * @param descLangs
     *            Description is supported on those languages
     * @return TRUE whether parent XML or one of its child has a description
     *         tag.
     */
	private boolean testObjectValidityPerLanguages(ProxyBusObject proxyObj, String parentObjectPath, String[] descLangs) throws Exception
	{
		logger.info("Found description languages: '%s' for the objectPath: '%s'", Arrays.toString(descLangs), parentObjectPath);

		String firstLangXML      = null;
		String firstLang         = null;
		boolean descriptionFound = false;
		//Pattern xmlDescPresent = Pattern.compile(INTROSPECTION_XML_DESC_EXPECTED, Pattern.DOTALL);

		for (String lang : descLangs)
		{
			String currentXML = getIntrospectionXML(proxyObj, parentObjectPath, lang);
			assertNotNull(String.format("Introspection XML is NULL Object Path: '%s'", parentObjectPath), currentXML);

			currentXML = removeXMLDesc(currentXML);

			logger.info("Testing language validity for the object path: '%s', language: '%s'", parentObjectPath, lang);

			if (firstLangXML == null)
			{
				assertTrue(String.format("The description tag wasn't found in the XML for the description language: '%s', Object path: '%s'",
						lang, parentObjectPath), currentXML.contains(INTROSPECTION_XML_DESC_EXPECTED));

				logger.info("The object '%s' contains a description tag in the language: '%s'", parentObjectPath, lang);

				if (descLangs.length == 1)
				{
					logger.info("The object '%s' supports a single description language: '%s'", parentObjectPath, lang);
					return true;
				}

				firstLang        = lang;
				firstLangXML     = currentXML;
				descriptionFound = true;
				continue;
			}

			logger.info("Test identity of the XMLs in the first language: '%s' and the current language: '%s', Object Path: '%s'", firstLang, lang, parentObjectPath);

            // If current language is not a first language, compare current
            // language XML with the first language XML
			assertEquals(String.format("The XML in the first language: '%s' is not identical to the XML in the current language '%s',"
					+ "object path: '%s", firstLang, lang, parentObjectPath), firstLangXML, currentXML);

			logger.info(String.format("The XMLs in the first language: '%s' and the current language: '%s', Object Path: '%s' are identical",
					firstLang, lang, parentObjectPath));
		}// for :: descLangs

		testChildrenObjectValidity(parentObjectPath, firstLangXML);
		return descriptionFound;
	}
	
	/**
     * Searches in the received announcement object paths that implement the
     * {@link AllSeenIntrospectable} interface
     * 
     * @return Array of the object paths
	 * @throws Exception 
     */
	protected List<String> getAllSeenIntrospectablObjectPaths() throws Exception
	{
		List<String> retList  = new ArrayList<String>();
		String introIfaceName = getIntrospectableInterfaceName();
		
		BusIntrospector busIntrospector = getIntrospector();
		
		AboutObjectDescription[] objDescs = deviceAboutAnnouncement.getObjectDescriptions();

		for (AboutObjectDescription bod : objDescs)
		{
			String path = bod.path;

			for (String iface : bod.interfaces)
			{
				// The AllSeenIntrospectable interface was found => add the path
                // to the returned list
				if (iface.equals(introIfaceName))
				{
					retList.add(path);
				}
			}
		}
		
		//[AT4] starts: if Introspectable is not present on Announcement, check if it is present on Bus
		if (!retList.isEmpty())
		{
			return retList;
		}
		
		for (AboutObjectDescription busObjectDescription : objDescs)
		{
			String path = busObjectDescription.path;
			NodeDetail nodeDetail = null;
			try
			{
				nodeDetail = busIntrospector.introspect(path);
			}
			catch (BusException e)
			{
				handleIntrospectionBusException(path, e);
			}
			catch (Exception ex)
			{
				fail("Encountered exception while trying to parse the introspection xml: " + ex.getMessage());
				throw new Exception("Encountered exception while trying to parse the introspection xml", ex);
			}

			IntrospectionNode introspectionNode = nodeDetail.getIntrospectionNode();

			for (IntrospectionInterface introspectionInterface : introspectionNode.getInterfaces())
			{
				// The AllSeenIntrospectable interface was found => add the path
                // to the returned list
				if (introspectionInterface.getName().equals(introIfaceName))
				{
					retList.add(path);
				}
			}
		}
		
		//[AT4] ends
		
		return retList;
	}
	
	/**
     * @return Returns the AJ name of the {@link AllSeenIntrospectable}
     *         interface
     */
	private String getIntrospectableInterfaceName()
	{
		// Retrieve the AJ name of the introspection interface
		BusInterface ifaceName = AllSeenIntrospectable.class.getAnnotation(BusInterface.class);
		return ifaceName.name();
	}
	
    /**
     * Returns the supported description languages for the given object path
     * 
     * @param proxyObj
     *            {@link ProxyBusObject}
     * @param objectPath
     *            The object to be asked for the description languages
     * @return Array of the description languages
     */
	private String[] getDescriptionLanguages(ProxyBusObject proxyObj, String objectPath)
	{
		String[] langs = new String[]{};

		try
		{
			langs = proxyObj.getInterface(AllSeenIntrospectable.class).GetDescriptionLanguages();
		}
		catch (BusException be)
		{
			fail(String.format("Failed to call GetDescriptionLanguages for the Object Path: %s", objectPath));
		}
		
		return langs;
	}
	
	/**
     * Retrieves the introspection XML
     * 
     * @param proxyObj
     *            The {@link ProxyBusObject}
     * @param objectPath
     *            Object path to be introspected
     * @param language
     *            The language to query the introspection
     * @return Introspection XML
     */
	private String getIntrospectionXML(ProxyBusObject proxyObj, String objectPath, String lang)
	{
		String introXML = null;

		try
		{
			introXML = proxyObj.getInterface(AllSeenIntrospectable.class).IntrospectWithDescription(lang);
		}
		catch (BusException be)
		{
			fail(String.format("Failed to call IntrospectWithDescription for the Object Path: %s", objectPath));
		}
		
		return introXML;
	}

    /**
     * This method removes the content of the XML description tags
     * 
     * @param introspection
     * @return Introspected XML without the description content
     */
	private String removeXMLDesc(String introspection)
	{
		return introspection.replaceAll(INTROSPECTION_XML_DESC_REGEX, INTROSPECTION_XML_DESC_PLACEHOLDER);
	}
	
	/** 
	 * [AT4] Added methods to emulate JUnit behaviour
	 * 
	 * assertEquals
	 * assertTrue
	 * assertFalse
	 * assertNotNull
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

	private void assertEquals(String errorMessage, Class<? extends Variant> first, Class<? extends Variant> second)
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

	private void assertFalse(String errorMessage, boolean condition)
	{
		if (condition)
		{
			fail(errorMessage);
		}
		else
		{
			logger.info("Partial Verdict: PASS");
		}
	}
	
	private void assertNull(String errorMessage, Object object)
	{
		if (object != null)
		{
			fail(errorMessage);
		}
		else
		{
			logger.info("Partial Verdict: PASS");
		}
	}
	
	private void assertNotNull(String errorMessage, Object object)
	{
		if (object == null)
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
		if (inconcluse)
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
}