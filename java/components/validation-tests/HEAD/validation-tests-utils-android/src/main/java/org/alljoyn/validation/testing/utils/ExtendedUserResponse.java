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
package org.alljoyn.validation.testing.utils;

import org.alljoyn.validation.framework.UserResponse;

public class ExtendedUserResponse extends UserResponse
{

    private boolean activityStopping = false;

    public boolean isActivityStopping()
    {
        return activityStopping;
    }

    public void setActivityStopping(boolean activityStopping)
    {
        this.activityStopping = activityStopping;
    }

}