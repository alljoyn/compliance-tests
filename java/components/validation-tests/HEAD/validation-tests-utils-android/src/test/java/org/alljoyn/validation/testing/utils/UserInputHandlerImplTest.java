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

import static org.junit.Assert.assertEquals;

import org.alljoyn.validation.framework.UserInputDetails;
import org.alljoyn.validation.framework.UserInputHandler;
import org.alljoyn.validation.framework.UserResponse;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowAlertDialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

@RunWith(MyRobolectricTestRunner.class)
@Config(manifest = "AndroidManifest.xml")
public class UserInputHandlerImplTest
{

    Activity activity;
    UserInputHandler dialogMessageHandler;

    @Before
    public void setup()
    {
        activity = Robolectric.buildActivity(Activity.class).create().start().resume().get();

        dialogMessageHandler = new UserInputHandlerImpl(activity);

    }

    @Test
    @Ignore
    public void waitForUserInputPositiveButton() throws InterruptedException
    {
        UserInputDetails userInputDetails = Mockito.mock(UserInputDetails.class);
        Mockito.when(userInputDetails.getPositiveButton()).thenReturn("Positive Button");
        Mockito.when(userInputDetails.getMessage()).thenReturn("Message");
        Mockito.when(userInputDetails.getTitle()).thenReturn("Title");

        UserResponse userResponse = dialogMessageHandler.waitForUserInput(userInputDetails);
        AlertDialog dialog = ShadowAlertDialog.getLatestAlertDialog();

        ShadowAlertDialog shadowOfDialog = Robolectric.shadowOf(dialog);
        assertEquals(shadowOfDialog.getMessage(), "Positive Message");
        assertEquals(shadowOfDialog.getTitle(), "Title");

        dialog.getButton(DialogInterface.BUTTON_POSITIVE).performClick();

        assertEquals(UserResponse.POSITIVE_BUTTON, userResponse.getButtonPressed());
    }

    @Test
    @Ignore
    public void waitForUserInputNegativeButton() throws InterruptedException
    {
        UserInputDetails userInputDetails = Mockito.mock(UserInputDetails.class);
        Mockito.when(userInputDetails.getPositiveButton()).thenReturn("Negative Button");
        Mockito.when(userInputDetails.getMessage()).thenReturn("Negative Message");

        UserResponse userResponse = dialogMessageHandler.waitForUserInput(userInputDetails);
        AlertDialog dialog = ShadowAlertDialog.getLatestAlertDialog();

        ShadowAlertDialog shadowOfDialog = Robolectric.shadowOf(dialog);
        assertEquals(shadowOfDialog.getMessage(), "Negative Message");

        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).performClick();

        assertEquals(UserResponse.NEGATIVE_BUTTON, userResponse.getButtonPressed());
    }

    @Test
    @Ignore
    public void waitForUserInputNeutralButton() throws InterruptedException
    {
        UserInputDetails userInputDetails = Mockito.mock(UserInputDetails.class);
        Mockito.when(userInputDetails.getPositiveButton()).thenReturn("Neutral Button");
        Mockito.when(userInputDetails.getMessage()).thenReturn("Neutral Message");

        UserResponse userResponse = dialogMessageHandler.waitForUserInput(userInputDetails);
        AlertDialog dialog = ShadowAlertDialog.getLatestAlertDialog();

        ShadowAlertDialog shadowOfDialog = Robolectric.shadowOf(dialog);
        assertEquals(shadowOfDialog.getMessage(), "Neutral Message");

        dialog.getButton(DialogInterface.BUTTON_NEUTRAL).performClick();

        assertEquals(UserResponse.NEUTRAL_BUTTON, userResponse.getButtonPressed());
    }

}