/*******************************************************************************
 *     Copyright (c) Open Connectivity Foundation (OCF) and AllJoyn Open
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
 *     THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL
 *     WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED
 *     WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE
 *     AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL
 *     DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR
 *     PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER
 *     TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 *     PERFORMANCE OF THIS SOFTWARE.
 *******************************************************************************/
package org.alljoyn.validation.testing.suites;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.alljoyn.bus.BusException;
import org.alljoyn.validation.framework.AboutAnnouncement;
import org.alljoyn.validation.framework.AllJoynAnnouncedDevice;
import org.alljoyn.validation.framework.ValidationTestCase;
import org.alljoyn.validation.framework.ValidationTestGroup;
import org.alljoyn.validation.framework.ValidationTestItem;
import org.alljoyn.validation.framework.ValidationTestSuite;
import org.alljoyn.validation.framework.annotation.Disabled;
import org.alljoyn.validation.framework.annotation.ValidationSuite;
import org.alljoyn.validation.framework.annotation.ValidationTest;
import org.alljoyn.validation.testing.utils.ValidationTestComparator;
import org.alljoyn.validation.testing.utils.about.AboutAnnouncementDetails;
import org.alljoyn.validation.testing.utils.log.Logger;
import org.alljoyn.validation.testing.utils.log.LoggerFactory;

public abstract class BaseTestSuiteManager implements ValidationTestSuite
{
    private static final String TAG = "BaseTestSuiteManager";
    private static final Logger logger = LoggerFactory.getLogger(TAG);

    @Override
    public List<ValidationTestGroup> getApplicableTests(AllJoynAnnouncedDevice allJoynAnnouncedDevice)
    {
        List<ValidationTestGroup> testGroups = new ArrayList<ValidationTestGroup>();

        for (AboutAnnouncement aboutAnnouncement : allJoynAnnouncedDevice.getAnnouncements())
        {
            AboutAnnouncementDetails aboutAnnouncementDetails = getAboutAnnouncementDetails(aboutAnnouncement);

            if (addTestGroupForApplication(aboutAnnouncementDetails))
            {
                if (addTestGroupWithPathInformation())
                {
                    testGroups.addAll(createTestGroupsWithPathInformation(aboutAnnouncementDetails));
                }
                else
                {
                    testGroups.add(createTestGroup(aboutAnnouncementDetails));
                }
            }
        }

        return testGroups;
    }

    protected String getTestGroupIdFromClass(Class<? extends ValidationTestCase> clazz)
    {
        ValidationSuite validationSuite = clazz.getAnnotation(ValidationSuite.class);
        return validationSuite.name();
    }

    protected AboutAnnouncementDetails getAboutAnnouncementDetails(AboutAnnouncement aboutAnnouncement)
    {
        AboutAnnouncementDetails aboutAnnouncementDetails = new AboutAnnouncementDetails(aboutAnnouncement);
        try
        {
            aboutAnnouncementDetails.convertAboutMap();
        }
        catch (BusException e)
        {
            logger.debug("Exception convertingAboutMap", e);
        }
        return aboutAnnouncementDetails;
    }

    protected void addTestItemToGroup(ValidationTestGroup testGroup, String testCaseId, String testSuiteClassName, String methodName, boolean selectedInitially,
            long timeoutInMilliseconds)
    {
        ValidationTestItem testItem;
        if (timeoutInMilliseconds >= 0)
        {
            testItem = new ValidationTestItem(testCaseId, testSuiteClassName, methodName, timeoutInMilliseconds);
        }
        else
        {
            testItem = new ValidationTestItem(testCaseId, testSuiteClassName, methodName);
        }
        testItem.setInitiallySelected(selectedInitially);
        testGroup.addTestItem(testItem);
    }

    protected void addTestItemToGroup(ValidationTestGroup testGroup, String testCaseId, String testSuiteClassName, String methodName)
    {
        addTestItemToGroup(testGroup, testCaseId, testSuiteClassName, methodName, true, -1);
    }

    protected void addTestItemToGroup(ValidationTestGroup testGroup, String testCaseId, String testSuiteClassName, String methodName, long timeoutInMilliseconds)
    {
        ValidationTestItem testItem = new ValidationTestItem(testCaseId, testSuiteClassName, methodName, timeoutInMilliseconds);
        testGroup.addTestItem(testItem);
    }

    protected void addTestItemsToGroupFromAnnotations(ValidationTestGroup testGroup, Class<? extends ValidationTestCase> clazz)
    {
        Method[] methods = clazz.getMethods();
        List<Method> methodList = new ArrayList<Method>();
        for (Method method : methods)
        {
            ValidationTest validationTest = method.getAnnotation(ValidationTest.class);
            Disabled disabled = method.getAnnotation(Disabled.class);

            if ((validationTest != null) && (disabled == null) && method.getName().startsWith("test"))
            {
                methodList.add(method);
            }
        }

        Collections.sort(methodList, ValidationTestComparator.getInstance());

        for (Method method : methodList)
        {
            ValidationTest validationTest = method.getAnnotation(ValidationTest.class);
            Disabled disabled = method.getAnnotation(Disabled.class);

            if ((validationTest != null) && (disabled == null))
            {
                addTestItemToGroup(testGroup, validationTest.name(), clazz.getName(), method.getName(), isSelectedInitially(), validationTest.timeout());
            }
        }
    }

    protected boolean isSelectedInitially()
    {
        return true;
    }

    protected boolean addTestGroupWithPathInformation()
    {
        return false;
    }

    protected List<ValidationTestGroup> createTestGroupsWithPathInformation(AboutAnnouncementDetails aboutAnnouncement)
    {
        return null;
    }

    protected boolean addTestGroupForApplication(AboutAnnouncementDetails aboutAnnouncement)
    {
        return false;
    }

    protected ValidationTestGroup createTestGroup(AboutAnnouncementDetails aboutAnnouncement)
    {
        Class<? extends ValidationTestCase> clazz = getTestSuiteClass();

        ValidationTestGroup testGroup = new ValidationTestGroup(getTestGroupIdFromClass(clazz), aboutAnnouncement);

        addTestItemsToGroupFromAnnotations(testGroup, clazz);

        return testGroup;
    }

    protected abstract Class<? extends ValidationTestCase> getTestSuiteClass();

}