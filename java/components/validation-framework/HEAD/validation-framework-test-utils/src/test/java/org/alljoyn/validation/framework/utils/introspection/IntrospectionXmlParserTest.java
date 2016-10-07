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
package org.alljoyn.validation.framework.utils.introspection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

import java.net.URL;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.alljoyn.validation.framework.utils.introspection.IntrospectionXmlParser;
import org.alljoyn.validation.framework.utils.introspection.bean.IntrospectionInterface;
import org.alljoyn.validation.framework.utils.introspection.bean.IntrospectionNode;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.xml.sax.XMLReader;

@RunWith(MockitoJUnitRunner.class)
public class IntrospectionXmlParserTest
{
    @Mock
    private SAXParserFactory saxParserFactory;
    @Mock
    private SAXParser saxParser;
    @Mock
    private XMLReader xmlReader;

    private URL url;

    @Before
    public void setUp() throws Exception
    {
        url = ClassLoader.getSystemResource("test.xml");
        when(saxParserFactory.newSAXParser()).thenReturn(saxParser);
        when(saxParser.getXMLReader()).thenReturn(xmlReader);
    }

    @Test
    public void parserIntrospectionNodeXml() throws Exception
    {
        IntrospectionXmlParser testMetadataXmlParser = new IntrospectionXmlParser();

        URL url = ClassLoader.getSystemResource("testIntrospectionNode.xml");
        IntrospectionNode introspectionNode = testMetadataXmlParser.parseXML(url.openStream());

        assertNotNull(introspectionNode);

        assertEquals("/org/freedesktop/sample_object", introspectionNode.getName());
        assertEquals(2, introspectionNode.getSubNodes().size());
        assertEquals(1, introspectionNode.getInterfaces().size());

        IntrospectionInterface iface = introspectionNode.getInterfaces().get(0);
        assertEquals("org.freedesktop.SampleInterface", iface.getName());
        assertEquals(1, iface.getSignals().size());
        assertEquals(3, iface.getMethods().size());
        assertEquals(1, iface.getProperties().size());
    }

    @Test
    public void parserTestXml() throws Exception
    {
        IntrospectionXmlParser testMetadataXmlParser = new IntrospectionXmlParser();
        IntrospectionNode introspectionNode = testMetadataXmlParser.parseXML(url.openStream());

        assertNotNull(introspectionNode);

        assertNull(introspectionNode.getName());
        assertEquals(2, introspectionNode.getSubNodes().size());
        assertEquals(4, introspectionNode.getInterfaces().size());

        IntrospectionInterface iface = introspectionNode.getInterfaces().get(0);
        assertEquals("org.alljoyn.Control.TV.LG", iface.getName());
        assertEquals(1, iface.getSignals().size());
        assertEquals(5, iface.getMethods().size());
        assertEquals(5, iface.getProperties().size());
    }
}