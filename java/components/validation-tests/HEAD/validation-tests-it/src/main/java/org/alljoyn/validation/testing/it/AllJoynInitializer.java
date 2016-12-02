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
package org.alljoyn.validation.testing.it;

import org.alljoyn.validation.testing.utils.bus.BusAttachmentMgr;

import android.test.AndroidTestCase;

public class AllJoynInitializer extends AndroidTestCase
{
    public void testStartup()
    {
        BusAttachmentMgr.init(getContext());
    }

    public void testShutdown()
    {
    }
}