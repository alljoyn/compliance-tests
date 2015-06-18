/*
 * Copyright AllSeen Alliance. All rights reserved.
 *
 *    Permission to use, copy, modify, and/or distribute this software for any
 *    purpose with or without fee is hereby granted, provided that the above
 *    copyright notice and this permission notice appear in all copies.
 *
 *    THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 *    WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 *    MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 *    ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 *    WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 *    ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 *    OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */
package com.at4wireless.alljoyn.core.commons;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import org.alljoyn.bus.AboutListener;
import org.alljoyn.bus.AboutObjectDescription;
import org.alljoyn.bus.BusException;
import org.alljoyn.bus.Variant;
import org.alljoyn.bus.annotation.BusSignalHandler;
import org.alljoyn.services.common.AnnouncementHandler;
import org.alljoyn.services.common.BusObjectDescription;

import com.at4wireless.alljoyn.core.about.AboutAnnouncementDetails;
import com.at4wireless.alljoyn.core.commons.log.LoggerFactory;
import com.at4wireless.alljoyn.core.commons.log.Logger;



// TODO: Auto-generated Javadoc
/**
 * The Class DeviceAnnouncementHandler.
 */
public class DeviceAnnouncementHandler implements AboutListener
{
    
    /** The Constant TAG. */
    private static final String TAG = "DeviceAnnounceHandler";
    
    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger(TAG);
    
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



    /* (non-Javadoc)
     * @see org.alljoyn.bus.AboutListener#announced(java.lang.String, int, short, org.alljoyn.bus.AboutObjectDescription[], java.util.Map)
     */
    public void announced(String busName, int version, short port, 
    		AboutObjectDescription[] objectDescriptions,
    		Map<String, Variant> aboutData) {

		String serviceName = busName;
		AboutAnnouncementDetails receivedAboutAnnouncement = new AboutAnnouncementDetails(serviceName, version, port, objectDescriptions, aboutData);
	   
	    try
	    {
	        receivedAboutAnnouncement.convertAboutMap();
	    }
	    catch (BusException e)
	    {
	        logger.warn("BusException processing AboutMap", e);
	    }

	    String deviceId = null;
	    UUID appId = null;
	    
		try {
			deviceId = receivedAboutAnnouncement.getDeviceId();
			
			appId = receivedAboutAnnouncement.getAppId();
		} catch (Exception e) {
			logger.error(e.toString());
		}
	   



	    if (deviceIdMatches(dutDeviceId, deviceId) && appIdMatches(dutAppId, appId))
	    {
	    	logger.info(String.format("Received About announcement signal from DUT with deviceId: %s, appId; %s ", deviceId, appId));
	        //logger.info("Partial Verdict: PASS");
	    	receivedAnnouncements.add(receivedAboutAnnouncement);       
	    }
	    else
	    {
	    	logger.info(String.format("Ignoring About announcement signal from DUT with deviceId: %s, appId; %s ", deviceId, appId));
	    }
		
	}

	
	
}