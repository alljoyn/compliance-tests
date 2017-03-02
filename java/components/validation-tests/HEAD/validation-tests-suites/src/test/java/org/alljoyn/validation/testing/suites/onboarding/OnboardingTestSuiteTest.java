/*******************************************************************************
 *     Copyright (c) Open Connectivity Foundation (OCF), AllJoyn Open Source
 *     Project (AJOSP) Contributors and others.
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
package org.alljoyn.validation.testing.suites.onboarding;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import junit.framework.AssertionFailedError;

import org.alljoyn.about.client.AboutClient;
import org.alljoyn.about.transport.AboutTransport;
import org.alljoyn.onboarding.OnboardingService;
import org.alljoyn.onboarding.OnboardingService.AuthType;
import org.alljoyn.onboarding.client.OnboardingClient;
import org.alljoyn.onboarding.transport.OBLastError;
import org.alljoyn.onboarding.transport.OnboardingTransport.ConfigureWifiMode;
import org.alljoyn.validation.framework.AboutAnnouncement;
import org.alljoyn.validation.framework.AppUnderTestDetails;
import org.alljoyn.validation.framework.ValidationTestContext;
import org.alljoyn.validation.testing.utils.about.AboutAnnouncementDetails;
import org.alljoyn.validation.testing.utils.onboarding.WifiHelper;
import org.alljoyn.validation.testing.utils.services.ServiceHelper;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class OnboardingTestSuiteTest extends OnboardingTestSuite
{
    private OnboardingTestSuite onboardingTestSuite;

    @Mock
    private ValidationTestContext mockTestContext;

    @Mock
    private WifiHelper mockWifiHelper;

    @Mock
    private AboutAnnouncementDetails mockAboutAnnouncementDetails;

    @Mock
    protected ServiceHelper mockServiceHelper;

    @Mock
    private AboutClient mockAboutClient;

    @Mock
    private OnboardingClient mockOnboardingClient;

    @Mock
    private OBLastError mockObLastError;

    private CountDownLatch waitForWaitForDeviceAnnouncementCall = new CountDownLatch(2);

    private Thread testThread;
    private AssertionFailedError assertionFailedError;
    protected Exception thrownException;

    private String appName = "appName";
    private String deviceName = "deviceName";
    private UUID appId = UUID.randomUUID();
    private String deviceId = appId.toString();

    private AppUnderTestDetails appUnderTestDetails;

    @Before
    public void setup() throws Exception
    {
        MockitoAnnotations.initMocks(this);

        when(mockServiceHelper.waitForNextDeviceAnnouncement(anyLong(), any(TimeUnit.class))).thenAnswer(new Answer<AboutAnnouncement>()
        {
            @Override
            public AboutAnnouncement answer(InvocationOnMock invocation) throws Throwable
            {
                waitForWaitForDeviceAnnouncementCall.countDown();
                return mockAboutAnnouncementDetails;
            }
        });

        when(mockAboutAnnouncementDetails.supportsInterface(AboutTransport.INTERFACE_NAME)).thenReturn(true);
        when(mockAboutAnnouncementDetails.supportsInterface("org.alljoyn.Onboarding")).thenReturn(true);
        when(mockAboutAnnouncementDetails.getAppId()).thenReturn(appId);
        when(mockAboutAnnouncementDetails.getAppName()).thenReturn(appName);
        when(mockAboutAnnouncementDetails.getDeviceId()).thenReturn(deviceId);
        when(mockAboutAnnouncementDetails.getDeviceName()).thenReturn(deviceName);

        // provide the deviceId for the DUT to the test
        appUnderTestDetails = new AppUnderTestDetails(appId, deviceId);
        when(mockTestContext.getAppUnderTestDetails()).thenReturn(appUnderTestDetails);

        when(mockServiceHelper.connectAboutClient(mockAboutAnnouncementDetails)).thenReturn(mockAboutClient);

        when(mockTestContext.getTestParameter(OnboardingTestParameters.PERSONAL_AP_SSID)).thenReturn("personalap");
        when(mockTestContext.getTestParameter(OnboardingTestParameters.PERSONAL_AP_PASSPHRASE)).thenReturn("password");
        when(mockTestContext.getTestParameter(OnboardingTestParameters.SOFT_AP_SSID)).thenReturn("softap");
    }

    // Onboarding-v1-03
    @Test
    @Ignore
    public void connectivityOverSoftAP() throws Exception
    {
        when(mockWifiHelper.waitForNetworkAvailable(any(String.class), eq(TIME_TO_WAIT_FOR_SOFT_AP_IN_MS_SHORT), any(TimeUnit.class))).thenReturn(true);
        // when(mockWifiHelper.connectToNetwork(any(String.class), eq(true),
        // eq(TIME_TO_WAIT_TO_CONNECT_TO_SOFT_AP_IN_MS),
        // any(TimeUnit.class))).thenReturn("softap");
        when(mockServiceHelper.connectOnboardingClient(mockAboutAnnouncementDetails)).thenReturn(mockOnboardingClient);
        when(mockOnboardingClient.getState()).thenReturn(OBS_STATE_PERSONAL_AP_NOT_CONFIGURED);
        // create instance of test suite class that we're testing
        onboardingTestSuite = new OnboardingTestSuite()
        {
            @Override
            protected WifiHelper getWifiHelper()
            {
                return mockWifiHelper;
            }

            @Override
            public ServiceHelper getServiceHelper()
            {
                return mockServiceHelper;
            }
        };
        onboardingTestSuite.setValidationTestContext(mockTestContext);

        // run the test in a separate thread to capture the
        // AssertionFailedErrors and any exceptions thrown
        setupTestAndExecuteTestMethod(new TestWrapper()
        {
            @Override
            public void executeTestMethod() throws Exception
            {
                onboardingTestSuite.testOnboarding_v1_03_ConnectivityOverSoftAP();
            }
        });

        // wait for test to exit
        testThread.join();

        verify(mockWifiHelper, atLeastOnce()).waitForNetworkAvailable(any(String.class), eq(TIME_TO_WAIT_FOR_SOFT_AP_IN_MS_SHORT), eq(TimeUnit.MILLISECONDS));

        assertNull(assertionFailedError);
        assertNull(thrownException);
    }

    // Onboarding-v1-04
    // TODO remove test 4
    @Test
    @Ignore
    public void configureWifiWithOutOfRangeValue() throws Exception
    {
        when(mockWifiHelper.waitForNetworkAvailable(any(String.class), eq(TIME_TO_WAIT_FOR_SOFT_AP_IN_MS_SHORT), eq(TimeUnit.MILLISECONDS))).thenReturn(true);
        // when(mockWifiHelper.connectToNetwork(any(String.class), eq(true),
        // eq(TIME_TO_WAIT_TO_CONNECT_TO_SOFT_AP_IN_MS),
        // eq(TimeUnit.MILLISECONDS))).thenReturn("softap");
        when(mockServiceHelper.connectOnboardingClient(mockAboutAnnouncementDetails)).thenReturn(mockOnboardingClient);
        // when(mockOnboardingClient.configureWiFi(any(String.class),
        // any(String.class), eq(INVALID_AUTH_TYPE))).thenThrow(
        // new ErrorReplyBusException("org.alljoyn.Error.OutOfRange",
        // "some error message"));
        when(mockOnboardingClient.getState()).thenReturn(OBS_STATE_PERSONAL_AP_NOT_CONFIGURED);

        // create instance of test suite class that we're testing
        onboardingTestSuite = new OnboardingTestSuite()
        {
            @Override
            protected WifiHelper getWifiHelper()
            {
                return mockWifiHelper;
            }

            @Override
            public ServiceHelper getServiceHelper()
            {
                return mockServiceHelper;
            }
        };
        onboardingTestSuite.setValidationTestContext(mockTestContext);

        // run the test in a separate thread to capture the
        // AssertionFailedErrors and any exceptions thrown
        setupTestAndExecuteTestMethod(new TestWrapper()
        {
            @Override
            public void executeTestMethod() throws Exception
            {
                onboardingTestSuite.testOnboarding_v1_04_ConfigureWiFiWithOutOfRangeValue();
            }
        });

        // wait for test to exit
        testThread.join();

        assertNull(assertionFailedError);
        assertNull(thrownException);
    }

    // Onboarding-v1-05 (condition two met)
    @Test
    @Ignore
    public void configureWifiWithWrongPersonalApSsidWithConditionTwoMet() throws Exception
    {
        when(mockWifiHelper.waitForNetworkAvailable(any(String.class), eq(TIME_TO_WAIT_FOR_SOFT_AP_IN_MS_SHORT), eq(TimeUnit.MILLISECONDS))).thenReturn(true);
        // when(mockWifiHelper.connectToNetwork(any(String.class), eq(true),
        // eq(TIME_TO_WAIT_TO_CONNECT_TO_SOFT_AP_IN_MS),
        // eq(TimeUnit.MILLISECONDS))).thenReturn("softap");
        when(mockServiceHelper.connectOnboardingClient(mockAboutAnnouncementDetails)).thenReturn(mockOnboardingClient);
        when(mockOnboardingClient.getState()).thenReturn(OnboardingService.OnboardingState.PERSONAL_AP_CONFIGURED_NOT_VALIDATED.getStateId(),
                OnboardingService.OnboardingState.PERSONAL_AP_CONFIGURED_ERROR.getStateId());
        ConfigureWifiMode mockConfigureWifiMode = Mockito.mock(ConfigureWifiMode.class);
        when(mockConfigureWifiMode.getValue()).thenReturn(OBS_CONFIGURE_WIFI_RETURN_SUPPORTS_CHANNEL_SWITCHING);
        when(mockOnboardingClient.configureWiFi(any(String.class), any(String.class), any(AuthType.class))).thenReturn(mockConfigureWifiMode);

        final short OBLASTERROR_ERROR_CODE_UNREACHABLE = 1;
        when(mockOnboardingClient.GetLastError()).thenReturn(mockObLastError);
        when(mockObLastError.getErrorCode()).thenReturn(OBLASTERROR_ERROR_CODE_UNREACHABLE);

        // create instance of test suite class that we're testing
        onboardingTestSuite = new OnboardingTestSuite()
        {
            @Override
            protected WifiHelper getWifiHelper()
            {
                return mockWifiHelper;
            }

            @Override
            public ServiceHelper getServiceHelper()
            {
                return mockServiceHelper;
            }

        };
        onboardingTestSuite.setValidationTestContext(mockTestContext);

        // run the test in a separate thread to capture the
        // AssertionFailedErrors and any exceptions thrown
        setupTestAndExecuteTestMethod(new TestWrapper()
        {
            @Override
            public void executeTestMethod() throws Exception
            {
                onboardingTestSuite.testOnboarding_v1_05_ConfigureWiFiWithWrongSSID();
            }
        });

        // wait for test to exit
        testThread.join();

        verify(mockOnboardingClient).configureWiFi(INVALID_NETWORK_NAME, "", AuthType.OPEN);
        verify(mockOnboardingClient, atLeastOnce()).connectWiFi();

        assertNull(assertionFailedError);
        assertNull(thrownException);
    }

    // Onboarding-v1-06 (condition two met)
    @Test
    @Ignore
    public void configureWifiWithInvalidPassphraseForPersonalApWithConditionTwoMet() throws Exception
    {
        when(mockWifiHelper.waitForNetworkAvailable(any(String.class), eq(TIME_TO_WAIT_FOR_SOFT_AP_IN_MS_SHORT), eq(TimeUnit.MILLISECONDS))).thenReturn(true);
        // when(mockWifiHelper.connectToNetwork(any(String.class), eq(true),
        // eq(TIME_TO_WAIT_TO_CONNECT_TO_SOFT_AP_IN_MS),
        // eq(TimeUnit.MILLISECONDS))).thenReturn("softap");
        when(mockServiceHelper.connectOnboardingClient(mockAboutAnnouncementDetails)).thenReturn(mockOnboardingClient);
        when(mockOnboardingClient.getState()).thenReturn(OnboardingService.OnboardingState.PERSONAL_AP_CONFIGURED_NOT_VALIDATED.getStateId(),
                OnboardingService.OnboardingState.PERSONAL_AP_CONFIGURED_ERROR.getStateId());
        ConfigureWifiMode mockConfigureWifiMode = Mockito.mock(ConfigureWifiMode.class);
        when(mockConfigureWifiMode.getValue()).thenReturn(OBS_CONFIGURE_WIFI_RETURN_SUPPORTS_CHANNEL_SWITCHING);
        when(mockOnboardingClient.configureWiFi(any(String.class), any(String.class), any(AuthType.class))).thenReturn(mockConfigureWifiMode);

        final short OBLASTERROR_ERROR_CODE_UNAUTHORIZED = 3;
        when(mockOnboardingClient.GetLastError()).thenReturn(mockObLastError);
        when(mockObLastError.getErrorCode()).thenReturn(OBLASTERROR_ERROR_CODE_UNAUTHORIZED);

        // create instance of test suite class that we're testing
        onboardingTestSuite = new OnboardingTestSuite()
        {
            @Override
            protected WifiHelper getWifiHelper()
            {
                return mockWifiHelper;
            }

            @Override
            public ServiceHelper getServiceHelper()
            {
                return mockServiceHelper;
            }

        };
        onboardingTestSuite.setValidationTestContext(mockTestContext);

        // run the test in a separate thread to capture the
        // AssertionFailedErrors and any exceptions thrown
        setupTestAndExecuteTestMethod(new TestWrapper()
        {
            @Override
            public void executeTestMethod() throws Exception
            {
                onboardingTestSuite.testOnboarding_v1_06_ConfigureWiFiWithWrongPassword();
            }
        });

        // wait for test to exit
        testThread.join();

        verify(mockOnboardingClient).configureWiFi(any(String.class), eq(INVALID_NETWORK_PASSPHRASE), eq(AuthType.WPA2_AUTO));
        verify(mockOnboardingClient, atLeastOnce()).connectWiFi();

        assertNull(assertionFailedError);
        assertNull(thrownException);
    }

    @Test
    @Ignore
    public void testConnectingToSoftAPThrowsWifiNotEnabledException() throws Exception
    {
        // create instance of test suite class that we're testing
        onboardingTestSuite = new OnboardingTestSuite()
        {
            @Override
            protected WifiHelper getWifiHelper()
            {
                // return new
                // WifiHelperImpl(Robolectric.getShadowApplication().getApplicationContext())
                // {
                // @Override
                // public boolean isWifiEnabled()
                // {
                // return false;
                // }
                //
                // };
                // TODO fix this one
                return null;
            }

            @Override
            public ServiceHelper getServiceHelper()
            {
                return mockServiceHelper;
            }
        };
        onboardingTestSuite.setValidationTestContext(mockTestContext);

        // run the test in a separate thread to capture the
        // AssertionFailedErrors and any exceptions thrown
        setupTestAndExecuteTestMethod(new TestWrapper()
        {
            @Override
            public void executeTestMethod() throws Exception
            {
                onboardingTestSuite.testOnboarding_v1_03_ConnectivityOverSoftAP();
            }
        });

        // wait for test to exit
        testThread.join();

        assertNull(assertionFailedError);
        assertNotNull(thrownException); // expecting WifiNotEnabledException to
                                        // be thrown here
    }

    private interface TestWrapper
    {
        void executeTestMethod() throws Exception;
    }

    private void setupTestAndExecuteTestMethod(final TestWrapper testWrapper)
    {
        Runnable runnable = new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    onboardingTestSuite.setUp();
                    testWrapper.executeTestMethod();
                }
                catch (AssertionFailedError t)
                {
                    assertionFailedError = t;
                }
                catch (Exception e)
                {
                    thrownException = e;
                }
                try
                {
                    onboardingTestSuite.tearDown();
                }
                catch (Exception e)
                {
                }
                waitForWaitForDeviceAnnouncementCall.countDown();
                waitForWaitForDeviceAnnouncementCall.countDown();
            }

        };
        testThread = new Thread(runnable);
        testThread.start();
    }
}