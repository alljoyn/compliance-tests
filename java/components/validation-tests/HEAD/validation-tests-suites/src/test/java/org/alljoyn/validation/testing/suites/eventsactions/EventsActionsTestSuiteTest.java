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
