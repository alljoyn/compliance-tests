/*******************************************************************************
 *  Copyright (c) 2013 - 2014, AllSeen Alliance. All rights reserved.
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
package org.alljoyn.validation.testing.suites.about;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

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
import org.alljoyn.bus.Variant;
import org.alljoyn.services.android.utils.AndroidLogger;
import org.alljoyn.services.common.BusObjectDescription;
import org.alljoyn.services.common.utils.TransportUtil;
import org.alljoyn.validation.framework.AppUnderTestDetails;
import org.alljoyn.validation.framework.ValidationBaseTestCase;
import org.alljoyn.validation.framework.annotation.ValidationSuite;
import org.alljoyn.validation.framework.annotation.ValidationTest;
import org.alljoyn.validation.framework.utils.introspection.BusIntrospector;
import org.alljoyn.validation.framework.utils.introspection.bean.InterfaceDetail;
import org.alljoyn.validation.framework.utils.introspection.bean.IntrospectionInterface;
import org.alljoyn.validation.framework.utils.introspection.bean.IntrospectionNode;
import org.alljoyn.validation.framework.utils.introspection.bean.NodeDetail;
import org.alljoyn.validation.testing.utils.InterfaceValidator;
import org.alljoyn.validation.testing.utils.ValidationResult;
import org.alljoyn.validation.testing.utils.about.AboutAnnouncementDetails;
import org.alljoyn.validation.testing.utils.audio.MediaType;
import org.alljoyn.validation.testing.utils.log.Logger;
import org.alljoyn.validation.testing.utils.log.LoggerFactory;
import org.alljoyn.validation.testing.utils.services.ServiceHelper;
import org.xml.sax.SAXException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

@ValidationSuite(name = "About-v1")
public class AboutTestSuite extends ValidationBaseTestCase
{
    protected static final String TAG = "AboutTestSuite";
    private static final Logger logger = LoggerFactory.getLogger(TAG);
    private static final String BUS_APPLICATION_NAME = "AboutTestSuite";
    private static final String ERROR_MSG_BUS_INTROSPECTION = "Encountered exception while trying to introspect the bus";
    private static final String DBUS_ERROR_SERVICE_UNKNOWN = "org.freedesktop.DBus.Error.ServiceUnknown";
    private String defaultLanguage;
    private AboutClient aboutClient;
    private AboutIconClient aboutIconClient;
    protected AboutAnnouncementDetails deviceAboutAnnouncement;
    private ServiceHelper serviceHelper;
    private SimpleDateFormat simpleDateFormat;
    private static final long ANNOUCEMENT_TIMEOUT_IN_SECONDS = 30;
    private static final String DATE_FORMAT = "yyyy-MM-dd";

    private AppUnderTestDetails appUnderTestDetails;
    private UUID dutAppId;
    private String dutDeviceId;

    public AboutTestSuite()
    {
        simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);
        simpleDateFormat.setLenient(false);
    }

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        logger.debug("test setUp started");

        try
        {
            appUnderTestDetails = getValidationTestContext().getAppUnderTestDetails();
            dutDeviceId = appUnderTestDetails.getDeviceId();
            logger.debug("Running About test case against Device ID: %s", dutDeviceId);
            dutAppId = appUnderTestDetails.getAppId();
            logger.debug("Running About test case against App ID: %s", dutAppId);

            serviceHelper = getServiceHelper();
            serviceHelper.initialize(BUS_APPLICATION_NAME, dutDeviceId, dutAppId);

            deviceAboutAnnouncement = serviceHelper.waitForNextDeviceAnnouncement(ANNOUCEMENT_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS);
            assertNotNull("Timed out waiting for About announcement", deviceAboutAnnouncement);

            defaultLanguage = deviceAboutAnnouncement.getDefaultLanguage();
            aboutClient = serviceHelper.connectAboutClient(deviceAboutAnnouncement);
            aboutIconClient = serviceHelper.connectAboutIconClient(deviceAboutAnnouncement);

            logger.debug("Session established");
            logger.debug("test setUp done");
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

    @Override
    protected void tearDown() throws Exception
    {
        super.tearDown();
        logger.debug("test tearDown started");
        releaseResources();
        logger.debug("test tearDown done");
    }

    @ValidationTest(name = "About-v1-01")
    public void testAbout_v1_01_AboutAnnouncement() throws Exception
    {
        String aboutPath = getAboutInterfacePathFromAnnouncement();
        assertNotNull("About interface not present in announcement", aboutPath);
        assertEquals("About interface present at the wrong path", AboutTransport.OBJ_PATH, aboutPath);

        verifyAboutData(deviceAboutAnnouncement.getAboutData());
    }

    @ValidationTest(name = "About-v1-02")
    public void testAbout_v1_02_AboutVersion() throws Exception
    {
        assertEquals("About version does not match", 1, aboutClient.getVersion());
    }

    @ValidationTest(name = "About-v1-03")
    public void testAbout_v1_03_GetObjectDescription() throws Exception
    {
        Map<String, String[]> aboutObjectDescriptionMap = new HashMap<String, String[]>();
        Map<String, String[]> announcementObjectDescriptionMap = new HashMap<String, String[]>();
        populateMap(deviceAboutAnnouncement.getObjectDescriptions(), announcementObjectDescriptionMap);
        populateMap(aboutClient.getBusObjectDescriptions(), aboutObjectDescriptionMap);
        assertEquals("GetObjectDescription does not contain the same set of paths as the announcement", aboutObjectDescriptionMap.keySet(),
                announcementObjectDescriptionMap.keySet());

        for (String key : aboutObjectDescriptionMap.keySet())
        {
            assertTrue("GetObjectDescription not consistent for " + key, Arrays.equals(aboutObjectDescriptionMap.get(key), announcementObjectDescriptionMap.get(key)));
        }
    }

    @ValidationTest(name = "About-v1-04")
    public void testAbout_v1_04_AboutAnnouncementConsistentWithBusObjects() throws Exception
    {
        BusIntrospector busIntrospector = getIntrospector();

        Set<String> announcementPathInterfaceSet = new HashSet<String>();
        Set<String> busIntrospectPathInterfaceSet = new HashSet<String>();

        for (BusObjectDescription busObjectDescription : deviceAboutAnnouncement.getObjectDescriptions())
        {
            String path = busObjectDescription.getPath();
            populateAnnouncementPathInterfaceSet(announcementPathInterfaceSet, busObjectDescription, path);
            populateBusIntrospectPathInterfaceSet(busIntrospector, busIntrospectPathInterfaceSet, path);
        }

        for (String announcementKey : announcementPathInterfaceSet)
        {
            String[] pathAndInterfaces = announcementKey.split(":");
            String errorMessage = new StringBuilder("AboutAnnouncement advertises interface ").append(pathAndInterfaces[1]).append(" at path ").append(pathAndInterfaces[0])
                    .append(", but bus does not contain such interface at that path.").toString();
            assertTrue(errorMessage, busIntrospectPathInterfaceSet.contains(announcementKey));
        }
    }

    @ValidationTest(name = "About-v1-05")
    public void testAbout_v1_05_StandardizedInterfacesMatchDefinitions() throws Exception
    {
        List<InterfaceDetail> standardizedIntrospectionInterfacesExposedOnBus = getIntrospector().getStandardizedInterfacesExposedOnBus();
        for (InterfaceDetail objectDetail : standardizedIntrospectionInterfacesExposedOnBus)
        {
            for (IntrospectionInterface intfName : objectDetail.getIntrospectionInterfaces())
            {
                logger.debug(String.format("Found object at %s implementing %s", objectDetail.getPath(), intfName.getName()));
            }
        }
        ValidationResult validationResult = getInterfaceValidator().validate(standardizedIntrospectionInterfacesExposedOnBus);

        assertTrue(validationResult.getFailureReason(), validationResult.isValid());
    }

    @ValidationTest(name = "About-v1-06")
    public void testAbout_v1_06_GetAboutForDefaultLanguage() throws Exception
    {
        logger.debug("Calling getAbout on About interface with lanuage " + defaultLanguage);
        Map<String, Object> aboutMap = aboutClient.getAbout(defaultLanguage);
        logger.debug("Checking that all required fields are present");
        verifyAboutMap(aboutMap, defaultLanguage);
    }

    @ValidationTest(name = "About-v1-07")
    public void testAbout_v1_07_GetAboutForSupportedLanguages() throws Exception
    {
        logger.debug("Calling getAbout on About interface with lanuage " + defaultLanguage);
        Map<String, Object> aboutMapDefaultLanguage = aboutClient.getAbout(defaultLanguage);
        String[] supportedLanguages = (String[]) aboutMapDefaultLanguage.get(AboutKeys.ABOUT_SUPPORTED_LANGUAGES);
        validateSupportedLanguagesContainsDefaultLanguage(supportedLanguages);

        if (supportedLanguages.length == 1)
        {
            getValidationTestContext().addNote("Device only supports one language");
        }
        else
        {
            validateSupportedLanguagesAboutMap(supportedLanguages, aboutMapDefaultLanguage);
        }
    }

    @ValidationTest(name = "About-v1-08")
    public void testAbout_v1_08_GetAboutForUnspecifiedLanguage() throws Exception
    {
        logger.debug("Calling getAbout on About interface");
        Map<String, Object> aboutMapDefaultLanguage = aboutClient.getAbout(defaultLanguage);
        Map<String, Object> aboutMapNoLanguage = aboutClient.getAbout("");
        compareFieldsInAboutMap(aboutMapDefaultLanguage, aboutMapNoLanguage, "");
    }

    @ValidationTest(name = "About-v1-09")
    public void testAbout_v1_09_GetAboutForUnsupportedLanguage() throws Exception
    {
        boolean exceptionThrown = false;
        logger.debug("Calling getAbout on About interface with an unsupported language INVALID");
        try
        {
            aboutClient.getAbout("INVALID");
        }
        catch (ErrorReplyBusException ex)
        {
            exceptionThrown = true;
            assertEquals("Wrong errorName", "org.alljoyn.Error.LanguageNotSupported", ex.getErrorName());
        }
        assertTrue("Calling getAbout on the About interface with an unsupported language must throw an exception", exceptionThrown);
    }

    @ValidationTest(name = "About-v1-10")
    public void testAbout_v1_10_GetAboutIcon() throws Exception
    {
        if (!deviceAboutAnnouncement.supportsInterface(IconTransport.INTERFACE_NAME))
        {
            getValidationTestContext().addNote("Device does not support AboutIcon");
        }
        else
        {
            logger.debug("Calling get About Icon retrieve Icon data");
            String mimeType = aboutIconClient.getMimeType();
            int iconSize = aboutIconClient.getSize();

            if (mimeType == null || mimeType.isEmpty())
            {
                logger.debug("mimetype is empty");
                getValidationTestContext().addNote("GetContent() method is not supported");
            }
            else if (iconSize == 0)
            {
                logger.debug("The icon size is zero");
                getValidationTestContext().addNote("GetContent() method is not supported");
            }
            else
            {
                assertTrue("Mime type should match pattern image/*", mimeType.startsWith(MediaType.ImagePrefix.getValue()));
                assertTrue("Icon size should be less than " + BusAttachment.ALLJOYN_MAX_ARRAY_LEN, iconSize < BusAttachment.ALLJOYN_MAX_ARRAY_LEN);

                byte[] iconContent = aboutIconClient.GetContent();
                assertEquals("Size of GetContent does not match iconSize", iconSize, iconContent.length);
                Bitmap iconBitmap = BitmapFactory.decodeByteArray(iconContent, 0, iconSize);
                logger.debug("The IconBitMap size: '" + iconSize + "', Icon Height: '" + iconBitmap.getHeight() + "', Icon Width: '" + iconBitmap.getWidth() + "', toString(): "
                        + iconBitmap);
            }
        }
    }

    @ValidationTest(name = "About-v1-11")
    public void testAbout_v1_11_GetAboutIconValidUrl() throws Exception
    {
        if (!deviceAboutAnnouncement.supportsInterface(IconTransport.INTERFACE_NAME))
        {
            getValidationTestContext().addNote("Device does not support AboutIcon");
        }
        else
        {
            validateIconUrl();
        }
    }

    protected void validateIconUrl() throws BusException
    {
        logger.debug("Creating about client and testing the Icon url validity");
        String iconUrl = aboutIconClient.GetUrl();

        if (iconUrl == null || iconUrl.isEmpty())
        {
            logger.debug("Url is empty");
            getValidationTestContext().addNote("URL returned is null/empty");
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
                logger.debug("Icon URL is malformed ", malformedUrlException);
                exception = malformedUrlException;
            }
            assertNull("The received icon URL: '" + iconUrl + "' isn't valid", exception);
        }
    }

    protected BusIntrospector getIntrospector()
    {
        return serviceHelper.getBusIntrospector(aboutClient);
    }

    protected InterfaceValidator getInterfaceValidator() throws IOException, ParserConfigurationException, SAXException
    {
        return new InterfaceValidator(getValidationTestContext());
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

    private void releaseResources()
    {
        disconnectFromAboutClient();
        disconnectFromAboutIconClient();
        if (serviceHelper != null)
        {
            serviceHelper.release();
            serviceHelper = null;
        }
    }

    private void disconnectFromAboutClient()
    {
        if (aboutClient != null)
        {
            aboutClient.disconnect();
            aboutClient = null;
        }
    }

    private void disconnectFromAboutIconClient()
    {
        if (aboutIconClient != null)
        {
            aboutIconClient.disconnect();
            aboutIconClient = null;
        }
    }

    private String getAboutInterfacePathFromAnnouncement()
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

            logger.debug("BusObjectDescription: " + busObjectDescription.getPath() + " supports " + Arrays.toString(busObjectDescription.interfaces));
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

    private void validateSupportedLanguagesAboutMap(String[] supportedLanguages, Map<String, Object> aboutMapDefaultLanguage) throws BusException, Exception
    {
        for (String supportedLanguage : supportedLanguages)
        {
            if (!supportedLanguage.equals(defaultLanguage))
            {
                logger.debug("Calling getAbout on About interface with lanuage " + supportedLanguage);
                Map<String, Object> aboutMapSupportedLanguage = aboutClient.getAbout(supportedLanguage);
                verifyAboutMap(aboutMapSupportedLanguage, supportedLanguage);
                compareNonLocalizedFieldsInAboutMap(aboutMapDefaultLanguage, aboutMapSupportedLanguage, supportedLanguage);
            }
        }
    }

    private void populateMap(BusObjectDescription[] busObjectDescriptions, Map<String, String[]> objectDescriptionMap)
    {
        for (BusObjectDescription busObjectDescription : busObjectDescriptions)
        {
            objectDescriptionMap.put(busObjectDescription.path, busObjectDescription.interfaces);
        }
    }

    private void verifyAboutData(Map<String, Variant> aboutData)
    {
        verifyFieldIsPresent(AboutKeys.ABOUT_APP_ID, aboutData);
        verifyFieldIsPresent(AboutKeys.ABOUT_DEFAULT_LANGUAGE, aboutData);
        verifyFieldIsPresent(AboutKeys.ABOUT_DEVICE_NAME, aboutData);
        verifyFieldIsPresent(AboutKeys.ABOUT_DEVICE_ID, aboutData);
        verifyFieldIsPresent(AboutKeys.ABOUT_APP_NAME, aboutData);
        verifyFieldIsPresent(AboutKeys.ABOUT_MANUFACTURER, aboutData);
        verifyFieldIsPresent(AboutKeys.ABOUT_MODEL_NUMBER, aboutData);
    }

    private void verifyFieldIsPresent(String key, Map<String, Variant> aboutData)
    {
        assertNotNull(key + " is a required field", aboutData.get(key));
    }

    private void verifyAboutMap(Map<String, Object> aboutMap, String language) throws Exception
    {
        checkForNull(aboutMap, language);
        validateSignature(aboutMap, language);
        validateDateOfManufacture(aboutMap, language);
        validateSupportUrl(aboutMap, language);
    }

    private void checkForNull(Map<String, Object> aboutMap, String language)
    {
        checkForNull(aboutMap, AboutKeys.ABOUT_APP_ID, language);
        checkForNull(aboutMap, AboutKeys.ABOUT_DEFAULT_LANGUAGE, language);
        checkForNull(aboutMap, AboutKeys.ABOUT_DEVICE_NAME, language);
        checkForNull(aboutMap, AboutKeys.ABOUT_DEVICE_ID, language);
        checkForNull(aboutMap, AboutKeys.ABOUT_APP_NAME, language);
        checkForNull(aboutMap, AboutKeys.ABOUT_MANUFACTURER, language);
        checkForNull(aboutMap, AboutKeys.ABOUT_MODEL_NUMBER, language);
        checkForNull(aboutMap, AboutKeys.ABOUT_SUPPORTED_LANGUAGES, language);
        checkForNull(aboutMap, AboutKeys.ABOUT_DESCRIPTION, language);
        checkForNull(aboutMap, AboutKeys.ABOUT_SOFTWARE_VERSION, language);
        checkForNull(aboutMap, AboutKeys.ABOUT_AJ_SOFTWARE_VERSION, language);
    }

    private void checkForNull(Map<String, Object> aboutMap, String fieldName, String language)
    {
        assertNotNull(fieldName + " is a required field for language " + language, aboutMap.get(fieldName));
    }

    private void validateSignature(Map<String, Object> aboutMap, String language) throws AnnotationBusException
    {
        Map<String, Variant> aboutVariantMap = TransportUtil.toVariantMap(aboutMap);
        validateSignature(aboutVariantMap, AboutKeys.ABOUT_APP_ID, "ay", language);
        validateSignature(aboutVariantMap, AboutKeys.ABOUT_DEFAULT_LANGUAGE, "s", language);
        validateSignature(aboutVariantMap, AboutKeys.ABOUT_DEVICE_NAME, "s", language);
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
        assertEquals(String.format("Signature does not match for required field %s for language %s", fieldName, language), signature, aboutVariantMap.get(fieldName).getSignature());
    }

    private void validateDateOfManufacture(Map<String, Object> aboutMap, String language)
    {
        if (aboutMap.containsKey(AboutKeys.ABOUT_DATE_OF_MANUFACTURE))
        {
            String dateOfManufacture = aboutMap.get(AboutKeys.ABOUT_DATE_OF_MANUFACTURE).toString();
            assertTrue(String.format("DateOfManufacture field value %s does not match expected date pattern YYYY-MM-DD", dateOfManufacture), isValidDate(dateOfManufacture));
        }
    }

    private void validateSupportUrl(Map<String, Object> aboutMap, String language)
    {
        if (aboutMap.containsKey(AboutKeys.ABOUT_SUPPORT_URL))
        {
            String supportUrl = aboutMap.get(AboutKeys.ABOUT_SUPPORT_URL).toString();
            assertTrue(String.format("%s is not a valid URL", supportUrl), isValidUrl(supportUrl));
        }
    }

    private void compareFieldsInAboutMap(Map<String, Object> expectedAboutMap, Map<String, Object> aboutMap, String language) throws Exception
    {
        compareAbout(AboutKeys.ABOUT_DEVICE_NAME, expectedAboutMap.get(AboutKeys.ABOUT_DEVICE_NAME).toString(), aboutMap.get(AboutKeys.ABOUT_DEVICE_NAME).toString(), language);
        compareAbout(AboutKeys.ABOUT_APP_NAME, expectedAboutMap.get(AboutKeys.ABOUT_APP_NAME).toString(), aboutMap.get(AboutKeys.ABOUT_APP_NAME).toString(), language);
        compareAbout(AboutKeys.ABOUT_MANUFACTURER, expectedAboutMap.get(AboutKeys.ABOUT_MANUFACTURER).toString(), aboutMap.get(AboutKeys.ABOUT_MANUFACTURER).toString(), language);
        compareAbout(AboutKeys.ABOUT_DESCRIPTION, expectedAboutMap.get(AboutKeys.ABOUT_DESCRIPTION).toString(), aboutMap.get(AboutKeys.ABOUT_DESCRIPTION).toString(), language);

        compareNonLocalizedFieldsInAboutMap(expectedAboutMap, aboutMap, language);
    }

    private void compareNonLocalizedFieldsInAboutMap(Map<String, Object> expectedAboutMap, Map<String, Object> aboutMap, String language) throws Exception
    {
        compareRequiredFieldsInAboutMap(expectedAboutMap, aboutMap, language);
        compareNonRequiredFieldsInAboutMap(expectedAboutMap, aboutMap, language);
    }

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

    private void compareNonRequiredFieldsInAboutMap(Map<String, Object> expectedAboutMap, Map<String, Object> aboutMap, String language) throws Exception
    {
        compareAboutNonRequired(expectedAboutMap, aboutMap, language, AboutKeys.ABOUT_DATE_OF_MANUFACTURE);
        compareAboutNonRequired(expectedAboutMap, aboutMap, language, AboutKeys.ABOUT_HARDWARE_VERSION);
        compareAboutNonRequired(expectedAboutMap, aboutMap, language, AboutKeys.ABOUT_SUPPORT_URL);
    }

    private void compareAboutNonRequired(Map<String, Object> expectedAboutMap, Map<String, Object> aboutMap, String language, String fieldName) throws Exception
    {
        if (expectedAboutMap.containsKey(fieldName))
        {
            if (aboutMap.containsKey(fieldName))
            {
                compareAbout(fieldName, expectedAboutMap.get(fieldName).toString(), aboutMap.get(fieldName).toString(), language);
            }
            else
            {
                fail(prepareAssertionFailureResponse(fieldName, language));
            }
        }
        else
        {
            assertFalse(prepareAssertionFailureResponse(fieldName, language), aboutMap.containsKey(fieldName));
        }
    }

    private void compareAbout(String fieldName, String expectedAboutFieldValue, String aboutFieldValue, String language) throws Exception
    {
        String assertionFailureResponse = prepareAssertionFailureResponse(fieldName, language);
        assertEquals(assertionFailureResponse, expectedAboutFieldValue, aboutFieldValue);
    }

    private String prepareAssertionFailureResponse(String fieldName, String language)
    {
        StringBuilder assertionFailureResponseBuilder = new StringBuilder();
        assertionFailureResponseBuilder.append(fieldName);
        assertionFailureResponseBuilder.append(" value returned from the GetAbout for language ");
        assertionFailureResponseBuilder.append(language);
        assertionFailureResponseBuilder.append(" does not match default language value;");

        return assertionFailureResponseBuilder.toString();
    }

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
            logger.error("Encountered exception while trying to parse the introspection xml", ex);
            throw new Exception("Encountered exception while trying to parse the introspection xml", ex);
        }

        IntrospectionNode introspectionNode = nodeDetail.getIntrospectionNode();

        for (IntrospectionInterface introspectionInterface : introspectionNode.getInterfaces())
        {
            String ifacename = introspectionInterface.getName();
            String key = new StringBuilder(path).append(":").append(ifacename).toString();
            busIntrospectPathInterfaceSet.add(key);
            String message = new StringBuilder("Bus Introspection contains interface ").append(ifacename).append(" at path ").append(path).toString();
            logger.debug(message);
        }
    }

    private void handleIntrospectionBusException(String path, BusException e) throws Exception
    {
        String msg = ERROR_MSG_BUS_INTROSPECTION;
        if (e instanceof ErrorReplyBusException && DBUS_ERROR_SERVICE_UNKNOWN.equals(((ErrorReplyBusException) e).getErrorName()))
        {
            msg = new StringBuilder("AboutAnnouncement has the path ").append(path).append(", but it is not found on the Bus Intropsection.").toString();
        }
        logger.error(msg, e);
        throw new Exception(msg, e);
    }

    private void populateAnnouncementPathInterfaceSet(Set<String> announcementPathInterfaceSet, BusObjectDescription busObjDescription, String path)
    {
        for (String ifacename : busObjDescription.getInterfaces())
        {
            String key = new StringBuilder(path).append(":").append(ifacename).toString();
            announcementPathInterfaceSet.add(key);
            String message = new StringBuilder("AboutAnnouncement contains interface ").append(ifacename).append(" at path ").append(path).toString();
            logger.debug(message);
        }
    }

    protected ServiceHelper getServiceHelper()
    {
        return new ServiceHelper(new AndroidLogger());
    }
}