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
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.SocketTimeoutException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.SAXException;

import com.at4wireless.alljoyn.localagent.controller.TestCaseController;
import com.at4wireless.alljoyn.localagent.controller.exception.SendLogException;
import com.at4wireless.alljoyn.localagent.controller.exception.SendResultsException;
import com.at4wireless.alljoyn.localagent.controller.reader.TestCaseConsole;
import com.at4wireless.alljoyn.localagent.model.common.ConfigParameter;
import com.at4wireless.alljoyn.localagent.view.common.ViewCommons;
import com.at4wireless.alljoyn.localagent.view.renderer.ImagePanel;

@SuppressWarnings("serial")
public class ExecuteTestCaseWindow extends JDialog
{
	private static final Logger logger = LogManager.getLogger(ExecuteTestCaseWindow.class.getName());
	private static final int LOG_WINDOW_WIDTH = 900;
	private static final int LOG_WINDOW_HEIGHT = 600;
	private static final int LOG_CONTENT_PANE_WIDTH = 850;
	private static final int LOG_CONTENT_PANE_HEIGHT = 338;
	private static final String HEADER = "header.jpg";
	private static final String SHORT_FOOTER = "short_footer.jpg";
	private static final String STOP_BUTTON = "ico_stop.jpg";
	private static final String SAVE_BUTTON = "save.jpg";
	
	private TestCasesDetailsWindow testCasesDetailsWindow;
	private LoginWindow loginWindow;
	private TestCaseController testCaseController;
	private int projectId;
	private String testCaseName;
	private String certificationRelease;
	private boolean runningAll;
	
	public ExecuteTestCaseWindow(JFrame parentFrame, TestCasesDetailsWindow testCasesDetailsWindow, LoginWindow loginWindow, String certificationRelease, 
			int projectId, String testCaseName, boolean runningAll)
	{
		super(parentFrame, String.format("Running Test Case %s", testCaseName), true);
		
		this.testCasesDetailsWindow = testCasesDetailsWindow;
		this.loginWindow = loginWindow;
		this.testCaseController = testCasesDetailsWindow.getTestCaseController();
		this.projectId = projectId;
		this.testCaseName = testCaseName;
		this.certificationRelease = certificationRelease;
		this.runningAll = runningAll;
		
		setModal(true);
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
		drawLogContent();
		drawStopButton();
		drawFooter();
	}
	
	private void drawHeader()
	{
		Image headerImage = ViewCommons.readImageFromFile(ConfigParameter.RESOURCES_PATH, HEADER);
		ImagePanel headerImagePanel = new ImagePanel(headerImage);
		
		headerImagePanel.setBounds(new Rectangle(0, 0, LOG_WINDOW_WIDTH, headerImage.getHeight(null)));
		
		add(headerImagePanel);
	}
	
	public void drawLogContent()
	{
		JTextArea logContentPane = new JTextArea();
		JScrollPane logContentScrollPane;
		
		logContentPane.setEditable(false);
		logContentPane.setFont(new JTextPane().getFont()); //JTextPane and JTextArea have different Font size. This is done to set the same size to execution and result display
		logContentPane.setLineWrap(true);
		logContentPane.setWrapStyleWord(true);
		
		logContentScrollPane = new JScrollPane(logContentPane);
		logContentScrollPane.setBorder(null);
		logContentScrollPane.setBounds(new Rectangle(LOG_WINDOW_WIDTH - LOG_CONTENT_PANE_WIDTH - 5, 136, LOG_CONTENT_PANE_WIDTH, LOG_CONTENT_PANE_HEIGHT));
		
		add(logContentScrollPane);
		
		TestCaseConsole.redirectOutput(logContentPane, ExecuteTestCaseWindow.this, runningAll);
		runTestCase("c++");
	}
	
	public void destroyTestCaseProcess()
	{
		testCaseController.destroyTestCaseProcess();
	}
	
	public void runTestCase(String language)
	{
		try
		{
			testCaseController.runTestCase(certificationRelease, testCaseName, language);
		}
		catch (IOException e)
		{
			logger.warn("Malformed command to create the Test Cases Package process");
			JOptionPane.showMessageDialog(ExecuteTestCaseWindow.this, "An error occurred trying to execute the Test Case.");
			System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
			System.setErr(new PrintStream(new FileOutputStream(FileDescriptor.err)));
		}
	}
	
	private void drawFooter()
	{
		Image footerImage = ViewCommons.readImageFromFile(ConfigParameter.RESOURCES_PATH, SHORT_FOOTER);
		ImagePanel footerImagePanel = new ImagePanel(footerImage);
		
		footerImagePanel.setBounds(new Rectangle(0, 474, LOG_WINDOW_WIDTH, footerImage.getHeight(null)));
		
		add(footerImagePanel);
	}
	
