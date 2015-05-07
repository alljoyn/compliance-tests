package com.at4wireless.alljoyn.localagent;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

public class CapturePane extends JPanel implements Consumer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4899250979148720613L;
	private JTextPane output;
	String log="";
	private TestCasesWindow testCasesWindow;
	private Boolean execution=false;

	public CapturePane(TestCasesWindow testCasesWin) {
		setLayout(new BorderLayout());

		testCasesWindow=testCasesWin;
		

		output = new JTextPane();


		output.setEditable(false);



		JScrollPane scrollPanel=new JScrollPane(output);

		scrollPanel.setEnabled(false);


		add(scrollPanel);


	}

	@Override
	public void appendText(final String text) {
		if (EventQueue.isDispatchThread()) {


			log=log.concat(text);
			if(text.contains("started")){

				execution=true;
			}




			if(execution){
				output.setText(output.getText().concat(text) );
			}else{
				output.setText("");
			}


 
			if(text.contains(":Final Verdict:")){
				
				testCasesWindow.enableSendResults();
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

	public String getLog() {


		//String log=output.getText();
		return log;
	}        

	public void deleteLog() {
		log="";

		output=new JTextPane();
		return; 
	}    





}