/*******************************************************************************
 *   *  
 *     Copyright (c) 2016 Open Connectivity Foundation and AllJoyn Open
 *     Source Project Contributors and others.
 *     
 *     All rights reserved. This program and the accompanying materials are
 *     made available under the terms of the Apache License, Version 2.0
 *     which accompanies this distribution, and is available at
 *     http://www.apache.org/licenses/LICENSE-2.0

 *******************************************************************************/
package org.alljoyn.validation.testing.utils.services;

import java.util.HashMap;
import java.util.Map;

public class PasswordStore
{
    private Map<String, char[]> passwordStore = new HashMap<String, char[]>();

    public char[] getPassword(String peerName)
    {
        return passwordStore.get(peerName);
    }

    public void setPassword(String peerName, char[] password)
    {
        passwordStore.put(peerName, password);
    }
}