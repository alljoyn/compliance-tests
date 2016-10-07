/*******************************************************************************
 *   *  Copyright (c) Open Connectivity Foundation (OCF) and AllJoyn Open
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
package org.alljoyn.validation.testing.suites.onboarding;

import static org.alljoyn.validation.framework.ValidationTestItem.DEFAULT_TEST_TIMEOUT_IN_MILLISECONDS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.alljoyn.onboarding.transport.OnboardingTransport;
import org.alljoyn.services.common.BusObjectDescription;
import org.alljoyn.validation.framework.AboutAnnouncement;
import org.alljoyn.validation.framework.AllJoynAnnouncedDevice;
import org.alljoyn.validation.framework.ValidationTestGroup;
import org.alljoyn.validation.framework.ValidationTestItem;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class OnboardingTestSuiteManagerTest
{
    private OnboardingTestSuiteManager onboardingTestSuiteManager;
    @Mock
    private AllJoynAnnouncedDevice allJoynAnnouncedDevice;
    @Mock
    private AboutAnnouncement firstAboutAnnouncement;
    @Mock
    private AboutAnnouncement secondAboutAnnouncement;

    @Before
    public void setup()
    {
        MockitoAnnotations.initMocks(this);
        onboardingTestSuiteManager = new OnboardingTestSuiteManager();

        BusObjectDescription[] supportsConfig = new BusObjectDescription[1];
        BusObjectDescription bod = new BusObjectDescription();
        bod.interfaces = new String[]
        { OnboardingTransport.INTERFACE_NAME };
        bod.path = OnboardingTransport.OBJ_PATH;
        supportsConfig[0] = bod;

        when(firstAboutAnnouncement.getServiceName()).thenReturn("ServiceName1");
        when(secondAboutAnnouncement.getServiceName()).thenReturn("ServiceName2");
        when(firstAboutAnnouncement.getObjectDescriptions()).thenReturn(supportsConfig);
        when(secondAboutAnnouncement.getObjectDescriptions()).thenReturn(supportsConfig);
    }

    @Test
    public void getApplicableTestsReturnsEmptyListWhenNoTestExists()
    {
        when(allJoynAnnouncedDevice.getAnnouncements()).thenReturn(new ArrayList<AboutAnnouncement>());
        List<ValidationTestGroup> testGroups = onboardingTestSuiteManager.getApplicableTests(allJoynAnnouncedDevice);

        assertTrue(testGroups.isEmpty());
    }

    @Test
    public void getApplicableTests()
    {
        List<AboutAnnouncement> aboutAnnouncements = new ArrayList<AboutAnnouncement>();
        aboutAnnouncements.add(firstAboutAnnouncement);
        aboutAnnouncements.add(secondAboutAnnouncement);
        when(allJoynAnnouncedDevice.getAnnouncements()).thenReturn(aboutAnnouncements);
        List<ValidationTestGroup> testGroups = onboardingTestSuiteManager.getApplicableTests(allJoynAnnouncedDevice);

        assertEquals(2, testGroups.size());
        validateValidationTestGroup(testGroups.get(0), firstAboutAnnouncement);
        validateValidationTestGroup(testGroups.get(1), secondAboutAnnouncement);
    }

    private void validateValidationTestGroup(ValidationTestGroup validationTestGroup, AboutAnnouncement aboutAnnouncement)
    {
        assertEquals("Onboarding-v1", validationTestGroup.getTestGroupId());
        assertEquals(aboutAnnouncement.getServiceName(), validationTestGroup.getAboutAnnouncement().getServiceName());
        assertNull(validationTestGroup.getInterfaceName());
        assertNull(validationTestGroup.getObjectPath());

        validateValidationTestItems(validationTestGroup);
    }

    private void validateValidationTestItems(ValidationTestGroup validationTestGroup)
    {
        // expected test order:
        int[] testIdx =
        { 1, 3, 5, 6, 7, 8, 9, 10, 11, 12, 2 };

        List<ValidationTestItem> validationTestItems = validationTestGroup.getTestItems();
        assertEquals(testIdx.length, validationTestItems.size());
        for (int i = 0; i < testIdx.length; i++)
        {
            validateValidationTestItem(validationTestItems.get(i), validationTestGroup, String.format("Onboarding-v1-%02d", testIdx[i]));
        }
    }

    private void validateValidationTestItem(ValidationTestItem validationTestItem, ValidationTestGroup validationTestGroup, String testCaseId)
    {
        assertEquals(validationTestGroup, validationTestItem.getTestGroup());
        assertEquals(testCaseId, validationTestItem.getTestCaseId());
        assertEquals(OnboardingTestSuite.class.getName(), validationTestItem.getClassName());
        assertEquals(DEFAULT_TEST_TIMEOUT_IN_MILLISECONDS, validationTestItem.getTimeoutInMilliseconds());
        assertTrue(validationTestItem.isInitiallySelected());
    }
}