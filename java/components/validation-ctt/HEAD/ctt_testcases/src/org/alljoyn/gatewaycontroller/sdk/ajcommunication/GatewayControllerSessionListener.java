/*
 * Copyright (c) Open Connectivity Foundation (OCF) and AllJoyn Open
 *    Source Project (AJOSP) Contributors and others.
 *
 *    SPDX-License-Identifier: Apache-2.0
 *
 *    All rights reserved. This program and the accompanying materials are
 *    made available under the terms of the Apache License, Version 2.0
 *    which accompanies this distribution, and is available at
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Copyright (c) Open Connectivity Foundation and Contributors to AllSeen
 *    Alliance. All rights reserved.
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