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
package org.alljoyn.validation.testing.suites.audio;

import static org.alljoyn.validation.testing.utils.audio.AudioServiceInterfaceName.Stream;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.alljoyn.validation.framework.ValidationTestCase;
import org.alljoyn.validation.framework.ValidationTestGroup;
import org.alljoyn.validation.framework.ValidationTestSuite;
import org.alljoyn.validation.testing.suites.BaseTestSuiteManager;
import org.alljoyn.validation.testing.utils.about.AboutAnnouncementDetails;

public class AudioTestSuiteManager extends BaseTestSuiteManager implements ValidationTestSuite
{
    @Override
    protected Class<? extends ValidationTestCase> getTestSuiteClass()
    {
        return AudioTestSuite.class;
    }

    @Override
    protected List<ValidationTestGroup> createTestGroupsWithPathInformation(AboutAnnouncementDetails aboutAnnouncement)
    {
        Set<String> objectPaths = aboutAnnouncement.getObjectPaths(Stream.getValue());
        List<ValidationTestGroup> validationTestGroups = new ArrayList<ValidationTestGroup>();

        for (String objectPath : objectPaths)
        {
            ValidationTestGroup validationTestGroup = createTestGroup(aboutAnnouncement);
            validationTestGroup.setObjectPath(objectPath);
            validationTestGroups.add(validationTestGroup);
        }

        return validationTestGroups;
    }

    @Override
    protected boolean addTestGroupForApplication(AboutAnnouncementDetails aboutAnnouncement)
    {
        return aboutAnnouncement.supportsInterface(Stream.getValue());
    }

    @Override
    protected boolean addTestGroupWithPathInformation()
    {
        return true;
    }
}