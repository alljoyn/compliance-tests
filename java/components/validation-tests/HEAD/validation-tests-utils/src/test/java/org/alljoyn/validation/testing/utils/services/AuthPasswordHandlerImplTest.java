/*******************************************************************************
 *  Copyright (c) Open Connectivity Foundation (OCF) and AllJoyn Open
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
 *      THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL
 *      WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED
 *      WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE
 *      AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL
 *      DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR
 *      PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER
 *      TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 *      PERFORMANCE OF THIS SOFTWARE.
 *******************************************************************************/
package org.alljoyn.validation.testing.utils.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.alljoyn.services.android.security.SrpAnonymousKeyListener;
import org.alljoyn.validation.testing.utils.MyRobolectricTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

@RunWith(MyRobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class AuthPasswordHandlerImplTest
{

    private AuthPasswordHandlerImpl authPasswordHandlerImpl;
    private PasswordStore mockPasswordStore;
    private String authPeer = "authPeer";
    private String mechanism = "mechanism";

    @Before
    public void setup() throws Exception
    {
        mockPasswordStore = mock(PasswordStore.class);
        authPasswordHandlerImpl = new AuthPasswordHandlerImpl(mockPasswordStore);
    }

    @Test
    public void testCompletedAuthenticated() throws Exception
    {
        assertFalse(authPasswordHandlerImpl.isPeerAuthenticationSuccessful(authPeer));
        assertFalse(authPasswordHandlerImpl.isPeerAuthenticated(authPeer));

        authPasswordHandlerImpl.completed(mechanism, authPeer, true);
        assertTrue(authPasswordHandlerImpl.isPeerAuthenticated(authPeer));
        assertTrue(authPasswordHandlerImpl.isPeerAuthenticationSuccessful(authPeer));
    }

    @Test
    public void testCompletedNotAuthenticated() throws Exception
    {
        authPasswordHandlerImpl.completed(mechanism, authPeer, false);
        assertFalse(authPasswordHandlerImpl.isPeerAuthenticationSuccessful(authPeer));
        assertTrue(authPasswordHandlerImpl.isPeerAuthenticated(authPeer));
    }

    @Test
    public void testResetAuthentication() throws Exception
    {
        authPasswordHandlerImpl.completed(mechanism, authPeer, true);
        assertTrue(authPasswordHandlerImpl.isPeerAuthenticated(authPeer));
        assertTrue(authPasswordHandlerImpl.isPeerAuthenticationSuccessful(authPeer));
        authPasswordHandlerImpl.resetAuthentication(authPeer);
        assertFalse(authPasswordHandlerImpl.isPeerAuthenticated(authPeer));
        assertFalse(authPasswordHandlerImpl.isPeerAuthenticationSuccessful(authPeer));
    }

    @Test
    public void testGetPassword() throws Exception
    {
        char[] currentPassword = "111111".toCharArray();
        when(mockPasswordStore.getPassword(authPeer)).thenReturn(currentPassword);

        assertEquals(currentPassword, authPasswordHandlerImpl.getPassword(authPeer));
    }

    @Test
    public void testGetPasswordWhenNull() throws Exception
    {
        when(mockPasswordStore.getPassword(authPeer)).thenReturn(null);

        assertEquals(SrpAnonymousKeyListener.DEFAULT_PINCODE, authPasswordHandlerImpl.getPassword(authPeer));
    }

}