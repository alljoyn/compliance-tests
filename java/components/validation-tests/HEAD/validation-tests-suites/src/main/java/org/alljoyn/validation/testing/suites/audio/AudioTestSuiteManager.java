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