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
package com.at4wireless.alljoyn.localagent;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;


// TODO: Auto-generated Javadoc
/**
 * The Class CapturePane, used to appends text to the logframe.
 */
public class CapturePane extends JPanel implements Consumer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4899250979148720613L;
	
	/** The output. */
	private JTextPane output;
	
	/** The log. */
	String log="";
	
	/** The test cases window. */
	private TestCasesWindow testCasesWindow;
	
	/** The execution. */
	private Boolean execution=false;

	/**
	 * Instantiates a new capture pane.
	 *
	 * @param testCasesWin the test cases win
	 */
	public CapturePane(TestCasesWindow testCasesWin) {
		setLayout(new BorderLayout());

		testCasesWindow=testCasesWin;
		

		output = new JTextPane();


		output.setEditable(false);



		JScrollPane scrollPanel=new JScrollPane(output);

		scrollPanel.setEnabled(false);


		add(scrollPanel);


	}

	/* (non-Javadoc)
	 * @see com.at4wireless.alljoyn.localagent.Consumer#appendText(java.lang.String)
	 */
	@Override
	public void appendText(final String text) {
		if (EventQueue.isDispatchThread()) {


			log=log.concat(text);
			if(text.contains("started")){

				execution=true;
			}




			if(execution){
				output.setText(output.getText().concat(text) );
				
				if(text.contains(":Final Verdict:")){
					
					testCasesWindow.enableSendResults();
					
				}
			
			}else{
				output.setText("");
			}


 

		} else {

			EventQueue.invokeLater(new Runnable() {
				@Override
				public void run() {
					appendText(text);
					repaint();
				}
			});

		}
	}

	/**
	 * Gets the log.
	 *
	 * @return the log
	 */
	public String getLog() {


		
		return log;
	}        

	/**
	 * Delete log.
	 */
	public void deleteLog() {
		log="";
		execution=false;//AMC
		output=new JTextPane();
		return; 
	}    





}