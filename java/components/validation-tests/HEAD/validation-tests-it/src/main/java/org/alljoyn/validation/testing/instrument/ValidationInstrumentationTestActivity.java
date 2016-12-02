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
package org.alljoyn.validation.testing.instrument;

import org.alljoyn.validation.framework.UserInputHandler;
import org.alljoyn.validation.testing.utils.UserInputHandlerImpl;

import android.R;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class ValidationInstrumentationTestActivity extends Activity
{
    private UserInputHandler userInputHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Log.d("ValidationInstrumentationTestActivity", "onCreate");
        setContentView(R.layout.select_dialog_item);
        userInputHandler = new UserInputHandlerImpl(this);
    }

    public UserInputHandler getUserInputHandler()
    {
        return userInputHandler;
    }
}