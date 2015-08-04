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
import com.at4wireless.alljoyn.core.commons.ServiceHelper;
import com.at4wireless.alljoyn.core.commons.log.WindowsLoggerImpl;
import com.at4wireless.alljoyn.core.interfacevalidator.InterfaceValidator;
import com.at4wireless.alljoyn.core.interfacevalidator.ValidationResult;
import com.at4wireless.alljoyn.core.introspection.BusIntrospector;
import com.at4wireless.alljoyn.core.introspection.bean.InterfaceDetail;
import com.at4wireless.alljoyn.core.introspection.bean.IntrospectionInterface;
import com.at4wireless.alljoyn.core.introspection.bean.IntrospectionNode;
import com.at4wireless.alljoyn.core.introspection.bean.NodeDetail;

/**
 * Manages execution of Core Test Cases
 */
public class AboutTestSuite
{ 
	private static final String INTROSPECTABLE_INTERFACE_NAME = "org.allseen.Introspectable";
	protected static final String TAG = "AboutTestSuite";
	private static final WindowsLoggerImpl logger =  new WindowsLoggerImpl(TAG);
	private static final String BUS_APPLICATION_NAME = "AboutTestSuite";
	private static final String ERROR_MSG_BUS_INTROSPECTION = "Encountered exception while trying to introspect the bus";
	private static final String DBUS_ERROR_SERVICE_UNKNOWN = "org.freedesktop.DBus.Error.ServiceUnknown";
	private String defaultLanguage;
	//private AboutClient aboutClient; //[AT4] AboutClient is deprecated
	private AboutProxy aboutProxy;
	//private AboutIconClient aboutIconClient; //[AT4] AboutIconClient is deprecated
	private AboutIconProxy aboutIconProxy;
	protected AboutAnnouncementDetails deviceAboutAnnouncement;
	private  ServiceHelper serviceHelper;
	private  SimpleDateFormat simpleDateFormat;
	private  final String DATE_FORMAT = "yyyy-MM-dd";

	//private AppUnderTestDetails appUnderTestDetails; //[AT4] Not needed
	private UUID dutAppId;
	private String dutDeviceId;
	
	/**
     * This regular expression is used to replace description tags with the
     * INTROSPECTION_XML_DESC_PLACEHOLDER
     */
	private static final String INTROSPECTION_XML_DESC_REGEX = "<description.*?>.*?</description>";
	//private static final String INTROSPECTION_XML_DESC_REGEX = "(<description>).*(</description>.*)";
	
	/**
     * This placeholder is used to change the description tags in the
     * introspected XML
     */
    private static final String INTROSPECTION_XML_DESC_PLACEHOLDER = "<description></description>";
	//private static final String INTROSPECTION_XML_DESC_PLACEHOLDER   = "$1$2";
	
	/**
     * The expected result after the introspection XML will be modified as a
     * result of applying the
     * {@link EventsActionsTestSuite#INTROSPECTION_XML_DESC_REGEX}
     */
    private static final String INTROSPECTION_XML_DESC_EXPECTED = "<description></description>";
    //private static final String INTROSPECTION_XML_DESC_EXPECTED      = "<description></description>";
    
    /**
     * Announcement time out
     */
    //private static long ANNOUCEMENT_TIMEOUT_IN_SECONDS = 60; //[AT4] Changed "ANNOUCEMENT" to "ANNOUNCEMENT"
    private static long ANNOUNCEMENT_TIMEOUT_IN_SECONDS = 60;
	
	/** 
	 * [AT4] Added attributes to perform the test cases
	 * 
	 * pass	stores the final verdict of the test case
	 * ics	map that stores ICS values	
	 * ixit	map that stores IXIT values
	 * 
	 * */
	
	private Map<String, Boolean> ics;
	private Map<String, String> ixit;
	private boolean pass = true;
	private boolean inconcluse = false;

