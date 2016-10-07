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
package org.alljoyn.validation.testing.utils.services;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.alljoyn.services.android.security.AuthPasswordHandler;
import org.alljoyn.services.android.security.SrpAnonymousKeyListener;
import org.alljoyn.validation.testing.utils.log.Logger;
import org.alljoyn.validation.testing.utils.log.LoggerFactory;

public class AuthPasswordHandlerImpl implements AuthPasswordHandler
{
    private static final String TAG = "AuthPasswordHandlerImpl";
    private static final Logger logger = LoggerFactory.getLogger(TAG);
    private PasswordStore passwordStore = new PasswordStore();
    private Map<String, Boolean> peerAuthenticated = new HashMap<String, Boolean>();
    private Map<String, Boolean> peerAuthenticationSuccessful = new HashMap<String, Boolean>();

    public AuthPasswordHandlerImpl(PasswordStore passwordStore)
    {
        this.passwordStore = passwordStore;
    }

    @Override
    public void completed(String mechanism, String authPeer, boolean authenticated)
    {
        if (!authenticated)
        {
            peerAuthenticated.put(authPeer, true);
            peerAuthenticationSuccessful.put(authPeer, false);
            logger.info(String.format(" ** %s failed to authenticate", authPeer));
        }
        else
        {
            peerAuthenticated.put(authPeer, true);
            peerAuthenticationSuccessful.put(authPeer, true);
            logger.info(String.format(" ** %s successfully authenticated", authPeer));
        }
    }

    @Override
    public char[] getPassword(String peerName)
    {
        char[] securedSessionPassword = passwordStore.getPassword(peerName);

        securedSessionPassword = (securedSessionPassword != null) ? securedSessionPassword : SrpAnonymousKeyListener.DEFAULT_PINCODE;
        logger.debug(String.format("Providing password for %s as %s", peerName, Arrays.toString(securedSessionPassword)));
        return securedSessionPassword;
    }

    public void resetAuthentication(String authPeer)
    {
        peerAuthenticated.put(authPeer, false);
        peerAuthenticationSuccessful.put(authPeer, false);
    }

    public boolean isPeerAuthenticated(String authPeer)
    {
        return isTrueBoolean(peerAuthenticated.get(authPeer));

    }

    public boolean isPeerAuthenticationSuccessful(String authPeer)
    {
        return isTrueBoolean(peerAuthenticationSuccessful.get(authPeer));
    }

    protected boolean isTrueBoolean(Boolean value)
    {
        boolean result;
        if (value == null)
        {
            result = false;
        }
        else
        {
            result = value;
        }
        return result;
    }
}