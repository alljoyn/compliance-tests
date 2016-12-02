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

package org.alljoyn.validation.testing.suites.eventsactions;

import org.alljoyn.bus.annotation.BusInterface;
import org.alljoyn.bus.ifaces.AllSeenIntrospectable;
import org.alljoyn.validation.framework.ValidationTestCase;
import org.alljoyn.validation.framework.ValidationTestSuite;
import org.alljoyn.validation.testing.suites.BaseTestSuiteManager;
import org.alljoyn.validation.testing.utils.about.AboutAnnouncementDetails;
import org.alljoyn.validation.testing.utils.log.Logger;
import org.alljoyn.validation.testing.utils.log.LoggerFactory;

public class EventsActionsTestSuiteManager extends BaseTestSuiteManager implements ValidationTestSuite
{

    private static final String TAG = "EventsActionsTestSuiteManager";
    private static final Logger logger = LoggerFactory.getLogger(TAG);

    /**
     * @see org.alljoyn.validation.testing.suites.BaseTestSuiteManager#getTestSuiteClass()
     */
    @Override
    protected Class<? extends ValidationTestCase> getTestSuiteClass()
    {

        return EventsActionsTestSuite.class;
    }

    /**
     * @see org.alljoyn.validation.testing.suites.BaseTestSuiteManager#addTestGroupForApplication(org.alljoyn.validation.testing.utils.about.AboutAnnouncementDetails)
     */
    @Override
    protected boolean addTestGroupForApplication(AboutAnnouncementDetails aboutAnnouncement)
    {

        logger.info("Received announcement deviceId: '%s', appId: '%s', busName: '%s'", aboutAnnouncement.getDeviceId(), aboutAnnouncement.getAppId(),
                aboutAnnouncement.getServiceName());

        // Retrieve the AJ name of the introspection interface
        BusInterface ifaceName = AllSeenIntrospectable.class.getAnnotation(BusInterface.class);
        String ajIfaceName = ifaceName.name();

        return aboutAnnouncement.supportsInterface(ajIfaceName);
    }
}