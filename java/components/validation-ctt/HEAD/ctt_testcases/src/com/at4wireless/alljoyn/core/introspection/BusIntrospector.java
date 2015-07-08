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
package com.at4wireless.alljoyn.core.introspection;

import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.alljoyn.bus.BusException;
import org.xml.sax.SAXException;

import com.at4wireless.alljoyn.core.introspection.bean.InterfaceDetail;
import com.at4wireless.alljoyn.core.introspection.bean.NodeDetail;

public interface BusIntrospector
{
    <T> T getInterface(String path, Class<T> interfaceClass);

    NodeDetail introspect(String path) throws BusException, IOException, ParserConfigurationException, SAXException;

    List<NodeDetail> introspectEntireTree(String path) throws BusException, IOException, ParserConfigurationException, SAXException;

    List<InterfaceDetail> getStandardizedInterfacesExposedOnBus() throws BusException, IOException, ParserConfigurationException, SAXException;

    List<InterfaceDetail> getInterfacesExposedOnBusBasedOnName(String interfaceName) throws BusException, IOException, ParserConfigurationException, SAXException;

    List<InterfaceDetail> getInterfacesExposedOnBusUnderSpecifiedPathBasedOnName(String path, String interfaceName) throws BusException, IOException, ParserConfigurationException,
            SAXException;

    boolean isInterfacePresent(String path, String interfaceName) throws BusException, IOException, ParserConfigurationException, SAXException;

    boolean isAncestorInterfacePresent(String path, String ancestorInterfaceName) throws BusException, IOException, ParserConfigurationException, SAXException;
}