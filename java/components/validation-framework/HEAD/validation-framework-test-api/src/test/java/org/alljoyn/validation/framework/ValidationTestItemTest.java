package org.alljoyn.validation.framework;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;

import org.alljoyn.validation.framework.ValidationTestGroup;
import org.alljoyn.validation.framework.ValidationTestItem;
import org.junit.Test;

public class ValidationTestItemTest
{
    private static final int TIMEOUT_IN_MILLISECONDS = 500;
    private static final int DEFAULT_TIMEOUT_IN_MILLISECONDS = 120000;
    private static final String METHOD_NAME = "methodName";
    private static final String CLASS_NAME = "className";
    private static final String TEST_CASE_ID = "testCaseId";

    @Test
    public void constructWithoutTimeout()
    {
        ValidationTestItem validationTestItem = new ValidationTestItem(TEST_CASE_ID, CLASS_NAME, METHOD_NAME);

        validate(validationTestItem, DEFAULT_TIMEOUT_IN_MILLISECONDS);
        assertNull(validationTestItem.getTestGroup());
    }

    @Test
    public void constructWithTimeout()
    {
        ValidationTestItem validationTestItem = new ValidationTestItem(TEST_CASE_ID, CLASS_NAME, METHOD_NAME, TIMEOUT_IN_MILLISECONDS);

        validate(validationTestItem, TIMEOUT_IN_MILLISECONDS);
        assertNull(validationTestItem.getTestGroup());
    }

    @Test
    public void setTestGroup()
    {
        ValidationTestGroup testGroup = mock(ValidationTestGroup.class);
        ValidationTestItem validationTestItem = new ValidationTestItem(TEST_CASE_ID, CLASS_NAME, METHOD_NAME, TIMEOUT_IN_MILLISECONDS);
        validationTestItem.setTestGroup(testGroup);

        validate(validationTestItem, TIMEOUT_IN_MILLISECONDS);
        assertEquals(testGroup, validationTestItem.getTestGroup());
    }

    private void validate(ValidationTestItem validationTestItem, int timeout)
    {
        assertEquals(TEST_CASE_ID, validationTestItem.getTestCaseId());
        assertEquals(CLASS_NAME, validationTestItem.getClassName());
        assertEquals(METHOD_NAME, validationTestItem.getMethodName());
        assertEquals(timeout, validationTestItem.getTimeoutInMilliseconds());
    }
}