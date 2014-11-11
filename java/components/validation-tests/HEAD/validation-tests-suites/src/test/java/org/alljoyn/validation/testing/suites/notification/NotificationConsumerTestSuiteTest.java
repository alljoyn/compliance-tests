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
 *      THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL
 *      WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED
 *      WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE
 *      AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL
 *      DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR
 *      PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER
 *      TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 *      PERFORMANCE OF THIS SOFTWARE.
 *******************************************************************************/
package org.alljoyn.validation.testing.suites.notification;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.alljoyn.about.AboutServiceImpl;
import org.alljoyn.bus.BusAttachment;
import org.alljoyn.bus.Status;
import org.alljoyn.ns.NotificationSender;
import org.alljoyn.ns.NotificationServiceException;
import org.alljoyn.services.common.PropertyStore;
import org.alljoyn.validation.framework.UserInputDetails;
import org.alljoyn.validation.framework.UserResponse;
import org.alljoyn.validation.framework.ValidationTestContext;
import org.alljoyn.validation.testing.suites.BaseTestSuiteTest;
import org.alljoyn.validation.testing.suites.MyRobolectricTestRunner;
import org.alljoyn.validation.testing.utils.services.ServiceHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.annotation.Config;

@RunWith(MyRobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class NotificationConsumerTestSuiteTest extends BaseTestSuiteTest
{

    private NotificationConsumerTestSuite notifTestSuite;

    @Mock
    private ServiceHelper mockServiceHelper;

    @Mock
    private ValidationTestContext mockTestContext;
    protected Exception thrownException;

    @Mock
    private AboutServiceImpl mockAboutService;
    @Mock
    private BusAttachment mockBusAttachment;

    private NotificationSender mockNotifSender = mock(NotificationSender.class);
    private PropertyStore mockPropertyStore = mock(PropertyStore.class);

    @Before
    public void setup() throws Exception
    {
        MockitoAnnotations.initMocks(this);

        when(mockBusAttachment.connect()).thenReturn(Status.OK);

        notifTestSuite = new NotificationConsumerTestSuite()
        {

            @Override
            protected int getRandomNumber(int n)
            {
                return 0;
            }

            @Override
            protected PropertyStore getPropertyStoreImpl()
            {
                return mockPropertyStore;
            }

            @Override
            protected ServiceHelper getServiceHelper()
            {
                return mockServiceHelper;
            }
        };

        notifTestSuite.setValidationTestContext(mockTestContext);

        when(mockServiceHelper.initNotificationSender(mockPropertyStore)).thenReturn(mockNotifSender);
    }

    protected void setUserResponse(int optionSelected) throws InterruptedException
    {
        UserResponse userResponse = new UserResponse();
        userResponse.setOptionSelected(optionSelected);
        when(mockTestContext.waitForUserInput(any(UserInputDetails.class))).thenReturn(userResponse);
    }

    @Test
    public void test_resourcesReleasedOnException() throws Exception
    {
        when(mockServiceHelper.initNotificationSender(mockPropertyStore)).thenThrow(new NotificationServiceException("error"));

        executeTestMethodThrowsException(getTestWrapperFor_v1_01(), "error");

        verify(mockServiceHelper).release();
    }

    @Test
    public void test_01_success() throws Exception
    {
        setUserResponse(0);

        executeTestMethod(getTestWrapperFor_v1_01());
    }

    @Test
    public void test_01_wrongOptionSelected() throws Exception
    {
        setUserResponse(1);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_01(), "Incorrect message option selected expected:<Test Msg [1]> but was:<Test Msg [2]>");
    }

    @Test
    public void test_01_NoOptionSelected() throws Exception
    {
        setUserResponse(-1);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_01(), "A message option was not selected");
    }

    @Test
    public void test_02_success() throws Exception
    {
        setUserResponse(0);

        executeTestMethod(getTestWrapperFor_v1_02());
    }

    @Test
    public void test_02_wrongOptionSelected() throws Exception
    {
        setUserResponse(1);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_02(), "Incorrect message option selected expected:<Two languages Msg [1]> but was:<Two languages Msg [2]>");
    }

    @Test
    public void test_02_NoOptionSelected() throws Exception
    {
        setUserResponse(-1);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_02(), "A message option was not selected");
    }

    @Test
    public void test_03_success() throws Exception
    {
        setUserResponse(0);

        executeTestMethod(getTestWrapperFor_v1_03());
    }

    @Test
    public void test_03_wrongOptionSelected() throws Exception
    {
        setUserResponse(1);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_03(), "Incorrect message option selected expected:<No langTag Msg [1]> but was:<No langTag Msg [2]>");
    }

    @Test
    public void test_03_NoOptionSelected() throws Exception
    {
        setUserResponse(-1);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_03(), "A message option was not selected");
    }

    @Test
    public void test_04_success() throws Exception
    {
        setUserResponse(0);

        executeTestMethod(getTestWrapperFor_v1_04());
    }

    @Test
    public void test_04_wrongOptionSelected() throws Exception
    {
        setUserResponse(1);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_04(), "Incorrect message option selected expected:<Invalid langTag Msg [1]> but was:<Invalid langTag Msg [2]>");
    }

    @Test
    public void test_04_NoOptionSelected() throws Exception
    {
        setUserResponse(-1);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_04(), "A message option was not selected");
    }

    @Test
    public void test_05_success() throws Exception
    {
        setUserResponse(0);

        executeTestMethod(getTestWrapperFor_v1_05());
    }

    @Test
    public void test_05_wrongOptionSelected() throws Exception
    {
        setUserResponse(1);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_05(),
                "Incorrect message option selected expected:<...Y); Priority Msg 2 ([WARNING); Priority Msg 3 (INFO])> but was:<...Y); Priority Msg 2 ([INFO); Priority Msg 3 (WARNING])>");
    }

    @Test
    public void test_05_NoOptionSelected() throws Exception
    {
        setUserResponse(-1);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_05(), "A message option was not selected");
    }

    @Test
    public void test_06_success() throws Exception
    {
        setUserResponse(0);

        executeTestMethod(getTestWrapperFor_v1_06());
    }

    @Test
    public void test_06_wrongOptionSelected() throws Exception
    {
        setUserResponse(1);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_06(), "Incorrect message option selected expected:<Msg w/ attributes [1]> but was:<Msg w/ attributes [2]>");
    }

    @Test
    public void test_06_NoOptionSelected() throws Exception
    {
        setUserResponse(-1);

        executeTestMethodFailsAssertion(getTestWrapperFor_v1_06(), "A message option was not selected");
    }

    protected TestWrapper getTestWrapperFor_v1_01()
    {
        return new TestWrapper()
        {
            @Override
            public void executeTestMethod() throws Exception
            {
                notifTestSuite.testNotification_Consumer_v1_01();
            }
        };
    }

    protected TestWrapper getTestWrapperFor_v1_02()
    {
        return new TestWrapper()
        {
            @Override
            public void executeTestMethod() throws Exception
            {
                notifTestSuite.testNotification_Consumer_v1_02();
            }
        };
    }

    protected TestWrapper getTestWrapperFor_v1_03()
    {
        return new TestWrapper()
        {
            @Override
            public void executeTestMethod() throws Exception
            {
                notifTestSuite.testNotification_Consumer_v1_03();
            }
        };
    }

    protected TestWrapper getTestWrapperFor_v1_04()
    {
        return new TestWrapper()
        {
            @Override
            public void executeTestMethod() throws Exception
            {
                notifTestSuite.testNotification_Consumer_v1_04();
            }
        };
    }

    protected TestWrapper getTestWrapperFor_v1_05()
    {
        return new TestWrapper()
        {
            @Override
            public void executeTestMethod() throws Exception
            {
                notifTestSuite.testNotification_Consumer_v1_05();
            }
        };
    }

    protected TestWrapper getTestWrapperFor_v1_06()
    {
        return new TestWrapper()
        {
            @Override
            public void executeTestMethod() throws Exception
            {
                notifTestSuite.testNotification_Consumer_v1_06();
            }
        };
    }

    @Override
    protected void executeTestMethod(TestWrapper testWrapper) throws Exception
    {
        notifTestSuite.setUp();
        try
        {
            testWrapper.executeTestMethod();
        }
        finally
        {
            notifTestSuite.tearDown();
        }
    }
}