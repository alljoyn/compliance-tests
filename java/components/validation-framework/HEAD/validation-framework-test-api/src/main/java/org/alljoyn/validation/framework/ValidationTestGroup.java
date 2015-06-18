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
 *      THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL
 *      WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED
 *      WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE
 *      AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL
 *      DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR
 *      PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER
 *      TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 *      PERFORMANCE OF THIS SOFTWARE.
 *******************************************************************************/
package org.alljoyn.validation.framework;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 
 * A {@code ValidationTestGroup} is a collection of tests.
 * 
 */
public class ValidationTestGroup
{
    private String testGroupId;
    private String objectPath;
    private String interfaceName;
    private List<ValidationTestItem> testItems = new ArrayList<ValidationTestItem>();
    private AboutAnnouncement aboutAnnouncement;

    /**
     * @param testGroupId
     *            id of the group, e.g. {@code About-v1}
     * @param aboutAnnouncement
     *            {@link AboutAnnouncement}
     */
    public ValidationTestGroup(String testGroupId, AboutAnnouncement aboutAnnouncement)
    {
        this(testGroupId, aboutAnnouncement, null, null);
    }

    /**
     * @param testGroupId
     *            id of the group, e.g. {@code About-v1}
     * @param aboutAnnouncement
     *            {@link AboutAnnouncement}
     * @param objectPath
     *            path of the object being tested on the Alljoyn bus, e.g.
     *            {@code /About}
     * @param interfaceName
     *            name of the interface being tested, e.g.
     *            {@code org.alljoyn.About}
     */
    public ValidationTestGroup(String testGroupId, AboutAnnouncement aboutAnnouncement, String objectPath, String interfaceName)
    {
        this.testGroupId = testGroupId;
        this.aboutAnnouncement = aboutAnnouncement;
        this.objectPath = objectPath;
        this.interfaceName = interfaceName;
    }

    /**
     * @return id of the group, e.g. {@code About-v1}
     */
    public String getTestGroupId()
    {
        return testGroupId;
    }

    /**
     * @return path of the object being tested on the Alljoyn bus, e.g.
     *         {@code /About}
     */
    public String getObjectPath()
    {
        return objectPath;
    }

    /**
     * @return name of the interface being tested, e.g.
     *         {@code org.alljoyn.About}
     */
    public String getInterfaceName()
    {
        return interfaceName;
    }

    /**
     * @param testItem
     *            {@link ValidationTestItem}
     */
    public void addTestItem(ValidationTestItem testItem)
    {
        testItem.setTestGroup(this);
        testItems.add(testItem);
    }

    /**
     * @return list of {@link ValidationTestItem}
     */
    public List<ValidationTestItem> getTestItems()
    {
        return Collections.unmodifiableList(testItems);
    }

    /**
     * @return {@link AboutAnnouncement}
     */
    public AboutAnnouncement getAboutAnnouncement()
    {
        return aboutAnnouncement;
    }

    /**
     * @param objectPath
     *            path of the object being tested on the Alljoyn bus, e.g.
     *            {@code /About}
     */
    public void setObjectPath(String objectPath)
    {
        this.objectPath = objectPath;
    }
}