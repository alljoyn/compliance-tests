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