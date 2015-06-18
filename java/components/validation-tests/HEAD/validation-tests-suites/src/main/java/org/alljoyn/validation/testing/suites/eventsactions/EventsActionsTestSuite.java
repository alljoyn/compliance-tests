/*******************************************************************************
 *     Copyright (c) Open Connectivity Foundation (OCF) and AllJoyn Open
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

package org.alljoyn.validation.testing.suites.eventsactions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.alljoyn.about.client.AboutClient;
import org.alljoyn.bus.BusException;
import org.alljoyn.bus.ProxyBusObject;
import org.alljoyn.bus.annotation.BusInterface;
import org.alljoyn.bus.ifaces.AllSeenIntrospectable;
import org.alljoyn.services.android.utils.AndroidLogger;
import org.alljoyn.services.common.BusObjectDescription;
import org.alljoyn.validation.framework.AppUnderTestDetails;
import org.alljoyn.validation.framework.ValidationBaseTestCase;
import org.alljoyn.validation.framework.annotation.ValidationSuite;
import org.alljoyn.validation.framework.annotation.ValidationTest;
import org.alljoyn.validation.testing.utils.about.AboutAnnouncementDetails;
import org.alljoyn.validation.testing.utils.log.Logger;
import org.alljoyn.validation.testing.utils.log.LoggerFactory;
import org.alljoyn.validation.testing.utils.services.ServiceHelper;

/**
 * The class tests Events & Actions core functionality
 */
@ValidationSuite(name = "EventsActions-v1")
public class EventsActionsTestSuite extends ValidationBaseTestCase {

    private static final String TAG    = "EventsActionsTestSuite";
    private static final Logger logger = LoggerFactory.getLogger(TAG);

    private static final String BUS_APPLICATION_NAME                 = "EventsActions";

    /**
     * Time to wait for an Announcement signal to arrive
     */
    private static final long ANNOUNCEMENT_TIMEOUT_IN_SECONDS        = 30;

    /**
     * This regular expression is used to replace description tags with the INTROSPECTION_XML_DESC_PLACEHOLDER
     */
    private static final String INTROSPECTION_XML_DESC_REGEX         = "(<description>).*(</description>.*)";

    /**
     * This placeholder is used to change the description tags in the introspected XML
     */
    private static final String INTROSPECTION_XML_DESC_PLACEHOLDER   = "$1$2";

    /**
     * The expected result after the introspection XML will be modified as a result of applying the
     * {@link EventsActionsTestSuite#INTROSPECTION_XML_DESC_REGEX}
     */
    private static final String INTROSPECTION_XML_DESC_EXPECTED      = "<description></description>";
     /**
      * {@link ServiceHelper} object with utilities methods
      */
    private ServiceHelper serviceHelper;

    /**
     * The content of the received Announcement signal
     */
    private AboutAnnouncementDetails deviceAboutAnnouncement;

    /**
    * Announcement time out
    */
    private static final long ANNOUCEMENT_TIMEOUT_IN_SECONDS = 60;

    private AboutClient aboutClient;
    private UUID dutAppId;
    private String dutDeviceId;
    private AppUnderTestDetails appUnderTestDetails;


     @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        logger.debug("test setUp started");

