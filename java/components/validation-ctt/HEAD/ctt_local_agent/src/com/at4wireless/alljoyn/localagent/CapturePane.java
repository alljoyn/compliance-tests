/*******************************************************************************
 *  Copyright AllSeen Alliance. All rights reserved.
 *
 *     Permission to use, copy, modify, and/or distribute this software for any
 *     purpose with or without fee is hereby granted, provided that the above
 *     copyright notice and this permission notice appear in all copies.
 *
 *     THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 *     WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 *     MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 *     ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 *     WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 *     ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 *     OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 *******************************************************************************/
package com.at4wireless.alljoyn.localagent;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

/**
 * Appends text to the log frame
 */
public class CapturePane extends JPanel implements Consumer
{
	/** Serializable version number */
	private static final long serialVersionUID = 4899250979148720613L;
	
	/** Text output */
	private JTextPane output;
	
	/** stored content to be returned and used inside the application */
	String log = "";
	
	/** Test Cases Window instance to return to */
	private TestCasesWindow testCasesWindow;
	
	/** State of the execution: true if executing, false otherwise */
	private Boolean execution = false;

	/**
	 * Instantiates a new capture pane.
	 *
	 * @param	testCasesWin	test cases windows to associate with results sending
	 */
	public CapturePane(TestCasesWindow testCasesWin)
	{
		this.testCasesWindow=testCasesWin;
		this.output = new JTextPane();
		
		setLayout(new BorderLayout());
		output.setEditable(false);

		JScrollPane scrollPanel = new JScrollPane(output);
		scrollPanel.setEnabled(false);
		add(scrollPanel);
	}

	/* (non-Javadoc)
	 * @see com.at4wireless.alljoyn.localagent.Consumer#appendText(java.lang.String)
	 */
	@Override
	public void appendText(final String text)
	{
		if (EventQueue.isDispatchThread())
		{
			log += text;
			
			if(text.contains("started"))
			{
				execution = true;
			}
			
			if(execution)
			{
				StyledDocument document = (StyledDocument) output.getDocument();
			    try
			    {
					document.insertString(document.getLength(), text, null);
				} 
			    catch (BadLocationException e)
			    {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (text.contains(":Final Verdict:"))
				{
					testCasesWindow.enableSendResults();
				}
			}
			else
			{
				output.setText("");
			}
		}
		else
		{
			EventQueue.invokeLater(new Runnable()
			{
				@Override
				public void run()
				{
					appendText(text);
					repaint();
				}
			});
		}
	}

	/**
	 * Returns the stored text
	 *
	 * @return	logged text
	 */
	public String getLog()
	{
		return log;
	}        

	/**
	 * Clears the stored text
	 */
	public void clear()
	{
		log = "";
		execution = false;
		output = null; 
	}
}