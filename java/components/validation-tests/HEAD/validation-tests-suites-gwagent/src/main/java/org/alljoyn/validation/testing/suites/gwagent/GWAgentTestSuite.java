/*******************************************************************************
 *   *  
 *     Copyright (c) 2016 Open Connectivity Foundation and AllJoyn Open
 *     Source Project Contributors and others.
 *     
 *     All rights reserved. This program and the accompanying materials are
 *     made available under the terms of the Apache License, Version 2.0
 *     which accompanies this distribution, and is available at
 *     http://www.apache.org/licenses/LICENSE-2.0

 *******************************************************************************/
package org.alljoyn.validation.testing.suites.gwagent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.ParserConfigurationException;

import org.alljoyn.about.client.AboutClient;
import org.alljoyn.bus.BusException;
import org.alljoyn.services.android.utils.AndroidLogger;
import org.alljoyn.services.common.ServiceAvailabilityListener;
import org.alljoyn.validation.framework.AppUnderTestDetails;
import org.alljoyn.validation.framework.annotation.ValidationSuite;
import org.alljoyn.validation.framework.annotation.ValidationTest;
import org.alljoyn.validation.framework.utils.introspection.BusIntrospector;
import org.alljoyn.validation.framework.utils.introspection.bean.InterfaceDetail;
import org.alljoyn.validation.framework.utils.introspection.bean.IntrospectionInterface;
import org.alljoyn.validation.testing.utils.BaseTestSuite;
import org.alljoyn.validation.testing.utils.about.AboutAnnouncementDetails;
import org.alljoyn.validation.testing.utils.log.Logger;
import org.alljoyn.validation.testing.utils.log.LoggerFactory;
import org.alljoyn.validation.testing.utils.services.ServiceAvailabilityHandler;
import org.alljoyn.validation.testing.utils.services.ServiceHelper;
import org.xml.sax.SAXException;

@ValidationSuite(name = "GWAgent-v1")
public class GWAgentTestSuite extends BaseTestSuite implements ServiceAvailabilityListener
{
    private static final String TAG = "GWAgentTestSuite";
    private static final Logger logger = LoggerFactory.getLogger(TAG);
    private static final String BUS_APPLICATION_NAME = "GWAgentTestSuite";
    public static final long ANNOUCEMENT_TIMEOUT_IN_SECONDS = 30;

    static final String GWAGENT_IFACE_PREFIX = "org.alljoyn.gwagent.ctrl";
    static final String GWAGENT_APPMGMT_IFNAME = GWAGENT_IFACE_PREFIX + ".AppMgmt";
    static final String GWAGENT_CTRLAPP_IFNAME = GWAGENT_IFACE_PREFIX + ".App";
    static final String GWAGENT_CTRLACLMGMT_IFNAME = GWAGENT_IFACE_PREFIX + ".AclMgmt";
    static final String GWAGENT_CTRLACL_IFNAME = GWAGENT_IFACE_PREFIX + ".Acl";
    static final String GWAGENT_APPMGMT_PATH = "/gw";

    private ServiceHelper serviceHelper;
    private AboutClient aboutClient;
    private BusIntrospector busIntrospector;

    private AboutAnnouncementDetails deviceAboutAnnouncement;

    private AppUnderTestDetails appUnderTestDetails;
    private UUID dutAppId;
    private String dutDeviceId;
    private String keyStorePath;

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();

