/*******************************************************************************
 *  Copyright (c) Open Connectivity Foundation (OCF) and AllJoyn Open
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

/**
 * {@code UserInputDetails} contains information about the input provided by
 * user for an action.
 * 
 */
public class UserInputDetails
{
    private String title;
    private String message;
    private String positiveButton;
    private String negativeButton;
    private String neutralButton;
    private String[] options;

    /**
     * This constructor can be used to present the user with a notification
     * message. For e.g.<br>
     * <br>
     * 
     * {@code new UserInputDetails("Onboard DUT","Please Onboard the device to the Personal AP and then click Continue", "Continue"); }
     * 
     * @param title
     *            the title of the popup box
     * @param message
     *            the message presented to the user
     * @param dismissButton
     *            name of the dismiss button
     */
    public UserInputDetails(String title, String message, String dismissButton)
    {
        this.title = title;
        this.message = message;
        this.positiveButton = dismissButton;
    }

    /**
     * This constructor can be used to present the user with a dialog and ask
     * the user to confirm some event or action. For e.g.<br>
     * <br>
     * 
     * {@code new UserInputDetails("Message Confirmation","Was the message received", "Yes", "No"); }
     * 
     * @param title
     *            the title of the popup box
     * @param message
     *            the message presented to the user
     * @param positiveButton
     *            name of the positive response button
     * @param negativeButton
     *            name of the negative response button
     */
    public UserInputDetails(String title, String message, String positiveButton, String negativeButton)
    {
        this.title = title;
        this.message = message;
        this.positiveButton = positiveButton;
        this.negativeButton = negativeButton;
    }

    /**
     * This constructor can be used to present the user with a dialog and ask
     * the user to confirm some event or action. For e.g.<br>
     * <br>
     * 
     * {@code new UserInputDetails("Message Confirmation","Was the message received", "Yes", "No", "Not Applicable"); }
     * 
     * @param title
     *            the title of the popup box
     * @param message
     *            the message presented to the user
     * @param positiveButton
     *            name of the positive response button
     * @param negativeButton
     *            name of the negative response button
     * @param neutralButton
     *            name of the neutral response button
     */
    public UserInputDetails(String title, String message, String positiveButton, String negativeButton, String neutralButton)
    {
        this.title = title;
        this.message = message;
        this.positiveButton = positiveButton;
        this.negativeButton = negativeButton;
        this.neutralButton = neutralButton;
    }

    /**
     * This constructor can be used to present the user with a dialog and ask
     * the user to select one of the options presented. For e.g.<br>
     * <br>
     * 
     * {@code String[] msgArray = }{{@code "Priority Message 1", "Priority
     * Message 2", "Priority Message 3" } ; <br>
     * {@code new UserInputDetails("Select the message(s) received", msgArray); }
     * 
     * @param title
     *            the title of the popup box
     * @param options
     *            the array of options presented to the user
     */
    public UserInputDetails(String title, String[] options)
    {
        this.title = title;
        this.options = options;
    }

    /**
     * @return the title of the popup box
     */
    public String getTitle()
    {
        return title;
    }

    /**
     * @param title
     *            the title of the popup box
     */
    public void setTitle(String title)
    {
        this.title = title;
    }

    /**
     * @return the message presented to the user
     */
    public String getMessage()
    {
        return message;
    }

    /**
     * @param message
     *            the message presented to the user
     */
    public void setMessage(String message)
    {
        this.message = message;
    }

    /**
     * @return name of the positive response button
     */
    public String getPositiveButton()
    {
        return positiveButton;
    }

    /**
     * @param positiveButton
     *            name of the positive response button, e.g. {@code Ok}
     */
    public void setPositiveButton(String positiveButton)
    {
        this.positiveButton = positiveButton;
    }

    /**
     * @return name of the negative response button
     */
    public String getNegativeButton()
    {
        return negativeButton;
    }

    /**
     * @param negativeButton
     *            name of the negative response button, e.g. {@code Cancel}
     */
    public void setNegativeButton(String negativeButton)
    {
        this.negativeButton = negativeButton;
    }

    /**
     * @return name of the neutral response button
     */
    public String getNeutralButton()
    {
        return neutralButton;
    }

    /**
     * @param neutralButton
     *            name of the neutral response button
     */
    public void setNeutralButton(String neutralButton)
    {
        this.neutralButton = neutralButton;
    }

    /**
     * @return array of options
     */
    public String[] getOptions()
    {
        return options;
    }

    /**
     * @param options
     */
    public void setOptions(String[] options)
    {
        this.options = options;
    }
}