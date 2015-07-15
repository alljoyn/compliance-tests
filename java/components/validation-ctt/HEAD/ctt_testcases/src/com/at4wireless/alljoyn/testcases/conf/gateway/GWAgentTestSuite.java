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
package com.at4wireless.alljoyn.testcases.conf.gateway;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.ParserConfigurationException;

import org.alljoyn.about.client.AboutClient;
import org.alljoyn.bus.BusException;
import org.alljoyn.services.common.ServiceAvailabilityListener;
import org.xml.sax.SAXException;

import com.at4wireless.alljoyn.core.about.AboutAnnouncementDetails;
import com.at4wireless.alljoyn.core.commons.ServiceAvailabilityHandler;
import com.at4wireless.alljoyn.core.commons.ServiceHelper;
import com.at4wireless.alljoyn.core.commons.log.WindowsLoggerImpl;
import com.at4wireless.alljoyn.core.introspection.BusIntrospector;
import com.at4wireless.alljoyn.core.introspection.bean.InterfaceDetail;
import com.at4wireless.alljoyn.core.introspection.bean.IntrospectionInterface;

public class GWAgentTestSuite implements ServiceAvailabilityListener
//public class GWAgentTestSuite
{ 
	private static final String TAG = "GWAgentTestSuite";
	//private static final Logger logger = LoggerFactory.getLogger(TAG);
	private static final WindowsLoggerImpl logger =  new WindowsLoggerImpl(TAG);
	private static final String BUS_APPLICATION_NAME = "GWAgentTestSuite";
	//public static final long ANNOUCEMENT_TIMEOUT_IN_SECONDS = 30;
	public long ANNOUNCEMENT_TIMEOUT_IN_SECONDS = 30;
	
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

    //private AppUnderTestDetails appUnderTestDetails;
    private UUID dutAppId;
    private String dutDeviceId;
    //private String keyStorePath;
    private String keyStorePath = "/KeyStore";

	/** 
	 * [AT4] Added attributes to perform the test cases
	 * 
	 * pass	stores the final verdict of the test case
	 * ics	map that stores ICS values	
	 * ixit	map that stores IXIT values
	 * 
	 * */
	boolean pass = true;
    Map<String, Boolean> ics;
    Map<String, String> ixit;

	public GWAgentTestSuite(String testCase,
			boolean iCSG_GatewayServiceFramework,
			boolean iCSG_ProfileManagementInterface,
			boolean iCSG_AppAccessInterface,
			boolean iCSG_AppManagementInterface, String iXITCO_AppId,
			String iXITCO_DeviceId, String iXITCO_DefaultLanguage,
			String iXITG_AppMgmtVersion, String iXITG_CtrlAppVersion,
			String iXITG_CtrlAccessVersion, String iXITG_CtrlAclVersion,
			String iXITG_ConnAppVersion, String gPCO_AnnouncementTimeout,
			String gPG_SessionClose)
	{

		ics = new HashMap<String, Boolean>();
		ixit = new HashMap<String, String>();
		
		ics.put("ICS_GatewayServiceFramework", iCSG_GatewayServiceFramework);
		ics.put("ICSG_ProfileManagementInterface", iCSG_ProfileManagementInterface);
		ics.put("ICSG_AppAccessInterface", iCSG_AppAccessInterface);
		ics.put("ICSG_AppManagementInterface", iCSG_AppManagementInterface);

		ixit.put("IXITCO_AppId", iXITCO_AppId);
		ixit.put("IXITCO_DeviceId", iXITCO_DeviceId);
		ixit.put("IXITCO_DefaultLanguage", iXITCO_DefaultLanguage);
		ixit.put("IXITG_AppMgmtVersion", iXITG_AppMgmtVersion);
		ixit.put("IXITG_CtrlAppVersion", iXITG_CtrlAppVersion);
		ixit.put("IXITG_CtrlAccessVersion", iXITG_CtrlAccessVersion);
		ixit.put("IXITG_CtrlAclVersion", iXITG_CtrlAclVersion);
		ixit.put("IXITG_ConnAppVersion", iXITG_ConnAppVersion);

		ANNOUNCEMENT_TIMEOUT_IN_SECONDS = Integer.parseInt(gPCO_AnnouncementTimeout);
		//SESSION_CLOSE_TIMEOUT_IN_SECONDS = Integer.parseInt(gPG_SessionClose);

		try
		{
			runTestCase(testCase);
		}
		catch (Exception e)
		{
			if (e.getMessage().equals("Timed out waiting for About announcement"))
			{
				fail("Timed out waiting for About announcement");
			}
			else
			{
				fail("Exception: "+e.toString());
			}
		}
	}

	public void runTestCase(String testCase) throws Exception
	{	
		setUp();
		logger.info("Running testcase: "+testCase);
		
		if (testCase.equals("GWAgent-v1-01"))
		{
			testGWAgent_v1_01_ValiateCtrlAppMgmtInterfaces();
		}
		else
		{
			fail("Test Case not valid");
		}

		tearDown();
	}
	
	private void setUp() throws Exception
	{
		//super.setUp();
		
		logger.noTag("====================================================");
		logger.debug("test setUp started");

		try
		{
			//appUnderTestDetails = getValidationTestContext().getAppUnderTestDetails();
			//dutDeviceId = appUnderTestDetails.getDeviceId();
			dutDeviceId = ixit.get("IXITCO_DeviceId");
			logger.info(String.format("Running GWAgent test case against Device ID: %s", dutDeviceId));
			//dutAppId = appUnderTestDetails.getAppId();
			dutAppId = UUID.fromString(ixit.get("IXITCO_AppId"));
			logger.info(String.format("Running GWAgent test case against App ID: %s", dutAppId));
			//keyStorePath = getValidationTestContext().getKeyStorePath();
			logger.info(String.format("Running GWAgent test case using KeyStorePath: %s", keyStorePath));

			serviceHelper = getServiceHelper();
			serviceHelper.initialize(BUS_APPLICATION_NAME, dutDeviceId, dutAppId);
			//initGWAgentHelper();

			deviceAboutAnnouncement = serviceHelper.waitForNextDeviceAnnouncement(ANNOUNCEMENT_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS);
			//assertNotNull("Timed out waiting for About announcement", deviceAboutAnnouncement);
			if (deviceAboutAnnouncement == null)
			{
				throw new Exception("Timed out waiting for About announcement");
			}
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
			//logger.debug("test setUp thrown an exception "+ e.toString());
			//releaseResources();
			throw exception;
		}
		
		logger.noTag("====================================================");
	}
	
	private void tearDown()
	{
		logger.noTag("====================================================");
		logger.info("test tearDown started");
		releaseResources();
		logger.info("test tearDown done");
		logger.noTag("====================================================");
	}
	
	private void releaseResources()
	{
		//releaseGWAgentHelper();
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
        //return new ServiceHelper(new AndroidLogger());
    	return new ServiceHelper(logger);
    }
    
	/** 
	 * [AT4] Added methods to emulate JUnit behaviour
	 * 
	 * assertEquals
	 * assertTrue
	 * 
	 * */
	private void assertEquals(String errorMessage, String first, String second)
	{
		if (!first.equals(second))
		{
			fail(errorMessage);
		}
	}
	
	private void assertEquals(String errorMessage, int first, int second)
	{
		if (first != second)
		{
			fail(errorMessage);
		}
	}

	private void assertTrue(String errorMessage, boolean condition) {
		
		if (!condition)
		{
			fail(errorMessage);
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
