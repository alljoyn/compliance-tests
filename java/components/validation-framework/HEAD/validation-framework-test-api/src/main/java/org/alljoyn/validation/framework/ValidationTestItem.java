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

/**
 * This class contains information about a test.
 * 
 */
public class ValidationTestItem
{
    public static final long DEFAULT_TEST_TIMEOUT_IN_MILLISECONDS = 120000;
    private String className;
    private String methodName;
    private String testCaseId;
    private long timeoutInMilliseconds;
    private ValidationTestGroup testGroup;
    private boolean initiallySelected = true;

    /**
     * @param testCaseId
     *            id of the test, e.g. {@code About-v1-01}
     * @param className
     *            name of the test suite class, e.g. {@code AboutTestSuite}
     * @param methodName
     *            name of the method which contains the test, e.g.
     *            {@code testAbout_v1_01_AboutAnnouncement}
     */
    public ValidationTestItem(String testCaseId, String className, String methodName)
    {
        initialize(testCaseId, className, methodName, DEFAULT_TEST_TIMEOUT_IN_MILLISECONDS);
    }

    /**
     * @param testCaseId
     *            id of the test, e.g. {@code About-v1-01}
     * @param className
     *            name of the test suite class, e.g. {@code AboutTestSuite}
     * @param methodName
     *            name of the method which contains the test, e.g.
     *            {@code testAbout_v1_01_AboutAnnouncement}
     * @param timeoutInMilliseconds
     *            maximum time duration after which the test will time out if it
     *            does not complete
     */
    public ValidationTestItem(String testCaseId, String className, String methodName, long timeoutInMilliseconds)
    {
        initialize(testCaseId, className, methodName, timeoutInMilliseconds);
    }

    /**
     * @return name of the test suite class, e.g. {@code AboutTestSuite}
     */
    public String getClassName()
    {
        return className;
    }

    /**
     * @return name of the method which contains the test, e.g.
     *         {@code testAbout_v1_01_AboutAnnouncement}
     */
    public String getMethodName()
    {
        return methodName;
    }

    /**
     * @return id of the test, e.g. {@code About-v1-01}
     */
    public String getTestCaseId()
    {
        return testCaseId;
    }

    /**
     * @return maximum time duration after which the test will time out if it
     *         does not complete
     */
    public long getTimeoutInMilliseconds()
    {
        return timeoutInMilliseconds;
    }

    /**
     * @return {@code ValidationTestGroup} to which the
     *         {@code ValidationTestItem} belongs to
     */
    public ValidationTestGroup getTestGroup()
    {
        return testGroup;
    }

    /**
     * @param testGroup
     *            {@code ValidationTestGroup} to which the
     *            {@code ValidationTestItem} belongs to
     */
    public void setTestGroup(ValidationTestGroup testGroup)
    {
        this.testGroup = testGroup;
    }

    /**
     * @return initiallySelected
     */
    public boolean isInitiallySelected()
    {
        return initiallySelected;
    }

    /**
     * @param initiallySelected
     */
    public void setInitiallySelected(boolean initiallySelected)
    {
        this.initiallySelected = initiallySelected;
    }

    private void initialize(String testCaseId, String className, String methodName, long timeoutInMilliseconds)
    {
        this.testCaseId = testCaseId;
        this.className = className;
        this.methodName = methodName;
        this.timeoutInMilliseconds = timeoutInMilliseconds;
    }
}