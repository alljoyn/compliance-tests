/*******************************************************************************
 *  Copyright (c) Open Connectivity Foundation (OCF) and AllJoyn Open
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
import java.awt.Font;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.crypto.SecretKey;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;

import com.at4wireless.alljoyn.localagent.Main;
import com.at4wireless.alljoyn.localagent.controller.LoginController;
import com.at4wireless.alljoyn.localagent.model.common.ConfigParameter;
import com.at4wireless.alljoyn.localagent.view.action.PleaseWaitAction;
import com.at4wireless.alljoyn.localagent.view.common.ViewCommons;
import com.at4wireless.alljoyn.localagent.view.custom.JIconPasswordField;
import com.at4wireless.alljoyn.localagent.view.custom.JIconTextField;
import com.at4wireless.alljoyn.localagent.view.renderer.ImagePanel;
import com.sun.jersey.api.client.ClientHandlerException;

@SuppressWarnings("serial")
public class LoginWindow extends JDialog
{
	private static final Logger logger = LogManager.getLogger(LoginWindow.class.getName());
	private static final int LOGIN_PANEL_WIDTH = 700;
	private static final int LOGIN_PANEL_HEIGHT = 300;
	private static final String LOGIN_BACKGROUND = "MainWindowBackground.jpg";
	private static final String USER_LABEL = "User Name:";
	private static final String USER_ICON = "ico_user.png";
	private static final String PASSWORD_LABEL = "Password:";
	private static final String PASSWORD_ICON = "ico_password.png";
	private static final Color LABELS_COLOR = Color.decode("#1d6482");
	private static final String LOGIN_BUTTON = "ico_login.png";
	private static final String CLOSE_BUTTON = "ico_close.jpg";
	
	private LoginController loginController;
	private JFrame loginInvoker;
	
    public LoginWindow(JFrame parentFrame, boolean modal)
    {
        super(parentFrame, modal);
        
        loginController = new LoginController();
        loginInvoker = parentFrame;
        
        setBounds(ViewCommons.setCenteredRectangle(LOGIN_PANEL_WIDTH, LOGIN_PANEL_HEIGHT));
        setUndecorated(true);
        setContentPane(setLoginWindowBackground());
        createLoginPanel(); 
    }
    
    private ImagePanel setLoginWindowBackground()
    {
    	Image backgroundImage = ViewCommons.readImageFromFile(ConfigParameter.RESOURCES_PATH, LOGIN_BACKGROUND);
    	ImagePanel newContentPane = new ImagePanel(backgroundImage);
    	
    	newContentPane.setLayout(null);
    	
    	return newContentPane;
    }
    
    private void createLoginPanel()
    {
    	drawUserInputField();
    	drawPasswordInputField();
    	drawLoginButton();
    	drawCloseButton();
    	drawFooter();
    }
    
    private void drawUserInputField()
    {
    	JLabel userInputLabel = new JLabel(USER_LABEL);
    	JIconTextField userInputField = new JIconTextField();
    	
    	userInputLabel.setForeground(LABELS_COLOR);
    	userInputLabel.setBounds(new Rectangle(60, 150, 80, 20));
    	
    	userInputField.setBounds(new Rectangle(140, 150, 220, 23));
    	userInputField.setIcon(new ImageIcon(ConfigParameter.RESOURCES_PATH + File.separator + USER_ICON));
    	userInputField.addActionListener( new ActionListener()
    	{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				sendAuthentication();
			}
    		
    	});
    	
    	getContentPane().add(userInputLabel);
    	getContentPane().add(userInputField);
    	
    	this.addWindowListener(new WindowAdapter()
    	{
    		public void windowOpened(WindowEvent e)
    		{
    			userInputField.requestFocus();
    		}
    	});
    }
    
    private void drawPasswordInputField()
    {
    	JLabel passwordInputLabel = new JLabel(PASSWORD_LABEL);
    	JIconPasswordField passwordInputField = new JIconPasswordField();
    	
    	passwordInputLabel.setForeground(LABELS_COLOR);
    	passwordInputLabel.setBounds(new Rectangle(60, 180, 80, 20));
    	
    	passwordInputField.setBounds(new Rectangle(140, 180, 220, 23));
    	passwordInputField.setIcon(new ImageIcon(ConfigParameter.RESOURCES_PATH + File.separator + PASSWORD_ICON));
    	passwordInputField.addActionListener( new ActionListener()
    	{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				sendAuthentication();
			}
    		
    	});
    	
    	getContentPane().add(passwordInputLabel);
    	getContentPane().add(passwordInputField);
    }
    
    private void drawLoginButton()
    {
    	JButton loginButton = new JButton();
    	
    	loginButton.setIcon(new ImageIcon(ViewCommons.readImageFromFile(ConfigParameter.RESOURCES_PATH, LOGIN_BUTTON)));
    	loginButton.addActionListener(new ActionListener()
    	{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				sendAuthentication();
			}
    	});
    	
    	loginButton.setBounds(new Rectangle(250, 220, 83, 23));
    	getContentPane().add(loginButton);
    }
    
	private void sendAuthentication()
    {
    	JIconTextField userInputField = (JIconTextField) getContentPane().getComponent(1);
		JIconPasswordField passwordInputField = (JIconPasswordField) getContentPane().getComponent(3);
		
		logger.info("Authenticating with CTT Web Server...");
		
		try
		{
			if (loginController.loginWithWebServer(userInputField.getText(), String.valueOf(passwordInputField.getPassword())))
			{
				setVisible(false);
				logger.info("Authentication succeeded");
				
				try
				{
					logger.info("Checking if CTT Local Agent is up-to-date");
					checkForTestToolUpdates();
					initialize();	
					loginInvoker.setVisible(true);
				}
				catch (InterruptedException | ExecutionException e)
				{	
					if (e instanceof InterruptedException)
					{
						logger.warn("SwingWorker was interrupted");
					}
					else if (e instanceof ExecutionException)
					{
						Throwable cause = e.getCause();
						
						if (cause instanceof NullPointerException)
						{
							logger.warn("Invalid AES cipher key received from server");
						}
					}
					
					JOptionPane.showMessageDialog(LoginWindow.this, "Cannot initialize CTT Local Agent, please try to authenticate again.");
						
					userInputField.setText(""); //[JTF] This makes re-type every time you try to authenticate. Is it the appropriate behaviour?
					passwordInputField.setText("");
					userInputField.requestFocus();
					
					setVisible(true);
				}
			}
			else
			{
				logger.warn("Incorrect user and/or password");
				JOptionPane.showMessageDialog(LoginWindow.this, "Incorrect user and/or password");
				passwordInputField.requestFocus();
				passwordInputField.selectAll();
			}
		}
		catch (ClientHandlerException e)
		{
			logger.warn("Communication with server failed");
			JOptionPane.showMessageDialog(LoginWindow.this, "Communication with CTT Web Server failed.");
		}
		catch (JSONException e)
		{
			logger.warn("Incorrect response received");
			logger.debug(e.getMessage());
			JOptionPane.showMessageDialog(LoginWindow.this, "Incorrect response received");
		}
    }
    
    private void drawCloseButton()
    {
    	JButton closeButton = new JButton();
    	
    	closeButton.setIcon(new ImageIcon(ViewCommons.readImageFromFile(ConfigParameter.RESOURCES_PATH, CLOSE_BUTTON)));
    	closeButton.setBounds(new Rectangle(157, 220, 83, 23));
    	closeButton.addActionListener(new ActionListener()
    	{
			@Override
			public void actionPerformed(ActionEvent e)
			{	
				logger.info("Closing application...");
				System.exit(0);
			}
    		
    	});
    	
    	getContentPane().add(closeButton);
    }
    
    private void drawFooter()
    {
    	JLabel versionLabel = new JLabel(Main.VERSION_LABEL);
    	JLabel poweredLabel = new JLabel(Main.POWERED_BY_LABEL);
    	
    	versionLabel.setFont(versionLabel.getFont().deriveFont(Font.PLAIN, versionLabel.getFont().getSize() - 1));
		versionLabel.setForeground(new Color(79, 80, 80));
		versionLabel.setBounds(new Rectangle(505, 250, 200, 23));
		
		poweredLabel.setFont(poweredLabel.getFont().deriveFont(Font.PLAIN, poweredLabel.getFont().getSize() - 1));
		poweredLabel.setForeground(new Color(79, 80, 80));
		poweredLabel.setBounds(new Rectangle(530, 265, 200, 23));
		
		getContentPane().add(versionLabel);
		getContentPane().add(poweredLabel);
    }
    
    private void checkForTestToolUpdates() throws InterruptedException, ExecutionException
    {
    	SwingWorker<List<Object>, Void> mySwingWorker = new SwingWorker<List<Object>, Void>()
		{
			@Override
			protected List<Object> doInBackground() throws Exception
			{
				return loginController.isTestToolUpdated(Main.CTT_LOCAL_AGENT_VERSION);
			}
		};

		PleaseWaitAction checkingForUpdatesAction = new PleaseWaitAction(loginInvoker, mySwingWorker, "Looking for CTT updates, please wait...");
		checkingForUpdatesAction.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null));
		
		List<Object> responseFromServer = mySwingWorker.get();
		
		if (!responseFromServer.isEmpty())
		{
			if (!(Boolean) responseFromServer.get(0))
			{
				logger.info("A new version of CTT Local Agent is available");
				UpdateTestToolWindow updateTestToolWindow = new UpdateTestToolWindow(loginInvoker, Main.CTT_LOCAL_AGENT_VERSION,
						(String) responseFromServer.get(1), loginController.getSessionToken());
				updateTestToolWindow.setVisible(true);
			}
			else
			{
				logger.info("CTT Local Agent is updated");
			}
		}
		else
		{
			logger.warn("CTT Local Agent version is newer than server version");
		}
    }
    
    private void initialize() throws InterruptedException, ExecutionException
    {
    	SwingWorker<Void, Void> mySwingWorker = new SwingWorker<Void, Void>()
		{
			@Override
			protected Void doInBackground() throws Exception
			{
				logger.info("Exchanging encryption with Web Server...");
				loginController.exchangeEncryption();
				return null;
			}
		};

		PleaseWaitAction initializingAction = new PleaseWaitAction(loginInvoker, mySwingWorker, 
				"CTT Local Agent is updated. Initializing, please wait...");
		initializingAction.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null));
		
		mySwingWorker.get();
    }
    
    public String getAuthenticatedUser()
    {
    	return loginController.getAuthenticatedUser();
    }
    
    public String getSessionToken()
    {
    	return loginController.getSessionToken();
    }
    
    public SecretKey getCipherKey()
    {
    	return loginController.getCipherKey();
    }
}