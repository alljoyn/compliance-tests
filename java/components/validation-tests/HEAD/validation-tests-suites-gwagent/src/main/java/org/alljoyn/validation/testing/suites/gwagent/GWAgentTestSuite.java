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
package org.alljoyn.validation.testing.suites.gwagent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.ParserConfigurationException;

import org.alljoyn.about.AboutKeys;
import org.alljoyn.about.client.AboutClient;
import org.alljoyn.bus.BusException;
import org.alljoyn.gatewaycontroller.sdk.GatewayController;
import org.alljoyn.services.android.utils.AndroidLogger;
import org.alljoyn.services.common.ServiceAvailabilityListener;
import org.alljoyn.validation.framework.AppUnderTestDetails;
import org.alljoyn.validation.framework.ValidationBaseTestCase;
import org.alljoyn.validation.framework.annotation.ValidationSuite;
import org.alljoyn.validation.framework.annotation.ValidationTest;
import org.alljoyn.validation.framework.utils.introspection.BusIntrospector;
import org.alljoyn.validation.framework.utils.introspection.bean.InterfaceDetail;
import org.alljoyn.validation.framework.utils.introspection.bean.IntrospectionInterface;
import org.alljoyn.validation.testing.utils.ValidationResult;
import org.alljoyn.validation.testing.utils.about.AboutAnnouncementDetails;
import org.alljoyn.validation.testing.utils.gwagent.GWAgentHelper;
import org.alljoyn.validation.testing.utils.gwagent.GWAgentInterfaceValidator;
import org.alljoyn.validation.testing.utils.log.Logger;
import org.alljoyn.validation.testing.utils.log.LoggerFactory;
import org.alljoyn.validation.testing.utils.services.ServiceAvailabilityHandler;
import org.xml.sax.SAXException;

@ValidationSuite(name = "GWAgent-v1")
public class GWAgentTestSuite extends ValidationBaseTestCase implements ServiceAvailabilityListener
{
    private static final String TAG = "GWAgentTestSuite";
    private static final Logger logger = LoggerFactory.getLogger(TAG);
    private static final String BUS_APPLICATION_NAME = "GWAgentTestSuite";
    public static final long ANNOUCEMENT_TIMEOUT_IN_SECONDS = 30;
    private static final long SESSION_CLOSE_TIMEOUT_IN_SECONDS = 5;

    private GWAgentHelper gwagentHelper;
    private AboutClient aboutClient;
    private GatewayController gatewayController;

    private AboutAnnouncementDetails deviceAboutAnnouncement;

    private AppUnderTestDetails appUnderTestDetails;
    private UUID dutAppId;
    private String dutDeviceId;
    private ServiceAvailabilityHandler serviceAvailabilityHandler;
    private String keyStorePath;

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();

        logger.debug("test setUp started");

