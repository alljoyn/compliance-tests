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
package com.at4wireless.alljoyn.localagent.view.action;

import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractAction;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

public class PleaseWaitAction extends AbstractAction
{
	private static final long serialVersionUID = 1L;
	protected static final long SLEEP_TIME = 3 * 1000;
	private JFrame parentFrame;
	private SwingWorker<?, Void> mySwingWorker;
	private String textToDisplay;

	public PleaseWaitAction(JFrame parent, SwingWorker<?, Void> mySwingWorker, String textToDisplay)
	{
		super("PleaseWaitAction");
		this.parentFrame = parent;
		this.mySwingWorker = mySwingWorker;
		this.textToDisplay = textToDisplay;
	}

	@Override
	public void actionPerformed(ActionEvent evt)
	{
		final JDialog dialog = new JDialog(parentFrame, "CTT Local Agent", ModalityType.APPLICATION_MODAL);
		JProgressBar progressBar = new JProgressBar();

		mySwingWorker.addPropertyChangeListener(new PropertyChangeListener()
		{
			@Override
			public void propertyChange(PropertyChangeEvent evt)
			{
				if (evt.getPropertyName().equals("state"))
				{
					if (evt.getNewValue() == SwingWorker.StateValue.DONE)
					{
						dialog.dispose();
					}
				}
			}
		});
		
		mySwingWorker.execute();

		progressBar.setIndeterminate(true);
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(progressBar, BorderLayout.CENTER);
		panel.add(new JLabel(textToDisplay), BorderLayout.PAGE_START);
		dialog.add(panel);
		dialog.pack();
		dialog.setLocationRelativeTo(parentFrame);
		dialog.setVisible(true);
   }
}