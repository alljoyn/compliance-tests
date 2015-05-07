package com.at4wireless.alljoyn.core.commons;



import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class UserInputDetails {
int selected;
	public UserInputDetails(String message, String[] msgArray) {
				
		
		// selected=JOptionPane.showOptionDialog(null, message, null, JOptionPane.INFORMATION_MESSAGE, 0, null, msgArray, null);
	
		// String[] options = new String[] {"Yes", "No", "Maybe", "Cancel"};
		 selected = JOptionPane.showOptionDialog(null, message, "Title",
		        JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
		        null, msgArray, msgArray[0]);
		 
	}
	
	public UserInputDetails(String title, String message, String[] button) {
		
		//JOptionPane.showConfirmDialog(null, message, title, JOptionPane.PLAIN_MESSAGE);
		
	//	String[] options = new String[] {"Yes", "No"};
		 selected = JOptionPane.showOptionDialog(null, message, title,
		        JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
		        null, button, button[0]);
	}
	
	 public int getOptionSelected(){
		
		
		return selected;
	}
}