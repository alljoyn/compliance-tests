/*
 * Copyright (c) 2016 Open Connectivity Foundation (OCF) and AllJoyn Open
 *    Source Project (AJOSP) Contributors and others.
 *
 *    SPDX-License-Identifier: Apache-2.0
 *
 *    All rights reserved. This program and the accompanying materials are
 *    made available under the terms of the Apache License, Version 2.0
 *    which accompanies this distribution, and is available at
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Copyright 2016 Open Connectivity Foundation and Contributors to
 *    AllSeen Alliance. All rights reserved.
 *
 *    Permission to use, copy, modify, and/or distribute this software for
 *    any purpose with or without fee is hereby granted, provided that the
 *    above copyright notice and this permission notice appear in all
 *    copies.
 *
 *     THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL
 *     WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED
 *     WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE
 *     AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL
 *     DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR
 *     PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER
 *     TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 *     PERFORMANCE OF THIS SOFTWARE.
 */
package com.at4wireless.alljoyn.core.commons;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import org.alljoyn.bus.BusException;
import org.alljoyn.bus.Variant;
import org.alljoyn.services.common.AnnouncementHandler;
import org.alljoyn.services.common.BusObjectDescription;

import com.at4wireless.alljoyn.core.about.AboutAnnouncementDetails;
import com.at4wireless.alljoyn.core.commons.log.WindowsLoggerImpl;


// TODO: Auto-generated Javadoc
/**
 * The Class DeviceAnnouncementHandler.
 */
public class DeviceAnnouncementHandler implements AnnouncementHandler
{
    
    /** The Constant TAG. */
    private static final String TAG = "DeviceAnnounceHandler";
	
	/** The Constant logger. */
	private static final WindowsLoggerImpl logger =  new WindowsLoggerImpl(TAG);
    
    /** The dut device id. */
    private String dutDeviceId;
    
    /** The dut app id. */
    private UUID dutAppId;
    
    /** The received announcements. */
    private LinkedBlockingDeque<AboutAnnouncementDetails> receivedAnnouncements = new LinkedBlockingDeque<AboutAnnouncementDetails>();
    
    /** The lost devices. */
    private LinkedBlockingDeque<String> lostDevices = new LinkedBlockingDeque<String>();

    /**
     * Instantiates a new device announcement handler.
     *
     * @param dutDeviceId the dut device id
     * @param dutAppId the dut app id
     */
    public DeviceAnnouncementHandler(String dutDeviceId, UUID dutAppId)
    {
        this.dutAppId = dutAppId;
        this.dutDeviceId = dutDeviceId;
    }

    /**
     * Wait for next device announcement.
     *
     * @param timeout the timeout
     * @param unit the unit
     * @return the about announcement details
     * @throws InterruptedException the interrupted exception
     */
    public AboutAnnouncementDetails waitForNextDeviceAnnouncement(long timeout, TimeUnit unit) throws InterruptedException
    {
        logger.info(String.format("Waiting for About announcement signal from device: %s; appId: %s", dutDeviceId, dutAppId));
        return receivedAnnouncements.poll(timeout, unit);
    }

    /**
     * Wait for session to close.
     *
     * @param timeout the timeout
     * @param unit the unit
     * @throws InterruptedException the interrupted exception
     */
    public void waitForSessionToClose(long timeout, TimeUnit unit) throws InterruptedException
    {
        logger.info(String.format("Waiting for session to close for device: %s", dutDeviceId));
        lostDevices.poll(timeout, unit);
    }

    /* (non-Javadoc)
     * @see org.alljoyn.services.common.AnnouncementHandler#onDeviceLost(java.lang.String)
     */
    @Override
    public void onDeviceLost(String deviceName)
    {
        logger.debug("onDeviceLost: " + deviceName);
        lostDevices.add(deviceName);
    }

    /* (non-Javadoc)
     * @see org.alljoyn.services.common.AnnouncementHandler#onAnnouncement(java.lang.String, short, org.alljoyn.services.common.BusObjectDescription[], java.util.Map)
     */
    @Override
    public void onAnnouncement(String serviceName, short port, BusObjectDescription[] objectDescriptions, Map<String, Variant> aboutData)
    {
        logger.debug(String.format("Received About announcement signal from serviceName: %s", serviceName));
        AboutAnnouncementDetails receivedAboutAnnouncement = new AboutAnnouncementDetails(serviceName, port, objectDescriptions, aboutData);
        try
        {
            receivedAboutAnnouncement.convertAboutMap();
        }
        catch (BusException e)
        {
            logger.warn("BusException processing AboutMap", e);
        }

        String deviceId = receivedAboutAnnouncement.getDeviceId();
        UUID appId = receivedAboutAnnouncement.getAppId();

        if (deviceIdMatches(dutDeviceId, deviceId) && appIdMatches(dutAppId, appId))
        {
            logger.debug(String.format("Received About announcement signal from DUT with deviceId: %s, appId; %s ", deviceId, appId));

            receivedAnnouncements.add(receivedAboutAnnouncement);
        }
        else
        {
            logger.debug(String.format("Ignoring About announcement signal from DUT with deviceId: %s, appId; %s ", deviceId, appId));
        }
    }

    /**
     * Device id matches.
     *
     * @param dutDeviceId the dut device id
     * @param deviceId the device id
     * @return true, if successful
     */
    private boolean deviceIdMatches(String dutDeviceId, String deviceId)
    {
        return ((dutDeviceId == null) || dutDeviceId.equals(deviceId));
    }

    /**
     * App id matches.
     *
     * @param dutAppId the dut app id
     * @param appId the app id
     * @return true, if successful
     */
    private boolean appIdMatches(UUID dutAppId, UUID appId)
    {
        return ((dutAppId == null) || dutAppId.equals(appId));
    }

    /**
     * Clear queued device announcements.
     */
    public void clearQueuedDeviceAnnouncements()
    {
        receivedAnnouncements.clear();
    }
}