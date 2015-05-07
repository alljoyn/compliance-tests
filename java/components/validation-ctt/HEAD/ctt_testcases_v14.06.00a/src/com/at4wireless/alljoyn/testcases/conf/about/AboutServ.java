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

import org.alljoyn.about.AboutKeys;
import org.alljoyn.about.client.AboutClient;
import org.alljoyn.about.icon.AboutIconClient;
import org.alljoyn.about.transport.AboutTransport;
import org.alljoyn.about.transport.IconTransport;
import org.alljoyn.bus.AnnotationBusException;
import org.alljoyn.bus.BusAttachment;
import org.alljoyn.bus.BusException;
import org.alljoyn.bus.ErrorReplyBusException;
import org.alljoyn.bus.ProxyBusObject;
import org.alljoyn.bus.Variant;
import org.alljoyn.bus.annotation.BusInterface;
import org.alljoyn.bus.ifaces.AllSeenIntrospectable;
import org.alljoyn.services.common.BusObjectDescription;
import org.alljoyn.services.common.utils.TransportUtil;
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
 * The Class AboutServ.
 */
public class AboutServ { 

	/** . */
	private  Boolean pass=true;
	
	/** The inconcluse. */
	private boolean inconcluse=false;

	/** The introspectable interface name. */
	private  final String INTROSPECTABLE_INTERFACE_NAME = "org.allseen.Introspectable";
	
	/** The tag. */
	protected  final String TAG = "AboutTestSuite";
	
	/** The logger. */
	private  final WindowsLoggerImpl logger =  new WindowsLoggerImpl(TAG);
	
	/** The bus application name. */
	private  final String BUS_APPLICATION_NAME = "AboutTestSuite";
	
	/** The error msg bus introspection. */
	private  final String ERROR_MSG_BUS_INTROSPECTION = "Encountered exception while trying to introspect the bus";
	
	/** The dbus error service unknown. */
	private  final String DBUS_ERROR_SERVICE_UNKNOWN = "org.freedesktop.DBus.Error.ServiceUnknown";
	
	/** The default language. */
	private  String defaultLanguage;
	
	/** The about client. */
	private  AboutClient aboutClient;
	
	/** The about icon client. */
	private  AboutIconClient aboutIconClient;

	/** The device about announcement. */
	protected  AboutAnnouncementDetails deviceAboutAnnouncement;
	
	/** The service helper. */
	private  ServiceHelper serviceHelper;
	
	/** The simple date format. */
	private  SimpleDateFormat simpleDateFormat;
	
	/** The date format. */
	private  final String DATE_FORMAT = "yyyy-MM-dd";
	
	/** The time out. */
	private  int  timeOut=30;

	/**
	 * This regular expression is used to replace description tags with the INTROSPECTION_XML_DESC_PLACEHOLDER
	 */
	private  final String INTROSPECTION_XML_DESC_REGEX         = "(<description>).*(</description>.*)";
	/**
	 * This placeholder is used to change the description tags in the introspected XML
	 */
	private  final String INTROSPECTION_XML_DESC_PLACEHOLDER   = "$1$2";
	/**
	 * The expected result after the introspection XML will be modified as a result of applying the
	 * {@link EventsActionsTestSuite#INTROSPECTION_XML_DESC_REGEX}
	 */
	private  final String INTROSPECTION_XML_DESC_EXPECTED      = "<description></description>";

	/** The ics. */
	private Map<String, Boolean> ics;
	
	/** The ixit. */
	private Map<String, String> ixit;

	/**
	 * Instantiates a new about service testcase.
	 *
	 * @param testCase the test case
	 * @param iCSCO_DateOfManufacture the ICSCO date of manufacture
	 * @param iCSCO_HardwareVersion the ICSCO hardware version
	 * @param iCSCO_SupportUrl the ICSCO support url
	 * @param iCSCO_IconInterface the ICSCO icon interface
	 * @param iCSCO_DeviceName the ICSCO device name
	 * @param iXITCO_AboutVersion the IXITCO about version
	 * @param iXITCO_AppId the IXITCO app id
	 * @param iXITCO_DefaultLanguage the IXITCO default language
	 * @param iXITCO_DeviceName the IXITCO device name
	 * @param iXITCO_DeviceId the IXITCO device id
	 * @param iXITCO_AppName the IXITCO app name
	 * @param iXITCO_Manufacturer the IXITCO manufacturer
	 * @param iXITCO_ModelNumber the IXITCO model number
	 * @param iXITCO_SoftwareVersion the IXITCO software version
	 * @param iXITCO_AJSoftwareVersion the IXITCO aj software version
	 * @param iXITCO_HardwareVersion the IXITCO hardware version
	 * @param iXITCO_IntrospectableVersion the IXITCO introspectable version
	 * @param iXITCO_SupportedLanguages the IXITCO supported languages
	 * @param iXITCO_Description the IXITCO description
	 * @param iXITCO_DateOfManufacture the IXITCO date of manufacture
	 * @param iXITCO_SupportUrl the IXITCO support url
	 * @param gPCO_AnnouncementTimeout the GPCO announcement timeout
	 */
	public AboutServ(String testCase,
			boolean iCSCO_DateOfManufacture, boolean iCSCO_HardwareVersion,
			boolean iCSCO_SupportUrl, boolean iCSCO_IconInterface, boolean iCSCO_DeviceName,
			String iXITCO_AboutVersion, String iXITCO_AppId,
			String iXITCO_DefaultLanguage, String iXITCO_DeviceName,
			String iXITCO_DeviceId, String iXITCO_AppName,
			String iXITCO_Manufacturer, String iXITCO_ModelNumber,
			String iXITCO_SoftwareVersion,String iXITCO_AJSoftwareVersion, String iXITCO_HardwareVersion,
			String iXITCO_IntrospectableVersion, String iXITCO_SupportedLanguages,
			String iXITCO_Description, String iXITCO_DateOfManufacture,
			String iXITCO_SupportUrl, String gPCO_AnnouncementTimeout){

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

		timeOut = Integer.parseInt(gPCO_AnnouncementTimeout);

		try {
			runTestCase(testCase);

		} catch(Exception e){
			logger.error(e.getMessage());
			if (e!=null) {
				if (e.getMessage().equals("Timed out waiting for About announcement")) {
					fail("Timed out waiting for About announcement");
				} else {
					String errorMsg = "Exception: "+e.toString();
					fail(errorMsg);
				}
				inconcluse=true;
			}
		}
	}
	
	
	/**
	 * Run test case.
	 *
	 * @param test the test
	 * @throws Exception the exception
	 */
	public  void runTestCase(String test) throws Exception{

		setUp();
		logger.info("Running testcase: "+test);
		if(test.equals("About-v1-01")){
			testAbout_v1_01_AboutAnnouncement();
		}else if(test.equals("About-v1-02")){
			testAbout_v1_02_AboutVersion();
		}else if(test.equals("About-v1-03")){
			testAbout_v1_03_GetObjectDescription();
		}else if(test.equals("About-v1-04")){
			testAbout_v1_04_AboutAnnouncementConsistentWithBusObjects();
		}else if(test.equals("About-v1-05")){
			testAbout_v1_05_StandardizedInterfacesMatchDefinitions();
		}else if(test.equals("About-v1-06")){
			testAbout_v1_06_GetAboutForDefaultLanguage();
		}else if(test.equals("About-v1-07")){
			testAbout_v1_07_GetAboutForSupportedLanguages();
		}else if(test.equals("About-v1-08")){
			testAbout_v1_08_GetAboutForUnspecifiedLanguage();
		}else if(test.equals("About-v1-09")){
			testAbout_v1_09_GetAboutForUnsupportedLanguage();
		}else if(test.equals("About-v1-10")){
			testAbout_v1_10_GetAboutIcon();
		}else if(test.equals("About-v1-11")){
			testAbout_v1_11_GetAboutIconValidUrl();
		}else if(test.equals("EventsActions-v1-01")) {
			testEventsActions_v1_01();
		}else{
			fail("TestCase not valid");
		}
		tearDown();
		
	}

