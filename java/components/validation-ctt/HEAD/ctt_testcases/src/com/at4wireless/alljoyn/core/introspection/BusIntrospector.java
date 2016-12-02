/*
 *  * 
 *    Copyright (c) 2016 Open Connectivity Foundation and AllJoyn Open
 *    Source Project Contributors and others.
 *    
 *    All rights reserved. This program and the accompanying materials are
 *    made available under the terms of the Apache License, Version 2.0
 *    which accompanies this distribution, and is available at
 *    http://www.apache.org/licenses/LICENSE-2.0

 */
package com.at4wireless.alljoyn.core.introspection;

import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.alljoyn.bus.BusException;
import org.xml.sax.SAXException;

import com.at4wireless.alljoyn.core.introspection.bean.InterfaceDetail;
import com.at4wireless.alljoyn.core.introspection.bean.NodeDetail;


// TODO: Auto-generated Javadoc
/**
 * The Interface BusIntrospector.
 */
public interface BusIntrospector
{
    
    /**
     * Gets the interface.
     *
     * @param <T> the generic type
     * @param path the path
     * @param interfaceClass the interface class
     * @return the interface
     */
    <T> T getInterface(String path, Class<T> interfaceClass);

    /**
     * Introspect.
     *
     * @param path the path
     * @return the node detail
     * @throws BusException the bus exception
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws ParserConfigurationException the parser configuration exception
     * @throws SAXException the SAX exception
     */
    NodeDetail introspect(String path) throws BusException, IOException, ParserConfigurationException, SAXException;

    /**
     * Introspect entire tree.
     *
     * @param path the path
     * @return the list
     * @throws BusException the bus exception
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws ParserConfigurationException the parser configuration exception
     * @throws SAXException the SAX exception
     */
    List<NodeDetail> introspectEntireTree(String path) throws BusException, IOException, ParserConfigurationException, SAXException;

    /**
     * Gets the standardized interfaces exposed on bus.
     *
     * @return the standardized interfaces exposed on bus
     * @throws BusException the bus exception
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws ParserConfigurationException the parser configuration exception
     * @throws SAXException the SAX exception
     */
    List<InterfaceDetail> getStandardizedInterfacesExposedOnBus() throws BusException, IOException, ParserConfigurationException, SAXException;

    /**
     * Gets the interfaces exposed on bus based on name.
     *
     * @param interfaceName the interface name
     * @return the interfaces exposed on bus based on name
     * @throws BusException the bus exception
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws ParserConfigurationException the parser configuration exception
     * @throws SAXException the SAX exception
     */
    List<InterfaceDetail> getInterfacesExposedOnBusBasedOnName(String interfaceName) throws BusException, IOException, ParserConfigurationException, SAXException;

    /**
     * Gets the interfaces exposed on bus under specified path based on name.
     *
     * @param path the path
     * @param interfaceName the interface name
     * @return the interfaces exposed on bus under specified path based on name
     * @throws BusException the bus exception
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws ParserConfigurationException the parser configuration exception
     * @throws SAXException the SAX exception
     */
    List<InterfaceDetail> getInterfacesExposedOnBusUnderSpecifiedPathBasedOnName(String path, String interfaceName) throws BusException, IOException, ParserConfigurationException,
            SAXException;

    /**
     * Checks if is interface present.
     *
     * @param path the path
     * @param interfaceName the interface name
     * @return true, if is interface present
     * @throws BusException the bus exception
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws ParserConfigurationException the parser configuration exception
     * @throws SAXException the SAX exception
     */
    boolean isInterfacePresent(String path, String interfaceName) throws BusException, IOException, ParserConfigurationException, SAXException;

    /**
     * Checks if is ancestor interface present.
     *
     * @param path the path
     * @param ancestorInterfaceName the ancestor interface name
     * @return true, if is ancestor interface present
     * @throws BusException the bus exception
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws ParserConfigurationException the parser configuration exception
     * @throws SAXException the SAX exception
     */
    boolean isAncestorInterfacePresent(String path, String ancestorInterfaceName) throws BusException, IOException, ParserConfigurationException, SAXException;
}