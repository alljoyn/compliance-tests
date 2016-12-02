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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.alljoyn.validation.framework.AboutAnnouncement;
import org.alljoyn.validation.testing.utils.onboarding.DeviceOffboarderTask.OffboardingOperationImpl;
import org.alljoyn.validation.testing.utils.onboarding.DeviceOnboarderTask.OnboardingOperationImpl;

import android.content.Context;

public class DeviceOnboarderImpl implements DeviceOnboarder
{
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private Context context;

    public DeviceOnboarderImpl(Context context)
    {
        this.context = context;
    }

    @Override
    public OffboardingOperation offboardDevice(AboutAnnouncement aboutAnnouncement, WifiNetworkConfig personalAPConfig, OffboardingProgressListener listener)
    {
        DeviceOffboarderTask deviceOffboarderTask = new DeviceOffboarderTask(context, aboutAnnouncement, personalAPConfig, listener);
        OffboardingOperationImpl obsOperation = deviceOffboarderTask.getRunnable();
        executorService.submit(obsOperation);
        return obsOperation;
    }

    @Override
    public OnboardingOperation onboardDevice(WifiNetworkConfig softAPConfig, WifiNetworkConfig personalAPConfig, OnboardingProgressListener listener)
    {
        DeviceOnboarderTask deviceOnboarderTask = new DeviceOnboarderTask(context, softAPConfig, personalAPConfig, listener);
        OnboardingOperationImpl obsOperation = deviceOnboarderTask.getRunnable();
        executorService.submit(obsOperation);
        return obsOperation;
    }

}