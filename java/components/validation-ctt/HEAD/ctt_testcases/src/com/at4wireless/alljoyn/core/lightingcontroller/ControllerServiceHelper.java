/*******************************************************************************
 *   *  Copyright (c) Open Connectivity Foundation (OCF) and AllJoyn Open
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
 *      THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL
 *      WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED
 *      WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE
 *      AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL
 *      DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR
 *      PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER
 *      TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 *      PERFORMANCE OF THIS SOFTWARE.
 *******************************************************************************/
package com.at4wireless.alljoyn.core.lightingcontroller;

import org.alljoyn.bus.BusAttachment;
import org.alljoyn.bus.BusException;
import org.alljoyn.bus.Mutable;
import org.alljoyn.bus.ProxyBusObject;
import org.alljoyn.bus.SessionListener;
import org.alljoyn.bus.SessionOpts;
import org.alljoyn.bus.Status;

import com.at4wireless.alljoyn.core.about.AboutAnnouncementDetails;
import com.at4wireless.alljoyn.core.commons.ServiceHelper;
import com.at4wireless.alljoyn.core.commons.log.Logger;
import com.at4wireless.alljoyn.core.commons.log.WindowsLoggerImpl;
import com.at4wireless.alljoyn.core.introspection.BusIntrospector;
import com.at4wireless.alljoyn.core.introspection.XmlBasedBusIntrospector;

/**
 * Class to connect to the ControllerService and establish a multi-point
 * session.
 */
public class ControllerServiceHelper extends ServiceHelper
{
	private static final Logger logger = new WindowsLoggerImpl(ControllerServiceHelper.class.getSimpleName());
	
    private int sessionId;
    private String serviceName;
    private short contactPort;

    public ControllerServiceHelper()
    {
        sessionId = -1;
        serviceName = null;
    }

    public void connect(AboutAnnouncementDetails aboutAnnouncementDetails) throws Exception
    {
        serviceName = aboutAnnouncementDetails.getServiceName();
        contactPort = aboutAnnouncementDetails.getPort();
        String deviceName = aboutAnnouncementDetails.getDeviceName();

        logger.info("Joining session for serviceName: " + serviceName + " port: " + contactPort + " deviceName: " + deviceName);

        Status status = doJoinSession(aboutAnnouncementDetails.getServiceName(), aboutAnnouncementDetails.getPort());
        if (status != Status.OK)
        {
            throw new BusException(String.format("Failed to connect AboutClient to client: %s", status));
        }

        logger.info("Session established");
        logger.info("Partial Verdict: PASS");
    }

    public short getPort()
    {
        return contactPort;
    }

    public String getServiceName()
    {
        return serviceName;
    }

    public int getSessionId()
    {
        return sessionId;
    }

    public BusIntrospector getBusIntrospector()
    {
        return new XmlBasedBusIntrospector(getBusAttachment(), getServiceName(), getSessionId());
    }

    public ProxyBusObject getProxyBusObject(String path, Class<?>[] classes)
    {
        return getBusAttachment().getProxyBusObject(serviceName, path, sessionId, classes);
    }
    
    private Status doJoinSession(final String name, short contactPort)
    {
        SessionOpts sessionOpts = new SessionOpts();
        sessionOpts.isMultipoint = true;
        Mutable.IntegerValue mutableSessionId = new Mutable.IntegerValue();
        BusAttachment bus = getBusAttachment();

        Status status = bus.joinSession(name, contactPort, mutableSessionId, sessionOpts, new SessionListener()
        {
            @Override
            public void sessionMemberAdded(int sid, String busId)
            {

            }
            // [ASACOMP-66] sessionLost method changes its input attributes
            // [ASACOMP-66] Changes start.
            @Override
            //public void sessionLost(int sid) 				
            public void sessionLost(int sid, int reason)
            // [ASACOMP-66] Changes end.
            {

            }
        });

        if (status == Status.OK)
        {
            sessionId = mutableSessionId.value;
        }
        else
        {
            sessionId = -1;
        }

        return status;
    }

}