/*******************************************************************************
 *  Copyright (c) 2013 - 2014, AllSeen Alliance. All rights reserved.
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
package org.alljoyn.validation.testing.instrument;

import java.util.UUID;

import junit.framework.TestCase;

import org.alljoyn.validation.framework.AppUnderTestDetails;
import org.alljoyn.validation.framework.TestCaseNoteListener;
import org.alljoyn.validation.framework.UserInputDetails;
import org.alljoyn.validation.framework.UserInputHandler;
import org.alljoyn.validation.framework.UserResponse;
import org.alljoyn.validation.framework.ValidationBaseTestCase;
import org.alljoyn.validation.framework.ValidationTestCase;
import org.alljoyn.validation.testing.ValidationTestContextImpl;

import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

public class InstrumentationTestCaseWrapper extends ActivityInstrumentationTestCase2<ValidationInstrumentationTestActivity>
{
    protected ValidationBaseTestCase testCase;

    public InstrumentationTestCaseWrapper(Class<? extends TestCase> clazz, String methodName) throws Exception
    {
        super(ValidationInstrumentationTestActivity.class);

        if (!ValidationTestCase.class.isAssignableFrom(clazz))
        {
            throw new RuntimeException("Class must implement ValidationTestCase");
        }

        testCase = (ValidationBaseTestCase) clazz.newInstance();
        testCase.setName(methodName);
        setName(methodName);
    }

    @Override
    protected void setUp() throws Exception
    {
        Log.d("InstrumentationTest", "Setting up test");
        String absolutePath = getInstrumentation().getContext().getFileStreamPath("alljoyn_keystore").getAbsolutePath();
        final ValidationInstrumentationApplication instrumentApp = (ValidationInstrumentationApplication) getInstrumentation().getContext().getApplicationContext();

        ValidationTestContextImpl testContext = null;
        boolean useGui = Boolean.parseBoolean(instrumentApp.getInstrumentParameter(InstrumentationArgKey.EnableInteractive.getValue()));

        if (useGui)
        {
            testContext = createTestContextWhichUsesGuiToHandleUserInput();
        }
        else
        {
            testContext = createTestContext(instrumentApp);
        }

        for (InstrumentationArgKey argKey : InstrumentationArgKey.values())
        {
            String paramkey = argKey.getValue();
            String paramValue = instrumentApp.getInstrumentParameter(paramkey);
            testContext.setTestParameter(paramkey, paramValue);
        }

        testContext.setAppUnderTestDetails(new AppUnderTestDetails(UUID.fromString(instrumentApp.getInstrumentParameter(InstrumentationArgKey.AppId.getValue())), instrumentApp
                .getInstrumentParameter(InstrumentationArgKey.DeviceId.getValue())));

        testContext.setContext(getInstrumentation().getContext());
        testContext.setKeyStorePath(absolutePath);
        testContext.setTestCaseNoteListener(new TestCaseNoteListener()
        {
            @Override
            public void addNote(String note)
            {
                Log.d("InstrumentationTest", String.format("Note added: %s", note));
            }
        });

        testCase.setValidationTestContext(testContext);
    }

    @Override
    protected void runTest() throws Throwable
    {
        testCase.runBare();
    }

    private ValidationTestContextImpl createTestContext(final ValidationInstrumentationApplication instrumentApp)
    {
        ValidationTestContextImpl testContext = new ValidationTestContextImpl()
        {
            @Override
            public UserResponse waitForUserInput(UserInputDetails userInputDetails) throws InterruptedException
            {
                String userInputTimeoutValueInMS = instrumentApp.getInstrumentParameter(InstrumentationArgKey.UserInputTimeoutValueInMS.getValue());
                Long timeoutValue = 5000l;

                try
                {
                    timeoutValue = Long.valueOf(userInputTimeoutValueInMS);
                }
                catch (NumberFormatException e)
                {
                    Log.w("error on parsing userInputTimeoutValueInMS.", e);
                }

                Thread.sleep(timeoutValue);

                return new UserResponse();
            }
        };

        return testContext;
    }

    private ValidationTestContextImpl createTestContextWhichUsesGuiToHandleUserInput()
    {
        final UserInputHandler userInputHandler = getActivity().getUserInputHandler();

        ValidationTestContextImpl testContext = new ValidationTestContextImpl()
        {
            @Override
            public UserResponse waitForUserInput(UserInputDetails userInputDetails) throws InterruptedException
            {
                return userInputHandler.waitForUserInput(userInputDetails);
            }
        };

        return testContext;
    }
}