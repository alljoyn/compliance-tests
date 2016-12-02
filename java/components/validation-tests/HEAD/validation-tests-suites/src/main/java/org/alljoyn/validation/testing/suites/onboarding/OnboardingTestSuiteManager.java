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
package org.alljoyn.validation.testing.suites.onboarding;

import org.alljoyn.onboarding.transport.OnboardingTransport;
import org.alljoyn.validation.framework.ValidationTestCase;
import org.alljoyn.validation.framework.ValidationTestSuite;
import org.alljoyn.validation.testing.suites.BaseTestSuiteManager;
import org.alljoyn.validation.testing.utils.about.AboutAnnouncementDetails;

public class OnboardingTestSuiteManager extends BaseTestSuiteManager implements ValidationTestSuite
{
    @Override
    protected Class<? extends ValidationTestCase> getTestSuiteClass()
    {
        return OnboardingTestSuite.class;
    }

    @Override
    protected boolean addTestGroupForApplication(AboutAnnouncementDetails aboutAnnouncement)
    {
        return aboutAnnouncement.supportsInterface(OnboardingTransport.INTERFACE_NAME);
    }
}