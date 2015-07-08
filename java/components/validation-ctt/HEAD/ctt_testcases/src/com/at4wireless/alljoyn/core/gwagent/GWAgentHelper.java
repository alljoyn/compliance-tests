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
package com.at4wireless.alljoyn.core.gwagent;

import java.util.UUID;

import org.alljoyn.bus.Status;
import org.alljoyn.gatewaycontroller.sdk.GatewayController;
import org.alljoyn.gatewaycontroller.sdk.ajcommunication.CommunicationUtil.SessionResult;

import com.at4wireless.alljoyn.core.about.AboutAnnouncementDetails;
import com.at4wireless.alljoyn.core.commons.ServiceHelper;
import com.at4wireless.alljoyn.core.commons.log.WindowsLoggerImpl;

public class GWAgentHelper extends ServiceHelper
{
    protected static final String TAG = "GWAgentHelper";
	private static final WindowsLoggerImpl logger =  new WindowsLoggerImpl(TAG);
    private int sessionId = 0;

    public GWAgentHelper(WindowsLoggerImpl logger)
    {
        super(logger);
    }

    @Override
    public void initialize(String busApplicationName, String deviceId, UUID appId) throws Exception
    {
        super.initialize(busApplicationName, deviceId, appId);
        startGatewayController();
    }

    public void startGatewayController() throws Exception
    {
        if (GatewayController.getInstance().getBusAttachment() == null)
        {
            GatewayController.getInstance().init(getBusAttachment());
        }
    }

    public GatewayController connectGatewayController(AboutAnnouncementDetails aboutAnnouncementDetails) throws Exception
    {
        SessionResult sessionResult = GatewayController.getInstance().joinSession(aboutAnnouncementDetails.getServiceName());
        if (sessionResult.getStatus() == Status.OK)
        {
            sessionId = sessionResult.getSid();
            return GatewayController.getInstance();
        }
        return null;
    }

    @Override
    public void release()
    {
        if (GatewayController.getInstance().getBusAttachment() == null)
        {
            if (sessionId != 0)
            {
                GatewayController.getInstance().leaveSession(sessionId);
            }
            GatewayController.getInstance().shutdown();
        }
        super.release();
    }
}
