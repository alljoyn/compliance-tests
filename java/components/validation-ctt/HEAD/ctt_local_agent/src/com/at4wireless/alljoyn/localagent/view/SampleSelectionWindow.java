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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.SAXException;

import com.at4wireless.alljoyn.localagent.controller.TestCaseController;
import com.at4wireless.alljoyn.localagent.model.common.ConfigParameter;
import com.at4wireless.alljoyn.localagent.view.common.ViewCommons;
import com.at4wireless.alljoyn.localagent.view.custom.CustomTable;
import com.at4wireless.alljoyn.localagent.view.renderer.ImagePanel;

@SuppressWarnings("serial")
public class SampleSelectionWindow extends JDialog
{
	private static final Logger logger = LogManager.getLogger(SampleSelectionWindow.class.getName());
	private static final int SAMPLES_WINDOW_WIDTH = 750;
	private static final int SAMPLES_WINDOW_HEIGHT = 418;
	private static final String NEXT_BUTTON = "ico_next.png";
	private static final String SHORT_FOOTER = "short_footer.jpg";
	private static final String HEADER = "header.jpg";
	private static final String BACK_BUTTON = "back.jpg";
	private static final Object samplesTableColumns[] = {"DeviceId", "AppId", "Software Version", "Hardware Version"};
	
	private TestCaseController testCaseController;
	private boolean cancelled;
	
	public SampleSelectionWindow(JFrame parentFrame, TestCaseController testCaseController, Object[][] samplesTableContent)
	{
		super(parentFrame, "CTT Local Agent", true);
		
		this.testCaseController = testCaseController;
		this.cancelled = false;
	
		setLayout(null);
		setBounds(ViewCommons.setCenteredRectangle(SAMPLES_WINDOW_WIDTH, SAMPLES_WINDOW_HEIGHT));
		setResizable(false);
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		setTitle("Select a sample");
		addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				cancelled = true;
				dispose();
			}
		});
		
		drawHeader();
		drawSamplesTable(samplesTableContent);	
		drawNextButton();
		drawBackButton();
		drawFooter();
	}
	
	private void drawHeader()
	{
		Image headerImage = ViewCommons.readImageFromFile(ConfigParameter.RESOURCES_PATH, HEADER);
		ImagePanel headerImagePanel = new ImagePanel(headerImage);
		
		headerImagePanel.setBounds(new Rectangle(0, 0, SAMPLES_WINDOW_WIDTH, headerImage.getHeight(null)));
		
		add(headerImagePanel);
	}
	
	private void drawSamplesTable(Object[][] samplesTableContent)
	{
		logger.info("Displaying samples to use in the execution");
		
		TableModel noEditableCellsModel = new DefaultTableModel(samplesTableColumns, samplesTableContent.length)
		{
			public boolean isCellEditable(int row, int column)
			{					
				return false;
			}
		};
		
		CustomTable table = new CustomTable(noEditableCellsModel);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		for (int i = 0; i < samplesTableContent.length; i++)
		{
			table.setValueAt(samplesTableContent[i][0], i, 0);
			table.setValueAt(samplesTableContent[i][1], i, 1);
			table.setValueAt(samplesTableContent[i][2], i, 2);
			table.setValueAt(samplesTableContent[i][3], i, 3);
		}
		
		table.addMouseListener(new MouseAdapter()
		{
			public void mouseReleased(MouseEvent e)
			{
				getContentPane().getComponent(2).setEnabled(true);
			}
		});
		
		TableRowSorter<TableModel> sorter = new TableRowSorter<>(table.getModel());
		
		table.setRowSorter(sorter);
		table.setBorder(null);
		
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.getViewport().setBackground(Color.WHITE);
		scrollPane.setBounds(new Rectangle(0, 136, SAMPLES_WINDOW_WIDTH, 156));
		add(scrollPane);
	}
	
	private void drawFooter()
	{
		Image footerImage = ViewCommons.readImageFromFile(ConfigParameter.RESOURCES_PATH, SHORT_FOOTER);
		ImagePanel footerImagePanel = new ImagePanel(footerImage);
		
		footerImagePanel.setBounds(new Rectangle(0, 292, SAMPLES_WINDOW_WIDTH, footerImage.getHeight(null)));
		
		add(footerImagePanel);
	}
	
	private void drawNextButton()
	{
		JButton nextButton = new JButton();
    	
    	nextButton.setIcon(new ImageIcon(ViewCommons.readImageFromFile(ConfigParameter.RESOURCES_PATH, NEXT_BUTTON)));
    	nextButton.addActionListener(new ActionListener()
    	{
			@Override
			public void actionPerformed(ActionEvent event)
			{
				JTable table = (JTable) ((JScrollPane) getContentPane().getComponent(1)).getViewport().getView();
				
				logger.info("Sample selected");
				
				try
				{
					testCaseController.setIxitWithSelectedSample((String) table.getValueAt(table.getSelectedRow(), 0),
							(String) table.getValueAt(table.getSelectedRow(), 1),
							(String) table.getValueAt(table.getSelectedRow(), 2),
							(String) table.getValueAt(table.getSelectedRow(), 3));
				}
				catch (SAXException | IOException | ParserConfigurationException e)
				{
					logger.warn("Cannot open the project details file");
					JOptionPane.showMessageDialog(SampleSelectionWindow.this, "A problem occurred setting sample's information."
							+ "The Test Case will be executed with the first setted sample");
				}
				catch (TransformerException e)
				{
					logger.warn("Cannot modify the project details file");
					JOptionPane.showMessageDialog(SampleSelectionWindow.this, "A problem occurred setting sample's information."
							+ "The Test Case will be executed with the first setted sample");
				}
				dispose();
			}
    	});
    	
    	nextButton.setBounds(new Rectangle(383, 329, 76, 24));
    	nextButton.setEnabled(false);
    	
    	add(nextButton);
	}
	
	private void drawBackButton()
	{
		JButton backButton = new JButton();
		
		backButton.setIcon(new ImageIcon(ViewCommons.readImageFromFile(ConfigParameter.RESOURCES_PATH, BACK_BUTTON)));
    	backButton.addActionListener(new ActionListener()
    	{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				logger.info("Test Case execution aborted");
				
				cancelled = true;
				dispose();
			}
    	});
    	backButton.setBounds(new Rectangle(291, 329, 83, 23));
    	
    	add(backButton);
	}
	
	public boolean isCancelled()
	{
		return cancelled;
	}
}