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

import org.alljoyn.validation.framework.ValidationBaseTestCase;
import org.alljoyn.validation.testing.utils.log.Logger;
import org.alljoyn.validation.testing.utils.log.LoggerFactory;

public class BaseTestSuite extends ValidationBaseTestCase
{
    private static final String ABOUT_ANNOUNCEMENT_TIMEOUT_PARAMETER_NAME = "aboutAnnouncementTimeoutInSeconds";
    private static final String TAG = "BaseTestSuite";
    private static final Logger logger = LoggerFactory.getLogger(TAG);
    private static final long DEFAULT_ANNOUNCEMENT_TIMEOUT_IN_SECONDS = 60;

    protected long determineAboutAnnouncementTimeout()
    {
        String aboutAnnouncementTimeoutProvided = (String) getValidationTestContext().getTestParameter(ABOUT_ANNOUNCEMENT_TIMEOUT_PARAMETER_NAME);
        long aboutAnnouncementTimeout = DEFAULT_ANNOUNCEMENT_TIMEOUT_IN_SECONDS;

        try
        {
            if (aboutAnnouncementTimeoutProvided != null && !aboutAnnouncementTimeoutProvided.isEmpty())
            {
                logger.debug("About announcement timeout provided: %s seconds", aboutAnnouncementTimeoutProvided);
                aboutAnnouncementTimeout = Long.parseLong(aboutAnnouncementTimeoutProvided);

                if (aboutAnnouncementTimeout < 0)
                {
                    logger.debug("Invalid About announcement timeout value provided");
                    aboutAnnouncementTimeout = DEFAULT_ANNOUNCEMENT_TIMEOUT_IN_SECONDS;
                }
            }
        }
        catch (Exception exception)
        {
            logger.debug("Invalid About announcement timeout value provided");
            aboutAnnouncementTimeout = DEFAULT_ANNOUNCEMENT_TIMEOUT_IN_SECONDS;
        }

        logger.debug("Running test case using About announcement timeout: %s seconds", aboutAnnouncementTimeout);

        return aboutAnnouncementTimeout;
    }
}