	public AboutTestSuite(String testCase,
			boolean iCSCO_DateOfManufacture, boolean iCSCO_HardwareVersion,
			boolean iCSCO_SupportUrl, boolean iCSCO_IconInterface, boolean iCSCO_DeviceName,
			String iXITCO_AboutVersion, String iXITCO_AppId,
			String iXITCO_DefaultLanguage, String iXITCO_DeviceName,
			String iXITCO_DeviceId, String iXITCO_AppName,
			String iXITCO_Manufacturer, String iXITCO_ModelNumber,
			String iXITCO_SoftwareVersion,String iXITCO_AJSoftwareVersion, String iXITCO_HardwareVersion,
			String iXITCO_IntrospectableVersion, String iXITCO_SupportedLanguages,
			String iXITCO_Description, String iXITCO_DateOfManufacture,
			String iXITCO_SupportUrl, String gPCO_AnnouncementTimeout)
	{
		/** 
		 * [AT4] Attributes initialization
		 * */
		
		ics = new HashMap<String,Boolean>();
		ixit = new HashMap<String,String>();

		ics.put("ICSCO_DateOfManufacture", iCSCO_DateOfManufacture);
		ics.put("ICSCO_HardwareVersion", iCSCO_HardwareVersion);
		ics.put("ICSCO_SupportUrl", iCSCO_SupportUrl);
		ics.put("ICSCO_IconInterface", iCSCO_IconInterface);
		ics.put("ICSCO_DeviceName", iCSCO_DeviceName);

		ixit.put("IXITCO_AboutVersion", iXITCO_AboutVersion);
		ixit.put("IXITCO_AppId", iXITCO_AppId);
		ixit.put("IXITCO_DefaultLanguage", iXITCO_DefaultLanguage);
		ixit.put("IXITCO_DeviceName", iXITCO_DeviceName);
		ixit.put("IXITCO_DeviceId", iXITCO_DeviceId);
		ixit.put("IXITCO_AppName", iXITCO_AppName);
		ixit.put("IXITCO_Manufacturer", iXITCO_Manufacturer);
		ixit.put("IXITCO_ModelNumber", iXITCO_ModelNumber);
		ixit.put("IXITCO_SoftwareVersion", iXITCO_SoftwareVersion);
		ixit.put("IXITCO_AJSoftwareVersion", iXITCO_AJSoftwareVersion);
		ixit.put("IXITCO_HardwareVersion", iXITCO_HardwareVersion);
		ixit.put("IXITCO_IntrospectableVersion", iXITCO_IntrospectableVersion);
		ixit.put("IXITCO_SupportedLanguages", iXITCO_SupportedLanguages);
		ixit.put("IXITCO_Description", iXITCO_Description);
		ixit.put("IXITCO_DateOfManufacture", iXITCO_DateOfManufacture);
		ixit.put("IXITCO_SupportUrl", iXITCO_SupportUrl);

		ANNOUNCEMENT_TIMEOUT_IN_SECONDS = Integer.parseInt(gPCO_AnnouncementTimeout);

		try {
			runTestCase(testCase);
		} catch(Exception e) {
			/*if (e.getMessage().equals("Timed out waiting for About announcement")) {
				//fail("Timed out waiting for About announcement");
			} else {*/
				String errorMsg = "Exception: "+e.toString();
				logger.error(errorMsg);
				//fail(errorMsg);
			//}
			inconcluse=true;
		}
	}

	public void runTestCase(String test) throws Exception
	{
		setUp();
		logger.info("Running testcase: "+test);
		
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
		
		tearDown();
	}

	protected void setUp() throws Exception
	{
		logger.noTag("====================================================");
		logger.info("test setUp started");
		try 
		{
			//appUnderTestDetails = getValidationTestContext().getAppUnderTestDetails();
			//dutDeviceId = appUnderTestDetails.getDeviceId();
			dutDeviceId = ixit.get("IXITCO_DeviceId"); //[AT4]
			logger.info(String.format("Running About test case against Device ID: %s", dutDeviceId));
			//dutAppId = appUnderTestDetails.getAppId();
			dutAppId = UUID.fromString(ixit.get("IXITCO_AppId")); //[AT4]
			logger.info(String.format("Running About test case against App ID: %s", dutAppId));
			
			serviceHelper = getServiceHelper();
			serviceHelper.initialize(BUS_APPLICATION_NAME, dutDeviceId, dutAppId);

			deviceAboutAnnouncement = serviceHelper.waitForNextDeviceAnnouncement(ANNOUNCEMENT_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS);
			//assertNotNull("Timed out waiting for About Announcement", deviceAboutAnnouncement); //[AT4] Uncomment and delete if
			if (deviceAboutAnnouncement == null)
			{
				throw new Exception("Timed out waiting for About announcement");
			}	
			
			logger.info("Partial Verdict: PASS"); //[AT4]
			
			defaultLanguage = ixit.get("IXITCO_DefaultLanguage");
			//aboutClient = serviceHelper.connectAboutClient(deviceAboutAnnouncement);
			//aboutIconClient = serviceHelper.connectAboutIconClient(deviceAboutAnnouncement);
			//aboutProxy = serviceHelper.connectAboutProxy(deviceAboutAnnouncement); [AT4] Changed to connect on each test case. Other option is put a condition to avoid About-v1-01
			//aboutIconProxy = serviceHelper.connectAboutIconProxy(deviceAboutAnnouncement);
			
			//logger.info("Session established");
			logger.info("test setUp done");
		}
		catch (Exception exception)
		{
			logger.error("Exception setting up resources: "+ exception.getMessage()); //[AT4]
			try 
			{
				//releaseResources(); //[AT4]
				tearDown();
			} 
			catch (Exception newException) 
			{
				logger.error("Exception releasing resources: "+ newException.getMessage());
			}
			
			throw exception;
		}
		
		logger.noTag("====================================================");
	}

