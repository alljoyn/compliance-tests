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
package org.alljoyn.validation.testing.utils.onboarding;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import android.content.Context;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class OnboardingHelperTest
{
    private Context mockContext;
    private OnboardingHelper onboardingHelper;

    @Test
    public void testGetAuthType()
    {
        mockContext = mock(Context.class);
        onboardingHelper = new OnboardingHelper(mockContext);

        assertEquals(-2, onboardingHelper.getAuthType("-2"));
        assertEquals(9999, onboardingHelper.getAuthType("9999"));
        assertEquals(-1, onboardingHelper.getAuthType("blah"));

        assertEquals(-3, onboardingHelper.getAuthType("WPA2_AUTO"));
        assertEquals(-3, onboardingHelper.getAuthType("WPA2"));
        assertEquals(-2, onboardingHelper.getAuthType("WPA_AUTO"));
        assertEquals(-2, onboardingHelper.getAuthType("WPA"));
        assertEquals(-1, onboardingHelper.getAuthType("ANY"));
        assertEquals(0, onboardingHelper.getAuthType("OPEN"));
        assertEquals(0, onboardingHelper.getAuthType("NONE"));
        assertEquals(1, onboardingHelper.getAuthType("WEP"));
        assertEquals(2, onboardingHelper.getAuthType("WPA_TKIP"));
        assertEquals(3, onboardingHelper.getAuthType("WPA_CCMP"));
        assertEquals(4, onboardingHelper.getAuthType("WPA2_TKIP"));
        assertEquals(5, onboardingHelper.getAuthType("WPA2_CCMP"));
        assertEquals(6, onboardingHelper.getAuthType("WPS"));
    }

    @Test
    public void testMapAuthTypeToAuthTypeString()
    {
        mockContext = mock(Context.class);
        onboardingHelper = new OnboardingHelper(mockContext);

        assertEquals("OPEN", onboardingHelper.mapAuthTypeToAuthTypeString((short) -4));
        assertEquals("WPA2_AUTO", onboardingHelper.mapAuthTypeToAuthTypeString((short) -3));
        assertEquals("WPA_AUTO", onboardingHelper.mapAuthTypeToAuthTypeString((short) -2));
        assertEquals("ANY", onboardingHelper.mapAuthTypeToAuthTypeString((short) -1));
        assertEquals("OPEN", onboardingHelper.mapAuthTypeToAuthTypeString((short) 0));
        assertEquals("WEP", onboardingHelper.mapAuthTypeToAuthTypeString((short) 1));
        assertEquals("WPA_TKIP", onboardingHelper.mapAuthTypeToAuthTypeString((short) 2));
        assertEquals("WPA_CCMP", onboardingHelper.mapAuthTypeToAuthTypeString((short) 3));
        assertEquals("WPA2_TKIP", onboardingHelper.mapAuthTypeToAuthTypeString((short) 4));
        assertEquals("WPA2_CCMP", onboardingHelper.mapAuthTypeToAuthTypeString((short) 5));
        assertEquals("WPS", onboardingHelper.mapAuthTypeToAuthTypeString((short) 6));
        assertEquals("OPEN", onboardingHelper.mapAuthTypeToAuthTypeString((short) 7));
    }
}