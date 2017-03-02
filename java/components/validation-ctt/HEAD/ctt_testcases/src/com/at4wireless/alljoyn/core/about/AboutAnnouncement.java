/*******************************************************************************
 *     Copyright (c) Open Connectivity Foundation (OCF), AllJoyn Open Source
 *     Project (AJOSP) Contributors and others.
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
package com.at4wireless.alljoyn.core.about;

import java.util.Map;

import org.alljoyn.bus.AboutObjectDescription;
import org.alljoyn.bus.Variant;

/**
 * This class contains details of the announcement broadcast by About feature
 * about the device/app and list of supported interfaces.
 * 
 */
public class AboutAnnouncement
{
    private String serviceName;
    private short port;
    //private BusObjectDescription[] objectDescriptions; //[AT4] deprecated
    private AboutObjectDescription[] objectDescriptions;
    private Map<String, Variant> aboutData;
    
    private int version; //[AT4]
    
    /**
     * @param version
     * 			  the version of the about interface
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
    //public AboutAnnouncement(String serviceName, short port, BusObjectDescription[] objectDescriptions, Map<String, Variant> aboutData)
    public AboutAnnouncement(String serviceName, int version, short port, AboutObjectDescription[] objectDescriptions, Map<String, Variant> aboutData)
    {
    	this.version=version; //[AT4]
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
    //public BusObjectDescription[] getObjectDescriptions()
    public AboutObjectDescription[] getObjectDescriptions()
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
    
    /**
     * [AT4]
     * @return app/device about interface version
     */
    public int getVersion()
    {
        return version;
    }



}