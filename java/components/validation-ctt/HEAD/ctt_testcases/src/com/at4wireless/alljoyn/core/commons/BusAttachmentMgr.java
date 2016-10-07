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
 *      THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL
 *      WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED
 *      WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE
 *      AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL
 *      DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR
 *      PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER
 *      TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 *      PERFORMANCE OF THIS SOFTWARE.
 *******************************************************************************/
package com.at4wireless.alljoyn.core.commons;

import org.alljoyn.bus.BusAttachment;
import org.alljoyn.bus.BusException;
import org.alljoyn.bus.BusObject;
import org.alljoyn.bus.SessionOpts;
import org.alljoyn.bus.Status;

import com.at4wireless.alljoyn.core.commons.log.Logger;
import com.at4wireless.alljoyn.core.commons.log.WindowsLoggerImpl;

public class BusAttachmentMgr
{
    private BusAttachment busAttachment;
    private static final Logger logger = new WindowsLoggerImpl(BusAttachmentMgr.class.getSimpleName());
    private String daemonName;
    private String advertisedName;
    private static boolean initialized = false;

    public BusAttachmentMgr() 
    {
        if (!initialized)
        {
            initialized = true; 
        }
    }

    public void create(String busApplicationName, BusAttachment.RemoteMessage policy) throws BusException
    {
    	logger.info("Creating BusAttachment");
        busAttachment = createBusAttachment(busApplicationName, policy);
    }

    public void connect() throws BusException
    {
    	logger.info("Connecting BusAttachment");
        Status status = busAttachment.connect();

        if (status != Status.OK)
        {
            String message = "Unable to connect busAttachment: " + status;
            logger.error(message);
            throw new BusException(message);
        }
    }

    public void advertise() throws BusException
    {
        daemonName = "org.alljoyn.BusNode_" + busAttachment.getGlobalGUIDString();
        Status status = busAttachment.requestName(daemonName, BusAttachment.ALLJOYN_REQUESTNAME_FLAG_DO_NOT_QUEUE);

        if (status != Status.OK)
        {
            String message = "Failed to requestName '" + daemonName + "': " + status;
            daemonName = null;
            logger.error(message);
            throw new BusException(message);
        }

        advertisedName = "quiet@" + daemonName;
        //status = busAttachment.advertiseName(advertisedName, SessionOpts.TRANSPORT_WLAN); //[AT4] Deprecated
        status = busAttachment.advertiseName(advertisedName, SessionOpts.TRANSPORT_IP);

        if (status != Status.OK)
        {
            advertisedName = null;
            String message = "Failed to advertiseName: " + daemonName;
            logger.error(message);
            throw new BusException(message);
        }
    }

    public void release()
    {
        if (advertisedName != null)
        {
        	//Status status = busAttachment.advertiseName(advertisedName, SessionOpts.TRANSPORT_WLAN); //[AT4] Deprecated
            Status status = busAttachment.cancelAdvertiseName(advertisedName, SessionOpts.TRANSPORT_IP);
            if (status != Status.OK)
            {
                logger.warn(String.format("cancelAdvertiseName returned: %s", status));
            }
            advertisedName = null;
        }

        if (daemonName != null)
        {
            Status status = busAttachment.releaseName(daemonName);
            if (status != Status.OK)
            {
                logger.warn(String.format("releaseName returned: %s", status));
            }
            daemonName = null;
        }

        if (busAttachment != null)
        {
         
        	logger.info("Disconnecting BusAttachment");
            busAttachment.clearKeyStore();
            busAttachment.disconnect();
            busAttachment.release();
            busAttachment = null;
        }
    }

    protected BusAttachment createBusAttachment(String applicationName, BusAttachment.RemoteMessage policy)
    {
        return new BusAttachment(applicationName, policy);
    }

    public BusAttachment getBusAttachment()
    {
        return busAttachment;
    }

    public void registerSignalHandler(Object signalHandler) throws BusException
    {
        Status status = busAttachment.registerSignalHandlers(signalHandler);

        if (status != Status.OK)
        {
            String message = String.format("Signal handler %s registration failed with status %s", signalHandler.getClass().getName(), status);
            logger.error(message);
            throw new BusException(message);
        }
    }

    public void registerBusObject(BusObject busObject, String objectPath) throws BusException
    {
        Status status = busAttachment.registerBusObject(busObject, objectPath);

        if (status != Status.OK)
        {
            String message = String.format("Failed to register object: %s", status);
            logger.error(message);
            throw new BusException(message);
        }
    }

    public String getBusUniqueName()
    {
        return busAttachment.getUniqueName();
    }
}