	/**
	 * Set up.
	 *
	 * @throws Exception the exception
	 */
	protected void setUp() throws Exception
	{
		System.out.println("====================================================");
		logger.info("test setUp started");

		try 
		{      
			logger.info("Running About test case against Device ID: "
					+ixit.get("IXITCO_DeviceId"));
			logger.info("Running About test case against App ID: "
					+UUID.fromString(ixit.get("IXITCO_AppId")));	
			serviceHelper = getServiceHelper();
			serviceHelper.initialize(BUS_APPLICATION_NAME, ixit.get("IXITCO_DeviceId"), 
					UUID.fromString(ixit.get("IXITCO_AppId")));

			deviceAboutAnnouncement = serviceHelper.waitForNextDeviceAnnouncement(timeOut, TimeUnit.SECONDS);

			if(deviceAboutAnnouncement==null){
				throw new Exception("Timed out waiting for About announcement");
			}
			
			logger.info("Partial Verdict: PASS");

			defaultLanguage = ixit.get("IXITCO_DefaultLanguage");
			
			aboutClient = serviceHelper.connectAboutClient(deviceAboutAnnouncement);
			aboutIconClient = serviceHelper.connectAboutIconClient(deviceAboutAnnouncement);
			

			logger.info("test setUp done");
		}
		catch (Exception exception)
		{
			logger.error("Exception setting up resources: "+ exception.getMessage());

			try 
			{
				releaseResources();
			} 
			catch (Exception newException) 
			{
				logger.error("Exception releasing resources: "+ newException.getMessage());
			}

			throw exception;
		}
		System.out.println("====================================================");
	}

	/**
	 * Tear down.
	 *
	 * @throws Exception the exception
	 */
	protected  void tearDown() throws Exception
	{
		System.out.println("====================================================");

		logger.info("test tearDown started");
		releaseResources();
		logger.info("test tearDown done");
		System.out.println("====================================================");
	}

	/**
	 * Test about_v1_01 about announcement.
	 *
	 * @throws Exception the exception
	 */
	public  void testAbout_v1_01_AboutAnnouncement() throws Exception
	{	
		logger.info("Testing if version matches IXITCO_AboutVersion");
		String aboutPath = getAboutInterfacePathFromAnnouncement();
		logger.info("Testing if About interface is present in announcement");
		assertNotNull("About interface not present in announcement", aboutPath);
		logger.info("Testing if About interface is present at the right path");
		assertEquals("About interface present at the wrong path", AboutTransport.OBJ_PATH, aboutPath);

		verifyAboutData(deviceAboutAnnouncement.getAboutData());	
	}


	/**
	 * Test about_v1_02_ about version.
	 *
	 * @throws Exception the exception
	 */
	public  void testAbout_v1_02_AboutVersion() throws Exception
	{

		logger.info("Verifying that the Version property retrieved from the application's About Client"
				+ " matches IXITCO_AboutVersion");

		if(Integer.parseInt(ixit.get("IXITCO_AboutVersion"))!= aboutClient.getVersion()){
			fail("About version does not match: "+aboutClient.getVersion()+" is not equal to "+ ixit.get("IXITCO_AboutVersion"));
		} else {
			logger.info("About version parameter matches Version property");
			logger.info("Partial Verdict: PASS");
			
		}	}

	/**
	 * Test about_v1_03_ get object description.
	 *
	 * @throws Exception the exception
	 */
	public  void testAbout_v1_03_GetObjectDescription() throws Exception
	{


		Map<String, String[]> aboutObjectDescriptionMap = new HashMap<String, String[]>();
		Map<String, String[]> announcementObjectDescriptionMap = new HashMap<String, String[]>();
		populateMap(deviceAboutAnnouncement.getObjectDescriptions(), announcementObjectDescriptionMap);
		populateMap(aboutClient.getBusObjectDescriptions(), aboutObjectDescriptionMap);

		
		Set<String> aboutSet = aboutObjectDescriptionMap.keySet();
		Set<String> announceSet = announcementObjectDescriptionMap.keySet();
		
		logger.info("About: "+aboutSet);
		logger.info("Announce: "+announceSet);
		
		if(!aboutSet.equals(announceSet)){
			fail("GetObjectDescription does not contain the same set of paths as the announcement");

		}else{
			logger.info("GetObjectDescription does contain the same set of paths as the announcement");
			logger.info("Partial Verdict: PASS");

		}

		for (String key : aboutObjectDescriptionMap.keySet())
		{
			if (!Arrays.equals(aboutObjectDescriptionMap.get(key), announcementObjectDescriptionMap.get(key))) {
				fail("GetObjectDescription not consistent for " + key );
			} else{
				logger.info("GetObjectDescription is consistent for " + key );
				logger.info("Partial Verdict: PASS");
			}
		}		
	}

