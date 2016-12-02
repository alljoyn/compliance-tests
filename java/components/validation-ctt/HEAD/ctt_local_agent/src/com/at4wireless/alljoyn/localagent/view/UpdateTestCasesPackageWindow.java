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
package com.at4wireless.alljoyn.localagent.view;

import java.awt.Color;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingWorker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.at4wireless.alljoyn.localagent.controller.UpdateTestCasesController;
import com.at4wireless.alljoyn.localagent.model.common.ConfigParameter;
import com.at4wireless.alljoyn.localagent.view.action.PleaseWaitAction;
import com.at4wireless.alljoyn.localagent.view.common.ViewCommons;
import com.at4wireless.alljoyn.localagent.view.renderer.ImagePanel;

@SuppressWarnings("serial")
public class UpdateTestCasesPackageWindow extends JDialog
{
	private static final Logger logger = LogManager.getLogger(UpdateTestCasesPackageWindow.class.getName());
	private static final int UPDATE_TEST_CASES_WINDOW_WIDTH = 650;
	private static final int UPDATE_TEST_CASES_WINDOW_HEIGHT = 318;
	private static final int UPDATE_TEST_CASES_CONTENT_PANE_WIDTH = 300;
	private static final int UPDATE_TEST_CASES_CONTENT_PANE_HEIGHT = 55;
	private static final String HEADER = "header.jpg";
	private static final String SHORT_FOOTER = "short_footer.jpg";
	private static final String BACK_BUTTON = "back.jpg";
	private static final String INSTALL_BUTTON = "install.jpg";
	
	private JFrame parentFrame;
	private UpdateTestCasesController updateTestCasesController;
	private boolean updateAborted;
	private String sessionToken;
	private String fullTestCasesPackageServerVersion;
	
	public UpdateTestCasesPackageWindow(JFrame parentFrame, boolean isDebugAndReleaseExists, String fullTestCasesPackageServerVersion, String sessionToken)
	{
		super(parentFrame, "CTT Test Cases Package update", true);
		this.parentFrame = parentFrame;
		this.updateTestCasesController = new UpdateTestCasesController();
		this.updateAborted = false;
		this.sessionToken = sessionToken;
		this.fullTestCasesPackageServerVersion = fullTestCasesPackageServerVersion;
		
		setLayout(null);
		setBounds(ViewCommons.setCenteredRectangle(UPDATE_TEST_CASES_WINDOW_WIDTH, UPDATE_TEST_CASES_WINDOW_HEIGHT));
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
		drawText(isDebugAndReleaseExists);
		drawBackButton();
		drawInstallButton();
		drawFooter();
	}
	
	private void drawHeader()
	{
		Image headerImage = ViewCommons.readImageFromFile(ConfigParameter.RESOURCES_PATH, HEADER);
		ImagePanel headerImagePanel = new ImagePanel(headerImage);
		
		headerImagePanel.setBounds(new Rectangle(0, 0, UPDATE_TEST_CASES_WINDOW_WIDTH, 136));
		add(headerImagePanel);
	}
	
	private void drawText(boolean isDebugAndReleaseExists)
	{
		JTextPane updateTestCasesPackageTextPane = new JTextPane();
		updateTestCasesPackageTextPane.setEditable(false);
		
		if (isDebugAndReleaseExists)
		{
			updateTestCasesPackageTextPane.setText(String.format("A release version of Test Cases Package %s is available to download."
					+ "\nPlease click on Install to upgrade from debug to release.", fullTestCasesPackageServerVersion));
		}
		else
		{
			updateTestCasesPackageTextPane.setText(String.format("The installation of the Test Cases Package %s is needed."
					+ "\nPlease click on Install to continue", fullTestCasesPackageServerVersion));
		}
		
		JScrollPane windowTextContentScrollPane = new JScrollPane(updateTestCasesPackageTextPane);
		windowTextContentScrollPane.setSize(UPDATE_TEST_CASES_CONTENT_PANE_HEIGHT, UPDATE_TEST_CASES_CONTENT_PANE_WIDTH);
		windowTextContentScrollPane.setBounds(new Rectangle(200, 136, UPDATE_TEST_CASES_CONTENT_PANE_WIDTH, UPDATE_TEST_CASES_CONTENT_PANE_HEIGHT));
		windowTextContentScrollPane.setBorder(null);
		add(windowTextContentScrollPane);
	}
	
	private void drawFooter()
	{
		Image footerImage = ViewCommons.readImageFromFile(ConfigParameter.RESOURCES_PATH, SHORT_FOOTER);
		ImagePanel footerImagePanel = new ImagePanel(footerImage);
		
		footerImagePanel.setBounds(new Rectangle(0, UPDATE_TEST_CASES_WINDOW_HEIGHT - 126, UPDATE_TEST_CASES_WINDOW_WIDTH, 99));
		
		add(footerImagePanel);
	}
	
	public void drawBackButton()
	{
		JButton backButton = new JButton();
		backButton.setIcon(new ImageIcon(ViewCommons.readImageFromFile(ConfigParameter.RESOURCES_PATH, BACK_BUTTON)));
	
    	backButton.addActionListener(new ActionListener()
    	{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				logger.info("Update aborted");
				
				updateAborted = true;
				dispose();
			}
    	});
    	
    	backButton.setBounds(new Rectangle(237, 230, 83, 23));
    	
    	add(backButton);
	}
	
	public void drawInstallButton()
	{
		JButton installButton = new JButton();
		installButton.setIcon(new ImageIcon(ViewCommons.readImageFromFile(ConfigParameter.RESOURCES_PATH, INSTALL_BUTTON)));
	
		installButton.addActionListener(new ActionListener()
    	{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				installButtonAction();
			}
    	});
    	
		installButton.setBounds(new Rectangle(330, 230, 83, 23));
    	
    	add(installButton);
	}
	
	public void installButtonAction()
	{
		logger.info(String.format("Installing %s.", fullTestCasesPackageServerVersion));
		
		SwingWorker<Void, Void> mySwingWorker = new SwingWorker<Void, Void>()
		{
			@Override
			protected Void doInBackground() throws Exception
			{
				try
				{
					updateTestCasesController.downloadUpdate(sessionToken, fullTestCasesPackageServerVersion);
				}
				catch (Exception e)
				{
					// A new message should be displayed alerting about the error in the download
					updateAborted = true;
				}
				return null;
			}
		};

		PleaseWaitAction checkingForUpdatesAction = new PleaseWaitAction(parentFrame, mySwingWorker, "Downloading Test Cases Package, please wait...");
		checkingForUpdatesAction.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null));
		dispose();
	}
	
	public boolean isUpdateAborted()
	{
		return updateAborted;
	}
}