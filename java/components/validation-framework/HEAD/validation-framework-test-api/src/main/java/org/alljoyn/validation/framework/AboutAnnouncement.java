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
package org.alljoyn.validation.framework;

import java.util.Map;

import org.alljoyn.bus.Variant;
import org.alljoyn.services.common.BusObjectDescription;

/**
 * This class contains details of the announcement broadcast by About feature
 * about the device/app and list of supported interfaces.
 * 
 */
public class AboutAnnouncement
{
    private String serviceName;
    private short port;
    private BusObjectDescription[] objectDescriptions;
    private Map<String, Variant> aboutData;

    /**
     * @param serviceName
     *            the bus unique name of the peer exposing the service
     * @param port
     *            the port on which the app will listen
     * @param objectDescriptions
     *            object paths and the list of all interfaces available at the
     *            given object path
     * @param aboutData
     *            app/device details
     */
    public AboutAnnouncement(String serviceName, short port, BusObjectDescription[] objectDescriptions, Map<String, Variant> aboutData)
    {
        this.serviceName = serviceName;
        this.port = port;
        this.objectDescriptions = objectDescriptions;
        this.aboutData = aboutData;
    }

    /**
     * @return the bus unique name of the peer exposing the service
     */
    public String getServiceName()
    {
        return serviceName;
    }

    /**
     * @return port on which the app will listen
     */
    public short getPort()
    {
        return port;
    }

    /**
     * @return array of {@code BusObjectDescription} which contains object paths
     *         and the list of all interfaces available at the given object path
     */
    public BusObjectDescription[] getObjectDescriptions()
    {
        return objectDescriptions;
    }

    /**
     * @return app/device details
     */
    public Map<String, Variant> getAboutData()
    {
        return aboutData;
    }
}