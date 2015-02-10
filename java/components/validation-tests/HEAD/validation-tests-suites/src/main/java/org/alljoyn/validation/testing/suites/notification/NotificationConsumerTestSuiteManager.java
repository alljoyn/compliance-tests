/*******************************************************************************
 *  Copyright AllSeen Alliance. All rights reserved.
 *
 *     Permission to use, copy, modify, and/or distribute this software for any
 *     purpose with or without fee is hereby granted, provided that the above
 *     copyright notice and this permission notice appear in all copies.
 *
 *     THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 *     WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 *     MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 *     ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 *     WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 *     ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 *     OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
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