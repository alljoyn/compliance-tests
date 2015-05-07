package com.at4wireless.alljoyn.core.iop;

import java.awt.Dimension;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import com.at4wireless.alljoyn.core.commons.log.WindowsLoggerImpl;

public class IOPMessage {
	
	WindowsLoggerImpl logger=null;
	
	
	
	public IOPMessage(WindowsLoggerImpl logger) {
		this.logger=logger;
	}


	public  void showMessage(String title,String msg) {
		logger.debug(msg);
		
	
		JTextPane textPane = new JTextPane();
		textPane.setText(msg);
		
		textPane.setSize(new Dimension(480, 10));
		textPane.setPreferredSize(new Dimension(480, 150));
		JScrollPane scroll=new JScrollPane(textPane);
		JOptionPane.showOptionDialog(null, 
				scroll,
			    title, 
		        JOptionPane.PLAIN_MESSAGE, 
		        JOptionPane.PLAIN_MESSAGE, 
		        null, 
		        new String[]{"Next"},
		        "default");
		
		
	}
	
	
	public  int showQuestion(String title, String msg) {
		logger.debug(msg);
		
		JTextPane textPane = new JTextPane();
		textPane.setText(msg);
		
		textPane.setSize(new Dimension(480, 10));
		textPane.setPreferredSize(new Dimension(480, 150));
		JScrollPane scroll=new JScrollPane(textPane);
		int resp = JOptionPane.showOptionDialog(null, 
				scroll,
			    title, 
		        JOptionPane.YES_NO_OPTION, 
		        JOptionPane.QUESTION_MESSAGE, 
		        null, 
		        new String[]{"Yes","No"}, // this is the array
		        "default");
	
			
		if(resp==0){
			logger.debug("response:Yes");
		}else{
			logger.debug("response:No");
		}
		return resp;
	}

}
