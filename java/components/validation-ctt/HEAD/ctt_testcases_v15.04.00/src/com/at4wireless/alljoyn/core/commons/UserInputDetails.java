/*
 * Copyright AllSeen Alliance. All rights reserved.
 *
 *    Permission to use, copy, modify, and/or distribute this software for any
 *    purpose with or without fee is hereby granted, provided that the above
 *    copyright notice and this permission notice appear in all copies.
 *
 *    THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 *    WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 *    MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 *    ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 *    WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 *    ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 *    OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */
package com.at4wireless.alljoyn.core.commons;



import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

// TODO: Auto-generated Javadoc
/**
 * The Class UserInputDetails.
 */
public class UserInputDetails {

/** The selected. */
int selected;
	
	/**
	 * Instantiates a new user input details.
	 *
	 * @param message the message
	 * @param msgArray the msg array
	 */
	public UserInputDetails(String message, String[] msgArray) {
				
		
		// selected=JOptionPane.showOptionDialog(null, message, null, JOptionPane.INFORMATION_MESSAGE, 0, null, msgArray, null);
	
		// String[] options = new String[] {"Yes", "No", "Maybe", "Cancel"};
		 selected = JOptionPane.showOptionDialog(null, message, "Title",
		        JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
		        null, msgArray, msgArray[0]);
		 
	}
	
	/**
	 * Instantiates a new user input details.
	 *
	 * @param title the title
	 * @param message the message
	 * @param button the button
	 */
	public UserInputDetails(String title, String message, String[] button) {
		
		//JOptionPane.showConfirmDialog(null, message, title, JOptionPane.PLAIN_MESSAGE);
		
	//	String[] options = new String[] {"Yes", "No"};
		 selected = JOptionPane.showOptionDialog(null, message, title,
		        JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
		        null, button, button[0]);
	}
	
	 /**
 	 * Gets the option selected.
 	 *
 	 * @return the option selected
 	 */
 	public int getOptionSelected(){
		
		
		return selected;
	}
}