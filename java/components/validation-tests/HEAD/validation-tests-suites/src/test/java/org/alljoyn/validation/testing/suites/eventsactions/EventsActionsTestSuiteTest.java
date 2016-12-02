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
package org.alljoyn.validation.testing.suites.eventsactions;

import static org.mockito.Mockito.when;

import org.alljoyn.bus.BusException;
import org.alljoyn.bus.ProxyBusObject;
import org.alljoyn.bus.ifaces.AllSeenIntrospectable;
import org.alljoyn.validation.testing.suites.MyRobolectricTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.annotation.Config;

@RunWith(MyRobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class EventsActionsTestSuiteTest
{

    @Mock
    private ProxyBusObject mockProxyObj;
    @Mock
    private AllSeenIntrospectable mockInstropectable;

    private String xmlStringEn = "<node name=\"/testNode\"><description>description [en]</description><interface><description>Events [en]</description>"
            + "<property name=\"Version\" type=\"q\" access=\"read\"/><signal name=\"sampleSignal\" sessionless=\"true\"><description>Signal description [en]</description>"
            + "</signal></interface><interface><description>Actions [en]</description><property name=\"Version\" type=\"q\" access=\"read\"/><method name=\"sampleAction\">"
            + "<description>Sample Action description</description></method></interface></node>";
    private String xmlStringEs = "<node name=\"/testNode\"><description>description [es]</description><interface><description>Events [es]</description>"
            + "<property name=\"Version\" type=\"q\" access=\"read\"/><signal name=\"sampleSignal\" sessionless=\"true\"><description>Signal description [es]</description>"
            + "</signal></interface><interface><description>Actions [es]</description><property name=\"Version\" type=\"q\" access=\"read\"/><method name=\"sampleAction\">"
            + "<description>Sample Action description</description></method></interface></node>";
    private String xmlStringEnWithAttribute = "<node name=\"/testNode\"><description language=\"en\">description [en]</description><interface><description language=\"en\">Events [en]</description>"
            + "<property name=\"Version\" type=\"q\" access=\"read\"/><signal name=\"sampleSignal\" sessionless=\"true\"><description language=\"en\">Signal description [en]</description>"
            + "</signal></interface><interface><description language=\"en\">Actions [en]</description><property name=\"Version\" type=\"q\" access=\"read\"/><method name=\"sampleAction\">"
            + "<description language=\"en\">Sample Action description</description></method></interface></node>";
    private String xmlStringEsWithAttribute = "<node name=\"/testNode\"><description language=\"es\">description [es]</description><interface><description language=\"es\">Evests [es]</description>"
            + "<property name=\"Version\" type=\"q\" access=\"read\"/><signal name=\"sampleSignal\" sessionless=\"true\"><description language=\"es\">Signal description [es]</description>"
            + "</signal></interface><interface><description language=\"es\">Actions [es]</description><property name=\"Version\" type=\"q\" access=\"read\"/><method name=\"sampleAction\">"
            + "<description language=\"es\">Sample Action description</description></method></interface></node>";

    @Before
    public void setup() throws Exception
    {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testObjectValidityPerLanguages() throws BusException
    {
        EventsActionsTestSuite testSuite = new EventsActionsTestSuite()
        {
            boolean testChildrenObjectValidity(String parentObjectPath, String parentIntroXML)
            {
                return true;
            };
        };

        String descLangs[] =
        { "en", "es" };

        when(mockProxyObj.getInterface(AllSeenIntrospectable.class)).thenReturn(mockInstropectable);
        when(mockInstropectable.IntrospectWithDescription("en")).thenReturn(xmlStringEn);
        when(mockInstropectable.IntrospectWithDescription("es")).thenReturn(xmlStringEs);
        testSuite.testObjectValidityPerLanguages(mockProxyObj, "", descLangs);

        when(mockInstropectable.IntrospectWithDescription("en")).thenReturn(xmlStringEnWithAttribute);
        when(mockInstropectable.IntrospectWithDescription("es")).thenReturn(xmlStringEsWithAttribute);
        testSuite.testObjectValidityPerLanguages(mockProxyObj, "", descLangs);

    }
}