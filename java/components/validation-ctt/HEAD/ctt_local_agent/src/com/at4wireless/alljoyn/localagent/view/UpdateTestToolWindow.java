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
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingWorker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.at4wireless.alljoyn.localagent.controller.UpdateTestToolController;
import com.at4wireless.alljoyn.localagent.model.common.ConfigParameter;
import com.at4wireless.alljoyn.localagent.model.common.ModelCommons;
import com.at4wireless.alljoyn.localagent.view.action.PleaseWaitAction;
import com.at4wireless.alljoyn.localagent.view.common.ViewCommons;
import com.at4wireless.alljoyn.localagent.view.renderer.ImagePanel;

@SuppressWarnings("serial")
public class UpdateTestToolWindow extends JDialog
{
	private static final Logger logger = LogManager.getLogger(UpdateTestToolWindow.class.getName());
	private static final int UPDATE_WINDOW_WIDTH = 650;
	private static final int UPDATE_WINDOW_HEIGHT = 318;
	private static final int UPDATE_TEXT_WIDTH = 300;
	private static final int UPDATE_TEXT_HEIGHT = 55;
	private static final String HEADER = "header.jpg";
	private static final String SHORT_FOOTER = "short_footer.jpg";
	private static final String BACK_BUTTON = "back.jpg";
	private static final String INSTALL_BUTTON = "install.jpg";
	
	private UpdateTestToolController updateTestToolController;
	private JFrame parentFrame;
	private String sessionToken;
	
	public UpdateTestToolWindow(JFrame parentFrame, String testToolLocalVersion, String testToolServerVersion, String sessionToken)
	{
		super(parentFrame, "CTT Local Agent update", true);
		
		this.updateTestToolController = new UpdateTestToolController();
		this.parentFrame = parentFrame;
		this.sessionToken = sessionToken;
		
		setLayout(null);
		setBounds(ViewCommons.setCenteredRectangle(UPDATE_WINDOW_WIDTH, UPDATE_WINDOW_HEIGHT));
		setResizable(false);
		getContentPane().setBackground(Color.WHITE);
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				logger.info("Closing application...");
				System.exit(0);
				//dispose(); //This could be useful if we want to make a working-without-being-updated test tool
			}
		});
		
		drawHeader();
		drawText(testToolLocalVersion, testToolServerVersion);
		drawBackButton();
		drawInstallButton();
		drawFooter();
	}
	
	private void drawHeader()
	{
		Image headerImage = ViewCommons.readImageFromFile(ConfigParameter.RESOURCES_PATH, HEADER);
		ImagePanel headerImagePanel = new ImagePanel(headerImage);
		
		headerImagePanel.setBounds(new Rectangle(0, 0, UPDATE_WINDOW_WIDTH, headerImage.getHeight(null)));
		add(headerImagePanel);
	}
	
	private void drawText(String testToolLocalVersion, String testToolServerVersion)
	{
		JTextPane updateTestToolTextPane = new JTextPane();

		updateTestToolTextPane.setEditable(false);
		updateTestToolTextPane.setText(String.format("You need to upgrade the CTT Local Agent."
				+ "\nVersion installed: %s"
				+ "\nNew version: %s", testToolLocalVersion, testToolServerVersion));
		
		JScrollPane windowTextContentScrollPane = new JScrollPane(updateTestToolTextPane);
		windowTextContentScrollPane.setSize(UPDATE_TEXT_HEIGHT, UPDATE_TEXT_WIDTH);
		windowTextContentScrollPane.setBounds(new Rectangle(200, 136, UPDATE_TEXT_WIDTH, UPDATE_TEXT_HEIGHT));
		windowTextContentScrollPane.setBorder(null);
		add(windowTextContentScrollPane);
	}
	
	private void drawFooter()
	{
		Image footerImage = ViewCommons.readImageFromFile(ConfigParameter.RESOURCES_PATH, SHORT_FOOTER);
		ImagePanel footerImagePanel = new ImagePanel(footerImage);
		
		footerImagePanel.setBounds(new Rectangle(0, UPDATE_WINDOW_HEIGHT - 126, UPDATE_WINDOW_WIDTH, footerImage.getHeight(null)));
		
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
				logger.info("Closing application...");
				System.exit(0);
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
				logger.info("Downloading new CTT Local Agent version.");
				SwingWorker<Void, Void> mySwingWorker = new SwingWorker<Void, Void>()
				{
					@Override
					protected Void doInBackground() throws Exception
					{
						updateTestToolController.downloadUpdate(sessionToken);
						return null;
					}
				};

				PleaseWaitAction checkingForUpdatesAction = new PleaseWaitAction(parentFrame, mySwingWorker, "Downloading CTT Local Agent, please wait...");
				checkingForUpdatesAction.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null));
				dispose();
				
				JOptionPane.showMessageDialog(null, String.format("CTT Local Agent has been successfully downloaded."
						+ "\nProgram will be closed now to allow you to install the new version."
						+ "\nInstaller location: %s" + File.separator +"%s"
						+ "\nRemember to right-click on the installation package icon and press \"Run as Administrator\"",
						new File("").getAbsolutePath(), ModelCommons.getConfigValue("DownloadPath")));
				
				logger.info("Closing application...");
				System.exit(0);
			}
    	});
    	
		installButton.setBounds(new Rectangle(330, 230, 83, 23));
    	
    	add(installButton);
	}
}