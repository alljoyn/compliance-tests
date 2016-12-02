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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class PasswordStoreTest
{

    @Test
    public void testGetAndSetPassword() throws Exception
    {
        PasswordStore passwordStore = new PasswordStore();
        String peerName = "peerName";
        String peerName2 = "peerName2";

        assertNull(passwordStore.getPassword(peerName));

        char[] password1 = "111111".toCharArray();
        passwordStore.setPassword(peerName, password1);

        assertEquals(password1, passwordStore.getPassword(peerName));

        assertNull(passwordStore.getPassword(peerName2));
    }

}