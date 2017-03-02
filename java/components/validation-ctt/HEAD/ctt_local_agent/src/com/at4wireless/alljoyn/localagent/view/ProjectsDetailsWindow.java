/*******************************************************************************
 *     Copyright (c) Open Connectivity Foundation (OCF), AllJoyn Open Source
 *     Project (AJOSP) Contributors and others.
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
 *     THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL
 *     WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED
 *     WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE
 *     AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL
 *     DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR
 *     PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER
 *     TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 *     PERFORMANCE OF THIS SOFTWARE.
*******************************************************************************/
package com.at4wireless.alljoyn.localagent.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.apache.http.client.ClientProtocolException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;

import com.at4wireless.alljoyn.localagent.Main;
import com.at4wireless.alljoyn.localagent.controller.ProjectController;
import com.at4wireless.alljoyn.localagent.model.common.ConfigParameter;
import com.at4wireless.alljoyn.localagent.view.action.PleaseWaitAction;
import com.at4wireless.alljoyn.localagent.view.common.ViewCommons;
import com.at4wireless.alljoyn.localagent.view.custom.CustomTable;
import com.at4wireless.alljoyn.localagent.view.renderer.ImagePanel;
import com.at4wireless.alljoyn.localagent.view.renderer.ResultsButtonRenderer;

@SuppressWarnings("serial")
public class ProjectsDetailsWindow extends JPanel
{
	private static final Logger logger = LogManager.getLogger(ProjectsDetailsWindow.class.getName());
	private static final String NEXT_BUTTON = "ico_next.png";
	private static final String REFRESH_BUTTON = "ico_refresh.png";
	private static final String SHORT_FOOTER = "short_footer.jpg";
	private static final String HEADER = "header.jpg";
	private static final String projectsTableColumns[] = {"ProjectId", "Project Name", "Type", "Created", "Modified",
		"Certification Release", "Associated DUT", "Associated GU", "Configured", "Results"};
	
	private LoginWindow loginWindow;
	private TestCasesDetailsWindow testCaseWindow;
	private ResultsDetailsWindow resultsWindow;
	private ProjectController projectController;
	private int selectedProjectId;
	private String selectedProjectCr;
	
	public ProjectsDetailsWindow(LoginWindow loginWindow)
	{	
		this.loginWindow = loginWindow;
		this.resultsWindow = new ResultsDetailsWindow(this, this.loginWindow);
		this.projectController = new ProjectController();
		this.testCaseWindow = new TestCasesDetailsWindow(ProjectsDetailsWindow.this, loginWindow);
	}
	
	public void draw()
	{
		setLayout(null);
		setBounds(new Rectangle(0, 0, ((JFrame) SwingUtilities.getWindowAncestor(this)).getContentPane().getWidth(),
				((JFrame) SwingUtilities.getWindowAncestor(this)).getContentPane().getHeight()));
		drawHeader();
		JScrollPane whiteScrollPane = new JScrollPane(new JTable());
		whiteScrollPane.getViewport().setBackground(Color.WHITE);
		add(whiteScrollPane);
		add(new JButton());
		add(new JButton());
		drawLabels();
		drawFooter();
		drawProjectsTable();
		drawNextButton();
		drawRefreshButton();
	}
	
	private void drawHeader()
	{
		Image headerImage = ViewCommons.readImageFromFile(ConfigParameter.RESOURCES_PATH, HEADER);
		ImagePanel headerImagePanel = new ImagePanel(headerImage);
		
		headerImagePanel.setBounds(new Rectangle(0, 0, headerImage.getWidth(null), headerImage.getHeight(null)));
		add(headerImagePanel);
	}
	
