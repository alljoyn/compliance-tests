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
 * {@code UserResponse} contains results of an user operation.
 * 
 */
public class UserResponse
{
    public static final int NO_BUTTON = 0;
    public static final int POSITIVE_BUTTON = 1;
    public static final int NEGATIVE_BUTTON = 2;
    public static final int NEUTRAL_BUTTON = 3;
    private int optionSelected = -1;
    private int buttonPressed;

    /**
     * @return number corresponding to the button pressed
     */
    public int getButtonPressed()
    {
        return buttonPressed;
    }

    /**
     * @param buttonPressed
     *            number corresponding to the button pressed
     */
    public void setButtonPressed(int buttonPressed)
    {
        this.buttonPressed = buttonPressed;
    }

    /**
     * @return integer value of the option selected by the user
     */
    public int getOptionSelected()
    {
        return optionSelected;
    }

    /**
     * @param optionSelected
     *            integer value of the option selected by the user
     */
    public void setOptionSelected(int optionSelected)
    {
        this.optionSelected = optionSelected;
    }
}