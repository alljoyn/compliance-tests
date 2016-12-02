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
package com.at4wireless.alljoyn.core.gwagent;

import java.util.UUID;

import org.alljoyn.bus.Status;
import org.alljoyn.gatewaycontroller.sdk.GatewayController;
import org.alljoyn.gatewaycontroller.sdk.ajcommunication.CommunicationUtil.SessionResult;
import org.alljoyn.services.common.utils.GenericLogger;

import com.at4wireless.alljoyn.core.about.AboutAnnouncementDetails;
import com.at4wireless.alljoyn.core.commons.ServiceHelper;
import com.at4wireless.alljoyn.core.commons.log.Logger;
import com.at4wireless.alljoyn.core.commons.log.WindowsLoggerImpl;

// TODO: Auto-generated Javadoc
/**
 * The Class GWAgentHelper.
 */
public class GWAgentHelper extends ServiceHelper
{

    /** The Constant TAG. */
    protected static final String TAG = "GWAgentHelper";
	
	/** The Constant logger. */
	private static final WindowsLoggerImpl logger =  new WindowsLoggerImpl(TAG);
    
    /** The session id. */
    private int sessionId = 0;

    /**
     * Instantiates a new GW agent helper.
     *
     * @param logger the logger
     */
    public GWAgentHelper(WindowsLoggerImpl logger)
    {
        super();
    }

    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.commons.ServiceHelper#initialize(java.lang.String, java.lang.String, java.util.UUID)
     */
    @Override
    public void initialize(String busApplicationName, String deviceId, UUID appId) throws Exception
    {
        super.initialize(busApplicationName, deviceId, appId);
        startGatewayController();
    }

    /**
     * Start gateway controller.
     *
     * @throws Exception the exception
     */
    public void startGatewayController() throws Exception
    {
        if (GatewayController.getInstance().getBusAttachment() == null)
        {
            GatewayController.getInstance().init(getBusAttachment());
        }
    }

    /**
     * Connect gateway controller.
     *
     * @param aboutAnnouncementDetails the about announcement details
     * @return the gateway controller
     * @throws Exception the exception
     */
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

    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.commons.ServiceHelper#release()
     */
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