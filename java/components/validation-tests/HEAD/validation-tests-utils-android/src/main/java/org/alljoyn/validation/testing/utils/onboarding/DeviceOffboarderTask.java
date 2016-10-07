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
package org.alljoyn.validation.testing.utils.onboarding;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import org.alljoyn.bus.BusException;
import org.alljoyn.validation.framework.AboutAnnouncement;
import org.alljoyn.validation.testing.utils.about.AboutAnnouncementDetails;
import org.alljoyn.validation.testing.utils.log.Logger;
import org.alljoyn.validation.testing.utils.log.LoggerFactory;
import org.alljoyn.validation.testing.utils.onboarding.DeviceOnboarder.OffboardingOperation;
import org.alljoyn.validation.testing.utils.onboarding.DeviceOnboarder.OffboardingProgressListener;
import org.alljoyn.validation.testing.utils.onboarding.DeviceOnboarder.OffboardingState;

import android.content.Context;

public class DeviceOffboarderTask implements Callable<Object>
{
    private static final String TAG = "DeviceOffboarderTask";
    private static final Logger logger = LoggerFactory.getLogger(TAG);
    private AboutAnnouncementDetails aboutAnnouncementDetails;
    private OffboardingProgressListener listener;
    private OnboardingHelper onboardingHelper;
    private String keyStorePath;

    public DeviceOffboarderTask(Context context, AboutAnnouncement aboutAnnouncement, WifiNetworkConfig personalAPConfig, OffboardingProgressListener listener)
    {
        initAboutAnnouncementDetail(aboutAnnouncement);

        this.listener = listener;

        keyStorePath = context.getFileStreamPath("alljoyn_keystore").getAbsolutePath();
        onboardingHelper = getOnboardingHelper(context);
        onboardingHelper.setPersonalAPConfig(personalAPConfig);
    }

    protected OnboardingHelper getOnboardingHelper(Context context)
    {
        return new OnboardingHelper(context);
    }

    private void releaseResources()
    {
        if (onboardingHelper != null)
        {
            onboardingHelper.release();
            onboardingHelper = null;
        }
    }

    @Override
    public Object call() throws Exception
    {
        try
        {
            listener.onStateChanged(OffboardingState.WAITING_ANNOUNCEMENT_AND_CONNECTING);
            onboardingHelper.initialize(keyStorePath, aboutAnnouncementDetails.getDeviceId(), aboutAnnouncementDetails.getAppId());

            onboardingHelper.waitForAboutAnnouncementAndThenConnect();

            listener.onStateChanged(OffboardingState.CALLING_OFFBOARD);
            onboardingHelper.callOffboard();

            listener.onFinished(true, null);
        }
        catch (Exception e)
        {
            listener.onFinished(false, e.getMessage());
        }
        finally
        {
            releaseResources();
        }
        return null;
    }

    public class OffboardingOperationImpl extends FutureTask<Object> implements OffboardingOperation
    {

        public OffboardingOperationImpl(Callable<Object> callable)
        {
            super(callable);
        }

        @Override
        public void cancel()
        {
            super.cancel(true);
        }

    }

    public OffboardingOperationImpl getRunnable()
    {
        return new OffboardingOperationImpl(this);
    }

    private void initAboutAnnouncementDetail(AboutAnnouncement aboutAnnouncement)
    {
        this.aboutAnnouncementDetails = new AboutAnnouncementDetails(aboutAnnouncement);

        try
        {
            aboutAnnouncementDetails.convertAboutMap();
        }
        catch (BusException e)
        {
            logger.warn("BusException processing AboutMap", e);
        }
    }
}