        try
        {
            appUnderTestDetails = getValidationTestContext().getAppUnderTestDetails();
            dutDeviceId = appUnderTestDetails.getDeviceId();
            logger.debug(String.format("Running GWAgent test case against Device ID: %s", dutDeviceId));
            dutAppId = appUnderTestDetails.getAppId();
            logger.debug(String.format("Running GWAgent test case against App ID: %s", dutAppId));
            keyStorePath = getValidationTestContext().getKeyStorePath();
            logger.debug(String.format("Running GWAgent test case using KeyStorePath: %s", keyStorePath));

            initGWAgentHelper();

            logger.debug("test setUp done");
        }
        catch (Exception e)
        {
            logger.debug("test setUp thrown an exception", e);
            releaseResources();
            throw e;
        }
    }

    protected void initGWAgentHelper() throws BusException, Exception
    {
        releaseGWAgentHelper();
        gwagentHelper = createGWAgentHelper();

        gwagentHelper.initialize(BUS_APPLICATION_NAME, dutDeviceId, dutAppId);

        deviceAboutAnnouncement = waitForNextAnnouncementAndCheckFieldValue(AboutKeys.ABOUT_DEVICE_NAME, "AllJoyn Gateway Agent"); // waitForNextDeviceAnnouncement();

        connectAboutClient(deviceAboutAnnouncement);
        connectGatewayController(deviceAboutAnnouncement);

        gwagentHelper.enableAuthentication(keyStorePath);
    }

    private void releaseGWAgentHelper()
    {
        try
        {
            if (aboutClient != null)
            {
                aboutClient.disconnect();
                aboutClient = null;
            }
            if (gatewayController != null)
            {
                gatewayController.shutdown();
                gatewayController = null;
            }
            if (gwagentHelper != null)
            {
                gwagentHelper.release();
                waitForSessionToClose();
                gwagentHelper = null;
            }
        }
        catch (Exception ex)
        {
            logger.debug("Exception releasing resources", ex);
        }
    }

    private void connectGatewayController(AboutAnnouncementDetails aboutAnnouncement) throws Exception
    {
        gatewayController = gwagentHelper.connectGatewayController(aboutAnnouncement);
    }

    private void connectAboutClient(AboutAnnouncementDetails aboutAnnouncement) throws Exception
    {
        serviceAvailabilityHandler = createServiceAvailabilityHandler();
        aboutClient = gwagentHelper.connectAboutClient(aboutAnnouncement, serviceAvailabilityHandler);
    }

    private AboutAnnouncementDetails waitForNextDeviceAnnouncement() throws Exception
    {
        logger.info("Waiting for About announcement");
        return gwagentHelper.waitForNextDeviceAnnouncement(ANNOUCEMENT_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS, true);
    }

    private void waitForSessionToClose() throws Exception
    {
        logger.info("Waiting for session to close");
        gwagentHelper.waitForSessionToClose(SESSION_CLOSE_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS);
    }

    @Override
    protected void tearDown() throws Exception
    {
        super.tearDown();
        logger.debug("test tearDown started");
        releaseResources();
        logger.debug("test tearDown done");
    }

    @ValidationTest(name = "GWAgent-v1-01")
    public void testGWAgent_v1_01_GWAgentInterfacesMatchDefinitions() throws Exception
    {
        List<InterfaceDetail> gwagentCtrlIntrospectionInterfacesExposedOnBus = new ArrayList<InterfaceDetail>();
        List<InterfaceDetail> gwagentCtrlAppMgmtIntrospectionInterfacesExposedOnBus = getIntrospector().getInterfacesExposedOnBusUnderSpecifiedPathBasedOnName("/",
                "org.alljoyn.gwagent.ctrl.AppMgmt");
        for (InterfaceDetail gwObjectDetail : gwagentCtrlAppMgmtIntrospectionInterfacesExposedOnBus)
        {
            if (gwObjectDetail.getPath().equals("/gw"))
            {
                gwagentCtrlIntrospectionInterfacesExposedOnBus.add(gwObjectDetail);
                List<InterfaceDetail> gwagentCtrlAppIntrospectionInterfacesExposedOnBus = getIntrospector().getInterfacesExposedOnBusUnderSpecifiedPathBasedOnName(
                        gwObjectDetail.getPath(), "org.alljoyn.gwagent.ctrl.App");
                for (InterfaceDetail gwAppObjectDetail : gwagentCtrlAppIntrospectionInterfacesExposedOnBus)
                {
                    gwagentCtrlIntrospectionInterfacesExposedOnBus.add(gwAppObjectDetail);
                }
                List<InterfaceDetail> gwagentCtrlAclMgmtIntrospectionInterfacesExposedOnBus = getIntrospector().getInterfacesExposedOnBusUnderSpecifiedPathBasedOnName(
                        gwObjectDetail.getPath(), "org.alljoyn.gwagent.ctrl.AclMgmt");
                for (InterfaceDetail gwAclMgmtObjectDetail : gwagentCtrlAclMgmtIntrospectionInterfacesExposedOnBus)
                {
                    gwagentCtrlIntrospectionInterfacesExposedOnBus.add(gwAclMgmtObjectDetail);
                }
                for (InterfaceDetail gwAppObjectDetail : gwagentCtrlAppIntrospectionInterfacesExposedOnBus)
                {
                    boolean matched = false;
                    for (InterfaceDetail gwAclMgmtObjectDetail : gwagentCtrlAclMgmtIntrospectionInterfacesExposedOnBus)
                    {
                        if (gwAppObjectDetail.getPath().equals(gwAclMgmtObjectDetail.getPath()))
                        {
                            matched = true;
                            List<InterfaceDetail> gwagentCtrlAclIntrospectionInterfacesExposedOnBus = getIntrospector().getInterfacesExposedOnBusUnderSpecifiedPathBasedOnName(
                                    gwObjectDetail.getPath(), "org.alljoyn.gwagent.ctrl.Acl");
                            for (InterfaceDetail gwAclObjectDetail : gwagentCtrlAclIntrospectionInterfacesExposedOnBus)
                            {
                                gwagentCtrlIntrospectionInterfacesExposedOnBus.add(gwAclObjectDetail);
                            }
                        }
                    }
                    assertTrue(String.format("Application at path %s does NOT have ALL relevant interfaces", gwAppObjectDetail.getPath()), matched);
                }
            }
        }
        for (InterfaceDetail objectDetail : gwagentCtrlIntrospectionInterfacesExposedOnBus)
        {
            for (IntrospectionInterface intfName : objectDetail.getIntrospectionInterfaces())
            {
                logger.info(String.format("Found object at %s implementing %s", objectDetail.getPath(), intfName.getName()));
            }
        }
        ValidationResult validationResult = getInterfaceValidator().validate(gwagentCtrlIntrospectionInterfacesExposedOnBus);

        assertTrue(validationResult.getFailureReason(), validationResult.isValid());
    }

    @Override
    public void connectionLost()
    {
        logger.debug("The connection with the remote device has lost");
    }

    protected GWAgentHelper createGWAgentHelper()
    {
        return new GWAgentHelper(new AndroidLogger());
    }

    protected ServiceAvailabilityHandler createServiceAvailabilityHandler()
    {
        return new ServiceAvailabilityHandler();
    }

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

    protected BusIntrospector getIntrospector()
    {
        return gwagentHelper.getBusIntrospector(aboutClient);
    }

    protected GWAgentInterfaceValidator getInterfaceValidator() throws IOException, ParserConfigurationException, SAXException
    {
        return new GWAgentInterfaceValidator(getValidationTestContext());
    }

    private void releaseResources()
    {
        releaseGWAgentHelper();
    }
}