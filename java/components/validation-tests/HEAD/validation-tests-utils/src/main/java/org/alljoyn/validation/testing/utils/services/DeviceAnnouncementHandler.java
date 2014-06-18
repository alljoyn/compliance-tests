/*******************************************************************************
 *  Copyright (c) 2013 - 2014, AllSeen Alliance. All rights reserved.
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
package org.alljoyn.validation.testing.utils.services;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import org.alljoyn.bus.BusException;
import org.alljoyn.bus.Variant;
import org.alljoyn.services.common.AnnouncementHandler;
import org.alljoyn.services.common.BusObjectDescription;
import org.alljoyn.validation.testing.utils.about.AboutAnnouncementDetails;
import org.alljoyn.validation.testing.utils.log.Logger;
import org.alljoyn.validation.testing.utils.log.LoggerFactory;

public class DeviceAnnouncementHandler implements AnnouncementHandler
{
    private static final String TAG = "DeviceAnnounceHandler";
    private static final Logger logger = LoggerFactory.getLogger(TAG);
    private String dutDeviceId;
    private UUID dutAppId;
    private LinkedBlockingDeque<AboutAnnouncementDetails> receivedAnnouncements = new LinkedBlockingDeque<AboutAnnouncementDetails>();
    private LinkedBlockingDeque<String> lostDevices = new LinkedBlockingDeque<String>();

    public DeviceAnnouncementHandler(String dutDeviceId, UUID dutAppId)
    {
        this.dutAppId = dutAppId;
        this.dutDeviceId = dutDeviceId;
    }

    public AboutAnnouncementDetails waitForNextDeviceAnnouncement(long timeout, TimeUnit unit) throws InterruptedException
    {
        logger.info(String.format("Waiting for About announcement signal from device: %s; appId: %s", dutDeviceId, dutAppId));
        return receivedAnnouncements.poll(timeout, unit);
    }

    public void waitForSessionToClose(long timeout, TimeUnit unit) throws InterruptedException
    {
        logger.info(String.format("Waiting for session to close for device: %s", dutDeviceId));
        lostDevices.poll(timeout, unit);
    }

    @Override
    public void onDeviceLost(String deviceName)
    {
        logger.debug("onDeviceLost: " + deviceName);
        lostDevices.add(deviceName);
    }

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

    private boolean deviceIdMatches(String dutDeviceId, String deviceId)
    {
        return ((dutDeviceId == null) || dutDeviceId.equals(deviceId));
    }

    private boolean appIdMatches(UUID dutAppId, UUID appId)
    {
        return ((dutAppId == null) || dutAppId.equals(appId));
    }

    public void clearQueuedDeviceAnnouncements()
    {
        receivedAnnouncements.clear();
    }
}