	public void drawStopButton()
	{
		JButton backButton = new JButton();
		backButton.setIcon(new ImageIcon(ViewCommons.readImageFromFile(ConfigParameter.RESOURCES_PATH, STOP_BUTTON)));
	
    	backButton.addActionListener(new ActionListener()
    	{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				Object[] yesNoOptions = { "Yes", "No" };
				int yesNoReply = JOptionPane.showOptionDialog(null, "Do you want to stop the execution?", "",
				    JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, 
				    yesNoOptions, yesNoOptions[0]);
				
				if (yesNoReply == 0)
				{
					logger.info("Execution cancelled");
					
					runningAll = false;
					testCaseController.destroyTestCaseProcess();
					dispose();
				}
			}
    	});
    	
    	int xPosition = (LOG_WINDOW_WIDTH - 83)/2;
    	int yPosition = 511;
    	backButton.setBounds(new Rectangle(xPosition, yPosition, 83, 23));
    	
    	add(backButton);
	}
	
	public void drawSaveButton()
	{
		JButton saveButton = (JButton) getContentPane().getComponent(2);
		
		saveButton.setIcon(new ImageIcon(ViewCommons.readImageFromFile(ConfigParameter.RESOURCES_PATH, SAVE_BUTTON)));
		
		saveButton.removeActionListener(saveButton.getActionListeners()[0]);
		saveButton.addActionListener(new ActionListener()
    	{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				logger.info("Saving execution results");
				saveButtonAction();
			}
    	});
	}
	
	public void saveButtonAction()
	{
		System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
		System.setErr(new PrintStream(new FileOutputStream(FileDescriptor.err)));
		
		List<String> resultVerdictAndDatetime = new ArrayList<String>();
		try
		{
			resultVerdictAndDatetime = testCaseController.getResultVerdictAndDatetime(testCaseName);
		}
		catch (SAXException | IOException | ParserConfigurationException e)
		{
			logger.warn("Cannot open the execution results file");
			JOptionPane.showMessageDialog(ExecuteTestCaseWindow.this, "A problem occurred getting execution results.");
			return;
		}
		
		try
		{
			testCaseController.sendResultsToServer(loginWindow.getAuthenticatedUser(), loginWindow.getSessionToken(), 
					loginWindow.getCipherKey(), projectId, testCaseName, 
					((JTextArea) ((JScrollPane) getContentPane().getComponent(1)).getViewport().getView()).getText());
			testCasesDetailsWindow.updateVerdictAndDatetime(testCaseName, resultVerdictAndDatetime.get(0), resultVerdictAndDatetime.get(1));
			dispose();
		}
		catch (SocketTimeoutException e)
		{
			logger.error("Timeout waiting for response from the server");
			JOptionPane.showMessageDialog(ExecuteTestCaseWindow.this, "Server does not respond."
					+ " Log will be sent the next time.");
			saveEncryptedLogFile();
		}
		catch (SendResultsException e)
		{
			logger.error("Not possible to send results XML to the server");
			JOptionPane.showMessageDialog(ExecuteTestCaseWindow.this, "Not possible to send results to the server. "
					+ " Log will be sent the next time.");
			saveEncryptedLogFile();
		}
		catch (SendLogException e)
		{
			logger.error("Not possible to send log to the server");
			JOptionPane.showMessageDialog(ExecuteTestCaseWindow.this, "Not possible to send log to the server. "
					+ " Log will be sent the next time.");
			saveEncryptedLogFile();
		}
		catch (URISyntaxException e)
		{
			logger.warn("Malformed Url");
			JOptionPane.showMessageDialog(ExecuteTestCaseWindow.this, "Wrong Url. Please check that your configuration file is correct.");
		}
		catch (SAXException | IOException | ParserConfigurationException e)
		{
			logger.error("Cannot open the execution results file");
			JOptionPane.showMessageDialog(ExecuteTestCaseWindow.this, "A problem occurred while getting execution results."
					+ " Log cannot be sent or stored locally.");
		}
		catch (GeneralSecurityException e)
		{
			logger.error("Cannot encrypt the log file");
			JOptionPane.showMessageDialog(ExecuteTestCaseWindow.this, "A problem occurred while encrypting log."
					+ " It cannot be sent or stored locally.");
		}
	}
	
	private void saveEncryptedLogFile()
	{
		try
		{
			testCaseController.saveEncryptedLogFile(loginWindow.getCipherKey(), projectId, testCaseName, 
			((JTextArea) ((JScrollPane) getContentPane().getComponent(1)).getViewport().getView()).getText());
		}
		catch (SAXException | IOException | ParserConfigurationException e)
		{
			logger.error("Cannot open the execution results file");
			JOptionPane.showMessageDialog(ExecuteTestCaseWindow.this, "A problem occurred while getting execution results."
					+ " Log cannot be sent or stored locally.");
		}
		catch (GeneralSecurityException e)
		{
			logger.error("Cannot encrypt the log file");
			JOptionPane.showMessageDialog(ExecuteTestCaseWindow.this, "A problem occurred while encrypting log."
					+ " It cannot be sent or stored locally.");
		}
	}
	
	public boolean isStopped()
	{
		return !runningAll;
	}
}