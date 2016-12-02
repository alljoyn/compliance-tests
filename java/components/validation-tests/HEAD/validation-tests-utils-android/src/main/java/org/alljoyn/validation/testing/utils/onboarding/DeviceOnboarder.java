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

import org.alljoyn.validation.framework.AboutAnnouncement;

public interface DeviceOnboarder
{

    public static enum OffboardingState
    {
        WAITING_ANNOUNCEMENT_AND_CONNECTING, CALLING_OFFBOARD;
    }

    public interface OffboardingProgressListener
    {
        void onStateChanged(OffboardingState state);

        void onFinished(boolean successful, String errorMessage);
    }

    public static enum OnboardingState
    {
        CONNECTING_TO_SOFT_AP, WAITING_ANNOUNCEMENT_AND_CONNECTING, CALLING_CONFIGURE_WIFI, WAITING_DISCONNECT, CONNECTING_TO_PERSONAL_AP, WAITING_ANNOUNCEMENT_ON_PERSONAL_AP;
    }

    public interface OnboardingProgressListener
    {
        void onStateChanged(OnboardingState state);

        void onFinished(boolean successful, String errorMessage);
    }

    public interface OnboardingOperation
    {
        boolean isDone();

        void cancel();

        String getDeviceId();

        String getDeviceName();
    }

    public interface OffboardingOperation
    {
        boolean isDone();

        void cancel();
    }

    OffboardingOperation offboardDevice(AboutAnnouncement aboutAnnouncement, WifiNetworkConfig personalAPConfig, OffboardingProgressListener listener);

    OnboardingOperation onboardDevice(WifiNetworkConfig softAPConfig, WifiNetworkConfig personalAPConfig, OnboardingProgressListener listener);

}