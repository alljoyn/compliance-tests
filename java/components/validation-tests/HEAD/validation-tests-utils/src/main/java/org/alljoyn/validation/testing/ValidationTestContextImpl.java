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
package org.alljoyn.validation.testing;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alljoyn.validation.framework.AppUnderTestDetails;
import org.alljoyn.validation.framework.InterfaceAttribute;
import org.alljoyn.validation.framework.TestCaseNoteListener;
import org.alljoyn.validation.framework.UserInputDetails;
import org.alljoyn.validation.framework.UserResponse;
import org.alljoyn.validation.framework.ValidationTestContext;

import android.util.Log;

public class ValidationTestContextImpl implements ValidationTestContext
{
    private AppUnderTestDetails appUnderTestDetails;

    public void setAppUnderTestDetails(AppUnderTestDetails appUnderTestDetails)
    {
        this.appUnderTestDetails = appUnderTestDetails;
    }

    private String testObjectPath;
    private Object context;
    private String keyStorePath;
    private Map<String, Object> testParameterMap = new HashMap<String, Object>();

    private TestCaseNoteListener testCaseNoteListener = new TestCaseNoteListener()
    {
        @Override
        public void addNote(String note)
        {
            Log.i("TEST_CASE_NOTE", String.format("Note added: %s", note));
        }
    };

    @Override
    public AppUnderTestDetails getAppUnderTestDetails()
    {
        return appUnderTestDetails;
    }

    @Override
    public String getTestObjectPath()
    {
        return testObjectPath;
    }

    public void setTestObjectPath(String testObjectPath)
    {
        this.testObjectPath = testObjectPath;
    }

    @Override
    public Object getContext()
    {
        return context;
    }

    public void setContext(Object context)
    {
        this.context = context;
    }

    @Override
    public void addNote(String note)
    {
        testCaseNoteListener.addNote(note);
    }

    public TestCaseNoteListener getTestCaseNoteListener()
    {
        return testCaseNoteListener;
    }

    public void setTestCaseNoteListener(TestCaseNoteListener testCaseNoteListener)
    {
        this.testCaseNoteListener = testCaseNoteListener;
    }

    @Override
    public UserResponse waitForUserInput(UserInputDetails userInputDetails) throws InterruptedException
    {
        return new UserResponse();
    }

    @Override
    public void addInterfaceDetails(String interfaceName, short version, String objectPath, String details, List<InterfaceAttribute> attributes)
    {
    }

    @Override
    public String getKeyStorePath()
    {
        return keyStorePath;
    }

    public void setKeyStorePath(String keyStorePath)
    {
        this.keyStorePath = keyStorePath;
    }

    @Override
    public Object getTestParameter(String parameterName)
    {
        return testParameterMap.get(parameterName);
    }

    public void setTestParameter(String parameterName, Object parameterValue)
    {
        testParameterMap.put(parameterName, parameterValue);
    }
}