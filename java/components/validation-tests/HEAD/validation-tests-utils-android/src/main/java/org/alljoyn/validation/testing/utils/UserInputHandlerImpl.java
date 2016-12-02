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

import java.util.concurrent.LinkedBlockingDeque;

import org.alljoyn.validation.framework.UserInputDetails;
import org.alljoyn.validation.framework.UserInputHandler;
import org.alljoyn.validation.framework.UserResponse;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class UserInputHandlerImpl extends Handler implements UserInputHandler
{

    public static final int WAIT_FOR_INPUT_DIALOG = 0;
    private static final String TAG = "UserInputHandlerImpl";
    private Activity activity;

    private LinkedBlockingDeque<UserResponse> userResponses = new LinkedBlockingDeque<UserResponse>();

    public UserInputHandlerImpl(Activity activity)
    {
        super();
        this.activity = activity;
    }

    @Override
    public void handleMessage(Message msg)
    {
        switch (msg.what)
        {
        case WAIT_FOR_INPUT_DIALOG:
            Log.d(TAG, "inside input dialog handle message");

            UserInputDetails userInputDetails = (UserInputDetails) msg.obj;
            continuePopup(userInputDetails);
            break;
        }
    }

    public void onActivityStoppped()
    {
        ExtendedUserResponse userResponse = new ExtendedUserResponse();
        userResponse.setActivityStopping(true);
        userResponses.add(userResponse);
    }

    public void continuePopup(UserInputDetails userInputDetails)
    {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        if (userInputDetails.getMessage() != null)
        {
            builder.setMessage(userInputDetails.getMessage());
        }

        String positiveButton = userInputDetails.getPositiveButton();
        if (positiveButton != null)
        {
            builder.setPositiveButton(positiveButton, new DialogInterface.OnClickListener()
            {

                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    ExtendedUserResponse userResponse = new ExtendedUserResponse();
                    userResponse.setButtonPressed(UserResponse.POSITIVE_BUTTON);
                    userResponses.add(userResponse);
                }

            });
        }
        String negativeButton = userInputDetails.getNegativeButton();

        if (negativeButton != null)
        {
            builder.setNegativeButton(negativeButton, new DialogInterface.OnClickListener()
            {

                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    UserResponse userResponse = new ExtendedUserResponse();
                    userResponse.setButtonPressed(UserResponse.NEGATIVE_BUTTON);
                    userResponses.add(userResponse);
                }

            });
        }

        String[] options = userInputDetails.getOptions();

        if (options != null)
        {
            builder.setItems(options, new DialogInterface.OnClickListener()
            {

                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    UserResponse userResponse = new ExtendedUserResponse();
                    userResponse.setOptionSelected(which);
                    userResponses.add(userResponse);
                }

            });
        }

        String neutralButton = userInputDetails.getNeutralButton();

        if (neutralButton != null)
        {
            builder.setNeutralButton(neutralButton, new DialogInterface.OnClickListener()
            {

                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    UserResponse userResponse = new ExtendedUserResponse();
                    userResponse.setButtonPressed(UserResponse.NEUTRAL_BUTTON);
                    userResponses.add(userResponse);
                }

            });
        }

        String title = userInputDetails.getTitle();
        if (title.length() > 0)
        {
            builder.setTitle(title);
        }

        builder.setOnCancelListener(new DialogInterface.OnCancelListener()
        {

            @Override
            public void onCancel(DialogInterface dialog)
            {
                UserResponse userResponse = new ExtendedUserResponse();
                userResponse.setButtonPressed(UserResponse.NO_BUTTON);
                userResponses.add(userResponse);

            }
        });

        AlertDialog alert = builder.create();

        alert.show();

    }

    @Override
    public UserResponse waitForUserInput(UserInputDetails userInputDetails) throws InterruptedException
    {
        userResponses.clear();
        Message replyMsg = obtainMessage(WAIT_FOR_INPUT_DIALOG, userInputDetails);
        sendMessage(replyMsg);
        return userResponses.take();
    }

}