	protected void tearDown() throws Exception
	{
		//super.tearDown();
		logger.noTag("====================================================");
		logger.info("test tearDown started");
		
		releaseResources();
		
		logger.info("test tearDown done");
		logger.noTag("====================================================");
	}

	public void testAbout_v1_01_AboutAnnouncement() throws Exception
	{	
		logger.info("Testing if version matches IXITCO_AboutVersion");

		if (Integer.parseInt(ixit.get("IXITCO_AboutVersion"))!=deviceAboutAnnouncement.getVersion())
		{
			fail("About version does not match: "+deviceAboutAnnouncement.getVersion()+" is not equal to "+Integer.parseInt(ixit.get("IXITCO_AboutVersion")));
		}
		else
		{
			logger.info("About version matches IXITCO_AboutVersion: "+Integer.parseInt(ixit.get("IXITCO_AboutVersion")) );
			logger.info("Partial Verdict: PASS");
		} //[AT4] Checks if IXIT interface version is equal to device interface version

		/*String aboutPath = getAboutInterfacePathFromAnnouncement();
		logger.info("Testing if About interface is present in announcement");
		assertNotNull("About interface not present in announcement", aboutPath);
		logger.info("Testing if About interface is present at the right path");
		assertEquals("About interface present at the wrong path", About.OBJ_PATH, aboutPath);*/

		validatePathIfAboutInterfacePresentInAnnouncement(); //[AT4]
		verifyAboutData(deviceAboutAnnouncement.getAboutData());
		
		aboutProxy = serviceHelper.connectAboutProxy(deviceAboutAnnouncement); //[AT4] Establishes session after About validation
	}

	public void testAbout_v1_02_AboutVersion() throws Exception
	{
		aboutProxy = serviceHelper.connectAboutProxy(deviceAboutAnnouncement);

		logger.info("Verifying that the Version property retrieved from the application's About bus"
				+ " object matches the version parameter in its About announcement");

		if (aboutProxy.getVersion() != deviceAboutAnnouncement.getVersion())
		{
			fail("About version does not match: "+deviceAboutAnnouncement.getVersion()+" is not equal to "+ aboutProxy.getVersion());
		}
		else
		{
			logger.info("About version parameter matches Version property");
			logger.info("Partial Verdict: PASS");
		}
		
		//assertEquals("About version does not match", 1, aboutClient.getVersion()); //[AT4] Wrong, aboutProxy version has to be retrieved
	}