	public boolean drawProjectsTable()
	{
		Object[][] projectsTableContent = null;
		
		logger.info("Retrieving projects information from Web Server");
		
		SwingWorker<Object[][], Void> mySwingWorker = new SwingWorker<Object[][], Void>()
		{
			@Override
			protected Object[][] doInBackground() throws Exception
			{
				Object[][] projectsTableContent = null;
				
				projectsTableContent = projectController.getProjectsListFromServer(loginWindow.getAuthenticatedUser(), 
						loginWindow.getSessionToken());
				
				return projectsTableContent;
			}
		};

		PleaseWaitAction checkingForUpdatesAction = new PleaseWaitAction((JFrame) SwingUtilities.getWindowAncestor(ProjectsDetailsWindow.this),
				mySwingWorker, "Retrieving projects information, please wait...");
		checkingForUpdatesAction.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null));
		
		try
		{
			projectsTableContent = mySwingWorker.get();
		}
		catch (InterruptedException e)
		{
			logger.warn("SwingWorker was interrupted");
			JOptionPane.showMessageDialog(ProjectsDetailsWindow.this, "Program execution was interrupted, please click on refresh to try again.");
			return false;
		}
		catch (ExecutionException e)
		{
			if (e.getCause() instanceof SocketTimeoutException)
			{
				logger.warn("Timeout waiting for response from the server");
				JOptionPane.showMessageDialog(ProjectsDetailsWindow.this, "Server does not respond, please click on refresh to try again.");
				return false;
			}
		}
		
		if (projectsTableContent != null)
		{
			TableModel noEditableCellsModel = new DefaultTableModel(projectsTableColumns, projectsTableContent.length)
			{
				private static final long serialVersionUID = -5114222498322422255L;
	
				public boolean isCellEditable(int row, int column)
				{
					return false;
				}
			};
			
			CustomTable table = new CustomTable(noEditableCellsModel);
			
			for (int i = 0; i < projectsTableContent.length; i++)
			{
				for (int j = 0; j < projectsTableContent[0].length; j++)
				{
					table.setValueAt(projectsTableContent[i][j], i, j);
				}
			}
			
			table.removeColumn(table.getColumnModel().getColumn(0));
			
			table.addMouseListener(new MouseAdapter()
			{
				public void mouseReleased(MouseEvent e)
				{
					JTable target = (JTable)e.getSource();
					int row = target.getSelectedRow();
					int column = target.getSelectedColumn();
					
					selectedProjectId = (int) target.getModel().getValueAt(table.convertRowIndexToModel(target.getSelectedRow()), 0);
					selectedProjectCr = (String) target.getModel().getValueAt(table.convertRowIndexToModel(target.getSelectedRow()), 5);
					
					if ((column == 8) && ((Boolean) table.getValueAt(row, column)))
					{
						try
						{
							resultsWindow.drawResultsTable(selectedProjectId);
						}
						catch (ClientProtocolException e1)
						{
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						catch (URISyntaxException e1)
						{
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						catch (IOException e1)
						{
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						catch (JSONException e1)
						{
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						catch (InterruptedException e1)
						{
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						catch (ExecutionException e1)
						{
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						
						resultsWindow.drawBackButton();
						getComponent(3).setVisible(false);
					}
					else
					{
						getComponent(2).setEnabled(((String) table.getValueAt(row, 7)).equals("Yes"));
					}
				}
			});
			
			TableRowSorter<TableModel> sorter = new TableRowSorter<>(table.getModel());
			sorter.setSortable(9, false);
			
			table.setRowSorter(sorter);
			table.setBorder(null);
			
			TableColumn resultsColumn = table.getColumn("Results");
			resultsColumn.setCellRenderer(new ResultsButtonRenderer());
			resultsColumn.setMaxWidth(85);
			resultsColumn.setMinWidth(85);
			
			JScrollPane scrollPane = (JScrollPane) getComponent(1);
			scrollPane.setBounds(new Rectangle(0, getComponent(0).getHeight(), getWidth(), getHeight() - getComponent(0).getHeight() - getComponent(6).getHeight()));
			scrollPane.setViewportView(table);
		}
		
		return true;
	}
	
	private void drawFooter()
	{
		Image footerImage = ViewCommons.readImageFromFile(ConfigParameter.RESOURCES_PATH, SHORT_FOOTER);
		ImagePanel footerImagePanel = new ImagePanel(footerImage);
		int yPosition = getHeight() - footerImage.getHeight(null) + 12;
		
		footerImagePanel.setBounds(new Rectangle(0, yPosition, footerImage.getWidth(null), footerImage.getHeight(null)));
		
		add(footerImagePanel);
	}
	
	public void drawNextButton()
    {
		JButton nextButton = (JButton) getComponent(2);
    	
    	nextButton.setIcon(new ImageIcon(ViewCommons.readImageFromFile(ConfigParameter.RESOURCES_PATH, NEXT_BUTTON)));

    	if (nextButton.getActionListeners().length > 0)
    	{
    		nextButton.removeActionListener(nextButton.getActionListeners()[0]);
    	}
    	nextButton.addActionListener(new ActionListener()
    	{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				boolean continueToTestCasesWindow = false;
				
				try
				{
					continueToTestCasesWindow = checkForTestCasesPackageUpdates();
				}
				catch (InterruptedException e1)
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				catch (ExecutionException e1)
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				if (continueToTestCasesWindow)
				{
					logger.info(String.format("Test Cases Package version is updated"));
					SwingWorker<Void, Void> mySwingWorker = new SwingWorker<Void, Void>()
					{
						@Override
						protected Void doInBackground() throws Exception
						{
							testCaseWindow.drawTestCasesTable(selectedProjectId, selectedProjectCr);
							testCaseWindow.drawBackButton();
							testCaseWindow.drawRunAllButton();
							
							return null;
						}
					};

					PleaseWaitAction drawingTestCasesAction = new PleaseWaitAction((JFrame) SwingUtilities.getWindowAncestor(ProjectsDetailsWindow.this), 
							mySwingWorker, String.format("Retrieving project's Test Cases from server, please wait..."));
					drawingTestCasesAction.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null));
					
				}
			}
    	});
    	
    	nextButton.setBounds(new Rectangle(530, 411, 76, 24));
    	nextButton.setEnabled(false);
    }
	
	public void drawRefreshButton()
	{
		JButton refreshButton = (JButton) getComponent(3);
    	
    	refreshButton.setIcon(new ImageIcon(ViewCommons.readImageFromFile(ConfigParameter.RESOURCES_PATH, REFRESH_BUTTON)));

    	if (refreshButton.getActionListeners().length > 0)
    	{
    		refreshButton.removeActionListener(refreshButton.getActionListeners()[0]);
    	}
    	refreshButton.setBounds(new Rectangle(417, 411, 103, 24));
    	refreshButton.addActionListener(new ActionListener()
    	{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				drawProjectsTable();
				selectedProjectId = 0;
				getComponent(2).setEnabled(false);
			}
    	});
    }
	
	private void drawLabels()
    {
    	JLabel versionLabel = new JLabel(Main.VERSION_LABEL);
    	JLabel poweredLabel = new JLabel(Main.POWERED_BY_LABEL);
    	
    	versionLabel.setFont(versionLabel.getFont().deriveFont(Font.PLAIN, versionLabel.getFont().getSize() - 1));
		versionLabel.setForeground(new Color(79, 80, 80));
		versionLabel.setBounds(new Rectangle(815, 425, 200, 23));
		
		poweredLabel.setFont(poweredLabel.getFont().deriveFont(Font.PLAIN, poweredLabel.getFont().getSize() - 1));
		poweredLabel.setForeground(new Color(79, 80, 80));
		poweredLabel.setBounds(new Rectangle(840, 440, 200, 23));
		
		add(versionLabel);
		add(poweredLabel);
    }
	
	private boolean checkForTestCasesPackageUpdates() throws InterruptedException, ExecutionException
	{	
		boolean continueToTestCasesWindow = true;
		
		logger.info("Checking if Test Cases Package version is updated");
		
		SwingWorker<List<Object>, Void> mySwingWorker = new SwingWorker<List<Object>, Void>()
		{
			@Override
			protected List<Object> doInBackground() throws Exception
			{
				boolean isTestCasesPackageInstalled = projectController.isTestCasesPackageInstalled(selectedProjectCr);
				List<Object> serverResponse = projectController.isTestCasesPackageVersionUpdated(loginWindow.getSessionToken(), selectedProjectCr);
				boolean isDebugAndReleaseExists = projectController.isDebugAndReleaseExists(loginWindow.getSessionToken(), selectedProjectCr);
				List<Object> checkingResults = new ArrayList<Object>();
				
				checkingResults.add(isTestCasesPackageInstalled);
				checkingResults.addAll(serverResponse);
				checkingResults.add(isDebugAndReleaseExists);
				
				return checkingResults;
			}
		};

		PleaseWaitAction checkingForUpdatesAction = new PleaseWaitAction((JFrame) SwingUtilities.getWindowAncestor(this), mySwingWorker, 
				String.format("Looking for CTT Test Cases Package %s updates, please wait...", selectedProjectCr));
		checkingForUpdatesAction.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null));
		
		List<Object> checkingForUpdatesResults = mySwingWorker.get();

		if ((Boolean) checkingForUpdatesResults.get(0))
		{
			logger.info(String.format("A version of Test Cases package %s is installed.", selectedProjectCr));

			if (!(Boolean) checkingForUpdatesResults.get(1))
			{
				logger.info(String.format("Test Cases package %s has an update.", selectedProjectCr));
				
				UpdateTestCasesPackageWindow updateTestCasesPackageWindow = new UpdateTestCasesPackageWindow(
						(JFrame) SwingUtilities.getWindowAncestor(ProjectsDetailsWindow.this), true,
						selectedProjectCr + "_" + checkingForUpdatesResults.get(2), loginWindow.getSessionToken());

				if (!(Boolean) checkingForUpdatesResults.get(3))
				{
					updateTestCasesPackageWindow.installButtonAction();
				}
				else
				{
					updateTestCasesPackageWindow.setVisible(true);
					
					if (updateTestCasesPackageWindow.isUpdateAborted())
					{
						continueToTestCasesWindow = false;
					}
				}
			}
		}
		else
		{
			logger.info(String.format("No version of Test Cases package %s installed.", selectedProjectCr));
			
			UpdateTestCasesPackageWindow updateTestCasesPackageWindow = new UpdateTestCasesPackageWindow(
					(JFrame) SwingUtilities.getWindowAncestor(ProjectsDetailsWindow.this), false, 
					selectedProjectCr + "_" + checkingForUpdatesResults.get(2), loginWindow.getSessionToken());
			
			updateTestCasesPackageWindow.setVisible(true);

			if (updateTestCasesPackageWindow.isUpdateAborted())
			{
				continueToTestCasesWindow = false;
			}
		}
		
		return continueToTestCasesWindow;
	}
}