	/**
	 * Test about_v1_04_ about announcement consistent with bus objects.
	 *
	 * @throws Exception the exception
	 */
	public final  void testAbout_v1_04_AboutAnnouncementConsistentWithBusObjects() throws Exception
	{


		BusIntrospector busIntrospector = getIntrospector();

		Set<String> announcementPathInterfaceSet = new HashSet<String>();
		Set<String> busIntrospectPathInterfaceSet = new HashSet<String>();

		for (BusObjectDescription busObjectDescription : deviceAboutAnnouncement.getObjectDescriptions())
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
				if(!busIntrospectPathInterfaceSet.contains(announcementKey)){

					fail(errorMessage);

				}else{
					logger.info("Interface "+pathAndInterfaces[1]+" found at path "+pathAndInterfaces[0]);
					logger.info("Partial Verdict: PASS");
				}
			}
		}
	

	
		
		
	}

	/**
	 * Test about_v1_05_ standardized interfaces match definitions.
	 *
	 * @throws Exception the exception
	 */
	public  void testAbout_v1_05_StandardizedInterfacesMatchDefinitions() throws Exception
	{

		List<InterfaceDetail> standardizedIntrospectionInterfacesExposedOnBus = getIntrospector().getStandardizedInterfacesExposedOnBus();
		for (InterfaceDetail objectDetail : standardizedIntrospectionInterfacesExposedOnBus)
		{
			for (IntrospectionInterface intfName : objectDetail.getIntrospectionInterfaces())
			{
				logger.info(String.format("Found object at %s implementing %s", objectDetail.getPath(), intfName.getName()));
			}
		}
		ValidationResult validationResult = getInterfaceValidator().validate(standardizedIntrospectionInterfacesExposedOnBus);

		if (!validationResult.isValid()) {
			fail(validationResult.getFailureReason());
		} else {
			logger.info("Partial Verdict: PASS");
		}
		
		
	
	}

	/**
	 * Test about_v1_06_ get about for default language.
	 *
	 * @throws Exception the exception
	 */
	public  void testAbout_v1_06_GetAboutForDefaultLanguage() throws Exception
	{

		logger.info("Calling getAbout on About interface with language: " + defaultLanguage);
		logger.info("Calling getAbout on About interface with language " + defaultLanguage);
		Map<String, Object> aboutMap = aboutClient.getAbout(defaultLanguage);
		logger.info("Partial Verdict: PASS");
		logger.info("Checking that all required fields are present");
		verifyAboutMap(aboutMap, defaultLanguage);
	}


	/**
	 * Test about_v1_07_ get about for supported languages.
	 *
	 * @throws Exception the exception
	 */
	public  void testAbout_v1_07_GetAboutForSupportedLanguages() throws Exception
	{

		logger.info("Calling getAbout on About interface with language " + defaultLanguage);
		
		Map<String, Object> aboutMapDefaultLanguage = aboutClient.getAbout(defaultLanguage);


		logger.info("Partial Verdict: PASS");

		String[] supportedLanguages = (String[]) aboutMapDefaultLanguage.get(AboutKeys.ABOUT_SUPPORTED_LANGUAGES);
		validateSupportedLanguagesContainsDefaultLanguage(supportedLanguages);

		if (supportedLanguages.length == 1)
		{
			logger.addNote("Device only supports one language");
		}
		else
		{
			validateSupportedLanguagesAboutMap(supportedLanguages, aboutMapDefaultLanguage);
		}
		

		



	}

	/**
	 * Test about_v1_08_ get about for unspecified language.
	 *
	 * @throws Exception the exception
	 */
	public  void testAbout_v1_08_GetAboutForUnspecifiedLanguage() throws Exception
	{
	
		logger.info("Calling getAbout on About interface for "+defaultLanguage);
		Map<String, Object> aboutMapDefaultLanguage = aboutClient.getAbout(defaultLanguage);
		logger.info("Calling getAbout on About interface for "+defaultLanguage);
		Map<String, Object> aboutMapNoLanguage = aboutClient.getAbout("");
		compareFieldsInAboutMap(aboutMapDefaultLanguage, aboutMapNoLanguage, "");

		logger.info("Partial Verdict: PASS");
	}

	/**
	 * Test about_v1_09_ get about for unsupported language.
	 *
	 * @throws Exception the exception
	 */
	public  void testAbout_v1_09_GetAboutForUnsupportedLanguage() throws Exception
	{
	
		
		boolean exceptionThrown = false;
		logger.info("Calling getAbout on About interface with an unsupported language INVALID");
		try
		{
			aboutClient.getAbout("INVALID");
		}
		catch (ErrorReplyBusException ex)
		{
			if("org.alljoyn.Error.LanguageNotSupported".equals(ex.getErrorName())){
				logger.info("A LanguageNotSupported exception was Thrown");
			}
			exceptionThrown = true;
			assertEquals("Wrong errorName", "org.alljoyn.Error.LanguageNotSupported", ex.getErrorName());

		}
		assertTrue("Calling getAbout on the About interface with an unsupported language must throw an exception", exceptionThrown);
	
	
	}


	/**
	 * Test about_v1_10_ get about icon.
	 *
	 * @throws Exception the exception
	 */
	public  void testAbout_v1_10_GetAboutIcon() throws Exception
	{
		if (!deviceAboutAnnouncement.supportsInterface(IconTransport.INTERFACE_NAME))
		{
			fail("Device does not support AboutIcon");
		}
		else
		{
			logger.info("Calling get About Icon retrieve Icon data");
			String mimeType = aboutIconClient.getMimeType();
			int iconSize = aboutIconClient.getSize();

			if (mimeType == null || mimeType.isEmpty())
			{
				logger.info("mimetype is empty");
				logger.addNote("GetContent() method is not supported");
				pass=false;
			}
			else if (iconSize == 0)
			{
				logger.info("The icon size is zero");
				logger.addNote("GetContent() method is not supported");
				pass=false;
			}
			else
			{
				assertTrue("Mime type should match pattern image/*", mimeType.startsWith(MediaType.ImagePrefix.getValue()));
				assertTrue("Icon size should be less than " + BusAttachment.ALLJOYN_MAX_ARRAY_LEN, iconSize < BusAttachment.ALLJOYN_MAX_ARRAY_LEN);

				byte[] iconContent = aboutIconClient.GetContent();
				assertEquals("Size of GetContent does not match iconSize", iconSize, iconContent.length);
				InputStream in = new ByteArrayInputStream(iconContent);
				BufferedImage iconBitmap = ImageIO.read(in);


				logger.info("The IconBitMap size: '" + iconSize + "', Icon Height: '" + iconBitmap.getHeight() + "', Icon Width: '" + iconBitmap.getWidth() + "', toString(): "
						+ iconBitmap);
			}
		}

		
	}


	/**
	 * Test about_v1_11_ get about icon valid url.
	 *
	 * @throws Exception the exception
	 */
	public  void testAbout_v1_11_GetAboutIconValidUrl() throws Exception
	{
		if (!ics.get("ICSCO_IconInterface"))
		{
			logger.addNote("Device does not support AboutIcon");
			pass=false;
		}
		else
		{

			validateIconUrl();
		}
	}

	/**
	 * Test events actions_v1_01.
	 *
	 * @throws Exception the exception
	 */
	public  void testEventsActions_v1_01() throws Exception {

		logger.info("Executing the test");
		logger.info("Received announcement from device: "+deviceAboutAnnouncement.getDeviceId()
				+" app: "+deviceAboutAnnouncement.getAppId()+", bus: "
				+deviceAboutAnnouncement.getServiceName());

		List<String> objectPaths = getAllSeenIntrospectablObjectPaths();
		assertTrue("Looks like this object doesn't implement the: '" + getIntrospectableInterfaceName() + "' interface,"
				+ " even though it states it does in the announcement", objectPaths.size() > 0);

		logger.info("Object paths to be tested: "+objectPaths);
		for (String objectPath : objectPaths) {

			logger.info("==> Testing Announced Object Path: "+objectPath);

			assertTrue("The test of the Announced Object Path: '" + objectPath + "' has failed", testObjectValidity(objectPath));
			logger.info("==> The test of the Announced Object Path: "+objectPath+" passed successfully");
		}
	}

	/**
	 * Test object validity.
	 *
	 * @param objectPath the object path
	 * @return true, if successful
	 * @throws Exception the exception
	 */
	private  boolean testObjectValidity(String objectPath) throws Exception {

		logger.info("Testing Object Path: '%s'", objectPath);
		ProxyBusObject proxyObj = serviceHelper.getProxyBusObject(aboutClient, objectPath,
				new Class<?>[]{AllSeenIntrospectable.class});

		String[] descLangs = getDescriptionLanguages(proxyObj, objectPath);
		if ( descLangs.length == 0 ) {

			logger.warn("No description languages found for the Object Path: '%s'. Introspecting child objects with NO_LANGUAGE",
					objectPath);

			String introXML = getIntrospectionXML(proxyObj, objectPath, "NO_LANGUAGE");
			assertNotNull("Introspection XML is NULL Object Path: '" + objectPath + "'", introXML);
			return testChildrenObjectValidity(objectPath, introXML);
		}

		return testObjectValidityPerLanguages(proxyObj, objectPath, descLangs);
	}

	/**
	 * Verifies that for each description language the introspected XML contains a description tag.
	 * Verifies that introspection XMLs in different description languages are identical.
	 * The verification is performed after the description content is cut by the {@link EventsActionsTestSuite#removeXMLDesc(String)}
	 * method. Afterwards the verification algorithm is applied on the child objects by the call to the
	 * {@link EventsActionsTestSuite#testChildrenObjectValidity(String, String)}
	 * @param proxyObj {@link ProxyBusObject}
	 * @param parentObjectPath The object path of the parent object
	 * @param descLangs Description is supported on those languages
	 * @return TRUE whether parent XML or one of its child has a description tag.
	 * @throws Exception 
	 */
	private  boolean testObjectValidityPerLanguages(ProxyBusObject proxyObj, String parentObjectPath, String[] descLangs) throws Exception {

		logger.info("Found description languages: '%s' for the objectPath: '%s'", Arrays.toString(descLangs), parentObjectPath);

		String firstLangXML      = null;
		String firstLang         = null;
		boolean descriptionFound = false;

		for (String lang : descLangs) {

			String currentXML = getIntrospectionXML(proxyObj, parentObjectPath, lang);
			assertNotNull("Introspection XML is NULL Object Path: '" + parentObjectPath + "'", currentXML);

			//Print the introspection XML
			//logger.info("The introspection XML, the lang: '%s': '%s'", lang, currentXML);

			currentXML = removeXMLDesc(currentXML);

			logger.info("Testing language validity for the object path: '%s', language: '%s'", parentObjectPath, lang);

			if ( firstLangXML == null ) {

				assertTrue("The description tag wasn't found in the XML for the description language: '" + lang + "', " +
						"Object Path: '" + parentObjectPath + "'", currentXML.contains(INTROSPECTION_XML_DESC_EXPECTED));

				logger.info("The object '%s' contains a description tag in the language: '%s'", parentObjectPath, lang);

				if ( descLangs.length == 1 ) {

					logger.info("The object '%s' supports a single description language: '%s'", parentObjectPath, lang);
					return true;
				}

				firstLang        = lang;
				firstLangXML     = currentXML;
				descriptionFound = true;
				continue;
			}

			logger.info("Test identity of the XMLs in the first language: '%s' and the current language: '%s', " +
					"Object Path: '%s'", firstLang, lang, parentObjectPath);

			//Print the intospection XML in the first language and in the current language
			//logger.info("The expected XML in the first lang: '%s': '%s'", firstLang, firstLangXML);
			//logger.info("The tested XML in the current lang: '%s': '%s'", lang, currentXML);

			//If current language is not a first language, compare current language XML with the first language XML
			assertEquals("The XML in the first language: '" + firstLang + "' is not identical to the XML in the current language: '" +
					lang + "', object path: '" + parentObjectPath + "'", firstLangXML, currentXML);

			logger.info("The XMLs in the first language: '%s' and the current language: '%s', " +
					"Object Path: '%s' are identical", firstLang, lang, parentObjectPath);
		}//for :: descLangs

		testChildrenObjectValidity(parentObjectPath, firstLangXML);
		return descriptionFound;
	}



	/**
	 * This method removes the content of the XML description tags
	 * @param introspection
	 * @return Introspected XML without the description content
	 */
	private  String removeXMLDesc(String introspection) {

		return introspection.replaceAll(INTROSPECTION_XML_DESC_REGEX, INTROSPECTION_XML_DESC_PLACEHOLDER);
	}















	/**
	 * Parses parent's introspection XML and calls {@link EventsActionsTestSuite#testObjectValidity(String)} for each child object.
	 * @param parentObjectPath Parent object path that was introspected
	 * @param parentIntroXML Parent introspection XML
	 * @return TRUE whether at least one of the child objects has a description tag
	 * @throws Exception 
	 */
	private  boolean testChildrenObjectValidity(String parentObjectPath, String parentIntroXML) throws Exception {

		EvAcIntrospectionNode introspectNode = null;

		try {

			introspectNode = new EvAcIntrospectionNode(parentObjectPath);
			introspectNode.parse(parentIntroXML);
		} catch (Exception e) {

			logger.error("Failed to parse the introspection XML, object path: "+ parentObjectPath);
			logger.error("Error", e);
			fail("Failed to parse the introspection XML, object path: "+ parentObjectPath);
		}

		logger.info("Testing child objects of the parent object: '%s'", parentObjectPath);

		List<EvAcIntrospectionNode> childrenNodes = introspectNode.getChidren();
		boolean descFoundBroth                    = false;

		if ( childrenNodes == null || childrenNodes.size() == 0 ) {

			logger.warn("The object '%s' doesn't have any child object", parentObjectPath);
			return false;
		}

		for (EvAcIntrospectionNode childNode : introspectNode.getChidren()) {

			boolean descFoundChild = testObjectValidity(childNode.getPath());
			String logMsg          = descFoundChild ? "contains a description tag" : "doesn't contain any description tag";

			logger.info("The object or its offspring: '%s' %s", childNode.getPath(), logMsg);
			if ( !descFoundBroth ) {

				descFoundBroth = descFoundChild;
			}
		}

		String logMsg  = descFoundBroth ? "contain a description tag" : "doesn't contain any description tag";
		logger.info("Child objects of the parent: '%s' %s", parentObjectPath, logMsg);
		return descFoundBroth;
	}
	/**
	 * Retrieves the introspection XML
	 * @param proxyObj The {@link ProxyBusObject}
	 * @param objectPath Object path to be introspected
	 * @param language The language to query the introspection
	 * @return Introspection XML
	 */
	private  String getIntrospectionXML(ProxyBusObject proxyObj, String objectPath, String lang) {

		String introXML = null;

		try {
			introXML = proxyObj.getInterface(AllSeenIntrospectable.class).IntrospectWithDescription(lang);
		} catch (BusException be) {

			logger.error("Failed to call IntrospectWithDescription for the Object Path: "+ objectPath);
			logger.error("Error", be);
			fail("Failed to call IntrospectWithDescription for the Object Path: "+ objectPath);
		}

		return introXML;
	}


	/**
	 * Returns the supported description languages for the given object path
	 * @param proxyObj {@link ProxyBusObject}
	 * @param objectPath The object to be asked for the description languages
	 * @return Array of the description languages
	 */
	private  String[] getDescriptionLanguages(ProxyBusObject proxyObj, String objectPath) {

		String[] langs = new String[]{};

		try {
			langs = proxyObj.getInterface(AllSeenIntrospectable.class).GetDescriptionLanguages();
		} catch (BusException be) {

			logger.error("Failed to call GetDescriptionLanguages for the Object Path: "+ objectPath);
			logger.error("Error", be);
			fail("Failed to call GetDescriptionLanguages for the Object Path: "+ objectPath);
		}

		return langs;
	}



	/**
	 * Gets the allseen introspectableobject paths.
	 *
	 * @return the allseen introspectableobject paths
	 */
	protected  List<String> getAllSeenIntrospectablObjectPaths() {

		List<String> retList  = new ArrayList<String>();
		String introIfaceName = getIntrospectableInterfaceName();

		BusObjectDescription[] objDescs = deviceAboutAnnouncement.getObjectDescriptions();
		for (BusObjectDescription bod : objDescs) {

			String path = bod.path;

			for (String iface : bod.interfaces) {

				// The AllSeenIntrospectable interface was found => add the path to the returned list
				if ( iface.equals(introIfaceName) ) {
					retList.add(path);
				}
			}
		}

		return retList;
	}


	/**
	 * Gets the introspectable interface name.
	 *
	 * @return the introspectable interface name
	 */
	private  String getIntrospectableInterfaceName() {

		//Retrieve the AJ name of the introspection interface
		BusInterface ifaceName = AllSeenIntrospectable.class.getAnnotation(BusInterface.class);
		return ifaceName.name();
	}













	/**
	 * Validate icon url.
	 *
	 * @throws BusException the bus exception
	 */
	protected  void validateIconUrl() throws BusException
	{
		logger.info("Creating about client and testing the Icon url validity");
		String iconUrl = aboutIconClient.GetUrl();

		if (iconUrl == null || iconUrl.isEmpty())
		{
			logger.info("Url is empty");
			logger.addNote("URL returned is null/empty");
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
				logger.info("Icon URL is malformed ", malformedUrlException);
				exception = malformedUrlException;
				pass=false;
			}

			if(exception==null){
				logger.info("The received icon URL: '" + iconUrl + "' isn't valid");

			}
		}
		
		
	}


	/**
	 * Assert equals.
	 *
	 * @param msg the msg
	 * @param int1 the int1
	 * @param int2 the int2
	 */
	private  void assertEquals(String msg, int int1, int int2) {
		

		if(int2!=int1){

			fail(msg);

		}else{
			logger.info("Partial Verdict: PASS");
		}




	}


	/**
	 * Compare fields in about map.
	 *
	 * @param aboutMapDefaultLanguage the about map default language
	 * @param aboutMapNoLanguage the about map no language
	 * @param language the language
	 * @throws Exception the exception
	 */
	private  void compareFieldsInAboutMap(Map<String, Object> aboutMapDefaultLanguage, Map<String, Object> aboutMapNoLanguage, String language) throws Exception
	{
		compareAboutNonRequired(aboutMapDefaultLanguage, aboutMapNoLanguage, language, AboutKeys.ABOUT_DEVICE_NAME);
		compareAbout(AboutKeys.ABOUT_APP_NAME, aboutMapDefaultLanguage.get(AboutKeys.ABOUT_APP_NAME).toString(), aboutMapNoLanguage.get(AboutKeys.ABOUT_APP_NAME).toString(), language);
		compareAbout(AboutKeys.ABOUT_MANUFACTURER, aboutMapDefaultLanguage.get(AboutKeys.ABOUT_MANUFACTURER).toString(), aboutMapNoLanguage.get(AboutKeys.ABOUT_MANUFACTURER).toString(), language);
		compareAbout(AboutKeys.ABOUT_DESCRIPTION, aboutMapDefaultLanguage.get(AboutKeys.ABOUT_DESCRIPTION).toString(), aboutMapNoLanguage.get(AboutKeys.ABOUT_DESCRIPTION).toString(), language);

		compareNonLocalizedFieldsInAboutMap(aboutMapDefaultLanguage, aboutMapNoLanguage, language);
	}
	/**
	 * Assert true.
	 *
	 * @param msg the msg
	 * @param bool the bool
	 */
	private  void assertTrue(String msg, boolean bool) {
		

		if(!bool){

			fail(msg);

		}else{
			logger.info("Partial Verdict: PASS");
		}


	}



	/**
	 * Assert false.
	 *
	 * @param msg the msg
	 * @param bool the bool
	 */
	private  void assertFalse(String msg,
			boolean bool) {
		


		if(bool){

			fail(msg);
		}else{
			logger.info("Partial Verdict: PASS");
		}


	}






	/**
	 * Assert equals.
	 *
	 * @param msg the msg
	 * @param expectedValue the expected value
	 * @param currentValue the current value
	 */
	private  void assertEquals(String msg,
			String expectedValue, String currentValue) {
	
		if(!expectedValue.equals(currentValue)){


			fail(msg);

		}else{
			logger.info("Partial Verdict: PASS");
		}
		}

	/**
	 * Checks if is default language present.
	 *
	 * @param supportedLanguages the supported languages
	 * @return true, if is default language present
	 */
	private  boolean isDefaultLanguagePresent(String[] supportedLanguages)
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
	/**
	 * Validate supported languages contains default language.
	 *
	 * @param supportedLanguages the supported languages
	 */
	private  void validateSupportedLanguagesContainsDefaultLanguage(String[] supportedLanguages)
	{
		assertTrue("No supported language found in About announcement", supportedLanguages != null);
		assertTrue("No supported language found in About announcement", supportedLanguages.length > 0);
		assertTrue("Default language not found in supported languages list of About announcement", isDefaultLanguagePresent(supportedLanguages));
	}

	/**
	 * Validate supported languages about map.
	 *
	 * @param supportedLanguages the supported languages
	 * @param aboutMapDefaultLanguage the about map default language
	 * @throws BusException the bus exception
	 * @throws Exception the exception
	 */
	private  void validateSupportedLanguagesAboutMap(String[] supportedLanguages, Map<String, Object> aboutMapDefaultLanguage) throws BusException, Exception
	{
		
		for (String supportedLanguage : supportedLanguages)
		{
			if (!supportedLanguage.equals(defaultLanguage))
			{
				logger.info("Calling getAbout on About interface with lanuage " + supportedLanguage);
				Map<String, Object> aboutMapSupportedLanguage = aboutClient.getAbout(supportedLanguage);
				verifyAboutMap(aboutMapSupportedLanguage, supportedLanguage);
				compareNonLocalizedFieldsInAboutMap(aboutMapDefaultLanguage, aboutMapSupportedLanguage, supportedLanguage);
			}
		}
		
		
		
	}

	/**
	 * Compare non localized fields in about map.
	 *
	 * @param aboutMapDefaultLanguage the about map default language
	 * @param aboutMapSupportedLanguage the about map supported language
	 * @param language the language
	 * @throws Exception the exception
	 */
	private  void compareNonLocalizedFieldsInAboutMap(Map<String, Object> aboutMapDefaultLanguage, Map<String, Object> aboutMapSupportedLanguage, String language) throws Exception
	{
		compareRequiredFieldsInAboutMap(aboutMapDefaultLanguage, aboutMapSupportedLanguage, language);
		compareNonRequiredFieldsInAboutMap(aboutMapDefaultLanguage, aboutMapSupportedLanguage, language);
	}

	/**
	 * Compare required fields in about map.
	 *
	 * @param expectedAboutMap the expected about map
	 * @param aboutMap the about map
	 * @param language the language
	 * @throws Exception the exception
	 */
	private void compareRequiredFieldsInAboutMap(Map<String, Object> expectedAboutMap, Map<String, Object> aboutMap, String language) throws Exception
	{
		compareAbout(AboutKeys.ABOUT_APP_ID, expectedAboutMap.get(AboutKeys.ABOUT_APP_ID).toString(), aboutMap.get(AboutKeys.ABOUT_APP_ID).toString(), language);
		compareAbout(AboutKeys.ABOUT_DEFAULT_LANGUAGE, expectedAboutMap.get(AboutKeys.ABOUT_DEFAULT_LANGUAGE).toString(),
				aboutMap.get(AboutKeys.ABOUT_DEFAULT_LANGUAGE).toString(), language);
		compareAbout(AboutKeys.ABOUT_DEVICE_ID, expectedAboutMap.get(AboutKeys.ABOUT_DEVICE_ID).toString(), aboutMap.get(AboutKeys.ABOUT_DEVICE_ID).toString(), language);
		compareAbout(AboutKeys.ABOUT_MODEL_NUMBER, expectedAboutMap.get(AboutKeys.ABOUT_MODEL_NUMBER).toString(), aboutMap.get(AboutKeys.ABOUT_MODEL_NUMBER).toString(), language);
		assertTrue("The SupportedLanguages value returned from the GetAbout for language " + language + " does not match default language value",
				Arrays.equals((String[]) expectedAboutMap.get(AboutKeys.ABOUT_SUPPORTED_LANGUAGES), (String[]) aboutMap.get(AboutKeys.ABOUT_SUPPORTED_LANGUAGES)));
		compareAbout(AboutKeys.ABOUT_SOFTWARE_VERSION, expectedAboutMap.get(AboutKeys.ABOUT_SOFTWARE_VERSION).toString(),
				aboutMap.get(AboutKeys.ABOUT_SOFTWARE_VERSION).toString(), language);
		compareAbout(AboutKeys.ABOUT_AJ_SOFTWARE_VERSION, expectedAboutMap.get(AboutKeys.ABOUT_AJ_SOFTWARE_VERSION).toString(), aboutMap.get(AboutKeys.ABOUT_AJ_SOFTWARE_VERSION)
				.toString(), language);
	}

	/**
	 * Compare non required fields in about map.
	 *
	 * @param aboutMapDefaultLanguage the about map default language
	 * @param aboutMapSupportedLanguage the about map supported language
	 * @param language the language
	 * @throws Exception the exception
	 */
	private  void compareNonRequiredFieldsInAboutMap(Map<String, Object> aboutMapDefaultLanguage, Map<String, Object> aboutMapSupportedLanguage, String language) throws Exception
	{
		compareAboutNonRequired(aboutMapDefaultLanguage, aboutMapSupportedLanguage, language, AboutKeys.ABOUT_DATE_OF_MANUFACTURE);
		compareAboutNonRequired(aboutMapDefaultLanguage, aboutMapSupportedLanguage, language, AboutKeys.ABOUT_HARDWARE_VERSION);
		compareAboutNonRequired(aboutMapDefaultLanguage, aboutMapSupportedLanguage, language, AboutKeys.ABOUT_SUPPORT_URL);
	}

	/**
	 * Compare about non required.
	 *
	 * @param aboutMapDefaultLanguage the about map default language
	 * @param aboutMapSupportedLanguage the about map supported language
	 * @param language the language
	 * @param fieldName the field name
	 * @throws Exception the exception
	 */
	private  void compareAboutNonRequired(Map<String, Object> aboutMapDefaultLanguage, Map<String, Object> aboutMapSupportedLanguage, String language, String fieldName) throws Exception
	{
		if (aboutMapDefaultLanguage.containsKey(fieldName))
		{
			if (aboutMapSupportedLanguage.containsKey(fieldName))
			{
				compareAbout(fieldName, aboutMapDefaultLanguage.get(fieldName).toString(), aboutMapSupportedLanguage.get(fieldName).toString(), language);
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




	/**
	 * Compare about.
	 *
	 * @param fieldName the field name
	 * @param expectedAboutFieldValue the expected about field value
	 * @param aboutFieldValue the about field value
	 * @param language the language
	 * @throws Exception the exception
	 */
	private  void compareAbout(String fieldName, String expectedAboutFieldValue, String aboutFieldValue, String language) throws Exception
	{
		String assertionFailureResponse = prepareAssertionFailureResponse(fieldName,expectedAboutFieldValue,aboutFieldValue, language);
		if(language.equals("")){
			language="unspecified language";
		}


		if(expectedAboutFieldValue.equals(aboutFieldValue)){
			logger.info(fieldName+" received: "+aboutFieldValue+" is equal to IXITCO_"+fieldName+": "+expectedAboutFieldValue);
		}
		assertEquals(assertionFailureResponse, expectedAboutFieldValue, aboutFieldValue);
	}

	/**
	 * Prepare assertion failure response.
	 *
	 * @param fieldName the field name
	 * @param language the language
	 * @return the string
	 */
	private String prepareAssertionFailureResponse(String fieldName, String language)
	{
		StringBuilder assertionFailureResponseBuilder = new StringBuilder();
		assertionFailureResponseBuilder.append(fieldName);
		assertionFailureResponseBuilder.append(" value returned from the GetAbout for language ");
		assertionFailureResponseBuilder.append(language);
		assertionFailureResponseBuilder.append(" does not match default language value;");

		return assertionFailureResponseBuilder.toString();
	}


	/**
	 * Prepare assertion failure response.
	 *
	 * @param fieldName the field name
	 * @param ixit the ixit
	 * @param value the value
	 * @param language the language
	 * @return the string
	 */
	private  String prepareAssertionFailureResponse(String fieldName ,String ixit ,String value,String language)
	{
		StringBuilder assertionFailureResponseBuilder = new StringBuilder();
		assertionFailureResponseBuilder.append(fieldName);
		assertionFailureResponseBuilder.append(" value returned from the About announcement "+value);

		assertionFailureResponseBuilder.append(" does not match IXIT: "+ixit);

		return assertionFailureResponseBuilder.toString();
	}




	/*private  void populateMap(BusObjectDescription[] busObjectDescriptions, Map<String, String[]> objectDescriptionMap)
	{
		for (BusObjectDescription busObjectDescription : busObjectDescriptions)
		{
			objectDescriptionMap.put(busObjectDescription.path, busObjectDescription.interfaces);
		}
	}*/


	/**
	 * Populate map.
	 *
	 * @param aboutObjectDescriptions the about object descriptions
	 * @param objectDescriptionMap the object description map
	 */
	private  void populateMap(BusObjectDescription[] aboutObjectDescriptions, Map<String, String[]> objectDescriptionMap)
	{
		for (BusObjectDescription busObjectDescription : aboutObjectDescriptions)
		{
			objectDescriptionMap.put(busObjectDescription.path, busObjectDescription.interfaces);
		}
	}





	/**
	 * Release resources.
	 */
	private  void releaseResources()
	{
		if (serviceHelper != null) {
			serviceHelper.release();
			serviceHelper = null;
		}
	}

	/**
	 * Verify about data.
	 *
	 * @param aboutData the about data
	 * @throws Exception the exception
	 */
	private  void verifyAboutData(Map<String, Variant> aboutData) throws Exception
	{
		verifyFieldIsPresent(AboutKeys.ABOUT_APP_ID, aboutData);
		verifyFieldIsPresent(AboutKeys.ABOUT_DEFAULT_LANGUAGE, aboutData);
		//compareAbout(AboutKeys.ABOUT_DEFAULT_LANGUAGE,IXITCO_DefaultLanguage,aboutData.get(AboutKeys.ABOUT_DEFAULT_LANGUAGE).getObject(String.class),"");
		compareAbout(AboutKeys.ABOUT_DEFAULT_LANGUAGE,ixit.get("IXITCO_DefaultLanguage"),aboutData.get(AboutKeys.ABOUT_DEFAULT_LANGUAGE).getObject(String.class),"");

		if(ics.get("ICSCO_DeviceName")) {
			verifyFieldIsPresent(AboutKeys.ABOUT_DEVICE_NAME, aboutData);
			//compareAbout(AboutKeys.ABOUT_DEVICE_NAME,IXITCO_DeviceName,aboutData.get(AboutKeys.ABOUT_DEVICE_NAME).getObject(String.class),"");
			compareAbout(AboutKeys.ABOUT_DEVICE_NAME,ixit.get("IXITCO_DeviceName"),aboutData.get(AboutKeys.ABOUT_DEVICE_NAME).getObject(String.class),"");
		} //JTF: Need to add case ICSCO_DeviceName is false and field is present

		verifyFieldIsPresent(AboutKeys.ABOUT_DEVICE_ID, aboutData);
		//compareAbout(AboutKeys.ABOUT_DEVICE_ID,IXITCO_DeviceId,(String) aboutData.get(AboutKeys.ABOUT_DEVICE_ID).getObject(String.class),"");
		compareAbout(AboutKeys.ABOUT_DEVICE_ID,ixit.get("IXITCO_DeviceId"),(String) aboutData.get(AboutKeys.ABOUT_DEVICE_ID).getObject(String.class),"");

		verifyFieldIsPresent(AboutKeys.ABOUT_APP_NAME, aboutData);
		//compareAbout(AboutKeys.ABOUT_APP_NAME,IXITCO_AppName, aboutData.get(AboutKeys.ABOUT_APP_NAME).getObject(String.class),"");
		compareAbout(AboutKeys.ABOUT_APP_NAME,ixit.get("IXITCO_AppName"), aboutData.get(AboutKeys.ABOUT_APP_NAME).getObject(String.class),"");

		verifyFieldIsPresent(AboutKeys.ABOUT_MANUFACTURER, aboutData);
		//compareAbout(AboutKeys.ABOUT_MANUFACTURER,IXITCO_Manufacturer, aboutData.get(AboutKeys.ABOUT_MANUFACTURER).getObject(String.class),"");
		compareAbout(AboutKeys.ABOUT_MANUFACTURER,ixit.get("IXITCO_Manufacturer"), aboutData.get(AboutKeys.ABOUT_MANUFACTURER).getObject(String.class),"");

		verifyFieldIsPresent(AboutKeys.ABOUT_MODEL_NUMBER, aboutData);
		//compareAbout(AboutKeys.ABOUT_MODEL_NUMBER,IXITCO_ModelNumber, aboutData.get(AboutKeys.ABOUT_MODEL_NUMBER).getObject(String.class),"");
		compareAbout(AboutKeys.ABOUT_MODEL_NUMBER,ixit.get("IXITCO_ModelNumber"), aboutData.get(AboutKeys.ABOUT_MODEL_NUMBER).getObject(String.class),"");

	}


	/**
	 * Verify field is present.
	 *
	 * @param key the key
	 * @param aboutData the about data
	 * @throws BusException the bus exception
	 */
	private  void verifyFieldIsPresent(String key, Map<String, Variant> aboutData) throws BusException
	{
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






	/**
	 * Gets the about interface path from announcement.
	 *
	 * @return the about interface path from announcement
	 */
	private  String getAboutInterfacePathFromAnnouncement()
	{
		String aboutPath = null;

		for (BusObjectDescription busObjectDescription : deviceAboutAnnouncement.getObjectDescriptions())
		{
			for (String interfaceName : busObjectDescription.interfaces)
			{
				if (interfaceName.equals(AboutTransport.INTERFACE_NAME))
				{
					aboutPath = busObjectDescription.path;
				}
			}

			logger.info("BusObjectDescription: " + busObjectDescription.path + " supports " + Arrays.toString(busObjectDescription.interfaces));
		}
		return aboutPath;
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
	 */
	protected  BusIntrospector getIntrospector()
	{
		return serviceHelper.getBusIntrospector(aboutClient);
	}


	/**
	 * Populate bus introspect path interface set.
	 *
	 * @param busIntrospector the bus introspector
	 * @param busIntrospectPathInterfaceSet the bus introspect path interface set
	 * @param path the path
	 * @throws Exception the exception
	 */
	private  void populateBusIntrospectPathInterfaceSet(BusIntrospector busIntrospector, Set<String> busIntrospectPathInterfaceSet, String path) throws Exception
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

	/**
	 * Handle introspection bus exception.
	 *
	 * @param path the path
	 * @param e the e
	 * @throws Exception the exception
	 */
	private  void handleIntrospectionBusException(String path, BusException e) throws Exception
	{
		String msg = ERROR_MSG_BUS_INTROSPECTION;
		if (e instanceof ErrorReplyBusException && DBUS_ERROR_SERVICE_UNKNOWN.equals(((ErrorReplyBusException) e).getErrorName()))
		{
			msg = new StringBuilder("AboutAnnouncement has the path ").append(path).append(", but it is not found on the Bus Intropsection.").toString();
		}

		fail(msg);

		throw new Exception(msg, e);
	}

	/**
	 * Populate announcement path interface set.
	 *
	 * @param announcementPathInterfaceSet the announcement path interface set
	 * @param aboutObjectDescription the about object description
	 * @param path the path
	 */
	private  void populateAnnouncementPathInterfaceSet(Set<String> announcementPathInterfaceSet, BusObjectDescription aboutObjectDescription, String path)
	{
		for (String ifacename : aboutObjectDescription.interfaces)
		{
			String key = new StringBuilder(path).append(":").append(ifacename).toString();
			announcementPathInterfaceSet.add(key);
			String message = new StringBuilder("AboutAnnouncement contains interface ").append(ifacename).append(" at path ").append(path).toString();
			logger.info(message);
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
	protected  InterfaceValidator getInterfaceValidator() throws IOException, ParserConfigurationException, SAXException
	{
		return new InterfaceValidator();
	}



	/**
	 * Verify about map.
	 *
	 * @param aboutMap the about map
	 * @param language the language
	 * @throws Exception the exception
	 */
	private  void verifyAboutMap(Map<String, Object> aboutMap, String language) throws Exception
	{
		checkForNull(aboutMap, language);

		validateSignature(aboutMap, language);

		if(ics.get("ICSCO_DateOfManufacture")) {
			validateDateOfManufacture(aboutMap, language);
		}
		if(ics.get("ICSCO_SupportUrl")) {
			validateSupportUrl(aboutMap, language);
		}
	}


	/**
	 * Check for null.
	 *
	 * @param aboutMap the about map
	 * @param language the language
	 */
	private  void checkForNull(Map<String, Object> aboutMap, String language)
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
		if(ics.get("ICSCO_DateOfManufacture")) checkForNull(aboutMap, AboutKeys.ABOUT_DATE_OF_MANUFACTURE, language);
		if(ics.get("ICSCO_HardwareVersion")) checkForNull(aboutMap, AboutKeys.ABOUT_HARDWARE_VERSION, language);
		if(ics.get("ICSCO_SupportUrl")) checkForNull(aboutMap, AboutKeys.ABOUT_SUPPORT_URL, language);
	}

	
		
		/**
		 * Check for null.
		 *
		 * @param aboutMap the about map
		 * @param fieldName the field name
		 * @param language the language
		 */
		private void checkForNull(Map<String, Object> aboutMap, String fieldName, String language)
		{
			if(aboutMap.get(fieldName)==null){

				fail(fieldName + " is a required field for language: "+language);

			}else{logger.info(fieldName+" is present");
			if(language==ixit.get("IXITCO_DefaultLanguage")) {
				try {
					if(fieldName==AboutKeys.ABOUT_SUPPORTED_LANGUAGES) {
						//compareAbout(fieldName,ixit.get("IXITCO_"+fieldName),(String) aboutMap.get(fieldName).getObject(String.class),"");
					} else {
						compareAbout(fieldName,ixit.get("IXITCO_"+fieldName),(String) aboutMap.get(fieldName).toString(),"");
					}
				} catch (BusException e) {
					logger.error(e.getMessage());
				} catch (Exception e) {
					logger.error(e.getMessage());
				}
			}
		}
	}


	/**
	 * Validate signature.
	 *
	 * @param aboutMap the about map
	 * @param language the language
	 * @throws AnnotationBusException the annotation bus exception
	 */
	private  void validateSignature(Map<String, Object> aboutMap, String language) throws AnnotationBusException
	{
		Map<String, Variant> aboutVariantMap = TransportUtil.toVariantMap(aboutMap);
		
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

	/**
	 * Validate signature for non required fields.
	 *
	 * @param language the language
	 * @param aboutVariantMap the about variant map
	 * @throws AnnotationBusException the annotation bus exception
	 */
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

	/**
	 * Validate signature.
	 *
	 * @param aboutVariantMap the about variant map
	 * @param fieldName the field name
	 * @param signature the signature
	 * @param language the language
	 * @throws AnnotationBusException the annotation bus exception
	 */
	private  void validateSignature(Map<String, Variant> aboutVariantMap, String fieldName, String signature, String language) throws AnnotationBusException
	{
		if(!signature.equals(aboutVariantMap.get(fieldName).getSignature())){

			fail("Signature does not match for required field: "+fieldName+" for language "+language);
			//logger.error(signature+"!="+aboutVariantMap.get(fieldName).getSignature());


		}else{
			logger.info("Signature matches for required field "+fieldName+" for language "+language);
			logger.info("Partial Verdict: PASS");
		}

		
		
		
	}

	/**
	 * Validate date of manufacture.
	 *
	 * @param aboutMap the about map
	 * @param language the language
	 * @throws BusException the bus exception
	 */
	private  void validateDateOfManufacture(Map<String, Object> aboutMap, String language) throws BusException
	{

		if (aboutMap.containsKey(AboutKeys.ABOUT_DATE_OF_MANUFACTURE))
		{
			simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);
			simpleDateFormat.setLenient(false);

			String dateOfManufacture = aboutMap.get(AboutKeys.ABOUT_DATE_OF_MANUFACTURE).toString();
			logger.info("Validating Date Of Manufacture: "+dateOfManufacture);

			if(!isValidDate(dateOfManufacture)){
				fail(String.format("DateOfManufacture field value %s does not match expected date pattern YYYY-MM-DD", dateOfManufacture));
			}

		}
	}



	/**
	 * Validate support url.
	 *
	 * @param aboutMap the about map
	 * @param language the language
	 * @throws BusException the bus exception
	 */
	private  void validateSupportUrl(Map<String, Object> aboutMap, String language) throws BusException
	{
		if (aboutMap.containsKey(AboutKeys.ABOUT_SUPPORT_URL))
		{
			logger.info("Validating Support Url: "+aboutMap.get(AboutKeys.ABOUT_SUPPORT_URL).toString());
			String supportUrl = aboutMap.get(AboutKeys.ABOUT_SUPPORT_URL).toString();
			if(!isValidUrl(supportUrl)){

				fail(String.format("%s is not a valid URL", supportUrl));
			}

		}
	}


	/**
	 * Checks if is valid date.
	 *
	 * @param date the date
	 * @return true, if is valid date
	 */
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



	/**
	 * Checks if is valid url.
	 *
	 * @param supportUrl the support url
	 * @return true, if is valid url
	 */
	boolean isValidUrl(String supportUrl)
	{
		boolean isValid = true;
		URL url;
		try {
			url = new URL(supportUrl);
			url.toURI();
		} catch (MalformedURLException|URISyntaxException e1) {
			logger.debug(e1.getMessage());
			isValid = false;			
			e1.printStackTrace();
		}
		return isValid;
	}

	/**
	 * Gets the pass.
	 *
	 * @return the pass
	 */
	public boolean getPass() {
		return pass;
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

	/**
	 * Assert not null.
	 *
	 * @param errorMsg the error msg
	 * @param notNull the not null
	 */
	private  void assertNotNull(String errorMsg, Object notNull) {

		if(notNull==null){
			fail(errorMsg);
		}else{
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

		if(inconcluse){
			verdict="INCONC";
		}else if(pass){
			verdict="PASS";
		}else if(!pass){
			verdict="FAIL";
		}
		return verdict;
	}
}
