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
package com.at4wireless.alljoyn.core.iop;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import com.at4wireless.alljoyn.core.commons.log.Logger;
import com.at4wireless.alljoyn.core.commons.log.WindowsLoggerImpl;

public class IOPMessage
{
	private static final Logger logger = new WindowsLoggerImpl(IOPMessage.class.getSimpleName());
	public static final int IOP_MESSAGE_POPUP_WIDTH = 500;
	public static final int IOP_MESSAGE_POPUP_HEIGHT = 200;
	public static final int IOP_MESSAGE_TEXT_WIDTH = 480;
	public static final int IOP_MESSAGE_TEXT_HEIGHT = 150;

	/** The response code 
	 * 		 0	:	Yes 
	 * 		 1	:	No
	 * 		-1	:	No answer selected
	 * */
	int response = -1;

	/** The dialog used to show information. */
	JDialog dialog;

	public IOPMessage()
	{

	}

	public void showMessage(String title, String msg)
	{
		logger.debug(msg);

		JTextPane textPane = new JTextPane();
		textPane.setText(msg);

		textPane.setSize(new Dimension(IOP_MESSAGE_TEXT_WIDTH, IOP_MESSAGE_TEXT_HEIGHT));
		textPane.setPreferredSize(new Dimension(IOP_MESSAGE_TEXT_WIDTH, IOP_MESSAGE_TEXT_HEIGHT));

		textPane.setEditable(false);
		JScrollPane scroll = new JScrollPane(textPane);

		dialog = new JDialog();
		Rectangle bounds = null ;
		Dimension screenDimensions = Toolkit.getDefaultToolkit().getScreenSize();
		bounds = new Rectangle((int) (screenDimensions.width/2) - IOP_MESSAGE_POPUP_WIDTH/2, 
				(int) (screenDimensions.height/2) - IOP_MESSAGE_POPUP_HEIGHT/2,
				IOP_MESSAGE_POPUP_WIDTH, 
				IOP_MESSAGE_POPUP_HEIGHT);
		dialog.setBounds(bounds);
		dialog.setTitle(title);
		dialog.add(scroll, BorderLayout.CENTER);
		dialog.setResizable(false);
		JButton buttonNext = new JButton("Next");
		buttonNext.setForeground(new Color(255, 255, 255));
		buttonNext.setBackground(new Color(68, 140, 178));
		buttonNext.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				dialog.dispose();
			}
		});

		JPanel buttonPanel = new JPanel();
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWeights = new double[]{1.0};
		gridBagLayout.rowWeights = new double[]{1.0};
		buttonPanel.setLayout(gridBagLayout);
		GridBagConstraints gbc_next = new GridBagConstraints();
		gbc_next.gridx = 0;
		gbc_next.gridy = 0;
		gbc_next.anchor=GridBagConstraints.CENTER;
		buttonPanel.add(buttonNext, gbc_next);	
		dialog.add(buttonPanel, BorderLayout.SOUTH);
		dialog.setAlwaysOnTop(true); //<-- this line
		dialog.setModal(true);
		dialog.setResizable(false);
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialog.setVisible(true);
	}

	public int showQuestion(String title, String msg)
	{
		logger.debug(msg);

		JTextPane textPane = new JTextPane();
		textPane.setText(msg);
		textPane.setEditable(false);
		textPane.setSize(new Dimension(IOP_MESSAGE_TEXT_WIDTH, IOP_MESSAGE_TEXT_HEIGHT));
		textPane.setPreferredSize(new Dimension(IOP_MESSAGE_TEXT_WIDTH, IOP_MESSAGE_TEXT_HEIGHT));
		JScrollPane scroll = new JScrollPane(textPane);
		dialog = new JDialog();
		Rectangle bounds = null ;
		Dimension screenDimensions = Toolkit.getDefaultToolkit().getScreenSize();
		bounds = new Rectangle((int) (screenDimensions.width/2) - IOP_MESSAGE_POPUP_WIDTH/2, 
				(int) (screenDimensions.height/2) - IOP_MESSAGE_POPUP_HEIGHT/2,
				IOP_MESSAGE_POPUP_WIDTH, 
				IOP_MESSAGE_POPUP_HEIGHT);
		dialog.setBounds(bounds);
		dialog.setTitle(title);
		dialog.add(scroll,BorderLayout.CENTER);
		dialog.setResizable(false);
		JButton buttonYes = new JButton("Yes");
		buttonYes.setForeground(new Color(255, 255, 255));
		buttonYes.setBackground(new Color(68, 140, 178));
		buttonYes.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				logger.info("response:Yes");
				response = 0;
				dialog.dispose();
			}
		});

		JButton buttonNo = new JButton("No");

		buttonNo.setForeground(new Color(255, 255, 255));
		buttonNo.setBackground(new Color(68, 140, 178));
		buttonNo.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				logger.info("response:No");
				response = 1;
				dialog.dispose();
			}
		});

		JPanel buttonPanel = new JPanel();
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWeights = new double[]{0.5, 0.5};
		gridBagLayout.rowWeights = new double[]{1.0};

		buttonPanel.setLayout(gridBagLayout);

		GridBagConstraints gbc_yes = new GridBagConstraints();
		gbc_yes.gridx = 0;
		gbc_yes.gridy = 0;
		gbc_yes.anchor = GridBagConstraints.EAST;
		buttonPanel.add(buttonYes, gbc_yes);		
		gbc_yes.insets = new Insets(20,0,20,0);
		GridBagConstraints gbc_no = new GridBagConstraints();

		gbc_no.gridx = 1;
		gbc_no.gridy = 0;
		gbc_no.anchor = GridBagConstraints.WEST;
		buttonPanel.add(buttonNo, gbc_no);
		dialog.add(buttonPanel, BorderLayout.SOUTH);
		dialog.setAlwaysOnTop(true); 
		dialog.setModal(true);
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialog.setVisible(true);
		int resp = response;

		return resp;
	}

}