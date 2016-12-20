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
 *     THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL
 *     WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED
 *     WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE
 *     AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL
 *     DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR
 *     PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER
 *     TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 *     PERFORMANCE OF THIS SOFTWARE.
 *******************************************************************************/
package org.alljoyn.validation.testing.suites.controlpanel;

import static org.alljoyn.validation.testing.utils.controlpanel.InterfacePathPattern.ControlPanel;
import static org.alljoyn.validation.testing.utils.controlpanel.InterfacePathPattern.HttpControl;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.ParserConfigurationException;

import org.alljoyn.about.client.AboutClient;
import org.alljoyn.about.icon.AboutIconClient;
import org.alljoyn.about.transport.AboutTransport;
import org.alljoyn.bus.BusAttachment;
import org.alljoyn.bus.BusException;
import org.alljoyn.bus.ErrorReplyBusException;
import org.alljoyn.bus.Variant;
import org.alljoyn.ioe.controlpanelservice.communication.interfaces.ActionControl;
import org.alljoyn.ioe.controlpanelservice.communication.interfaces.ActionControlSecured;
import org.alljoyn.ioe.controlpanelservice.communication.interfaces.AlertDialog;
import org.alljoyn.ioe.controlpanelservice.communication.interfaces.AlertDialogSecured;
import org.alljoyn.ioe.controlpanelservice.communication.interfaces.Container;
import org.alljoyn.ioe.controlpanelservice.communication.interfaces.ContainerSecured;
import org.alljoyn.ioe.controlpanelservice.communication.interfaces.ControlPanel;
import org.alljoyn.ioe.controlpanelservice.communication.interfaces.HTTPControl;
import org.alljoyn.ioe.controlpanelservice.communication.interfaces.Label;
import org.alljoyn.ioe.controlpanelservice.communication.interfaces.ListPropertyControl;
import org.alljoyn.ioe.controlpanelservice.communication.interfaces.ListPropertyControlSecured;
import org.alljoyn.ioe.controlpanelservice.communication.interfaces.NotificationAction;
import org.alljoyn.ioe.controlpanelservice.communication.interfaces.PropertyControl;
import org.alljoyn.ioe.controlpanelservice.communication.interfaces.PropertyControlSecured;
import org.alljoyn.ioe.controlpanelservice.ui.ajstruct.ListPropertyWidgetRecordAJ;
import org.alljoyn.validation.framework.AppUnderTestDetails;
import org.alljoyn.validation.framework.ValidationTestContext;
import org.alljoyn.validation.framework.utils.introspection.BusIntrospector;
import org.alljoyn.validation.framework.utils.introspection.XmlBasedBusIntrospector;
import org.alljoyn.validation.framework.utils.introspection.bean.InterfaceDetail;
import org.alljoyn.validation.framework.utils.introspection.bean.IntrospectionInterface;
import org.alljoyn.validation.framework.utils.introspection.bean.IntrospectionNode;
import org.alljoyn.validation.framework.utils.introspection.bean.NodeDetail;
import org.alljoyn.validation.testing.suites.BaseTestSuiteTest;
import org.alljoyn.validation.testing.suites.MyRobolectricTestRunner;
import org.alljoyn.validation.testing.utils.InterfaceValidator;
import org.alljoyn.validation.testing.utils.about.AboutAnnouncementDetails;
import org.alljoyn.validation.testing.utils.services.ServiceHelper;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.annotation.Config;
import org.xml.sax.SAXException;

