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
 *     THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL
 *     WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED
 *     WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE
 *     AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL
 *     DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR
 *     PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER
 *     TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 *     PERFORMANCE OF THIS SOFTWARE.
 *******************************************************************************/
package org.alljoyn.validation.testing.utils.gwagent;

import java.util.UUID;

import org.alljoyn.bus.Status;
import org.alljoyn.gatewaycontroller.sdk.GatewayController;
import org.alljoyn.gatewaycontroller.sdk.ajcommunication.CommunicationUtil.SessionResult;
import org.alljoyn.services.common.utils.GenericLogger;
import org.alljoyn.validation.testing.utils.about.AboutAnnouncementDetails;
import org.alljoyn.validation.testing.utils.log.Logger;
import org.alljoyn.validation.testing.utils.log.LoggerFactory;
import org.alljoyn.validation.testing.utils.services.ServiceHelper;

public class GWAgentHelper extends ServiceHelper
{

    protected static final String TAG = "GWAgentHelper";
    private static final Logger logger = LoggerFactory.getLogger(TAG);
    private int sessionId = 0;

    public GWAgentHelper(GenericLogger logger)
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