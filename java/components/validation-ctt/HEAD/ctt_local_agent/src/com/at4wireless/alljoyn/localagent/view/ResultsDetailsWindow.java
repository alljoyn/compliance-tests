/*******************************************************************************
 *     Copyright (c) Open Connectivity Foundation (OCF) and AllJoyn Open
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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
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

import com.at4wireless.alljoyn.localagent.controller.ResultController;
import com.at4wireless.alljoyn.localagent.model.common.ConfigParameter;
import com.at4wireless.alljoyn.localagent.view.action.PleaseWaitAction;
import com.at4wireless.alljoyn.localagent.view.common.ViewCommons;
import com.at4wireless.alljoyn.localagent.view.custom.CustomTable;
import com.at4wireless.alljoyn.localagent.view.renderer.FullLogButtonRenderer;

public class ResultsDetailsWindow
{
	private static final Logger logger = LogManager.getLogger(ResultsDetailsWindow.class.getName());
	private static final String BACK_BUTTON = "back.jpg";
	private static final Object resultsTableColumns[] = {"Name", "Description", "Date and Time execution", "Certification Release",
			"Final Verdict", "Full Log"};
	
	private ProjectsDetailsWindow projectWindow;
	private LoginWindow loginWindow;
	private ResultController resultController;
	
	public ResultsDetailsWindow(ProjectsDetailsWindow projectWindow, LoginWindow loginWindow)
	{
		this.projectWindow = projectWindow;
		this.loginWindow = loginWindow;
		this.resultController = new ResultController();
	}
	
	public void drawResultsTable(int selectedProjectId) throws ClientProtocolException, URISyntaxException, IOException, 
			JSONException, InterruptedException, ExecutionException
	{
		Object[][] resultsTableContent;
		
		logger.info("Retrieving project results from Web Server.");
		
		SwingWorker<Object[][], Void> mySwingWorker = new SwingWorker<Object[][], Void>()
		{
			@Override
			protected Object[][] doInBackground() throws Exception
			{
				return resultController.getResultsListFromServer(loginWindow.getAuthenticatedUser(), loginWindow.getSessionToken(), selectedProjectId);
			}
		};

		PleaseWaitAction checkingForUpdatesAction = new PleaseWaitAction((JFrame) SwingUtilities.getWindowAncestor(projectWindow), 
				mySwingWorker, "Looking for CTT updates, please wait...");
		checkingForUpdatesAction.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null));
		resultsTableContent = mySwingWorker.get();
		
		JScrollPane resultsScrollPane = (JScrollPane) projectWindow.getComponent(1);
		TableModel noEditableCellsModel = new DefaultTableModel(resultsTableColumns, resultsTableContent.length)
		{			
			private static final long serialVersionUID = -2458127522509467589L;

			public boolean isCellEditable(int row, int column)
			{
				return false;
			}
		};
		
		CustomTable table = new CustomTable(noEditableCellsModel);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		for (int i = 0; i < resultsTableContent.length; i++)
		{
			table.setValueAt(resultsTableContent[i][0], i, 0);
			table.setValueAt(resultsTableContent[i][1], i, 1);
			table.setValueAt(resultsTableContent[i][2], i, 2);
			table.setValueAt(resultsTableContent[i][3], i, 3);
			table.setValueAt(resultsTableContent[i][4], i, 4);
			table.setValueAt(resultsTableContent[i][5], i, 5);
		}
		
		table.addMouseListener(new MouseAdapter()
		{
			public void mouseReleased(MouseEvent e)
			{
				JTable target = (JTable)e.getSource();
				int row = target.getSelectedRow();
				int column = target.getSelectedColumn();
				
				if (column == 5)
				{
					SwingWorker<Void, Void> mySwingWorker = new SwingWorker<Void, Void>()
					{
						@Override
						protected Void doInBackground() throws Exception
						{
							LogContentWindow logContentWindow = new LogContentWindow((JFrame) SwingUtilities.getWindowAncestor(projectWindow), 
									loginWindow.getAuthenticatedUser(), loginWindow.getSessionToken(), selectedProjectId,
									(String) target.getValueAt(row, 0), (String) target.getValueAt(row, column), loginWindow.getCipherKey());
							logContentWindow.setVisible(true);
							return null;
						}
					};

					PleaseWaitAction waitingForLogAction = new PleaseWaitAction((JFrame) SwingUtilities.getWindowAncestor(projectWindow), 
							mySwingWorker, "Retrieving selected log from server, please wait...");
					waitingForLogAction.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null));
				}
			}
		});
		
		TableRowSorter<TableModel> sorter = new TableRowSorter<>(table.getModel());
		sorter.setSortable(5, false);
		
		table.setRowSorter(sorter);
		table.setBorder(null);
		
		TableColumn fullLogColumn = table.getColumn("Full Log");
		fullLogColumn.setCellRenderer(new FullLogButtonRenderer());
		fullLogColumn.setMaxWidth(85);
		fullLogColumn.setMinWidth(85);
		resultsScrollPane.setViewportView(table);
	}
	
	public void drawBackButton()
	{
		JButton backButton = (JButton) projectWindow.getComponent(2);
		backButton.setIcon(new ImageIcon(ViewCommons.readImageFromFile(ConfigParameter.RESOURCES_PATH, BACK_BUTTON)));
	
		backButton.removeActionListener(backButton.getActionListeners()[0]);
    	backButton.addActionListener(new ActionListener()
    	{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if (projectWindow.drawProjectsTable())
				{
					projectWindow.drawNextButton();
					projectWindow.getComponent(3).setVisible(true);
				}			
			}
    	});
    	
    	backButton.setEnabled(true);
	}
}