	public void testAbout_v1_03_GetObjectDescription() throws Exception
	{
		aboutProxy = serviceHelper.connectAboutProxy(deviceAboutAnnouncement);

		Map<String, String[]> aboutObjectDescriptionMap = new HashMap<String, String[]>();
		Map<String, String[]> announcementObjectDescriptionMap = new HashMap<String, String[]>();
		populateMap(deviceAboutAnnouncement.getObjectDescriptions(), announcementObjectDescriptionMap);
		//populateMap(aboutClient.getBusObjectDescriptions(), aboutObjectDescriptionMap);
		populateMap(aboutProxy.getObjectDescription(), aboutObjectDescriptionMap);
		/*assertEquals("GetObjectDescription does not contain the same set of paths as the announcement", aboutObjectDescriptionMap.keySet(),
				announcementObjectDescriptionMap.keySet());*/
		
		Set<String> aboutSet = aboutObjectDescriptionMap.keySet();
		Set<String> announceSet = announcementObjectDescriptionMap.keySet();

		logger.info("About: "+aboutSet);
		logger.info("Announce: "+announceSet);

		if(!aboutSet.equals(announceSet))
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
			//assertTrue("GetObjectDescription not consistent for "+key, Arrays.equals(aboutObjectDescriptionMap.get(key), announcementObjectDescriptionMap.get(key)));
			if (!Arrays.equals(aboutObjectDescriptionMap.get(key), announcementObjectDescriptionMap.get(key)))
			{
				fail("GetObjectDescription not consistent for " + key);
			}
			else
			{
				logger.info("GetObjectDescription is consistent for " + key);
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
				String errorMessage = new StringBuilder("AboutAnnouncement advertises interface ").append(pathAndInterfaces[1]).append(" at path ").append(pathAndInterfaces[0])
						.append(", but bus does not contain such interface at that path.").toString();
				//assertTrue(errorMessage, busIntrospectPathInterfaceSet.contains(announcementKey));
				if (!busIntrospectPathInterfaceSet.contains(announcementKey))
				{
					fail(errorMessage);
				}
				else
				{
					logger.info("Interface "+pathAndInterfaces[1]+" found at path "+pathAndInterfaces[0]);
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

		//assertTrue(validationResult.getFailureReason(), validationResult.isValid());
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

		//logger.info("Calling getAbout on About interface with lanuage: " + defaultLanguage);
		logger.info("Calling getAboutData on About interface with language: " + defaultLanguage);
		//Map<String, Object> aboutMap = aboutClient.getAbout(defaultLanguage);
		Map<String, Variant> aboutMap = aboutProxy.getAboutData(defaultLanguage);
		
		/*String lang= defaultLanguage;

		aboutProxy = serviceHelper.connectAboutProxy(deviceAboutAnnouncement);

		logger.info("Calling getAboutData");

		Map<String, Variant> aboutData = null;

		try {

			aboutData = aboutProxy.getAboutData(lang);

		} catch (BusException e) {

			fail("Unexpected BusException :"+e.toString());

			return;
		}*/	
		logger.info("Partial Verdict: PASS");

		logger.info("Checking that all required fields are present");
		//verifyAboutMap(aboutData, lang);
		verifyAboutMap(aboutMap, defaultLanguage);
	}

	public void testAbout_v1_07_GetAboutForSupportedLanguages() throws Exception
	{
		aboutProxy = serviceHelper.connectAboutProxy(deviceAboutAnnouncement);

		//logger.info("Calling getAbout on About interface with language " + defaultLanguage);
		logger.info("Calling getAboutData on About interface with language " + defaultLanguage);
		//Map<String, Object> aboutMapDefaultLanguage = aboutClient.getAbout(defaultLanguage);
		Map<String, Variant> aboutMapDefaultLanguage = aboutProxy.getAboutData(defaultLanguage);
		/*aboutProxy = serviceHelper.connectAboutProxy(deviceAboutAnnouncement);

		logger.info("Calling getAboutData");

		Map<String, Variant> aboutMapDefaultLanguage = null;

		try {

			aboutMapDefaultLanguage = aboutProxy.getAboutData(defaultLanguage);

		} catch (BusException e) {

			fail("Unexpected BusException :"+e.toString());
			return;
		}*/

		logger.info("Partial Verdict: PASS");

		//String[] supportedLanguages = (String[]) aboutMapDefaultLanguage.get(AboutKeys.ABOUT_SUPPORTED_LANGUAGES);
		String[] supportedLanguages =  aboutMapDefaultLanguage.get(AboutKeys.ABOUT_SUPPORTED_LANGUAGES).getObject(String[].class);
		validateSupportedLanguagesContainsDefaultLanguage(supportedLanguages);

		if (supportedLanguages.length == 1)
		{
			//getValidationTestContext().addNote("Device only supports one language");
			logger.addNote("Device only supports one language");
		}
		else
		{
			validateSupportedLanguagesAboutMap(supportedLanguages, aboutMapDefaultLanguage);
		}
	}

	public void testAbout_v1_08_GetAboutForUnspecifiedLanguage() throws Exception
	{
		aboutProxy = serviceHelper.connectAboutProxy(deviceAboutAnnouncement);

		/*Map<String, Variant> aboutMapDefaultLanguage = null;
		Map<String, Variant> aboutMapNoLanguage = null;*/

		//logger.info("Calling getAbout on About interface");
		logger.info("Calling getAboutData on About Interface");
		/*Map<String, Object> aboutMapDefaultLanguage = aboutClient.getAbout(defaultLanguage);
        Map<String, Object> aboutMapNoLanguage = aboutClient.getAbout("");*/
		Map<String, Variant> aboutMapDefaultLanguage = aboutProxy.getAboutData(defaultLanguage);
		Map<String, Variant> aboutMapNoLanguage = aboutProxy.getAboutData("");
		/*aboutProxy = serviceHelper.connectAboutProxy(deviceAboutAnnouncement);

		logger.info("Calling getAboutData");

		try {
			aboutMapDefaultLanguage = aboutProxy.getAboutData(defaultLanguage);
			aboutMapNoLanguage= aboutProxy.getAboutData("");
		} catch (BusException e) {

			fail("Unexpected BusException :"+e.toString());
		}*/

		logger.info("Partial Verdict: PASS");

		compareFieldsInAboutMap(aboutMapDefaultLanguage, aboutMapNoLanguage, "");
	}

	public void testAbout_v1_09_GetAboutForUnsupportedLanguage() throws Exception
	{
		aboutProxy = serviceHelper.connectAboutProxy(deviceAboutAnnouncement);

		boolean exceptionThrown = false;
		logger.info("Calling getAbout on About interface with an unsupported language INVALID");
		//aboutProxy = serviceHelper.connectAboutProxy(deviceAboutAnnouncement);

		logger.info("Calling getAboutData");

		try {
			//aboutClient.getAbout("INVALID");
			aboutProxy.getAboutData("INVALID");
			//Map<String, Variant> aboutMapUnsupportedLanguage = aboutProxy.getAboutData("INVALID");
		}
		catch (ErrorReplyBusException ex)
		{
			/*if("org.alljoyn.Error.LanguageNotSupported".equals(ex.getErrorName())){
				logger.info("A LanguageNotSupported exception was Thrown");
			}*/
			exceptionThrown = true;
			assertEquals("Wrong errorName", "org.alljoyn.Error.LanguageNotSupported", ex.getErrorName());
		}
		assertTrue("Calling getAbout on the About interface with an unsupported language must throw an exception", exceptionThrown);
	}

	public void testAbout_v1_10_GetAboutIcon() throws Exception
	{
		if (!ics.get("ICSCO_IconInterface"))
		{
			fail("Test Case not applicable. ICSCO_IconInterface is set to false");
		}
		else
		{
			if (!deviceAboutAnnouncement.supportsInterface(Icon.INTERFACE_NAME))
			{
				//getValidationTestContext().addNote("Device does not support AboutIcon");
				fail("Device does not support AboutIcon");
			}
			else
			{
				aboutProxy =serviceHelper.connectAboutProxy(deviceAboutAnnouncement);
				aboutIconProxy = serviceHelper.connectAboutIconProxy(deviceAboutAnnouncement);

				logger.info("Calling get About Icon retrieve Icon data");
				//String mimeType = aboutIconClient.getMimeType();
				String mimeType = aboutIconProxy.getMimeType();
	            //int iconSize = aboutIconClient.getSize();
				int iconSize = aboutIconProxy.getSize();

				if (mimeType == null || mimeType.isEmpty())
				{
					logger.info("mimetype is empty");
					//getValidationTestContext().addNote("GetContent() method is not supported");
					logger.addNote("GetContent() method is not supported");
					pass = false;
				}
				else if (iconSize == 0)
				{
					logger.info("The icon size is zero");
					//getValidationTestContext().addNote("GetContent() method is not supported");
					logger.addNote("GetContent() method is not supported");
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
		if (!ics.get("ICSCO_IconInterface"))
		{
			fail("Test Case not applicable. ICSCO_IconInterface is set to false");
		}
		else {
			if (!deviceAboutAnnouncement.supportsInterface(Icon.INTERFACE_NAME))
			{
				//getValidationTestContext().addNote("Device does not support AboutIcon");
				fail("Device does not support AboutIcon");
			} 
			else
			{
				aboutProxy = serviceHelper.connectAboutProxy(deviceAboutAnnouncement);
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
			//getValidationTestContext().addNote("URL returned is null/empty");
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
				fail("Icon URL is malformed: "+ malformedUrlException);
				exception = malformedUrlException;
			}
			
			//assertNull("The received icon URL: '"+ iconUrl + "' isn't valid", exception);
			if(exception==null) {
				logger.info("The received icon URL: '" + iconUrl + "' isn't valid");
			}
		}
	}
	
	protected BusIntrospector getIntrospector()
	{
		//return serviceHelper.getBusIntrospector(aboutClient);
		return serviceHelper.getBusIntrospector(deviceAboutAnnouncement);
	}
	
	protected InterfaceValidator getInterfaceValidator() throws IOException, ParserConfigurationException, SAXException
	{
		//return new InterfaceValidator(getValidationTestContext());
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
            //assertEquals("About interface present at the wrong path", AboutTransport.OBJ_PATH, aboutPath);
            assertEquals("About interface present at the wrong path", About.OBJ_PATH, aboutPath); //[AT4] AboutTransport is deprecated
        }
        else
        {
        	logger.info("About interface not present in announcement");
        	//fail("About interface not present in announcement");
        }
    }
	
	private void releaseResources()
	{
		//disconnectFromAboutClient();
		//disconnectFromAboutIconClient();
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

    /*private void disconnectFromAboutIconClient()
    {
        if (aboutIconClient != null)
        {
            aboutIconClient.disconnect();
            aboutIconClient = null;
        }
    }*/
	
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
			logger.info("BusObjectDescription: " + busObjectDescription.path + " supports " + Arrays.toString(busObjectDescription.interfaces));
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
				logger.info("Calling getAbout on About interface with language " + supportedLanguage);
				//Map<String, Object> aboutMapSupportedLanguage = aboutClient.getAbout(supportedLanguage);
				aboutProxy = serviceHelper.connectAboutProxy(deviceAboutAnnouncement);
				Map<String, Variant> aboutMapSupportedLanguage = null;

				try {
					aboutMapSupportedLanguage = aboutProxy.getAboutData(supportedLanguage);
				} catch (BusException e) {
					fail("Unexpected BusException :"+e.toString());
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
		compareAbout(AboutKeys.ABOUT_DEFAULT_LANGUAGE,ixit.get("IXITCO_DefaultLanguage"),aboutData.get(AboutKeys.ABOUT_DEFAULT_LANGUAGE).getObject(String.class),"");

		if(ics.get("ICSCO_DeviceName")) {
			verifyFieldIsPresent(AboutKeys.ABOUT_DEVICE_NAME, aboutData);
			compareAbout(AboutKeys.ABOUT_DEVICE_NAME,ixit.get("IXITCO_DeviceName"),aboutData.get(AboutKeys.ABOUT_DEVICE_NAME).getObject(String.class),"");
		} //JTF: Need to add case ICSCO_DeviceName is false and field is present

		verifyFieldIsPresent(AboutKeys.ABOUT_DEVICE_ID, aboutData);
		compareAbout(AboutKeys.ABOUT_DEVICE_ID,ixit.get("IXITCO_DeviceId"),(String) aboutData.get(AboutKeys.ABOUT_DEVICE_ID).getObject(String.class),"");

		verifyFieldIsPresent(AboutKeys.ABOUT_APP_NAME, aboutData);
		compareAbout(AboutKeys.ABOUT_APP_NAME,ixit.get("IXITCO_AppName"), aboutData.get(AboutKeys.ABOUT_APP_NAME).getObject(String.class),"");

		verifyFieldIsPresent(AboutKeys.ABOUT_MANUFACTURER, aboutData);
		compareAbout(AboutKeys.ABOUT_MANUFACTURER,ixit.get("IXITCO_Manufacturer"), aboutData.get(AboutKeys.ABOUT_MANUFACTURER).getObject(String.class),"");

		verifyFieldIsPresent(AboutKeys.ABOUT_MODEL_NUMBER, aboutData);
		compareAbout(AboutKeys.ABOUT_MODEL_NUMBER,ixit.get("IXITCO_ModelNumber"), aboutData.get(AboutKeys.ABOUT_MODEL_NUMBER).getObject(String.class),"");
	}
	
	private void verifyFieldIsPresent(String key, Map<String, Variant> aboutData) throws BusException
	{
		//assertNotNull(key + " is a required field", aboutData.get(key));
		if(aboutData.get(key)==null) {

			fail(key + " is a required field" );

		}else{
			if(key.equals(AboutKeys.ABOUT_APP_ID)) {
				logger.info(key+" is present and matches IXIT: "+ ixit.get("IXITCO_AppId"));
				logger.info("Partial Verdict: PASS");
			} else {	
				logger.info(key+" is present and is: "+ aboutData.get(key).getObject(String.class));
				logger.info("Partial Verdict: PASS");
			}
		}
	}
	
	private void verifyAboutMap(Map<String, Variant> aboutMap, String language) throws Exception
	{
		checkForNull(aboutMap, language);
		validateSignature(aboutMap, language);

		if(ics.get("ICSCO_DateOfManufacture")) { //[AT4] Added ICS checking
			validateDateOfManufacture(aboutMap, language);
		}
		if(ics.get("ICSCO_SupportUrl")) { //[AT4] Added ICS checking
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
		if(ics.get("ICSCO_DateOfManufacture")) checkForNull(aboutMap, AboutKeys.ABOUT_DATE_OF_MANUFACTURE, language); //[AT4]
		if(ics.get("ICSCO_HardwareVersion")) checkForNull(aboutMap, AboutKeys.ABOUT_HARDWARE_VERSION, language); //[AT4]
		if(ics.get("ICSCO_SupportUrl")) checkForNull(aboutMap, AboutKeys.ABOUT_SUPPORT_URL, language); //[AT4]
	}
	
	private void checkForNull(Map<String, Variant> aboutMap, String fieldName, String language)
	{
		//assertNotNull(fieldName + " is a required field for language " +language, aboutMap.get(fieldName));
		if(aboutMap.get(fieldName)==null)
		{
			fail(fieldName + " is a required field for language: "+language);
		} else {
			logger.info(fieldName+" is present");
			if(language==ixit.get("IXITCO_DefaultLanguage")) {
				try {
					if(fieldName==AboutKeys.ABOUT_SUPPORTED_LANGUAGES) {
						//compareAbout(fieldName,ixit.get("IXITCO_"+fieldName),(String) aboutMap.get(fieldName).getObject(String.class),"");
					} else {
						compareAbout(fieldName,ixit.get("IXITCO_"+fieldName),(String) aboutMap.get(fieldName).getObject(String.class),"");
					}
				} catch (BusException e) {
					logger.error(e.getMessage());
				} catch (Exception e) {
					logger.error(e.getMessage());
				}
			}
		}
	}
	
	//private void validateSignature(Map<String, Object> aboutMap, String language) throws AnnotationBusException
	private void validateSignature(Map<String, Variant> aboutMap, String language) throws AnnotationBusException
	{
		//Map<String, Variant> aboutVariantMap = TransportUtil.toVariantMap(aboutMap);
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
		if(!signature.equals(aboutVariantMap.get(fieldName).getSignature()))
		{
			fail("Signature does not match for required field: "+fieldName+" for language "+language);
		} else {
			logger.info("Signature matches for required field "+fieldName+" for language "+language);
			logger.info("Partial Verdict: PASS");
		}
	}
	
	private void validateDateOfManufacture(Map<String, Variant> aboutMap, String language) throws BusException
	{
		if (aboutMap.containsKey(AboutKeys.ABOUT_DATE_OF_MANUFACTURE))
		{
			simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);
			simpleDateFormat.setLenient(false);

			//String dateOfManufacture = aboutMap.get(AboutKeys.ABOUT_DATE_OF_MANUFACTURE).toString();
			String dateOfManufacture = aboutMap.get(AboutKeys.ABOUT_DATE_OF_MANUFACTURE).getObject(String.class );
			logger.info("Validating Date Of Manufacture: "+dateOfManufacture);

            //assertTrue(String.format("DateOfManufacture field value %s does not match expected date pattern YYYY-MM-DD", dateOfManufacture), isValidDate(dateOfManufacture));
			if(!isValidDate(dateOfManufacture)){
				fail(String.format("DateOfManufacture field value %s does not match expected date pattern YYYY-MM-DD", dateOfManufacture));
			}
		}
	}
	
	private void validateSupportUrl(Map<String, Variant> aboutMap, String language) throws BusException
	{
		if (aboutMap.containsKey(AboutKeys.ABOUT_SUPPORT_URL))
		{
			logger.info("Validating Support Url: "+aboutMap.get(AboutKeys.ABOUT_SUPPORT_URL).getObject(String.class));
			//String supportUrl = aboutMap.get(AboutKeys.ABOUT_SUPPORT_URL).toString();
			String supportUrl = aboutMap.get(AboutKeys.ABOUT_SUPPORT_URL).getObject(String.class);
			//assertTrue(String.format("%s is not a valid URL", supportUrl), isValidUrl(supportUrl));
			if(!isValidUrl(supportUrl))
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
		String assertionFailureResponse = prepareAssertionFailureResponse(fieldName,expectedAboutFieldValue,aboutFieldValue, language);
		
		if(language.equals("")) {
			language="unspecified language";
		}

		if(expectedAboutFieldValue.equals(aboutFieldValue)) {
			logger.info(fieldName+" received: "+aboutFieldValue+" is equal to IXITCO_"+fieldName+": "+expectedAboutFieldValue);
		}
		
		assertEquals(assertionFailureResponse, expectedAboutFieldValue, aboutFieldValue);
	}
	
	private void compareAbout(String fieldName, Class<? extends Variant> class1,
			Class<? extends Variant> class2, String language)
	{
		String assertionFailureResponse = prepareAssertionFailureResponse(fieldName, language);
		assertEquals(assertionFailureResponse, class1, class2);
		if(class1.equals(class2)){
			logger.info(fieldName+" is the same for the default language and the localized field");
		}
	} //[AT4]
	
	private String prepareAssertionFailureResponse(String fieldName, String language)
	{
		StringBuilder assertionFailureResponseBuilder = new StringBuilder();
		assertionFailureResponseBuilder.append(fieldName);
		assertionFailureResponseBuilder.append(" value returned from the GetAbout for language ");
		assertionFailureResponseBuilder.append(language);
		assertionFailureResponseBuilder.append(" does not match default language value;");

		return assertionFailureResponseBuilder.toString();
	}

	private String prepareAssertionFailureResponse(String fieldName ,String ixit ,String value,String language)
	{
		StringBuilder assertionFailureResponseBuilder = new StringBuilder();
		assertionFailureResponseBuilder.append(fieldName);
		assertionFailureResponseBuilder.append(" value returned from the About announcement "+value);

		assertionFailureResponseBuilder.append(" does not match IXIT: "+ixit);

		return assertionFailureResponseBuilder.toString();
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
			msg = new StringBuilder("AboutAnnouncement has the path ").append(path).append(", but it is not found on the Bus Intropsection.").toString();
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
		//return new ServiceHelper(new AndroidLogger());
		return new ServiceHelper(logger);
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
		assertTrue("Looks like this object doesn't implement the: '" + getIntrospectableInterfaceName() + "' interface," + " even though it states it does in the announcement", 
				objectPaths.size() > 0);

		logger.info("Object paths to be tested: "+objectPaths);
		for (String objectPath : objectPaths)
		{
			
			logger.info("==> Testing Announced Object Path: "+objectPath);

			assertTrue("The test of the Announced Object Path: '" + objectPath + "' has failed", testObjectValidity(objectPath));
			logger.info("==> The test of the Announced Object Path: "+objectPath+" passed successfully");
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
		
		logger.info(String.format("Testing Object Path: '%s'", objectPath));
		/*ProxyBusObject proxyObj = serviceHelper.getProxyBusObject(aboutClient, objectPath, new Class<?>[]
		        { AllSeenIntrospectable.class });*/
		ProxyBusObject proxyObj = serviceHelper.getProxyBusObject(deviceAboutAnnouncement, objectPath,
				new Class<?>[]{AllSeenIntrospectable.class});
		String[] descLangs = getDescriptionLanguages(proxyObj, objectPath);
		if ( descLangs.length == 0 )
		{
			
			logger.warn("No description languages found for the Object Path: '%s'. Introspecting child objects with NO_LANGUAGE", objectPath);

			String introXML = getIntrospectionXML(proxyObj, objectPath, "NO_LANGUAGE");
			assertNotNull("Introspection XML is NULL Object Path: '" + objectPath + "'", introXML);
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
			
			logger.error("Failed to parse the introspection XML, object path: "+ parentObjectPath);
			logger.error("Error", e);
			//fail();
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
			if ( !descFoundBroth )
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
			assertNotNull("Introspection XML is NULL Object Path: '" + parentObjectPath + "'", currentXML);
			
			// Print the introspection XML
            // logger.debug("The introspection XML, the lang: '%s': '%s'", lang,
            // currentXML);

			currentXML = removeXMLDesc(currentXML);

			logger.info("Testing language validity for the object path: '%s', language: '%s'", parentObjectPath, lang);

			if ( firstLangXML == null )
			{

				//boolean isXmlDescPresent = xmlDescPresent.matcher(currentXML).find();
				
				assertTrue("The description tag wasn't found in the XML for the description language: '" + lang + "', " +
						"Object Path: '" + parentObjectPath + "'", currentXML.contains(INTROSPECTION_XML_DESC_EXPECTED));

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

			logger.info("Test identity of the XMLs in the first language: '%s' and the current language: '%s', " + "Object Path: '%s'", firstLang, lang, parentObjectPath);

			// Print the intospection XML in the first language and in the
            // current language
            // logger.debug("The expected XML in the first lang: '%s': '%s'",
            // firstLang, firstLangXML);
            // logger.debug("The tested XML in the current lang: '%s': '%s'",
            // lang, currentXML);

            // If current language is not a first language, compare current
            // language XML with the first language XML
			assertEquals("The XML in the first language: '" + firstLang + "' is not identical to the XML in the current language: '" + lang + "', object path: '" 
						+ parentObjectPath + "'", firstLangXML, currentXML);

			logger.info("The XMLs in the first language: '%s' and the current language: '%s', " + "Object Path: '%s' are identical", firstLang, lang, parentObjectPath);
		}// for :: descLangs

		testChildrenObjectValidity(parentObjectPath, firstLangXML);
		return descriptionFound;
	}
	
	/**
     * Searches in the received announcement object paths that implement the
     * {@link AllSeenIntrospectable} interface
     * 
     * @return Array of the object paths
     */
	protected List<String> getAllSeenIntrospectablObjectPaths()
	{
		
		List<String> retList  = new ArrayList<String>();
		String introIfaceName = getIntrospectableInterfaceName();

		//BusObjectDescription[] objDescs = deviceAboutAnnouncement.getObjectDescriptions();
		AboutObjectDescription[] objDescs = deviceAboutAnnouncement.getObjectDescriptions();
		//for (BusObjectDescription bod : objDescs)
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

		String[] langs = new String[]
		{};

		try
		{
			langs = proxyObj.getInterface(AllSeenIntrospectable.class).GetDescriptionLanguages();
		}
		catch (BusException be)
		{

			logger.error("Failed to call GetDescriptionLanguages for the Object Path: "+ objectPath);
			logger.error("Error", be);
			//fail();
			fail("Failed to call GetDescriptionLanguages for the Object Path: "+ objectPath);
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

			logger.error("Failed to call IntrospectWithDescription for the Object Path: "+ objectPath);
			logger.error("Error", be);
			//fail();
			fail("Failed to call IntrospectWithDescription for the Object Path: "+ objectPath);
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
		if (first != second) {
			fail(errorMessage);
		} else {
			logger.info("Partial Verdict: PASS");
		}
	}
	
	private void assertEquals(String errorMessage, String first, String second)
	{
		if (!first.equals(second)) {
			fail(errorMessage);
		} else {
			logger.info("Partial Verdict: PASS");
		}
	}

	private void assertEquals(String errorMessage, Class<? extends Variant> first, Class<? extends Variant> second)
	{
		if (!first.equals(second)) {
			fail(errorMessage);
		} else {
			logger.info("Partial Verdict: PASS");
		}
	}

	private void assertTrue(String errorMessage, boolean condition)
	{
		if (!condition) {
			fail(errorMessage);
		} else {
			logger.info("Partial Verdict: PASS");
		}
	}

	private void assertFalse(String errorMessage, boolean condition)
	{
		if (condition) {
			fail(errorMessage);
		} else {
			logger.info("Partial Verdict: PASS");
		}
	}
	
	private void assertNotNull(String errorMessage, Object object)
	{
		if (object == null) {
			fail(errorMessage);
		} else {
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
		if (inconcluse) {
			return "INCONC";
		}
		
		if (pass) {
			return "PASS";
		} else {
			return "FAIL";
		}
	}
}
