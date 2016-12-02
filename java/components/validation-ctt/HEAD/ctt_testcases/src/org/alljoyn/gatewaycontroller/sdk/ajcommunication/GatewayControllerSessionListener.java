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

package org.alljoyn.gatewaycontroller.sdk.ajcommunication;

import org.alljoyn.bus.SessionListener;
import org.alljoyn.gatewaycontroller.sdk.ajcommunication.CommunicationUtil.SessionResult;

import com.at4wireless.alljoyn.core.commons.log.WindowsLoggerImpl;



// TODO: Auto-generated Javadoc
/**
 * This class is responsible for handling session related events from the
 * AllJoyn system. Extend this class to receive the events of: <br>
 * - sessionJoined <br>
 * - sessioLost <br>
 * 
 * The events are called on the AllJoyn thread, so avoid blocking them with long
 * running tasks.
 */
public class GatewayControllerSessionListener extends SessionListener {
    
    /** The Constant TAG. */
    private static final String TAG = "gwc" + GatewayControllerSessionListener.class.getSimpleName();
	
	/** The Constant Log. */
	private static final WindowsLoggerImpl Log =  new WindowsLoggerImpl(TAG);

    /**
     * Receives sessionLost event. Avoid blocking this thread with long running
     * tasks.
     * 
     * @param sessionId
     *            The id of the lost session
     * @param reason
     *            The reason of the lost session
     * @see org.alljoyn.bus.SessionListener#sessionLost(int, int)
     */
    @Override
    public void sessionLost(int sessionId, int reason) {
        Log.debug( "Received SESSION_LOST for SID: '" + sessionId + "', reason: '" + reason + "'");
    }

    /**
     * Receives result of joining session asynchronously. Avoid blocking this
     * thread with long running tasks.
     * 
     * @param result
     */
    public void sessionJoined(SessionResult result) {
        Log.debug(  "SESSION_JOINED: '" + result + "'");
    }

}