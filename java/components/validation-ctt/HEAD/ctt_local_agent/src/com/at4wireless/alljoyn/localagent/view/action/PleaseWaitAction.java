/*******************************************************************************
 *   *  Copyright (c) Open Connectivity Foundation (OCF) and AllJoyn Open
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