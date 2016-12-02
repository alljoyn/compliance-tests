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
package com.at4wireless.alljoyn.core.commons;

import javax.swing.JOptionPane;

public class UserInputDetails
{
	int selected;
	
	public UserInputDetails(String message, String[] msgArray)
	{
		 selected = JOptionPane.showOptionDialog(null, message, "Title",
		        JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
		        null, msgArray, msgArray[0]);
	}
	
	public UserInputDetails(String title, String message, String[] button)
	{
		 selected = JOptionPane.showOptionDialog(null, message, title,
		        JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
		        null, button, button[0]);
	}
	
 	public int getOptionSelected()
 	{
		return selected;
	}
}