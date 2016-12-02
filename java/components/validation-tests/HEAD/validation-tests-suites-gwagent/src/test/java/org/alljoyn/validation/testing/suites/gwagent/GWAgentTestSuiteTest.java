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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import junit.framework.AssertionFailedError;

import org.alljoyn.about.client.AboutClient;
import org.alljoyn.about.icon.AboutIconClient;
import org.alljoyn.bus.BusAttachment;
import org.alljoyn.bus.BusException;
import org.alljoyn.validation.framework.AppUnderTestDetails;
import org.alljoyn.validation.framework.ValidationTestContext;
import org.alljoyn.validation.framework.utils.introspection.XmlBasedBusIntrospector;
import org.alljoyn.validation.framework.utils.introspection.bean.InterfaceDetail;
import org.alljoyn.validation.framework.utils.introspection.bean.IntrospectionInterface;
import org.alljoyn.validation.framework.utils.introspection.bean.IntrospectionNode;
import org.alljoyn.validation.framework.utils.introspection.bean.NodeDetail;
import org.alljoyn.validation.testing.utils.InterfaceValidator;
import org.alljoyn.validation.testing.utils.about.AboutAnnouncementDetails;
import org.alljoyn.validation.testing.utils.services.ServiceHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.annotation.Config;

@RunWith(MyRobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class GWAgentTestSuiteTest
{

    private static final String CONNECTOR_APP1_PATH = "/gw/connectorApp1";
    private static final String CONNECTOR_APP1_ACL_PATH = "/gw/connectorApp1/acl";

    @Mock
    private ValidationTestContext mockTestContext;
    @Mock
    private AboutAnnouncementDetails mockAboutAnnouncement;
    @Mock
    protected ServiceHelper mockServiceHelper;
    @Mock
    private AboutClient mockAboutClient;
    @Mock
    private AboutIconClient mockAboutIconClient;
    @Mock
    private XmlBasedBusIntrospector mockIntrospector;
    @Mock
    private BusAttachment mockBusAttachment;
    @Mock
    private InterfaceValidator mockInterfaceValidator;
    @Mock
    private NodeDetail mockNodeDetail;
    @Mock
    private IntrospectionInterface mockIntrospectionInterface;
    @Mock
    private IntrospectionNode mockIntrospectionNode;
    protected Exception thrownException;
    private String deviceId = "deviceId";
    private UUID appId = UUID.randomUUID();
    private AppUnderTestDetails appUnderTestDetails;
    private GWAgentTestSuite gwAgentTestSuite;

    public interface TestWrapper
    {
        void executeTestMethod() throws Exception;
    }

    protected void executeTestMethod(TestWrapper testWrapper, BusException expectedException) throws AssertionFailedError, Exception
    {
        BusException actualException = null;
        try
        {
            executeTestMethod(testWrapper);
            fail(String.format("No Exception thrown, expecting BusException: %s", expectedException.toString()));
        }
        catch (BusException e)
        {
            actualException = e;
        }
        assertEquals(expectedException, actualException);
    }

    protected void executeTestMethodThrowsException(TestWrapper testWrapper, String expectedExceptionMessage) throws AssertionFailedError, Exception
    {
        String actualExceptionMessage = null;
        try
        {
            executeTestMethod(testWrapper);
            fail(String.format("No Exception thrown, expecting Exception with message: %s", expectedExceptionMessage));
        }
        catch (Exception e)
        {
            actualExceptionMessage = e.getMessage();
        }
        assertEquals(expectedExceptionMessage, actualExceptionMessage);
    }

    protected void executeTestMethodFailsAssertion(TestWrapper testWrapper, String expectedAssertionFailure) throws Exception
    {
        String assertionFailureMessage = null;
        try
        {
            executeTestMethod(testWrapper);
            fail(String.format("No assertion failure, expecting assertionFailure with message: %s", expectedAssertionFailure));
        }
        catch (AssertionFailedError e)
        {
            assertionFailureMessage = e.getMessage();
        }
        assertEquals(expectedAssertionFailure, assertionFailureMessage);
    }

    void executeTestMethod(TestWrapper testWrapper) throws Exception
    {
        gwAgentTestSuite.setUp();

        try
        {
            testWrapper.executeTestMethod();
        }
        finally
        {
            gwAgentTestSuite.tearDown();
        }

    }

    @Before
    public void setup() throws Exception
    {
        MockitoAnnotations.initMocks(this);

        appUnderTestDetails = new AppUnderTestDetails(appId, deviceId);
        when(mockTestContext.getAppUnderTestDetails()).thenReturn(appUnderTestDetails);

        when(mockAboutAnnouncement.supportsInterface(GWAgentTestSuite.GWAGENT_APPMGMT_IFNAME)).thenReturn(true);

        setupMockServiceHelper();
        constructGwAgentTestSuite();
    }

    private void setupMockServiceHelper() throws Exception
    {
        when(mockServiceHelper.waitForNextDeviceAnnouncement(anyLong(), any(TimeUnit.class))).thenReturn(mockAboutAnnouncement);
        when(mockServiceHelper.connectAboutClient(mockAboutAnnouncement)).thenReturn(mockAboutClient);
        when(mockServiceHelper.getBusIntrospector(mockAboutClient)).thenReturn(mockIntrospector);
    }

    private void constructGwAgentTestSuite()
    {
        gwAgentTestSuite = new GWAgentTestSuite()
        {
            @Override
            public ServiceHelper getServiceHelper()
            {
                return mockServiceHelper;
            }
        };

        gwAgentTestSuite.setValidationTestContext(mockTestContext);
    }

    protected TestWrapper getTestWrapperFor_v1_01()
    {
        return new TestWrapper()
        {
            @Override
            public void executeTestMethod() throws Exception
            {
                gwAgentTestSuite.testGWAgent_v1_01_ValiateCtrlAppMgmtInterfaces();
            }
        };
    }

    @Test
    public void testValidateGwAgentBusObjects() throws Exception
    {
        List<InterfaceDetail> appMgmtDetailList = getInterfaceDetailList(GWAgentTestSuite.GWAGENT_APPMGMT_PATH);
        when(mockIntrospector.getInterfacesExposedOnBusBasedOnName(GWAgentTestSuite.GWAGENT_APPMGMT_IFNAME)).thenReturn(appMgmtDetailList);

        List<InterfaceDetail> ctrlAppDetailList = getInterfaceDetailList(CONNECTOR_APP1_PATH);
        when(mockIntrospector.getInterfacesExposedOnBusUnderSpecifiedPathBasedOnName(GWAgentTestSuite.GWAGENT_APPMGMT_PATH, GWAgentTestSuite.GWAGENT_CTRLAPP_IFNAME)).thenReturn(
                ctrlAppDetailList);

        List<InterfaceDetail> ctrlAclMgmtDetailList = getInterfaceDetailList(CONNECTOR_APP1_PATH);
        when(mockIntrospector.getInterfacesExposedOnBusUnderSpecifiedPathBasedOnName(GWAgentTestSuite.GWAGENT_APPMGMT_PATH, GWAgentTestSuite.GWAGENT_CTRLACLMGMT_IFNAME))
                .thenReturn(ctrlAclMgmtDetailList);

        List<InterfaceDetail> aclDetailList = getInterfaceDetailList(CONNECTOR_APP1_ACL_PATH);
        when(mockIntrospector.getInterfacesExposedOnBusUnderSpecifiedPathBasedOnName(CONNECTOR_APP1_PATH, GWAgentTestSuite.GWAGENT_CTRLACL_IFNAME)).thenReturn(aclDetailList);

        when(mockIntrospector.isInterfacePresent(CONNECTOR_APP1_PATH, GWAgentTestSuite.GWAGENT_CTRLAPP_IFNAME)).thenReturn(true);
        when(mockIntrospector.isInterfacePresent(CONNECTOR_APP1_PATH, GWAgentTestSuite.GWAGENT_CTRLACLMGMT_IFNAME)).thenReturn(true);
        executeTestMethod(getTestWrapperFor_v1_01());
    }

    @Test
    public void testValidateGwAgentBusObjectsFailsWhenNotAdvertised() throws Exception
    {
        when(mockAboutAnnouncement.supportsInterface(GWAgentTestSuite.GWAGENT_APPMGMT_IFNAME)).thenReturn(false);
        executeTestMethodFailsAssertion(getTestWrapperFor_v1_01(), "About announcement does not advertise interface: org.alljoyn.gwagent.ctrl.AppMgmt");
    }

    @Test
    public void testValidateGwAgentBusObjectsFailsWhenNoneFound() throws Exception
    {
        when(mockIntrospector.getInterfacesExposedOnBusBasedOnName(GWAgentTestSuite.GWAGENT_APPMGMT_IFNAME)).thenReturn(new ArrayList<InterfaceDetail>());
        executeTestMethodFailsAssertion(getTestWrapperFor_v1_01(), "One BusObject implementing org.alljoyn.gwagent.ctrl.AppMgmt not found on bus expected:<1> but was:<0>");
    }

    @Test
    public void testValidateGwAgentBusObjectsFailsWhenFoundAtInvalidPath() throws Exception
    {
        List<InterfaceDetail> interfaceDetailList = getInterfaceDetailList("/mygw");
        when(mockIntrospector.getInterfacesExposedOnBusBasedOnName(GWAgentTestSuite.GWAGENT_APPMGMT_IFNAME)).thenReturn(interfaceDetailList);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_01(),
                "Object implementing org.alljoyn.gwagent.ctrl.AppMgmt found at /mygw instead of /gw expected:</[my]gw> but was:</[]gw>");
    }

    @Test
    public void testValidateGwAgentBusObjectsFailsWhenFoundMultiple() throws Exception
    {
        List<InterfaceDetail> interfaceDetailList = new ArrayList<InterfaceDetail>();
        interfaceDetailList.add(Mockito.mock(InterfaceDetail.class));
        interfaceDetailList.add(Mockito.mock(InterfaceDetail.class));

        when(mockIntrospector.getInterfacesExposedOnBusBasedOnName(GWAgentTestSuite.GWAGENT_APPMGMT_IFNAME)).thenReturn(interfaceDetailList);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_01(), "One BusObject implementing org.alljoyn.gwagent.ctrl.AppMgmt not found on bus expected:<1> but was:<2>");
    }

    @Test
    public void testValidateGwAgentBusObjectsWhenOnlyAclMgmtPresent() throws Exception
    {
        List<InterfaceDetail> appMgmtDetailList = getInterfaceDetailList(GWAgentTestSuite.GWAGENT_APPMGMT_PATH);
        when(mockIntrospector.getInterfacesExposedOnBusBasedOnName(GWAgentTestSuite.GWAGENT_APPMGMT_IFNAME)).thenReturn(appMgmtDetailList);

        when(mockIntrospector.getInterfacesExposedOnBusUnderSpecifiedPathBasedOnName(GWAgentTestSuite.GWAGENT_APPMGMT_PATH, GWAgentTestSuite.GWAGENT_CTRLAPP_IFNAME)).thenReturn(
                new ArrayList<InterfaceDetail>());

        List<InterfaceDetail> ctrlAclMgmtDetailList = getInterfaceDetailList(CONNECTOR_APP1_PATH);
        when(mockIntrospector.getInterfacesExposedOnBusUnderSpecifiedPathBasedOnName(GWAgentTestSuite.GWAGENT_APPMGMT_PATH, GWAgentTestSuite.GWAGENT_CTRLACLMGMT_IFNAME))
                .thenReturn(ctrlAclMgmtDetailList);

        when(mockIntrospector.isInterfacePresent(CONNECTOR_APP1_PATH, GWAgentTestSuite.GWAGENT_CTRLAPP_IFNAME)).thenReturn(false);
        when(mockIntrospector.isInterfacePresent(CONNECTOR_APP1_PATH, GWAgentTestSuite.GWAGENT_CTRLACLMGMT_IFNAME)).thenReturn(true);
        executeTestMethodFailsAssertion(getTestWrapperFor_v1_01(), "BusObject at /gw/connectorApp1 must implement org.alljoyn.gwagent.ctrl.App");
    }

    @Test
    public void testValidateGwAgentBusObjectsWhenOnlyAppPresent() throws Exception
    {
        List<InterfaceDetail> appMgmtDetailList = getInterfaceDetailList(GWAgentTestSuite.GWAGENT_APPMGMT_PATH);
        when(mockIntrospector.getInterfacesExposedOnBusBasedOnName(GWAgentTestSuite.GWAGENT_APPMGMT_IFNAME)).thenReturn(appMgmtDetailList);

        List<InterfaceDetail> ctrlAppDetailList = getInterfaceDetailList(CONNECTOR_APP1_PATH);
        when(mockIntrospector.getInterfacesExposedOnBusUnderSpecifiedPathBasedOnName(GWAgentTestSuite.GWAGENT_APPMGMT_PATH, GWAgentTestSuite.GWAGENT_CTRLAPP_IFNAME)).thenReturn(
                ctrlAppDetailList);

        when(mockIntrospector.getInterfacesExposedOnBusUnderSpecifiedPathBasedOnName(GWAgentTestSuite.GWAGENT_APPMGMT_PATH, GWAgentTestSuite.GWAGENT_CTRLACLMGMT_IFNAME))
                .thenReturn(new ArrayList<InterfaceDetail>());

        when(mockIntrospector.isInterfacePresent(CONNECTOR_APP1_PATH, GWAgentTestSuite.GWAGENT_CTRLAPP_IFNAME)).thenReturn(true);
        when(mockIntrospector.isInterfacePresent(CONNECTOR_APP1_PATH, GWAgentTestSuite.GWAGENT_CTRLACLMGMT_IFNAME)).thenReturn(false);
        executeTestMethodFailsAssertion(getTestWrapperFor_v1_01(), "BusObject at /gw/connectorApp1 must implement org.alljoyn.gwagent.ctrl.AclMgmt");
    }

    @Test
    public void testValidateGwAgentBusObjectsNoAcls() throws Exception
    {
        List<InterfaceDetail> appMgmtDetailList = getInterfaceDetailList(GWAgentTestSuite.GWAGENT_APPMGMT_PATH);
        when(mockIntrospector.getInterfacesExposedOnBusBasedOnName(GWAgentTestSuite.GWAGENT_APPMGMT_IFNAME)).thenReturn(appMgmtDetailList);

        List<InterfaceDetail> ctrlAppDetailList = getInterfaceDetailList(CONNECTOR_APP1_PATH);
        when(mockIntrospector.getInterfacesExposedOnBusUnderSpecifiedPathBasedOnName(GWAgentTestSuite.GWAGENT_APPMGMT_PATH, GWAgentTestSuite.GWAGENT_CTRLAPP_IFNAME)).thenReturn(
                ctrlAppDetailList);

        List<InterfaceDetail> ctrlAclMgmtDetailList = getInterfaceDetailList(CONNECTOR_APP1_PATH);
        when(mockIntrospector.getInterfacesExposedOnBusUnderSpecifiedPathBasedOnName(GWAgentTestSuite.GWAGENT_APPMGMT_PATH, GWAgentTestSuite.GWAGENT_CTRLACLMGMT_IFNAME))
                .thenReturn(ctrlAclMgmtDetailList);

        when(mockIntrospector.getInterfacesExposedOnBusUnderSpecifiedPathBasedOnName(CONNECTOR_APP1_PATH, GWAgentTestSuite.GWAGENT_CTRLACL_IFNAME)).thenReturn(
                new ArrayList<InterfaceDetail>());

        when(mockIntrospector.isInterfacePresent(CONNECTOR_APP1_PATH, GWAgentTestSuite.GWAGENT_CTRLAPP_IFNAME)).thenReturn(true);
        when(mockIntrospector.isInterfacePresent(CONNECTOR_APP1_PATH, GWAgentTestSuite.GWAGENT_CTRLACLMGMT_IFNAME)).thenReturn(true);
        executeTestMethod(getTestWrapperFor_v1_01());
    }

    @Test
    public void testValidateGwAgentBusObjectsNoApps() throws Exception
    {
        List<InterfaceDetail> appMgmtDetailList = getInterfaceDetailList(GWAgentTestSuite.GWAGENT_APPMGMT_PATH);
        when(mockIntrospector.getInterfacesExposedOnBusBasedOnName(GWAgentTestSuite.GWAGENT_APPMGMT_IFNAME)).thenReturn(appMgmtDetailList);

        when(mockIntrospector.getInterfacesExposedOnBusUnderSpecifiedPathBasedOnName(GWAgentTestSuite.GWAGENT_APPMGMT_PATH, GWAgentTestSuite.GWAGENT_CTRLAPP_IFNAME)).thenReturn(
                new ArrayList<InterfaceDetail>());

        when(mockIntrospector.getInterfacesExposedOnBusUnderSpecifiedPathBasedOnName(GWAgentTestSuite.GWAGENT_APPMGMT_PATH, GWAgentTestSuite.GWAGENT_CTRLACLMGMT_IFNAME))
                .thenReturn(new ArrayList<InterfaceDetail>());

        executeTestMethod(getTestWrapperFor_v1_01());
    }

    @Test
    public void testExceptionFromInitialize() throws Exception
    {
        BusException be = new BusException();
        doThrow(be).when(mockServiceHelper).initialize(anyString(), anyString(), any(UUID.class));

        try
        {
            executeTestMethod(getTestWrapperFor_v1_01());
            fail();
        }
        catch (BusException e)
        {
            assertEquals(be, e);
        }
        verify(mockServiceHelper).release();
    }

    @Test
    public void testExceptionFromConnectAboutClient() throws Exception
    {
        BusException be = new BusException("Failed to connect AboutClient to client: FAIL");
        doThrow(be).when(mockServiceHelper).connectAboutClient(mockAboutAnnouncement);

        try
        {
            executeTestMethod(getTestWrapperFor_v1_01());
            fail();
        }
        catch (Exception e)
        {
            assertEquals(be, e);
        }
        verify(mockServiceHelper).release();
    }

    private List<InterfaceDetail> getInterfaceDetailList(String path)
    {
        InterfaceDetail interfaceDetail = Mockito.mock(InterfaceDetail.class);
        when(interfaceDetail.getPath()).thenReturn(path);
        List<InterfaceDetail> interfaceDetailList = new ArrayList<InterfaceDetail>();
        interfaceDetailList.add(interfaceDetail);

        return interfaceDetailList;
    }

}