@RunWith(MyRobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class ControlPanelTestSuiteTest extends BaseTestSuiteTest
{
    private static final String SECURED_DIALOG_MESSAGE = "secured_message";
    private static final String DIALOG_MESSAGE = "message";
    private static final String CONTROL_PANEL_INTERFACE_NAME = "org.alljoyn.ControlPanel.ControlPanel";
    private static final String CONTAINER_INTERFACE_NAME = "org.alljoyn.ControlPanel.Container";
    private static final String SECURED_CONTAINER_INTERFACE_NAME = "org.alljoyn.ControlPanel.SecuredContainer";
    private static final String PROPERTY_INTERFACE_NAME = "org.alljoyn.ControlPanel.Property";
    private static final String SECURED_PROPERTY_INTERFACE_NAME = "org.alljoyn.ControlPanel.SecuredProperty";
    private static final String LABEL_PROPERTY_INTERFACE_NAME = "org.alljoyn.ControlPanel.LabelProperty";
    private static final String ACTION_INTERFACE_NAME = "org.alljoyn.ControlPanel.Action";
    private static final String SECURED_ACTION_INTERFACE_NAME = "org.alljoyn.ControlPanel.SecuredAction";
    private static final String DIALOG_INTERFACE_NAME = "org.alljoyn.ControlPanel.Dialog";
    private static final String SECURED_DIALOG_INTERFACE_NAME = "org.alljoyn.ControlPanel.SecuredDialog";
    private static final String LIST_PROPERTY_INTERFACE_NAME = "org.alljoyn.ControlPanel.ListProperty";
    private static final String SECURED_LIST_PROPERTY_INTERFACE_NAME = "org.alljoyn.ControlPanel.SecuredListProperty";
    private static final String NOTIFICATION_ACTION_INTERFACE_NAME = "org.alljoyn.ControlPanel.NotificationAction";

    private static final String CONTROL_PANEL_PATH = "/ControlPanel/unit/panelName";
    private static final String CONTROL_PANEL_CONTAINER_PATH = "/ControlPanel/unit/panelName/language/containerName";
    private static final String CONTROL_PANEL_DIALOG_PATH = "/ControlPanel/unit/panelName/language/dialog";
    private static final String HTTP_CONTROL_PATH = "/ControlPanel/unit/HTTPControl";
    private static final String CONTROL_PANEL_PROPERTY_PATH = "/ControlPanel/unit/panelName/language/objectName";
    private static final String CONTROL_PANEL_LABEL_PROPERTY_PATH = "/ControlPanel/unit/panelName/language/objectName";
    private static final String CONTROL_PANEL_ACTION_PATH = "/ControlPanel/unit/panelName/language/objectName";
    private static final String CONTROL_PANEL_LIST_PROPERTY_PATH = "/ControlPanel/unit/panelName/language/objectName";
    private static final String NOTIFICATION_ACTION_PATH = CONTROL_PANEL_PATH;
    private static final String NOTIFICATION_ACTION_CONTAINER_PATH = CONTROL_PANEL_CONTAINER_PATH;

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
    @Mock
    private HttpClient mockHttpClient;

    private ControlPanelTestSuite controlPanelTestSuite;
    protected Exception thrownException;
    private String deviceId = "deviceId";
    private UUID appId = UUID.randomUUID();
    private AppUnderTestDetails appUnderTestDetails;

    @Before
    public void setup() throws Exception
    {
        MockitoAnnotations.initMocks(this);

        appUnderTestDetails = new AppUnderTestDetails(appId, deviceId);
        when(mockTestContext.getAppUnderTestDetails()).thenReturn(appUnderTestDetails);

        when(mockAboutAnnouncement.supportsInterface(AboutTransport.INTERFACE_NAME)).thenReturn(true);
        constructControlPanelTestSuite();
        setupMockServiceHelper();
    }

    @Test
    public void testGetHttpClient()
    {
        controlPanelTestSuite = new ControlPanelTestSuite();
        HttpClient httpClient = controlPanelTestSuite.getHttpClient();

        assertTrue(httpClient instanceof DefaultHttpClient);
        assertTrue(httpClient != controlPanelTestSuite.getHttpClient());
    }

    @Test
    public void testGetServiceHelper()
    {
        controlPanelTestSuite = new ControlPanelTestSuite();
        ServiceHelper serviceHelper = controlPanelTestSuite.getServiceHelper();

        assertNotNull(serviceHelper);
        assertTrue(serviceHelper != controlPanelTestSuite.getServiceHelper());
    }

    @Test
    public void testGetIntrospector() throws Exception
    {
        controlPanelTestSuite = new ControlPanelTestSuite()
        {
            @Override
            public ServiceHelper getServiceHelper()
            {
                return mockServiceHelper;
            }
        };
        controlPanelTestSuite.setValidationTestContext(mockTestContext);
        controlPanelTestSuite.setUp();
        BusIntrospector busIntrospector = controlPanelTestSuite.getIntrospector();

        assertNotNull(busIntrospector);
        assertEquals(busIntrospector, controlPanelTestSuite.getIntrospector());
    }

    @Test
    public void testValidateHttpControlInterfacePath()
    {
        assertFalse(controlPanelTestSuite.isValidPath(HttpControl.getValue(), ""));
        assertFalse(controlPanelTestSuite.isValidPath(HttpControl.getValue(), "/ControlPanel/HTTPControl"));
        assertFalse(controlPanelTestSuite.isValidPath(HttpControl.getValue(), "/ControlPanel/unit"));
        assertFalse(controlPanelTestSuite.isValidPath(HttpControl.getValue(), "/unit/HTTPControl"));
        assertFalse(controlPanelTestSuite.isValidPath(HttpControl.getValue(), "/ControlPanelHTTPControl"));
        assertFalse(controlPanelTestSuite.isValidPath(HttpControl.getValue(), "ControlPanel/unit/HTTPControl"));
        assertFalse(controlPanelTestSuite.isValidPath(HttpControl.getValue(), "/ControlPanel//HTTPControl"));
        assertFalse(controlPanelTestSuite.isValidPath(HttpControl.getValue(), "/controlPanel/unit/httpControl"));
        assertFalse(controlPanelTestSuite.isValidPath(HttpControl.getValue(), "/ControlPanel/unit1/unit2/HTTPControl"));
        assertTrue(controlPanelTestSuite.isValidPath(HttpControl.getValue(), HTTP_CONTROL_PATH));
    }

    @Test
    public void testValidateControlPanelInterfacePath()
    {
        assertFalse(controlPanelTestSuite.isValidPath(ControlPanel.getValue(), ""));
        assertFalse(controlPanelTestSuite.isValidPath(ControlPanel.getValue(), "/ControlPanel"));
        assertFalse(controlPanelTestSuite.isValidPath(ControlPanel.getValue(), "/ControlPanel/mockUnit"));
        assertTrue(controlPanelTestSuite.isValidPath(ControlPanel.getValue(), "/ControlPanel/mockUnit/mockPanelName"));
        assertFalse(controlPanelTestSuite.isValidPath(ControlPanel.getValue(), "/ControlPanel/mockUnit//mockPanelName/"));
        assertFalse(controlPanelTestSuite.isValidPath(ControlPanel.getValue(), "/ControlPanel/mockUnit/mockPanelName/"));
        assertFalse(controlPanelTestSuite.isValidPath(ControlPanel.getValue(), "/ControlPanel/mockUnit/mockPanelName/language"));
        assertFalse(controlPanelTestSuite.isValidPath(ControlPanel.getValue(), "/ControlPanel/mockUnit/mockPanelName/language/containerName"));
    }

    @Test
    public void testValidateControlPanelBusObjectsAddsNoteWhenNoneFound() throws Exception
    {
        when(mockIntrospector.getInterfacesExposedOnBusBasedOnName(CONTROL_PANEL_INTERFACE_NAME)).thenReturn(new ArrayList<InterfaceDetail>());

        executeTestMethod(getTestWrapperFor_v1_01());
        verify(mockTestContext).addNote("No bus objects implement ControlPanel interface");
    }

    @Test
    public void testValidateControlPanelBusObjectsFailsIfFoundAtInvalidPath() throws Exception
    {
        List<InterfaceDetail> interfaceDetailList = getInterfaceDetailList("invalid_path");
        when(mockIntrospector.getInterfacesExposedOnBusBasedOnName(CONTROL_PANEL_INTERFACE_NAME)).thenReturn(interfaceDetailList);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_01(), "invalid_path does not match the expected pattern /ControlPanel/{unit}/{panelName}");
    }

    @Test
    public void testValidateControlPanelBusObjectsFailsWhenVersionDoesNotMatch() throws Exception
    {
        ControlPanel controlPanel = getMockControlPanel((short) 2);
        List<InterfaceDetail> interfaceDetailList = getInterfaceDetailList(CONTROL_PANEL_PATH);
        when(mockIntrospector.getInterfacesExposedOnBusBasedOnName(CONTROL_PANEL_INTERFACE_NAME)).thenReturn(interfaceDetailList);
        when(mockIntrospector.getInterface(CONTROL_PANEL_PATH, ControlPanel.class)).thenReturn(controlPanel);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_01(), "Interface version does not match expected:<1> but was:<2>");
    }

    @Test
    public void testValidateControlPanelBusObjectsFailsIfNoContainerObjectExistsUnderPanel() throws Exception
    {
        ControlPanel controlpanel = getMockControlPanel((short) 1);
        List<InterfaceDetail> interfaceDetailList = getInterfaceDetailList(CONTROL_PANEL_PATH);
        when(mockIntrospector.getInterfacesExposedOnBusBasedOnName(CONTROL_PANEL_INTERFACE_NAME)).thenReturn(interfaceDetailList);
        when(mockIntrospector.getInterface(CONTROL_PANEL_PATH, ControlPanel.class)).thenReturn(controlpanel);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_01(),
                "No object implementing org.alljoyn.ControlPanel.Container nor org.alljoyn.ControlPanel.SecuredContainer is under path /ControlPanel/unit/panelName");
    }

    @Test
    public void testValidateControlPanelBusObjectsPassesIfValidContainerObjectExistsUnderPanel() throws Exception
    {
        ControlPanel controlpanel = getMockControlPanel((short) 1);
        List<InterfaceDetail> interfaceDetailList = getInterfaceDetailList(CONTROL_PANEL_PATH);
        when(mockIntrospector.getInterfacesExposedOnBusBasedOnName(CONTROL_PANEL_INTERFACE_NAME)).thenReturn(interfaceDetailList);
        when(mockIntrospector.getInterface(CONTROL_PANEL_PATH, ControlPanel.class)).thenReturn(controlpanel);
        List<InterfaceDetail> containerInterfaceDetailList = getInterfaceDetailList(CONTROL_PANEL_CONTAINER_PATH);
        when(mockIntrospector.getInterfacesExposedOnBusUnderSpecifiedPathBasedOnName(CONTROL_PANEL_PATH, CONTAINER_INTERFACE_NAME)).thenReturn(containerInterfaceDetailList);

        executeTestMethod(getTestWrapperFor_v1_01());
    }

    @Test
    public void testValidateControlPanelBusObjectsPassesIfValidSecuredContainerObjectExistsUnderPanel() throws Exception
    {
        ControlPanel controlpanel = getMockControlPanel((short) 1);
        List<InterfaceDetail> interfaceDetailList = getInterfaceDetailList(CONTROL_PANEL_PATH);
        when(mockIntrospector.getInterfacesExposedOnBusBasedOnName(CONTROL_PANEL_INTERFACE_NAME)).thenReturn(interfaceDetailList);
        when(mockIntrospector.getInterface(CONTROL_PANEL_PATH, ControlPanel.class)).thenReturn(controlpanel);
        List<InterfaceDetail> securedContainerInterfaceDetailList = getInterfaceDetailList(CONTROL_PANEL_CONTAINER_PATH);
        when(mockIntrospector.getInterfacesExposedOnBusUnderSpecifiedPathBasedOnName(CONTROL_PANEL_PATH, SECURED_CONTAINER_INTERFACE_NAME)).thenReturn(
                securedContainerInterfaceDetailList);

        executeTestMethod(getTestWrapperFor_v1_01());
    }

    @Test
    public void testValidateContainerBusObjectsAddsNoteWhenNoneFound() throws Exception
    {
        when(mockIntrospector.getInterfacesExposedOnBusBasedOnName(CONTAINER_INTERFACE_NAME)).thenReturn(new ArrayList<InterfaceDetail>());
        when(mockIntrospector.getInterfacesExposedOnBusBasedOnName(SECURED_CONTAINER_INTERFACE_NAME)).thenReturn(new ArrayList<InterfaceDetail>());

        executeTestMethod(getTestWrapperFor_v1_02());
        verify(mockTestContext).addNote("No bus objects implement Container nor SecuredContainer interfaces");
    }

    @Test
    public void testValidateContainerBusObjectsFailsWhenValidAncestorIsNotFoundForContainer() throws Exception
    {
        Container container = getMockContainer((short) 1, 0, new HashMap<Short, Variant>());
        setupDataForValidateContainerBusObjectsAndStartTest(container, org.alljoyn.ioe.controlpanelservice.communication.interfaces.ControlPanel.IFNAME, false);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_02(),
                "No parent bus object that implements ControlPanel nor NotificationAction nor Container nor SecuredContainer nor ListProperty nor SecuredListProperty interface found");
    }

    @Test
    public void testValidateContainerBusObjectsFailsWhenValidAncestorIsNotFoundForSecuredContainer() throws Exception
    {
        ContainerSecured securedContainer = getMockSecuredContainer((short) 1, 0, new HashMap<Short, Variant>());
        setupDataForValidateContainerBusObjectsAndStartTest(securedContainer, org.alljoyn.ioe.controlpanelservice.communication.interfaces.ControlPanel.IFNAME, false);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_02(),
                "No parent bus object that implements ControlPanel nor NotificationAction nor Container nor SecuredContainer nor ListProperty nor SecuredListProperty interface found");
    }

    @Test
    public void testValidateContainerBusObjectsFailsWhenContainerVersionDoesNotMatch() throws Exception
    {
        Container container = getMockContainer((short) 2, 0, new HashMap<Short, Variant>());
        setupDataForValidateContainerBusObjectsAndStartTest(container);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_02(), "Interface version does not match expected:<1> but was:<2>");
    }

    @Test
    public void testValidateContainerBusObjectsFailsWhenSecuredContainerVersionDoesNotMatch() throws Exception
    {
        ContainerSecured securedContainer = getMockSecuredContainer((short) 2, 0, new HashMap<Short, Variant>());
        setupDataForValidateContainerBusObjectsAndStartTest(securedContainer);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_02(), "Interface version does not match expected:<1> but was:<2>");
    }

    @Test
    public void testValidateContainerBusObjectsFailsWhenContainerStatesPropertyDoesNotMatch() throws Exception
    {
        Container container = getMockContainer((short) 1, 2, new HashMap<Short, Variant>());
        setupDataForValidateContainerBusObjectsAndStartTest(container);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_02(), "Interface state 2 does not equals expected value of 0 or 1");
    }

    @Test
    public void testValidateContainerBusObjectsFailsWhenSecuredContainerStatesPropertyDoesNotMatch() throws Exception
    {
        ContainerSecured securedContainer = getMockSecuredContainer((short) 1, 2, new HashMap<Short, Variant>());
        setupDataForValidateContainerBusObjectsAndStartTest(securedContainer);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_02(), "Interface state 2 does not equals expected value of 0 or 1");
    }

    @Test
    public void testValidateContainerBusObjectsFailsWhenContainerParametersContainsInvalidValueForKey0() throws Exception
    {
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 0, new Variant(5));
        Container container = getMockContainer((short) 1, 0, parameters);
        setupDataForValidateContainerBusObjectsAndStartTest(container);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_02(), "Signature does not match for key 0 expected:<[s]> but was:<[i]>");
    }

    @Test
    public void testValidateContainerBusObjectsFailsWhenSecuredContainerParametersContainsInvalidValueForKey0() throws Exception
    {
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 0, new Variant(5));
        ContainerSecured securedContainer = getMockSecuredContainer((short) 1, 1, parameters);
        setupDataForValidateContainerBusObjectsAndStartTest(securedContainer);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_02(), "Signature does not match for key 0 expected:<[s]> but was:<[i]>");
    }

    @Test
    public void testValidateContainerBusObjectsFailsWhenContainerParametersContainsInvalidValueForKey1() throws Exception
    {
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 1, new Variant("invalid"));
        Container container = getMockContainer((short) 1, 0, parameters);
        setupDataForValidateContainerBusObjectsAndStartTest(container);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_02(), "Signature does not match for key 1 expected:<[u]> but was:<[s]>");
    }

    @Test
    public void testValidateContainerBusObjectsFailsWhenSecuredContainerParametersContainsInvalidValueForKey1() throws Exception
    {
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 1, new Variant("invalid"));
        ContainerSecured securedContainer = getMockSecuredContainer((short) 1, 1, parameters);
        setupDataForValidateContainerBusObjectsAndStartTest(securedContainer);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_02(), "Signature does not match for key 1 expected:<[u]> but was:<[s]>");
    }

    @Test
    public void testValidateContainerBusObjectsFailsWhenContainerParametersContainsInvalidValueForKey2() throws Exception
    {
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 2, new Variant("invalid"));
        Container container = getMockContainer((short) 1, 0, parameters);
        setupDataForValidateContainerBusObjectsAndStartTest(container);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_02(), "Signature does not match for key 2 expected:<[aq]> but was:<[s]>");
    }

    @Test
    public void testValidateContainerBusObjectsFailsWhenSecuredContainerParametersContainsInvalidValueForKey2() throws Exception
    {
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 2, new Variant("invalid"));
        ContainerSecured securedContainer = getMockSecuredContainer((short) 1, 1, parameters);
        setupDataForValidateContainerBusObjectsAndStartTest(securedContainer);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_02(), "Signature does not match for key 2 expected:<[aq]> but was:<[s]>");
    }

    @Test
    public void testValidateContainerBusObjectsFailsWhenContainerParametersIsMissingValueForKey2() throws Exception
    {
        short[] layoutHints = null;
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 2, new Variant(layoutHints));
        Container container = getMockContainer((short) 1, 0, parameters);
        setupDataForValidateContainerBusObjectsAndStartTest(container);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_02(), "Signature does not match for key 2 expected:<aq> but was:<null>");
    }

    @Test
    public void testValidateContainerBusObjectsFailsWhenSecuredContainerParametersIsMissingValueForKey2() throws Exception
    {
        short[] layoutHints = null;
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 2, new Variant(layoutHints));
        ContainerSecured securedContainer = getMockSecuredContainer((short) 1, 1, parameters);
        setupDataForValidateContainerBusObjectsAndStartTest(securedContainer);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_02(), "Signature does not match for key 2 expected:<aq> but was:<null>");
    }

    @Test
    public void testValidateContainerBusObjectsFailsWhenContainerParametersHasInvalidValueForKey2() throws Exception
    {
        short[] layoutHints = new short[1];
        layoutHints[0] = 0;
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 2, new Variant(layoutHints, "aq"));
        Container container = getMockContainer((short) 1, 0, parameters);
        setupDataForValidateContainerBusObjectsAndStartTest(container);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_02(), "0 does not match expected value of 1 or 2 for key 2");
    }

    @Test
    public void testValidateContainerBusObjectsFailsWhenSecuredContainerParametersHasInvalidValueForKey2() throws Exception
    {
        short[] layoutHints = new short[1];
        layoutHints[0] = 0;
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 2, new Variant(layoutHints, "aq"));
        ContainerSecured securedContainer = getMockSecuredContainer((short) 1, 1, parameters);
        setupDataForValidateContainerBusObjectsAndStartTest(securedContainer);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_02(), "0 does not match expected value of 1 or 2 for key 2");
    }

    @Test
    public void testValidateContainerBusObjectsPassesWhenContainerHasAllValidValues() throws Exception
    {
        short[] layoutHints = new short[1];
        layoutHints[0] = 1;
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 0, new Variant("string"));
        parameters.put((short) 1, new Variant(5, "u"));
        parameters.put((short) 2, new Variant(layoutHints, "aq"));
        Container container = getMockContainer((short) 1, 0, parameters);
        setupDataForValidateContainerBusObjectsAndStartTest(container);

        executeTestMethod(getTestWrapperFor_v1_02());
    }

    @Test
    public void testValidateContainerBusObjectsPassesWhenSecuredContainerHasAllValidValues() throws Exception
    {
        short[] layoutHints = new short[1];
        layoutHints[0] = 2;
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 0, new Variant("string"));
        parameters.put((short) 1, new Variant(5, "u"));
        parameters.put((short) 2, new Variant(layoutHints, "aq"));
        ContainerSecured securedContainer = getMockSecuredContainer((short) 1, 1, parameters);
        setupDataForValidateContainerBusObjectsAndStartTest(securedContainer);

        executeTestMethod(getTestWrapperFor_v1_02());
    }

    @Test
    public void testValidateContainerBusObjectsPassesWhenContainerParametersHasMoreThanOneValueForKey2() throws Exception
    {
        short[] layoutHints = new short[2];
        layoutHints[0] = 2;
        layoutHints[1] = 1;
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 2, new Variant(layoutHints, "aq"));
        Container container = getMockContainer((short) 1, 0, parameters);
        setupDataForValidateContainerBusObjectsAndStartTest(container);

        executeTestMethod(getTestWrapperFor_v1_02());
    }

    @Test
    public void testValidateContainerBusObjectsPassesWhenSecuredContainerParametersHasMoreThanOneValueForKey2() throws Exception
    {
        short[] layoutHints = new short[2];
        layoutHints[0] = 2;
        layoutHints[1] = 1;
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 2, new Variant(layoutHints, "aq"));
        ContainerSecured securedContainer = getMockSecuredContainer((short) 1, 1, parameters);
        setupDataForValidateContainerBusObjectsAndStartTest(securedContainer);

        executeTestMethod(getTestWrapperFor_v1_02());
    }

    @Test
    public void testValidateContainerBusObjectsPassesWhenContainerIsMissingKey0And1And2() throws Exception
    {
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        Container container = getMockContainer((short) 1, 0, parameters);
        setupDataForValidateContainerBusObjectsAndStartTest(container);

        executeTestMethod(getTestWrapperFor_v1_02());
    }

    @Test
    public void testValidateContainerBusObjectsPassesWhenSecuredContainerIsMissingKey0And1And2() throws Exception
    {
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        ContainerSecured securedContainer = getMockSecuredContainer((short) 1, 1, parameters);
        setupDataForValidateContainerBusObjectsAndStartTest(securedContainer);

        executeTestMethod(getTestWrapperFor_v1_02());
    }

    @Test
    public void testValidatePropertyBusObjectsAddsNoteWhenNoneFound() throws Exception
    {
        when(mockIntrospector.getInterfacesExposedOnBusBasedOnName(PROPERTY_INTERFACE_NAME)).thenReturn(new ArrayList<InterfaceDetail>());
        when(mockIntrospector.getInterfacesExposedOnBusBasedOnName(SECURED_PROPERTY_INTERFACE_NAME)).thenReturn(new ArrayList<InterfaceDetail>());

        executeTestMethod(getTestWrapperFor_v1_03());
        verify(mockTestContext).addNote("No bus objects implement Property nor SecuredProperty interfaces");
    }

    @Test
    public void testValidatePropertyBusObjectsFailsWhenValidAncestorIsNotFoundForProperty() throws Exception
    {
        PropertyControl property = getMockProperty((short) 1, 0, new HashMap<Short, Variant>(), new Variant("value"));
        setupDataForValidatePropertyBusObjectsAndStartTest(property, Container.IFNAME, false);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_03(), "No parent bus object that implements Container nor SecuredContainer interface found");
    }

    @Test
    public void testValidatePropertyBusObjectsFailsWhenValidAncestorIsNotFoundForSecuredProperty() throws Exception
    {
        PropertyControlSecured securedProperty = getMockSecuredProperty((short) 1, 0, new HashMap<Short, Variant>(), new Variant("value"));
        setupDataForValidatePropertyBusObjectsAndStartTest(securedProperty, Container.IFNAME, false);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_03(), "No parent bus object that implements Container nor SecuredContainer interface found");
    }

    @Test
    public void testValidatePropertyBusObjectsFailsWhenPropertyVersionDoesNotMatch() throws Exception
    {
        PropertyControl property = getMockProperty((short) 2, 0, new HashMap<Short, Variant>(), new Variant("value"));
        setupDataForValidatePropertyBusObjectsAndStartTest(property);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_03(), "Interface version does not match expected:<1> but was:<2>");
    }

    @Test
    public void testValidatePropertyBusObjectsFailsWhenSecuredPropertyVersionDoesNotMatch() throws Exception
    {
        PropertyControlSecured securedProperty = getMockSecuredProperty((short) 2, 0, new HashMap<Short, Variant>(), new Variant("value"));
        setupDataForValidatePropertyBusObjectsAndStartTest(securedProperty);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_03(), "Interface version does not match expected:<1> but was:<2>");
    }

    @Test
    public void testValidatePropertyBusObjectsFailsWhenPropertyStatesPropertyDoesNotMatch() throws Exception
    {
        PropertyControl property = getMockProperty((short) 1, 4, new HashMap<Short, Variant>(), new Variant("value"));
        setupDataForValidatePropertyBusObjectsAndStartTest(property);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_03(), "Interface state 4 does not equals expected value of 0, 1, 2 or 3");
    }

    @Test
    public void testValidatePropertyBusObjectsFailsWhenSecuredPropertyStatesPropertyDoesNotMatch() throws Exception
    {
        PropertyControlSecured securedProperty = getMockSecuredProperty((short) 1, 4, new HashMap<Short, Variant>(), new Variant("value"));
        setupDataForValidatePropertyBusObjectsAndStartTest(securedProperty);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_03(), "Interface state 4 does not equals expected value of 0, 1, 2 or 3");
    }

    @Test
    public void testValidatePropertyBusObjectsFailsWhenPropertyParametersContainsInvalidValueForKey0() throws Exception
    {
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 0, new Variant(5));
        PropertyControl property = getMockProperty((short) 1, 0, parameters, new Variant("value"));
        setupDataForValidatePropertyBusObjectsAndStartTest(property);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_03(), "Signature does not match for key 0 expected:<[s]> but was:<[i]>");
    }

    @Test
    public void testValidatePropertyBusObjectsFailsWhenSecuredPropertyParametersContainsInvalidValueForKey0() throws Exception
    {
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 0, new Variant(5));
        PropertyControlSecured securedProperty = getMockSecuredProperty((short) 1, 1, parameters, new Variant("value"));
        setupDataForValidatePropertyBusObjectsAndStartTest(securedProperty);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_03(), "Signature does not match for key 0 expected:<[s]> but was:<[i]>");
    }

    @Test
    public void testValidatePropertyBusObjectsFailsWhenPropertyParametersContainsInvalidValueForKey1() throws Exception
    {
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 1, new Variant("invalid"));
        PropertyControl property = getMockProperty((short) 1, 0, parameters, new Variant("value"));
        setupDataForValidatePropertyBusObjectsAndStartTest(property);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_03(), "Signature does not match for key 1 expected:<[u]> but was:<[s]>");
    }

    @Test
    public void testValidatePropertyBusObjectsFailsWhenSecuredPropertyParametersContainsInvalidValueForKey1() throws Exception
    {
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 1, new Variant("invalid"));
        PropertyControlSecured securedProperty = getMockSecuredProperty((short) 1, 1, parameters, new Variant("value"));
        setupDataForValidatePropertyBusObjectsAndStartTest(securedProperty);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_03(), "Signature does not match for key 1 expected:<[u]> but was:<[s]>");
    }

    @Test
    public void testValidatePropertyBusObjectsFailsWhenPropertyParametersContainsInvalidValueForKey2() throws Exception
    {
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 2, new Variant("invalid"));
        PropertyControl property = getMockProperty((short) 1, 0, parameters, new Variant("value"));
        setupDataForValidatePropertyBusObjectsAndStartTest(property);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_03(), "Signature does not match for key 2 expected:<[aq]> but was:<[s]>");
    }

    @Test
    public void testValidatePropertyBusObjectsFailsWhenSecuredPropertyParametersContainsInvalidValueForKey2() throws Exception
    {
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 2, new Variant("invalid"));
        PropertyControlSecured securedProperty = getMockSecuredProperty((short) 1, 1, parameters, new Variant("value"));
        setupDataForValidatePropertyBusObjectsAndStartTest(securedProperty);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_03(), "Signature does not match for key 2 expected:<[aq]> but was:<[s]>");
    }

    @Test
    public void testValidatePropertyBusObjectsFailsWhenPropertyParametersIsMissingValueForKey2() throws Exception
    {
        short[] layoutHints = null;
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 2, new Variant(layoutHints));
        PropertyControl property = getMockProperty((short) 1, 0, parameters, new Variant("value"));
        setupDataForValidatePropertyBusObjectsAndStartTest(property);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_03(), "Signature does not match for key 2 expected:<aq> but was:<null>");
    }

    @Test
    public void testValidatePropertyBusObjectsFailsWhenSecuredPropertyParametersIsMissingValueForKey2() throws Exception
    {
        short[] layoutHints = null;
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 2, new Variant(layoutHints));
        PropertyControlSecured securedProperty = getMockSecuredProperty((short) 1, 1, parameters, new Variant("value"));
        setupDataForValidatePropertyBusObjectsAndStartTest(securedProperty);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_03(), "Signature does not match for key 2 expected:<aq> but was:<null>");
    }

    @Test
    public void testValidatePropertyBusObjectsFailsWhenPropertyParametersHasMoreThanOneValueForKey2() throws Exception
    {
        short[] layoutHints = new short[2];
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 2, new Variant(layoutHints, "aq"));
        PropertyControl property = getMockProperty((short) 1, 0, parameters, new Variant("value"));
        setupDataForValidatePropertyBusObjectsAndStartTest(property);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_03(), "Key 2 contains more than one value expected:<1> but was:<2>");
    }

    @Test
    public void testValidatePropertyBusObjectsFailsWhenSecuredPropertyParametersHasMoreThanOneValueForKey2() throws Exception
    {
        short[] layoutHints = new short[2];
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 2, new Variant(layoutHints, "aq"));
        PropertyControlSecured securedProperty = getMockSecuredProperty((short) 1, 1, parameters, new Variant("value"));
        setupDataForValidatePropertyBusObjectsAndStartTest(securedProperty);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_03(), "Key 2 contains more than one value expected:<1> but was:<2>");
    }

    @Test
    public void testValidatePropertyBusObjectsFailsWhenPropertyParametersHasInvalidValueForKey2() throws Exception
    {
        short[] layoutHints = new short[1];
        layoutHints[0] = 0;
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 2, new Variant(layoutHints, "aq"));
        PropertyControl property = getMockProperty((short) 1, 0, parameters, new Variant("value"));
        setupDataForValidatePropertyBusObjectsAndStartTest(property);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_03(), "0 is not a valid value for key 2");
    }

    @Test
    public void testValidatePropertyBusObjectsFailsWhenSecuredPropertyParametersHasInvalidValueForKey2() throws Exception
    {
        short[] layoutHints = new short[1];
        layoutHints[0] = 0;
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 2, new Variant(layoutHints, "aq"));
        PropertyControlSecured securedProperty = getMockSecuredProperty((short) 1, 1, parameters, new Variant("value"));
        setupDataForValidatePropertyBusObjectsAndStartTest(securedProperty);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_03(), "0 is not a valid value for key 2");
    }

    @Test
    public void testValidatePropertyBusObjectsPassesWhenPropertyIsMissingKey0And1And2And3And4And5() throws Exception
    {
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        PropertyControl property = getMockProperty((short) 1, 0, parameters, new Variant(true));
        setupDataForValidatePropertyBusObjectsAndStartTest(property);

        executeTestMethod(getTestWrapperFor_v1_03());
    }

    @Test
    public void testValidatePropertyBusObjectsPassesWhenSecuredPropertyIsMissingKey0And1And2And3And4And5() throws Exception
    {
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        PropertyControlSecured securedProperty = getMockSecuredProperty((short) 1, 1, parameters, new Variant(true));
        setupDataForValidatePropertyBusObjectsAndStartTest(securedProperty);

        executeTestMethod(getTestWrapperFor_v1_03());
    }

    @Test
    public void testValidatePropertyBusObjectsFailsWhenPropertyHasInvalidValuesForHintId1() throws Exception
    {
        short[] layoutHints = new short[1];
        layoutHints[0] = 1;
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 0, new Variant("string"));
        parameters.put((short) 1, new Variant(5, "u"));
        parameters.put((short) 2, new Variant(layoutHints, "aq"));
        PropertyControl property = getMockProperty((short) 1, 0, parameters, new Variant("value"));
        setupDataForValidatePropertyBusObjectsAndStartTest(property);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_03(), "Signature does not match for property Value when hint id is 1 expected:<[b]> but was:<[s]>");
    }

    @Test
    public void testValidatePropertyBusObjectsFailsWhenSecuredPropertyHasInvalidValuesForHintId1() throws Exception
    {
        short[] layoutHints = new short[1];
        layoutHints[0] = 1;
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 0, new Variant("string"));
        parameters.put((short) 1, new Variant(5, "u"));
        parameters.put((short) 2, new Variant(layoutHints, "aq"));
        PropertyControlSecured securedProperty = getMockSecuredProperty((short) 1, 1, parameters, new Variant("value"));
        setupDataForValidatePropertyBusObjectsAndStartTest(securedProperty);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_03(), "Signature does not match for property Value when hint id is 1 expected:<[b]> but was:<[s]>");
    }

    @Test
    public void testValidatePropertyBusObjectsPassesWhenPropertyHasAllValidValuesForHintId1() throws Exception
    {
        short[] layoutHints = new short[1];
        layoutHints[0] = 1;
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 0, new Variant("string"));
        parameters.put((short) 1, new Variant(5, "u"));
        parameters.put((short) 2, new Variant(layoutHints, "aq"));
        PropertyControl property = getMockProperty((short) 1, 0, parameters, new Variant(true));
        setupDataForValidatePropertyBusObjectsAndStartTest(property);

        executeTestMethod(getTestWrapperFor_v1_03());
    }

    @Test
    public void testValidatePropertyBusObjectsPassesWhenSecuredPropertyHasAllValidValuesForHintId1() throws Exception
    {
        short[] layoutHints = new short[1];
        layoutHints[0] = 1;
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 0, new Variant("string"));
        parameters.put((short) 1, new Variant(5, "u"));
        parameters.put((short) 2, new Variant(layoutHints, "aq"));
        PropertyControlSecured securedProperty = getMockSecuredProperty((short) 1, 1, parameters, new Variant(true));
        setupDataForValidatePropertyBusObjectsAndStartTest(securedProperty);

        executeTestMethod(getTestWrapperFor_v1_03());
    }

    @Test
    public void testValidatePropertyBusObjectsFailsWhenPropertyHasNoOptionalParameter4AndHintIdIs2() throws Exception
    {
        short[] layoutHints = new short[1];
        layoutHints[0] = 2;
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 0, new Variant("string"));
        parameters.put((short) 1, new Variant(5, "u"));
        parameters.put((short) 2, new Variant(layoutHints, "aq"));
        PropertyControl property = getMockProperty((short) 1, 0, parameters, new Variant("value"));
        setupDataForValidatePropertyBusObjectsAndStartTest(property);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_03(), "Parameters should contain key 4 when hint id is 2");
    }

    @Test
    public void testValidatePropertyBusObjectsFailsWhenSecuredPropertyHasNoOptionalParameter4AndHintIdIs2() throws Exception
    {
        short[] layoutHints = new short[1];
        layoutHints[0] = 2;
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 0, new Variant("string"));
        parameters.put((short) 1, new Variant(5, "u"));
        parameters.put((short) 2, new Variant(layoutHints, "aq"));
        PropertyControlSecured securedProperty = getMockSecuredProperty((short) 1, 1, parameters, new Variant("value"));
        setupDataForValidatePropertyBusObjectsAndStartTest(securedProperty);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_03(), "Parameters should contain key 4 when hint id is 2");
    }

    @Test
    public void testValidatePropertyBusObjectsFailsWhenPropertyHasInvalidValuesForHintId2() throws Exception
    {
        short[] layoutHints = new short[1];
        layoutHints[0] = 2;
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 0, new Variant("string"));
        parameters.put((short) 1, new Variant(5, "u"));
        parameters.put((short) 2, new Variant(layoutHints, "aq"));
        parameters.put((short) 4, new Variant(10));
        PropertyControl property = getMockProperty((short) 1, 0, parameters, new Variant("value"));
        setupDataForValidatePropertyBusObjectsAndStartTest(property);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_03(), "Signature does not match for key 4 expected:<[a(vs)]> but was:<[i]>");
    }

    @Test
    public void testValidatePropertyBusObjectsFailsWhenSecuredPropertyHasInvalidValuesForHintId2() throws Exception
    {
        short[] layoutHints = new short[1];
        layoutHints[0] = 2;
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 0, new Variant("string"));
        parameters.put((short) 1, new Variant(5, "u"));
        parameters.put((short) 2, new Variant(layoutHints, "aq"));
        parameters.put((short) 4, new Variant(10));
        PropertyControlSecured securedProperty = getMockSecuredProperty((short) 1, 1, parameters, new Variant("value"));
        setupDataForValidatePropertyBusObjectsAndStartTest(securedProperty);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_03(), "Signature does not match for key 4 expected:<[a(vs)]> but was:<[i]>");
    }

    @Test
    public void testValidatePropertyBusObjectsFailsWhenPropertyHasNoOptionalParameter4AndHintIdIs3() throws Exception
    {
        short[] layoutHints = new short[1];
        layoutHints[0] = 3;
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 0, new Variant("string"));
        parameters.put((short) 1, new Variant(5, "u"));
        parameters.put((short) 2, new Variant(layoutHints, "aq"));
        PropertyControl property = getMockProperty((short) 1, 0, parameters, new Variant("value"));
        setupDataForValidatePropertyBusObjectsAndStartTest(property);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_03(), "Parameters should contain key 4 when hint id is 3");
    }

    @Test
    public void testValidatePropertyBusObjectsFailsWhenSecuredPropertyHasNoOptionalParameter4AndHintIdIs3() throws Exception
    {
        short[] layoutHints = new short[1];
        layoutHints[0] = 3;
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 0, new Variant("string"));
        parameters.put((short) 1, new Variant(5, "u"));
        parameters.put((short) 2, new Variant(layoutHints, "aq"));
        PropertyControlSecured securedProperty = getMockSecuredProperty((short) 1, 1, parameters, new Variant("value"));
        setupDataForValidatePropertyBusObjectsAndStartTest(securedProperty);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_03(), "Parameters should contain key 4 when hint id is 3");
    }

    @Test
    public void testValidatePropertyBusObjectsFailsWhenPropertyHasInvalidValuesForHintId3() throws Exception
    {
        short[] layoutHints = new short[1];
        layoutHints[0] = 3;
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 0, new Variant("string"));
        parameters.put((short) 1, new Variant(5, "u"));
        parameters.put((short) 2, new Variant(layoutHints, "aq"));
        parameters.put((short) 4, new Variant(10));
        PropertyControl property = getMockProperty((short) 1, 0, parameters, new Variant("value"));
        setupDataForValidatePropertyBusObjectsAndStartTest(property);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_03(), "Signature does not match for key 4 expected:<[a(vs)]> but was:<[i]>");
    }

    @Test
    public void testValidatePropertyBusObjectsFailsWhenSecuredPropertyHasInvalidValuesForHintId3() throws Exception
    {
        short[] layoutHints = new short[1];
        layoutHints[0] = 3;
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 0, new Variant("string"));
        parameters.put((short) 1, new Variant(5, "u"));
        parameters.put((short) 2, new Variant(layoutHints, "aq"));
        parameters.put((short) 4, new Variant(10));
        PropertyControlSecured securedProperty = getMockSecuredProperty((short) 1, 1, parameters, new Variant("value"));
        setupDataForValidatePropertyBusObjectsAndStartTest(securedProperty);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_03(), "Signature does not match for key 4 expected:<[a(vs)]> but was:<[i]>");
    }

    @Test
    public void testValidatePropertyBusObjectsFailsWhenPropertyHasNoOptionalParameter4AndHintIdIs4() throws Exception
    {
        short[] layoutHints = new short[1];
        layoutHints[0] = 4;
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 0, new Variant("string"));
        parameters.put((short) 1, new Variant(5, "u"));
        parameters.put((short) 2, new Variant(layoutHints, "aq"));
        PropertyControl property = getMockProperty((short) 1, 0, parameters, new Variant("value"));
        setupDataForValidatePropertyBusObjectsAndStartTest(property);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_03(), "Parameters should contain key 4 when hint id is 4");
    }

    @Test
    public void testValidatePropertyBusObjectsFailsWhenSecuredPropertyHasNoOptionalParameter4AndHintIdIs4() throws Exception
    {
        short[] layoutHints = new short[1];
        layoutHints[0] = 4;
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 0, new Variant("string"));
        parameters.put((short) 1, new Variant(5, "u"));
        parameters.put((short) 2, new Variant(layoutHints, "aq"));
        PropertyControlSecured securedProperty = getMockSecuredProperty((short) 1, 1, parameters, new Variant("value"));
        setupDataForValidatePropertyBusObjectsAndStartTest(securedProperty);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_03(), "Parameters should contain key 4 when hint id is 4");
    }

    @Test
    public void testValidatePropertyBusObjectsFailsWhenPropertyHasInvalidValuesForHintId4() throws Exception
    {
        short[] layoutHints = new short[1];
        layoutHints[0] = 4;
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 0, new Variant("string"));
        parameters.put((short) 1, new Variant(5, "u"));
        parameters.put((short) 2, new Variant(layoutHints, "aq"));
        parameters.put((short) 4, new Variant(10));
        PropertyControl property = getMockProperty((short) 1, 0, parameters, new Variant("value"));
        setupDataForValidatePropertyBusObjectsAndStartTest(property);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_03(), "Signature does not match for key 4 expected:<[a(vs)]> but was:<[i]>");
    }

    @Test
    public void testValidatePropertyBusObjectsFailsWhenSecuredPropertyHasInvalidValuesForHintId4() throws Exception
    {
        short[] layoutHints = new short[1];
        layoutHints[0] = 4;
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 0, new Variant("string"));
        parameters.put((short) 1, new Variant(5, "u"));
        parameters.put((short) 2, new Variant(layoutHints, "aq"));
        parameters.put((short) 4, new Variant(10));
        PropertyControlSecured securedProperty = getMockSecuredProperty((short) 1, 1, parameters, new Variant("value"));
        setupDataForValidatePropertyBusObjectsAndStartTest(securedProperty);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_03(), "Signature does not match for key 4 expected:<[a(vs)]> but was:<[i]>");
    }

    @Test
    public void testValidatePropertyBusObjectsFailsWhenPropertyHasInvalidValuesForHintId5() throws Exception
    {
        short[] layoutHints = new short[1];
        layoutHints[0] = 5;
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 0, new Variant("string"));
        parameters.put((short) 1, new Variant(5, "u"));
        parameters.put((short) 2, new Variant(layoutHints, "aq"));
        PropertyControl property = getMockProperty((short) 1, 0, parameters, new Variant("value"));
        setupDataForValidatePropertyBusObjectsAndStartTest(property);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_03(), "Property Value type needs to be numeric when hint id is 5. Found signature: s");
    }

    @Test
    public void testValidatePropertyBusObjectsFailsWhenSecuredPropertyHasInvalidValuesForHintId5() throws Exception
    {
        short[] layoutHints = new short[1];
        layoutHints[0] = 5;
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 0, new Variant("string"));
        parameters.put((short) 1, new Variant(5, "u"));
        parameters.put((short) 2, new Variant(layoutHints, "aq"));
        PropertyControlSecured securedProperty = getMockSecuredProperty((short) 1, 1, parameters, new Variant("value"));
        setupDataForValidatePropertyBusObjectsAndStartTest(securedProperty);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_03(), "Property Value type needs to be numeric when hint id is 5. Found signature: s");
    }

    @Test
    public void testValidatePropertyBusObjectsPassesWhenPropertyHasAllValidValuesForHintId5() throws Exception
    {
        short[] layoutHints = new short[1];
        layoutHints[0] = 5;
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 0, new Variant("string"));
        parameters.put((short) 1, new Variant(5, "u"));
        parameters.put((short) 2, new Variant(layoutHints, "aq"));
        PropertyControl property = getMockProperty((short) 1, 0, parameters, new Variant(10));
        setupDataForValidatePropertyBusObjectsAndStartTest(property);

        executeTestMethod(getTestWrapperFor_v1_03());
    }

    @Test
    public void testValidatePropertyBusObjectsPassesWhenSecuredPropertyHasAllValidValuesForHintId5() throws Exception
    {
        short[] layoutHints = new short[1];
        layoutHints[0] = 5;
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 0, new Variant("string"));
        parameters.put((short) 1, new Variant(5, "u"));
        parameters.put((short) 2, new Variant(layoutHints, "aq"));
        PropertyControlSecured securedProperty = getMockSecuredProperty((short) 1, 1, parameters, new Variant(10));
        setupDataForValidatePropertyBusObjectsAndStartTest(securedProperty);

        executeTestMethod(getTestWrapperFor_v1_03());
    }

    @Test
    public void testValidatePropertyBusObjectsFailsWhenPropertyHasInvalidValuesForHintId6() throws Exception
    {
        short[] layoutHints = new short[1];
        layoutHints[0] = 6;
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 0, new Variant("string"));
        parameters.put((short) 1, new Variant(5, "u"));
        parameters.put((short) 2, new Variant(layoutHints, "aq"));
        PropertyControl property = getMockProperty((short) 1, 0, parameters, new Variant("value"));
        setupDataForValidatePropertyBusObjectsAndStartTest(property);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_03(), "Signature does not match for property Value when hint id is 6 expected:<[q(qqq)]> but was:<[s]>");
    }

    @Test
    public void testValidatePropertyBusObjectsFailsWhenSecuredPropertyHasInvalidValuesForHintId6() throws Exception
    {
        short[] layoutHints = new short[1];
        layoutHints[0] = 6;
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 0, new Variant("string"));
        parameters.put((short) 1, new Variant(5, "u"));
        parameters.put((short) 2, new Variant(layoutHints, "aq"));
        PropertyControlSecured securedProperty = getMockSecuredProperty((short) 1, 1, parameters, new Variant("value"));
        setupDataForValidatePropertyBusObjectsAndStartTest(securedProperty);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_03(), "Signature does not match for property Value when hint id is 6 expected:<[q(qqq)]> but was:<[s]>");
    }

    @Test
    public void testValidatePropertyBusObjectsFailsWhenPropertyHasInvalidValuesForHintId7() throws Exception
    {
        short[] layoutHints = new short[1];
        layoutHints[0] = 7;
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 0, new Variant("string"));
        parameters.put((short) 1, new Variant(5, "u"));
        parameters.put((short) 2, new Variant(layoutHints, "aq"));
        PropertyControl property = getMockProperty((short) 1, 0, parameters, new Variant("value"));
        setupDataForValidatePropertyBusObjectsAndStartTest(property);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_03(), "Signature does not match for property Value when hint id is 7 expected:<[q(qqq)]> but was:<[s]>");
    }

    @Test
    public void testValidatePropertyBusObjectsFailsWhenSecuredPropertyHasInvalidValuesForHintId7() throws Exception
    {
        short[] layoutHints = new short[1];
        layoutHints[0] = 7;
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 0, new Variant("string"));
        parameters.put((short) 1, new Variant(5, "u"));
        parameters.put((short) 2, new Variant(layoutHints, "aq"));
        PropertyControlSecured securedProperty = getMockSecuredProperty((short) 1, 1, parameters, new Variant("value"));
        setupDataForValidatePropertyBusObjectsAndStartTest(securedProperty);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_03(), "Signature does not match for property Value when hint id is 7 expected:<[q(qqq)]> but was:<[s]>");
    }

    @Test
    public void testValidatePropertyBusObjectsFailsWhenPropertyHasInvalidValuesForHintId8() throws Exception
    {
        short[] layoutHints = new short[1];
        layoutHints[0] = 8;
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 0, new Variant("string"));
        parameters.put((short) 1, new Variant(5, "u"));
        parameters.put((short) 2, new Variant(layoutHints, "aq"));
        PropertyControl property = getMockProperty((short) 1, 0, parameters, new Variant("value"));
        setupDataForValidatePropertyBusObjectsAndStartTest(property);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_03(), "Property Value type needs to be numeric when hint id is 8. Found signature: s");
    }

    @Test
    public void testValidatePropertyBusObjectsFailsWhenSecuredPropertyHasInvalidValuesForHintId8() throws Exception
    {
        short[] layoutHints = new short[1];
        layoutHints[0] = 8;
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 0, new Variant("string"));
        parameters.put((short) 1, new Variant(5, "u"));
        parameters.put((short) 2, new Variant(layoutHints, "aq"));
        PropertyControlSecured securedProperty = getMockSecuredProperty((short) 1, 1, parameters, new Variant("value"));
        setupDataForValidatePropertyBusObjectsAndStartTest(securedProperty);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_03(), "Property Value type needs to be numeric when hint id is 8. Found signature: s");
    }

    @Test
    public void testValidatePropertyBusObjectsPassesWhenPropertyHasAllValidValuesForHintId8() throws Exception
    {
        short[] layoutHints = new short[1];
        layoutHints[0] = 8;
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 0, new Variant("string"));
        parameters.put((short) 1, new Variant(5, "u"));
        parameters.put((short) 2, new Variant(layoutHints, "aq"));
        PropertyControl property = getMockProperty((short) 1, 0, parameters, new Variant(10));
        setupDataForValidatePropertyBusObjectsAndStartTest(property);

        executeTestMethod(getTestWrapperFor_v1_03());
    }

    @Test
    public void testValidatePropertyBusObjectsPassesWhenSecuredPropertyHasAllValidValuesForHintId8() throws Exception
    {
        short[] layoutHints = new short[1];
        layoutHints[0] = 8;
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 0, new Variant("string"));
        parameters.put((short) 1, new Variant(5, "u"));
        parameters.put((short) 2, new Variant(layoutHints, "aq"));
        PropertyControlSecured securedProperty = getMockSecuredProperty((short) 1, 1, parameters, new Variant(10));
        setupDataForValidatePropertyBusObjectsAndStartTest(securedProperty);

        executeTestMethod(getTestWrapperFor_v1_03());
    }

    @Test
    public void testValidatePropertyBusObjectsFailsWhenPropertyHasInvalidValuesForHintId9() throws Exception
    {
        short[] layoutHints = new short[1];
        layoutHints[0] = 9;
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 0, new Variant("string"));
        parameters.put((short) 1, new Variant(5, "u"));
        parameters.put((short) 2, new Variant(layoutHints, "aq"));
        PropertyControl property = getMockProperty((short) 1, 0, parameters, new Variant("value"));
        setupDataForValidatePropertyBusObjectsAndStartTest(property);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_03(), "Property Value type needs to be numeric when hint id is 9. Found signature: s");
    }

    @Test
    public void testValidatePropertyBusObjectsFailsWhenSecuredPropertyHasInvalidValuesForHintId9() throws Exception
    {
        short[] layoutHints = new short[1];
        layoutHints[0] = 9;
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 0, new Variant("string"));
        parameters.put((short) 1, new Variant(5, "u"));
        parameters.put((short) 2, new Variant(layoutHints, "aq"));
        PropertyControlSecured securedProperty = getMockSecuredProperty((short) 1, 1, parameters, new Variant("value"));
        setupDataForValidatePropertyBusObjectsAndStartTest(securedProperty);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_03(), "Property Value type needs to be numeric when hint id is 9. Found signature: s");
    }

    @Test
    public void testValidatePropertyBusObjectsPassesWhenPropertyHasAllValidValuesForHintId9() throws Exception
    {
        short[] layoutHints = new short[1];
        layoutHints[0] = 9;
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 0, new Variant("string"));
        parameters.put((short) 1, new Variant(5, "u"));
        parameters.put((short) 2, new Variant(layoutHints, "aq"));
        PropertyControl property = getMockProperty((short) 1, 0, parameters, new Variant(10));
        setupDataForValidatePropertyBusObjectsAndStartTest(property);

        executeTestMethod(getTestWrapperFor_v1_03());
    }

    @Test
    public void testValidatePropertyBusObjectsPassesWhenSecuredPropertyHasAllValidValuesForHintId9() throws Exception
    {
        short[] layoutHints = new short[1];
        layoutHints[0] = 9;
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 0, new Variant("string"));
        parameters.put((short) 1, new Variant(5, "u"));
        parameters.put((short) 2, new Variant(layoutHints, "aq"));
        PropertyControlSecured securedProperty = getMockSecuredProperty((short) 1, 1, parameters, new Variant(10));
        setupDataForValidatePropertyBusObjectsAndStartTest(securedProperty);

        executeTestMethod(getTestWrapperFor_v1_03());
    }

    @Test
    public void testValidatePropertyBusObjectsFailsWhenPropertyHasInvalidValuesForHintId10() throws Exception
    {
        short[] layoutHints = new short[1];
        layoutHints[0] = 10;
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 0, new Variant("string"));
        parameters.put((short) 1, new Variant(5, "u"));
        parameters.put((short) 2, new Variant(layoutHints, "aq"));
        PropertyControl property = getMockProperty((short) 1, 0, parameters, new Variant("value"));
        setupDataForValidatePropertyBusObjectsAndStartTest(property);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_03(), "Property Value type needs to be numeric when hint id is 10. Found signature: s");
    }

    @Test
    public void testValidatePropertyBusObjectsFailsWhenSecuredPropertyHasInvalidValuesForHintId10() throws Exception
    {
        short[] layoutHints = new short[1];
        layoutHints[0] = 10;
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 0, new Variant("string"));
        parameters.put((short) 1, new Variant(5, "u"));
        parameters.put((short) 2, new Variant(layoutHints, "aq"));
        PropertyControlSecured securedProperty = getMockSecuredProperty((short) 1, 1, parameters, new Variant("value"));
        setupDataForValidatePropertyBusObjectsAndStartTest(securedProperty);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_03(), "Property Value type needs to be numeric when hint id is 10. Found signature: s");
    }

    @Test
    public void testValidatePropertyBusObjectsPassesWhenPropertyHasAllValidValuesForHintId10() throws Exception
    {
        short[] layoutHints = new short[1];
        layoutHints[0] = 10;
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 0, new Variant("string"));
        parameters.put((short) 1, new Variant(5, "u"));
        parameters.put((short) 2, new Variant(layoutHints, "aq"));
        PropertyControl property = getMockProperty((short) 1, 0, parameters, new Variant(10));
        setupDataForValidatePropertyBusObjectsAndStartTest(property);

        executeTestMethod(getTestWrapperFor_v1_03());
    }

    @Test
    public void testValidatePropertyBusObjectsPassesWhenSecuredPropertyHasAllValidValuesForHintId10() throws Exception
    {
        short[] layoutHints = new short[1];
        layoutHints[0] = 10;
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 0, new Variant("string"));
        parameters.put((short) 1, new Variant(5, "u"));
        parameters.put((short) 2, new Variant(layoutHints, "aq"));
        PropertyControlSecured securedProperty = getMockSecuredProperty((short) 1, 1, parameters, new Variant(10));
        setupDataForValidatePropertyBusObjectsAndStartTest(securedProperty);

        executeTestMethod(getTestWrapperFor_v1_03());
    }

    @Test
    public void testValidatePropertyBusObjectsFailsWhenPropertyHasInvalidValuesForHintId11() throws Exception
    {
        short[] layoutHints = new short[1];
        layoutHints[0] = 11;
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 0, new Variant("string"));
        parameters.put((short) 1, new Variant(5, "u"));
        parameters.put((short) 2, new Variant(layoutHints, "aq"));
        PropertyControl property = getMockProperty((short) 1, 0, parameters, new Variant(true));
        setupDataForValidatePropertyBusObjectsAndStartTest(property);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_03(), "Signature does not match for property Value when hint id is 11 expected:<[s]> but was:<[b]>");
    }

    @Test
    public void testValidatePropertyBusObjectsFailsWhenSecuredPropertyHasInvalidValuesForHintId11() throws Exception
    {
        short[] layoutHints = new short[1];
        layoutHints[0] = 11;
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 0, new Variant("string"));
        parameters.put((short) 1, new Variant(5, "u"));
        parameters.put((short) 2, new Variant(layoutHints, "aq"));
        PropertyControlSecured securedProperty = getMockSecuredProperty((short) 1, 1, parameters, new Variant(true));
        setupDataForValidatePropertyBusObjectsAndStartTest(securedProperty);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_03(), "Signature does not match for property Value when hint id is 11 expected:<[s]> but was:<[b]>");
    }

    @Test
    public void testValidatePropertyBusObjectsPassesWhenPropertyHasAllValidValuesForHintId11() throws Exception
    {
        short[] layoutHints = new short[1];
        layoutHints[0] = 11;
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 0, new Variant("string"));
        parameters.put((short) 1, new Variant(5, "u"));
        parameters.put((short) 2, new Variant(layoutHints, "aq"));
        PropertyControl property = getMockProperty((short) 1, 0, parameters, new Variant("value"));
        setupDataForValidatePropertyBusObjectsAndStartTest(property);

        executeTestMethod(getTestWrapperFor_v1_03());
    }

    @Test
    public void testValidatePropertyBusObjectsPassesWhenSecuredPropertyHasAllValidValuesForHintId11() throws Exception
    {
        short[] layoutHints = new short[1];
        layoutHints[0] = 11;
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 0, new Variant("string"));
        parameters.put((short) 1, new Variant(5, "u"));
        parameters.put((short) 2, new Variant(layoutHints, "aq"));
        PropertyControlSecured securedProperty = getMockSecuredProperty((short) 1, 1, parameters, new Variant("value"));
        setupDataForValidatePropertyBusObjectsAndStartTest(securedProperty);

        executeTestMethod(getTestWrapperFor_v1_03());
    }

    @Test
    public void testValidatePropertyBusObjectsFailsWhenPropertyHasInvalidValuesForHintId12() throws Exception
    {
        short[] layoutHints = new short[1];
        layoutHints[0] = 12;
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 0, new Variant("string"));
        parameters.put((short) 1, new Variant(5, "u"));
        parameters.put((short) 2, new Variant(layoutHints, "aq"));
        PropertyControl property = getMockProperty((short) 1, 0, parameters, new Variant("value"));
        setupDataForValidatePropertyBusObjectsAndStartTest(property);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_03(), "Property Value type needs to be numeric when hint id is 12. Found signature: s");
    }

    @Test
    public void testValidatePropertyBusObjectsFailsWhenSecuredPropertyHasInvalidValuesForHintId12() throws Exception
    {
        short[] layoutHints = new short[1];
        layoutHints[0] = 12;
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 0, new Variant("string"));
        parameters.put((short) 1, new Variant(5, "u"));
        parameters.put((short) 2, new Variant(layoutHints, "aq"));
        PropertyControlSecured securedProperty = getMockSecuredProperty((short) 1, 1, parameters, new Variant("value"));
        setupDataForValidatePropertyBusObjectsAndStartTest(securedProperty);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_03(), "Property Value type needs to be numeric when hint id is 12. Found signature: s");
    }

    @Test
    public void testValidatePropertyBusObjectsPassesWhenPropertyHasAllValidValuesForHintId12() throws Exception
    {
        short[] layoutHints = new short[1];
        layoutHints[0] = 12;
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 0, new Variant("string"));
        parameters.put((short) 1, new Variant(5, "u"));
        parameters.put((short) 2, new Variant(layoutHints, "aq"));
        PropertyControl property = getMockProperty((short) 1, 0, parameters, new Variant(10));
        setupDataForValidatePropertyBusObjectsAndStartTest(property);

        executeTestMethod(getTestWrapperFor_v1_03());
    }

    @Test
    public void testValidatePropertyBusObjectsPassesWhenSecuredPropertyHasAllValidValuesForHintId12() throws Exception
    {
        short[] layoutHints = new short[1];
        layoutHints[0] = 12;
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 0, new Variant("string"));
        parameters.put((short) 1, new Variant(5, "u"));
        parameters.put((short) 2, new Variant(layoutHints, "aq"));
        PropertyControlSecured securedProperty = getMockSecuredProperty((short) 1, 1, parameters, new Variant(10));
        setupDataForValidatePropertyBusObjectsAndStartTest(securedProperty);

        executeTestMethod(getTestWrapperFor_v1_03());
    }

    @Test
    public void testValidatePropertyBusObjectsFailsWhenPropertyHasInvalidValuesForHintId13() throws Exception
    {
        short[] layoutHints = new short[1];
        layoutHints[0] = 13;
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 0, new Variant("string"));
        parameters.put((short) 1, new Variant(5, "u"));
        parameters.put((short) 2, new Variant(layoutHints, "aq"));
        PropertyControl property = getMockProperty((short) 1, 0, parameters, new Variant(true));
        setupDataForValidatePropertyBusObjectsAndStartTest(property);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_03(), "Signature does not match for property Value when hint id is 13 expected:<[s]> but was:<[b]>");
    }

    @Test
    public void testValidatePropertyBusObjectsFailsWhenSecuredPropertyHasInvalidValuesForHintId13() throws Exception
    {
        short[] layoutHints = new short[1];
        layoutHints[0] = 13;
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 0, new Variant("string"));
        parameters.put((short) 1, new Variant(5, "u"));
        parameters.put((short) 2, new Variant(layoutHints, "aq"));
        PropertyControlSecured securedProperty = getMockSecuredProperty((short) 1, 1, parameters, new Variant(true));
        setupDataForValidatePropertyBusObjectsAndStartTest(securedProperty);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_03(), "Signature does not match for property Value when hint id is 13 expected:<[s]> but was:<[b]>");
    }

    @Test
    public void testValidatePropertyBusObjectsPassesWhenPropertyHasAllValidValuesForHintId13() throws Exception
    {
        short[] layoutHints = new short[1];
        layoutHints[0] = 13;
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 0, new Variant("string"));
        parameters.put((short) 1, new Variant(5, "u"));
        parameters.put((short) 2, new Variant(layoutHints, "aq"));
        PropertyControl property = getMockProperty((short) 1, 0, parameters, new Variant("value"));
        setupDataForValidatePropertyBusObjectsAndStartTest(property);

        executeTestMethod(getTestWrapperFor_v1_03());
    }

    @Test
    public void testValidatePropertyBusObjectsPassesWhenSecuredPropertyHasAllValidValuesForHintId13() throws Exception
    {
        short[] layoutHints = new short[1];
        layoutHints[0] = 13;
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 0, new Variant("string"));
        parameters.put((short) 1, new Variant(5, "u"));
        parameters.put((short) 2, new Variant(layoutHints, "aq"));
        PropertyControlSecured securedProperty = getMockSecuredProperty((short) 1, 1, parameters, new Variant("value"));
        setupDataForValidatePropertyBusObjectsAndStartTest(securedProperty);

        executeTestMethod(getTestWrapperFor_v1_03());
    }

    @Test
    public void testValidatePropertyBusObjectsFailsWhenPropertyParametersContainsInvalidValueForKey3() throws Exception
    {
        short[] layoutHints = new short[1];
        layoutHints[0] = 1;
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 2, new Variant(layoutHints, "aq"));
        parameters.put((short) 3, new Variant(5));
        PropertyControl property = getMockProperty((short) 1, 0, parameters, new Variant(true));
        setupDataForValidatePropertyBusObjectsAndStartTest(property);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_03(), "Signature does not match for key 3 expected:<[s]> but was:<[i]>");
    }

    @Test
    public void testValidatePropertyBusObjectsFailsWhenSecuredPropertyParametersContainsInvalidValueForKey3() throws Exception
    {
        short[] layoutHints = new short[1];
        layoutHints[0] = 1;
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 2, new Variant(layoutHints, "aq"));
        parameters.put((short) 3, new Variant(5));
        PropertyControlSecured securedProperty = getMockSecuredProperty((short) 1, 1, parameters, new Variant(true));
        setupDataForValidatePropertyBusObjectsAndStartTest(securedProperty);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_03(), "Signature does not match for key 3 expected:<[s]> but was:<[i]>");
    }

    @Test
    public void testValidatePropertyBusObjectsFailsWhenPropertyParametersContainsInvalidValueForKey4() throws Exception
    {
        short[] layoutHints = new short[1];
        layoutHints[0] = 1;
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 2, new Variant(layoutHints, "aq"));
        parameters.put((short) 4, new Variant(5));
        PropertyControl property = getMockProperty((short) 1, 0, parameters, new Variant(true));
        setupDataForValidatePropertyBusObjectsAndStartTest(property);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_03(), "Signature does not match for key 4 expected:<[a(vs)]> but was:<[i]>");
    }

    @Test
    public void testValidatePropertyBusObjectsFailsWhenSecuredPropertyParametersContainsInvalidValueForKey4() throws Exception
    {
        short[] layoutHints = new short[1];
        layoutHints[0] = 1;
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 2, new Variant(layoutHints, "aq"));
        parameters.put((short) 4, new Variant(5));
        PropertyControlSecured securedProperty = getMockSecuredProperty((short) 1, 1, parameters, new Variant(true));
        setupDataForValidatePropertyBusObjectsAndStartTest(securedProperty);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_03(), "Signature does not match for key 4 expected:<[a(vs)]> but was:<[i]>");
    }

    @Test
    public void testValidatePropertyBusObjectsFailsWhenPropertyParametersContainsInvalidValueForKey5() throws Exception
    {
        short[] layoutHints = new short[1];
        layoutHints[0] = 1;
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 2, new Variant(layoutHints, "aq"));
        parameters.put((short) 5, new Variant(5));
        PropertyControl property = getMockProperty((short) 1, 0, parameters, new Variant(true));
        setupDataForValidatePropertyBusObjectsAndStartTest(property);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_03(), "Signature does not match for key 5 expected:<[(vvv)]> but was:<[i]>");
    }

    @Test
    public void testValidatePropertyBusObjectsFailsWhenSecuredPropertyParametersContainsInvalidValueForKey5() throws Exception
    {
        short[] layoutHints = new short[1];
        layoutHints[0] = 1;
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 2, new Variant(layoutHints, "aq"));
        parameters.put((short) 5, new Variant(5));
        PropertyControlSecured securedProperty = getMockSecuredProperty((short) 1, 1, parameters, new Variant(true));
        setupDataForValidatePropertyBusObjectsAndStartTest(securedProperty);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_03(), "Signature does not match for key 5 expected:<[(vvv)]> but was:<[i]>");
    }

    @Test
    public void testValidateLabelPropertyBusObjectsAddsNoteWhenNoneFound() throws Exception
    {
        when(mockIntrospector.getInterfacesExposedOnBusBasedOnName(LABEL_PROPERTY_INTERFACE_NAME)).thenReturn(new ArrayList<InterfaceDetail>());

        executeTestMethod(getTestWrapperFor_v1_04());
        verify(mockTestContext).addNote("No bus objects implement LabelProperty interface");
    }

    @Test
    public void testValidateLabelPropertyBusObjectsFailsWhenValidAncestorIsNotFound() throws Exception
    {
        Label label = getMockLabel((short) 2, 0, new HashMap<Short, Variant>());
        setupDataForValidateLabelPropertyBusObjectsAndStartTest(label, Container.IFNAME, false);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_04(), "No parent bus object that implements Container nor SecuredContainer interface found");
    }

    @Test
    public void testValidateLabelPropertyBusObjectsFailsWhenVersionDoesNotMatch() throws Exception
    {
        Label label = getMockLabel((short) 2, 0, new HashMap<Short, Variant>());
        setupDataForValidateLabelPropertyBusObjectsAndStartTest(label);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_04(), "Interface version does not match expected:<1> but was:<2>");
    }

    @Test
    public void testValidateLabelPropertyBusObjectsFailsWhenStatesPropertyDoesNotMatch() throws Exception
    {
        Label label = getMockLabel((short) 1, 2, new HashMap<Short, Variant>());
        setupDataForValidateLabelPropertyBusObjectsAndStartTest(label);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_04(), "Interface state 2 does not equals expected value of 0 or 1");
    }

    @Test
    public void testValidateLabelPropertyBusObjectsFailsWhenParametersContainsInvalidValueForKey1() throws Exception
    {
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 1, new Variant("invalid"));
        Label label = getMockLabel((short) 1, 0, parameters);
        setupDataForValidateLabelPropertyBusObjectsAndStartTest(label);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_04(), "Signature does not match for key 1 expected:<[u]> but was:<[s]>");
    }

    @Test
    public void testValidateLabelPropertyBusObjectsFailsWhenParametersContainsInvalidValueForKey2() throws Exception
    {
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 2, new Variant("invalid"));
        Label label = getMockLabel((short) 1, 0, parameters);
        setupDataForValidateLabelPropertyBusObjectsAndStartTest(label);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_04(), "Signature does not match for key 2 expected:<[aq]> but was:<[s]>");
    }

    @Test
    public void testValidateLabelPropertyBusObjectsFailsWhenParametersIsMissingValueForKey2() throws Exception
    {
        short[] layoutHints = null;
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 2, new Variant(layoutHints));
        Label label = getMockLabel((short) 1, 0, parameters);
        setupDataForValidateLabelPropertyBusObjectsAndStartTest(label);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_04(), "Signature does not match for key 2 expected:<aq> but was:<null>");
    }

    @Test
    public void testValidateLabelPropertyBusObjectsFailsWhenParametersHasMoreThanOneValueForKey2() throws Exception
    {
        short[] layoutHints = new short[2];
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 2, new Variant(layoutHints, "aq"));
        Label label = getMockLabel((short) 1, 0, parameters);
        setupDataForValidateLabelPropertyBusObjectsAndStartTest(label);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_04(), "Key 2 contains more than one value expected:<1> but was:<2>");
    }

    @Test
    public void testValidateLabelPropertyBusObjectsFailsWhenParametersHasInvalidValueForKey2() throws Exception
    {
        short[] layoutHints = new short[1];
        layoutHints[0] = 0;
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 2, new Variant(layoutHints, "aq"));
        Label label = getMockLabel((short) 1, 0, parameters);
        setupDataForValidateLabelPropertyBusObjectsAndStartTest(label);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_04(), "Value does not match for key 2 expected:<1> but was:<0>");
    }

    @Test
    public void testValidateLabelPropertyBusObjectsPassesWhenLabelHasAllValidValues() throws Exception
    {
        short[] layoutHints = new short[1];
        layoutHints[0] = 1;
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 1, new Variant(5, "u"));
        parameters.put((short) 2, new Variant(layoutHints, "aq"));
        Label label = getMockLabel((short) 1, 0, parameters);
        setupDataForValidateLabelPropertyBusObjectsAndStartTest(label);

        executeTestMethod(getTestWrapperFor_v1_04());
    }

    @Test
    public void testValidateLabelPropertyBusObjectsPassesWhenParametersIsMissingKey1AndKey2() throws Exception
    {
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        Label label = getMockLabel((short) 1, 0, parameters);
        setupDataForValidateLabelPropertyBusObjectsAndStartTest(label);

        executeTestMethod(getTestWrapperFor_v1_04());
    }

    @Test
    public void testValidateActionBusObjectsAddsNoteWhenNoneFound() throws Exception
    {
        when(mockIntrospector.getInterfacesExposedOnBusBasedOnName(ACTION_INTERFACE_NAME)).thenReturn(new ArrayList<InterfaceDetail>());
        when(mockIntrospector.getInterfacesExposedOnBusBasedOnName(SECURED_ACTION_INTERFACE_NAME)).thenReturn(new ArrayList<InterfaceDetail>());

        executeTestMethod(getTestWrapperFor_v1_05());
        verify(mockTestContext).addNote("No bus objects implement Action nor SecuredAction interfaces");
    }

    @Test
    public void testValidateActionBusObjectsFailsWhenValidAncestorIsNotFoundForAction() throws Exception
    {
        ActionControl action = getMockAction((short) 1, 0, new HashMap<Short, Variant>());
        setupDataForValidateActionBusObjectsAndStartTest(action, Container.IFNAME, false);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_05(), "No parent bus object that implements Container nor SecuredContainer interface found");
    }

    @Test
    public void testValidateActionBusObjectsFailsWhenValidAncestorIsNotFoundForSecuredAction() throws Exception
    {
        ActionControlSecured securedAction = getMockSecuredAction((short) 1, 0, new HashMap<Short, Variant>());
        setupDataForValidateActionBusObjectsAndStartTest(securedAction, Container.IFNAME, false);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_05(), "No parent bus object that implements Container nor SecuredContainer interface found");
    }

    @Test
    public void testValidateActionBusObjectsFailsWhenActionVersionDoesNotMatch() throws Exception
    {
        ActionControl action = getMockAction((short) 2, 0, new HashMap<Short, Variant>());
        setupDataForValidateActionBusObjectsAndStartTest(action);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_05(), "Interface version does not match expected:<1> but was:<2>");
    }

    @Test
    public void testValidateActionBusObjectsFailsWhenSecuredActionVersionDoesNotMatch() throws Exception
    {
        ActionControlSecured securedAction = getMockSecuredAction((short) 2, 0, new HashMap<Short, Variant>());
        setupDataForValidateActionBusObjectsAndStartTest(securedAction);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_05(), "Interface version does not match expected:<1> but was:<2>");
    }

    @Test
    public void testValidateActionBusObjectsFailsWhenActionStatesPropertyDoesNotMatch() throws Exception
    {
        ActionControl action = getMockAction((short) 1, 2, new HashMap<Short, Variant>());
        setupDataForValidateActionBusObjectsAndStartTest(action);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_05(), "Interface state 2 does not equals expected value of 0 or 1");
    }

    @Test
    public void testValidateActionBusObjectsFailsWhenSecuredActionStatesPropertyDoesNotMatch() throws Exception
    {
        ActionControlSecured securedAction = getMockSecuredAction((short) 1, 2, new HashMap<Short, Variant>());
        setupDataForValidateActionBusObjectsAndStartTest(securedAction);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_05(), "Interface state 2 does not equals expected value of 0 or 1");
    }

    @Test
    public void testValidateActionBusObjectsFailsWhenActionParametersContainsInvalidValueForKey0() throws Exception
    {
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 0, new Variant(5));
        ActionControl action = getMockAction((short) 1, 0, parameters);
        setupDataForValidateActionBusObjectsAndStartTest(action);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_05(), "Signature does not match for key 0 expected:<[s]> but was:<[i]>");
    }

    @Test
    public void testValidateActionBusObjectsFailsWhenSecuredActionParametersContainsInvalidValueForKey0() throws Exception
    {
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 0, new Variant(5));
        ActionControlSecured securedAction = getMockSecuredAction((short) 1, 1, parameters);
        setupDataForValidateActionBusObjectsAndStartTest(securedAction);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_05(), "Signature does not match for key 0 expected:<[s]> but was:<[i]>");
    }

    @Test
    public void testValidateActionBusObjectsFailsWhenActionParametersContainsInvalidValueForKey1() throws Exception
    {
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 1, new Variant("invalid"));
        ActionControl action = getMockAction((short) 1, 0, parameters);
        setupDataForValidateActionBusObjectsAndStartTest(action);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_05(), "Signature does not match for key 1 expected:<[u]> but was:<[s]>");
    }

    @Test
    public void testValidateActionBusObjectsFailsWhenSecuredActionParametersContainsInvalidValueForKey1() throws Exception
    {
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 1, new Variant("invalid"));
        ActionControlSecured securedAction = getMockSecuredAction((short) 1, 1, parameters);
        setupDataForValidateActionBusObjectsAndStartTest(securedAction);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_05(), "Signature does not match for key 1 expected:<[u]> but was:<[s]>");
    }

    @Test
    public void testValidateActionBusObjectsFailsWhenActionParametersContainsInvalidValueForKey2() throws Exception
    {
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 2, new Variant("invalid"));
        ActionControl action = getMockAction((short) 1, 0, parameters);
        setupDataForValidateActionBusObjectsAndStartTest(action);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_05(), "Signature does not match for key 2 expected:<[aq]> but was:<[s]>");
    }

    @Test
    public void testValidateActionBusObjectsFailsWhenSecuredActionParametersContainsInvalidValueForKey2() throws Exception
    {
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 2, new Variant("invalid"));
        ActionControlSecured securedAction = getMockSecuredAction((short) 1, 1, parameters);
        setupDataForValidateActionBusObjectsAndStartTest(securedAction);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_05(), "Signature does not match for key 2 expected:<[aq]> but was:<[s]>");
    }

    @Test
    public void testValidateActionBusObjectsFailsWhenActionParametersIsMissingValueForKey2() throws Exception
    {
        short[] layoutHints = null;
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 2, new Variant(layoutHints));
        ActionControl action = getMockAction((short) 1, 0, parameters);
        setupDataForValidateActionBusObjectsAndStartTest(action);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_05(), "Signature does not match for key 2 expected:<aq> but was:<null>");
    }

    @Test
    public void testValidateActionBusObjectsFailsWhenSecuredActionParametersIsMissingValueForKey2() throws Exception
    {
        short[] layoutHints = null;
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 2, new Variant(layoutHints));
        ActionControlSecured securedAction = getMockSecuredAction((short) 1, 1, parameters);
        setupDataForValidateActionBusObjectsAndStartTest(securedAction);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_05(), "Signature does not match for key 2 expected:<aq> but was:<null>");
    }

    @Test
    public void testValidateActionBusObjectsFailsWhenActionParametersHasMoreThanOneValueForKey2() throws Exception
    {
        short[] layoutHints = new short[2];
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 2, new Variant(layoutHints, "aq"));
        ActionControl action = getMockAction((short) 1, 0, parameters);
        setupDataForValidateActionBusObjectsAndStartTest(action);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_05(), "Key 2 contains more than one value expected:<1> but was:<2>");
    }

    @Test
    public void testValidateActionBusObjectsFailsWhenSecuredActionParametersHasMoreThanOneValueForKey2() throws Exception
    {
        short[] layoutHints = new short[2];
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 2, new Variant(layoutHints, "aq"));
        ActionControlSecured securedAction = getMockSecuredAction((short) 1, 1, parameters);
        setupDataForValidateActionBusObjectsAndStartTest(securedAction);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_05(), "Key 2 contains more than one value expected:<1> but was:<2>");
    }

    @Test
    public void testValidateActionBusObjectsFailsWhenActionParametersHasInvalidValueForKey2() throws Exception
    {
        short[] layoutHints = new short[1];
        layoutHints[0] = 0;
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 2, new Variant(layoutHints, "aq"));
        ActionControl action = getMockAction((short) 1, 0, parameters);
        setupDataForValidateActionBusObjectsAndStartTest(action);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_05(), "Value does not match for key 2 expected:<1> but was:<0>");
    }

    @Test
    public void testValidateActionBusObjectsFailsWhenSecuredActionParametersHasInvalidValueForKey2() throws Exception
    {
        short[] layoutHints = new short[1];
        layoutHints[0] = 0;
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 2, new Variant(layoutHints, "aq"));
        ActionControlSecured securedAction = getMockSecuredAction((short) 1, 1, parameters);
        setupDataForValidateActionBusObjectsAndStartTest(securedAction);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_05(), "Value does not match for key 2 expected:<1> but was:<0>");
    }

    @Test
    public void testValidateActionBusObjectsPassesWhenActionHasAllValidValues() throws Exception
    {
        short[] layoutHints = new short[1];
        layoutHints[0] = 1;
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 0, new Variant("string"));
        parameters.put((short) 1, new Variant(5, "u"));
        parameters.put((short) 2, new Variant(layoutHints, "aq"));
        ActionControl action = getMockAction((short) 1, 0, parameters);
        setupDataForValidateActionBusObjectsAndStartTest(action);

        executeTestMethod(getTestWrapperFor_v1_05());
    }

    @Test
    public void testValidateActionBusObjectsPassesWhenSecuredActionHasAllValidValues() throws Exception
    {
        short[] layoutHints = new short[1];
        layoutHints[0] = 1;
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 0, new Variant("string"));
        parameters.put((short) 1, new Variant(5, "u"));
        parameters.put((short) 2, new Variant(layoutHints, "aq"));
        ActionControlSecured securedAction = getMockSecuredAction((short) 1, 1, parameters);
        setupDataForValidateActionBusObjectsAndStartTest(securedAction);

        executeTestMethod(getTestWrapperFor_v1_05());
    }

    @Test
    public void testValidateActionBusObjectsPassesWhenActionIsMissingKey0And1And2() throws Exception
    {
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        ActionControl action = getMockAction((short) 1, 0, parameters);
        setupDataForValidateActionBusObjectsAndStartTest(action);

        executeTestMethod(getTestWrapperFor_v1_05());
    }

    @Test
    public void testValidateActionBusObjectsPassesWhenSecuredActionIsMissingKey0And1And2() throws Exception
    {
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        ActionControlSecured securedAction = getMockSecuredAction((short) 1, 1, parameters);
        setupDataForValidateActionBusObjectsAndStartTest(securedAction);

        executeTestMethod(getTestWrapperFor_v1_05());
    }

    @Test
    public void testValidateDialogBusObjectsAddsNoteWhenNoneFound() throws Exception
    {
        when(mockIntrospector.getInterfacesExposedOnBusBasedOnName(DIALOG_INTERFACE_NAME)).thenReturn(new ArrayList<InterfaceDetail>());
        when(mockIntrospector.getInterfacesExposedOnBusBasedOnName(SECURED_DIALOG_INTERFACE_NAME)).thenReturn(new ArrayList<InterfaceDetail>());

        executeTestMethod(getTestWrapperFor_v1_06());
        verify(mockTestContext).addNote("No bus objects implement Dialog nor SecuredDialog interfaces");
    }

    @Test
    public void testValidateDialogBusObjectsFailsWhenValidAncestorIsNotFoundForDialog() throws Exception
    {
        AlertDialog dialog = getMockDialog((short) 1, 0, new HashMap<Short, Variant>(), (short) 3);
        setupDataForValidateDialogBusObjectsAndStartTest(dialog, Container.IFNAME, false);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_06(), "No parent bus object that implements Action nor SecuredAction nor NotificationAction interface found");
    }

    @Test
    public void testValidateDialogBusObjectsFailsWhenValidAncestorIsNotFoundForSecuredDialog() throws Exception
    {
        AlertDialogSecured securedDialog = getMockSecuredDialog((short) 1, 0, new HashMap<Short, Variant>(), (short) 3);
        setupDataForValidateDialogBusObjectsAndStartTest(securedDialog, Container.IFNAME, false);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_06(), "No parent bus object that implements Action nor SecuredAction nor NotificationAction interface found");
    }

    @Test
    public void testValidateDialogBusObjectsFailsWhenDialogVersionDoesNotMatch() throws Exception
    {
        AlertDialog dialog = getMockDialog((short) 2, 0, new HashMap<Short, Variant>(), (short) 3);
        setupDataForValidateDialogBusObjectsAndStartTest(dialog);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_06(), "Interface version does not match expected:<1> but was:<2>");
    }

    @Test
    public void testValidateDialogBusObjectsFailsWhenSecuredDialogVersionDoesNotMatch() throws Exception
    {
        AlertDialogSecured securedDialog = getMockSecuredDialog((short) 2, 0, new HashMap<Short, Variant>(), (short) 3);
        setupDataForValidateDialogBusObjectsAndStartTest(securedDialog);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_06(), "Interface version does not match expected:<1> but was:<2>");
    }

    @Test
    public void testValidateDialogBusObjectsFailsWhenDialogStatesPropertyDoesNotMatch() throws Exception
    {
        AlertDialog dialog = getMockDialog((short) 1, 2, new HashMap<Short, Variant>(), (short) 3);
        setupDataForValidateDialogBusObjectsAndStartTest(dialog);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_06(), "Interface state 2 does not equals expected value of 0 or 1");
    }

    @Test
    public void testValidateDialogBusObjectsFailsWhenSecuredDialogStatesPropertyDoesNotMatch() throws Exception
    {
        AlertDialogSecured securedDialog = getMockSecuredDialog((short) 1, 2, new HashMap<Short, Variant>(), (short) 3);
        setupDataForValidateDialogBusObjectsAndStartTest(securedDialog);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_06(), "Interface state 2 does not equals expected value of 0 or 1");
    }

    @Test
    public void testValidateDialogBusObjectsFailsWhenDialogParametersContainsInvalidValueForKey0() throws Exception
    {
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 0, new Variant(5));
        AlertDialog dialog = getMockDialog((short) 1, 0, parameters, (short) 3);
        setupDataForValidateDialogBusObjectsAndStartTest(dialog);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_06(), "Signature does not match for key 0 expected:<[s]> but was:<[i]>");
    }

    @Test
    public void testValidateDialogBusObjectsFailsWhenSecuredDialogParametersContainsInvalidValueForKey0() throws Exception
    {
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 0, new Variant(5));
        AlertDialogSecured securedDialog = getMockSecuredDialog((short) 1, 1, parameters, (short) 3);
        setupDataForValidateDialogBusObjectsAndStartTest(securedDialog);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_06(), "Signature does not match for key 0 expected:<[s]> but was:<[i]>");
    }

    @Test
    public void testValidateDialogBusObjectsFailsWhenDialogParametersContainsInvalidValueForKey1() throws Exception
    {
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 1, new Variant("invalid"));
        AlertDialog dialog = getMockDialog((short) 1, 0, parameters, (short) 3);
        setupDataForValidateDialogBusObjectsAndStartTest(dialog);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_06(), "Signature does not match for key 1 expected:<[u]> but was:<[s]>");
    }

    @Test
    public void testValidateDialogBusObjectsFailsWhenSecuredDialogParametersContainsInvalidValueForKey1() throws Exception
    {
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 1, new Variant("invalid"));
        AlertDialogSecured securedDialog = getMockSecuredDialog((short) 1, 1, parameters, (short) 3);
        setupDataForValidateDialogBusObjectsAndStartTest(securedDialog);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_06(), "Signature does not match for key 1 expected:<[u]> but was:<[s]>");
    }

    @Test
    public void testValidateDialogBusObjectsFailsWhenDialogParametersContainsInvalidValueForKey2() throws Exception
    {
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 2, new Variant("invalid"));
        AlertDialog dialog = getMockDialog((short) 1, 0, parameters, (short) 3);
        setupDataForValidateDialogBusObjectsAndStartTest(dialog);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_06(), "Signature does not match for key 2 expected:<[aq]> but was:<[s]>");
    }

    @Test
    public void testValidateDialogBusObjectsFailsWhenSecuredDialogParametersContainsInvalidValueForKey2() throws Exception
    {
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 2, new Variant("invalid"));
        AlertDialogSecured securedDialog = getMockSecuredDialog((short) 1, 1, parameters, (short) 3);
        setupDataForValidateDialogBusObjectsAndStartTest(securedDialog);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_06(), "Signature does not match for key 2 expected:<[aq]> but was:<[s]>");
    }

    @Test
    public void testValidateDialogBusObjectsFailsWhenDialogParametersIsMissingValueForKey2() throws Exception
    {
        short[] layoutHints = null;
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 2, new Variant(layoutHints));
        AlertDialog dialog = getMockDialog((short) 1, 0, parameters, (short) 3);
        setupDataForValidateDialogBusObjectsAndStartTest(dialog);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_06(), "Signature does not match for key 2 expected:<aq> but was:<null>");
    }

    @Test
    public void testValidateDialogBusObjectsFailsWhenSecuredDialogParametersIsMissingValueForKey2() throws Exception
    {
        short[] layoutHints = null;
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 2, new Variant(layoutHints));
        AlertDialogSecured securedDialog = getMockSecuredDialog((short) 1, 1, parameters, (short) 3);
        setupDataForValidateDialogBusObjectsAndStartTest(securedDialog);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_06(), "Signature does not match for key 2 expected:<aq> but was:<null>");
    }

    @Test
    public void testValidateDialogBusObjectsFailsWhenDialogParametersHasMoreThanOneValueForKey2() throws Exception
    {
        short[] layoutHints = new short[2];
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 2, new Variant(layoutHints, "aq"));
        AlertDialog dialog = getMockDialog((short) 1, 0, parameters, (short) 3);
        setupDataForValidateDialogBusObjectsAndStartTest(dialog);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_06(), "Key 2 contains more than one value expected:<1> but was:<2>");
    }

    @Test
    public void testValidateDialogBusObjectsFailsWhenSecuredDialogParametersHasMoreThanOneValueForKey2() throws Exception
    {
        short[] layoutHints = new short[2];
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 2, new Variant(layoutHints, "aq"));
        AlertDialogSecured securedDialog = getMockSecuredDialog((short) 1, 1, parameters, (short) 3);
        setupDataForValidateDialogBusObjectsAndStartTest(securedDialog);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_06(), "Key 2 contains more than one value expected:<1> but was:<2>");
    }

    @Test
    public void testValidateDialogBusObjectsFailsWhenDialogParametersHasInvalidValueForKey2() throws Exception
    {
        short[] layoutHints = new short[1];
        layoutHints[0] = 0;
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 2, new Variant(layoutHints, "aq"));
        AlertDialog dialog = getMockDialog((short) 1, 0, parameters, (short) 3);
        setupDataForValidateDialogBusObjectsAndStartTest(dialog);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_06(), "Value does not match for key 2 expected:<1> but was:<0>");
    }

    @Test
    public void testValidateDialogBusObjectsFailsWhenSecuredDialogParametersHasInvalidValueForKey2() throws Exception
    {
        short[] layoutHints = new short[1];
        layoutHints[0] = 0;
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 2, new Variant(layoutHints, "aq"));
        AlertDialogSecured securedDialog = getMockSecuredDialog((short) 1, 1, parameters, (short) 3);
        setupDataForValidateDialogBusObjectsAndStartTest(securedDialog);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_06(), "Value does not match for key 2 expected:<1> but was:<0>");
    }

    @Test
    public void testValidateDialogBusObjectsFailsWhenDialogParametersContainsInvalidValueForKey6() throws Exception
    {
        short[] layoutHints = new short[1];
        layoutHints[0] = 1;
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 2, new Variant(layoutHints, "aq"));
        parameters.put((short) 6, new Variant(5, "u"));
        AlertDialog dialog = getMockDialog((short) 1, 0, parameters, (short) 3);
        setupDataForValidateDialogBusObjectsAndStartTest(dialog);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_06(), "Signature does not match for key 6 expected:<[s]> but was:<[u]>");
    }

    @Test
    public void testValidateDialogBusObjectsFailsWhenSecuredDialogParametersContainsInvalidValueForKey6() throws Exception
    {
        short[] layoutHints = new short[1];
        layoutHints[0] = 1;
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 2, new Variant(layoutHints, "aq"));
        parameters.put((short) 6, new Variant(5, "u"));
        AlertDialogSecured securedDialog = getMockSecuredDialog((short) 1, 1, parameters, (short) 3);
        setupDataForValidateDialogBusObjectsAndStartTest(securedDialog);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_06(), "Signature does not match for key 6 expected:<[s]> but was:<[u]>");
    }

    @Test
    public void testValidateDialogBusObjectsFailsWhenDialogParametersIsMissingKey7AndNumberOfActionsIs3() throws Exception
    {
        short[] layoutHints = new short[1];
        layoutHints[0] = 1;
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 2, new Variant(layoutHints, "aq"));
        parameters.put((short) 6, new Variant("value"));
        AlertDialog dialog = getMockDialog((short) 1, 0, parameters, (short) 3);
        setupDataForValidateDialogBusObjectsAndStartTest(dialog);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_06(), "Key 7 is missing");
    }

    @Test
    public void testValidateDialogBusObjectsFailsWhenSecuredDialogParametersIsMissingKey7AndNumberOfActionsIs3() throws Exception
    {
        short[] layoutHints = new short[1];
        layoutHints[0] = 1;
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 2, new Variant(layoutHints, "aq"));
        parameters.put((short) 6, new Variant("value"));
        AlertDialogSecured securedDialog = getMockSecuredDialog((short) 1, 1, parameters, (short) 3);
        setupDataForValidateDialogBusObjectsAndStartTest(securedDialog);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_06(), "Key 7 is missing");
    }

    @Test
    public void testValidateDialogBusObjectsFailsWhenDialogParametersContainsInvalidValueForKey7AndNumberOfActionsIs3() throws Exception
    {
        short[] layoutHints = new short[1];
        layoutHints[0] = 1;
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 2, new Variant(layoutHints, "aq"));
        parameters.put((short) 6, new Variant("value"));
        parameters.put((short) 7, new Variant(5, "u"));
        AlertDialog dialog = getMockDialog((short) 1, 0, parameters, (short) 3);
        setupDataForValidateDialogBusObjectsAndStartTest(dialog);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_06(), "Signature does not match for key 7 expected:<[s]> but was:<[u]>");
    }

    @Test
    public void testValidateDialogBusObjectsFailsWhenSecuredDialogParametersContainsInvalidValueForKey7AndNumberOfActionsIs3() throws Exception
    {
        short[] layoutHints = new short[1];
        layoutHints[0] = 1;
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 2, new Variant(layoutHints, "aq"));
        parameters.put((short) 6, new Variant("value"));
        parameters.put((short) 7, new Variant(5, "u"));
        AlertDialogSecured securedDialog = getMockSecuredDialog((short) 1, 1, parameters, (short) 3);
        setupDataForValidateDialogBusObjectsAndStartTest(securedDialog);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_06(), "Signature does not match for key 7 expected:<[s]> but was:<[u]>");
    }

    @Test
    public void testValidateDialogBusObjectsFailsWhenDialogParametersIsMissingKey7AndNumberOfActionsIs2() throws Exception
    {
        short[] layoutHints = new short[1];
        layoutHints[0] = 1;
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 2, new Variant(layoutHints, "aq"));
        parameters.put((short) 6, new Variant("value"));
        AlertDialog dialog = getMockDialog((short) 1, 0, parameters, (short) 2);
        setupDataForValidateDialogBusObjectsAndStartTest(dialog);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_06(), "Key 7 is missing");
    }

    @Test
    public void testValidateDialogBusObjectsFailsWhenSecuredDialogParametersIsMissingKey7AndNumberOfActionsIs2() throws Exception
    {
        short[] layoutHints = new short[1];
        layoutHints[0] = 1;
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 2, new Variant(layoutHints, "aq"));
        parameters.put((short) 6, new Variant("value"));
        AlertDialogSecured securedDialog = getMockSecuredDialog((short) 1, 1, parameters, (short) 2);
        setupDataForValidateDialogBusObjectsAndStartTest(securedDialog);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_06(), "Key 7 is missing");
    }

    @Test
    public void testValidateDialogBusObjectsFailsWhenDialogParametersContainsInvalidValueForKey7AndNumberOfActionsIs2() throws Exception
    {
        short[] layoutHints = new short[1];
        layoutHints[0] = 1;
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 2, new Variant(layoutHints, "aq"));
        parameters.put((short) 6, new Variant("value"));
        parameters.put((short) 7, new Variant(5, "u"));
        AlertDialog dialog = getMockDialog((short) 1, 0, parameters, (short) 2);
        setupDataForValidateDialogBusObjectsAndStartTest(dialog);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_06(), "Signature does not match for key 7 expected:<[s]> but was:<[u]>");
    }

    @Test
    public void testValidateDialogBusObjectsFailsWhenSecuredDialogParametersContainsInvalidValueForKey7AndNumberOfActionsIs2() throws Exception
    {
        short[] layoutHints = new short[1];
        layoutHints[0] = 1;
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 2, new Variant(layoutHints, "aq"));
        parameters.put((short) 6, new Variant("value"));
        parameters.put((short) 7, new Variant(5, "u"));
        AlertDialogSecured securedDialog = getMockSecuredDialog((short) 1, 1, parameters, (short) 2);
        setupDataForValidateDialogBusObjectsAndStartTest(securedDialog);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_06(), "Signature does not match for key 7 expected:<[s]> but was:<[u]>");
    }

    @Test
    public void testValidateDialogBusObjectsFailsWhenDialogParametersIsMissingKey8AndNumberOfActionsIs3() throws Exception
    {
        short[] layoutHints = new short[1];
        layoutHints[0] = 1;
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 2, new Variant(layoutHints, "aq"));
        parameters.put((short) 6, new Variant("value"));
        parameters.put((short) 7, new Variant("value"));
        AlertDialog dialog = getMockDialog((short) 1, 0, parameters, (short) 3);
        setupDataForValidateDialogBusObjectsAndStartTest(dialog);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_06(), "Key 8 is missing");
    }

    @Test
    public void testValidateDialogBusObjectsFailsWhenSecuredDialogParametersIsMissingKey8AndNumberOfActionsIs3() throws Exception
    {
        short[] layoutHints = new short[1];
        layoutHints[0] = 1;
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 2, new Variant(layoutHints, "aq"));
        parameters.put((short) 6, new Variant("value"));
        parameters.put((short) 7, new Variant("value"));
        AlertDialogSecured securedDialog = getMockSecuredDialog((short) 1, 1, parameters, (short) 3);
        setupDataForValidateDialogBusObjectsAndStartTest(securedDialog);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_06(), "Key 8 is missing");
    }

    @Test
    public void testValidateDialogBusObjectsFailsWhenDialogParametersContainsInvalidValueForKey8AndNumberOfActionsIs3() throws Exception
    {
        short[] layoutHints = new short[1];
        layoutHints[0] = 1;
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 2, new Variant(layoutHints, "aq"));
        parameters.put((short) 6, new Variant("value"));
        parameters.put((short) 7, new Variant("value"));
        parameters.put((short) 8, new Variant(5, "u"));
        AlertDialog dialog = getMockDialog((short) 1, 0, parameters, (short) 3);
        setupDataForValidateDialogBusObjectsAndStartTest(dialog);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_06(), "Signature does not match for key 8 expected:<[s]> but was:<[u]>");
    }

    @Test
    public void testValidateDialogBusObjectsFailsWhenSecuredDialogParametersContainsInvalidValueForKey8AndNumberOfActionsIs3() throws Exception
    {
        short[] layoutHints = new short[1];
        layoutHints[0] = 1;
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 2, new Variant(layoutHints, "aq"));
        parameters.put((short) 6, new Variant("value"));
        parameters.put((short) 7, new Variant("value"));
        parameters.put((short) 8, new Variant(5, "u"));
        AlertDialogSecured securedDialog = getMockSecuredDialog((short) 1, 1, parameters, (short) 3);
        setupDataForValidateDialogBusObjectsAndStartTest(securedDialog);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_06(), "Signature does not match for key 8 expected:<[s]> but was:<[u]>");
    }

    @Test
    public void testValidateDialogBusObjectsFailsWhenNumberOfActionsIs1AndInvokingDialogAction2DoesNotThrowException() throws Exception
    {
        short[] layoutHints = new short[1];
        layoutHints[0] = 1;
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 2, new Variant(layoutHints, "aq"));
        parameters.put((short) 6, new Variant("value"));
        AlertDialog dialog = getMockDialog((short) 1, 0, parameters, (short) 1);
        setupDataForValidateDialogBusObjectsAndStartTest(dialog);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_06(), "Invoking Action2() must throw exception");
    }

    @Test
    public void testValidateDialogBusObjectsFailsWhenNumberOfActionsIs1AndInvokingSecuredDialogAction2DoesNotThrowException() throws Exception
    {
        short[] layoutHints = new short[1];
        layoutHints[0] = 1;
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 2, new Variant(layoutHints, "aq"));
        parameters.put((short) 6, new Variant("value"));
        AlertDialogSecured securedDialog = getMockSecuredDialog((short) 1, 1, parameters, (short) 1);
        setupDataForValidateDialogBusObjectsAndStartTest(securedDialog);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_06(), "Invoking Action2() must throw exception");
    }

    @Test
    public void testValidateDialogBusObjectsFailsWhenNumberOfActionsIs1AndInvokingDialogAction3DoesNotThrowException() throws Exception
    {
        short[] layoutHints = new short[1];
        layoutHints[0] = 1;
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 2, new Variant(layoutHints, "aq"));
        parameters.put((short) 6, new Variant("value"));
        AlertDialog dialog = getMockDialog((short) 1, 0, parameters, (short) 1);
        ErrorReplyBusException errorReplyBusException = new ErrorReplyBusException("org.alljoyn.Error.MethodNotAllowed", "Method Not Allowed");
        doThrow(errorReplyBusException).when(dialog).Action2();
        setupDataForValidateDialogBusObjectsAndStartTest(dialog);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_06(), "Invoking Action3() must throw exception");
    }

    @Test
    public void testValidateDialogBusObjectsFailsWhenNumberOfActionsIs1AndInvokingSecuredDialogAction3DoesNotThrowException() throws Exception
    {
        short[] layoutHints = new short[1];
        layoutHints[0] = 1;
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 2, new Variant(layoutHints, "aq"));
        parameters.put((short) 6, new Variant("value"));
        AlertDialogSecured securedDialog = getMockSecuredDialog((short) 1, 1, parameters, (short) 1);
        ErrorReplyBusException errorReplyBusException = new ErrorReplyBusException("org.alljoyn.Error.MethodNotAllowed", "Method Not Allowed");
        doThrow(errorReplyBusException).when(securedDialog).Action2();
        setupDataForValidateDialogBusObjectsAndStartTest(securedDialog);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_06(), "Invoking Action3() must throw exception");
    }

    @Test
    public void testValidateDialogBusObjectsFailsWhenNumberOfActionsIs2AndInvokingDialogAction3DoesNotThrowException() throws Exception
    {
        short[] layoutHints = new short[1];
        layoutHints[0] = 1;
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 2, new Variant(layoutHints, "aq"));
        parameters.put((short) 6, new Variant("value"));
        parameters.put((short) 7, new Variant("value"));
        AlertDialog dialog = getMockDialog((short) 1, 0, parameters, (short) 2);
        setupDataForValidateDialogBusObjectsAndStartTest(dialog);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_06(), "Invoking Action3() must throw exception");
    }

    @Test
    public void testValidateDialogBusObjectsFailsWhenNumberOfActionsIs2AndInvokingSecuredDialogAction3DoesNotThrowException() throws Exception
    {
        short[] layoutHints = new short[1];
        layoutHints[0] = 1;
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 2, new Variant(layoutHints, "aq"));
        parameters.put((short) 6, new Variant("value"));
        parameters.put((short) 7, new Variant("value"));
        AlertDialogSecured securedDialog = getMockSecuredDialog((short) 1, 1, parameters, (short) 2);
        setupDataForValidateDialogBusObjectsAndStartTest(securedDialog);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_06(), "Invoking Action3() must throw exception");
    }

    @Test
    public void testValidateDialogBusObjectsSuccessWhenDialogNumberOfActionsIs1() throws Exception
    {
        short[] layoutHints = new short[1];
        layoutHints[0] = 1;
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 2, new Variant(layoutHints, "aq"));
        parameters.put((short) 6, new Variant("value"));
        parameters.put((short) 7, new Variant("value"));
        parameters.put((short) 8, new Variant("value"));
        AlertDialog dialog = getMockDialog((short) 1, 1, parameters, (short) 1);
        ErrorReplyBusException errorReplyBusException = new ErrorReplyBusException("org.alljoyn.Error.MethodNotAllowed", "Method Not Allowed");
        doThrow(errorReplyBusException).when(dialog).Action2();
        doThrow(errorReplyBusException).when(dialog).Action3();
        setupDataForValidateDialogBusObjectsAndStartTest(dialog);

        executeTestMethod(getTestWrapperFor_v1_06());
    }

    @Test
    public void testValidateDialogBusObjectsSuccessWhenSecuredDialogNumberOfActionsIs1() throws Exception
    {
        short[] layoutHints = new short[1];
        layoutHints[0] = 1;
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 2, new Variant(layoutHints, "aq"));
        parameters.put((short) 6, new Variant("value"));
        parameters.put((short) 7, new Variant("value"));
        parameters.put((short) 8, new Variant("value"));
        AlertDialogSecured securedDialog = getMockSecuredDialog((short) 1, 1, parameters, (short) 1);
        ErrorReplyBusException errorReplyBusException = new ErrorReplyBusException("org.alljoyn.Error.MethodNotAllowed", "Method Not Allowed");
        doThrow(errorReplyBusException).when(securedDialog).Action2();
        doThrow(errorReplyBusException).when(securedDialog).Action3();
        setupDataForValidateDialogBusObjectsAndStartTest(securedDialog);

        executeTestMethod(getTestWrapperFor_v1_06());
    }

    @Test
    public void testValidateDialogBusObjectsSuccessWhenDialogNumberOfActionsIs2() throws Exception
    {
        short[] layoutHints = new short[1];
        layoutHints[0] = 1;
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 2, new Variant(layoutHints, "aq"));
        parameters.put((short) 6, new Variant("value"));
        parameters.put((short) 7, new Variant("value"));
        AlertDialog dialog = getMockDialog((short) 1, 1, parameters, (short) 2);
        ErrorReplyBusException errorReplyBusException = new ErrorReplyBusException("org.alljoyn.Error.MethodNotAllowed", "Method Not Allowed");
        doThrow(errorReplyBusException).when(dialog).Action3();
        setupDataForValidateDialogBusObjectsAndStartTest(dialog);

        executeTestMethod(getTestWrapperFor_v1_06());
    }

    @Test
    public void testValidateDialogBusObjectsSuccessWhenSecuredDialogNumberOfActionsIs2() throws Exception
    {
        short[] layoutHints = new short[1];
        layoutHints[0] = 1;
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 2, new Variant(layoutHints, "aq"));
        parameters.put((short) 6, new Variant("value"));
        parameters.put((short) 7, new Variant("value"));
        AlertDialogSecured securedDialog = getMockSecuredDialog((short) 1, 1, parameters, (short) 2);
        ErrorReplyBusException errorReplyBusException = new ErrorReplyBusException("org.alljoyn.Error.MethodNotAllowed", "Method Not Allowed");
        doThrow(errorReplyBusException).when(securedDialog).Action3();
        setupDataForValidateDialogBusObjectsAndStartTest(securedDialog);

        executeTestMethod(getTestWrapperFor_v1_06());
    }

    @Test
    public void testValidateDialogBusObjectsSuccessWhenDialogNumberOfActionsIs3() throws Exception
    {
        short[] layoutHints = new short[1];
        layoutHints[0] = 1;
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 2, new Variant(layoutHints, "aq"));
        parameters.put((short) 6, new Variant("value"));
        parameters.put((short) 7, new Variant("value"));
        parameters.put((short) 8, new Variant("value"));
        AlertDialog dialog = getMockDialog((short) 1, 1, parameters, (short) 3);
        setupDataForValidateDialogBusObjectsAndStartTest(dialog);

        executeTestMethod(getTestWrapperFor_v1_06());
    }

    @Test
    public void testValidateDialogBusObjectsSuccessWhenSecuredDialogNumberOfActionsIs3() throws Exception
    {
        short[] layoutHints = new short[1];
        layoutHints[0] = 1;
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 2, new Variant(layoutHints, "aq"));
        parameters.put((short) 6, new Variant("value"));
        parameters.put((short) 7, new Variant("value"));
        parameters.put((short) 8, new Variant("value"));
        AlertDialogSecured securedDialog = getMockSecuredDialog((short) 1, 1, parameters, (short) 3);
        setupDataForValidateDialogBusObjectsAndStartTest(securedDialog);

        executeTestMethod(getTestWrapperFor_v1_06());
    }

    @Test
    public void testValidateActionBusObjectsPassesWhenActionIsMissingAllKeys() throws Exception
    {
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        AlertDialog dialog = getMockDialog((short) 1, 1, parameters, (short) 1);
        ErrorReplyBusException errorReplyBusException = new ErrorReplyBusException("org.alljoyn.Error.MethodNotAllowed", "Method Not Allowed");
        doThrow(errorReplyBusException).when(dialog).Action2();
        doThrow(errorReplyBusException).when(dialog).Action3();
        setupDataForValidateDialogBusObjectsAndStartTest(dialog);

        executeTestMethod(getTestWrapperFor_v1_06());
    }

    @Test
    public void testValidateActionBusObjectsPassesWhenSecuredActionIsMissingAllKeys() throws Exception
    {
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        AlertDialogSecured securedDialog = getMockSecuredDialog((short) 1, 1, parameters, (short) 1);
        ErrorReplyBusException errorReplyBusException = new ErrorReplyBusException("org.alljoyn.Error.MethodNotAllowed", "Method Not Allowed");
        doThrow(errorReplyBusException).when(securedDialog).Action2();
        doThrow(errorReplyBusException).when(securedDialog).Action3();
        setupDataForValidateDialogBusObjectsAndStartTest(securedDialog);

        executeTestMethod(getTestWrapperFor_v1_06());
    }

    @Test
    public void testValidateListPropertyBusObjectsAddsNoteWhenNoneFound() throws Exception
    {
        when(mockIntrospector.getInterfacesExposedOnBusBasedOnName(LIST_PROPERTY_INTERFACE_NAME)).thenReturn(new ArrayList<InterfaceDetail>());
        when(mockIntrospector.getInterfacesExposedOnBusBasedOnName(SECURED_LIST_PROPERTY_INTERFACE_NAME)).thenReturn(new ArrayList<InterfaceDetail>());

        executeTestMethod(getTestWrapperFor_v1_07());
        verify(mockTestContext).addNote("No bus objects implement ListProperty nor SecuredListProperty interfaces");
    }

    @Test
    public void testValidateListPropertyBusObjectsFailsWhenValidAncestorIsNotFoundForListProperty() throws Exception
    {
        ListPropertyControl listProperty = getMockListProperty((short) 1, 0, new HashMap<Short, Variant>());
        setupDataForValidateListPropertyBusObjectsAndStartTest(listProperty, Container.IFNAME, false);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_07(), "No parent bus object that implements Container nor SecuredContainer interface found");
    }

    @Test
    public void testValidateListPropertyBusObjectsFailsWhenValidAncestorIsNotFoundForSecuredListProperty() throws Exception
    {
        ListPropertyControlSecured securedListProperty = getMockSecuredListProperty((short) 1, 0, new HashMap<Short, Variant>());
        setupDataForValidateListPropertyBusObjectsAndStartTest(securedListProperty, Container.IFNAME, false);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_07(), "No parent bus object that implements Container nor SecuredContainer interface found");
    }

    @Test
    public void testValidateListPropertyBusObjectsFailsWhenListPropertyVersionDoesNotMatch() throws Exception
    {
        ListPropertyControl listProperty = getMockListProperty((short) 2, 0, new HashMap<Short, Variant>());
        setupDataForValidateListPropertyBusObjectsAndStartTest(listProperty);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_07(), "Interface version does not match expected:<1> but was:<2>");
    }

    @Test
    public void testValidateListPropertyBusObjectsFailsWhenSecuredListPropertyVersionDoesNotMatch() throws Exception
    {
        ListPropertyControlSecured securedListProperty = getMockSecuredListProperty((short) 2, 0, new HashMap<Short, Variant>());
        setupDataForValidateListPropertyBusObjectsAndStartTest(securedListProperty);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_07(), "Interface version does not match expected:<1> but was:<2>");
    }

    @Test
    public void testValidateListPropertyBusObjectsFailsWhenListPropertyStatesPropertyDoesNotMatch() throws Exception
    {
        ListPropertyControl listProperty = getMockListProperty((short) 1, 2, new HashMap<Short, Variant>());
        setupDataForValidateListPropertyBusObjectsAndStartTest(listProperty);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_07(), "Interface state 2 does not equals expected value of 0 or 1");
    }

    @Test
    public void testValidateListPropertyBusObjectsFailsWhenSecuredListPropertyStatesPropertyDoesNotMatch() throws Exception
    {
        ListPropertyControlSecured securedListProperty = getMockSecuredListProperty((short) 1, 2, new HashMap<Short, Variant>());
        setupDataForValidateListPropertyBusObjectsAndStartTest(securedListProperty);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_07(), "Interface state 2 does not equals expected value of 0 or 1");
    }

    @Test
    public void testValidateListPropertyBusObjectsFailsWhenListPropertyParametersContainsInvalidValueForKey0() throws Exception
    {
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 0, new Variant(5));
        ListPropertyControl listProperty = getMockListProperty((short) 1, 0, parameters);
        setupDataForValidateListPropertyBusObjectsAndStartTest(listProperty);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_07(), "Signature does not match for key 0 expected:<[s]> but was:<[i]>");
    }

    @Test
    public void testValidateListPropertyBusObjectsFailsWhenSecuredListPropertyParametersContainsInvalidValueForKey0() throws Exception
    {
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 0, new Variant(5));
        ListPropertyControlSecured securedListProperty = getMockSecuredListProperty((short) 1, 1, parameters);
        setupDataForValidateListPropertyBusObjectsAndStartTest(securedListProperty);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_07(), "Signature does not match for key 0 expected:<[s]> but was:<[i]>");
    }

    @Test
    public void testValidateListPropertyBusObjectsFailsWhenListPropertyParametersContainsInvalidValueForKey1() throws Exception
    {
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 1, new Variant("invalid"));
        ListPropertyControl listProperty = getMockListProperty((short) 1, 0, parameters);
        setupDataForValidateListPropertyBusObjectsAndStartTest(listProperty);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_07(), "Signature does not match for key 1 expected:<[u]> but was:<[s]>");
    }

    @Test
    public void testValidateListPropertyBusObjectsFailsWhenSecuredListPropertyParametersContainsInvalidValueForKey1() throws Exception
    {
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 1, new Variant("invalid"));
        ListPropertyControlSecured securedListProperty = getMockSecuredListProperty((short) 1, 1, parameters);
        setupDataForValidateListPropertyBusObjectsAndStartTest(securedListProperty);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_07(), "Signature does not match for key 1 expected:<[u]> but was:<[s]>");
    }

    @Test
    public void testValidateListPropertyBusObjectsFailsWhenListPropertyParametersContainsInvalidValueForKey2() throws Exception
    {
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 2, new Variant("invalid"));
        ListPropertyControl listProperty = getMockListProperty((short) 1, 0, parameters);
        setupDataForValidateListPropertyBusObjectsAndStartTest(listProperty);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_07(), "Signature does not match for key 2 expected:<[aq]> but was:<[s]>");
    }

    @Test
    public void testValidateListPropertyBusObjectsFailsWhenSecuredListPropertyParametersContainsInvalidValueForKey2() throws Exception
    {
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 2, new Variant("invalid"));
        ListPropertyControlSecured securedListProperty = getMockSecuredListProperty((short) 1, 1, parameters);
        setupDataForValidateListPropertyBusObjectsAndStartTest(securedListProperty);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_07(), "Signature does not match for key 2 expected:<[aq]> but was:<[s]>");
    }

    @Test
    public void testValidateListPropertyBusObjectsFailsWhenListPropertyParametersIsMissingValueForKey2() throws Exception
    {
        short[] layoutHints = null;
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 2, new Variant(layoutHints));
        ListPropertyControl listProperty = getMockListProperty((short) 1, 0, parameters);
        setupDataForValidateListPropertyBusObjectsAndStartTest(listProperty);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_07(), "Signature does not match for key 2 expected:<aq> but was:<null>");
    }

    @Test
    public void testValidateListPropertyBusObjectsFailsWhenSecuredListPropertyParametersIsMissingValueForKey2() throws Exception
    {
        short[] layoutHints = null;
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 2, new Variant(layoutHints));
        ListPropertyControlSecured securedListProperty = getMockSecuredListProperty((short) 1, 1, parameters);
        setupDataForValidateListPropertyBusObjectsAndStartTest(securedListProperty);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_07(), "Signature does not match for key 2 expected:<aq> but was:<null>");
    }

    @Test
    public void testValidateListPropertyBusObjectsFailsWhenListPropertyParametersHasMoreThanOneValueForKey2() throws Exception
    {
        short[] layoutHints = new short[2];
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 2, new Variant(layoutHints, "aq"));
        ListPropertyControl listProperty = getMockListProperty((short) 1, 0, parameters);
        setupDataForValidateListPropertyBusObjectsAndStartTest(listProperty);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_07(), "Key 2 contains more than one value expected:<1> but was:<2>");
    }

    @Test
    public void testValidateListPropertyBusObjectsFailsWhenSecuredListPropertyParametersHasMoreThanOneValueForKey2() throws Exception
    {
        short[] layoutHints = new short[2];
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 2, new Variant(layoutHints, "aq"));
        ListPropertyControlSecured securedListProperty = getMockSecuredListProperty((short) 1, 1, parameters);
        setupDataForValidateListPropertyBusObjectsAndStartTest(securedListProperty);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_07(), "Key 2 contains more than one value expected:<1> but was:<2>");
    }

    @Test
    public void testValidateListPropertyBusObjectsFailsWhenListPropertyParametersHasInvalidValueForKey2() throws Exception
    {
        short[] layoutHints = new short[1];
        layoutHints[0] = 0;
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 2, new Variant(layoutHints, "aq"));
        ListPropertyControl listProperty = getMockListProperty((short) 1, 0, parameters);
        setupDataForValidateListPropertyBusObjectsAndStartTest(listProperty);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_07(), "Value does not match for key 2 expected:<1> but was:<0>");
    }

    @Test
    public void testValidateListPropertyBusObjectsFailsWhenSecuredListPropertyParametersHasInvalidValueForKey2() throws Exception
    {
        short[] layoutHints = new short[1];
        layoutHints[0] = 0;
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 2, new Variant(layoutHints, "aq"));
        ListPropertyControlSecured securedListProperty = getMockSecuredListProperty((short) 1, 1, parameters);
        setupDataForValidateListPropertyBusObjectsAndStartTest(securedListProperty);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_07(), "Value does not match for key 2 expected:<1> but was:<0>");
    }

    @Test
    public void testValidateListPropertyBusObjectsFailsWhenListPropertyValueIsNull() throws Exception
    {
        short[] layoutHints = new short[1];
        layoutHints[0] = 1;
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 0, new Variant("string"));
        parameters.put((short) 1, new Variant(5, "u"));
        parameters.put((short) 2, new Variant(layoutHints, "aq"));
        ListPropertyControl listProperty = getMockListProperty((short) 1, 0, parameters, null);
        setupDataForValidateListPropertyBusObjectsAndStartTest(listProperty);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_07(), "Interface value cannot be null");
    }

    @Test
    public void testValidateListPropertyBusObjectsFailsWhenSecuredListPropertyValueIsNull() throws Exception
    {
        short[] layoutHints = new short[1];
        layoutHints[0] = 1;
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 0, new Variant("string"));
        parameters.put((short) 1, new Variant(5, "u"));
        parameters.put((short) 2, new Variant(layoutHints, "aq"));
        ListPropertyControlSecured securedListProperty = getMockSecuredListProperty((short) 1, 1, parameters, null);
        setupDataForValidateListPropertyBusObjectsAndStartTest(securedListProperty);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_07(), "Interface value cannot be null");
    }

    @Test
    public void testValidateListPropertyBusObjectsFailsWhenListPropertyValueLabelIsNull() throws Exception
    {
        short[] layoutHints = new short[1];
        layoutHints[0] = 1;
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 0, new Variant("string"));
        parameters.put((short) 1, new Variant(5, "u"));
        parameters.put((short) 2, new Variant(layoutHints, "aq"));
        ListPropertyWidgetRecordAJ listPropertyWidgetRecordAJ = new ListPropertyWidgetRecordAJ();
        listPropertyWidgetRecordAJ.recordId = 1;
        ListPropertyWidgetRecordAJ[] listPropertyWidgetRecordAJs = new ListPropertyWidgetRecordAJ[1];
        listPropertyWidgetRecordAJs[0] = listPropertyWidgetRecordAJ;
        ListPropertyControl listProperty = getMockListProperty((short) 1, 0, parameters, listPropertyWidgetRecordAJs);
        setupDataForValidateListPropertyBusObjectsAndStartTest(listProperty);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_07(), "Label cannot be null");
    }

    @Test
    public void testValidateListPropertyBusObjectsFailsWhenSecuredListPropertyValueLabelIsNull() throws Exception
    {
        short[] layoutHints = new short[1];
        layoutHints[0] = 1;
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 0, new Variant("string"));
        parameters.put((short) 1, new Variant(5, "u"));
        parameters.put((short) 2, new Variant(layoutHints, "aq"));
        ListPropertyWidgetRecordAJ listPropertyWidgetRecordAJ = new ListPropertyWidgetRecordAJ();
        listPropertyWidgetRecordAJ.recordId = 1;
        ListPropertyWidgetRecordAJ[] listPropertyWidgetRecordAJs = new ListPropertyWidgetRecordAJ[1];
        listPropertyWidgetRecordAJs[0] = listPropertyWidgetRecordAJ;
        ListPropertyControlSecured securedListProperty = getMockSecuredListProperty((short) 1, 1, parameters, listPropertyWidgetRecordAJs);
        setupDataForValidateListPropertyBusObjectsAndStartTest(securedListProperty);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_07(), "Label cannot be null");
    }

    @Test
    public void testValidateListPropertyBusObjectsFailsWhenListPropertyValueLabelIsEmpty() throws Exception
    {
        short[] layoutHints = new short[1];
        layoutHints[0] = 1;
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 0, new Variant("string"));
        parameters.put((short) 1, new Variant(5, "u"));
        parameters.put((short) 2, new Variant(layoutHints, "aq"));
        ListPropertyWidgetRecordAJ listPropertyWidgetRecordAJ = new ListPropertyWidgetRecordAJ();
        listPropertyWidgetRecordAJ.recordId = 1;
        listPropertyWidgetRecordAJ.label = "";
        ListPropertyWidgetRecordAJ[] listPropertyWidgetRecordAJs = new ListPropertyWidgetRecordAJ[1];
        listPropertyWidgetRecordAJs[0] = listPropertyWidgetRecordAJ;
        ListPropertyControl listProperty = getMockListProperty((short) 1, 0, parameters, listPropertyWidgetRecordAJs);
        setupDataForValidateListPropertyBusObjectsAndStartTest(listProperty);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_07(), "Label cannot be empty");
    }

    @Test
    public void testValidateListPropertyBusObjectsFailsWhenSecuredListPropertyValueLabelIsEmpty() throws Exception
    {
        short[] layoutHints = new short[1];
        layoutHints[0] = 1;
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 0, new Variant("string"));
        parameters.put((short) 1, new Variant(5, "u"));
        parameters.put((short) 2, new Variant(layoutHints, "aq"));
        ListPropertyWidgetRecordAJ listPropertyWidgetRecordAJ = new ListPropertyWidgetRecordAJ();
        listPropertyWidgetRecordAJ.recordId = 1;
        listPropertyWidgetRecordAJ.label = "";
        ListPropertyWidgetRecordAJ[] listPropertyWidgetRecordAJs = new ListPropertyWidgetRecordAJ[1];
        listPropertyWidgetRecordAJs[0] = listPropertyWidgetRecordAJ;
        ListPropertyControlSecured securedListProperty = getMockSecuredListProperty((short) 1, 1, parameters, listPropertyWidgetRecordAJs);
        setupDataForValidateListPropertyBusObjectsAndStartTest(securedListProperty);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_07(), "Label cannot be empty");
    }

    @Test
    public void testValidateListPropertyBusObjectsFailsWhenListPropertyValueRecordIdsAreNotUnique() throws Exception
    {
        short[] layoutHints = new short[1];
        layoutHints[0] = 1;
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 0, new Variant("string"));
        parameters.put((short) 1, new Variant(5, "u"));
        parameters.put((short) 2, new Variant(layoutHints, "aq"));
        ListPropertyWidgetRecordAJ listPropertyWidgetRecordAJ1 = new ListPropertyWidgetRecordAJ();
        listPropertyWidgetRecordAJ1.recordId = 1;
        listPropertyWidgetRecordAJ1.label = "label1";
        ListPropertyWidgetRecordAJ listPropertyWidgetRecordAJ2 = new ListPropertyWidgetRecordAJ();
        listPropertyWidgetRecordAJ2.recordId = 1;
        listPropertyWidgetRecordAJ2.label = "label2";
        ListPropertyWidgetRecordAJ[] listPropertyWidgetRecordAJs = new ListPropertyWidgetRecordAJ[2];
        listPropertyWidgetRecordAJs[0] = listPropertyWidgetRecordAJ1;
        listPropertyWidgetRecordAJs[1] = listPropertyWidgetRecordAJ2;
        ListPropertyControl listProperty = getMockListProperty((short) 1, 0, parameters, listPropertyWidgetRecordAJs);
        setupDataForValidateListPropertyBusObjectsAndStartTest(listProperty);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_07(), "Record IDs need to be unique");
    }

    @Test
    public void testValidateListPropertyBusObjectsFailsWhenSecuredListPropertyValueRecordIdsAreNotUnique() throws Exception
    {
        short[] layoutHints = new short[1];
        layoutHints[0] = 1;
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 0, new Variant("string"));
        parameters.put((short) 1, new Variant(5, "u"));
        parameters.put((short) 2, new Variant(layoutHints, "aq"));
        ListPropertyWidgetRecordAJ listPropertyWidgetRecordAJ1 = new ListPropertyWidgetRecordAJ();
        listPropertyWidgetRecordAJ1.recordId = 1;
        listPropertyWidgetRecordAJ1.label = "label1";
        ListPropertyWidgetRecordAJ listPropertyWidgetRecordAJ2 = new ListPropertyWidgetRecordAJ();
        listPropertyWidgetRecordAJ2.recordId = 1;
        listPropertyWidgetRecordAJ2.label = "label2";
        ListPropertyWidgetRecordAJ[] listPropertyWidgetRecordAJs = new ListPropertyWidgetRecordAJ[2];
        listPropertyWidgetRecordAJs[0] = listPropertyWidgetRecordAJ1;
        listPropertyWidgetRecordAJs[1] = listPropertyWidgetRecordAJ2;
        ListPropertyControlSecured securedListProperty = getMockSecuredListProperty((short) 1, 1, parameters, listPropertyWidgetRecordAJs);
        setupDataForValidateListPropertyBusObjectsAndStartTest(securedListProperty);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_07(), "Record IDs need to be unique");
    }

    @Test
    public void testValidateListPropertyBusObjectsPassesWhenListPropertyHasAllValidValues() throws Exception
    {
        short[] layoutHints = new short[1];
        layoutHints[0] = 1;
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 0, new Variant("string"));
        parameters.put((short) 1, new Variant(5, "u"));
        parameters.put((short) 2, new Variant(layoutHints, "aq"));
        ListPropertyControl listProperty = getMockListProperty((short) 1, 0, parameters, getListPropertyWidgetRecordAJs());
        setupDataForValidateListPropertyBusObjectsAndStartTest(listProperty);

        executeTestMethod(getTestWrapperFor_v1_07());
    }

    @Test
    public void testValidateListPropertyBusObjectsPassesWhenSecuredListPropertyHasAllValidValues() throws Exception
    {
        short[] layoutHints = new short[1];
        layoutHints[0] = 1;
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 0, new Variant("string"));
        parameters.put((short) 1, new Variant(5, "u"));
        parameters.put((short) 2, new Variant(layoutHints, "aq"));
        ListPropertyControlSecured securedListProperty = getMockSecuredListProperty((short) 1, 1, parameters, getListPropertyWidgetRecordAJs());
        setupDataForValidateListPropertyBusObjectsAndStartTest(securedListProperty);

        executeTestMethod(getTestWrapperFor_v1_07());
    }

    @Test
    public void testValidateListPropertyBusObjectsPassesWhenListPropertyIsMissingKey0And1And2() throws Exception
    {
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        ListPropertyControl listProperty = getMockListProperty((short) 1, 0, parameters, getListPropertyWidgetRecordAJs());
        setupDataForValidateListPropertyBusObjectsAndStartTest(listProperty);

        executeTestMethod(getTestWrapperFor_v1_07());
    }

    @Test
    public void testValidateListPropertyBusObjectsPassesWhenSecuredListPropertyIsMissingKey0And1And2() throws Exception
    {
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        ListPropertyControlSecured securedListProperty = getMockSecuredListProperty((short) 1, 1, parameters, getListPropertyWidgetRecordAJs());
        setupDataForValidateListPropertyBusObjectsAndStartTest(securedListProperty);

        executeTestMethod(getTestWrapperFor_v1_07());
    }

    @Test
    public void testValidateListPropertyBusObjectsPassesWhenListPropertyValueIsEmpty() throws Exception
    {
        short[] layoutHints = new short[1];
        layoutHints[0] = 1;
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 0, new Variant("string"));
        parameters.put((short) 1, new Variant(5, "u"));
        parameters.put((short) 2, new Variant(layoutHints, "aq"));
        ListPropertyControl listProperty = getMockListProperty((short) 1, 0, parameters, new ListPropertyWidgetRecordAJ[0]);
        setupDataForValidateListPropertyBusObjectsAndStartTest(listProperty);

        executeTestMethod(getTestWrapperFor_v1_07());
    }

    @Test
    public void testValidateListPropertyBusObjectsPassesWhenSecuredListPropertyValueIsEmpty() throws Exception
    {
        short[] layoutHints = new short[1];
        layoutHints[0] = 1;
        HashMap<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put((short) 0, new Variant("string"));
        parameters.put((short) 1, new Variant(5, "u"));
        parameters.put((short) 2, new Variant(layoutHints, "aq"));
        ListPropertyControlSecured securedListProperty = getMockSecuredListProperty((short) 1, 1, parameters, new ListPropertyWidgetRecordAJ[0]);
        setupDataForValidateListPropertyBusObjectsAndStartTest(securedListProperty);

        executeTestMethod(getTestWrapperFor_v1_07());
    }

    @Test
    public void testValidateNotificationActioBusObjectsAddsNoteWhenNoneFound() throws Exception
    {
        when(mockIntrospector.getInterfacesExposedOnBusBasedOnName(NOTIFICATION_ACTION_INTERFACE_NAME)).thenReturn(new ArrayList<InterfaceDetail>());

        executeTestMethod(getTestWrapperFor_v1_08());
        verify(mockTestContext).addNote("No bus objects implement NotificationAction interface");
    }

    @Test
    public void testValidateNotificationActionBusObjectsFailsIfFoundAtInvalidPath() throws Exception
    {
        List<InterfaceDetail> interfaceDetailList = getInterfaceDetailList("invalid_path");
        when(mockIntrospector.getInterfacesExposedOnBusBasedOnName(NOTIFICATION_ACTION_INTERFACE_NAME)).thenReturn(interfaceDetailList);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_08(), "invalid_path does not match the expected pattern /ControlPanel/{unit}/{actionPanelName}");
    }

    @Test
    public void testValidateNotificationActionBusObjectsFailsWhenVersionDoesNotMatch() throws Exception
    {
        NotificationAction notificationAction = getMockNotificationAction((short) 2);
        List<InterfaceDetail> interfaceDetailList = getInterfaceDetailList(NOTIFICATION_ACTION_PATH);
        when(mockIntrospector.getInterfacesExposedOnBusBasedOnName(NOTIFICATION_ACTION_INTERFACE_NAME)).thenReturn(interfaceDetailList);
        when(mockIntrospector.getInterface(NOTIFICATION_ACTION_PATH, NotificationAction.class)).thenReturn(notificationAction);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_08(), "Interface version does not match expected:<1> but was:<2>");
    }

    @Test
    public void testValidateNotificationActionBusObjectsFailsIfNoContainerOrDialogObjectExistsUnderPanel() throws Exception
    {
        NotificationAction notificationAction = getMockNotificationAction((short) 1);
        List<InterfaceDetail> interfaceDetailList = getInterfaceDetailList(NOTIFICATION_ACTION_PATH);
        when(mockIntrospector.getInterfacesExposedOnBusBasedOnName(NOTIFICATION_ACTION_INTERFACE_NAME)).thenReturn(interfaceDetailList);
        when(mockIntrospector.getInterface(NOTIFICATION_ACTION_PATH, NotificationAction.class)).thenReturn(notificationAction);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_08(), "No object implementing org.alljoyn.ControlPanel.Container nor org.alljoyn.ControlPanel.SecuredContainer "
                + "nor org.alljoyn.ControlPanel.Dialog nor org.alljoyn.ControlPanel.SecuredDialog is found under path /ControlPanel/unit/panelName");
    }

    @Test
    public void testValidateNotificationActionBusObjectsPassesIfValidContainerObjectExistsUnderPanel() throws Exception
    {
        NotificationAction notificationAction = getMockNotificationAction((short) 1);
        List<InterfaceDetail> interfaceDetailList = getInterfaceDetailList(NOTIFICATION_ACTION_PATH);
        when(mockIntrospector.getInterfacesExposedOnBusBasedOnName(NOTIFICATION_ACTION_INTERFACE_NAME)).thenReturn(interfaceDetailList);
        when(mockIntrospector.getInterface(NOTIFICATION_ACTION_PATH, NotificationAction.class)).thenReturn(notificationAction);
        List<InterfaceDetail> containerInterfaceDetailList = getInterfaceDetailList(NOTIFICATION_ACTION_CONTAINER_PATH);
        when(mockIntrospector.getInterfacesExposedOnBusUnderSpecifiedPathBasedOnName(NOTIFICATION_ACTION_PATH, CONTAINER_INTERFACE_NAME)).thenReturn(containerInterfaceDetailList);

        executeTestMethod(getTestWrapperFor_v1_08());
    }

    @Test
    public void testValidateNotificationActionBusObjectsPassesIfValidSecuredContainerObjectExistsUnderPanel() throws Exception
    {
        NotificationAction notificationAction = getMockNotificationAction((short) 1);
        List<InterfaceDetail> interfaceDetailList = getInterfaceDetailList(NOTIFICATION_ACTION_PATH);
        when(mockIntrospector.getInterfacesExposedOnBusBasedOnName(NOTIFICATION_ACTION_INTERFACE_NAME)).thenReturn(interfaceDetailList);
        when(mockIntrospector.getInterface(NOTIFICATION_ACTION_PATH, NotificationAction.class)).thenReturn(notificationAction);
        List<InterfaceDetail> securedContainerInterfaceDetailList = getInterfaceDetailList(NOTIFICATION_ACTION_CONTAINER_PATH);
        when(mockIntrospector.getInterfacesExposedOnBusUnderSpecifiedPathBasedOnName(NOTIFICATION_ACTION_PATH, SECURED_CONTAINER_INTERFACE_NAME)).thenReturn(
                securedContainerInterfaceDetailList);

        executeTestMethod(getTestWrapperFor_v1_08());
    }

    @Test
    public void testValidateNotificationActionBusObjectsPassesIfValidDialogObjectExistsUnderPanel() throws Exception
    {
        NotificationAction notificationAction = getMockNotificationAction((short) 1);
        List<InterfaceDetail> interfaceDetailList = getInterfaceDetailList(NOTIFICATION_ACTION_PATH);
        when(mockIntrospector.getInterfacesExposedOnBusBasedOnName(NOTIFICATION_ACTION_INTERFACE_NAME)).thenReturn(interfaceDetailList);
        when(mockIntrospector.getInterface(NOTIFICATION_ACTION_PATH, NotificationAction.class)).thenReturn(notificationAction);
        List<InterfaceDetail> dialogInterfaceDetailList = getInterfaceDetailList(NOTIFICATION_ACTION_CONTAINER_PATH);
        when(mockIntrospector.getInterfacesExposedOnBusUnderSpecifiedPathBasedOnName(NOTIFICATION_ACTION_PATH, DIALOG_INTERFACE_NAME)).thenReturn(dialogInterfaceDetailList);

        executeTestMethod(getTestWrapperFor_v1_08());
    }

    @Test
    public void testValidateNotificationActionBusObjectsPassesIfValidSecuredDialogObjectExistsUnderPanel() throws Exception
    {
        NotificationAction notificationAction = getMockNotificationAction((short) 1);
        List<InterfaceDetail> interfaceDetailList = getInterfaceDetailList(NOTIFICATION_ACTION_PATH);
        when(mockIntrospector.getInterfacesExposedOnBusBasedOnName(NOTIFICATION_ACTION_INTERFACE_NAME)).thenReturn(interfaceDetailList);
        when(mockIntrospector.getInterface(NOTIFICATION_ACTION_PATH, NotificationAction.class)).thenReturn(notificationAction);
        List<InterfaceDetail> securedDialogInterfaceDetailList = getInterfaceDetailList(NOTIFICATION_ACTION_CONTAINER_PATH);
        when(mockIntrospector.getInterfacesExposedOnBusUnderSpecifiedPathBasedOnName(NOTIFICATION_ACTION_PATH, SECURED_DIALOG_INTERFACE_NAME)).thenReturn(
                securedDialogInterfaceDetailList);

        executeTestMethod(getTestWrapperFor_v1_08());
    }

    @Test
    public void testValidateHttpControlBusObjectsAddsNoteWhenNoneFound() throws Exception
    {
        when(mockIntrospector.getInterfacesExposedOnBusBasedOnName(HTTPControl.IFNAME)).thenReturn(new ArrayList<InterfaceDetail>());

        executeTestMethod(getTestWrapperFor_v1_09());
        verify(mockTestContext).addNote("No bus objects implement HTTPControl interface");
    }

    @Test
    public void testValidateHttpControlBusObjectsFailsWhenPathDoesNotMatchPattern() throws Exception
    {
        InterfaceDetail interfaceDetail = Mockito.mock(InterfaceDetail.class);
        when(interfaceDetail.getPath()).thenReturn("invalid_path");
        List<InterfaceDetail> interfaceDetailList = new ArrayList<InterfaceDetail>();
        interfaceDetailList.add(interfaceDetail);
        when(mockIntrospector.getInterfacesExposedOnBusBasedOnName(HTTPControl.IFNAME)).thenReturn(interfaceDetailList);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_09(), "invalid_path does not match the expected pattern /ControlPanel/{unit}/HTTPControl");
    }

    @Test
    public void testValidateHttpControlBusObjectsFailsWhenVersionDoesNotMatch() throws Exception
    {
        InterfaceDetail interfaceDetail = Mockito.mock(InterfaceDetail.class);
        when(interfaceDetail.getPath()).thenReturn(HTTP_CONTROL_PATH);
        List<InterfaceDetail> interfaceDetailList = new ArrayList<InterfaceDetail>();
        interfaceDetailList.add(interfaceDetail);

        HTTPControl httpControl = getMockHttpControl((short) 2, "https://www.alljoyn.org");
        when(mockIntrospector.getInterfacesExposedOnBusBasedOnName(HTTPControl.IFNAME)).thenReturn(interfaceDetailList);
        when(mockIntrospector.getInterface(HTTP_CONTROL_PATH, HTTPControl.class)).thenReturn(httpControl);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_09(), "Interface version does not match expected:<1> but was:<2>");
    }

    @Test
    public void testValidateHttpControlBusObjectsFailsWhenRootUrlIsNull() throws Exception
    {
        InterfaceDetail interfaceDetail = Mockito.mock(InterfaceDetail.class);
        when(interfaceDetail.getPath()).thenReturn(HTTP_CONTROL_PATH);
        List<InterfaceDetail> interfaceDetailList = new ArrayList<InterfaceDetail>();
        interfaceDetailList.add(interfaceDetail);

        HTTPControl httpControl = getMockHttpControl((short) 1, null);
        when(mockIntrospector.getInterfacesExposedOnBusBasedOnName(HTTPControl.IFNAME)).thenReturn(interfaceDetailList);
        when(mockIntrospector.getInterface(HTTP_CONTROL_PATH, HTTPControl.class)).thenReturn(httpControl);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_09(), "Root URL returned is null");
    }

    @Test
    public void testValidateHttpControlBusObjectsFailsWhenRootUrlIsInvalid() throws Exception
    {
        InterfaceDetail interfaceDetail = Mockito.mock(InterfaceDetail.class);
        when(interfaceDetail.getPath()).thenReturn(HTTP_CONTROL_PATH);
        List<InterfaceDetail> interfaceDetailList = new ArrayList<InterfaceDetail>();
        interfaceDetailList.add(interfaceDetail);

        HTTPControl httpControl = getMockHttpControl((short) 1, "url");
        when(mockIntrospector.getInterfacesExposedOnBusBasedOnName(HTTPControl.IFNAME)).thenReturn(interfaceDetailList);
        when(mockIntrospector.getInterface(HTTP_CONTROL_PATH, HTTPControl.class)).thenReturn(httpControl);

        when(mockHttpClient.execute((HttpGet) anyObject())).thenThrow(new RuntimeException());

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_09(), "url is not a valid URL");
    }

    @Test
    public void testValidateHttpControlBusObjectsFailsWhenRootUrlIsNotAccessible() throws Exception
    {
        InterfaceDetail interfaceDetail = Mockito.mock(InterfaceDetail.class);
        when(interfaceDetail.getPath()).thenReturn(HTTP_CONTROL_PATH);
        List<InterfaceDetail> interfaceDetailList = new ArrayList<InterfaceDetail>();
        interfaceDetailList.add(interfaceDetail);

        HTTPControl httpControl = getMockHttpControl((short) 1, "https://www.allseenalliance.org/invalidurl");
        when(mockIntrospector.getInterfacesExposedOnBusBasedOnName(HTTPControl.IFNAME)).thenReturn(interfaceDetailList);
        when(mockIntrospector.getInterface(HTTP_CONTROL_PATH, HTTPControl.class)).thenReturn(httpControl);
        when(mockHttpClient.execute((HttpGet) anyObject())).thenThrow(new RuntimeException("url is not accessible"));

        executeTestMethodThrowsException(getTestWrapperFor_v1_09(), "url is not accessible");
    }

    @Test
    public void testValidateHttpControlBusObjectsSuccess() throws Exception
    {
        InterfaceDetail interfaceDetail = Mockito.mock(InterfaceDetail.class);
        when(interfaceDetail.getPath()).thenReturn(HTTP_CONTROL_PATH);
        List<InterfaceDetail> interfaceDetailList = new ArrayList<InterfaceDetail>();
        interfaceDetailList.add(interfaceDetail);

        HTTPControl httpControl = getMockHttpControl((short) 1, "https://www.alljoyn.org");
        when(mockIntrospector.getInterfacesExposedOnBusBasedOnName(HTTPControl.IFNAME)).thenReturn(interfaceDetailList);
        when(mockIntrospector.getInterface(HTTP_CONTROL_PATH, HTTPControl.class)).thenReturn(httpControl);

        HttpResponse httpResponse = getMockHttpResponse();
        when(mockHttpClient.execute((HttpGet) anyObject())).thenReturn(httpResponse);

        executeTestMethod(getTestWrapperFor_v1_09());
    }

    @Test
    public void testValidateSecuredControlPanelBusObjectsAddsNoteWhenNoneFound() throws Exception
    {
        executeTestMethod(getTestWrapperFor_v1_10());
        verify(mockTestContext).addNote("No bus objects implement one of the secured ControlPanel interfaces");
    }

    @Test
    public void testValidateSecuredControlPanelBusObjectsFailsIfSecuredContainerExistsAndExceptionIsNotThrown() throws Exception
    {
        InterfaceDetail interfaceDetail = Mockito.mock(InterfaceDetail.class);
        when(interfaceDetail.getPath()).thenReturn(CONTROL_PANEL_CONTAINER_PATH);
        List<InterfaceDetail> interfaceDetailList = new ArrayList<InterfaceDetail>();
        interfaceDetailList.add(interfaceDetail);
        when(mockIntrospector.getInterfacesExposedOnBusBasedOnName(SECURED_CONTAINER_INTERFACE_NAME)).thenReturn(interfaceDetailList);
        ContainerSecured securedContainer = Mockito.mock(ContainerSecured.class);
        when(mockIntrospector.getInterface(CONTROL_PANEL_CONTAINER_PATH, ContainerSecured.class)).thenReturn(securedContainer);
        when(mockServiceHelper.getAuthPassword(mockAboutAnnouncement)).thenReturn(new char[]
        {});

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_10(), "Exception should be thrown on connecting with the wrong password to retrieve container version!");
        verify(mockServiceHelper).clearPeerAuthenticationFlags(mockAboutAnnouncement);
        verify(mockServiceHelper).setAuthPassword(eq(mockAboutAnnouncement), (char[]) anyObject());
    }

    @Test
    public void testValidateSecuredControlPanelBusObjectsFailsIfSecuredContainerExistsAndPeerIsNotAuthenticated() throws Exception
    {
        InterfaceDetail interfaceDetail = Mockito.mock(InterfaceDetail.class);
        when(interfaceDetail.getPath()).thenReturn(CONTROL_PANEL_CONTAINER_PATH);
        List<InterfaceDetail> interfaceDetailList = new ArrayList<InterfaceDetail>();
        interfaceDetailList.add(interfaceDetail);
        when(mockIntrospector.getInterfacesExposedOnBusBasedOnName(SECURED_CONTAINER_INTERFACE_NAME)).thenReturn(interfaceDetailList);
        ContainerSecured securedContainer = Mockito.mock(ContainerSecured.class);
        when(securedContainer.getVersion()).thenThrow(new BusException());
        when(mockIntrospector.getInterface(CONTROL_PANEL_CONTAINER_PATH, ContainerSecured.class)).thenReturn(securedContainer);
        when(mockServiceHelper.getAuthPassword(mockAboutAnnouncement)).thenReturn(new char[]
        {});

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_10(), "Authentication should have failed");
        verify(mockServiceHelper).clearPeerAuthenticationFlags(mockAboutAnnouncement);
        verify(mockServiceHelper).setAuthPassword(eq(mockAboutAnnouncement), (char[]) anyObject());
    }

    @Test
    public void testValidateSecuredControlPanelBusObjectsFailsIfSecuredContainerExistsAndPeerAuthenticationIsSuccessful() throws Exception
    {
        InterfaceDetail interfaceDetail = Mockito.mock(InterfaceDetail.class);
        when(interfaceDetail.getPath()).thenReturn(CONTROL_PANEL_CONTAINER_PATH);
        List<InterfaceDetail> interfaceDetailList = new ArrayList<InterfaceDetail>();
        interfaceDetailList.add(interfaceDetail);
        when(mockIntrospector.getInterfacesExposedOnBusBasedOnName(SECURED_CONTAINER_INTERFACE_NAME)).thenReturn(interfaceDetailList);
        ContainerSecured securedContainer = Mockito.mock(ContainerSecured.class);
        when(securedContainer.getVersion()).thenThrow(new BusException());
        when(mockIntrospector.getInterface(CONTROL_PANEL_CONTAINER_PATH, ContainerSecured.class)).thenReturn(securedContainer);
        when(mockServiceHelper.getAuthPassword(mockAboutAnnouncement)).thenReturn(new char[]
        {});
        when(mockServiceHelper.isPeerAuthenticationAttempted(mockAboutAnnouncement)).thenReturn(true);
        when(mockServiceHelper.isPeerAuthenticationSuccessful(mockAboutAnnouncement)).thenReturn(true);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_10(), "Authentication should have failed");
        verify(mockServiceHelper).clearPeerAuthenticationFlags(mockAboutAnnouncement);
        verify(mockServiceHelper).setAuthPassword(eq(mockAboutAnnouncement), (char[]) anyObject());
    }

    @Test
    public void testValidateSecuredControlPanelBusObjectsPassesIfSecuredContainerExistsAndPeerAuthenticationFails() throws Exception
    {
        InterfaceDetail interfaceDetail = Mockito.mock(InterfaceDetail.class);
        when(interfaceDetail.getPath()).thenReturn(CONTROL_PANEL_CONTAINER_PATH);
        List<InterfaceDetail> interfaceDetailList = new ArrayList<InterfaceDetail>();
        interfaceDetailList.add(interfaceDetail);
        when(mockIntrospector.getInterfacesExposedOnBusBasedOnName(SECURED_CONTAINER_INTERFACE_NAME)).thenReturn(interfaceDetailList);
        ContainerSecured securedContainer = Mockito.mock(ContainerSecured.class);
        when(securedContainer.getVersion()).thenThrow(new BusException());
        when(mockIntrospector.getInterface(CONTROL_PANEL_CONTAINER_PATH, ContainerSecured.class)).thenReturn(securedContainer);
        when(mockServiceHelper.getAuthPassword(mockAboutAnnouncement)).thenReturn(new char[]
        {});
        when(mockServiceHelper.isPeerAuthenticationAttempted(mockAboutAnnouncement)).thenReturn(true);
        when(mockServiceHelper.isPeerAuthenticationSuccessful(mockAboutAnnouncement)).thenReturn(false);

        executeTestMethod(getTestWrapperFor_v1_10());
        verify(mockServiceHelper).clearPeerAuthenticationFlags(mockAboutAnnouncement);
        verify(mockServiceHelper).setAuthPassword(eq(mockAboutAnnouncement), (char[]) anyObject());
    }

    @Test
    public void testValidateSecuredControlPanelBusObjectsFailsIfSecuredPropertyExistsAndExceptionIsNotThrown() throws Exception
    {
        InterfaceDetail interfaceDetail = Mockito.mock(InterfaceDetail.class);
        when(interfaceDetail.getPath()).thenReturn(CONTROL_PANEL_PROPERTY_PATH);
        List<InterfaceDetail> interfaceDetailList = new ArrayList<InterfaceDetail>();
        interfaceDetailList.add(interfaceDetail);
        when(mockIntrospector.getInterfacesExposedOnBusBasedOnName(SECURED_PROPERTY_INTERFACE_NAME)).thenReturn(interfaceDetailList);
        PropertyControlSecured securedProperty = Mockito.mock(PropertyControlSecured.class);
        when(mockIntrospector.getInterface(CONTROL_PANEL_PROPERTY_PATH, PropertyControlSecured.class)).thenReturn(securedProperty);
        when(mockServiceHelper.getAuthPassword(mockAboutAnnouncement)).thenReturn(new char[]
        {});

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_10(), "Exception should be thrown on connecting with the wrong password to retrieve property version!");
        verify(mockServiceHelper).clearPeerAuthenticationFlags(mockAboutAnnouncement);
        verify(mockServiceHelper).setAuthPassword(eq(mockAboutAnnouncement), (char[]) anyObject());
    }

    @Test
    public void testValidateSecuredControlPanelBusObjectsFailsIfSecuredPropertyExistsAndPeerIsNotAuthenticated() throws Exception
    {
        InterfaceDetail interfaceDetail = Mockito.mock(InterfaceDetail.class);
        when(interfaceDetail.getPath()).thenReturn(CONTROL_PANEL_PROPERTY_PATH);
        List<InterfaceDetail> interfaceDetailList = new ArrayList<InterfaceDetail>();
        interfaceDetailList.add(interfaceDetail);
        when(mockIntrospector.getInterfacesExposedOnBusBasedOnName(SECURED_PROPERTY_INTERFACE_NAME)).thenReturn(interfaceDetailList);
        PropertyControlSecured securedProperty = Mockito.mock(PropertyControlSecured.class);
        when(securedProperty.getVersion()).thenThrow(new BusException());
        when(mockIntrospector.getInterface(CONTROL_PANEL_PROPERTY_PATH, PropertyControlSecured.class)).thenReturn(securedProperty);
        when(mockServiceHelper.getAuthPassword(mockAboutAnnouncement)).thenReturn(new char[]
        {});

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_10(), "Authentication should have failed");
        verify(mockServiceHelper).clearPeerAuthenticationFlags(mockAboutAnnouncement);
        verify(mockServiceHelper).setAuthPassword(eq(mockAboutAnnouncement), (char[]) anyObject());
    }

    @Test
    public void testValidateSecuredControlPanelBusObjectsFailsIfSecuredPropertyExistsAndPeerAuthenticationIsSuccessful() throws Exception
    {
        InterfaceDetail interfaceDetail = Mockito.mock(InterfaceDetail.class);
        when(interfaceDetail.getPath()).thenReturn(CONTROL_PANEL_PROPERTY_PATH);
        List<InterfaceDetail> interfaceDetailList = new ArrayList<InterfaceDetail>();
        interfaceDetailList.add(interfaceDetail);
        when(mockIntrospector.getInterfacesExposedOnBusBasedOnName(SECURED_PROPERTY_INTERFACE_NAME)).thenReturn(interfaceDetailList);
        PropertyControlSecured securedProperty = Mockito.mock(PropertyControlSecured.class);
        when(securedProperty.getVersion()).thenThrow(new BusException());
        when(mockIntrospector.getInterface(CONTROL_PANEL_PROPERTY_PATH, PropertyControlSecured.class)).thenReturn(securedProperty);
        when(mockServiceHelper.getAuthPassword(mockAboutAnnouncement)).thenReturn(new char[]
        {});
        when(mockServiceHelper.isPeerAuthenticationAttempted(mockAboutAnnouncement)).thenReturn(true);
        when(mockServiceHelper.isPeerAuthenticationSuccessful(mockAboutAnnouncement)).thenReturn(true);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_10(), "Authentication should have failed");
        verify(mockServiceHelper).clearPeerAuthenticationFlags(mockAboutAnnouncement);
        verify(mockServiceHelper).setAuthPassword(eq(mockAboutAnnouncement), (char[]) anyObject());
    }

    @Test
    public void testValidateSecuredControlPanelBusObjectsPassesIfSecuredPropertyExistsAndPeerAuthenticationFails() throws Exception
    {
        InterfaceDetail interfaceDetail = Mockito.mock(InterfaceDetail.class);
        when(interfaceDetail.getPath()).thenReturn(CONTROL_PANEL_PROPERTY_PATH);
        List<InterfaceDetail> interfaceDetailList = new ArrayList<InterfaceDetail>();
        interfaceDetailList.add(interfaceDetail);
        when(mockIntrospector.getInterfacesExposedOnBusBasedOnName(SECURED_PROPERTY_INTERFACE_NAME)).thenReturn(interfaceDetailList);
        PropertyControlSecured securedProperty = Mockito.mock(PropertyControlSecured.class);
        when(securedProperty.getVersion()).thenThrow(new BusException());
        when(mockIntrospector.getInterface(CONTROL_PANEL_PROPERTY_PATH, PropertyControlSecured.class)).thenReturn(securedProperty);
        when(mockServiceHelper.getAuthPassword(mockAboutAnnouncement)).thenReturn(new char[]
        {});
        when(mockServiceHelper.isPeerAuthenticationAttempted(mockAboutAnnouncement)).thenReturn(true);
        when(mockServiceHelper.isPeerAuthenticationSuccessful(mockAboutAnnouncement)).thenReturn(false);

        executeTestMethod(getTestWrapperFor_v1_10());
        verify(mockServiceHelper).clearPeerAuthenticationFlags(mockAboutAnnouncement);
        verify(mockServiceHelper).setAuthPassword(eq(mockAboutAnnouncement), (char[]) anyObject());
    }

    @Test
    public void testValidateSecuredControlPanelBusObjectsFailsIfSecuredActionExistsAndExceptionIsNotThrown() throws Exception
    {
        InterfaceDetail interfaceDetail = Mockito.mock(InterfaceDetail.class);
        when(interfaceDetail.getPath()).thenReturn(CONTROL_PANEL_ACTION_PATH);
        List<InterfaceDetail> interfaceDetailList = new ArrayList<InterfaceDetail>();
        interfaceDetailList.add(interfaceDetail);
        when(mockIntrospector.getInterfacesExposedOnBusBasedOnName(SECURED_ACTION_INTERFACE_NAME)).thenReturn(interfaceDetailList);
        ActionControlSecured securedAction = Mockito.mock(ActionControlSecured.class);
        when(mockIntrospector.getInterface(CONTROL_PANEL_ACTION_PATH, ActionControlSecured.class)).thenReturn(securedAction);
        when(mockServiceHelper.getAuthPassword(mockAboutAnnouncement)).thenReturn(new char[]
        {});

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_10(), "Exception should be thrown on connecting with the wrong password to retrieve action version!");
        verify(mockServiceHelper).clearPeerAuthenticationFlags(mockAboutAnnouncement);
        verify(mockServiceHelper).setAuthPassword(eq(mockAboutAnnouncement), (char[]) anyObject());
    }

    @Test
    public void testValidateSecuredControlPanelBusObjectsFailsIfSecuredActionExistsAndPeerIsNotAuthenticated() throws Exception
    {
        InterfaceDetail interfaceDetail = Mockito.mock(InterfaceDetail.class);
        when(interfaceDetail.getPath()).thenReturn(CONTROL_PANEL_ACTION_PATH);
        List<InterfaceDetail> interfaceDetailList = new ArrayList<InterfaceDetail>();
        interfaceDetailList.add(interfaceDetail);
        when(mockIntrospector.getInterfacesExposedOnBusBasedOnName(SECURED_ACTION_INTERFACE_NAME)).thenReturn(interfaceDetailList);
        ActionControlSecured securedAction = Mockito.mock(ActionControlSecured.class);
        when(securedAction.getVersion()).thenThrow(new BusException());
        when(mockIntrospector.getInterface(CONTROL_PANEL_ACTION_PATH, ActionControlSecured.class)).thenReturn(securedAction);
        when(mockServiceHelper.getAuthPassword(mockAboutAnnouncement)).thenReturn(new char[]
        {});

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_10(), "Authentication should have failed");
        verify(mockServiceHelper).clearPeerAuthenticationFlags(mockAboutAnnouncement);
        verify(mockServiceHelper).setAuthPassword(eq(mockAboutAnnouncement), (char[]) anyObject());
    }

    @Test
    public void testValidateSecuredControlPanelBusObjectsFailsIfSecuredActionExistsAndPeerAuthenticationIsSuccessful() throws Exception
    {
        InterfaceDetail interfaceDetail = Mockito.mock(InterfaceDetail.class);
        when(interfaceDetail.getPath()).thenReturn(CONTROL_PANEL_ACTION_PATH);
        List<InterfaceDetail> interfaceDetailList = new ArrayList<InterfaceDetail>();
        interfaceDetailList.add(interfaceDetail);
        when(mockIntrospector.getInterfacesExposedOnBusBasedOnName(SECURED_ACTION_INTERFACE_NAME)).thenReturn(interfaceDetailList);
        ActionControlSecured securedAction = Mockito.mock(ActionControlSecured.class);
        when(securedAction.getVersion()).thenThrow(new BusException());
        when(mockIntrospector.getInterface(CONTROL_PANEL_ACTION_PATH, ActionControlSecured.class)).thenReturn(securedAction);
        when(mockServiceHelper.getAuthPassword(mockAboutAnnouncement)).thenReturn(new char[]
        {});
        when(mockServiceHelper.isPeerAuthenticationAttempted(mockAboutAnnouncement)).thenReturn(true);
        when(mockServiceHelper.isPeerAuthenticationSuccessful(mockAboutAnnouncement)).thenReturn(true);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_10(), "Authentication should have failed");
        verify(mockServiceHelper).clearPeerAuthenticationFlags(mockAboutAnnouncement);
        verify(mockServiceHelper).setAuthPassword(eq(mockAboutAnnouncement), (char[]) anyObject());
    }

    @Test
    public void testValidateSecuredControlPanelBusObjectsPassesIfSecuredActionExistsAndPeerAuthenticationFails() throws Exception
    {
        InterfaceDetail interfaceDetail = Mockito.mock(InterfaceDetail.class);
        when(interfaceDetail.getPath()).thenReturn(CONTROL_PANEL_ACTION_PATH);
        List<InterfaceDetail> interfaceDetailList = new ArrayList<InterfaceDetail>();
        interfaceDetailList.add(interfaceDetail);
        when(mockIntrospector.getInterfacesExposedOnBusBasedOnName(SECURED_ACTION_INTERFACE_NAME)).thenReturn(interfaceDetailList);
        ActionControlSecured securedAction = Mockito.mock(ActionControlSecured.class);
        when(securedAction.getVersion()).thenThrow(new BusException());
        when(mockIntrospector.getInterface(CONTROL_PANEL_ACTION_PATH, ActionControlSecured.class)).thenReturn(securedAction);
        when(mockServiceHelper.getAuthPassword(mockAboutAnnouncement)).thenReturn(new char[]
        {});
        when(mockServiceHelper.isPeerAuthenticationAttempted(mockAboutAnnouncement)).thenReturn(true);
        when(mockServiceHelper.isPeerAuthenticationSuccessful(mockAboutAnnouncement)).thenReturn(false);

        executeTestMethod(getTestWrapperFor_v1_10());
        verify(mockServiceHelper).clearPeerAuthenticationFlags(mockAboutAnnouncement);
        verify(mockServiceHelper).setAuthPassword(eq(mockAboutAnnouncement), (char[]) anyObject());
    }

    @Test
    public void testValidateSecuredControlPanelBusObjectsFailsIfSecuredDialogExistsAndExceptionIsNotThrown() throws Exception
    {
        InterfaceDetail interfaceDetail = Mockito.mock(InterfaceDetail.class);
        when(interfaceDetail.getPath()).thenReturn(CONTROL_PANEL_DIALOG_PATH);
        List<InterfaceDetail> interfaceDetailList = new ArrayList<InterfaceDetail>();
        interfaceDetailList.add(interfaceDetail);
        when(mockIntrospector.getInterfacesExposedOnBusBasedOnName(SECURED_DIALOG_INTERFACE_NAME)).thenReturn(interfaceDetailList);
        AlertDialogSecured securedDialog = Mockito.mock(AlertDialogSecured.class);
        when(mockIntrospector.getInterface(CONTROL_PANEL_DIALOG_PATH, AlertDialogSecured.class)).thenReturn(securedDialog);
        when(mockServiceHelper.getAuthPassword(mockAboutAnnouncement)).thenReturn(new char[]
        {});

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_10(), "Exception should be thrown on connecting with the wrong password to retrieve dialog version!");
        verify(mockServiceHelper).clearPeerAuthenticationFlags(mockAboutAnnouncement);
        verify(mockServiceHelper).setAuthPassword(eq(mockAboutAnnouncement), (char[]) anyObject());
    }

    @Test
    public void testValidateSecuredControlPanelBusObjectsFailsIfSecuredDialogExistsAndPeerIsNotAuthenticated() throws Exception
    {
        InterfaceDetail interfaceDetail = Mockito.mock(InterfaceDetail.class);
        when(interfaceDetail.getPath()).thenReturn(CONTROL_PANEL_DIALOG_PATH);
        List<InterfaceDetail> interfaceDetailList = new ArrayList<InterfaceDetail>();
        interfaceDetailList.add(interfaceDetail);
        when(mockIntrospector.getInterfacesExposedOnBusBasedOnName(SECURED_DIALOG_INTERFACE_NAME)).thenReturn(interfaceDetailList);
        AlertDialogSecured securedDialog = Mockito.mock(AlertDialogSecured.class);
        when(securedDialog.getVersion()).thenThrow(new BusException());
        when(mockIntrospector.getInterface(CONTROL_PANEL_DIALOG_PATH, AlertDialogSecured.class)).thenReturn(securedDialog);
        when(mockServiceHelper.getAuthPassword(mockAboutAnnouncement)).thenReturn(new char[]
        {});

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_10(), "Authentication should have failed");
        verify(mockServiceHelper).clearPeerAuthenticationFlags(mockAboutAnnouncement);
        verify(mockServiceHelper).setAuthPassword(eq(mockAboutAnnouncement), (char[]) anyObject());
    }

    @Test
    public void testValidateSecuredControlPanelBusObjectsFailsIfSecuredDialogExistsAndPeerAuthenticationIsSuccessful() throws Exception
    {
        InterfaceDetail interfaceDetail = Mockito.mock(InterfaceDetail.class);
        when(interfaceDetail.getPath()).thenReturn(CONTROL_PANEL_DIALOG_PATH);
        List<InterfaceDetail> interfaceDetailList = new ArrayList<InterfaceDetail>();
        interfaceDetailList.add(interfaceDetail);
        when(mockIntrospector.getInterfacesExposedOnBusBasedOnName(SECURED_DIALOG_INTERFACE_NAME)).thenReturn(interfaceDetailList);
        AlertDialogSecured securedDialog = Mockito.mock(AlertDialogSecured.class);
        when(securedDialog.getVersion()).thenThrow(new BusException());
        when(mockIntrospector.getInterface(CONTROL_PANEL_DIALOG_PATH, AlertDialogSecured.class)).thenReturn(securedDialog);
        when(mockServiceHelper.getAuthPassword(mockAboutAnnouncement)).thenReturn(new char[]
        {});
        when(mockServiceHelper.isPeerAuthenticationAttempted(mockAboutAnnouncement)).thenReturn(true);
        when(mockServiceHelper.isPeerAuthenticationSuccessful(mockAboutAnnouncement)).thenReturn(true);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_10(), "Authentication should have failed");
        verify(mockServiceHelper).clearPeerAuthenticationFlags(mockAboutAnnouncement);
        verify(mockServiceHelper).setAuthPassword(eq(mockAboutAnnouncement), (char[]) anyObject());
    }

    @Test
    public void testValidateSecuredControlPanelBusObjectsPassesIfSecuredDialogExistsAndPeerAuthenticationFails() throws Exception
    {
        InterfaceDetail interfaceDetail = Mockito.mock(InterfaceDetail.class);
        when(interfaceDetail.getPath()).thenReturn(CONTROL_PANEL_DIALOG_PATH);
        List<InterfaceDetail> interfaceDetailList = new ArrayList<InterfaceDetail>();
        interfaceDetailList.add(interfaceDetail);
        when(mockIntrospector.getInterfacesExposedOnBusBasedOnName(SECURED_DIALOG_INTERFACE_NAME)).thenReturn(interfaceDetailList);
        AlertDialogSecured securedDialog = Mockito.mock(AlertDialogSecured.class);
        when(securedDialog.getVersion()).thenThrow(new BusException());
        when(mockIntrospector.getInterface(CONTROL_PANEL_DIALOG_PATH, AlertDialogSecured.class)).thenReturn(securedDialog);
        when(mockServiceHelper.getAuthPassword(mockAboutAnnouncement)).thenReturn(new char[]
        {});
        when(mockServiceHelper.isPeerAuthenticationAttempted(mockAboutAnnouncement)).thenReturn(true);
        when(mockServiceHelper.isPeerAuthenticationSuccessful(mockAboutAnnouncement)).thenReturn(false);

        executeTestMethod(getTestWrapperFor_v1_10());
        verify(mockServiceHelper).clearPeerAuthenticationFlags(mockAboutAnnouncement);
        verify(mockServiceHelper).setAuthPassword(eq(mockAboutAnnouncement), (char[]) anyObject());
    }

    @Test
    public void testValidateSecuredControlPanelBusObjectsFailsIfSecuredListPropertyExistsAndExceptionIsNotThrown() throws Exception
    {
        InterfaceDetail interfaceDetail = Mockito.mock(InterfaceDetail.class);
        when(interfaceDetail.getPath()).thenReturn(CONTROL_PANEL_LIST_PROPERTY_PATH);
        List<InterfaceDetail> interfaceDetailList = new ArrayList<InterfaceDetail>();
        interfaceDetailList.add(interfaceDetail);
        when(mockIntrospector.getInterfacesExposedOnBusBasedOnName(SECURED_LIST_PROPERTY_INTERFACE_NAME)).thenReturn(interfaceDetailList);
        ListPropertyControlSecured securedListProperty = Mockito.mock(ListPropertyControlSecured.class);
        when(mockIntrospector.getInterface(CONTROL_PANEL_LIST_PROPERTY_PATH, ListPropertyControlSecured.class)).thenReturn(securedListProperty);
        when(mockServiceHelper.getAuthPassword(mockAboutAnnouncement)).thenReturn(new char[]
        {});

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_10(), "Exception should be thrown on connecting with the wrong password to retrieve list property version!");
        verify(mockServiceHelper).clearPeerAuthenticationFlags(mockAboutAnnouncement);
        verify(mockServiceHelper).setAuthPassword(eq(mockAboutAnnouncement), (char[]) anyObject());
    }

    @Test
    public void testValidateSecuredControlPanelBusObjectsFailsIfSecuredListPropertyExistsAndPeerIsNotAuthenticated() throws Exception
    {
        InterfaceDetail interfaceDetail = Mockito.mock(InterfaceDetail.class);
        when(interfaceDetail.getPath()).thenReturn(CONTROL_PANEL_LIST_PROPERTY_PATH);
        List<InterfaceDetail> interfaceDetailList = new ArrayList<InterfaceDetail>();
        interfaceDetailList.add(interfaceDetail);
        when(mockIntrospector.getInterfacesExposedOnBusBasedOnName(SECURED_LIST_PROPERTY_INTERFACE_NAME)).thenReturn(interfaceDetailList);
        ListPropertyControlSecured securedListProperty = Mockito.mock(ListPropertyControlSecured.class);
        when(securedListProperty.getVersion()).thenThrow(new BusException());
        when(mockIntrospector.getInterface(CONTROL_PANEL_LIST_PROPERTY_PATH, ListPropertyControlSecured.class)).thenReturn(securedListProperty);
        when(mockServiceHelper.getAuthPassword(mockAboutAnnouncement)).thenReturn(new char[]
        {});

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_10(), "Authentication should have failed");
        verify(mockServiceHelper).clearPeerAuthenticationFlags(mockAboutAnnouncement);
        verify(mockServiceHelper).setAuthPassword(eq(mockAboutAnnouncement), (char[]) anyObject());
    }

    @Test
    public void testValidateSecuredControlPanelBusObjectsFailsIfSecuredListPropertyExistsAndPeerAuthenticationIsSuccessful() throws Exception
    {
        InterfaceDetail interfaceDetail = Mockito.mock(InterfaceDetail.class);
        when(interfaceDetail.getPath()).thenReturn(CONTROL_PANEL_LIST_PROPERTY_PATH);
        List<InterfaceDetail> interfaceDetailList = new ArrayList<InterfaceDetail>();
        interfaceDetailList.add(interfaceDetail);
        when(mockIntrospector.getInterfacesExposedOnBusBasedOnName(SECURED_LIST_PROPERTY_INTERFACE_NAME)).thenReturn(interfaceDetailList);
        ListPropertyControlSecured securedListProperty = Mockito.mock(ListPropertyControlSecured.class);
        when(securedListProperty.getVersion()).thenThrow(new BusException());
        when(mockIntrospector.getInterface(CONTROL_PANEL_LIST_PROPERTY_PATH, ListPropertyControlSecured.class)).thenReturn(securedListProperty);
        when(mockServiceHelper.getAuthPassword(mockAboutAnnouncement)).thenReturn(new char[]
        {});
        when(mockServiceHelper.isPeerAuthenticationAttempted(mockAboutAnnouncement)).thenReturn(true);
        when(mockServiceHelper.isPeerAuthenticationSuccessful(mockAboutAnnouncement)).thenReturn(true);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_10(), "Authentication should have failed");
        verify(mockServiceHelper).clearPeerAuthenticationFlags(mockAboutAnnouncement);
        verify(mockServiceHelper).setAuthPassword(eq(mockAboutAnnouncement), (char[]) anyObject());
    }

    @Test
    public void testValidateSecuredControlPanelBusObjectsPassesIfSecuredListPropertyExistsAndPeerAuthenticationFails() throws Exception
    {
        InterfaceDetail interfaceDetail = Mockito.mock(InterfaceDetail.class);
        when(interfaceDetail.getPath()).thenReturn(CONTROL_PANEL_LIST_PROPERTY_PATH);
        List<InterfaceDetail> interfaceDetailList = new ArrayList<InterfaceDetail>();
        interfaceDetailList.add(interfaceDetail);
        when(mockIntrospector.getInterfacesExposedOnBusBasedOnName(SECURED_LIST_PROPERTY_INTERFACE_NAME)).thenReturn(interfaceDetailList);
        ListPropertyControlSecured securedListProperty = Mockito.mock(ListPropertyControlSecured.class);
        when(securedListProperty.getVersion()).thenThrow(new BusException());
        when(mockIntrospector.getInterface(CONTROL_PANEL_LIST_PROPERTY_PATH, ListPropertyControlSecured.class)).thenReturn(securedListProperty);
        when(mockServiceHelper.getAuthPassword(mockAboutAnnouncement)).thenReturn(new char[]
        {});
        when(mockServiceHelper.isPeerAuthenticationAttempted(mockAboutAnnouncement)).thenReturn(true);
        when(mockServiceHelper.isPeerAuthenticationSuccessful(mockAboutAnnouncement)).thenReturn(false);

        executeTestMethod(getTestWrapperFor_v1_10());
        verify(mockServiceHelper).clearPeerAuthenticationFlags(mockAboutAnnouncement);
        verify(mockServiceHelper).setAuthPassword(eq(mockAboutAnnouncement), (char[]) anyObject());

    }

    @Test
    public void isValidUrlTest()
    {
        assertFalse(controlPanelTestSuite.isValidUrl("date"));
        assertFalse(controlPanelTestSuite.isValidUrl("http://"));
        assertFalse(controlPanelTestSuite.isValidUrl("tmp://allseenalliance.org"));
        assertFalse(controlPanelTestSuite.isValidUrl("http://allseenalliance.org:8989890987789"));
        assertTrue(controlPanelTestSuite.isValidUrl("http://allseenalliance.org/index.html"));
    }

    @Test
    public void assertValidAncestorIsPresentForContainerReturnsTrueIfControlPanelIsPresent() throws Exception
    {
        when(mockIntrospector.isAncestorInterfacePresent("/com", org.alljoyn.ioe.controlpanelservice.communication.interfaces.ControlPanel.IFNAME)).thenReturn(true);
        controlPanelTestSuite.setUp();
        controlPanelTestSuite.assertValidAncestorIsPresentForContainer("/com");
    }

    @Test
    public void assertValidAncestorIsPresentForContainerReturnsTrueIfNotificationActionIsPresent() throws Exception
    {
        when(mockIntrospector.isAncestorInterfacePresent("/com", NotificationAction.IFNAME)).thenReturn(true);
        controlPanelTestSuite.setUp();
        controlPanelTestSuite.assertValidAncestorIsPresentForContainer("/com");
    }

    @Test
    public void assertValidAncestorIsPresentForContainerReturnsTrueIfContainerIsPresent() throws Exception
    {
        when(mockIntrospector.isAncestorInterfacePresent("/com", Container.IFNAME)).thenReturn(true);
        controlPanelTestSuite.setUp();
        controlPanelTestSuite.assertValidAncestorIsPresentForContainer("/com");
    }

    @Test
    public void assertValidAncestorIsPresentForContainerReturnsTrueIfSecuredContainerIsPresent() throws Exception
    {
        when(mockIntrospector.isAncestorInterfacePresent("/com", ContainerSecured.IFNAME)).thenReturn(true);
        controlPanelTestSuite.setUp();
        controlPanelTestSuite.assertValidAncestorIsPresentForContainer("/com");
    }

    @Test
    public void assertValidAncestorIsPresentForContainerReturnsTrueIfListPropertyIsPresent() throws Exception
    {
        when(mockIntrospector.isAncestorInterfacePresent("/com", ListPropertyControl.IFNAME)).thenReturn(true);
        controlPanelTestSuite.setUp();
        controlPanelTestSuite.assertValidAncestorIsPresentForContainer("/com");
    }

    @Test
    public void assertValidAncestorIsPresentForContainerReturnsTrueIfSecuredListPropertyIsPresent() throws Exception
    {
        when(mockIntrospector.isAncestorInterfacePresent("/com", ListPropertyControlSecured.IFNAME)).thenReturn(true);
        controlPanelTestSuite.setUp();
        controlPanelTestSuite.assertValidAncestorIsPresentForContainer("/com");
    }

    @Test
    public void assertValidAncestorIsPresentForDialogReturnsTrueIfActionIsPresent() throws Exception
    {
        when(mockIntrospector.isAncestorInterfacePresent("/com", ActionControl.IFNAME)).thenReturn(true);
        controlPanelTestSuite.setUp();
        controlPanelTestSuite.assertValidAncestorIsPresentForDialog("/com");
    }

    @Test
    public void assertValidAncestorIsPresentForDialogReturnsTrueIfSecuredActionIsPresent() throws Exception
    {
        when(mockIntrospector.isAncestorInterfacePresent("/com", ActionControlSecured.IFNAME)).thenReturn(true);
        controlPanelTestSuite.setUp();
        controlPanelTestSuite.assertValidAncestorIsPresentForDialog("/com");
    }

    @Test
    public void assertValidAncestorIsPresentForDialogReturnsTrueIfNotificationActionIsPresent() throws Exception
    {
        when(mockIntrospector.isAncestorInterfacePresent("/com", NotificationAction.IFNAME)).thenReturn(true);
        controlPanelTestSuite.setUp();
        controlPanelTestSuite.assertValidAncestorIsPresentForDialog("/com");
    }

    @Test
    public void assertAncestorContainerIsPresentReturnsTrueIfContainerIsPresent() throws Exception
    {
        when(mockIntrospector.isAncestorInterfacePresent("/com", Container.IFNAME)).thenReturn(true);
        controlPanelTestSuite.setUp();
        controlPanelTestSuite.assertAncestorContainerIsPresent("/com");
    }

    @Test
    public void assertAncestorContainerIsPresentReturnsTrueIfSecuredContainerIsPresent() throws Exception
    {
        when(mockIntrospector.isAncestorInterfacePresent("/com", ContainerSecured.IFNAME)).thenReturn(true);
        controlPanelTestSuite.setUp();
        controlPanelTestSuite.assertAncestorContainerIsPresent("/com");
    }

    private List<InterfaceDetail> getInterfaceDetailList(String path)
    {
        InterfaceDetail interfaceDetail = Mockito.mock(InterfaceDetail.class);
        when(interfaceDetail.getPath()).thenReturn(path);
        List<InterfaceDetail> interfaceDetailList = new ArrayList<InterfaceDetail>();
        interfaceDetailList.add(interfaceDetail);

        return interfaceDetailList;
    }

    private ControlPanel getMockControlPanel(short version) throws BusException
    {
        ControlPanel controlPanel = Mockito.mock(ControlPanel.class);
        when(controlPanel.getVersion()).thenReturn(version);

        return controlPanel;
    }

    private Container getMockContainer(Short version, Integer states, Map<Short, Variant> parameters) throws BusException
    {
        Container container = Mockito.mock(Container.class);
        when(container.getVersion()).thenReturn(version);
        when(container.getStates()).thenReturn(states);
        when(container.getOptParams()).thenReturn(parameters);

        return container;
    }

    private ContainerSecured getMockSecuredContainer(Short version, Integer states, Map<Short, Variant> parameters) throws BusException
    {
        ContainerSecured securedContainer = Mockito.mock(ContainerSecured.class);
        when(securedContainer.getVersion()).thenReturn(version);
        when(securedContainer.getStates()).thenReturn(states);
        when(securedContainer.getOptParams()).thenReturn(parameters);

        return securedContainer;
    }

    private PropertyControl getMockProperty(Short version, Integer states, Map<Short, Variant> parameters, Variant propertyValue) throws BusException
    {
        PropertyControl property = Mockito.mock(PropertyControl.class);
        when(property.getVersion()).thenReturn(version);
        when(property.getStates()).thenReturn(states);
        when(property.getOptParams()).thenReturn(parameters);
        when(property.getValue()).thenReturn(propertyValue);

        return property;
    }

    private PropertyControlSecured getMockSecuredProperty(Short version, Integer states, Map<Short, Variant> parameters, Variant propertyValue) throws BusException
    {
        PropertyControlSecured securedProperty = Mockito.mock(PropertyControlSecured.class);
        when(securedProperty.getVersion()).thenReturn(version);
        when(securedProperty.getStates()).thenReturn(states);
        when(securedProperty.getOptParams()).thenReturn(parameters);
        when(securedProperty.getValue()).thenReturn(propertyValue);

        return securedProperty;
    }

    private Label getMockLabel(Short version, Integer states, Map<Short, Variant> parameters) throws BusException
    {
        Label label = Mockito.mock(Label.class);
        when(label.getVersion()).thenReturn(version);
        when(label.getStates()).thenReturn(states);
        when(label.getOptParams()).thenReturn(parameters);
        when(label.getLabel()).thenReturn("labelValue");

        return label;
    }

    private ActionControl getMockAction(Short version, Integer states, Map<Short, Variant> parameters) throws BusException
    {
        ActionControl action = Mockito.mock(ActionControl.class);
        when(action.getVersion()).thenReturn(version);
        when(action.getStates()).thenReturn(states);
        when(action.getOptParams()).thenReturn(parameters);

        return action;
    }

    private ActionControlSecured getMockSecuredAction(Short version, Integer states, Map<Short, Variant> parameters) throws BusException
    {
        ActionControlSecured securedAction = Mockito.mock(ActionControlSecured.class);
        when(securedAction.getVersion()).thenReturn(version);
        when(securedAction.getStates()).thenReturn(states);
        when(securedAction.getOptParams()).thenReturn(parameters);

        return securedAction;
    }

    private AlertDialog getMockDialog(Short version, Integer states, Map<Short, Variant> parameters, Short numberOfActions) throws BusException
    {
        AlertDialog dialog = Mockito.mock(AlertDialog.class);
        when(dialog.getVersion()).thenReturn(version);
        when(dialog.getStates()).thenReturn(states);
        when(dialog.getOptParams()).thenReturn(parameters);
        when(dialog.getMessage()).thenReturn(DIALOG_MESSAGE);
        when(dialog.getNumActions()).thenReturn(numberOfActions);

        return dialog;
    }

    private AlertDialogSecured getMockSecuredDialog(Short version, Integer states, Map<Short, Variant> parameters, Short numberOfActions) throws BusException
    {
        AlertDialogSecured securedDialog = Mockito.mock(AlertDialogSecured.class);
        when(securedDialog.getVersion()).thenReturn(version);
        when(securedDialog.getStates()).thenReturn(states);
        when(securedDialog.getOptParams()).thenReturn(parameters);
        when(securedDialog.getMessage()).thenReturn(SECURED_DIALOG_MESSAGE);
        when(securedDialog.getNumActions()).thenReturn(numberOfActions);

        return securedDialog;
    }

    private ListPropertyControl getMockListProperty(Short version, Integer states, Map<Short, Variant> parameters) throws BusException
    {
        ListPropertyControl listProperty = Mockito.mock(ListPropertyControl.class);
        when(listProperty.getVersion()).thenReturn(version);
        when(listProperty.getStates()).thenReturn(states);
        when(listProperty.getOptParams()).thenReturn(parameters);

        return listProperty;
    }

    private ListPropertyControl getMockListProperty(Short version, Integer states, Map<Short, Variant> parameters, ListPropertyWidgetRecordAJ[] listPropertyWidgetRecordAJs)
            throws BusException
    {
        ListPropertyControl listProperty = Mockito.mock(ListPropertyControl.class);
        when(listProperty.getVersion()).thenReturn(version);
        when(listProperty.getStates()).thenReturn(states);
        when(listProperty.getOptParams()).thenReturn(parameters);
        when(listProperty.getValue()).thenReturn(listPropertyWidgetRecordAJs);

        return listProperty;
    }

    private ListPropertyControlSecured getMockSecuredListProperty(Short version, Integer states, Map<Short, Variant> parameters) throws BusException
    {
        ListPropertyControlSecured securedListProperty = Mockito.mock(ListPropertyControlSecured.class);
        when(securedListProperty.getVersion()).thenReturn(version);
        when(securedListProperty.getStates()).thenReturn(states);
        when(securedListProperty.getOptParams()).thenReturn(parameters);

        return securedListProperty;
    }

    private ListPropertyControlSecured getMockSecuredListProperty(Short version, Integer states, Map<Short, Variant> parameters,
            ListPropertyWidgetRecordAJ[] listPropertyWidgetRecordAJs) throws BusException
    {
        ListPropertyControlSecured securedListProperty = Mockito.mock(ListPropertyControlSecured.class);
        when(securedListProperty.getVersion()).thenReturn(version);
        when(securedListProperty.getStates()).thenReturn(states);
        when(securedListProperty.getOptParams()).thenReturn(parameters);
        when(securedListProperty.getValue()).thenReturn(listPropertyWidgetRecordAJs);

        return securedListProperty;
    }

    private NotificationAction getMockNotificationAction(short version) throws BusException
    {
        NotificationAction notificationAction = Mockito.mock(NotificationAction.class);
        when(notificationAction.getVersion()).thenReturn(version);

        return notificationAction;
    }

    private void setupDataForValidateContainerBusObjectsAndStartTest(Container container) throws Exception
    {
        setupDataForValidateContainerBusObjectsAndStartTest(container, org.alljoyn.ioe.controlpanelservice.communication.interfaces.ControlPanel.IFNAME, true);
    }

    private void setupDataForValidateContainerBusObjectsAndStartTest(Container container, String ancestorInterfaceName, boolean ancestorPresent) throws Exception
    {
        List<InterfaceDetail> interfaceDetailList = getInterfaceDetailList(CONTROL_PANEL_CONTAINER_PATH);
        when(mockIntrospector.getInterfacesExposedOnBusBasedOnName(CONTAINER_INTERFACE_NAME)).thenReturn(interfaceDetailList);
        when(mockIntrospector.getInterface(CONTROL_PANEL_CONTAINER_PATH, Container.class)).thenReturn(container);
        when(mockIntrospector.isAncestorInterfacePresent(CONTROL_PANEL_CONTAINER_PATH, ancestorInterfaceName)).thenReturn(ancestorPresent);
    }

    private void setupDataForValidateContainerBusObjectsAndStartTest(ContainerSecured securedContainer) throws Exception
    {
        setupDataForValidateContainerBusObjectsAndStartTest(securedContainer, org.alljoyn.ioe.controlpanelservice.communication.interfaces.ControlPanel.IFNAME, true);
    }

    private void setupDataForValidateContainerBusObjectsAndStartTest(ContainerSecured securedContainer, String ancestorInterfaceName, boolean ancestorPresent) throws Exception,
            InterruptedException
    {
        List<InterfaceDetail> interfaceDetailList = getInterfaceDetailList(CONTROL_PANEL_CONTAINER_PATH);
        when(mockIntrospector.getInterfacesExposedOnBusBasedOnName(SECURED_CONTAINER_INTERFACE_NAME)).thenReturn(interfaceDetailList);
        when(mockIntrospector.getInterface(CONTROL_PANEL_CONTAINER_PATH, ContainerSecured.class)).thenReturn(securedContainer);
        when(mockIntrospector.isAncestorInterfacePresent(CONTROL_PANEL_CONTAINER_PATH, ancestorInterfaceName)).thenReturn(ancestorPresent);
    }

    private void setupDataForValidatePropertyBusObjectsAndStartTest(PropertyControl property) throws Exception
    {
        setupDataForValidatePropertyBusObjectsAndStartTest(property, Container.IFNAME, true);
    }

    private void setupDataForValidatePropertyBusObjectsAndStartTest(PropertyControl property, String ancestorInterfaceName, boolean ancestorPresent) throws Exception
    {
        List<InterfaceDetail> interfaceDetailList = getInterfaceDetailList(CONTROL_PANEL_PROPERTY_PATH);
        when(mockIntrospector.getInterfacesExposedOnBusBasedOnName(PROPERTY_INTERFACE_NAME)).thenReturn(interfaceDetailList);
        when(mockIntrospector.getInterface(CONTROL_PANEL_PROPERTY_PATH, PropertyControl.class)).thenReturn(property);
        when(mockIntrospector.isAncestorInterfacePresent(CONTROL_PANEL_PROPERTY_PATH, ancestorInterfaceName)).thenReturn(ancestorPresent);
    }

    private void setupDataForValidatePropertyBusObjectsAndStartTest(PropertyControlSecured securedProperty) throws Exception
    {
        setupDataForValidatePropertyBusObjectsAndStartTest(securedProperty, Container.IFNAME, true);
    }

    private void setupDataForValidatePropertyBusObjectsAndStartTest(PropertyControlSecured securedProperty, String ancestorInterfaceName, boolean ancestorPresent) throws Exception
    {
        List<InterfaceDetail> interfaceDetailList = getInterfaceDetailList(CONTROL_PANEL_PROPERTY_PATH);
        when(mockIntrospector.getInterfacesExposedOnBusBasedOnName(SECURED_PROPERTY_INTERFACE_NAME)).thenReturn(interfaceDetailList);
        when(mockIntrospector.getInterface(CONTROL_PANEL_PROPERTY_PATH, PropertyControlSecured.class)).thenReturn(securedProperty);
        when(mockIntrospector.isAncestorInterfacePresent(CONTROL_PANEL_PROPERTY_PATH, ancestorInterfaceName)).thenReturn(ancestorPresent);
    }

    private void setupDataForValidateLabelPropertyBusObjectsAndStartTest(Label label) throws Exception
    {
        setupDataForValidateLabelPropertyBusObjectsAndStartTest(label, Container.IFNAME, true);
    }

    private void setupDataForValidateLabelPropertyBusObjectsAndStartTest(Label label, String ancestorInterfaceName, boolean ancestorPresent) throws Exception
    {
        List<InterfaceDetail> interfaceDetailList = getInterfaceDetailList(CONTROL_PANEL_LABEL_PROPERTY_PATH);
        when(mockIntrospector.getInterfacesExposedOnBusBasedOnName(LABEL_PROPERTY_INTERFACE_NAME)).thenReturn(interfaceDetailList);
        when(mockIntrospector.getInterface(CONTROL_PANEL_LABEL_PROPERTY_PATH, Label.class)).thenReturn(label);
        when(mockIntrospector.isAncestorInterfacePresent(CONTROL_PANEL_LABEL_PROPERTY_PATH, ancestorInterfaceName)).thenReturn(ancestorPresent);
    }

    private void setupDataForValidateActionBusObjectsAndStartTest(ActionControl action) throws Exception
    {
        setupDataForValidateActionBusObjectsAndStartTest(action, Container.IFNAME, true);
    }

    private void setupDataForValidateActionBusObjectsAndStartTest(ActionControl action, String ancestorInterfaceName, boolean ancestorPresent) throws Exception
    {
        List<InterfaceDetail> interfaceDetailList = getInterfaceDetailList(CONTROL_PANEL_ACTION_PATH);
        when(mockIntrospector.getInterfacesExposedOnBusBasedOnName(ACTION_INTERFACE_NAME)).thenReturn(interfaceDetailList);
        when(mockIntrospector.getInterface(CONTROL_PANEL_ACTION_PATH, ActionControl.class)).thenReturn(action);
        when(mockIntrospector.isAncestorInterfacePresent(CONTROL_PANEL_ACTION_PATH, ancestorInterfaceName)).thenReturn(ancestorPresent);
    }

    private void setupDataForValidateActionBusObjectsAndStartTest(ActionControlSecured securedAction) throws Exception
    {
        setupDataForValidateActionBusObjectsAndStartTest(securedAction, Container.IFNAME, true);
    }

    private void setupDataForValidateActionBusObjectsAndStartTest(ActionControlSecured securedAction, String ancestorInterfaceName, boolean ancestorPresent) throws Exception
    {
        List<InterfaceDetail> interfaceDetailList = getInterfaceDetailList(CONTROL_PANEL_ACTION_PATH);
        when(mockIntrospector.getInterfacesExposedOnBusBasedOnName(SECURED_ACTION_INTERFACE_NAME)).thenReturn(interfaceDetailList);
        when(mockIntrospector.getInterface(CONTROL_PANEL_ACTION_PATH, ActionControlSecured.class)).thenReturn(securedAction);
        when(mockIntrospector.isAncestorInterfacePresent(CONTROL_PANEL_ACTION_PATH, ancestorInterfaceName)).thenReturn(ancestorPresent);
    }

    private void setupDataForValidateDialogBusObjectsAndStartTest(AlertDialog dialog) throws BusException, IOException, ParserConfigurationException, SAXException,
            InterruptedException
    {
        setupDataForValidateDialogBusObjectsAndStartTest(dialog, ActionControl.IFNAME, true);
    }

    private void setupDataForValidateDialogBusObjectsAndStartTest(AlertDialog dialog, String ancestorInterfaceName, boolean ancestorPresent) throws BusException, IOException,
            ParserConfigurationException, SAXException, InterruptedException
    {
        List<InterfaceDetail> interfaceDetailList = getInterfaceDetailList(CONTROL_PANEL_DIALOG_PATH);
        when(mockIntrospector.getInterfacesExposedOnBusBasedOnName(DIALOG_INTERFACE_NAME)).thenReturn(interfaceDetailList);
        when(mockIntrospector.getInterface(CONTROL_PANEL_DIALOG_PATH, AlertDialog.class)).thenReturn(dialog);
        when(mockIntrospector.isAncestorInterfacePresent(CONTROL_PANEL_DIALOG_PATH, ancestorInterfaceName)).thenReturn(ancestorPresent);
    }

    private void setupDataForValidateDialogBusObjectsAndStartTest(AlertDialogSecured securedDialog) throws BusException, IOException, ParserConfigurationException, SAXException,
            InterruptedException
    {
        setupDataForValidateDialogBusObjectsAndStartTest(securedDialog, ActionControl.IFNAME, true);
    }

    private void setupDataForValidateDialogBusObjectsAndStartTest(AlertDialogSecured securedDialog, String ancestorInterfaceName, boolean ancestorPresent) throws BusException,
            IOException, ParserConfigurationException, SAXException, InterruptedException
    {
        List<InterfaceDetail> interfaceDetailList = getInterfaceDetailList(CONTROL_PANEL_DIALOG_PATH);
        when(mockIntrospector.getInterfacesExposedOnBusBasedOnName(SECURED_DIALOG_INTERFACE_NAME)).thenReturn(interfaceDetailList);
        when(mockIntrospector.getInterface(CONTROL_PANEL_DIALOG_PATH, AlertDialogSecured.class)).thenReturn(securedDialog);
        when(mockIntrospector.isAncestorInterfacePresent(CONTROL_PANEL_DIALOG_PATH, ancestorInterfaceName)).thenReturn(ancestorPresent);
    }

    private void setupDataForValidateListPropertyBusObjectsAndStartTest(ListPropertyControl listProperty) throws BusException, IOException, ParserConfigurationException,
            SAXException, InterruptedException
    {
        setupDataForValidateListPropertyBusObjectsAndStartTest(listProperty, Container.IFNAME, true);
    }

    private void setupDataForValidateListPropertyBusObjectsAndStartTest(ListPropertyControl listProperty, String ancestorInterfaceName, boolean ancestorPresent)
            throws BusException, IOException, ParserConfigurationException, SAXException, InterruptedException
    {
        List<InterfaceDetail> interfaceDetailList = getInterfaceDetailList(CONTROL_PANEL_LIST_PROPERTY_PATH);
        when(mockIntrospector.getInterfacesExposedOnBusBasedOnName(LIST_PROPERTY_INTERFACE_NAME)).thenReturn(interfaceDetailList);
        when(mockIntrospector.getInterface(CONTROL_PANEL_LIST_PROPERTY_PATH, ListPropertyControl.class)).thenReturn(listProperty);
        when(mockIntrospector.isAncestorInterfacePresent(CONTROL_PANEL_LIST_PROPERTY_PATH, ancestorInterfaceName)).thenReturn(ancestorPresent);
    }

    private void setupDataForValidateListPropertyBusObjectsAndStartTest(ListPropertyControlSecured securedListProperty) throws BusException, IOException,
            ParserConfigurationException, SAXException, InterruptedException
    {
        setupDataForValidateListPropertyBusObjectsAndStartTest(securedListProperty, Container.IFNAME, true);
    }

    private void setupDataForValidateListPropertyBusObjectsAndStartTest(ListPropertyControlSecured securedListProperty, String ancestorInterfaceName, boolean ancestorPresent)
            throws BusException, IOException, ParserConfigurationException, SAXException, InterruptedException
    {
        List<InterfaceDetail> interfaceDetailList = getInterfaceDetailList(CONTROL_PANEL_LIST_PROPERTY_PATH);
        when(mockIntrospector.getInterfacesExposedOnBusBasedOnName(SECURED_LIST_PROPERTY_INTERFACE_NAME)).thenReturn(interfaceDetailList);
        when(mockIntrospector.getInterface(CONTROL_PANEL_LIST_PROPERTY_PATH, ListPropertyControlSecured.class)).thenReturn(securedListProperty);
        when(mockIntrospector.isAncestorInterfacePresent(CONTROL_PANEL_LIST_PROPERTY_PATH, ancestorInterfaceName)).thenReturn(ancestorPresent);
    }

    private HTTPControl getMockHttpControl(short version, String rootUrl) throws Exception
    {
        HTTPControl httpControl = Mockito.mock(HTTPControl.class);
        when(httpControl.getVersion()).thenReturn(version);
        when(httpControl.GetRootURL()).thenReturn(rootUrl);

        return httpControl;
    }

    private HttpResponse getMockHttpResponse()
    {
        HttpResponse httpResponse = Mockito.mock(HttpResponse.class);
        StatusLine status = Mockito.mock(StatusLine.class);
        when(httpResponse.getStatusLine()).thenReturn(status);

        return httpResponse;
    }

    private ListPropertyWidgetRecordAJ[] getListPropertyWidgetRecordAJs()
    {
        ListPropertyWidgetRecordAJ listPropertyWidgetRecordAJ = new ListPropertyWidgetRecordAJ();
        listPropertyWidgetRecordAJ.recordId = 1;
        listPropertyWidgetRecordAJ.label = "label1";
        ListPropertyWidgetRecordAJ[] listPropertyWidgetRecordAJs = new ListPropertyWidgetRecordAJ[1];
        listPropertyWidgetRecordAJs[0] = listPropertyWidgetRecordAJ;

        return listPropertyWidgetRecordAJs;
    }

    private void setupMockServiceHelper() throws Exception
    {
        when(mockServiceHelper.waitForNextDeviceAnnouncement(anyLong(), any(TimeUnit.class))).thenReturn(mockAboutAnnouncement);
        when(mockServiceHelper.connectAboutClient(mockAboutAnnouncement)).thenReturn(mockAboutClient);
        when(mockServiceHelper.getBusIntrospector(mockAboutClient)).thenReturn(mockIntrospector);
    }

    private void constructControlPanelTestSuite()
    {
        controlPanelTestSuite = new ControlPanelTestSuite()
        {
            @Override
            public HttpClient getHttpClient()
            {
                return mockHttpClient;
            }

            @Override
            public ServiceHelper getServiceHelper()
            {
                return mockServiceHelper;
            }
        };

        controlPanelTestSuite.setValidationTestContext(mockTestContext);
    }

    protected TestWrapper getTestWrapperFor_v1_01()
    {
        return new TestWrapper()
        {
            @Override
            public void executeTestMethod() throws Exception
            {
                controlPanelTestSuite.testControlPanel_v1_01_ValidateControlPanelBusObjects();
            }
        };
    }

    protected TestWrapper getTestWrapperFor_v1_02()
    {
        return new TestWrapper()
        {
            @Override
            public void executeTestMethod() throws Exception
            {
                controlPanelTestSuite.testControlPanel_v1_02_ValidateContainerBusObjects();
            }
        };
    }

    protected TestWrapper getTestWrapperFor_v1_03()
    {
        return new TestWrapper()
        {
            @Override
            public void executeTestMethod() throws Exception
            {
                controlPanelTestSuite.testControlPanel_v1_03_ValidatePropertyBusObjects();
            }
        };
    }

    protected TestWrapper getTestWrapperFor_v1_04()
    {
        return new TestWrapper()
        {
            @Override
            public void executeTestMethod() throws Exception
            {
                controlPanelTestSuite.testControlPanel_v1_04_ValidateLabelPropertyBusObjects();
            }
        };
    }

    protected TestWrapper getTestWrapperFor_v1_05()
    {
        return new TestWrapper()
        {
            @Override
            public void executeTestMethod() throws Exception
            {
                controlPanelTestSuite.testControlPanel_v1_05_ValidateActionBusObjects();
            }
        };
    }

    protected TestWrapper getTestWrapperFor_v1_06()
    {
        return new TestWrapper()
        {
            @Override
            public void executeTestMethod() throws Exception
            {
                controlPanelTestSuite.testControlPanel_v1_06_ValidateDialogBusObjects();
            }
        };
    }

    protected TestWrapper getTestWrapperFor_v1_07()
    {
        return new TestWrapper()
        {
            @Override
            public void executeTestMethod() throws Exception
            {
                controlPanelTestSuite.testControlPanel_v1_07_ValidateListPropertyBusObjects();
            }
        };
    }

    protected TestWrapper getTestWrapperFor_v1_08()
    {
        return new TestWrapper()
        {
            @Override
            public void executeTestMethod() throws Exception
            {
                controlPanelTestSuite.testControlPanel_v1_08_ValidateNotificationActionBusObjects();
            }
        };
    }

    protected TestWrapper getTestWrapperFor_v1_09()
    {
        return new TestWrapper()
        {
            @Override
            public void executeTestMethod() throws Exception
            {
                controlPanelTestSuite.testControlPanel_v1_09_ValidateHttpControlBusObjects();
            }
        };
    }

    protected TestWrapper getTestWrapperFor_v1_10()
    {
        return new TestWrapper()
        {
            @Override
            public void executeTestMethod() throws Exception
            {
                controlPanelTestSuite.testControlPanel_v1_10_ValidateSecuredControlPanelBusObjects();
            }
        };
    }

    @Override
    protected void executeTestMethod(TestWrapper testWrapper) throws Exception
    {
        controlPanelTestSuite.setUp();

        try
        {
            testWrapper.executeTestMethod();
        }
        finally
        {
            controlPanelTestSuite.tearDown();
        }
    }
}