        try
        {
            appUnderTestDetails = getValidationTestContext().getAppUnderTestDetails();
            dutDeviceId = appUnderTestDetails.getDeviceId();
            logger.debug("Running EventsActions test case against Device ID: %s", dutDeviceId);
            dutAppId = appUnderTestDetails.getAppId();
            logger.debug("Running EventsActions test case against App ID: %s", dutAppId);

            serviceHelper = getServiceHelper();
            serviceHelper.initialize(BUS_APPLICATION_NAME, dutDeviceId, dutAppId);

            deviceAboutAnnouncement = serviceHelper.waitForNextDeviceAnnouncement(ANNOUCEMENT_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS);
            assertNotNull("Timed out waiting for About announcement", deviceAboutAnnouncement);

            aboutClient = serviceHelper.connectAboutClient(deviceAboutAnnouncement);
            assertTrue("Failed to establish a session with the Announcement sender", aboutClient.isConnected());
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

    /**
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {

        super.tearDown();
        releaseResources();
    }

    /**
     * Initializes {@link ServiceHelper}
     * @return {@link ServiceHelper}
     */
    protected ServiceHelper getServiceHelper() {

        return new ServiceHelper(new AndroidLogger());
    }

    /**
     * Release the test resources
     */
    protected void releaseResources() {

        logger.info("Releasing the test resources");

        disconnectAboutClient();

        if ( serviceHelper != null ) {

            serviceHelper.release();
            serviceHelper = null;
        }

        if ( deviceAboutAnnouncement != null ) {

            deviceAboutAnnouncement = null;
        }
    }

    /**
     * Verifies that the object or one of its child objects, implement the {@link AllSeenIntrospectable}
     * interface and has the "description" tag.
     * If an object has a description in multiple languages,
     * the introspection XMLs of each object should be identical.
     */
    @ValidationTest(name="EventsActions-v1-01")
    public void testEventsActions_v1_1() {

        logger.info("Executing the test");
        logger.info("Received announcement from device: '%s' app: '%s', bus: '%s'",
                      deviceAboutAnnouncement.getDeviceId(),
                      deviceAboutAnnouncement.getAppId(),
                      deviceAboutAnnouncement.getServiceName());

        List<String> objectPaths = getAllSeenIntrospectablObjectPaths();
        assertTrue("Looks like this object doesn't implement the: '" + getIntrospectableInterfaceName() + "' interface,"
                     + " even though it states it does in the announcement", objectPaths.size() > 0);

        logger.info("Object paths to be tested: '%s'", objectPaths);
        for (String objectPath : objectPaths) {

            logger.info("==> Testing Announced Object Path: '%s'", objectPath);

            assertTrue("The test of the Announced Object Path: '" + objectPath + "' has failed", testObjectValidity(objectPath));
            logger.info("==> The test of the Announced Object Path: '%s' passed successfully", objectPath);
        }
    }

    /**
     * Runs the test for the received object path
     * @param objectPath
     * @return whether description was found at any place for this object path or its sub objects
     */
    private boolean testObjectValidity(String objectPath) {

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
     * Parses parent's introspection XML and calls {@link EventsActionsTestSuite#testObjectValidity(String)} for each child object.
     * @param parentObjectPath Parent object path that was introspected
     * @param parentIntroXML Parent introspection XML
     * @return TRUE whether at least one of the child objects has a description tag
     */
    private boolean testChildrenObjectValidity(String parentObjectPath, String parentIntroXML) {

        EvAcIntrospectionNode introspectNode = null;

        try {

            introspectNode = new EvAcIntrospectionNode(parentObjectPath);
            introspectNode.parse(parentIntroXML);
        } catch (Exception e) {

            logger.error("Failed to parse the introspection XML, object path: '%s'", parentObjectPath);
            logger.error("Error", e);
            fail();
        }

        logger.debug("Testing child objects of the parent object: '%s'", parentObjectPath);

        List<EvAcIntrospectionNode> childrenNodes = introspectNode.getChidren();
        boolean descFoundBroth                    = false;

        if ( childrenNodes == null || childrenNodes.size() == 0 ) {

            logger.warn("The object '%s' doesn't have any child object", parentObjectPath);
            return false;
        }

        for (EvAcIntrospectionNode childNode : introspectNode.getChidren()) {

            boolean descFoundChild = testObjectValidity(childNode.getPath());
            String logMsg          = descFoundChild ? "contains a description tag" : "doesn't contain any description tag";

            logger.debug("The object or its offspring: '%s' %s", childNode.getPath(), logMsg);
            if ( !descFoundBroth ) {

                descFoundBroth = descFoundChild;
            }
        }

        String logMsg  = descFoundBroth ? "contain a description tag" : "doesn't contain any description tag";
        logger.debug("Child objects of the parent: '%s' %s", parentObjectPath, logMsg);
        return descFoundBroth;
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
     */
    private boolean testObjectValidityPerLanguages(ProxyBusObject proxyObj, String parentObjectPath, String[] descLangs) {

        logger.info("Found description languages: '%s' for the objectPath: '%s'", Arrays.toString(descLangs), parentObjectPath);

        String firstLangXML      = null;
        String firstLang         = null;
        boolean descriptionFound = false;

        for (String lang : descLangs) {

            String currentXML = getIntrospectionXML(proxyObj, parentObjectPath, lang);
            assertNotNull("Introspection XML is NULL Object Path: '" + parentObjectPath + "'", currentXML);

            //Print the introspection XML
            //logger.debug("The introspection XML, the lang: '%s': '%s'", lang, currentXML);

            currentXML = removeXMLDesc(currentXML);

            logger.debug("Testing language validity for the object path: '%s', language: '%s'", parentObjectPath, lang);

            if ( firstLangXML == null ) {

                assertTrue("The description tag wasn't found in the XML for the description language: '" + lang + "', " +
                           "Object Path: '" + parentObjectPath + "'", currentXML.contains(INTROSPECTION_XML_DESC_EXPECTED));

                logger.info("The object '%s' contains a description tag in the language: '%s'", parentObjectPath, lang);

                if ( descLangs.length == 1 ) {

                    logger.debug("The object '%s' supports a single description language: '%s'", parentObjectPath, lang);
                    return true;
                }

                firstLang        = lang;
                firstLangXML     = currentXML;
                descriptionFound = true;
                continue;
            }

            logger.debug("Test identity of the XMLs in the first language: '%s' and the current language: '%s', " +
                         "Object Path: '%s'", firstLang, lang, parentObjectPath);

            //Print the intospection XML in the first language and in the current language
            //logger.debug("The expected XML in the first lang: '%s': '%s'", firstLang, firstLangXML);
            //logger.debug("The tested XML in the current lang: '%s': '%s'", lang, currentXML);

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
     * Searches in the received announcement object paths that implement the {@link AllSeenIntrospectable}
     * interface
     * @return Array of the object paths
     */
    protected List<String> getAllSeenIntrospectablObjectPaths() {

        List<String> retList  = new ArrayList<String>();
        String introIfaceName = getIntrospectableInterfaceName();

        BusObjectDescription[] objDescs = deviceAboutAnnouncement.getObjectDescriptions();
        for (BusObjectDescription bod : objDescs) {

            String path = bod.getPath();

            for (String iface : bod.getInterfaces()) {

                // The AllSeenIntrospectable interface was found => add the path to the returned list
                if ( iface.equals(introIfaceName) ) {
                    retList.add(path);
                }
            }
        }

        return retList;
    }

    /**
     * Disconnect and releases the {@link AboutClient}
     */
    private void disconnectAboutClient() {

        if ( aboutClient != null && aboutClient.isConnected() ) {

            aboutClient.disconnect();
            aboutClient = null;
        }
    }

    /**
     * @return Returns the AJ name of the {@link AllSeenIntrospectable} interface
     */
    private String getIntrospectableInterfaceName() {

        //Retrieve the AJ name of the introspection interface
        BusInterface ifaceName = AllSeenIntrospectable.class.getAnnotation(BusInterface.class);
        return ifaceName.name();
    }

    /**
     * Returns the supported description languages for the given object path
     * @param proxyObj {@link ProxyBusObject}
     * @param objectPath The object to be asked for the description languages
     * @return Array of the description languages
     */
    private String[] getDescriptionLanguages(ProxyBusObject proxyObj, String objectPath) {

        String[] langs = new String[]{};

        try {
            langs = proxyObj.getInterface(AllSeenIntrospectable.class).GetDescriptionLanguages();
        } catch (BusException be) {

            logger.error("Failed to call GetDescriptionLanguages for the Object Path: '%s'", objectPath);
            logger.error("Error", be);
            fail();
        }

        return langs;
    }

    /**
     * Retrieves the introspection XML
     * @param proxyObj The {@link ProxyBusObject}
     * @param objectPath Object path to be introspected
     * @param language The language to query the introspection
     * @return Introspection XML
     */
    private String getIntrospectionXML(ProxyBusObject proxyObj, String objectPath, String lang) {

        String introXML = null;

        try {
            introXML = proxyObj.getInterface(AllSeenIntrospectable.class).IntrospectWithDescription(lang);
        } catch (BusException be) {

            logger.error("Failed to call IntrospectWithDescription for the Object Path: '%s'", objectPath);
            logger.error("Error", be);
            fail();
        }

        return introXML;
    }

    /**
     * This method removes the content of the XML description tags
     * @param introspection
     * @return Introspected XML without the description content
     */
    private String removeXMLDesc(String introspection) {

        return introspection.replaceAll(INTROSPECTION_XML_DESC_REGEX, INTROSPECTION_XML_DESC_PLACEHOLDER);
    }
}
