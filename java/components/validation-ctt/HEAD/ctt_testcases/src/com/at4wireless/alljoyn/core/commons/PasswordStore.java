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

import java.util.HashMap;
import java.util.Map;

// TODO: Auto-generated Javadoc
/**
 * The Class PasswordStore.
 */
public class PasswordStore
{
    
    /** The password store. */
    private Map<String, char[]> passwordStore = new HashMap<String, char[]>();

    /**
     * Gets the password.
     *
     * @param peerName the peer name
     * @return the password
     */
    public char[] getPassword(String peerName)
    {
        return passwordStore.get(peerName);
    }

    /**
     * Sets the password.
     *
     * @param peerName the peer name
     * @param password the password
     */
    public void setPassword(String peerName, char[] password)
    {
        passwordStore.put(peerName, password);
    }
}