        try
        {
            logger.debug("test setUp started");

            appUnderTestDetails = getValidationTestContext().getAppUnderTestDetails();
            dutDeviceId = appUnderTestDetails.getDeviceId();
            logger.debug(String.format("Running GWAgent test case against Device ID: %s", dutDeviceId));
            dutAppId = appUnderTestDetails.getAppId();
            logger.debug(String.format("Running GWAgent test case against App ID: %s", dutAppId));
            keyStorePath = getValidationTestContext().getKeyStorePath();
            logger.debug(String.format("Running GWAgent test case using KeyStorePath: %s", keyStorePath));

            serviceHelper = getServiceHelper();
            serviceHelper.initialize(BUS_APPLICATION_NAME, dutDeviceId, dutAppId);

            deviceAboutAnnouncement = serviceHelper.waitForNextDeviceAnnouncement(determineAboutAnnouncementTimeout(), TimeUnit.SECONDS);
            assertNotNull("Timed out waiting for About announcement", deviceAboutAnnouncement);
            aboutClient = serviceHelper.connectAboutClient(deviceAboutAnnouncement);
            serviceHelper.enableAuthentication(keyStorePath);
            busIntrospector = getIntrospector();
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

    private void releaseResources()
    {
        disconnectFromAboutClient();

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

    @ValidationTest(name = "GWAgent-v1-01")
    public void testGWAgent_v1_01_ValiateCtrlAppMgmtInterfaces() throws Exception
    {
        List<InterfaceDetail> gwAgentObjects = new ArrayList<InterfaceDetail>();

        assertTrue("About announcement does not advertise interface: " + GWAGENT_APPMGMT_IFNAME, deviceAboutAnnouncement.supportsInterface(GWAGENT_APPMGMT_IFNAME));

        List<InterfaceDetail> appMgmtInterfaceDetailList = busIntrospector.getInterfacesExposedOnBusBasedOnName(GWAGENT_APPMGMT_IFNAME);

        validateAppMgmtBusObject(appMgmtInterfaceDetailList, gwAgentObjects);

        validateAppBusObjects(gwAgentObjects);

        validateAclMgmtBusObjects(gwAgentObjects);

        logBusObjectDetails(gwAgentObjects);

    }

    private void logBusObjectDetails(List<InterfaceDetail> gwAgentObjects)
    {
        for (InterfaceDetail objectDetail : gwAgentObjects)
        {
            for (IntrospectionInterface intfName : objectDetail.getIntrospectionInterfaces())
            {
                logger.info(String.format("Found object at %s implementing %s", objectDetail.getPath(), intfName.getName()));
            }
        }
    }

    private void validateAclMgmtBusObjects(List<InterfaceDetail> gwAgentObjects) throws BusException, IOException, ParserConfigurationException, SAXException
    {
        for (InterfaceDetail gwAclMgmtInterfaceDetail : getIntrospector().getInterfacesExposedOnBusUnderSpecifiedPathBasedOnName(GWAGENT_APPMGMT_PATH, GWAGENT_CTRLACLMGMT_IFNAME))
        {
            String path = gwAclMgmtInterfaceDetail.getPath();
            assertTrue("BusObject at " + path + " must implement " + GWAGENT_CTRLAPP_IFNAME, busIntrospector.isInterfacePresent(path, GWAGENT_CTRLAPP_IFNAME));
            gwAgentObjects.add(gwAclMgmtInterfaceDetail);

            for (InterfaceDetail gwAclInterfaceDetail : getIntrospector().getInterfacesExposedOnBusUnderSpecifiedPathBasedOnName(path, GWAGENT_CTRLACL_IFNAME))
            {
                gwAgentObjects.add(gwAclInterfaceDetail);
            }

        }
    }

    private void validateAppBusObjects(List<InterfaceDetail> gwAgentObjects) throws BusException, IOException, ParserConfigurationException, SAXException
    {
        for (InterfaceDetail gwAppInterfaceDetail : getIntrospector().getInterfacesExposedOnBusUnderSpecifiedPathBasedOnName(GWAGENT_APPMGMT_PATH, GWAGENT_CTRLAPP_IFNAME))
        {
            String path = gwAppInterfaceDetail.getPath();
            assertTrue("BusObject at " + path + " must implement " + GWAGENT_CTRLACLMGMT_IFNAME, busIntrospector.isInterfacePresent(path, GWAGENT_CTRLACLMGMT_IFNAME));
            gwAgentObjects.add(gwAppInterfaceDetail);
        }
    }

    private void validateAppMgmtBusObject(List<InterfaceDetail> appMgmtInterfaceDetailList, List<InterfaceDetail> gwAgentObjects)
    {
        assertEquals("One BusObject implementing " + GWAGENT_APPMGMT_IFNAME + " not found on bus", 1, appMgmtInterfaceDetailList.size());

        InterfaceDetail gwAppMgmtInterfaceDetail = appMgmtInterfaceDetailList.get(0);
        String gwAppMgmtPath = gwAppMgmtInterfaceDetail.getPath();
        assertEquals("Object implementing " + GWAGENT_APPMGMT_IFNAME + " found at " + gwAppMgmtPath + " instead of /gw", gwAppMgmtPath, GWAGENT_APPMGMT_PATH);
        gwAgentObjects.add(gwAppMgmtInterfaceDetail);
    }

    @Override
    public void connectionLost()
    {
        logger.debug("The connection with the remote device has lost");
    }

    protected ServiceAvailabilityHandler createServiceAvailabilityHandler()
    {
        return new ServiceAvailabilityHandler();
    }

    BusIntrospector getIntrospector()
    {
        return serviceHelper.getBusIntrospector(aboutClient);
    }

    ServiceHelper getServiceHelper()
    {
        return new ServiceHelper(new AndroidLogger());
    }

}