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
package com.at4wireless.alljoyn.localagent.view;

import java.awt.Color;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;

import javax.crypto.SecretKey;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.at4wireless.alljoyn.localagent.controller.LogController;
import com.at4wireless.alljoyn.localagent.model.common.ConfigParameter;
import com.at4wireless.alljoyn.localagent.view.common.ViewCommons;
import com.at4wireless.alljoyn.localagent.view.renderer.ImagePanel;

@SuppressWarnings("serial")
public class LogContentWindow extends JDialog
{
	private static final Logger logger = LogManager.getLogger(LogContentWindow.class.getName());
	private static final int LOG_WINDOW_WIDTH = 900;
	private static final int LOG_WINDOW_HEIGHT = 600;
	private static final int LOG_CONTENT_PANE_WIDTH = 850;
	private static final int LOG_CONTENT_PANE_HEIGHT = 338;
	private static final String HEADER = "header.jpg";
	private static final String SHORT_FOOTER = "short_footer.jpg";
	private static final String BACK_BUTTON = "back.jpg";
	
	private LogController logController;
	
	public LogContentWindow(JFrame parent, String authenticatedUser, String sessionToken, int projectId, String testCaseName, String logFileName, SecretKey cipherKey)
	{
		super(parent, String.format("Log of Test Case %s", testCaseName), false);
		logController = new LogController();
		
		setLayout(null);
		setBounds(ViewCommons.setCenteredRectangle(LOG_WINDOW_WIDTH, LOG_WINDOW_HEIGHT));
		setResizable(false);
		getContentPane().setBackground(Color.WHITE);
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				dispose();
			}
		});
		
		drawHeader();
		drawLogContent(authenticatedUser, sessionToken, projectId, logFileName, cipherKey);
		drawBackButton();
		drawFooter();
	}
	
	private void drawHeader()
	{
		Image headerImage = ViewCommons.readImageFromFile(ConfigParameter.RESOURCES_PATH, HEADER);
		ImagePanel headerImagePanel = new ImagePanel(headerImage);
		
		headerImagePanel.setBounds(new Rectangle(0, 0, LOG_WINDOW_WIDTH, headerImage.getHeight(null)));
		add(headerImagePanel);
	}
	
	private void drawLogContent(String authenticatedUser, String sessionToken, int projectId, String logFileName, SecretKey cipherKey)
	{
		JTextPane logContentPane = new JTextPane();
		JScrollPane logContentScrollPane;
		
		logger.info("Retrieving log content from Web Server");
		
		try
		{
			logContentPane.setText(logController.getLogContentFromServer(authenticatedUser, sessionToken, projectId, logFileName, cipherKey));
		}
		catch (URISyntaxException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (GeneralSecurityException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logContentPane.setEditable(false);
		
		logContentScrollPane = new JScrollPane(logContentPane);
		logContentScrollPane.setBorder(null);
		logContentScrollPane.setBounds(new Rectangle(LOG_WINDOW_WIDTH - LOG_CONTENT_PANE_WIDTH - 5, 136, LOG_CONTENT_PANE_WIDTH, LOG_CONTENT_PANE_HEIGHT));
		
		add(logContentScrollPane);
	}
	
	private void drawFooter()
	{
		Image footerImage = ViewCommons.readImageFromFile(ConfigParameter.RESOURCES_PATH, SHORT_FOOTER);
		ImagePanel footerImagePanel = new ImagePanel(footerImage);
		
		footerImagePanel.setBounds(new Rectangle(0, 474, LOG_WINDOW_WIDTH, footerImage.getHeight(null)));
		
		add(footerImagePanel);
	}
	
	public void drawBackButton()
	{
		JButton backButton = new JButton();
		int xPosition = (LOG_WINDOW_WIDTH - 83)/2;
		int yPosition = 511;
		
		backButton.setIcon(new ImageIcon(ViewCommons.readImageFromFile(ConfigParameter.RESOURCES_PATH, BACK_BUTTON)));
		backButton.setBounds(new Rectangle(xPosition, yPosition, 83, 23));
    	backButton.addActionListener(new ActionListener()
    	{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				dispose();
			}
    	});
    	
    	add(backButton);
	}
}