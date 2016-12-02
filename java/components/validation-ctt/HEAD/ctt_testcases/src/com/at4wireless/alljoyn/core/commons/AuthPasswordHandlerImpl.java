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
package com.at4wireless.alljoyn.core.commons;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.at4wireless.alljoyn.core.commons.log.LoggerFactory;
import com.at4wireless.alljoyn.core.commons.log.Logger;

// TODO: Auto-generated Javadoc
//import org.alljoyn.services.android.security.AuthPasswordHandler;
//import org.alljoyn.services.android.security.SrpAnonymousKeyListener;
//ANDROID



/**
 * The Class AuthPasswordHandlerImpl.
 */
public class AuthPasswordHandlerImpl implements AuthPasswordHandler
{
    
    /** The Constant TAG. */
    private static final String TAG = "AuthPasswordHandlerImpl";
    
    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger(TAG);
    
    /** The password store. */
    private PasswordStore passwordStore = new PasswordStore();
    
    /** The peer authenticated. */
    private Map<String, Boolean> peerAuthenticated = new HashMap<String, Boolean>();
    
    /** The peer authentication successful. */
    private Map<String, Boolean> peerAuthenticationSuccessful = new HashMap<String, Boolean>();

    /**
     * Instantiates a new auth password handler impl.
     *
     * @param passwordStore the password store
     */
    public AuthPasswordHandlerImpl(PasswordStore passwordStore)
    {
        this.passwordStore = passwordStore;
    }

    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.commons.AuthPasswordHandler#completed(java.lang.String, java.lang.String, boolean)
     */
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

    /* (non-Javadoc)
     * @see com.at4wireless.alljoyn.core.commons.AuthPasswordHandler#getPassword(java.lang.String)
     */
    @Override
    public char[] getPassword(String peerName)
    {
        char[] securedSessionPassword = passwordStore.getPassword(peerName);

        securedSessionPassword = (securedSessionPassword != null) ? securedSessionPassword : SrpAnonymousKeyListener.DEFAULT_PINCODE;
        logger.debug(String.format("Providing password for %s as %s", peerName, Arrays.toString(securedSessionPassword)));
        return securedSessionPassword;
    }

    /**
     * Reset authentication.
     *
     * @param authPeer the auth peer
     */
    public void resetAuthentication(String authPeer)
    {
        peerAuthenticated.put(authPeer, false);
        peerAuthenticationSuccessful.put(authPeer, false);
    }

    /**
     * Checks if is peer authenticated.
     *
     * @param authPeer the auth peer
     * @return true, if is peer authenticated
     */
    public boolean isPeerAuthenticated(String authPeer)
    {
        return isTrueBoolean(peerAuthenticated.get(authPeer));

    }

    /**
     * Checks if is peer authentication successful.
     *
     * @param authPeer the auth peer
     * @return true, if is peer authentication successful
     */
    public boolean isPeerAuthenticationSuccessful(String authPeer)
    {
        return isTrueBoolean(peerAuthenticationSuccessful.get(authPeer));
    }

    /**
     * Checks if is true boolean.
     *
     * @param value the value
     * @return true, if is true boolean
     */
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