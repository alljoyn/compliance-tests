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
package org.alljoyn.validation.testing.suites.notification;

import java.util.ArrayList;
import java.util.List;

import org.alljoyn.validation.framework.AboutAnnouncement;
import org.alljoyn.validation.framework.AllJoynAnnouncedDevice;
import org.alljoyn.validation.framework.ValidationTestCase;
import org.alljoyn.validation.framework.ValidationTestGroup;
import org.alljoyn.validation.framework.ValidationTestSuite;
import org.alljoyn.validation.testing.suites.BaseTestSuiteManager;
import org.alljoyn.validation.testing.utils.about.AboutAnnouncementDetails;

public class NotificationConsumerTestSuiteManager extends BaseTestSuiteManager implements ValidationTestSuite
{
    @Override
    protected Class<? extends ValidationTestCase> getTestSuiteClass()
    {
        return NotificationConsumerTestSuite.class;
    }

    @Override
    public List<ValidationTestGroup> getApplicableTests(AllJoynAnnouncedDevice allJoynAnnouncedDevice)
    {
        List<ValidationTestGroup> testGroups = new ArrayList<ValidationTestGroup>();
        List<AboutAnnouncement> aboutAnnouncements = allJoynAnnouncedDevice.getAnnouncements();

        if (aboutAnnouncements.size() > 0)
        {
            AboutAnnouncementDetails aboutAnnouncement = getAboutAnnouncementDetails(aboutAnnouncements.get(0));
            testGroups.add(createTestGroup(aboutAnnouncement));
        }

        return testGroups;
    }

    @Override
    protected boolean isSelectedInitially()
    {
        return false;
    }
}