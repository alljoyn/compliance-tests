/*******************************************************************************
 *  Copyright (c) 2016 Open Connectivity Foundation (OCF) and AllJoyn Open
 *     Source Project (AJOSP) Contributors and others.
 *
 *     SPDX-License-Identifier: Apache-2.0
 *
 *     All rights reserved. This program and the accompanying materials are
 *     made available under the terms of the Apache License, Version 2.0
 *     which accompanies this distribution, and is available at
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Copyright 2016 Open Connectivity Foundation and Contributors to
 *     AllSeen Alliance. All rights reserved.
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